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
				<span class="flex-1">用户管理</span>
			</div>

			<div class="panel panel-default panel-box-shadow detect_div_container" style="margin-top: 0px;">
			    <div class="detect_div users_div_container" style="padding: 30px 30px;">
					<p class="detect_div_hd">用户组管理<span>管理员可以编辑本单位的用户组权限</span></p>

					<!-- <div class="users_div">
						 <i class="fa fa-users" aria-hidden="true"></i>
						 <div class="users_desc">
						 	<p>用户组1</p>
							<p>wx3e4e1</p>
						 </div>
						 <div class="modal_menu">
						 	<p class="startUsersGrant">编辑权限</p>
						 	<p class="startUsersMember">编辑用户组成员</p>
						 </div>
					</div> -->

			    </div>
			</div>

			<div class="panel panel-default panel-box-shadow detect_div_container">
			    <div class="detect_div user_div_container" style="padding: 30px 30px;">
					<p class="detect_div_hd flex flex-align-center">
						<span class="flex-none" style="color: inherit; font-size: inherit; margin-left: 0px;">成员管理</span>
						<span class="flex-1">管理员可以配置成员的特定权限</span>
						<!-- <span class="flex-none success_info" style="margin-right: 10px; display: none;">权限编辑成功</span>
						<span class="flex-none fail_info" style="margin-right: 10px; display: none;">权限编辑失败</span>
						 -->
					</p>

				</div>
			</div>
		</div>
	</main>

	<!-- 模态框（Modal） -->
	<div class="modal fade" id="userGrantModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="width: 980px; margin: 0 auto; overflow: auto;">
	    <div style="display: table-cell; vertical-align: middle;">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_modal">&times;</button>
	                <h4 class="modal-title" id="myModalLabel" style="display: inline-block; vertical-align: middle; width: auto; margin-right: 10px;">成员权限</h4>
	            </div>
	            <div class="modal-body" style="padding: 0px;">
	            	<div style="overflow-x: auto;">
						<div class="table_display_container user_grant_table">
							<div class="table_display_row">
								<div class="table_display_th">权限对象</div>
								<div class="table_display_th">继承用户组</div>
								<div class="table_display_th">（继承）可读</div>
								<div class="table_display_th">（继承）可写</div>
								<div class="table_display_th">（私有）可读</div>
								<div class="table_display_th">（私有）可写</div>
								<div class="table_display_th">（汇总）可读</div>
								<div class="table_display_th">（汇总）可写</div>
							</div>
							<div class="table_display_row" grant_type="queue">
								<div class="table_display_td">缓冲队列</div>
								<div class="table_display_td users_group">无</div>
								<div class="table_display_td inherit_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="console">
								<div class="table_display_td">算法可视</div>
								<div class="table_display_td users_group">无</div>
								<div class="table_display_td inherit_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="model">
								<div class="table_display_td">发票模板</div>
								<div class="table_display_td users_group">无</div>
								<div class="table_display_td inherit_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="fault">
								<div class="table_display_td">错误发票</div>
								<div class="table_display_td users_group">无</div>
								<div class="table_display_td inherit_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="log">
								<div class="table_display_td">日志查询</div>
								<div class="table_display_td users_group">无</div>
								<div class="table_display_td inherit_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="user">
								<div class="table_display_td">用户管理</div>
								<div class="table_display_td users_group">无</div>
								<div class="table_display_td inherit_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="7">
								<div class="table_display_td">单位管理</div>
								<div class="table_display_td users_group">无</div>
								<div class="table_display_td inherit_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
						</div>
					</div>
	            </div>
	            <div class="modal-footer">
	            	<button type="button" class="btn btn-primary flex-none edit_grant" style="padding: 5px 15px;" data-write="true">编辑</button>
					<button type="button" class="btn btn-primary flex-none save_grant" style="padding: 5px 15px; margin-right: 10px; display: none;" data-dismiss="modal">保存</button>
					<button type="button" class="btn btn-default flex-none cancel_edit" style="padding: 5px 15px; display: none;" data-dismiss="modal">取消</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal -->
	</div>

	<div class="modal fade" id="usersGrantModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="width: 980px; margin: 0 auto; overflow: auto;">
	    <div style="display: table-cell; vertical-align: middle;">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_modal">&times;</button>
	                <h4 class="modal-title" style="display: inline-block; vertical-align: middle; width: auto; margin-right: 10px;">用户组权限管理</h4>
	            </div>
	            <div class="modal-body" style="padding: 0px;">
	            	<div style="overflow: auto;">
						<div class="table_display_container users_grant_table">
							<div class="table_display_row">
								<div class="table_display_th">权限对象</div>
								<div class="table_display_th">可读</div>
								<div class="table_display_th">可写</div>
							</div>
							<div class="table_display_row" grant_type="queue">
								<div class="table_display_td">缓冲队列</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="console">
								<div class="table_display_td">算法可视</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="model">
								<div class="table_display_td">发票模板</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="fault">
								<div class="table_display_td">错误发票</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="log">
								<div class="table_display_td">日志查询</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="user">
								<div class="table_display_td">用户管理</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">单位管理</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
						</div>
					</div>
	            </div>
	            <div class="modal-footer">
	            	<button type="button" class="btn btn-primary flex-none edit_grant" style="padding: 5px 15px;" data-write="true">编辑</button>
					<button type="button" class="btn btn-primary flex-none save_grant" style="padding: 5px 15px; margin-right: 10px; display: none;" data-dismiss="modal">保存</button>
					<button type="button" class="btn btn-default flex-none cancel_edit" style="padding: 5px 15px; display: none;" data-dismiss="modal">取消</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal -->
	</div>

	<div class="modal fade" id="usersMemberModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="width: 1080px; margin: 0 auto; overflow: auto;">
	    <div style="display: table-cell; vertical-align: middle;">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_modal">&times;</button>
	                <h4 class="modal-title" style="display: inline-block; vertical-align: middle; width: auto; margin-right: 10px;">用户组成员管理</h4>
	            </div>
	            <div class="modal-body" style="padding: 20px 0px 20px 20px; overflow: auto;">
	            	<div style="float: right; width: 280px; min-height: 300px; padding: 0px 20px; height: 100%; border-left: 1px solid rgba(200, 200, 200, 0.4);">
						<button type="button" class="btn btn-primary" style="width: 100%;" id="start_edit">启用编辑</button>

						<input type="text" class="form-control" name="addUserId" placeholder="请输入要添加至本用户组的用户id" style="margin-top: 40px;" disabled="true" id="addUserId"/>
						<button type="button" class="btn btn-primary" style="width: 100%; margin-top: 20px;" disabled="true" id="addUserBtn">添加用户</button>
					</div>
	            	<div style="margin-right: 300px;">
						<div class="user_div users_member_div">
							 <img src="pic/头像.png" style="width: 40px; margin-right: 10px;">
							 <div class="user_desc">
							 	<p>Eric Wong</p>
								<p>中山大学</p>
							 </div>
							 <i class="fa fa-times-circle" aria-hidden="true"></i>
						</div>

						<div class="user_div users_member_div">
							 <img src="pic/头像.png" style="width: 40px; margin-right: 10px;">
							 <div class="user_desc">
							 	<p>Eric Wong</p>
								<p>中山大学</p>
							 </div>
						</div>

						<div class="user_div users_member_div">
							 <img src="pic/头像.png" style="width: 40px; margin-right: 10px;">
							 <div class="user_desc">
							 	<p>Eric Wong</p>
								<p>中山大学</p>
							 </div>
						</div>

						<div class="user_div users_member_div">
							 <img src="pic/头像.png" style="width: 40px; margin-right: 10px;">
							 <div class="user_desc">
							 	<p>Eric Wong</p>
								<p>中山大学</p>
							 </div>
						</div>
					</div>
					
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal -->
	</div>

	<script type="text/javascript" src="script/common.js"></script>
	<script type="text/javascript">

        //jsp加入
        var user_json = <%=JSON.toJSONString(request.getAttribute("user"))%>
    
		var user_grant_array = []; //记录当前的原本用户对象及其权限的数组
		var edit_grant_array = []; //记录正在修改的用户对象及其权限的数组
		var send_grant_array = []; //将要发送给服务器的被修改的用户对象及其权限的数组

		var users_grant_array = []; //记录当前的原本用户对象及其权限的数组
		var users_edit_array = []; //记录正在修改的用户对象及其权限的数组
		var users_send_array = []; //将要发送给服务器的被修改的用户对象及其权限的数组

		//绑定编辑按钮，点击后开始编辑
		function beginEdit(type) {
			$(".edit_grant").each(function(){
				$(this).unbind("click").click(function() {
					$(this).css("display", "none");
					$(".save_grant").css("display", "inline-block");
					$(".cancel_edit").css("display", "inline-block");

					if(type == 0) { //代表user_modal
						$(".user_grant_table .table_display_td[data-write='true'] i").css("cursor", "pointer");
						$(".user_grant_table .table_display_td[data-write='true'] i").each(function() {
							$(this).click(function(){clickIcon($(this))});
						})
						$(".user_grant_table").addClass("display_table_hover");
					}
					else if(type == 1) { //代表users_grant
						$(".users_grant_table .table_display_td[data-write='true'] i").css("cursor", "pointer");
						$(".users_grant_table .table_display_td[data-write='true'] i").each(function() {
							$(this).click(function(){clickIcon($(this))});
						})
					}
				})
			})
		}

		//退出编辑界面
		function exitEdit() {
			$(".edit_grant").css("display", "inline-block");
			$(".save_grant").css("display", "none");
			$(".cancel_edit").css("display", "none");

			$(".grant_table i").css("cursor", "default");
			$(".grant_table i").unbind("click");
		}

		//点击取消按钮
		function clickCancel() {
			$("#cancel_edit").click(function() {
				//还原全部数组
				edit_grant_array = user_grant_array;
				send_grant_array.splice(0, send_grant_array.length);

				//还原视图
				$(".grant_table").html("")
				$(".grant_table").append("<div class=\"table_display_row\"><div class=\"table_display_th\">成员</div><div class=\"table_display_th\">修改模板</div><div class=\"table_display_th\">增加模板</div><div class=\"table_display_th\">查询模板</div><div class=\"table_display_th\">删除模板</div><div class=\"table_display_th\">监控识别</div><div class=\"table_display_th\">日志查询</div><div class=\"table_display_th\">缓冲队列查询</div><div class=\"table_display_th\">错误发票查询</div><div class=\"table_display_th\">增加用户</div><div class=\"table_display_th\">编辑用户</div><div class=\"table_display_th\">查询用户</div><div class=\"table_display_th\">删除用户</div></div>");
				for(var i = 0; i < user_grant_array.length; i++) {
					addUserGrant(user_grant_array[i]);
				}

				exitEdit();
			})
		}

		//点击保存按钮
		function clickSave() {
			$("#save_grant").click(function() {
				//发送ajax请求告诉服务器哪些用户的哪些权限被修改
				console.log(send_grant_array);
				$.ajax({
					type: 'POST',
					url : 'http://' + ip2 + '/invoice/updateUsersPermission.action',
					data : {
						user_list : JSON.stringify(send_grant_array)
					},
					success : function() {
						user_grant_array = edit_grant_array;
						send_grant_array.splice(0, send_grant_array.length);
						exitEdit();
						$(".success_info").css("display", "inline-block");
						setTimeout(function() {
							$(".success_info").css("display", "none");
						}, 2500)
					},
					error : function(err) {
						exitEdit();
						//还原全部数组
						edit_grant_array = user_grant_array;
						send_grant_array.splice(0, send_grant_array.length);

						//还原视图
						$(".grant_table").html("")
						$(".grant_table").append("<div class=\"table_display_row\"><div class=\"table_display_th\">成员</div><div class=\"table_display_th\">修改模板</div><div class=\"table_display_th\">增加模板</div><div class=\"table_display_th\">查询模板</div><div class=\"table_display_th\">删除模板</div><div class=\"table_display_th\">监控识别</div><div class=\"table_display_th\">日志查询</div><div class=\"table_display_th\">缓冲队列查询</div><div class=\"table_display_th\">错误发票查询</div><div class=\"table_display_th\">增加用户</div><div class=\"table_display_th\">编辑用户</div><div class=\"table_display_th\">查询用户</div><div class=\"table_display_th\">删除用户</div></div>");
						for(var i = 0; i < user_grant_array.length; i++) {
							addUserGrant(user_grant_array[i]);
						}
						$(".fail_info").css("display", "inline-block");
						setTimeout(function() {
							$(".fail_info").css("display", "none");
						}, 2500)
					}
				})

			})
		}

		//点击勾选图标
		function clickIcon(click_jq) {
			toggleChecked(click_jq);

			//修改edit_grant_array
			for(var i = 0; i < edit_grant_array.length; i++) {
				if(edit_grant_array[i].user_id == click_jq.parent().parent().prop("user_id")) {
					if(click_jq.hasClass("fa-check-square-o")) {
						edit_grant_array[i].permissions.push({
							permission_id: click_jq.parent().prop("grant_type")
						});	
					}
					else {
						for(var j = 0; j < edit_grant_array[i].permissions.length; j++) {
							if(edit_grant_array[i].permissions[j].permission_id == click_jq.parent().prop("grant_type")) {
								edit_grant_array[i].permissions.splice(j, 1);
							}
						}
					}
					break;
				}
			}

			//记录修改的地方并放入send_grant_array
			var hasUserId = false;
			for(var i = 0; i < send_grant_array.length; i++) {
				if(send_grant_array[i].user_id == click_jq.parent().parent().prop("user_id")) {
					hasUserId = true;
					for(var j = 0; j < send_grant_array[i].update_permission.length; j++) {
						if(send_grant_array[i].update_permissions[j].permission_id == click_jq.parent().prop("grant_type")){
							send_grant_array[i].update_permissions[j].is_checked = send_grant_array[i].update_permissions[j].is_checked == 1 ? 0 : 1;
						}
					}
					break;
				}
			}

			if(hasUserId == false) {
				//alert(click_jq.parent().prop("grant_type"));
				send_grant_array.push({
					user_id : click_jq.parent().parent().prop("user_id"),
					group_id : click_jq.parent().parent().prop("group_id"),
					update_permissions : [{
						permission_id : click_jq.parent().prop("grant_type"),
						is_checked : click_jq.hasClass("fa-check-square-o") == true ? 1 : 0
					}]
				})	
			}

		}

		//将array中的用户对象放入视图
		function addUserGrant(temp_user) {

			$(".user_div_container").append("<div class=\"user_div\"><img src=\"pic/头像.png\" style=\"width: 40px; margin-right: 10px;\"><div class=\"user_desc\"><p>"+temp_user.user_name+"</p><p>"+temp_user.company_name+"</p></div></div>");

			$(".user_div_container .user_div:last-child").get(0).user_id = temp_user.user_id;
			$(".user_div_container .user_div:last-child").get(0).group_id = temp_user.group_id;
			clickUser($(".user_div_container .user_div:last-child"));
		}

		//增加用户组
		function addUsersGroup(temp_group) {
			$(".users_div_container").append("<div class=\"users_div\"><i class=\"fa fa-users\" aria-hidden=\"true\"></i><div class=\"users_desc\"><p>"+temp_group.group_name+"</p><p>"+temp_group.group_id+"</p></div><div class=\"modal_menu\"><p class=\"startUsersGrant\">编辑权限</p><p class=\"startUsersMember\">编辑用户组成员</p></div></div>");

			$(".users_div_container .users_div:last-child").get(0).users_object = temp_group;
			hoverUsers($(".users_div_container .users_div:last-child"));
		}

		//获取用户及其权限
		function getUserGrant() {
			$.ajax({
				type: 'POST',
				url : "http://" + ip2 + "/invoice/getManagerUsers.action",
				data : {
					user_id : user_id
				},
				success : function(res, status) {
					var data = JSON.parse(res).user_list;
					for(var i = 0; i < data.length; i++) {
						var temp_user = data[i];
						user_grant_array.push(temp_user);
						addUserGrant(temp_user);
						// console.log(data.length);
					}
					// edit_grant_array = user_grant_array;
				},
				error: function() {
					console.log("error");
				}
			})
		}

		//获取用户组及其权限
		function getUsersGrant() {
			$.ajax({
				type: 'POST',
				url : "http://" + ip2 + "/invoice/getManagerGroups.action",
				data : {
					user_id : user_id
				},
				success : function(res, status) {
					console.log(res);
					var data = JSON.parse(res).group_list;
					for(var i = 0; i < data.length; i++) {
						var temp_group = data[i];
						addUsersGroup(temp_group);
						// console.log(data.length);
					}
					// edit_grant_array = user_grant_array;
				},
				error: function() {
					console.log("error");
				}
			})
		}

		//根据permission_list来绘制user_modal表格
		function flushUserGrantTable(permissions) {
			$("")
			for(var i = 0; i < permissions.length; i++) {

			}
		}

		//根据users_permission_list来绘制Users_grant_modal的表格
		function flushUsersGrantTable(permissions) {
			for(var i = 0; i < permissions.length; i++) {
				
			}
		}

		//点击成员头像
		function clickUser(user_jq) {
			user_jq.click(function() {
				$("#userGrantModal").modal('show');
				$("#userGrantModal").css("display", "table");
				ModalVerticalAlign($("#userGrantModal").get(0));
			})
		}

		//悬浮用户组头像
		function hoverUsers(users_jq) {
			var users_object = users_jq.users_object;
			clickStartUsersGrant(users_object);
			clickStartUsersMember(users_object);
			users_jq.mouseenter(function() {
				$(this).children(".modal_menu").css("opacity", "1");
			})
			users_jq.mouseleave(function() {
				$(this).children(".modal_menu").css("opacity", "0");
			})
		}

		//点击用户组编辑用户组权限按钮
		function clickStartUsersGrant(users_object) {
			$(".startUsersGrant").click(function() {
				$("#usersGrantModal").modal('show');
				$("#usersGrantModal").css("display", "table");
				ModalVerticalAlign($("#usersGrantModal").get(0));

				//获取权限填写表格
				users_grant_array = users_object.permissions;

			})
		}

		//点击用户组编辑成员权限按钮
		function clickStartUsersMember(users_object) {
			$(".startUsersMember").click(function() {
				$("#usersMemberModal").modal('show');
				$("#usersMemberModal").css("display", "table");
				ModalVerticalAlign($("#usersMemberModal").get(0));	
			})
		}

		//初始化user_grant_modal模态框
		function initUserGrantModal() {
			$("#userGrantModal").on("show.bs.modal", function() {
				$(".edit_grant").css("display", "inline-block");
				$(".save_grant").css("display", "none");
				$(".cancel_edit").css("display", "none");	

				$(".user_grant_table .table_display_td[data-write='true'] i").css("cursor", "default");
				$(".user_grant_table .table_display_td[data-write='true'] i").each(function() {
					$(this).unbind("click");
				})
				$(".user_grant_table").removeClass("display_table_hover");

				beginEdit(0);
			})
		}

		//初始化users_grant_modal模态框
		function initUsersGrantModal() {
			$("#usersGrantModal").on("show.bs.modal", function() {
				$(".edit_grant").css("display", "inline-block");
				$(".save_grant").css("display", "none");
				$(".cancel_edit").css("display", "none");	

				$(".users_grant_table .table_display_td[data-write='true'] i").css("cursor", "default");
				$(".users_grant_table .table_display_td[data-write='true'] i").each(function() {
					$(this).unbind("click");
				})
				beginEdit(1);
			})
		}

		//初始化users_member_modal模态框
		function initUsersMemberModal() {
			$("#usersMemberModal").on("show.bs.modal", function() {
				$("#start_edit").attr("disabled", false);
				$("#addUserId").attr("disabled", true);
				$("#addUserId").val("");
				$("#addUserBtn").attr("disabled", true);

				$(".users_member_div i").css("opacity", "0");
				clickStartEdit();
			})
		}

		//点击users_member模态框中的编辑按钮
		function clickStartEdit() {
			$("#start_edit").unbind('click').click(function() {
				$(".users_member_div i").css("opacity", "1");
				$("#start_edit").attr("disabled", true);
				$("#addUserId").attr("disabled", false);
				$("#addUserBtn").attr("disabled", false);
			})
		}

        $(document).ready(function(){
        	console.log(document.documentElement.clientHeight);
        	console.log($("#userGrantModal").get(0).offsetHeight);
        	// 判断权限
        	justifyUserGrant(user_json);

        	initUserGrantModal();
        	initUsersGrantModal();
        	initUsersMemberModal();

        	getUserGrant();
        	getUsersGrant();
        })
	</script>
</body>
</html>