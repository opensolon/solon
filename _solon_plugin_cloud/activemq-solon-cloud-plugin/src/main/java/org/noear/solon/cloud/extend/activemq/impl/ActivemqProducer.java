package org.noear.solon.cloud.extend.activemq.impl;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ScheduledMessage;
import org.noear.snack.ONode;
import org.noear.solon.cloud.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * @author liuxuehua12
 * @since 2.0
 */
public class ActivemqProducer {
	static Logger log = LoggerFactory.getLogger(ActivemqProducer.class);
	private ActiveMQConnectionFactory factory;
	private Connection connection;
	
	public ActivemqProducer(ActiveMQConnectionFactory factory){
		this.factory = factory;
	}
	
	private void init() throws JMSException{
		connection = factory.createConnection();
		connection.start();		
	}
	
	private void close() throws JMSException{
		if(connection != null){
			connection.close();
		}
	}
		
	public boolean publish(Event event, String topic) throws JMSException  {
		init();
		//创建会话
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //创建一个目标
        Destination destination = session.createTopic(topic);
        //创建一个生产者
        MessageProducer producer = session.createProducer(destination);

		//创建消息
        TextMessage message = session.createTextMessage(ONode.stringify(event));

		//支持延时消息
		long delay = 0;
		if (event.scheduled() != null) {
			delay = event.scheduled().getTime() - System.currentTimeMillis();
		}

		if(delay > 0) {
			message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delay);
		}

        //发布消息
        try{
        	producer.send(destination,message);
        	return true;
        }catch(JMSException e){
        	log.error(e.getMessage(),e);
        	return false;
        }finally{
        	close();
        }
    }

}
