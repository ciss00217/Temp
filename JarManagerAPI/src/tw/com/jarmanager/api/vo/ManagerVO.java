package tw.com.jarmanager.api.vo;

import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/****************
 * loadingTime	   : 等待各個jar發送BeatID的時間
 * 
 * reCheckTime	   : 重新判別各個jar是否存活的時間
 * 
 * ****************/

@XmlRootElement(name="managerVO")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "loadingTime", "reCheckTime" })
public class ManagerVO {
	private long loadingTime;
	private long reCheckTime;

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
