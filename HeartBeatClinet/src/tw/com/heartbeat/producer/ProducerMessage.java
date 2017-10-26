package tw.com.heartbeat.producer;

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
	MessageProducer producer = null;
	TextMessage textMessage = null;
	
	public void send(String mseeage) throws Exception {
		try {
			logger.debug("send:" + mseeage);
			textMessage.setText(mseeage);
			producer.send(textMessage);
		} catch (Exception e) {
			logger.debug("Error:" + e.getMessage());
		}
	}

	public ProducerMessage(String xmlFilePath) throws JMSException {


		RabbitFactory RabbitFactory = new RabbitFactory(xmlFilePath);

		Destination rmqDestination = RabbitFactory.CreateRabbitDestination();

		ConnectionFactory connectionFactory =RabbitFactory.CreateRabbitConnectionFactory();
		Connection connection = connectionFactory.createConnection();

		connection.start();

		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		producer = session.createProducer(rmqDestination);

		textMessage = session.createTextMessage();
	}

}
