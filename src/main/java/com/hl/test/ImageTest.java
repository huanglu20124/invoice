package com.hl.test;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;

import org.junit.Test;

import com.hl.dao.RedisDao;
import com.hl.domain.Permission;
import com.hl.domain.User;
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
 
	@Test
	public void test8() throws Exception {
		File file = new File("E:/study_and_work/invoice/repo/backup/backup3.0/model/original/201710/c741a5bc-f249-4795-81f9-30137124bbf0.jpg");
		Double len = new Long(file.length()).doubleValue();
		System.out.println(new Double(len/1024).intValue());
	}
	
	@Test
	public void test9() throws Exception {
		User user1 = new User();
		user1.setUser_id(1);
		List<User>list1 = new ArrayList<>();
		List<User>list2 = new ArrayList<>();
		list1.add(user1);
		user1.setUser_id(2);
		list2.add(user1);
		User user2 = list1.get(0);
		User user3 = list1.get(0);
		System.out.println(user2.equals(user3));
		System.out.println(user2.getUser_id() + "   " + user3.getUser_id());
	}
	
	@Test
	public void test10() throws Exception {
		Permission permission1 = new Permission();
		Permission permission2 = new Permission();
		permission1.setPermission_id(1);
		permission2.setPermission_id(1);
		System.out.println(permission1.equals(permission2));
		Set<Permission>set = new HashSet<>();
		set.add(permission1);
		set.add(permission2);
		System.out.println(set);
	}
	
	@Test
	public void test14() throws Exception {
		String folder_path = "E:/study_and_work/invoice/repo/票据扫描/增值税_origin";
		File file = new File(folder_path);
		String[]list = file.list();
		System.out.println(list.length);
		int k = 1;
		for(String file_name : list){
			File origin = new File("E:/study_and_work/invoice/repo/票据扫描/增值税_origin/" + file_name);
			System.out.println(file_name);
			if(origin.exists()){
				origin.renameTo(new File("E:/study_and_work/invoice/repo/票据扫描/增值税/"+k+".jpg"));
				//origin.deleteOnExit();
				k++;
			}else {
				System.out.println("文件不存在");
			}
		}
	}
	
	@Test
	public void test15() throws Exception {
		File file = new File("E:/study_and_work/invoice/repo/票据扫描/增值税/" + "test.txt");
		FileWriter writer = new FileWriter(file);
		for(int i = 346; i <= 690; i++){
			writer.write(i+".jpg 1 943.40 中山大学 13903387\r\n");
		}
		writer.close();
	}
	

}
