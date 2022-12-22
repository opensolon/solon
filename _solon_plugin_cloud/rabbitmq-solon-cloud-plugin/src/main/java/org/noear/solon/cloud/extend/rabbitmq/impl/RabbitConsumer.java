package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.Channel;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.service.CloudEventObserverManger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author noear
 * @since 1.3
 */
public class RabbitConsumer {
    private CloudProps cloudProps;
    private RabbitConfig cfg;
    private Channel channel;
    private RabbitChannelFactory factory;
    private RabbitConsumeHandler handler;
    private RabbitProducer producer;


    public RabbitConsumer(CloudProps cloudProps, RabbitProducer producer, RabbitChannelFactory factory) {
        this.cloudProps = cloudProps;
        this.cfg = factory.getConfig();
        this.factory = factory;
        this.producer = producer;
    }

    /**
     * 初始化
     */
    public void init(CloudEventObserverManger observerManger) throws IOException, TimeoutException {
        channel = factory.getChannel();
        handler = new RabbitConsumeHandler(cloudProps, producer, cfg, channel, observerManger);

        int prefetchCount = factory.getCloudProps().getEventPrefetchCount();
        if (prefetchCount < 1) {
            prefetchCount = 10;
        }

        //1.申明同时接收数量
        channel.basicQos(prefetchCount);

        //2.申明队列
        queueDeclareNormal(observerManger);
        queueDeclareReady();
        queueDeclareRetry();
    }


    /**
     * 申明常规队列
     */
    private void queueDeclareNormal(CloudEventObserverManger observerManger) throws IOException {
        Map<String, Object> args = new HashMap<>();

        channel.queueDeclare(cfg.queue_normal, cfg.durable, cfg.exclusive, cfg.autoDelete, args);

        for (String topic : observerManger.topicAll()) {
            channel.queueBind(cfg.queue_normal, cfg.exchangeName, topic, args);
        }

        channel.basicConsume(cfg.queue_normal, handler);
    }

    /**
     * 申明定时队列
     */
    private void queueDeclareReady() throws IOException {
        Map<String, Object> args = new HashMap<>();
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
