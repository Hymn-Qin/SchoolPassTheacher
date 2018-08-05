package com.yuanding.schoolteacher.bean;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.database.Cursor;

public class CpkUserModel {

    /**
     * @Fields serialVersionUID : TODO(用户实体类)
     * "pids": "1,2","organ_attr": "3",
     */
    private String user_id;
    private String uniqid;
    private String username;
    private String phone;
    private String sex;

    private String photo_url;
    private String school_id;
    private String name;
    private String device_token;
    private String getui_client_id;

    private String sn_number;
    private String teacher_status;
    private String organ_id;
    private String organ_name;
    private String school_name;

    private String quniqid;
    private String qutoken;
    private String token;
    private String bang_url;
    private String sms_url;
    private String time;
    private String leave_detail_url;

    public String getLeave_detail_url() {
        return leave_detail_url;
    }

    public void setLeave_detail_url(String leave_detail_url) {
        this.leave_detail_url = leave_detail_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUniqid() {
        return uniqid;
    }

    public void setUniqid(String uniqid) {
        this.uniqid = uniqid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public String getSchool_id() {
        return school_id;
    }

    public void setSchool_id(String school_id) {
        this.school_id = school_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getQuniqid() {
        return quniqid;
    }

    public void setQuniqid(String quniqid) {
        this.quniqid = quniqid;
    }

    public String getQutoken() {
        return qutoken;
    }

    public void setQutoken(String qutoken) {
        this.qutoken = qutoken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBang_url() {
        return bang_url;
    }

    public void setBang_url(String bang_url) {
        this.bang_url = bang_url;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getGetui_client_id() {
        return getui_client_id;
    }

    public void setGetui_client_id(String getui_client_id) {
        this.getui_client_id = getui_client_id;
    }

    public String getSn_number() {
        return sn_number;
    }

    public void setSn_number(String sn_number) {
        this.sn_number = sn_number;
    }

    public String getTeacher_status() {
        return teacher_status;
    }

    public void setTeacher_status(String teacher_status) {
        this.teacher_status = teacher_status;
    }

    public String getOrgan_id() {
        return organ_id;
    }

    public void setOrgan_id(String organ_id) {
        this.organ_id = organ_id;
    }

    public String getOrgan_name() {
        return organ_name;
    }

    public void setOrgan_name(String organ_name) {
        this.organ_name = organ_name;
    }

    public String getSms_url() {
        return sms_url;
    }

    public void setSms_url(String sms_url) {
        this.sms_url = sms_url;
    }
}
