package com.hl.socket;

import java.io.IOException;
import java.net.Socket;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Component("socketListener")
@Scope("singleton")
public class SocketLoadTool {

	private static Socket socket;
	static{
		try {
			socket = SocketUtil.getSocket();
			System.out.println("Socket监听器打开");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Socket监听器创建失败");
		}
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	

}
