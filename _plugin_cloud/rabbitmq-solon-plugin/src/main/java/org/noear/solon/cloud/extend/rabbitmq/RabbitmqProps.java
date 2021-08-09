package org.noear.solon.cloud.extend.rabbitmq;

import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudProps;

/**
 * @author noear
 * @since 1.2
 */
public class RabbitmqProps {
    static final String EVENT_VIRTUAL_HOST = "solon.cloud.rabbitmq.event.virtualHost";
    static final String EVENT_EXCHANGE = "solon.cloud.rabbitmq.event.exchange";
    static final String EVENT_QUEUE = "solon.cloud.rabbitmq.event.queue";

    public static final CloudProps instance = new CloudProps("rabbitmq");


    /**
     * 交换机
     * */
    public static String getEventExchange() {
        return Solon.cfg().get(EVENT_EXCHANGE);
    }

    /**
     * 队列
     * */
    public static String getEventQueue(){
        return Solon.cfg().get(EVENT_QUEUE);
    }

    /**
     * 虚拟主机
     * */
    public static String getEventVirtualHost() {
        return Solon.cfg().get(EVENT_VIRTUAL_HOST);
    }
}
