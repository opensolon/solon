package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.Channel;
import org.noear.solon.cloud.service.CloudEventObserverEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 1.3
 */
public class RabbitConsumer {
    private RabbitConfig cfg;
    private Channel channel;
    private RabbitChannelFactory factory;
    private RabbitConsumeHandler handler;
    private RabbitProducer producer;


    public RabbitConsumer(RabbitProducer producer,RabbitChannelFactory factory) {
        this.cfg = factory.getConfig();
        this.factory = factory;
        this.producer = producer;
    }

    /**
     * 绑定
     */
    public void bind(Map<String, CloudEventObserverEntity> observerMap) throws IOException, TimeoutException {
        channel = factory.getChannel();
        handler = new RabbitConsumeHandler(producer, cfg, channel, observerMap);

        Map<String, Object> args = new HashMap<>();

        Map<String, Object> args_retry = new HashMap<>();
        args_retry.put("x-message-ttl", 10000);
        args_retry.put("x-dead-letter-exchange", cfg.exchangeName);
        args_retry.put("x-dead-letter-routing-key", cfg.queue_retry);


        //1.申明同时接收数量
        channel.basicQos(10);

        //2.申明队列
        queueDeclareAndBind(cfg.queue_normal, args, observerMap);
        queueDeclareReady(cfg.queue_ready, args, observerMap);
        queueDeclareRetry(cfg.queue_retry, args_retry, observerMap);
        queueDeclareAndBind(cfg.queue_dead, args, observerMap);
    }

    private void queueDeclareReady(String queueName, Map<String, Object> args, Map<String, CloudEventObserverEntity> observerMap) throws IOException {
        //1.声明队列 (队列名, 是否持久化, 是否排他, 是否自动删除, 队列属性);
        //String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        channel.queueDeclare(queueName, cfg.durable, cfg.exclusive, cfg.autoDelete, args);

        channel.queueBind(queueName, cfg.exchangeName, cfg.queue_ready, args);
    }

    private void queueDeclareAndBind(String queueName, Map<String, Object> args, Map<String, CloudEventObserverEntity> observerMap) throws IOException {
        //1.声明队列 (队列名, 是否持久化, 是否排他, 是否自动删除, 队列属性);
        //String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        channel.queueDeclare(queueName, cfg.durable, cfg.exclusive, cfg.autoDelete, args);

        //2.将队列Binding到交换机上 (队列名, 交换机名, Routing key, 绑定属性);
        //String queue, String exchange, String routingKey, Map<String, Object> arguments
        for (String topic : observerMap.keySet()) {
            channel.queueBind(queueName, cfg.exchangeName, topic, args);
        }

        //3.设置消息代理接口
        channel.basicConsume(queueName, handler);
    }

    private void queueDeclareRetry(String queueName, Map<String, Object> args, Map<String, CloudEventObserverEntity> observerMap) throws IOException {
        //1.声明队列 (队列名, 是否持久化, 是否排他, 是否自动删除, 队列属性);
        //String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        channel.queueDeclare(queueName, cfg.durable, cfg.exclusive, cfg.autoDelete, args);

        //2.将队列Binding到交换机上 (队列名, 交换机名, Routing key, 绑定属性);
        //String queue, String exchange, String routingKey, Map<String, Object> arguments
        channel.queueBind(queueName, cfg.exchangeName, cfg.queue_retry, args);

        //3.设置消息代理接口
        channel.basicConsume(queueName, handler);
    }
}
