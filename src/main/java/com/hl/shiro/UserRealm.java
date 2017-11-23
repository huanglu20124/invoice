package com.hl.shiro;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.alibaba.fastjson.JSON;
import com.hl.dao.UserDao;
import com.hl.domain.Permission;
import com.hl.domain.User;

public class UserRealm extends AuthorizingRealm {

	@Resource(name = "userDao")
	private UserDao userDao;
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		//授权
		//从 principals获取主身份信息
		User user = (User) principals.getPrimaryPrincipal();
		//根据身份信息获取用户权限信息
		Set<Permission>permissions = new HashSet<>();
		//取交集
		permissions.addAll(userDao.getUserPermission(user.getUser_id()));
		permissions.addAll(userDao.getGroupPermission(user.getGroup_id()));
		//放到下面的数组中
		List<String>permission_strs = new ArrayList<>();
		Iterator<Permission>iterator = permissions.iterator();
		while(iterator.hasNext()){
			Permission permission = iterator.next();
			permission_strs.add(permission.getPermission_name());
			//System.out.println("权限名称为"+permission.getPermission_name());
		}
		SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
		simpleAuthorizationInfo.addStringPermissions(permission_strs);
		return simpleAuthorizationInfo;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		//认证
		// token是用户输入的用户名和密码 
		// 第一步从token中取出用户名
		String user_name = (String) token.getPrincipal();
		// 第二步：根据用户输入的user_name从数据库查询
		User user = userDao.getUserByName(user_name);
		System.out.println(user_name);
		if(user == null){
			//抛出账户不存在的异常
			throw new UnknownAccountException();
		}
		if(user.getLocked() == 1){
			//抛出账户锁定异常
			throw new LockedAccountException();
		}
		String password = user.getUser_password();
		String salt = user.getSalt();
		//权限集合
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
		user.setPermissions(list_permissions);
		//将User设置simpleAuthenticationInfo
		SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user, password,ByteSource.Util.bytes(salt), this.getName());
		return authenticationInfo;
	}
	
	@Override
	public void setName(String name) {
		super.setName("customRealm");
	}

}
