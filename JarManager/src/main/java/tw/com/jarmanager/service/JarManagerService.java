package tw.com.jarmanager.service;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.jarmanager.api.service.JarManagerAPIService;
import tw.com.jarmanager.api.vo.JarProjectVO;

@Service
public class JarManagerService {

	private final Logger logger = LoggerFactory.getLogger(JarManagerService.class);

	public void clearQueue() throws JMSException {

		JarManagerAPIService jarManagerAPIService = new JarManagerAPIService();
		jarManagerAPIService.setXmlFilePath("D:\\jarTest\\JarManagerAPI.xml");

		jarManagerAPIService.getDeathList();

	}

	public List<HeartBeatClientVO> getSoleHeartBeatClientVOList() throws JMSException {

		JarManagerAPIService jarManagerAPIService = new JarManagerAPIService();
		jarManagerAPIService.setXmlFilePath("D:\\jarTest\\JarManagerAPI.xml");

		List<HeartBeatClientVO> heartBeatClientVOList = jarManagerAPIService.getSoleHeartBeatClientVOList();

		return heartBeatClientVOList;

	}

	public List<JarProjectVO> getJarProjectVOStatus() throws IOException, JMSException {

		JarManagerAPIService jarManagerAPIService = new JarManagerAPIService();
		jarManagerAPIService.setXmlFilePath("D:\\jarTest\\JarManagerAPI.xml");

		List<JarProjectVO> jarProjectVOList = jarManagerAPIService.getJarProjectVOStatus("127.0.0.1", 9527);
		String PID_TEXT;
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();  
	       String name = runtime.getName(); // format: "pid@hostname"  
	       try {  
	           PID_TEXT=name.substring(0, name.indexOf('@'));  
	       } catch (Exception e) {  
	           PID_TEXT="-1";  
	       }
	       
	       System.out.println("PID_TEXT:"+ PID_TEXT);

		return jarProjectVOList;

	}

}
