# Spring boot starter for [Apache Pulsar](https://pulsar.apache.org/)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.majusko/pulsar-java-spring-boot-starter/badge.svg)](https://search.maven.org/artifact/io.github.majusko/pulsar-java-spring-boot-starter)
[![Release](https://jitpack.io/v/majusko/pulsar-java-spring-boot-starter.svg)](https://jitpack.io/#majusko/pulsar-java-spring-boot-starter)
[![Build Status](https://github.com/majusko/pulsar-java-spring-boot-starter/actions/workflows/test.yml/badge.svg)](https://github.com/majusko/pulsar-java-spring-boot-starter/actions/workflows/test.yml)
[![Test Coverage](https://codecov.io/gh/majusko/pulsar-java-spring-boot-starter/branch/master/graph/badge.svg)](https://codecov.io/gh/majusko/pulsar-java-spring-boot-starter/branch/master)
[![TCode Quality](https://github.com/majusko/pulsar-java-spring-boot-starter/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/majusko/java-pulsar-example/actions/workflows/codeql-analysis.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT) [![Join the chat at https://gitter.im/pulsar-java-spring-boot-starter/community](https://badges.gitter.im/pulsar-java-spring-boot-starter/community.svg)](https://gitter.im/pulsar-java-spring-boot-starter/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Example/Demo project

#### [Java Pulsar Example Project](https://github.com/majusko/java-pulsar-example)

## Quick Start

Simple steps to start using the library.

#### 1. Add Maven dependency

```xml
<dependency>
  <groupId>io.github.majusko</groupId>
  <artifactId>pulsar-java-spring-boot-starter</artifactId>
  <version>1.2.0</version>
</dependency>
```

#### 2. Create your data class

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

#### 3. Configure Producer

Create your configuration class with all producers you would like to register.

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

Use registered producers by simply injecting the `PulsarTemplate` into your service.

```java
@Service
class MyProducer {

	@Autowired
	private PulsarTemplate<MyMsg> producer;

	void sendHelloWorld() throws PulsarClientException {
		producer.send("my-topic", new MyMsg("Hello world!"));
	}
}

```

#### 4. Configure Consumer

Annotate your service method with `@PulsarConsumer` annotation.

```java
@Service
class MyConsumer {
    
    @PulsarConsumer(topic="my-topic", clazz=MyMsg.class)
    void consume(MyMsg msg) {
    	// TODO process your message
    	System.out.println(msg.getData());
    }
}
```
#### 5. Configure Batch Consumer
Annotate your service method with `@PulsarConsumer` annotation and set batch attribute to `true`.

```java
@Service
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
#### 6. Configure Batch Consumer With Messages To Be Acknowledged From Returned List
Annotate your service method with `@PulsarConsumer` annotation and set batch attribute to `true`.
Return a list from your consumer method, which contains MessageId's to be acknowledged.

```java
@Service
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
#### 7. Configure Batch Consumer With Manual Acknowledge Control
Annotate your service method with `@PulsarConsumer` annotation. Set batch attribute to `true` and
set batchAckMode attribute to `BatchAckMode.MANUAL`. Your consumer method should contain one more parameter
of type Consumer.

```java
@Service
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


#### 8. Minimal Configuration

```properties

pulsar.service-url=pulsar://localhost:6650

```

## Documentation

### Configuration

Default configuration:
```properties

#PulsarClient
pulsar.service-url=pulsar://localhost:6650
pulsar.io-threads=10
pulsar.listener-threads=10
pulsar.enable-tcp-no-delay=false
pulsar.keep-alive-interval-sec=20
pulsar.connection-timeout-sec=10
pulsar.operation-timeout-sec=15
pulsar.starting-backoff-interval-ms=100
pulsar.max-backoff-interval-sec=10
pulsar.consumer-name-delimiter=
pulsar.namespace=default
pulsar.tenant=public
pulsar.auto-start=true
pulsar.allow-interceptor=false

#Consumer
pulsar.consumer.default.dead-letter-policy-max-redeliver-count=-1
pulsar.consumer.default.ack-timeout-ms=3000

```

TLS connection configuration:
```properties
pulsar.service-url=pulsar+ssl://localhost:6651
pulsar.tlsTrustCertsFilePath=/etc/pulsar/tls/ca.crt
pulsar.tlsCiphers=TLS_DH_RSA_WITH_AES_256_GCM_SHA384,TLS_DH_RSA_WITH_AES_256_CBC_SHA
pulsar.tlsProtocols=TLSv1.3,TLSv1.2
pulsar.allowTlsInsecureConnection=false
pulsar.enableTlsHostnameVerification=false

pulsar.tlsTrustStorePassword=brokerpw
pulsar.tlsTrustStorePath=/var/private/tls/broker.truststore.jks
pulsar.tlsTrustStoreType=JKS

pulsar.useKeyStoreTls=false
```

Pulsar client authentication (Only one of the options can be used)
```properties
# TLS
pulsar.tls-auth-cert-file-path=/etc/pulsar/tls/cert.cert.pem
pulsar.tls-auth-key-file-path=/etc/pulsar/tls/key.key-pk8.pem

#Token based
pulsar.token-auth-value=43th4398gh340gf34gj349gh304ghryj34fh

#OAuth2 based
pulsar.oauth2-issuer-url=https://accounts.google.com
pulsar.oauth2-credentials-url=file:/path/to/file
pulsar.oauth2-audience=https://broker.example.com
```

## Properties explained:

### PulsarClient

- `pulsar.service-url` - URL used to connect to pulsar cluster. Use `pulsar+ssl://` URL to enable TLS configuration. Examples: `pulsar://my-broker:6650` for regular endpoint `pulsar+ssl://my-broker:6651` for TLS encrypted endpoint
- `pulsar.io-threads` - Number of threads to be used for handling connections to brokers.
- `pulsar.listener-threads` - Set the number of threads to be used for message listeners/subscribers.
- `pulsar.enable-tcp-no-delay` -  Whether to use TCP no-delay flag on the connection, to disable Nagle algorithm.
- `pulsar.keep-alive-interval-sec` - Keep alive interval for each client-broker-connection.
- `pulsar.connection-timeout-sec` - duration of time to wait for a connection to a broker to be established. If the duration passes without a response from the broker, the connection attempt is dropped.
- `pulsar.operation-timeout-sec` - Operation timeout.
- `pulsar.starting-backoff-interval-ms` - Duration of time for a backoff interval (Retry algorithm).
- `pulsar.max-backoff-interval-sec` - The maximum duration of time for a backoff interval (Retry algorithm).
- `pulsar.consumer-name-delimiter` - Consumer names are connection of bean name and method with a delimiter. By default, there is no delimiter and words are connected together.
- `pulsar.namespace` - Namespace separation. For example: app1/app2 OR dev/staging/prod. More in [Namespaces docs](https://pulsar.apache.org/docs/en/concepts-messaging/#namespaces).
- `pulsar.tenant` - Pulsar multi-tenancy support. More in [Multi Tenancy docs](https://pulsar.apache.org/docs/en/concepts-multi-tenancy/).
- `pulsar.auto-start` - Whether the subscriptions should start on application startup. Useful in case you wish to not subscribe on some environments (dev,PoC,...).
- `pulsar.allow-interceptor` - Whether the application should allow usage of interceptors and inject default interceptors with `DEBUG` level logging. It also switches on the Micrometer & Prometheus metrics collecting.
- `pulsar.listener-name` - Multiple advertised listeners support - when a Pulsar cluster is deployed in the production environment, it may require to expose multiple advertised addresses for the broker. For example, when you deploy a Pulsar cluster in Kubernetes and want other clients. [Multiple advertised listeners docs](https://pulsar.apache.org/docs/en/concepts-multiple-advertised-listeners/)

**Change only in case TLS is enabled** (By using `pulsar+ssl://` as `pulsar.service-url` value prefix.)

- `pulsar.tlsTrustCertsFilePath` -  Path to the trusted TLS certificate file
- `pulsar.tlsCiphers` - A list of cipher suites. This is a named combination of authentication, encryption, MAC and key exchange algorithm used to negotiate the security settings for a network connection using TLS or SSL network protocol. By default, all the available cipher suites are supported.
- `pulsar.tlsProtocols` - The SSL protocol used to generate the SSLContext.
- `pulsar.tlsTrustStorePassword` - The store password for the key store file.
- `pulsar.tlsTrustStorePath` - The location of the trust store file.
- `pulsar.tlsTrustStoreType` - The file format of the trust store file.
- `pulsar.useKeyStoreTls` - Whether use KeyStore type as tls configuration parameter. False means use default pem type configuration.
- `pulsar.allowTlsInsecureConnection` - Whether the Pulsar client accepts untrusted TLS certificate from broker
- `pulsar.enableTlsHostnameVerification` - Whether to enable TLS hostname verification

### PulsarClient Authentication properties (optional)

Only one of the following authentication methods can be used.

**Pulsar TLS client authentication**

- `pulsar.tls-auth-cert-file-path` - the path to the TLS client public key
- `pulsar.tls-auth-key-file-path` - the path to the TLS client private key

**Pulsar token based client authentication**

- `pulsar.token-auth-value` - the client auth token

**Pulsar OAuth2 based client authentication**

- `pulsar.oauth2-issuer-url` - URL of the authentication provider which allows the Pulsar client to obtain an access token.
- `pulsar.oauth2-credentials-url` - URL to a JSON credentials file. Support the following pattern formats: `file:///path/to/file`, `file:/path/to/file` or `data:application/json;base64,<base64-encoded value>`
- `pulsar.oauth2-audience` - An OAuth 2.0 "resource server" identifier for the Pulsar cluster.

### PulsarConsumer default configurations

- `pulsar.consumer.default.dead-letter-policy-max-redeliver-count` - How many times should pulsar try to retry sending the message to consumer.
- `pulsar.consumer.default.ack-timeout-ms` - How soon should be the message acked and how soon will dead letter mechanism try to retry to send the message.
- `pulsar.consumer.default.subscription-type` - By default all subscriptions are `Exclusive`. You can override this default value here globally or set individualy in each `@PulsarConsumer` annotation.

### Additional usages

#### 1. PulsarMessage Wrapper

In case you need to access pulsar metadata you simply use `PulsarMessage` as a wrapper and data will be injected for you.

```java
@Service
class MyConsumer {
    
    @PulsarConsumer(topic="my-topic", clazz=MyMsg.class)
    void consume(PulsarMessage<MyMsg> myMsg) { 
        producer.send(TOPIC, msg.getValue()); 
    }
}
```

#### 2. Overriding default consumer and subscription names
By default, all subscription and consumer names are auto-generated, and you don't need to worry about configuring them for most of the use cases.
However, you are able to override the automatic generation of the subscription and consumer names if your use case requires special configurations.

```java
@PulsarConsumer(
        topic = "my-topic",
        clazz = MyMsg.class,
        consumerName = "my-consumer",
        subscriptionName = "my-subscription")
```

#### 3. SpeL support

You can configure a **topic, consumer and subscription** names in `application.properties`

```properties
my.custom.topic.name=foo
my.custom.consumer.name=foo
my.custom.subscription.name=foo
```

```java
@Service
class MyConsumer {
    
    @PulsarConsumer(
        topic = "${my.custom.topic.name}",
        clazz = MyMsg.class,
        consumerName = "${my.custom.consumer.name}",
        subscriptionName = "${my.custom.subscription.name}")
    public void consume(MyMsg myMsg) {
    }
}
```

#### 4. Error handling

All failed messages should be handled with Pulsar features like for example "Dead Letter Policies".
However, for debug, development and logging purposes you may want to subscribe to all error messages
in your application as well. You just need to autowire `ConsumerAggregator` and subscribe to `onError` method.

```java
@Service
public class PulsarErrorHandler {

    @Autowired
    private ConsumerAggregator aggregator;

    @EventListener(ApplicationReadyEvent.class)
    public void pulsarErrorHandler() {
        aggregator.onError(failedMessage ->
                failedMessage.getException()
                        .printStackTrace());
    }
}
```

#### 5. Reactor support (Flux)

If you wish to use reactor core for your project, it's possible with using different flow of consumer creation as you can see below.

1. First, you need to create a configuration class where you configure and register your consumer beans.

```java
@Configuration
public class MyFluxConsumers {
    
    @Autowired
    private FluxConsumerFactory fluxConsumerFactory;

    @Bean
    public FluxConsumer<MyMsg> myFluxConsumer() {
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

2. You simply autowire your bean and subscribe to your reactor stream.

```java
@Service
public class MyFluxConsumerService {
    
    @Autowired
    private FluxConsumer<MyMsg> myFluxConsumer;

    @EventListener(ApplicationReadyEvent.class)
    public void subscribe() {
        myFluxConsumer
            .asSimpleFlux()
            .subscribe(msg -> System.out.println(msg.getData()));
    }
}
```

3. (Optional) If you wish to acknowledge your messages manually you can configure your consumers a bit differently.

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
@Service
public class MyFluxConsumerService {
    
    @Autowired
    private FluxConsumer<MyMsg> myFluxConsumer;

    @EventListener(ApplicationReadyEvent.class)
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

#### 6. Interceptor - Adding default or custom consumer or producer interceptors

You can register your own interceptors and use it for example with some additional logging.
First, you need to allow default interceptor that already have some `DEBUG` level logging in place.

```properties
pulsar.allow-interceptor=true
```

For custom interceptor you need to create a bean that extends the `DefaultConsumerInterceptor`. Example usage:

*Consumer Interceptor Example:*
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
*Producer Interceptor Example:*
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

## API & Monitoring

Project implements Prometheus metrics using Micrometer.
Simply switch the interceptor on, and you will be able to connect to project with
prometheus endpoints with many custom counters that will help you monitor your application.
You need to allow interceptors in project.

Interceptor configuration:
```
pulsar.allow-interceptor=true
```

## Contributing

All contributors are welcome. If you never contributed to the open-source, start with reading the [Github Flow](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/github-flow).

### Roadmap task
1. Pick a task from [issues](https://github.com/majusko/pulsar-java-spring-boot-starter/issues) section.
2. Create a [pull request](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/about-pull-requests) with reference (url) to the task inside the [Projects](https://github.com/majusko/pulsar-java-spring-boot-starter/projects) section.
3. Rest and enjoy the great feeling of being a contributor.

### Hotfix
1. Create an [issue](https://help.github.com/en/github/managing-your-work-on-github/about-issues)
2. Create a [pull request](https://help.github.com/en/github/collaborating-with-issues-and-pull-requests/about-pull-requests) with reference to the issue
3. Rest and enjoy the great feeling of being a contributor.
