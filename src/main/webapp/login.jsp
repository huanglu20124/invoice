<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>中山大学发票识别监控系统</title>
	<meta charset="utf-8">
	<script src="script/jquery-3.2.1.min.js"></script>
	<script type="text/javascript" src="script/bootstrap.min.js"></script>
	<script type="text/javascript" src="script/jquery.form.js"></script>
	<link rel="stylesheet" type="text/css" href="style/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="style/layout.css">
	<link rel="stylesheet" type="text/css" href="font-awesome-4.7.0/css/font-awesome.min.css">
</head>
<body>
	<img src="pic/login_bg.jpg" style="height: 100%; width: auto; opacity: 0.5;">
	<main class="login_main">
		<i class="fa fa-user-circle login_hd" aria-hidden="true" style="font-size: 60px; display: inline-block; margin-bottom: 0.5em;"></i>
		<ul id="login_tab" class="nav nav-tabs login_nav">
			<li class="active">
				<a href="#user" data-toggle="tab">用户登录</a>
			</li>
			<li><a href="#manager" data-toggle="tab">管理员登录</a></li>
		</ul>
		<div class="tab-content">
			<div class="tab-pane fade in active" id="user">
				<form role="form" id="user_login" action="${pageContext.request.contextPath}/loginCheck" method="POST">
					<div class="input-group">
			            <span class="input-group-addon"><i class="fa fa-info" aria-hidden="true"></i></span>
			            <input type="text" class="form-control" placeholder="请输入用户账号" name="user_name">
			        </div>
			        <div class="input-group">
			            <span class="input-group-addon"><i class="fa fa-lock"></i></span>
			            <input type="text" class="form-control" placeholder="请输入密码" name="user_password">
			        </div>
			        <button type="sumbit" class="btn btn-primary">登录</button>
			        <button type="sumbit" class="btn btn-default">注册</button>
				</form>
			</div>
			<div class="tab-pane fade" id="manager">
				<form role="form">
					<div class="input-group">
			            <span class="input-group-addon"><i class="fa fa-info" aria-hidden="true"></i></span>
			            <input type="text" class="form-control" placeholder="请输入管理员账号" name="user_name">
			        </div>
			        <div class="input-group">
			            <span class="input-group-addon"><i class="fa fa-lock"></i></span>
			            <input type="text" class="form-control" placeholder="请输入密码" name="user_password">
			        </div>
			        <button type="sumbit" class="btn btn-primary">登录</button>
			        <button type="sumbit" class="btn btn-default">注册</button>
				</form>
			</div>
		</div>
	</main>

	<script type="text/javascript">
		var ip2; //host_ip
	    //读取config.xml配置ip等信息
	    function loadxml(fileName) {
	    	$.ajax({
	    		async : false,
	    		url : fileName,
	    		dataType : "xml",
	    		type : "GET",
	    		success : function(res, status) {
	    			var xml_data = res;
	    			ip2 = xml_data.getElementsByTagName("connect_ip")[0].innerHTML;
	    			console.log(ip2);
	    		},
	    		error : function() {
	    			alert("读取配置文件失败，稍后重试");
	    		}
	    	})
	    }
		$(document).ready(function(){
			var height = parseFloat($(".login_hd").get(0).offsetHeight);
			$(".login_hd").css("marginTop", -height/2 + "px");
			var height_ = parseFloat($("main").get(0).offsetHeight)+100;
			var width_ = parseFloat($("main").get(0).offsetWidth);
			$("main").css("marginTop", -height_/2 + "px");
			$("main").css("marginLeft", -width_/2 + "px");
			loadxml("config.xml");
			
			var options = { 
		        // target:        '#output1',   // target element(s) to be updated with server response 
		        // beforeSubmit:  showRequest,  // pre-submit callback 
		        success: function(res){  // post-submit callback 
		        	alert("登录成功");
		        },  
		 
		        // other available options:       // override for form's 'action' attribute 
		        type: 'POST',        // 'get' or 'post', override for form's 'method' attribute 
		        //dataType:  null        // 'xml', 'script', or 'json' (expected server response type) 
		        //clearForm: true        // clear all form fields after successful submit 
		        resetForm: true        // reset the form after successful submit 
		 
		        // $.ajax options can be used here too, for example: 
		        //timeout:   3000 
		    }; 
		    // bind form using 'ajaxForm' 
		    //$('#user_login').ajaxForm(options);
		})
	</script>
</body>
</html>