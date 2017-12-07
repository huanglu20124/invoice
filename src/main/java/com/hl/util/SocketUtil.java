package com.hl.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Remote;

public class SocketUtil { 
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
