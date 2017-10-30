package com.hl.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hl.socket.SocketLoadTool;

@Controller
public class HelloController {
	@RequestMapping("/helloWorld")
	public String hello(ModelMap model){
		System.out.println("hello被调用");
		model.addAttribute("message", "hello黄路");
		return "hello";
	}
	
	public void test(HttpServletRequest request, HttpServletResponse response) throws IOException{
		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
		SocketLoadTool socketListener = (SocketLoadTool) applicationContext.getBean("socketListener");
		if(socketListener != null){
			Socket socket = socketListener.getAlgorithmSocket();
			if(socket != null){
				System.out.println("成功获取到socket");
			}else {
				System.out.println("socket为空");
			}
		}else {
			System.out.println("监听器为空的");
		}
		PrintWriter writer = response.getWriter();
		writer.write("接受请求完毕");
	}
}
