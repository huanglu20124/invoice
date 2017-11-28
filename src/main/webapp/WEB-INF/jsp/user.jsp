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
			    <div class="detect_div" style="padding: 30px 30px;">
					<p class="detect_div_hd">用户组管理<span>管理员可以编辑本单位的用户组权限</span></p>

					<div class="users_div">
						 <i class="fa fa-users" aria-hidden="true"></i>
						 <div class="users_desc">
						 	<p>用户组1</p>
							<p>wx3e4e1</p>
						 </div>
						 <div class="modal_menu">
						 	<p class="startUsersGrant">编辑权限</p>
						 	<p class="startUsersMember">编辑用户组成员</p>
						 </div>
					</div>
					
					<div class="users_div">
						 <i class="fa fa-users" aria-hidden="true"></i>
						 <div class="users_desc">
						 	<p>用户组2</p>
							<p>wx3e4e2</p>
						 </div>
					</div>

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
					
					<div class="user_div">
						 <img src="pic/头像.png" style="width: 40px; margin-right: 10px;">
						 <div class="user_desc">
						 	<p>Eric Wong</p>
							<p>中山大学</p>
						 </div>
					</div>

					<!-- <div style="overflow-x: auto;">
						<div class="table_display_container grant_table">
							<div class="table_display_row">
								<div class="table_display_th">成员</div>
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
						</div>
					</div> -->
				</div>
			</div>
		</div>
	</main>

	<!-- 模态框（Modal） -->
	<div class="modal fade" id="userGrantModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="width: 980px; margin: 0 auto;">
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
							<div class="table_display_row">
								<div class="table_display_td">缓冲队列</div>
								<div class="table_display_td">无</div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">算法可视</div>
								<div class="table_display_td">无</div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">发票模板</div>
								<div class="table_display_td">无</div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">错误发票</div>
								<div class="table_display_td">无</div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">日志查询</div>
								<div class="table_display_td">无</div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">用户管理</div>
								<div class="table_display_td">无</div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">单位管理</div>
								<div class="table_display_td">无</div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
						</div>
					</div>
	            </div>
	            <div class="modal-footer">
	            	<button type="button" class="btn btn-primary flex-none" style="padding: 5px 15px;" id="edit_grant">编辑</button>
					<button type="button" class="btn btn-primary flex-none" style="padding: 5px 15px; margin-right: 10px; display: none;" id="save_grant" data-dismiss="modal">保存</button>
					<button type="button" class="btn btn-default flex-none" style="padding: 5px 15px; display: none;" id="cancel_edit" data-dismiss="modal">取消</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal -->
	</div>

	<div class="modal fade" id="usersGrantModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="width: 980px; margin: 0 auto;">
	    <div style="display: table-cell; vertical-align: middle;">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_modal">&times;</button>
	                <h4 class="modal-title" style="display: inline-block; vertical-align: middle; width: auto; margin-right: 10px;">用户组管理</h4>
	            </div>
	            <div class="modal-body" style="padding: 0px;">
	            	<div style="overflow: auto;">
						<div class="table_display_container user_grant_table">
							<div class="table_display_row">
								<div class="table_display_th">权限对象</div>
								<div class="table_display_th">可读</div>
								<div class="table_display_th">可写</div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">缓冲队列</div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">算法可视</div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">发票模板</div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">错误发票</div>
								<div class="table_display_td">无</div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">日志查询</div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">用户管理</div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row">
								<div class="table_display_td">单位管理</div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" data-write="true"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
						</div>
					</div>
	            </div>
	            <div class="modal-footer">
	            	<button type="button" class="btn btn-primary flex-none" style="padding: 5px 15px;" id="edit_grant">编辑</button>
					<button type="button" class="btn btn-primary flex-none" style="padding: 5px 15px; margin-right: 10px; display: none;" id="save_grant" data-dismiss="modal">保存</button>
					<button type="button" class="btn btn-default flex-none" style="padding: 5px 15px; display: none;" id="cancel_edit" data-dismiss="modal">取消</button>
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

		//绑定编辑按钮，点击后开始编辑
		function beginEdit() {
			$("#edit_grant").click(function() {
				$(this).css("display", "none");
				$("#save_grant").css("display", "inline-block");
				$("#cancel_edit").css("display", "inline-block");

				$(".user_grant_table .table_display_td[data-write='true'] i").css("cursor", "pointer");
				$(".user_grant_table .table_display_td[data-write='true'] i").each(function() {
					$(this).click(function(){clickIcon($(this))});
				})
				$(".user_grant_table").addClass("display_table_hover");
			})
		}

		//退出编辑界面
		function exitEdit() {
			$("#edit_grant").css("display", "inline-block");
			$("#save_grant").css("display", "none");
			$("#cancel_edit").css("display", "none");

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
					console.log(data);
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

		//点击成员头像
		function clickUser(user_jq) {
			user_jq.click(function() {
				$("#userGrantModal").modal('show');
				$("#userGrantModal").css("display", "table");
				ModalVerticalAlign($("#userGrantModal").get(0));
			})
		}

		//点击用户组头像
		function clickUsers(users_jq) {
			users_jq.click(function() {
				$("#usersGrantModal").modal('show');
				$("#usersGrantModal").css("display", "table");
				ModalVerticalAlign($("#usersGrantModal").get(0));
			})
		}

        $(document).ready(function(){
        	console.log(document.documentElement.clientHeight);
        	console.log($("#userGrantModal").get(0).offsetHeight);
        	// 判断权限
        	justifyUserGrant(user_json);

        	getUserGrant();
        	beginEdit();
        	// clickCancel();
        	// clickSave();

        })
	</script>
</body>
</html>