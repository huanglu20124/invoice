package com.hl.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.TextMessage;
import com.alibaba.fastjson.JSON;
import com.hl.dao.InvoiceDao;
import com.hl.dao.RedisDao;
import com.hl.dao.UserDao;
import com.hl.domain.Action;
import com.hl.domain.Invoice;
import com.hl.domain.LocalConfig;
import com.hl.domain.Model;
import com.hl.domain.ModelAction;
import com.hl.domain.RecognizeAction;
import com.hl.domain.RecognizeConsole;
import com.hl.domain.ResponseMessage;
import com.hl.domain.User;
import com.hl.service.InvoiceService;
import com.hl.util.Const;
import com.hl.util.ImageUtil;
import com.hl.util.MessageUtil;
import com.hl.util.SocketLoadTool;
import com.hl.util.TimeUtil;
import com.hl.websocket.SystemWebSocketHandler;

@Service("invoiceService")
public class InvoiceServiceImpl implements InvoiceService {

	@Resource(name = "redisDao")
	private RedisDao redisDao;

	@Resource(name = "invoiceDao")
	private InvoiceDao invoiceDao;

	@Resource(name = "userDao")
	private UserDao userDao;

	@Resource(name = "systemWebSocketHandler")
	private SystemWebSocketHandler systemWebSocketHandler;

	@Resource(name = "socketListener")
	private SocketLoadTool socketLoadTool;

	@Resource(name = "localConfig")
	private LocalConfig localConfig;

	// ajax处理web请求
	@Override
	public void addRecognizeInvoice(Map<String, Object> ans_map, RecognizeAction recognizeAction,
			List<String>url_suffixs,Integer thread_msg) {
		// 获得当前用户，首先进行权限判断
		User user = userDao.getUserById(recognizeAction.getUser_id());
		//获得发票列表
		List<Invoice>invoice_list = recognizeAction.getInvoice_list();
		//websocket用的map
		Map<String, Object> broadcast_map = new HashMap<>();
		//将该action加入到数据库中
		recognizeAction.setMsg_id(1);
		Integer action_id = invoiceDao.addAction(recognizeAction);
		// 发票全部加入到等待队列里,队列左进右出
		int k = 0;
		for (Invoice invoice : invoice_list) {
			//生成uuid，作为invoice排队的主键
			String uuid = UUID.randomUUID().toString();
			invoice.setUuid(uuid);
			//前端需要的一些信息，后期补充
			invoice.setAction_id(action_id);
			invoice.setAction_start_time(TimeUtil.getCurrentTime());
			invoice.setUser_id(user.getUser_id());
			invoice.setUser_name(user.getUser_name());
			invoice.setCompany_id(user.getCompany_id());
			invoice.setCompany_name(user.getCompany_name());
			String url_suffix = url_suffixs.get(k);
			invoice.setInvoice_url(url_suffix);
			invoice.setUrl(localConfig.getIp() + url_suffix);
			invoice.setImage_size(ImageUtil.getImageSize(localConfig.getImagePath() + url_suffix));
			k++;
			//设置发票序号
			invoice.setOrder(k);
			//暂时将recognize_num存到invoice里。。
			invoice.setRecognize_num(invoice_list.size());
			System.out.println("上传的url_suffix为" + invoice.getInvoice_url() + "对应uuid为：" + uuid);
			redisDao.leftPush(Const.RECOGNIZE_WAIT, uuid);
			redisDao.addKey(uuid, JSON.toJSONString(invoice));
		}
		System.out.println("等待队列新增" + invoice_list.size() + "张发票");

		// 将增加的通知给全体用户
		broadcast_map.put(Const.MSG_ID, 201);
		broadcast_map.put(Const.NEW_RECOGNIZE, invoice_list);
		systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(broadcast_map)));

		Long recognize_size = redisDao.getWaitSize(); // 识别队列
		Long manage_size = redisDao.getManageSize();// 操作队列
		System.out.println("当前识别队列的数量为：" + recognize_size);
		System.out.println("当前操作队列的数量为：" + manage_size);

		Long image_size = Long.valueOf(invoice_list.size());
		if ((recognize_size - image_size) == 0l && manage_size == 0l) {
			System.out.println("通知切换线程进行下一步操作");
			// 7.通知切换线程进行下一步操作
			synchronized (thread_msg) {
				thread_msg.notifyAll();
			}
		}
		ans_map.put(Const.SUCCESS, "已加入队列，等待算法服务器处理");
		ans_map.put("recognize_size", recognize_size);
		ans_map.put("manage_size", manage_size);

	}

	// ajax处理web请求
	@Override
	public void addOrUpdateInvoiceModel(Map<String, Object>ans_map, ModelAction modelAction,Integer thread_msg){
		// 首先进行权限判断
		User user = userDao.getUserById(modelAction.getUser_id());
		// 添加新的发票模型
		// 1生成一条行为，插入action表，并获取返回的action_id
		Integer action_id = null;
		action_id = invoiceDao.addAction(modelAction);
		//补全modelAction的一些参数
		modelAction.setAction_id(action_id);
		modelAction.setAction_start_time(TimeUtil.getCurrentTime());
		modelAction.setUser_name(user.getUser_name());
		modelAction.setCompany_name(user.getCompany_name());
		redisDao.leftPush(Const.MANAGE_WAIT, action_id.toString());// 加入到操作队列
		// key为action_id,value为modelAction
		redisDao.addKey(action_id.toString(), JSON.toJSONString(modelAction));
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
		System.out.println("修改发票模板的请求已加入队列，等待算法服务器处理");
		ans_map.put("recognize_size", recognize_size);
		ans_map.put("manage_size", manage_size);
	}

	// ajax处理web请求
	@Override
	public void deleteInvoiceModel(Map<String, Object> ans_map, Integer user_id, Integer model_id, Integer thread_msg) {
		// 首先进行权限判断
		User user = userDao.getUserById(user_id);
		// 删除发票模板
		// 1生成一条行为，插入action表，并获取返回的action_id
		Integer action_id = invoiceDao.addNewModelAction(user_id);
		redisDao.leftPush(Const.MANAGE_WAIT, action_id.toString());// 加入到操作队列
		// key为action_id，value为图片url以及msg_id=2,还有json_model
		Map<String, Object> msg_map = new HashMap<>();
		msg_map.put(Const.MODEL_ID, model_id);
		msg_map.put(Const.MSG_ID, 3);
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

	// websocket返回处理结果 msg_id = 1, 100, 101, 102
	@Override
	public void broadcastRecognizeProcess(InputStream inputStream,OutputStream outputStream,Integer delay) {
		// 先得到等待队列头的uuid
		String uuid = redisDao.getRight(Const.RECOGNIZE_WAIT, 0l);
		//找到对应的invoice对象
		String invoice_str = (String) redisDao.getValue(uuid);
		Invoice invoice = JSON.parseObject(invoice_str, Invoice.class);
		Integer action_id = invoice.getAction_id();
		// 如果是属于一次action里的第一张，更新action相关信息
		if(invoice.getOrder() == 1){
			invoiceDao.runAction(action_id);
		}
		//补充，将当前要跑的发票的url等信息发给前端
		broadcastNextRecognize(invoice);
		// 将后缀转换为本地硬盘路径
		String url_suffix = invoice.getInvoice_url();
		String absolute_path = ImageUtil.suffixToBmp(localConfig.getImagePath() + url_suffix);
		//补充协议json信息
		Map<String, Object> msg_map = new HashMap<>();
		msg_map.put(Const.URL, absolute_path);
		msg_map.put(Const.DELAY, delay);
		//发送消息给算法端
		MessageUtil.sendMessage(outputStream, 1, JSON.toJSONString(msg_map),systemWebSocketHandler);
		System.out.println(uuid + "发送了识别请求");
		// 将算法运行结果分阶段的广播给所有管理员
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		Integer model_id = 0;// 模板编号
		// 结果同时推送给模拟客户端
		Socket customerSocket = socketLoadTool.getCustomerSocket();

		while (true) {
			// 处理一次消息数据
			try {
				ResponseMessage message = MessageUtil.getMessage(inputStream);
				if (message != null) {
					if (message.getMsg_id() == 1) {
						if (customerSocket != null) {
							MessageUtil.sendMessage(customerSocket.getOutputStream(), message.getMsg_id(),
									message.getJson_str(), null);
							System.out.println("成功将识别后的信息发送给客户端");
						} else {
							System.out.println("与模拟客户端的连接未打开！");
						}
						// 同时，加入到redis队列里记录
						redisDao.leftPush(Const.RECOGNIZE_PROCESS, message.getFinalMessage(action_id));
						// 解析json数据，若成功将发票识别数据存入数据库
						Map<String, Object> invoice_data = JSON.parseObject(message.getJson_str());
						int status = (int) invoice_data.get("status");
						Integer invoice_id = null;
						if (status == 0) {
							//设置发票的识别状态为成功，以及识别时间
							invoice.setInvoice_status(1);
							invoice.setRecognize_time(TimeUtil.getCurrentTime());
							invoice_id = invoiceDao.addRecognizeInvoice(invoice_data,invoice);
							System.out.println("成功将该发票信息写入数据库,invoice_id=" + invoice_id);
							// 更新识别数量这东西
							//redisDao.addSelf(Const.MINUTE_SUM);
						}
						// 如果是最后一张的话，更新action表的一些信息
						if(invoice.getOrder() == invoice.getRecognize_num()){
							invoiceDao.finishAction(action_id,1);//1代表成功
							System.out.println("成功更新该action,action_id=" + action_id);
						}
						// 该模板的成功识别次数加一
						invoiceDao.plusModelSuccess(model_id);
						// 弹出队列头，同时删除key(如果正常识别的话，如果不能，则加到异常队列)
						redisDao.pop(Const.RECOGNIZE_WAIT);
						if (status < 0) {
							// 识别失败，加入异常发票队列
							redisDao.leftPush(Const.EXCEPTION_WAIT, uuid);
						} else {
							redisDao.deleteKey(uuid);
						}
						// 最后，清除识别的过程队列
						redisDao.deleteKey(Const.RECOGNIZE_PROCESS);
						// 所得json字符串直接广播发给用户
						systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)));
						// 结束循环监听
						break;
					} else if (message.getMsg_id() == 100) {
						// 同时，获得了model_id
						Map<String, Object> temp_map = JSON.parseObject(message.getJson_str());
						model_id = (Integer) temp_map.get("id");// 之后将模板id写入数据库
						//赋值给invoice
						invoice.setModel_id(model_id);
						String label = invoiceDao.getModelLabel(model_id);
						// 在json里加入模板url,以及label
						String json_str = message.getJson_str();
						Map<String, Object> json_map = JSON.parseObject(json_str);
						json_map.put(Const.URL, localConfig.getIp() + invoiceDao.getModelUrl(model_id));
						json_map.put(Const.MODEL_LABEL, label);
						json_map.put(Const.MSG_ID, 100);
						// 同时，加入到redis队列里记录
						redisDao.leftPush(Const.RECOGNIZE_PROCESS, JSON.toJSONString(json_map));
						message.setJson_str(JSON.toJSONString(json_map));
						// 过程信息，直接转交给前端
						systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)));
						System.out.println("model_id为" + model_id);
					} else if (message.getMsg_id() == 101 || message.getMsg_id() == 102) {
						// 同时，加入到redis队列里记录
						redisDao.leftPush(Const.RECOGNIZE_PROCESS, message.getFinalMessage(action_id));
						// 过程信息，直接转交给前端
						systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)));
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
				err_map.put(Const.ERR, "接收算法服务器数据异常");
				systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)));
			}
		}

	}

	// websocket返回处理结果 msg_id = 2
	@Override
	public void broadcastAddNewModel(InputStream inputStream, int action_id, Map<String, Object> json_model_map,
			String url_suffix) {
		// 首先，更新action开始跑算法的时间
		invoiceDao.runAction(action_id);
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		String url = ImageUtil.suffixToJpg(localConfig.getIp() + url_suffix);// 变为网络url
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
			// systemWebSocketHandler.sendMessageToUsers(new
			// TextMessage(str.getBytes("gbk")));
			System.out.println(str);
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(str));
			// 同时得到model_id
			Integer model_id = null;
			// 2.成功的话，model表加入一个新model,model_id主键由算法端决定
			if (status == 0) {
				model_id = (int) response_map.get("id");
				Integer image_size = ImageUtil.getImageSize(localConfig.getImagePath() + url_suffix);
				invoiceDao.addModel(model_id, json_model_map, TimeUtil.getCurrentTime(), url_suffix, image_size);
			}
			// 3.更新数据库的model表,model_id，跑完的时间
			invoiceDao.finishAction(action_id, status);;
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
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)));
		}

	}

	// websocket返回处理结果 msg_id = 4
	@Override
	public void broadcastUpdateModel(InputStream inputStream, Integer action_id, Map<String, Object> json_model_map,
			String url_suffix, int model_id) {
		// 首先，更新action开始跑算法的时间
		invoiceDao.runAction(action_id);
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		String url = ImageUtil.suffixToJpg(localConfig.getIp() + url_suffix);// 将后缀变为网络url
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
			temp_map.put(Const.JSON_MODEL, json_model_map);
			String str = JSON.toJSONString(temp_map);
			if (status == 0) {
				// 2.model表更新该model
				invoiceDao.updateModel(model_id, JSON.toJSONString(json_model_map), url_suffix,action_id);
			}
			// 3.更新数据库的action表,model_id，跑完的时间
			invoiceDao.finishAction(action_id, status);;
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
				systemWebSocketHandler.sendMessageToUsers(new TextMessage(str.getBytes("utf-8")));
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
			err_map.put(Const.ERR, "接受算法服务器数据异常");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)));
		}

	}

	// websocket返回处理结果 msg_id = 3
	@Override
	public void broadcastDeleteModel(InputStream inputStream, Integer action_id, int model_id) {
		// 首先，更新action开始跑算法的时间
		invoiceDao.runAction(action_id);
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		// 处理增加模板的结果
		try {
			ResponseMessage message = MessageUtil.getMessage(inputStream);
			String json_str = message.getJson_str();
			// 1.解析json，先将结果直接返回给web端
			Map<String, Object> response_map = JSON.parseObject(json_str);
			int status = (int) response_map.get("status");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)));
			// 2.model表删除该model,其他携带该外键的行全部更新
			String url_suffix = null;
			// 这个url用来后面删除图片文件
			if (status == 0) {
				// 找到图片文件,删除全部本地文件
				url_suffix = invoiceDao.getModelUrl(model_id);
				ImageUtil.deleteAllModelImage(localConfig.getImagePath(), url_suffix);
				// 找到全部携带该model_id的invoice，设置外键为null
				invoiceDao.deleteInvoiceForeginModel(model_id);
				// 找到全部携带该model_id的action，设置外键为null
				invoiceDao.deleteActionForeginModel(model_id);
				// 删除model
				invoiceDao.deleteModel(model_id);
				// 找到全部大于该model_id的行
				List<Integer> ids = invoiceDao.getBiggerModelId(model_id);
				for (Integer id : ids) {
					// 全部id减一
					invoiceDao.minusModelId(id);
				}
			}
			// 3.更新数据库的action表,model_id，跑完的时间
			invoiceDao.finishDeleteModelAction(action_id, status);
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
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)));
		}
	}

	// websocket返回处理结果 msg_id = 5
	@Override
	public void broadcastClearModel(InputStream inputStream, Integer action_id) {
		// 一键清空所有模板
		invoiceDao.runAction(action_id);
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		// 处理增加模板的结果
		try {
			ResponseMessage message = MessageUtil.getMessage(inputStream);
			String json_str = message.getJson_str();
			// 1.解析json，先将结果直接返回给web端
			Map<String, Object> response_map = JSON.parseObject(json_str);
			int status = (int) response_map.get("status");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)));
			// 2.model表删除该model,其他携带该外键的行全部更新
			if (status == 0) {
				// 全部model_id!=null 的invoice，设置外键为null
				invoiceDao.deleteAllInvoiceForeginModel();
				// 全部model_id!=null 的action，设置外键为null
				invoiceDao.deleteAllActionForeginModel();
				// 得到全部model_url
				List<String> urls = invoiceDao.getAllModelUrl();
				// 删除全部本地图片文件
				for (String url : urls) {
					ImageUtil.deleteAllModelImage(localConfig.getImagePath(), url);
				}
				// 删除全部model
				invoiceDao.clearAllModel();
			}
			// 3.更新数据库的action表,model_id，跑完的时间
			invoiceDao.finishDeleteModelAction(action_id, status);
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
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)));
		}
	}

	// ajax处理web请求
	@Override
	public void getAllModel(Map<String, Object> ans_map, Integer user_id, Integer page) {
		// 返回当前模板库全部信息,一次12条
		// 首先进行权限判断
		User user = userDao.getUserById(user_id);
		List<Model> model_list = invoiceDao.getTwelveModel(page);
		Collections.reverse(model_list);
		// 将url_suffix转为网络url
		for (Model model : model_list) {
			String url_suffix = model.getModel_url();
			model.setModel_url(localConfig.getIp() + url_suffix);
		}
		ans_map.put(Const.MODEL_LIST, model_list);
	}

	// websocket返回处理结果 msg_id = 200
	@Override
	public String broadcastRecognizeWaitFirst() {
		// 连接建立后，立刻向用户返回识别队列的信息（头1024个）
		Map<String, Object> ans_map = new HashMap<>();
		List<Object> recognize_wait_origin = redisDao.getRangeId(Const.RECOGNIZE_WAIT);
		int recognize_size = recognize_wait_origin.size();
		// 在数据库里查询，返回一个action的主要信息
		if (recognize_wait_origin != null && recognize_size != 0) {
			List<Invoice> recognize_wait = new ArrayList<>();
			for (Object temp : recognize_wait_origin) {
				String uuid = (String) temp;
				String invoice_str = (String) redisDao.getValue(uuid);
				Invoice invoice = JSON.parseObject(invoice_str,Invoice.class);
				recognize_wait.add(invoice);
				System.out.println("将缓冲队列的信息推送给前端");
			}
			ans_map.put(Const.RECOGNIZE_WAIT, recognize_wait);
		}
		ans_map.put(Const.MSG_ID, 200);
		return JSON.toJSONString(ans_map);
	}

	// ajax处理web请求
	@Override
	public void deleteAllModel(Map<String, Object> ans_map, Integer user_id, Integer thread_msg) {
		// 清空所有发票模板
		// 首先进行权限判断
		User user = userDao.getUserById(user_id);
		// 1生成一条行为，插入action表，并获取返回的action_id
		Integer action_id = invoiceDao.addNewModelAction(user_id);
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

	// ajax处理web请求 msg_id = 202
	@Override
	public String openConsole() {
		// 打开监控台，返回当前图片的url，action_id,以及之前过程得到的识别信息
		List<Object> region_list_origin = redisDao.getRangeId(Const.RECOGNIZE_PROCESS);
		List<String> region_list = new ArrayList<>();
		// 变成String。。
		for (Object object : region_list_origin) {
			region_list.add((String) object);
		}
		Collections.reverse(region_list);
		System.out.println("region_list = " + region_list);

		String uuid = redisDao.getRight(Const.RECOGNIZE_WAIT, 0l);
		if (uuid != null) {
			String invoice_str = (String) redisDao.getValue(uuid);
			Invoice invoice = JSON.parseObject(invoice_str,Invoice.class);
			// 补充，加入img_str
			String local_path = ImageUtil.suffixToJpg(localConfig.getImagePath() + invoice.getInvoice_url());
			String img_str = ImageUtil.GetImageStr(local_path);
			RecognizeConsole console = new RecognizeConsole();
			console.setUser_id(invoice.getUser_id());
			console.setUser_name(invoice.getUser_name());
			console.setCompany_id(invoice.getCompany_id());
			console.setCompany_name(invoice.getCompany_name());
			console.setAction_start_time(invoice.getAction_start_time());
			console.setImg_str("data:image/jpg;base64," + img_str);
			console.setRegion_list(region_list);
			return JSON.toJSONString(console);
		}else {
			return "{}";
		}
	}

	// websocket返回处理结果 msg_id = 203
	@Override
	public void broadcastNextRecognize(Invoice invoice) {
		// 补充，将当前要跑的发票的url等信息发给前端
		Map<String, Object> start_map = new HashMap<>();
		start_map.put(Const.ACTION_ID, invoice.getAction_id());
		String url_suffix = invoice.getInvoice_url();
		// 将url变为网络url
		start_map.put(Const.URL, localConfig.getIp() + url_suffix);
		// 加入图片大小
		start_map.put(Const.IMAGE_SIZE, invoice.getImage_size());
		// 补充，加入img_str
		String local_path = localConfig.getImagePath() + url_suffix;
		String img_str = ImageUtil.GetImageStr(local_path);
		start_map.put(Const.IMG_STR, "data:image/jpg;base64," + img_str);

		start_map.put(Const.MSG_ID, 203);
		start_map.put(Const.USER_NAME, invoice.getUser_name());
		start_map.put(Const.ACTION_START_TIME, invoice.getAction_start_time());
		start_map.put(Const.COMPANY_NAME, invoice.getCompany_name());
		systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(start_map)));
	}

	// ajax处理web请求，已经废弃
	@Override
	public void changeImageUrlIp() {
		List<String> urls = invoiceDao.getAllModelUrl();
		for (String url : urls) {
			// 获得后一段
			int index = url.indexOf("e");
			String part2 = url.substring(index + 1, url.length());
			invoiceDao.updateModelUrl(url, localConfig.getIp() + "/invoice" + part2);
		}

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
			invoiceDao.updateModelJsonModel(model_id + 1, json_model_list.get(model_id));
		}

	}

	// ajax处理web请求，调整发票识别速度
	@Override
	public void UpdateRecognizeSpeed(Map<String, Object> ans_map, Integer user_id, Integer delay,
			ServletContext servletContext) {
		// 首先进行权限判断
		User user = userDao.getUserById(user_id);
		servletContext.setAttribute(Const.DELAY, delay);
		ans_map.put(Const.SUCCESS, "成功调整速度");
	}



}
