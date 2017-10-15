package com.hl.domain;

public class LocalConfig {
	private String ip;
	private String dataBasePath;
	private String imagePath;//图片存储库的根目录
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDataBasePath() {
		return dataBasePath;
	}
	public void setDataBasePath(String dataBasePath) {
		this.dataBasePath = dataBasePath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	
}
