package com.hl.test;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.jdbc.support.JdbcUtils;

import com.alibaba.fastjson.JSON;
import com.hl.util.JDBCUtil;
import com.hl.util.TimeUtil;
import com.mysql.jdbc.Connection;

public class TimeTest {
	@Test
	public void test1() throws Exception {
		String sql = "select * from company where company_register_time < ?";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = JDBCUtil.getConn();
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setTimestamp(1, TimeUtil.StrToTimestamp("2017-11-09 23:29:53"));
			resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				Timestamp timestamp2 = resultSet.getTimestamp("company_register_time");
				System.out.println(timestamp2);
			}
		} catch (Exception e) {
			JDBCUtil.close(preparedStatement, resultSet, connection);
		}
	}

	@Test
	public void test2() throws Exception {
		String sql = "insert into company values(null,test,1,null,null,0)";
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = JDBCUtil.getConn();
			preparedStatement = connection.prepareStatement(sql);
			//preparedStatement.setTimestamp(1, TimeUtil.StrToTimestamp(TimeUtil.getCurrentTime()));
			preparedStatement.execute();
		} catch (Exception e) {
			JDBCUtil.close(preparedStatement, resultSet, connection);
		}
	}

}
