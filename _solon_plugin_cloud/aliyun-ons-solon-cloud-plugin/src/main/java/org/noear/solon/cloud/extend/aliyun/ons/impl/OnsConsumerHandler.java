package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.aliyun.ons.OnsProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cgy
 * @since 1.11
 */
public class OnsConsumerHandler implements MessageListener {
    static Logger log = LoggerFactory.getLogger(OnsConsumerHandler.class);

    CloudEventObserverManger observerManger;

    OnsConfig config;

    public OnsConsumerHandler(OnsConfig config, CloudEventObserverManger observerManger) {
        this.observerManger = observerManger;
        this.config = config;
    }

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        boolean isOk = true;

        log.debug("Ons Consume Message ok! topic:[" + message.getTopic() + "] msgId:[" + message.getMsgID() + "]");

        try {
            String topicNew = message.getTopic();
            String group = null;
            String topic = null;
            if (topicNew.contains(OnsProps.GROUP_SPLIT_MARK)) {
                group = topicNew.split(OnsProps.GROUP_SPLIT_MARK)[0];
                topic = topicNew.split(OnsProps.GROUP_SPLIT_MARK)[1];
            } else {
                topic = topicNew;
            }

            Event event = new Event(topic, new String(message.getBody()));
            event.key(message.getKey());
            event.tags(message.getTag());
            event.times(message.getReconsumeTimes());
            event.channel(config.getChannelName());

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

        if (Utils.isEmpty(event.tags())) {
            handler = observerManger.getByTopicAndTag(topicNew, "*");
        } else {
            handler = observerManger.getByTopicAndTag(topicNew, event.tags());

            if (handler == null) {
                handler = observerManger.getByTopicAndTag(topicNew, "*");
            }
        }

        if (handler != null) {
            isOk = handler.handle(event);
        } else {
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", topicNew);
        }

        return isOk;
    }
}
