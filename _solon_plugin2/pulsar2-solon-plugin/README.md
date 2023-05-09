# pulsar2-solon-plugin

## 1、pulsar2 基础使用参考
[https://gitee.com/noear/solon-integration/tree/main/pulsar2-solon-plugin/src/test](https://gitee.com/noear/solon-integration/tree/main/pulsar2-solon-plugin/src/test)
#### 1）在`pom.xml`中引入依赖

```xml
  <dependency>
	<groupId>org.noear</groupId>
	<artifactId>pulsar2-solon-plugin</artifactId>
	<version>1.0.0</version>
  </dependency>
```

#### 2) 创建相应的消息体Java Bean

```java

public class MyMsg {

    private String data;
    
    public MyMsg(String data) {
        this.data = data;
    }

    public MyMsg() {}

    public String getData() {
        return data;
    }
}
```

#### 3) Java Config 配置生产者


```java
@Configuration
public class TestProducerConfiguration {

    @Bean
    public ProducerFactory producerFactory() {
        return new ProducerFactory()
            .addProducer("my-topic", MyMsg.class)
            .addProducer("other-topic", String.class);
    }
}
```
该插件已经默认注入 ` PulsarTemplate `  Java Bean 了，可以直接通过` @Inject `注解来获取到 ` PulsarTemplate `实例

```java
@Component
class MyProducer {

	@Inject
	private PulsarTemplate producer;

	void sendHelloWorld() throws PulsarClientException {
		producer.send("my-topic", new MyMsg("Hello world!"));
	}
}

```

#### 4） Java Config 配置消费者

 `@PulsarConsumer` 注解只能添加在方法上

```java
@Component
class MyConsumer {
    
    @PulsarConsumer(topic="my-topic", clazz=MyMsg.class)
    void consume(MyMsg msg) {
    	// TODO process your message
    	System.out.println(msg.getData());
    }
}
```
#### 5) Java Config 批量配置消费者
只需将 `@PulsarConsumer` 注解的 ` batch `属性设置为 `true`.

```java
@Component
class MyBatchConsumer {
    
    @PulsarConsumer(topic = "my-topic",
            clazz=MyMsg.class,
            consumerName = "my-consumer",
            subscriptionName = "my-subscription",
            batch = true)
    public void consumeString(Messages<MyMsg> msgs) {
    		msgs.forEach((msg) -> {
    				System.out.println(msg);
    		});
    	}
    		
}
```
#### 6） java Config 配置消息消费返回后确认的消息
将 `@PulsarConsumer` 注解的 ` batch `属性设置为 `true`.

```java
@Component
class MyBatchConsumer {
    
    @PulsarConsumer(topic = "my-topic",
            clazz=MyMsg.class,
            consumerName = "my-consumer",
            subscriptionName = "my-subscription",
            batch = true)
    public List<MessageId> consumeString(Messages<MyMsg> msgs) {
    		List<MessageId> ackList = new ArrayList<>();
    		msgs.forEach((msg) -> {
    				System.out.println(msg);
    				ackList.add(msg.getMessageId());
    		});
    		return ackList;
    	}
    		
}
```
#### 7) java Config 配置消息消费后，需手工确认的消息
将 `@PulsarConsumer` 注解的 ` batch `属性设置为 `true`. 和 ` batchAckMode ` 属性设置为 `BatchAckMode.MANUAL`

```java
@Component
class MyBatchConsumer {
    
    @PulsarConsumer(topic = "my-topic",
            clazz=MyMsg.class,
            consumerName = "my-consumer",
            subscriptionName = "my-subscription",
            batch = true,
            batchAckMode = BatchAckMode.MANUAL)
    public void consumeString(Messages<MyMsg> msgs,Consumer<MyMsg> consumer) {
    			List<MessageId> ackList = new ArrayList<>();
	    		msgs.forEach((msg) -> {
	    			try {
	    				System.out.println(msg);
	    				ackList.add(msg.getMessageId());
	    			} catch (Exception ex) {
		    			System.err.println(ex.getMessage());
	    				consumer.negativeAcknowledge(msg);
		    		}
	    		});
	    		consumer.acknowledge(ackList);
	}
    		
}
```


#### 8） 最小化配置

```properties

solon.pulsar2.service-url=pulsar://localhost:6650

```

### 9) 配置参考

Default configuration:
```properties

#PulsarClient
solon.pulsar2.service-url=pulsar://localhost:6650
solon.pulsar2.io-threads=10
solon.pulsar2.listener-threads=10
solon.pulsar2.enable-tcp-no-delay=false
solon.pulsar2.keep-alive-interval-sec=20
solon.pulsar2.connection-timeout-sec=10
solon.pulsar2.operation-timeout-sec=15
solon.pulsar2.starting-backoff-interval-ms=100
solon.pulsar2.max-backoff-interval-sec=10
solon.pulsar2.consumer-name-delimiter=
solon.pulsar2.namespace=default
solon.pulsar2.tenant=public
solon.pulsar2.auto-start=true
solon.pulsar2.allow-interceptor=false

#Consumer
solon.pulsar2.consumer.default.dead-letter-policy-max-redeliver-count=-1
solon.pulsar2.consumer.default.ack-timeout-ms=3000

```

TLS connection configuration:
```properties
solon.pulsar2.service-url=pulsar+ssl://localhost:6651
solon.pulsar2.tlsTrustCertsFilePath=/etc/pulsar/tls/ca.crt
solon.pulsar2.tlsCiphers=TLS_DH_RSA_WITH_AES_256_GCM_SHA384,TLS_DH_RSA_WITH_AES_256_CBC_SHA
solon.pulsar2.tlsProtocols=TLSv1.3,TLSv1.2
solon.pulsar2.allowTlsInsecureConnection=false
solon.pulsar2.enableTlsHostnameVerification=false

solon.pulsar2.tlsTrustStorePassword=brokerpw
solon.pulsar2.tlsTrustStorePath=/var/private/tls/broker.truststore.jks
solon.pulsar2.tlsTrustStoreType=JKS

solon.pulsar2.useKeyStoreTls=false
```

Pulsar client authentication (Only one of the options can be used)
```properties
# TLS
solon.pulsar2.tls-auth-cert-file-path=/etc/pulsar/tls/cert.cert.pem
solon.pulsar2.tls-auth-key-file-path=/etc/pulsar/tls/key.key-pk8.pem

#Token based
solon.pulsar2.token-auth-value=43th4398gh340gf34gj349gh304ghryj34fh

#OAuth2 based
solon.pulsar2.oauth2-issuer-url=https://accounts.google.com
solon.pulsar2.oauth2-credentials-url=file:/path/to/file
solon.pulsar2.oauth2-audience=https://broker.example.com
```

## 属性说明，搬至官网:

### PulsarClient

- `solon.pulsar2.service-url` - URL used to connect to pulsar cluster. Use `pulsar+ssl://` URL to enable TLS configuration. Examples: `pulsar://my-broker:6650` for regular endpoint `pulsar+ssl://my-broker:6651` for TLS encrypted endpoint
- `solon.pulsar2.io-threads` - Number of threads to be used for handling connections to brokers.
- `solon.pulsar2.listener-threads` - Set the number of threads to be used for message listeners/subscribers.
- `solon.pulsar2.enable-tcp-no-delay` -  Whether to use TCP no-delay flag on the connection, to disable Nagle algorithm.
- `solon.pulsar2.keep-alive-interval-sec` - Keep alive interval for each client-broker-connection.
- `solon.pulsar2.connection-timeout-sec` - duration of time to wait for a connection to a broker to be established. If the duration passes without a response from the broker, the connection attempt is dropped.
- `solon.pulsar2.operation-timeout-sec` - Operation timeout.
- `solon.pulsar2.starting-backoff-interval-ms` - Duration of time for a backoff interval (Retry algorithm).
- `solon.pulsar2.max-backoff-interval-sec` - The maximum duration of time for a backoff interval (Retry algorithm).
- `solon.pulsar2.consumer-name-delimiter` - Consumer names are connection of bean name and method with a delimiter. By default, there is no delimiter and words are connected together.
- `solon.pulsar2.namespace` - Namespace separation. For example: app1/app2 OR dev/staging/prod. More in [Namespaces docs](https://solon.pulsar2.apache.org/docs/en/concepts-messaging/#namespaces).
- `solon.pulsar2.tenant` - Pulsar multi-tenancy support. More in [Multi Tenancy docs](https://solon.pulsar2.apache.org/docs/en/concepts-multi-tenancy/).
- `solon.pulsar2.auto-start` - Whether the subscriptions should start on application startup. Useful in case you wish to not subscribe on some environments (dev,PoC,...).
- `solon.pulsar2.allow-interceptor` - Whether the application should allow usage of interceptors and inject default interceptors with `DEBUG` level logging. It also switches on the Micrometer & Prometheus metrics collecting.
- `solon.pulsar2.listener-name` - Multiple advertised listeners support - when a Pulsar cluster is deployed in the production environment, it may require to expose multiple advertised addresses for the broker. For example, when you deploy a Pulsar cluster in Kubernetes and want other clients. [Multiple advertised listeners docs](https://solon.pulsar2.apache.org/docs/en/concepts-multiple-advertised-listeners/)

**Change only in case TLS is enabled** (By using `pulsar+ssl://` as `solon.pulsar2.service-url` value prefix.)

- `solon.pulsar2.tlsTrustCertsFilePath` -  Path to the trusted TLS certificate file
- `solon.pulsar2.tlsCiphers` - A list of cipher suites. This is a named combination of authentication, encryption, MAC and key exchange algorithm used to negotiate the security settings for a network connection using TLS or SSL network protocol. By default, all the available cipher suites are supported.
- `solon.pulsar2.tlsProtocols` - The SSL protocol used to generate the SSLContext.
- `solon.pulsar2.tlsTrustStorePassword` - The store password for the key store file.
- `solon.pulsar2.tlsTrustStorePath` - The location of the trust store file.
- `solon.pulsar2.tlsTrustStoreType` - The file format of the trust store file.
- `solon.pulsar2.useKeyStoreTls` - Whether use KeyStore type as tls configuration parameter. False means use default pem type configuration.
- `solon.pulsar2.allowTlsInsecureConnection` - Whether the Pulsar client accepts untrusted TLS certificate from broker
- `solon.pulsar2.enableTlsHostnameVerification` - Whether to enable TLS hostname verification

### PulsarClient Authentication properties (optional)

Only one of the following authentication methods can be used.

**Pulsar TLS client authentication**

- `solon.pulsar2.tls-auth-cert-file-path` - the path to the TLS client public key
- `solon.pulsar2.tls-auth-key-file-path` - the path to the TLS client private key

**Pulsar token based client authentication**

- `solon.pulsar2.token-auth-value` - the client auth token

**Pulsar OAuth2 based client authentication**

- `solon.pulsar2.oauth2-issuer-url` - URL of the authentication provider which allows the Pulsar client to obtain an access token.
- `solon.pulsar2.oauth2-credentials-url` - URL to a JSON credentials file. Support the following pattern formats: `file:///path/to/file`, `file:/path/to/file` or `data:application/json;base64,<base64-encoded value>`
- `solon.pulsar2.oauth2-audience` - An OAuth 2.0 "resource server" identifier for the Pulsar cluster.

### PulsarConsumer default configurations

- `solon.pulsar2.consumer.default.dead-letter-policy-max-redeliver-count` - How many times should pulsar try to retry sending the message to consumer.
- `solon.pulsar2.consumer.default.ack-timeout-ms` - How soon should be the message acked and how soon will dead letter mechanism try to retry to send the message.
- `solon.pulsar2.consumer.default.subscription-type` - By default all subscriptions are `Exclusive`. You can override this default value here globally or set individualy in each `@PulsarConsumer` annotation.

## 2、进阶用法

#### 1） 响应式 Reactor support (Flux)，待完善


```java
@Configuration
public class MyFluxConsumers {

    @Bean
    public FluxConsumer myFluxConsumer(FluxConsumerFactory fluxConsumerFactory) {
        return fluxConsumerFactory.newConsumer(
            PulsarFluxConsumer.builder()
                .setTopic("flux-topic")
                .setConsumerName("flux-consumer")
                .setSubscriptionName("flux-subscription")
                .setMessageClass(MyMsg.class)
                .build());
    }
}
```

```java
@Component
public class MyFluxConsumerService {
    
    @Inject
    private FluxConsumer myFluxConsumer;

    public void subscribe() {
        myFluxConsumer
            .asSimpleFlux()
            .subscribe(msg -> System.out.println(msg.getData()));
    }
}
```

3. (可选) 如果您希望手动确认消息，则可以以不同的方式配置您的消费者.

```java
PulsarFluxConsumer.builder()
    .setTopic("flux-topic")
    .setConsumerName("flux-consumer")
    .setSubscriptionName("flux-subscription")
    .setMessageClass(MyMsg.class)
    .setSimple(false) // This is your required change in bean configuration class
    .build());
```

```java
@Component
public class MyFluxConsumerService {
    
    @Inject
    private FluxConsumer myFluxConsumer;

    public void subscribe() {
        myFluxConsumer.asFlux()
            .subscribe(msg -> {
                try {
                    final MyMsg myMsg = (MyMsg) msg.getMessage().getValue();

                    System.out.println(myMsg.getData());

                    // you need to acknowledge the message manually on finished job
                    msg.getConsumer().acknowledge(msg.getMessage());
                } catch (PulsarClientException e) {
                    // you need to negatively acknowledge the message manually on failures
                    msg.getConsumer().negativeAcknowledge(msg.getMessage());
                }
            });
    }
}
```

#### 2) 调式模式

默认在日志文件输出 `DEBUG` .

```properties
solon.pulsar2.allow-interceptor=true
```

默认注入 `DefaultConsumerInterceptor`的实例.或者可自定义:

*消费者Consumer Interceptor Example:*
```java
@Component
public class PulsarConsumerInterceptor extends DefaultConsumerInterceptor<Object> {
    @Override
    public Message beforeConsume(Consumer<Object> consumer, Message message) {
        System.out.println("do something");
        return super.beforeConsume(consumer, message);
    }
}
```
*生产者Producer Interceptor Example:*
```java
@Component
public class PulsarProducerInterceptor extends DefaultProducerInterceptor {

    @Override
    public Message beforeSend(Producer producer, Message message) {
        super.beforeSend(producer, message);
        System.out.println("do something");
        return message;
    }

    @Override
    public void onSendAcknowledgement(Producer producer, Message message, MessageId msgId, Throwable exception) {
        super.onSendAcknowledgement(producer, message, msgId, exception);
    }
}
```

