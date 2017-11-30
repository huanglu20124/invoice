package com.hl.domain;

import java.util.List;

public class RecognizeConsole {
	private Integer user_id;
	private String user_name;
	private Integer company_id;
	private String company_name;
	private String img_str;
	private List<String>region_list;
	private String action_time;
	private Integer msg_id;
	
	//发票识别速度
	private Integer delay;
	
	public Integer getDelay() {
		return delay;
	}
	public void setDelay(Integer delay) {
		this.delay = delay;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public Integer getCompany_id() {
		return company_id;
	}
	public void setCompany_id(Integer company_id) {
		this.company_id = company_id;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getImg_str() {
		return img_str;
	}
	public void setImg_str(String img_str) {
		this.img_str = img_str;
	}
	public List<String> getRegion_list() {
		return region_list;
	}
	public void setRegion_list(List<String> region_list) {
		this.region_list = region_list;
	}
	public Integer getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(Integer msg_id) {
		this.msg_id = msg_id;
	}
	public String getAction_time() {
		return action_time;
	}
	public void setAction_time(String action_time) {
		this.action_time = action_time;
	}
	
}
