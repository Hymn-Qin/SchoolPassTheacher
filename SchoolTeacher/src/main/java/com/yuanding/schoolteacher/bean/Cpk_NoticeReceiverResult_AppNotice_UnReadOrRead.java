package com.yuanding.schoolteacher.bean;

import java.util.List;


/**
 * 身边-》通知-》 app通知详情-》图文接收结果实体类
 * 
 * @author Administrator
 * 
 */
public class Cpk_NoticeReceiverResult_AppNotice_UnReadOrRead {

	int status;
	String msg;
	int time; 
	
	/**
	 * 未读时存在
	 */
	int unread_num;
	int unactive_num;
	
	/**
	 * 已读时存在
	 */
	int read_num;
	
	int allow_invite; //0 不允许邀请  1 允许邀请
	
	
	List<Cpk_NoticeReceiverResult_AppNotice_UserInfo> userlist;
	

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}


	public int getUnread_num() {
		return unread_num;
	}

	public void setUnread_num(int unread_num) {
		this.unread_num = unread_num;
	}

	public int getUnactive_num() {
		return unactive_num;
	}

	public void setUnactive_num(int unactive_num) {
		this.unactive_num = unactive_num;
	}

	public List<Cpk_NoticeReceiverResult_AppNotice_UserInfo> getUserlist() {
		return userlist;
	}

	public void setUserlist(
			List<Cpk_NoticeReceiverResult_AppNotice_UserInfo> userlist) {
		this.userlist = userlist;
	}
	
	public int getRead_num() {
		return read_num;
	}

	public void setRead_num(int read_num) {
		this.read_num = read_num;
	}

	public int getAllow_invite() {
		return allow_invite;
	}

	public void setAllow_invite(int allow_invite) {
		this.allow_invite = allow_invite;
	}
}
