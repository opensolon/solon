package org.noear.solon.cloud.extend.rocketmq.impl;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.rocketmq.RocketmqProps;

/**
 * @author noear
 * @since 1.3
 */
public class RocketmqConfig {
    /**
     * 订阅组名
     */
    public String exchangeName;

    public String queueName;

    public String server;

    public RocketmqConfig() {
        this.exchangeName = RocketmqProps.instance.getEventExchange();

        if (Utils.isEmpty(exchangeName)) {
            exchangeName = "DEFAULT";
        }

        this.queueName = RocketmqProps.instance.getEventQueue();

        if (Utils.isEmpty(queueName)) {
            queueName = Solon.cfg().appGroup() + "_" + Solon.cfg().appName();
        }
    }
}
