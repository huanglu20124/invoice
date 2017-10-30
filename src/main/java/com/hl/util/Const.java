package com.hl.util;

public class Const {
	
//	public static final String LOCAL_IP  = "http://192.168.1.24:8080";
//	public static final String LOCAL_IP  = "http://172.18.92.209:8080";
	public static final String LOCAL_IP  = "http://192.168.191.2:8080";
	
//	public static final String REMOTE_IP = "192.168.137.221";
	//public static final String REMOTE_IP = "192.168.1.14";
	public static final String PORT = "8889";
	
	//公司或组织表
	public static final String COMPANY_ID         = "company_id";
	public static final String COMPANY_NAME       = "company_name";
	public static final String COMPANY_AUTH       = "company_auth";
	public static final String COMPANY_REGISTER_TIME = "company_register_time";
	public static final String COMPANY_DESCRIPTION="company_description";
	public static final String COMPANY_USER_NUM   = "company_user_num";
	//用户或管理员表
	public static final String USER_ID            = "user_id";
	public static final String USER_NAME          = "user_name";
	public static final String USER_PASSWORD      = "user_password";
	public static final String USER_REGISTER_TIME = "user_register_time";
	public static final String USER_TYPE          = "user_type";
	//用户权限管理
	public static final String USER_AUTH          = "user_auth";
	public static final String MODEL_AUTH         = "model_auth";
	public static final String INVOICE_AUTH       = "invoice_auth";
	public static final String ACTION_AUTH        = "action_auth";
	
	//发票模板表
	public static final String MODEL_ID           = "model_id";
	public static final String JSON_MODEL         = "json_model";
	public static final String MODEL_REGISTER_TIME="model_register_time";
	public static final String MODEL_SUCCESS_COUNTER="model_success_counter";
	public static final String MODEL_URL          = "model_url";
	public static final String MODEL_LABEL        = "model_label";
	public static final String IMAGE_SIZE         = "image_size";
	//行为表
	public static final String ACTION_ID          = "action_id";
	public static final String STATUS             = "status";
	public static final String ACTION_START_TIME  = "action_start_time";
	public static final String ACTION_RUN_TIME    = "action_run_time";
	public static final String ACTION_END_TIME    = "action_end_time";
	
	//发票表
	public static final String INVOICE_ID         = "invoice_id";
	public static final String IS_CHECK           = "is_check";
	public static final String CHECK_TIME         = "check_time";
	public static final String INVOICE_TYPE       = "invoice_type";
	public static final String INVOICE_MONEY      = "invoice_money";
	public static final String INVOICE_CUSTOMER   = "invoice_customer";
	public static final String INVOICE_CODE       = "invoice_code";
	public static final String INVOICE_DATE       = "invoice_date";
	public static final String INVOICE_TIME       = "invoice_time";
	public static final String INVOICE_DETAIL     = "invoice_detail";
	public static final String INVOICE_IDENTITY   = "invoice_identity";
	public static final String INVOICE_REGION_NUM = "invoice_region_num";
	public static final String INVOICE_URL        = "invoice_url";
	//服务端返回给前端的信息
	public static final String ERR = "err";
	public static final String SUCCESS = "success";
	
	//redis三大队列
	public static final String RECOGNIZE_WAIT      = "recognize_wait"; //等待队列,只有操作队列为空时，才能执行这个
	public static final String EXCEPTION_WAIT      = "exception_wait"; //异常队列,识别失败的发票
	public static final String MANAGE_WAIT         = "manage_wait";     //操作队列，优先级最高,模板信息的增删改
	
	//用来通知切换任务线程的
	public static final String THREAD_MSG          = "thread_msg";  //上锁的对象

	public static final String NEW_RECOGNIZE       = "new_recognize";//新增的需要识别的发票
	public static final String NEW_EXCEPTION       = "new_exception";//新增的异常发票
	
	
	//与算法服务器通讯有关
	public static final String MSG_ID              = "msg_id";
	public static final String URL                 = "url";
	
	//接受Base64图片有关
	public static final String IMG_STR      = "img_str";
	public static final String FILE_NAME    = "file_name";
	
	//与web前端通讯有关
	public static final String MODEL_LIST          = "model_list";
	
	//记录发票识别过程
	public static final String RECOGNIZE_PROCESS   = "recognize_process";
	//之前识别过的信息
	public static final String REGION_LIST         = "region_list";
	
	//客户端发给服务器的
	public static final String CODE                = "code";
	
	//图片保存所用，代表图片所在的文件夹+文件名形成的后缀
	public static final String URL_SUFFIX          = "url_suffix";
	
	//一分钟的吞吐量
	public static final String MINUTE_SUM          = "minute_sum";
	
	//识别延时
	public static final String DELEY               = "delay";
}
