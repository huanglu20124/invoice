<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
账号登录
<form action="${pageContext.request.contextPath}/loginCheck" method="post">
  username：<input type="text" name="user_name">
  user_password：<input type="text" name="user_password">
  <input type="submit" value="提交" > 
</form>
${err}
<br>
</body>
</html>