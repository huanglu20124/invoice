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
						<div class="select_result_hd flex flex-align-center">
							<span class="flex-1">查询结果</span>
							<span class="flex-none" style="display: inline-block; font-size: 14px; color: rgba(150, 150, 150, 0.6); overflow: hidden; width: 190px;">
								<span style="display: inline-block; float: left; width: 100px; vertical-align: middle;">每页日志条数:</span>
								<input class="form-control" type="number" name="section" value="10" min="5" max="30" style="font-size: 14px; height: 25px; display: inline-block; width: 80px; vertical-align: middle;" />
							</span>
						</div>

						<!-- <div class="table_container" style="border: 1px solid rgba(200, 200, 200, 0.7); border-radius: 5px; padding-top: 0.5em;"> -->
							<!-- <table class="table table-responsive table-striped table-hover" >
								<thead>
									<tr style="color: #9a9a9a;"><th>用户名称</th><th>责任单位</th><th>操作id</th><th>操作开始时间</th><th>算法开始时间</th><th>操作结束时间</th></tr>
								</thead>
								<tbody>
								</tbody>
							</table> -->
					<!-- </div> -->
					<div class="log_table_container" style="display: none;">
						<div class="table_display_container log_table" style="border-radius: 6px; overflow: hidden;">
							<div class="table_display_row">
								<div class="table_display_th" data-column="1">
									<span>日志序号</span>
									<div class="th_div">
										<p class="hide_p">隐藏该列</p>
									</div>
								</div>
								<div class="table_display_th" data-column="2">
									<span>操作时间</span>
									<div class="th_div">
										<p class="hide_p">隐藏该列</p>
									</div>
								</div>
								<div class="table_display_th" data-column="3">
									<span>操作ip地址</span>
									<div class="th_div">
										<p class="hide_p">隐藏该列</p>
									</div>
								</div>
								<div class="table_display_th" data-column="4">
									<span>用户名称</span>
									<div class="th_div">
										<p class="hide_p">隐藏该列</p>
									</div>
								</div>
								<div class="table_display_th" data-column="5">
									<span>操作事件</span>
									<div class="th_div">
										<p class="hide_p">隐藏该列</p>
									</div>
								</div>
							</div>
						</div>
						<div class="page_foot">
							<span class="left_page"><<</span>
							<span class="page_desc">
								当前页数 <span class="cur_page">1</span>/<span class="all_page">1</span>
							</span>
							<span class="right_page">>></span>
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

        var log_num = 0; //记录日志序号
        var hide_column_array = []; //记录需要隐藏的列数

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

		//点击日志查询列
		function clickLogColumn() {
			$(".log_table .table_display_th").unbind("click").click(function() {
				$(this).children(".th_div").toggle();
				// console.log($(this).children("span").width());
				var left_dis = parseFloat($(this).children("span").width()) + 25 - parseFloat($(this).children(".th_div").width());
				$(this).children(".th_div").css("left", left_dis+"px");
			})
		}

		//点击隐藏该列按钮
		function clickhideP() {
			$(".hide_p").each(function(){
				$(this).click(function() {
					var nth_num = $(this).parent().parent().data("column");
					hide_column_array.push(nth_num);
					$(".log_table .table_display_th:nth-child(" + nth_num + ")").css("display", "none");
					$(".log_table .table_display_td:nth-child(" + nth_num + ")").each(function(){
						$(this).css("display", "none");
					});
				})
			})
		}

		//发送查询ajax
		function getLog(page, section, start_time, end_time, type, keyword) {
    		$.ajax({
    			type: "POST",
    			url: "http://"+ip2+"/invoice/getTwentyAction.action",
    			data: {
    				page: page, //首次请求
    				section: section,
    				startTime: start_time,
    				endTime: end_time,
    				type: type,
    				keyword: keyword
    			},
    			success: function(res, status) {
    				// console.log(res);
    				var data = JSON.parse(res);
    				// $(".select_result").css("display", "block");
    				//先清空上一次查询的结果
    				$(".select_result .log_table .table_display_row:nth-child(n+2)").remove();
    				hide_column_array.splice(0, hide_column_array.length);
    				
    				log_num = page * section;

    				if(data.action_list.length == 0 || data.action_list == undefined) {
    					$(".select_result .text_describe").css("display", "block");
    					$(".select_result .log_table_container").css("display", "none");
    				}

    				else{
    					$(".select_result .text_describe").css("display", "none");
    					$(".select_result .log_table_container").css("display", "block");
    					$(".all_page").text(data.page_sum);

        				for(var i = 0; i < data.action_list.length; i++) {
        					// console.log(data.action_list[i]);
        					var temp_data = data.action_list[i];
							// var data = res[i];
							$(".select_result .log_table").append("<div class=\"table_display_row\"><div class=\"table_display_td\">"+(++log_num)+"</div><div class=\"table_display_td\">"+temp_data.action_time+"</div><div class=\"table_display_td\">"+temp_data.user_ip+"</div><div class=\"table_display_td\">"+temp_data.user_name+"</div><div class=\"table_display_td\">"+temp_data.description+"</div></div>");
        				}	
    				}
    			},
    			error: function() {
    				$(".select_result .log_table_container").css("display", "none");
    				$(".select_result .text_describe").css("display", "block");
    			}
    		})
		}

        $(document).ready(function(){
        	// console.log(document.documentElement.clientHeight);
        	// 判断权限
        	justifyUserGrant(user_json);
        	justifyRW(user_json);

        	getNowFormatDate();

        	//首次加载日志查询
        	getLog(0, $("input[name='section']").val(), null, null, null, null);


        	//绑定提交按钮获取日志查询结果
        	$("#rizhi_select").click(function(){
        		// console.log($("#start_date").val() + " " + $("#start_time").val());
        		var start_time = null;
        		var end_time = null;
        		if($("#start_date").val() != null && $("#start_time").val() != null) {
        			start_time = $("#start_date").val() + " " + $("#start_time").val();
        		}
        		if($("#end_date").val() != null && $("#end_time").val() != null) {
        			end_time = $("#end_date").val() + " " + $("#end_time").val();
        		}
        		getLog(parseInt($(".cur_page").text())-1, $("input[name='section']").val(), start_time, end_time, $("select[name='type']").val(), $("#keyword").val());
        	})

        	$(".right_page").click(function() {
        		var start_time = null;
        		var end_time = null;
        		if($("#start_date").val() != null && $("#start_time").val() != null) {
        			start_time = $("#start_date").val() + " " + $("#start_time").val();
        		}
        		if($("#end_date").val() != null && $("#end_time").val() != null) {
        			end_time = $("#end_date").val() + " " + $("#end_time").val();
        		}
        		$(".cur_page").text(parseInt($(".cur_page").text())+1);
        		getLog(parseInt($(".cur_page").text())-1, $("input[name='section']").val(), start_time, end_time, $("select[name='type']").val(), $("#keyword").val());
        	})

        	$(".left_page").click(function() {
        		var start_time = null;
        		var end_time = null;
        		if($("#start_date").val() != null && $("#start_time").val() != null) {
        			start_time = $("#start_date").val() + " " + $("#start_time").val();
        		}
        		if($("#end_date").val() != null && $("#end_time").val() != null) {
        			end_time = $("#end_date").val() + " " + $("#end_time").val();
        		}
        		$(".cur_page").text(parseInt($(".cur_page").text())-1);
        		getLog(parseInt($(".cur_page").text())-1, $("input[name='section']").val(), start_time, end_time, $("select[name='type']").val(), $("#keyword").val());
        	})


        	clickLogColumn();
        	clickhideP();
        })
	</script>
</body>
</html>