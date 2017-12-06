package tw.com.heartbeat.client.test;

import java.io.File;
import java.time.LocalDateTime;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import tw.com.heartbeat.clinet.serivce.HeartBeatService;
import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.heartbeat.clinet.vo.HeartBeatClientXMLVO;
import tw.com.heartbeat.clinet.vo.HeartBeatConnectionFactoryVO;
import tw.com.heartbeat.clinet.vo.HeartBeatDestinationVO;

public class HeartBeatTest {

	public static void main(String[] args) {
		boolean iscreateHeartBeatXMLTest = createHeartBeatXMLTest();

		System.out.println("iscreateHeartBeatXMLTest: " + iscreateHeartBeatXMLTest);
		HeartBeatClientXMLVO heartBeatClientXMLVO=getXML();
		
		HeartBeatService heartBeatService = new HeartBeatService("D:\\FTP2Q-HeatBeatClinetBeans.xml");

	}

	public static HeartBeatClientXMLVO getXML() {
		File file = new File("D:\\FTP2Q-HeatBeatClinetBeans.xml");
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

	public static boolean createHeartBeatXMLTest() {
		boolean isSucess = false;

		HeartBeatClientXMLVO heartBeatClientXMLVO = new HeartBeatClientXMLVO();

		HeartBeatConnectionFactoryVO heartBeatConnectionFactory = new HeartBeatConnectionFactoryVO();
		heartBeatConnectionFactory.setPassword("password");
		heartBeatConnectionFactory.setHost("192.168.112.199");
		heartBeatConnectionFactory.setPort(5672);
		heartBeatConnectionFactory.setVirtualHost("/");
		heartBeatConnectionFactory.setUsername("admin");

		HeartBeatDestinationVO destination = new HeartBeatDestinationVO();
		destination.setAmqp(true);
		destination.setAmqpExchangeName("jms.durable.queues");
		destination.setAmqpQueueName("jmsHeart");
		destination.setAmqpRoutingKey("jmsHeart");
		destination.setDestinationName("jmsHeart");

		HeartBeatClientVO heartBeatClientVO = new HeartBeatClientVO();
		heartBeatClientVO.setBeatID("FTP2Q");
		heartBeatClientVO.setFileName("FTP2Q");
		heartBeatClientVO.setTimeSeries(1800000);

		heartBeatClientXMLVO.setDestination(destination);
		heartBeatClientXMLVO.setHeartBeatConnectionFactoryVO(heartBeatConnectionFactory);
		heartBeatClientXMLVO.setHeartBeatClientVO(heartBeatClientVO);
		try {

			File file = new File("D:\\FTP2Q-HeatBeatClinetBeans.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(HeartBeatClientXMLVO.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(heartBeatClientXMLVO, file);
			jaxbMarshaller.marshal(heartBeatClientXMLVO, System.out);
			isSucess = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return isSucess;

	}

}
