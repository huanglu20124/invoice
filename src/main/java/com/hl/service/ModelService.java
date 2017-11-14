package com.hl.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.hl.domain.Model;
import com.hl.domain.ModelAction;

public interface ModelService {
	public void addOrUpdateInvoiceModel(Map<String, Object>ans_map, ModelAction modelAction,Integer thread_msg);
	public void broadcastAddNewModel(InputStream inputStream, ModelAction modelAction);
	public void deleteInvoiceModel(Map<String, Object> ans_map, Integer user_id, Integer model_id, Integer thread_msg);
	public void broadcastUpdateModel(InputStream inputStream, ModelAction modelAction);
	public void broadcastDeleteModel(InputStream inputStream, ModelAction modelAction);
	public void getAllModel(Map<String, Object> ans_map, Integer user_id, Integer start);
	public void deleteAllModel(Map<String, Object> ans_map, Integer user_id, Integer thread_msg);
	public void broadcastClearModel(InputStream inputStream, Integer integer);
	public void rewriteJsonModel()throws Exception;
	public List<Model> searchModelLabel(Integer page,Integer user_id, String keywords);
}
