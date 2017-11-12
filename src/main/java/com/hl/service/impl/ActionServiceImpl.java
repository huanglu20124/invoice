package com.hl.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import com.hl.dao.ActionDao;
import com.hl.domain.Action;
import com.hl.service.ActionService;

@Service("actionService")
public class ActionServiceImpl implements ActionService{

	@Resource(name = "actionDao")
	private ActionDao actionDao;
	
	@Override
	public List<Action> getTwentyActionByTime(Integer page,String startTime,String endTime) {
		//一次获取二十条日志
		List<Action>actions = actionDao.getTwentyActionByTime(page,startTime,endTime);
		return actions;
	}

	@Override
	public List<Action> getTwentyActionByKeywords(Integer page, String startTime, String endTime, String keywrods) {
		//日期结合关键字查询
		//先找到最大和最小的action_id范围
		Map<String,Object>id_map = actionDao.getMaxAndMin(startTime,endTime);
		Integer max_id = (Integer) id_map.get("max");
		Integer min_id = (Integer) id_map.get("min");
		
		List<Action> action_list;
		try {
			action_list = actionDao.getTwentyActionByKeywords(page,max_id,min_id,keywrods);
		} catch (SolrServerException e) {
			e.printStackTrace();
			return null;
		}
		return action_list;
	}

}
