package io.github.majusko.pulsar2.solon.annotation;

import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;

import io.github.majusko.pulsar2.solon.constant.BatchAckMode;
import io.github.majusko.pulsar2.solon.constant.Serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/**
 * 消费者的方法注解
 * @author Administrator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PulsarConsumer {
    String topic();

    Class<?> clazz() default byte[].class;

    Serialization serialization() default Serialization.JSON;

    /**
     * Type of subscription.
     *
     * Shared - This will allow you to have multiple consumers/instances of the application in a cluster with same subscription
     * name and guarantee that the message is read only by one consumer.
     *
     * Exclusive - message will be delivered to every subscription name only once but won't allow to instantiate multiple
     * instances or consumers of the same subscription name. With a default configuration you don't need to worry about horizontal
     * scaling because message will be delivered to each pod in a cluster since in case of exclusive subscription
     * the name is unique per instance and can be nicely used to update state of each pod in case your service
     * is stateful (For example - you need to update in-memory cached configuration for each instance of authorization microservice).
     *
     * By default the type is `Exclusive` but you can also override the default in `application.properties`.
     * This can be handy in case you are using `Shared` subscription in your application all the time and you
     * don't want to override this value every time you use `@PulsarConsumer`.
     */
    SubscriptionType[] subscriptionType() default {};

    /**
     * (Optional) Consumer names are auto-generated but in case you wish to use your custom consumer names,
     * feel free to override it.
     */
    String consumerName() default "";

    /**
     * (Optional) Subscription names are auto-generated but in case you wish to use your custom subscription names,
     * feel free to override it.
     */
    String subscriptionName() default "";

    /**
     * Maximum number of times that a message will be redelivered before being sent to the dead letter queue.
     * Note: Currently, dead letter topic is enabled only in the shared subscription mode.
     */
    int maxRedeliverCount() default -1;

    /**
     * Name of the dead topic where the failing messages will be sent.
     */
    String deadLetterTopic() default "";

    /**
     * If value is set to true, the consumer will autostart on application startup automatically.
     * When the value is set to false, consumer will not subscribe to the topic.
     * By default, the value is `true`
     */
    boolean autoStart() default true;

    /**
     * Set the namespace, which is set in the configuration file by default.
     * After the setting here, it shall prevail. It is mainly used for multiple namespaces in one project.
     */
    String namespace() default "";

    /**
     * When creating a consumer, if the subscription does not exist, a new subscription will be created.
     * By default, the subscription will be created at the end of the topic (Latest).
     */
    SubscriptionInitialPosition initialPosition() default SubscriptionInitialPosition.Latest;
    
    /**
     * Set batch receive mode to true. If set to true consumer method receives multiple messages.
     * Consumer method's parameter should be in org.apache.pulsar.client.api.Messages<?> type.   
     */
    boolean batch() default false;
    
    
    /**
     * This attribute is only considered when batch attribute is set to true.
     * It gives user to manually acknowledge or negative acknowledge messages.
     * When you set this attribute to BatchAckMode.MANUAL, you should acknowledge
     * messages otherwise you will duplicate process messages. You should also define
     * your a second parameter of org.apache.pulsar.client.api.Consumer<?> type. You
     * will use Consumer object to give acknowledge or negative acknowledge.
     * 
     * If this value set to BatchAckMode.AUTO then it will acknowledge all messages.
     * if no exception occurs. If any exception occurs or thrown then all messages would be
     * negative acknowledged.
     */
    BatchAckMode batchAckMode() default BatchAckMode.AUTO;
    
    /**
     * This attribute is only considered when batch attribute is set to true.
     * 
     * 1.If set maxNumMessages = 10, maxNumBytes = 1MB and without timeout, it means Consumer.batchReceive()
     * will always wait until there is enough messages. 
     * 
     * 2.If set maxNumMessages = 0, maxNumBytes = 0 and timeout = 100ms, it means Consumer.batchReceive() 
     * will waiting for 100ms whether or not there is enough messages.
     */
    int maxNumMessage() default -1;
    
    /**
     * This attribute is only considered when batch attribute is set to true.
     * 
     * 1.If set maxNumMessages = 10, maxNumBytes = 1MB and without timeout, it means Consumer.batchReceive()
     * will always wait until there is enough messages. 
     * 
     * 2.If set maxNumMessages = 0, maxNumBytes = 0 and timeout = 100ms, it means Consumer.batchReceive() 
     * will waiting for 100ms whether or not there is enough messages.
     */
    int maxNumBytes() default 10 * 1024 * 1024;
    
    /**
     * This attribute is only considered when batch attribute is set to true.
     * 
     * 1.If set maxNumMessages = 10, maxNumBytes = 1MB and without timeout, it means Consumer.batchReceive()
     * will always wait until there is enough messages. 
     * 
     * 2.If set maxNumMessages = 0, maxNumBytes = 0 and timeout = 100ms, it means Consumer.batchReceive() 
     * will waiting for 100ms whether or not there is enough messages.
     */
    int timeoutMillis() default 100;

    /**
     * Subscription properties. You can use this to filter the results.
     */
    public SubscriptionProp[] subscriptionProperties() default {};
}
