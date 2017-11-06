package com.hl.dao;

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

}
