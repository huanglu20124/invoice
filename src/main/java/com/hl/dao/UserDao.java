package com.hl.dao;

import java.util.Collection;
import java.util.List;

import com.hl.domain.Permission;
import com.hl.domain.User;

public interface UserDao {

	User getUserByNamePwd(String user_name, String user_password);

	String getNameById(Integer user_id);

	String getUserCompanyNmae(Integer user_id);

	User getUserById(Integer user_id);

	User getUserByName(String user_name);

	List<Permission> getUserPermission(Integer user_id);

	List<Permission>  getGroupPermission(Integer group_id);

	List<User> getManagerUsers(Integer user_id);

	boolean getIsPermission(Integer user_id, Integer permission_id);

	void deleteUserPermission(Integer user_id, Integer permission_id);

	void addUserPermission(Integer user_id, Integer permission_id);

}
