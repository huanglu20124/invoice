package com.hl.test;

import static org.junit.Assert.*;

import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hl.dao.RedisDao;
import com.hl.util.ImageUtil;
import com.hl.util.Const;
import com.hl.util.IOUtil;

public class ImageTest {
	
	@Resource(name = "redisDao")
	private RedisDao redisDao;

	@Test
	public void test2() throws Exception {
		String image_url = "E:/invoice/originImage/123.jpg";
		int last_index = image_url.lastIndexOf("/");
		String absolute_path = image_url.substring(last_index + 1, image_url.length());
		System.out.println(absolute_path);
	}
	
	@Test
	public void test3() throws Exception {
		String image_url = "http://192.168.1.72:8080/invoice/handleImage/1.bmp";
		for(int i = 0; i < 10; i++){
			String uuid = UUID.randomUUID().toString();
			redisDao.leftPush(Const.RECOGNIZE_WAIT, UUID.randomUUID().toString());
			redisDao.addKey(uuid, image_url);
		}
	}

	@Test
	public void test4() throws Exception {
		ImageUtil.bmpTojpg("E:/invoice/handleImage/2.bmp", "E:/invoice/handleImage/");
	}

	@Test
	public void test5() throws Exception {
		IOUtil.writeToLocal(ImageUtil.GetImageStr("E:/invoice/originImage/1.bmp"));
	}
	
	@Test
	public void test6() throws Exception {
		String url = "http://192.168.1.36:8080/invoice/handleImage/ddb0056e-182e-48f5-927b-19195bb2323a.jpg";
		int index = url.indexOf("e");
		String part2 = url.substring(index + 1, url.length());
		System.out.println(part2);
	}

}
