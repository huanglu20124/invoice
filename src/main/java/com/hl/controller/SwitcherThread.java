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
			if (algorithmSocket != null) {
				System.out.println("SwitcherThread成功连接到算法服务器");
				outputStream = algorithmSocket.getOutputStream();
				inputStream = algorithmSocket.getInputStream();
			} else {
				System.out.println("SwitcherThread成功连接到算法服务器失败");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		servletContext.setAttribute(Const.THREAD_MSG, thread_msg);// 将这个变量存到servletContext中
		servletContext.setAttribute(Const.DELAY, 0);
	}

	@Override
	public void run() {
		System.out.println("新建SwitcherThread，开始执行");
		synchronized (thread_msg) {
			while (true) {
				wait_size = redisDao.getWaitSize();
				manage_size = redisDao.getManageSize();
				if (wait_size == 0l && manage_size == 0l) {
					System.out.println("SwitcherThread睡眠,等待新请求加入两个队列");
					try {
						thread_msg.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("SwitcherThread被唤醒");
				}
				// 重新读
				wait_size = redisDao.getWaitSize();
				manage_size = redisDao.getManageSize();
				if (manage_size != 0l) {
					switchManageModel();
				}
				// 重新读
				wait_size = redisDao.getWaitSize();
				manage_size = redisDao.getManageSize();
				if (wait_size != 0l && manage_size == 0) {
					switchRecognizeInvoice();
				}
			}
		}
	}

	public void switchRecognizeInvoice() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		checkAlogrithmConnect();
		// 得到最新的延时速度
		deley = (Integer) servletContext.getAttribute(Const.DELAY);
		// 0.延时3秒
		// 调用service层方法处理识别过程返回的数据
		invoiceService.broadcastRecognizeProcess(inputStream, outputStream, deley);
	}

	public void switchManageModel() {
		checkAlogrithmConnect();
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
			File dir = new File(localConfig.getImagePath() + file_path  + "original/");
			String[] temps = dir.list();
			for (String temp : temps) {
				origins.add(localConfig.getImagePath() + file_path + temp);
			}
			// 得到json_model，加入图片url
			Map<String, Object> json_model = modelAction.getJson_model();
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
			System.out.println(action_id + "发送了清空发票模板请求");
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
			List<Map<String, Object>> msg_list = new ArrayList<>();
			for (ModelAction action : batch_list) {
				Map<String, Object> msg_map = new HashMap<>();
				// 先得到文件仓库路径
				String file_path = action.getFile_path();
				// 得到全部原图
				List<String> origins = new ArrayList<>();
				File dir = new File(localConfig.getImagePath() + file_path + "original/");
				String[] temps = dir.list();
				for (String temp : temps) {
					origins.add(localConfig.getImagePath() + file_path + temp);
				}
				// 得到json_model，加入图片url
				Map<String, Object> json_model = action.getJson_model();
				json_model.put(Const.URL, origins);
				// 另外json_model还要包一层。
				msg_map.put(Const.JSON_MODEL, json_model);
				msg_list.add(msg_map);
			}
			if(msg_list.size() == 1){
				//发送单张模板的请求
				MessageUtil.sendMessage(outputStream, 2, JSON.toJSONString(msg_list.get(0)), systemWebSocketHandler);
				logger.info("发送了新增单张发票模板请求");
			}else {
				MessageUtil.sendMessage(outputStream, 6, JSON.toJSONString(msg_list), systemWebSocketHandler);
				logger.info("发送了批处理新增发票模板请求，处理数量为" + batch_list.size());
			}
			//弹出队列头该modelAction
			redisDao.pop(Const.MANAGE_WAIT);
			//删除全部相关key
			for(String temp : action_ids){
				redisDao.deleteKey(temp);
			}			
			redisDao.deleteKey(modelAction.getBatch_id().toString());
			// 调用service层的方法处理增加模板的结果
			modelService.broadcastAddModelMul(inputStream, batch_list);
		}
			break;
		default:
			break;
		}

	}

	private void checkAlogrithmConnect() {
		// 检查与算法端的连接是否保持，如果断开的话，直接重连
		if (socketLoadTool.getAlgorithmSocket() == null || socketLoadTool.getAlgorithmSocket().isClosed() == true) {
			try {
				Socket socket = new Socket("127.0.0.1", new Integer(Const.PORT));
				socketLoadTool.setAlgorithmSocket(socket);
				algorithmSocket = socket;
				outputStream = algorithmSocket.getOutputStream();
				inputStream = algorithmSocket.getInputStream();
				System.out.println("断线重连成功");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("断线重连失败");
			}

		}
	}

}
