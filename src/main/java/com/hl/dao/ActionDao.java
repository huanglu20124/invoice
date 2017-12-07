package com.hl.dao;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;

import com.hl.domain.Action;
import com.hl.domain.ActionQuery;
import com.hl.domain.RecognizeAction;

public interface ActionDao {

	public List<Action> getTwentyActionByTime(Integer page,String startTime,String endTime);
	public Integer addAction(final Action action);
	public Map<String, Object> getMaxAndMin(String startTime, String endTime);
	public ActionQuery solrGetTwentyActionByKeyword(Integer page, Integer max_id, Integer min_id, String keywrod)throws SolrServerException;
	public void solrAddUpdateAction(Action action);
	public String getUuid(Integer action_id);
	public Action getActionById(Integer action_id);
	public Integer getActionSumByTime(String startTime, String endTime);
	public void updateActionDescription(Integer action_id, String description);
	public ActionQuery getTwentyActionByKeywordIp(String startTime, String endTime, String keyword, Integer page);
	public ActionQuery getTwentyActionByKeywordUserName(String startTime, String endTime, String keyword, Integer page);
	public ActionQuery getTwentyActionByKeywordCompanyName(String startTime, String endTime, String keyword,
			Integer page);
	public ActionQuery getTwentyActionByKeywordDescription(String startTime, String endTime, String keyword,
			Integer page);
	public List<Action> getTwentyActionInit(Integer page);
	public Integer getActionSumInit();
}
