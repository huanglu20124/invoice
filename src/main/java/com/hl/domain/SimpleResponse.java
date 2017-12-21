package com.hl.domain;

public class SimpleResponse {
	private String success;
	private String err;
	
	public SimpleResponse(String success,String err) {
		this.err = err;
		this.success = success;
	}
	
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getErr() {
		return err;
	}
	public void setErr(String err) {
		this.err = err;
	}
	
}
