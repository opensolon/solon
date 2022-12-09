

#### 配置示例

```yaml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名


solon.cloud.aliyun.ons:
  server: http://MQ_IN**************.mq.cn-qingdao.aliyuncs.com:80   #TCP地址
  accessKey: LTAI5t6tC2**********
  secretKey: MLaRt1yTRdfzt2***********
  event:
    enable: true                  #是否启用（默认：启用）
    producerGroup: GID_TEST
    consumerGroup: GID_TEST
    sendMsgTimeoutMillis: 5000     #默认：3000 消息发送的超时时间/毫秒
    messageModel: BROADCASTING  #默认：CLUSTERING 表示集群消费CLUSTERING  表示广播消费BROADCASTING
    consumeThreadNums: 50    # 默认20 实例的消费线程数
    maxReconsumeTimes: 30    # 默认16 设置消息消费失败的最大重试次数

```