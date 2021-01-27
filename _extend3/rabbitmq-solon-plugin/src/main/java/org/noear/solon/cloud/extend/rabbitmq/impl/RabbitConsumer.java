package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.Channel;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;
import org.noear.solon.cloud.service.CloudEventObserverEntity;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 1.3
 */
public class RabbitConsumer {
    private RabbitConfig config;
    private Channel channel;
    private RabbitChannelFactory factory;

    public RabbitConsumer(RabbitChannelFactory factory) {
        this.config = factory.getConfig();
        this.factory = factory;

        try {
            bind0();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 绑定
     */
    private void bind0() throws IOException, TimeoutException {
        channel = factory.newChannel();

        String queueName = RabbitmqProps.instance.getEventQueue();

        if (Utils.isEmpty(queueName)) {
            queueName = Solon.cfg().appGroup() + "_" + Solon.cfg().appName();
        }

        //1.声明队列 (队列名, 是否持久化, 是否排他, 是否自动删除, 队列属性);
        //String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        channel.queueDeclare(queueName,
                config.rabbit_durable,
                config.consume_exclusive,
                config.rabbit_autoDelete,
                new HashMap<>());

        //2.将队列Binding到交换机上 (队列名, 交换机名, Routing key, 绑定属性);
        //String queue, String exchange, String routingKey, Map<String, Object> arguments
        channel.queueBind(queueName,
                config.exchangeName,
                "*",
                new HashMap<>());

        //3.申明同时接收数量
        channel.basicQos(10);
    }

    /**
     * 绑定观察者
     */
    public void attach(CloudEventObserverEntity observer) throws IOException {
        RabbitConsumeHandler consumer = new RabbitConsumeHandler(config, channel, observer);
        channel.basicConsume(observer.topic, false, consumer);
    }
}
