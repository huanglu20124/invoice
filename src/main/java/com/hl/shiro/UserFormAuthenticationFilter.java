package com.hl.shiro;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import com.hl.dao.ActionDao;
import com.hl.domain.Action;
import com.hl.domain.Company;
import com.hl.domain.User;
import com.hl.service.UserService;

public class UserFormAuthenticationFilter extends FormAuthenticationFilter {

	@Resource(name = "userService")
	private UserService userService;
	
	//原FormAuthenticationFilter的认证方法
	@Override
	protected boolean onAccessDenied(ServletRequest request,
			ServletResponse response) throws Exception {
		System.out.println("登录成功");
		return super.onAccessDenied(request, response);
	}
	
	@Override
	protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
			ServletResponse response) throws Exception {
		System.out.println("登录成功");
		Session session = subject.getSession();
		
		Company company = (Company) session.getAttribute("company");
		if(company == null){
			User user = (User) subject.getPrincipal();
			if(user != null){
			    Company temp =  userService.getCompany(user.getUser_id());
			    System.out.println(temp.getCompany_name());
			    session.setAttribute("company", temp);
			}
			
		}

		
		return super.onLoginSuccess(token, subject, request, response);
	}

		
}
