package com.yuanding.schoolteacher.bean;



/**
 *  
 * @version 创建时间：2015年11月27日 下午2:39:33 考勤个人详情
 */
public class Cpk_Side_Attence_Statistics {
	
	
	 private String title;
	    private String create_time;
	    private String atd_status;
	    private String atd_id;
	    private String name;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getCreate_time() {
			return create_time;
		}
		public void setCreate_time(String create_time) {
			this.create_time = create_time;
		}
		public String getAtd_status() {
			return atd_status;
		}
		public void setAtd_status(String atd_status) {
			this.atd_status = atd_status;
		}
		public String getAtd_id() {
			return atd_id;
		}
		public void setAtd_id(String atd_id) {
			this.atd_id = atd_id;
		}
}
