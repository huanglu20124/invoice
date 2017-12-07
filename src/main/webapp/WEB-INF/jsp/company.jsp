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

			<div class="panel_hd_line flex flex-align-end">
				<span class="flex-1" style="font-size: 16px;">共<span id="muban_num">0</span>个单位</span>
				<span class="flex-none" style="font-size: 14px; margin-left: 2em;">
	        		查看方式:<select id="show_type" class="form-control" style="display: inline-block; width: 8em; height: 25px; margin-left: 0.5em; padding: 0em 0.5em; font-size: 13.5px;" onchange="changeShowType()">
	        			<option selected>缩略图</option>
	        			<option>列表</option>
	        			<option>详细信息</option>
	        		</select>
	        	</span>
	        	<span class="flex-none" style="margin-left: 2em; color: #a0a5b1;"><input type="text" id="search_input" class="form-control" size="30" style="height: 25px; vertical-align: middle; display: inline-block; width: auto;" placeholder="--请输入要搜索的模板id--"><img src="pic/search.png" style="display: inline-block; height: 20px; width: auto; vertical-align: middle; margin-left: 5px; cursor: pointer;" onclick="searchproduct()" /></span>
			</div>
			<div class="panel panel-default panel-box-shadow">
			    <div class="panel-body muban_contain">
			    	<div class="thumbnail_muban" style="padding-top: 15px;">
				  
					</div>
					<div class="list_muban" style="display: none;">
						
					</div>
					<div class="detail_muban" style="display: none;">
						<table class="table table-hover table-striped muban_table">
						  <thead>
						    <tr>
						      <th><span>名称</span></th>
						      <th><span class="sorted_down">修改日期</span></th>
						      <th><span>文件大小</span></th>
						      <th><span>类型</span></th>
						    </tr>
						  </thead>
						  <tbody style="font-size: 13px; line-height: 1.5em;">
							    <!-- <tr><td>Eric</td><td>2017.8.17</td><td>180KB</td><td>jpg</td></tr>
							    <tr><td>Tywen</td><td>2016.9.17</td><td>280KB</td><td>jpg</td></tr>
							    <tr><td>Ponyo</td><td>2017.9.17</td><td>140KB</td><td>png</td></tr> -->
							    
						  </tbody>
						</table>
					</div>
			    </div>
			</div>
		</div>
	</main>
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style=" padding-left: 0px; margin: 0px auto;">
	    <div>
	        <div class="modal-content">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_modal">&times;</button>
	                <h4 class="modal-title" id="myModalLabel" style="display: inline-block; vertical-align: middle; width: auto; margin-right: 10px;">单位管理</h4>
	                <div class="progress progress-striped active"  style="display: none; vertical-align: middle; width: 30%; margin-bottom: 0px;" id="myModalLabel_progress">
					    <div class="progress-bar" role="progressbar" aria-valuenow="60" 
					        aria-valuemin="0" aria-valuemax="100" style="width: 20%;">
					    </div>
					</div>
	            </div>
	            <div class="modal-body" style="padding: 0px;">
	            	<div id="canvas_container" style="display: inline-block; vertical-align: top;">
						<canvas id="myCanvas"></canvas>
					</div>
					<div style="width: 200px; display: inline-block; vertical-align: top; padding: 20px 0px 0px 20px;">
						<button type="button" class="btn btn-primary" style="width: 100%;" id="getEdit" data-write="true">启用编辑</button>
						<form role="form" id="global_setting" style="margin-top: 20px;">
						  <div class="form-group">
						    <label for="biaoqian" class="control-label">发票类型</label>
					    	<input type="text" class="form-control" placeholder="请输入发票类型" id="biaoqian" name="biaoqian" />
						  </div>
						  <div class="form-group">
						    <label for="dinge" class="control-label">定额发票</label>
						    <div class="checkbox">
							    <label>
							      <input type="checkbox" id="dinge_checkbox">若发票为定额发票请打勾
							    </label>
						  	</div>
					    	<input type="text" class="form-control" placeholder="请输入定额发票数值" id="dinge" name="dinge" disabled/>
						  </div>
						  <button type="button" class="btn btn-danger" data-dismiss="modal" id="delete_mb" style="width: 100%;">删除模板</button>
	               		  <button type="submit" class="btn btn-primary" data-dismiss="modal" id="submit_modal" style="width: 100%; margin-top: 10px;">添加/修改模板</button>
						</form>
					</div>
					<div class="hid_panel">
						<span id="close"><img src="pic/close_disabled.png"/></span>
						<span id="certain"><img src="pic/tick_disabled.png"/></span>
						<span id="setting"><img src="pic/setting.png" /></span>
					</div>
					<div class="setting_panel form">
						<form id="setting_form" role="form">
							<div class="form-group">
								<label for="quyu" class="control-label">区域标签:</label>
								<select class="form-control" name="quyu" onchange="form_change()">
									<option selected="selected" value="money">金额（小写）</option>
									<option value="id_card">金额（大写）</option>
									<option value="head">抬头</option>
									<option value="date">日期</option>
									<option value="detail">详细信息</option>
									<option value="invoice_id">发票号码</option>
								</select>
							</div>
							<div class="form-group">
								<label for="font_color" class="control-label">字体颜色:</label>
								<select class="form-control" name="font_color" onchange="form_change()">
									<option selected="selected" value="black">黑色</option>
									<option value="red">红色</option>
									<option value="blue">蓝色</option>
								</select>
							</div>
							<div class="form-group">
								<label for="bg_color" class="control-label">背景颜色:</label>
								<select class="form-control" name="bg_color" onchange="form_change()">
									<option selected="selected" value="white">白色</option>
									<option value="green">绿色</option>
									<option value="red">红色</option>
									<option value="blue">蓝色</option>
								</select>
							</div>
							<div class="form-group">
								<label for="keyword" class="control-label">关键字:</label>
								<input type="text" name="keyword" class="form-control" onchange="form_change()"/>
							</div>
							<label for="name">干扰</label>
							<div>
							    <label class="checkbox-inline">
							        <input type="checkbox" name="ganrao" value="直线干扰">直线干扰
							    </label>
							    <label class="checkbox-inline">
							        <input type="checkbox" name="ganrao" value="印章干扰">印章干扰
							    </label>
							</div>
						</form>
					</div>
	            </div>
	        </div><!-- /.modal-content -->
	    </div><!-- /.modal -->
	</div>
	<div class="modal fade" id="progressModal" tabindex="-1" aria-hidden="true" style="margin: 0px auto; margin-top: 200px; width: 33%;">
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

        //声明canvas
        var c=document.getElementById("myCanvas");
		var canvas_width = invoice_width, canvas_height = invoice_height;
		var cxt=c.getContext("2d");
		cxt.strokeStyle = "#00ff36";
		var paint_area=[]; //画图的区域
		var final_paint_area = []; //备份记录最终画图区域
		var temp_img_str; //保存当前返回的img_str
		var addImage_filename; //记录服务器生成的新添图片的名字
		var index = -1, last_index= -1, cur_x, cur_y; //记录当前鼠标滑到的画图区域index及其坐标


		//储存服务器传过来的对象
		var model_array = [];

        function flushForm() { //激活所有表单，开启修改功能
        	$("#global_setting *").not("#dinge").each(function(){
        		$(this).get(0).disabled = false;
        	})
        	$("#setting_form *").each(function(){
        		$(this).get(0).disabled = false;
        	})
        	if($("#global_setting #dinge_checkbox").get(0).checked == true) {
        		$("#dinge").get(0).disabled = true;
        	}
        	$("#myCanvas").css("cursor", "crosshair");
        	up_done = true;
        	button_use = true;
        	$("#close img").get(0).src = "pic/close.png";
        	$("#certain img").get(0).src = "pic/tick.png";

        	$("#getEdit").get(0).disabled = true;

        	$("#close").removeClass('disabled_button');
        	$("#certain").removeClass('disabled_button');
        }

        //点击搜索按钮
        function searchproduct() {
			var search_value = $("#search_input").val();
			//alert(search_value);
			$.ajax({
				type: 'POST',
				url: search_value == "" ? "http://"+ip2+"/invoice/getAllModel.action" : "http://"+ip2+"/invoice/searchModelLabel.action",
				data: search_value == "" ? {
					user_id: user_id,
					page : 0 //首次查询，第一页page为0
				} : {
					page: 0, //首次首页查询
					keyword: search_value
				},
				success: function(res) {
					tellConsole(res,2);
					var res1 = JSON.parse(res);
					
					//添加模板img元素
					clearModelArray();
					for(var i = 0; i < res1.model_list.length; i++) {
						//alert(res1.model_list[i].json_model);
						addImgMuban(res1.model_list[i].model_url, res1.model_list[i].json_model, res1.model_list[i].model_id, res1.model_list[i].model_register_time, res1.model_list[i].image_size, res1.model_list[i].model_label);
					}
					
					var muban_num = res1.model_list.length;
					$("#muban_num").text(muban_num.toString());
				},
				error: function(err) {

				}
			})
			tellConsole(search_value,3);
		}

		//提交新增图片按钮 
		function addImageSubmit() {
			$("#myCanvas").css("backgroundImage","url(\'\')");
			$("#myModalLabel").text("正在上传图片...");
			$("#myModalLabel_progress ").css("display", "inline-block");
			$('#myModal').modal('show');
			$("#myModalLabel_progress .progress-bar").css("width", "40%");
			flushForm();
			muban_type = 0;
			return true;
		}

		//点击模板后的动作
		function clickMuban(jq_Muban) {

			tellConsole(cxt, 3);

			initPaintForm(jq_Muban.get(0).src);
			$('#myModal').modal('show');
			temp_click_jq_img = jq_Muban;

			addImage_filename = jq_Muban.get(0).file_name;
			json_model = JSON.parse(jq_Muban.get(0).json_model);
			tellConsole(json_model, 0);
			muban_type = 1;

			//讲json_model的内容push进paint_area并设置global setting
	 		if(json_model.money != undefined) {
	 			paint_area.push(json_model.money);	
	 		}
			if(json_model.head != undefined) {
	 			paint_area.push(json_model.head);	
	 		}
	 		if(json_model.date != undefined) {
	 			paint_area.push(json_model.date);	
	 		}
	 		if(json_model.time != undefined) {
	 			paint_area.push(json_model.time);	
	 		}
	 		if(json_model.id_card != undefined) {
	 			paint_area.push(json_model.id_card);	
	 		}
	 		if(json_model.detail != undefined) {
	 			paint_area.push(json_model.detail);	
	 		}
	 		if(json_model.invoice_id != undefined) {
	 			paint_area.push(json_model.invoice_id);	
	 		}
	 		if(json_model.global_setting != undefined) {
	 			$("#global_setting input[name='biaoqian']").val(json_model.global_setting.label);
	 			$("#global_setting #dinge_checkbox").get(0).checked = json_model.global_setting.quota == undefined ? false : true;
	 			$("#global_setting input[name='dinge']").val(json_model.global_setting.quota == undefined ? "" : json_model.global_setting.quota);
	 		}

	 		tellConsole(paint_area, 3);

			//点击某张图片后发送获取img_str的请求
			$.ajax({
				url: "http://" + ip2 + "/invoice/getImgStr.action",
				type: "POST",
				cache: false,
				data:{
					url: addImage_filename
				},
				success: function(res) {
					res1 = JSON.parse(res);
					temp_img_str = res1.img_str;
					// alert(res1.img_str);
					$("#myCanvas").get(0).style.backgroundImage = "url(" + res1.img_str.toString() + ")";
					getPaint(paint_area, cxt);
				},
				error: function(e) {
					tellConsole(e, 1);
				}
			})
		}

		//添加模板图片至模板库
		function addImgMuban(url, json_model, id, model_register_time, image_size, model_label) { 

			console.log(url);
			//放入model_array
			var model_object = {
				model_url : url,
				file_name : url,
				json_model : json_model,
				model_id : id,
				model_register_time : model_register_time,
				image_size : image_size,
				model_label : model_label
			};
			model_array.push(model_object);

			//缩略图视角
			addToThumbnail(model_object);

			//列表视角
			addToList(model_object);

			//详细信息视角
			addToDetail(model_object);

			//判断显示哪种视图
			whichToShow();

		}

		//将model_array中的一项放入thumbnail视图
		function addToThumbnail(model_array_object) {
			$(".thumbnail_muban").append("<div><img /><p>1</p></div>");
			$(".thumbnail_muban div:last-child").addClass("ku_img_container");	
			$(".thumbnail_muban div:last-child p").addClass("ku_img_id");
			$(".thumbnail_muban div:last-child p").text(model_array_object.model_label);
			$(".thumbnail_muban div:last-child img").get(0).src = model_array_object.model_url;
			$(".thumbnail_muban div:last-child img").get(0).model_url = model_array_object.model_url;
			$(".thumbnail_muban div:last-child img").get(0).file_name = model_array_object.file_name;
			$(".thumbnail_muban div:last-child img").addClass("ku_img");	
			$(".thumbnail_muban div:last-child img").get(0).style.height = parseFloat($(".muban_contain div:last-child img").width() * parseFloat(invoice_height / invoice_width)) + "px";
			$(".thumbnail_muban div:last-child img").get(0).json_model = model_array_object.json_model;
			$(".thumbnail_muban div:last-child img").get(0).model_id = model_array_object.model_id;
			$(".thumbnail_muban div:last-child img").unbind("click").click(function() {
				clickMuban($(this));
			})
		}

		//将model_array中的一项放入list视图
		function addToList(model_array_object) {
			$(".list_muban").append("<div class=\"list_muban_contain\"><span class=\"fa fa-image\"></span><span class=\"ku_img_id\"></span></div>")
			$(".list_muban .list_muban_contain:last-child .ku_img_id").text(model_array_object.model_label);
			$(".list_muban .list_muban_contain:last-child .fa-image").get(0).json_model = model_array_object.json_model;
			$(".list_muban .list_muban_contain:last-child .fa-image").get(0).model_id = model_array_object.model_id;
			$(".list_muban .list_muban_contain:last-child .fa-image").get(0).model_url = model_array_object.model_url;
			$(".list_muban .list_muban_contain:last-child .fa-image").get(0).file_name = model_array_object.file_name;
			$(".list_muban .list_muban_contain:last-child .fa-image").unbind("click").click(function() {
				clickMuban($(this));
				
			})
		}

		//将model_array中的一项放入detail视图
		function addToDetail(model_array_object) {
			$(".muban_table tbody").append("<tr><td>" + model_array_object.model_label + "</td><td>" + model_array_object.model_register_time + "</td><td>" + model_array_object.image_size + "KB</td><td>" + model_array_object.model_url.split(".")[model_array_object.model_url.split(".").length-1] + "</td></tr>");
			$(".muban_table tbody tr:last-child").get(0).json_model = model_array_object.json_model;
			$(".muban_table tbody tr:last-child").get(0).model_id = model_array_object.model_id;
			$(".muban_table tbody tr:last-child").get(0).model_url = model_array_object.model_url;
			$(".muban_table tbody tr:last-child").get(0).file_name = model_array_object.file_name;
			$(".muban_table tbody tr:last-child").unbind("click").click(function() {
				clickMuban($(this));
			})
		}

		//修改模板庫中的模板信息
		function ChangeInfo(id, attr_name, value) {
			for(var i = 0; i < model_array.length; i++) {
				//console.log(id+" "+attr_name+" "+value+" "+model_array.length+" "+model_array[i].model_id);
				if(model_array[i].model_id == id) {
					model_array[i][attr_name] = value;
					flushView(id, attr_name, value);
					break;
				}
			}
		}

		//更新視圖中模板的信息
		function flushView(id, attr_name, value) {
			// 縮略圖
			console.log(value);
			$(".thumbnail_muban div img").each(function() {
				if($(this).get(0).model_id == id) {
					$(this).prop(attr_name, value);
					if(attr_name == "model_url") { //缩略图的src相应改变显示图片效果
						$(this).get(0).src = value;
					}
					else if(attr_name == "json_model") {
						$(this).prop(attr_name, JSON.stringify(value));
						//console.log($(this).get(0).json_model);
					}
				}
			})

			//List視圖
			$(".list_muban .list_muban_contain .fa-image").each(function() {
				if($(this).get(0).model_id == id) {
					if(attr_name == "json_model") {
						$(this).prop(attr_name, JSON.stringify(value));
					}
					else
						$(this).prop(attr_name, value);
				}
			})

			//Detail視圖
			$(".muban_table tbody tr").each(function(){
				if($(this).get(0).model_id == id) {
					if(attr_name == "json_model") {
						$(this).prop(attr_name, JSON.stringify(value));
					}
					else
						$(this).prop(attr_name, value);
				}
			})
		}

		//清空model_array以及全部视图
		function clearModelArray() {
			model_array.splice(0, model_array.length);
			$(".thumbnail_muban").html("");
			$(".list_muban .list_muban_contain").html("");
			$(".muban_table tbody ").html("");
		}

		//刪除model_array中的一項并更新視圖
		function deleteObject(id) {
			for(var i = 0; i < model_array.length; i++) {
				if(model_array[i].model_id == id) {
					model_array.splice(i, 1);
				}
				break;
			}

			// 縮略圖
			$(".thumbnail_muban div img").each(function() {
				if($(this).get(0).model_id == id) {
					$(this).parent().remove();
				}
			})

			//List視圖
			$(".list_muban .list_muban_contain .fa-image").each(function() {
				if($(this).get(0).model_id == id) {
					$(this).parent().remove();
				}
			})

			//Detail視圖
			$(".muban_table tbody tr").each(function(){
				if($(this).get(0).model_id == id) {
					$(this).remove();
				}
			})
		}

		//判断显示哪个视图
		function whichToShow() {
			if($("#show_type").val() == "缩略图") {
				$(".thumbnail_muban").css("display", "block");
				$(".list_muban").css("display", "none");
				$(".detail_muban").css("display", "none");
			}
			else if($("#show_type").val() == "列表") {
				$(".list_muban").css("display", "block");
				$(".thumbnail_muban").css("display", "none");
				$(".detail_muban").css("display", "none");
			}
			else if($("#show_type").val() == "详细信息") {
				$(".detail_muban").css("display", "block");
				$(".list_muban").css("display", "none");
				$(".thumbnail_muban").css("display", "none");
			}
		}

		//切换查看视图模式
		function changeShowType() {
			whichToShow();
			return true;
		}

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

        	
		})
	</script>
</body>
</html>