package com.yuanding.schoolteacher.bean;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月27日 下午2:00:54
 * 通知评论列表
 */
public class Cpk_Side_Notice_Comment_detail {

	//活动
	private String message_id;
	private String user_id;
	private String create_time;
	private String content;
	private String username;
	private String reply_id;
	public String getReply_id() {
		return reply_id;
	}

	public void setReply_id(String reply_id) {
		this.reply_id = reply_id;
	}

	private String photo_url;
	private String uniqid;
	private String name;
	private String reply_comment_list;
	public String getReply_comment_list() {
		return reply_comment_list;
	}

	public void setReply_comment_list(String reply_comment_list) {
		this.reply_comment_list = reply_comment_list;
	}

	private String reply_comment_info;
	public String getReply_comment_info() {
		return reply_comment_info;
	}

	public void setReply_comment_info(String reply_comment_info) {
		this.reply_comment_info = reply_comment_info;
	}

	public String getPhoto_url() {
		return photo_url;
	}

	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMessage_id() {
		return message_id;
	}

	public void setMessage_id(String message_id) {
		this.message_id = message_id;
	}


	public String getUniqid() {
		return uniqid;
	}

	public void setUniqid(String uniqid) {
		this.uniqid = uniqid;
	}
	
	

}
