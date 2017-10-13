package tw.com.heartbeat.clinet.serivce;



import javax.jms.JMSException;

import org.apache.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.google.gson.Gson;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.heartbeat.consumer.ConsumerMessage;
import tw.com.heartbeat.producer.ProducerMessage;

public class HeartBeatService {
	private static final org.apache.log4j.Logger logger = LogManager.getLogger(HeartBeatService.class);
	private ProducerMessage producerMessage;
	private ConsumerMessage consumerMessage;
	private ApplicationContext context = null;
	private HeartBeatClientVO heartBeatClientVO = null;
	Gson gson = null;

	public HeartBeatService() {
		this.gson = new Gson();
		this.context = new ClassPathXmlApplicationContext("HeatBeatClinetBeans.xml");
		this.producerMessage = creartProducerMessage();
		this.consumerMessage = creartConsumerMessage();
	}

	public HeartBeatService(HeartBeatClientVO heartBeatClientVO) {
		this.context = new ClassPathXmlApplicationContext("HeatBeatClinetBeans.xml");
		this.heartBeatClientVO = heartBeatClientVO;
		this.producerMessage = creartProducerMessage();
		this.consumerMessage = creartConsumerMessage();
		this.gson = new Gson();
	}

	public void beat() {

		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				String beatID = heartBeatClientVO.getBeatID();
				String beatString = gson.toJson(heartBeatClientVO);
				try {
					if (consumerMessage.checkMessage(beatID)) {
						logger.debug("send: beatID:"+beatID);
						
						producerMessage.send(beatString);
					}else{
						logger.debug("beatID: "+beatID+" exist");
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

				ApplicationContext context = new ClassPathXmlApplicationContext("HeatBeatClinetBeans.xml");
				HeartBeatClientVO heartBeatClientVO = (HeartBeatClientVO) context.getBean("heartBeatClientVO");

				try {
					ProducerMessage producerMessage = creartProducerMessage();

					ConsumerMessage consumerMessage = creartConsumerMessage();

					Gson gson = new Gson();

					String beatString = gson.toJson(heartBeatClientVO);

					while (true) {
						String beatID = heartBeatClientVO.getBeatID();

						if (consumerMessage.checkMessage(beatID)) {

							producerMessage.send(beatString);
						}
						logger.debug("sleep: " + heartBeatClientVO.getTimeSeries());
						Thread.sleep(heartBeatClientVO.getTimeSeries());
					}

				} catch (Exception e) {
					logger.debug("Error: " + e.getMessage());
				}

			}
		});
		th.start();

	}

	public static ConsumerMessage creartConsumerMessage() {
		ConsumerMessage consumerMessage = null;
		try {

			consumerMessage = new ConsumerMessage("heartBeatDestination");

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

	public static ProducerMessage creartProducerMessage() {
		ProducerMessage producerMessage = null;
		try {

			producerMessage = new ProducerMessage("heartBeatDestination");

		} catch (JMSException e) {
			logger.debug("Error: " + e.getMessage());
		}

		return producerMessage;
	}

	public static void main(String[] args) {

		startBeat();
	}

}
