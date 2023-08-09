

#### 配置示例

简单配置

```yaml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.rocketmq.event:
  server: "localhost:9876" #服务地址
```

更多可选配置

```yaml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.rocketmq.event:
  channel: "biz" #多个 soon cloud event 插件同用时，才有用
  server: "localhost:9876" #rocketmq服务地址
  accessKey: "LTAI5t6tC2**********"  #v2.4.3 后支持
  secretKey: "MLaRt1yTRdfzt2***********" #v2.4.3 后支持
  publishTimeout: "3000" #消息发布超时（单位：ms）
  consumerGroup: "${solon.app.group}_${solon.app.name}" #消费组
  consumeThreadNums: 0 #消费线程数，0表示默认
  maxReconsumeTimes: 0 #消费消息失败的最大重试次数，0表示默认
  producerGroup: "DEFAULT" #生产组
```