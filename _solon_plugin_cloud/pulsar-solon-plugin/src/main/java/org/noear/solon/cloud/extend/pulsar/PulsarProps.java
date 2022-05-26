package org.noear.solon.cloud.extend.pulsar;

import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.5
 */
public class PulsarProps {
    public static final String GROUP_SPLIT_MART = ":";

    public static final String PROP_EVENT_consumerGroup = "event.consumerGroup";
    public static final String PROP_EVENT_producerGroup = "event.producerGroup";

    public static final CloudProps instance = new CloudProps("pulsar");
}
