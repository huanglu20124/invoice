package com.hl.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.stereotype.Service;

import com.hl.dao.ActionDao;
import com.hl.domain.Action;
import com.hl.domain.ActionQuery;
import com.hl.service.ActionService;

@Service("actionService")
public class ActionServiceImpl implements ActionService{

	@Resource(name = "actionDao")
	private ActionDao actionDao;
	
	@Override
	public ActionQuery getTwentyActionByTime(Integer page,String startTime,String endTime) {
		ActionQuery actionQuery = new ActionQuery();
		//一次获取二十条日志
		List<Action>actions = actionDao.getTwentyActionByTime(page,startTime,endTime);
		//获取总页数
		Integer sum = actionDao.getActionSumByTime(startTime,endTime);
		Integer page_sum = sum/20 +1;
		actionQuery.setPage_sum(page_sum);
		actionQuery.setAction_list(actions);
		return actionQuery;
	}

	@Override
	public ActionQuery getTwentyActionByKeyword(Integer page, String startTime, String endTime, String keyword) {
		//日期结合关键字查询
		//先找到最大和最小的action_id范围
		Map<String,Object>id_map = actionDao.getMaxAndMin(startTime,endTime);
		Integer max_id = (Integer) id_map.get("max");
		Integer min_id = (Integer) id_map.get("min");
		
		ActionQuery actionQuery = null;;
		try {
			actionQuery = actionDao.solrGetTwentyActionByKeyword(page,max_id,min_id,keyword);
		} catch (SolrServerException e) {
			e.printStackTrace();
			return null;
		}
		return actionQuery;
	}

}
