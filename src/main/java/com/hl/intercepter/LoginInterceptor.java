package com.hl.intercepter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
public class LoginInterceptor implements HandlerInterceptor{

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		System.out.println("页面渲染后 1");
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		System.out.println("方法后 1");	
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		System.out.println("方法前 1");
		//判断用户是否登陆  如果没有登陆  重定向到登陆页面   不放行   如果登陆了  就放行了
		String requestUri =  request.getRequestURI();
		if(!requestUri.contains("/login")){
			String username = (String) request.getSession().getAttribute("user_name");
			if(username == null){
				System.out.println("重定向到登录界面");
				response.sendRedirect(request.getContextPath() + "/login.jsp");
				return false;
			}
		}
		return true;
	}

}
