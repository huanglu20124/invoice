package com.hl.test;

import static org.junit.Assert.*;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hl.service.InvoiceService;
import com.hl.socket.SocketLoadTool;

public class SocketTest { 
	@Test
	public void test1() throws Exception {
		Socket socket = new Socket("127.0.0.1", 8889);
		OutputStream oStream = socket.getOutputStream();
		oStream.write("黄路aaa".getBytes());
		oStream.flush();
		oStream.close();
		Thread.sleep(600000);
	}
	
}
