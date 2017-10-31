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

	public boolean addJarProjectVOXml(JarProjectVO jarProjectVO) {

		try {
			logger.debug("addJarProjectVOXml:addJarProjectVOXml");

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
		}

		return true;
	}

	public boolean updateJarProjectVOXml(JarProjectVO jarProjectVO) {

		try {
			logger.debug("updateJarProjectVOXml");

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
		}

		return true;
	}

	public boolean deleteJarProjectVOXml(List<String> jarIDList) {

		try {
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

	@AnnotationVO(description = "關閉程序", methodName = "-closeManager  D:\\yourFileXmlPath")
	public static void closeManager() {
		JarManagerAPIService.exit = true;
	}

	// 指令說明
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

	
	
	/***************
	 * 流程為:
	 * 
	 * 1.啟動	
	 * 		1.取得XML		
	 * 		
	 * 		2.轉成物件   	//插入濾除tag是false的
	 * 
	 * 		3.取出List<jarVO>
	 * 
	 *		4.迴圈執行啟動
	 *
	 * 
	 * 2.檢查
	 * 		1.濾除xml 被刪除的 		//之前沒 並且在濾除時 依靠pid 去關閉程序
	 * 		
	 * 		2.取得須重啟的list:
	 * 						 
	 * 								1. jarProjectVOMap 傳入正在執行的Map
	 * 
	 * 								2. 取到系統設定檔List   (並移除不執行的(isNeedRun)) //之前沒
	 * 
	 * 								3. 再取到有發送心跳協議的	
	 * 
	 * 								4. 比對正在執行的跟設定檔 如果map無 就放入deathList
	 * 
	 * 								5. 將map裡第一次執行的放入時間
	 * 
	 * 								6. deathList 放入設定檔移除掉有心跳協議的且移除間隔時間未到達的
	 * 
	 * 3.重啟
	 * 
	 * 4.放入狀態 到 socketServer
	 * 
	 * 5.等待
	 * 
	 * 
	 * ************/
	@AnnotationVO(description = "啟動JarManagerService 用來監控觀察底下的JAR 如果client 阻塞 或 關閉 就重啟它", methodName = "-startManager D:\\yourFileXmlPath")
	public static void startManager() {

		ResponseVO responseVO = startJars();

		logger.debug("responseVO isSuccess: " + responseVO.isSuccess());

		if (responseVO.isSuccess()) {

			SocketServer socketServer = new SocketServer(9527);
			socketServer.start();

			// 獲得啟動的managerVO
			HashMap<String, JarProjectVO> oldJarProjectVOMap = (HashMap<String, JarProjectVO>) responseVO.getObj();

			while (true) {

				if (exit) {
					break;
				}

				try {
					oldJarProjectVOMap = mapRemoveNoExitByXml(oldJarProjectVOMap);
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				List<JarProjectVO> deathList = null;
				try {
					deathList = getDeathList(oldJarProjectVOMap);
				} catch (JMSException e1) {
					logger.debug("Error:" + e1.getMessage());
				}

				try {
					HashMap<String, JarProjectVO> newJarProjectVOMap = reStart(deathList, oldJarProjectVOMap);
					oldJarProjectVOMap = newJarProjectVOMap;
				} catch (IOException e) {
					logger.debug("Error:" + e.getMessage());
				}

				List<JarProjectVO> jarProjectVOList = getJarProjectVOList(oldJarProjectVOMap);

				try {
					RabbitFactory rabbitFactory = new RabbitFactory(xmlFile);
					logger.debug("sleep:" + rabbitFactory.CreateManagerVO().getReCheckTime());
					Thread.sleep(rabbitFactory.CreateManagerVO().getReCheckTime());
				} catch (InterruptedException e) {
					logger.debug("Error:" + e.getMessage());
				}
				socketServer.setJarProjectVOList(jarProjectVOList);

			}

		}
	}

	/***
	 * 移除不存在xml的map
	 * 
	 * @throws IOException
	 * 
	 ****/
	public static HashMap<String, JarProjectVO> mapRemoveNoExitByXml(HashMap<String, JarProjectVO> oldJarProjectVOMap)
			throws IOException {
		List<JarProjectVO> jarXMLList = getXMLJarProjectVOList(xmlFile);

		Set keySet = new HashSet();
		keySet = oldJarProjectVOMap.keySet();
		Iterator it = keySet.iterator();

		List<String> mapRemove = new ArrayList<String>();

		while (it.hasNext()) {
			boolean isExit = false;
			String beatIDByMap = it.next().toString();
			for (JarProjectVO jarXMLVO : jarXMLList) {
				String beatIDByXml = jarXMLVO.getBeatID();

				if (beatIDByXml.equals(beatIDByMap)) {
					isExit = true;
				}
			}

			if (!isExit) {
				mapRemove.add(beatIDByMap);
			}
		}

		for (String removeKey : mapRemove) {
			JarProjectVO jarProjectVO = oldJarProjectVOMap.get(removeKey);
			if (jarProjectVO != null) {
				Long pid = jarProjectVO.getPid();

				if (pid != null) {
					ProcessUtial.destoryProcess(pid);
				}
			}

			oldJarProjectVOMap.remove(removeKey);
		}

		return oldJarProjectVOMap;
	}

	/*******************************************
	 * 回傳可轉換成json的JarProjectVOList ps:用於查詢狀態
	 *****************************************/
	public static List<JarProjectVO> getJarProjectVOList(HashMap<String, JarProjectVO> jarProjectVOMap) {
		if (jarProjectVOMap == null) {
			return null;
		}

		List<JarProjectVO> jarProjectVOList = new ArrayList<JarProjectVO>();
		Set set = new HashSet();
		set = jarProjectVOMap.keySet();
		Iterator it = set.iterator();

		String s;
		while (it.hasNext()) {
			s = it.next().toString();
			JarProjectVO jarProjectVO = jarProjectVOMap.get(s);
			JarProjectVO forJsonJarVO = new JarProjectVO();
			forJsonJarVO.setBeatID(jarProjectVO.getBeatID());
			forJsonJarVO.setDescription(jarProjectVO.getDescription());
			forJsonJarVO.setFileName(jarProjectVO.getFileName());
			forJsonJarVO.setFilePathXMLList(jarProjectVO.getFilePathXMLList());
			forJsonJarVO.setJarFilePath(jarProjectVO.getJarFilePath());
			forJsonJarVO.setNotFindCount(jarProjectVO.getNotFindCount());
			forJsonJarVO.setTimeSeries(jarProjectVO.getTimeSeries());
			forJsonJarVO.setNeedRun(jarProjectVO.isNeedRun());
			forJsonJarVO.setPid(jarProjectVO.getPid());
			jarProjectVOList.add(forJsonJarVO);
		}

		return jarProjectVOList;
	}

	/*
	 * 開啟註冊在XML並且狀態是要執行的 的jar 2.將做好的process包裝成 jarProjectVO 放到map key:BeatID"
	 */
	public static ResponseVO startJars() {

		ResponseVO responseVO = new ResponseVO();

		RabbitFactory rabbitFactory = new RabbitFactory(xmlFile);

		try {
			List<JarProjectVO> jarProjectVOList = rabbitFactory.CreateProjectVOList();

			HashMap<String, JarProjectVO> jarProjectVOMap = new HashMap<String, JarProjectVO>();

			for (JarProjectVO jarProjectVO : jarProjectVOList) {
				boolean isrun = jarProjectVO.isNeedRun();
				if (jarProjectVO.isNeedRun()) {
					startJarVO(jarProjectVO, jarProjectVOMap);
				}

			}

			responseVO.setSuccessObj(jarProjectVOMap);
			logger.debug("startJars() is executed!");

		} catch (Exception e) {
			logger.debug("Error: " + e.getMessage());
			responseVO.setErrorReason(e.getMessage());
		}

		return responseVO;

	}

	public static HashMap<String, JarProjectVO> startJarVO(JarProjectVO jarProjectVO,
			HashMap<String, JarProjectVO> jarProjectVOMap) {
		try {
			ProcessUtial processUtial = new ProcessUtial();
			String fileName = (jarProjectVO.getFileName() + ".jar");
			String[] commandLinearr = jarProjectVO.getCommandLinearr();

			ProcessBuilder processBuilder = new ProcessBuilder(commandLinearr);
			Process newProcess;

			newProcess = processBuilder.start();

			Long newPid = processUtial.getProcessID(newProcess);

			jarProjectVO.setPid(newPid);
			jarProjectVO.setNotFindCount(0);
			jarProjectVO.setLocalDateTime(null);
			JarConsole jarConsole = new JarConsole(newProcess, fileName);
			jarProjectVO.setJarConsole(jarConsole);

			logger.debug("jarProjectVOgetLocalDateTime:" + jarProjectVO.getLocalDateTime());

			String key = jarProjectVO.getBeatID();
			jarConsole.start();
			jarProjectVOMap.put(key, jarProjectVO);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jarProjectVOMap;

	}

	/**
	 * 現在有正在跑的process map
	 * 
	 * 沒傳送到jms的 DeathList vo
	 * 
	 * 
	 * 
	 * 依靠DeathList的JarProjectVO來處理jarProjectVOMap
	 * 
	 * 重新啟動 notFindCount次數>=5的JarProjectVO
	 * 
	 * notFindCount<5 notFindCount++
	 * 
	 * 
	 * 1.傳入 須重啟的List
	 * 
	 * 2.傳入正在執行的map
	 * 
	 * 3.map濾除錯誤次數小於5的
	 * 
	 * 4.關閉有在map的list程序(錯誤次數小於5的除外)
	 * 
	 * 5.重啟需重啟的List
	 * 
	 * 
	 * 
	 */
	public static HashMap<String, JarProjectVO> reStart(List<JarProjectVO> DeathList,
			HashMap<String, JarProjectVO> jarProjectVOMap) throws IOException {
		ProcessUtial processUtial = new ProcessUtial();

		for (JarProjectVO deathjarProjectVO : DeathList) {
			JarProjectVO jarProjectVO = jarProjectVOMap.get(deathjarProjectVO.getBeatID());

			// 如果說有在map 代表他正在執行 把她關閉 在開啟
			if (jarProjectVO != null) {

				Long pid = jarProjectVO.getPid();

				logger.debug("Not Alive!!");
				logger.debug("FileName: " + deathjarProjectVO.getFileName());
				logger.debug("BeatID: " + deathjarProjectVO.getBeatID());
				int notFindCount = jarProjectVO.getNotFindCount();

				if (notFindCount >= 5) {

					logger.debug("destroy.....");

					processUtial.destoryProcess(pid);

					jarProjectVOMap = startJarVO(jarProjectVO, jarProjectVOMap);

				} else {
					logger.debug("notFindCount:" + notFindCount);

					logger.debug("notFindCount++");
					notFindCount++;
					jarProjectVO.setNotFindCount(notFindCount);
				}

				// 如果說有沒在map 開啟新的
			} else {
				jarProjectVOMap = startJarVO(deathjarProjectVO, jarProjectVOMap);

			}
		}
		return jarProjectVOMap;
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

		//// xmlFile = "D:\\JarManagerAPI.xml";
		////
		//// ConsumerMessage aaa= new ConsumerMessage();
		//// List<HeartBeatClientVO> aaaa=aaa.getSoleHeartBeatClientVOList();
		////
		//// System.out.println("請輸入執行參數");
		//
		// xmlFile = "D:\\JarManagerAPI.xml";
		// // xmlFile = "D:\\XMLFilePath\\JarManagerAPI.xml";
		//
		//
		// startManager();

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
