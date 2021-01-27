package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.BuiltinExchangeType;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;

/**
 * Rabbit 配置
 *
 * @author noear
 * @since 1.3
 */
public class RabbitConfig {
    /**
     * 交换器名称
     */
    public String exchangeName;
    /**
     * 交换器类型
     * <p>
     * Fanout Exchange：给所有队列发
     * Direct Exchange：只给routingKey相关的队列发（更适合事件总线）
     * Topic Exchange：只给routingKey相关的队列发，但是支持路由键的模糊匹配
     */
    public BuiltinExchangeType exchangeType = BuiltinExchangeType.DIRECT;

    /**
     * 是否持久化
     */
    public boolean rabbit_durable = true;
    /**
     * 是否自动删除
     */
    public boolean rabbit_autoDelete = false;
    /**
     * 是否为内部
     */
    public boolean rabbit_internal = false;

    public boolean rabbit_mandatory = false;

    /**
     * 是否排它
     */
    public boolean consume_exclusive = false;
    /**
     * 消息时，是否自动应答
     */
    public boolean consume_autoAck = false;

    /**
     * 服务器地址
     */
    public String server;
    /**
     * 用户名
     */
    public String username;
    /**
     * 密码
     */
    public String password;

    public RabbitConfig() {
        exchangeName = RabbitmqProps.instance.getEventBroker();
        if (Utils.isEmpty(exchangeName)) {
            exchangeName = "DEFAULT";
        }
    }
}
