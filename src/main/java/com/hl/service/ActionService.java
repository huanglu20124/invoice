package com.hl.service;

import java.util.List;
import java.util.Map;

import com.hl.domain.Action;
import com.hl.domain.ActionQuery;

public interface ActionService {

	ActionQuery getTwentyActionByTime(Integer start,String startTime,String endTime);

	ActionQuery getTwentyActionByKeyword(Integer page, String startTime, String endTime, String keywrods);

}
