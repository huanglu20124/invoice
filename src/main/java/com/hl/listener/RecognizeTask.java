package com.hl.listener;

import java.util.TimerTask;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hl.dao.RedisDao;
import com.hl.util.Const;

public class RecognizeTask extends TimerTask{

	private ServletContext servletContext;
	private Logger logger;
	private Integer last_count;
	private RedisDao redisDao;
	public RecognizeTask(ServletContext servletContext,Logger logger) {
		this.servletContext = servletContext;
		this.logger = logger;
		last_count = 0;
		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		redisDao = (RedisDao) applicationContext.getBean("redisDao");
		//初始化置为0
		redisDao.addKey(Const.MINUTE_SUM, "0");
	}
	@Override
	public void run() {
		Integer latest_num = new Integer((String) redisDao.getValue(Const.MINUTE_SUM));
	    logger.info("过去30秒识别的数量为" + (latest_num - last_count));
		last_count = latest_num;
	}

}
