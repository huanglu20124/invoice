package com.hl.exception;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class InvoiceExceptionResolver implements HandlerExceptionResolver {

	public static Logger logger = Logger.getLogger(InvoiceExceptionResolver.class);
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// 统一异常处理代码，如果是普通请求返回jsp页面，ajax则返回异常信息
		// 异常信息
		String message = null;
		InvoiceException invoiceException = null;
		// 如果ex是系统 自定义的异常，直接取出异常信息
		if (ex instanceof InvoiceException) {
			invoiceException = (InvoiceException) ex;
		} else {
			// 针对非CustomException异常，对这类重新构造成一个CustomException，异常信息为“未知错误”
			invoiceException = new InvoiceException("服务器未知错误" + ex.getMessage());
		}
		message = invoiceException.getMessage() + "\r\n" + getException(ex) + "\r\n";
		//日志记录原异常
		logger.error(message);
		//判断是否是ajax请求
		if((request.getHeader("accept").contains("application/json")) ||
			(request.getHeader("X-Requested-With") != null) ||
			(request.getHeader("X-Requested-With").contains("XMLHttpRequest"))){
			//异步请求
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.write(message);
				writer.flush();
				System.out.println("json格式返回异常");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}else {
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("message",message);
			modelAndView.setViewName("error");
			System.out.println("返回错误界面");
			return modelAndView;
		}
	}

	 public static String getException(Exception e){
	        StackTraceElement[] ste = e.getStackTrace();
	        StringBuffer sb = new StringBuffer();
	        sb.append(e.getMessage() + " ");
	        //错误信息限定在前5行
	        if(ste.length <= 10){
		        for (int i = 0; i < ste.length; i++) {
			          sb.append(ste[i].toString() + "\r\n");
			        }
	        }else {
		        for (int i = 0; i < 5; i++) {
			          sb.append(ste[i].toString() + "\r\n");
			        }
			}
	        return sb.toString();
	    }
}
