package com.hl.dao.impl;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
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
import org.apache.solr.common.SolrInputDocument;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.hl.dao.ActionDao;
import com.hl.domain.Action;
import com.hl.domain.ActionQuery;
import com.hl.domain.Model;
import com.hl.domain.RecognizeAction;
import com.hl.util.Const;
import com.hl.util.TimeUtil;

public class ActionDaoImpl extends JdbcDaoSupport implements ActionDao{
	
	@Resource(name = "solrServer")
	private SolrServer solrServer;
	
	class ActionMapper implements RowMapper<Action>{

		@Override
		public Action mapRow(ResultSet rs, int rowNum) throws SQLException {
			Action action = new Action();
			action.setAction_id(rs.getInt(Const.ACTION_ID));			
			action.setCompany_id(rs.getInt(Const.COMPANY_ID));
			action.setCompany_name(rs.getString(Const.COMPANY_NAME));
			action.setUser_id(rs.getInt(Const.USER_ID));
			action.setUser_name(rs.getString(Const.USER_NAME));
			action.setAction_time(rs.getString("action_time"));
			action.setDescription(rs.getString("description"));
			action.setUser_ip(rs.getString("user_ip"));
			return action;
		}
		
	}
		
	@Override
	public Integer addAction(final Action action) {
		//生成一条行为，插入action表，并获取返回的action_id(主键) 
		final String sql = "insert into invoice.action values(null,?,?,?,?,?)";
		final String action_time = com.hl.util.TimeUtil.getCurrentTime();
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//返回主键
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public java.sql.PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				java.sql.PreparedStatement psm =  connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				psm.setInt(1,action.getUser_id());
				psm.setString(2, action.getUser_ip());
				psm.setInt(3, action.getCompany_id());
				psm.setString(4, action_time);
				psm.setString(5, action.getDescription());
				return psm;
			}
		},keyHolder);
		
		return keyHolder.getKey().intValue();
	}
	

	@Override
	public List<Action> getTwentyActionByTime(Integer page,String startTime,String endTime) {
		//只根据时间日期,一次获取二十条日志
		String sql = "select c.*, a.user_name, b.company_name "
				+ " from user a, company b, action c "
				+ " where a.user_id=c.user_id and b.company_id=c.company_id "
				+ " and c.action_time >= ? and c.action_time <= ?"
				+ " LIMIT ?,20";
		int begin = page*20;
		if(startTime != null && endTime != null){
			return getJdbcTemplate().query(sql, new ActionMapper(),startTime,endTime,begin);
		}else {
			return null;
		}
		
	}
	
	@Override
	public Map<String, Object> getMaxAndMin(String startTime, String endTime) {
		String sql = "select MAX(action_id) as 'max', MIN(action_id) as 'min' from action "
				+ " where action_time > ? and action_time < ?";
		return getJdbcTemplate().queryForMap(sql,startTime,endTime);
	}

	@Override
	public ActionQuery solrGetTwentyActionByKeyword(Integer page, Integer max_id, Integer min_id, String keyword) throws SolrServerException {
		//从索引库查询（弃用，日志并不适合全文检索）
		System.out.println("page=" + page +" max_id=" + max_id + " min_id=" + min_id + " keywrods=" + keyword);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(keyword);
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
			action.setAction_time((String) document.get("action_time"));
			action.setCompany_id((Integer) document.get("company_id"));
			action.setCompany_name((String) document.get("company_name"));
			action.setUser_id((Integer) document.get("user_id"));
			action.setUser_name((String) document.get("user_name"));
			actions.add(action);
		}
		
		ActionQuery actionQuery = new ActionQuery();
		actionQuery.setAction_list(actions);
		
		//再进行一次查询，获得总条数
		SolrQuery solrQuery_sum = new SolrQuery();
		solrQuery_sum.setQuery(keyword);
		//设置默认域
		solrQuery_sum.set("df", "action_keywords");
		//设置过滤
		solrQuery_sum.set("fq", "action_id:["+ min_id + " TO " +max_id +"]");
		//得到结果
		QueryResponse response_sum = solrServer.query(solrQuery_sum);
		SolrDocumentList docs_sum = response_sum.getResults();
		Long sum = docs_sum.getNumFound();
		Integer page_sum = (sum.intValue()/20) + 1;
		actionQuery.setPage_sum(page_sum);
		return actionQuery;
	}

	
	@Override
	public void solrAddUpdateAction(Action action) {
		//弃用
		//添加到索引库
		SolrInputDocument document = new SolrInputDocument();
		//document.setField("id", action.getAction_uuid());
		document.setField("action_id", action.getAction_id());
		document.setField("user_id", action.getUser_id());
		document.setField("action_time", action.getAction_time());
		document.setField("company_id", action.getCompany_id());
		document.setField("company_name", action.getCompany_name());
		document.setField("user_name", action.getUser_name());
		try {
			solrServer.add(document);
			solrServer.commit();
			System.out.println("成功添加到索引库");
		} catch (SolrServerException e) {
			System.out.println("添加到索引库失败");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public String getUuid(Integer action_id) {
		String sql = "select action_uuid from action where action_id = ?";
		return getJdbcTemplate().queryForObject(sql, String.class,action_id);
	}

	
	@Override
	public Action getActionById(Integer action_id) {
		String sql = "select c.*, a.user_name, b.company_name "
				+ " from user a, company b, action c "
				+ " where a.user_id=c.user_id and b.company_id=c.company_id and c.action_id=?";
		return getJdbcTemplate().queryForObject(sql, new ActionMapper(),action_id);
	}

	@Override
	public Integer getActionSumByTime(String startTime, String endTime) {
		String sql = "select count(*) from action where action_time >= ? and action_time <= ?";
		return getJdbcTemplate().queryForObject(sql, Integer.class,startTime,endTime);
	}

	@Override
	public void updateActionDescription(Integer action_id, String description) {
		String sql = "update action set description=? where action_id=?";
		getJdbcTemplate().update(sql,description,action_id);
	}

	@Override
	public ActionQuery getTwentyActionByKeywordIp(String startTime, String endTime, String keyword, Integer page) {
		String sql1 = null;
		List<Action>list = null;
		if(startTime != null && endTime != null){
			 sql1 = "SELECT SQL_CALC_FOUND_ROWS a.*,b.user_name,c.company_name "
						+ " FROM action a, user b, company c "
						+ " where a.user_id=b.user_id and a.company_id=c.company_id "
						+ " and action_time>=? and action_time<=? "
						+ " and user_ip=? "
						+ " LIMIT ?,20";
			list = getJdbcTemplate().query(sql1, new ActionMapper(),startTime,endTime,keyword,page*20);
		}else {
			 sql1 = "SELECT SQL_CALC_FOUND_ROWS a.*,b.user_name,c.company_name "
						+ " FROM action a, user b, company c "
						+ " where a.user_id=b.user_id and a.company_id=c.company_id "
						+ " and user_ip=? "
						+ " LIMIT ?,20";
			 list = getJdbcTemplate().query(sql1, new ActionMapper(),keyword,page*20);
		}
		ActionQuery query = new ActionQuery();
		query.setAction_list(list);
		if(page == 0){
			//查询总页数
			String sql2 = "SELECT FOUND_ROWS();";
			Integer sum = getJdbcTemplate().queryForObject(sql2, Integer.class);
			query.setPage_sum(sum);
		}
		return query;
	}


	@Override
	public ActionQuery getTwentyActionByKeywordUserName(String startTime, String endTime, String keyword,
			Integer page) {
		String sql1 = null;
		List<Action>list = null;
		if(startTime != null && endTime != null){
			sql1 = "SELECT SQL_CALC_FOUND_ROWS a.*,b.user_name,c.company_name "
					+ " FROM action a, user b, company c "
					+ " where a.user_id=b.user_id and a.company_id=c.company_id "
					+ " and action_time>=? and action_time<=? "
					+ " and user_name LIKE '%" + keyword + "%' "
					+ " LIMIT ?,20";
			list = getJdbcTemplate().query(sql1, new ActionMapper(),startTime,endTime,page*20);
		}else {
			sql1 = "SELECT SQL_CALC_FOUND_ROWS a.*,b.user_name,c.company_name "
					+ " FROM action a, user b, company c "
					+ " where a.user_id=b.user_id and a.company_id=c.company_id "
					+ " and user_name LIKE '%" + keyword + "%' "
					+ " LIMIT ?,20";
			list = getJdbcTemplate().query(sql1, new ActionMapper(),page*20);
		}

		ActionQuery query = new ActionQuery();
		query.setAction_list(list);
		if(page == 0){
			//查询总页数
			String sql2 = "SELECT FOUND_ROWS();";
			Integer sum = getJdbcTemplate().queryForObject(sql2, Integer.class);
			query.setPage_sum(sum);
		}
		return query;
	}


	@Override
	public ActionQuery getTwentyActionByKeywordCompanyName(String startTime, String endTime, String keyword,
			Integer page) {
		String sql1 = null;
		List<Action>list = null;
		if(startTime != null && endTime != null){
			sql1 = "SELECT SQL_CALC_FOUND_ROWS a.*,b.user_name,c.company_name "
					+ " FROM action a, user b, company c "
					+ " where a.user_id=b.user_id and a.company_id=c.company_id "
					+ " and action_time>=? and action_time<=? "
					+ " and company_name LIKE '%" + keyword + "%' "
					+ " LIMIT ?,20";
			list = getJdbcTemplate().query(sql1, new ActionMapper(),startTime,endTime,page*20);
		}else {
			sql1 = "SELECT SQL_CALC_FOUND_ROWS a.*,b.user_name,c.company_name "
					+ " FROM action a, user b, company c "
					+ " where a.user_id=b.user_id and a.company_id=c.company_id "
					+ " and company_name LIKE '%" + keyword + "%' "
					+ " LIMIT ?,20";
			list = getJdbcTemplate().query(sql1, new ActionMapper(),page*20);
		}

		ActionQuery query = new ActionQuery();
		query.setAction_list(list);
		if(page == 0){
			//查询总页数
			String sql2 = "SELECT FOUND_ROWS();";
			Integer sum = getJdbcTemplate().queryForObject(sql2, Integer.class);
			query.setPage_sum(sum);
		}
		return query;
	}


	@Override
	public ActionQuery getTwentyActionByKeywordDescription(String startTime, String endTime, String keyword,
			Integer page) {
		String sql1 = null;
		List<Action>list = null;
		if(startTime != null && endTime != null){
			sql1 = "SELECT SQL_CALC_FOUND_ROWS a.*,b.user_name,c.company_name "
					+ " FROM action a, user b, company c "
					+ " where a.user_id=b.user_id and a.company_id=c.company_id "
					+ " and action_time>=? and action_time<=? "
					+ " and description LIKE '%" + keyword + "%' "
					+ " LIMIT ?,20";
			list = getJdbcTemplate().query(sql1, new ActionMapper(),startTime,endTime,page*20);
		}else {
			sql1 = "SELECT SQL_CALC_FOUND_ROWS a.*,b.user_name,c.company_name "
					+ " FROM action a, user b, company c "
					+ " where a.user_id=b.user_id and a.company_id=c.company_id "
					+ " and description LIKE '%" + keyword + "%' "
					+ " LIMIT ?,20";
			list = getJdbcTemplate().query(sql1, new ActionMapper(),page*20);
		}

		ActionQuery query = new ActionQuery();
		query.setAction_list(list);
		if(page == 0){
			//查询总页数
			String sql2 = "SELECT FOUND_ROWS();";
			Integer sum = getJdbcTemplate().queryForObject(sql2, Integer.class);
			query.setPage_sum(sum);
		}
		return query;
	}


	
	@Override
	public List<Action> getTwentyActionInit(Integer page) {
		//不分日期 关键字的分页查询
		String sql = "select c.*, a.user_name, b.company_name "
				+ " from user a, company b, action c "
				+ " where a.user_id=c.user_id and b.company_id=c.company_id "
				+ " LIMIT ?,20";
		int begin = page*20;
		return getJdbcTemplate().query(sql, new ActionMapper(),begin);
	}


	@Override
	public Integer getActionSumInit() {
		//获取日志总数
		String sql = "select count(*) from action";
		return getJdbcTemplate().queryForObject(sql, Integer.class);
	}
		
}
