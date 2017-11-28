<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="com.alibaba.fastjson.JSON"%>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <meta charset="utf-8" />
</head>
<body>
    <aside>
        <div class="aside_nav_list">
            <a href="" class="aside_nav_list-item nav_disabled" data-permission="queue">
                <i class="fa fa-bar-chart aside_nav_list-item-icon"></i>
                <span>缓冲队列</span>
            </a>
            <a href=""  class="aside_nav_list-item nav_disabled" data-permission="console">
                <i class="fa fa-television aside_nav_list-item-icon"></i>
                <span>监控显示</span>
            </a>
            <a href="" class="aside_nav_list-item nav_disabled" data-permission="model">
                <i class="fa fa-clipboard aside_nav_list-item-icon"></i>
                <span>模板库</span>
            </a>
            <a href="" class="aside_nav_list-item nav_disabled" data-permission="fault">
                <i class="fa fa-times-circle-o aside_nav_list-item-icon"></i>
                <span>报错发票
                    <span class="badge fault_num" style="margin-left: 10px;"></span>
                </span>
            </a>
            <a href="" class="aside_nav_list-item nav_disabled" data-permission="log">
                <i class="fa fa-tasks aside_nav_list-item-icon" aria-hidden="true"></i>
                <span>日志查询</span>
            </a>
            <a href="" class="aside_nav_list-item nav_disabled" data-permission="user">
                <i class="fa fa-user-o aside_nav_list-item-icon" aria-hidden="true"></i>
                <span>用户管理</span>
            </a>
            <a href="${pageContext.request.contextPath}/ownedit.action" class="aside_nav_list-item" data-permission="ownedit">
                <i class="fa fa-cog aside_nav_list-item-icon" aria-hidden="true"></i>
                <span>个人设置</span>
            </a>
        </div>
    </aside>
</body>
</html>
