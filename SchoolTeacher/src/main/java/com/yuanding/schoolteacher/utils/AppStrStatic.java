package com.yuanding.schoolteacher.utils;

import com.yuanding.schoolteacher.A_0_App;

/**
 * 
* @ClassName: AppStrStatic
* @Description: TODO(App静态配置)
* @author Jiaohaili 
* @date 2015年11月2日 下午4:35:45
*
 */
public class AppStrStatic {
	
	public static final String PACKAGE_NAME = "com.yuanding.schoolteacher";// 备用调用的异常包名
    public static final String VERSION_NAME = "2.5.3";// 备用调用的异常版本号
    public static final int VERSION_CODE = 20170614;// 备用调用的异常版本Code

	/**************************这里存放本APP中用到的常量*********************/
	//本地测试环境
	public static final String SERVER_LINE_TEST_ENVIRONMENT = "http://192.168.50.221:6061/V6/";
	//线上正式环境
    public static final String SERVER_FARMAL_ON_LINE_ENVIRONMENT = "https://api.tch.weixiaobang.net/V6/";
    //预上线环境
//    public static final String SERVER_FARMAL_ON_LINE_ENVIRONMENT = "https://test.tch.weixiaobang.net/V6/";
    
    //Socket 配置修改   注意！！
    public static final String HOST_ONLINE = "211.67.168.36";// 服务host外：211.67.168.36
    public static final String HOST_ONOFF = "192.168.50.221";// 服务host内：192.168.50.221
    public static final int PORT = 9502;// 服务端口
    
    //Xutils 
    public final static int XUTILS_DEFAULT_CONN_TIMEOUT = 1000 * 15; // 15s请求超时时间,()
    public final static int XUTILS_INCREASE_CONN_TIMEOUT = 1000 * 30; // 30s请求超时时间,()
    
    //百度推送配置
//	public static final String API_KEY_BAI_DU_PUSH = "yG0pMPGGZRhVg5rkCWeItism";//百度推送线下
	 public static final String API_KEY_BAI_DU_PUSH = "351MwnpagIh0iVy1wGONg9g1";//百度推送线上
	public static final String API_KEY_YOU_MENG_TONG_JI = "568330d367e58e7abf000bee";//友盟统计
    public static final String API_WEIXIN_SHARE_APP_ID = "wx3db3f0afe0da69f3";//微信分享app_id
    public static final String API_WEIXIN_SHARE_APP_SECRET = "4c6acd3bed77f6bbed539e018cecfff1";//微信分享app_screct
	
    //SD卡图片缓存目录
	public static final String SD_PIC_CACHE = "SchoolTeacher/Cache";
	//SD卡缓存总目录
	public static final String SD_PIC = "/SchoolTeacher";
	
	//SP缓存目录
	public static final String SHARE_PREFER_USER_INFO = "school_teacher_user_info";
	public static final String SHARE_PREFER_USER_PHONE = "school_teacher_user_phone";
	public static final String SHARE_PREFER_GESTURE = "school_teacher_user_gesture";
	public static final String SHARE_PREFER_GESTURE_ERROR = "school_teacher_user_gesture_error";
	
	public static final String USER_LOGIN_TOKEN = "user_login_token";
	public static final String USER_LOGIN_UNIQID = "user_login_uniqid";
	public static final String USER_LOGIN_QUNIQID = "user_login_quniqid";
	public static final String USER_LOGIN_QUTOKEN = "user_login_qutoken";
	public static final String USER_LOGIN_PHONE = "user_login_phone";
	public static final String USER_LOGIN_STATUS = "user_login_status";
	public static final String USER_LOGIN_NAME = "user_login_name";
	public static final String USER_LOGIN_POR_URL = "user_login_por_url";
    public static final String USER_BANG_DOU_URL = "user_bangdou_url";
    public static final String USER_SMS_ACCOUNT_URL = "user_sms_account_url";

    public static final String USER_LOGIN_CHANNEL_ID = "user_login_channel_id";
    public static final String KEY_USER_LOGIN_CHANNEL_ID = "key_user_login_channel_id";
    public static final String KEY_USER_LOGIN_CLIENT_ID = "key_user_login_client_id";
    
	public static final String GESTURE_NOTICE_STTATE = "gesture_notice_state";
	public static final String GESTURE_FALSE_COUNT = "gesture_false_count";
	public static final String GESTURE_FALSE_TIME = "gesture_false_time";
	
    public static final String SHARE_PREFER_USER_PUSH_NOTROUBLE = "school_teacher_user_notrouble";
    public static final String KEY_SHARE_PREFER_USER_PUSH_NOTROUBLE = "school_teacher_user_notrouble_values";
    
    public static final String SHARE_PREFER_USER_QUICK_NOTROUBLE = "school_teacher_user_quick";
    public static final String KEY_SHARE_PREFER_USER_QUICK_NOTROUBLE = "school_teacher_user_quick_values";
    
    public static final String KEY_SHARE_PREFER_UPDATE_TIME = "school_teacher_user_update_time";
	/***************固定的url链接和服务电话**************************/
	//用户注册协议
    public static final String LINK_USER_REGEDIT = "https://h5.weixiaobang.net/teacher_agreement.html";
    //用户运行权限设置
    public static final String LINK_USER_JURISDICTION = "https://h5.weixiaobang.net/android/models/index.html?type="; 
    //用户产品介绍
    public static final String LINK_USER_INTRODUCTION = "https://h5.weixiaobang.net/about/products.html";
    //用户常见问题
    public static final String LINK_USER_QUESTION = "https://h5.weixiaobang.net/about/commonproblem.html";
    //考勤帮助
    public static final String LINK_USER_ATTDENCE_HELP = "https://h5.weixiaobang.net/help/attendance_help_v5.html";
    //短信账户帮助
    public static final String LINK_USER_SMS_HELP = "https://h5.weixiaobang.net/usercenter/message/problem/problemlist.html";
    /***************密码长度设置**************************/
    public static final int PWD_MIN_LENGTH = 6;//密码最小长度
    public static final int PWD_MAX_LENGTH = 24;//密码最大长度
    
	/***************公共请求参数**************************/
	
	public static final String ZCODE = "zcode";
	public static final String ZCODE_VALUE = "yuanding";
	public static final String SIGN = "sign";
	
	/***************用户状态配置**************************/
    public static final String USER_ROLE_UNDER_REVIEW = "0";//审核中
    public static final String USER_ROLE_INACTIVATED = "1";//未激活 (库中)
    public static final String USER_ROLE_HAVA_CERTIFIED = "2";//已认证
    public static final String USER_ROLE_LOCKED = "3";//已锁定
    public static final String USER_ROLE_NEW_USER = "4";//新用户
    public static final String USER_ROLE_AUDIT_FAILURE = "5";//审核失败
    public static final String USER_ROLE_NO_SUPPLEMENTARY_INFO = "6";//未补充资料
    
	
	/***************启动页面********************/
	public static final String HelpIntro = "teacher_start_intro";
	public static final String SHARE_APP_DATA = "teacher_shareAppData";
	
	public static final String HAVA_USER_TOKEN = "teacher_have_user_token";
	public static final String ENTER_ZHIBO_ACY_TYPE = "teacher_enter_zhibo_type";
	public static final String TAG_USER_IS_DELETE = "tag_user_is_delete";
	
    public static final String SHARE_COURSE_SEMESTER_STATUS = "share_course_semester_status";
    
	//发送微校邦验证短信的号码
    public static final String SEND_YANZHENG_MESSAGE_NO = "10690365714664486104";
    public static final int SEND_YANZHENG_MESSAGE_COUNT = 6;//位数
    
	//短信发送等待时间间隔单位为秒
	public static final int message_interval = 60;
	//两次点击按钮_时间间隔单位为秒
    public static final int interval_double_click = 2000;
    
    //登录点击按钮和评论时间间隔（单位秒）
    public static final int INTERVAL_MAIN_TIME = 1;
    public static final int INTERVAL_LOGIN_TIME = 2;
    public static final int INTERVAL_COMMENT_TIME = 10;
    public static final int INTERVAL_RONGYUN_CONNECT = 5;
    
    //评论最多和最少字数的限制
    public static final int WORD_COMMENT_MAX_LIMIT = 150;
    public static final int WORD_COMMENT_MIN_LIMIT = 3;
    
    //清除通知手势密码痕迹的时间设置
    public static final int INTERVAL_NOTCIE_CLEAR_HENJI = 500;
    public static final int INTERVAL_NOTCIE_FORGET_PWD = 10 * 60 * 1000;//10分钟 之内
    public static final int INTERVAL_NOTCIE_FORGET_COUNT = 5;//次数
    
    public static final int cache_key_side_attdence_time = 3600 * 10;
    
    //首页消息固定项目消息ID
    public static final String TAG_MESSAGE_ID_ASSISTANT = "tag_message_id_assistant";
    public static final String TAG_MESSAGE_ID_OFFICIAL = "tag_message_id_official";
    public static final String TAG_MESSAGE_ID_GROUP = "tag_message_id_group";
    
    /***************缓存key值********************/
    //依据网络判断读取本地缓存或者服务器，无缓存时间限制
    public static final String cache_key_side_found_found = "teacher_side_found_found__key";
    public static final String cache_key_side_found_acy = "teacher_side_found_acy__key";
    public static final String cache_key_side_found_my = "teacher_key_side_found_my __key";
    public static final String cache_key_repair_detail = "teacher_repair_detail __key";
    public static final String cache_key_repair_all = "teacher_repair_all __key";
    public static final String cache_key_repair_my = "teacher_repair_my __key";
    public static final String cache_key_wages = "teacher_wages_key";
    public static final String cache_key_lecture_detail = "teacher_lecture_detail_key";
    public static final String cache_key_lecture = "teacher_lecture__key";
    public static final String cache_key_acy = "teacher_acy__key";
    public static final String cache_key_acy_detail = "teacher_acy_detail";
    public static final String cache_key_colleague = "teacher_colleague_key";
    public static final String cache_key_school_in = "teacher_school_in_key";
    public static final String cache_key_person_info = "teacher_person_info_key";
    public static final String cache_key_notice_sent = "teacher_notice_sent_key";
    public static final String cache_key_group_info = "teacher_group_info_key";
    
    public static final String cache_key_notice_detail = "teacher_cache_key_notice_detail";
    public static final String cache_key_notice_unsent = "teacher_notice_unsent_key";
    public static final String cache_key_notice_draft = "teacher_notice_draft_key";
    public static final String cache_key_my_message_all_student = "teacher_my_message_key_all_student";
    public static final String cache_key_my_message_all_teacher = "teacher_my_message_key_all_teacher";
    public static final String cache_key_notice_list = "teacher_notice_list_key";
    public static final String cache_key_notice_detail_text = "teacher_notice_detail_text_key ";
    public static final String cache_key_mess_detail = "teacher_mess_detail__key";
    public static final String cache_key_mess_detail_sys = "teacher_cache_key_mess_detail_sys";
    public static final String cache_key_school_out_click="teacher_school_out_click";
    public static final String cache_key_schoolasstistant_list = "teacher_schoolasstistant_list_key";
    public static final String cache_key_schoolassistant_detail_text = "teacher_schoolassistant_detail_text_key";
    public static final String cache_key_schoolassistant_detail_tips = "teacher_schoolassistant_detail_tips";
    public static final String cache_key_attdence_detail="teacher_attdence_detail";
    public static final String cache_key_personal_statistics="teacher_attdence_personal_statistics";
    public static final String cache_key_personal_detail="teacher_attdence_personal_detail";
 
    public static final String cache_key_invite_record = "teachert_side_invite_record_key"; 
    public static final String cache_key_invite_main = "teacher_side_invite_main_key";  
    public static final String cache_key_info = "teacher_info__key";
    public static final String cache_key_info_detail = "teacher_info_detail_key"; 
    public static final String cache_key_appnotice_read = "teacher_mySide_appnotice_read";  //已读
    public static final String cache_key_appnotice_unread = "teacher_mySide_appnotice_unread"; //未读
    public static final String cache_key_appnotice_unread_or_read = "teacher_mySide_appnotice_unread_or_read"; //未读或已读
    public static final String cache_key_appnotice_success_or_failure = "teacher_mySide_appnotice_success_or_failure"; //成功或失败
    public static final String cache_key_sysnotice = "teacher_attdence_sysnotice";  //系统通知
    public static final String cache_key_official_list = "teacher_official_list_key";
    public static final String cache_key_notice_official_detail_text = "teacher_notice_official_detail_text_key";
    public static final String cache_key_notice_failure_list = "teacher_notice_failure_list_key";
    public static final String cache_key_attence_class_set_list = "teacher_attence_class_set_list";
    public static final String cache_key_attence_class_people_list = "teacher_attence_class_people_list";
    public static final String cache_key_side_delegatedattdence_list="teacher_delegated_attdence_list";
    public static final String cache_key_mess_school_assistant_main = "b_mess_school_assistant_main";
    
    public static final String cache_key_b_side_course ="b_side_course_click_key";
    //多页面同一接口，缓存标签
    public static String TAG_ATTDENCE_DETAIL ="tag_attdence_detail";
    public static String TAG_EXAMINE_NOTICE_DETAIL ="tag_examine_notice_detail";
    
    //不根据网络判断，先读取本地缓存，再访问服务器，无缓存时间限制
    public static final String cache_key_my_message = "teacher_my_message_key";
    public static final String cache_key_mySide = "teacher_mySide_key";
    public static final String cache_key_my_account = "teacher_cache_key_my_account";
    
    //有网：读取接口获得最新封面，无网：直接，读取本地缓存
    public static final String cache_key_student_A_1_Start_Acy = "teacher_A_1_Start_Acy";
    
    //依据网络判断读取本地缓存或者服务器，缓存时间限制为cache_key_side_attdence_time 10H
    public static final String cache_key_side_attdence_list="teacher_attdence_list";
    
    public static final String cache_key_notice_list_waitsure = "teacher_notice_list_key_waitsure";
    public static final String cache_key_receipt_detail = "teacher_notice_cache_key_receipt_detail";

    /*****************************************动态提示语变量***************************************************/


    //微信分享内容和链接
    public static String default_ShareTitle = "下载"+ A_0_App.APP_NAME+"APP 500邦豆等你来领！";
    public static String default_ShareContent = A_0_App.APP_NAME+" 我的校园微生活";
    public static String default_shareInviteUrl = "http://www.weixiaobang.com/index.php?m=mobile&c=mobile&a=download";    //微信分享默认的链接

    //短信分享内容
    public static String sms_share_content = "小伙伴邀请你来体验"+A_0_App.APP_NAME+"APP，官方通知、课程表、老师同学通讯录一个"+A_0_App.APP_NAME+"全搞定，还有新用户大礼，快快下载吧！http://t.cn/RVSxTPk"; //短信分享默认的内容

    public static String app_str_name() {
        return A_0_App.APP_NAME;
    }
    public static String put_title_exit() {
        return "确定退出"+ A_0_App.APP_NAME+"吗?";
    }
    public static String kicked_offline() {
        return "你的"+A_0_App.APP_NAME+"账号在另一个设备登录。如果这不是你本人的操作，你的"+A_0_App.APP_NAME+"账号可能已经泄漏，请及时修改密码";
    }
    public static String str_regedit_agree_title() {
        return A_0_App.APP_NAME+"注册协议";
    }
    public static String str_register_text() {
        return "注册"+A_0_App.APP_NAME;
    }
    public static String str_guide_not_certified() {
        return "欢迎注册"+A_0_App.APP_NAME+"，您是未认证用户，部分应用暂无使用权限，请先到个人中心提交个人认证资料，等待审核。";
    }
    public static String str_guide_certified() {
        return "欢迎注册"+A_0_App.APP_NAME+"，请到个人中心审核您的个人资料，以便日后准确接收院系、班级的校务信息。";
    }
    public static String str_about_copyright_bottom() {
        return "Copyright © 2017 weixiaobang.com";
    }
    public static String str_app_weixin_copyurlsuccess() {
        return "微信号\"Wei_Xiao_Bang\"已复制到剪切板，请到微信搜索关注！";
    }
    public static String str_my_account_invite_defaultsharetitle() {
        return "下载"+A_0_App.APP_NAME+"APP 500邦豆等你来领！";
    }
    public static String str_my_account_invite_defaultsharecontent() {
        return A_0_App.APP_NAME+" 我的校园微生活";
    }
    public static String str_my_side_appnotice_receiverresult_dialog_content() {
        return "您将批量发送短信，邀请好友下载安装"+A_0_App.APP_NAME+"APP";
    }

    public static String str_about_weinxin_number() {
        return "Wei_Xiao_Bang";
    }
}
