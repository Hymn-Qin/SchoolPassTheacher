package com.yuanding.schoolteacher.bean;

/**
 * 通知接收结果页面（身边-》通知列表--》通知详情--》通知接收结果->学生实体类
 * @author Administrator
 */
public class Cpk_NoticeReceiverResult_AppNotice_UserInfo {

	String true_name;
	String photo_url;
	int status; //2 : 未安装app  ，除2以外都是未读
	String uniqid;
	public String getTrue_name() {
		return true_name;
	}
	public void setTrue_name(String true_name) {
		this.true_name = true_name;
	}
	public String getPhoto_url() {
		return photo_url;
	}
	public void setPhoto_url(String photo_url) {
		this.photo_url = photo_url;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getUniqid() {
		return uniqid;
	}
	public void setUniqid(String uniqid) {
		this.uniqid = uniqid;
	}
}
