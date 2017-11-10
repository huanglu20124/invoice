package com.hl.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.hl.dao.ActionDao;
import com.hl.domain.Action;
import com.hl.service.ActionService;

@Service("actionService")
public class ActionServiceImpl implements ActionService{

	@Resource(name = "actionDao")
	private ActionDao actionDao;
	
	@Override
	public List<Action> getTwentyAction(Integer page,String startTime,String endTime) {
		//一次获取二十条日志
		List<Action>actions = actionDao.getTwentyAction(page,startTime,endTime);
		return actions;
	}

}
