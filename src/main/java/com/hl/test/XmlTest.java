package com.hl.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.hl.domain.LocalConfig;
import com.hl.util.IOUtil;

public class XmlTest {
	@Test
	public void test1() throws Exception {
		
		SAXReader saxReader = new SAXReader();
		String path = "E:/study_and_work/invoice/repo/ISRS5.6/ISRS5.3/ISRS5.2/ISRS5.0/x64/Debug/Database-utf8.xml";
		Document document = saxReader.read(new File(path));
		//根元素
		Element root = document.getRootElement();
		System.out.println("获取根节点:" + root.getName());
		//获取所有子元素
		Element database_current_size = root.element("Database_current_size");
		//得到模板数量
		Integer num =  new Integer((String)database_current_size.getData());
		System.out.println("得到模板数量 = " + num);
		Element element_invoice_info = root.element("invoice_info");
		List<Element>elements = element_invoice_info.elements("_");
		List<Map<String, Object>>json_model_list = new ArrayList<>();
		for(int i = 0; i < num; i ++){
			//一个模板一个json_model,最终存在json_model_list里面
			Map<String, Object>json_model_map = new HashMap<>();
			Map<String, Object>global_setting_map = new HashMap<>();
			
			Element element_root = elements.get(i);
			
			String label = element_root.element("label").getText().replaceAll("\"", "");
			String quota = element_root.elementText("quota");
			global_setting_map.put("label", label);
			global_setting_map.put("quota", quota);
			json_model_map.put("global_setting",global_setting_map);
			
			Element element_info_area = element_root.element("info_area");
			List<Element>roots_info_area = element_info_area.elements("_");
			int area_num = roots_info_area.size();
			System.out.println("area_num = " + area_num);
			//现在只有两个区域
			area_num = 2;
			for(int j = 0; j < area_num; j++){
				Element element = roots_info_area.get(j);//得到_
				//开始遍历每个区域
				Map<String, Object>area_map = new HashMap<>();
				
				Integer x = new Integer(element.elementText("absolute_x"));
				area_map.put("absolute_x", x);
				Integer y = new Integer(element.elementText("absolute_y"));
				area_map.put("absolute_y", y);
				Integer height = new Integer(element.elementText("height"));
				area_map.put("height", height);
				Integer width = new Integer(element.elementText("length"));
				area_map.put("width", width);
				Integer remove_line = new Integer(element.elementText("remove_line"));
				area_map.put("remove_line", remove_line);
				Integer remove_stamp = new Integer(element.elementText("remove_stamp"));
				area_map.put("remove_stamp", remove_stamp);
				String keywords = element.elementText("keywords").replaceAll("\"", "");;
				area_map.put("keywords", keywords);
				if(j == 0){
					//money node
					json_model_map.put("money", area_map);
				}
				else if(j == 1){
					//head node
					json_model_map.put("head", area_map);
				}
				
			}
			json_model_list.add(json_model_map);
		}
		String str = JSON.toJSONString(json_model_list);
		System.out.println(str);
		IOUtil.writeToLocal(str);
	}
}
