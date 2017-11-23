package com.hl.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.alibaba.fastjson.JSON;
import com.hl.dao.ModelDao;
import com.hl.domain.Model;
import com.hl.domain.ModelAction;
import com.hl.util.Const;

public class ModelDaoImpl extends JdbcDaoSupport implements ModelDao {

	@Override
	public void addModel(ModelAction modelAction) {
		//增加一个新模板
		String sql = "insert into model values(?,?,?,0,?,?,?,?);";
		String json_model = modelAction.getJson_model();
		Map<String, Object>json_map = JSON.parseObject(json_model);
		//获得json_model里的model_label
		Map<String, Object>global_setting_map = (Map<String, Object>) json_map.get("global_setting");
		String model_label = (String) global_setting_map.get("label");
		getJdbcTemplate().update(sql,modelAction.getModel_id(),
				json_model,modelAction.getAction_end_time(),
				modelAction.getUrl_suffix(),model_label,modelAction.getImage_size(),modelAction.getAction_id());
	}

	@Override
	public void updateModel(ModelAction modelAction) {
		String sql = "update model set json_model=?, model_url=?, model_label=?, model_register_time=?, action_id = ? where model_id=?";
		//获得json_model里的model_label
		Map<String, Object>json_map = JSON.parseObject(modelAction.getJson_model());
		Map<String, Object>global_setting_map = (Map<String, Object>) json_map.get("global_setting");
		String model_label = (String) global_setting_map.get("label");
		getJdbcTemplate().update(sql,modelAction.getJson_model(),modelAction.getUrl_suffix(),
				model_label,com.hl.util.TimeUtil.getCurrentTime(),modelAction.getAction_id(),modelAction.getModel_id());
		
	}
	
	@Override
	public void deleteModel(int model_id) {
		//删除model
		String sql = "delete from model where model_id = ?";
		getJdbcTemplate().update(sql,model_id);
	}
	
	@Override
	public String getModelUrl(int model_id) {
		String sql = "select model_url from model where model_id = ?";
		return getJdbcTemplate().queryForObject(sql, String.class,model_id);
	}
	
	@Override
	public List<String> getAllModelUrl() {
		//得到全部model_url
		String sql = "select model_url from model";
		return getJdbcTemplate().queryForList(sql,String.class);
	}
	
	@Override
	public List<Integer> getBiggerModelId(int model_id) {
		//得到全部大于某个model_id的id
		String sql = "select model_id from model where model_id > ? order by model_id asc";
		return getJdbcTemplate().queryForList(sql,Integer.class,model_id);
	}

	@Override
	public void minusModelId(Integer model_id) {
		//model_id减一
		String sql = "update model set model_id = model_id-1 where model_id = ?";
		getJdbcTemplate().update(sql,model_id);
	}
	
	@Override
	public void clearAllModel() {
		//删除全部model
		String sql = "delete from model";
		getJdbcTemplate().update(sql);
	}
	
	@Override
	public void plusModelSuccess(Integer model_id) {
		//识别成功次数加一
		String sql = "update model set model_success_counter = model_success_counter + 1  where model_id = ?";
		getJdbcTemplate().update(sql,model_id);	
	}

	@Override
	public void updateModelUrl(String url,String changed_url) {
		String sql = "update model set model_url = ? where model_url = ?";
		getJdbcTemplate().update(sql,changed_url,url);
	}

	@Override
	public void updateModelJsonModel(int model_id, String json_model) {
		String sql = "update model set json_model = ? where model_id = ?";
		getJdbcTemplate().update(sql,json_model,model_id);
	}

	@Override
	public String getModelLabel(Integer model_id) {
		String sql = "select model_label from model where model_id=?";
		return getJdbcTemplate().queryForObject(sql, String.class,model_id);
	}
	
	@Override
	public List<Model> getTwelveModel(Integer page) {
		if(page == 0){
			String sql = "select * from model order by model_id desc LIMIT 12";
			return getJdbcTemplate().query(sql,new ModelRowmapper());
		}else {
			Integer beagin = page * 12;
			String sql = "select * from model order by model_id desc LIMIT ?,12";
			return getJdbcTemplate().query(sql,new ModelRowmapper(),beagin);
		}
		
	}
	
	class ModelRowmapper implements RowMapper<Model>{
		@Override
		public Model mapRow(ResultSet rs, int rowNum) throws SQLException {
			Model model = new Model();
			model.setJson_model(rs.getString(Const.JSON_MODEL));
			model.setModel_id(rs.getInt(Const.MODEL_ID));
			model.setModel_register_counter(rs.getInt(Const.MODEL_SUCCESS_COUNTER));
			model.setModel_register_time(rs.getString(Const.MODEL_REGISTER_TIME));
			model.setModel_url(rs.getString(Const.MODEL_URL));
			model.setModel_label(rs.getString(Const.MODEL_LABEL));
			model.setImage_size(rs.getInt(Const.IMAGE_SIZE));
			model.setAction_id(rs.getInt(Const.ACTION_ID));
			return model;
		}
		
	}

	@Override
	public List<Model> searchModelLabel(Integer page,String keywords) {
		if(page == 0){
			String sql = "select * from model where model_label LIKE '%"+ keywords +"%' LIMIT 12";
			return getJdbcTemplate().query(sql, new ModelRowmapper());
		}else {
			String sql = "select * from model where model_label LIKE '%"+ keywords +"%' LIMIT ?,12";
			return getJdbcTemplate().query(sql, new ModelRowmapper(),page*12);
		}

	}

}
