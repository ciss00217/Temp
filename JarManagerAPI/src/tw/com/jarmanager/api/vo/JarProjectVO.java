package tw.com.jarmanager.api.vo;

import java.util.ArrayList;
import java.util.List;

import tw.com.jarmanager.api.service.JarConsole;
import tw.com.jarmanager.api.service.JarManagerAPIService;





/************************************************************
 * fileName			: jar的檔案名稱
 * 
 * beatID			: jar所代表的ID
 * 
 * pid				: jar process的PID
 * 
 * jarConsole		: jar要查看的Console
 * 
 * isAlive			: jar是否存活
 * 
 * filePathXMLList  : jar的XML設定檔
 * 
 * timeSeries		: jar送過來的發送間隔
 * 
 * jarFilePath		: jar的檔案位置
 * 
 * description		: jar的說明
 * 
 * notFindCount 	: jar的未執行次數
 * 
 * getCommandLinearr: jar的CommandLine 
 * 
*************************************************************/
public class JarProjectVO {
	private String fileName;
	private String beatID;
	private JarConsole jarConsole;
	private List<String> filePathXMLList;
	private long timeSeries;
	private String jarFilePath;
	private String description;
	private int notFindCount;
	private Long pid;
	
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	public int getNotFindCount() {
		return notFindCount;
	}
	public void setNotFindCount(int notFindCount) {
		this.notFindCount = notFindCount;
	}
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

	public JarConsole getJarConsole() {
		return jarConsole;
	}
	public void setJarConsole(JarConsole jarConsole) {
		this.jarConsole = jarConsole;
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
