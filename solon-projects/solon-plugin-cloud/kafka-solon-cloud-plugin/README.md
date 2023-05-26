

#### 配置示例

```yaml
solon.app:
  group: demo       #配置服务使用的默认组
  name: helloapp    #发现服务使用的应用名

solon.cloud.kafka:
  server: "192.168.199.182:9092" #多个地址逗号(,)隔开
  event:
    producer: "...." # 对 KafkaProducer 的参数配置（默认：不需要加）
    consumer: "...." # 对 KafkaConsumer 的参数配置（默认：不需要加）

```