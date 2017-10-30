package com.hl.socket;

import java.io.IOException;
import java.net.Socket;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hl.domain.LocalConfig;
import com.hl.util.Const;

@Component("socketListener")
@Scope("singleton")
public class SocketLoadTool {

	private Socket algorithmSocket;
	private Socket customerSocket;
	
	public SocketLoadTool() {
		
		try {
			algorithmSocket = new Socket("127.0.0.1",new Integer(Const.PORT));
			System.out.println("与算法端成功建立连接");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("与算法端成功建立连接失败");
		}
		
		try {
			customerSocket = new Socket("127.0.0.1", 9000);
			System.out.println("与客户端成功建立连接");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("与客户端成功建立连接失败");
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
