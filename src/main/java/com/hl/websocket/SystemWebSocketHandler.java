package com.hl.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSON;
import com.hl.dao.RedisDao;
import com.hl.service.InvoiceService;
import com.hl.util.Const;
import com.hl.util.SocketLoadTool;

@Component("systemWebSocketHandler")
public class SystemWebSocketHandler implements WebSocketHandler {
	
	@Resource(name = "socketListener")
	private SocketLoadTool socketListener;
	
	@Resource(name = "redisDao")
	private RedisDao redisDao;
	
	private Logger log = LoggerFactory.getLogger(SystemWebSocketHandler.class);
	
	@Resource(name = "invoiceService")
	private InvoiceService invoiceService;
	
    private static final ArrayList<WebSocketSession> users = new ArrayList<WebSocketSession>();;
 

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	System.out.println("通过websocket与web前端建立连接");
    	log.debug("ConnectionEstablished");
        users.add(session); //把当前会话添加到用户列表里
        //连接建立后，立刻向用户返回识别队列的信息（头200个）
//        String ans_str = invoiceService.broadcastRecognizeWaitFirst();
//        session.sendMessage(new TextMessage(ans_str.getBytes("utf-8")));
    }
    //接收消息，（可选）返回消息
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    	System.out.println("接收到消息" + message.toString());
//    	Object object = message.getPayload();
//    	if(object instanceof String){
//         	String message_str = (String) message.getPayload();
//        	System.out.println(message_str);
//        	if(!message_str.startsWith("java")){
//            	String json_str = (String) message.getPayload();
//            	Map<String, Object>client_map = JSON.parseObject(json_str);
//            	Integer code = (Integer) client_map.get(Const.CODE);
//            	System.out.println("来自客户端的code为" + code);
//            	session.sendMessage(new TextMessage("测试用，服务器已收到code=0"));
//            	sendMessageToUsers(new TextMessage("测试用，服务器已收到code=0"));
//            	switch (code) {
//        		case 0:
//        			Map<String, Object> ans_map = new HashMap<>();
//        			invoiceService.openConsole(ans_map);
//        			System.out.println(ans_map);
//        			session.sendMessage(new TextMessage(JSON.toJSONString(ans_map)));
//        			System.out.println("websocekt推送code = 0消息完毕");
//        			break;
//    
//        		default:
//        			break;
//        		}
//        	}else {
//				System.out.println("接收到心跳包");
//			}
//    	}
    }
 
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        users.remove(session);
        System.out.println("传输异常，websocket会话关闭");
        log.debug("handleTransportError" + exception.getMessage());
    }
 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        users.remove(session);
        log.debug("afterConnectionClosed" + closeStatus.getReason());
        System.out.println("会话正常关闭");
    }
 
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
 
    /**
     * 给所有在线用户发送消息
     *
     * @param message
     */
    public void sendMessageToUsers(TextMessage message) {
    	try {
            for (WebSocketSession user : users) {
                try {
                    if (user.isOpen()) {
                        user.sendMessage(message);
                        System.out.println("成功群发消息");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    
    
 
}
