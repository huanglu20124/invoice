package com.hl.domain;

public class Action {
	//基于日志的操作行为类，对应数据库表,user_name和company_name要连接查询一波
	private Integer action_id;
	private Integer  user_id;
	private String user_name;
	private Integer company_id;
	private String company_name;
	private Integer msg_id;
	private String action_start_time;
	private String action_run_time;
	private String action_end_time;
	//作为索引数据库的主键
	private String action_uuid;
	
	public String getAction_uuid() {
		return action_uuid;
	}
	public void setAction_uuid(String action_uuid) {
		this.action_uuid = action_uuid;
	}
	public Integer getAction_id() {
		return action_id;
	}
	public void setAction_id(Integer action_id) {
		this.action_id = action_id;
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
	public Integer getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(Integer msg_id) {
		this.msg_id = msg_id;
	}
	public String getAction_start_time() {
		return action_start_time;
	}
	public void setAction_start_time(String action_start_time) {
		this.action_start_time = action_start_time;
	}
	public String getAction_run_time() {
		return action_run_time;
	}
	public void setAction_run_time(String action_run_time) {
		this.action_run_time = action_run_time;
	}
	public String getAction_end_time() {
		return action_end_time;
	}
	public void setAction_end_time(String action_end_time) {
		this.action_end_time = action_end_time;
	}
	
	
}
