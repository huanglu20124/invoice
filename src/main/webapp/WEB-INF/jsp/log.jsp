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
				<span class="flex-1">日志查询</span>
			</div>

			<div class="panel panel-default panel-box-shadow">
			    <div class="panel-body" style="min-height:550px; padding: 30px 30px;">
					<div class="dateTime_select form-group">
						<span class="form_hd">时间段</span> 
						<input class="form-control" type="date" name="start_date" id="start_date">日<input class="form-control" type="time" name="start_time" id="start_time">时
						&nbsp;&nbsp;至&nbsp;&nbsp; <input class="form-control" type="date" name="end_date" id="end_date">日<input class="form-control" type="time" name="end_time" id="end_time">时
					</div>
					<div class="keyword_select form-group" style="margin-top: 25px;">
						<label class="form_hd" style="font-weight: normal;">关键字</label> 
						<input class="form-control" type="text" name="keyword" id="keyword" placeholder="选填，输入搜索内容的关键字" style="padding: 5px 10px; min-width: 20em;">
					</div>

					<button class="btn btn-primary" type="submit" style="padding-left: 2.5em; padding-right: 2.5em; margin-top: 10px;" id="rizhi_select">查询</button>

					<div class="select_result" style="display: none;">
						<p class="select_result_hd">查询结果</p>

						<div class="table_container" style="border: 1px solid rgba(200, 200, 200, 0.7); border-radius: 5px; padding-top: 0.5em;">
							<table class="table table-responsive table-striped table-hover" >
								<thead>
									<tr style="color: #9a9a9a;"><th>用户名称</th><th>责任单位</th><th>操作id</th><th>操作开始时间</th><th>算法开始时间</th><th>操作结束时间</th></tr>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>

						<div class="text_describe" style="display: none;">
							暂无数据
						</div>
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

        $(document).ready(function(){
        	// console.log(document.documentElement.clientHeight);
        	// 判断权限
        	justifyUserGrant(user_json);

        	//绑定提交按钮获取日志查询结果
        	$("#rizhi_select").click(function(){
        		console.log($("#start_date").val() + " " + $("#start_time").val());
        		$.ajax({
        			type: "POST",
        			url: "http://"+ip2+"/invoice/getTwentyAction.action",
        			data: {
        				page: 0, //首次请求
        				startTime: $("#start_date").val() + " " + $("#start_time").val(),
        				endTime: $("#end_date").val() + " " + $("#end_time").val(),
        				keyword: $("#keyword").val() == null ? null : $("#keyword").val()
        			},
        			success: function(res, status) {
        				var data = JSON.parse(res);
        				$(".select_result").css("display", "block");
        				//先清空上一次查询的结果
        				$(".select_result table tbody").html("");

        				if(res.length == 0) {
        					$(".select_result .text_describe").css("display", "block");
        				}
        				else{
	        				for(var i = 0; i < data.action_list.length; i++) {
	        					console.log(data.action_list[i]);
	        					var temp_data = data.action_list[i];
								// var data = res[i];
								$(".select_result table tbody").append("<tr><td>"+temp_data.user_name+"</td><td>"+temp_data.company_name+"</td><td>"+temp_data.msg_id+"</td><td>"+temp_data.action_time+"</td><td>"+temp_data.action_run_time+"</td><td>"+temp_data.action_end_time+"</td></tr>");
	        				}	
        				}
        			},
        			error: function() {
        				$(".select_result").css("display", "block");
        				$(".select_result .text_describe").css("display", "block");
        			}
        		})
        	})
        })
	</script>
</body>
</html>