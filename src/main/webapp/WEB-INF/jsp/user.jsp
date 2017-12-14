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
					<p class="detect_div_hd flex flex-align-center">
						<span class="flex-none" style="color: inherit; font-size: inherit; margin-left: 0px;">用户组管理</span>
						<span class="flex-1">管理员可以编辑本单位的用户组权限</span>
						<span class="flex-none text-primary" style="margin-right: 10px; display: inline-block; cursor: pointer;" title="增加用户组"><i class="fa fa-plus" aria-hidden="true"></i></span>
						<span class="flex-none text-danger" title="删除用户组" style="cursor: pointer; margin-left: 0px;"><i class="fa fa-minus" aria-hidden="true"></i></span>
					</p>

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
								<div class="table_display_th" data-writeshow="true">继承用户组</div>
								<div class="table_display_th" data-writeshow="true">（继承）可读</div>
								<div class="table_display_th" data-writeshow="true">（继承）可写</div>
								<div class="table_display_th" data-writeshow="true" data-write="true">（私有）可读</div>
								<div class="table_display_th" data-writeshow="true" data-write="true">（私有）可写</div>
								<div class="table_display_th">（汇总）可读</div>
								<div class="table_display_th">（汇总）可写</div>
							</div>
							<div class="table_display_row" grant_type="queue">
								<div class="table_display_td" title="用户传送的待识别发票的队列">
									<i class="fa fa-bar-chart grant_table_icon"></i>缓冲队列
								</div>
								<div class="table_display_td users_group" data-writeshow="true">无</div>
								<div class="table_display_td inherit_read" data-writeshow="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write" data-writeshow="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read"  data-writeshow="true" data-write="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write"  data-writeshow="true" data-write="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="console">
								<div class="table_display_td" title="发票识别算法运算过程的可视化">
									<i class="fa fa-television grant_table_icon"></i>算法可视
								</div>
								<div class="table_display_td users_group" data-writeshow="true">无</div>
								<div class="table_display_td inherit_read" data-writeshow="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write" data-writeshow="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read"  data-writeshow="true" data-write="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write"  data-writeshow="true" data-write="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="model">
								<div class="table_display_td" title="用户自定义的用于识别发票的模板">
									<i class="fa fa-clipboard grant_table_icon"></i>发票模板
								</div>
								<div class="table_display_td users_group" data-writeshow="true">无</div>
								<div class="table_display_td inherit_read" data-writeshow="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write" data-writeshow="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read"  data-writeshow="true" data-write="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write"  data-writeshow="true" data-write="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="fault">
								<div class="table_display_td" title="无法被算法识别的发票">
									<i class="fa fa-times-circle-o grant_table_icon"></i>无法识别发票
								</div>
								<div class="table_display_td users_group" data-writeshow="true">无</div>
								<div class="table_display_td inherit_read" data-writeshow="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write" data-writeshow="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-writeshow="true" data-write="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-writeshow="true" data-write="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="log">
								<div class="table_display_td" title="查询系统用户的操作日志">
									<i class="fa fa-tasks grant_table_icon"></i>日志查询
								</div>
								<div class="table_display_td users_group" data-writeshow="true">无</div>
								<div class="table_display_td inherit_read" data-writeshow="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write" data-writeshow="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-writeshow="true" data-write="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-writeshow="true" data-write="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="user">
								<div class="table_display_td" title="管理用户的权限">
									<i class="fa fa-user-o grant_table_icon"></i>用户管理
								</div>
								<div class="table_display_td users_group" data-writeshow="true">无</div>
								<div class="table_display_td inherit_read" data-writeshow="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write" data-writeshow="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-writeshow="true" data-write="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-writeshow="true" data-write="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="group">
								<div class="table_display_td" title="管理用户组的权限及成员">
									<i class="fa fa-users grant_table_icon"></i>用户组管理
								</div>
								<div class="table_display_td users_group" data-writeshow="true">无</div>
								<div class="table_display_td inherit_read" data-writeshow="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write" data-writeshow="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-writeshow="true" data-write="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-writeshow="true" data-write="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="company">
								<div class="table_display_td" title="管理单位的名称及负责人等">
									<i class="fa fa-university grant_table_icon" aria-hidden="true"></i>单位管理
								</div>
								<div class="table_display_td users_group" data-writeshow="true">无</div>
								<div class="table_display_td inherit_read" data-writeshow="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td inherit_write" data-writeshow="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_read" data-writeshow="true" data-write="true" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td private_write" data-writeshow="true" data-write="true" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_read"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td gather_write"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
						</div>
					</div>
	            </div>
	            <div class="modal-footer">
	            	<button type="button" class="btn btn-primary flex-none edit_grant" style="padding: 5px 15px;" data-write="true">编辑</button>
					<button type="button" class="btn btn-primary flex-none save_grant" style="padding: 5px 15px; margin-right: 10px; display: none;" data-dismiss="modal" id="user_grant_save">保存</button>
					<button type="button" class="btn btn-default flex-none cancel_edit" style="padding: 5px 15px; display: none;" data-dismiss="modal">取消</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal -->
	</div>

	<div class="modal fade" id="usersGrantModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="width: 980px; margin: 0 auto; overflow: auto;">
	    <div style="display: table-cell; vertical-align: middle;">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
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
								<div class="table_display_td" title="用户传送的待识别发票的队列">缓冲队列</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="console">
								<div class="table_display_td" title="发票识别算法运算过程的可视化">算法可视</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="model">
								<div class="table_display_td" title="用户自定义的用于识别发票的模板">发票模板</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="fault">
								<div class="table_display_td" title="无法被算法识别的发票">无法识别发票</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="log">
								<div class="table_display_td" title="查询系统用户的操作日志">日志查询</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="user">
								<div class="table_display_td" title="管理用户的权限">用户管理</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="group">
								<div class="table_display_td" title="管理用户组的权限及成员">用户组管理</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
							<div class="table_display_row" grant_type="company">
								<div class="table_display_td" title="管理单位的名称及负责人等">单位管理</div>
								<div class="table_display_td" rw_type="r"><i class="fa fa-square-o" aria-hidden="true"></i></div>
								<div class="table_display_td" rw_type="rw"><i class="fa fa-square-o" aria-hidden="true"></i></div>
							</div>
						</div>
					</div>
	            </div>
	            <div class="modal-footer">
	            	<button type="button" class="btn btn-primary flex-none edit_grant" style="padding: 5px 15px;" data-write="true">编辑</button>
					<button type="button" class="btn btn-primary flex-none save_grant" style="padding: 5px 15px; margin-right: 10px; display: none;" data-dismiss="modal" id="users_grant_save">保存</button>
					<button type="button" class="btn btn-default flex-none cancel_edit" style="padding: 5px 15px; display: none;" data-dismiss="modal">取消</button>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal -->
	</div>

	<div class="modal fade" id="usersMemberModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="width: 980px; margin: 0 auto; overflow: auto;">
	    <div style="display: table-cell; vertical-align: middle; position: relative;">
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_modal">&times;</button>
	                <h4 class="modal-title" style="display: inline-block; vertical-align: middle; width: auto; margin-right: 10px;">用户组成员管理</h4>
	                <div class="progress progress-striped active"  style="display: none; vertical-align: middle; width: 30%; margin-bottom: 0px;" id="myModalLabel_progress">
					    <div class="progress-bar" role="progressbar" aria-valuenow="20" 
					        aria-valuemin="0" aria-valuemax="100" style="width: 20%;">
					    </div>
					</div>
	            </div>
	            <div class="modal-body" style="padding: 20px 0px 20px 20px; overflow: auto;">
	            	<div style="float: right; width: 280px; min-height: 300px; padding: 0px 20px; height: 100%; border-left: 1px solid rgba(200, 200, 200, 0.4);">
						<button type="button" class="btn btn-primary" style="width: 100%;" id="start_edit" data-write="true">启用编辑</button>

						<input type="text" class="form-control" name="addUserId" placeholder="请输入要添加至本用户组的用户id" style="margin-top: 40px;" disabled="true" id="addUserId"/>
						<button type="button" class="btn btn-primary" style="width: 100%; margin-top: 20px;" disabled="true" id="addUserBtn">添加用户</button>
					</div>
	            	<div style="margin-right: 300px;" class="users_member_container">
					</div>
					
					<div class="deleteAlert">
						<div class="alertHd">
							<!-- <a type="button" class="close confirm_cancel" aria-hidden="true" style="display: inline-block; vertical-align: middle;">&times;</a> -->
							<h5 style="display: inline-block; vertical-align: middle;">确认删除用户？</h5>
						</div>
						<button type="button" class="btn btn-danger form-control" id="confirm_delete">删除</button>
						<button type="button" class="btn btn-default form-control confirm_cancel">取消</button>
					</div>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal -->
	</div>

	<div class="modal fade" id="progressModal" tabindex="-1" aria-hidden="true" style="margin: 0px auto; width: 33%;">
		<div>
	        <div class="modal-content">
	        	<div class="modal-header">
	        		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        		<h4 class="modal-title">正在修改权限...</h4>
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

	<div class="modal fade" id="confirmModal" tabindex="-1" aria-hidden="true" style="margin: 0px auto; width: 33%;">
		<div>
	        <div class="modal-content">
	        	<div class="modal-header">
	        		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        		<h4 class="modal-title">删除确认</h4>
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
	                <button type="button" class="btn btn-danger" data-dismiss="modal">删除</button>
	                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
	            </div>
	        </div>
	    </div>
	</div>

	<script type="text/javascript" src="script/common.js"></script>
	<script type="text/javascript">

        //jsp加入
        var user_json = <%=JSON.toJSONString(request.getAttribute("user"))%>
    
		var user_grant_array = []; //记录当前的原本用户对象及其权限的数组
		var edit_grant_array = []; //记录正在修改的用户对象及其权限的数组
		var user_send_array = []; //记录将要发送给服务器的被修改的用户对象及其权限的数组

		var click_user_jq; //记录当前被点击的用户对象
		var click_users_jq; //记录当前被点击的用户组对象
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
							$(this).click(function(){clickUserIcon($(this))});
						})
						$(".user_grant_table").addClass("display_table_hover");
						$("[data-writeshow='true']").css("display", "table-cell");

						// ModalVerticalAlign($("#userGrantModal").get(0));
					}
					else if(type == 1) { //代表users_grant
						$(".users_grant_table .table_display_td i").css("cursor", "pointer");
						$(".users_grant_table .table_display_td i").each(function() {
							$(this).unbind("click").click(function(){clickUsersGrantIcon($(this))});
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

		//点击user_modal勾选图标
		function clickUserIcon(click_jq) {
			toggleChecked(click_jq);
			flushGatherGrant(click_jq.parent().parent());

			//修改user_send_array
			var hasUserId = false;
			for(var i = 0; i < user_send_array.length; i++) {
				if(user_send_array[i].permission_name == (click_jq.parent().parent().attr("grant_type") + "-" + click_jq.parent().attr("rw_type"))) {

					users_send_array[i].is_checked = click_jq.hasClass("fa-check-square-o") ? 1 : 0;
					hasUserId = true;
					break;
				}
			}

			if(hasUserId == false) {
				//alert(click_jq.parent().prop("grant_type"));
				user_send_array.push({
					is_checked: click_jq.hasClass("fa-check-square-o") ? 1 : 0,
					permission_name: click_jq.parent().parent().attr("grant_type")+"-"+click_jq.parent().attr("rw_type")
				})	
			}
		}

		//点击user_grant_modal保存按钮
		function clickUserGrantSave() {
			$("#user_grant_save").click(function() {
				//发送ajax请求告诉服务器哪些用户的哪些权限被修改
				// console.log(user_send_array);
				$("#progressModal").modal("show");
				$.ajax({
					type: 'POST',
					url : 'http://' + ip2 + '/invoice/updateUsersPermission.action',
					data : {
						permission_list : JSON.stringify(user_send_array),
						user_id : click_user_jq.get(0).user_object.user_id
					},
					success : function(res, status) {
						// console.log(res);
						$("#progressModal h4").text("权限修改成功");
						$("#progressModal .progress-bar").get(0).style.width = "100%";
						setTimeout(function(){
							$("#progressModal").modal('hide');
						}, 1000);

						click_user_jq.get(0).user_object.permissions = JSON.parse(res).permission_list;
						user_send_array.splice(0, user_send_array.length);
					},
					error : function(err) {
						user_send_array.splice(0, user_send_array.length);
						$("#progressModal h4").text("权限修改失败");
						$("#progressModal .progress-bar").addClass("progress-bar-danger");
						$("#progressModal .btn").get(0).disabled = false;
					}
				})
			})
		}

		//点击users_grant_modal保存按钮
		function clickUsersGrantSave() {
			$("#users_grant_save").click(function() {
				//发送ajax请求告诉服务器哪些用户的哪些权限被修改
				// console.log(users_send_array + " " + click_users_jq.get(0).users_object.group_id);
				$("#progressModal").modal("show");
				$.ajax({
					type: 'POST',
					url : 'http://' + ip2 + '/invoice/updateGroupPermission.action',
					data : {
						permission_list : JSON.stringify(users_send_array),
						group_id : click_users_jq.get(0).users_object.group_id
					},
					success : function(res, status) {
						$("#progressModal h4").text("权限修改成功");
						$("#progressModal .progress-bar").get(0).style.width = "100%";
						setTimeout(function(){
							$("#progressModal").modal('hide');
						}, 1000);

						// //更新每个元素自身保存的permissions
						// for(var i = 0; i < users_send_array.length; i++) {
						// 	var exit_permission = false;
						// 	for(var j = 0; j < click_users_jq.get(0).users_object.permissions.length; j++) {
						// 		if(click_users_jq.get(0).users_object.permissions[j].permission_name == users_send_array[i].permission_name) {
						// 			if(users_send_array[i].is_checked == 0)
						// 				click_users_jq.get(0).users_object.permissions.splice(j, 1);
						// 			exit_permission = true;
						// 			break;
						// 		}
						// 	}	
						// 	if(exit_permission == false) {
						// 		click_users_jq.get(0).users_object.permissions.push(users_send_array[i]);	
						// 	}
						// }

						click_users_jq.get(0).users_object.permissions = JSON.parse(res).permission_list;
						users_send_array.splice(0, users_send_array.length);
					},
					error : function(err) {
						users_send_array.splice(0, users_send_array.length);
						$("#progressModal h4").text("权限修改失败");
						$("#progressModal .progress-bar").addClass("progress-bar-danger");
						$("#progressModal .btn").get(0).disabled = false;
					}
				})
				
			})
		}

		//点击users_modal勾选图标
		function clickUsersGrantIcon(click_jq) {
			toggleChecked(click_jq);
			//修改users_send_array
			var hasUserId = false;
			for(var i = 0; i < users_send_array.length; i++) {
				if(users_send_array[i].permission_name == (click_jq.parent().parent().attr("grant_type") + "-" + click_jq.parent().attr("rw_type"))) {

					users_send_array[i].is_checked = click_jq.hasClass("fa-check-square-o") ? 1 : 0;
					hasUserId = true;
					break;
				}
			}

			if(hasUserId == false) {
				//alert(click_jq.parent().prop("grant_type"));
				users_send_array.push({
					is_checked: click_jq.hasClass("fa-check-square-o") ? 1 : 0,
					permission_name: click_jq.parent().parent().attr("grant_type")+"-"+click_jq.parent().attr("rw_type")
				})	
			}

			// console.log(users_send_array);
		}

		//将array中的用户对象放入视图
		function addUserGrant(temp_user) {

			$(".user_div_container").append("<div class=\"user_div\"><img src=\"pic/头像.png\" style=\"width: 40px; margin-right: 10px;\"><div class=\"user_desc\"><p>"+temp_user.user_name+"</p><p>"+temp_user.company_name+"</p></div></div>");

			$(".user_div_container .user_div:last-child").get(0).user_object = temp_user;

			$(".user_div_container .user_div:last-child").unbind("click").click(function() {
				clickUser($(this));	
			})
		}

		//增加用户组
		function addUsersGroup(temp_group) {
			$(".users_div_container").append("<div class=\"users_div\"><i class=\"fa fa-users\" aria-hidden=\"true\"></i><div class=\"users_desc\"><p>"+temp_group.group_name+"</p><p>"+temp_group.group_id+"</p></div><div class=\"modal_menu\"><p class=\"startUsersGrant\">编辑权限</p><p class=\"startUsersMember\">编辑用户组成员</p></div></div>");

			$(".users_div_container .users_div:last-child").get(0).users_object = temp_group;
			$(".users_div_container .users_div:last-child .startUsersGrant").click(function() {
				(function(btn_jq){
					clickStartUsersGrant(btn_jq);
				})($(this));
			})
			$(".users_div_container .users_div:last-child .startUsersMember").click(function() {
				(function(btn_jq){
					clickStartUsersMember(btn_jq);
				})($(this));
			})

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
					var data = JSON.parse(res).group_list;
					for(var i = 0; i < data.length; i++) {
						(function(temp_group) {
							addUsersGroup(temp_group);
						})(data[i]);
					}
					// edit_grant_array = user_grant_array;
				},
				error: function() {
					console.log("error");
				}
			})
		}

		//获取用户组中的所属用户
		function getGroupUser() {
			console.log(click_users_jq.get(0).users_object);
			console.log(click_users_jq.get(0).users_object.company_id);
			$.ajax({
				type: 'POST',
				url : "http://" + ip2 + "/invoice/getGroupUsers.action",
				data : {
					group_id: click_users_jq.get(0).users_object.group_id, 
					company_id: click_users_jq.get(0).users_object.company_id
				},
				success : function(res, status) {
					// console.log(click_users_jq.get(0).users_object);
					// console.log(res);
					var data = JSON.parse(res).user_list;
					for(var i = 0; i < data.length; i++) {
						// console.log(data[i]);
						addUserToGroup(data[i]);
					}
					// edit_grant_array = user_grant_array;
				},
				error: function() {
					console.log("error");
				}
			})
		}

		//添加用户到用户组视图中
		function addUserToGroup(user_object) {
			$(".users_member_container").append("<div class=\"user_div users_member_div\"><img src=\"pic/头像.png\" style=\"width: 40px; margin-right: 10px;\"><div class=\"user_desc\"><p>" + user_object.user_name + "</p><p>" + user_object.company_name + "</p></div><i class=\"fa fa-times-circle\" aria-hidden=\"true\"></i></div>");

			$(".users_member_container .users_member_div:last-child").get(0).user_object = user_object;
			$(".users_member_container .users_member_div:last-child i").unbind("click").click(function() {
				deleteUserBtn($(this).parent());
			})
		}

		//更新user_modal权限表格的汇总权限
		function flushGatherGrant(table_row_jq) {
			var r = 0, rw = 0;
			table_row_jq.children("[data-writeshow='true']").each(function() {
				if($(this).attr("rw_type") == "r" && $(this).children().hasClass("fa-check-square-o")) {
					r++;
				}
				else if($(this).attr("rw_type") == "rw" && $(this).children().hasClass("fa-check-square-o")){
					rw++;
				}
			})

			if(r > 0 && table_row_jq.children(".gather_read").children().hasClass("fa-square-o")) {
				table_row_jq.children(".gather_read").children().removeClass("fa-square-o");
				table_row_jq.children(".gather_read").children().addClass("fa-check-square-o");
			}
			else if(rw > 0 && table_row_jq.children(".gather_write").children().hasClass("fa-square-o")) {
				table_row_jq.children(".gather_write").children().removeClass("fa-square-o");
				table_row_jq.children(".gather_write").children().addClass("fa-check-square-o");
			}
			else if(r == 0 && table_row_jq.children(".gather_read").children().hasClass("fa-check-square-o")) {
				table_row_jq.children(".gather_read").children().removeClass("fa-check-square-o");
				table_row_jq.children(".gather_read").children().addClass("fa-square-o");
			}
			else if(rw == 0 && table_row_jq.children(".gather_write").children().hasClass("fa-check-square-o")) {
				table_row_jq.children(".gather_write").children().removeClass("fa-check-square-o");
				table_row_jq.children(".gather_write").children().addClass("fa-square-o");
			}
		}

		//根据permission_list来绘制user_modal表格
		function flushUserGrantTable(permissions) {
			$(".user_grant_table .table_display_row").each(function() {
				for(var i = 0; i < permissions.length; i++) {
					var permission_name = permissions[i].permission_name;
					if($(this).attr("grant_type") == permission_name.split("-")[0]) {
						if(permissions[i].isPrivate == 1) {
							$(this).children(".table_display_td[rw_type='" + permission_name.split("-")[1] + "'][data-write='true']").children().removeClass("fa-square-o");
							$(this).children(".table_display_td[rw_type='" + permission_name.split("-")[1] + "'][data-write='true']").children().addClass("fa-check-square-o");
							$(this).children(".users_group").text("无");
						}
						else {
							console.log(permissions[i].origin_groups);
							$(this).children(".table_display_td[rw_type='" + permission_name.split("-")[1] + "']").not("[data-write='true']").children().removeClass("fa-square-o");
							$(this).children(".table_display_td[rw_type='" + permission_name.split("-")[1] + "']").not("[data-write='true']").children().addClass("fa-check-square-o");	
							$(this).children(".users_group").text(permissions[i].origin_groups.join("、"));
						}
						
						flushGatherGrant($(this));
					}
				}
			})
		}

		//根据users_permission_list来绘制Users_grant_modal的表格
		function flushUsersGrantTable(permissions) { 
			// console.log($(".users_grant_table .table_display_row").eq(0).attr("grant_type"));
			console.log(permissions);
			$(".users_grant_table .table_display_row").each(function() {
				for(var i = 0; i < permissions.length; i++) {
					var permission_name = permissions[i].permission_name;
					// console.log(permission_name + " " + $(this).attr("grant_type"));
					if($(this).attr("grant_type") == permission_name.split("-")[0]) {
						$(this).children(".table_display_td[rw_type='" + permission_name.split("-")[1] + "']").children().removeClass("fa-square-o");
						$(this).children(".table_display_td[rw_type='" + permission_name.split("-")[1] + "']").children().addClass("fa-check-square-o");
					}
				}	
			})
		}

		//点击成员头像
		function clickUser(user_jq) {
			click_user_jq = user_jq;
			// console.log(user_jq.get(0).user_object.user_id);
			$.ajax({
				type: 'POST',
				url : "http://"+ip2+"/invoice/getUserPermission.action",
				data: {
					user_id : user_jq.get(0).user_object.user_id
				},
				success : function(res, status) {
					var data = JSON.parse(res);
					user_jq.get(0).user_object.pemissions = data.permission_list;
					$("#userGrantModal").modal('show');
					// $("#userGrantModal").css("display", "table");
					// ModalVerticalAlign($("#userGrantModal").get(0));

					flushUserGrantTable(user_jq.get(0).user_object.permissions);
				}
			})
		}

		//悬浮用户组头像
		function hoverUsers(users_jq) {
			users_jq.mouseenter(function() {
				$(this).children(".modal_menu").css("opacity", "1");
			})
			users_jq.mouseleave(function() {
				$(this).children(".modal_menu").css("opacity", "0");
			})
		}

		//点击用户组编辑用户组权限按钮
		function clickStartUsersGrant(btn_jq) {
			click_users_jq = btn_jq.parent().parent();
			var users_object = btn_jq.parent().parent().get(0).users_object;

			$("#usersGrantModal").modal('show');
			// $("#usersGrantModal").css("display", "table");
			// ModalVerticalAlign($("#usersGrantModal").get(0));
			//获取权限填写表格
			flushUsersGrantTable(users_object.permissions);
		}

		//点击用户组编辑成员按钮
		function clickStartUsersMember(btn_jq) {
			click_users_jq = btn_jq.parent().parent();
			$("#usersMemberModal").modal('show');
			// $("#usersMemberModal").css("display", "table");
			// ModalVerticalAlign($("#usersMemberModal").get(0));	
		}

		//初始化user_grant_modal模态框
		function initUserGrantModal() {
			$("#userGrantModal").on("show.bs.modal", function() {
				$(".edit_grant").css("display", "inline-block");
				$(".save_grant").css("display", "none");
				$(".cancel_edit").css("display", "none");	
				$("[data-writeshow='true']").css("display", "none");

				$(".user_grant_table .table_display_td i").each(function() {
					if($(this).hasClass("fa-check-square-o")) {
						$(this).removeClass("fa-check-square-o");
						$(this).addClass("fa-square-o");
					}
				})

				$(".user_grant_table .table_display_td[data-write='true'] i").css("cursor", "default");
				$(".user_grant_table .table_display_td[data-write='true'] i").each(function() {
					$(this).unbind("click");
				})

				$(".user_grant_table .users_group").each(function() {
					$(this).text("无");
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

				$(".users_grant_table .table_display_td i").css("cursor", "default");
				$(".users_grant_table .table_display_td i").each(function() {
					$(this).unbind("click");
					if($(this).hasClass("fa-check-square-o")) {
						$(this).removeClass("fa-check-square-o");
						$(this).addClass("fa-square-o");
					}
				})

				users_send_array.splice(0, users_send_array.length);
				beginEdit(1);
			})

			$("#usersGrantModal").on("hide.bs.modal", function() {
				$(".users_grant_table .table_display_td i").removeClass("fa-check-square-o");
				$(".users_grant_table .table_display_td i").addClass("fa-square-o");
			})
		}

		//初始化users_member_modal模态框
		function initUsersMemberModal() {
			$("#usersMemberModal").on("show.bs.modal", function() {
				$("#start_edit").attr("disabled", false);
				$("#addUserId").attr("disabled", true);
				$("#addUserId").val("");
				$("#addUserBtn").attr("disabled", true);
				
				// $(".deleteAlert").css("display", "none");
				clickStartEdit();
				clickAddUserBtn();

				//获取当前用户组的用户
				$(".users_member_container").html("");
				getGroupUser();
			})

			$("#usersMemberModal").on("hide.bs.modal",function() {
				$(".deleteAlert").css("opacity", "0");
			})
		}

		//初始化progressModal进度条模态框
		function initProgressModal() {
			$("#progressModal").on("show.bs.modal", function() {
				$("#progressModal h4").text("正在修改权限...");
				$("#progressModal .progress-bar").get(0).style.width = "40%";
			})
		}

		//初始化confirmModal模态框
		function initConfirmModal() {
			$("#confirm_delete").unbind("show.bs.modal");
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

		//点击users_member模态框中的添加用户按钮
		function clickAddUserBtn() {
			$("#addUserBtn").unbind("click").click(function() {
				$("#myModalLabel_progress").css("display", "inline-block");
				$("#myModalLabel_progress .progress-bar").css("width", "40%");
				$.ajax({
					type: 'POST',
					url: "http://"+ip2+"/invoice/addGroupUser.action",
					data : {
						group_id: click_users_jq.get(0).users_object.group_id,
						user_id : $("#addUserId").val()
					},
					success: function(res, status) {
						// console.log(res);
						if(JSON.parse(res).err == undefined) {
							$("#myModalLabel_progress .progress-bar").css("width", "100%");
							var user = JSON.parse(res).user;
							addUserToGroup(user);	
							$("#addUserId").val("");
							$(".users_member_container .users_member_div:last-child i").css("opacity", "1");
							setTimeout(function(){
								$("#myModalLabel_progress").css("display", "none");
								$("#myModalLabel_progress .progress-bar").css("width", "20%");
							}, 1000);
						}
						else {
							console.log("error");
							$("#myModalLabel_progress .progress-bar").addClass("progress-bar-danger");
							setTimeout(function(){
								$("#myModalLabel_progress").css("display", "none");
								$("#myModalLabel_progress .progress-bar").css("width", "20%");
								$("#myModalLabel_progress .progress-bar").removeClass("progress-bar-danger");
							}, 2000);
						}
					},
					error : function(err) {
						console.log("error");
						$("#myModalLabel_progress .progress-bar").addClass("progress-bar-danger");
						setTimeout(function(){
							$("#myModalLabel_progress").css("display", "none");
							$("#myModalLabel_progress .progress-bar").css("width", "20%");
							$("#myModalLabel_progress .progress-bar").removeClass("progress-bar-danger");
						}, 2000);
					}
				})	
			})
		}

		//点击左上角交叉删除用户
		function deleteUserBtn(user_jq) {
			// $(".deleteAlert").css("display", "block");
			$(".deleteAlert").css("opacity", 1);
			// console.log("confirm_here");
			clickConfirmDelete(user_jq);	
			clickDeleteCancel();
		}

		//点击确认删除
		function clickConfirmDelete(user_jq) {
			// $(".deleteAlert").css("display", "none");
			$("#confirm_delete").unbind("click").click(function() {
				$(".deleteAlert").css("opacity", 0);
				$("#myModalLabel_progress").css("display", "inline-block");
				$("#myModalLabel_progress .progress-bar").css("width", "40%");
				$.ajax({
					type: 'POST',
					url: "http://"+ip2+"/invoice/removeGroupUser.action",
					data : {
						user_id : user_jq.get(0).user_object.user_id,
						group_id : click_users_jq.get(0).users_object.group_id
					},
					success: function(res, status) {
						if(JSON.parse(res).success != undefined) {
							$("#myModalLabel_progress .progress-bar").css("width", "100%");
							user_jq.remove();	
							setTimeout(function(){
								$("#myModalLabel_progress").css("display", "none");
								$("#myModalLabel_progress .progress-bar").css("width", "20%");
							}, 1000);
						}
					},
					error : function(err) {
						console.log("error");
					}
				})
			})
		}

		//点击取消删除
		function clickDeleteCancel() {
			$(".confirm_cancel").unbind("click").click(function() {
				$(".deleteAlert").css("opacity", 0);
				// $(".deleteAlert").css("display", "none");
			})
		}

		//判断user和group的权限
		function justifyUserGroup(user_json) {
			var hasUser = false, hasGroup = false;
			for(var i = 0; i < user_json.permissions.length; i++) {
				// console.log(user_json.permissions[i].permission_name);
				if(user_json.permissions[i].permission_name.split("-")[0] == "user") {
					hasUser = true;
				}
				else if(user_json.permissions[i].permission_name.split("-")[0] == "group") {
					hasGroup = true;
				}
			}
			if(!hasUser) {
				$(".users_div_container").parent().css("display", "none");
			}
			if(!hasGroup) {
				$(".user_div_container").parent().css("display", "none");
			}
		}

        $(document).ready(function(){
        	// console.log(document.documentElement.clientHeight);
        	// console.log($("#userGrantModal").get(0).offsetHeight);
        	// 判断权限
        	justifyUserGrant(user_json);
        	justifyRW(user_json);
        	justifyUserGroup(user_json);

        	initUserGrantModal();
        	initUsersGrantModal();
        	initUsersMemberModal()
        	initProgressModal();
        	initConfirmModal();

        	getUserGrant();
        	getUsersGrant();

        	clickUsersGrantSave();
        	clickUserGrantSave();
        })
	</script>
</body>
</html>