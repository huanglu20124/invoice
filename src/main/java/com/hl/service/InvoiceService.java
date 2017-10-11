package com.hl.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public interface InvoiceService {
	public void addRecognizeInvoice(Map<String, Object>ans_map, Integer user_id, List<String>image_urls,Integer thread_msg);
	public void addOrUpdateInvoiceModel(Map<String, Object>ans_map, Integer user_id,Map<String, Object>model_json_map,String url,Integer model_id,Integer thread_msg,Integer msg_id);
	public void broadcastRecognizeProcess(InputStream inputStream,int action_id,String url);
	public void broadcastAddNewModel(InputStream inputStream, int action_id,Map<String, Object>json_model_map,String url);
	public void deleteInvoiceModel(Map<String, Object> ans_map, Integer user_id, Integer model_id, Integer thread_msg);
	public void broadcastUpdateModel(InputStream inputStream, Integer integer, Map<String, Object> json_model_map,
			String url,int model_id);
	public void broadcastDeleteModel(InputStream inputStream, Integer integer, int model_id);
	public void getAllModel(Map<String, Object> ans_map, Integer user_id, Integer start);
	public String broadcastRecognizeWaitFirst();
	public void deleteAllModel(Map<String, Object> ans_map, Integer user_id, Integer thread_msg);
	public void broadcastClearModel(InputStream inputStream, Integer integer);
	public void openConsole(Map<String, Object> ans_map);
	public void broadcastNextRecognize(Integer action_id, String url);
	public void changeImageUrlIp();
}
