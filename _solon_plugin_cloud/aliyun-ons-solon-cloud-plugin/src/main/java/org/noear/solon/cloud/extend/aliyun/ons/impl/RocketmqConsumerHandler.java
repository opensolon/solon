package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.aliyun.ons.RocketmqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cgy
 * @since 1.11.3
 */
public class RocketmqConsumerHandler implements MessageListener {
    static Logger log = LoggerFactory.getLogger(RocketmqConsumerHandler.class);

    CloudEventObserverManger observerManger;
    String eventChannelName;

    public RocketmqConsumerHandler(CloudProps cloudProps, CloudEventObserverManger observerManger) {
        this.observerManger = observerManger;
        eventChannelName = cloudProps.getEventChannel();
    }

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        boolean isOk = true;

        try {
            String topicNew = message.getTopic();
            String group = null;
            String topic = null;
            if (topicNew.contains(RocketmqProps.GROUP_SPLIT_MART)) {
                group = topicNew.split(RocketmqProps.GROUP_SPLIT_MART)[0];
                topic = topicNew.split(RocketmqProps.GROUP_SPLIT_MART)[1];
            } else {
                topic = topicNew;
            }

            Event event = new Event(topic, new String(message.getBody()));
            event.tags(message.getTag());
            event.key(String.join(",", message.getKey()));
            event.times(message.getReconsumeTimes());
            event.channel(eventChannelName);
            if (Utils.isNotEmpty(group)) {
                event.group(group);
            }
            isOk = isOk && onReceive(event, topicNew);
        } catch (Throwable ex) {
            isOk = false;
            EventBus.push(ex);
        }

        if (isOk) {
            return Action.CommitMessage;
        } else {
            return Action.ReconsumeLater;
        }
    }


    /**
     * 处理接收事件
     */
    protected boolean onReceive(Event event, String topicNew) throws Throwable {
        boolean isOk = true;
        CloudEventHandler handler = null;

        handler = observerManger.get(topicNew);
        if (handler != null) {
            isOk = handler.handle(event);
        } else {
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", topicNew);
        }

        return isOk;
    }

}
