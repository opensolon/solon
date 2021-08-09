package org.noear.solon.cloud.extend.rocketmq;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class RocketmqProps {
    private static final String EVENT_CONSUMERGROUP = "solon.cloud.rocketmq.event.consumerGroup";
    private static final String EVENT_PRODUCERGROUP = "solon.cloud.rocketmq.event.producerGroup";

    public static final CloudProps instance = new CloudProps("rocketmq");

    public static String consumerGroup() {
        return Solon.cfg().get(EVENT_CONSUMERGROUP);
    }

    public static String producerGroup() {
        return Solon.cfg().get(EVENT_PRODUCERGROUP);
    }
}
