package com.hl.domain;

public class Company {
	private Integer company_id;
	private String company_name;
	private String company_register_time;
	private String company_description;
	private Integer company_user_num;
	private String company_logo;
	private Integer user_id;//负责人id
	private String user_name;//负责人名字
	
	
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
	public String getCompany_register_time() {
		return company_register_time;
	}
	public void setCompany_register_time(String company_register_time) {
		this.company_register_time = company_register_time;
	}
	public String getCompany_description() {
		return company_description;
	}
	public void setCompany_description(String company_description) {
		this.company_description = company_description;
	}
	public Integer getCompany_user_num() {
		return company_user_num;
	}
	public void setCompany_user_num(Integer company_user_num) {
		this.company_user_num = company_user_num;
	}
	public String getCompany_logo() {
		return company_logo;
	}
	public void setCompany_logo(String company_logo) {
		this.company_logo = company_logo;
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
	
	
}
