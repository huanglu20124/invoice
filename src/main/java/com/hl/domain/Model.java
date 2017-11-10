package com.hl.domain;

import java.util.Map;

import com.alibaba.fastjson.JSON;

//模板类
public class Model {
	private Integer model_id;
	private String json_model;
	private String model_register_time;
	private Integer model_register_counter;
	private String model_url;
	private String model_label;
	private Integer image_size;
	private Integer action_id;
	public String getModel_label() {
		return model_label;
	}
	public void setModel_label(String model_label) {
		this.model_label = model_label;
	}
	public Integer getModel_id() {
		return model_id;
	}
	public void setModel_id(Integer model_id) {
		this.model_id = model_id;
	}
	public String getJson_model() {
		return json_model;
	}
	public void setJson_model(String json_model) {
		this.json_model = json_model;
	}
	public String getModel_register_time() {
		return model_register_time;
	}
	public void setModel_register_time(String model_register_time) {
		this.model_register_time = model_register_time;
	}
	public Integer getModel_register_counter() {
		return model_register_counter;
	}
	public void setModel_register_counter(Integer model_register_counter) {
		this.model_register_counter = model_register_counter;
	}
	public String getModel_url() {
		return model_url;
	}
	public void setModel_url(String model_url) {
		this.model_url = model_url;
	}
	public Integer getImage_size() {
		return image_size;
	}
	public void setImage_size(Integer image_size) {
		this.image_size = image_size;
	}
	public Integer getAction_id() {
		return action_id;
	}
	public void setAction_id(Integer action_id) {
		this.action_id = action_id;
	}

	
	
	
}