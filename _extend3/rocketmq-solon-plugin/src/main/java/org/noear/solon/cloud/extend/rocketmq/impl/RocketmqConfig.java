package org.noear.solon.cloud.extend.rocketmq.impl;

import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.rocketmq.RocketmqProps;

/**
 * @author noear 2021/1/28 created
 */
public class RocketmqConfig {
    /**
     * 订阅组名
     */
    public String exchangeName;

    public String server;

    public RocketmqConfig() {
        this.exchangeName = RocketmqProps.instance.getEventExchange();

        if (Utils.isEmpty(exchangeName)) {
            exchangeName = "DEFAULT";
        }
    }
}
