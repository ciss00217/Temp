package tw.com.jarmanager.createxml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import tw.com.jarmanager.api.vo.HeartBeatConnectionFactoryVO;
import tw.com.jarmanager.api.vo.HeartBeatDestinationVO;
import tw.com.jarmanager.api.vo.JarManagerAPIXMLVO;
import tw.com.jarmanager.api.vo.JarProjectVO;
import tw.com.jarmanager.api.vo.ManagerVO;

public class CreateJarManagerAPIXml {
	CreateJarManagerAPIXml(HeartBeatConnectionFactoryVO heartBeatConnectionFactory,HeartBeatDestinationVO heartBeatDestinationVO,List<JarProjectVO> jarProjectVOList,ManagerVO managerVO){
		
	}

	public static void main(String[] args) {
		JarManagerAPIXMLVO jAPIXML = new JarManagerAPIXMLVO();

		HeartBeatConnectionFactoryVO heartBeatConnectionFactory = new HeartBeatConnectionFactoryVO();
		heartBeatConnectionFactory.setPassword("password");
		heartBeatConnectionFactory.setHost("192.168.0.1");
		heartBeatConnectionFactory.setPort(928);
		heartBeatConnectionFactory.setVirtualHost("//");
		heartBeatConnectionFactory.setUsername("user");
		
		HeartBeatDestinationVO heartBeatDestinationVO = new HeartBeatDestinationVO();
		heartBeatDestinationVO.setAmqp(true);
		heartBeatDestinationVO.setAmqpExchangeName("jms.durable.queues");
		heartBeatDestinationVO.setAmqpQueueName("jmsHeart");
		heartBeatDestinationVO.setAmqpRoutingKey("jmsHeart");
		heartBeatDestinationVO.setDestinationName("jmsHeart");
		
		ManagerVO managerVO = new ManagerVO();
		managerVO.setLoadingTime(10000);
		managerVO.setReCheckTime(30000);
		
		List<JarProjectVO> jarProjectVOList = new ArrayList<JarProjectVO>();
		
		jAPIXML.setHeartBeatConnectionFactoryVO(heartBeatConnectionFactory);
		jAPIXML.setHeartBeatDestinationVO(heartBeatDestinationVO);
		jAPIXML.setManagerVO(managerVO);
		
		JarProjectVO jarProjectVO = new JarProjectVO();
		jarProjectVO.setBeatID("SFStatus");
		jarProjectVO.setFileName("SFStatus");
		jarProjectVO.setTimeSeries(30000);
		jarProjectVO.setDescription("SFStatus說明");
		jarProjectVO.setJarFilePath("D:\\jarFilePath\\SFStatus.jar");
		
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("D:\\XMLFilePath\\SFStatus.xml");
		arrayList.add("D:\\XMLFilePath\\SFStatus.xml2");

		jarProjectVO.setFilePathXMLList(arrayList);
		
		jarProjectVOList.add(jarProjectVO);
		
		jAPIXML.setJarProjectVOList(jarProjectVOList);


		try {

			File file = new File("d:\\file.xml");
			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(jAPIXML, file);
			jaxbMarshaller.marshal(jAPIXML, System.out);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
