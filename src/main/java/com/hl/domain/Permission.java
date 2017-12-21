package com.hl.domain;

import java.util.List;

public class Permission {
	private Integer permission_id; //权限id
	private String  permission_name;//权限名称
	private Integer isPrivate; //0位公有，1为私有
	private Integer is_checked;//前端送过来的，是否打钩
	private List<String>origin_groups;//如果该权限是公有的，还要记录它来自的用户组名字
	
	public Integer getPermission_id() {
		return permission_id;
	}
	public void setPermission_id(Integer permission_id) {
		this.permission_id = permission_id;
	}
	public String getPermission_name() {
		return permission_name;
	}
	public void setPermission_name(String permission_name) {
		this.permission_name = permission_name;
	}
	@Override
	public boolean equals(Object obj) {
		Permission permission = (Permission) obj;
		if(this.permission_id == permission.permission_id){
			return true;
		}else {
			return false;
		}
	}
	public Integer getIsPrivate() {
		return isPrivate;
	}
	public void setIsPrivate(Integer isPrivate) {
		this.isPrivate = isPrivate;
	}
	public Integer getIs_checked() {
		return is_checked;
	}
	public void setIs_checked(Integer is_checked) {
		this.is_checked = is_checked;
	}
	public List<String> getOrigin_groups() {
		return origin_groups;
	}
	public void setOrigin_groups(List<String> origin_groups) {
		this.origin_groups = origin_groups;
	}

	@Override
	public String toString() {
		return permission_id+"---"+permission_name;
	}
	
	@Override
	public int hashCode() {
		return permission_id.hashCode();
	}

}
