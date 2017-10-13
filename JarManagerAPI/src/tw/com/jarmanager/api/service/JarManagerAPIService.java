package tw.com.jarmanager.api.service;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.jarmanager.api.consumer.ConsumerMessage;
import tw.com.jarmanager.api.socket.SocketClient;
import tw.com.jarmanager.api.socket.SocketServer;
import tw.com.jarmanager.api.util.ProcessUtial;
import tw.com.jarmanager.api.vo.AnnotationVO;
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
			ManagerVO managerVO = (ManagerVO) responseVO.getObj();

			while (true) {

				if (exit) {
					break;
				}

				HashMap<String, JarProjectVO> oldJarProjectVOMap = managerVO.getJarProjectVOMap();

				List<JarProjectVO> deathList = null;
				try {
					deathList = getDeathList();
				} catch (JMSException e1) {
					logger.debug("Error:" + e1.getMessage());
				}

				try {
					HashMap<String, JarProjectVO> newJarProjectVOMap = reStart(deathList, oldJarProjectVOMap);
					managerVO.setJarProjectVOMap(newJarProjectVOMap);
				} catch (IOException e) {
					logger.debug("Error:" + e.getMessage());
				}

				List<JarProjectVO> jarProjectVOList = getJarProjectVOList(managerVO);

				try {
					logger.debug("sleep:" + managerVO.getReCheckTime());
					Thread.sleep(managerVO.getReCheckTime());
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
	public static List<JarProjectVO> getJarProjectVOList(ManagerVO managerVO) {
		HashMap<String, JarProjectVO> hashMap = managerVO.getJarProjectVOMap();
		List<JarProjectVO> jarProjectVOList = new ArrayList<JarProjectVO>();
		Set set = new HashSet();
		set = hashMap.keySet();
		Iterator it = set.iterator();

		String s;
		while (it.hasNext()) {
			s = it.next().toString();
			JarProjectVO jarProjectVO = hashMap.get(s);
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
		ApplicationContext context = new FileSystemXmlApplicationContext(xmlFile);

		ManagerVO managerVO = (ManagerVO) context.getBean("managerConfig");

		try {
			List<JarProjectVO> jarProjectVOList = managerVO.getJarProjectVOList();

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

			managerVO.setJarProjectVOMap(jarProjectVOMap);
			logger.debug("startJars() is executed!");

		} catch (Exception e) {
			logger.debug("Error: " + e.getMessage());
			responseVO.setErrorReason(e.getMessage());
		}

		responseVO.setSuccessObj(managerVO);

		return responseVO;

	}

	/**
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
				JarConsole jarConsole = new JarConsole(newProcess, fileName);
				jarProjectVO.setJarConsole(jarConsole);

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
	 * 邏輯: 1.取得現有的xml JarProjectVOList 2.取得jms裡的 List 3.移除
	 * JarProjectVOList.vo.BeatID.equal(heartBeatClientVOList.vo.BeatID)
	 * 
	 * @throws JMSException
	 */
	public static List<JarProjectVO> getDeathList() throws JMSException {
		ConsumerMessage messageConsumer = new ConsumerMessage();

		ApplicationContext context = new FileSystemXmlApplicationContext(xmlFile);
		ManagerVO managerConfig = (ManagerVO) context.getBean("managerConfig");

		List<JarProjectVO> jarProjectVOList = managerConfig.getJarProjectVOList();

		if (jarProjectVOList == null || jarProjectVOList.size() < 1) {
			return null;
		}

		List<HeartBeatClientVO> heartBeatClientVOList = messageConsumer.getHeartBeatClientVOListFromHeart();

		for (HeartBeatClientVO heartBeatClientVO : heartBeatClientVOList) {
			String hearBeatID = heartBeatClientVO.getBeatID();
			managerConfig.getJarProjectVOMap();

			jarProjectVOList.removeIf((JarProjectVO jarProjectVO) -> jarProjectVO.getBeatID().equals(hearBeatID));
		}
		return jarProjectVOList;
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

		xmlFile = "D:\\jarTest\\JarManagerAPI.xml";
		startManager();

		// if (args.length == 1) {
		//
		// String action = args[0];
		// switch (action) {
		// case "-all":
		// getAllMethodDescription();
		// break;
		//
		// }
		//
		// } else if (args.length > 1) {
		//
		// String action = args[0];
		// xmlFile = args[1];
		//
		// System.out.println("action: " + action);
		// switch (action) {
		// case "-closeManager":
		// closeManager();
		// System.out.println("關閉中...");
		// break;
		// case "-startManager":
		// startManager();
		// System.out.println("執行中...");
		// break;
		// case "action3":
		//
		// break;
		// }
		//
		// } else {
		//
		// System.out.println("請輸入執行參數");
		// System.out.println("指令說明: -all ");
		//
		// }

	}

}
