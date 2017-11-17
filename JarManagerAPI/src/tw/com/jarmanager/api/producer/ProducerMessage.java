package tw.com.jarmanager.api.producer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tw.com.heartbeat.factory.RabbitFactory;

public class ProducerMessage {

	private static final Logger logger = LogManager.getLogger(ProducerMessage.class);
	
	Destination destination = null;

	ConnectionFactory connectionFactory = null;
	
	MessageProducer producer = null;
	
	TextMessage textMessage = null;

	public ProducerMessage(Destination destination, ConnectionFactory connectionFactory) throws JMSException {
		
		this.destination = destination;
		
		this.connectionFactory = connectionFactory;
		
		Connection connection = connectionFactory.createConnection();

		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		producer = session.createProducer(destination);

		textMessage = session.createTextMessage();
		
	}

	public void send(String mseeage) throws Exception {
		try {
			logger.debug("send:" + mseeage);
			textMessage.setText(mseeage);
			producer.send(textMessage);
		} catch (Exception e) {
			logger.debug("Error:" + e.getMessage());
		}
	}


}
