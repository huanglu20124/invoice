var ip2; //host_ip
var wsuri; //websocket_url
var invoice_height, invoice_width, invoice_width_ver, invoice_height_ver;
var console_level; //控制台輸出等級 值越大輸出越多東西
var ws = null;
var queue_sub_num = 0;
var nth_area = 1; //记录这是识别的第几个区域
var start_region = 0; //判断是否要将当前websocket返回的json字符串记录下来
var region_list = []; //记录返回的json字符串
var relatvie_image_size = 0; //记录计算透明度时的相对文件大小
var rect_slide_interval = 0; //虚线矩形的滚动
var cur_text_x, cur_text_y, cur_x, cur_y, cur_width, cur_height;
var fault_array = []; //记录错误发票的数组
var user_id = 1; //记录当前用户的id
var root_host; //记录根主机名

//读取config.xml配置ip等信息
function loadxml(fileName) {
	$.ajax({
		async : false,
		url : fileName,
		dataType : "xml",
		type : "GET",
		success : function(res, status) {
			var xml_data = res;
            //console.log(xml_data);
			ip2 = xml_data.getElementsByTagName("connect_ip")[0].innerHTML;
            root_host = xml_data.getElementsByTagName("root_host")[0].innerHTML;
            invoice_width = xml_data.getElementsByTagName("invoice_width")[0].innerHTML;
            invoice_height = xml_data.getElementsByTagName("invoice_height")[0].innerHTML;
            invoice_width_ver = xml_data.getElementsByTagName("invoice_width_ver")[0].innerHTML;
            invoice_height_ver = xml_data.getElementsByTagName("invoice_height_ver")[0].innerHTML;
            console_level = xml_data.getElementsByTagName("console_level")[0].innerHTML;
            relatvie_image_size = xml_data.getElementsByTagName("relatvie_image_size")[0].innerHTML;
			wsuri = "ws://" + ip2 + "/invoice/webSocketServer.action";
			tellConsole(wsuri, 2);
		},
		error : function() {
			alert("读取配置文件失败，稍后重试");
		}
	})
}

function handleShow(data) {
	console.log(data);
    if(data.msg_id == 203) {
        console.log(data.msg_id);
        //copy_fapiao获取背景图, show_fapiao绘制图片
        $("#copy_fapiao").css("backgroundImage", "url(" + data.img_str + ")");
        $("#show_fapiao").css("backgroundImage", "url('')");
        var temp_img = new Image();
        temp_img.onload = function(){
            console.log("here");
            cxt.clearRect(0, 0, parseFloat($("#show_fapiao").width()), parseFloat($("#show_fapiao").height()));
            cxt.drawImage(temp_img, 0, 0, parseFloat($("#show_fapiao").width()), parseFloat($("#show_fapiao").height()));
            $(".muban_info").text("（正在搜索可用模板）");
            $("#muban").get(0).src = "pic/search_placehold.png";

            $("#user_name").text($("#user_name").text().split("：")[0] + "：" + data.user_name);
            $("#action_start_time").text($("#action_start_time").text().split("：")[0] + "：" + data.action_time);
            $("#company_name").text($("#company_name").text().split("：")[0] + "：" + data.company_name);
        
        }
        temp_img.src = data.img_str;
    }
    else if(data.msg_id == 100 && data.status == 0) {
        $(".muban_info").text("（模板名称：" + data.label + "）");
        $("#muban").get(0).src = data.url;

        $("td.area_hd").eq(0).next().text(data.model_label);
        $("td.area_hd").eq(0).next().next().text("1.00");
        $("td.area_hd").eq(0).parent().addClass("info_blue");
    }
    else if(data.msg_id == 101 && data.status == 0) {
        //模糊其它区域
        if(nth_area == 1){
            stackBlurCanvasRGB("show_fapiao", 0, 0, parseFloat($("#show_fapiao").width()), parseFloat($("#show_fapiao").height()), 15); 
            nth_area ++;
        } 
        
        //框出识别区域并使其区域清晰
        // console.log(cxt1);
        var position = coordinateConvert(data.position.x, data.position.y, data.position.w, data.position.h);
        //console.log("data.position.x:" + data.position.x + ";" + "position.convert_x:" + position.convert_x);
        var temp_imageData = cxt1.getImageData(position.convert_x, position.convert_y, position.convert_w, position.convert_h);
        cxt.putImageData(temp_imageData, position.convert_x, position.convert_y);
        //cxt.strokeRect(position.convert_x, position.convert_y, position.convert_w, position.convert_h);
        //cxt.strokeStyle = "#00b717";
        strokeDashRect(cxt, position.convert_x, position.convert_y, position.convert_x+position.convert_w, position.convert_h+position.convert_y, 10, temp_imageData);

        if(position.convert_y < 20) cur_text_y = position.convert_y + 5 + position.convert_h;
        else cur_text_y = position.convert_y - 10;

        cur_text_x = position.convert_x;
        cur_x = position.convert_x; cur_y = position.convert_y;
        cur_width = position.convert_w; cur_height = position.convert_h;

        $(".title_load").text("（正在识别" + data.pos_id + "）");
    }
    else if(data.msg_id == 102 && data.status == 0) {
        if(data.pos_id != "金额") {
            $("td.area_hd").each(function() {
                if($(this).text() == data.pos_id) {
                    $(this).next().text(data.ocr_result);
                    $(this).next().next().text(data.probability);

                    if(data.probability >= 0.9) {
                        $(this).parent().addClass("info_blue");
                    }
                    else {
                        $(this).parent().addClass("info_red");
                    }

                }
            });    
        }

        tellConsole(cur_text_x + " " + cur_text_y, 3);
        //绘制相关文字
        cxt.fillStyle = "#ff0000";//颜色
        cxt.font = "normal 15px 黑体";//字体
        //cxt.textAlign = "center";//水平对齐　
        tellConsole(cxt, 4);
        cxt.fillText(data.pos_id + "：" + data.ocr_result, cur_text_x, cur_text_y);

        //画回实线矩形
        //cxt.strokeStyle = "#00ff36";
        cxt.strokeRect(cur_x, cur_y, cur_width, cur_height);
        clearInterval(rect_slide_interval);
    }
    else if(data.msg_id == 1 && data.status == 0) {
        tellConsole(data["金额"], 3);
        $("td.area_hd").each(function() {
            if($(this).text() == "金额") {
                if($(this).next().text() == "") {
                    $(this).next().text(data["金额"]);
                    if(data["金额prob"] > data["中文金额prob"]) {
                        $(this).next().next().text(data["金额prob"]); 
                        if(parseFloat(data["金额prob"]) >= 0.9) {
                            $(this).parent().addClass("info_blue");
                        }
                        else {
                            $(this).parent().addClass("info_red");
                        }
                    }
                    else {
                        $(this).next().next().text(data["中文金额prob"]); 
                        if(parseFloat(data["中文金额prob"]) >= 0.9) {
                            $(this).parent().addClass("info_blue");
                        }
                        else {
                            $(this).parent().addClass("info_red");
                        }
                    }
                }
            }
        });
        $(".title_load").text("（识别完毕）");
        nth_area = 1;
        tellConsole(data, 3);

        //过3秒后重置
        setTimeout(function(){
            $(".title_load").text("");
            cxt.clearRect(0, 0, parseFloat($("#show_fapiao").width()), parseFloat($("#show_fapiao").height()));
            cxt1.clearRect(0, 0, parseFloat($("#copy_fapiao").width()), parseFloat($("#copy_fapiao").height()));
            //$("#show_fapiao").css("backgroundImage", "url('pic/shibie_placehold.png')");

            $("#muban").get(0).src = "pic/shibie_placehold.png";
            $("#copy_fapiao").css("backgroundImage", "url('pic/shibie_placehold.png')");
            $(".muban_info").text("");

            $("#user_name").text($("#user_name").text().split('：')[0] + "：");
            $("#action_start_time").text($("#action_start_time").text().split('：')[0] + "：");
            $("#company_name").text($("#company_name").text().split('：')[0] + "：");

            $("td.area_hd").each(function() {
                $(this).next().text("");
                $(this).next().next().text("");

                $(this).parent().removeClass("info_blue");
                $(this).parent().removeClass("info_red");
            });
        }, 3000);
    }     
}

//連接websocket
function connectEndpoint(){

    ws = new WebSocket(wsuri);
    // ws.reconnectInterval = 5000;
    // ws.timeoutInterval = 10000;
    // ws.maxReconnectAttempts = 20;
    var img_list = [];

    ws.onmessage = function(evt) {
        //alert(evt.data);
        tellConsole(evt.data, 3);
        var data = JSON.parse(evt.data);
        //所有页面都接收到的请求
        if(data.msg_id == 205) {
            console.log("205");
            $(".fault_num").text(data.fault_num.toString());
            if(data.fault_num == 0) {
                $(".fault_num").css("display", "none");
            }
            else {
                $(".fault_num").css("display", "inline-block");
            }
        }

        // console.jsp
        if(window.location.href.indexOf("console.action") != -1) {
            //console.log(start_region);
            if(data.msg_id == 204) {
                console.log(data.region_list);
                for(var i = 0; i < data.region_list.length; i++) {
                    var data1 = JSON.parse(data.region_list[i]);
                    handleShow(data1);
                }
            }
            else {
                handleShow(data);     
            }      
            
        }      

        // queue.jsp
        else if(window.location.href.indexOf("queue.action") != -1){
            //增加数目
            if(data.msg_id == 201) {
                var num = parseInt($("#waiting_num").text());
                for(var i = 0; i < data.new_recognize.length; i++){
                    $(".waiting_list").append("<i class=\"fa fa-file rect_img waiting_list_item\" aria-hidden=\"true\"></i>");
                    var opacity_ = parseFloat(data.new_recognize[i].image_size / relatvie_image_size) > 1 ? 1 : parseFloat(data.new_recognize[i].image_size / relatvie_image_size); 

                    $(".waiting_list i:last-child").get(0).base_json = data.new_recognize[i];
                    $(".waiting_list i:last-child").css("opacity", opacity_);
                    $(".waiting_list i:last-child").click(function() {
                        $("#showWaiting").modal('show');
                        var temp_json = $(this).get(0).base_json;
                        tellConsole($(this).get(0).base_json, 3);
                        $("#user_info").text(temp_json.user_name);
                        $("#time_info").text(temp_json.action_time);
                        $("#img_info").get(0).src = temp_json.url;
                    })
                    num++;
                }
                $("#waiting_num").text(num.toString());
            }

            //减少数目
            else if(data.msg_id == 1) {
                $(".waiting_list i").first().remove();
                var num = parseInt($("#waiting_num").text());
                if(num > 0) num--;
                else queue_sub_num--;
                $("#waiting_num").text(num.toString());
            }    
        }
        
        // model.jsp
        else if(window.location.href.indexOf("model.action") != -1){
            if(data.msg_id == 2) { //单图新增
                //新增成功
                if(data.status == 0) {
                    $("#progressModal h4").text("添加模板成功");
                    $("#progressModal .progress-bar").get(0).style.width = "100%";
                    setTimeout(function(){$("#progressModal").modal('hide');}, 2000);
                    //增加图片至模板库
                    console.log(data.model_label);
                    addImgMuban(data.url, temp_json_model, data.id, data.model_register_time, data.image_size, data.label);

                    //模板数相应增加
                    var muban_num = parseInt($("#muban_num").text());
                    muban_num += 1;
                    $("#muban_num").text(muban_num.toString());
                    
                    $(".temp_save_muban").css("display", "none");
                    $(".temp_save_muban *").not("p").each(function(){
                        $(this).remove();
                    });

                }
                //新增失败
                else {
                    $("#progressModal h4").text("添加模板失败");
                    $("#progressModal .progress-bar").addClass("progress-bar-danger");
                    $("#progressModal .btn").get(0).disabled = false;
                }
            }
            else if(data.msg_id == 6) { //多图新增

            }

            //删除模板返回msg_id = 3
            else if(data.msg_id == 3) {
                //删除成功
                if(data.status == 0) {
                    $("#progressModal h4").text("删除模板成功");
                    $("#progressModal .progress-bar").get(0).style.width = "100%";
                    setTimeout(function(){$("#progressModal").modal('hide');}, 2000);


                    deleteObject(temp_click_jq_img.get(0).model_id);
                    //模板数相应减少
                    var muban_num = parseInt($("#muban_num").text());
                    muban_num -= 1;
                    $("#muban_num").text(muban_num.toString());
                }
                //删除失败
                else {
                    $("#progressModal h4").text("删除模板失败");
                    $("#progressModal .progress-bar").addClass("progress-bar-danger");
                    $("#progressModal .btn").get(0).disabled = false;
                }
            }

            //修改模板返回Msg_id = 4
            else if(data.msg_id == 4) {
                //修改成功
                if(data.status == 0) { //接受新的json_model，修改三种视图中的对应项及model_array

                    $("#progressModal h4").text("修改模板成功");
                    $("#progressModal .progress-bar").get(0).style.width = "100%";
                    setTimeout(function(){$("#progressModal").modal('hide');}, 2000);

                    //直接修改視圖中的src和json_model
                    // alert(edited_canvas_url);s
                    ChangeInfo(temp_click_jq_img.get(0).model_id, "model_url", edited_canvas_url);
                    ChangeInfo(temp_click_jq_img.get(0).model_id, "json_model", data.json_model);
                }
                //修改失败
                else {
                    $("#progressModal h4").text("修改模板失败");
                    $("#progressModal .progress-bar").addClass("progress-bar-danger");
                    $("#progressModal .btn").get(0).disabled = false;
                }
            }    
        }
    
        //fault.jsp
        else if(window.location.href.indexOf("fault.action") != -1){
            if(data.msg_id == 206) {
                for(var i = 0; i < data.fault_invoice.length; i++) {
                    addFaultInvoice(data.fault_invoice[i]);
                }
            }
        }

    };

    ws.onclose = function(evt) {
        tellConsole("close", 1);
    };

    ws.onopen = function(evt) {
        tellConsole("open", 1);
        if(window.location.href.indexOf("queue.action") != -1) {
            ws.send(JSON.stringify({
                code: "001", 
                console_status: 1  //queue.html对应1
            }));
        }
        else if(window.location.href.indexOf("console.action") != -1) {
            ws.send(JSON.stringify({
                code: "001", 
                console_status: 2  //model.html对应2
            }));
        }
        else if(window.location.href.indexOf("model.action") != -1) {
            ws.send(JSON.stringify({
                code: "001", 
                console_status: 3  //paint.html对应3
            }));
        }
        else if(window.location.href.indexOf("fault.action") != -1) {
            ws.send(JSON.stringify({
                code: "001", 
                console_status: 4  //fault.html对应4
            }));
        }
        else if(window.location.href.indexOf("log.action") != -1) {
            ws.send(JSON.stringify({
                code: "001", 
                console_status: 5  //rizhi.html对应5
            }));
        }
        else if(window.location.href.indexOf("user.action") != -1) {
            ws.send(JSON.stringify({
                code: "001", 
                console_status: 6  //grant.html对应6
            }));
        }
        else if(window.location.href.indexOf("ownedit.action") != -1) {
            ws.send(JSON.stringify({
                code: "001", 
                console_status: 7  //ownedit.html对应7
            }));
        }
    };
}

//根據調試等級控制控制台輸出
function tellConsole(content, level) {
    if(level <= console_level) {
        console.log(content);
    }
}

//画虚线
function drawDashLineHoritical(ctx, x1, y1, x2, y2, dashLength, offset){
    var dashLen = dashLength === undefined ? 5 : dashLength,
    xpos = x2 - x1, //得到横向的宽度;
    ypos = y2 - y1, //得到纵向的高度;
    numDashes = Math.floor(Math.sqrt(xpos * xpos + ypos * ypos) / dashLen); 
    //利用正切获取斜边的长度除以虚线长度，得到要分为多少段;
    ctx.beginPath();
    var last_x_overflow = false;
    for(var i=0; i<numDashes; i++){
        var temp_x = x1 + (xpos/numDashes) * i + offset;
        //判断是否溢出
        if(temp_x > x2) {
            temp_x = temp_x - x2 + x1;  
            if(i % 2 != 0) {
                if(last_x_overflow == false) {
                    ctx.lineTo(x2, y2);
                    ctx.moveTo(x1, y1); 
                }
                ctx.lineTo(temp_x, y1); 
            }
            else {
                ctx.moveTo(temp_x, y1);
                last_x_overflow = true;
            }
        }
        else if(temp_x < x1) {
            temp_x = x2 - (x1 - temp_x);
            if(i % 2 != 0) { //后置点溢出下界 前置点必然也溢出了下界
                ctx.lineTo(temp_x, y2); 
            }
            else { //前置点溢出下界
                ctx.moveTo(temp_x, y1);
                last_x_overflow = true;
            }
        }

        //没有溢出
        else {
            if(i % 2 === 0){
                last_x_overflow = false;
                ctx.moveTo(temp_x, y1 + (ypos/numDashes) * i); 
                //有了横向宽度和多少段，得出每一段是多长，起点 + 每段长度 * i = 要绘制的起点；
            }
            else{
                if(last_x_overflow == false) { //前置点和后置点都没有溢出 正常运作
                    ctx.lineTo(temp_x, y1 + (ypos/numDashes) * i);
                }
                else { //前置点溢出 后置点没溢出
                    ctx.lineTo(x2, y1);
                    ctx.moveTo(x1, y1);
                    ctx.lineTo(temp_x, y1);
                }
            }   
        }
    }
    ctx.closePath();
    ctx.stroke();
}   

function drawDashLineVertical(ctx, x1, y1, x2, y2, dashLength, offset){
    var dashLen = dashLength === undefined ? 5 : dashLength,
    xpos = x2 - x1, //得到横向的宽度;
    ypos = y2 - y1, //得到纵向的高度;
    numDashes = Math.floor(Math.sqrt(xpos * xpos + ypos * ypos) / dashLen); 
    //利用正切获取斜边的长度除以虚线长度，得到要分为多少段;
    ctx.beginPath();
    var last_y_overflow = false;
    for(var i=0; i<numDashes; i++){
        var temp_y = y1 + (ypos/numDashes) * i + offset;
        //判断是否溢出
        if(temp_y > y2) {
            temp_y = temp_y - y2 + y1;
            if(i % 2 != 0) {
                if(last_y_overflow == false) {
                    ctx.lineTo(x2, y2);
                    ctx.moveTo(x1, y1);
                }           
                ctx.lineTo(x1, temp_y);
            }
            else {
                ctx.moveTo(x1, temp_y);
                last_y_overflow = true;
            }
        }
        else if(temp_y < y1) {
            temp_y = y2 - (y1 - temp_y); 
            if(i % 2 != 0) { //后置点溢出下界 前置点必然也溢出了下界
                ctx.lineTo(x2, temp_y);
            }
            else { //前置点溢出下界
                ctx.moveTo(x1, temp_y);
                last_y_overflow = true;
            }
        }

        else {
            if(i % 2 === 0){
                last_y_overflow = false;
                ctx.moveTo(x1 + (xpos/numDashes) * i, temp_y); 
                //有了横向宽度和多少段，得出每一段是多长，起点 + 每段长度 * i = 要绘制的起点；
            }
            else{
                if(last_y_overflow == false) { //前置点和后置点都没有溢出 正常运作
                    ctx.lineTo(x1 + (xpos/numDashes) * i, temp_y);
                }
                else { //前置点溢出 后置点没溢出
                    ctx.lineTo(x1, y2);
                    ctx.moveTo(x1, y1);
                    ctx.lineTo(x1, temp_y);
                }
                
            }   
        }
    }
    ctx.closePath();
    ctx.stroke();
}

//画矩形滚动虚线
function strokeDashRect(ctx, x1, y1, x2, y2, dashLength, imageData) {

    //初始状态
    drawDashLineHoritical(ctx, x1, y1, x2, y1, dashLength, 0);
    drawDashLineHoritical(ctx, x1, y2, x2, y2, dashLength, 0);
    drawDashLineVertical(ctx, x1, y1, x1, y2, dashLength, 0);
    drawDashLineVertical(ctx, x2, y1, x2, y2, dashLength, 0);

    //轮转
    var offset = 0;
    rect_slide_interval = setInterval(function(){
        offset = (offset + dashLength/2) % (dashLength*2);
        //渲染每次保持不变的东西
        ctx.clearRect(x1-1, y1-1, x2-x1+2, y2-y1+2);
        ctx.putImageData(imageData, x1, y1);
        //cxt.putImageData(temp_imageData, position.convert_x, position.convert_y);
        //刷新以下虚线
        drawDashLineHoritical(ctx, x1, y1, x2, y1, dashLength, offset);
        drawDashLineHoritical(ctx, x1, y2, x2, y2, dashLength, -offset);
        drawDashLineVertical(ctx, x1, y1, x1, y2, dashLength, -offset);
        drawDashLineVertical(ctx, x2, y1, x2, y2, dashLength, offset);

    // var x = 100, y = 50;
    }, 100);
}

//使模态框垂直居中
function modelMiddle(modal_jq){
    modal_jq.on('show.bs.modal', function () {
        var srceen_height = document.documentElement.clientHeight;
        // console.log(modal_jq.find(".modal-content").eq(0).get(0).offsetHeight);
        // var div_height = modal_jq.children("div").eq(0).get(0).offsetHeight;
        var margint_top = srceen_height/2 - 100;
        modal_jq.css("marginTop", margint_top+"px");
    })
}

//确定main_content内容区的宽度
function adjustMainContent() {
    var main_content_width = document.documentElement.clientWidth - $("aside").get(0).offsetWidth - 10;
    $(".main_content").css("width", main_content_width + "px");
}

//获取当前selected的页面
function getSelectedPage() {
    var href = window.location.href;
    $(".aside_nav_list-item").each(function() {
        if(href.indexOf($(this).data("permission")) != -1) {
            $(this).removeClass("nav_disabled");
            $(this).addClass("selected");
        }
    })
}

//判断用户拥有的权限
function justifyUserGrant(user_json) {
    $(".own_user_name").text(user_json.user_name);
    var permission_array = user_json.permissions;
    user_id = user_json.user_id;
    //判断导航栏链接的权限
    $(".aside_nav_list-item").each(function() {
        if($(this).hasClass("nav_disabled")) {
            for(var i = 0; i < permission_array.length; i++) {
                if(permission_array[i].permission_name.split("-")[0] == "group" && $(this).data("permission") == "user") {
                    $(this).attr("href", $(this).data("permission")+".action");
                    $(this).css("cursor", "pointer");
                    $(this).removeClass("nav_disabled");
                    continue;
                }
                if(permission_array[i].permission_name.split("-")[0] == $(this).data("permission")) {
                    $(this).attr("href", $(this).data("permission")+".action");
                    $(this).css("cursor", "pointer");
                    $(this).removeClass("nav_disabled");
                }
            }     
        }      
    })  
}

//判断用户特定的读写权限
function justifyRW(user_json) {
    var cur_page_permission = $(".aside_nav_list-item.selected").data("permission");

    //判断一些“写”的权限能否操作
    if($("[data-write='true']").get(0) != undefined) {
        var permission_array = user_json.permissions;
        for(var i = 0; i < permission_array.length; i++) {
            if(permission_array[i].permission_name.split("-")[0] == cur_page_permission) {
                if(permission_array[i].permission_name.split("-")[1] == "rw") {
                    $("[data-write='true']").get(0).disabled = false;
                }
            }
        }    
    }
}

//改变勾选图标
function toggleChecked(click_jq) {

    if(click_jq.hasClass("fa-check-square-o")) {
        click_jq.removeClass("fa-check-square-o");
        click_jq.addClass('fa-square-o');
        // alert("here");
    }
    else {
        click_jq.removeClass("fa-square-o");
        click_jq.addClass('fa-check-square-o');
    }
}

//权限对应
function grantSwitch(table_jq, permissions, j) {
    // console.log($(".grant_table .table_display_row:last-child .table_display_td:last-child"));
    table_jq.prop("grant_type", j);
    for(var m = 0; m < permissions.length; m++) {
        var temp_permission = permissions[m];
        if(temp_permission.permission_id == j) {
            toggleChecked(table_jq.children());
            break;
        }
    }
}

//模态框垂直居中
function ModalVerticalAlign(modal_jq) {
    // console.log(document.documentElement.clientHeight + " " + modal_jq.offsetHeight);
    // console.log("init here " + parseFloat($(modal_jq).width()) + " " + document.documentElement.clientWidth);
    // console.log("init here " + parseFloat($(modal_jq).height()) + " " + document.documentElement.clientHeight);
    if(parseFloat($(modal_jq).width()) > document.documentElement.clientWidth){
        $(modal_jq).css("width", document.documentElement.clientWidth+"px");
        $(modal_jq).find(".modal-content").css("width", document.documentElement.clientWidth+"px");
    } else {

    }

    if(parseFloat($(modal_jq).height()) > document.documentElement.clientHeight){
        $(modal_jq).find(".modal-content").css("height", document.documentElement.clientHeight+"px");
    } else {
        $(modal_jq).find(".modal-content").css("height", parseFloat($(modal_jq).height())+"px");
    }

    if(modal_jq.offsetHeight < document.documentElement.clientHeight) {
        var margin_top = parseFloat((document.documentElement.clientHeight - modal_jq.offsetHeight)/2);
        $(modal_jq).css("marginTop", margin_top+"px");
    } else {
        $(modal_jq).css("marginTop", "0px");
    }
}

//使页面模态框都居中
function AllModalVerticalAlign() {
    if($(".modal").get(0) != undefined) {
        $(".modal").each(function() {
            $(this).on("show.bs.modal", function() {
                // console.log("modal_here");
                $(this).css("display", "table");
                $(this).find(".modal-content").css("overflow", "auto");
                ModalVerticalAlign($(this).get(0));
            })
        })
    }
}

$(document).ready(function() {
    // adjustMainContent();
    loadxml("config.xml");
    getSelectedPage();
    connectEndpoint();
    AllModalVerticalAlign();

    //设置main_content的Min_height
    $(".main_content").css("minHeight", (document.documentElement.clientHeight-150)+"px");
})