package com.hl.service;

import com.hl.domain.ActionQuery;

public interface ActionService {

	ActionQuery getTwentyActionByTime(Integer start,String startTime,String endTime,Integer  section);

	ActionQuery getTwentyActionByKeyword(Integer page, String startTime, String endTime,
			String keywrods,Integer type,Integer  section);

	ActionQuery getTwentyActionInit(Integer page,Integer  section);


}
