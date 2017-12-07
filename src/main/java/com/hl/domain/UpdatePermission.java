package com.hl.domain;

public class UpdatePermission extends Permission{
	private Integer is_checked;//0为勾，1为没勾
	
	public Integer getIs_checked() {
		return is_checked;
	}
	public void setIs_checked(Integer is_checked) {
		this.is_checked = is_checked;
	}
	
}
