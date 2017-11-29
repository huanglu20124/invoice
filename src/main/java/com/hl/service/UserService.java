package com.hl.service;

import java.util.List;

import com.hl.domain.Group;
import com.hl.domain.Permission;
import com.hl.domain.SimpleResponse;
import com.hl.domain.UpdatePermission;
import com.hl.domain.UpdateUser;
import com.hl.domain.User;

public interface UserService {

	User loginByNamePwd(String user_name, String user_password);

	List<User> getManagerUsers(Integer user_id);

	SimpleResponse updateUsersPermission(List<UpdateUser>users);

	List<Group> getManagerGroups(Integer user_id);

	SimpleResponse updateGroupPermission(List<UpdatePermission> list, Integer group_id);

	SimpleResponse addGroupUser(Integer user_id, Integer group_id);

	SimpleResponse removeGroupUser(Integer user_id);

	List<Permission> getUserPermission(Integer user_id);

	List<User> getGroupUser(Integer group_id,Integer company_id);

}
