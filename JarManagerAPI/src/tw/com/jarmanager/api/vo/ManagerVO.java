package tw.com.jarmanager.api.vo;

import java.util.HashMap;
import java.util.List;

public class ManagerVO {
	private List<JarProjectVO> jarProjectVOList;
	private String folderPath;
	private HashMap<String, JarProjectVO> jarProjectVOMap;
	private long loadingTime;
	private long reCheckTime;
	
	public List<JarProjectVO> getJarProjectVOList() {
		return jarProjectVOList;
	}
	public void setJarProjectVOList(List<JarProjectVO> jarProjectVOList) {
		this.jarProjectVOList = jarProjectVOList;
	}
	public String getFolderPath() {
		return folderPath;
	}
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	public HashMap<String, JarProjectVO> getJarProjectVOMap() {
		return jarProjectVOMap;
	}
	public void setJarProjectVOMap(HashMap<String, JarProjectVO> jarProjectVOMap) {
		this.jarProjectVOMap = jarProjectVOMap;
	}
	public long getLoadingTime() {
		return loadingTime;
	}
	public void setLoadingTime(long loadingTime) {
		this.loadingTime = loadingTime;
	}
	public long getReCheckTime() {
		return reCheckTime;
	}
	public void setReCheckTime(long reCheckTime) {
		this.reCheckTime = reCheckTime;
	}
}
