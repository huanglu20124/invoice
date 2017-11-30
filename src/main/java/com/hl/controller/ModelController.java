package com.hl.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSON;
import com.hl.domain.LocalConfig;
import com.hl.domain.Model;
import com.hl.domain.ModelAction;
import com.hl.domain.ModelQuery;
import com.hl.domain.SimpleResponse;
import com.hl.service.InvoiceService;
import com.hl.service.ModelService;
import com.hl.util.Const;
import com.hl.util.IOUtil;
import com.hl.util.ImageUtil;
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
	public void addNewModel(HttpServletRequest request, HttpServletResponse response,String img_str,Integer type) throws IOException {
		System.out.println("接收到来自web端的新增模板或修改模板的请求");
		String action_str = request.getParameter("modelAction");
		System.out.println("modelAction = " + action_str);
		ModelAction modelAction = JSON.parseObject(action_str,ModelAction.class); 
		Map<String, Object> ans_map = new HashMap<>();
		// 用于工作人员的接口，上传处理过的图片
		IOUtil.writeToLocal(img_str);
		//System.out.println("file_name "+ request.getParameter("file_name"));
		//名字来自客户端返回的
		String file_name = ImageUtil.getFileName(request.getParameter("file_name"));
		//String url_suffix = "image/model/handle/" + TimeUtil.getYearMonthDir() + "/" + file_name;
		String url_suffix = "image/model/handle/" + "201710" + "/" + file_name;
		//设置给modelAction
		modelAction.setUrl_suffix(url_suffix);
		if (ImageUtil.generateImage(img_str, localConfig.getImagePath() + "image/model/handle/"+"201710",
				file_name) == true) {
			System.out.println("上传文件成功");
			modelAction.setImage_size(ImageUtil.getImageSize(localConfig.getImagePath() + url_suffix));
		} else {
			ans_map.put(Const.ERR, "上传文件失败");
			System.out.println("上传文件失败");
		}
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		if(type == 0){
			//增加模板
			modelAction.setMsg_id(2);
		}else {
			//修改模板
			modelAction.setMsg_id(4);
		}
		modelAction.setUser_ip(IpUtil.getIpAddr(request));
		modelService.addOrUpdateInvoiceModel(ans_map,modelAction,thread_msg);
		 response.getWriter().write(JSON.toJSONString(ans_map));
	}

	// 删除发票模板，ajax上传
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/deleteModel.action", method = RequestMethod.POST)
	public void deleteModel(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("接收到删除单张模板的请求");
		Map<String, Object> ans_map = new HashMap<>();
		Integer user_id = new Integer(request.getParameter(Const.USER_ID));
		Integer model_id = new Integer(request.getParameter(Const.MODEL_ID));
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		System.out.println("model_id = " + model_id);
		String user_ip = IpUtil.getIpAddr(request);
		modelService.deleteInvoiceModel(ans_map,user_id,model_id,user_ip,thread_msg);
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
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
	public void uploadModelOrigin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println("接收到来自web端的上传发票原图请求");
		final Map<String, Object> ans_map = new HashMap<>();
		// 建立文件夹,子目录为年+月
		File save_folder = new File(localConfig.getImagePath() + "image/model/original/" + "201710");
		if (save_folder.exists() == false) {
			save_folder.mkdirs();
		}
		// 获取全部文件
		CommonsMultipartResolver cmr = new CommonsMultipartResolver(request.getServletContext());
		if (cmr.isMultipart(request)) {
			MultipartHttpServletRequest request2 = (MultipartHttpServletRequest) request;
			Iterator<String> files = request2.getFileNames();
			// 获取其他参数
			while (files.hasNext()) {
				MultipartFile file = request2.getFile(files.next());
				//保存的文件名由uuid生成
				String save_file_name = UUID.randomUUID().toString() + ".bmp";//暂时 保存的文件名=原始文件名
				//生成后缀
				String url_suffix = "image/model/original/"+"201710"+"/"+save_file_name;
				try {
					//先保存bmp
					File bmp_file = new File(save_folder, save_file_name);
					FileOutputStream fos = new FileOutputStream(bmp_file);
					InputStream ins = file.getInputStream();
					IOUtil.inToOut(ins, fos);
					IOUtil.close(ins, fos);
					System.out.println("上传文件成功;");
					//再保存jpg
					ImageUtil.bmpTojpg(localConfig.getImagePath() + url_suffix);
					//重要！将文件url返回给web端
					ans_map.put("file_name", ImageUtil.suffixToJpg(localConfig.getIp() + url_suffix));
					String local_jpg = ImageUtil.suffixToJpg(localConfig.getImagePath() + url_suffix);
					ans_map.put("img_str", "data:image/jpg;base64," + ImageUtil.GetImageStr(local_jpg));
				} catch (Exception e) {
					e.printStackTrace();
					ans_map.put(Const.ERR, "上传文件失败");
				}
			}
		} else {
			ans_map.put(Const.ERR, "请求格式有错误");
		}
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
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
			SimpleResponse simpleResponse = new SimpleResponse();
			simpleResponse.setErr("请输入查询关键字");
			return JSON.toJSONString(simpleResponse);
		}
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
