

#### 配置示例

简单配置

```yaml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.rocketmq.event:
  server: "localhost:9876" #服务地址
```

完整配置

```yaml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.rocketmq.event:
  server: "localhost:9876" #服务地址
  accessKey: LTAI5t6tC2**********
  secretKey: MLaRt1yTRdfzt2***********

```