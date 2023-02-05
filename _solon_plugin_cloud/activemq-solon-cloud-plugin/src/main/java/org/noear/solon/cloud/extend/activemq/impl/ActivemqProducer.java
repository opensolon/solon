package org.noear.solon.cloud.extend.activemq.impl;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.noear.snack.ONode;
import org.noear.solon.cloud.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                
        TextMessage message = session.createTextMessage(ONode.stringify(event));
            
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
