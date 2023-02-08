package org.noear.solon.cloud.extend.activemq.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudEventHandler;
import org.noear.solon.cloud.CloudProps;
import org.noear.solon.cloud.annotation.EventLevel;
import org.noear.solon.cloud.exception.CloudEventException;
import org.noear.solon.cloud.extend.activemq.ActivemqProps;
import org.noear.solon.cloud.extend.activemq.impl.ActivemqConsumer;
import org.noear.solon.cloud.extend.activemq.impl.ActivemqProducer;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.cloud.service.CloudEventObserverManger;
import org.noear.solon.cloud.service.CloudEventServicePlus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liuxuehua12
 * @since 2.0
 */
public class CloudEventServiceActivemqImp implements CloudEventServicePlus {
	static Logger log = LoggerFactory.getLogger(CloudEventServiceActivemqImp.class);
	 private CloudProps cloudProps;
	 private ActivemqProducer producer;
	 private ActivemqConsumer consumer;
	
	public CloudEventServiceActivemqImp(CloudProps cloudProps) {
		this.cloudProps = cloudProps;

        ActiveMQConnectionFactory factory = null;

        try {
            String brokerUrl = "tcp://" + cloudProps.getEventServer();
            String username = cloudProps.getUsername();
            String password = cloudProps.getPassword();
            if (Utils.isEmpty(cloudProps.getUsername())) {
                factory = new ActiveMQConnectionFactory(brokerUrl);
            } else {
                factory = new ActiveMQConnectionFactory(username, password, brokerUrl);
            }

            producer = new ActivemqProducer(factory);
            consumer = new ActivemqConsumer(factory, producer);
        }catch (Exception e){
            throw new CloudEventException(e);
        }
	}

	@Override
	public boolean publish(Event event) throws CloudEventException {
        if (Utils.isEmpty(event.topic())) {
            throw new IllegalArgumentException("Event missing topic");
        }

        if (Utils.isEmpty(event.content())) {
            throw new IllegalArgumentException("Event missing content");
        }

        if (Utils.isEmpty(event.key())) {
            event.key(Utils.guid());
        }

        //new topic
        String topicNew = ActivemqProps.getTopicNew(event);
        try {
        	boolean re = producer.publish(event, topicNew);
            return re;
        } catch (Throwable ex) {
            throw new CloudEventException(ex);
        }
	}
	
	CloudEventObserverManger observerManger = new CloudEventObserverManger();
	
	@Override
	public void attention(EventLevel level, String channel, String group,
			String topic, String tag, CloudEventHandler observer) {
		//new topic
        String topicNew;
        if (Utils.isEmpty(group)) {
            topicNew = topic;
        } else {
            topicNew = group + ActivemqProps.GROUP_SPLIT_MARK + topic;
        }

        observerManger.add(topicNew, level, group, topic, tag, observer);

	}
	
    public void subscribe() {
        try {
            if (observerManger.topicSize() > 0) {
                consumer.init(observerManger);
            }
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private String channel;
    private String group;

    @Override
    public String getChannel() {
        if (channel == null) {
            channel = cloudProps.getEventChannel();
        }
        return channel;
    }

    @Override
    public String getGroup() {
        if (group == null) {
            group = cloudProps.getEventGroup();
        }

        return group;
    }

}
