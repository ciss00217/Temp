package tw.com.jarmanager.api.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tw.com.jarmanager.api.service.JarManagerAPIService;
import tw.com.jarmanager.api.vo.JarProjectVO;

public class MapReMove {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Map<String,String> map= new HashMap<String,String>();
		map.put("1","1");
		map.put("2","1");
		map.put("3","1");
		map.put("4","1");
		
		Set set = new HashSet();
		set = map.keySet();
		Iterator it = set.iterator();
		String s;
		while (it.hasNext()) {
			s = it.next().toString();
			System.out.print(s);
			//map.remove("2");
		
		}
		
		
		
		
		JarManagerAPIService jarManagerAPIService = new JarManagerAPIService();
		jarManagerAPIService.xmlFile= "D:\\JarManagerAPI.xml";
		

			
		HashMap<String, JarProjectVO> oldJarProjectVOMap = new HashMap<String, JarProjectVO>();
		
		JarProjectVO JarProjectVO1 = new JarProjectVO();
		JarProjectVO1.setBeatID("aaaa");
		
		JarProjectVO JarProjectVO2 = new JarProjectVO();
		JarProjectVO2.setBeatID("aaaa1");
		
		JarProjectVO JarProjectVO3 = new JarProjectVO();
		JarProjectVO3.setBeatID("SFStatus");
		
		JarProjectVO JarProjectVO4 = new JarProjectVO();
		JarProjectVO4.setBeatID("SFBalance");
		
		String key1=JarProjectVO1.getBeatID();
		String key2=JarProjectVO2.getBeatID();
		String key3=JarProjectVO3.getBeatID();
		String key4=JarProjectVO4.getBeatID();
		
		oldJarProjectVOMap.put(key1, JarProjectVO1);
		oldJarProjectVOMap.put(key2, JarProjectVO2);
		oldJarProjectVOMap.put(key3, JarProjectVO3);
		oldJarProjectVOMap.put(key4, JarProjectVO4);
		
		
		oldJarProjectVOMap=jarManagerAPIService.mapRemoveNoExitByXml(oldJarProjectVOMap);
		
		System.out.println("test");

	}

}
