package tw.com.jms.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParser {
	public static void main(String[] args) {
		// System.out.println(getXMLText("heartBeatConnectionFactory",
		// "virtualHost","D:\\XMLFilePath\\HeatBeatClinetBeans.xml"));
	}

	public String getXMLText(String className, String property, String XMLFilePath) {
		try {
			File inputFile = new File(XMLFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			System.out.println("Root element :"+className +property);

			NodeList nList = doc.getElementsByTagName(className);

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					return eElement.getElementsByTagName(property).item(0).getTextContent();
				}

			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}