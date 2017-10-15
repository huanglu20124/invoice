package com.hl.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;

import com.hl.dao.RedisDao;
import com.hl.util.ImageUtil;
import com.hl.util.Const;
import com.hl.util.IOUtil;
import net.sf.json.xml.XMLSerializer;

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
		for (int i = 0; i < 10; i++) {
			String uuid = UUID.randomUUID().toString();
			redisDao.leftPush(Const.RECOGNIZE_WAIT, UUID.randomUUID().toString());
			redisDao.addKey(uuid, image_url);
		}
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

	@Test
	public void test7() throws Exception {
		net.sf.json.xml.XMLSerializer xmlSerializer = new net.sf.json.xml.XMLSerializer();
		//String path = "E:/study_and_work/invoice/repo/ISRS5.2/ISRS5.0/x64/Debug/Database.xml";
		String path = "E:/invoice/test.txt";
		File file = new File(path);
		String str = null;
		if (file.exists()) {
			System.out.println("文件存在，准备读取");
			try {
				FileInputStream fis = new FileInputStream(file);
				byte[] buf = new byte[1024];
				StringBuffer sb = new StringBuffer();
				while ((fis.read(buf)) != -1) {
					sb.append(new String(buf));
					buf = new byte[1024];// 重新生成，避免和上次读取的数据重复
				}
				str = sb.toString();
				System.out.println(str);
				String result = xmlSerializer.read(str).toString();
				IOUtil.writeToLocal(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			System.out.println("文件不存在");
		}
	}
}
