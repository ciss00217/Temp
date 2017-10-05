package tw.com.jarmanager.api.vo;

import java.util.ArrayList;
import java.util.List;

import tw.com.jarmanager.api.service.JarConsole;
import tw.com.jarmanager.api.service.JarManagerAPIService;

public class JarProjectVO {
	private String fileName;
	private String beatID;
	private Process process;
	private JarConsole jarConsole;
	private Boolean isAlive;
	private List<String> filePathXMLList;
	private long timeSeries;
	private String jarFilePath;
	private String description;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getBeatID() {
		return beatID;
	}
	public void setBeatID(String beatID) {
		this.beatID = beatID;
	}
	public Process getProcess() {
		return process;
	}
	public void setProcess(Process process) {
		this.process = process;
	}
	public JarConsole getJarConsole() {
		return jarConsole;
	}
	public void setJarConsole(JarConsole jarConsole) {
		this.jarConsole = jarConsole;
	}
	public Boolean getIsAlive() {
		return isAlive;
	}
	public void setIsAlive(Boolean isAlive) {
		this.isAlive = isAlive;
	}
	public List<String> getFilePathXMLList() {
		return filePathXMLList;
	}
	public void setFilePathXMLList(List<String> filePathXMLList) {
		this.filePathXMLList = filePathXMLList;
	}
	public long getTimeSeries() {
		return timeSeries;
	}
	public void setTimeSeries(long timeSeries) {
		this.timeSeries = timeSeries;
	}
	public String getJarFilePath() {
		return jarFilePath;
	}
	public void setJarFilePath(String jarFilePath) {
		this.jarFilePath = jarFilePath;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String[] getCommandLinearr() {

		List<String> commandLinearrList = new ArrayList<String>();
		
		commandLinearrList.add("java");

		commandLinearrList.add("-jar");

		commandLinearrList.add(jarFilePath);

		commandLinearrList.addAll(filePathXMLList);
		
		String[] stockArr = new String[commandLinearrList.size()];
		stockArr = commandLinearrList.toArray(stockArr);

		return stockArr;
	}


}
