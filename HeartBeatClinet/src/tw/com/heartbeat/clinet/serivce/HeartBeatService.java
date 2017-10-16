package tw.com.heartbeat.clinet.serivce;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import tw.com.heartbeat.clinet.vo.HeartBeatClientVO;
import tw.com.heartbeat.consumer.ConsumerMessage;
import tw.com.heartbeat.producer.ProducerMessage;
import tw.com.jms.util.XMLParser;

public class HeartBeatService {
	private static final Logger logger = LogManager.getLogger(HeartBeatService.class);
	private ProducerMessage producerMessage;
	private ConsumerMessage consumerMessage;
	private HeartBeatClientVO heartBeatClientVO = null;
	Gson gson = null;

	public HeartBeatService() {
		this.gson = new Gson();
		this.producerMessage = creartProducerMessage();
		this.consumerMessage = creartConsumerMessage();
	}

	public HeartBeatService(HeartBeatClientVO heartBeatClientVO) {
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

				String beatID = XMLParser.getXMLText("heartBeatClientVO", "beatID",
						"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
				String fileName = XMLParser.getXMLText("heartBeatClientVO", "fileName",
						"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");
				String timeSeriesStr = XMLParser.getXMLText("heartBeatClientVO", "timeSeries",
						"D:\\XMLFilePath\\HeatBeatClinetBeans.xml");

				Long timeSeries = null;

				if (timeSeriesStr == null) {
					timeSeries = null;
				} else {
					timeSeries = Long.parseLong(timeSeriesStr);
				}

				heartBeatClientVO.setBeatID(beatID);
				heartBeatClientVO.setFileName(fileName);
				heartBeatClientVO.setTimeSeries(timeSeries);

				try {
					ProducerMessage producerMessage = creartProducerMessage();

					ConsumerMessage consumerMessage = creartConsumerMessage();

					Gson gson = new Gson();

					String beatString = gson.toJson(heartBeatClientVO);

					while (true) {
						beatID = heartBeatClientVO.getBeatID();

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
