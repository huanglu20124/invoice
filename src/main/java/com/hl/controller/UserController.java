package com.hl.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.hl.domain.User;
import com.hl.exception.CustomException;
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
	@RequestMapping(value = "/loginCheck", method = RequestMethod.POST)
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	public String userLogin(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String user_name = request.getParameter(Const.USER_NAME);
		String password = request.getParameter(Const.USER_PASSWORD);
		User user = null;
		 if((user = userService.loginByNamePwd(user_name,password)) != null){
		      session.setAttribute(Const.USER_NAME, user.getUser_name());
		      session.setAttribute("user",user);
		      System.out.println("登录成功,重定向到paint.action");
		      logger.debug("用户"+ user_name +"登录");
		      //重定向
		      return "redirect:/paint.action";
		      
		 }else {
			session.setAttribute("err", "账号或密码错误");
			return "redirect:/login.action";
		}
		
		//如果登陆失败从request中获取认证异常信息，shiroLoginFailure就是shiro异常类的全限定名
//		String exceptionClassName = (String) request.getAttribute("shiroLoginFailure");
//		//根据shiro返回的异常类路径判断，抛出指定异常信息
//		if(exceptionClassName!=null){
//			if (UnknownAccountException.class.getName().equals(exceptionClassName)) {
//				//最终会抛给异常处理器
//				throw new CustomException("账号不存在");
//			} else if (IncorrectCredentialsException.class.getName().equals(
//					exceptionClassName)) {
//				throw new CustomException("用户名/密码错误");
//			}else {
//				throw new Exception();//最终在异常处理器生成未知错误
//			}
//		}
//		//此方法不处理登陆成功（认证成功），shiro认证成功会自动跳转到上一个请求路径
//		//登陆失败还到login页面
//		return "login";
	}

	//接口2：用户注销
	@RequestMapping(value = "/logout.action", method = RequestMethod.GET)
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	public String userLogout(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String user_name = request.getParameter(Const.USER_NAME);
		request.getSession().invalidate();
	    System.out.println("注销成功");
	    return "redirect:/login.action";
	}

	//jsp接口
	@RequestMapping(value = "/login.action", method = RequestMethod.GET)
	public String loginAction(){
		return "login";
	}
}
