package com.hl.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.naming.AuthenticationException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.hl.domain.User;
import com.hl.exception.InvoiceException;
import com.hl.service.UserService;
import com.hl.util.Const;
import com.sun.org.apache.bcel.internal.generic.ReturnaddressType;

/**
 * 用户系统控制器
 * 
 * @author road
 */
@Controller
public class UserController {

	@Resource(name = "userService")
	private UserService userService;

	private static Logger logger = Logger.getLogger(UserController.class);
	//接口1，用户登录,和shiro中的配置文件一致
	@RequestMapping(value = "/login.action")
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	public String userLogin(HttpServletRequest request) throws Exception {
		//如果登陆失败从request中获取认证异常信息，shiroLoginFailure就是shiro异常类的全限定名
		String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
		//根据shiro返回的异常类路径判断，抛出指定异常信息
		if(exceptionClassName != null){
			if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
				//最终会抛给异常处理器
				System.out.println("账号不存在");
				throw new InvoiceException("账号不存在");
			} else if (IncorrectCredentialsException.class.getName().equals(
					exceptionClassName)) {
				System.out.println("用户名/密码错误");
				throw new InvoiceException("用户名/密码错误");
			}else if(AuthenticationException.class.getName().equals(exceptionClassName)){
				System.out.println("账号不存在！");
				throw new InvoiceException("账号不存在！");
			}else {
				throw new Exception();//最终在异常处理器生成未知错误
			}
		}
		//登录失败返回到login界面
		return "login";
	}

}
