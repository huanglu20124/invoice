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
        <span class="flex-none own_user_name" style="margin-right: 1.5em; font-size: 16px; color: #6a6e76;"> </span>
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
				<a href="" class="aside_nav_list-item nav_disabled" data-permission="queue">
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
                <a href="${pageContext.request.contextPath}/ownedit.action" class="aside_nav_list-item selected">
                    <i class="fa fa-cog aside_nav_list-item-icon" aria-hidden="true"></i>
                    <span>个人设置</span>
                </a>
			</div>
		</aside>
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
				grantSwitch(user_json.permissions, j);
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