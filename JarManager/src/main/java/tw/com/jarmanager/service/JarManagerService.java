package tw.com.jarmanager.service;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Service;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.jarmanager.api.service.JarManagerAPIService;
import tw.com.jarmanager.api.vo.JarProjectVO;

@Service
public class JarManagerService {

	private final Logger logger = LoggerFactory.getLogger(JarManagerService.class);

	public void clearQueue() throws JMSException {
		ApplicationContext context = new FileSystemXmlApplicationContext("classpath:jarmanager-config.xml");
		String xmlpath = (String) context.getBean("xmlpath");
		JarManagerAPIService jarManagerAPIService = new JarManagerAPIService();
		jarManagerAPIService.setXmlFilePath(xmlpath);

		// jarManagerAPIService.getDeathList();

	}

	public List<HeartBeatClientVO> getSoleHeartBeatClientVOList() throws JMSException {
		ApplicationContext context = new FileSystemXmlApplicationContext("classpath:jarmanager-config.xml");
		String xmlpath = (String) context.getBean("xmlpath");
		JarManagerAPIService jarManagerAPIService = new JarManagerAPIService();
		jarManagerAPIService.setXmlFilePath(xmlpath);

		List<HeartBeatClientVO> heartBeatClientVOList = jarManagerAPIService.getSoleHeartBeatClientVOList();

		return heartBeatClientVOList;

	}

	public List<JarProjectVO> getJarProjectVOStatus() throws IOException, JMSException {
		ApplicationContext context = new FileSystemXmlApplicationContext("classpath:jarmanager-config.xml");
		String xmlpath = (String) context.getBean("xmlpath");
		JarManagerAPIService jarManagerAPIService = new JarManagerAPIService();
		jarManagerAPIService.setXmlFilePath(xmlpath);

		List<JarProjectVO> jarProjectVOList = jarManagerAPIService.getJarProjectVOStatus("127.0.0.1", 9527);
		String PID_TEXT;
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String name = runtime.getName(); // format: "pid@hostname"
		try {
			PID_TEXT = name.substring(0, name.indexOf('@'));
		} catch (Exception e) {
			PID_TEXT = "-1";
		}

		System.out.println("PID_TEXT:" + PID_TEXT);

		return jarProjectVOList;

	}

	public List<JarProjectVO> getXMLJarPeojectVOs() throws IOException, JMSException {
		ApplicationContext context = new FileSystemXmlApplicationContext("classpath:jarmanager-config.xml");
		String xmlpath = (String) context.getBean("xmlpath");
		JarManagerAPIService jarManagerAPIService = new JarManagerAPIService();
		jarManagerAPIService.setXmlFilePath(xmlpath);

		List<JarProjectVO> jarProjectVOList = jarManagerAPIService.getXMLJarProjectVOList(xmlpath);

		return jarProjectVOList;

	}

	public boolean addJarProjectVOXml(JarProjectVO jarProjectVO) throws IOException, JMSException {
		ApplicationContext context = new FileSystemXmlApplicationContext("classpath:jarmanager-config.xml");
		String xmlpath = (String) context.getBean("xmlpath");
		JarManagerAPIService jarManagerAPIService = new JarManagerAPIService();
		jarManagerAPIService.setXmlFilePath(xmlpath);

		return jarManagerAPIService.addJarProjectVOXml(jarProjectVO);
	}
	
	public boolean deleteJarProjectVOXml(List<String> ids) throws IOException, JMSException {
		ApplicationContext context = new FileSystemXmlApplicationContext("classpath:jarmanager-config.xml");
		String xmlpath = (String) context.getBean("xmlpath");
		JarManagerAPIService jarManagerAPIService = new JarManagerAPIService();
		jarManagerAPIService.setXmlFilePath(xmlpath);

		return jarManagerAPIService.deleteJarProjectVOXml(ids);
	}

}
