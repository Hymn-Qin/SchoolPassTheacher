package com.yuanding.schoolteacher.bean;

public class Cpk_Attence_Group {
public String group_id;
public String user_id;
public String school_id;
public String group_name;
public String group_desc;
public String group_num;
private boolean check;
public boolean isCheck() {
	return check;
}

public void setCheck(boolean check) {
	this.check = check;
}
public String getGroup_id() {
	return group_id;
}
public void setGroup_id(String group_id) {
	this.group_id = group_id;
}
public String getUser_id() {
	return user_id;
}
public void setUser_id(String user_id) {
	this.user_id = user_id;
}
public String getSchool_id() {
	return school_id;
}
public void setSchool_id(String school_id) {
	this.school_id = school_id;
}
public String getGroup_name() {
	return group_name;
}
public void setGroup_name(String group_name) {
	this.group_name = group_name;
}
public String getGroup_desc() {
	return group_desc;
}
public void setGroup_desc(String group_desc) {
	this.group_desc = group_desc;
}
public String getGroup_num() {
	return group_num;
}
public void setGroup_num(String group_num) {
	this.group_num = group_num;
}
}
