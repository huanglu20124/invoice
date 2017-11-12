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
import com.hl.service.ActionService;
import com.hl.util.Const;
import com.mysql.fabric.xmlrpc.base.Array;

@Controller
public class ActionController {
	@Resource(name = "actionService")
	private ActionService actionService;
	//日志action类控制器
	// ajax接口：按时间排序 + 关键词，一次获取20条日志，
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/getTwentyAction.action", method = RequestMethod.POST)
	@ResponseBody
	public String getTwentyAction(Integer page,String startTime,String endTime,String keywrods) throws IOException{
		System.out.println("接收到查询操作日志的请求");
		System.out.println(page + "  " + startTime + "  " + endTime + "   " + keywrods);
		List<Action> action_list = null;
		if(keywrods == null){
			action_list = actionService.getTwentyActionByTime(page,startTime,endTime);
		}else {
			action_list = actionService.getTwentyActionByKeywords(page,startTime,endTime,keywrods);
		}
		if(action_list == null){
			action_list = new ArrayList<>();
		}
		System.out.println(JSON.toJSONString(action_list));
		return JSON.toJSONString(action_list);
	}
	
}
