package org.noear.solon.cloud.extend.rocketmq.impl;

import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.message.MessageBuilder;
import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Event;

import java.nio.charset.StandardCharsets;

/**
 * @author noear
 * @since 1.3
 */
class MessageUtil {
    public static Message buildNewMeaage(ClientServiceProvider producer, Event event, String topic) {
        String topicNew = topic.replace(".", "_");

        if (Utils.isEmpty(event.key())) {
            event.key(Utils.guid());
        }

        MessageBuilder messageBuilder = producer.newMessageBuilder();

        messageBuilder.setTopic(topicNew)
                //设置消息索引键，可根据关键字精确查找某条消息。
                .setKeys(event.key())
                //设置消息Tag，用于消费端根据指定Tag过滤消息。
                .setTag(event.tags())
                //消息体。
                .setBody(event.content().getBytes(StandardCharsets.UTF_8));


        if (event.scheduled() != null) {
            long delayTimestamp = event.scheduled().getTime() - System.currentTimeMillis();
            messageBuilder.setDeliveryTimestamp(delayTimestamp);
        }

        return messageBuilder.build();

    }
}
