
配置示例

```yaml
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