package org.noear.solon.cloud.extend.activemq.impl;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.activemq.ActivemqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.utils.ExpirationUtils;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author liuxuehua12
 * @since 2.0
 */
public class ActivemqConsumeHandler implements MessageListener {
    static Logger log = LoggerFactory.getLogger(ActivemqConsumeHandler.class);

    private CloudEventObserverManger observerManger;
    private ActivemqProducer producer;

    public ActivemqConsumeHandler(CloudEventObserverManger observerManger, ActivemqProducer producer) {
        super();
        this.observerManger = observerManger;
        this.producer = producer;
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textmsg = (TextMessage) message;
        try {
            Event event = ONode.deserialize(textmsg.getText(), Event.class);

            boolean isOk = onReceive(event);

            if (isOk == false) {
                event.times(event.times() + 1);
                try {
                    isOk = producer.publish(event, getTopicNew(event), ExpirationUtils.getExpiration(event.times()));
                } catch (Throwable ex) {
                    //log.error("re public error:"+ex.getMessage(),ex);
                }
            }

            if(isOk){
                textmsg.acknowledge();
            }
        } catch (Throwable e) {
            //log.error("onMessage error:"+e.getMessage(),e);
            e = Utils.throwableUnwrap(e);
            EventBus.push(e);
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 处理接收事件
     */
    private boolean onReceive(Event event) throws Throwable {
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

    private String getTopicNew(Event event){
        return ActivemqProps.getTopicNew(event);
    }
}
