
package com.yuanding.schoolteacher.bean;

import java.io.Serializable;

/**
 * @author Jiaohaili
 * @version 创建时间：2016-4-7 下午11:52:02 考勤，添加被考勤人
 */
public class Cpk_Side_Attence_Add_Contact implements Serializable{

    private String organ_id;
    private String organ_name;
    private String child_total_num;
    private String user_total_num;
    private String parentid;
    private String next;
    public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	private String level;
    public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	private boolean check;
    public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
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

    public String getUser_total_num() {
        return user_total_num;
    }

    public void setUser_total_num(String user_total_num) {
        this.user_total_num = user_total_num;
    }

}
