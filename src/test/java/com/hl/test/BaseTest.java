package com.hl.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:applicationContext-commons.xml",
	"classpath:applicationContext-dao.xml",
	"classpath:applicationContext-dataBase.xml",
	"classpath:applicationContext-shiro.xml",
	"classpath:springmvc-context.xml"})
public class BaseTest {
	@Before
	public void init() {
		System.out.println("测试开始");
	}
	
	@Test
	public void defaultTest() throws Exception {
		System.out.println("默认测试方法");
	}
	@After
	public void after() {
		System.out.println("测试完成");
	}
}
