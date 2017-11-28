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
				<span class="flex-1">个人设置</span>
			</div>

			<div class="panel panel-default panel-box-shadow detect_div_container" style="margin-top: 0px;">
			    <div class="detect_div" style="padding: 30px 30px;">
					<p class="detect_div_hd">基本设置</p>
					<div class="table_display_container ownedit_table">
						<div class="table_display_row">
							<span class="table_display_th">基本信息</span>
							<span class="table_display_th"></span>
							<span class="table_display_th text_right">操作</span>
						</div>
						<div class="table_display_row">
							<span class="table_display_td">账户名称</span>
							<span class="table_display_td user_name">Eric Wong</span>
							<span class="table_display_td text_right edit"><a>修改</a></span>
						</div>
						<div class="table_display_row">
							<span class="table_display_td">账户id</span>
							<span class="table_display_td user_id">wx3e4e1ddf8d7f6773</span>
							<span class="table_display_td text_right edit"></span>
						</div>
						<div class="table_display_row">
							<span class="table_display_td">所属单位</span>
							<span class="table_display_td company_name">中山大学</span>
							<span class="table_display_td text_right edit"></span>
						</div>
					</div>
			    </div>
			</div>

			<div class="panel panel-default panel-box-shadow detect_div_container">
			    <div class="detect_div" style="padding: 30px 30px;">
					<p class="detect_div_hd">操作权限<span>以下为用户可以操作的权限</span></p>
					<div style="overflow-x: auto;">
						<div class="table_display_container edit_grant_table">
							<div class="table_display_row">
								<div class="table_display_th">修改模板</div>
								<div class="table_display_th">增加模板</div>
								<div class="table_display_th">查询模板</div>
								<div class="table_display_th">删除模板</div>
								<div class="table_display_th">监控识别</div>
								<div class="table_display_th">日志查询</div>
								<div class="table_display_th">缓冲队列查询</div>
								<div class="table_display_th">错误发票查询</div>
								<div class="table_display_th">增加用户</div>
								<div class="table_display_th">编辑用户</div>
								<div class="table_display_th">查询用户</div>
								<div class="table_display_th">删除用户</div>
							</div>
							<div class="table_display_row">
							</div>
						</div>
					</div>
			    </div>
			</div>
		</div>
	</main>

	<!-- 模态窗口 -->
	<div class="modal fade col-lg-10" id="editModal" tabindex="-1" aria-hidden="true" style="margin: 0px auto; ">
		<div>
	        <div class="modal-content">
	        	<div class="modal-header">
	        		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        		<h4 class="modal-title">修改信息</h4>
	        	</div>
	        	<div class="modal-body">
	        		<span class="edit_name">账户名称</span>
	        		<input type="text" name="edit_content" class="form-control" />
	        	</div>
	        	<div class="modal-footer">
	                <button type="button" class="btn btn-primary" data-dismiss="modal" id="certain_edit">确定</button>
	                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
	            </div>
	        </div>
	    </div>
	</div>


	<script type="text/javascript" src="script/common.js"></script>
	<script type="text/javascript">

        //jsp加入
        var user_json = <%=JSON.toJSONString(request.getAttribute("user"))%>

		function showEditModal() {
			$(".edit").click(function() {
				$("#editModal").modal("show");
			})
		}

		function displayUserJson() {
			//显示基本信息
			$(".user_name").text(user_json.user_name);
			$(".user_id").text(user_json.user_id);
			$(".company_name").text(user_json.company_name);

			//显示权限信息

			//添加权限选取框
			for(var j = 1; j <= 14; j++) {
				if(j == 5) {
					j ++;
					continue;
				}
				$(".edit_grant_table .table_display_row:last-child").append("<div class=\"table_display_td\"><i class=\"fa fa-square-o\" aria-hidden=\"true\"></i></div>");
				// console.log(user_json.permissions);
				grantSwitch($(".edit_grant_table .table_display_row:last-child .table_display_td:last-child"), user_json.permissions, j);
			}
		}

        $(document).ready(function(){
        	// console.log(document.documentElement.clientHeight);
        	// 判断权限
        	justifyUserGrant(user_json);
        	displayUserJson();
        	// 绑定模态框及调整垂直居中
        	showEditModal();
        	modelMiddle($("#editModal"));
        })
	</script>
</body>
</html>