package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.Message;
import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Event;

import java.nio.charset.StandardCharsets;

/**
 * @author cgy
 * @since 1.11
 */
class MessageUtil {
    public static Message buildNewMessage(Event event, String topic) {
        String topicNew = topic.replace(".", "_");

        if (Utils.isEmpty(event.key())) {
            event.key(Utils.guid());
        }

        Message message = new Message(
                topicNew,
                event.tags(),
                event.key(),
                event.content().getBytes(StandardCharsets.UTF_8));
        message.setKey(event.key());

        if (event.scheduled() != null) {
            long delayTimestamp = event.scheduled().getTime() - System.currentTimeMillis();
            message.setStartDeliverTime(delayTimestamp);
        }
        return message;

    }
}
