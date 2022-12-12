

#### 配置示例

```yaml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名


solon.cloud.aliyun.ons:
  server: http://MQ_IN**************.mq.cn-qingdao.aliyuncs.com:80   #TCP地址
  event:
    enable: true             #默认 true ，是否启用
    accessKey: LTAI5t6tC2**********
    secretKey: MLaRt1yTRdfzt2***********
    producerGroup: GID_TEST  #默认 DEFAULT，生产组
    consumerGroup: GID_TEST  #默认 {slon.app.group}_{solon.app.name}，消费组
    publishTimeout: 3000     #默认 3000 事件发布超时/毫秒
    consumeThreadNums: 20    #默认 20 实例的消费线程数
    maxReconsumeTimes: 16    #默认 16 设置消息消费失败的最大重试次数

```