var ip2; //host_ip
var wsuri; //websocket_url
var invoice_height, invoice_width;
var console_level; //控制台輸出等級 值越大輸出越多東西
var ws = null;

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
            invoice_width = xml_data.getElementsByTagName("invoice_width")[0].innerHTML;
            invoice_height = xml_data.getElementsByTagName("invoice_height")[0].innerHTML;
            console_level = xml_data.getElementsByTagName("console_level")[0].innerHTML;
			wsuri = "ws://" + ip2 + "/invoice/webSocketServer.action";
			tellConsole(wsuri, 2);
		},
		error : function() {
			alert("读取配置文件失败，稍后重试");
		}
	})
}

//連接websocket
function connectEndpoint(){

    ws = new ReconnectingWebSocket(wsuri);
    ws.reconnectInterval = 5000;
    ws.timeoutInterval = 10000;
    ws.maxReconnectAttempts = 20;
    var img_list = [];
    var nth_area = 1; //记录这是识别的第几个区域
    var cur_text_x, cur_text_y;

    ws.onmessage = function(evt) {
        //alert(evt.data);
        tellConsole(evt.data, 3);
        var data = JSON.parse(evt.data);
        //show.html
        if(window.location.href.indexOf("show.html") != -1) {
            if(data.msg_id == 203 || data.msg_id == 202) {
                //copy_fapiao获取背景图, show_fapiao绘制图片
                $("#copy_fapiao").css("backgroundImage", "url(" + data.img_str + ")");
                $("#show_fapiao").css("backgroundImage", "url('')");
                var temp_img = new Image();
                temp_img.onload = function(){
                    cxt.clearRect(0, 0, parseFloat($("#show_fapiao").width()), parseFloat($("#show_fapiao").height()));
                    cxt.drawImage(temp_img, 0, 0, parseFloat($("#show_fapiao").width()), parseFloat($("#show_fapiao").height()));
                    $(".muban_info").text("（正在搜索可用模板）");
                    $("#muban").get(0).src = "pic/search_placehold.png";

                    if(data.msg_id == 203) {
                        $("#user_name").text($("#user_name").text() + data.user_name);
                        $("#action_start_time").text($("#action_start_time").text() + data.action_start_time);
                        $("#company_name").text($("#company_name").text() + data.company_name);
                    }
                
                    else if(data.msg_id == 202) {
                        if(data.user_name != undefined) {
                            $("#user_name").text($("#user_name").text() + data.user_name);
                            $("#action_start_time").text($("#action_start_time").text() + data.action_start_time);
                            $("#company_name").text($("#company_name").text() + data.company_name);    
                        }
                        tellConsole(data.region_list, 2);
                        for(var i = 0; i < data.region_list.length; i++) {
                            var data1 = JSON.parse(data.region_list[i]);
                            $("td.area_hd").each(function() {
                                if($(this).text() == data1.pos_id) {
                                    $(this).next().text(data1.ocr_result);
                                    $(this).next().next().text(data1.probability);
                                }
                            });
                            
                        }
                    }
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
                cxt.strokeRect(position.convert_x, position.convert_y, position.convert_w, position.convert_h);

                if(position.convert_y < 20) cur_text_y = position.convert_y + 5 + position.convert_h;
                else cur_text_y = position.convert_y - 10;

                cur_text_x = position.convert_x;    
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
            }
            else if(data.msg_id == 1 && data.status == 0) {
                tellConsole(data["金额"], 3);
                $("td.area_hd").each(function() {
                    if($(this).text() == "金额") {
                        if($(this).next().text() == "") {
                            $(this).next().text(data["金额"]);
                            if(data["金额prob"] > data["中文金额prob"]) {
                                $(this).next().next().text(data["金额prob"]); 
                                if(data["金额prob"] >= 0.9) {
                                    $(this).parent().addClass("info_blue");
                                }
                                else {
                                    $(this).parent().addClass("info_red");
                                }
                            }
                            else {
                                $(this).next().next().text(data["中文金额prob"]); 
                                if(data["中文金额prob"] >= 0.9) {
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
                    $("#show_fapiao").css("backgroundImage", "url('pic/shibie_placehold.png')");
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
        

        // queue.html
        else if(window.location.href.indexOf("queue.html") != -1){
            //增加数目
            if(data.msg_id == 201) {
                var num = parseInt($("#waiting_num").text());
                console.log("new_recognize's length:" + data.new_recognize.length)
                for(var i = 0; i < data.new_recognize.length; i++){
                    $(".waiting_list").append("<img src=\"pic/rectangle.png\" class=\"rect_img\" />");
                    var opacity_ = parseFloat(data.new_recognize[i].image_size / 500) > 1 ? 1 : parseFloat(data.new_recognize[i].image_size / 500); 
                    $(".waiting_list img:last-child").get(0).base_json = data.new_recognize[i];
                    $(".waiting_list img:last-child").css("opacity", opacity_);
                    $(".waiting_list img:last-child").click(function() {
                        $("#showWaiting").modal('show');
                        var temp_json = $(this).get(0).base_json;
                        tellConsole($(this).get(0).base_json, 3);
                        $("#user_info").text(temp_json.user_name);
                        $("#time_info").text(temp_json.action_start_time);
                        $("#img_info").get(0).src = temp_json.url;
                    })
                    num++;
                }
                $("#waiting_num").text(num.toString());
            }

            //减少数目
            else if(data.msg_id == 1) {
                $(".waiting_list img").first().remove();
                var num = parseInt($("#waiting_num").text());
                num--;
                $("#waiting_num").text(num.toString());
            }    
        }
        
        //paint.html

        else if(window.location.href.indexOf("paint.html") != -1){
            if(data.msg_id == 2) {
                //新增成功
                if(data.status == 0) {
                    $("#progressModal h4").text("添加模板成功");
                    $("#progressModal .progress-bar").get(0).style.width = "100%";
                    setTimeout(function(){$("#progressModal").modal('hide');}, 2000);
                    //增加图片至模板库
                    addImgMuban(data.url, temp_json_model, data.id, data.model_register_time, data.image_size, data.model_label);

                    //模板数相应增加
                    var muban_num = parseInt($("#muban_num").text());
                    muban_num += 1;
                    $("#muban_num").text(muban_num.toString());
                    
                }
                //新增失败
                else {
                    $("#progressModal h4").text("添加模板失败");
                    $("#progressModal .progress-bar").addClass("progress-bar-danger");
                    $("#progressModal .btn").get(0).disabled = false;
                }
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
        
    };

    ws.onclose = function(evt) {
        tellConsole("close", 1);
    };

    ws.onopen = function(evt) {
        tellConsole("open", 1);
    };
}

//根據調試等級控制控制台輸出
function tellConsole(content, level) {
    if(level <= console_level) {
        console.log(content);
    }
}

$(document).ready(function() {
    loadxml("config.xml");
    connectEndpoint();
})