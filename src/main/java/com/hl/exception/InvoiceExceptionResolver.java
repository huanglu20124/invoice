package com.hl.exception;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.omg.CORBA.UserException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class InvoiceExceptionResolver implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		// 输出异常
		ex.printStackTrace();
		// 统一异常处理代码
		// 针对系统自定义的CustomException异常，就可以直接从异常类中获取异常信息，将异常处理在错误页面展示
		// 异常信息
		String message = null;
		InvoiceException invoiceException = null;
		// 如果ex是系统 自定义的异常，直接取出异常信息
		if (ex instanceof InvoiceException) {
			invoiceException = (InvoiceException) ex;
		} else {
			// 针对非CustomException异常，对这类重新构造成一个CustomException，异常信息为“未知错误”
			invoiceException = new InvoiceException("未知错误\r\n" + ex.getMessage());
		}
		// 错误 信息
		message = invoiceException.getMessage();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message",message);
		modelAndView.setViewName("error");
		return modelAndView;
	}

}
