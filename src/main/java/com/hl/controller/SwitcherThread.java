package com.hl.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSON;
import com.hl.dao.RedisDao;
import com.hl.domain.LocalConfig;
import com.hl.domain.ModelAction;
import com.hl.service.InvoiceService;
import com.hl.service.ModelService;
import com.hl.util.Const;
import com.hl.util.MessageUtil;
import com.hl.util.SocketLoadTool;
import com.hl.websocket.SystemWebSocketHandler;

//调度线程的类
public class SwitcherThread implements Runnable {

	private ServletContext servletContext;

	private RedisDao redisDao;

	private InvoiceService invoiceService;

	private ModelService modelService;

	private SystemWebSocketHandler systemWebSocketHandler;

	private SocketLoadTool socketLoadTool;

	private LocalConfig localConfig;

	private Long wait_size = 0l;
	private Long manage_size = 0l;
	
	
	// 与算法端
	private Socket algorithmSocket;

	private InputStream inputStream = null;
	private OutputStream outputStream = null;

	private Integer thread_msg = 0;

	private Integer deley = 0;

	private static Logger logger = Logger.getLogger(SwitcherThread.class);
	
	// 用于上锁的对象，当前线程在等待队列和操作队列都为空的时候进入等待状态，交给其他请求线程执行
	// 其他线程执行完后，通知该线程继续切换
	public SwitcherThread(Integer thread_msg, ServletContext servletContext) {
		ApplicationContext applicationContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(servletContext);
		this.socketLoadTool = (SocketLoadTool) applicationContext.getBean("socketListener");
		this.redisDao = (RedisDao) applicationContext.getBean("redisDao");
		this.systemWebSocketHandler = (SystemWebSocketHandler) applicationContext.getBean("systemWebSocketHandler");
		this.invoiceService = (InvoiceService) applicationContext.getBean("invoiceService");
		this.modelService = (ModelService) applicationContext.getBean("modelService");
		this.localConfig = (LocalConfig) applicationContext.getBean("localConfig");

		this.thread_msg = thread_msg;
		this.servletContext = servletContext;
		this.algorithmSocket = socketLoadTool.getAlgorithmSocket();
		try {
			if (algorithmSocket != null && algorithmSocket.isConnected()) {
				logger.info("SwitcherThread成功连接到算法服务器");
				outputStream = algorithmSocket.getOutputStream();
				inputStream = algorithmSocket.getInputStream();
			} else {
				logger.info("SwitcherThread成功连接到算法服务器失败");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		servletContext.setAttribute(Const.THREAD_MSG, thread_msg);// 将这个变量存到servletContext中
		servletContext.setAttribute(Const.DELAY, 0);
	}

	@Override
	public void run() {
		logger.info("新建SwitcherThread，开始执行");
		synchronized (thread_msg) {
			while (true) {
				wait_size = redisDao.getWaitSize();
				manage_size = redisDao.getManageSize();
				if (wait_size == 0l && manage_size == 0l) {
					logger.info("SwitcherThread睡眠,等待新请求加入两个队列");
					threadWait();
					logger.info("SwitcherThread被唤醒");
				}
				//先检查连接是否保持，如果断开就重连
				checkAlogrithmConnect();
				// 重新读
				wait_size = redisDao.getWaitSize();
				manage_size = redisDao.getManageSize();
				if (manage_size != 0l) {
					try {
						logger.info("切换到模板操作");
						switchManageModel();
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("模板操作出错,即将清除模板操作,线程睡眠");
						clearManageWait();
						threadWait();
						logger.info("SwitcherThread被唤醒");
					}		
				}
				
				// 重新读
				wait_size = redisDao.getWaitSize();
				manage_size = redisDao.getManageSize();
				if (wait_size != 0l && manage_size == 0) {
					try {
						logger.info("切换到发票识别");
						switchRecognizeInvoice();
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("发票识别出错,即将清除发票队列操作,线程睡眠");
						clearRecognizeWait();
						threadWait();
						logger.info("SwitcherThread被唤醒");					
					}

				}
			}
		}
	}

	public void switchRecognizeInvoice() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		// 得到最新的延时速度
		deley = (Integer) servletContext.getAttribute(Const.DELAY);
		logger.info("延时速度为" + deley);
		// 0.延时3秒
		// 调用service层方法处理识别过程返回的数据
		invoiceService.broadcastRecognizeProcess(inputStream, outputStream, deley);
	}

	public void switchManageModel() {
		// 1.先得到等待队列头的action_id
		String action_id = redisDao.getRight(Const.MANAGE_WAIT, 0l);
		// 2.根据这个作为key，取得对应的url,json_model,msg_id
		String manage_map_str = (String) redisDao.getValue(action_id);
		ModelAction modelAction = JSON.parseObject(manage_map_str, ModelAction.class);
		// 4.判断msg_id，决定采取的操作类型
		int msg_id = modelAction.getMsg_id();
		switch (msg_id) {	
		case 3:// 删除
		{
			int model_id = modelAction.getModel_id();
			// 另外还要包一层。。
			Map<String, Object> temp = new HashMap<>();
			temp.put("id", model_id);
			MessageUtil.sendMessage(outputStream, 3, JSON.toJSONString(temp), systemWebSocketHandler);
			logger.info(action_id + "发送了删除发票模板请求,model_id=" + model_id);
			// 调用service层的方法处理删除模板的结果
			modelService.broadcastDeleteModel(inputStream, modelAction);
		}
		break;

		case 4: // 修改
		{
			Map<String, Object> msg_map = new HashMap<>();
			// 先得到文件仓库路径
			String file_path = modelAction.getFile_path();
			// 得到全部原图
			List<String> origins = new ArrayList<>();
			File dir = new File(localConfig.getImagePath() + file_path);
			String[] temps = dir.list();
			for (String temp : temps) {
				if(!temp.contains("model"))//排除掉模板图片
				origins.add(localConfig.getImagePath() + file_path + temp);
			}
			// 得到json_model，加入图片url
			Map<String, Object> json_model = modelAction.getJson_model();
			json_model.put("id", modelAction.getModel_id());
			//暂时只发送一张原图，暂定第一张
			json_model.put(Const.URL, origins.get(0));
			// 另外json_model还要包一层。
			msg_map.put(Const.JSON_MODEL, json_model);
			MessageUtil.sendMessage(outputStream, 4, JSON.toJSONString(msg_map), systemWebSocketHandler);
			logger.info(action_id + "发送了修改发票模板请求,model_id="+modelAction.getModel_id());
			//弹出队列头该modelAction
			redisDao.pop(Const.MANAGE_WAIT);
			//删除该action的key
			redisDao.deleteKey(action_id);
			modelService.broadcastUpdateModel(inputStream, modelAction);
		}
		break;

		case 5:// 清空
			MessageUtil.sendMessage(outputStream, 5, null, systemWebSocketHandler);
			logger.info(action_id + "发送了清空发票模板请求");
			// 4. 弹出队列头，删除key
			redisDao.pop(Const.MANAGE_WAIT);
			logger.info(action_id + "弹出操作队列");
			redisDao.deleteKey(action_id + "");
			modelService.broadcastClearModel(inputStream, new Integer(action_id));
			break;
			
		case 6:
		{
			//增加多张模板
			// 得到batch_id，遍历所有携带该batch_id的modelAction,
			String batch_id = modelAction.getBatch_id();
			// 如果是第一张的话，得到新增模板缓存队列
			List<String> action_ids = redisDao.getRangeId(batch_id.toString());
			List<ModelAction>batch_list = new ArrayList<>();
			for(String key : action_ids){
				ModelAction action = JSON.parseObject((String)redisDao.getValue(key),
						ModelAction.class);
				batch_list.add(action);
			}
			// 准备批量发送
			Map<String, Object>final_map = new HashMap<>();
			List<Map<String, Object>> json_model_list = new ArrayList<>();
			for (ModelAction action : batch_list) {
				// 先得到文件仓库路径
				String file_path = action.getFile_path();
				// 得到全部原图
				List<String> origins = new ArrayList<>();
				File dir = new File(localConfig.getImagePath() + file_path);
				String[] temps = dir.list();
				for (String temp : temps) {
					if(!temp.contains("model"))//排除掉模板图片
					origins.add(localConfig.getImagePath() + file_path + temp);
					logger.info("添加了模板原图" + file_path + temp);
				}
				// 得到json_model，加入图片url
				Map<String, Object> json_model = action.getJson_model();
				json_model.put(Const.URL, origins);
				json_model.put("image_num", origins.size());
				json_model_list.add(json_model);
			}
			if(json_model_list.size() == 1){
				//发送单张模板的请求
				final_map.put(Const.JSON_MODEL,json_model_list.get(0));
				MessageUtil.sendMessage(outputStream, 2, JSON.toJSONString(final_map), systemWebSocketHandler);
				logger.info("发送了新增单张发票模板请求");
			}else {
				final_map.put(Const.JSON_MODEL, json_model_list);
				MessageUtil.sendMessage(outputStream, 6, JSON.toJSONString(final_map), systemWebSocketHandler);
				logger.info("发送了批处理新增发票模板请求，处理数量为" + batch_list.size());
			}
			// 调用service层的方法处理增加模板的结果
			if(batch_list.size() == 1){
				modelService.broadcastAddModelSingle(inputStream,batch_list.get(0));
			}else {
				modelService.broadcastAddModelMul(inputStream, batch_list);
			}		
		}
			break;
		default:
			break;
		}

	}

	private void checkAlogrithmConnect() {
		if(socketLoadTool.getAlgorithmSocket() != null)
			logger.info(socketLoadTool.getAlgorithmSocket().isClosed());
		// 检查与算法端的连接是否保持，如果断开的话，直接重连
		if (socketLoadTool.getAlgorithmSocket() == null ||
				socketLoadTool.getAlgorithmSocket().isClosed() == true
				||socketLoadTool.getAlgorithmSocket().isConnected() == false) {
			try {
				logger.info("即将断线重连");
				Socket socket = new Socket("127.0.0.1", new Integer(Const.PORT));
				socketLoadTool.setAlgorithmSocket(socket);
				algorithmSocket = socket;
				outputStream = algorithmSocket.getOutputStream();
				inputStream = algorithmSocket.getInputStream();
				logger.info("断线重连成功");
			} catch (IOException e) {
				e.printStackTrace();
				
				logger.error("断线重连失败,线程休眠");
				synchronized (thread_msg){
					threadWait();
				}
				logger.info("SwitcherThread被唤醒");	
			}

		}
	}
	
	private void threadWait(){
		try {
			thread_msg.wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void clearRecognizeWait(){
		List<String>uuids = redisDao.getRangeId(Const.RECOGNIZE_WAIT);
		for(String uuid : uuids){
			redisDao.deleteKey(uuid);
		}	
		redisDao.deleteKey(Const.RECOGNIZE_WAIT);
		logger.info("清除识别队列完成");
	}

	private void clearManageWait(){
		List<String>action_ids = redisDao.getRangeId(Const.MANAGE_WAIT);
		for(String action_id : action_ids){
			String manage_map_str = (String) redisDao.getValue(action_id);
			ModelAction modelAction = JSON.parseObject(manage_map_str, ModelAction.class);
			if(modelAction.getMsg_id() != 6){
				redisDao.deleteKey(action_id);
			}else {
				//批处理类型
				redisDao.deleteKey(action_id);
				// 得到batch_id，遍历所有携带该batch_id的modelAction,
				String batch_id = modelAction.getBatch_id();
				// 如果是第一张的话，得到新增模板缓存队列
				List<String> extra_ids = redisDao.getRangeId(batch_id.toString());
				for(String id : extra_ids){
					redisDao.deleteKey(id);
				}
				//删除队列
				redisDao.deleteKey(batch_id);
			}
		}
		redisDao.deleteKey(Const.MANAGE_WAIT);
		logger.info("清除操作队列完成");
	}
}
