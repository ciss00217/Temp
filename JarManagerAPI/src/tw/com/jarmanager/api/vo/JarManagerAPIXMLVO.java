package tw.com.jarmanager.api.vo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "class")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "heartBeatConnectionFactoryVO", "heartBeatDestinationVO", "jarProjectVOList", "managerVO"})
public class JarManagerAPIXMLVO {
	@XmlElement
	private HeartBeatConnectionFactoryVO heartBeatConnectionFactoryVO;
	@XmlElement
	private HeartBeatDestinationVO heartBeatDestinationVO;
	@XmlElement
	private ManagerVO managerVO;

	@XmlElementWrapper(name = "jarProjectVOList")
	@XmlElement(name = "jarProjectVO")
	private List<JarProjectVO> jarProjectVOList;

	public List<JarProjectVO> getJarProjectVOList() {
		return jarProjectVOList;
	}

	public void setJarProjectVOList(List<JarProjectVO> jarProjectVOList) {
		this.jarProjectVOList = jarProjectVOList;
	}

	public HeartBeatConnectionFactoryVO getHeartBeatConnectionFactoryVO() {
		return heartBeatConnectionFactoryVO;
	}

	public void setHeartBeatConnectionFactoryVO(HeartBeatConnectionFactoryVO heartBeatConnectionFactoryVO) {
		this.heartBeatConnectionFactoryVO = heartBeatConnectionFactoryVO;
	}

	public HeartBeatDestinationVO getHeartBeatDestinationVO() {
		return heartBeatDestinationVO;
	}

	public void setHeartBeatDestinationVO(HeartBeatDestinationVO heartBeatDestinationVO) {
		this.heartBeatDestinationVO = heartBeatDestinationVO;
	}

	public ManagerVO getManagerVO() {
		return managerVO;
	}

	public void setManagerVO(ManagerVO managerVO) {
		this.managerVO = managerVO;
	}

}
