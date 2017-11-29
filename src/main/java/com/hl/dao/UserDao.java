package com.hl.dao;

import java.util.Collection;
import java.util.List;

import com.hl.domain.Group;
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

	boolean getIsUserPermission(Integer user_id, Integer permission_id);

	void deleteUserPermission(Integer user_id, Integer permission_id);

	void addUserPermission(Integer user_id, Integer permission_id);

	List<Group> getManagerGroups(Integer user_id);

	boolean getIsGroupPermission(Integer permission_id, Integer group_id);

	void deleteGroupPermission(Integer group_id, Integer permission_id);

	void addGroupPermission(Integer group_id, Integer permission_id);

	void addGroupUser(Integer user_id, Integer group_id);

	void removeGroupUser(Integer user_id);

	List<User> getGroupUser(Integer group_id, Integer comapny_id);


}
