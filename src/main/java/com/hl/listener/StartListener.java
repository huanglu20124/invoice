package com.hl.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.hl.controller.SwitcherThread;

public class StartListener implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("任务监听器启动");
		ServletContext servletContext = sce.getServletContext();
		Integer thread_msg = 0;
		SwitcherThread switcherThread = new SwitcherThread(thread_msg, servletContext);
		new Thread(switcherThread).start();
	}

}
