package com.yuanding.schoolteacher.bean;

import java.util.List;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月27日 下午2:39:33 身边联系人
 */
public class Cpk_Notice_Linkman {

	/**
	 * //我的分组
	 */
	private List<Cpk_Side_Link_Group> group;
	/**
	 * //我的同事
	 */
	private List<Cpk_Side_Link_Group> stration;
	/**
	 * //我的学生
	 */
	private List<Cpk_Side_Link_Group> teach;// 教学机构

	public List<Cpk_Side_Link_Group> getGroup() {
		return group;
	}

	public void setGroup(List<Cpk_Side_Link_Group> group) {
		this.group = group;
	}

	public List<Cpk_Side_Link_Group> getStration() {
		return stration;
	}

	public void setStration(List<Cpk_Side_Link_Group> stration) {
		this.stration = stration;
	}

	public List<Cpk_Side_Link_Group> getTeach() {
		return teach;
	}

	public void setTeach(List<Cpk_Side_Link_Group> teach) {
		this.teach = teach;
	}
}
