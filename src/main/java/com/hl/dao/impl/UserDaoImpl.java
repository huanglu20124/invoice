package com.hl.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.hl.dao.UserDao;
import com.hl.domain.User;
import com.hl.util.Const;

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
	
	class UserRowmapper implements RowMapper<User>{
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setUser_name(rs.getString(Const.USER_NAME));
			user.setUser_password(rs.getString(Const.USER_PASSWORD));
			user.setUser_id(rs.getInt(Const.USER_ID));
			user.setCompany_id(rs.getInt(Const.COMPANY_ID));
			user.setCompany_name(rs.getString(Const.COMPANY_NAME));
			user.setUser_register_time(rs.getString(Const.USER_REGISTER_TIME));
			user.setUser_type(rs.getInt(Const.USER_TYPE));
			return user;
		}
		
	}
}
