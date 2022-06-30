
配置示例

```yaml
solon.app:
  group: demo
  name: demoapp

dubbo:
  application:
    owner: demo
  registry:
    - address: zookeeper://192.168.133.129:2181
  protocol:
    name: dubbo
    port: 20880 #def = ${server.port + 20000}

```