

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
  channel: "" #多个 soon cloud event 插件同用时，才有用
  server: "localhost:9876" #服务地址
  accessKey: LTAI5t6tC2**********
  secretKey: MLaRt1yTRdfzt2***********
  publishTimeout: "3000"
  consumeThreadNums: #消费线程数
  consumerGroup: "${solon.app.group}_${solon.app.name}" #消费用
  maxReconsumeTimes:
  producerGroup: "DEFAULT" #生产组

```