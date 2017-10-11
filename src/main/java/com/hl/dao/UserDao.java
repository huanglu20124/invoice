package com.hl.dao;

import com.hl.domain.User;

public interface UserDao {

	User getUserByNamePwd(String user_name, String user_password);

}
