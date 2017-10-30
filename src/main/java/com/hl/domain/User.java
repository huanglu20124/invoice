package com.hl.domain;

public class User {
	private Integer user_id;
	private String user_name;
	private String user_password;
	private String company_name;
	private Integer company_id;
	private String user_register_time;
	private Integer user_type;
	
	//权限管理集合
	private Integer user_auth;
	private Integer model_auth;
	private Integer invoice_auth;
	private Integer action_auth;
	
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public Integer getCompany_id() {
		return company_id;
	}
	public void setCompany_id(Integer company_id) {
		this.company_id = company_id;
	}
	public String getUser_register_time() {
		return user_register_time;
	}
	public void setUser_register_time(String user_register_time) {
		this.user_register_time = user_register_time;
	}
	public Integer getUser_auth() {
		return user_auth;
	}
	public void setUser_auth(Integer user_auth) {
		this.user_auth = user_auth;
	}
	public Integer getUser_type() {
		return user_type;
	}
	public void setUser_type(Integer user_type) {
		this.user_type = user_type;
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
	public String getUser_password() {
		return user_password;
	}
	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}
	public Integer getModel_auth() {
		return model_auth;
	}
	public void setModel_auth(Integer model_auth) {
		this.model_auth = model_auth;
	}
	public Integer getInvoice_auth() {
		return invoice_auth;
	}
	public void setInvoice_auth(Integer invoice_auth) {
		this.invoice_auth = invoice_auth;
	}
	public Integer getAction_auth() {
		return action_auth;
	}
	public void setAction_auth(Integer action_auth) {
		this.action_auth = action_auth;
	}
	
	
}
