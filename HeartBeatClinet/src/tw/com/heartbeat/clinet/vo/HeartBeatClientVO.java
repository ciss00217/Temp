package tw.com.heartbeat.clinet.vo;

public class HeartBeatClientVO {
	private String fileName;
	private String beatID;
	private long timeSeries=1000*60*10;
	
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
	public long getTimeSeries() {
		return timeSeries;
	}
	public void setTimeSeries(long timeSeries) {
		this.timeSeries = timeSeries;
	}

}
