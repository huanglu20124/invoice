package com.hl.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.hl.domain.User;
import com.hl.service.UserService;
import com.hl.util.Const;

/**
 * 用户系统控制器
 * 
 * @author road
 */
@Controller
public class UserController {

	@Resource(name = "userService")
	private UserService userService;

	@RequestMapping(value = "/loginCheck", method = RequestMethod.POST)
	String login(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String user_name = request.getParameter(Const.USER_NAME);
		String password = request.getParameter(Const.USER_PASSWORD);
		User user = null;
		 if((user = userService.loginByNamePwd(user_name,password)) != null){
		 session.setAttribute(Const.USER_NAME, user.getUser_name());
		 return "hello";
		 }else {
		 return "login";
		 }

	}
}
