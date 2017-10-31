package tw.com.jarmanager.api.consumer;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.jarmanager.api.factory.RabbitFactory;
import tw.com.jarmanager.api.service.JarManagerAPIService;
import tw.com.jms.util.Util;

public class ConsumerMessage {
	private static final Logger logger = LogManager.getLogger(ConsumerMessage.class);

	protected Connection connection;
	protected Destination destination;
	private Session session;

	public ConsumerMessage()  {
	
			RabbitFactory rabbitFactory = new RabbitFactory(JarManagerAPIService.xmlFile);

			this.destination = rabbitFactory.CreateRabbitDestination();

			ConnectionFactory connectionFactory = rabbitFactory.CreateRabbitConnectionFactory();

			try {
				this.connection = connectionFactory.createConnection();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				connection.start();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public List<String> getMessage() throws JMSException {
		String text;
		List<String> textList = new ArrayList<String>();
		MessageConsumer messageConsumer = session.createConsumer(destination);

		while (true) {
			Message message = messageConsumer.receive(1);
			if (message != null) {
				text = Util.convertMsg(message);
				textList.add(text);

				logger.debug("receive:" + text);

			} else {
				break;
			}
		}

		return textList;
	}

	/**
	 * 取得Jms HearBeat裡面的訊息 並轉換成HeartBeatClientVO
	 * 
	 * ps: Queue Message 裡面的data並不刪除
	 ***/
	public List<HeartBeatClientVO> getHeartBeatClientVOList() throws JMSException {
		Gson gson = new Gson();

		QueueBrowser queueBrowser = session.createBrowser((Queue) destination);

		Enumeration enumeration = queueBrowser.getEnumeration();

		List<HeartBeatClientVO> heartBeatClientVOList = new ArrayList<HeartBeatClientVO>();

		while (enumeration.hasMoreElements()) {
			Message message = (Message) enumeration.nextElement();

			String heartBeatClientVOJson = Util.convertMsg(message);

			HeartBeatClientVO heartBeatClientVO = gson.fromJson(heartBeatClientVOJson, HeartBeatClientVO.class);

			heartBeatClientVOList.add(heartBeatClientVO);

		}

		return heartBeatClientVOList;
	}

	/**
	 * 取得Jms HearBeat裡面的訊息去除重複
	 * 
	 * 並轉換成HeartBeatClientVO
	 * 
	 * ps: Queue Message 裡面的data並不刪除
	 ***/
	public List<HeartBeatClientVO> getSoleHeartBeatClientVOList() throws JMSException {
		Gson gson = new Gson();

		QueueBrowser queueBrowser = session.createBrowser((Queue) destination);

		Enumeration enumeration = queueBrowser.getEnumeration();

		List<HeartBeatClientVO> heartBeatClientVOList = new ArrayList<HeartBeatClientVO>();

		HashSet<String> heartBeatClientVOhashSet = new HashSet<String>();

		while (enumeration.hasMoreElements()) {
			Message message = (Message) enumeration.nextElement();

			String heartBeatClientVOJson = Util.convertMsg(message);

			if (heartBeatClientVOhashSet.add(heartBeatClientVOJson)) {

				HeartBeatClientVO heartBeatClientVO = gson.fromJson(heartBeatClientVOJson, HeartBeatClientVO.class);

				heartBeatClientVOList.add(heartBeatClientVO);
			}

		}

		return heartBeatClientVOList;
	}

	/****
	 * 1.Get Queue HeartBeatClientVOJSONList 2.HeartBeatClientVOJSONList to
	 * HeartBeatClientVOList 3.Return HeartBeatClientVOJSONList
	 * 
	 * @throws JMSException
	 **/
	public List<HeartBeatClientVO> getHeartBeatClientVOListFromHeart() throws JMSException {
		List<String> textList = getMessage();
		List<HeartBeatClientVO> heartBeatClientVOList = new ArrayList<HeartBeatClientVO>();

		for (String str : textList) {
			HeartBeatClientVO heartBeatClientVO = getHeartBeatClientVO(str);
			heartBeatClientVOList.add(heartBeatClientVO);
		}

		return heartBeatClientVOList;
	}

	/******
	 * HeartBeatClientVOJSON TO HeartBeatClientVO
	 *****/
	public static HeartBeatClientVO getHeartBeatClientVO(String json) {
		Gson gson = new Gson();

		HeartBeatClientVO heartBeatClientVO = gson.fromJson(json, HeartBeatClientVO.class);

		return heartBeatClientVO;
	}

}
