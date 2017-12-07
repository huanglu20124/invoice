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
</head>
<body>
	<jsp:include page="header.jsp" flush="true" />
	<main>
		<jsp:include page="aside_menu.jsp" flush="true" />
		<div class="main_content">

			<div class="main_content_hd flex flex-align-end">
				<span class="flex-1">报错发票</span>
			</div>
			<div class="panel_hd_line flex flex-align-end">
				<span class="flex-1" style="font-size: 16px;">共<span class="fault_num">0</span>张未被识别发票</span>
				<span class="flex-none" style="font-size: 14px; margin-left: 2em;">
	        		查看方式:<select id="show_type" class="form-control" style="display: inline-block; width: 8em; height: 25px; margin-left: 0.5em; padding: 0em 0.5em; font-size: 13.5px;" onchange="changeShowType()">
	        			<option selected>缩略图</option>
	        			<option>列表</option>
	        			<option>详细信息</option>
	        		</select>
	        	</span>
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
						      <th><span>发票id</span></th>
						      <th><span>所用模板id</span></th>
						      <th><span>发送者</span></th>
						      <th><span>所属单位</span></th>
						      <th><span class="sorted_down">发送时间</span></th>
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
	<script type="text/javascript" src="script/common.js"></script>
	<script type="text/javascript">

       //jsp加入
       var user_json = <%=JSON.toJSONString(request.getAttribute("user"))%>
		//储存服务器传过来的对象
		// var fault_array = [];

		//添加模板图片至模板库
		function addFaultInvoice(temp_fault_invoice) { 

			// console.log(url);
			//放入model_array
			fault_array.push(temp_fault_invoice);

			//缩略图视角
			addToThumbnail(temp_fault_invoice);

			//列表视角
			addToList(temp_fault_invoice);

			//详细信息视角
			addToDetail(temp_fault_invoice);

			//判断显示哪种视图
			whichToShow();

		}

		//将model_array中的一项放入thumbnail视图
		function addToThumbnail(temp_fault_invoice) {
			$(".thumbnail_muban").append("<div><img /><p>1</p></div>");
			$(".thumbnail_muban div:last-child").addClass("ku_img_container");	
			$(".thumbnail_muban div:last-child p").addClass("ku_img_id");
			$(".thumbnail_muban div:last-child p").text(temp_fault_invoice.invoice_id);
			$(".thumbnail_muban div:last-child img").get(0).src = temp_fault_invoice.invoice_url;
			$(".thumbnail_muban div:last-child img").addClass("ku_img");	
			$(".thumbnail_muban div:last-child img").get(0).style.height = parseFloat($(".muban_contain div:last-child img").width() * parseFloat(invoice_height / invoice_width)) + "px";

			$(".thumbnail_muban div:last-child img").get(0).invoice_info = temp_fault_invoice;
			$(".thumbnail_muban div:last-child img").unbind("click").click(function() {
				clickMuban($(this));
			})
		}

		//将model_array中的一项放入list视图
		function addToList(temp_fault_invoice) {
			$(".list_muban").append("<div class=\"list_muban_contain\"><span class=\"fa fa-image\"></span><span class=\"ku_img_id\"></span></div>")
			$(".list_muban .list_muban_contain:last-child .ku_img_id").text(temp_fault_invoice.invoice_id);
			$(".list_muban .list_muban_contain:last-child .fa-image").get(0).invoice_info = temp_fault_invoice;
			$(".list_muban .list_muban_contain:last-child .fa-image").unbind("click").click(function() {
				clickMuban($(this));
				
			})
		}

		//将model_array中的一项放入detail视图
		function addToDetail(temp_fault_invoice) {
			$(".muban_table tbody").append("<tr><td>" + temp_fault_invoice.invoice_id + "</td><td>" + temp_fault_invoice.model_id + "</td><td>" + temp_fault_invoice.user_name + "</td><td>" + temp_fault_invoice.company_name + "</td></tr>" + temp_fault_invoice.recognize_time + "</td></tr>");
			$(".muban_table tbody tr:last-child").get(0).invoice_info = temp_fault_invoice;
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

		function getFaultInvoice(page) {
			//发送ajax其请求获取前20个报错发票
        	$.ajax({
        		type: 'POST',
        		url : 'http://' + ip2 + '/invoice/getFaultQueue.action',
        		data : {
        			page : page
        		},
        		success : function(res, status) {
        			console.log(res);
        			var data = JSON.parse(res);
        			$(".fault_num").text(data.fault_num);
        			for(var i = 0; i < data.fault_list.length; i++) {
        				addFaultInvoice(data.fault_list[i]);
        			}
        		},
        		error : function() {
        			console.log("error");
        		}
        	})
		}

        $(document).ready(function(){

        	// 判断权限
        	justifyUserGrant(user_json);
        	justifyRW(user_json);

        	getFaultInvoice(0);
        	//connectEndpoint();
        	//ws.send("success");
        })
	</script>
</body>
</html>