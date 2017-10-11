package com.hl.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.hadoop.hdfs.server.namenode.status_jsp;
import org.junit.Test;


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
