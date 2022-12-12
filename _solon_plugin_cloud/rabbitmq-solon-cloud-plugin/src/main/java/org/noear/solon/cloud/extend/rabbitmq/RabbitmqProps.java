package org.noear.solon.cloud.extend.rabbitmq;

import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class RabbitmqProps {
    public static final String GROUP_SPLIT_MARK = ":";

    public static final String PROP_EVENT_virtualHost = "event.virtualHost";
    public static final String PROP_EVENT_exchange = "event.exchange";
    public static final String PROP_EVENT_queue = "event.queue";

    public static final CloudProps instance = new CloudProps("rabbitmq");
}
