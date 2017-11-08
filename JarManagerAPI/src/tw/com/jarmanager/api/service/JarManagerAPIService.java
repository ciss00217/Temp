package tw.com.jarmanager.api.service;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.jarmanager.api.consumer.ConsumerMessage;
import tw.com.jarmanager.api.factory.RabbitFactory;
import tw.com.jarmanager.api.socket.SocketClient;
import tw.com.jarmanager.api.socket.SocketServer;
import tw.com.jarmanager.api.util.ProcessUtial;
import tw.com.jarmanager.api.vo.AnnotationVO;
import tw.com.jarmanager.api.vo.HeartBeatConnectionFactoryVO;
import tw.com.jarmanager.api.vo.HeartBeatDestinationVO;
import tw.com.jarmanager.api.vo.JarManagerAPIXMLVO;
import tw.com.jarmanager.api.vo.JarProjectVO;
import tw.com.jarmanager.api.vo.ManagerVO;
import tw.com.jarmanager.api.vo.ResponseVO;

public class JarManagerAPIService {
	private static final Logger logger = LogManager.getLogger(JarManagerAPIService.class);
	private static ResponseVO responseVO;
	static boolean exit = false;
	public static List<JarProjectVO> XMLjarVOs;
	public static String xmlFile = "";

	public static ResponseVO getResponseVO() {
		return responseVO;
	}

	public static void setResponseVO(ResponseVO responseVO) {
		JarManagerAPIService.responseVO = responseVO;
	}

	public static boolean isExit() {
		return exit;
	}

	public static void setExit(boolean exit) {
		JarManagerAPIService.exit = exit;
	}

	public static String getXmlFile() {
		return xmlFile;
	}

	public static void setXmlFilePath(String xmlFile) {
		JarManagerAPIService.xmlFile = xmlFile;
	}
	
	public static boolean chackData(JarProjectVO jarProjectVO) {

		String beatID = jarProjectVO.getBeatID();

		String fileName = jarProjectVO.getFileName();

		String jarFilePath = jarProjectVO.getJarFilePath();

		Long timeSeries = jarProjectVO.getTimeSeries();
		if (null == fileName || "".equals(fileName.trim())) {
			return false;
		}

		if (null == jarFilePath || "".equals(jarFilePath.trim())) {
			return false;
		}

		if (null == timeSeries || timeSeries < 30000) {
			return false;
		}

		if (null == beatID || "".equals(beatID.trim())) {
			return false;
		}

		return true;

	}

	public static boolean addJarProjectVOXml(JarProjectVO jarProjectVO) {

		boolean isSucess = chackData(jarProjectVO);

		if (!isSucess) {
			return isSucess;
		}

		try {
			List<JarProjectVO> xmlJarVOList = getXMLJarProjectVOList(xmlFile);

			for (JarProjectVO xmlJarVO : xmlJarVOList) {
				String xmlBeatIDId = xmlJarVO.getBeatID();
				String addVOBeatIDId = jarProjectVO.getBeatID();

				if (xmlBeatIDId.equals(addVOBeatIDId)) {
					return false;
				}
			}

			logger.debug("addJarProjectVOXml:addJarProjectVOXml");

			SocketClient socketClient = new SocketClient("127.0.0.1", 9527);

			isSucess = socketClient.sendChangeBeatID(jarProjectVO);

			List<JarProjectVO> jarProjectVOList = socketClient.getJarProjectVOList();

			File file = new File(xmlFile);
			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);

			jarManagerAPIXMLVO.getJarProjectVOList().add(jarProjectVO);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(jarManagerAPIXMLVO, file);
			jaxbMarshaller.marshal(jarManagerAPIXMLVO, System.out);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}
	
	
	public static JarManagerAPIXMLVO getJarManagerAPIXMLVO() {

		try {

			File file = new File(xmlFile);
			JAXBContext jaxbContext;

			jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);
			return jarManagerAPIXMLVO;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static boolean jarManagerSetUp(JarManagerAPIXMLVO jarManagerSetUp) {

		try {

			File file = new File(xmlFile);
			JAXBContext jaxbContext;

			jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);

			jarManagerAPIXMLVO.setHeartBeatConnectionFactoryVO(jarManagerSetUp.getHeartBeatConnectionFactoryVO());
			jarManagerAPIXMLVO.setHeartBeatDestinationVO(jarManagerSetUp.getHeartBeatDestinationVO());
			jarManagerAPIXMLVO.setManagerVO(jarManagerSetUp.getManagerVO());

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(jarManagerAPIXMLVO, file);
			jaxbMarshaller.marshal(jarManagerAPIXMLVO, System.out);

			return true;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
//	public static boolean updateJarProjectVOXml(JarProjectVO jarProjectVO, String befBeatID) {
//
//		if (befBeatID == null || "".equals(befBeatID.trim())) {
//			logger.debug("befBeatID is null");
//			return false;
//		}
//
//		List<JarProjectVO> xmlJarVOList = getXMLJarProjectVOList(xmlFile);
//
//		for (JarProjectVO xmlJarVO : xmlJarVOList) {
//			String xmlBeatIDId = xmlJarVO.getBeatID();
//			String addVOBeatIDId = jarProjectVO.getBeatID();
//
//			if (xmlBeatIDId.equals(addVOBeatIDId) && !xmlBeatIDId.equals(befBeatID)) {
//				logger.debug("befBeatID is repeat");
//				return false;
//			}
//		}
//
//		try {
//			logger.debug("updateJarProjectVOXml");
//
//			SocketClient socketClient = new SocketClient("127.0.0.1", 9527);
//
//			boolean isSucess = socketClient.sendChangeBeatID(jarProjectVO);
//
//			File file = new File(xmlFile);
//			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);
//
//			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);
//
//			List<JarProjectVO> list = jarManagerAPIXMLVO.getJarProjectVOList();
//
//			for (JarProjectVO mJarProjectVO : list) {
//				if (mJarProjectVO.getBeatID().equals(befBeatID)) {
//					mJarProjectVO.setBeatID(jarProjectVO.getBeatID());
//					mJarProjectVO.setDescription(jarProjectVO.getDescription());
//					mJarProjectVO.setFileName(jarProjectVO.getFileName());
//					mJarProjectVO.setFilePathXMLList(jarProjectVO.getFilePathXMLList());
//					mJarProjectVO.setJarFilePath(jarProjectVO.getJarFilePath());
//					mJarProjectVO.setTimeSeries(jarProjectVO.getTimeSeries());
//				}
//			}
//
//			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//
//			// output pretty printed
//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//			jaxbMarshaller.marshal(jarManagerAPIXMLVO, file);
//			jaxbMarshaller.marshal(jarManagerAPIXMLVO, System.out);
//
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return false;
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return true;
//
//	}

	public static boolean updateJarProjectVOXml(JarProjectVO jarProjectVO) {

		boolean isSucess = chackData(jarProjectVO);

		if (!isSucess) {
			return isSucess;
		}

		try {
			logger.debug("updateJarProjectVOXml");

			SocketClient socketClient = new SocketClient("127.0.0.1", 9527);

			isSucess = socketClient.sendChangeBeatID(jarProjectVO);

			File file = new File(xmlFile);
			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);

			List<JarProjectVO> list = jarManagerAPIXMLVO.getJarProjectVOList();

			for (JarProjectVO mJarProjectVO : list) {
				if (mJarProjectVO.getBeatID().equals(jarProjectVO.getBeatID())) {
					mJarProjectVO.setDescription(jarProjectVO.getDescription());
					mJarProjectVO.setFileName(jarProjectVO.getFileName());
					mJarProjectVO.setFilePathXMLList(jarProjectVO.getFilePathXMLList());
					mJarProjectVO.setJarFilePath(jarProjectVO.getJarFilePath());
					mJarProjectVO.setTimeSeries(jarProjectVO.getTimeSeries());
				}
			}

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(jarManagerAPIXMLVO, file);
			jaxbMarshaller.marshal(jarManagerAPIXMLVO, System.out);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public static boolean deleteJarProjectVOXml(List<String> jarIDList) {

		try {
			
			SocketClient socketClient = new SocketClient("127.0.0.1", 9527);

			boolean isSucess = socketClient.sendChangeBeatID(jarIDList);
			
			logger.debug("addJarProjectVOXml:addJarProjectVOXml");

			File file = new File(xmlFile);
			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);

			List<JarProjectVO> jarProjectVOList = jarManagerAPIXMLVO.getJarProjectVOList();

			for (String id : jarIDList) {
				jarProjectVOList.removeIf((JarProjectVO jarProjectVO) -> jarProjectVO.getBeatID().equals(id));
			}

			jarManagerAPIXMLVO.setJarProjectVOList(jarProjectVOList);

			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(jarManagerAPIXMLVO, file);
			jaxbMarshaller.marshal(jarManagerAPIXMLVO, System.out);

		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	/****
	 * 用來製作JarManagerAPIXML的方法
	 * 
	 **/
	public boolean createJarManagerAPIXml(HeartBeatConnectionFactoryVO heartBeatConnectionFactoryVO,
			HeartBeatDestinationVO heartBeatDestinationVO, List<JarProjectVO> jarProjectVOList, ManagerVO managerVO,
			String path) {
		JarManagerAPIXMLVO jAPIXML = new JarManagerAPIXMLVO();
		jAPIXML.setHeartBeatConnectionFactoryVO(heartBeatConnectionFactoryVO);
		jAPIXML.setHeartBeatDestinationVO(heartBeatDestinationVO);
		jAPIXML.setJarProjectVOList(jarProjectVOList);
		jAPIXML.setManagerVO(managerVO);

		try {

			File file = new File(path);
			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			jaxbMarshaller.marshal(jAPIXML, file);
			jaxbMarshaller.marshal(jAPIXML, System.out);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	/***
	 * 用來獲得JarManagerAPIXML JarProjectVOList的方法
	 **/
	public static List<JarProjectVO> getXMLJarProjectVOList(String XMLPath) {
		RabbitFactory rabbitFactory = new RabbitFactory(XMLPath);
		return rabbitFactory.CreateProjectVOList();
	}
	
	/***
	 * 用來獲得JarManagerAPIXML JarProjectVOList的方法
	 **/
	public static List<JarProjectVO> getXMLNeedRunList(String XMLPath) {
		RabbitFactory rabbitFactory = new RabbitFactory(XMLPath);
		List<JarProjectVO> jarNeedRunList= rabbitFactory.CreateProjectVOList();
		jarNeedRunList.removeIf((JarProjectVO jarProjectVO) -> !jarProjectVO.getNeedRun());
		return jarNeedRunList;

	}

	@AnnotationVO(description = "關閉程序", methodName = "-closeManager  D:\\yourFileXmlPath")
	public static void closeManager() {
		JarManagerAPIService.exit = true;
	}

	/**********************
	 * 指令說明
	 * ********************/
	public static void getAllMethodDescription() throws ClassNotFoundException {
		Class<?> c = Class.forName("tw.com.jarmanager.api.service.JarManagerAPIService");
		Method[] methods = c.getMethods();
		for (Method method : methods) {
			Annotation[] annotations = method.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof AnnotationVO) {
					AnnotationVO annotationVO = (AnnotationVO) annotation;
					System.out.println("methodsName: " + annotationVO.methodName());
					System.out.println("description: " + annotationVO.description());
					System.out.println("");
				}
			}
		}

	}

	/******************************
	 * 改版使用socketServer 來控制	新	刪	修
	 * 
	 * 1.啟動		JarManagerAPIServiceMethod.startJars
	 * 		1.取得XML
	 * 
	 * 		2.轉成物件   
	 * 
	 * 		3.取出List<jarVO>
	 * 
	 * 		4.迴圈執行啟動
	 * 
	 * 		5.放入map
	 * 
	 * 
	 * 2.檢查		JarManagerAPIServiceMethod.checkData
	 * 		1.檢查socketServer來判別是否有新刪修 
	 * 	
	 * 		2.有的話 就讀取   XML  沒有的話取全域變數
	 * 
	 * 		3.取得須重啟的
	 * 
	 * 3.重啟	 JarManagerAPIServiceMethod.reStart
	 * 		
	 * 		1.重啟須重啟的
	 * 
	 * 
	 * 4.放入狀態 到 socketServer
	 * 
	 * 5.等待
	 * 
	 * 6.當ui修改時 回傳狀態 要改成	待重啟中
	 * 
	 *  @throws IOException 
	 * *********************************/
	@AnnotationVO(description = "啟動JarManagerService 用來監控觀察底下的JAR 如果client 阻塞 或 關閉 就重啟它", methodName = "-startManager D:\\yourFileXmlPath")
	public static void startManager()  {

		XMLjarVOs = getXMLNeedRunList(xmlFile);

		HashMap<String, JarProjectVO> jarProjectVOMap = JarManagerAPIServiceMethod.startJars(XMLjarVOs);

		SocketServer socketServer = new SocketServer(9527);
		socketServer.start();

		while (true) {
			if (socketServer.isApiXMLChange()) {
				XMLjarVOs = getXMLNeedRunList(xmlFile);
			}

			List<JarProjectVO> reStartList = null;
			try {
				reStartList = JarManagerAPIServiceMethod.checkData(jarProjectVOMap, socketServer);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				jarProjectVOMap = JarManagerAPIServiceMethod.reStart(reStartList, jarProjectVOMap);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			JarManagerAPIServiceMethod.setStatusToSocketServer(jarProjectVOMap, socketServer);

			try {
				RabbitFactory rabbitFactory = new RabbitFactory(xmlFile);
				logger.debug("sleep:" + rabbitFactory.CreateManagerVO().getReCheckTime());
				Thread.sleep(rabbitFactory.CreateManagerVO().getReCheckTime());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// HashMap<String, JarProjectVO> jarProjectVOMap = reStart(reStartList)
		
		//		setStatus(newJarProjectVOMap,socketServer);
		
		

//		ResponseVO responseVO = startJars();
//
//		logger.debug("responseVO isSuccess: " + responseVO.isSuccess());
//
//		if (responseVO.isSuccess()) {
//
//			SocketServer socketServer = new SocketServer(9527);
//			socketServer.start();
//
//			// 獲得啟動的managerVO
//			HashMap<String, JarProjectVO> oldJarProjectVOMap = (HashMap<String, JarProjectVO>) responseVO.getObj();
//
//			while (true) {
//
//				if (exit) {
//					break;
//				}
//
//				try {
//					oldJarProjectVOMap = mapRemoveNoExitByXml(oldJarProjectVOMap);
//				} catch (IOException e2) {
//					// TODO Auto-generated catch block
//					e2.printStackTrace();
//				}
//
//				List<JarProjectVO> deathList = null;
//				try {
//					deathList = getDeathList(oldJarProjectVOMap);
//				} catch (JMSException e1) {
//					logger.debug("Error:" + e1.getMessage());
//				}
//
//				try {
//					HashMap<String, JarProjectVO> newJarProjectVOMap = reStart(deathList, oldJarProjectVOMap);
//					oldJarProjectVOMap = newJarProjectVOMap;
//				} catch (IOException e) {
//					logger.debug("Error:" + e.getMessage());
//				}
//
//				List<JarProjectVO> jarProjectVOList = getJarProjectVOList(oldJarProjectVOMap);
//
//				try {
//					RabbitFactory rabbitFactory = new RabbitFactory(xmlFile);
//					logger.debug("sleep:" + rabbitFactory.CreateManagerVO().getReCheckTime());
//					Thread.sleep(rabbitFactory.CreateManagerVO().getReCheckTime());
//				} catch (InterruptedException e) {
//					logger.debug("Error:" + e.getMessage());
//				}
//				socketServer.setJarProjectVOList(jarProjectVOList);
//
//			}
//
//		}
	}

	/**
	 * 取得Jms HearBeat裡面的訊息 並轉換成HeartBeatClientVO
	 * 
	 * ps: Queue Message 裡面的data並不刪除
	 ***/
	public static List<HeartBeatClientVO> getHeartBeatClientVOList() throws JMSException {
		ConsumerMessage ConsumerMessage = new ConsumerMessage();
		return ConsumerMessage.getHeartBeatClientVOList();
	}

	/**
	 * 取得Jms HearBeat裡面的訊息去除重複
	 * 
	 * 並轉換成HeartBeatClientVO
	 * 
	 * ps: Queue Message 裡面的data並不刪除
	 ***/
	public static List<HeartBeatClientVO> getSoleHeartBeatClientVOList() throws JMSException {
		ConsumerMessage ConsumerMessage = new ConsumerMessage();
		return ConsumerMessage.getSoleHeartBeatClientVOList();
	}

	/************************
	 * 取得無發送訊息的JarProjectVO 並刪除jms Message
	 * 
	 * 邏輯:
	 * 
	 * 1. jarProjectVOMap 傳入正在執行的Map
	 * 
	 * 2. 取到系統設定檔List 並移除不執行的(isNeedRun)
	 * 
	 * 3. 再取到有發送心跳協議的
	 * 
	 * 4. 比對正在執行的跟設定檔 如果map無 就放入deathList
	 * 
	 * 5. 將map裡第一次執行的放入時間
	 * 
	 * 6. deathList 放入設定檔移除掉有心跳協議的且移除間隔時間未到達的
	 * 
	 **********************/
	public static List<JarProjectVO> getDeathList(HashMap<String, JarProjectVO> jarProjectVOMap) throws JMSException {

		ConsumerMessage messageConsumer = new ConsumerMessage();

		RabbitFactory rabbitFactory = new RabbitFactory(xmlFile);

		// 先取到系統設定檔List
		List<JarProjectVO> jarVOConfigList = rabbitFactory.CreateProjectVOList();

		jarVOConfigList.removeIf((JarProjectVO jarProjectVO) -> !jarProjectVO.isNeedRun());

		// 再取到jms
		List<HeartBeatClientVO> heartBeatClientVOList = messageConsumer.getHeartBeatClientVOListFromHeart();

		List<JarProjectVO> deathList = new ArrayList<JarProjectVO>();

		// 比對正在執行的跟設定檔 如果設定檔無 就放入deathList
		for (JarProjectVO jarVOConfig : jarVOConfigList) {
			JarProjectVO jAliveVO = jarProjectVOMap.get(jarVOConfig.getBeatID());

			// 先比對jarProjectVOList跟正在執行的map 的beathId 如果沒有在map 裡面 代表他掛了 放入
			// DeathList
			if (null == jAliveVO) {
				deathList.add(jarVOConfig);
			} else {
				// 存取比對時間 jarProjectVO.set(map.time)
				jarVOConfig.setLocalDateTime(jAliveVO.getLocalDateTime());

				// 第一次執行的放入時間
				if (null == jAliveVO.getLocalDateTime()) {
					jAliveVO.setLocalDateTime(LocalDateTime.now());
				}
			}

		}

		// 移除釣有傳送心跳協議的 並將有心跳的放入現在時間
		for (HeartBeatClientVO heartBeatClientVO : heartBeatClientVOList) {
			String hearBeatID = heartBeatClientVO.getBeatID();
			JarProjectVO jAliveVO = jarProjectVOMap.get(hearBeatID);
			jAliveVO.setNotFindCount(0);

			if (jAliveVO != null) {
				jAliveVO.setLocalDateTime(LocalDateTime.now());
			}

			jarVOConfigList.removeIf((JarProjectVO jarProjectVO) -> jarProjectVO.getBeatID().equals(hearBeatID));
		}

		// 移除掉 時間間隔<設定檔時間 or 時間間隔 = null (還沒檢查過)
		for (JarProjectVO jarVO : jarVOConfigList) {

			LocalDateTime lastTime = jarVO.getLocalDateTime();

			LocalDateTime timeNow = LocalDateTime.now();

			if (lastTime == null) {
				deathList.add(jarVO);
			} else {

				ZonedDateTime lastTimeZdt = lastTime.atZone(ZoneId.of("UTC"));
				ZonedDateTime timeNowZdt = timeNow.atZone(ZoneId.of("UTC"));

				long lastTimeMillis = lastTimeZdt.toInstant().toEpochMilli();
				long timeNowMillis = timeNowZdt.toInstant().toEpochMilli();
				long intervals = timeNowMillis - lastTimeMillis;

				long configLong = jarVO.getTimeSeries();

				if (intervals > configLong) {
					deathList.add(jarVO);

					logger.debug("intervals >= configLong:" + intervals + "   " + configLong);

				} else {

					logger.debug(
							jarVO.getBeatID() + ": intervals ------------configLong:" + intervals + "   " + configLong);
				}

			}
		}

		return deathList;
	}

	/**
	 * 取得管控的程序狀態
	 * 
	 * @throws JMSException
	 * @throws IOException
	 */
	public static List<JarProjectVO> getJarProjectVOStatus(String host, int port) throws JMSException, IOException {

		SocketClient socketClient = new SocketClient(host, port);

		List<JarProjectVO> jarProjectVOList = socketClient.getJarProjectVOList();

		return jarProjectVOList;

	}
	
	/**
	 * args[0]: action args[1]:
	 * 
	 * @throws ClassNotFoundException
	 * @throws JMSException
	 * 
	 **/
	public static void main(String[] args) throws ClassNotFoundException, JMSException {

////		 // xmlFile = "D:\\JarManagerAPI.xml";
////		 //
////		 // ConsumerMessage aaa= new ConsumerMessage();
////		 // List<HeartBeatClientVO> aaaa=aaa.getSoleHeartBeatClientVOList();
////		 //
////		 // System.out.println("請輸入執行參數");
////		
//		 xmlFile = "D:\\JarManagerAPI.xml";
//		 // xmlFile = "D:\\XMLFilePath\\JarManagerAPI.xml";
//		
//		
//		 startManager();

		if (args.length == 1) {

			String action = args[0];
			switch (action) {
			case "-all":
				getAllMethodDescription();
				break;
			default:
				System.out.println(
						"請輸入正確格式: java -jar yourJarFilePath\\JarManagerAPI.jar yourXMLFilePath\\JarManagerAPI.xml -yourAction");
				System.out.println("指令說明: java -jar yourJarFilePath\\JarManagerAPI.jar -all ");
				break;
			}

		} else if (args.length > 1) {

			String action = args[1];
			xmlFile = args[0];

			System.out.println("action: " + action);
			switch (action) {
			case "-closeManager":
				closeManager();
				System.out.println("關閉中...");
				break;
			case "-startManager":
				startManager();
				System.out.println("執行中...");
				break;
			default:
				System.out.println(
						"請輸入正確格式: java -jar yourJarFilePath\\JarManagerAPI.jar yourXMLFilePath\\JarManagerAPI.xml -action");
				System.out.println("指令說明: java -jar yourJarFilePath\\JarManagerAPI.jar -all ");
				break;
			}

		} else {

			System.out.println("請輸入執行參數");
			System.out.println("指令說明: -all ");

		}

	}

}
