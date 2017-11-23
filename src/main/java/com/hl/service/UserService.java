package com.hl.service;

import java.util.List;

import com.hl.domain.SimpleResponse;
import com.hl.domain.UpdateUser;
import com.hl.domain.User;

public interface UserService {

	User loginByNamePwd(String user_name, String user_password);

	List<User> getManagerUsers(Integer user_id);

	SimpleResponse updateUsersPermission(List<UpdateUser>users);

}
