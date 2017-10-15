package com.hl.dao;

import java.util.List;
import java.util.Map;

import com.hl.domain.Model;

public interface InvoiceDao {

	Integer addRecognizeAction(Integer user_id);
	Integer addNewModelAction(final Integer user_id);

	int addRecognizeInvoice(Map<String, Object> invoice_data,Integer model_id,String url);

	int finishRecognizeAction(int action_id, int invoice_id, int status);

	int startAction(int action_id);
	void addModel(int model_id, Map<String, Object> json_map, String currentTime,String url);
	void finishAddModelAction(int action_id, int model_id,int status);
	void updateModel(int model_id, String jsonString,String url);
	Integer addUpdateModelAction(Integer user_id, Integer model_id);
	void finishUpdateModelAction(Integer action_id, int status);
	void deleteInvoiceForeginModel(int model_id);
	void deleteActionForeginModel(int model_id);
	void deleteModel(int model_id);
	void finishDeleteModelAction(Integer action_id, int status);
	List<Model>getTwelveModel(Integer start);
	Map<String, Object> getOneAction(Integer action_id);
	String getModelUrl(int model_id);
	void deleteAllInvoiceForeginModel();
	void deleteAllActionForeginModel();
	List<String> getAllModelUrl();
	List<Integer> getBiggerModelId(int model_id);
	void minusModelId(Integer id);
	void clearAllModel();
	Map<String, Object> findActionUserNameTime(Integer action_id);
	void plusModelSuccess(Integer model_id);
	void updateModelUrl(String url,String changed_url);
	void updateModelJsonModel(int model_id, String string);



}
