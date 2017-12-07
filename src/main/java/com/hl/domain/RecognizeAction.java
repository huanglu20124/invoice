package com.hl.domain;

import java.util.List;

public class RecognizeAction extends Action{
	//继承自action类，识别队列专用的类
	private List<Invoice>invoice_list;
	
	public List<Invoice> getInvoice_list() {
		return invoice_list;
	}

	public void setInvoice_list(List<Invoice> invoice_list) {
		this.invoice_list = invoice_list;
	}

	
	
}
