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
	<jsp:include page="header.jsp" flush="true" />
	<main>
		<jsp:include page="aside_menu.jsp" flush="true" />
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
			
			<button type="button" class="btn btn-danger" onclick="deleteTempSaveList()" style="margin: 15px 15px 15px 0px;" data-write="true">清空缓冲队列</button>
			
			<label for="analog_img" class="file_label" data-write="true" style="margin-top: 15px;">选择本地图片识别</label>
			<input type="file" onchange="addImgs()" id="analog_img" multiple style="display: none;" />
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
	
	<div class="modal fade" id="progressModal" tabindex="-1" aria-hidden="true" style="margin: 0px auto; width: 33%;">
		<div>
	        <div class="modal-content">
	        	<div class="modal-header">
	        		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        		<h4 class="modal-title">正在清空...</h4>
	        	</div>
	        	<div class="modal-body">
	        		<div class="progress progress-striped active">
					    <div class="progress-bar" role="progressbar" aria-valuenow="60" 
					        aria-valuemin="0" aria-valuemax="100" style="width: 40%;">
					        <span class="sr-only">40% 完成</span>
					    </div>
					</div>
	        	</div>
	        	<div class="modal-footer">
	                <button type="button" class="btn btn-default" data-dismiss="modal" disabled id="certain_progress">确定</button>
	            </div>
	        </div>
	    </div>
	</div>
	
	<script type="text/javascript" src="script/common.js"></script>
	<script type="text/javascript">

		//发送图片
		function addImgs() {
			console.log($('#analog_img').get(0).files);
			var invoice_list = [];
			var formData = new FormData();
			for(var i = 0; i < $('#analog_img').get(0).files.length; i++) {
				invoice_list.push({
					invoice_image_id : "00",
					invoice_note: "中山大学"
				})				
				formData.append("file", $('#analog_img').get(0).files[i]);
			}
			var recognize = {
				user_id : 1,
				company_id : 1,
				invoice_list: invoice_list
			}
			
			formData.append("recognizeAction", JSON.stringify(recognize));
			console.log(formData);
			$.ajax({
		        url: "http://" + ip2 + "/invoice/recognizeImage.action",
		        type: 'POST',
		        data: formData,
		        cache: false,
		        processData: false,
		        contentType: false,
		        success : function(data) {
		            console.log(data);
		        },
		        error: function(err) {
		        	console.log("error");
		        }
		    });
		}
	
       //jsp加入
       var user_json = <%=JSON.toJSONString(request.getAttribute("user"))%>
        //生成随机数
        function GetRandomNum(Min,Max)
		{   
			var Range = Max - Min;   
			var Rand = Math.random();   
			return(Min + parseFloat(Rand * Range));   
		}  

      //清空上传队列
		function deleteTempSaveList() {
			$("#progressModal h4").text("正在清空");
			$("#progressModal .progress-bar").get(0).style.width = "40%";
			$("#progressModal").modal('show');
			$.ajax({
				url: "http://" + ip2 + "/invoice/clearRecognizeQueue.action",
				type: 'POST',
				success: function(res,status) {
					$("#progressModal h4").text("清空上传队列成功");
        			$("#progressModal .progress-bar").get(0).style.width = "100%";
					setTimeout(function(){
						$("#progressModal").modal('hide');
						$("#progressModal .progress-bar").get(0).style.width = "40%";
					}, 1500);
					$(".temp_save_muban .ku_img_container").each(function() {
						$(this).remove();
					})
				},		
				error: function(err) {
					$("#progressModal h4").text("清空上传队列失败");
					$("#progressModal .progress-bar").addClass("progress-bar-danger");
					$("#progressModal .btn").get(0).disabled = false;
					console.log("清空上传队列失败");
				}
			})
		}
        
        $(document).ready(function(){
        	// 判断权限
        	justifyUserGrant(user_json);
        	justifyRW(user_json);

        	// console.log(document.documentElement.clientHeight);
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
		                        $("#time_info").text(temp_json.action_time);
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