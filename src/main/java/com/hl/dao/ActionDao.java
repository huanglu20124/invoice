package com.hl.dao;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;

import com.hl.domain.Action;

public interface ActionDao {

	public List<Action> getTwentyActionByTime(Integer page,String startTime,String endTime);
	public Integer addAction(final Action action);
	public void runAction(Integer action_id);
	public void finishAction(Integer action_id, int status);
	public Map<String, Object> getMaxAndMin(String startTime, String endTime);
	public List<Action> getTwentyActionByKeywords(Integer page, Integer max_id, Integer min_id, String keywrods)throws SolrServerException;

}
