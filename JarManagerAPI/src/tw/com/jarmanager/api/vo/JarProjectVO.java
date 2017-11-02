package tw.com.jarmanager.api.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import tw.com.jarmanager.api.service.JarConsole;

/************************************************************
 * fileName : jar的檔案名稱
 * 
 * beatID : jar所代表的ID
 * 
 * pid : jar process的PID
 * 
 * jarConsole : jar要查看的Console
 * 
 * filePathXMLList : jar的XML設定檔
 * 
 * timeSeries : jar送過來的發送間隔
 * 
 * jarFilePath : jar的檔案位置
 * 
 * description : jar的說明
 * 
 * needRun		:jar是否執行
 * 
 * firstScuessRun : 是否有成功執行過
 * 
 * notFindCount : jar的未執行次數
 * 
 * getCommandLinearr: jar的CommandLine
 * 
 *************************************************************/

@XmlRootElement(name = "jarProjectVO")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "fileName", "beatID", "jarFilePath", "description","needRun","timeSeries", "filePathXMLList" ,"firstSuccessRun"})
public class JarProjectVO {
	@XmlAttribute
	private String id;
	@XmlElement
	private String fileName;
	@XmlElement
	private String beatID;
	@XmlElement
	private String jarFilePath;
	@XmlElement
	private String description;
	@XmlElement
	private boolean needRun;
	@XmlElement
	private long timeSeries;
	@XmlElementWrapper(name = "filePathXMLList")
	@XmlElement(name = "item")
	private List<String> filePathXMLList;
	@XmlElement
	private boolean firstSuccessRun;
	
	public boolean getFirstSuccessRun() {
		return firstSuccessRun;
	}
	
	public boolean isFirstSuccessRun() {
		return firstSuccessRun;
	}

	public void setFirstSuccessRun(boolean firstSuccessRun) {
		this.firstSuccessRun = firstSuccessRun;
	}

	public boolean isNeedRun() {
		return needRun;
	}
	
	public boolean getNeedRun() {
		return needRun;
	}

	public void setNeedRun(boolean needRun) {
		this.needRun = needRun;
	}

	private JarConsole jarConsole;

	private int notFindCount;
	private LocalDateTime localDateTime;
	private Long pid;

	public LocalDateTime getLocalDateTime() {
		return localDateTime;
	}

	public void setLocalDateTime(LocalDateTime localDateTime) {
		this.localDateTime = localDateTime;
	}

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
		this.id = beatID;
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
