package com.hl.domain;

public class ModelAction extends Action{
	//继承自action类，增加或者修改发票模板类
	private String url_suffix;
	private Integer image_size;
	private String url;
	private String json_model;
	private Integer model_id;
	public String getUrl_suffix() {
		return url_suffix;
	}
	public void setUrl_suffix(String url_suffix) {
		this.url_suffix = url_suffix;
	}
	public Integer getImage_size() {
		return image_size;
	}
	public void setImage_size(Integer image_size) {
		this.image_size = image_size;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getJson_model() {
		return json_model;
	}
	public void setJson_model(String json_model) {
		this.json_model = json_model;
	}
	public Integer getModel_id() {
		return model_id;
	}
	public void setModel_id(Integer model_id) {
		this.model_id = model_id;
	}
	
	
}
