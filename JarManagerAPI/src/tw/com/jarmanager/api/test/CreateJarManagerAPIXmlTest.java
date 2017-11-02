package tw.com.jarmanager.api.test;

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

public class CreateJarManagerAPIXmlTest {

	public static void main(String[] args) {
		JarManagerAPIXMLVO jAPIXML = new JarManagerAPIXMLVO();

		HeartBeatConnectionFactoryVO heartBeatConnectionFactory = new HeartBeatConnectionFactoryVO();
		heartBeatConnectionFactory.setPassword("password");
		heartBeatConnectionFactory.setHost("192.168.112.199");
		heartBeatConnectionFactory.setPort(5672);
		heartBeatConnectionFactory.setVirtualHost("/");
		heartBeatConnectionFactory.setUsername("admin");

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
		jarProjectVO.setNeedRun(true);

		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("D:\\jarXml\\SFStatus.xml");
		arrayList.add("D:\\jarXml\\HeatBeatClinetBeans.xml");

		jarProjectVO.setFilePathXMLList(arrayList);

		jarProjectVOList.add(jarProjectVO);

		JarProjectVO jarProjectVO1 = new JarProjectVO();
		jarProjectVO1.setBeatID("SFOutboundDetail");
		jarProjectVO1.setFileName("SFOutboundDetail");
		jarProjectVO1.setTimeSeries(30000);
		jarProjectVO1.setDescription("SFOutboundDetail說明");
		jarProjectVO1.setJarFilePath("D:\\jarFilePath\\SFOutboundDetail.jar");
		jarProjectVO1.setNeedRun(true);

		ArrayList<String> arrayList1 = new ArrayList<String>();
		arrayList1.add("D:\\jarXml\\SFOutboundDetail.xml");
		arrayList1.add("D:\\jarXml\\HeatBeatClinetBeans.xml");
		jarProjectVO1.setFilePathXMLList(arrayList1);

		jarProjectVOList.add(jarProjectVO1);

		JarProjectVO jarProjectVO2 = new JarProjectVO();
		jarProjectVO2.setBeatID("SFBalance");
		jarProjectVO2.setFileName("SFBalance");
		jarProjectVO2.setTimeSeries(30000);
		jarProjectVO2.setDescription("SFBalance說明");
		jarProjectVO2.setJarFilePath("D:\\jarFilePath\\SFBalance.jar");
		jarProjectVO2.setNeedRun(true);

		ArrayList<String> arrayList2 = new ArrayList<String>();
		arrayList2.add("D:\\jarXml\\SFBalance.xml");
		arrayList2.add("D:\\jarXml\\HeatBeatClinetBeans.xml");

		jarProjectVO2.setFilePathXMLList(arrayList2);

		jarProjectVOList.add(jarProjectVO2);

		JarProjectVO jarProjectVO3 = new JarProjectVO();
		jarProjectVO3.setBeatID("Q2W");
		jarProjectVO3.setFileName("Q2W");
		jarProjectVO3.setTimeSeries(60000);
		jarProjectVO3.setDescription("Q2W說明");
		jarProjectVO3.setJarFilePath("D:\\jarFilePath\\Q2W.jar");
		jarProjectVO3.setNeedRun(true);

		ArrayList<String> arrayList3 = new ArrayList<String>();
		arrayList3.add("D:\\jarXml\\q2w-config.xml");
		arrayList3.add("D:\\jarXml\\xmlconverter-config.xml");
		arrayList3.add("D:\\jarXml\\HeatBeatClinetBeans.xml");

		jarProjectVO3.setFilePathXMLList(arrayList3);

		jarProjectVOList.add(jarProjectVO3);

		jAPIXML.setJarProjectVOList(jarProjectVOList);

		try {

			File file = new File("D:\\JarManagerAPI.xml");
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
