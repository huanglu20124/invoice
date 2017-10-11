package com.hl.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.socket.TextMessage;

import com.alibaba.fastjson.JSON;
import com.hl.domain.ResponseMessage;
import com.hl.websocket.SystemWebSocketHandler;

public class MessageUtil {
	//发送信息，接收信息相关的工具类
	
	//发送信息给算法端
	public static void sendMessage(OutputStream outputStream,int msg_id, String json_str,SystemWebSocketHandler systemWebSocketHandler){
		//消息长度为 4+4+len
		//最后一个参数用来广播错误数据
		try {
			if(json_str != null){
				byte[] json_byte = json_str.getBytes("gbk");
				int msg_len = json_byte.length;
				System.out.println(msg_len);
				int last_length = 8 + msg_len;
				byte[] msg_bytes = new byte[last_length];//确定最终消息长度
				byte[] msg_id_array = IOUtil.intToByteArray(msg_id);
				byte[] msg_len_array = IOUtil.intToByteArray(msg_len);	
				//复制到最终数组
				System.arraycopy(msg_id_array, 0, msg_bytes, 0, 4);
				System.arraycopy(msg_len_array, 0, msg_bytes, 4, 4);
				System.arraycopy(json_byte, 0, msg_bytes, 8, msg_len);
				outputStream.write(msg_bytes);
				outputStream.flush();
			}else {
				int msg_len = 0;
				byte[] msg_bytes = new byte[8];//确定最终消息长度
				byte[] msg_id_array = IOUtil.intToByteArray(msg_id);
				byte[] msg_len_array = IOUtil.intToByteArray(msg_len);
				System.arraycopy(msg_id_array, 0, msg_bytes, 0, 4);
				System.arraycopy(msg_len_array, 0, msg_bytes, 4, 4);
				outputStream.write(msg_bytes);
				outputStream.flush();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("发送消息异常");
			Map<String,Object>err_map = new HashMap<>();
			err_map.put(Const.ERR, "向服务器发送消息异常");
			systemWebSocketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(err_map)));
		}

	}
	
	//从算法端接收信息
	public static  ResponseMessage getMessage(InputStream inputStream) throws IOException {
		ResponseMessage message = new ResponseMessage();
		int readSize = 0;
		int msg_id = 0;
		int msg_len = 0;
		String json_str = null;

		byte[] cache = new byte[1024];// 缓冲区
		// 头部
		byte[] msg_id_array = new byte[4];
		// length部分
		byte[] msg_len_array = new byte[4];

		while ((readSize = inputStream.read(cache)) > 0) {
			System.out.println("readSize=" + readSize);
			// 分离出三段
			System.arraycopy(cache, 0, msg_id_array, 0, 4);
			msg_id = IOUtil.byteArrayToInt(msg_id_array);
			System.arraycopy(cache, 4, msg_len_array, 0, 4);
			msg_len = IOUtil.byteArrayToInt(msg_len_array);
			byte[] json_byte = new byte[msg_len];
			System.arraycopy(cache, 8, json_byte, 0, msg_len);
			json_str = new String(json_byte, "gbk");
			System.out.println("msg_id = " + msg_id);
			System.out.println("msg_len = " + msg_len);
			System.out.println("json_byte= " + json_str);
			message.setMsg_id(msg_id);
			message.setMsg_len(msg_len);
			message.setJson_str(json_str);
			return message;
		}
		return null;
	}

}
