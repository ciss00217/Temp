package tw.com.heartbeat.factory;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;

import tw.com.jms.util.XMLParser;

public class RabbitFactory {
	public RMQDestination CreateRabbitDestination() {
		XMLParser XMLParser = new XMLParser();

		String amqpStr = XMLParser.getXMLText("heartBeatDestination", "amqp",
				"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
		boolean amqp = Boolean.valueOf(amqpStr);
		String amqpExchangeName = XMLParser.getXMLText("heartBeatDestination", "amqpExchangeName",
				"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
		String amqpQueueName = XMLParser.getXMLText("heartBeatDestination", "amqpQueueName",
				"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
		String routingKey = XMLParser.getXMLText("heartBeatDestination", "amqpRoutingKey",
				"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
		String destinationName = XMLParser.getXMLText("heartBeatDestination", "destinationName",
				"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");

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

		String username = XMLParser.getXMLText("heartBeatConnectionFactory", "username",
				"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");

		String password = XMLParser.getXMLText("heartBeatConnectionFactory", "password",
				"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
		String virtualHost = XMLParser.getXMLText("heartBeatConnectionFactory", "virtualHost",
				"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
		String host = XMLParser.getXMLText("heartBeatConnectionFactory", "host",
				"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
		String portStr = XMLParser.getXMLText("heartBeatConnectionFactory", "port",
				"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");

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
