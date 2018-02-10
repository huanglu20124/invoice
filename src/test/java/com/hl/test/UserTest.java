package com.hl.test;

import static org.junit.Assert.*;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.hl.service.ModelService;
import com.hl.service.UserService;
import com.mysql.jdbc.TimeUtil;

public class UserTest extends BaseTest {
	
	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "modelService")
	private ModelService modelService;
	
	@Test
	public void test1() throws Exception {
		System.out.println(modelService.getAllModel (1, 1));
	}
}
