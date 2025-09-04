#!/bin/bash
# deploy-micro.sh

# 参数检查
if [ $# -lt 4 ]; then
    echo "Usage: $0 <microservice_dir> <SVC_NAME> <image_tag> <main_node> [worker_node1 worker_node2 ...]"
    echo "Example: $0 /path/to/microservice my-service v1.0 root@192.168.1.100 root@192.168.1.101"
    exit 1
fi

# 接收参数
MICROSERVICE_DIR=$1
SVC_NAME=$2
IMAGE_TAG=$3
MAIN_NODE=$4
WORKER_NODES=("${@:5}")  # 从第五个参数开始都是工作节点

# 定义变量
ORIGINAL_DIR=$(pwd)
YAML_PATH="~/fx/yml-micro/$SVC_NAME.yml"
IMAGE_TAR="$SVC_NAME-app.tar"
DEFAULT_DOCKERFILE="$ORIGINAL_DIR/Dockerfile"  # 使用原始目录的Dockerfile

# 错误处理函数
handle_error() {
    echo "Error on line $1"
    exit 1
}

trap 'handle_error $LINENO' ERR

# 进入微服务目录
cd "$MICROSERVICE_DIR" || { echo "无法进入目录 $MICROSERVICE_DIR"; exit 1; }

# 1. 构建Java后端
echo "========== 构建Java后端 =========="
mvn clean package -Dskiptest
# 检查本地是否存在Dockerfile
if [ -f "Dockerfile" ]; then
    echo "▶ 使用当前目录的 Dockerfile"
    docker build -t "$SVC_NAME:$IMAGE_TAG" .
else
    # 检查默认Dockerfile是否存在
    if [ ! -f "$DEFAULT_DOCKERFILE" ]; then
        echo "错误：默认Dockerfile不存在于 $DEFAULT_DOCKERFILE"
        exit 1
    fi
    echo "▶ 使用默认Dockerfile: $DEFAULT_DOCKERFILE"
    echo "当前位置: $(pwd)"
    docker build --no-cache -f "$DEFAULT_DOCKERFILE" -t "$SVC_NAME:$IMAGE_TAG"  ./
fi


# 2. 保存镜像并清理本地
echo "========== 保存镜像并清理本地 =========="
docker save -o "./$IMAGE_TAR" "$SVC_NAME:$IMAGE_TAG"
docker rmi "$SVC_NAME:$IMAGE_TAG"

# 3. 分发镜像到所有节点
echo "========== 分发镜像到所有节点 =========="
scp "./$IMAGE_TAR" "$MAIN_NODE:~/fx/"

worker_nodes_str="${WORKER_NODES[*]}"

# 通过主节点分发到工作节点（如果有）
if [ ${#WORKER_NODES[@]} -gt 0 ]; then
    ssh "$MAIN_NODE" << EOF
        # 并行分发到工作节点
        nodes=($worker_nodes_str)
        for node in "\${nodes[@]}"; do
            echo "分发到工作节点: \$node"
            scp ~/fx/$IMAGE_TAR "\$node:~/fx/"
        done
        wait  # 等待所有后台任务完成
EOF
fi

# 4. 加载镜像到所有节点
echo "========== 加载镜像到所有节点 =========="
if [ ${#WORKER_NODES[@]} -gt 0 ]; then
  ssh "$MAIN_NODE" << EOF
          nodes=($worker_nodes_str)
          for node in "\${nodes[@]}"; do
              echo "加载到工作节点: \$node"
              ssh "\$node" "docker load -i ~/fx/$IMAGE_TAR"
          done
          wait
EOF
fi

# 5. 更新K8s部署
echo "========== 更新K8s部署 =========="
ssh "$MAIN_NODE" << EOF
#    kubectl delete -f $YAML_PATH || echo "删除旧部署失败"
    sed -i 's|image: $SVC_NAME:.*|image: $SVC_NAME:$IMAGE_TAG|g' $YAML_PATH || echo "YAML文件修改失败"
    kubectl apply -f $YAML_PATH || echo "新部署应用失败"
EOF

# 6. 清理旧镜像（保留最新3个）
#echo "========== 清理旧镜像 =========="
#if [ ${#WORKER_NODES[@]} -gt 0 ]; then
#  ssh "$MAIN_NODE" << EOF
#          nodes=($worker_nodes_str)
#          for node in "\${nodes[@]}"; do
#              echo "清理工作节点: \$node"
#              ssh "\$node" << 'INNER_EOF'
#                  docker images --filter "until=3h" --format "{{.ID}}" | xargs -r docker rmi || echo "镜像清理失败"
#INNER_EOF
#          done
#EOF
#fi

echo "========== $SVC_NAME 部署完成 =========="
echo "YAML文件已更新: $YAML_PATH"
cd ..