package com.hl.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import com.alibaba.fastjson.JSON;
import com.hl.dao.ActionDao;
import com.hl.dao.InvoiceDao;
import com.hl.dao.ModelDao;
import com.hl.dao.RedisDao;
import com.hl.dao.UserDao;
import com.hl.domain.Action;
import com.hl.domain.Invoice;
import com.hl.domain.LocalConfig;
import com.hl.domain.OcrResult;
import com.hl.domain.RecognizeAction;
import com.hl.domain.RecognizeConsole;
import com.hl.domain.ResponseMessage;
import com.hl.domain.SimpleResponse;
import com.hl.domain.TestCase;
import com.hl.domain.User;
import com.hl.service.InvoiceService;
import com.hl.util.CheckUtil;
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

	@Resource(name = "actionDao")
	private ActionDao actionDao;

	@Resource(name = "modelDao")
	private ModelDao modelDao;
	
	@Resource(name = "systemWebSocketHandler")
	private SystemWebSocketHandler systemWebSocketHandler;

	@Resource(name = "socketListener")
	private SocketLoadTool socketLoadTool;

	@Resource(name = "localConfig")
	private LocalConfig localConfig;

	private static Logger logger = Logger.getLogger(InvoiceServiceImpl.class);
	
	// ajax处理web请求
	@Override
	public String  addRecognizeInvoice(RecognizeAction recognizeAction,TestCase testCase,
			List<String>url_suffixs,Integer thread_msg) {
		Map<String, Object>ans_map = new HashMap<>();
		// 获得当前用户，首先进行权限判断
		User user = userDao.getUserById(recognizeAction.getUser_id());
		//获得发票列表
		List<Invoice>invoice_list = recognizeAction.getInvoice_list();
		//websocket用的map
		Map<String, Object> broadcast_map = new HashMap<>();
		//将该action加入到数据库中
		recognizeAction.setDescription("识别发票[]");
		Integer action_id = actionDao.addAction(recognizeAction);
		recognizeAction.setAction_id(action_id);
		//然后加到索引库里
		//actionDao.solrAddUpdateAction(recognizeAction);
		//如果发过来的是一整套测试集，若是第一次发
		if(testCase != null && testCase.getPage() == 0){
			CheckUtil.initGlobal(redisDao, testCase);
			logger.info("收到测试集，初始化" + testCase.getTest_name());
		}
		
		// 发票全部加入到等待队列里,队列左进右出
		int k = 0;
		for (Invoice invoice : invoice_list) {
			//生成uuid，作为invoice排队的主键
			String uuid = UUID.randomUUID().toString();
			invoice.setUuid(uuid);
			//前端需要的一些信息，后期补充
			invoice.setAction_id(action_id);
			invoice.setAction_time(TimeUtil.getCurrentTime());
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
			logger.info("上传的url_suffix为" + invoice.getInvoice_url() + "对应uuid为：" + uuid);
			redisDao.leftPush(Const.RECOGNIZE_WAIT, uuid);
			redisDao.addKey(uuid, JSON.toJSONString(invoice));
		}
		logger.info("等待队列新增" + invoice_list.size() + "张发票");

		// 将增加的通知给全体用户
		broadcast_map.put(Const.MSG_ID, 201);
		broadcast_map.put(Const.NEW_RECOGNIZE, invoice_list);
		systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(broadcast_map)),
				new int[]{1});

		Long recognize_size = redisDao.getWaitSize(); // 识别队列
		Long manage_size = redisDao.getManageSize();// 操作队列
		logger.info("当前识别队列的数量为：" + recognize_size);
		logger.info("当前操作队列的数量为：" + manage_size);

		Long image_size = Long.valueOf(invoice_list.size());
		if ((recognize_size - image_size) == 0l && manage_size == 0l) {
			logger.info("通知切换线程进行下一步操作");
			// 7.通知切换线程进行下一步操作
			synchronized (thread_msg) {
				thread_msg.notifyAll();
			}
		}
		ans_map.put(Const.SUCCESS, "已加入队列，等待算法服务器处理");
		ans_map.put("recognize_size", recognize_size);
		ans_map.put("manage_size", manage_size);
		return JSON.toJSONString(ans_map);
	}

	// websocket返回处理结果 msg_id = 1, 100, 101, 102
	@Override
	public void broadcastRecognizeProcess(InputStream inputStream,OutputStream outputStream,Integer delay) {
		//测试集如果有的话
		TestCase testCase = null;
		// 先得到等待队列头的uuid
		String uuid = redisDao.getRight(Const.RECOGNIZE_WAIT, 0l);
		//找到对应的invoice对象
		String invoice_str = (String) redisDao.getValue(uuid);
		Invoice invoice = JSON.parseObject(invoice_str, Invoice.class);
		Integer action_id = invoice.getAction_id();
		Action action = actionDao.getActionById(action_id);
		//补充，将当前要跑的发票的url等信息发给前端
		broadcastNextRecognize(invoice);
		// 将后缀转换为本地硬盘路径
		String url_suffix = invoice.getInvoice_url();
		String absolute_path = localConfig.getImagePath() + url_suffix;
		//补充协议json信息
		Map<String, Object> msg_map = new HashMap<>();
		msg_map.put(Const.URL, absolute_path);
		msg_map.put(Const.DELAY, delay);
		//发送消息给算法端
		MessageUtil.sendMessage(outputStream, 1, JSON.toJSONString(msg_map),systemWebSocketHandler);
		logger.info(uuid + "发送了识别请求");
		// 将算法运行结果分阶段的广播给所有管理员
		Map<String, Object> err_map = new HashMap<>();// 用来发送异常消息
		Integer model_id = 0;// 模板编号
		// 结果同时推送给模拟客户端
		Socket customerSocket = socketLoadTool.getCustomerSocket();
		//是否是报错发票
		Boolean isWrong = false;
		//用来记录发票识别的id，写入日志中
		List<Integer>invoice_id_list = new ArrayList<>();
		while (true) {
			// 处理一次消息数据
			try {
				ResponseMessage message = MessageUtil.getMessage(inputStream);
				if (message != null) {
					if (message.getMsg_id() == 1) {
						if (customerSocket != null) {
							MessageUtil.sendMessage(customerSocket.getOutputStream(), message.getMsg_id(),
									message.getJson_str(), null);
							logger.info("成功将识别后的信息发送给客户端");
						} else {
							//System.out.println("与模拟客户端的连接未打开！");
						}
						// 同时，加入到redis队列里记录
						redisDao.leftPush(Const.RECOGNIZE_PROCESS, message.getFinalMessage(action_id));
						// 解析json数据，若成功将发票识别数据存入数据库
						Map<String, Object> invoice_data = JSON.parseObject(message.getJson_str());
						int status = (int) invoice_data.get("status");
						Integer invoice_id = null;
						if (status == 0) {
							//设置发票的识别状态为成功，以及识别时间
							invoice.setInvoice_status(0);
							invoice.setRecognize_time(TimeUtil.getCurrentTime());
							//得到region_list
							List<String> region_list = redisDao.getRangeId(Const.RECOGNIZE_PROCESS);
							Collections.reverse(region_list);
							invoice.setRegion_list(JSON.toJSONString(region_list));
							if(isWrong) {
								invoice.setIs_fault(1);//设置错误发票
								//更新redis报错发票数量
								redisDao.addSelf("fault_num");
								//返回最新的报错发票数量
								Map<String, Object>map = new HashMap<>();
								map.put("msg_id", 205);
								map.put("fault_num", new Integer((String)redisDao.getValue("fault_num")));
								//告诉其他页面
								logger.info("发送更新错误发票数量的消息");
								systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(map)),
										new int[]{1,2,3,5,6,7});
								//告诉错误发票页面
								Map<String, Object>map2 = new HashMap<>();
								map2.put("msg_id", 206);
								map2.put("fault_invoice", JSON.toJSONString(invoice));
								systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(map2)),
										new int[]{4});
							}
							else {
								invoice.setIs_fault(0);
							}
							invoice_id = invoiceDao.addRecognizeInvoice(invoice_data,invoice);
							//记录下来id
							invoice_id_list.add(invoice_id);
							logger.info("成功将该发票信息写入数据库,invoice_id=" + invoice_id);
							//如果是测试集的话，准备统计识别结果、识别率
							String testCase_str = (String) redisDao.getValue("testCase");
							if(testCase_str != null){
								testCase = JSON.parseObject(testCase_str,TestCase.class);
								CheckUtil.checkOnce(redisDao,testCase,invoice_data,invoice.getOrder());
							}
							// 更新识别数量这东西
							//redisDao.addSelf(Const.MINUTE_SUM);
						}
						// 如果是最后一张的话，更新action表的一些信息
						if(invoice.getOrder() == invoice.getRecognize_num()){
							action.setDescription("识别发票" + invoice_id_list.toString());
							actionDao.updateActionDescription(action.getAction_id(),action.getDescription());
							//actionDao.solrAddUpdateAction(action);
							logger.info("成功更新该action,action_id=" + action_id);
							if(testCase != null){
								if(testCase.getPage()*10 + invoice.getOrder() == testCase.getPic_num()){
									logger.info("测试集以及到了最后一张");
									//判断是否到了测试集最后一张，完成一些善后操作
									CheckUtil.finishCheck(redisDao,testCase);
								}
							}
						}
						// 该模板的成功识别次数加一
						modelDao.plusModelSuccess(model_id);
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
						systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)),new int[]{2});
						// 结束循环监听
						//恢复isWrong
						isWrong = false;
						break;
					} else if (message.getMsg_id() == 100) {
						// 同时，获得了model_id
						Map<String, Object> temp_map = JSON.parseObject(message.getJson_str());
						model_id = (Integer) temp_map.get("id");// 之后将模板id写入数据库
						//赋值给invoice
						invoice.setModel_id(model_id);
						String label = modelDao.getModelLabel(model_id);
						// 在json里加入模板url,以及label
						String json_str = message.getJson_str();
						Map<String, Object> json_map = JSON.parseObject(json_str);
						json_map.put(Const.URL, localConfig.getIp() + modelDao.getModelUrl(model_id));
						json_map.put(Const.MODEL_LABEL, label);
						json_map.put(Const.MSG_ID, 100);
						// 同时，加入到redis队列里记录
						redisDao.leftPush(Const.RECOGNIZE_PROCESS, JSON.toJSONString(json_map));
						message.setJson_str(JSON.toJSONString(json_map));
						// 过程信息，直接转交给前端
						systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)),new int[]{2});
						logger.info("model_id为" + model_id);
					} else if (message.getMsg_id() == 101 || message.getMsg_id() == 102) {
						//加入可信度判断
						if(message.getMsg_id() == 102){
							OcrResult result = JSON.parseObject(message.getJson_str(),OcrResult.class);
							//一旦某个区域可信度低于0.9，判定为报错发票，录到数据库fault_invoice表中
							if(Double.valueOf(result.getProbability()) < 0.9){
								if(isWrong == false){
									isWrong = true;
								}
							}
						}
						// 同时，加入到redis队列里记录
						redisDao.leftPush(Const.RECOGNIZE_PROCESS, message.getFinalMessage(action_id));
						// 过程信息，直接转交给前端
						systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)),new int[]{2});
					}else if (message.getMsg_id() == -1) {
						logger.info("该发票没有相关分类");
						// 弹出队列头，同时删除key(如果正常识别的话，如果不能，则加到异常队列)
						redisDao.pop(Const.RECOGNIZE_WAIT);
						redisDao.deleteKey(uuid);
						systemWebSocketHandler.sendMessageToUsers(new TextMessage(message.getFinalMessage(action_id)),new int[]{2});
						break;
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
				err_map.put(Const.ERR, "接收算法服务器数据异常");
				systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)),new int[]{2});
			}
		}

	}

	// websocket返回处理结果 msg_id = 200
	@Override
	public String broadcastRecognizeWaitFirst() {
		// 连接建立后，立刻向用户返回识别队列的信息（头1024个）
		Map<String, Object> ans_map = new HashMap<>();
		List<String> recognize_wait_origin = redisDao.getRangeId(Const.RECOGNIZE_WAIT);
		int recognize_size = recognize_wait_origin.size();
		// 在数据库里查询，返回一个action的主要信息
		if (recognize_wait_origin != null && recognize_size != 0) {
			List<Invoice> recognize_wait = new ArrayList<>();
			for (String uuid : recognize_wait_origin) {
				String invoice_str = (String) redisDao.getValue(uuid);
				Invoice invoice = JSON.parseObject(invoice_str,Invoice.class);
				recognize_wait.add(invoice);
				logger.info("将缓冲队列的信息推送给前端");
			}
			ans_map.put(Const.RECOGNIZE_WAIT, recognize_wait);
		}
		ans_map.put(Const.MSG_ID, 200);
		return JSON.toJSONString(ans_map);
	}

	// ajax处理web请求 msg_id = 202
	@Override
	public String openConsole(Integer delay) {
		// 打开监控台，返回当前图片的url，action_id,img_str
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
			console.setAction_time(invoice.getAction_time());
			console.setImg_str("data:image/jpg;base64," + img_str);
			console.setMsg_id(202);
			//获取发票识别延时
			console.setDelay(delay);
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
		start_map.put(Const.ACTION_START_TIME, invoice.getAction_time());
		start_map.put(Const.COMPANY_NAME, invoice.getCompany_name());
		systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(start_map)),new int[]{2});
	}

	// websocket返回处理结果 msg_id = 204
	@Override
	public void broadcastRegionList(){
		List<String> region_list = redisDao.getRangeId(Const.RECOGNIZE_PROCESS);
		Collections.reverse(region_list);
		logger.info("region_list = " + region_list);
		Map<String, Object>temp = new HashMap<>();
		temp.put("region_list", region_list);
		temp.put("msg_id", 204);
		systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(temp)), new int[]{2});
	}
	
	// ajax处理web请求，已经废弃
	@Override
	public void changeImageUrlIp() {
		List<String> urls = modelDao.getAllModelUrl();
		for (String url : urls) {
			// 获得后一段
			int index = url.indexOf("e");
			String part2 = url.substring(index + 1, url.length());
			modelDao.updateModelUrl(url, localConfig.getIp() + "/invoice" + part2);
		}

	}

	// ajax处理web请求，调整发票识别速度
	@Override
	public void UpdateRecognizeSpeed(Map<String, Object> ans_map, Integer user_id, Integer delay,
			ServletContext servletContext) {
		servletContext.setAttribute(Const.DELAY, delay);
		ans_map.put(Const.SUCCESS, "成功调整速度");
	}

	
	//ajax，获取20张报错发票
	@Override
	public List<Invoice> getTwentyFaultQueue(Integer page) {
		List<Invoice>list = invoiceDao.getTwentyFaultInvoice(page);
		return list;
	}

	
	@Override
	public String clearRecognizeQueue() {
		List<String>recognize_list = redisDao.getRangeId(Const.RECOGNIZE_WAIT);
		if(recognize_list != null && recognize_list.size() > 0){
			for(String uuid : recognize_list){
				//删除一张发票的缓存
				redisDao.deleteKey(uuid);
			}
		}
		redisDao.deleteKey(Const.RECOGNIZE_WAIT);
		redisDao.deleteKey(Const.RECOGNIZE_PROCESS);
		return JSON.toJSONString(new SimpleResponse("清除成功！", null));
	}

	



}
