<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.alibaba.fastjson.JSON"%>
<!DOCTYPE html>
<html>
<head>
	<title>中山大学发票识别监控系统</title>
	<meta charset="utf-8">
	<script type="text/javascript" src="script/jquery-3.2.1.min.js"></script>
	<script type="text/javascript" src="script/StackBlur.js"></script>
	<script type="text/javascript" src="script/bootstrap.min.js"></script>
    <script type="text/javascript" src="script/jquery-ui.min.js"></script>
    <script type="text/javascript" src="script/reconnecting-websocket.min.js"></script>
    <link rel="stylesheet" type="text/css" href="style/jquery-ui.min.css">
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
                <span class="flex-1">监控显示</span>
            </div>
			<div style="display: inline-block; width: 64%; vertical-align: top; margin-right: 2%;">
				<div class="panel panel-default">
				    <div class="panel-heading">
				        <p class="help-block" style="margin: 0; font-size: 14px;"><span id="user_name">发送者：</span><span style="margin-left: 3em;" id="action_start_time">发送时间：</span><span id="company_name" style="margin-left: 3em;">责任单位：</span></p>
				    </div>
				    <div class="panel-body" style="padding: 0px;">
						<div style="width: 100%; height:auto; overflow: hidden; border-radius: 4px; position: relative;" id="canvas_panel_body">
							<canvas style="background-color: transparent; background-size: cover; position:relative; z-index: 2;" id="show_fapiao"></canvas>
							<!-- 用于备份图片的画布 -->
							<canvas id="copy_fapiao" style="z-index: 1; position: absolute; background: url('pic/shibie_placehold.png') no-repeat center; background-size: cover;"></canvas>
						</div>
				    </div>
				</div>
                <div class="slider_container" style="width: 100%;">
                    <div class="slider_des" style="font-size: 16px; line-height: 3em;">滑动滑块以调节演示速度：</div>
                    <div id="slider" data-toggle="tooltip"></div>
                </div>
			</div>
			<div style="display: inline-block; width: 33%; vertical-align: top;">
				<div class="panel panel-default">
				    <div class="panel-heading">
				        <h3 class="panel-title">区域信息<span class="title_load"></span></h3>
				    </div>
				    <div class="panel-body">

                        <table class="table table-hover table-responsive">
                            <thead>
                                <tr><th>识别区域</th><th>识别结果</th><th>可信度</th></tr>
                            </thead>
                            <tbody>
                                <tr><td class="area_hd">发票类型</td><td></td><td></td></tr>
                                <tr><td class="area_hd">金额</td><td></td><td></td></tr>
                                <tr><td class="area_hd">客户名称</td><td></td><td></td></tr>
                                <tr><td class="area_hd">发票号码</td><td></td><td></td></tr>
                                <tr><td class="area_hd">日期</td><td></td><td></td></tr>
                                <tr><td class="area_hd">时间</td><td></td><td></td></tr>
                                <tr><td class="area_hd">具体信息</td><td></td><td></td></tr>
                                <!-- <tr><td class="area_hd">身份证号码</td><td></td><td></td></tr> -->
                            </tbody>
                        </table>
				    </div>
				</div>
				<div class="panel panel-default">
					<div class="panel-heading">
				        <h3 class="panel-title">套用模板<span class="muban_info"></span></h3>
				    </div>
				    <div class="panel-body">
				        <img src="pic/shibie_placehold.png" style="width: 100%;" id="muban" />
				    </div>
				</div>
			</div>
		</div>
	</main>

    <script type="text/javascript" src="script/common.js"></script>
	<script type="text/javascript">
        // var ws = null;
        //jsp加入
        var user_json = <%=JSON.toJSONString(request.getAttribute("user"))%>

        function resetImgLine(jq_ele, img_list) {
        	jq_ele.each(function(index, e){
				$(this).html("");
				if(index < img_list.length) {
					$(this).append("<img>");
					$(this).children().eq(0).get(0).src = img_list[index].url;	
				}
			})
        }

        //返回的画布坐标和实际画布坐标换算
        function coordinateConvert(x, y, w, h) {
        	var size = parseFloat($("#show_fapiao").width()) / invoice_width;
        	return {
        		convert_x: parseFloat(x * size),
        		convert_y: parseFloat(y * size),
        		convert_w: parseFloat(w * size),
        		convert_h: parseFloat(h * size)
        	}
        }

        //初始化slider
        var tooltip_cancel = 1; //表示tooltip是否可以隐藏掉
        function initSlider() {
            $( "#slider" ).slider({
                orientation: "horizontal",
                range: "min",
                max: 30,
                value: 0,
                step: 1,
                change : function(event, ui) {
                    tooltip_cancel = 1;
                    $.ajax({
                        type: 'POST',
                        url: "http://" + ip2 + "/invoice/changeSpeed.action",
                        data: {
                            delay: ui.value,
                            user_id: user_id
                        }
                    })
                },
                slide: function(event, ui) {
                    $("#slide_tooltip").text(ui.value.toString());
                    tooltip_cancel = 0;
                }
            });

            //tooltip
            $(".ui-slider-handle").append("<span id=\'slide_tooltip\'>0</span>");
            $(".ui-slider-handle").mouseenter(function(){
                $("#slide_tooltip").css("opacity", 1);
            })
            $(".ui-slider-handle").mouseleave(function(){
                if(tooltip_cancel == 1) {
                    $("#slide_tooltip").css("opacity", 0);    
                }  
            })
        }

        //获取画布及其上下文
        var c=document.getElementById("show_fapiao");
        var c1=document.getElementById("copy_fapiao");
		var cxt;
		var cxt1;
        //初始化画布调整画布及图片比例
        function initCanvasPhoto() {
            tellConsole((invoice_height + " " + invoice_width), 4);
        	$("#muban").get(0).style.height = parseFloat($("#muban").width() * parseFloat(invoice_height / invoice_width)) + "px";
        	$("#show_fapiao").get(0).width = $("#canvas_panel_body").get(0).offsetWidth;
        	$("#show_fapiao").get(0).height = parseFloat($("#show_fapiao").get(0).width * parseFloat(invoice_height / invoice_width));
    		$("#show_fapiao").css("backgroundSize", $("#show_fapiao").get(0).width+"px "+$("#show_fapiao").get(0).height+"px");
        	$("#copy_fapiao").get(0).width = $("#show_fapiao").get(0).width;
        	$("#copy_fapiao").get(0).height = $("#show_fapiao").get(0).height;
        	$("#copy_fapiao").css("backgroundSize", $("#copy_fapiao").get(0).width+"px "+$("#copy_fapiao").get(0).height+"px");

        	$("#copy_fapiao").get(0).style.top = $("#show_fapiao").get(0).offsetTop;
        	$("#copy_fapiao").get(0).style.left = $("#show_fapiao").get(0).offsetLeft;
        }

        $(document).ready(function(){

            // 判断权限
            justifyUserGrant(user_json);

        	initCanvasPhoto();
        
        	cxt = c.getContext("2d");
        	cxt1 = c1.getContext("2d");
        	cxt.strokeStyle = "#00ff36";
			cxt.lineWidth = 2;
            //开始加载图片
            $.ajax({
                url: "http://" + ip2 + "/invoice/openConsole.action",
                type: "POST",
                data: {},
                success: function(res) {
                    tellConsole(res, 2);
                    var data = JSON.parse(res);
                    if(data.img_str != undefined) {
                        $("#copy_fapiao").css("backgroundImage", "url(" + data.img_str + ")");
                        $("#show_fapiao").css("backgroundImage", "url('')");
                        $( "#slider" ).slider({value: data.delay});
                        $("#slide_tooltip").text(data.delay.toString());

                        var temp_img = new Image();
                        temp_img.onload = function(){
                            cxt.clearRect(0, 0, parseFloat($("#show_fapiao").width()), parseFloat($("#show_fapiao").height()));
                            cxt.drawImage(temp_img, 0, 0, parseFloat($("#show_fapiao").width()), parseFloat($("#show_fapiao").height()));

                            //console.log("here" + data.region_list);
                            ws.send(JSON.stringify({
                                code: "002"
                            }));
                            
                        }
                        temp_img.src = data.img_str;
                        //temp_img.src = data.url;
                        if(data.user_name != undefined) {
                            $("#user_name").text($("#user_name").text().split("：")[0] + "：" + data.user_name);
                            $("#action_start_time").text($("#action_start_time").text().split("：")[0] + "：" + data.action_time);
                            $("#company_name").text($("#company_name").text().split("：")[0] + "：" + data.company_name);   
                        }    
                    }
                    
                },
                error: function(e) {
                    tellConsole(e, 1);
                }
            })
        	//ws.send("success");

            //加载滑块
            initSlider();

        })
	</script>
</body>
</html>