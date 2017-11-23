<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.alibaba.fastjson.JSON"%>
<!DOCTYPE html>
<html>
<head>
	<title>中山大学发票识别监控系统</title>
	<meta charset="utf-8">
	<script src="script/jquery-3.2.1.min.js"></script>
	<script type="text/javascript" src="script/bootstrap.min.js"></script>
	<script type="text/javascript" src="script/reconnecting-websocket.min.js"></script>
	<link rel="stylesheet" type="text/css" href="style/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="font-awesome-4.7.0/css/font-awesome.min.css">
	<link rel="stylesheet" type="text/css" href="style/layout.css">
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
	<main>
		<aside>
			<div class="aside_nav_list">
				<a href="${pageContext.request.contextPath}/queue.action" class="aside_nav_list-item selected" data-permission="queue">
                    <i class="fa fa-bar-chart aside_nav_list-item-icon"></i>
                    <span>缓冲队列</span>
                </a>
				<a href=""  class="aside_nav_list-item nav_disabled" data-permission="console">
                    <i class="fa fa-television aside_nav_list-item-icon"></i>
                    <span>监控显示</span>
                </a>
				<a href="" class="aside_nav_list-item nav_disabled" data-permission="model">
                    <i class="fa fa-clipboard aside_nav_list-item-icon"></i>
                    <span>模板库</span>
                </a>
				<a href="" class="aside_nav_list-item nav_disabled" data-permission="fault">
                    <i class="fa fa-times-circle-o aside_nav_list-item-icon"></i>
                    <span>报错发票
                    	<span class="badge fault_num" style="margin-left: 10px;"></span>
                    </span>
                </a>
                <a href="" class="aside_nav_list-item nav_disabled" data-permission="log">
                    <i class="fa fa-tasks aside_nav_list-item-icon" aria-hidden="true"></i>
                    <span>日志查询</span>
                </a>
                <a href="" class="aside_nav_list-item nav_disabled" data-permission="user">
                    <i class="fa fa-user-o aside_nav_list-item-icon" aria-hidden="true"></i>
                    <span>用户管理</span>
                </a>
                <a href="${pageContext.request.contextPath}/ownedit.action" class="aside_nav_list-item">
                    <i class="fa fa-cog aside_nav_list-item-icon" aria-hidden="true"></i>
                    <span>个人设置</span>
                </a>
			</div>
		</aside>
		<div class="main_content">

			<div class="main_content_hd flex flex-align-end">
				<span class="flex-1">缓冲队列</span>
				<span class="flex-none" style="color: #a0a5b1; font-size: 16px;">共<span id="waiting_num">0</span>个待完成任务</span>
			</div>

			<div class="panel panel-default panel-box-shadow">
			    <div class="panel-body" style="height:550px;">
					<div style="display: inline-block; vertical-align:top; width: 15%; height: 100%; padding-right: 20px; border-right: 1px solid rgba(200,200,200,0.5)">
						<div class="flex flex-v flex-align-center" style="width: 100%; height: 100%; text-align: center; justify-content: center;">
							<i class="fa fa-file flex-none" aria-hidden="true" style="color: rgba(100, 100, 100, 0.5); font-size: 60px;"></i>
							<p class="flex-none" style="margin-top: 20px; line-height: 2em;">文件图标代表正在等待的操作任务</p>
						</div>
					</div>
					<div class="waiting_list" style="display: inline-block; width: 80%; height: 100%; vertical-align:top;">
						
					</div>
			    </div>
			</div>
		</div>
	</main>

	<!-- 模态窗口 -->
	<div class="modal fade col-lg-10" id="showWaiting" tabindex="-1" aria-hidden="true" style="margin: 0px auto; ">
		<div>
	        <div class="modal-content">
	        	<div class="modal-header">
	        		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        		<h4 class="modal-title">等待任务</h4>
	        	</div>
	        	<div class="modal-body">
	        		<div style="width: 70%; margin-right: 3%; display: inline-block; vertical-align: top;">
	        			<img src="" style="width: 100%;" id="img_info" />
	        		</div>
	        		<div style="width: 25%; display: inline-block; vertical-align: top;">
	        			<div class="panel panel-primary">
						    <div class="panel-heading">
						        <h3 class="panel-title">
						            发送用户
						        </h3>
						    </div>
						    <div class="panel-body" id="user_info">
						        xxx
						    </div>
						</div>
						<div class="panel panel-primary">
						    <div class="panel-heading">
						        <h3 class="panel-title">
						            发送时间
						        </h3>
						    </div>
						    <div class="panel-body" id="time_info">
						        xxx
						    </div>
						</div>
						<div class="panel panel-primary">
						    <div class="panel-heading">
						        <h3 class="panel-title" id="detail_info">
						            备注信息
						        </h3>
						    </div>
						    <div class="panel-body">
						        xxx
						    </div>
						</div>
	        		</div>
	        	</div>
	        	<div class="modal-footer">
	                <button type="button" class="btn btn-default" data-dismiss="modal" id="certain_progress">确定</button>
	            </div>
	        </div>
	    </div>
	</div>

	<script type="text/javascript" src="script/common.js"></script>
	<script type="text/javascript">

       //jsp加入
       var user_json = <%=JSON.toJSONString(request.getAttribute("user"))%>
        //生成随机数
        function GetRandomNum(Min,Max)
		{   
			var Range = Max - Min;   
			var Rand = Math.random();   
			return(Min + parseFloat(Rand * Range));   
		}  

        $(document).ready(function(){
        	// 判断权限
        	justifyUserGrant(user_json);

        	// console.log(document.documentElement.clientHeight);
        	$("#showWaiting").css("marginTop", document.documentElement.clientHeight*0.08 + "px");
        	var img_width = parseFloat($("#img_info").width());
        	$("#img_info").css("height", img_info*invoice_height/invoice_width);

        	//发送ajax请求获取当前缓冲队列
        	$.ajax({
        		url : "http://" + ip2 + "/invoice/recognizeWait.action",
        		type : "POST",
        		data : {},
        		success: function(res){
        			var data = JSON.parse(res);
        			if(data.recognize_wait != undefined) {
	        			
	            		for(var i = 0; i < data.recognize_wait.length; i++) {
	            			//$(".waiting_list").append("<img src=\"pic/rectangle.png\" class=\"rect_img\" />");
		                    $(".waiting_list").prepend("<i class=\"fa fa-file rect_img waiting_list_item\" aria-hidden=\"true\"></i>");
		                    var opacity_ = parseFloat(data.recognize_wait[i].image_size / relatvie_image_size) > 1 ? 1 : parseFloat(data.recognize_wait[i].image_size / relatvie_image_size); 
		                    $(".waiting_list i:first-child").get(0).base_json = data.recognize_wait[i];
		                    $(".waiting_list i:first-child").css("opacity", opacity_);
		                    $(".waiting_list i:first-child").click(function() {
		                        $("#showWaiting").modal('show');
		                        var temp_json = $(this).get(0).base_json;
		                        tellConsole($(this).get(0).base_json, 3);
		                        $("#user_info").text(temp_json.user_name);
		                        $("#time_info").text(temp_json.action_start_time);
		                        $("#detail_info").text(temp_json.invoice_note);
		                        $("#img_info").get(0).src = temp_json.url;
		                    })
	            		}

	            		$("#waiting_num").text((data.recognize_wait.length+queue_sub_num).toString());	
        			}

        		},
        		error: function(e) {
        			tellConsole(e, 1);
        		}
        	})

        })
	</script>
</body>
</html>