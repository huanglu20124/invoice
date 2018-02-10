 package com.hl.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.hl.domain.LocalConfig;
import com.hl.domain.ModelAction;
import com.hl.domain.ModelQuery;
import com.hl.domain.SimpleResponse;
import com.hl.exception.InvoiceException;
import com.hl.service.ModelService;
import com.hl.util.Const;
import com.hl.util.IpUtil;
import com.hl.websocket.SystemWebSocketHandler;

@Controller
public class ModelController {
	
	private static Logger logger = Logger.getLogger(ModelController.class);
	
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
		logger.info("接收到来自web端的新增模板的请求");
		return modelService.addModel(request);
	}

	//修改发票模板
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/updateModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String updateModel(HttpServletRequest request, HttpServletResponse response,String img_str,Integer type) throws InvoiceException{
		logger.info("接收到来自web端的修改模板的请求");
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		return modelService.updateModel(request,thread_msg);
	}
	
	// 删除发票模板，ajax上传
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/deleteModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String deleteModel(HttpServletRequest request,Integer user_id, Integer model_id) throws InvoiceException{
		logger.info("接收到删除单张模板的请求");
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		logger.info("model_id = " + model_id);
		String user_ip = IpUtil.getIpAddr(request);
		return modelService.deleteModel(user_id,model_id,user_ip,thread_msg);
	}

	// 返回当前模板库全部信息,一次12条
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/getAllModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String getAllModel(Integer user_id, Integer page)throws IOException{
		logger.info("接收到来自web端的返回当前模板库全部信息的请求, page=" + page);
		return modelService.getAllModel(user_id,page);
	}

	//上传发票模板原图
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/uploadModelOrigin.action", method = RequestMethod.POST)
	@ResponseBody
	public String uploadModelOrigin(HttpServletRequest request,@RequestParam("type")Integer type,
			@RequestParam("file")MultipartFile[]files) throws InvoiceException{
		logger.info("接收到来自web端的上传发票原图请求");
		logger.info("session_id=" + request.getSession().getId());
		logger.info("file_path=" + request.getParameter("file_path"));
		String file_path = request.getParameter("file_path");
		return modelService.uploadModelOrigin(files,type,file_path);
	}
 
	//一键清空模板
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/deleteAllModel.action", method = RequestMethod.POST)
	public void deleteAllModel(HttpServletRequest request, HttpServletResponse response) throws IOException{
		logger.info("接收到清空模板的请求");
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
		logger.info("收到提交全部模板的请求batch_id=" + batch_id);
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		return modelService.pushBatchModel(batch_id,thread_msg);
	}	
	
	//点击一张模板，获得它的imgStr
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/getImgStr.action", method = RequestMethod.POST)
	@ResponseBody
	public String getImgStr(String url)throws InvoiceException{
		logger.info("接收到发送模板图片imgStr的请求");
		return modelService.getImgStr(url);
	}
	
	//关掉画模板的窗口
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/cancelAddModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String cancelAddModel(String file_path) throws InvoiceException{
		logger.info("收到删除上传原图的请求");
		return modelService.cancelAddModel(file_path);
	}		

	//根据session中存储的batch_id获取队列
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/getModelQueue.action", method = RequestMethod.POST)
	@ResponseBody
	public String getModelQueue(HttpSession session) throws InvoiceException{
		logger.info("收到获取提交模板队列的请求");
		String batch_id = (String)session.getAttribute("batch_id");
		if(batch_id == null){
			//throw new InvoiceException("session中的batch_id为空!");
			Map<String, Object>ansMap = new HashMap<>();
			ansMap.put("list", new ArrayList<>());
			//System.out.println(JSON.toJSONString(ansMap));
			return JSON.toJSONString(ansMap);
		}else {
			return modelService.getModelQueue(batch_id);
		}
		
	}
	 		
	//特殊接口：将DataBase.xml文件里面的内容写入Mysql数据库，已经废弃
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/rewriteJsonModel.action", method = RequestMethod.POST)
	public void rewriteJsonModel(HttpServletRequest request, HttpServletResponse response)throws IOException{
		logger.info("接收到将本地json_model写入Mysql数据库的请求");
		PrintWriter writer = response.getWriter();
		Map<String, Object> ans_map = new HashMap<>();
		try {
			modelService.rewriteJsonModel();
			ans_map.put(Const.SUCCESS, "更新成功");
			logger.info("更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			ans_map.put(Const.SUCCESS, "更新失败");
			logger.info("更新失败");
		}
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}

	//删除缓冲队列中的一张模板
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/deleteCacheModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String deleteCacheModel(Integer action_id) throws InvoiceException{
		logger.info("收到删除缓冲队列中的一张模板的请求");
		return modelService.deleteCacheModel(action_id);
	}
	
	//修改缓冲队列中的一张模板
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/updateCacheModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String updateCacheModel(String modelAction, String img_str) throws InvoiceException{
		logger.info("收到修改缓冲队列中的一张模板的请求");
		return modelService.updateCacheModel(JSON.parseObject(modelAction, ModelAction.class),img_str);
	}	
	
	//清空已提交的模板操作
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/clearManageModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String clearManageModel() throws InvoiceException{
		logger.info("收到清空已提交模板队列的请求");
		return modelService.clearManageModel();
	}

	//一键备份
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/clearManageModel.action", method = RequestMethod.POST)
	@ResponseBody
	public String backupModel() throws InvoiceException{
		//主要工作：1、model文件夹备份   2、mysql model表备份，invoice表清空   3、database、training备份
		logger.info("收到一键备份的请求");
		return modelService.backupModel();
	}	
	
}
