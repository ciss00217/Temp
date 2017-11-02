package tw.com.jarmanager.api.test;

import java.util.ArrayList;
import java.util.List;

import tw.com.jarmanager.api.factory.RabbitFactory;
import tw.com.jarmanager.api.service.JarManagerAPIService;
import tw.com.jarmanager.api.vo.JarManagerAPIXMLVO;
import tw.com.jarmanager.api.vo.JarProjectVO;

public class FactoryTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		RabbitFactory RabbitFactory=new RabbitFactory("d:\\123123.xml");
//		List<JarProjectVO> LIST=RabbitFactory.CreateProjectVOList();
//		
//		
//		System.out.print("12312");
		
		JarManagerAPIService jarManagerAPIService=new JarManagerAPIService();
	
		jarManagerAPIService.setXmlFilePath("D:\\JarManagerAPI.xml");
		
		JarProjectVO jarProjectVO1 = new JarProjectVO();
		jarProjectVO1.setBeatID("SFStatus");
		jarProjectVO1.setFileName("QWEQWE");
		jarProjectVO1.setTimeSeries(30000);
		jarProjectVO1.setDescription("WQEQEWQ說明");
		jarProjectVO1.setJarFilePath("ADADAD");

		ArrayList<String> arrayList1 = new ArrayList<String>();
		arrayList1.add("ADADAD");
		arrayList1.add("ADADAD");
		jarProjectVO1.setFilePathXMLList(arrayList1);
		
		
		
		boolean isok=jarManagerAPIService.updateJarProjectVOXml(jarProjectVO1);
		
		System.out.println(isok);
	}
	
	void changeObject(JarProjectVO AAA){
		AAA.setBeatID("ASDASA");
		AAA.setFileName("LOJJ");
	}

}
