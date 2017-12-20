package com.hl.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.hl.dao.RedisDao;
import com.hl.domain.TestCase;

public class CheckUtil {
	public static void initGlobal(RedisDao redisDao, TestCase testCase){
		redisDao.addKey("testCase", JSON.toJSONString(testCase));
	}

	
	public static void checkOnce(RedisDao redisDao,TestCase testCase, Map<String, Object> invoice_data,Integer order) {
		List<String>area_names = testCase.getArea_names();
		//先读取这一行的正确答案
		//定位
		System.out.println("order=" + order);
		Integer index = testCase.getPic_indexs().get(order - 1);
		File file = new File(testCase.getCheck_path());
		String answer = null;
		if(file.exists()){
			try {
				FileInputStream fileInputStream = new FileInputStream(file);
				List<String>answers = IOUtils.readLines(fileInputStream,"gbk");
				answer = answers.get(index - 1);
				System.out.println("读到的一行为" + answer);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(answer != null){
				String[] answer_str = answer.split(" ");
				for(int i = 0; i < area_names.size(); i++){
					String value = (String) invoice_data.get(area_names.get(i));
					if(value == null || (!value.equals(answer_str[i + 2]))){
						redisDao.leftPush(area_names.get(i), "0");
						System.out.println("不相等，识别=" + value + "   标准=" +answer_str[i + 2] );
					}else {
						redisDao.leftPush(area_names.get(i), "1");
						System.out.println("相等，识别=" + value + "   标准=" +answer_str[i + 2] );
					}
					
				}				
			}
			System.out.println("一次校验结果完毕,开始统计识别率");
			for(String area_name : area_names){
				List<String>records= redisDao.getRangeId(area_name);
				int bottom = records.size();
				int count = 0;
				for(String record : records){
					if(record.equals("1")){
						count++;
					}
				}
				Double accuracy = (double)count/bottom;
				System.out.println(area_name + "区域的识别率为" + accuracy);
			}
	
		}else {
			System.out.println("核验文件不存在！");
		}
	}


	public static void finishCheck(RedisDao redisDao,TestCase testCase) {
		List<String>area_names = testCase.getArea_names();
		for(String area_name :area_names){
			redisDao.deleteKey(area_name);
		}
		redisDao.deleteKey("testCase");
		System.out.println("redis相关清理完毕");
	}

	
	
}
