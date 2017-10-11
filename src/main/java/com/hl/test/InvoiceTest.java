package com.hl.test;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hl.dao.InvoiceDao;
import com.hl.util.IOUtil;
import com.hl.util.TimeUtil;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:applicationContext.xml")
public class InvoiceTest {
//	@Resource(name = "invoiceDao")
//	private InvoiceDao invoiceDao;
	
	@Test
	public void test1() throws Exception {
		System.out.println(TimeUtil.getCurrentTime());
	}
	
	@Test
	public void test2() throws Exception {
		byte[]bytes = IOUtil.intToByteArray(100);
		for(byte b : bytes){
			System.out.println(b);
		}
		System.out.println(IOUtil.byteArrayToInt(bytes));
	}
}
