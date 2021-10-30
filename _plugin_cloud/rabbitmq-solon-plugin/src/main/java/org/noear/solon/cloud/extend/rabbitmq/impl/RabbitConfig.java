package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.BuiltinExchangeType;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
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
     */
    public BuiltinExchangeType exchangeType = BuiltinExchangeType.DIRECT;

    /**
     * 是否持久化
     */
    public boolean durable = true;
    /**
     * 是否自动删除
     */
    public boolean autoDelete = false;
    /**
     * 是否为内部
     */
    public boolean internal = false;

    /**
     * 标志告诉服务器至少将该消息route到一个队列中，否则将消息返还给生产者
     */
    public boolean mandatory = false;

    /**
     * 标志告诉服务器如果该消息关联的queue上有消费者，则马上将消息投递给它；
     * 如果所有queue都没有消费者，直接把消息返还给生产者，不用将消息入队列等待消费者了。
     */
    public boolean immediate = false;

    /**
     * 是否排它
     */
    public boolean exclusive = false;

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

    public String queue_normal;
    public String queue_ready;
    public String queue_retry;

    private final CloudProps cloudProps;

    public RabbitConfig(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        exchangeName = getEventExchange();
        if (Utils.isEmpty(exchangeName)) {
            exchangeName = "DEFAULT";
        }

        String queueName = getEventQueue();

        if (Utils.isEmpty(queueName)) {
            queueName = Solon.cfg().appGroup() + "_" + Solon.cfg().appName();
        }

        queue_normal = queueName + "@normal";
        queue_ready = queueName + "@ready";
        queue_retry = queueName + "@retry";
    }


    /**
     * 交换机
     */
    public String getEventExchange() {
        return cloudProps.getProp("event.exchange");
    }

    /**
     * 队列
     */
    public String getEventQueue() {
        return cloudProps.getProp("event.queue");
    }

}
