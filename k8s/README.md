## 0) 统一命名空间（强烈推荐）
kubectl get ns fxtravel 2>$null || kubectl create ns fxtravel
kubectl config set-context --current --namespace=fxtravel

## 1) metrics-server 安装/补丁（保证 top 可用）
如果你已有自己的 metrics-server.yaml 就 apply；否则用官方清单：
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml

## 无论如何，给本地集群打补丁（Docker Desktop/Minikube 常用）
kubectl patch deployment metrics-server -n kube-system --type='json' -p @"
[
{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-insecure-tls"},
{"op":"add","path":"/spec/template/spec/containers/0/args/-","value":"--kubelet-preferred-address-types=InternalIP,ExternalIP,Hostname"}
]
"@
kubectl -n kube-system rollout status deploy/metrics-server

## 2) 构建四个本地镜像（在四个服务目录分别执行或改成你的真实目录）,需要手动 build docker
cd ..\gateway-service ; docker build -t gateway-service:latest .
cd ..\hotel-service   ; docker build -t hotel-service:latest .
cd ..\train-service   ; docker build -t train-service:latest .
cd ..\user-service    ; docker build -t user-service:latest .

## 3) 先把 config & hpa 应用到 fxtravel（确保 YAML 里没 namespace 的话，这里加 -n）
kubectl apply -n fxtravel -f .\k8s\config\degrade-config.yaml
kubectl apply -n fxtravel -f .\k8s\hpa\gateway-hpa.yaml
kubectl apply -n fxtravel -f .\k8s\hpa\hotel-hpa.yaml
kubectl apply -n fxtravel -f .\k8s\hpa\train-hpa.yaml
kubectl apply -n fxtravel -f .\k8s\hpa\user-hpa.yaml

## 4) 部署四个服务（如果 YAML 没写 namespace，记得加 -n fxtravel）
kubectl apply -n fxtravel -f .\k8s\deploy\gateway-deploy.yaml
kubectl apply -n fxtravel -f .\k8s\deploy\hotel-deploy.yaml
kubectl apply -n fxtravel -f .\k8s\deploy\train-deploy.yaml
kubectl apply -n fxtravel -f .\k8s\deploy\user-deploy.yaml

## 5) 强制把镜像指向你本地刚 build 的名字（防止 YAML 里还是占位符）
kubectl set image deploy/gateway-service gateway=gateway-service:latest -n fxtravel
kubectl set image deploy/hotel-service   hotel=hotel-service:latest   -n fxtravel
kubectl set image deploy/train-service   train=train-service:latest   -n fxtravel
kubectl set image deploy/user-service    user=user-service:latest     -n fxtravel

## 6) 给容器加上 HPA 需要的 CPU requests（若 YAML 已经写了可跳过）
kubectl set resources deploy/gateway-service -n fxtravel --containers=gateway --requests=cpu=200m,memory=256Mi --limits=cpu=1,memory=512Mi
kubectl set resources deploy/hotel-service   -n fxtravel --containers=hotel   --requests=cpu=200m,memory=256Mi --limits=cpu=1,memory=512Mi
kubectl set resources deploy/train-service   -n fxtravel --containers=train   --requests=cpu=200m,memory=256Mi --limits=cpu=1,memory=512Mi
kubectl set resources deploy/user-service    -n fxtravel --containers=user    --requests=cpu=200m,memory=256Mi --limits=cpu=1,memory=512Mi

## 7) 滚动并等待就绪
kubectl rollout status deploy/gateway-service -n fxtravel
kubectl rollout status deploy/hotel-service   -n fxtravel
kubectl rollout status deploy/train-service   -n fxtravel
kubectl rollout status deploy/user-service    -n fxtravel

## 8) 等 30~60s，验证 metrics + HPA
kubectl get pods -n fxtravel -o wide
kubectl top pods -n fxtravel
kubectl get hpa -n fxtravel




# 删除
## 0) 固定命名空间
kubectl get ns fxtravel 2>$null || kubectl create ns fxtravel
kubectl config set-context --current --namespace=fxtravel

## 1) 删 HPA
kubectl delete hpa --all -n fxtravel

## 2) 删常见工作负载与网络资源
kubectl delete deploy,svc,ingress,job,cronjob,rs,po --all -n fxtravel

## 3) 删配置与凭证（按需）
kubectl delete configmap degrade-config -n fxtravel --ignore-not-found
kubectl delete secret regcred -n fxtravel --ignore-not-found

## 4) 存储（若用到）
kubectl delete pvc --all -n fxtravel

## 5) 确认已清空
kubectl get all -n fxtravel
kubectl get hpa,cm,secret,pvc,ingress -n fxtravel
