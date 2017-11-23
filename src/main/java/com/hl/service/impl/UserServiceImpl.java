package com.hl.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hl.dao.UserDao;
import com.hl.domain.Permission;
import com.hl.domain.SimpleResponse;
import com.hl.domain.UpdatePermission;
import com.hl.domain.UpdateUser;
import com.hl.domain.User;
import com.hl.service.UserService;

@Service("userService")
public class UserServiceImpl implements UserService{

	@Resource(name = "userDao")
	private UserDao userDao;
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	
	@Override
	public User loginByNamePwd(String user_name, String user_password) {
		return userDao.getUserByNamePwd(user_name, user_password);
	}

	@Override
	public List<User> getManagerUsers(Integer user_id) {
		List<User>list = userDao.getManagerUsers(user_id);
		if(list == null){
			list = new ArrayList<>();
		}
		//添加用户权限
		for(User user : list){
			user.setPermissions(getUserPermission(user));
		}
		return list;
	}

	
	@Override
	public SimpleResponse updateUsersPermission(List<UpdateUser>users) {
		SimpleResponse response = new SimpleResponse();
		//修改用户权限
		for(UpdateUser user : users){
			//遍历
			for(UpdatePermission permission : user.getUpdate_permissions()){
				if(userDao.getIsPermission(user.getUser_id(),permission.getPermission_id()) == true){
					//有这个权限了
					if(permission.getIs_checked() == 0){
						//没打钩就删除
						userDao.deleteUserPermission(user.getUser_id(),permission.getPermission_id());
					}
				}else {
					if(permission.getIs_checked() == 1){
						userDao.addUserPermission(user.getUser_id(),permission.getPermission_id());
					}
				}
			}
		}
		response.setSuccess("修改成功");
		return response;
	}
	
	
	//取交集，获得用户全部权限
	public List<Permission> getUserPermission(User user){
		//根据身份信息获取用户权限信息
		Set<Permission>permissions = new HashSet<>();
		//取交集
		permissions.addAll(userDao.getUserPermission(user.getUser_id()));
		permissions.addAll(userDao.getGroupPermission(user.getGroup_id()));
		//放到下面的数组中
		List<Permission>list_permissions = new ArrayList<>();
		Iterator<Permission>iterator = permissions.iterator();
		while(iterator.hasNext()){
			Permission permission = iterator.next();
			list_permissions.add(permission);
		}
		return list_permissions;
	}
	
}
