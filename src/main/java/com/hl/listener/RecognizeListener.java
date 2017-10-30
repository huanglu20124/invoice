package com.hl.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

public class RecognizeListener implements ServletContextListener{

	private Integer last_count = 0;
	private RecognizeTimer timer;
	private static Logger logger = Logger.getLogger(RecognizeListener.class);
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("关闭发票识别统计器");
		if(timer != null){
			timer.stopTimer();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("初始化发票识别统计器");
		timer = new RecognizeTimer(arg0.getServletContext(),logger);
		timer.startTimer();
	}

}
