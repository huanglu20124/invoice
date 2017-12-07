package com.hl.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.hl.domain.Action;
import com.hl.domain.ActionQuery;
import com.hl.domain.SimpleResponse;
import com.hl.service.ActionService;
import com.hl.util.Const;

@Controller
public class ActionController {
	@Resource(name = "actionService")
	private ActionService actionService;
	//日志action类控制器
	// ajax接口：按时间排序 + 关键词，一次获取20条日志，
	//type= 0:用户ip， 1：用户名字， 2：单位名字， 3：操作类型
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/getTwentyAction.action", method = RequestMethod.POST)
	@ResponseBody
	public String getTwentyAction(Integer page,String startTime,String endTime,String keyword,Integer type) throws IOException{
		System.out.println("接收到查询操作日志的请求");
		System.out.println(page + "  " + startTime + "  " + endTime + "   " + keyword + "。");
		ActionQuery actionQuery = null;
		if(startTime == null && endTime == null && keyword == null){
			//初始化加载,只输入页数
			actionQuery = actionService.getTwentyActionInit(page);
		}
			
		if(keyword == null && startTime != null && endTime != null){
			//只输入时间
			actionQuery = actionService.getTwentyActionByTime(page,startTime,endTime);
		}
		
        if(keyword != null){
        	//输入关键字，包括了输入和没输入时间的情况
			actionQuery = actionService.getTwentyActionByKeyword(page,startTime,endTime,keyword,type);
		}
		if(actionQuery != null){
			System.out.println(JSON.toJSONString(actionQuery));
			return JSON.toJSONString(actionQuery);
		}else {
			return "{}";
		}

	}
		
}

