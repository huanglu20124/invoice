package com.hl.util;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("socketListener")
@Scope("singleton")
public class SocketLoadTool {

	private Socket algorithmSocket;
	private Socket customerSocket;
	
	private static Logger logger = Logger.getLogger(SocketLoadTool.class);
	
	public SocketLoadTool() {
		
		try {
			algorithmSocket = new Socket("127.0.0.1",new Integer(Const.PORT));
			logger.info("与算法端成功建立连接");
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("与算法端成功建立连接失败");
		}
		
		try {
			customerSocket = new Socket("127.0.0.1", 9000);
			logger.info("与客户端成功建立连接");
		} catch (IOException e) {
			//e.printStackTrace();
			logger.info("与客户端成功建立连接失败");
		}
	}
	
	
	public Socket getAlgorithmSocket() {
		return algorithmSocket;
	}
	public void setAlgorithmSocket(Socket algorithmSocket) {
		this.algorithmSocket = algorithmSocket;
	}


	
	public Socket getCustomerSocket() {
		return customerSocket;
	}


	public void setCustomerSocket(Socket customerSocket) {
		this.customerSocket = customerSocket;
	}

	
	

}
