package com.hl.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.hdfs.server.namenode.status_jsp;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import com.alibaba.fastjson.JSON;


public class IOUtil {
	private IOUtil(){
		
	}
	
	public static void inToOut(InputStream in, OutputStream out) throws IOException{
		byte[]bytes = new byte[1024];
		int i = 0;
		while((i =in.read(bytes)) != -1){
			out.write(bytes, 0, i);
		}
	}
	
	public static void close(InputStream in, OutputStream out){
		if(in != null){
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				in = null;
			}
		}
		
		if(out != null){
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				out = null;
			}
		}
	}

	public static int byteArrayToInt(byte [] b){
		return b[0] &0xFF | (b[1] & 0xFF)<<8 | (b[2] & 0xFF)<<16 | (b[3] & 0xFF)<<24;
	}
	
	public static byte[] intToByteArray(int i) {
		  byte[] result = new byte[4];   
		  //由高位到低位
		  result[3] = (byte)((i >> 24) & 0xFF);
		  result[2] = (byte)((i >> 16) & 0xFF);
		  result[1] = (byte)((i >> 8) & 0xFF); 
		  result[0] = (byte)(i & 0xFF);
		  return result;
	}

	public static String dataBaseToJsonModel(String path) throws Exception{
		//从本地的DataBase.xml读取出json_model
		SAXReader saxReader = new SAXReader();
		//String path = "E:/study_and_work/invoice/repo/ISRS5.2/ISRS5.0/x64/Debug/Database.xml";
		Document document = saxReader.read(new File(path));
		//根元素
		Element root = document.getRootElement();
		System.out.println("获取根节点:" + root.getName());
		//获取所有子元素
		Element database_current_size = root.element("Database_current_size");
		//得到模板数量
		Integer num =  new Integer((String)database_current_size.getData());
		System.out.println("得到模板数量 :" + num);
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
		return str;
	}
	
	//将一个字符串写入一个本地文件，测试用
	public static void writeToLocal(String str){
		try {
			if(str == null){
				System.out.println("字符串为空");
			}
			File file = new java.io.File("E:/invoice","test.txt");
			file.delete();
			if(!file.exists()){
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write(str);
			System.out.println("写入本地文件测试");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
