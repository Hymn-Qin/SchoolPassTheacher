package com.yuanding.schoolteacher.bean;

public class Cpk_Search_Recipient {
public String true_name;
public String phone;
public String user_id;
public String photo_url;
public String type;
public String class_id;
private String uniqid;//等同于TargetId
private boolean check;
public boolean isCheck() {
	return check;
}
public void setCheck(boolean check) {
	this.check = check;
}
public String getUniqid() {
	return uniqid;
}
public void setUniqid(String uniqid) {
	this.uniqid = uniqid;
}
public String getTrue_name() {
	return true_name;
}
public void setTrue_name(String true_name) {
	this.true_name = true_name;
}
public String getPhone() {
	return phone;
}
public void setPhone(String phone) {
	this.phone = phone;
}
public String getUser_id() {
	return user_id;
}
public void setUser_id(String user_id) {
	this.user_id = user_id;
}
public String getPhoto_url() {
	return photo_url;
}
public void setPhoto_url(String photo_url) {
	this.photo_url = photo_url;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getClass_id() {
	return class_id;
}
public void setClass_id(String class_id) {
	this.class_id = class_id;
}
}
