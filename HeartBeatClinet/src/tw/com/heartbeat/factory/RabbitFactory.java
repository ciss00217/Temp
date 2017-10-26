package tw.com.heartbeat.factory;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;

import tw.com.jms.util.XMLParser;

public class RabbitFactory {
	String filePath;

	public RabbitFactory(String filePath) {
		this.filePath = filePath;
	}

	public RMQDestination CreateRabbitDestination() {
		XMLParser XMLParser = new XMLParser();

		String amqpStr = XMLParser.getXMLText("heartBeatDestination", "amqp", filePath);
		boolean amqp = Boolean.valueOf(amqpStr);
		String amqpExchangeName = XMLParser.getXMLText("heartBeatDestination", "amqpExchangeName", filePath);
		String amqpQueueName = XMLParser.getXMLText("heartBeatDestination", "amqpQueueName", filePath);
		String routingKey = XMLParser.getXMLText("heartBeatDestination", "amqpRoutingKey", filePath);
		String destinationName = XMLParser.getXMLText("heartBeatDestination", "destinationName", filePath);

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
