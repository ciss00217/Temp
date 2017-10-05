package tw.com.jms.util;

import java.nio.charset.StandardCharsets;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
	private static final Logger logger = LogManager.getLogger(Util.class);

	public static String convertMsg(Message message) throws JMSException {
		String text = "";
		String messageType = message.getJMSType();

		switch (messageType) {
		case "TextMessage":
			TextMessage m = (TextMessage) message;
			text = m.getText();
			return text;
		case "MapMessage":
			logger.debug("錯誤未實現MapMessage作法");
			return text;
		case "BytesMessage":

			BytesMessage byteMessage = (BytesMessage) message;
			byte[] byteArr = new byte[(int) byteMessage.getBodyLength()];

			byteMessage.readBytes(byteArr);

			text = new String(byteArr, StandardCharsets.UTF_8);

			return text;
		case "StreamMessage":
			logger.debug("錯誤未實現StreamMessage作法");
			return text;
		case "ObjectMessage":
			logger.debug("錯誤未實現ObjectMessage作法");
			return text;
		case "Message":
			logger.debug("錯誤未實現Message作法");
			return text;
		}
		return text;
	}

}
