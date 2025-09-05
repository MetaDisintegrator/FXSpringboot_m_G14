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



# 下面是步骤

# train
cd train-service
mvn clean package -DskipTests
docker build -t ehernng/train-service:1.0.1 .
docker push ehernng/train-service:1.0.1

# hotel
cd ../hotel-service
mvn clean package -DskipTests
docker build -t ehernng/hotel-service:1.0.1 .
docker push ehernng/hotel-service:1.0.1

# user
cd ../user-service
mvn clean package -DskipTests
docker build -t ehernng/user-service:1.0.1 .
docker push ehernng/user-service:1.0.1

kubectl get ns fxtravel 2>$null
kubectl config set-context --current --namespace=fxtravel

kubectl apply -f ./k8s/metrics-server.yaml
kubectl -n kube-system rollout status deploy/metrics-server

## 3) 部署四个服务（如果 YAML 没写 namespace，记得加 -n fxtravel）
kubectl apply -n fxtravel -f .\k8s\hotel-service.yaml
kubectl apply -n fxtravel -f .\k8s\train-service.yaml
kubectl apply -n fxtravel -f .\k8s\user-service.yaml

# 等30秒
kubectl get pods -n fxtravel -o wide
kubectl top pods -n fxtravel
kubectl get hpa -n fxtravel

* 这边就已经部署好了，后面测试

kubectl apply -f ./pressure-test/load-all-job.yaml

while ($true) { kubectl -n fxtravel get hpa; Start-Sleep 1 }
# 也看看 CPU 绝对值（确认确实被打到了）
while ($true) { kubectl -n fxtravel top pods; Start-Sleep 1 }



# 降级服务
kubectl -n fxtravel set env deploy/train-service JAVA_TOOL_OPTIONS="-Dapp.degrade-all=true"
kubectl -n fxtravel set env deploy/hotel-service JAVA_TOOL_OPTIONS="-Dapp.degrade-all=true"

kubectl -n fxtravel rollout status deploy/train-service
kubectl -n fxtravel rollout status deploy/hotel-service


kubectl apply -f pressure-test/degrade-test.yaml
kubectl -n fxtravel logs job/verify-degrade-train-hotel -f


