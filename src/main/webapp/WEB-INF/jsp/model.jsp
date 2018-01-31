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
				<span class="flex-1">模板库</span>
			</div>

			<div class="panel_hd_line flex flex-align-end">
				<span class="flex-1" style="font-size: 16px;">共<span id="muban_num">0</span>张模板</span>
				<span class="flex-none" style="font-size: 16px; margin-right: 0.8em;" onclick="zoom(1)">
					<i class="fa fa-search-plus" aria-hidden="true"></i>
				</span>
				<span class="flex-none" style="font-size: 16px;" onclick="zoom(0)">
					<i class="fa fa-search-minus" aria-hidden="true"></i>
				</span>
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
			    	<div class="thumbnail_muban_container">
				    	<div class="thumbnail_muban" style="padding-top: 15px;">
					  		<div style="display:none"></div>
						</div>
						<div class="page_foot">
							<span class="left_page"><<</span>
							<span class="page_desc">
								当前页数 <span class="cur_page">1</span>/<span class="all_page">1</span>
							</span>
							<span class="right_page">>></span>
						</div>
					</div>
					<div class="list_muban" style="display: none;">
						<div style="display:none"></div>
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
							    <tr><td>Tywen</td><td>2m 016.9.17</td><td>280KB</td><td>jpg</td></tr>
							    <tr><td>Ponyo</td><td>2017.9.17</td><td>140KB</td><td>png</td></tr> -->
							    
						  </tbody>
						</table>
					</div>
			    </div>
			</div>

			<div class="temp_save_muban muban_contain" style="display: none; margin-top: 20px;">
				<p style="margin-bottom: 1em">模板上传队列</p	>
			</div>

			<form role="form" id="addImageForm" method="post" enctype="multipart/form-data">
				<input type="hidden" name="type" id="upload_type" value="0" />
				<div class="form-group">
					<label for="inputImageFile" class="file_label" data-write="true">选择本地图片新增模板库</label>
					<input type="file" name="file" id="inputImageFile" style="display: none;" onchange="fileHandler(this.files)" />
					<!-- <p class="help-block">点击提交后可在原图上制作模板</p> -->
					<button type="button" class="btn btn-primary" style="margin-left: 20px; vertical-align: middle;" id="addImageForm_btn">上传新增队列</button>
				</div>
				<!-- <button type="submit" class="btn btn-default" onclick="addImageSubmit()" data-write="true">提交图片并制作新的模板</button> -->
				<button type="button" class="btn btn-danger" onclick="deleteAllMuban()">清空模板库</button>
			</form>
		</div>
	</main>
	<!-- 模态框（Modal） -->
	<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style=" padding-left: 0px; margin: 0px auto;">
	    <div>
	        <div class="modal-content" style="overflow: auto; width: 1400px;">
	            <div class="modal-header">
	                <button type="button" class="close" data-dismiss="modal" aria-hidden="true" id="close_modal">&times;</button>
	                <h4 class="modal-title" id="myModalLabel" style="display: inline-block; vertical-align: middle; width: auto; margin-right: 10px;">操作图片</h4>
	                <div class="progress progress-striped active"  style="display: none; vertical-align: middle; width: 30%; margin-bottom: 0px;" id="myModalLabel_progress">
					    <div class="progress-bar" role="progressbar" aria-valuenow="60" 
					        aria-valuemin="0" aria-valuemax="100" style="width: 20%;">
					    </div>
					</div>
	            </div>
	            <div class="modal-body" style="padding: 0px;">
	            	<div id="canvas_container" style="display: inline-block; vertical-align: top; text-align: center;">
						<canvas id="myCanvas"></canvas>
					</div>
					<div style="width: 200px; display: inline-block; vertical-align: top; padding: 20px 0px 0px 20px;" class="right_body">
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
						</form>
						<div class="other_img_name_container" style="width: 100%; padding: 10px 0px; display: none; border-top: 1px solid rgba(200,200,200,0.5);">
							<p style="margin-bottom: 0.5em;">选择的其它原图名称</p>
							<div class="other_img_name"></div>
						</div>
						<form id="addMubanImg_form" style="width: 100%;" method="post" enctype="multipart/form-data" >
							<input type="hidden" name="type" value="1" />
							<input type="hidden" name="file_path" />
							<div class="form-group" style="margin-bottom: 0px;">
							<label for="add_other_img" class="file_label" data-write="true" style="text-align: center; width: 100%; font-weight: normal;">选择同类原图</label>
							<input type="file" name="file" id="add_other_img" style="display: none;" multiple onchange="addHandler(this.files)" />
							</div>
						</form>
						<button type="submit" class="btn btn-primary" data-dismiss="modal" id="submit_modal" style="width: 100%;">添加模板</button>
						<button type="button" class="btn btn-primary" data-dismiss="modal" id="edit_modal" style="width: 100%; margin-top: 10px;">修改模板</button>
						<button type="button" class="btn btn-danger" data-dismiss="modal" id="delete_mb" style="width: 100%; margin-top: 10px;">删除模板</button>
						
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
        var temp_click_flag; //记录当前被点击的模板是否存在于暂存队列
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
		var addImage_filename; //记录服务器生成的新添图片的名字或路径
		var addImage_url; //记录服务器生成的新添图片的绝对主机路径
		var index = -1, last_index= -1, cur_x, cur_y; //记录当前鼠标滑到的画图区域index及其坐标
		var other_img_array = []; //记录当前新增的模板的相关原图
		var batch_id; //暂存队列的batch_id
		var option_width; //长图的补长

		//储存服务器传过来的对象
		var model_array = [];

        //paint
		var change = false; //表示是否更改过区域表单信息
		function form_change() {
			tellConsole("here",4);
			change = true;
			up_done = false;
			return true;
		}
	 	function windowTocanvas(canvas, x, y) {  
            var bbox = canvas.getBoundingClientRect();  
            return {  
                x: x - bbox.left * (canvas.width / bbox.width),   
                y: y - bbox.top * (canvas.height / bbox.height)  
            };  

        } 
        function getPaint(paint_area, cxt) {
        	for(var i = 0; i < paint_area.length; i++){
				cxt.strokeRect(paint_area[i].begin_x, paint_area[i].begin_y, paint_area[i].width, paint_area[i].height);
			}
        }
        function ifPaint(paint_area, x, y) {
        	var final_index = -1;
        	var paint_num = 0;
        	var cur_dist = -1; // 判断当前光标离左边底角的距离
        	for(var i = 0; i < paint_area.length; i++) {
        		if(((x >= paint_area[i].begin_x)&&(x <= paint_area[i].begin_x+paint_area[i].width)) || ((x <= paint_area[i].begin_x)&&(x >= paint_area[i].begin_x+paint_area[i].width))) {
        			if(((y >= paint_area[i].begin_y)&&(y <= paint_area[i].begin_y+paint_area[i].height)) || ((y <= paint_area[i].begin_y)&&(y >= paint_area[i].begin_y+paint_area[i].height))){
        				
        				var temp_dist = Math.abs(x - paint_area[i].large_x) * Math.abs(x - paint_area[i].large_x) + Math.abs(y - paint_area[i].large_y) * Math.abs(x - paint_area[i].large_y);
        				if(cur_dist == -1) {
        					cur_dist = temp_dist;
        					final_index = i;
        				}
        				else if(cur_dist != -1 && cur_dist > temp_dist) {
        					cur_dist = temp_dist;
        					final_index = i;
        				}
        				paint_num ++;
        			}
        		}
        	}	
        	if(paint_num == 0) return -1;
        	else return final_index;
        }

        //init paint
        var up_done=false, button_use=false; //up_done表示是否可以开始绘图, button_use表示打勾、打叉按钮是否可用
        var muban_type = 0; //记录是修改还是增加模板
        var zoom = [8, 13, 18, 23, 28], zoom_index = 3; //图片纵深大小级别

        //调整ku_img_container长宽
        function adjustKuImg() {
        	$(".ku_img_container .ku_img").each(function() {
				if($(this).get(0).image_whsize >= 1) {
					$(this).get(0).style.height = parseFloat($(this).width() * parseFloat(invoice_height / invoice_width)) + "px";	
				} else {
					var width_str = parseFloat(invoice_height / invoice_height_ver * invoice_width_ver / invoice_width)*100 + "%";
					$(this).get(0).style.width = width_str;
					$(this).get(0).style.height = parseFloat($(this).width() * parseFloat(invoice_height_ver / invoice_width_ver)) + "px";
				}
			})
        }

        //调整图片的宽度
        function zoom(type) {
        	if(type == 1) { // zoom in
        		$(".ku_img_container").css("width", zoom[++zoom_index] + "%");
        		adjustKuImgContainer();

        	} else { //zoom out
        		$(".ku_img_container").css("width", zoom[--zoom_index] + "%");
        		adjustKuImgContainer();
        	}
        }

        function initPaintForm(src) { //初始化全局及局部表单，在未启动编辑之前禁用修改功能
        	
        	$("#getEdit").get(0).disabled = false;
        	$("#myCanvas").css("backgroundImage", "url(\'" + "\')");

        	// $("#global_setting *").each(function(){
        	// 	$(this).get(0).disabled = true;
        	// })
        	$(".right_body *").not("#getEdit").each(function(){
        		$(this).get(0).disabled = true;
        	}) 

        	$("#setting_form *").each(function(){
        		$(this).get(0).disabled = true;
        	})

        	$("#myModal .file_label").addClass("file_label_disabled");

        	$("#myCanvas").css("cursor", "default");
        	up_done = false; 
        	button_use = false;
        	$("#close img").get(0).src = "pic/close_disabled.png";
        	$("#certain img").get(0).src = "pic/tick_disabled.png";

        	$("#close").addClass('disabled_button');
        	$("#certain").addClass('disabled_button');

        	$("#myModalLabel").text("操作图片");
			$("#myModalLabel_progress ").css("display", "none");
			$("#myModalLabel_progress .progress-bar").css("width", "0%");

			flushListenFunction(); //激活画图前mousemove的监听函数

        	var img = new Image();
        	img.onload = function() {
				justifySize(img.width, img.height); //判断画布比例
        	}
        	img.src = src;
        }

        function flushForm() { //激活所有表单，开启修改功能
        	// $("#global_setting *").not("#dinge").each(function(){
        	// 	$(this).get(0).disabled = false;
        	// })
        	$(".right_body *").not("#dinge").each(function(){
        		$(this).get(0).disabled = false;
        	}) 

        	$("#setting_form *").each(function(){
        		$(this).get(0).disabled = false;
        	})

        	$("#myModal .file_label").removeClass("file_label_disabled");

        	if($("#global_setting #dinge_checkbox").get(0).checked == true) {
        		$("#dinge").get(0).disabled = false;
        	} else {
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
 
        function flushListenFunction() {
        	//画图前鼠标移到对应位置的响应函数
			$("#myCanvas").mousemove(function(event1){
				if(!change){ // 修改好局部信息后才能响应改函数
					cur_x = windowTocanvas(c, event1.pageX-$(document).scrollLeft(), event1.pageY-$(document).scrollTop()).x;
					cur_y = windowTocanvas(c, event1.pageX-$(document).scrollLeft(), event1.pageY-$(document).scrollTop()).y;
					index = ifPaint(paint_area, cur_x, cur_y);
					if(index != -1){
						if(index != last_index) { //如果当前所在的绘图索引更改，局部表单消失
							$(".setting_panel").css("display","none");
							last_index = index;
						}
						$(".hid_panel").css({
							"display": "block",
							"top": paint_area[index].large_y,
							"right": $(".modal-body").width()-(paint_area[index].large_x + option_width)
						});
					}
					else {
						if(change == false) {		
							$(".hid_panel").css("display", "none");
							$(".setting_panel").css("display","none");
							$("#setting_form").get(0).reset();
							change = false;
						}
					}		
				}
				
			})

			//画图前进入设置按钮
			$("#setting").mouseenter(function(){
				var setting_width = parseInt($(".setting_panel").width());
				if(index != -1){
					$(".setting_panel").css("display", "block");
					$(".setting_panel").css("top", paint_area[index].large_y+"px");
					$(".setting_panel").css("left", paint_area[index].large_x+option_width+"px");
					if(change == false){
						$(".setting_panel select[name='quyu']").val(paint_area[index].quyu);
						$(".setting_panel select[name='bg_color']").val(paint_area[index].bg_color);
						$(".setting_panel select[name='font_color']").val(paint_area[index].font_color);
						$(".setting_panel input[name='keyword']").val(paint_area[index].keywords);
						for(var i = 0; i < paint_area[index].ganrao.length; i++) {
							$(".setting_panel input[value=\'"+paint_area[index].ganrao[i]+"\']").get(0).checked="checked";	
						}	
					}
				}
			});
        }

        //判断模板图片是横图还是竖图
        function justifySize(width, height) {
        	canvas_width = width;
        	canvas_height = height;
        	console.log("width:" + width + " height:" + height);
        	if(width >= height) {
        		$("#myCanvas").get(0).width = invoice_width;
        		$("#myCanvas").get(0).height = invoice_height;
        		$("#myCanvas").css("backgroundSize", invoice_width+"px "+invoice_height+"px");
        		$("#myCanvas").parent().css("borderStyle", "none");
        		option_width = 0;
        		// $("#myCanvas").parent().css("width", invoice_width+"px");
        		// $("#myCanvas").parent().css("height", invoice_height+"px");
        	}
        	else {
        		$("#myCanvas").get(0).width = invoice_width_ver;
        		$("#myCanvas").get(0).height = invoice_height_ver;
        		$("#myCanvas").css("backgroundSize", invoice_width_ver+"px "+invoice_height_ver+"px");
        		$("#myCanvas").parent().css("border", "1px solid rgb(229,229,229)");
        		option_width = $("#myCanvas").get(0).offsetLeft;
        		// $("#myCanvas").parent().css("height", invoice_height_ver+"px");
        	}
        	//还原画布上下文
        	cxt.strokeStyle = "#00ff36";
			cxt.lineWidth = 2;
        }

        //上传图片处理函数
        function fileHandler(files) {
        	// console.log(files[0].name);
        	var file = files[0];
        	var imageType = /^image\//;
    
			if (imageType.test(file.type)) {
				var img = new Image();
				img.onload = function() {
					justifySize(img.width, img.height);
					other_img_array = [];
					$(".other_img_name_container").css("display", "none");
	        		$(".other_img_name").text("");
					$("#myModal").modal("show");
					// console.log(img.src);
					// $("#test_img").get(0).src = img.src;
					console.log($("#addImageForm input[name='type']").val());
					$("#addImageForm").submit();
					// cxt.drawImage(img, 0, 0, $("#myCanvas").get(0).width, $("#myCanvas").get(0).height);
					// $("#copy_fapiao").css("backgroundImage", "url(" + data.img_str + ")");
					// $("#myCanvas").css("backgroundImage", "url(" + img.src + ")");
					addImageSubmit();

					window.URL.revokeObjectURL(image_src);
				}

				// var reader = new FileReader();
				// reader.onload = (function(aImg) { 
				// 	return function(e) { aImg.src = e.target.result; }; 
				// })(img);
				// reader.readAsDataURL(file);
				
				var image_src = window.URL.createObjectURL(file);
				img.src = image_src;
	        } else {
	        	alert("请选择图片文件");
	        }
        }

        //上传其它原图处理函数
        function addHandler(files) {
        	var imageType = /^image\//;
        	var temp_array = [];
        	var standard = true;
        	for(var i = 0; i < files.length; i++) {
				if (imageType.test(files[i].type)) {
					temp_array.push(files[i].name);

				} else {
					alert("请选择图片文件");
					standard = false;
					break;
				}
        	}
        	if(standard == true) {
        		other_img_array.push.apply(other_img_array, temp_array);
        		var other_img_name_str = "";
        		for(var i in other_img_array) {
        			other_img_name_str += other_img_array[i];
        			if(i != other_img_array.length-1) 
        				other_img_name_str += "、";
        		}
        		$(".other_img_name").text(other_img_name_str);
        		$(".other_img_name_container").css("display", "block");

        		// $("#addMubanImg_form input[name='origins']").val(other_img_name.split("、"));
        		$("#addMubanImg_form").submit();
        	}
        	temp_array = null;
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
						addImgMuban(res1.model_list[i].model_url, res1.model_list[i].origin_url, res1.model_list[i].json_model, res1.model_list[i].model_id, res1.model_list[i].model_register_time, res1.model_list[i].image_size, res1.model_list[i].model_label);
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
			// var file = $("#inputImageFile").val();
			// console.log($("#inputImageFile").val());
			// var temp_img = new Image();
			// temp_img.onload = function() {
			// 	console.log(temp_img.width + " " + temp_img.height);
			// }
			// temp_img.src = $("#inputImageFile").val();
			
			$("#myCanvas").css("backgroundImage","url(\'\')");
			$("#myModalLabel").text("正在上传图片...");
			$("#myModalLabel_progress ").css("display", "inline-block");
			$('#myModal').modal('show');
			$("#myModalLabel_progress .progress-bar").css("width", "40%");
			muban_type = 0;
			return true;
		}

		//点击清空模板库按钮
		function deleteAllMuban() {
			$("#progressModal h4").text("正在清空");
			$("#progressModal .progress-bar").get(0).style.width = "40%";
			$("#progressModal").modal('show');
			$.ajax({
				url: "http://"+ip2+"/invoice/deleteAllModel.action",
				type: "POST",
				data: {
					user_id: user_id,
					msg_id : "5"
				},
				success: function(res, status) {
					$("#progressModal h4").text("清空模板库成功");
        			$("#progressModal .progress-bar").get(0).style.width = "100%";
        			$("#muban_num").text("0");
					setTimeout(function(){
						$("#progressModal").modal('hide');
						$("#progressModal .progress-bar").get(0).style.width = "40%";
					}, 1500);
					$(".muban_contain").html("");
				},
				error: function() {
					$("#progressModal h4").text("清空模板库失败");
					$("#progressModal .progress-bar").addClass("progress-bar-danger");
					$("#progressModal .btn").get(0).disabled = false;
				}
			})
		}

		//点击模板后的动作
		function clickMuban(jq_Muban, click_flag) {

			tellConsole(cxt, 3);

			initPaintForm(jq_Muban.get(0).origin_url);
			$('#myModal').modal('show');
			temp_click_jq_img = jq_Muban;
			temp_click_flag = click_flag;

			addImage_filename = jq_Muban.get(0).file_name;
			json_model = JSON.parse(jq_Muban.get(0).json_model);
			// console.log("json_model: "+json_model.toString());
			// muban_type = 1;

			if(jq_Muban.get(0).other_img_array != undefined) {
				other_img_array = jq_Muban.get(0).other_img_array;
				var other_img_name_str = "";
        		for(var i in other_img_array) {
        			other_img_name_str += other_img_array[i];
        			if(i != other_img_array.length-1) 
        				other_img_name_str += "、";
        		}
        		// console.log(other_img_name_str);
        		$(".other_img_name").text(other_img_name_str);
        		$(".other_img_name_container").css("display", "block");
			} else {
				$(".other_img_name_container").css("display", "none");
	        	$(".other_img_name").text("");
				other_img_array = [];
			}

			//将json_model的内容push进paint_area并设置global setting

	 		if(json_model.global_setting != undefined) {
	 			$("#global_setting input[name='biaoqian']").val(json_model.global_setting.label);
	 			$("#global_setting #dinge_checkbox").get(0).checked = json_model.global_setting.quota == 0 ? false : true;
	 			$("#global_setting input[name='dinge']").val(json_model.global_setting.quota == 0 ? "" : json_model.global_setting.quota);
	 		}

	 		for(var i in json_model) {
	 			if(json_model.hasOwnProperty(i) && i != "global_setting" && json_model[i] != null) {
	 				console.log(i);
	 				paint_area.push(json_model[i]);
	 			}
	 		}
	 		console.log(paint_area);

			//点击某张图片后发送获取img_str的请求
			$.ajax({
				url: "http://" + ip2 + "/invoice/getImgStr.action",
				type: "POST",
				cache: false,
				data:{
					url: jq_Muban.get(0).origin_url
				},
				success: function(res) {
					//console.log(res);
					res1 = JSON.parse(res);
					temp_img_str = res1.img_str;
					// alert(res1.img_str);
					$("#myCanvas").get(0).style.backgroundImage = "url(" + res1.img_str.toString() + ")";
					var img = new Image();
					img.onload = function() {
						getPaint(paint_area, cxt);	
					}
					img.src = res1.img_str.toString();
				},
				error: function(e) {
					tellConsole(e, 1);
				}
			})
		}

		//添加模板图片至模板库
		function addImgMuban(url, origin_url, json_model, id, model_register_time, image_size, model_label) { 

			// console.log(url);
			//放入model_array
			var model_object = {
				model_url : url,
				origin_url : origin_url,
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
			// alert(model_array_object.model_url);
			console.log(model_array_object.model_url);
			$(".thumbnail_muban").append("<div><img /><p></p></div>");
			$(".thumbnail_muban div:last-child").addClass("ku_img_container");	
			$(".thumbnail_muban div:last-child p").addClass("ku_img_id");
			// console.log(model_array_object.model_label);
			$(".thumbnail_muban div:last-child p").text(model_array_object.model_label);
			$(".thumbnail_muban div:last-child img").addClass("ku_img");
			$(".thumbnail_muban div:last-child img").get(0).onload = function() {
				$(this).get(0).image_whsize = $(this).get(0).width / $(this).get(0).height;
				if($(this).get(0).width > $(this).get(0).height) {
					$(this).get(0).style.height = parseFloat($(this).width() * parseFloat(invoice_height / invoice_width)) + "px";	
				} else {
					var width_str = parseFloat(invoice_height / invoice_height_ver * invoice_width_ver / invoice_width)*100 + "%";
					$(this).get(0).style.width = width_str;
					$(this).get(0).style.height = parseFloat($(this).width() * parseFloat(invoice_height_ver / invoice_width_ver)) + "px";
				}
			}
			$(".thumbnail_muban div:last-child img").get(0).src = model_array_object.model_url;
			$(".thumbnail_muban div:last-child img").get(0).model_url = model_array_object.model_url;
			$(".thumbnail_muban div:last-child img").get(0).origin_url = model_array_object.origin_url;
			$(".thumbnail_muban div:last-child img").get(0).file_name = model_array_object.file_name;
			$(".thumbnail_muban div:last-child img").get(0).json_model = model_array_object.json_model;
			$(".thumbnail_muban div:last-child img").get(0).model_id = model_array_object.model_id;
			$(".thumbnail_muban div:last-child img").unbind("click").click(function() {
				clickMuban($(this), 1);
			})
		}

		//暂存新增的model入temp_save_muban中
		function addToTempSaveMuban(model_array_object) {
			console.log("model_array_object.origin_url=" + model_array_object.origin_url);
			$(".temp_save_muban").append("<div><img /><p></p></div>");
			$(".temp_save_muban div:last-child").addClass("ku_img_container");	
			$(".temp_save_muban div:last-child p").addClass("ku_img_id");
			// console.log(model_array_object.model_label);
			$(".temp_save_muban div:last-child p").text(model_array_object.json_model.global_setting.label);
			$(".temp_save_muban div:last-child img").addClass("ku_img");
			$(".temp_save_muban div:last-child img").get(0).onload = function() {
				if($(this).get(0).width > $(this).get(0).height) {
					$(this).get(0).style.height = parseFloat($(this).width() * parseFloat(invoice_height / invoice_width)) + "px";	
				} else {
					var width_str = parseFloat(invoice_height / invoice_height_ver * invoice_width_ver / invoice_width)*100 + "%";
					$(this).get(0).style.width = width_str;
					$(this).get(0).style.height = parseFloat($(this).width() * parseFloat(invoice_height_ver / invoice_width_ver)) + "px";
				}
			}
			$(".temp_save_muban div:last-child img").get(0).action_id = model_array_object.action_id;
			$(".temp_save_muban div:last-child img").get(0).src = model_array_object.model_url;
			$(".temp_save_muban div:last-child img").get(0).origin_url = model_array_object.origin_url;
			$(".temp_save_muban div:last-child img").get(0).json_model = JSON.stringify(model_array_object.json_model);
			console.log(model_array_object.other_img_array);
			$(".temp_save_muban div:last-child img").get(0).other_img_array = model_array_object.other_img_array;
			$(".temp_save_muban div:last-child img").unbind("click").click(function() {
				clickMuban($(this), 0);
			})
		}

		//将model_array中的一项放入list视图
		function addToList(model_array_object) {
			$(".list_muban").append("<div class=\"list_muban_contain\"><span class=\"fa fa-image\"></span><span class=\"ku_img_id\"></span></div>")
			$(".list_muban .list_muban_contain:last-child .ku_img_id").text(model_array_object.model_label);
			$(".list_muban .list_muban_contain:last-child .fa-image").get(0).json_model = model_array_object.json_model;
			$(".list_muban .list_muban_contain:last-child .fa-image").get(0).model_id = model_array_object.model_id;
			$(".list_muban .list_muban_contain:last-child .fa-image").get(0).model_url = model_array_object.model_url;
			$(".list_muban .list_muban_contain:last-child .fa-image").get(0).origin_url = model_array_object.origin_url;
			$(".list_muban .list_muban_contain:last-child .fa-image").get(0).file_name = model_array_object.file_name;
			$(".list_muban .list_muban_contain:last-child .fa-image").unbind("click").click(function() {
				clickMuban($(this), 1);
				
			})
		}

		//将model_array中的一项放入detail视图
		function addToDetail(model_array_object) {
			$(".muban_table tbody").append("<tr><td>" + model_array_object.model_label + "</td><td>" + model_array_object.model_register_time + "</td><td>" + model_array_object.image_size + "KB</td><td>" + model_array_object.model_url.split(".")[model_array_object.model_url.split(".").length-1] + "</td></tr>");
			$(".muban_table tbody tr:last-child").get(0).json_model = model_array_object.json_model;
			$(".muban_table tbody tr:last-child").get(0).model_id = model_array_object.model_id;
			$(".muban_table tbody tr:last-child").get(0).model_url = model_array_object.model_url;
			$(".muban_table tbody tr:last-child").get(0).origin_url = model_array_object.origin_url;
			$(".muban_table tbody tr:last-child").get(0).file_name = model_array_object.file_name;
			$(".muban_table tbody tr:last-child").unbind("click").click(function() {
				clickMuban($(this),1);
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

		//修改temp_save中的模板信息
		function changeTempSaveMuban(id, attr_name, value) {
			//temp_save视图
			$(".temp_save_muban div img").each(function() {
				if($(this).get(0).action_id == id) {
					$(this).prop(attr_name, value);
					if(attr_name == "model_url") { //缩略图的src相应改变显示图片效果
						$(this).get(0).src = value;
					}
					else if(attr_name == "json_model") {
						$(this).prop(attr_name, JSON.stringify(value));
						//console.log($(this).get(0).json_model);
					} 
					else if(attr_name == "model_label") {
						$(this).parent().children("p").text(value);
					}
				}
			})
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
					else if(attr_name == "model_label") {
						$(this).parent().children("p").text(value);
					}
				}
			})

			//List視圖
			$(".list_muban .list_muban_contain .fa-image").each(function() {
				if($(this).get(0).model_id == id) {
					if(attr_name == "json_model") {
						$(this).prop(attr_name, JSON.stringify(value));
					}
					else if(attr_name == "model_label") {
						$(this).parent().children(".ku_img_id").text(value);
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
					else if(attr_name == "model_label") {
						$(this).children("td:first-child").text(value);
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

			//temp_save视图
			$(".temp_save_muban div img").each(function(){
				if($(this).get(0).action_id == id) {
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

		//初始化画布模态框
		var myModal_real_height = 843; //mymodal的滚动高度

		function initCanvasModal() {
			var model_width = parseInt(invoice_width) + 240;
			//初始化模态框的长宽
			if(model_width > document.documentElement.clientWidth) {
				$("#myModal").css("width", document.documentElement.clientWidth+"px");
			} else {
				$("#myModal").css("width", model_width + "px");
			}
			if(myModal_real_height > document.documentElement.clientHeight) {
				$("#myModal .modal-content").css("height", document.documentElement.clientHeight+"px");
			} else {
				$("#myModal .modal-content").css("height", myModal_real_height+"px");	
			}
			
			// $("#myModal").css("height", model_height + "px");
			$("#canvas_container").css("width", invoice_width+"px");
			$("#canvas_container").css("height", invoice_height+"px");
			$("#myCanvas").get(0).height = invoice_height;
			$("#myCanvas").get(0).width = invoice_width;
		}

		function initAjaxForm() {
			var options = { 
		        // target:        '#output1',   // target element(s) to be updated with server response 
		        // beforeSubmit:  showRequest,  // pre-submit callback 
		        success: function(res){  // post-submit callback 
		        	tellConsole(res,2);
		        	var res1 = JSON.parse(res);
		        	if(res1.err != undefined) {
		        		$("#myModalLabel").text("加载图片失败");
		        		setTimeout(function(){
							$("#myModal").modal("hide");
						}, 1000);	
		        	} else {
			        	addImage_filename = res1.file_path;
			        	$("#addMubanImg_form input[name='file_path']").val(res1.file_path);
			        	addImage_url = res1.origin_url;
			        	tellConsole(res1.origin_url,3);
			        	var img_temp = new Image();
			        	img_temp.onload = function() {

			        		// justifySize(img_temp.width, img_temp.height);
			        		flushForm();
				        	temp_img_str = res1.img_str;
				        	$("#myCanvas").get(0).style.backgroundImage = "url(\'" + res1.img_str + "\')";
							$("#myModalLabel_progress .progress-bar").css("width", "100%");
							setTimeout(function(){
								$("#myModalLabel_progress ").css("display", "none");
								$("#myModalLabel_progress .progress-bar").css("width", "0%");
								$("#myModalLabel").text("操作图片");
							}, 1000);	
			        	}
			        	img_temp.src = res1.origin_url;	
		        	}
		        },  
		 
		        // other available options: 
		        url: "http://"+ip2+"/invoice/uploadModelOrigin.action",       // override for form's 'action' attribute 
		        //type:      type        // 'get' or 'post', override for form's 'method' attribute 
		        //dataType:  null        // 'xml', 'script', or 'json' (expected server response type) 
		        //clearForm: true        // clear all form fields after successful submit 
		        resetForm: true        // reset the form after successful submit 
		 
		        // $.ajax options can be used here too, for example: 
		        //timeout:   3000 
		    }; 

		    var options_other = {
		    	success: function(res){  // post-submit callback 
		        	tellConsole(res,2);
		        	var res1 = JSON.parse(res);
		        	if(res1.sucess != undefined) {
		        		tellConsole("success", 2);	
		        	} else {
			        	tellConsole("error", 2);
		        	}
		        },  
		 
		        // other available options: 
		        url: "http://"+ip2+"/invoice/uploadModelOrigin.action",       // override for form's 'action' attribute 
		        //type:      type        // 'get' or 'post', override for form's 'method' attribute 
		        //dataType:  null        // 'xml', 'script', or 'json' (expected server response type) 
		        //clearForm: true        // clear all form fields after successful submit 
		        resetForm: true        // reset the form after successful submit
		    } 
		    $('#addImageForm').ajaxForm(options);
		    $("#addMubanImg_form").ajaxForm(options_other);
		}

		$(document).ready(function() {
			// loadxml("config.xml");
			// connectEndpoint();
			// WebsocketJustify();

			// 窗口大小改变
			window.onresize = function(event) {
				initCanvasModal();
				justifySize(canvas_width, canvas_height);
				adjustKuImg();
				// $(".thumbnail_muban div img").each(function() {
				// 	if($(this).get(0).width > $(this).get(0).height) {
				// 		$(this).get(0).style.height = parseFloat($(this).width() * parseFloat(invoice_height / invoice_width)) + "px";	
				// 	} else {
				// 		var width_str = parseFloat(invoice_height / invoice_height_ver * invoice_width_ver / invoice_width)*100 + "%";
				// 		$(this).get(0).style.width = width_str;
				// 		$(this).get(0).style.height = parseFloat($(this).width() * parseFloat(invoice_height_ver / invoice_width_ver)) + "px";
				// 	}
				// })
			}

			// 判断权限
        	justifyUserGrant(user_json);
        	justifyRW(user_json);

			//初始化canvas
			initCanvasModal();			

			//ajaxForm配置添加图片按钮
			initAjaxForm();
		    
			//一次获取12条发票模板的请求（首次查询）
			$.ajax({
				async: true,
				url: "http://"+ip2+"/invoice/getAllModel.action",
				type : 'POST',
				cache : false,
				data: {
					user_id: user_id,
					page : 0 //首次查询，第一页page为0
				},
				success : function(res, status) {
					tellConsole(res,2);
					var res1 = JSON.parse(res);
					
					//添加模板img元素
					for(var i = 0; i < res1.model_list.length; i++) {
						//alert(res1.model_list[i].json_model);
						addImgMuban(res1.model_list[i].model_url, res1.model_list[i].origin_url, res1.model_list[i].json_model, res1.model_list[i].model_id, res1.model_list[i].model_register_time, res1.model_list[i].image_size, res1.model_list[i].model_label);
					}
					var muban_num = res1.model_list.length;
					$("#muban_num").text(muban_num.toString());

				},
				error : function() {
					tellConsole("首次获取12条发票模板错误", 1);
				}
			})

			//获取发送队列
			$.ajax({
			 	url: "http://"+ip2+"/invoice/getModelQueue.action",
			 	type : 'POST',
			 	cache : false,
			 	success : function(res, status) {
			 		tellConsole(res,-1);
			 		var res1 = JSON.parse(res);
					
			 		//添加模板img元素
			 		for(var i = 0; i < res1.list.length; i++) {
			 			var data = res1.list[i];
			 			console.log(data);
			 			var temp_model_object = {
			 				action_id: data.action_id,
			 				json_model : data.json_model,
			 				batch_id: data.batch_id,
			 				other_img_array: data.origins,
			 				model_url : data.model_url,
							origin_url : data.origin_url
			 			}
			 			$(".temp_save_muban").css("display", "block");
			 			addToTempSaveMuban(temp_model_object);
					}
					
			 		//var muban_num = res1.list.length;
			 		//$("#muban_num").text(muban_num.toString());

			 	},
			 	error : function() {
			 		tellConsole("首次获取12条发票模板错误", 1);
		    	}
			})


			// 回车进行搜索
			$("#searchproduct").on("focus", function(){
				$(document).keydown(function(e) {
					if(e.keyCode == 13) {
						searchproduct();
					}
				})
			})

			var begin_x, begin_y, end_x, end_y;
			var large_x, large_y, small_x, small_y;
			var paint = true, dosomething = false, paint_num = 0;

			//左键点击画图后的响应函数
			var first_time = true; //表示是否第一次进入mousedown函数
			$("#myCanvas").unbind("mousedown").mousedown(function(event){
				
				//$("#myCanvas").unbind("mousemove"); //删除绘图前的mousedown响应函数
				//$("#setting").unbind("mouseenter"); //删除绘图前setting按钮的mouseenter响应函数

				$(".hid_panel").css("display", "none"); //弹出框隐藏
				$(".setting_panel").css("display","none"); //弹出框隐藏
				if(index != -1) index = -1;

				if(up_done) {   // 如果可以绘制图片才开始响应以下函数
					paint = true; //可以开始绘图
					var startpoint = windowTocanvas(c, event.pageX-$(document).scrollLeft(), event.pageY-$(document).scrollTop());
					begin_x = startpoint.x; 
					begin_y = startpoint.y;

					$(this).unbind("mousemove").mousemove(function(event1){
						if(up_done){
							cur_x = windowTocanvas(c, event1.pageX-$(document).scrollLeft(), event1.pageY-$(document).scrollTop()).x;
							cur_y = windowTocanvas(c, event1.pageX-$(document).scrollLeft(), event1.pageY-$(document).scrollTop()).y;
							if(paint == true) { //判断当前是否处于绘图状态
								cxt.clearRect(0,0, c.width, c.height);
								getPaint(paint_area, cxt);
								var endpoint = windowTocanvas(c, event1.pageX-$(document).scrollLeft(), event1.pageY-$(document).scrollTop());
								cxt.strokeRect(begin_x, begin_y, endpoint.x-begin_x, endpoint.y-begin_y);				
							}
							else {
								index = ifPaint(paint_area, cur_x, cur_y)
								if(index != -1){
									if(index != last_index) { //如果当前所在的绘图索引更改，局部表单消失
										$(".setting_panel").css("display","none");
										last_index = index;
									}
									$(".hid_panel").css({
										"display": "block",
										"top": paint_area[index].large_y,
										"right": $(".modal-body").width()-paint_area[index].large_x
									});
								}
								else {
									if(change == false) {
										$(".hid_panel").css("display", "none");
										$(".setting_panel").css("display","none");
										$("#setting_form").get(0).reset();
										change = false;	
									}
								}
							}	
						}
					})
					
					$(this).unbind("mouseup").mouseup(function(event2){
						if(up_done){
							tellConsole((event2.pageX + " " + event2.pageY), 3);
							paint = false, up_done = false;
							end_x = windowTocanvas(c, event2.pageX-$(document).scrollLeft(), event2.pageY-$(document).scrollTop()).x;
							end_y = windowTocanvas(c, event2.pageX-$(document).scrollLeft(), event2.pageY-$(document).scrollTop()).y;
							large_x = end_x > begin_x ? end_x : begin_x;
							large_y = end_y > begin_y ? end_y : begin_y;
							small_x = end_x < begin_x ? end_x : begin_x;
							small_y = end_y < begin_y ? end_y : begin_y;
							$(".hid_panel").css({
								"display": "block",
								"top": large_y,
								"right": $(".modal-body").width()-(large_x+option_width)
							});
							$(".hid_panel img").mouseenter(function(){
								$(this).css("margin-bottom", "1px");
							});
							$(".hid_panel img").mouseleave(function(){
								$(this).css("margin-bottom", "0px");
							});
							// $(".hid_panel img").not("#setting").mouseenter(function(){
							// 	$(".setting_panel").css("display", "none");
							// });

							//画图后进入设置按钮
							$("#setting").unbind("mouseenter").mouseenter(function(){
								var setting_width = parseInt($(".setting_panel").width());
								if(index != -1){
									$(".setting_panel").css("display", "block");
									$(".setting_panel").css("top", paint_area[index].large_y+"px");
									$(".setting_panel").css("left", paint_area[index].large_x+option_width+"px");
									if(change == false){
										$(".setting_panel select[name='quyu']").val(paint_area[index].quyu);
										$(".setting_panel select[name='bg_color']").val(paint_area[index].bg_color);
										$(".setting_panel select[name='font_color']").val(paint_area[index].font_color);
										$(".setting_panel input[name='keyword']").val(paint_area[index].keywords);
										for(var i = 0; i < paint_area[index].ganrao.length; i++) {
											$(".setting_panel input[value=\'"+paint_area[index].ganrao[i]+"\']").get(0).checked="checked";	
										}	
									}
								}
								else{
									console.log(large_x + " " + large_y);
									// if(end_x > )
									$(".setting_panel").css("display", "block");
									$(".setting_panel").css("top", large_y+"px");
									$(".setting_panel").css("left", large_x+"px");	
								}
							});
							
						}
					})	
				}
			})

			//点击交叉图标
			$("#close").unbind('click').click(function(){
				if(button_use) {
					$(".hid_panel").css("display","none");
					if(index != -1){
						begin_x = paint_area[index].begin_x;
						begin_y = paint_area[index].begin_y;
						end_x = paint_area[index].end_x;
						end_y = paint_area[index].end_y;
					}
					if(begin_x <= end_x && begin_y <= end_y)
						cxt.clearRect(begin_x-3,begin_y-3, end_x-begin_x+5, end_y-begin_y+5);
					else if(begin_x <= end_x && begin_y > end_y)
						cxt.clearRect(begin_x-3,begin_y+3, end_x-begin_x+5, end_y-begin_y-5);
					else if(begin_x > end_x && begin_y <= end_y)
						cxt.clearRect(begin_x+3,begin_y-3, end_x-begin_x-5, end_y-begin_y+5);
					else 
						cxt.clearRect(begin_x+3,begin_y+3, end_x-begin_x-5, end_y-begin_y-5);
					$("#setting_form").get(0).reset();
					if(index != -1){
						paint_area.splice(index, 1);
					}
					getPaint(paint_area, cxt);
					$(".setting_panel").css("display", "none");
					up_done = true;
					change = false;	
				}
			});

			//点击打勾图标
			$("#certain").unbind('click').click(function(){
				if(button_use){
					$(".hid_panel").css("display","none");
					$(".setting_panel").css("display", "none");
					getPaint(paint_area, cxt);
					var check_value = [];
					$(".setting_panel input[name='ganrao']:checked").each(function(){
						check_value.push($(this).val());
					})
					//初次填写提交
					if(index == -1) {
						var temp_object = {
							begin_x: begin_x,
							begin_y: begin_y,
							end_x: end_x,
							end_y: end_y,
							width: Math.round(end_x-begin_x),
							height: Math.round(end_y-begin_y),
							large_x: large_x,
							large_y: large_y,
							x: Math.round(small_x),
							y: Math.round(small_y),
							quyu: $(".setting_panel select[name='quyu']").val(),
							bg_color: $(".setting_panel select[name='bg_color']").val(),
							font_color: $(".setting_panel select[name='font_color']").val(),
							keywords: $(".setting_panel input[name='keyword']").val(),
							ganrao: check_value,
							remove_line: $(".setting_panel input[value='直线干扰']").get(0).checked ? 1 : 0,
							remove_stamp: $(".setting_panel input[value='印章干扰']").get(0).checked ? 1 : 0,
						};
						tellConsole(temp_object, 3);
						paint_area.push(temp_object);	
					}
					//修改后提交
					else {
						var temp_object = {
							begin_x: paint_area[index].begin_x,
							begin_y: paint_area[index].begin_y,
							end_x: paint_area[index].end_x,
							end_y: paint_area[index]. end_y,
							width: paint_area[index].end_x-paint_area[index].begin_x,
							height: paint_area[index].end_y-paint_area[index].begin_y,
							large_x: paint_area[index].large_x,
							large_y: paint_area[index].large_y,
							x: paint_area[index].small_x,
							y: paint_area[index].small_y,
							quyu: $(".setting_panel select[name='quyu']").val(),
							bg_color: $(".setting_panel select[name='bg_color']").val(),
							font_color: $(".setting_panel select[name='font_color']").val(),
							keywords: $(".setting_panel input[name='keyword']").val(),
							ganrao: check_value,
							remove_line: $(".setting_panel input[value='直线干扰']").get(0).checked ? 1 : 0,
							remove_stamp: $(".setting_panel input[value='印章干扰']").get(0).checked ? 1 : 0,
						};
						paint_area.splice(index, 1, temp_object);
					}
					$("#setting_form").get(0).reset();
					up_done = true;
					change = false;	
				}
			});

			//表单有更改
			$(".setting_panel input[type='checkbox']").click(function(){
				change = true;
				updone = false;
			})

			//点击删除模板
			$("#delete_mb").click(function() {
				$.ajax({
					url: temp_click_flag == 1 ? "http://" + ip2 + "/invoice/deleteModel.action" : "http://" + ip2 + "/invoice/deleteCacheModel.action",
					type: "POST", 
					data: temp_click_flag == 1 ? {
						user_id : user_id,
						model_id : temp_click_jq_img.get(0).model_id
					} : {
						action_id : temp_click_jq_img.get(0).action_id
					},
					success: function(res, status) {
						tellConsole(res, 3);
						res1 = JSON.parse(res);
						if(res1.err != undefined) { 
							$("#progressModal h4").text("删除模板失败");
							$("#progressModal .progress-bar").addClass("progress-bar-danger");
							$("#progressModal .btn").get(0).disabled = false;
						}
						else {
							$("#progressModal .progress-bar").get(0).style.width = "80%";
						}

						if(temp_click_flag == 0) {
							$("#progressModal h4").text("删除模板成功");
		                    $("#progressModal .progress-bar").get(0).style.width = "100%";
		                    setTimeout(function(){$("#progressModal").modal('hide');}, 1000);
		                    deleteObject(temp_click_jq_img.get(0).action_id);
						}
					},
					error: function(e) {
						tellConsole(e,1);
						$("#progressModal h4").text("删除模板失败");
						$("#progressModal .progress-bar").addClass("progress-bar-danger");
						$("#progressModal .btn").get(0).disabled = false;
					}
				})

				//清楚表单及重置画布和paint_area
				cxt.clearRect(0,0,c.width,c.height);
				paint_area=[];
				$("#global_setting").get(0).reset();

				//显示进度条
				$("#progressModal h4").text("正在删除...");
    			$("#progressModal .progress-bar").get(0).style.width = "40%";
				$("#progressModal").modal('show');
			})

			//点击增加模板
			$("#submit_modal").click(function(){
				// console.log(other_img_array);
				cxt.clearRect(0,0, c.width, c.height);
				var temp_img = new Image();
				temp_img.onload = function() {
					cxt.drawImage(temp_img, 0,0,$("#myCanvas").get(0).width, $("#myCanvas").get(0).height);
					getPaint(paint_area, cxt);
					var canvas_url = c.toDataURL();
					if(muban_type == 1) edited_canvas_url = canvas_url;
					//alert(canvas_url);

					var area_ = [1,0,0,0,0,0,0,0], money_ = null, head_ = null, date_ = null, time_ = null, id_card_ = null, detail_ = null, invoice_id_ = null;
					//获取各个区域的表单信息变成json_model信息
					for(var i = 0; i < paint_area.length; i++){
						if(paint_area[i].quyu == 'money') {
							area_[1] = 1;
							money_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'head') {
							area_[2] = 1;
							head_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'date') {
							area_[3] = 1;
							date_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'time') {
							area_[4] = 1;
							time_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'id_card') {
							area_[5] = 1;
							id_card_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'detail') {
							area_[6] = 1;
							detail_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'invoice_id') {
							area_[7] = 1;
							invoice_id_ = paint_area[i];
						} 
					}
					console.log(paint_area);
					temp_json_model = {
			 			global_setting:{
							label: $("input[name='biaoqian']").val(),
							quota: $("input#dinge_checkbox").get(0).checked ? parseInt($("input[name='dinge']").val()) : 0,
							area_bitmap: area_.join("")	
						},
						money: money_,
						head: head_,
						date: date_,
						time: time_,
						id_card: id_card_,
						detail: detail_,
						invoice_id: invoice_id_
			 		};

					$.ajax({
						url: "http://" + ip2 + "/invoice/addModel.action",
						type: "POST",
						dataType: "text",
						data: {
							modelAction: JSON.stringify({
								user_id: user_id,
								json_model: temp_json_model,
						 		model_id: muban_type == 0 ? null : temp_click_jq_img.get(0).model_id,
						 		file_path: addImage_filename,
						 		batch_id : batch_id == undefined ? null : batch_id,
						 		company_id: 1,
						 		origins: other_img_array
							}),
							img_str: canvas_url,						
					 		// file_name: addImage_filename,			 		
					 		type: 1
					 	},
						success: function(res, status) {
							tellConsole(res, 0);
							res1 = JSON.parse(res);
							if(res1.err != undefined) { 
								$("#progressModal h4").text("添加/修改模板失败");
								$("#progressModal .progress-bar").addClass("progress-bar-danger");
								$("#progressModal .btn").get(0).disabled = false;
							}
							else {
								$("#progressModal .progress-bar").get(0).style.width = "100%";
								setTimeout(function(){
									$("#progressModal").modal('hide');
								}, 1000);

								if(res1.batch_id != undefined) {
									batch_id = res1.batch_id;
									var temp_model_object = {
										action_id: res1.action_id,
										json_model : temp_json_model,
										batch_id: res1.batch_id,
										other_img_array: other_img_array,
										model_url : res1.model_url,
										origin_url : res1.origin_url
									}
									$(".temp_save_muban").css("display", "block");
									addToTempSaveMuban(temp_model_object);
								}
							}
						},
						error: function(e) {
							tellConsole(e, 1);
							$("#progressModal h4").text("添加/修改模板失败");
							$("#progressModal .progress-bar").addClass("progress-bar-danger");
							$("#progressModal .btn").get(0).disabled = false;
						}
					})	

					//清除表单及重置画布和paint_area
					cxt.clearRect(0,0,c.width,c.height);
					paint_area=[];
					$("#global_setting").get(0).reset();
					tellConsole("finish", 4);
				}
				temp_img.crossOrigin = "anonymous"; //允许跨域
				// temp_img.src = $("#myCanvas").css("backgroundImage").split("url")[1].replace("(", "").replace(")","");
				temp_img.src = temp_img_str;

				//显示进度条
				$("#progressModal h4").text("正在添加...");
    			$("#progressModal .progress-bar").get(0).style.width = "40%";
				$("#progressModal").modal('show');
			})

			//点击修改模板
			$("#edit_modal").click(function(){
				cxt.clearRect(0,0, c.width, c.height);
				var temp_img = new Image();
				temp_img.onload = function() {
					cxt.drawImage(temp_img, 0,0,$("#myCanvas").get(0).width, $("#myCanvas").get(0).height);
					getPaint(paint_area, cxt);
					var canvas_url = c.toDataURL();
					if(muban_type == 1) edited_canvas_url = canvas_url;
					//alert(canvas_url);

					var area_ = [1,0,0,0,0,0,0,0], money_ = null, head_ = null, date_ = null, time_ = null, id_card_ = null, detail_ = null, invoice_id_ = null;
					//获取各个区域的表单信息变成json_model信息
					for(var i = 0; i < paint_area.length; i++){
						if(paint_area[i].quyu == 'money') {
							area_[1] = 1;
							money_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'head') {
							area_[2] = 1;
							head_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'date') {
							area_[3] = 1;
							date_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'time') {
							area_[4] = 1;
							time_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'id_card') {
							area_[5] = 1;
							id_card_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'detail') {
							area_[6] = 1;
							detail_ = paint_area[i];
						}
						else if(paint_area[i].quyu == 'invoice_id') {
							area_[7] = 1;
							invoice_id_ = paint_area[i];
						} 
					}

					temp_json_model = {
			 			global_setting:{
							label: $("input[name='biaoqian']").val(),
							quota: $("input#dinge_checkbox").get(0).checked ? parseInt($("input[name='dinge']").val()) : 0,
							area_bitmap: area_.join("")	
						},
						money: money_,
						head: head_,
						date: date_,
						time: time_,
						id_card: id_card_,
						detail: detail_,
						invoice_id: invoice_id_
			 		};

					$.ajax({
						url: temp_click_flag == 1 ? "http://" + ip2 + "/invoice/updateModel.action" : "http://" + ip2 + "/invoice/updateCacheModel.action",
						type: "POST",
						dataType: "text",
						data: temp_click_flag == 1 ? {
							modelAction: JSON.stringify({
								user_id: user_id,
								json_model: temp_json_model,
								company_id: 1,
								model_id: temp_click_jq_img.get(0).model_id
							}),
							img_str: canvas_url						
					 	} : {
					 		modelAction: JSON.stringify({
					 			action_id: temp_click_jq_img.get(0).action_id,
        						json_model: temp_json_model
					 		}),
					 		img_str: canvas_url
					 	},
						success: function(res, status) {
							tellConsole(res, 3);
							res1 = JSON.parse(res);
							if(res1.err != undefined) { 
								$("#progressModal h4").text("修改模板失败");
								$("#progressModal .progress-bar").addClass("progress-bar-danger");
								$("#progressModal .btn").get(0).disabled = false;
							}
							else {
								$("#progressModal .progress-bar").get(0).style.width = "80%";
							}
		                    console.log("model_url=" + res1.model_url);
							if(temp_click_flag == 0) {
								$("#progressModal h4").text("修改模板成功");
			                    $("#progressModal .progress-bar").get(0).style.width = "100%";
			                    setTimeout(function(){$("#progressModal").modal('hide');}, 2000);
			                    changeTempSaveMuban(temp_click_jq_img.get(0).action_id, "model_url", res1.model_url);
			                    changeTempSaveMuban(temp_click_jq_img.get(0).action_id, "json_model", temp_json_model);
			                    changeTempSaveMuban(temp_click_jq_img.get(0).action_id, "model_label", temp_json_model.global_setting.label);
							}
						},
						error: function(e) {
							tellConsole(e, 1);
							$("#progressModal h4").text("修改模板失败");
							$("#progressModal .progress-bar").addClass("progress-bar-danger");
							$("#progressModal .btn").get(0).disabled = false;
						}
					})	
					
					//清除表单及重置画布和paint_area
					cxt.clearRect(0,0,c.width,c.height);
					paint_area=[];
					$("#global_setting").get(0).reset();
					tellConsole("finish", 4);
				}
				temp_img.crossOrigin = "anonymous"; //允许跨域
				// temp_img.src = $("#myCanvas").css("backgroundImage").split("url")[1].replace("(", "").replace(")","");
				temp_img.src = temp_img_str;	

				//显示进度条
				$("#progressModal h4").text("正在修改...");
    			$("#progressModal .progress-bar").get(0).style.width = "40%";
				$("#progressModal").modal('show');
			})

			//点击一键上传
			$("#addImageForm_btn").click(function() {
				$("#progressModal h4").text("正在上传...");
				$("#progressModal .progress-bar").get(0).style.width = "40%";
				$.ajax({
					type: 'POST',
					url: "http://" + ip2 + "/invoice/pushBatchModel.action",
					data: {
						batch_id: batch_id
					},
					success: function(res) {
						$("#progressModal").modal("show");
						$("#progressModal .progress-bar").get(0).style.width = "80%";
					},
					error: function(err) {
						console.log("error");
					}
				})
			})

			//点击关闭模态窗口
			$("#close_modal").click(function(){
				cxt.clearRect(0,0, c.width, c.height);
				paint_area=[];
				$("#global_setting").get(0).reset();
				$("#myCanvas").css("backgroundImage", "url(\'\')");

				if(paint_area.length == 0) {
					$.ajax({
						type: 'POST',
						url: "http://" + ip2 + "/invoice/cancelAddModel.action",
						data: {
							file_path : addImage_filename
						},
						success: function(res) {
							console.log(res);
						}
					})	
				}
			})

			//点击确定进度条窗口
			$("#certain_progress").click(function(){
				$("#progressModal .progress-bar").removeClass("progress-bar-danger");
			})

			//global_setting 点击启用编辑按钮
			$("#getEdit").unbind("click").click(function() {
				flushForm();
			})

			$("#dinge_checkbox").unbind("click").click(function(){
				if($(this).get(0).checked == true) {
					$("#dinge").get(0).disabled = false;
				}
				else {
					$("#dinge").val("");
					$("#dinge").get(0).disabled = true;
				}
			})

			//点击详情列表表头
			$(".muban_table th").each(function(){
				$(this).click(function(){
					if($(this).find("span").attr("class") == "sorted_down" || $(this).find("span").attr("class") == "sorted_up") {
						if($(this).find("span").attr("class") == "sorted_down") {
							$(this).find("span").attr("class", "sorted_up"); 
						}
						else {
							$(this).find("span").attr("class", "sorted_down"); 
						}
						model_array.reverse();
						//详细信息视角
						$(".muban_table tbody").html("");
						for(var i = 0; i < model_array.length; i++) {
							addToDetail(model_array[i]);
						}
					} 
					else {
						clickToSort($(this).find("span").html());
						$(".muban_table tbody").html("");
						for(var i = 0; i < model_array.length; i++) {
							addToDetail(model_array[i]);
						}
						$(".muban_table th span").each(function(){
							if($(this).attr("class") != undefined)
								$(this).removeClass();
						})
						$(this).find("span").attr("class", "sorted_down"); 
					}
				})
			})

		})
	</script>
</body>
</html>