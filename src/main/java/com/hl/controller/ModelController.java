package com.hl.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.hl.domain.LocalConfig;
import com.hl.domain.ModelQuery;
import com.hl.domain.SimpleResponse;
import com.hl.exception.InvoiceException;
import com.hl.service.ModelService;
import com.hl.util.Const;
import com.hl.util.IpUtil;
import com.hl.websocket.SystemWebSocketHandler;

@Controller
public class ModelController {
	@Resource(name = "systemWebSocketHandler")
	private SystemWebSocketHandler systemWebSocketHandler;
	
	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "localConfig")
	private LocalConfig localConfig;
	
	//增加发票模板，ajax上传，图片为Base64，
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/addModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String addModel(HttpServletRequest request, HttpServletResponse response,String img_str,Integer type) throws InvoiceException{
		System.out.println("接收到来自web端的新增模板的请求");
		return modelService.addModel(request);
	}

	//修改发票模板
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/updateModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String updateModel(HttpServletRequest request, HttpServletResponse response,String img_str,Integer type) throws InvoiceException{
		System.out.println("接收到来自web端的修改模板的请求");
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		return modelService.updateModel(request,thread_msg);
	}
	
	// 删除发票模板，ajax上传
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/deleteModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String deleteModel(HttpServletRequest request,Integer user_id, Integer model_id) throws InvoiceException{
		System.out.println("接收到删除单张模板的请求");
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		System.out.println("model_id = " + model_id);
		String user_ip = IpUtil.getIpAddr(request);
		return modelService.deleteModel(user_id,model_id,user_ip,thread_msg);
	}

	// 返回当前模板库全部信息,一次12条
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/getAllModel.action", method = RequestMethod.POST)
	public void getAllModel(HttpServletRequest request, HttpServletResponse response)throws IOException{
		System.out.println("接收到来自web端的返回当前模板库全部信息的请求");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Map<String, Object> ans_map = new HashMap<>();
		Integer user_id = new Integer(request.getParameter(Const.USER_ID));
		Integer page = new Integer(request.getParameter("page"));
		modelService.getAllModel(ans_map,user_id,page);
		response.getWriter().write(JSON.toJSONString(ans_map));
	}

	//上传发票模板原图
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/uploadModelOrigin.action", method = RequestMethod.POST)
	@ResponseBody
	public String uploadModelOrigin(HttpServletRequest request,@RequestParam("type")Integer type,
			@RequestParam("file")MultipartFile[]files) throws InvoiceException{
		System.out.println("接收到来自web端的上传发票原图请求");
		return modelService.uploadModelOrigin(files,type,request.getSession());
	}

	//一键清空模板
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/deleteAllModel.action", method = RequestMethod.POST)
	public void deleteAllModel(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("接收到清空模板的请求");
		Integer user_id = new Integer(request.getParameter(Const.USER_ID));
		Map<String, Object> ans_map = new HashMap<>();
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		modelService.deleteAllModel(ans_map,user_id,thread_msg);
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();	
	}

	//通过label模糊查询发票模板信息
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/searchModelLabel.action", method = RequestMethod.POST)
	@ResponseBody
	public String searchModelLabel(Integer page,Integer user_id, String keyword){
		ModelQuery modelQuery = modelService.searchModelLabel(page,user_id,keyword);
		if(modelQuery != null){
			return JSON.toJSONString(modelQuery);
		}else {
			SimpleResponse simpleResponse = new SimpleResponse(null,null);
			simpleResponse.setErr("请输入查询关键字");
			return JSON.toJSONString(simpleResponse);
		}
	}
	
	//将该队列的modelAction加到manage队列中，通知线程切换
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/pushBatchModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String pushBatchModel(HttpServletRequest request,String batch_id) throws InvoiceException{
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		return modelService.pushBatchModel(batch_id,thread_msg);
	}	
	
	
	//特殊接口：将DataBase.xml文件里面的内容写入Mysql数据库，已经废弃
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/rewriteJsonModel.action", method = RequestMethod.POST)
	public void rewriteJsonModel(HttpServletRequest request, HttpServletResponse response)throws IOException{
		System.out.println("接收到将本地json_model写入Mysql数据库的请求");
		PrintWriter writer = response.getWriter();
		Map<String, Object> ans_map = new HashMap<>();
		try {
			modelService.rewriteJsonModel();
			ans_map.put(Const.SUCCESS, "更新成功");
			System.out.println("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			ans_map.put(Const.SUCCESS, "更新失败");
			System.out.println("更新失败");
		}
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}

	
}
