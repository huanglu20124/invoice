package com.hl.service;

import java.util.List;
import java.util.Map;

import com.hl.domain.Action;

public interface ActionService {

	List<Action> getTwentyAction(Integer start,String startTime,String endTime);

}
