package com.hl.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.hl.dao.UserDao;
import com.hl.domain.Permission;
import com.hl.domain.User;
import com.hl.util.Const;
import com.sun.org.apache.bcel.internal.generic.ReturnaddressType;

public class UserDaoImpl extends JdbcDaoSupport implements UserDao{

	@Override
	public User getUserByNamePwd(String user_name, String user_password) {
		//简单的用户名密码登录
		try {
			String sql = "select * from user where user_name = ? and user_password = ?";
			return getJdbcTemplate().queryForObject(sql, new UserRowmapper(),user_name,user_password);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String getNameById(Integer user_id) {
		String sql = "select user_name from user where user_id=?";
		return getJdbcTemplate().queryForObject(sql, String.class,user_id);
	}
	
	@Override
	public String getUserCompanyNmae(Integer user_id) {
		//查询所属的公司
		String sql = "select a.company_name from company a, user_company b "
				+ " where a.company_id=b.company_id and b.user_id=?";
		return getJdbcTemplate().queryForObject(sql, String.class,user_id);
	}

	
	@Override
	public User getUserById(Integer user_id) {
		try {
			String sql = "select * from user where user_id = ?";
			return getJdbcTemplate().queryForObject(sql, new UserRowmapper(),user_id);
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}

	}

	@Override
	public User getUserByName(String user_name) {
		try {
			String sql = "select * from user where user_name = ?";
			return getJdbcTemplate().queryForObject(sql, new UserRowmapper(),user_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	@Override
	public List<Permission> getUserPermission(Integer user_id) {
		String sql = "select a.permission_id, a.permission_name, a.permission_url "
				+ " from permission a, user_permission b where a.permission_id=b.permission_id and  user_id = ?";
		return getJdbcTemplate().query(sql,new PermissionRowmapper(),user_id);
	}
	
	class PermissionRowmapper implements RowMapper<Permission>{

		@Override
		public Permission mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		    Permission permission = new Permission();
		    permission.setPermission_id(resultSet.getInt(Const.PERMISSION_ID));
		    permission.setPermission_name(resultSet.getString(Const.PERMISSION_NAME));
		    permission.setPermission_url(resultSet.getString(Const.PERMISSION_URL));
			return permission;
		}
		
	}
	
	class UserRowmapper implements RowMapper<User>{
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setUser_name(rs.getString(Const.USER_NAME));
			user.setUser_password(rs.getString(Const.USER_PASSWORD));
			user.setUser_id(rs.getInt(Const.USER_ID));
			user.setCompany_id(rs.getInt(Const.COMPANY_ID));
			user.setUser_register_time(rs.getString(Const.USER_REGISTER_TIME));
			user.setSalt(rs.getString(Const.SALT));
			user.setLocked(rs.getInt(Const.LOCKED));
			user.setCompany_name(rs.getString(Const.COMPANY_NAME));
			return user;
		}
		
	}

}
