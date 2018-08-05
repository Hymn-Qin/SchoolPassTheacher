package com.yuanding.schoolteacher.bean;

public class ChildData {
	private String count;
	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	private String childName;
	private String id;
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private boolean childSelected;
	
	private int childImage;

	public String getChildName() {
		return childName;
	}

	public void setChildName(String childName) {
		this.childName = childName;
	}

	public boolean isChildSelected() {
		return childSelected;
	}

	public void setChildSelected(boolean childSelected) {
		this.childSelected = childSelected;
	}

	public int getChildImage() {
		return childImage;
	}

	public void setChildImage(int childImage) {
		this.childImage = childImage;
	}
	
}