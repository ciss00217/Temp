package tw.com.jms.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLParser {
	public static void main(String[] args) {
		getListXMLText("jarProjectVO", "Q2W", "filePathXMLList", "D:\\XMLFilePath\\JarManagerAPI.xml");
	}

	public static String getXMLText(String className, String property, String XMLFilePath) {
		try {
			File inputFile = new File(XMLFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

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

	public static String getXMLText(String className, String property, String XMLFilePath, String id) {
		try {
			File inputFile = new File(XMLFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName(className);

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String idTag = eElement.getAttribute("id");

					if (idTag.equals(id)) {

						return eElement.getElementsByTagName(property).item(0).getTextContent();
					}
				}

			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<String> getListXMLText(String className, String id, String property, String XMLFilePath) {
		try {
			File inputFile = new File(XMLFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName(className);

			List<String> list = new ArrayList<String>();

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					String idTag = eElement.getAttribute("id");
					if (idTag.equals(id)) {

						Node childNods = eElement.getElementsByTagName(property).item(0);

						NodeList childNodsList = childNods.getChildNodes();

						for (int i = 0; i < childNodsList.getLength(); i++) {
							String str = StringFilter(childNodsList.item(i).getTextContent());
							if (!"".equals(str)) {
								list.add(str);
							}
						}

					}
				}
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}

	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允許字母和數字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】『；：」「』。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

}