package com.hl.service;

import com.hl.domain.User;

public interface UserService {

	User loginByNamePwd(String user_name, String user_password);

}
