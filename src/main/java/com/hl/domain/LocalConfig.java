package com.hl.domain;

public class LocalConfig {
	private String ip;
	private String dataBasePath;
	private String imagePath;// 图片存储库的根目录
	private String customerHost; // 客户端的主机
	private Integer customerPort; // 客户端端口

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

	public String getCustomerHost() {
		return customerHost;
	}

	public void setCustomerHost(String customerHost) {
		this.customerHost = customerHost;
	}

	public Integer getCustomerPort() {
		return customerPort;
	}

	public void setCustomerPort(Integer customerPort) {
		this.customerPort = customerPort;
	}

}
