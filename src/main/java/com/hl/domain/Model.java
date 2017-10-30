package com.hl.domain;

import java.util.Map;

import com.alibaba.fastjson.JSON;

//模板类
public class Model {
	Integer model_id;
	String json_model;
	String model_register_time;
	Integer model_register_counter;
	String model_url;
	String model_label;
	Integer image_size;
	
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

	
	
	
}
