
package com.yuanding.schoolteacher.bean;

import java.io.Serializable;

/**
 * @author Jiaohaili
 * @version 创建时间：2016-4-7 下午11:52:02 添加联系人分组
 */
public class Cpk_Side_Attence_Add_Group implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private String id;
    private String name;
    private String count;
    private String phone_whitelist;
    private String parentid;
    public String getParentid() {
		return parentid;
	}
	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	public String getPhone_whitelist() {
		return phone_whitelist;
	}
	public void setPhone_whitelist(String phone_whitelist) {
		this.phone_whitelist = phone_whitelist;
	}
	private boolean check;
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
   

}
