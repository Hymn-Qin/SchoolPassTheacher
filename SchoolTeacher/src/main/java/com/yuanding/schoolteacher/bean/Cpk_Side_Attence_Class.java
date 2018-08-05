
package com.yuanding.schoolteacher.bean;

import java.io.Serializable;

/**
 * @author Jiaohaili
 * @version 创建时间：2016-4-7 下午11:24:24
 * 考勤班级列表
 */
public class Cpk_Side_Attence_Class implements Serializable {

    private String organ_id;
    private String organ_name;
    private String id;
    private String name;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
  
}
