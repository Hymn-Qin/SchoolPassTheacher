package com.yuanding.schoolteacher.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年1月6日 上午11:53:10 通讯录学生—通讯录
 */
public class Cpk_Student_Class_Contact {

    private String child_total_num;
    private String parent_id;
	private String organ_id;
	private String organ_name;
	private String level;
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
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

    public String getChild_total_num() {
        return child_total_num;
    }

    public void setChild_total_num(String child_total_num) {
        this.child_total_num = child_total_num;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

}
