package com.hl.dao;

import com.hl.domain.User;

public interface UserDao {

	User getUserByNamePwd(String user_name, String user_password);

	String getNameById(Integer user_id);

	String getUserCompanyNmae(Integer user_id);

	User getUserById(Integer user_id);

}
