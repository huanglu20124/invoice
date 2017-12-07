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
					<div class="form-group">
					    <label for="name" style="font-weight: normal;" class="form_hd">关键字类型</label>
					    <select class="form-control" name="type" style="padding: 0.3em; display: inline-block; width: 8em; margin-left: 5px;">
					      <option value="0">用户ip</option>
					      <option value="1">用户名字</option>
					      <option value="2">单位名字</option>
					      <option value="3" selected>操作类型</option>
					    </select>
				    </div>
					<div class="keyword_select form-group">
						<label class="form_hd" style="font-weight: normal;">关键字</label> 
						<input class="form-control" type="text" name="keyword" id="keyword" placeholder="选填，输入搜索内容的关键字" style="padding: 5px 10px; min-width: 20em;">
					</div>

					<button class="btn btn-primary" type="submit" style="padding-left: 2.5em; padding-right: 2.5em; margin-top: 10px;" id="rizhi_select">查询</button>

					<div class="select_result">
						<p class="select_result_hd">查询结果</p>

						<!-- <div class="table_container" style="border: 1px solid rgba(200, 200, 200, 0.7); border-radius: 5px; padding-top: 0.5em;"> -->
							<!-- <table class="table table-responsive table-striped table-hover" >
								<thead>
									<tr style="color: #9a9a9a;"><th>用户名称</th><th>责任单位</th><th>操作id</th><th>操作开始时间</th><th>算法开始时间</th><th>操作结束时间</th></tr>
								</thead>
								<tbody>
								</tbody>
							</table> -->
					<!-- </div> -->
					<div class="table_display_container log_table" style="border-radius: 6px; overflow: hidden; display: none;">
						<div class="table_display_row">
							<div class="table_display_th">用户名称</div>
							<div class="table_display_th">责任单位</div>
							<div class="table_display_th">操作事件</div>
							<div class="table_display_th">操作时间</div>
							<div class="table_display_th">操作ip地址</div>
						</div>
					</div>

					<div class="text_describe" style="display: none; text-align: center; font-size: 24px; margin-top: 70px;">
						暂无数据
					</div>
			    </div>
			</div>
		</div>
	</main>

	<script type="text/javascript" src="script/common.js"></script>
	<script type="text/javascript">

        //jsp加入
        var user_json = <%=JSON.toJSONString(request.getAttribute("user"))%>

        //获取当前日期和时间
        function getNowFormatDate() {
		    var date = new Date();
		    var seperator1 = "-";
		    var seperator2 = ":";
		    var month = date.getMonth() + 1;
		    var strDate = date.getDate();
		    var hour = date.getHours();
		    var minute = date.getMinutes();
		    if (month >= 1 && month <= 9) {
		        month = "0" + month;
		    }
		    if (strDate >= 0 && strDate <= 9) {
		        strDate = "0" + strDate;
		    }
		    if (hour >= 0 && hour <= 9) {
		        hour = "0" + hour;
		    }
		    if (minute >= 0 && minute <= 9) {
		        minute = "0" + minute;
		    }
		    var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate;
		    var currenttime = hour + seperator2 + minute;
		   	
		   	console.log(currenttime + " " + currentdate);
		   	$("input[type='date']").each(function() {
		   		$(this).val(currentdate);
		   	})

		   	$("input[type='time']").each(function() {
		   		$(this).val(currenttime);
		   	})
		}

        $(document).ready(function(){
        	// console.log(document.documentElement.clientHeight);
        	// 判断权限
        	justifyUserGrant(user_json);
        	justifyRW(user_json);

        	getNowFormatDate();
        	//首次加载查询
        	$.ajax({
    			type: "POST",
    			url: "http://"+ip2+"/invoice/getTwentyAction.action",
    			data: {
    				page: 0 //首次请求
    			},
    			success: function(res, status) {
    				var data = JSON.parse(res);
    				// $(".select_result").css("display", "block");
    				//先清空上一次查询的结果
    				$(".select_result .log_table .table_display_row:nth-child(n+2)").remove();

    				if(data.action_list.length == 0 || data.action_list == undefined) {
    					$(".select_result .text_describe").css("display", "block");
    					$(".select_result .log_table").css("display", "none");
    				}

    				else{
    					$(".select_result .text_describe").css("display", "none");
    					$(".select_result .log_table").css("display", "table");

        				for(var i = 0; i < data.action_list.length; i++) {
        					// console.log(data.action_list[i]);
        					var temp_data = data.action_list[i];
							// var data = res[i];
							$(".select_result .log_table").append("<div class=\"table_display_row\"><div class=\"table_display_td\">"+temp_data.user_name+"</div><div class=\"table_display_td\">"+temp_data.company_name+"</div><div class=\"table_display_td\">"+temp_data.description+"</div><div class=\"table_display_td\">"+temp_data.action_time+"</div><div class=\"table_display_td\">"+temp_data.user_ip+"</div></div>");
        				}	
    				}
    			},
    			error: function() {
    				$(".select_result .log_table").css("display", "none");
    				$(".select_result .text_describe").css("display", "block");
    			}
    		})


        	//绑定提交按钮获取日志查询结果
        	$("#rizhi_select").click(function(){
        		// console.log($("#start_date").val() + " " + $("#start_time").val());
        		var star_time = null;
        		var end_time = null;
        		if($("#start_date").val() != null && $("#start_time").val() != null) {
        			star_time = $("#start_date").val() + " " + $("#start_time").val();
        		}
        		if($("#end_date").val() != null && $("#end_time").val() != null) {
        			end_time = $("#end_date").val() + " " + $("#end_time").val();
        		}
        		$.ajax({
        			type: "POST",
        			url: "http://"+ip2+"/invoice/getTwentyAction.action",
        			data: {
        				page: 0, //首次请求
        				startTime: star_time,
        				endTime: end_time,
        				type: $("select[name='type']").val(),
        				keyword: $("#keyword").val() == null ? null : $("#keyword").val()
        			},
        			success: function(res, status) {
        				var data = JSON.parse(res);
        				// $(".select_result").css("display", "block");
        				//先清空上一次查询的结果
        				$(".select_result .log_table .table_display_row:nth-child(n+2)").remove();

        				if(data.action_list.length == 0 || data.action_list == undefined) {
        					$(".select_result .text_describe").css("display", "block");
        					$(".select_result .log_table").css("display", "none");
        				}

        				else{
        					$(".select_result .text_describe").css("display", "none");
        					$(".select_result .log_table").css("display", "table");

            				for(var i = 0; i < data.action_list.length; i++) {
            					// console.log(data.action_list[i]);
            					var temp_data = data.action_list[i];
    							// var data = res[i];
    							$(".select_result .log_table").append("<div class=\"table_display_row\"><div class=\"table_display_td\">"+temp_data.user_name+"</div><div class=\"table_display_td\">"+temp_data.company_name+"</div><div class=\"table_display_td\">"+temp_data.description+"</div><div class=\"table_display_td\">"+temp_data.action_time+"</div><div class=\"table_display_td\">"+temp_data.user_ip+"</div></div>");
            				}	
        				}
        			},
        			error: function() {
        				$(".select_result .log_table").css("display", "none");
        				$(".select_result .text_describe").css("display", "block");
        			}
        		})
        	})
        })
	</script>
</body>
</html>