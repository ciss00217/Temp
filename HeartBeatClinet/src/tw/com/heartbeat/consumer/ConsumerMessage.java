package tw.com.heartbeat.consumer;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.heartbeat.factory.RabbitFactory;
import tw.com.jms.util.Util;

public class ConsumerMessage {
	private static final Logger logger = LogManager.getLogger(ConsumerMessage.class);

	protected Connection connection;
	protected Destination destination;
	private Session session;
	private QueueBrowser queueBrowser;
	private Enumeration<?> enumeration;
	private Gson gson;

	public ConsumerMessage(String destinationName) throws JMSException {
		try {

			RabbitFactory RabbitFactory = new RabbitFactory();

			Destination destination = RabbitFactory.CreateRabbitDestination();

			this.destination = destination;

			ConnectionFactory connectionFactory = RabbitFactory.CreateRabbitConnectionFactory();

			this.connection = connectionFactory.createConnection();

			connection.start();

			this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			this.gson = new Gson();

		} catch (JMSException e) {

			logger.debug("ERROR:" + e.getMessage());
		}
	}

	public boolean checkMessage(String beatID) throws JMSException {
		String text;

		if (queueBrowser == null) {
			queueBrowser = session.createBrowser((Queue) destination);

		} else {

			enumeration = queueBrowser.getEnumeration();

			while (enumeration.hasMoreElements()) {
				Message message = (Message) enumeration.nextElement();

				text = Util.convertMsg(message);

				logger.debug("peek:" + text);

				HeartBeatClientVO heartBeatClientVO = gson.fromJson(text, HeartBeatClientVO.class);

				String messageBeatID = heartBeatClientVO.getBeatID();

				logger.debug("check:");
				logger.debug("beatID:" + beatID);
				logger.debug("messageBeatID:" + messageBeatID);

				if (null != messageBeatID && messageBeatID.equals(beatID)) {

					logger.debug("exist: " + messageBeatID);
					return false;
				}
			}
			enumeration = null;
			logger.debug("beatID:" + beatID);
			logger.debug("not exist");
		}
		return true;
	}

}
