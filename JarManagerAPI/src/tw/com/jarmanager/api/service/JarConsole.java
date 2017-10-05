package tw.com.jarmanager.api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JarConsole extends Thread {
	Process process;
	String jarName;
	JarConsole(Process process,String jarName) {
		this.process = process;
		this.jarName = jarName;
	}


	@Override
	public void run() {

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		String line;
		try {
			while ((line = br.readLine()) != null) {
				System.out.println("["+String.format("%-25s",jarName+"]")+": " + line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Program terminated!");

	}

}
