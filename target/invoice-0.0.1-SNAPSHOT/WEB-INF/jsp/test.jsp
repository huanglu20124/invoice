<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript" src="/js/jquery-3.2.1.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Insert title here</title>
<script type="text/javascript">
	function ajaxTest(){
		$.ajax({
		data : {username:"reader001"},
		type:"POST",
		url:"http://localhost:8080/invoice/handleImage",
		error:function(data){
			alert("信息:"+data.msg);
		},
		success:function(data){
			alert("success:"+data.msg);
		}
		});
	}
</script>
</head>
<body>
	<input type="text" name="imgStr" id="imgStr"/>
	<input type="submit" value="test" onclick="ajaxTest();"/>
	<div id="result"></div>
</body>
</html>