package org.noear.solon.cloud.extend.rabbitmq;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class RabbitmqProps {
    static String EVENT_VIRTUAL_HOST = "solon.cloud.rocketmq.event.virtualHost";

    public static final CloudProps instance = new CloudProps("rocketmq");

    public static String getEventVirtualHost() {
        return Solon.cfg().get(EVENT_VIRTUAL_HOST);
    }
}
