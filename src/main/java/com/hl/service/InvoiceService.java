package com.hl.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import com.hl.domain.Invoice;
import com.hl.domain.RecognizeAction;
import com.hl.domain.TestCase;

public interface InvoiceService {
	public String addRecognizeInvoice(RecognizeAction action,TestCase testCase,List<String>url_suffixs,Integer thread_msg);
	public void broadcastRecognizeProcess(InputStream inputStream,OutputStream outputStream,Integer delay);
	public String broadcastRecognizeWaitFirst();
	public String openConsole(Integer delay);
	public void broadcastNextRecognize(Invoice invoice);
	public void changeImageUrlIp();
	public void UpdateRecognizeSpeed(Map<String, Object> ans_map, Integer user_id,Integer delay,ServletContext servletContext);
	public void broadcastRegionList();
	public List<Invoice> getTwentyFaultQueue(Integer page);
}
