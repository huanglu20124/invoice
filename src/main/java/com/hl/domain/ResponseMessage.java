package com.hl.domain;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.hl.util.Const;

public class ResponseMessage {
	//算法服务器返回的一条信息
	private int msg_id;
	private int msg_len;
	private String json_str;
	public int getMsg_id() {
		return msg_id;
	}
	public void setMsg_id(int msg_id) {
		this.msg_id = msg_id;
	}
	public int getMsg_len() {
		return msg_len;
	}
	public void setMsg_len(int msg_len) {
		this.msg_len = msg_len;
	}
	public String getJson_str() {
		return json_str;
	}
	public void setJson_str(String json_str) {
		this.json_str = json_str;
	}
	
	public String getFinalMessage(int action_id){
		//加入一些关键信息
		Map<String, Object>ans_map = JSON.parseObject(json_str);
		ans_map.put(Const.ACTION_ID, action_id);
		ans_map.put(Const.MSG_ID,msg_id);
		return JSON.toJSONString(ans_map);
	}
	
	public String getFinalConsoleMessage(int action_id,String user_name,Integer user_id,String action_start_time){
		//控制台需要的消息，加入一些关键信息
		Map<String, Object>ans_map = JSON.parseObject(json_str);
		ans_map.put(Const.ACTION_ID, action_id);
		ans_map.put(Const.MSG_ID,msg_id);
		ans_map.put(Const.USER_ID, user_id);
		ans_map.put(Const.USER_NAME, user_name);
		ans_map.put(Const.ACTION_START_TIME, action_start_time);
		return JSON.toJSONString(ans_map);
	}
}
