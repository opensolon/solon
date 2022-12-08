package org.noear.solon.cloud.extend.aliyun.ons.impl;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.extend.aliyun.ons.OnsProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author cgy
 * @since 1.11
 */
public class OnsConsumerHandler implements MessageListener {
    static Logger log = LoggerFactory.getLogger(OnsConsumerHandler.class);

    CloudEventObserverManger observerManger;
    String eventChannelName;

    OnsConfig onsConfig;

    public OnsConsumerHandler(OnsConfig onsConfig, CloudProps cloudProps, CloudEventObserverManger observerManger) {
        this.observerManger = observerManger;
        this.onsConfig = onsConfig;
        eventChannelName = cloudProps.getEventChannel();
    }

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        boolean isOk = true;
        if (onsConfig.getEnableConsoleLog()) {
            LogUtil.global().info("rocketMq Consume Message ok! topic:[" + message.getTopic() + "] msgId:[" + message.getMsgID() + "]");
        }
        try {
            String topicNew = message.getTopic();
            String group = null;
            String topic = null;
            if (topicNew.contains(OnsProps.GROUP_SPLIT_MART)) {
                group = topicNew.split(OnsProps.GROUP_SPLIT_MART)[0];
                topic = topicNew.split(OnsProps.GROUP_SPLIT_MART)[1];
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
