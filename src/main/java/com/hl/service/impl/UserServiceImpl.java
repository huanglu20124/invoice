package com.hl.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import com.hl.dao.UserDao;
import com.hl.domain.Company;
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
			user.setPermissions(getAllPermission(user.getUser_id()));
			//设置用户组集合
			user.setGroups(userDao.getUserGroups(user.getUser_id()));
		}
		return list;
	}
	
	@Override
	public List<Permission> updateUsersPermission(Integer user_id,List<Permission>permission_list) {
		List<Permission>ans_list = new ArrayList<>();
		//修改用户权限
		for(Permission permission : permission_list){
				if(userDao.getIsUserPermission(user_id,permission.getPermission_name()) == true){
					//有这个权限了
					if(permission.getIs_checked() == 0){
						//没打钩就删除
						userDao.deleteUserPermission(user_id,permission.getPermission_name());
					}else {
						ans_list.add(permission);
					}
				}else {
					if(permission.getIs_checked() == 1){
						userDao.addUserPermission(user_id,permission.getPermission_name());
						ans_list.add(permission);
					}
				}
		}
		return ans_list;
	}
		
	//获得用户全部权限公有加上私有的
	@Override
	public List<Permission> getAllPermission(Integer user_id){
		List<Permission>private_permissions = userDao.getUserPermission(user_id);
		for(Permission permission : private_permissions){
			permission.setIsPrivate(1);
		}
		//先得到用户的全部所属用户组，然后得到权限，再凑成集合，遍历
		List<Group>groups = userDao.getUserGroups(user_id);
		List<List<Permission>>groups_permissions = new ArrayList<>();
		for(Group group : groups){
			groups_permissions.add(userDao.getGroupPermission(group.getGroup_id()));
		}
		//得到集合,变成数组
	    Set<Permission>public_permission_set = new HashSet<>();
	    for(List<Permission>permissions : groups_permissions){
	    	public_permission_set.addAll(permissions);
	    }
	    List<Permission>public_permissions = new ArrayList<Permission>(public_permission_set);
		//最后进行记录，一个公有权限的所属用户组
	    Map<Permission, List<String>>map = new HashMap<>();
	    for(int i = 0; i < groups_permissions.size(); i++){
	    	List<Permission> permissions = groups_permissions.get(i);
	    	for(Permission permission : permissions){
	    		List<String>list = map.get(permission);
	    		if(list == null) list = new ArrayList<>();
	    		list.add(groups.get(i).getGroup_name());
	    		map.put(permission, list);
	    	}
	    }
	    //最后    
		for(Permission permission : public_permissions){
			List<String>list = map.get(permission);
			permission.setIsPrivate(0);
			permission.setOrigin_groups(list);
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
	public List<Permission> updateGroupPermission(List<Permission> list, Integer group_id) {
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
		List<Permission>ans_list = userDao.getGroupPermission(group_id);
		for(Permission permission : ans_list){
			permission.setIsPrivate(0);
		}
		return ans_list;
	}

	
	@Override
	public Map<String, Object>  addGroupUser(Integer user_id, Integer group_id) {
		Map<String, Object>map = new HashMap<>();
		//先检查一下
		try {
				userDao.addGroupUser(user_id,group_id);
				User user = userDao.getUserById(user_id);
				map.put("user", user);
		} catch (Exception e) {
			map.put("err","添加到用户组失败");
			e.printStackTrace();
		}		
		return map;
	}


	@Override
	public SimpleResponse removeGroupUser(Integer user_id,Integer group_id) {
		SimpleResponse simpleResponse = new SimpleResponse();
		try {
			userDao.removeGroupUser(user_id,group_id);
			simpleResponse.setSuccess("移出用户组成功");
		} catch (Exception e) {
			simpleResponse.setErr("移除用户组失败");
			e.printStackTrace();
		}
		return simpleResponse;
	}

	@Override
	public List<User> getGroupUser(Integer group_id, Integer company_id) {
		List<User> user_list = userDao.getGroupUser(group_id, company_id);
		System.out.println("user_list.size()=" + user_list.size());
		for(User user : user_list){
			user.setPermissions(getAllPermission(user.getUser_id()));
			//设置用户组集合
			user.setGroups(userDao.getUserGroups(user.getUser_id()));
		}
		return user_list;
	}

	
	@Override
	public Company getCompany(Integer user_id) {
		return userDao.getUserCompany(user_id);
	}


}
