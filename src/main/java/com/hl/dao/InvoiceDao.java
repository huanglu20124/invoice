package com.hl.dao;

import java.util.List;
import java.util.Map;

import com.hl.domain.Action;
import com.hl.domain.Invoice;
import com.hl.domain.Model;
import com.hl.domain.RecognizeAction;

public interface InvoiceDao {

	int addRecognizeInvoice(Map<String, Object> invoice_data,Invoice invoice);
	void deleteAllInvoiceForeginModel();
	void deleteInvoiceForeginModel(int model_id);
}
