package tw.com.heartbeat.clinet.serivce;

import java.time.LocalDateTime;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.rabbitmq.jms.admin.RMQConnectionFactory;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.heartbeat.consumer.ConsumerMessage;
import tw.com.heartbeat.factory.RabbitFactory;
import tw.com.heartbeat.producer.ProducerMessage;
import tw.com.jms.util.XMLParser;

public class HeartBeatService {
	private static final Logger logger = LogManager.getLogger(HeartBeatService.class);
	public static String xmlFilePath;
	private ProducerMessage producerMessage;
	private ConsumerMessage consumerMessage;
	private HeartBeatClientVO heartBeatClientVO = null;
	Gson gson = null;

	public HeartBeatService(String xmlFilePath) {
		this.gson = new Gson();
		this.xmlFilePath = xmlFilePath;
		this.producerMessage = creartProducerMessage(xmlFilePath);
		this.consumerMessage = creartConsumerMessage(xmlFilePath);
	}

	public HeartBeatService(HeartBeatClientVO heartBeatClientVO, String xmlFilePath) {
		this.heartBeatClientVO = heartBeatClientVO;
		this.xmlFilePath = xmlFilePath;
		this.producerMessage = creartProducerMessage(xmlFilePath);
		this.consumerMessage = creartConsumerMessage(xmlFilePath);

		this.gson = new Gson();
	}

	public void beat() {

		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				String beatID = heartBeatClientVO.getBeatID();

				heartBeatClientVO.setLocalDateTime(LocalDateTime.now());

				String beatString = gson.toJson(heartBeatClientVO);
				try {
					if (consumerMessage.checkMessage(beatID)) {
						logger.debug("send: beatID:" + beatID);

						producerMessage.send(beatString);
					} else {
						logger.debug("beatID: " + beatID + " exist");
					}
				} catch (Exception e) {
					logger.debug("Error: " + e.getMessage());
				}
			}
		});
		th.start();
	}

	public static void startBeat() {

		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {

				HeartBeatClientVO heartBeatClientVO = new HeartBeatClientVO();

				XMLParser XMLParser = new XMLParser();

				String beatID = XMLParser.getXMLText("heartBeatClientVO", "beatID", xmlFilePath);
				String fileName = XMLParser.getXMLText("heartBeatClientVO", "fileName", xmlFilePath);
				String timeSeriesStr = XMLParser.getXMLText("heartBeatClientVO", "timeSeries",
						"src\\HeatBeatClinetBeans.xml");

				Long timeSeries = null;

				if (timeSeriesStr == null) {
					timeSeries = null;
				} else {
					timeSeries = Long.parseLong(timeSeriesStr);
				}

				heartBeatClientVO.setBeatID(beatID);
				heartBeatClientVO.setFileName(fileName);
				heartBeatClientVO.setTimeSeries(timeSeries);
				heartBeatClientVO.setLocalDateTime(LocalDateTime.now());

			
					ProducerMessage producerMessage = creartProducerMessage(xmlFilePath);

					ConsumerMessage consumerMessage = creartConsumerMessage(xmlFilePath);

					Gson gson = new Gson();

					String beatString = gson.toJson(heartBeatClientVO);

					while (true) {
						beatID = heartBeatClientVO.getBeatID();

						try {
							if (consumerMessage.checkMessage(beatID)) {

								producerMessage.send(beatString);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						logger.debug("sleep: " + heartBeatClientVO.getTimeSeries());
						try {
							Thread.sleep(heartBeatClientVO.getTimeSeries());
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				

			}
		});
		th.start();

	}

	public static ConsumerMessage creartConsumerMessage(String xmlFilePath) {
		ConsumerMessage consumerMessage = null;
		try {

			consumerMessage = new ConsumerMessage(xmlFilePath);

		} catch (JMSException e) {
			logger.debug("Error: " + e.getMessage());
		}

		return consumerMessage;
	}

	public HeartBeatClientVO getHeartBeatClientVO() {
		return heartBeatClientVO;
	}

	public void setHeartBeatClientVO(HeartBeatClientVO heartBeatClientVO) {
		this.heartBeatClientVO = heartBeatClientVO;
	}

	public static ProducerMessage creartProducerMessage(String xmlFilePath) {
		ProducerMessage producerMessage = null;
		try {

			producerMessage = new ProducerMessage(xmlFilePath);

		} catch (JMSException e) {
			logger.debug("Error: " + e.getMessage());
		}

		return producerMessage;
	}

	public static void main(String[] args) throws InterruptedException {
		HeartBeatService HeartBeatService = new HeartBeatService("D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
		HeartBeatService.startBeat();
		
//		RabbitFactory RabbitFactory = new RabbitFactory("D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
//		RMQConnectionFactory asdasda=RabbitFactory.CreateRabbitConnectionFactory(); 
//		
//		asdasda.set
	}

}
