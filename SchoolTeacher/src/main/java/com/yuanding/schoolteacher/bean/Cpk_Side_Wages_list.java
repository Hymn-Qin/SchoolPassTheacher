package com.yuanding.schoolteacher.bean;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月28日 上午11:34:32 工资列表
 */
public class Cpk_Side_Wages_list {

	private String source;
	private long time;
	private String title;
	private String lists;
	private String teacher_name;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTeacher_name() {
		return teacher_name;
	}
	public void setTeacher_name(String teacher_name) {
		this.teacher_name = teacher_name;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLists() {
		return lists;
	}
	public void setLists(String lists) {
		this.lists = lists;
	}

}
