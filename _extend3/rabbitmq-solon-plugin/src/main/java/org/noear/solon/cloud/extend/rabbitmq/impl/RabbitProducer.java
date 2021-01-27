package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.noear.snack.ONode;
import org.noear.solon.cloud.model.Event;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 生产者
 *
 * @author noear
 * @since 1.3
 */
public class RabbitProducer {
    private RabbitConfig config;
    private Channel channel;
    private RabbitChannelFactory factory;
    private AMQP.BasicProperties rabbit_props;

    public RabbitProducer(RabbitChannelFactory factory) {
        this.config = factory.getConfig();
        this.factory = factory;
        this.rabbit_props = new AMQP.BasicProperties().builder()
                .deliveryMode(2)
                .contentEncoding("UTF-8")
                .build();
    }

    /**
     * 绑定
     */
    public void bind() throws IOException, TimeoutException {
        channel = factory.getChannel();

        //
        // 与生产者有关
        //


        Map<String, Object> args = new HashMap<>();

        //1.声明交换机(String exchange:交换机名 , String type:交换机类型 , boolean durable:是否持久化 , boolean autoDelete:是否自动删除 , boolean internal:是否是内部交换机, Map arguments:交换机属性) throws IOException ;
        channel.exchangeDeclare(config.exchangeName,
                config.exchangeType,
                config.durable,
                config.autoDelete,
                config.internal, args);
    }

    public void publish(Event event, String topic) throws IOException {
        byte[] event_data = ONode.stringify(event).getBytes(StandardCharsets.UTF_8);

        //basicPublish::String exchange, String routingKey, boolean mandatory, BasicProperties props, byte[] body
        //
        channel.basicPublish(config.exchangeName, topic, config.mandatory, rabbit_props, event_data);
    }

    /**
     * 发布事件
     */
    public void publish(Event event) throws IOException {
        if (event.scheduled() != null) {

        } else {
            if (config.exchangeType == BuiltinExchangeType.FANOUT) {
                publish(event, "");
            } else {
                publish(event, event.topic());
            }
        }
    }
}
