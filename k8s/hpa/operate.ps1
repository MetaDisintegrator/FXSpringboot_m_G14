param(
  [ValidateSet("init","apply-deploy","apply-hpa","set-images","start","stop","status","clean")]
  [string]$Action = "status",

  [string]$GatewayImage = "<YOUR_GATEWAY_IMAGE:TAG>",
  [string]$UserImage    = "<YOUR_USER_IMAGE:TAG>",
  [string]$TrainImage   = "<YOUR_TRAIN_IMAGE:TAG>",
  [string]$HotelImage   = "<YOUR_HOTEL_IMAGE:TAG>",

  [int]$Replicas = 2,
  [string]$Ns = "fxtravel"
)

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path

switch ($Action) {
  "init" {
    kubectl apply -f "$Root/../namespace.yaml"
    kubectl apply -f "$Root/../deploy/"
    kubectl apply -f "$Root"
  }
  "apply-deploy" {
    kubectl apply -f "$Root/../deploy/"
  }
  "apply-hpa" {
    kubectl apply -f "$Root"
  }
  "set-images" {
    if ($GatewayImage -notmatch "<YOUR_") { kubectl -n $Ns set image deploy/gateway-service gateway=$GatewayImage }
    if ($UserImage    -notmatch "<YOUR_") { kubectl -n $Ns set image deploy/user-service    user=$UserImage }
    if ($TrainImage   -notmatch "<YOUR_") { kubectl -n $Ns set image deploy/train-service   train=$TrainImage }
    if ($HotelImage   -notmatch "<YOUR_") { kubectl -n $Ns set image deploy/hotel-service   hotel=$HotelImage }
  }
  "start" {
    foreach ($d in "gateway-service","user-service","train-service","hotel-service") {
      kubectl -n $Ns scale deploy/$d --replicas=$Replicas
      kubectl -n $Ns rollout status deploy/$d
    }
  }
  "stop" {
    foreach ($d in "gateway-service","user-service","train-service","hotel-service") {
      kubectl -n $Ns scale deploy/$d --replicas=0
    }
  }
  "status" {
    kubectl get deploy,svc,hpa -n $Ns
    kubectl get pods -n $Ns -o wide
  }
  "clean" {
    kubectl delete ns $Ns
  }
}
