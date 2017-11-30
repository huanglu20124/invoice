package com.hl.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.alibaba.fastjson.JSON;
import com.hl.dao.UserDao;
import com.hl.domain.Group;
import com.hl.domain.Permission;
import com.hl.domain.User;
import com.hl.util.Const;

public class UserDaoImpl extends JdbcDaoSupport implements UserDao{
	
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
			user.setGroup_id(rs.getInt("group_id"));
			return user;
		}		
	}
	
	class GroupRowMapper implements RowMapper<Group>{
		@Override
		public Group mapRow(ResultSet rs, int rowNum) throws SQLException {
			Group group = new Group();
			group.setGroup_id(rs.getInt("group_id"));
			group.setCompany_id(rs.getInt("company_id"));
			group.setCompany_name(rs.getString("company_name"));
			group.setGroup_name(rs.getString("group_name"));
			group.setGroup_register_time(rs.getString("group_register_time"));
			return group;
		}
		
	}
	
	@Override
	public User getUserByNamePwd(String user_name, String user_password) {
		//简单的用户名密码登录
		try {
			String sql = "select a.*,b.company_name from user a, company b "
					+ " where a.user_name = ? and a.user_password = ? and a.company_id=b.company_id";
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
			String sql = "select a.*, b.company_name from user a, company b "
					+ " where a.user_id = ?  and a.company_id=b.company_id";
			return getJdbcTemplate().queryForObject(sql, new UserRowmapper(),user_id);
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}

	}

	@Override
	public User getUserByName(String user_name) {
		try {
			String sql = "select a.*, b.company_name from user a, company b "
					+ " where a.user_name = ?  and a.company_id=b.company_id";
			return getJdbcTemplate().queryForObject(sql, new UserRowmapper(),user_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	@Override
	public List<Permission> getUserPermission(Integer user_id) {
		String sql = "select a.*"
				+ " from permission a, user_permission b where a.permission_id=b.permission_id and  user_id = ?";
		return getJdbcTemplate().query(sql,new PermissionRowmapper(),user_id);
	}
	
	class PermissionRowmapper implements RowMapper<Permission>{

		@Override
		public Permission mapRow(ResultSet resultSet, int rowNum) throws SQLException {
		    Permission permission = new Permission();
		    permission.setPermission_id(resultSet.getInt(Const.PERMISSION_ID));
		    permission.setPermission_name(resultSet.getString(Const.PERMISSION_NAME));
			return permission;
		}
		
	}
	@Override
	public List<Permission> getGroupPermission(Integer group_id) {
		//获取当前用户组拥有的全部权限
		String sql = "select b.* from group_permission a, permission b "
				+ " where a.permission_id=b.permission_id and a.group_id=?";
		return getJdbcTemplate().query(sql, new PermissionRowmapper(),group_id);
	}

	@Override
	public List<User> getManagerUsers(Integer user_id) {
		//获取所属单位全部用户
		String sql = "select a.*,b.company_name from user a, company b  "
				+ " where a.company_id=b.company_id  and a.company_id= "
				+ "(select b.company_id from user b where b.user_id=?)";
		return getJdbcTemplate().query(sql, new UserRowmapper(),user_id);
	}

	@Override
	public boolean getIsUserPermission(Integer user_id, String permission_name) {
		String sql = "select a.permission_id from user_permission a,permission b"
				+ " where a.user_id=? and a.permission_id=b.permission_id "
				+ " and b.permission_name=?";
		try {
			Integer temp = getJdbcTemplate().queryForObject(sql, Integer.class,user_id,permission_name);
			if(temp != null) return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	public void deleteUserPermission(Integer user_id, String permission_name) {
		String sql = "delete from user_permission where user_id=? and permission_id = "
				+ " (select permission_id from permission where permission_name=?)";
		getJdbcTemplate().update(sql,user_id,permission_name);
	}

	@Override
	public void addUserPermission(Integer user_id, String permission_name) {
		String sql = "insert into user_permission values((select permission_id from permission where permission_name=?),?)";
		getJdbcTemplate().update(sql,permission_name,user_id);
	}

	
	
	@Override
	public List<Group> getManagerGroups(Integer user_id) {
		String sql = "select b.*,c.company_name from user a, user_group b, company c "
				+ " where a.company_id=b.company_id and a.company_id=c.company_id "
				+ " and  a.user_id=?";
		return getJdbcTemplate().query(sql, new GroupRowMapper(),user_id);
	}

	
	@Override
	public boolean getIsGroupPermission(String permission_name, Integer group_id) {
		String sql = "select a.permission_id from group_permission a,permission b"
				+ " where a.group_id=? and a.permission_id=b.permission_id "
				+ " and b.permission_name=?";
		try {
			Integer temp = getJdbcTemplate().queryForObject(sql, Integer.class,group_id,permission_name);
			if(temp != null) return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	@Override
	public void deleteGroupPermission(Integer group_id, String permission_name) {
		String sql = "delete from group_permission where group_id=? and permission_id = "
				+ " (select permission_id from permission where permission_name=?)";
		getJdbcTemplate().update(sql,group_id, permission_name);
	}

	@Override
	public void addGroupPermission(Integer group_id, String permission_name) {
		String sql = "insert into group_permission values(?,(select permission_id from permission where permission_name=?))";
		getJdbcTemplate().update(sql,group_id,permission_name);
	}

	
	@Override
	public void addGroupUser(Integer user_id, Integer group_id) {
		String sql = "update user set user_id=? and group_id=?";
		getJdbcTemplate().update(sql,user_id,group_id);
	}

	@Override
	public void removeGroupUser(Integer user_id) {
		String sql = "update user set group_id=null where user_id=?";
		getJdbcTemplate().update(sql,user_id);	
	}

	@Override
	public List<User> getGroupUser(Integer group_id, Integer company_id) {
		String sql = "select * from user where group_id=? and company_id=?";
		return getJdbcTemplate().query(sql, new UserRowmapper(),group_id,company_id);
	}

	
}
