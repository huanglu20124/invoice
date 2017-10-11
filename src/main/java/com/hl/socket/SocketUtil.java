package com.hl.socket;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Remote;

import com.hl.util.Const;

public class SocketUtil { 
	public static Socket getSocket() throws IOException{
		//新建一个socket
		Socket socket = new Socket("127.0.0.1", Integer.valueOf(Const.PORT));
		System.out.println("SocketUtil新建socket成功");
		return socket;
	}
	
	public static String getResponseStr(BufferedReader bufferedReader) throws IOException{
		String temp = null;
		StringBuilder sBuilder = new StringBuilder();
		while ((temp = bufferedReader.readLine()) != null) {
			sBuilder.append(temp);
			if (!(temp.endsWith(",") || temp.endsWith("}") || temp.endsWith("{"))) {
				break;
			}
		}
		sBuilder.append("}");
		return sBuilder.toString();
	}
	
	public static String getResponseByte(InputStream in){
		byte[]bytes = new byte[1024];
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		int a;
		try {
			while((a = in.read()) != -1){
				bs.write(bytes, 0, a);
			}
			String str = new String(bytes,"utf-8");
			return str;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void close(Socket socket,InputStream in, OutputStream out, BufferedReader reader, PrintWriter writer){
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
		
		if(reader != null){
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				reader = null;
			}
		}
		if(writer != null){
			writer.close();
		}
	}
	
	
}
