package tw.com.heartbeat.clinet.vo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="heartBeatConnectionFactory")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = { "username", "password", "virtualHost", "host","port" })
public class HeartBeatConnectionFactoryVO {
	private static final long serialVersionUID = 1L;
	@XmlElement
	private String username;
	@XmlElement
	private String password;
	@XmlElement
	private String virtualHost;
	@XmlElement
	private String host;
	@XmlElement
	private int port;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getVirtualHost() {
		return virtualHost;
	}
	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

}
