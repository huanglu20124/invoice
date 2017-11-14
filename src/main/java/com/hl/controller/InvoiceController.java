package com.hl.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.hl.domain.LocalConfig;
import com.hl.domain.ModelAction;
import com.hl.domain.RecognizeAction;
import com.hl.domain.TestCase;
import com.hl.domain.User;
import com.hl.service.InvoiceService;
import com.hl.util.ImageUtil;
import com.hl.util.CheckUtil;
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

	@Resource(name = "localConfig")
	private LocalConfig localConfig;
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 测试专用
		Logger logger = Logger.getLogger(InvoiceController.class);
		logger.info("info测试");
		logger.debug("debug测试");
		logger.error("error测试");
	}

	//用户上传一张或多张图片，加入识别发票的请求队列，表单格式上传(请求enctype必须为multiple，可上传一张或多组)
	//参数为 user_id ,company_id, invoice_image_id, invoice_note
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/recognizeImage.action", method = RequestMethod.POST)
	public void recognizeNewInvoice(HttpServletRequest request,HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		System.out.println("接收到来自web端的识别发票请求");
		final Map<String, Object> ans_map = new HashMap<>();
		RecognizeAction recognizeAction = JSON.parseObject(request.getParameter("recognizeAction"),RecognizeAction.class);
		//根目录下存储的文件夹
		String dir = "image/data";
		// 获取全部文件
		CommonsMultipartResolver cmr = new CommonsMultipartResolver(request.getServletContext());
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);
		List<String>url_suffixs = new ArrayList<>();
		if (cmr.isMultipart(request)) {
			MultipartHttpServletRequest request2 = (MultipartHttpServletRequest) request;
			Iterator<String> files = request2.getFileNames();
			// 获取对象锁
			while (files.hasNext()) {
				MultipartFile file = request2.getFile(files.next());
				String uuidName = UUID.randomUUID().toString() + ".bmp";
				//分别传入根目录，对应的存储文件夹，文件名传入
				String url_suffix = ImageUtil.getUrlSuffix(localConfig.getImagePath(),dir, uuidName);	
				try {
					//先保存bmp
					File bmp_file = new File(localConfig.getImagePath() + url_suffix);
					FileOutputStream fos = new FileOutputStream(bmp_file);
					InputStream ins = file.getInputStream();
					IOUtil.inToOut(ins, fos);
					IOUtil.close(ins, fos);
					System.out.println("上传文件成功;");
					//再保存jpg
					ImageUtil.bmpTojpg(localConfig.getImagePath() + url_suffix);
					url_suffixs.add(url_suffix);
				} catch (Exception e) {
					e.printStackTrace();
					ans_map.put(Const.ERR, "上传文件失败");
				}
			}
			System.out.println("图片上传完毕");
			//图片全部上传完毕才调用service层
			invoiceService.addRecognizeInvoice(ans_map,recognizeAction,null, url_suffixs,thread_msg);
			
		} else {
			ans_map.put(Const.ERR, "请求格式有错误");
		}
		response.getWriter().write(JSON.toJSONString(ans_map));;
	}

	//ajax json  接收imgStr作为图片保存，POST请求
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/recognizeImgStr.customer", method = RequestMethod.POST)
	public void  recognizeImgStr(HttpServletRequest request, HttpServletResponse response,String img_str_list)throws IOException{
		System.out.println("接收到imgStr并识别的请求");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Map<String, Object>ans_map = new HashMap<>();
		//获得recognizeAction对象的Invoice数组
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);
		//根目录下存储的文件夹
		String dir = "image/data";
		List<String>url_suffixs = new ArrayList<>();
		RecognizeAction recognizeAction = JSON.parseObject(request.getParameter("recognizeAction"), RecognizeAction.class);
		TestCase testCase = JSON.parseObject(request.getParameter("testCase"),TestCase.class);
		List<String>img_strs = (List<String>) JSON.parse(img_str_list);
		if(img_str_list != null){
			System.out.println("收到" + img_strs.size() + "张图片" );
			for(int i = 0; i < img_strs.size(); i++){
				String imgStr = img_strs.get(i);
				String uuidName = null;
				if(imgStr.startsWith("data:image/bmp")){
					uuidName = UUID.randomUUID().toString() + ".bmp";
					//分别传入根目录，创建对应的存储文件夹，文件名传入
					String url_suffix = ImageUtil.getUrlSuffix(localConfig.getImagePath(),dir, uuidName);	
					try {
						//先保存bmp
						ImageUtil.generateImage(imgStr, localConfig.getImagePath() + url_suffix);
						System.out.println("上传文件成功;");
						//再保存jpg
						ImageUtil.bmpTojpg(localConfig.getImagePath() + url_suffix);
						//将url_suffix转为jpg
						url_suffix = ImageUtil.suffixToJpg(url_suffix);
						url_suffixs.add(url_suffix);
					} catch (Exception e) {
						e.printStackTrace();
						ans_map.put(Const.ERR, "上传文件失败");
					}
				}
				else if(imgStr.startsWith("data:image/jpg")){
					System.out.println("传入图片不是bmp格式！");
				}
			}
			//图片全部上传完毕才调用service层
			invoiceService.addRecognizeInvoice(ans_map,recognizeAction,testCase,url_suffixs,thread_msg);
			
		}
		response.getWriter().write(JSON.toJSONString(ans_map));
	}

	//点击一张图片，获得它的imgStr
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/getImgStr.action", method = RequestMethod.POST)
	public void getImgStr(HttpServletRequest request, HttpServletResponse response)throws IOException{
		System.out.println("接收到发送模板图片imgStr的请求");
		Map<String, Object> ans_map = new HashMap<>();
		String url = request.getParameter(Const.URL);
		System.out.println(url);
		String url_suffix = ImageUtil.getUrlSuffix(url);
		String local_path = localConfig.getImagePath() + url_suffix;
		//获得原图
		String original_path = local_path.replace("handle", "original");
		String img_str = ImageUtil.GetImageStr(original_path);
		ans_map.put(Const.IMG_STR, "data:image/jpg;base64,"+ img_str);
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}

	//打开监控台，发送一些重要的信息给前端
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/openConsole.action", method = RequestMethod.POST)
	@ResponseBody
	public String openConsole(HttpServletRequest request, HttpServletResponse response)throws IOException{
		System.out.println("接收到打开监控台的请求");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Map<String, Object> ans_map = new HashMap<>();
		String ans_str = invoiceService.openConsole();
		return ans_str;
	}
	
	//获取缓冲队列
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/recognizeWait.action", method = RequestMethod.POST)
	public void recognizeWait(HttpServletRequest request, HttpServletResponse response)throws IOException{
		System.out.println("接收到获取缓冲队列的请求");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
        String ans_str = invoiceService.broadcastRecognizeWaitFirst();
		PrintWriter writer = response.getWriter();
		writer.write(ans_str);
		writer.flush();
		writer.close();
	}
	
	//调整发票识别速度的请求
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/changeSpeed.action", method = RequestMethod.POST)
	public void changeSpeed(HttpServletRequest request, HttpServletResponse response) throws IOException{
		System.out.println("接收到调整发票识别速度的请求");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		Map<String, Object>ans_map = new HashMap<>();
		Integer user_id = new Integer(request.getParameter(Const.USER_ID));
		Integer thread_msg = (Integer) request.getServletContext().getAttribute(Const.THREAD_MSG);//获取上锁对象
		Integer delay = new Integer(request.getParameter("delay"));
		invoiceService.UpdateRecognizeSpeed(ans_map,user_id,delay,request.getServletContext());
		PrintWriter writer = response.getWriter();
		writer.write(JSON.toJSONString(ans_map));
		writer.flush();
		writer.close();
	}
	
	//特殊接口：更换模板图片url中的ip，已经废弃
	@CrossOrigin(origins = "*", maxAge = 36000000) // 配置跨域访问
	@RequestMapping(value = "/changeImageUrlIp.action", method = RequestMethod.POST)
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


	//jsp接口
	@RequestMapping(value = "/paint.action", method = RequestMethod.GET)
	public ModelAndView paintAction(){
		System.out.println("准备渲染paint界面");
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		ModelAndView modelAndView = new ModelAndView();
		if(user != null){
			modelAndView.addObject(user);
		}
		modelAndView.setViewName("paint");
		return modelAndView;
	}
	
	//jsp接口
	@RequestMapping(value = "/show.action", method = RequestMethod.GET)
	public ModelAndView showAction(){
		System.out.println("准备渲染show界面");
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		ModelAndView modelAndView = new ModelAndView();
		if(user != null){
			modelAndView.addObject(user);
		}
		modelAndView.setViewName("show");
		return modelAndView;
	}
	
	//jsp接口
	@RequestMapping(value = "/queue.action", method = RequestMethod.GET)
	public ModelAndView queueAction(){
		System.out.println("准备渲染queue界面");
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		ModelAndView modelAndView = new ModelAndView();
		if(user != null){
			modelAndView.addObject(user);
		}
		modelAndView.setViewName("queue");
		return modelAndView;
	}
	
	//jsp接口
	@RequestMapping(value = "/fault.action", method = RequestMethod.GET)
	public ModelAndView faultAction(){
		System.out.println("准备渲染fault界面");
		System.out.println("准备渲染show界面");
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		ModelAndView modelAndView = new ModelAndView();
		if(user != null){
			modelAndView.addObject(user);
		}
		modelAndView.setViewName("fault");
		return modelAndView;
	}
}
