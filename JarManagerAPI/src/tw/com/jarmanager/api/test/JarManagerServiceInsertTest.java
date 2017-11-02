package tw.com.jarmanager.api.test;

import tw.com.jarmanager.api.service.JarManagerAPIService;
import tw.com.jarmanager.api.vo.JarProjectVO;

public class JarManagerServiceInsertTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		JarProjectVO jarProjectVO=new JarProjectVO();
		
		jarProjectVO.setBeatID("beatID");
		jarProjectVO.setDescription("OOOOO");
		jarProjectVO.setFileName("fileName");
		jarProjectVO.setTimeSeries(30000);
		jarProjectVO.setJarFilePath("asd");
		
		JarManagerAPIService.addJarProjectVOXml(jarProjectVO);
	}

}
