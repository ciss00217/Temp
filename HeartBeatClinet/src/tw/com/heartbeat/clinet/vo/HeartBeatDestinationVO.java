package tw.com.heartbeat.clinet.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "heartBeatDestination")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "destinationName", "amqp", "amqpQueueName", "amqpExchangeName", "amqpRoutingKey" })
public class HeartBeatDestinationVO{

	private static final long serialVersionUID = 1L;
	private String destinationName;
	private boolean amqp;
	private String amqpQueueName;
	private String amqpExchangeName;
	private String amqpRoutingKey;

	public String getDestinationName() {
		return destinationName;
	}

	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}

	public boolean isAmqp() {
		return amqp;
	}

	public void setAmqp(boolean amqp) {
		this.amqp = amqp;
	}

	public String getAmqpQueueName() {
		return amqpQueueName;
	}

	public void setAmqpQueueName(String amqpQueueName) {
		this.amqpQueueName = amqpQueueName;
	}

	public String getAmqpExchangeName() {
		return amqpExchangeName;
	}

	public void setAmqpExchangeName(String amqpExchangeName) {
		this.amqpExchangeName = amqpExchangeName;
	}

	public String getAmqpRoutingKey() {
		return amqpRoutingKey;
	}

	public void setAmqpRoutingKey(String amqpRoutingKey) {
		this.amqpRoutingKey = amqpRoutingKey;
	}

}
