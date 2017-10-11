package com.hl.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSON;
import com.hl.service.InvoiceService;
import com.hl.util.ImageUtil;
import com.hl.util.Const;
import com.hl.util.IOUtil;
import com.hl.websocket.SystemWebSocketHandler;

/**
 * 发票系统控制器
 * @author road
 */
@Controller
public class InvoiceController {

	@Resource(name = "systemWebSocketHandler")
	private SystemWebSocketHandler systemWebSocketHandler;

	@Resource(name = "invoiceService")
	private InvoiceService invoiceService;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 测试专用
		Logger logger = Logger.getLogger(InvoiceController.class);
		logger.info("info测试");
		logger.debug("debug测试");
		logger.error("error测试");
	}

	// 接口1：用户上传一张或多张图片，加入识别发票的请求队列，表单格式上传(请求enctype必须为multiple，可上传一张或多组)
	@RequestMapping(value = "/recognizeImage", method = RequestMethod.POST)
	public void recognizeNewInvoice(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println("接收到来自web端的识别发票请求");
		final Map<String, Object> ans_map = new HashMap<>();
		// 建立文件夹
		File save_folder = new File("E:/invoice/originImage");
		if (save_folder.exists() == false) {
			save_folder.mkdirs();
		}
		// 获取全部文件
		CommonsMultipartResolver cmr = new CommonsMultipartResolver(request.getServletContext());
		if (cmr.isMultipart(request)) {
			MultipartHttpServletRequest request2 = (MultipartHttpServletRequest) request;
			Iterator<String> files = request2.getFileNames();
			
			// 获取其他参数
			Integer user_id = Integer.valueOf(request.getParameter(Const.USER_ID));
			Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);
			List<String>image_urls = new ArrayList<>();
			
			while (files.hasNext()) {
				MultipartFile file = request2.getFile(files.next());
				String origin_file_name = file.getOriginalFilename();
				System.out.println("原始文件名:" + origin_file_name);
				String save_file_name = UUID.randomUUID().toString() + ".bmp";//暂时 保存的文件名=原始文件名
				// 返回图片的网络url
				String url = Const.LOCAL_IP + "/invoice/originImage/" + save_file_name;
				try {
					//先保存bmp
					File bmp_file = new File(save_folder, save_file_name);
					FileOutputStream fos = new FileOutputStream(bmp_file);
					InputStream ins = file.getInputStream();
					IOUtil.inToOut(ins, fos);
					IOUtil.close(ins, fos);
					System.out.println("上传文件成功;");
					//再保存jpg
					ImageUtil.bmpTojpg("E:/invoice/originImage/"+save_file_name, "E:/invoice/originImage/");
					//将jpg结尾的作为url
					image_urls.add(ImageUtil.urlToJpg(url));
				} catch (Exception e) {
					e.printStackTrace();
					ans_map.put(Const.ERR, "上传文件失败");
				}
			}
			//图片全部上传完毕才调用service层
			invoiceService.addRecognizeInvoice(ans_map, user_id, image_urls, thread_msg);
			
		} else {
			ans_map.put(Const.ERR, "请求格式有错误");
		}
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}

	// 接口2：增加或修改发票模板，ajax上传，图片为Base64，
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/addModel", method = RequestMethod.POST)
	public void addNewModel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("接收到来自web端的新增模板或修改模板的请求");
		Map<String, Object> ans_map = new HashMap<>();
		// 用于工作人员的接口，上传处理过的图片
		String img_str = request.getParameter("img_str");
		IOUtil.writeToLocal(img_str);
		String json_model = request.getParameter(Const.JSON_MODEL);
		System.out.println("json_model="+ json_model);
		Map<String, Object>model_json_map = JSON.parseObject(json_model);
		Integer user_id = new Integer(request.getParameter(Const.USER_ID));
		Integer model_id = new Integer(request.getParameter(Const.MODEL_ID));
		Integer type = new Integer(request.getParameter("type"));
		System.out.println("user_id="+user_id + " model_id="+ model_id);
		//名字来自客户端返回的
		String file_name = ImageUtil.getFileName(request.getParameter("file_name"));
		//String file_name = UUID.randomUUID().toString() + ".bmp";//生成随机名字
		String url = null;
		if (ImageUtil.generateImage(img_str, "E:/invoice/handleImage", file_name) == true) {
			//生成图片url
			url =  Const.LOCAL_IP + "/invoice/handleImage/" + file_name;
			System.out.println("上传文件成功");
		} else {
			ans_map.put(Const.ERR, "上传文件失败");
			System.out.println("上传文件失败");
		}
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		if(type == 0){
			//jpg结尾的作为url
			invoiceService.addOrUpdateInvoiceModel(ans_map, user_id,model_json_map,url,model_id,thread_msg,2);
		}else {
			invoiceService.addOrUpdateInvoiceModel(ans_map, user_id,model_json_map, url,model_id,thread_msg,4);
		}
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}

	// 接口3：删除发票模板，ajax上传
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/deleteModel", method = RequestMethod.POST)
	public void deleteModel(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("接收到删除单张模板的请求");
		Map<String, Object> ans_map = new HashMap<>();
		Integer user_id = new Integer(request.getParameter(Const.USER_ID));
		Integer model_id = new Integer(request.getParameter(Const.MODEL_ID));
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		System.out.println("model_id = " + model_id);
		invoiceService.deleteInvoiceModel(ans_map,user_id,model_id,thread_msg);
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}

	// 接口4：返回当前模板库全部信息,一次12条
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/getAllModel", method = RequestMethod.POST)
	public void getAllModel(HttpServletRequest request, HttpServletResponse response)throws IOException{
		System.out.println("接收到来自web端的返回当前模板库全部信息的请求");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Map<String, Object> ans_map = new HashMap<>();
		Integer user_id = new Integer(request.getParameter(Const.USER_ID));
		Integer start = new Integer(request.getParameter("start"));
		invoiceService.getAllModel(ans_map,user_id,start);
		System.out.println(ans_map);
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}

	// 接口5：上传发票模板原图
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/uploadModelOrigin", method = RequestMethod.POST)
	public void uploadModelOrigin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println("接收到来自web端的上传发票原图请求");
		final Map<String, Object> ans_map = new HashMap<>();
		// 建立文件夹
		File save_folder = new File("E:/invoice/originImage");
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
				String origin_file_name = file.getOriginalFilename();
				System.out.println("原始文件名:" + origin_file_name);
				//保存的文件名由uuid生成
				String save_file_name = UUID.randomUUID().toString() + ".bmp";//暂时 保存的文件名=原始文件名
				// 返回图片的网络url
				String url = Const.LOCAL_IP + "/invoice/originImage/" + save_file_name;
				try {
					//先保存bmp
					File bmp_file = new File(save_folder, save_file_name);
					FileOutputStream fos = new FileOutputStream(bmp_file);
					InputStream ins = file.getInputStream();
					IOUtil.inToOut(ins, fos);
					IOUtil.close(ins, fos);
					System.out.println("上传文件成功;");
					//再保存jpg
					ImageUtil.bmpTojpg("E:/invoice/originImage/" + save_file_name, "E:/invoice/originImage/");
					//重要！将文件url返回给web端
					ans_map.put("file_name", ImageUtil.urlToJpg(url));
					String local_bmp = ImageUtil.urlToLocalPathBmp(url, "E:/invoice/originImage/");
					String jpg = ImageUtil.urlToJpg(local_bmp);
					ans_map.put("img_str", "data:image/jpg;base64," + ImageUtil.GetImageStr(jpg));
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

	// 接口6：一键清空模板
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/deleteAllModel", method = RequestMethod.POST)
	public void deleteAllModel(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("接收到清空模板的请求");
		Integer user_id = new Integer(request.getParameter(Const.USER_ID));
		Map<String, Object> ans_map = new HashMap<>();
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		invoiceService.deleteAllModel(ans_map,user_id,thread_msg);
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();	
	}
	
	// 接口7：点击一张图片，获得它的imgStr
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/getImgStr", method = RequestMethod.POST)
	public void getImgStr(HttpServletRequest request, HttpServletResponse response)throws IOException{
		System.out.println("接收到发送模板图片imgStr的请求");
		Map<String, Object> ans_map = new HashMap<>();
		String url = request.getParameter(Const.URL);
		System.out.println(url);
		String local_path_bmp = ImageUtil.urlToLocalPathBmp(url, "E:/invoice/originImage/");
		String local_path_jpg = ImageUtil.urlToJpg(local_path_bmp);
		String img_str = ImageUtil.GetImageStr(local_path_jpg);
		ans_map.put(Const.IMG_STR, "data:image/jpg;base64,"+ img_str);
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}

	// 接口8：打开监控台，发送一些重要的信息给前端
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/openConsole", method = RequestMethod.POST)
	public void openConsole(HttpServletRequest request, HttpServletResponse response)throws IOException{
		System.out.println("接收到打开监控台的请求");
		Map<String, Object> ans_map = new HashMap<>();
		invoiceService.openConsole(ans_map);
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}
	
	// 接口9：获取缓冲队列
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/recognizeWait", method = RequestMethod.POST)
	public void recognizeWait(HttpServletRequest request, HttpServletResponse response)throws IOException{
		System.out.println("接收到获取缓冲队列的请求");
        String ans_str = invoiceService.broadcastRecognizeWaitFirst();
		PrintWriter writer = response.getWriter();
		writer.write(ans_str);
		writer.flush();
		writer.close();
	}
	
	//特殊接口：更换图片url中的ip
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/changeImageUrlIp", method = RequestMethod.POST)
	public void changeUrlIp(HttpServletRequest request, HttpServletResponse response)throws IOException{
		invoiceService.changeImageUrlIp();
		PrintWriter writer = response.getWriter();
		Map<String, Object> ans_map = new HashMap<>();
		ans_map.put(Const.SUCCESS, "更新成功");
		System.out.println("更新成功");
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}

	
}
