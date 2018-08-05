package com.yuanding.schoolteacher.bean;

import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月27日 下午2:39:33 通知详情
 */
public class Cpk_Side_Notice_Detail {
	
	private long create_time;
	private String message_id;
//	private String read_num;
	private String bg_img;
	private String title;
	private String content;
	private String reply_num;
	private String send_count;
	private String app_msg_sign;
	private String photo_url;
	private String organ_str;
	private String content_type;
	private long time;
	
	//图文详情-已读 未读数量
	private int unread_num;
	private int read_num;
	
	//短信详情-发送中 发送成功 发送失败
	int msg_status;
	String status_text;
	String status_text_color;
	int sms_failed;
	int sms_success;
	int sms_loading;
	String file_name;
	String file_size;
	String file_ext;
	String file_url;
	int is_appendix;

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public String getFile_size() {
		return file_size;
	}

	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	public String getFile_ext() {
		return file_ext;
	}

	public void setFile_ext(String file_ext) {
		this.file_ext = file_ext;
	}

	public String getFile_url() {
		return file_url;
	}

	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}

	public int getIs_appendix() {
		return is_appendix;
	}

	public void setIs_appendix(int is_appendix) {
		this.is_appendix = is_appendix;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public String getOrgan_str() {
		return organ_str;
	}

	public void setOrgan_str(String organ_str) {
		this.organ_str = organ_str;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	public String getApp_msg_sign() {
		return app_msg_sign;
	}

	public void setApp_msg_sign(String app_msg_sign) {
		this.app_msg_sign = app_msg_sign;
	}

	public String getSend_count() {
		return send_count;
	}

	public void setSend_count(String send_count) {
		this.send_count = send_count;
	}

	private List<Cpk_Side_Notice_Comment_detail> list;

	public long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}


//	public String getRead_num() {
//		return read_num;
//	}
//
//	public void setRead_num(String read_num) {
//		this.read_num = read_num;
//	}

	public String getBg_img() {
		return bg_img;
	}

	public void setBg_img(String bg_img) {
		this.bg_img = bg_img;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	

	public List<Cpk_Side_Notice_Comment_detail> getList() {
		return list;
	}

	public void setList(List<Cpk_Side_Notice_Comment_detail> list) {
		this.list = list;
	}

	public String getMessage_id() {
		return message_id;
	}

	public int getMsg_status() {
		return msg_status;
	}

	public void setMsg_status(int msg_status) {
		this.msg_status = msg_status;
	}

	public String getStatus_text() {
		return status_text;
	}

	public void setStatus_text(String status_text) {
		this.status_text = status_text;
	}

	public String getStatus_text_color() {
		return status_text_color;
	}

	public void setStatus_text_color(String status_text_color) {
		this.status_text_color = status_text_color;
	}

	public int getSms_failed() {
		return sms_failed;
	}

	public void setSms_failed(int sms_failed) {
		this.sms_failed = sms_failed;
	}

	public int getSms_success() {
		return sms_success;
	}

	public void setSms_success(int sms_success) {
		this.sms_success = sms_success;
	}

	public int getSms_loading() {
		return sms_loading;
	}

	public void setSms_loading(int sms_loading) {
		this.sms_loading = sms_loading;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getReply_num() {
		return reply_num;
	}

	public void setReply_num(String reply_num) {
		this.reply_num = reply_num;
	}

	public int getRead_num() {
		return read_num;
	}

	public void setRead_num(int read_num) {
		this.read_num = read_num;
	}

	public int getUnread_num() {
		return unread_num;
	}

	public void setUnread_num(int unread_num) {
		this.unread_num = unread_num;
	}

	

}
