package tw.com.heartbeat.factory;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;

import tw.com.heartbeat.clinet.vo.HeartBeatClientXMLVO;
import tw.com.jms.util.XMLParser;

public class RabbitFactory {
	String filePath;

	public RabbitFactory(String filePath) {
		this.filePath = filePath;
	}
	public HeartBeatClientXMLVO  getHeartBeatClientXMLVO() {
		File file = new File(filePath);
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(HeartBeatClientXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			HeartBeatClientXMLVO heartBeatClientXMLVO = (HeartBeatClientXMLVO) jaxbUnmarshaller.unmarshal(file);
			
			return heartBeatClientXMLVO;

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public RMQDestination CreateRabbitDestination() {
		XMLParser XMLParser = new XMLParser();

		String amqpStr = tw.com.jms.util.XMLParser.getXMLText("heartBeatDestination", "amqp", filePath);
		boolean amqp = Boolean.valueOf(amqpStr);
		String amqpExchangeName = tw.com.jms.util.XMLParser.getXMLText("heartBeatDestination", "amqpExchangeName", filePath);
		String amqpQueueName = tw.com.jms.util.XMLParser.getXMLText("heartBeatDestination", "amqpQueueName", filePath);
		String routingKey = tw.com.jms.util.XMLParser.getXMLText("heartBeatDestination", "amqpRoutingKey", filePath);
		String destinationName = tw.com.jms.util.XMLParser.getXMLText("heartBeatDestination", "destinationName", filePath);

		RMQDestination rmqDestination = new RMQDestination();
		rmqDestination.setAmqp(amqp);
		rmqDestination.setAmqpExchangeName(amqpExchangeName);
		rmqDestination.setAmqpQueueName(amqpQueueName);
		rmqDestination.setAmqpRoutingKey(routingKey);
		rmqDestination.setDestinationName(destinationName);

		return rmqDestination;

	}

	public RMQConnectionFactory CreateRabbitConnectionFactory() {
		XMLParser XMLParser = new XMLParser();

		String username = XMLParser.getXMLText("heartBeatConnectionFactory", "username", filePath);

		String password = XMLParser.getXMLText("heartBeatConnectionFactory", "password", filePath);
		String virtualHost = XMLParser.getXMLText("heartBeatConnectionFactory", "virtualHost", filePath);
		String host = XMLParser.getXMLText("heartBeatConnectionFactory", "host", filePath);
		String portStr = XMLParser.getXMLText("heartBeatConnectionFactory", "port", filePath);

		int port = Integer.parseInt(portStr);

		RMQConnectionFactory rmqConnectionFactory = new RMQConnectionFactory();
		rmqConnectionFactory.setUsername(username);
		rmqConnectionFactory.setPassword(password);
		rmqConnectionFactory.setVirtualHost(virtualHost);
		rmqConnectionFactory.setHost(host);
		rmqConnectionFactory.setPort(port);

		return rmqConnectionFactory;

	}

}
