
配置示例

```yaml
solon.app:
  group: demo
  name: demoapp

# 保持与 springboot 下的配置风格，方便迁移
dubbo:
  application:
    name: hello-provider
    owner: noear
  registry:
    address: nacos://localhost:8848
  protocol:
    name: dubbo
    port: 20880 #def = ${server.port + 20000}
```