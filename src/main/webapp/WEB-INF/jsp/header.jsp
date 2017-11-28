<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.alibaba.fastjson.JSON"%>
<!DOCTYPE html>
<html>
<head>
	<title></title>
	<meta charset="utf-8" />
</head>
<body>
	<header class="flex flex-align-center">
        <img src="pic/logo.png" style="height: 100%; vertical-align: middle;" class="flex-none" />
        <span style="margin-left: 0.5em; font-size: 18px; padding-left: 1em; border-left: 2px solid rgba(200,200,200,0.5); color: #6a6e76;" class="flex-1">智能发票识别监控平台</span>
        <span class="flex-none own_user_name" style="margin-right: 1.5em; font-size: 16px; color: #6a6e76;"></span>
        <span class="btn-group flex-none" style="margin-right: 20px;">
            <span type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" style="border:none;">
                <img src="pic/头像.png" style="height: 30px; margin-right: 1em;">
                <span class="caret"></span>
            </span>
            <ul class="dropdown-menu" style="min-width: 100px; margin-top: 5px;">
                <li><a href="">个人设置</a></li> 
                <li><a href="${pageContext.request.contextPath}/logout.action">退出登录</a></li>
            </ul>
        </span>
    </header>
</body>
</html>