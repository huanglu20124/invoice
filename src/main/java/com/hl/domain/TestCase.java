package com.hl.domain;

import java.util.List;

public class TestCase {
	private String test_name;
	private String pic_path;
	private String check_path;
	private List<String> area_names;
	private Integer page;
	private Integer pic_num;
	private List<Integer>pic_indexs; 
	
	public Integer getPic_num() {
		return pic_num;
	}
	public void setPic_num(Integer pic_num) {
		this.pic_num = pic_num;
	}
	public Integer getPage() {
		return page;
	}
	public void setPage(Integer page) {
		this.page = page;
	}
	public String getTest_name() {
		return test_name;
	}
	public void setTest_name(String test_name) {
		this.test_name = test_name;
	}
	public String getPic_path() {
		return pic_path;
	}
	public void setPic_path(String pic_path) {
		this.pic_path = pic_path;
	}
	public String getCheck_path() {
		return check_path;
	}
	public void setCheck_path(String check_path) {
		this.check_path = check_path;
	}
	public List<String> getArea_names() {
		return area_names;
	}
	public void setArea_names(List<String> area_names) {
		this.area_names = area_names;
	}
	public List<Integer> getPic_indexs() {
		return pic_indexs;
	}
	public void setPic_indexs(List<Integer> pic_indexs) {
		this.pic_indexs = pic_indexs;
	}
	
	
}
