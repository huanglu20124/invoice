package com.hl.dao;

import java.util.List;

import com.hl.domain.Model;
import com.hl.domain.ModelAction;
import com.hl.domain.ModelQuery;

public interface  ModelDao {
	int addModel(ModelAction modelAction);
	void updateModel(ModelAction modelAction);
	void deleteModel(int model_id);
	List<String> getAllModelUrl();
	List<Integer> getBiggerModelId(int model_id);
	void minusModelId(Integer id);
	void clearAllModel();
	void plusModelSuccess(Integer model_id);
	void updateModelUrl(String url,String changed_url);
	void updateModelJsonModel(int model_id, String string);
	String getModelLabel(Integer model_id);
	String getModelUrl(int model_id);
	ModelQuery getTwelveModel(Integer start);
	List<Model> searchModelLabel(Integer page,String keywords);
}
