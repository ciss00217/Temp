package tw.com.jarmanager.api.service;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.jarmanager.api.consumer.ConsumerMessage;
import tw.com.jarmanager.api.vo.AnnotationVO;
import tw.com.jarmanager.api.vo.JarProjectVO;
import tw.com.jarmanager.api.vo.ManagerVO;
import tw.com.jarmanager.api.vo.ResponseVO;

public class JarManagerAPIService {
	private static final Logger logger = LogManager.getLogger(JarManagerAPIService.class);
	static ResponseVO responseVO;
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

	/**
	 * 邏輯 1.startJars 得到啟動的jarVO 放入 HashMap
	 * 
	 * 2.
	 * 
	 **/
	@AnnotationVO(description = "啟動JarManagerService 用來監控觀察底下的JAR 如果client 阻塞 或 關閉 就重啟它", methodName = "-startManager D:\\yourFileXmlPath")
	public static void startManager() {

		ResponseVO responseVO = startJars();

		logger.debug("responseVO isSuccess: " + responseVO.isSuccess());

		if (responseVO.isSuccess()) {

			// 獲得啟動的managerVO
			ManagerVO managerVO = (ManagerVO) responseVO.getObj();
			while (true) {

				if (exit) {

					logger.debug("程序已關閉");
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

				try {
					logger.debug("sleep:" + managerVO.getReCheckTime());
					Thread.sleep(managerVO.getReCheckTime());
				} catch (InterruptedException e) {
					logger.debug("Error:" + e.getMessage());
				}

			}

		}
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

						jarProjectVO.setProcess(process);
						jarProjectVO.setJarConsole(jarConsole);
						logger.debug("process.isAlive()! :" + process.isAlive());
						jarProjectVOMap.put(key, jarProjectVO);

					}

				});
				th.start();

			}

			try {

				logger.debug("Wait Client Send BeatID");
				logger.debug("sleep:" + managerVO.getLoadingTime());
				Thread.sleep(managerVO.getLoadingTime());

			} catch (InterruptedException e1) {
				logger.debug("Error:" + e1.getMessage());
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
	 * 重新啟動放未找到次數為5的
	 * 
	 * DeathList的JarProjectVO
	 * 
	 */
	public static HashMap<String, JarProjectVO> reStart(List<JarProjectVO> DeathList,
			HashMap<String, JarProjectVO> jarProjectVOMap) throws IOException {

		for (JarProjectVO deathjarProjectVO : DeathList) {
			JarProjectVO jarProjectVO = jarProjectVOMap.get(deathjarProjectVO.getBeatID());

			int errorNumber = jarProjectVO.getErrorNumber();

			logger.debug("Not Alive!!");
			logger.debug("FileName: " + deathjarProjectVO.getFileName());
			logger.debug("BeatID: " + deathjarProjectVO.getBeatID());

			if (errorNumber >= 5) {

				logger.debug("destroy.....");
				Process oldProcess = jarProjectVO.getProcess();
				oldProcess.destroy();

				String fileName = (jarProjectVO.getFileName() + ".jar");
				String[] commandLinearr = jarProjectVO.getCommandLinearr();

				ProcessBuilder processBuilder = new ProcessBuilder(commandLinearr);
				Process newProcess = processBuilder.start();

				jarProjectVO.setProcess(newProcess);
				JarConsole jarConsole = new JarConsole(newProcess, fileName);
				jarProjectVO.setJarConsole(jarConsole);

				String key = jarProjectVO.getBeatID();
				jarProjectVOMap.put(key, jarProjectVO);
				jarConsole.start();
			} else if (errorNumber <= 5) {

				logger.debug("not find count +1");

				String key = jarProjectVO.getBeatID();
				jarProjectVO.setErrorNumber(errorNumber + 1);
				jarProjectVOMap.put(key, jarProjectVO);

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
			jarProjectVOList.removeIf((JarProjectVO jarProjectVO) -> jarProjectVO.getBeatID().equals(hearBeatID));
		}
		return jarProjectVOList;
	}

	/**
	 * 取得管控的程序狀態
	 * 
	 * 邏輯: 1.取得現有的xml JarProjectVOList 2.取得jms裡的 List 3.裝入狀態
	 * JarProjectVOList.vo.BeatID.equal(heartBeatClientVOList.vo.BeatID)
	 * 
	 * @throws JMSException
	 */
	public static List<JarProjectVO> getJarProjectVOStatus() throws JMSException {
		ConsumerMessage messageConsumer = new ConsumerMessage();

		ApplicationContext context = new FileSystemXmlApplicationContext(xmlFile);

		ManagerVO managerConfig = (ManagerVO) context.getBean("managerConfig");

		List<JarProjectVO> jarProjectVOList = managerConfig.getJarProjectVOList();

		if (jarProjectVOList == null || jarProjectVOList.size() < 1) {
			return null;
		}

		List<HeartBeatClientVO> heartBeatClientVOList = messageConsumer.getHeartBeatClientVOList();

		for (JarProjectVO jarProjectVO : jarProjectVOList) {
			for (HeartBeatClientVO heartBeatClientVO : heartBeatClientVOList) {
				if (jarProjectVO.getBeatID().equals(heartBeatClientVO.getBeatID())) {
					jarProjectVO.setTimeSeries(heartBeatClientVO.getTimeSeries());
					jarProjectVO.setIsAlive(true);
				}
			}

		}
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

		// xmlFile="D:\\jarTest\\JarManagerAPI.xml";
		// startManager();

		if (args.length == 1) {

			String action = args[0];
			switch (action) {
			case "-all":
				getAllMethodDescription();
				break;

			}

		} else if (args.length > 1) {

			String action = args[0];
			xmlFile = args[1];

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
			case "action3":

				break;
			}

		} else {

			System.out.println("請輸入執行參數");
			System.out.println("指令說明: -all ");

		}

	}

}
