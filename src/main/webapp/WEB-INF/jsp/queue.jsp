<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>中山大学发票识别监控系统</title>
	<meta charset="utf-8">
	<script src="script/jquery-3.2.1.min.js"></script>
	<script type="text/javascript" src="script/bootstrap.min.js"></script>
	<link rel="stylesheet" type="text/css" href="style/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="style/layout.css">
</head>
<body>
	<header>
		<img src="pic/zhongda.jpg" style="width: 15%;" />
	</header>
	<main>
		<div align="right">
	      <h3 align="right">欢迎!${user.user_name}!</h3>
	   	  <button onclick="javascrtpt:window.location.href='${pageContext.request.contextPath}/logout.action'">注销</button>
	    </div> 
		<aside class="col-lg-2">
			<div class="list-group">
				<a href="${pageContext.request.contextPath}/queue.action" class="list-group-item selected">缓冲队列</a>
				<a href="${pageContext.request.contextPath}/show.action" class="list-group-item">监控显示</a>
				<a href="${pageContext.request.contextPath}/paint.action" class="list-group-item">模板库</a>
				<a href="${pageContext.request.contextPath}/fault.action" class="list-group-item">报错发票
					<span class="badge">4</span>
				</a>
			</div>
		</aside>
		<div class="col-lg-10">
			<div class="panel panel-default">
			    <div class="panel-heading">
			        <h3 class="panel-title">缓冲队列（共<span id="waiting_num">0</span>个待完成任务)</h3>
			    </div>
			    <div class="panel-body waiting_list">
					<!-- <img src="image/rectangle.png" class="rect_img" />
					<img src="image/rectangle.png" class="rect_img" />
					<img src="image/rectangle.png" class="rect_img" />
					<img src="image/rectangle.png" class="rect_img" /> -->
			    </div>
			</div>
		</div>
	</main>

	<!-- 模态窗口 -->
	<div class="modal fade col-lg-10" id="showWaiting" tabindex="-1" aria-hidden="true" style="margin: 0px auto; margin-top: 50px;">
		<div>
	        <div class="modal-content">
	        	<div class="modal-header">
	        		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        		<h4 class="modal-title">等待任务</h4>
	        	</div>
	        	<div class="modal-body">
	        		<div style="width: 70%; margin-right: 3%; display: inline-block; vertical-align: top;">
	        			<img src="4.bmp" style="width: 100%;" id="img_info" />
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

	<script type="text/javascript">
		var ip2; //host_ip
		var wsuri; //websocket_url
        var ws = null;

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
        			wsuri = "ws://" + ip2 + "/invoice/webSocketServer";
        			console.log(wsuri);
        		},
        		error : function() {
        			alert("读取配置文件失败，稍后重试");
        		}
        	})
        }

        //连接websocket
        function connectEndpoint(){

            ws = new WebSocket(wsuri);
            var img_list = [];
            ws.onmessage = function(evt) {
            	//alert(evt.data);
            	var data = JSON.parse(evt.data);
            	console.log(data);
            	//增加数目
            	if(data.msg_id == 201) {
            		var num = parseInt($("#waiting_num").text());
            		for(var i = 0; i < data.new_recognize.length; i++){
            			$(".waiting_list").append("<img src=\"pic/rectangle.png\" class=\"rect_img\" />");
            			var opacity_ = parseFloat(data.new_recognize[i].image_size / 500) > 1 ? 1 : parseFloat(data.new_recognize[i].image_size / 500); 
            			$(".waiting_list img:last-child").get(0).base_json = data.new_recognize[i];
            			$(".waiting_list img:last-child").css("opacity", opacity_);
            			$(".waiting_list img:last-child").click(function() {
		        			$("#showWaiting").modal('show');
		        			var temp_json = JSON.parse($(this).get(0).base_json);
		        			$("#img_info").get(0).src = temp_json.url;
		        		})
		        		num++;
            		}
            		$("#waiting_num").text(num.toString());
            	}
            	//减少数目
            	else if(data.msg_id == 1) {
            		$(".waiting_list img").first().remove();
            		var num = parseInt($("#waiting_num").text());
            		num--;
            		$("#waiting_num").text(num.toString());
            	}
            };

            ws.onclose = function(evt) {
                console.log("close");
            };

            ws.onopen = function(evt) {
                console.log("open");
                //发送ajax请求获取当前缓冲队列
	        	$.ajax({
	        		url : "http://" + ip2 + "/invoice/recognizeWait",
	        		type : "POST",
	        		data : {},
	        		success: function(res){
	        			var data = JSON.parse(res);
	        			$("#waiting_num").text(data.recognize_wait.length.toString());
	            		for(var i = 0; i < data.recognize_wait.length; i++) {
	            			$(".waiting_list").append("<img src=\"pic/rectangle.png\" class=\"rect_img\" />");
	            			var opacity_ = parseFloat(data.recognize_wait[i].image_size / 500) > 1 ? 1 : parseFloat(data.recognize_wait[i].image_size / 500);
	            			$(".waiting_list img:last-child").css("opacity", opacity_);
	            			$(".waiting_list img:last-child").get(0).base_json = data.recognize_wait[i];
	            			$(".waiting_list img:last-child").click(function() {
			        			$("#showWaiting").modal('show');
			        			var temp_json = JSON.parse($(this).get(0).base_json);
			        			$("#user_info").text(temp_json.user_name);
			        			$("#time_info").text(temp_json.action_start_time);
			        			$("#img_info").get(0).src = temp_json.url;
			        		})
	            		}
	        		},
	        		error: function(e) {
	        			console.log(e);
	        		}
	        	})
            };
        }

        //生成随机数
        function GetRandomNum(Min,Max)
		{   
			var Range = Max - Min;   
			var Rand = Math.random();   
			return(Min + parseFloat(Rand * Range));   
		}  

        $(document).ready(function(){
        	loadxml("config.xml");
        	connectEndpoint();

        })
	</script>
</body>
</html>