package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.BuiltinExchangeType;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rabbit 配置
 *
 * @author noear
 * @since 1.3
 * @since 2.4
 */
public class RabbitConfig {
    static final Logger log = LoggerFactory.getLogger(RabbitConfig.class);
    /**
     * 虚拟主机
     */
    public final String virtualHost;
    /**
     * 交换器名称
     */
    public final String exchangeName;
    /**
     * 交换器类型
     */
    public final BuiltinExchangeType exchangeType = BuiltinExchangeType.DIRECT;

    /**
     * 是否持久化
     */
    public final boolean durable = true;
    /**
     * 是否自动删除
     */
    public final boolean autoDelete = false;
    /**
     * 是否为内部
     */
    public final boolean internal = false;

    /**
     * 标志告诉服务器至少将该消息route到一个队列中，否则将消息返还给生产者
     */
    public final boolean mandatory = false;

    /**
     * 标志告诉服务器如果该消息关联的queue上有消费者，则马上将消息投递给它；
     * 如果所有queue都没有消费者，直接把消息返还给生产者，不用将消息入队列等待消费者了。
     */
    public final boolean immediate = false;

    /**
     * 是否排它
     */
    public final boolean exclusive = false;

    /**
     * 服务器地址
     */
    public final String server;
    /**
     * 用户名
     */
    public final String username;
    /**
     * 密码
     */
    public final String password;

    public final String queue_normal;
    public final String queue_ready;
    public final String queue_retry;

    /**
     * 发布超时
     * */
    public final long publishTimeout;
    /**
     *
     * */
    public final int prefetchCount;

    private final CloudProps cloudProps;

    public RabbitConfig(CloudProps cloudProps) {
        this.cloudProps = cloudProps;

        publishTimeout = cloudProps.getEventPublishTimeout();
        prefetchCount = getPrefetchCountInternal();

        server = cloudProps.getEventServer();
        username = cloudProps.getEventUsername();
        password = cloudProps.getEventPassword();

        virtualHost = getVirtualHostInternal();

        exchangeName = getEventExchangeInternal();

        String queueName = getEventQueueInternal();
        if (Utils.isEmpty(queueName)) {
            queueName = exchangeName + "_" + Solon.cfg().appName();
        }

        queue_normal = queueName + "@normal";
        queue_ready = queueName + "@ready";
        queue_retry = queueName + "@retry";

        log.trace("queue_normal=" + queue_normal);
        log.trace("queue_ready=" + queue_ready);
        log.trace("queue_retry=" + queue_retry);
    }


    public String getEventChannel(){
        return cloudProps.getEventChannel();
    }

    private int getPrefetchCountInternal(){
        int tmp = cloudProps.getEventPrefetchCount();
        if (tmp < 1) {
            tmp = 10;
        }
        return tmp;
    }

    private String getVirtualHostInternal() {
        String tmp = cloudProps.getValue(RabbitmqProps.PROP_EVENT_virtualHost);

        if (Utils.isEmpty(tmp)) {
            return cloudProps.getNamespace();
        } else {
            return tmp;
        }
    }

    /**
     * 交换机
     */
    private String getEventExchangeInternal() {
        String tmp = cloudProps.getValue(RabbitmqProps.PROP_EVENT_exchange);

        if (Utils.isEmpty(tmp)) {
            return Solon.cfg().appGroup();
        } else {
            return tmp;
        }
    }

    /**
     * 队列
     */
    private String getEventQueueInternal() {
        return cloudProps.getValue(RabbitmqProps.PROP_EVENT_queue);
    }
}
