package com.hl.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import com.hl.domain.Action;
import com.hl.domain.Invoice;
import com.hl.domain.ModelAction;
import com.hl.domain.RecognizeAction;

public interface InvoiceService {
	public void addRecognizeInvoice(Map<String, Object> ans_map, RecognizeAction action,List<String>url_suffixs,Integer thread_msg);
	public void addOrUpdateInvoiceModel(Map<String, Object>ans_map, ModelAction modelAction,Integer thread_msg);
	public void broadcastRecognizeProcess(InputStream inputStream,OutputStream outputStream,Integer delay);
	public void broadcastAddNewModel(InputStream inputStream, int action_id,Map<String, Object>json_model_map,String url);
	public void deleteInvoiceModel(Map<String, Object> ans_map, Integer user_id, Integer model_id, Integer thread_msg);
	public void broadcastUpdateModel(InputStream inputStream, Integer integer, Map<String, Object> json_model_map,
			String url,int model_id);
	public void broadcastDeleteModel(InputStream inputStream, Integer integer, int model_id);
	public void getAllModel(Map<String, Object> ans_map, Integer user_id, Integer start);
	public String broadcastRecognizeWaitFirst();
	public void deleteAllModel(Map<String, Object> ans_map, Integer user_id, Integer thread_msg);
	public void broadcastClearModel(InputStream inputStream, Integer integer);
	public String openConsole();
	public void broadcastNextRecognize(Invoice invoice);
	public void changeImageUrlIp();
	public void rewriteJsonModel()throws Exception;
	public void UpdateRecognizeSpeed(Map<String, Object> ans_map, Integer user_id,Integer delay,ServletContext servletContext);
}
