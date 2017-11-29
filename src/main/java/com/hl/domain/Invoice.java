package com.hl.domain;

public class Invoice {
	// 识别之后的发票信息
	private Integer action_id;
	private Integer invoice_id;
	private Integer model_id;
	private String check_time;

	//识别出来的数据信息
	private String invoice_type;
	private String invoice_money;
	private String invoice_customer;
	private String invoice_code;
	private String invoice_date;
	private String invoice_time;
	private String invoice_detail;
	private String invoice_identity;
	private Integer invoice_region_num;

	// 图片相关信息
	private String invoice_url;// url_suffix
	private String url; // 网络url
	private String invoice_image_id;// 发送单位自己定义的图片id
	private String invoice_note;// 发送单位自己定义的备注
	private Integer image_size;
	private Integer invoice_status;
	// 生成uuid，用于排队
	private String uuid;
	// 识别完成的时间
	private String recognize_time;

	//识别区域信息
	private String region_list;
	//是否报错
	private Integer is_fault;
	
	// 前端需要的一些信息，后期补充
	private String action_time;// 发送时间
	private Integer user_id;
	private Integer company_id;
	private String user_name;
	private String company_name;
	private String img_str;

	// 标记其属于一次action里的第几张
	private Integer order;
	// 所属action识别的总数量
	private Integer recognize_num;

	public Integer getAction_id() {
		return action_id;
	}

	public String getRegion_list() {
		return region_list;
	}

	public void setRegion_list(String region_list) {
		this.region_list = region_list;
	}

	public Integer getIs_fault() {
		return is_fault;
	}

	public void setIs_fault(Integer is_fault) {
		this.is_fault = is_fault;
	}

	public void setAction_id(Integer action_id) {
		this.action_id = action_id;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(Integer invoice_id) {
		this.invoice_id = invoice_id;
	}

	public Integer getModel_id() {
		return model_id;
	}

	public void setModel_id(Integer model_id) {
		this.model_id = model_id;
	}

	public String getCheck_time() {
		return check_time;
	}

	public void setCheck_time(String check_time) {
		this.check_time = check_time;
	}

	public String getInvoice_type() {
		return invoice_type;
	}

	public void setInvoice_type(String invoice_type) {
		this.invoice_type = invoice_type;
	}

	public String getInvoice_money() {
		return invoice_money;
	}

	public void setInvoice_money(String invoice_money) {
		this.invoice_money = invoice_money;
	}

	public String getInvoice_customer() {
		return invoice_customer;
	}

	public void setInvoice_customer(String invoice_customer) {
		this.invoice_customer = invoice_customer;
	}

	public String getInvoice_code() {
		return invoice_code;
	}

	public void setInvoice_code(String invoice_code) {
		this.invoice_code = invoice_code;
	}

	public String getInvoice_date() {
		return invoice_date;
	}

	public void setInvoice_date(String invoice_date) {
		this.invoice_date = invoice_date;
	}

	public String getInvoice_time() {
		return invoice_time;
	}

	public void setInvoice_time(String invoice_time) {
		this.invoice_time = invoice_time;
	}

	public String getInvoice_detail() {
		return invoice_detail;
	}

	public void setInvoice_detail(String invoice_detail) {
		this.invoice_detail = invoice_detail;
	}

	public String getInvoice_identity() {
		return invoice_identity;
	}

	public void setInvoice_identity(String invoice_identity) {
		this.invoice_identity = invoice_identity;
	}

	public Integer getInvoice_region_num() {
		return invoice_region_num;
	}

	public void setInvoice_region_num(Integer invoice_region_num) {
		this.invoice_region_num = invoice_region_num;
	}

	public String getInvoice_url() {
		return invoice_url;
	}

	public void setInvoice_url(String invoice_url) {
		this.invoice_url = invoice_url;
	}

	public String getInvoice_image_id() {
		return invoice_image_id;
	}

	public void setInvoice_image_id(String invoice_image_id) {
		this.invoice_image_id = invoice_image_id;
	}

	public String getInvoice_note() {
		return invoice_note;
	}

	public void setInvoice_note(String invoice_note) {
		this.invoice_note = invoice_note;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getImage_size() {
		return image_size;
	}

	public void setImage_size(Integer image_size) {
		this.image_size = image_size;
	}

	public Integer getInvoice_status() {
		return invoice_status;
	}

	public void setInvoice_status(Integer invoice_status) {
		this.invoice_status = invoice_status;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getRecognize_time() {
		return recognize_time;
	}

	public void setRecognize_time(String recognize_time) {
		this.recognize_time = recognize_time;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getCompany_name() {
		return company_name;
	}

	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}

	public Integer getRecognize_num() {
		return recognize_num;
	}

	public void setRecognize_num(Integer recognize_num) {
		this.recognize_num = recognize_num;
	}

	public Integer getUser_id() {
		return user_id;
	}

	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}

	public Integer getCompany_id() {
		return company_id;
	}

	public void setCompany_id(Integer company_id) {
		this.company_id = company_id;
	}

	public String getImg_str() {
		return img_str;
	}

	public void setImg_str(String img_str) {
		this.img_str = img_str;
	}

	public String getAction_time() {
		return action_time;
	}

	public void setAction_time(String action_time) {
		this.action_time = action_time;
	}

}
