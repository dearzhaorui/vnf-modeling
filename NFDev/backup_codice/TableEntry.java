package it.polito.nfdev.nat;

import java.util.Date;

public class TableEntry {
	
	private String internalIp;
	private String internalPort;
	private String externalIp;
	private String externalPort;
	private Date timestamp;
	
	public String getInternalIp() {
		return internalIp;
	}
	public void setInternalIp(String internalIp) {
		this.internalIp = internalIp;
	}
	public String getInternalPort() {
		return internalPort;
	}
	public void setInternalPort(String internalPort) {
		this.internalPort = internalPort;
	}
	public String getExternalIp() {
		return externalIp;
	}
	public void setExternalIp(String externalIp) {
		this.externalIp = externalIp;
	}
	public String getExternalPort() {
		return externalPort;
	}
	public void setExternalPort(String externalPort) {
		this.externalPort = externalPort;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
