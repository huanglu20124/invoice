package com.hl.listener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;


public class RecognizeTimer {
	private Timer timer;
	private ServletContext servletContext;
	private Logger logger;
	public RecognizeTimer(ServletContext servletContext,Logger logger) {
		this.servletContext = servletContext;
		this.logger = logger;
	}
	public void startTimer(){
		timer = new Timer();
		GregorianCalendar gc = new GregorianCalendar();
		//系统启动10秒后开始执行任务
		gc.setTime(new Date());
		gc.add(Calendar.SECOND, 10);
		
		//每30s执行一次
		timer.schedule(new RecognizeTask(servletContext,logger), gc.getTime(),30000);
	}
	
	public void stopTimer(){
		if(timer != null){
			timer.cancel();
			System.out.println("RecognizeTimer定时器关闭了");
		}
	}
}
