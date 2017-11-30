package com.hl.domain;

import java.util.List;

public class Group {
	private Integer group_id;
	private String group_name;
	private Integer company_id;
	private String company_name;
	private String group_register_time;
	private List<Permission>permissions;
	
	public List<Permission> getPermissions() {
		return permissions;
	}
	public void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
	public Integer getGroup_id() {
		return group_id;
	}
	public void setGroup_id(Integer group_id) {
		this.group_id = group_id;
	}
	public String getGroup_name() {
		return group_name;
	}
	public void setGroup_name(String group_name) {
		this.group_name = group_name;
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
	public String getGroup_register_time() {
		return group_register_time;
	}
	public void setGroup_register_time(String group_register_time) {
		this.group_register_time = group_register_time;
	}
	
}
