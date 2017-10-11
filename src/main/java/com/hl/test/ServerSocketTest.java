package com.hl.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.hl.domain.ResponseMessage;
import com.hl.socket.SocketUtil;
import com.hl.util.IOUtil;

public class ServerSocketTest {
	public static void main(String[] args) throws InterruptedException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(8889);
			Socket socket = null;
			InputStream inputStream = null;
			PrintWriter printWriter = null;
			OutputStream outputStream = null;
			while (true) {
				try {
					System.out.println("开启监听");
					socket = serverSocket.accept();
					inputStream = socket.getInputStream();
					System.out.println("新增连接");
					outputStream = socket.getOutputStream();
//					Thread.sleep(60000);
//					msg2(outputStream);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void msg1(OutputStream outputStream) {
		try {
			int msg_id = 1;
			Map<String, Object> map1 = new HashMap<>();
			map1.put("status", 0);
			map1.put("发票类型", "定额发票");
			double money = 200.005;
			map1.put("金额", money);
			map1.put("客户名称", "中山大学");
			map1.put("发票号码", "1516949849");
			map1.put("日期", "1996-11-18");
			map1.put("时间", "这是时间");
			map1.put("具体信息", "这是发票啊啊啊啊啊");
			map1.put("身份证号码", "588787485498498");
			map1.put("region_num", 11);
			String json_str = JSON.toJSONString(map1);
			outputStream.write(messageToByte(msg_id, json_str));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void msg100(OutputStream outputStream) {
		try {
			int msg_id = 100;
			Map<String, Object> json_map = new HashMap<>();
			json_map.put("type", 1);
			json_map.put("status", 0);
			String json_str = JSON.toJSONString(json_map);
			outputStream.write(messageToByte(msg_id, json_str));

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void msg101(OutputStream outputStream) {
		try {
			int msg_id = 101;
			Map<String, Object> json_map = new HashMap<>();
			json_map.put("type", 3);
			json_map.put("status", 0);
			String json_str = JSON.toJSONString(json_map);
			outputStream.write(messageToByte(msg_id,json_str));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void msg2(OutputStream outputStream){
		try {
			int msg_id = 2;
			Map<String, Object>map = new HashMap<>();
			map.put("status", 0);
			map.put("id", 2);
			outputStream.write(messageToByte(msg_id, JSON.toJSONString(map)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	private static byte[] messageToByte(int msg_id,String json_str) throws IOException{
		
		byte[] json_str_bytes;
		json_str_bytes = json_str.getBytes("gbk");
		int json_len = json_str_bytes.length;
		byte[] msg_id_byte = IOUtil.intToByteArray(msg_id);
		byte[] msg_len_byte = IOUtil.intToByteArray(json_len);

		byte[] ans_byte = new byte[8 + json_len];
		System.arraycopy(msg_id_byte, 0, ans_byte, 0, 4);
		System.arraycopy(msg_len_byte, 0, ans_byte, 4, 4);
		System.arraycopy(json_str_bytes, 0, ans_byte, 8, json_len);
		
		System.out.println(msg_id_byte.length + "---" + msg_len_byte.length + "---" + json_len);
		System.out.println(
				"算法服务器已发送消息:msg_id=" + msg_id + "总长度为" + (msg_id_byte.length + msg_len_byte.length + json_len));
		
		return ans_byte;
	}
}
