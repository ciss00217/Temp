package tw.com.jarmanager.api.vo;

import java.util.List;

public class RequestVO {
	private String action;
	private List<String> ids;
	private JarManagerAPIXMLVO jarManagerAPIXMLVO;
	
	public JarManagerAPIXMLVO getJarManagerAPIXMLVO() {
		return jarManagerAPIXMLVO;
	}
	public void setJarManagerAPIXMLVO(JarManagerAPIXMLVO jarManagerAPIXMLVO) {
		this.jarManagerAPIXMLVO = jarManagerAPIXMLVO;
	}
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
