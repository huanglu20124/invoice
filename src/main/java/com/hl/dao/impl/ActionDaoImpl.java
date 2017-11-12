package com.hl.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.hadoop.classification.InterfaceAudience.Private;
import org.apache.mahout.common.distance.CosineDistanceMeasure;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.hl.dao.ActionDao;
import com.hl.domain.Action;
import com.hl.domain.Model;
import com.hl.util.Const;
import com.hl.util.TimeUtil;

public class ActionDaoImpl extends JdbcDaoSupport implements ActionDao{
	
	@Resource(name = "solrServer")
	private SolrServer solrServer;
	
	@Override
	public List<Action> getTwentyActionByTime(Integer page,String startTime,String endTime) {
		//只根据时间日期,一次获取二十条日志
		String sql = "select c.*, a.user_name, b.company_name "
				+ " from user a, company b, action c "
				+ " where a.user_id=c.user_id and b.company_id=c.company_id "
				+ " and c.action_start_time >= ? and c.action_start_time <= ?"
				+ " LIMIT ?,20";
		int begin = page*20;
		Timestamp start = TimeUtil.StrToTimestamp(startTime);
		Timestamp end = TimeUtil.StrToTimestamp(endTime);
		if(start != null && end != null){
			return getJdbcTemplate().query(sql, new ActionMapper(),start,end,begin);
		}else {
			return null;
		}
		
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
			action.setAction_uuid(rs.getString("action_uuid"));
			return action;
		}
		
	}
	
	@Override
	public Integer addAction(final Action action) {
		//生成一条行为，插入action表，并获取返回的action_id(主键) 
		final String sql = "insert into invoice.action values(null,?,?,0,?,null,null,?,?)";
		final String action_start_time = com.hl.util.TimeUtil.getCurrentTime();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//返回主键
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public java.sql.PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				java.sql.PreparedStatement psm =  connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				psm.setInt(1,action.getUser_id());
				psm.setInt(2, action.getMsg_id());
				psm.setString(3, action_start_time);
				psm.setInt(4, action.getCompany_id());
				psm.setString(5, action.getAction_uuid());
				return psm;
			}
		},keyHolder);
		return keyHolder.getKey().intValue();
	}
	
	@Override
	public void runAction(Integer action_id) {
		//更新action开始跑的时间
		String sql = "update action set action_run_time = ? where action_id = ?";
		getJdbcTemplate().update(sql,com.hl.util.TimeUtil.getCurrentTime(),action_id);	
	}

	@Override
	public void finishAction(Integer action_id, int status) {
		//更新action完成的时间
		String sql = "update action set action_end_time = ?, status = ? where action_id = ?";
		getJdbcTemplate().update(sql,com.hl.util.TimeUtil.getCurrentTime(),status,action_id);	
	}

	@Override
	public Map<String, Object> getMaxAndMin(String startTime, String endTime) {
		String sql = "select MAX(action_id) as 'max', MIN(action_id) as 'min' from action "
				+ " where action_start_time > ? and action_start_time < ?";
		return getJdbcTemplate().queryForMap(sql,startTime,endTime);
	}

	@Override
	public List<Action> getTwentyActionByKeywords(Integer page, Integer max_id, Integer min_id, String keywrods) throws SolrServerException {
		System.out.println("page=" + page +" max_id=" + max_id + " min_id=" + min_id + " keywrods=" + keywrods);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(keywrods);
		//设置默认域
		solrQuery.set("df", "action_keywords");
		//设置分页以及过滤
		solrQuery.set("fq", "action_id:["+ min_id + " TO " +max_id +"]");
		solrQuery.setStart(page * 20);
		solrQuery.setRows(20);
		
		//得到结果
		QueryResponse response = solrServer.query(solrQuery);
		// 文档结果集
		SolrDocumentList docs = response.getResults();
		System.out.println("记录条数为=" + docs.getNumFound());
		List<Action>actions = new ArrayList<>();
		for(SolrDocument document : docs){
			Action action = new Action();
			action.setAction_id((Integer) document.get("action_id"));
			action.setMsg_id((Integer) document.get("msg_id"));
			action.setAction_start_time((String) document.get("action_start_time"));
			action.setAction_run_time((String) document.get("action_run_time"));
			action.setAction_end_time((String) document.get("action_end_time"));
			action.setCompany_id((Integer) document.get("company_id"));
			action.setCompany_name((String) document.get("company_name"));
			action.setUser_id((Integer) document.get("user_id"));
			action.setUser_name((String) document.get("user_name"));
			actions.add(action);
		}
		return actions;
	}
	
	
}
