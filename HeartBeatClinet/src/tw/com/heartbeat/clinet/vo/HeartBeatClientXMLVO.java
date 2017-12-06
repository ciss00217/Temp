package tw.com.heartbeat.clinet.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "class")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "heartBeatConnectionFactoryVO","destination", "heartBeatClientVO"})
public class HeartBeatClientXMLVO {
	@XmlElement(name = "heartBeatConnectionFactory")
	private HeartBeatConnectionFactoryVO heartBeatConnectionFactoryVO;

	@XmlElement(name = "heartBeatDestination")
	private HeartBeatDestinationVO destination;

	@XmlElement(name = "heartBeatClientVO")
	private HeartBeatClientVO heartBeatClientVO;

	public HeartBeatConnectionFactoryVO getHeartBeatConnectionFactoryVO() {
		return heartBeatConnectionFactoryVO;
	}

	public void setHeartBeatConnectionFactoryVO(HeartBeatConnectionFactoryVO heartBeatConnectionFactoryVO) {
		this.heartBeatConnectionFactoryVO = heartBeatConnectionFactoryVO;
	}

	public HeartBeatDestinationVO getDestination() {
		return destination;
	}

	public void setDestination(HeartBeatDestinationVO destination) {
		this.destination = destination;
	}

	public HeartBeatClientVO getHeartBeatClientVO() {
		return heartBeatClientVO;
	}

	public void setHeartBeatClientVO(HeartBeatClientVO heartBeatClientVO) {
		this.heartBeatClientVO = heartBeatClientVO;
	}

}
