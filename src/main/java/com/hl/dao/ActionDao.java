package com.hl.dao;

import java.util.List;

import com.hl.domain.Action;

public interface ActionDao {

	List<Action> getTwentyAction(Integer page);

}
