package com.hl.util;

import org.apache.shiro.crypto.hash.Md5Hash;

public class MD5Utils {
	public void getMd5BySalt(){
		//原始 密码 
		String source = "123";
		//盐
		String salt = "eyerh";
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
