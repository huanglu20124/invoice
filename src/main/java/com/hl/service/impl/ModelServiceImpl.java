package com.hl.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hl.dao.ActionDao;
import com.hl.dao.InvoiceDao;
import com.hl.dao.ModelDao;
import com.hl.dao.RedisDao;
import com.hl.dao.UserDao;
import com.hl.domain.Action;
import com.hl.domain.LocalConfig;
import com.hl.domain.Model;
import com.hl.domain.ModelAction;
import com.hl.domain.ModelQuery;
import com.hl.domain.ResponseMessage;
import com.hl.domain.SimpleResponse;
import com.hl.domain.User;
import com.hl.exception.InvoiceException;
import com.hl.service.ModelService;
import com.hl.util.Const;
import com.hl.util.ImageUtil;
import com.hl.util.IpUtil;
import com.hl.util.MessageUtil;
import com.hl.util.SocketLoadTool;
import com.hl.util.TimeUtil;
import com.hl.websocket.SystemWebSocketHandler;

@Service("modelService")
public class ModelServiceImpl implements ModelService {
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

	private static Logger logger = Logger.getLogger(ModelServiceImpl.class);

	// ajax处理web请求
	@Override
	public String addModel(HttpServletRequest request) throws InvoiceException {
		String action_str = request.getParameter("modelAction");
		String img_str = request.getParameter("img_str");

		System.out.println("modelAction = " + action_str);
		ModelAction modelAction = JSON.parseObject(action_str, ModelAction.class);

		// 提取出模板名称
		@SuppressWarnings("unchecked")
		Map<String, Object> global_setting_map = (Map<String, Object>) modelAction.getJson_model()
				.get("global_setting");
		String label = (String) global_setting_map.get("label");
		modelAction.setModel_label(label);

		// 从获取文件路径
		String origin_file_path = modelAction.getFile_path();

		if (origin_file_path == null)
			throw new InvoiceException("服务器错误，session中找不到file_path");
		String file_path = "image/model/" + modelAction.getModel_label() + "-" + TimeUtil.getFileCurrentTime() + "/";
		// 重命名文件夹
		File origin = new File(localConfig.getImagePath() + origin_file_path);
		origin.renameTo(new File(localConfig.getImagePath() + file_path));
		origin.delete();
		modelAction.setFile_path(file_path);// 关键，要设置给modelAction
		// 生成模板框图
		if (ImageUtil.generateImage(img_str, localConfig.getImagePath() + file_path, "model.jpg") == true) {
			System.out.println("上传文件成功");
			modelAction.setImage_size(ImageUtil.getImageSize(localConfig.getImagePath() + file_path + "model.jpg"));
		} else {
			System.out.println("上传文件失败");
			throw new InvoiceException("保存图片失败");
		}
		// 增加模板(将单张模板的请求的情况包括进去)
		modelAction.setMsg_id(6);
		modelAction.setUser_ip(IpUtil.getIpAddr(request));
		// 首先进行权限判断
		User user = userDao.getUserById(modelAction.getUser_id());
		// 添加新的发票模型
		// 1生成一条行为，插入action表，并获取返回的action_id
		Integer action_id = null;
		modelAction.setDescription("增加模板[]");
		action_id = actionDao.addAction(modelAction);
		// 补全modelAction的一些参数
		modelAction.setAction_id(action_id);
		modelAction.setAction_time(TimeUtil.getCurrentTime());
		modelAction.setUser_name(user.getUser_name());
		modelAction.setCompany_name(user.getCompany_name());
		String origin_url = localConfig.getIp() + file_path + getOriginalUrl(file_path);
		String model_url = localConfig.getIp() + file_path + "model.jpg";
		modelAction.setOrigin_url(origin_url);
		modelAction.setModel_url(model_url);

		// 获得批处理id，如果为null的话，说明是第一次，需要创建；如果不是null的话，则说明要加入到批处理队里中，key为batch_id
		String batch_id = null;
		if ((batch_id = modelAction.getBatch_id()) == null) {
			batch_id = UUID.randomUUID().toString();
			modelAction.setBatch_id(batch_id);
		}
		redisDao.leftPush(batch_id, action_id.toString());// 加入到增加模板队列,
		// key为action_id,value为modelAction
		redisDao.addKey(action_id.toString(), JSON.toJSONString(modelAction));
		// 添加到索引库
		// actionDao.solrAddUpdateAction(modelAction);
		Map<String, Object> ans_map = new HashMap<>();
		ans_map.put(Const.SUCCESS, "已加入队列，等待算法服务器处理");
		ans_map.put("batch_id", batch_id);
		ans_map.put("origin_url", origin_url);
		ans_map.put("model_url", model_url);
		ans_map.put("action_id", modelAction.getAction_id());
		ans_map.put("json_model", JSON.toJSONString(modelAction.getJson_model()));
		// 最后一件事，把batch_id保存到session中
		request.getSession().setAttribute("batch_id", batch_id);
		System.out.println("增发票模板的请求已加入队列，等待算法服务器处理");
		logger.info("增加模板的返回:\r\n" +JSON.toJSONString(ans_map));
		return JSON.toJSONString(ans_map);
	}

	// ajax处理web请求
	@Override
	public String updateModel(HttpServletRequest request, Integer thread_msg) throws InvoiceException {
		// 修改模板
		String action_str = request.getParameter("modelAction");
		String img_str = request.getParameter("img_str");
		System.out.println("modelAction = " + action_str);
		ModelAction modelAction = JSON.parseObject(action_str, ModelAction.class);
		Integer model_id = modelAction.getModel_id();
		if (model_id == null)
			throw new InvoiceException("修改模板失败，model_id为空");
		// 提取出模板名称
		@SuppressWarnings("unchecked")
		Map<String, Object> global_setting_map = (Map<String, Object>) modelAction.getJson_model()
				.get("global_setting");
		String label = (String) global_setting_map.get("label");
		modelAction.setModel_label(label);
		// 从数据库查询原文件仓库路径
		String model_url = modelDao.getModelUrl(model_id);
		int flag = model_url.lastIndexOf("/");
		String file_path = model_url.substring(0, flag + 1);
		File dir = new File(localConfig.getImagePath() + file_path);
		if (!dir.exists())
			throw new InvoiceException("修改模板失败，原图片文件不存在！");
		// 更新文件夹名字
/*		String file_path = "image/model/" + label + "_" + TimeUtil.getFileCurrentTime() + "/";
		logger.info(localConfig.getImagePath() + file_path);
		dir.renameTo(new File(localConfig.getImagePath() + file_path));*/
		modelAction.setFile_path(file_path);
		modelAction.setModel_url(file_path + "model.jpg");
		// 生成模板框图，先将原文件删除
		File originImage = new File(localConfig.getImagePath() + file_path + "model.jpg");
		if (originImage.exists())
			originImage.delete();
		if (ImageUtil.generateImage(img_str, localConfig.getImagePath() + file_path, "model.jpg") == true) {
			System.out.println("上传文件成功");
			modelAction.setImage_size(ImageUtil.getImageSize(localConfig.getImagePath() + file_path + "model.jpg"));
		} else {
			System.out.println("上传文件失败");
			throw new InvoiceException("保存图片失败");
		}
		// 修改模板
		modelAction.setMsg_id(4);
		modelAction.setUser_ip(IpUtil.getIpAddr(request));
		// 首先进行权限判断
		User user = userDao.getUserById(modelAction.getUser_id());
		// 添加新的发票模型
		// 1生成一条行为，插入action表，并获取返回的action_id
		Integer action_id = null;
		modelAction.setDescription("修改模板[]");
		action_id = actionDao.addAction(modelAction);
		// 补全modelAction的一些参数
		modelAction.setAction_id(action_id);
		modelAction.setAction_time(TimeUtil.getCurrentTime());
		modelAction.setUser_name(user.getUser_name());
		modelAction.setCompany_name(user.getCompany_name());
		// 加入到操作队列
		redisDao.leftPush(Const.MANAGE_WAIT, action_id.toString());
		redisDao.addKey(action_id.toString(), JSON.toJSONString(modelAction));
		Long recognize_size = redisDao.getWaitSize(); // 识别队列
		Long manage_size = redisDao.getManageSize();// 操作队列
		if (recognize_size == 0l && manage_size == 1l) {
			// 10.通知切换线程
			synchronized (thread_msg) {
				thread_msg.notifyAll();
			}
		}
		//返回model_url
		Map<String, Object> map = new HashMap<>();
		map.put("success", "操作成功，等待服务器响应");
		map.put("model_url", localConfig.getIp() + file_path + "model.jpg");
		return JSON.toJSONString(map);
	}

	// ajax处理web请求
	@Override
	public String deleteModel(Integer user_id, Integer model_id, String user_ip, Integer thread_msg) {
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
		action.setDescription("删除模板[" + model_id + "]");
		action.setUser_ip(user_ip);
		action.setModel_id(model_id);
		Integer action_id = actionDao.addAction(action);
		action.setAction_id(action_id);
		// 从数据库查询原文件仓库路径
		String model_url = modelDao.getModelUrl(model_id);
		int flag = model_url.lastIndexOf("/");
		String file_path = model_url.substring(0, flag + 1);
		action.setFile_path(file_path);
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
		Map<String, Object> map = new HashMap<>();
		map.put(Const.SUCCESS, "已加入操作队列，等待算法服务器处理");
		return JSON.toJSONString(map);
	}

	@Override
	public void broadcastAddModelMul(InputStream inputStream, List<ModelAction> batch_list) {
		logger.info("处理多张发票：准备接收算法端消息");
		// 批处理增加模板的结果
		ResponseMessage message = null;
		try {
			message = MessageUtil.getMessage(inputStream);
		} catch (IOException e) {
			System.out.println("接收消息时异常");
			e.printStackTrace();
		}
		String json_str = message.getJson_str();

		// 要返回给前端的信息
		JSONObject object = JSONObject.parseObject(json_str);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> ans_list = (List<Map<String, Object>>) object.get("response");
		int k = 0;
		for (ModelAction modelAction : batch_list) {
			Map<String, Object> map = ans_list.get(k);
			// 首先，更新全部action开始跑算法的时间
			modelAction.setAction_time(TimeUtil.getCurrentTime());
			map.put("origin_url", modelAction.getOrigin_url());
			map.put("model_url", modelAction.getModel_url());
			map.put("model_label", modelAction.getModel_label());
			map.put("model_register_time", TimeUtil.getCurrentTime());
			map.put("image_size", modelAction.getImage_size());
			k++;
			// 准备写入数据库
			int status = (int) map.get("status");
			// 成功的话，model表加入一个新model,model_id主键由算法端决定
			if (status == 0) {
				int model_id = (int) map.get("id");
				map.put("model_id", model_id);
				// 更新部分信息
				modelAction.setModel_id(model_id);
				modelDao.addModel(modelAction);
				actionDao.updateActionDescription(modelAction.getAction_id(),
						"增加模板[" + modelAction.getModel_id() + "]");
				// 更新到索引库
				// actionDao.solrAddUpdateAction(modelAction);
				//从redis中删除该action
				redisDao.deleteKey(modelAction.getAction_id().toString());
			}
		}
		Map<String, Object>ans_map = new HashMap<>();
		ans_map.put("list", ans_list);
		ans_map.put("msg_id", 6);
		
		// 得到batch_id
		String batch_id = batch_list.get(0).getBatch_id();
		//弹出队列头该modelAction
		redisDao.pop(Const.MANAGE_WAIT);
		//删除批处理的key
		redisDao.deleteKey(batch_id);
		
		// 将批处理结果发送给前端
		systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(ans_map)), new int[] {3});

	}

	// websocket返回处理结果 msg_id = 4
	@Override
	public void broadcastUpdateModel(InputStream inputStream, ModelAction modelAction) {
		Integer action_id = modelAction.getAction_id();
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		String url = localConfig.getIp() + modelAction.getFile_path() + "model.jpg";
		// 处理增加模板的结果
		try {
			ResponseMessage message = MessageUtil.getMessage(inputStream);
			String json_str = message.getJson_str();
			// 1.解析json，先将结果直接返回给web端
			Map<String, Object> response_map = JSON.parseObject(json_str);
			int status = (int) response_map.get("status");
			String temp_str = message.getFinalMessage(action_id);
			Map<String, Object> temp_map = JSON.parseObject(temp_str);
			logger.info("返回给服务器的model.jpg=" + url);
			temp_map.put("model_url", url);
			temp_map.put("origin_url", localConfig.getIp() + modelAction.getFile_path() + "0.jpg");
			Map<String, Object> json_model = modelAction.getJson_model();
			temp_map.put(Const.JSON_MODEL, json_model);
			if (status == 0) {
				// 2.model表更新该model
				logger.info("修改模板成功！，准备写入数据库");
				modelDao.updateModel(modelAction);
				temp_map.put("success", "修改模板成功");
			}
			// 更新数据库及索引库
			modelAction.setDescription("修改模板[" + modelAction.getModel_id() + "]");
			actionDao.updateActionDescription(modelAction.getAction_id(), modelAction.getDescription());
			// actionDao.solrAddUpdateAction(modelAction);
			// 延时一秒，将结果推送给前端
			try {
				systemWebSocketHandler.sendMessageToUsers(new TextMessage((JSON.toJSONString(temp_map)).getBytes("utf-8")), new int[] { 3 });
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			err_map.put(Const.ERR, "接受算法服务器数据异常");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)), new int[] { 3 });
		}

	}

	// websocket返回处理结果 msg_id = 3
	@Override
	public void broadcastDeleteModel(InputStream inputStream, ModelAction modelAction) {
		// 首先，更新action开始跑算法的时间
		Integer action_id = modelAction.getAction_id();
		Integer model_id = modelAction.getModel_id();
		// actionDao.solrAddUpdateAction(modelAction);
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		// 处理删除模板的结果
		try {
			ResponseMessage message = MessageUtil.getMessage(inputStream);
			String json_str = message.getJson_str();
			// 1.解析json，先将结果直接返回给web端
			Map<String, Object> response_map = JSON.parseObject(json_str);
			int status = (int) response_map.get("status");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)),
					new int[] { 3 });
			// model表删除该model,其他携带该外键的行全部更新
			// 这个url用来后面删除图片文件
			if (status == 0) {
				// 找到图片文件,删除全部本地文件
				String file_path = modelAction.getFile_path();
				System.out.println("要删除的文件路径" + file_path);
				File dir = new File(localConfig.getImagePath() + file_path);
				if(dir.exists()){
					deleteDir(dir);
				}
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
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)), new int[] { 3 });
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
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)),
					new int[] { 3 });
			// 2.model表删除该model,其他携带该外键的行全部更新
			if (status == 0) {
				// 全部model_id!=null 的invoice，设置外键为null
				invoiceDao.deleteAllInvoiceForeginModel();
				// 得到全部model_url
				List<String> urls = modelDao.getAllModelUrl();
				// 删除全部本地图片文件
				for (String url : urls) {
					// 设置原图url
					int flag = url.lastIndexOf("/");
					String dir_path = localConfig.getImagePath() +  url.substring(0, flag + 1);
					System.out.println("要删除的文件夹为" + dir_path);
					deleteDir(new File(dir_path));
				}
				// 删除全部model
				modelDao.clearAllModel();
			}

			if (status < 0) {
				err_map.put(Const.ERR, "删除失败");
			}

		} catch (IOException e) {
			e.printStackTrace();
			err_map.put(Const.ERR, "接受算法服务器数据异常");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)), new int[] { 3 });
		}
	}

	// ajax处理web请求
	@Override
	public void getAllModel(Map<String, Object> ans_map, Integer user_id, Integer page) {
		// 返回当前模板库全部信息,一次12条
		List<Model> model_list = modelDao.getTwelveModel(page);
		Collections.reverse(model_list);
		// 将url_suffix转为网络url
		for (Model model : model_list) {
			String url_suffix = model.getModel_url();
			model.setModel_url(localConfig.getIp() + url_suffix);
			// 设置原图url
			int flag = url_suffix.lastIndexOf("/");
			String dir_path = url_suffix.substring(0, flag + 1);
			model.setOrigin_url(localConfig.getIp() + dir_path + getOriginalUrl(dir_path));
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
		@SuppressWarnings("unchecked")
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
			@SuppressWarnings("unchecked")
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

	// 通过label模糊查询发票
	@Override
	public ModelQuery searchModelLabel(Integer page, Integer user_id, String keyword) {
		ModelQuery modelQuery = new ModelQuery();
		if (keyword != null) {
			List<Model> model_list = modelDao.searchModelLabel(page, keyword);
			// 将url_suffix转为网络url
			for (Model model : model_list) {
				String url_suffix = model.getModel_url();
				model.setModel_url(localConfig.getIp() + url_suffix);
			}
			modelQuery.setModel_list(model_list);
		}
		return modelQuery;
	}

	// 上传单张模板原图的请求,type=0的话，则需要返回img_str;type=1的话，不需要返回img_str
	@Override
	public String uploadModelOrigin(MultipartFile[] files, Integer type, String file_path) throws InvoiceException {
		final Map<String, Object> ans_map = new HashMap<>();
		// type=0的话，则是上传原图，开始操作模板，所以要建立文件夹
		logger.info("type=" + type + " file_path=" + file_path);
		if (type == 0) {
			// 建立临时文件夹路径,"temp"+时间字符串， 文件夹的名字放到session中，模板完成之后，文件夹名字规范化
			String time_str = TimeUtil.getFileCurrentTime();
			file_path = "image/model/temp_" + time_str + "/";
			// 将file_path返回给客户端
			ans_map.put("file_path", "image/model/temp_" + time_str + "/");
		}
		if (file_path == null || file_path.equals(""))
			throw new InvoiceException("file_path");
		File save_folder = new File(localConfig.getImagePath() + file_path);
		if (save_folder.exists() == false) {
			save_folder.mkdirs();
		}
		// 先获取原文件夹有多少个原图，按序号来命名
		String[] origin_files = save_folder.list();
		Integer k = origin_files.length;
		for (int i = 0; i < files.length; i++) {
			MultipartFile multipartFile = files[i];
			String origin_file_name = multipartFile.getOriginalFilename();
			String save_name = null;
			System.out.println("原始文件名为" + origin_file_name);
			// 找到后缀名
			int flag = origin_file_name.lastIndexOf(".") + 1;
			String tail = origin_file_name.substring(flag, origin_file_name.length());
			if (tail.equals("jpg")) {
				save_name = (k + ".jpg");
				k++;
			} else if (tail.equals("png")) {
				save_name = (k + ".png");
				k++;
			} else if (tail.equals("bmp")) {
				save_name = (k + ".bmp");
				k++;
			} else {
				throw new InvoiceException("提交的文件格式不正确！");
			}
			if (save_name != null) {
				try {
					multipartFile.transferTo(new File(localConfig.getImagePath() + file_path + save_name));
				} catch (IOException e) {
					e.printStackTrace();
					throw new InvoiceException("保存文件出错！");
				}
				System.out.println("保存文件" + save_name + "成功");
			}
			if (i == 0 && type == 0 && save_name != null) {
				// 第一张图的话，要返回img_str以及它的url（用于前端展示）
				ans_map.put("img_str", "data:image/" + tail + ";base64,"
						+ ImageUtil.GetImageStr(localConfig.getImagePath() + file_path + save_name));
				ans_map.put("origin_url", localConfig.getIp() + file_path + save_name);
				System.out.println("返回img_str");
			}
		}
		ans_map.put("success", "上传文件成功");
		return JSON.toJSONString(ans_map);
	}

	// 将该队列的modelAction加到manage队列中，通知线程切换
	@Override
	public String pushBatchModel(String batch_id, Integer thread_msg) throws InvoiceException {
		
		// 得到batch_id队列全部action_id
		List<String> action_ids = redisDao.getRangeId(batch_id.toString());
		if (action_ids == null || action_ids.size() == 0)
			throw new InvoiceException("没有模板操作要加入队列");
		// 只在manage队列中加入第一张，后面的全部从增加模板缓冲队列中拿
		redisDao.leftPush(Const.MANAGE_WAIT, action_ids.get(0).toString());
		// redisDao.deleteKey(batch_id.toString());
		Long recognize_size = redisDao.getWaitSize(); // 识别队列
		Long manage_size = redisDao.getManageSize();// 操作队列
		if (recognize_size == 0l && manage_size == 1l) {
			// 10.通知切换线程
			synchronized (thread_msg) {
				thread_msg.notifyAll();
			}
		}
		Map<String, Object> map = new HashMap<>();
		map.put("success", "操作成功，等待服务器响应");
		return JSON.toJSONString(manage_size);
	}

	// 取消操作，删除文件
	@Override
	public String cancelAddModel(String file_path) {
		logger.info("file_path=" + file_path);
		if (file_path != null) {
			File dir = new File(localConfig.getImagePath() + file_path);
			logger.info(localConfig.getImagePath() + file_path);
			if (dir.exists()) {
				deleteDir(dir);
				logger.info("要删除原文件！");
			} else {
				logger.info("要删除的文件不存在！");
			}
		}
		return "{}";
	}

	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			// 递归删除目录中的子目录下
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	// 根据session中存储的batch_id获取队列
	@Override
	public String getModelQueue(String batch_id) {
		List<String> action_ids = redisDao.getRangeId(batch_id);
		List<ModelAction> list = new ArrayList<>();
		for (String action_id : action_ids) {
			ModelAction modelAction = JSON.parseObject((String) redisDao.getValue(action_id), ModelAction.class);
			list.add(modelAction);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("list", list);
		return JSON.toJSONString(map);
	}

	@Override
	public String getImgStr(String url) throws InvoiceException {
		Map<String, Object> ans_map = new HashMap<>();
		logger.info("图片url=" + url);
		if(url != null){
			String url_suffix = ImageUtil.getUrlSuffix(url);
			String local_path = localConfig.getImagePath() + url_suffix;
			File temp = new File(local_path);
			if (!temp.exists())
				throw new InvoiceException("原图片不存在，返回bas64图片编码失败！");
			// 获得原图
			String img_str = ImageUtil.GetImageStr(local_path);
			// 找到后缀名
			int flag = url.lastIndexOf(".") + 1;
			String tail = url.substring(flag, url.length());
			ans_map.put(Const.IMG_STR, "data:image/" + tail + ";base64," + img_str);
		}else {
			ans_map.put("err", "url为空");
		}
		return JSON.toJSONString(ans_map);
	}

	private String getOriginalUrl(String file_path) {
		// 得到第一张原图的文件名
		File dir = new File(localConfig.getImagePath() + file_path);
		String[] list = dir.list();
		if(list != null){
			for (String temp : list) {
				if (!temp.contains("model")) {
					// 找到第一张不是模板的文件名字
					return temp;
				}
			}
		}
		return "1.jpg";
	}

	@Override
	public String broadcastAddModelSingle(InputStream inputStream, ModelAction modelAction) {
		logger.info("处理单张发票：准备接收算法端消息");
		// 批处理增加模板的结果
		ResponseMessage message = null;
		try {
			message = MessageUtil.getMessage(inputStream);
		} catch (IOException e) {
			System.out.println("接收消息时异常");
			e.printStackTrace();
		}
		String json_str = message.getJson_str();

		// 要返回给前端的信息
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) JSON.parse(json_str);
		// 首先，更新全部action开始跑算法的时间
		modelAction.setAction_time(TimeUtil.getCurrentTime());
		map.put("origin_url", modelAction.getOrigin_url());
		map.put("model_url", modelAction.getModel_url());
		map.put("model_label", modelAction.getModel_label());
		map.put("model_register_time", TimeUtil.getCurrentTime());
		map.put("image_size", modelAction.getImage_size());
		// 准备写入数据库
		int status = (int) map.get("status");
		// 成功的话，model表加入一个新model,model_id主键由算法端决定
		if (status == 0) {
			int model_id = (int) map.get("id");
			map.put("model_id", model_id);
			map.put("msg_id", 2);
			// 更新部分信息
			modelAction.setModel_id(model_id);
			modelDao.addModel(modelAction);
			actionDao.updateActionDescription(modelAction.getAction_id(), "增加模板[" + modelAction.getModel_id() + "]");
			// 更新到索引库
			// actionDao.solrAddUpdateAction(modelAction);
			//删除该action以及batch_id
			redisDao.pop(Const.MANAGE_WAIT);
			redisDao.deleteKey(modelAction.getAction_id().toString());
			redisDao.deleteKey(modelAction.getBatch_id().toString());
			
		}
		// 将批处理结果发送给前端
		systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(map)), new int[] {3});
		return null;
	}

	@Override
	public String deleteCacheModel(Integer action_id) {
		//从redis中找到该action
		ModelAction modelAction = JSON.parseObject((String)redisDao.getValue(action_id.toString()),ModelAction.class);
		// 找到图片文件,删除全部本地文件
		String file_path = modelAction.getFile_path();
		System.out.println("要删除的文件路径" + file_path);
		File dir = new File(localConfig.getImagePath() + file_path);
		if(dir.exists()){
			deleteDir(dir);
		}
		//从缓冲队列中删除
		redisDao.removeListIndex(modelAction.getBatch_id(), action_id.toString());
		//删除该value
		redisDao.deleteKey(action_id.toString());
		return JSON.toJSONString(new SimpleResponse("删除成功", null));
	}

	@Override
	public String updateCacheModel(ModelAction modelAction, String img_str) throws InvoiceException{
		//修改缓冲队列中的一张模板的请求
		//得到原modelAction
		if (modelAction.getAction_id() == null)
			throw new InvoiceException("修改模板失败，action_id为空");
		ModelAction origin = JSON.parseObject((String)redisDao.getValue(modelAction.getAction_id().toString()),
				ModelAction.class);
		// 提取出模板名称
		@SuppressWarnings("unchecked")
		Map<String, Object> global_setting_map = (Map<String, Object>) modelAction.getJson_model()
				.get("global_setting");
		String label = (String) global_setting_map.get("label");
		
		//把新属性赋值给origin
		origin.setModel_label(label);
		origin.setJson_model(modelAction.getJson_model());
		
		//原文件仓库路径
		File dir = new File(localConfig.getImagePath() + origin.getFile_path());
		if (!dir.exists())
			throw new InvoiceException("修改模板失败，原图片文件不存在！");
		// 更新文件夹名字
		String file_path = origin.getFile_path();
/*		String file_path = label + "_" + TimeUtil.getFileCurrentTime() + "/";
		dir.renameTo(new File(localConfig.getImagePath() + file_path));
		modelAction.setFile_path(file_path);*/
		modelAction.setModel_url(localConfig.getIp() + file_path + "model.jpg");
		modelAction.setOrigin_url(localConfig.getIp() + file_path + "0.jpg");
		// 生成模板框图，先将原文件删除
		File originImage = new File(localConfig.getImagePath() + file_path + "model.jpg");
		if (originImage.exists())
			originImage.delete();
		if (ImageUtil.generateImage(img_str, localConfig.getImagePath() + file_path, "model.jpg") == true) {
			System.out.println("上传文件成功");
			modelAction.setImage_size(ImageUtil.getImageSize(localConfig.getImagePath() + file_path + "model.jpg"));
		} else {
			System.out.println("上传文件失败");
			throw new InvoiceException("保存图片失败");
		}
		//覆盖原来的action
		redisDao.addKey(origin.getAction_id().toString(), JSON.toJSONString(modelAction));
		Map<String, Object> map = new HashMap<>();
		map.put("success", "修改成功");
		map.put("model_url", localConfig.getIp() + file_path + "model.jpg");
		map.put("origin_url", localConfig.getIp() + file_path + "0.jpg");
		return JSON.toJSONString(map);
	}

	
}
