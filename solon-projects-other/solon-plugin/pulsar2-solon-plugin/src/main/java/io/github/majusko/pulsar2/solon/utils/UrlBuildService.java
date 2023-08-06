package io.github.majusko.pulsar2.solon.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.pulsar.client.api.ConsumerBuilder;
import org.apache.pulsar.client.api.DeadLetterPolicy;
import org.apache.pulsar.client.api.SubscriptionType;

import com.google.common.base.Strings;

import io.github.majusko.pulsar2.solon.collector.ConsumerHolder;
import io.github.majusko.pulsar2.solon.error.exception.ClientInitException;
import io.github.majusko.pulsar2.solon.properties.ConsumerProperties;
import io.github.majusko.pulsar2.solon.properties.PulsarProperties;

public class UrlBuildService {

    private final String consumerNameDelimiter;

    private static final String PERSISTENT_PREFIX = "persistent";
    private static final String NON_PERSISTENT_PREFIX = "non-persistent";
    private static final String DEFAULT_PERSISTENCE = PERSISTENT_PREFIX;
    private static final String CONSUMER_NAME_PREFIX = "consumer";
    private static final String SUBSCRIPTION_NAME_PREFIX = "subscription";
    private final static SubscriptionType DEFAULT_SUBSCRIPTION_TYPE = SubscriptionType.Exclusive;

    private final PulsarProperties pulsarProperties;
    private final ConsumerProperties consumerProperties;

    public UrlBuildService(PulsarProperties pulsarProperties, ConsumerProperties consumerProperties) {
        this.pulsarProperties = pulsarProperties;
        this.consumerProperties = consumerProperties;
        
        this.consumerNameDelimiter = pulsarProperties.getConsumerNameDelimiter();
    }

    public String buildTopicUrl(String topic) {
        return DEFAULT_PERSISTENCE + "://" + pulsarProperties.getTenant() + "/" + pulsarProperties.getNamespace() +
            "/" + topic;
    }

    public String buildTopicUrl(String topic, String namespace) {
        if (Strings.isNullOrEmpty(namespace)) {
            return buildTopicUrl(topic);
        }

        return DEFAULT_PERSISTENCE + "://" + pulsarProperties.getTenant() + "/" + namespace + "/" + topic;
    }

    public String buildPulsarConsumerName(String customConsumerName, String generatedConsumerName) {
        if (Strings.isNullOrEmpty(customConsumerName)) {
            return CONSUMER_NAME_PREFIX + consumerNameDelimiter + generatedConsumerName;
        }

        return customConsumerName;
    }

    public String buildPulsarSubscriptionName(String customSubscriptionName, String consumerName) {
        if (Strings.isNullOrEmpty(customSubscriptionName)) {
            return SUBSCRIPTION_NAME_PREFIX + consumerNameDelimiter + consumerName;
        }

        return customSubscriptionName;
    }

    public SubscriptionType getSubscriptionType(ConsumerHolder holder) throws ClientInitException {
        return getSubscriptionType(Arrays.stream(holder.getAnnotation().subscriptionType()).findFirst().orElse(null));
    }

    public SubscriptionType getSubscriptionType(SubscriptionType type) throws ClientInitException {
        if (type == null && Strings.isNullOrEmpty(consumerProperties.getSubscriptionType())) {
            type = DEFAULT_SUBSCRIPTION_TYPE;
        } else if (type == null && !Strings.isNullOrEmpty(consumerProperties.getSubscriptionType())) {
            try {
                type = SubscriptionType.valueOf(consumerProperties.getSubscriptionType());
            } catch (IllegalArgumentException exception) {
                throw new ClientInitException("There was unknown SubscriptionType.", exception);
            }
        }

        return type;
    }

    public void buildDeadLetterPolicy(int maxRedeliverCount, String deadLetterTopic, ConsumerBuilder<?> consumerBuilder) {
        DeadLetterPolicy.DeadLetterPolicyBuilder deadLetterBuilder = null;

        if (consumerProperties.getDeadLetterPolicyMaxRedeliverCount() >= 0) {
            deadLetterBuilder =
                DeadLetterPolicy.builder().maxRedeliverCount(consumerProperties.getDeadLetterPolicyMaxRedeliverCount());
        }

        if (maxRedeliverCount >= 0) {
            deadLetterBuilder =
                DeadLetterPolicy.builder().maxRedeliverCount(maxRedeliverCount);
        }

        if (deadLetterBuilder != null && !deadLetterTopic.isEmpty()) {
            deadLetterBuilder.deadLetterTopic(buildTopicUrl(deadLetterTopic));
        }

        if (deadLetterBuilder != null) {
            consumerBuilder.deadLetterPolicy(deadLetterBuilder.build());
        }
    }

    public String buildConsumerName(Class<?> clazz, Method method) {
        return clazz.getName() + consumerNameDelimiter + method.getName() + Arrays
            .stream(method.getGenericParameterTypes())
            .map(Type::getTypeName)
            .collect(Collectors.joining(consumerNameDelimiter));
    }
}
