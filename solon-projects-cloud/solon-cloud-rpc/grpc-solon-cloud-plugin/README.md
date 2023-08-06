

```yaml

server.grpc.port: 9090
server.grpc.name: demogrpc


# 使用注册与服务服务
solon.cloud.water:
  server: "waterapi:9371"


# 使用本地配置服务发现
solon.cloud.local:
  discovery:
    service:
      helloapi: 
        - "http://localhost:8081"
      userapi:
        - "http://userapi"

#grpc:
#  client:
#    userClient:
#      negotiationType: PLAINTEXT
#      address: static://localhost:9090
#  server:
#    name: demogrpc
#    port: 9090

```