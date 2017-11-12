package com.hl.service;

import java.util.List;
import java.util.Map;

import com.hl.domain.Action;

public interface ActionService {

	List<Action> getTwentyActionByTime(Integer start,String startTime,String endTime);

	List<Action> getTwentyActionByKeywords(Integer page, String startTime, String endTime, String keywrods);

}
