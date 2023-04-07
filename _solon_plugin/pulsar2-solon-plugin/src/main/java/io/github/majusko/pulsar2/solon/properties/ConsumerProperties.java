package io.github.majusko.pulsar2.solon.properties;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * pulsar2 consumer 消费者默认配置
 * @author Administrator
 *
 */
@Inject("${solon.pulsar2.consumer.default}")
@Configuration
public class ConsumerProperties {
    private int deadLetterPolicyMaxRedeliverCount = -1;
    private int ackTimeoutMs = 0;
    private String subscriptionType = "";

    public int getDeadLetterPolicyMaxRedeliverCount() {
        return deadLetterPolicyMaxRedeliverCount;
    }

    public void setDeadLetterPolicyMaxRedeliverCount(int deadLetterPolicyMaxRedeliverCount) {
        this.deadLetterPolicyMaxRedeliverCount = deadLetterPolicyMaxRedeliverCount;
    }

    public int getAckTimeoutMs() {
        return ackTimeoutMs;
    }

    public void setAckTimeoutMs(int ackTimeoutMs) {
        this.ackTimeoutMs = ackTimeoutMs;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }
}