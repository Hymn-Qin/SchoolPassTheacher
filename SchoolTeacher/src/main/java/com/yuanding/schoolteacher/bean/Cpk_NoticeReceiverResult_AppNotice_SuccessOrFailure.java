package com.yuanding.schoolteacher.bean;

import java.util.List;


/**
 * 身边-》通知-》 app通知详情-》短信接收结果实体类
 * 
 * @author Administrator
 * 
 */
public class Cpk_NoticeReceiverResult_AppNotice_SuccessOrFailure {

	int status;
	String msg;
	int time; 
	
	
	int sms_count;//短信接收成功、app接收成功
	int app_num; //未安装App、短信接收成功
	
	int allow_invite; //0 不允许 1 允许
	
	
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

	public List<Cpk_NoticeReceiverResult_AppNotice_UserInfo> getUserlist() {
		return userlist;
	}

	public void setUserlist(
			List<Cpk_NoticeReceiverResult_AppNotice_UserInfo> userlist) {
		this.userlist = userlist;
	}

	public int getSms_count() {
		return sms_count;
	}

	public void setSms_count(int sms_count) {
		this.sms_count = sms_count;
	}

	public int getApp_num() {
		return app_num;
	}

	public void setApp_num(int app_num) {
		this.app_num = app_num;
	}

	public int getAllow_invite() {
		return allow_invite;
	}

	public void setAllow_invite(int allow_invite) {
		this.allow_invite = allow_invite;
	}
	
}
