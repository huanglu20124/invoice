package com.hl.domain;

import java.util.List;
import java.util.Map;

public class ModelAction extends Action{
	//继承自action类，增加或者修改发票模板类 
	private Integer image_size;
	private Map<String, Object> json_model;//改为map类型
	private Integer model_id;
	//模板所在文件夹，例"image/model/增值税发票-20171110004814/"
	//后面加model.jpg即得到模板图片，加/original/1.jpg得到一张原图
	private String file_path;
	//批处理队列id，uuid生成，客户端确认之后，按照顺序将批处理队列的东西按顺序加到manage队列里
	//处理一个ModelAction的时候，扫描original下面的文件夹，将原图打包发给算法端
	private String batch_id;
	//模板名称，前端发过来的时候要提取出来，赋值
	private String model_label;
	//存储原图的名字
	public List<String>origins;
	
	public Integer getImage_size() {
		return image_size;
	}
	public void setImage_size(Integer image_size) {
		this.image_size = image_size;
	}

	public Integer getModel_id() {
		return model_id;
	}
	public void setModel_id(Integer model_id) {
		this.model_id = model_id;
	}
	public String getFile_path() {
		return file_path;
	}
	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}
	public String getBatch_id() {
		return batch_id;
	}
	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}
	public Map<String, Object> getJson_model() {
		return json_model;
	}
	public void setJson_model(Map<String, Object> json_model) {
		this.json_model = json_model;
	}
	public String getModel_label() {
		return model_label;
	}
	public void setModel_label(String model_label) {
		this.model_label = model_label;
	}
	public List<String> getOrigins() {
		return origins;
	}
	public void setOrigins(List<String> origins) {
		this.origins = origins;
	}
	
	
}
