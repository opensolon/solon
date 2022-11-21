package org.noear.solon.cloud.extend.local.service;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.exception.CloudJobException;
import org.noear.solon.cloud.extend.local.LocalProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.10
 */
public class CloudEventServiceLocalImpl implements CloudEventServicePlus {
    static Logger log = LoggerFactory.getLogger(CloudEventServiceLocalImpl.class);

    @Override
    public boolean publish(Event event) throws CloudEventException {
        if (Utils.isEmpty(event.topic())) {
            throw new IllegalArgumentException("Event missing topic");
        }

        if (Utils.isEmpty(event.content())) {
            throw new IllegalArgumentException("Event missing content");
        }

        //异步执行
        Utils.async(() -> {
            try {
                publishDo(event);
            } catch (Throwable e) {
                EventBus.push(new CloudJobException(e));
            }
        });

        return true;
    }

    private void publishDo(Event event) throws Throwable {
        //new topic
        String topicNew;
        if (Utils.isEmpty(event.group())) {
            topicNew = event.topic();
        } else {
            topicNew = event.group() + LocalProps.GROUP_TOPIC_SPLIT_MART + event.topic();
        }


        CloudEventHandler eventHandler = observerManger.get(topicNew);
        if (eventHandler == null) {
            eventHandler.handle(event);
        } else {
            //只需要记录一下
            log.warn("There is no observer for this event topic[{}]", event.topic());
        }
    }

    private CloudEventObserverManger observerManger = new CloudEventObserverManger();
    @Override
    public void attention(EventLevel level, String channel, String group, String topic, CloudEventHandler observer) {
        //new topic
        String topicNew;
        if (Utils.isEmpty(group)) {
            topicNew = topic;
        } else {
            topicNew = group + LocalProps.GROUP_TOPIC_SPLIT_MART + topic;
        }

        observerManger.add(topicNew, level, group, topic, observer);
    }

    @Override
    public String getChannel() {
        return "local";
    }

    @Override
    public String getGroup() {
        return Solon.cfg().appGroup();
    }
}
