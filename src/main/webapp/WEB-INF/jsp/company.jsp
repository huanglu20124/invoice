<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.alibaba.fastjson.JSON"%>
<!DOCTYPE html>
<html>
<head>
	<title>中山大学发票识别监控系统</title>
	<meta charset="utf-8">
	<META HTTP-EQUIV="pragma" CONTENT="no-cache"> 
	<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate"> 
	<META HTTP-EQUIV="expires" CONTENT="0">
	<script src="script/jquery-3.2.1.min.js"></script>
	<script type="text/javascript" src="script/jquery.form.js"></script>
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
				<span class="flex-1">单位管理</span>
			</div>

			<!-- <div class="panel_hd_line flex flex-align-end">
				<span class="flex-1" style="font-size: 16px;">共<span id="company_num">1</span>个单位</span>
				<span class="flex-none text-primary" style="margin-right: 1em; display: inline-block; cursor: pointer;" title="增加单位"><i class="fa fa-plus" aria-hidden="true"></i></span>
				<span class="flex-none text-danger" title="删除单位" style="cursor: pointer;"><i class="fa fa-minus" aria-hidden="true"></i></span>
			</div>
			<div class="panel panel-default panel-box-shadow">
			    <div class="panel-body company_container">
			    	<div class="company_div">
			    		<img src="pic/logo.png" style="width: 100%;">
			    		<div class="company_desc">
			    			<p>中山大学</p>
			    		</div>
			    		<i class="fa fa-times-circle" aria-hidden="true"></i>
			    	</div>
			    </div>
			</div> -->

			<div class="panel panel-default panel-box-shadow detect_div_container">
			    <!-- <div class="form-group flex flex-align-center">
			    	<label for="company_name" class="flex-none">单位名称</label>
			    	<input class="form-control flex-1" type="text" id="company_name" name="company_name" />
			    </div>
			    <div class="form-group flex flex-align-center">
			    	<label for="company_id" class="flex-none">单位id</label>
			    	<input class="form-control flex-1" type="text" id="company_id" name="company_id" />
			    </div>
			    <div class="form-group flex flex-align-center">
			    	<label for="company_manager" class="flex-none">单位责任人名称</label>
			    	<input class="form-control flex-1" type="text" id="company_manager" name="company_manager" />
			    </div>
			    <div class="form-group flex flex-align-center">
			    	<label for="company_manager_id" class="flex-none">单位责任人id</label>
			    	<input class="form-control flex-1" type="text" id="company_manager_id" name="company_manager_id" />
			    </div>
			    <div class="form-group flex flex-align-center">
			    	<label for="company_create_time" class="flex-none">单位注册时间</label>
			    	<input class="form-control flex-1" type="text" id="company_create_time" name="company_create_time" />
			    </div> -->
			    <div class="detect_div" style="padding: 30px 30px;">
					<p class="detect_div_hd">单位信息</p>
					<div class="table_display_container companyedit_table">
						<div class="table_display_row">
							<span class="table_display_th">基本信息</span>
							<span class="table_display_th"></span>
							<span class="table_display_th text_right">操作</span>
						</div>
						<div class="table_display_row">
							<span class="table_display_td">单位名称</span>
							<span class="table_display_td company_name">中山大学</span>
							<span class="table_display_td text_right edit" data-write="true"><a>修改</a></span>
						</div>
						<div class="table_display_row">
							<span class="table_display_td">单位id</span>
							<span class="table_display_td user_id">dw3e4e1ddf8d7f6773</span>
							<span class="table_display_td text_right edit"></span>
						</div>
						<div class="table_display_row">
							<span class="table_display_td">单位负责人</span>
							<span class="table_display_td company_manager">张三</span>
							<span class="table_display_td text_right edit"><a>修改</a></span>
						</div>
						<div class="table_display_row">
							<span class="table_display_td">单位注册时间</span>
							<span class="table_display_td register_time">2017-11-11</span>
							<span class="table_display_td text_right edit"><a>修改</a></span>
						</div>
						<div class="table_display_row">
							<span class="table_display_td">单位logo</span>
							<span class="table_display_td comapny_logo">
								<img src="pic/logo.png" style="height: 50px; width: auto;">
							</span>
							<span class="table_display_td text_right edit"><a>修改</a></span>
						</div>
					</div>
			    </div>
			</div>
		</div>
	</main>
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="companyModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="margin: 0px auto; width: 1000px;">
	    <div>
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_modal">&times;</button>
	                <h4 class="modal-title" id="myModalLabel" style="display: inline-block; vertical-align: middle; width: auto; margin-right: 10px;">编辑单位</h4>
	                <div class="progress progress-striped active"  style="display: none; vertical-align: middle; width: 30%; margin-bottom: 0px;" id="myModalLabel_progress">
					    <div class="progress-bar" role="progressbar" aria-valuenow="60" 
					        aria-valuemin="0" aria-valuemax="100" style="width: 20%;">
					    </div>
					</div>
	            </div>
	            <div class="modal-body" style="padding: 10px 20px;">
					<form>
						<div class="form-group">
							<label for="company_name">单位名称</label>
							<input type="text" name="company_name" value="中山大学" class="form-control" disabled />
						</div>
						<div class="form-group">
							<label for="company_id">单位id</label>
							<input type="text" name="company_id" value="wxsadsa123dsfa" class="form-control" disabled/>
						</div>
						<div class="form-group">
							<label for="company_manager">单位负责人</label>
							<input type="text" name="company_manager" value="张三" class="form-control" disabled/>
						</div>
						<div class="form-group">
							<label for="company_manager_id">负责人id</label>
							<input type="text" name="company_manager_id" value="dwfdsada14" class="form-control" disabled />
						</div>
					</form>
					<div>
						<p class="company_member" style="font-size: 16px;">单位成员</p>
						<div class="user_div_container">
					    	<div class="user_div">
					    		<img src="pic/头像.png" style="width: 40px; margin-right: 10px;">
					    		<div class="user_desc">
					    			<p>张三</p>
					    			<p>wxqs123as8</p>
					    		</div>
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
	<div class="modal fade" id="progressModal" tabindex="-1" aria-hidden="true" style="margin: 0px auto; width: 33%;">
		<div>
	        <div class="modal-content">
	        	<div class="modal-header">
	        		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
	        		<h4 class="modal-title">正在添加/修改...</h4>
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
	<script>
        // var ws = null;
        //jsp加入
        var user_json = <%=JSON.toJSONString(request.getAttribute("user"))%>

        var temp_click_jq_img; //记录当前被点击的图片或列表项
        var temp_json_model; //记录当前增加/修改上传的json_model
        var edited_canvas_url; //修改后的canvas_url

		//点击详细列表表头排序
		function clickToSort(type) {
			if(type == "名称") {
				model_array.sort(function(a, b) {
					return b.model_label.localeCompare(a.model_label);
				})
			}
			else if(type == "修改日期") {
				model_array.sort(function(a, b) {
					var a1 = new Date(a.model_register_time);
					var b1 = new Date(b.model_register_time);
					return b1.getTime() - a1.getTime();
				})
			}
			else if(type == "文件大小") {
				model_array.sort(function(a, b) {
					return parseInt(b.image_size) - parseInt(a.image_size);
				})
			}
			else if(type == "类型") {
				model_array.sort(function(a, b) {
					return b.model_url.split('.')[b.model_url.split('.').length-1].localeCompare(a.model_url.split('.')[a.model_url.split('.').length-1]);
				})
			}
		}

		$(document).ready(function() {
			// loadxml("config.xml");
			// connectEndpoint();
			// WebsocketJustify();
			// 判断权限
        	justifyUserGrant(user_json);
        	justifyRW(user_json);

        	// 绑定添加
        	$(".company_div").each(function() {
        		$(this).click(function() {
        			$("#companyModal").modal('show');
        		})
        	})
		})
	</script>
</body>
</html>