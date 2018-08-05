package com.yuanding.schoolteacher.bean;

/**
 * 身边通知列表
 * 
 */
public class Cpk_Side_Notice_List {
	private String create_time;
	private String read_num;//已读
	private String reply_num;
	private String title;
	private String bg_img;
	private String desc;
	private String message_id;
	private String type;
	private String receive_count;
    private String status_text;
    private String status_text_color;
    
	public String getReceive_count() {
		return receive_count;
	}

	public void setReceive_count(String receive_count) {
		this.receive_count = receive_count;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getApp_msg_sign() {
		return app_msg_sign;
	}

	public void setApp_msg_sign(String app_msg_sign) {
		this.app_msg_sign = app_msg_sign;
	}

	public String getFixed_time() {
		return fixed_time;
	}

	public void setFixed_time(String fixed_time) {
		this.fixed_time = fixed_time;
	}

	public String getSend_count() {
		return send_count;
	}

	public void setSend_count(String send_count) {
		this.send_count = send_count;
	}

	private String app_msg_sign;
	private String fixed_time;
	private String send_count;
	private String unread_num;//未读
	
	private int is_receipt; //是否回执 0 否 1 是
	private int unreceipt_num; //未回执数量
	
	private int receive_num;//	短信已收数量
	private int unreceive_num;//	短信未收
	private int is_appendix;
//	private String file_name;
//	private String file_size;
//	private String file_ext;
//	private String file_url;

//	public String getFile_name() {
//		return file_name;
//	}
//
//	public void setFile_name(String file_name) {
//		this.file_name = file_name;
//	}
//
//	public String getFile_size() {
//		return file_size;
//	}
//
//	public void setFile_size(String file_size) {
//		this.file_size = file_size;
//	}
//
//	public String getFile_ext() {
//		return file_ext;
//	}
//
//	public void setFile_ext(String file_ext) {
//		this.file_ext = file_ext;
//	}
//
//	public String getFile_url() {
//		return file_url;
//	}
//
//	public void setFile_url(String file_url) {
//		this.file_url = file_url;
//	}

	public int getIs_appendix() {
		return is_appendix;
	}

	public void setIs_appendix(int is_appendix) {
		this.is_appendix = is_appendix;
	}

	public String getBg_img() {
		return bg_img;
	}

	public void setBg_img(String bg_img) {
		this.bg_img = bg_img;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}

	public String getRead_num() {
		return read_num;
	}

	public void setRead_num(String read_num) {
		this.read_num = read_num;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReply_num() {
		return reply_num;
	}

	public void setReply_num(String reply_num) {
		this.reply_num = reply_num;
	}

	public String getUnread_num() {
		return unread_num;
	}

	public void setUnread_num(String unread_num) {
		this.unread_num = unread_num;
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

	public int getUnreceipt_num() {
		return unreceipt_num;
	}

	public void setUnreceipt_num(int unreceipt_num) {
		this.unreceipt_num = unreceipt_num;
	}

	public int getIs_receipt() {
		return is_receipt;
	}

	public void setIs_receipt(int is_receipt) {
		this.is_receipt = is_receipt;
	}

	public int getReceive_num() {
		return receive_num;
	}

	public void setReceive_num(int receive_num) {
		this.receive_num = receive_num;
	}

	public int getUnreceive_num() {
		return unreceive_num;
	}

	public void setUnreceive_num(int unreceive_num) {
		this.unreceive_num = unreceive_num;
	}

}
