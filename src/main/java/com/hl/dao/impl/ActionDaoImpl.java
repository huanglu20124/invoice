package com.hl.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.mahout.common.distance.CosineDistanceMeasure;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.hl.dao.ActionDao;
import com.hl.domain.Action;
import com.hl.domain.Model;
import com.hl.util.Const;

public class ActionDaoImpl extends JdbcDaoSupport implements ActionDao{

	@Override
	public List<Action> getTwentyAction(Integer page) {
		//一次获取二十条日志
		String sql = "select c.*, a.user_name, b.company_name "
				+ " from user a, company b, action c "
				+ " where a.user_id=c.user_id and b.company_id=c.company_id "
				+ " LIMIT ?,20";
		int begin = page*20;
		return getJdbcTemplate().query(sql, new ActionMapper(),begin);
	}
	
	class ActionMapper implements RowMapper<Action>{

		@Override
		public Action mapRow(ResultSet rs, int rowNum) throws SQLException {
			Action action = new Action();
			action.setAction_id(rs.getInt(Const.ACTION_ID));
			action.setAction_end_time(rs.getString(Const.ACTION_END_TIME));
			action.setAction_start_time(rs.getString(Const.ACTION_START_TIME));
			action.setAction_run_time(rs.getString(Const.ACTION_RUN_TIME));
			action.setCompany_id(rs.getInt(Const.COMPANY_ID));
			action.setCompany_name(rs.getString(Const.COMPANY_NAME));
			action.setUser_id(rs.getInt(Const.USER_ID));
			action.setUser_name(rs.getString(Const.USER_NAME));
			action.setMsg_id(rs.getInt(Const.MSG_ID));
			return action;
		}
		
	}
}
