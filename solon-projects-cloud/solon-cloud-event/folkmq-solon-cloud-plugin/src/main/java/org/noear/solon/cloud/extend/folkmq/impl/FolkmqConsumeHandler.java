package org.noear.solon.cloud.extend.folkmq.impl;

import org.noear.folkmq.client.MqMessageReceived;
import org.noear.folkmq.client.MqConsumeHandler;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.folkmq.FolkmqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author noear
 * @since 2.6
 */
public class FolkmqConsumeHandler implements MqConsumeHandler {
    static final Logger log = LoggerFactory.getLogger(FolkmqConsumeHandler.class);

    private CloudEventObserverManger observerManger;

    public FolkmqConsumeHandler(CloudEventObserverManger observerManger) {
        this.observerManger = observerManger;
    }

    @Override
    public void consume(MqMessageReceived message) throws IOException {
        try {
            String topicNew = message.getTopic();
            String group = null;
            String topic = null;
            if (topicNew.contains(FolkmqProps.GROUP_SPLIT_MARK)) {
                group = topicNew.split(FolkmqProps.GROUP_SPLIT_MARK)[0];
                topic = topicNew.split(FolkmqProps.GROUP_SPLIT_MARK)[1];
            } else {
                topic = topicNew;
            }

            Event event = new Event(topic, message.getContent());
            event.key(message.getTid());
            event.times(message.getTimes());
            event.tags(message.getTag());

            if (Utils.isNotEmpty(group)) {
                event.group(group);
            }

            //已设置自动延时策略
            boolean isOk = onReceive(event);

            message.acknowledge(isOk);
        } catch (Throwable e) {
            message.acknowledge(false);

            e = Utils.throwableUnwrap(e);
            log.warn(e.getMessage(), e); //todo: ?

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 处理接收事件（会吃掉异常）
     */
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
        String topicNew = getTopicNew(event);

        handler = observerManger.getByTopic(topicNew);
        if (handler != null) {
            isOk = handler.handle(event);
        } else {
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", topicNew);
        }

        return isOk;
    }

    private String getTopicNew(Event event) {
        return FolkmqProps.getTopicNew(event);
    }
}
