package com.hl.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hl.dao.UserDao;
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
	
	
}
