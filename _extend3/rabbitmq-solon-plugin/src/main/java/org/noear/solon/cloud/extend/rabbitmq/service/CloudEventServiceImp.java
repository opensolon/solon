package org.noear.solon.cloud.extend.rabbitmq.service;

import com.rabbitmq.client.*;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverEntity;
import org.noear.solon.cloud.service.CloudEventService;
import org.noear.solon.core.event.EventBus;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.2
 */
public class CloudEventServiceImp implements CloudEventService {
    RabbitMQX rabbitMQX;

    /**
     * 交换器名称
     */
    String rabbit_exchangeName;
    /**
     * 路由KEY
     */
    String rabbit_routingKey;

    String rabbit_queueName;
    /**
     * 类型
     */
    final BuiltinExchangeType rabbit_type = BuiltinExchangeType.DIRECT;
    /**
     * 是否持久化
     */
    final boolean rabbit_durable = true;
    /**
     * 是否自动删除
     */
    final boolean rabbit_autoDelete = false;
    /**
     * 是否为内部
     */
    final boolean rabbit_internal = false;

    /**
     * 消息属性
     */
    final AMQP.BasicProperties rabbit_msgProps;

    Channel channel;

    public CloudEventServiceImp(String server) {
        rabbit_exchangeName = Solon.cfg().appGroup();
        if (Utils.isEmpty(rabbit_exchangeName)) {
            rabbit_exchangeName = "DEFAULT_GROUP";
        }

        rabbit_routingKey = rabbit_exchangeName;

        rabbit_queueName = RabbitmqProps.instance.getEventQueue();

        rabbit_msgProps = new AMQP.BasicProperties().builder()
                .deliveryMode(2).contentType("UTF-8").build();

        try {
            rabbitMQX = new RabbitMQX(
                    server,
                    RabbitmqProps.instance.getUsername(),
                    RabbitmqProps.instance.getPassword());

            channel = rabbitMQX.createChannel();

            initDeclareAndBind(channel);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void initDeclareAndBind(Channel channel) throws IOException {
        //1.声明交换机(String exchange:交换机名 , String type:交换机类型 , boolean durable:是否持久化 , boolean autoDelete:是否自动删除 , boolean internal:是否是内部交换机, Map arguments:交换机属性) throws IOException ;
        channel.exchangeDeclare(rabbit_exchangeName, rabbit_type, rabbit_durable, rabbit_autoDelete, rabbit_internal, new HashMap<>());

        //2.声明队列 (队列名, 是否持久化, 是否排他, 是否自动删除, 队列属性);
        channel.queueDeclare(rabbit_queueName, rabbit_durable, rabbit_autoDelete, false, new HashMap<>());

        //3.将队列Binding到交换机上 (队列名, 交换机名, Routing key, 绑定属性);
        channel.queueBind(rabbit_queueName, rabbit_exchangeName, rabbit_routingKey, new HashMap<>());
    }

    @Override
    public boolean publish(Event event) {
        // 设置消息属性 发布消息 (exchange:交换机名, Routing key, props:消息属性, body:消息体);
        try {
            channel.basicPublish(
                    rabbit_exchangeName,
                    rabbit_routingKey,
                    false,
                    rabbit_msgProps,
                    event.content().getBytes(StandardCharsets.UTF_8));

            return true;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    Map<String, CloudEventObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(EventLevel level, String group, String topic, CloudEventHandler observer) {
        if (observerMap.containsKey(topic)) {
            return;
        }

        observerMap.put(topic, new CloudEventObserverEntity(level, group, topic, observer));
    }

    /**
     * 处理接收事件
     */
    public boolean onReceive(Event event) throws Throwable {
        boolean isOk = true;
        CloudEventObserverEntity entity = null;

        entity = observerMap.get(event.topic());
        if (entity != null) {
            isOk = entity.handler(event);
        }

        return isOk;
    }

    public void subscribe() {
        try {
            for (String topic : observerMap.keySet()) {
                RabbitConsumer consumer = new RabbitConsumer(channel, topic, this);
                channel.basicConsume(topic, consumer);
            }
        } catch (IOException ex) {
            EventBus.push(ex);
        }
    }
}
