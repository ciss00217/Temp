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
			logger.debug("addJarProjectVOXml:addJarProjectVOXml");

			File file = new File(xmlFile);
			JAXBContext jaxbContext = JAXBContext.newInstance(JarManagerAPIXMLVO.class);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JarManagerAPIXMLVO jarManagerAPIXMLVO = (JarManagerAPIXMLVO) jaxbUnmarshaller.unmarshal(file);
			
			List<JarProjectVO> list=jarManagerAPIXMLVO.getJarProjectVOList();
			
			for(JarProjectVO mJarProjectVO:list){
				if(mJarProjectVO.getBeatID().equals(jarProjectVO.getBeatID())){
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
	public List<JarProjectVO> getXMLJarProjectVOList(String XMLPath) {
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
			jarProjectVOList.add(forJsonJarVO);
		}

		return jarProjectVOList;
	}

	/*
	 * 開啟註冊在XML的jar 2.將做好的process包裝成 jarProjectVO 放到map key:BeatID"
	 */
	public static ResponseVO startJars() {

		ResponseVO responseVO = new ResponseVO();

		RabbitFactory rabbitFactory = new RabbitFactory(xmlFile);

		try {
			List<JarProjectVO> jarProjectVOList = rabbitFactory.CreateProjectVOList();

			HashMap<String, JarProjectVO> jarProjectVOMap = new HashMap<String, JarProjectVO>();

			ProcessUtial processUtial = new ProcessUtial();

			for (JarProjectVO jarProjectVO : jarProjectVOList) {
				Thread th = new Thread(new Runnable() {
					@Override
					public void run() {

						String fileName = (jarProjectVO.getFileName() + ".jar");
						String[] commandLinearr = jarProjectVO.getCommandLinearr();
						String key = jarProjectVO.getBeatID();
						Process process = null;
						JarConsole jarConsole = null;

						try {
							ProcessBuilder processBuilder = new ProcessBuilder(commandLinearr);
							process = processBuilder.start();

							jarConsole = new JarConsole(process, fileName);

							jarConsole.start();

							logger.debug("process exec!");
						} catch (IOException e) {
							logger.debug("Error:" + e.getMessage());

						}
						Long pid = processUtial.getProcessID(process);

						jarProjectVO.setPid(pid);
						jarProjectVO.setJarConsole(jarConsole);
						logger.debug("process.isAlive()! :" + process.isAlive());
						jarProjectVOMap.put(key, jarProjectVO);

					}

				});
				th.start();

			}

			responseVO.setSuccessObj(jarProjectVOMap);
			logger.debug("startJars() is executed!");

		} catch (Exception e) {
			logger.debug("Error: " + e.getMessage());
			responseVO.setErrorReason(e.getMessage());
		}

		return responseVO;

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
	 */
	public static HashMap<String, JarProjectVO> reStart(List<JarProjectVO> DeathList,
			HashMap<String, JarProjectVO> jarProjectVOMap) throws IOException {
		ProcessUtial processUtial = new ProcessUtial();

		for (JarProjectVO deathjarProjectVO : DeathList) {
			JarProjectVO jarProjectVO = jarProjectVOMap.get(deathjarProjectVO.getBeatID());

			if (jarProjectVO != null) {

				Long pid = jarProjectVO.getPid();

				logger.debug("Not Alive!!");
				logger.debug("FileName: " + deathjarProjectVO.getFileName());
				logger.debug("BeatID: " + deathjarProjectVO.getBeatID());
				int notFindCount = jarProjectVO.getNotFindCount();

				if (notFindCount >= 5) {

					logger.debug("destroy.....");

					processUtial.destoryProcess(pid);

					String fileName = (jarProjectVO.getFileName() + ".jar");
					String[] commandLinearr = jarProjectVO.getCommandLinearr();

					ProcessBuilder processBuilder = new ProcessBuilder(commandLinearr);
					Process newProcess = processBuilder.start();

					Long newPid = processUtial.getProcessID(newProcess);

					jarProjectVO.setPid(newPid);
					jarProjectVO.setNotFindCount(0);
					jarProjectVO.setLocalDateTime(null);
					JarConsole jarConsole = new JarConsole(newProcess, fileName);
					jarProjectVO.setJarConsole(jarConsole);

					logger.debug("jarProjectVOgetLocalDateTime:" + jarProjectVO.getLocalDateTime());

					String key = jarProjectVO.getBeatID();
					jarProjectVOMap.put(key, jarProjectVO);
					jarConsole.start();
				} else {
					logger.debug("notFindCount:" + notFindCount);

					logger.debug("notFindCount++");
					notFindCount++;
					jarProjectVO.setNotFindCount(notFindCount);
				}

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

	/**
	 * 取得無發送訊息的JarProjectVO 並刪除jms Message
	 * 
	 * 邏輯:
	 * 
	 * 1. 取到系統設定檔List : jarVOConfigList
	 * 
	 * 2. 取到有發送心跳協議的List : heartBeatClientVOList
	 * 
	 * 3. 取到 正在執行的 map : jarProjectVOMap
	 * 
	 * 4. 比對jarProjectVOList跟正在執行的jarProjectVOMap 的BeatID 如果沒有在map 裡面 代表他無執行 將其
	 * 放入 DeathList
	 * 
	 * 5. 比對的同時 放入檢查時間
	 * 
	 * 6. jarVOConfigList移除有傳送心跳協議的
	 * 
	 * 7. jarVOConfigList移除掉 時間間隔<設定檔時間 or 時間間隔 = null (還沒檢查過)
	 * 
	 * 8. 將jarVOConfigList內的VO 放入deathList
	 * 
	 * 9. 回傳deathList
	 * 
	 * @throws JMSException
	 */
	public static List<JarProjectVO> getDeathList(HashMap<String, JarProjectVO> jarProjectVOMap) throws JMSException {

		ConsumerMessage messageConsumer = new ConsumerMessage();

		RabbitFactory rabbitFactory = new RabbitFactory(xmlFile);

		// 問: 如何拿到上次成功收到時間

		// 答: 第一次為null 都算成功 裝入現在時間

		// 第二次時 存活的裝入現在時間 這樣的話

		// 第三次 失敗的間隔時間就是第一次+第二次的間隔時間

		// 先取到系統設定檔List
		List<JarProjectVO> jarVOConfigList = rabbitFactory.CreateProjectVOList();

		// 再取到jms
		List<HeartBeatClientVO> heartBeatClientVOList = messageConsumer.getHeartBeatClientVOListFromHeart();

		// 拿起正在執行的 map
		// jarProjectVOMap

		// 先比對jarProjectVOList跟正在執行的map 的beathId 如果沒有在map 裡面 代表他掛了 放入 DeathList

		List<JarProjectVO> deathList = new ArrayList<JarProjectVO>();

		for (JarProjectVO jarVOConfig : jarVOConfigList) {
			JarProjectVO jAliveVO = jarProjectVOMap.get(jarVOConfig.getBeatID());

			// 先比對jarProjectVOList跟正在執行的map 的beathId 如果沒有在map 裡面 代表他掛了 放入
			// DeathList
			if (null == jAliveVO) {
				deathList.add(jarVOConfig);
			} else {
				// 存取比對時間 jarProjectVO.set(map.time)
				jarVOConfig.setLocalDateTime(jAliveVO.getLocalDateTime());

				if (null == jAliveVO.getLocalDateTime()) {
					jAliveVO.setLocalDateTime(LocalDateTime.now());
				}

				// map.set(now time)
				// jAliveVO.setLocalDateTime(LocalDateTime.now());

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

		// 時間間隔 : 一開始為null 第一次是null的話放入 現在時間
		// 當 時間間隔<設定檔時間
		//

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

				// if 間隔時間intervals> configLong 代表如果正常執行的話應該已經送上去了 但是我們沒拿到
				// 所以他已經掛了 所以要放入
				// deatList
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

		// // xmlFile = "D:\\jarTest\\JarManagerAPI.xml";
		xmlFile = "D:\\XMLFilePath\\JarManagerAPI.xml";

		startManager();

		if (args.length == 1) {

			String action = args[0];
			switch (action) {
			case "-all":
				getAllMethodDescription();
				break;
			default:
				System.out.println(
						"請輸入正確格式: -java -jar yourJarFilePath\\JarManagerAPI.jar yourXMLFilePath\\JarManagerAPI.xml -yourAction");
				System.out.println("指令說明: -java -jar yourJarFilePath\\JarManagerAPI.jar -all ");
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
						"請輸入正確格式: -java -jar yourJarFilePath\\JarManagerAPI.jar yourXMLFilePath\\JarManagerAPI.xml -action");
				System.out.println("指令說明: -java -jar yourJarFilePath\\JarManagerAPI.jar -all ");
				break;
			}

		} else {

			System.out.println("請輸入執行參數");
			System.out.println("指令說明: -all ");

		}

	}

}
