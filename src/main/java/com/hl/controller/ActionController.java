package com.hl.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.hl.service.ActionService;
import com.hl.util.Const;
import com.mysql.fabric.xmlrpc.base.Array;

@Controller
public class ActionController {
	@Resource(name = "actionService")
	private ActionService actionService;
	//日志action类控制器
	// ajax接口：按时间排序，一次获取20条日志，
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/getTwentyAction.action", method = RequestMethod.POST)
	public void getTwentyAction(HttpServletRequest request, HttpServletResponse response, Integer page) throws IOException{
		System.out.println("接收到查询操作日志的请求");
		Map<String, Object>ans_map = new HashMap<>();
		actionService.getTwentyAction(page,ans_map);
		PrintWriter printWriter = response.getWriter();
		printWriter.write(JSON.toJSONString(ans_map));
		printWriter.flush();
	}
	
}
