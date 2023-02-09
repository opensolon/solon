package org.noear.solon.cloud.extend.activemq.impl;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.extend.activemq.ActivemqProps;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.core.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;

/**
 * @author liuxuehua12
 * @since 2.0
 */
public class ActivemqConsumeHandler implements MessageListener {
    static Logger log = LoggerFactory.getLogger(ActivemqConsumeHandler.class);

    private CloudEventObserverManger observerManger;
    private Session session;

    public ActivemqConsumeHandler(CloudEventObserverManger observerManger, Session session) {
        super();
        this.observerManger = observerManger;
        this.session = session;
    }

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage textmsg = (ActiveMQTextMessage) message;
        try {
            Event event = ONode.deserialize(textmsg.getText(), Event.class);
            event.times(textmsg.getRedeliveryCounter());

            //已设置自动延时策略
            boolean isOk = onReceive(event);

            if(isOk){
                textmsg.acknowledge();
            }else{
                session.recover();
            }
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            EventBus.pushTry(e);

            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 处理接收事件（会吃掉异常）
     * */
    private boolean onReceive(Event event) throws Throwable {
        try {
            return onReceiveDo(event);
        } catch (Throwable e) {
            EventBus.pushTry(e);
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

    private String getTopicNew(Event event){
        return ActivemqProps.getTopicNew(event);
    }
}
