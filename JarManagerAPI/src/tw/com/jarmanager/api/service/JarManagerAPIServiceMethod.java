package tw.com.jarmanager.api.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.jarmanager.api.consumer.ConsumerMessage;
import tw.com.jarmanager.api.socket.SocketServer;
import tw.com.jarmanager.api.util.ProcessUtial;
import tw.com.jarmanager.api.vo.JarProjectVO;

public class JarManagerAPIServiceMethod {
	private static final Logger logger = LogManager.getLogger(JarManagerAPIServiceMethod.class);
	
	/***********
	 * 存放狀態到 socketServer
	 * ****************/
	public static void setStatusToSocketServer(HashMap<String, JarProjectVO> jarProjectVOMap,SocketServer socketServer){
		List<JarProjectVO> jarProjectVOList = JarManagerAPIServiceMethod.getJarProjectVOListByMap(jarProjectVOMap);
		
		socketServer.setJarProjectVOList(jarProjectVOList);
	}

	/***************
	 * 重啟jarProjectVOs 並放入 jarProjectVOMap
	 * 
	 * jarProjectVOs : 要重啟的jar List
	 * 
	 ***************/
	public static HashMap<String, JarProjectVO> startJars(List<JarProjectVO> jarProjectVOs) {
		HashMap<String, JarProjectVO> jarProjectVOMap = new HashMap<String, JarProjectVO>();

		for (JarProjectVO jarProjectVO : jarProjectVOs) {
			jarProjectVOMap = startJarVO(jarProjectVO, jarProjectVOMap);
		}

		return jarProjectVOMap;
	}

	/***************
	 * 重啟jarProjectVO 並放入 jarProjectVOMap
	 * 
	 * jarProjectVO : 要重啟的jar
	 * 
	 * jarProjectVOMap: 正在執行的map
	 * 
	 ***************/
	public static HashMap<String, JarProjectVO> startJarVO(JarProjectVO jarProjectVO,
			HashMap<String, JarProjectVO> jarProjectVOMap) {
		try {
			logger.debug("startJarVO :" + jarProjectVO.getBeatID());
			
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

	/******************
	 * 關閉須關閉的資料
	 * 獲得需要重啟的資料 reStarts
	 * 
	 * 邏輯 
	 * 		先從SocketServer.isApiXMLChange 獲取是否有改
	 * 	
	 * 			SocketServer.hashMap<id>
	 * 
	 * 		用有修改的id去和xml比對
	 * 
	 * 		1. 如果不存在代表被刪除 map 需要刪除
	 * 
	 * 		2. 如果存在代表要重啟他		
	 *  
	 * 
	 * 		有改
	 * 			1. 抓取hashMap
	 * 
	 * 			2. HashSet<string> changeIDs=socketServer.getHashSet
	 * 		
	 * 			3. list<string> removeIDs=getReMoveJar(changeIDs)
	 * 
	 * 			4. reMoveJar(removeIDs)
	 * 
	 * 			5. List<JarProjectVO> restartJarVOs = getRestartJarVOs(changeIDs);
	 * 
	 * 			6. 走訪map 檢查map.jarVO.time 如果 time = null 代表是第一次 放入現在時間
	 * 		
	 * 			7. liveJars=getHeartBeatIDs
	 * 
	 * 			8. 走訪liveJars 將有在map 裡的liveJars.jar.id放入現在時間
	 * 
	 * 			9. deathList = (xmlJar - liveJars)  
	 * 
	 * 			10. deathList 移除 (time.now - map.jarVO.time<deathList.jarVO.timeSeries)
	 * 
	 *  
	 *  		11. map.get(deathList.BeatID) 
	 * 
	 * 				當map沒發現這id 代表他是後來新增的 因為之前已經放入jarVOs所以不動作
	 * 				當map.jar.error<5 代表還在容錯範圍內 err++ deathList.remove(id)
	 * 				當map.jar.error>5 代表錯了要放入重啟
	 * 
	 * 			12 restartJarVOs=restartJarVOs+deathList 排除重複
	 * 			
	 * 
	 * 		沒改
	 * 			1. 走訪map 檢查map.jarVO.time 如果 time = null 代表是第一次 放入現在時間
	 * 		
	 * 			2. liveJars=getHeartBeatIDs
	 * 
	 * 			3. 走訪liveJars 將有在map 裡的liveJars.jar.id放入現在時間
	 * 
	 * 			4. deathList = (xmlJar - liveJars)  
	 * 
	 * 			5. deathList 移除 ((time.now - map.jarVO.time) < deathList.jarVO.timeSeries )
	 * 
	 *  
	 *  		6. map.get(deathList.BeatID) 
	 * 
	 * 				當map沒發現這id 代表他是後來新增的 因為之前已經放入jarVOs所以不動作
	 * 				當map.jar.error<5 代表還在容錯範圍內 err++ deathList.remove(id)
	 * 				當map.jar.error>5 代表錯了要放入重啟
	 * 
	 * 			7. restartJarVOs=deathList 

	 * @throws IOException 
	 * @throws JMSException 
	 * 
	 * ************************/
	public static List<JarProjectVO> checkData(HashMap<String, JarProjectVO> jarProjectVOMap, SocketServer socketServer)
			throws IOException, JMSException {
		if (JarManagerAPIService.XMLjarVOs == null) {
			logger.debug("JarManagerAPIService.XMLjarVOs == null");
			return null;
		}

		List<JarProjectVO> restartJarVOs = new ArrayList<JarProjectVO>();

		if (socketServer.isApiXMLChange()) {
			HashSet<String> changeIDs = socketServer.getChangeIdSet();

			List<String> removeIDs = getReMoveJar(changeIDs);

			jarProjectVOMap = reMoveJar(jarProjectVOMap, removeIDs);

			restartJarVOs = getRestartJarVOs(changeIDs);
			
			socketServer.setApiXMLChange(false);
			
			socketServer.setChangeIdSet(new HashSet<String>());

		}
		


		for (String key : jarProjectVOMap.keySet()) {
			JarProjectVO jarProjectVO = jarProjectVOMap.get(key);

			LocalDateTime localDateTime = jarProjectVO.getLocalDateTime();

			if (localDateTime == null) {
				jarProjectVO.setLocalDateTime(LocalDateTime.now());
			}
		}

		List<HeartBeatClientVO> liveJars = getHeartBeatClientVOs();
		List<JarProjectVO> deathList = JarManagerAPIService.XMLjarVOs.stream().collect(Collectors.toList());

		for (HeartBeatClientVO heartBeatClientVO : liveJars) {
			String beatID = heartBeatClientVO.getBeatID();
			JarProjectVO jarMapVO = jarProjectVOMap.get(beatID);
			if (jarMapVO != null) {
				jarMapVO.setNotFindCount(0);
				jarMapVO.setFirstSuccessRun(true);
				jarMapVO.setLocalDateTime(LocalDateTime.now());
			}
			deathList.removeIf((JarProjectVO jarProjectVO) -> jarProjectVO.getBeatID().equals(beatID));
		}

		List<String> aliveJarIDs = new ArrayList<String>();
		for (JarProjectVO jarProjectVO : deathList) {
			String deathBeatID = jarProjectVO.getBeatID();
			JarProjectVO JarMapVO = jarProjectVOMap.get(deathBeatID);

			if (JarMapVO != null) {

				LocalDateTime lastTime = JarMapVO.getLocalDateTime();

				LocalDateTime timeNow = LocalDateTime.now();

				ZonedDateTime lastTimeZdt = lastTime.atZone(ZoneId.of("UTC"));
				ZonedDateTime timeNowZdt = timeNow.atZone(ZoneId.of("UTC"));

				long lastTimeMillis = lastTimeZdt.toInstant().toEpochMilli();
				long timeNowMillis = timeNowZdt.toInstant().toEpochMilli();
				long intervals = timeNowMillis - lastTimeMillis;

				long configLong = jarProjectVO.getTimeSeries();

				if (intervals > configLong) {
					logger.debug(jarProjectVO.getBeatID() + "間隔毫秒>= 設定毫秒:" + intervals + "   " + configLong);
					logger.debug(jarProjectVO.getBeatID() + ": 繼續是否重啟判別");
				} else {
					logger.debug(jarProjectVO.getBeatID() + ": 間隔毫秒 <=設定毫秒:" + intervals + "---" + configLong);
					logger.debug(jarProjectVO.getBeatID() + ": 設定為存活");

					aliveJarIDs.add(jarProjectVO.getBeatID());
				}
			}

		}

		for (JarProjectVO deathJarVO : deathList) {
			JarProjectVO jarMapVO = jarProjectVOMap.get(deathJarVO.getBeatID());

			if (jarMapVO != null) {

				int errorConut = jarMapVO.getNotFindCount();

				if (errorConut < 5) {
					logger.debug(jarMapVO.getBeatID() + ": 錯誤次數: "+errorConut+" 小於5");
					logger.debug(jarMapVO.getBeatID() + ": 設定為存活");
					logger.debug(jarMapVO.getBeatID() + ": 錯誤次數"+errorConut+"+1");
					
					errorConut++;
					
					jarMapVO.setNotFindCount(errorConut);
					aliveJarIDs.add(jarMapVO.getBeatID());
				}
			}

		}

		for (JarProjectVO restartJarVO : restartJarVOs) {
			aliveJarIDs.add(restartJarVO.getBeatID());
		}

		for (String id : aliveJarIDs) {
			deathList.removeIf((JarProjectVO jarProjectVO) -> jarProjectVO.getBeatID().equals(id));
		}

		restartJarVOs.addAll(deathList);
		return restartJarVOs;

	}
	
	private static List<HeartBeatClientVO> getHeartBeatClientVOs() throws JMSException {
		ConsumerMessage messageConsumer = new ConsumerMessage();
		// 再取到jms
		List<HeartBeatClientVO> heartBeatClientVOList = messageConsumer.getHeartBeatClientVOListFromHeart();

		return heartBeatClientVOList;
	}

	/********
	 * 關閉 map 裡的jar 並且移除 by removeIDs
	 **********/
	private static HashMap<String, JarProjectVO> reMoveJar(HashMap<String, JarProjectVO> jarProjectVOMap,
			List<String> removeIDs) throws IOException {

		for (String removeID : removeIDs) {
			Long pid = null;
			JarProjectVO jarProjectVO = jarProjectVOMap.get(removeID);
			if(jarProjectVO!=null){
				 pid = jarProjectVO.getPid();
			}
		
			if (pid != null) {
				ProcessUtial.destoryProcess(pid);
			}
			jarProjectVOMap.remove(removeID);
		}

		return jarProjectVOMap;
	}

	private static List<String> getReMoveJar(HashSet<String> changeIDs) {
		List<String> reMoveJarIDs = new ArrayList<String>();
		List<JarProjectVO> changeJarVOs = new ArrayList<JarProjectVO>();
		List<JarProjectVO> XMLjarVOs = JarManagerAPIService.XMLjarVOs;

		for (String changeID : changeIDs) {
			boolean isExit = false;
			for (JarProjectVO jarProjectVO : XMLjarVOs) {

				if (jarProjectVO.getBeatID().equals(changeID)) {
					changeJarVOs.add(jarProjectVO);
					isExit = true;
				}
			}

			if (!isExit) {
				reMoveJarIDs.add(changeID);
			}

		}

		return reMoveJarIDs;

	}

	private static List<JarProjectVO> getRestartJarVOs(HashSet<String> changeIDs) {
		List<String> reMoveJarIDs = new ArrayList<String>();
		List<JarProjectVO> changeJarVOs = new ArrayList<JarProjectVO>();
		List<JarProjectVO> XMLjarVOs = JarManagerAPIService.XMLjarVOs;

		for (String changeID : changeIDs) {
			boolean isExit = false;
			for (JarProjectVO jarProjectVO : XMLjarVOs) {

				if (jarProjectVO.getBeatID().equals(changeID)) {
					changeJarVOs.add(jarProjectVO);
					isExit = true;
				}
			}

			if (!isExit) {
				reMoveJarIDs.add(changeID);
			}

		}

		return changeJarVOs;

	}

	public static HashMap<String, JarProjectVO> reStart(List<JarProjectVO> reStartList,
			HashMap<String, JarProjectVO> jarProjectVOMap) throws IOException {
		ProcessUtial processUtial = new ProcessUtial();

		for (JarProjectVO reStartVO : reStartList) {
			JarProjectVO jarProjectVO = jarProjectVOMap.get(reStartVO.getBeatID());

			if (jarProjectVO != null) {
				Long pid = jarProjectVO.getPid();

				logger.debug("FileName: " + reStartVO.getFileName());
				logger.debug("BeatID: " + reStartVO.getBeatID());
				logger.debug("destroy.....");

				processUtial.destoryProcess(pid);

				
			}
			if (reStartVO != null) {
				jarProjectVOMap = startJarVO(reStartVO, jarProjectVOMap);
			}

		}
		return jarProjectVOMap;
	}
	
	/*******************************************
	 * 回傳可轉換成json的JarProjectVOList ps:用於查詢狀態
	 *****************************************/
	public static List<JarProjectVO> getJarProjectVOListByMap(HashMap<String, JarProjectVO> jarProjectVOMap) {
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
			forJsonJarVO.setFirstSuccessRun(jarProjectVO.getFirstSuccessRun());
			forJsonJarVO.setPid(jarProjectVO.getPid());
			jarProjectVOList.add(forJsonJarVO);
		}

		return jarProjectVOList;
	}

}
