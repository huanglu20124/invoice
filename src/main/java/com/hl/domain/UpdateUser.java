package com.hl.domain;

import java.util.List;

public class UpdateUser extends User{
	//被修改过的用户类
	private List<UpdatePermission>update_permissions;

	public List<UpdatePermission> getUpdate_permissions() {
		return update_permissions;
	}

	public void setUpdate_permissions(List<UpdatePermission> update_permissions) {
		this.update_permissions = update_permissions;
	}
	
}
