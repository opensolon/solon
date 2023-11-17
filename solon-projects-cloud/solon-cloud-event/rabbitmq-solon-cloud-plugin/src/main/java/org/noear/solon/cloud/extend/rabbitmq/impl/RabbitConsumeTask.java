package org.noear.solon.cloud.extend.rabbitmq.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.rabbitmq.RabbitmqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.utils.ExpirationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消费者接收处理任务
 *
 * @author noear
 * @since 2.6
 */
public class RabbitConsumeTask implements Runnable{
    static final Logger log = LoggerFactory.getLogger(RabbitConsumeTask.class);

    private String consumerTag;
    private Envelope envelope;
    private AMQP.BasicProperties properties;
    private byte[] body;

    private RabbitConsumeHandler master;

    public RabbitConsumeTask(RabbitConsumeHandler master, String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
        this.consumerTag = consumerTag;
        this.envelope = envelope;
        this.properties = properties;
        this.body = body;

        this.master = master;
    }

    @Override
    public void run() {
        try {
            String event_json = new String(body);
            Event event = ONode.deserialize(event_json, Event.class);
            event.channel(master.eventChannelName);

            boolean isOk = onReceive(event); //吃掉异常，方便下面的动作

            if (isOk == false) {
                event.times(event.times() + 1);

                try {
                    isOk = master.producer.publish(event, master.config.queue_ready, ExpirationUtils.getExpiration(event.times()));
                } catch (Throwable ex) {
                    master.getChannel().basicNack(envelope.getDeliveryTag(), false, true);
                    isOk = true;
                }
            }

            if (isOk) {
                master.getChannel().basicAck(envelope.getDeliveryTag(), false);
            }

        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);

            log.warn(e.getMessage(), e);
        }
    }

    /**
     * 处理接收事件（会吃掉异常）
     * */
    private boolean onReceive(Event event) throws Throwable {
        try {
            return onReceiveDo(event);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 处理接收事件
     */
    private boolean onReceiveDo(Event event) throws Throwable {
        boolean isOk = true;
        CloudEventHandler handler = null;

        //new topic
        String topicNew;
        if (Utils.isEmpty(event.group())) {
            topicNew = event.topic();
        } else {
            topicNew = event.group() + RabbitmqProps.GROUP_SPLIT_MARK + event.topic();
        }

        handler = master.observerManger.getByTopic(topicNew);
        if (handler != null) {
            isOk = handler.handle(event);
        } else {
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", topicNew);
        }

        return isOk;
    }
}
