package com.hl.domain;

import java.util.List;

public class ActionQuery {
	//日志查询类
	private Integer page_sum;
	private List<Action>action_list;
	public Integer getPage_sum() {
		return page_sum;
	}
	public void setPage_sum(Integer page_sum) {
		this.page_sum = page_sum;
	}
	public List<Action> getAction_list() {
		return action_list;
	}
	public void setAction_list(List<Action> action_list) {
		this.action_list = action_list;
	}
	
}
