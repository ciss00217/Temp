package tw.com.jarmanager.api.factory;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.jms.admin.RMQConnectionFactory;
import com.rabbitmq.jms.admin.RMQDestination;

import tw.com.jarmanager.api.service.JarManagerAPIServiceMethod;
import tw.com.jarmanager.api.vo.HeartBeatConnectionFactoryVO;
import tw.com.jarmanager.api.vo.HeartBeatDestinationVO;
import tw.com.jarmanager.api.vo.JarManagerAPIXMLVO;
import tw.com.jarmanager.api.vo.JarProjectVO;
import tw.com.jarmanager.api.vo.ManagerVO;

public class RabbitFactory {
	private static final Logger logger = LogManager.getLogger(RabbitFactory.class);

	String filePath;

	public RabbitFactory(String filePath) {
		this.filePath = filePath;
	}

	public RMQDestination CreateRabbitDestination() {

		try {
			File file = new File(filePath);
			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);
			
			logger.debug(jarManagerAPIXMLVO.getHeartBeatDestinationVO().getDestinationName());
			
			HeartBeatDestinationVO heartBeatDestinationVO = jarManagerAPIXMLVO.getHeartBeatDestinationVO();

			
			RMQDestination rMQDestination = new RMQDestination();

			rMQDestination.setDestinationName(heartBeatDestinationVO.getDestinationName());
			rMQDestination.setAmqp(heartBeatDestinationVO.isAmqp());
			rMQDestination.setAmqpExchangeName(heartBeatDestinationVO.getAmqpExchangeName());
			rMQDestination.setAmqpQueueName(heartBeatDestinationVO.getAmqpQueueName());
			rMQDestination.setAmqpRoutingKey(heartBeatDestinationVO.getAmqpRoutingKey());
			
			
			
			return rMQDestination;

		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}

	}

	public RMQConnectionFactory CreateRabbitConnectionFactory() {
		try {
			File file = new File(filePath);
			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);

			HeartBeatConnectionFactoryVO heartBeatConnectionFactoryVO = jarManagerAPIXMLVO
					.getHeartBeatConnectionFactoryVO();

			RMQConnectionFactory rMQConnectionFactory = new RMQConnectionFactory();
			String password = heartBeatConnectionFactoryVO.getPassword();
			rMQConnectionFactory.setPassword(password);

			rMQConnectionFactory.setHost(heartBeatConnectionFactoryVO.getHost());
			rMQConnectionFactory.setUsername(heartBeatConnectionFactoryVO.getUsername());
			rMQConnectionFactory.setVirtualHost(heartBeatConnectionFactoryVO.getVirtualHost());

			return rMQConnectionFactory;

		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}

	}

	public List<JarProjectVO> CreateProjectVOList() {

		try {
			File file = new File(filePath);
			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);
			return jarManagerAPIXMLVO.getJarProjectVOList();

		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ManagerVO CreateManagerVO() {

		try {
			File file = new File(filePath);
			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);
			return jarManagerAPIXMLVO.getManagerVO();

		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

}
