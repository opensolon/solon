package org.noear.solon.cloud.extend.rocketmq;

import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class RocketmqProps {
    public static final String GROUP_SPLIT_MART = "--";

    public static final String PROP_EVENT_namespace = "event.namespace";
    public static final String PROP_EVENT_consumerGroup = "event.consumerGroup";
    public static final String PROP_EVENT_producerGroup = "event.producerGroup";

    public static final CloudProps instance = new CloudProps("rocketmq");
}
