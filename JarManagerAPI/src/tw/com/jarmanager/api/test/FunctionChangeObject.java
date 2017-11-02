package tw.com.jarmanager.api.test;

import java.io.IOException;

import tw.com.jarmanager.api.vo.JarProjectVO;

public class FunctionChangeObject {
	public static void main(String[] args)  {
		FactoryTest factoryTest = new FactoryTest();
		JarProjectVO jarProjectVO = new JarProjectVO();
		jarProjectVO.setBeatID("beatID");
		
		factoryTest.changeObject(jarProjectVO);
		
		System.out.println("hi");
		
		System.out.println(jarProjectVO.getBeatID());
		System.out.println(jarProjectVO.getFileName());
	}
}
