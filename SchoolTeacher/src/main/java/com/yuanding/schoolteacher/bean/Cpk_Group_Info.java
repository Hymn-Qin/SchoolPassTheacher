package com.yuanding.schoolteacher.bean;

import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月14日 下午7:46:25 类说明
 */
public class Cpk_Group_Info {

	private String classname;
	private String uniqid;

	private List<Cpk_Group_Info_item> mlist;

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getUniqid() {
		return uniqid;
	}

	public void setUniqid(String uniqid) {
		this.uniqid = uniqid;
	}

	public List<Cpk_Group_Info_item> getMlist() {
		return mlist;
	}

	public void setMlist(List<Cpk_Group_Info_item> mlist) {
		this.mlist = mlist;
	}

}
