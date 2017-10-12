package tw.com.jarmanager.api.vo;

import java.util.HashMap;
import java.util.List;


/****************
 * jarProjectVOList: 放至初始的管理設定檔
 * 
 * jarProjectVOMap : 放置正在執行的各個Jar
 * 
 * loadingTime	   : 等待各個jar發送BeatID的時間
 * 
 * reCheckTime	   : 重新判別各個jar是否存活的時間
 * 
 * ****************/
public class ManagerVO {
	private List<JarProjectVO> jarProjectVOList;
	private HashMap<String, JarProjectVO> jarProjectVOMap;
	private long loadingTime;
	private long reCheckTime;
	
	public List<JarProjectVO> getJarProjectVOList() {
		return jarProjectVOList;
	}
	public void setJarProjectVOList(List<JarProjectVO> jarProjectVOList) {
		this.jarProjectVOList = jarProjectVOList;
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
