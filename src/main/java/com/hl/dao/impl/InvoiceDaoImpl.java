package com.hl.dao.impl;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.alibaba.fastjson.JSON;
import com.hl.dao.InvoiceDao;
import com.hl.domain.Action;
import com.hl.domain.Invoice;
import com.hl.domain.LocalConfig;
import com.hl.domain.Model;
import com.hl.domain.RecognizeAction;
import com.hl.util.Const;
import com.mysql.jdbc.TimeUtil;
import com.sun.istack.FinalArrayList;

public class InvoiceDaoImpl extends JdbcDaoSupport implements InvoiceDao{
	
	@Resource(name="localConfig")
	private LocalConfig localConfig;
	class FaultInvoiceRowmapper implements RowMapper<Invoice>{
		//只取部分前端需要的信息（错误发票）
		@Override
		public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
			Invoice invoice = new Invoice();
			invoice.setUser_name(rs.getString("user_name"));
			invoice.setCompany_name(rs.getString("company_name"));
			invoice.setInvoice_id(rs.getInt("invoice_id"));
			invoice.setModel_id(rs.getInt("model_id"));
			invoice.setInvoice_url(localConfig.getIp() + rs.getString("invoice_url"));
			invoice.setRecognize_time(rs.getString("recognize_time"));
			invoice.setRegion_list(rs.getString("region_list"));
			return invoice;
		}
		
	}
	@Override
	public int addRecognizeInvoice(Map<String, Object> invoice_data,final Invoice invoice) {
		//添加一条发票信息
		final String invoice_type = (String) invoice_data.get("发票类型");
		final String invoice_money = (String) invoice_data.get("金额");
		final String invoice_customer = (String) invoice_data.get("客户名称");
		final String invoice_code = (String) invoice_data.get("发票号码");
		final String invoice_date = (String) invoice_data.get("日期");
		final String invoice_time = (String) invoice_data.get("时间");
		final String invoice_detail = (String) invoice_data.get("具体信息");
		final String invoice_identity = (String) invoice_data.get("身份证号码");
		final int invoice_region_num = (int) invoice_data.get("region_num");
		final String sql = "insert into invoice values(null,?,0,null,?,"
				                                     + " ?,?,?,?,?,"
				                                     + " ?,?,?,?,?,"
				                                     + " ?,?,?,?,?,"
				                                     + "?,?);";
		KeyHolder keyHolder = new GeneratedKeyHolder();
		//返回主键
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement psm = connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				psm.setInt(1, invoice.getModel_id());
				psm.setString(2, invoice_type);
				psm.setString(3, invoice_money);
				psm.setString(4, invoice_customer);
				psm.setString(5, invoice_code);
				psm.setString(6, invoice_date);
				psm.setString(7, invoice_time);
				psm.setString(8, invoice_detail);
				psm.setString(9,invoice_identity);
				psm.setInt(10, invoice_region_num);
				psm.setString(11,invoice.getInvoice_url());
				psm.setString(12, invoice.getInvoice_image_id());
				psm.setString(13, invoice.getInvoice_note());
				psm.setInt(14,invoice.getAction_id());
				psm.setInt(15, invoice.getImage_size());
				psm.setInt(16, invoice.getInvoice_status());
				psm.setString(17, invoice.getRecognize_time());
				psm.setInt(18, invoice.getIs_fault());
				psm.setString(19, invoice.getRegion_list());
				return psm;
			}
		},keyHolder);
		return keyHolder.getKey().intValue();
	}

	@Override
	public void deleteInvoiceForeginModel(int model_id) {
		//找到全部携带该model_id的invoice，设置外键为null
		String sql = "update invoice set model_id = null where model_id = ?";
		getJdbcTemplate().update(sql,model_id);
	}

	@Override
	public void deleteAllInvoiceForeginModel() {
		//全部model_id!=null 的invoice，设置外键为null
		String sql = "update invoice set model_id = null";
		getJdbcTemplate().update(sql);
	}

	
	@Override
	public List<Invoice> getTwentyFaultInvoice(Integer page) {
		String sql = "select b.user_name, c.company_name, "
				+ " a.invoice_id, a.model_id, a.invoice_url, "
				+ " a.recognize_time, a.region_list "
				+ " from invoice a, user b, company c, action d "
				+ " where d.user_id=b.user_id and d.company_id=c.company_id and "
				+ " a.action_id=d.action_id and is_fault=1"
				+ " LIMIT ?,20";
		return getJdbcTemplate().query(sql, new FaultInvoiceRowmapper(),page);
	}


}
