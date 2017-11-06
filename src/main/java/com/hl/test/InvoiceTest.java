package com.hl.test;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.apache.shiro.crypto.hash.Md5Hash;
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

	@Test
	public void md5() throws Exception {
		//原始 密码 
		String source = "123";
		//盐
		String salt = "asdre";
		//散列次数
		int hashIterations = 1;
		//构造方法中：
		//第一个参数：明文，原始密码 
		//第二个参数：盐，通过使用随机数
		//第三个参数：散列的次数，比如散列两次，相当 于md5(md5(''))
		Md5Hash md5Hash = new Md5Hash(source, salt, hashIterations);
		String password_md5 =  md5Hash.toString();
		System.out.println(password_md5);
	}

}
