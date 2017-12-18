package com.hl.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.socket.TextMessage;

import com.alibaba.fastjson.JSON;
import com.hl.dao.ActionDao;
import com.hl.dao.InvoiceDao;
import com.hl.dao.ModelDao;
import com.hl.dao.RedisDao;
import com.hl.dao.UserDao;
import com.hl.domain.Action;
import com.hl.domain.ActionQuery;
import com.hl.domain.LocalConfig;
import com.hl.domain.Model;
import com.hl.domain.ModelAction;
import com.hl.domain.ModelQuery;
import com.hl.domain.ResponseMessage;
import com.hl.domain.User;
import com.hl.exception.InvoiceException;
import com.hl.service.ModelService;
import com.hl.util.Const;
import com.hl.util.IOUtil;
import com.hl.util.ImageUtil;
import com.hl.util.MessageUtil;
import com.hl.util.SocketLoadTool;
import com.hl.util.TimeUtil;
import com.hl.websocket.SystemWebSocketHandler;

@Service("modelService")
public class ModelServiceImpl implements ModelService{
	@Resource(name = "redisDao")
	private RedisDao redisDao;

	@Resource(name = "invoiceDao")
	private InvoiceDao invoiceDao;
	
	@Resource(name = "modelDao")
	private ModelDao modelDao;
	
	@Resource(name = "userDao")
	private UserDao userDao;

	@Resource(name = "actionDao")
	private ActionDao actionDao;

	@Resource(name = "systemWebSocketHandler")
	private SystemWebSocketHandler systemWebSocketHandler;

	@Resource(name = "socketListener")
	private SocketLoadTool socketLoadTool;

	@Resource(name = "localConfig")
	private LocalConfig localConfig;
	
	// ajax处理web请求
	@Override
	public void addOrUpdateInvoiceModel(Map<String, Object>ans_map, ModelAction modelAction,Integer thread_msg){
		// 首先进行权限判断
		User user = userDao.getUserById(modelAction.getUser_id());
		// 添加新的发票模型
		// 1生成一条行为，插入action表，并获取返回的action_id
		Integer action_id = null;
		if(modelAction.getModel_id() == null){
			modelAction.setDescription("增加模板[]");
		}else {
			modelAction.setDescription("修改模板["+modelAction.getModel_id()+"]");
		}
		action_id = actionDao.addAction(modelAction);
		//补全modelAction的一些参数
		modelAction.setAction_id(action_id);
		modelAction.setAction_time(TimeUtil.getCurrentTime());
		modelAction.setUser_name(user.getUser_name());
		modelAction.setCompany_name(user.getCompany_name());
		redisDao.leftPush(Const.MANAGE_WAIT, action_id.toString());// 加入到操作队列
		// key为action_id,value为modelAction
		redisDao.addKey(action_id.toString(), JSON.toJSONString(modelAction));
		//添加到索引库
		//actionDao.solrAddUpdateAction(modelAction);
		// 3.获取两个队列长度
		Long recognize_size = redisDao.getWaitSize(); // 识别队列
		Long manage_size = redisDao.getManageSize();// 操作队列
		System.out.println("当前识别队列的数量为：" + recognize_size);
		System.out.println("当前操作队列的数量为：" + manage_size);
		if (recognize_size == 0l && manage_size == 1l) {
			// 10.通知切换线程
			synchronized (thread_msg) {
				thread_msg.notifyAll();
			}

		}
		ans_map.put(Const.SUCCESS, "已加入队列，等待算法服务器处理");
		System.out.println("增加或修改发票模板的请求已加入队列，等待算法服务器处理");
		ans_map.put("recognize_size", recognize_size);
		ans_map.put("manage_size", manage_size);
	}

	// ajax处理web请求
	@Override
	public void deleteInvoiceModel(Map<String, Object> ans_map, Integer user_id, Integer model_id, String user_ip,Integer thread_msg) {
		// 首先进行权限判断
		User user = userDao.getUserById(user_id);
		// 删除发票模板
		// 1生成一条行为，插入action表，并获取返回的action_id
		ModelAction action = new ModelAction();
		action.setMsg_id(3);
		action.setUser_id(user.getUser_id());
		action.setAction_time(TimeUtil.getCurrentTime());
		action.setUser_name(user.getUser_name());
		action.setCompany_id(user.getCompany_id());
		action.setCompany_name(user.getCompany_name());
		action.setDescription("删除模板["+model_id+"]");
		action.setUser_ip(user_ip);
		action.setModel_id(model_id);
		Integer action_id = actionDao.addAction(action);
		action.setAction_id(action_id);
		redisDao.leftPush(Const.MANAGE_WAIT, action_id.toString());// 加入到操作队列
		// key为action_id，value为图片url以及msg_id=2,还有json_model
		redisDao.addKey(action_id.toString(), JSON.toJSONString(action));
		// 3.获取两个队列长度
		Long recognize_size = redisDao.getWaitSize(); // 识别队列
		Long manage_size = redisDao.getManageSize();// 操作队列
		System.out.println("当前识别队列的数量为：" + recognize_size);
		System.out.println("当前操作队列的数量为：" + manage_size);
		if (recognize_size == 0l && manage_size == 1l) {
			// 4.通知切换线程
			synchronized (thread_msg) {
				thread_msg.notifyAll();
			}

		}
		ans_map.put(Const.SUCCESS, "已加入操作队列，等待算法服务器处理");
		ans_map.put("recognize_size", recognize_size);
		ans_map.put("manage_size", manage_size);
	}
	
	// websocket返回处理结果 msg_id = 2
	@Override
	public void broadcastAddNewModel(InputStream inputStream, ModelAction modelAction) {
		Integer action_id = modelAction.getAction_id();
		// 首先，更新action开始跑算法的时间
		modelAction.setAction_time(TimeUtil.getCurrentTime());
		//更新到索引库
		//actionDao.solrAddUpdateAction(modelAction);
		
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		String url = localConfig.getIp()  + modelAction.getFile_path() + "model.jpg";// 变为网络url
		// 处理增加模板的结果
		try {
			ResponseMessage message = MessageUtil.getMessage(inputStream);
			String json_str = message.getJson_str();
			// 1.解析json，先将结果直接返回给web端
			Map<String, Object> response_map = JSON.parseObject(json_str);
			int status = (int) response_map.get("status");
			String temp_str = message.getFinalMessage(action_id);
			Map<String, Object> temp_map = JSON.parseObject(temp_str);
			temp_map.put(Const.URL, url);
			String str = JSON.toJSONString(temp_map);
			System.out.println(str);
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(str),new int[]{3});
			// 同时得到model_id
			Integer model_id = null;
			// 2.成功的话，model表加入一个新model,model_id主键由算法端决定
			if (status == 0) {
				model_id = (int) response_map.get("id");
				//Integer image_size = ImageUtil.getImageSize(localConfig.getImagePath() + modelAction.getUrl_suffix());
				//更新部分信息
				modelAction.setModel_id(model_id);
				//modelAction.setImage_size(image_size);
				modelAction.setDescription("增加模板["+modelAction.getModel_id()+"]");
				modelDao.addModel(modelAction);
				//更新到索引库
				//actionDao.solrAddUpdateAction(modelAction);
			}
			// 4. 弹出队列头，删除key
			redisDao.pop(Const.MANAGE_WAIT);
			System.out.println(action_id + "弹出操作队列");
			if (status < 0) {
				// 识别失败，加入异常发票队列
				redisDao.leftPush(Const.EXCEPTION_WAIT, action_id + "");
			} else {
				redisDao.deleteKey(action_id + "");
			}

		} catch (IOException e) {
			e.printStackTrace();
			err_map.put(Const.ERR, "接受算法服务器数据异常");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)),new int[]{3});
		}

	}

	// websocket返回处理结果 msg_id = 4
	@Override
	public void broadcastUpdateModel(InputStream inputStream, ModelAction modelAction) {
		Integer action_id = modelAction.getAction_id();		
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		//String url = ImageUtil.suffixToJpg(localConfig.getIp() + modelAction.getUrl_suffix());// 将后缀变为网络url
		// 处理增加模板的结果
		try {
			ResponseMessage message = MessageUtil.getMessage(inputStream);
			String json_str = message.getJson_str();
			// 1.解析json，先将结果直接返回给web端
			Map<String, Object> response_map = JSON.parseObject(json_str);
			int status = (int) response_map.get("status");
			String temp_str = message.getFinalMessage(action_id);
			Map<String, Object> temp_map = JSON.parseObject(temp_str);
			//temp_map.put(Const.URL, url);
			Map<String, Object>json_model_map = JSON.parseObject(modelAction.getJson_model());
			temp_map.put(Const.JSON_MODEL, json_model_map);
			String str = JSON.toJSONString(temp_map);
			if (status == 0) {
				// 2.model表更新该model
				modelDao.updateModel(modelAction);
			}
			//更新数据库及索引库
			modelAction.setDescription("修改模板["+modelAction.getModel_id()+"]");
			actionDao.updateActionDescription(modelAction.getAction_id(), modelAction.getDescription());
			//actionDao.solrAddUpdateAction(modelAction);
			// 4. 弹出队列头，删除key
			redisDao.pop(Const.MANAGE_WAIT);
			System.out.println(action_id + "弹出操作队列");
			if (status < 0) {
				// 识别失败，加入异常发票队列
				redisDao.leftPush(Const.EXCEPTION_WAIT, action_id + "");
			} else {
				redisDao.deleteKey(action_id + "");
			}
			//延时一秒，将结果推送给前端
			try {
				systemWebSocketHandler.sendMessageToUsers(new TextMessage(str.getBytes("utf-8")),new int[]{3});
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			err_map.put(Const.ERR, "接受算法服务器数据异常");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)),new int[]{3});
		}

	}

	// websocket返回处理结果 msg_id = 3
	@Override
	public void broadcastDeleteModel(InputStream inputStream, ModelAction modelAction) {
		// 首先，更新action开始跑算法的时间
		Integer action_id = modelAction.getAction_id();
		Integer model_id = modelAction.getModel_id();
		//actionDao.solrAddUpdateAction(modelAction);
		
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		// 处理删除模板的结果
		try {
			ResponseMessage message = MessageUtil.getMessage(inputStream);
			String json_str = message.getJson_str();
			// 1.解析json，先将结果直接返回给web端
			Map<String, Object> response_map = JSON.parseObject(json_str);
			int status = (int) response_map.get("status");
			System.out.println(message + "---" + systemWebSocketHandler);
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)),new int[]{3});
			// 2.model表删除该model,其他携带该外键的行全部更新
			String url_suffix = null;
			// 这个url用来后面删除图片文件
			if (status == 0) {
				// 找到图片文件,删除全部本地文件
				url_suffix = modelDao.getModelUrl(model_id);
				ImageUtil.deleteAllModelImage(localConfig.getImagePath(), url_suffix);
				// 找到全部携带该model_id的invoice，设置外键为null
				invoiceDao.deleteInvoiceForeginModel(model_id);
				// 删除model
				modelDao.deleteModel(model_id);
				// 找到全部大于该model_id的行
				List<Integer> ids = modelDao.getBiggerModelId(model_id);
				for (Integer id : ids) {
					// 全部id减一
					modelDao.minusModelId(id);
				}
			}
			// 4. 弹出队列头，删除key
			redisDao.pop(Const.MANAGE_WAIT);
			System.out.println(action_id + "弹出操作队列");
			redisDao.deleteKey(action_id + "");
			if (status < 0) {
				err_map.put(Const.ERR, "删除失败");
			}

		} catch (IOException e) {
			e.printStackTrace();
			err_map.put(Const.ERR, "接受算法服务器数据异常");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)),new int[]{3});
		}
	}

	// websocket返回处理结果 msg_id = 5
	@Override
	public void broadcastClearModel(InputStream inputStream, Integer action_id) {
		// 一键清空所有模板
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		// 处理增加模板的结果
		try {
			ResponseMessage message = MessageUtil.getMessage(inputStream);
			String json_str = message.getJson_str();
			// 1.解析json，先将结果直接返回给web端
			Map<String, Object> response_map = JSON.parseObject(json_str);
			int status = (int) response_map.get("status");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)),new int[]{3});
			// 2.model表删除该model,其他携带该外键的行全部更新
			if (status == 0) {
				// 全部model_id!=null 的invoice，设置外键为null
				invoiceDao.deleteAllInvoiceForeginModel();
				// 得到全部model_url
				List<String> urls = modelDao.getAllModelUrl();
				// 删除全部本地图片文件
				for (String url : urls) {
					ImageUtil.deleteAllModelImage(localConfig.getImagePath(), url);
				}
				// 删除全部model
				modelDao.clearAllModel();
			}
			// 4. 弹出队列头，删除key
			redisDao.pop(Const.MANAGE_WAIT);
			System.out.println(action_id + "弹出操作队列");
			redisDao.deleteKey(action_id + "");
			if (status < 0) {
				err_map.put(Const.ERR, "删除失败");
			}

		} catch (IOException e) {
			e.printStackTrace();
			err_map.put(Const.ERR, "接受算法服务器数据异常");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)),new int[]{3});
		}
	}

	// ajax处理web请求
	@Override
	public void getAllModel(Map<String, Object> ans_map, Integer user_id, Integer page) {
		// 返回当前模板库全部信息,一次12条
		// 首先进行权限判断
		User user = userDao.getUserById(user_id);
		List<Model> model_list = modelDao.getTwelveModel(page);
		Collections.reverse(model_list);
		// 将url_suffix转为网络url
		for (Model model : model_list) {
			String url_suffix = model.getModel_url();
			model.setModel_url(localConfig.getIp() + url_suffix);
		}
		ans_map.put(Const.MODEL_LIST, model_list);
	}
	
	// ajax处理web请求
	@Override
	public void deleteAllModel(Map<String, Object> ans_map, Integer user_id, Integer thread_msg) {
		// 清空所有发票模板
		// 首先进行权限判断
		User user = userDao.getUserById(user_id);
		// 1生成一条行为，插入action表，并获取返回的action_id
		Action action = new Action();
		action.setUser_id(user.getUser_id());
		action.setAction_time(TimeUtil.getCurrentTime());
		action.setUser_name(user.getUser_name());
		action.setCompany_id(user.getCompany_id());
		action.setCompany_name(user.getCompany_name());
		action.setMsg_id(5);
		Integer action_id = actionDao.addAction(action);
		redisDao.leftPush(Const.MANAGE_WAIT, action_id.toString());// 加入到操作队列
		// key为action_id，value为图片url以及msg_id=2,还有json_model
		Map<String, Object> msg_map = new HashMap<>();
		msg_map.put(Const.MSG_ID, 5);
		redisDao.addKey(action_id.toString(), JSON.toJSONString(msg_map));
		// 3.获取两个队列长度
		Long recognize_size = redisDao.getWaitSize(); // 识别队列
		Long manage_size = redisDao.getManageSize();// 操作队列
		System.out.println("当前识别队列的数量为：" + recognize_size);
		System.out.println("当前操作队列的数量为：" + manage_size);
		if (recognize_size == 0l && manage_size == 1l) {
			// 4.通知切换线程
			synchronized (thread_msg) {
				thread_msg.notifyAll();
			}

		}
		ans_map.put(Const.SUCCESS, "已加入操作队列，等待算法服务器处理");
		ans_map.put("recognize_size", recognize_size);
		ans_map.put("manage_size", manage_size);
	}
	
	// ajax处理web请求，已废弃
	@Override
	public void rewriteJsonModel() throws Exception {
		SAXReader saxReader = new SAXReader();
		String path = localConfig.getDataBasePath();
		Document document = saxReader.read(new File(path));
		// 根元素
		Element root = document.getRootElement();
		System.out.println("获取根节点:" + root.getName());
		// 获取所有子元素
		Element database_current_size = root.element("Database_current_size");
		// 得到模板数量
		Integer num = new Integer((String) database_current_size.getData());
		System.out.println("得到模板数量 = " + num);
		Element element_invoice_info = root.element("invoice_info");
		List<Element> elements = element_invoice_info.elements("_");
		List<String> json_model_list = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			// 一个模板一个json_model,最终存在json_model_list里面
			Map<String, Object> json_model_map = new HashMap<>();
			Map<String, Object> global_setting_map = new HashMap<>();

			Element element_root = elements.get(i);

			String label = element_root.element("label").getText().replaceAll("\"", "");
			String quota = element_root.elementText("quota");
			global_setting_map.put("label", label);
			global_setting_map.put("quota", quota);
			json_model_map.put("global_setting", global_setting_map);

			Element element_info_area = element_root.element("info_area");
			List<Element> roots_info_area = element_info_area.elements("_");
			int area_num = roots_info_area.size();
			System.out.println("area_num = " + area_num);
			// 现在只有两个区域
			area_num = 2;
			for (int j = 0; j < area_num; j++) {
				Element element = roots_info_area.get(j);// 得到_
				// 开始遍历每个区域
				Map<String, Object> area_map = new HashMap<>();

				Integer x = new Integer(element.elementText("absolute_x"));
				area_map.put("x", x);
				Integer y = new Integer(element.elementText("absolute_y"));
				area_map.put("y", y);
				Integer height = new Integer(element.elementText("height"));
				area_map.put("height", height);
				Integer width = new Integer(element.elementText("length"));
				area_map.put("width", width);
				Integer remove_line = new Integer(element.elementText("remove_line"));
				area_map.put("remove_line", remove_line);
				Integer remove_stamp = new Integer(element.elementText("remove_stamp"));
				area_map.put("remove_stamp", remove_stamp);
				String keywords = element.elementText("keywords").replaceAll("\"", "");
				;
				area_map.put("keywords", keywords);
				if (j == 0) {
					// money node
					json_model_map.put("money", area_map);
				} else if (j == 1) {
					// head node
					json_model_map.put("head", area_map);
				} else if (j == 7) {
					json_model_map.put("invoice_id", area_map);
				}

			}
			json_model_list.add(JSON.toJSONString(json_model_map));
		}
		// 根据顺序，重新写入数据库
		for (int model_id = 0; model_id < num; model_id++) {
			modelDao.updateModelJsonModel(model_id + 1, json_model_list.get(model_id));
		}

	}


	//通过label模糊查询发票
	@Override
	public ModelQuery searchModelLabel(Integer page,Integer user_id, String keyword) {
		ModelQuery modelQuery = new ModelQuery();
		if(keyword != null){
			List<Model>model_list = modelDao.searchModelLabel(page, keyword); 
			// 将url_suffix转为网络url
			for (Model model : model_list) {
				String url_suffix = model.getModel_url();
				model.setModel_url(localConfig.getIp() + url_suffix);
			}
			modelQuery.setModel_list(model_list);
		}
		return modelQuery;	
	}

	
	//上传单张模板原图的请求,type=0的话，则需要返回img_str;type=1的话，不需要返回img_str
	@Override
	public String uploadModelOrigin(MultipartFile[]files,Integer type,HttpSession session) throws InvoiceException{
		final Map<String, Object> ans_map = new HashMap<>();
		//建立临时文件夹,"temp"+时间字符串， 文件夹的名字放到session中，模板完成之后，文件夹名字规范化 
		String time_str = TimeUtil.getFileCurrentTime();
		String dir = localConfig.getImagePath() + "image/model/temp_"+ time_str + "/";
		File save_folder = new File(dir);
		if (save_folder.exists() == false) {
			save_folder.mkdirs();
		}
		//先获取原文件夹有多少个原图，按序号来命名
		String[]origin_files = save_folder.list();
		Integer k = origin_files.length + 1;
		for(int i = 0 ; i < files.length; i++){
			MultipartFile multipartFile = files[i];
			String origin_file_name = multipartFile.getOriginalFilename();
			String save_name = null;
			System.out.println("原始文件名为" + origin_file_name);
			//找到后缀名
			int flag = origin_file_name.lastIndexOf(".") + 1;
			String tail = origin_file_name.substring(flag, origin_file_name.length());
			if(tail.equals("jpg")){
				save_name = (k + ".jpg");
				k++;
			}
			else if (tail.equals("png")) {
				save_name = (k + ".png");
				k++;
			}
			else if (tail.equals("bmp")) {
				save_name = (k + ".bmp");
				k++;
			}
			else {
				throw new InvoiceException("提交的文件格式不正确！");
			}
			if(save_name != null){
				try {
					multipartFile.transferTo(new File(dir+save_name));
				}catch (IOException e) {
					throw new InvoiceException("保存文件出错！");
				}
				System.out.println("保存文件" + save_name +"成功");
			}
			if(i == 0 && type == 0 && save_name != null){
				//第一张图的话，要返回img_str以及它的url（用于前端展示）
				ans_map.put("img_str", "data:image/"+ tail +";base64," 
				+ ImageUtil.GetImageStr(dir+"/"+save_name));
				ans_map.put("url", localConfig.getIp()+"image/model/temp_"+time_str+save_name);
			}
		}
		ans_map.put("success", "上传文件成功");
		//将file_path存储在session中
		session.setAttribute("file_path","image/model/temp_"+ time_str + "/");
		return JSON.toJSONString(ans_map);
	}
}
