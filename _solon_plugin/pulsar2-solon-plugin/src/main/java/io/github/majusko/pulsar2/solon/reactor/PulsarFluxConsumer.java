package io.github.majusko.pulsar2.solon.reactor;

import com.google.common.base.Strings;

import io.github.majusko.pulsar2.solon.constant.Serialization;
import io.github.majusko.pulsar2.solon.error.exception.ClientInitException;

import org.apache.pulsar.client.api.SubscriptionInitialPosition;
import org.apache.pulsar.client.api.SubscriptionType;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

public class PulsarFluxConsumer<T> implements FluxConsumer<T> {

    private final Sinks.Many<T> simpleSink;

    private final Sinks.Many<FluxConsumerHolder> robustSink;

    private final String topic;

    private final Class<?> messageClass;

    private final Serialization serialization;

    private final SubscriptionType subscriptionType;

    private final String consumerName;

    private final String subscriptionName;

    private final int maxRedeliverCount;

    private final String deadLetterTopic;

    private final boolean simple;

    private final String namespace;

    private final SubscriptionInitialPosition initialPosition;

    private PulsarFluxConsumer(
        String topic,
        Class<?> messageClass,
        Serialization serialization,
        SubscriptionType subscriptionType,
        String consumerName,
        String subscriptionName,
        int maxRedeliverCount,
        String deadLetterTopic,
        boolean simple,
        SubscriptionInitialPosition initialPosition,
        int backPressureBufferSize,
        String namespace) {
        this.simpleSink = Sinks.many().multicast().onBackpressureBuffer(backPressureBufferSize, false);
        this.robustSink = Sinks.many().multicast().onBackpressureBuffer(backPressureBufferSize, false);
        this.topic = topic;
        this.messageClass = messageClass;
        this.serialization = serialization;
        this.subscriptionType = subscriptionType;
        this.consumerName = consumerName;
        this.subscriptionName = subscriptionName;
        this.maxRedeliverCount = maxRedeliverCount;
        this.deadLetterTopic = deadLetterTopic;
        this.simple = simple;
        this.initialPosition = initialPosition;
        this.namespace = namespace;
    }

    public String getTopic() {
        return topic;
    }

    public Class<?> getMessageClass() {
        return messageClass;
    }

    public Serialization getSerialization() {
        return serialization;
    }

    public SubscriptionType getSubscriptionType() {
        return subscriptionType;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public String getSubscriptionName() {
        return subscriptionName;
    }

    public int getMaxRedeliverCount() {
        return maxRedeliverCount;
    }

    public String getDeadLetterTopic() {
        return deadLetterTopic;
    }

    public boolean isSimple() {
        return simple;
    }

    public String getNamespace() {
        return namespace;
    }

    public SubscriptionInitialPosition getInitialPosition() {
        return initialPosition;
    }

    public Sinks.EmitResult simpleEmit(T msg) {
        return simpleSink.tryEmitNext(msg);
    }

    public Sinks.EmitResult simpleEmitError(Throwable error) {
        return simpleSink.tryEmitError(error);
    }

    public Flux<T> asSimpleFlux() {
        return simpleSink.asFlux();
    }

    public Flux<FluxConsumerHolder> asFlux() {
        return robustSink.asFlux();
    }

    public Sinks.EmitResult emit(FluxConsumerHolder msg) {
        return robustSink.tryEmitNext(msg);
    }

    public Sinks.EmitResult emitError(Throwable error) {
        return robustSink.tryEmitError(error);
    }

    public static FluxConsumerBuilder builder() {
        return new FluxConsumerBuilder();
    }

    public static class FluxConsumerBuilder {
        private String topic;

        private Class<?> messageClass = byte[].class;

        private Serialization serialization = Serialization.JSON;

        /**
         * (Optional) Type of subscription.
         * <p>
         * Shared - This will allow you to have multiple consumers/instances of the application in a cluster with same subscription
         * name and guarantee that the message is read only by one consumer.
         * <p>
         * Exclusive - message will be delivered to every subscription name only once but won't allow to instantiate multiple
         * instances or consumers of the same subscription name. With a default configuration you don't need to worry about horizontal
         * scaling because message will be delivered to each pod in a cluster since in case of exclusive subscription
         * the name is unique per instance and can be nicely used to update state of each pod in case your service
         * is stateful (For example - you need to update in-memory cached configuration for each instance of authorization microservice).
         * <p>
         * By default the type is `Exclusive` but you can also override the default in `application.properties`.
         * This can be handy in case you are using `Shared` subscription in your application all the time and you
         * don't want to override this value every time you use `@PulsarConsumer`.
         */
        private SubscriptionType subscriptionType = null;

        /**
         * Flux consumer names are NOT auto-generated. Choose your consumer name.
         */
        private String consumerName = "";

        /**
         * Flux subscription names are NOT auto-generated. Choose your subscription name.
         */
        private String subscriptionName = "";

        /**
         * Maximum number of times that a message will be redelivered before being sent to the dead letter queue.
         * Note: Currently, dead letter topic is enabled only in the shared subscription mode.
         */
        private int maxRedeliverCount = -1;

        /**
         * Name of the dead topic where the failing messages will be sent.
         */
        private String deadLetterTopic = "";

        /**
         * Define rather you wish to use simple subscription or you wish to handle negative acknowledges.
         * By default, the simple subscription is used.
         */
        private boolean simple = true;

        /**
         * Set the namespace, which is set in the configuration file by default.
         * After the setting here, it shall prevail. It is mainly used for multiple namespaces in one project.
         */
        private String namespace;

        /**
         * When creating a consumer, if the subscription does not exist, a new subscription will be created.
         * By default, the subscription will be created at the end of the topic (Latest).
         */
        private SubscriptionInitialPosition initialPosition = SubscriptionInitialPosition.Latest;

        /**
         * You can override the default buffer size for the backpressure behaviour of the reactor
         * core used for consumers.
         */
        private int backPressureBufferSize = Queues.SMALL_BUFFER_SIZE;

        public FluxConsumerBuilder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public FluxConsumerBuilder setMessageClass(Class<?> messageClass) {
            this.messageClass = messageClass;
            return this;
        }

        public FluxConsumerBuilder setSerialization(Serialization serialization) {
            this.serialization = serialization;
            return this;
        }

        public FluxConsumerBuilder setSubscriptionType(SubscriptionType subscriptionType) {
            this.subscriptionType = subscriptionType;
            return this;
        }

        public FluxConsumerBuilder setConsumerName(String consumerName) {
            this.consumerName = consumerName;
            return this;
        }

        public FluxConsumerBuilder setSubscriptionName(String subscriptionName) {
            this.subscriptionName = subscriptionName;
            return this;
        }

        public FluxConsumerBuilder setMaxRedeliverCount(int maxRedeliverCount) {
            this.maxRedeliverCount = maxRedeliverCount;
            return this;
        }

        public FluxConsumerBuilder setDeadLetterTopic(String deadLetterTopic) {
            this.deadLetterTopic = deadLetterTopic;
            return this;
        }

        public FluxConsumerBuilder setSimple(boolean simple) {
            this.simple = simple;
            return this;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public FluxConsumerBuilder setInitialPosition(SubscriptionInitialPosition initialPosition) {
            this.initialPosition = initialPosition;
            return this;
        }

        public FluxConsumerBuilder setBackPressureBufferSize(int backPressureBufferSize) {
            this.backPressureBufferSize = backPressureBufferSize;
            return this;
        }

        public <T> PulsarFluxConsumer<T> build() throws ClientInitException {
            validateBuilder();

            return new PulsarFluxConsumer<>(topic, messageClass, serialization, subscriptionType, consumerName, subscriptionName, maxRedeliverCount, deadLetterTopic, simple, initialPosition, backPressureBufferSize, namespace);
        }

        private void validateBuilder() throws ClientInitException {
            if (Strings.isNullOrEmpty(topic)) {
                throw new ClientInitException("Topic is empty");
            }
            if (Strings.isNullOrEmpty(consumerName)) {
                throw new ClientInitException("Consumer name is empty");
            }
            if (Strings.isNullOrEmpty(subscriptionName)) {
                throw new ClientInitException("Subscription name is empty");
            }
        }
    }
}
