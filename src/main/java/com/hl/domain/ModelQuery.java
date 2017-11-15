package com.hl.domain;

import java.util.List;

public class ModelQuery {
	private Integer page_sum;
	private List<Model>model_list;
	public Integer getPage_sum() {
		return page_sum;
	}
	public void setPage_sum(Integer page_sum) {
		this.page_sum = page_sum;
	}
	public List<Model> getModel_list() {
		return model_list;
	}
	public void setModel_list(List<Model> model_list) {
		this.model_list = model_list;
	}
	
}
