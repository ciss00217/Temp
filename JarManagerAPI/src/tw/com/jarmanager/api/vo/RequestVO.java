package tw.com.jarmanager.api.vo;

import java.util.List;

public class RequestVO {
	private String action;
	private List<String> ids;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	
}
