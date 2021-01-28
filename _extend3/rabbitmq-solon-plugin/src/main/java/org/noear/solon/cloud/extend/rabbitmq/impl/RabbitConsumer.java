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


    public RabbitConsumer(RabbitProducer producer, RabbitChannelFactory factory) {
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

        //1.申明同时接收数量
        channel.basicQos(10);

        //2.申明队列
        queueDeclareNormal(observerMap);
        queueDeclareReady();
        queueDeclareRetry();
    }


    /**
     * 申明常规队列
     */
    private void queueDeclareNormal(Map<String, CloudEventObserverEntity> observerMap) throws IOException {
        Map<String, Object> args = new HashMap<>();

        channel.queueDeclare(cfg.queue_normal, cfg.durable, cfg.exclusive, cfg.autoDelete, args);

        for (String topic : observerMap.keySet()) {
            channel.queueBind(cfg.queue_normal, cfg.exchangeName, topic, args);
        }

        channel.basicConsume(cfg.queue_normal, handler);
    }

    /**
     * 申明定时队列
     */
    private void queueDeclareReady() throws IOException {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 10 * 1000);
        args.put("x-dead-letter-exchange", cfg.exchangeName);
        args.put("x-dead-letter-routing-key", cfg.queue_retry);

        channel.queueDeclare(cfg.queue_ready, cfg.durable, cfg.exclusive, cfg.autoDelete, args);
        channel.queueBind(cfg.queue_ready, cfg.exchangeName, cfg.queue_ready, args);
    }


    /**
     * 申明重试队列（由定时队列自动转入）
     */
    private void queueDeclareRetry() throws IOException {
        Map<String, Object> args = new HashMap<>();

        channel.queueDeclare(cfg.queue_retry, cfg.durable, cfg.exclusive, cfg.autoDelete, args);
        channel.queueBind(cfg.queue_retry, cfg.exchangeName, cfg.queue_retry, args);

        channel.basicConsume(cfg.queue_retry, handler);
    }
}
