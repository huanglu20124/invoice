package com.hl.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hl.dao.UserDao;
import com.hl.domain.Group;
import com.hl.domain.Permission;
import com.hl.domain.SimpleResponse;
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
	public SimpleResponse updateUsersPermission(Integer user_id,List<Permission>permission_list) {
		SimpleResponse response = new SimpleResponse();
		//修改用户权限
		for(Permission permission : permission_list){
				if(userDao.getIsUserPermission(user_id,permission.getPermission_name()) == true){
					//有这个权限了
					if(permission.getIs_checked() == 0){
						//没打钩就删除
						userDao.deleteUserPermission(user_id,permission.getPermission_name());
					}
				}else {
					if(permission.getIs_checked() == 1){
						userDao.addUserPermission(user_id,permission.getPermission_name());
					}
				}
		}
		response.setSuccess("修改成功");
		return response;
	}
		
	//获得用户全部权限公有加上私有的
	public List<Permission> getUserPermission(User user){
		List<Permission>private_permissions = userDao.getUserPermission(user.getUser_id());
		for(Permission permission : private_permissions){
			permission.setIsPrivate(1);
		}
		List<Permission>public_permissions = userDao.getGroupPermission(user.getGroup_id());
		for(Permission permission : public_permissions){
			permission.setIsPrivate(0);
		}
		//相加
		private_permissions.addAll(public_permissions);
		return private_permissions;
	}

		
	@Override
	public List<Group> getManagerGroups(Integer user_id) {
		List<Group>list = userDao.getManagerGroups(user_id);
		if(list == null){
			list = new ArrayList<>();
		}
		//添加用户权限
		for(Group group : list){
			List<Permission>permissions = userDao.getGroupPermission(group.getGroup_id());
			//将权限属性定义为公有
			for(Permission permission : permissions){
				permission.setIsPrivate(0);
			}
			group.setPermissions(permissions);
		}
		return list;
	}


	@Override
	public SimpleResponse updateGroupPermission(List<Permission> list, Integer group_id) {
		System.out.println("group_id" + group_id);
		for(Permission permission : list){
			System.out.println(permission.getPermission_name());
			System.out.println("isChecked" + permission.getIs_checked());
			if(userDao.getIsGroupPermission(permission.getPermission_name(),group_id) == true){
				if(permission.getIs_checked() == 0){
					System.out.println("删除权限");
					userDao.deleteGroupPermission(group_id, permission.getPermission_name());
				}
			}else {
				if(permission.getIs_checked() == 1){
					System.out.println("增加权限");
					userDao.addGroupPermission(group_id, permission.getPermission_name());
				}
			}
		}
		SimpleResponse simpleResponse = new SimpleResponse();
		simpleResponse.setSuccess("修改成功");
		return simpleResponse;
	}

	
	@Override
	public SimpleResponse addGroupUser(Integer user_id, Integer group_id) {
		SimpleResponse simpleResponse = new SimpleResponse();
		try {
			userDao.addGroupUser(user_id,group_id);
			simpleResponse.setSuccess("添加到用户组成功");
		} catch (Exception e) {
			simpleResponse.setErr("添加到用户组失败");
			e.printStackTrace();
		}		
		return simpleResponse;
	}


	@Override
	public SimpleResponse removeGroupUser(Integer user_id) {
		SimpleResponse simpleResponse = new SimpleResponse();
		try {
			userDao.removeGroupUser(user_id);
			simpleResponse.setSuccess("移出用户组成功");
		} catch (Exception e) {
			simpleResponse.setErr("移除用户组失败");
			e.printStackTrace();
		}
		return simpleResponse;
	}

	
	@Override
	public List<Permission> getUserPermission(Integer user_id) {
		User user = userDao.getUserById(user_id);
		if(user != null){
			return getUserPermission(user);
		}else {
			return new ArrayList<>();
		}
	}

	@Override
	public List<User> getGroupUser(Integer group_id, Integer company_id) {
		List<User> user_list = userDao.getGroupUser(group_id, company_id);
		System.out.println("user_list.size()=" + user_list.size());
		for(User user : user_list){
			user.setPermissions(getUserPermission(user.getUser_id()));
		}
		return user_list;
	}


}
