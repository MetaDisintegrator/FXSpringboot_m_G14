#!/bin/bash
# deploy.sh

# 定义变量
FRONTEND_DIR="frontend"
IMAGE_TAG=$(date +%Y%m%d%H%M%S)
MAIN_NODE="root@192.168.184.131"        # 主节点使用主机名
WORKER_NODES=("worker1" "worker2")
#SVC_NAMES=("config-service" "discovery-service" "gateway-service" "user-service" "hotel-service" "train-service")
SVC_NAMES=("eureka-service" "eureka-client")

# 函数：错误处理
handle_error() {
    echo "Error on line $1"
    read -p "按任意键退出..." -n 1
    exit 1
}

trap 'handle_error $LINENO' ERR

#./deploy-micro.sh "discovery-service" "discovery-service" "123" "root@192.168.184.131" "worker1" "worker2" || read -p "1" -n 1

# 1. 构建各个微服务
echo "========== 构建微服务 =========="
for SVC_NAME in "${SVC_NAMES[@]}"; do
    echo -e "\n▶▶▶ 正在部署服务: $SVC_NAME"
    echo "--------------------------------------------------"

    # 调用 deploy-micro.sh 并实时显示输出
    ./deploy-micro.sh \
        "$SVC_NAME" \
        "$SVC_NAME" \
        "$IMAGE_TAG" \
        "$MAIN_NODE" \
        "${WORKER_NODES[@]}"

    # 检查上一条命令是否成功
    if [ $? -ne 0 ]; then
        echo -e "\n❌ 服务 $SVC_NAME 部署失败！"
        read -p "按任意键退出..." -n 1
        exit 1
    fi
done

echo "========== 清理所有Pod =========="
ssh  $MAIN_NODE << EOF
    kubectl delete pod --all -n default
EOF

echo "========== 部署完成 =========="
echo "新镜像标签: $IMAGE_TAG"
echo "所有服务已更新至 Kubernetes 集群"

read -p "按任意键退出..." -n 1