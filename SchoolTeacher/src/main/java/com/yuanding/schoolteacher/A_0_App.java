package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.ipc.RongExceptionHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StreamCorruptedException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.xutils.x;
import org.xutils.image.ImageOptions;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.baidu.android.pushservice.PushManager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.sea_monster.resource.ResourceHandler;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.PlatformConfig;
import com.yuanding.schoolteacher.bean.CpkUserModel;
import com.yuanding.schoolteacher.bean.Cpk_Attence_Group;
import com.yuanding.schoolteacher.bean.Cpk_Device_Info;
import com.yuanding.schoolteacher.bean.Cpk_Search_Recipient;
import com.yuanding.schoolteacher.bean.Cpk_Side_Attence_Add_Contact;
import com.yuanding.schoolteacher.bean.Cpk_Side_Attence_Add_Group;
import com.yuanding.schoolteacher.bean.Cpk_Student_Info;
import com.yuanding.schoolteacher.bean.Cpk_User_Authenticate;
import com.yuanding.schoolteacher.bean.Cpk_Version;
import com.yuanding.schoolteacher.service.Api;
import com.yuanding.schoolteacher.service.BackService;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.DensityUtils;
import com.yuanding.schoolteacher.utils.LogUtils;
import com.yuanding.schoolteacher.utils.NetWorkManager;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.utils.download.Update_App;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.yuanding.schoolteacher.view.LogoDialog;
import com.yuanding.schoolteacher.view.rongyun.CustomizeMessageItemProvider;
import com.yuanding.schoolteacher.view.rongyun.DemoContext;
import com.yuanding.schoolteacher.view.rongyun.WYZFNoticeContent;

public class A_0_App extends Application {

	/******************** 发布版本请修改 ******************************/
	public static boolean debug_Mode = false;// 本地测试模式True，正式线上模式为false
	public static boolean debug_Log = false;// Log开关

	public static String SERVER_REQUEST_BASE_URL = "";
	public static String SERVER_CONN_BASE_URL = "";
	private boolean mRuning = false;
	private static A_0_App mApp = null;
	public static boolean hadIntercept = true;

	public static int screenWidth, Width;
	public static int screenHeigh;
	private List<Activity> activityList;
	private List<Activity> activityList_rongyun;
	private List<Activity> resetPwdList;
	private List<Activity> registerList;
	private static Api api;
	private Cpk_Student_Info studentInfo;
	private Cpk_User_Authenticate authen;// 注册时的激活认证页面
	private String userPwd, userMobile;
 
	private static boolean isAfterScreenPage = false; //app启动是否经过了闪屏界面 false 没经过 ，true 经过
	private static String clickPushMsgReqPage = ""; //进程被杀掉以后 如果是点击推送消息 并且用户未登录 则app启动进到登录页以后  会从登录页面跳转到这个页面
	
	// USER_TOKEN自己服务器用户登录令牌(自己的)，USER_QUTOKEN_连接融云令牌RongToken(重要)，USER_UNIQID这个是用户和融云相同的targetId(重要)，USER_QUNIQID融云群组id
	public static String USER_TOKEN, USER_QUTOKEN,
			USER_UNIQID, USER_QUNIQID, USER_PHONE, USER_STATUS, USER_NAME,
			USER_POR_URL;// 用户状态
	public static String USER_BANGDOU_URL,USER_SMS_URL;

	// 存储设置
	private SharedPreferences mSharePre, mGesture, mGestureError;
	// 消息免打扰开关
    public int mPushState;//  0表示取消免打扰时段功能1 表示指定免打扰时段  2表示全天开启免打扰
    public boolean mQuickSwitchState;//true，表示又快捷方式

	private String leave_detail_url;//请销假
	// 通知手势开关
	public String mNoticeGesure;// 通知模块手势的状态 0 未设置 1为开 2为关
	private String channelId,clientid = "";// 百度云
	private String pushNoticeId, pushNoticeType, pushNoticeSubType,link_id,jump_module,message_type,title_name;// 通知的消息和类型
	public int mFalseAllCount = 0;// 初始错误次数为0

	public static String notice_sign = "";// 身边信息通知
	public static int count = 0;// 身边信息通知
	public static String result = "", userids = "", classids = "",
			organids = "";// 身边信息通知

	public int WB_BangDou_Tag = 0;// 0表示正常登录，1表示查看邦豆Token过期，2表示查看短信账户过期,3表示迎新管理会话过期
	// 通讯录卡顿标签

	// 定位分享
	private RongIM.LocationProvider.LocationCallback mLastLocationCallback;

	//接收到的身边信息通知
	private String loginReciverMessage;
    private String sideReciverMessage;
    
	private Cpk_Version version = null;
	private NetWorkManager networkMgr = null;
	public static String both;

	/**
	 * 身边 通知页面 刷新标识0-已发送、1待发送、2草稿箱
	 */
	public static int SIDE_NOTICE = -1;
	public static String strTitle = "全选";

	public static int Notice_more = 0;

	/**
	 * 添加联系人标识
	 */
	public static int contace_tab_posi = 0;// 通讯录选项卡

	public static int add_position = 0;// 通知选项卡
	
	/**
	 * 是否回执
	 */
	public static boolean isHuizhi = false; //默认不回执
	
	/**
	 * 通知选择收信人
	 */
	public static List<Cpk_Search_Recipient> notice_search_Contacts = new ArrayList<Cpk_Search_Recipient>();
	
	public static List<Cpk_Side_Attence_Add_Contact> student_all_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	public static List<Cpk_Side_Attence_Add_Contact> student_result_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	public static List<Cpk_Side_Attence_Add_Contact> student_result_Contacts_summit = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	public static List<Cpk_Side_Attence_Add_Contact> student_temp_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	
	public static List<Cpk_Side_Attence_Add_Contact> colleague_all_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	public static List<Cpk_Side_Attence_Add_Contact> colleague_result_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	public static List<Cpk_Side_Attence_Add_Contact> colleague_result_Contacts_summit = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	public static List<Cpk_Side_Attence_Add_Contact> colleague_temp_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	
	public static List<Cpk_Side_Attence_Add_Group> group_result_Contacts = new ArrayList<Cpk_Side_Attence_Add_Group>();
	public static List<Cpk_Side_Attence_Add_Group> group_result_Contacts_summit = new ArrayList<Cpk_Side_Attence_Add_Group>();
	public static List<Cpk_Side_Attence_Add_Group> group_temp_Contacts = new ArrayList<Cpk_Side_Attence_Add_Group>();

	/**
	 * 选择收信人检测选择过的标记
	 */
	public static int add_student_check = 0;
	public static int add_colleague_check = 0;
	public static int add_group_check = 0;
	/**
	 * 考勤
	 */
	public static int SIDE_ATTENCE =0;
	
	public static List<Cpk_Side_Attence_Add_Contact> attence_all_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	public static List<Cpk_Side_Attence_Add_Contact> attence_result_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	public static List<Cpk_Side_Attence_Add_Contact> attence_temp_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	public static List<Cpk_Side_Attence_Add_Contact> attence_result_Contacts_summit = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	
	public static List<Cpk_Attence_Group> attence_result_defined = new ArrayList<Cpk_Attence_Group>();
	public static List<Cpk_Attence_Group> attence_result_defined_summit = new ArrayList<Cpk_Attence_Group>();
	// Redmi Note 2
	public static String MODEL = "";

	public static int summit = 0;// 1确定 添加收信人用到
	public static String bgtemp = "";// 通知图文背景图片
	public static int checked = 0;
	public static Map<String, String> map_url = new TreeMap<String, String>();// 图片地址
	public static int biaozhi = 0;// 图片变量

	/*
	 * 设备信息
	 */
	private Cpk_Device_Info device_info;
	   
    /**
     * Sock登录成功与否的标志
     */
    public boolean socket_login,regedit_enter,mUpdating = false;
    
    protected static A_0_App instance;  
    public String class_ids = "";//课程表详情参与班级列表
    /**
     * 当前在消息页面点击的快捷方式类型
     */
    private int mCurrentFastIconClickType; // 0 app通知  1 sms通知
    private Drawable animalDrawable;
	private PackageManager pm;

	public static String APP_NAME="";
	@Override
	public void onCreate() {
		MultiDex.install(this);
		super.onCreate();
		inItXutils();
		inItRongIm();// 加入融云
//      inItBaiduLoc();//百度定位和地图   注意：此定位版本会导致锤子手机启动崩溃
		PlatformConfig.setWeixin(AppStrStatic.API_WEIXIN_SHARE_APP_ID, AppStrStatic.API_WEIXIN_SHARE_APP_SECRET);
		APP_NAME=getApplicationContext().getResources().getString(R.string.app_name);

		mApp = this;
		activityList = new ArrayList<Activity>();
		activityList_rongyun = new ArrayList<Activity>();
		resetPwdList = new ArrayList<Activity>();
		registerList = new ArrayList<Activity>();
		version = new Cpk_Version();
		device_info = new Cpk_Device_Info();
		
		MODEL = android.os.Build.MODEL;

		initClass();
		initImageLoader(getApplicationContext());

		/**
		 * umeng
		 */
		AnalyticsConfig.setAppkey(AppStrStatic.API_KEY_YOU_MENG_TONG_JI);
		MobclickAgent.updateOnlineConfig(this);

		animalDrawable = mApp.getResources().getDrawable(R.drawable.load_progress);
	}

	 /**
	  * 创建服务用于捕获崩溃异常    
	  */
   private UncaughtExceptionHandler restartHandler = new UncaughtExceptionHandler() {    
       public void uncaughtException(Thread thread, Throwable ex) {   	   
    	   if (null != ex.getMessage() && ex.getMessage().length() > 0 && ex.getMessage().contains("io.rong.push")) {
				// 因为融云产生的异常 不处理
			} else {
				restartApp();// 发生崩溃异常时,重启应用
			}
       }    
   };    

   private void inItXutils() {
       x.Ext.init(this); // 这一步之后, 我们就可以在任何地方使用x.app()来获取Application的实例了.
       x.Ext.setDebug(false); // 是否输出debug日志
   }
   
   /**
    * 异常重启app
    */
	public void restartApp(){  
       Intent intent = new Intent(instance,A_1_Start_Acy.class);  
       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
       instance.startActivity(intent);  
       android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前  
   }  
	
	private void inItRongIm() {

		if (getApplicationInfo().packageName
				.equals(getCurProcessName(getApplicationContext()))) {

			// RongPushClient.registerHWPush(this);
			// RongPushClient.registerMiPush(this, "2882303761517432809",
			// "5291743238809");
			// try {
			// RongPushClient.registerGCM(this);
			// } catch (RongException e) {
			// e.printStackTrace();
			// }

			try {
                RongIM.init(this);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

			/**
			 * c 融云SDK事件监听处理
			 * 
			 * 注册相关代码，只需要在主进程里做。
			 */
			if (getApplicationInfo().packageName
					.equals(getCurProcessName(getApplicationContext()))) {

				DemoContext.init(getApplicationContext());
				RongIM.setLocationProvider(new LocationProvider());
				new ResourceHandler.Builder().enableBitmapCache()
						.setOutputSizeLimit(120).setType("app").build(getApplicationContext());

				Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(
						this));

				try {
					RongIM.registerMessageType(WYZFNoticeContent.class);
					RongIM.registerMessageTemplate(new CustomizeMessageItemProvider());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

//	public LocationService locationService;
//	public Vibrator mVibrator;
//
//	private void inItBaiduLoc() {
//		/***
//		 * 初始化定位sdk，建议在Application中创建
//		 */
//		locationService = new LocationService(getApplicationContext());
//		mVibrator = (Vibrator) getApplicationContext().getSystemService(
//				Service.VIBRATOR_SERVICE);
//		SDKInitializer.initialize(getApplicationContext());
//	}

	private void initClass() {

		pm = getPackageManager();
		api = new Api(getApplicationContext());
		switchNetSetting();
		mSharePre = getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_INFO,
				Activity.MODE_PRIVATE);

		USER_TOKEN = mSharePre.getString(AppStrStatic.USER_LOGIN_TOKEN, "");
		USER_QUNIQID = mSharePre.getString(AppStrStatic.USER_LOGIN_QUNIQID, "");
		USER_QUTOKEN = mSharePre.getString(AppStrStatic.USER_LOGIN_QUTOKEN, "");
		USER_UNIQID = mSharePre.getString(AppStrStatic.USER_LOGIN_UNIQID, "");
		USER_PHONE = mSharePre.getString(AppStrStatic.USER_LOGIN_PHONE, "");
		USER_STATUS = mSharePre.getString(AppStrStatic.USER_LOGIN_STATUS, "");// 用户状态
		USER_NAME = mSharePre.getString(AppStrStatic.USER_LOGIN_NAME, "");// 用户名字
		USER_POR_URL = mSharePre.getString(AppStrStatic.USER_LOGIN_POR_URL, "");// 用户头像URl
		USER_BANGDOU_URL = mSharePre.getString(AppStrStatic.USER_BANG_DOU_URL,
				"");// 用户邦豆Url
		USER_SMS_URL = mSharePre.getString(AppStrStatic.USER_SMS_ACCOUNT_URL,"");// 用户邦豆Url
		ReadGestureSet();
		// mFalseTime =
		// mGesture.getLong(AppStrStatic.GESTURE_FALSE_TIME,System.currentTimeMillis());//失败的时间

        mPushState = getUserPushSetState();
        switch (mPushState) {
            case 0:
                PushManager.setNoDisturbMode(getApplicationContext(), 00, 00, 00, 00);
                break;
            case 1:
                PushManager.setNoDisturbMode(getApplicationContext(), 22, 00, 8, 00);
                break;    
            case 2:
                PushManager.setNoDisturbMode(getApplicationContext(), 00, 00, 23, 59);
                break;
            default:
                break;
        }
		networkMgr = new NetWorkManager(getApplicationContext());
		
		mQuickSwitchState = getUserQuickSetState();
	}
	
    /*
     * 切换网段
     */
    public void switchNetSetting() {
        if (getLogUsePermission()) {
            // 测试模式
            SERVER_REQUEST_BASE_URL = AppStrStatic.SERVER_LINE_TEST_ENVIRONMENT;
            SERVER_CONN_BASE_URL = AppStrStatic.HOST_ONOFF;
            debug_Log = true;
        } else {
            if (debug_Mode) {
                // 测试模式
                SERVER_REQUEST_BASE_URL = AppStrStatic.SERVER_LINE_TEST_ENVIRONMENT;
                SERVER_CONN_BASE_URL = AppStrStatic.HOST_ONOFF;
                debug_Log = true;
            } else {
                debug_Log = false;
                // 正式模式
                SERVER_REQUEST_BASE_URL = AppStrStatic.SERVER_FARMAL_ON_LINE_ENVIRONMENT;
                SERVER_CONN_BASE_URL = AppStrStatic.HOST_ONLINE;
                
                // 程序崩溃时触发线程  以下用来捕获程序崩溃异常 
                instance = this;  
                Thread.setDefaultUncaughtExceptionHandler(restartHandler);
            }
        }
    }
    
	public void ReadGestureSet() {
		mGesture = getSharedPreferences(AppStrStatic.SHARE_PREFER_GESTURE
				+ A_0_App.USER_UNIQID, Activity.MODE_PRIVATE);
		mGestureError = getSharedPreferences(
				AppStrStatic.SHARE_PREFER_GESTURE_ERROR + A_0_App.USER_UNIQID,
				Activity.MODE_PRIVATE);
		mNoticeGesure = mGesture.getString(AppStrStatic.GESTURE_NOTICE_STTATE
				+ A_0_App.USER_UNIQID, "0");// 通知手势开关——默认为未设置
		mFalseAllCount = mGestureError.getInt(AppStrStatic.GESTURE_FALSE_COUNT
				+ A_0_App.USER_UNIQID, 0);// 失败的总次数
	}

	//保存用户登录信息
	public void saveUserLoginInfo(CpkUserModel user) {
		Editor editor = getSharedPreferences(
				AppStrStatic.SHARE_PREFER_USER_INFO, MODE_PRIVATE).edit();

		USER_TOKEN = user.getToken();
		USER_QUNIQID = user.getQuniqid();
		USER_QUTOKEN = user.getQutoken();
		USER_UNIQID = user.getUniqid();
		USER_PHONE = user.getPhone();
		USER_STATUS = user.getTeacher_status();
		USER_NAME = user.getName();
		USER_POR_URL = user.getPhoto_url();
		USER_BANGDOU_URL = user.getBang_url();
		USER_SMS_URL = user.getSms_url();

		editor.putString(AppStrStatic.USER_LOGIN_TOKEN, USER_TOKEN);
		editor.putString(AppStrStatic.USER_LOGIN_QUNIQID, USER_QUNIQID);
		editor.putString(AppStrStatic.USER_LOGIN_QUTOKEN, USER_QUTOKEN);
		editor.putString(AppStrStatic.USER_LOGIN_UNIQID, USER_UNIQID);
		editor.putString(AppStrStatic.USER_LOGIN_PHONE, USER_PHONE);
		editor.putString(AppStrStatic.USER_LOGIN_STATUS, USER_STATUS);
		editor.putString(AppStrStatic.USER_LOGIN_NAME, USER_NAME);
		editor.putString(AppStrStatic.USER_LOGIN_POR_URL, USER_POR_URL);
		editor.putString(AppStrStatic.USER_BANG_DOU_URL, USER_BANGDOU_URL);
		editor.putString(AppStrStatic.USER_SMS_ACCOUNT_URL, USER_SMS_URL);
		editor.commit();
	}
	
    //更新用户登录信息
    public void updateUserLoginInfo(String user_status,String user_uniqid,String user_phone,String user_name,String user_por_url) {
        Editor editor = getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_INFO, MODE_PRIVATE).edit();

        USER_UNIQID = user_uniqid;
        USER_PHONE = user_phone;
        USER_NAME = user_name;
        USER_POR_URL = user_por_url;
        USER_STATUS = user_status;
        
        editor.putString(AppStrStatic.USER_LOGIN_STATUS, USER_STATUS);
        editor.putString(AppStrStatic.USER_LOGIN_UNIQID, USER_UNIQID);
        editor.putString(AppStrStatic.USER_LOGIN_PHONE, USER_PHONE);
        editor.putString(AppStrStatic.USER_LOGIN_NAME, USER_NAME);
        editor.putString(AppStrStatic.USER_LOGIN_POR_URL, USER_POR_URL);
        editor.commit();
    }
    
    //更新用户本地状态登录信息
    public void updateUserLoginInfo(String user_status) {
        Editor editor = getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_INFO, MODE_PRIVATE).edit();
        USER_STATUS = user_status;
        
        editor.putString(AppStrStatic.USER_LOGIN_STATUS, USER_STATUS);
        editor.commit();
    }
    
    public void saveUserPhone(String phone) {
        Editor editor = getSharedPreferences(
                AppStrStatic.SHARE_PREFER_USER_PHONE, MODE_PRIVATE).edit();
        editor.putString(AppStrStatic.SHARE_PREFER_USER_PHONE, phone);
        editor.commit();
    }
	/**
	 * 是否经过了闪屏 false的话 说明 没有经过闪屏 进程曾经被杀掉
	 * @return
	 */
	public static boolean isAfterScreenPage() {
		return A_0_App.isAfterScreenPage;
	}
	
	/**
	 * 是否经过了闪屏 在闪屏界面将这个值变成true
	 * @param isAfterScreenPage
	 */
	public static void setAfterScreenPage(boolean isAfterScreenPage) {
		A_0_App.isAfterScreenPage = isAfterScreenPage;
	}
	
    //重新上传ChannelID，并存储
    public void saveChannelId(String key,String values) {
        Editor editor_t = getSharedPreferences(AppStrStatic.USER_LOGIN_CHANNEL_ID + A_0_App.USER_UNIQID, MODE_PRIVATE).edit();
        editor_t.putString(key, values);
        editor_t.commit();
    }
    
    //获取Channel_Id
    public String getChannelIdStr(String key) {
        SharedPreferences mSharePre = getSharedPreferences(AppStrStatic.USER_LOGIN_CHANNEL_ID + A_0_App.USER_UNIQID,
                Activity.MODE_PRIVATE);
        String channel_id = mSharePre.getString(key, "");
        return channel_id;
    }

	// 清除通知手势
	public void clearNoticeState() {
		mNoticeGesure = "0";
		Editor editor = getSharedPreferences(
				AppStrStatic.SHARE_PREFER_GESTURE + A_0_App.USER_UNIQID,
				MODE_PRIVATE).edit();
		editor.remove(AppStrStatic.GESTURE_NOTICE_STTATE + A_0_App.USER_UNIQID);
		editor.commit();

		clearNoticeErrorValues();// 清除手势同时清除失败次数和时间
	}

	// 保存通知手势开关
	public void saveNoticeGesture(String notice_state) {
		mNoticeGesure = notice_state;
		Editor editor = getSharedPreferences(
				AppStrStatic.SHARE_PREFER_GESTURE + A_0_App.USER_UNIQID,
				MODE_PRIVATE).edit();
		editor.putString(AppStrStatic.GESTURE_NOTICE_STTATE
				+ A_0_App.USER_UNIQID, notice_state);
		editor.commit();
	}

	// 保存失败的次数
	public void saveNoticeErrorValues(int count, long time) {
		mFalseAllCount = count;
		Editor editor = getSharedPreferences(
				AppStrStatic.SHARE_PREFER_GESTURE_ERROR + A_0_App.USER_UNIQID,
				MODE_PRIVATE).edit();
		editor.putLong(String.valueOf(count), time);
		editor.putInt(AppStrStatic.GESTURE_FALSE_COUNT + A_0_App.USER_UNIQID,
				mFalseAllCount);
		editor.commit();
	}

	// 清除失败的次数和时间
	public void clearNoticeErrorValues() {
		mFalseAllCount = 0;
		Editor editor = getSharedPreferences(
				AppStrStatic.SHARE_PREFER_GESTURE_ERROR + A_0_App.USER_UNIQID,
				MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}

	public int getUserPushSetState() {
        SharedPreferences mSharePre = getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_PUSH_NOTROUBLE + A_0_App.USER_UNIQID,
                Activity.MODE_PRIVATE);
        int state = mSharePre.getInt(AppStrStatic.KEY_SHARE_PREFER_USER_PUSH_NOTROUBLE, 0);
        return state;
    }
    
    // 保存推送的状态
    public void setmPushState(int values) {
        mPushState = values;
        Editor editor = getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_PUSH_NOTROUBLE + A_0_App.USER_UNIQID, MODE_PRIVATE)
                .edit();
        editor.putInt(AppStrStatic.KEY_SHARE_PREFER_USER_PUSH_NOTROUBLE, values);
        editor.commit();
    }
    
    // 保存本次检查更新时间
    public void saveUpdateTime() {
        Editor editor = getSharedPreferences(AppStrStatic.KEY_SHARE_PREFER_UPDATE_TIME,MODE_PRIVATE).edit();
        editor.putLong(AppStrStatic.KEY_SHARE_PREFER_UPDATE_TIME, System.currentTimeMillis());
        editor.commit();
    }

    public long getUpdateTime() {
        SharedPreferences prefs = getSharedPreferences(AppStrStatic.KEY_SHARE_PREFER_UPDATE_TIME, Activity.MODE_PRIVATE);
        long getValue = prefs.getLong(AppStrStatic.KEY_SHARE_PREFER_UPDATE_TIME, 0);
        return getValue;
    }
    
    public boolean getUserQuickSetState() {
        SharedPreferences mSharePre = getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_QUICK_NOTROUBLE + A_0_App.USER_UNIQID,
                Activity.MODE_PRIVATE);
        boolean state = mSharePre.getBoolean(AppStrStatic.KEY_SHARE_PREFER_USER_QUICK_NOTROUBLE, true);
        return state;
    }
    
    // 保存快捷按钮的状态
    public void setmQuickState(boolean values) {
        mQuickSwitchState = values;
        Editor editor = getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_QUICK_NOTROUBLE + A_0_App.USER_UNIQID, MODE_PRIVATE)
                .edit();
        editor.putBoolean(AppStrStatic.KEY_SHARE_PREFER_USER_QUICK_NOTROUBLE, values);
        editor.commit();
    }

	// 获取指定失败次数的时间
	public long getCountTime(int errorCount) {
		return mGestureError.getLong(String.valueOf(errorCount),
				System.currentTimeMillis());
	}

	/*
	 * // 分享试图高度 public int getShowViewHeight() { int num = 0; if (screenWidth <
	 * 710) { num = 25; } else if (screenWidth >= 710 && screenWidth <= 900) {
	 * num = 0; } else if (screenWidth > 900 && screenWidth < 1000) { num = 10;
	 * } else if (screenWidth >= 1000) { num = 0; }
	 * 
	 * return num; }
	 */
	public int getShowViewHeight() {
		int num = 0;
		if (screenWidth < 710) {
			num = -9;
		} else if (screenWidth >= 710 && screenWidth <= 900) {
			num = -34;
		} else if (screenWidth > 900 && screenWidth < 1000) {
			num = -24;
		} else if (screenWidth >= 1000) {
			num = -34;
		}

		return num;
	}
	public int setWebviewSize() {
		int size= 17;
		if (screenWidth < 710) {
			size = 15;
		} else if (screenWidth >= 710 && screenWidth <= 900) {
			size = 15;
		} else if (screenWidth > 900 && screenWidth < 1000) {
			size = 15;
		} else if (screenWidth >= 1000) {
			size = 17;
		}

		return size;
	}
	// 发送到消息广播
	public void schoolStartPush(Context context, String action) {
		Intent iten = new Intent(action);
		context.sendBroadcast(iten);
	}

    
    @SuppressWarnings("unchecked")
    public Map<String, String> readOAuth() {
        Map<String, String> oAuth_1 = new HashMap<String, String>();
        SharedPreferences preferences = getSharedPreferences("base64",
                MODE_PRIVATE);
        String productBase64 = preferences.getString("oAuth_1", "");
        System.out.println("kkkkk" + productBase64);
        if (productBase64 != null && !productBase64.equals("")) {
            // 读取字节
            byte[] base64 = Base64.decodeBase64(productBase64.getBytes());

            // 封装到字节流
            ByteArrayInputStream bais = new ByteArrayInputStream(base64);
            try {
                // 再次封装
                ObjectInputStream bis = new ObjectInputStream(bais);
                try {
                    // 读取对象
                    oAuth_1 = (Map<String, String>) bis.readObject();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (StreamCorruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return oAuth_1;
    }

    
    public void saveOAuth(Map<String, String> oAuth_1) {
        SharedPreferences preferences = getSharedPreferences("base64",
                MODE_PRIVATE);
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(oAuth_1);
            // 将字节流编码成base64的字符窜
            String oAuth_Base64 = new String(Base64.encodeBase64(baos
                    .toByteArray()));
            Editor editor = preferences.edit();
            editor.putString("oAuth_1", oAuth_Base64);

            editor.commit();
        } catch (IOException e) {
            // TODO Auto-generated
        }
        Log.i("ok", "存储成功");
    }
    
    
    public void display_red() {
        String temp = "";
        Map<String, String> oAuth_1 = readOAuth();
        if (oAuth_1 == null || oAuth_1.size() == 0)
            return;
        for (Entry<String, String> entry : oAuth_1.entrySet()) {
            String strkey1 = entry.getKey();
            String strval1 = entry.getValue();
            temp = strval1 + temp;
        }

        if (temp.contains("1")) {
            A_Main_Acy.getInstance().showSideNoReadTag(true);
        } else {
            A_Main_Acy.getInstance().showSideNoReadTag(false);
        }
    }
    
	/**
	 * listview条目做动画的标识
	 */
	public static boolean colleague_flag = true;
	public int getmCurrentFastIconClickType() {
		return mCurrentFastIconClickType;
	}

	public void setmCurrentFastIconClickType(int mCurrentFastIconClickType) {
		this.mCurrentFastIconClickType = mCurrentFastIconClickType;
	}

	public static boolean student_list_flag = true;
	public static boolean select_student_flag = true;
	public static boolean select_colleague_flag = true;
	public static boolean isShowAnimation = false;
	/* ---------页面百叶窗动画效果_开始 -------- */
	// A_3_3_Complete_Marer_Nodify_Acy
	public static int modify_curPosi = -1;
	// A_3_3_Complete_Selcet_Class
	public static int selclass_curPosi = -1;
	// A_3_3_Complete_Selcet_School
	public static int selschool_curPosi = -1;
	// B_Account_BlackList_Acy
	public static int black_curPosi = -1;
	// B_Contact_Main_Colleague
	public static int colleague_curPosi = -1;
	// B_Contact_Main_In_School
	public static int in_curPosi = -1;
	// B_Contact_Main_Student_Class
	public static int student_class_curPosi = -1;
	// B_Contact_Main_Student_List
	public static int student_list_curPosi = -1;
	// B_Contact_Out_School_Click_Item_List
	public static int out_click_curPosi = -1;
	// B_Contact_Out_School_Search_Item
	public static int out_search_curPosi = -1;
	// B_Mess_Forward_Select_Change
	public static int select_change_curPosi = -1;
	// B_Mess_Forward_Select_Colleague
	public static int select_colleague_curPosi = -1;
	// B_Mess_Forward_Select_Student
	public static int select_student_curPosi = -1;
	// B_Mess_Forward_Select
	public static int forward_select_curPosi = -1;
	// B_Mess_Notice_List
	public static int notice_list_curPosi = -1;
	// B_Side_Acy_list_Scy
	public static int acy_list_curPosi = -1;
	// B_Side_Lectures_Acy
	public static int lecture_curPosi = -1;
	// B_Side_Notice_Main_Drafts
	public static int draft_curPosi = -1;
	// B_Side_Notice_Main_Selcet_Sign
	public static int select_sign_curPosi = -1;
	// B_Side_Notice_Main_Sent_Notice
	public static int sent_notice_curPosi = -1;
	// B_Side_Notice_Main_Sent_SMS
	public static int sent_sms_curPosi = -1;
	// B_Side_Notice_Main_Sent
	public static int sent_curPosi = -1;
	// B_Side_Notice_Main_UnSent
	public static int unsent_curPosi = -1;
	// B_Side_Repair_All
	public static int repair_all_list_curPosi = -1;
	// B_Side_Repair_my
	public static int repair_my_list_curPosi = -1;
	// B_Side_Repair_Acy
	public static int repair_curPosi = -1;
	// B_Side_Wages_Acy
	public static int wage_curPosi = -1;
	// B_Side_Found_My
	public static int side_found_my_list_curPosi = -1;
	// B_Side_Found_Found
	public static int side_found_found_curPosi = -1;
	// B_Side_Found_Acy
	public static int side_found_acy_curPosi = -1;
	// B_Side_attence
	public static int side_statistics_detail_curPosi = -1;

	// /* ---------页面百叶窗动画效果_结束 -------- */

	public String getLeave_detail_url() {
		return leave_detail_url;
	}

	public void setLeave_detail_url(String leave_detail_url) {
		this.leave_detail_url = leave_detail_url;
	}

	/********* -----------------封装------开始区域------------ ********/

	public NetWorkManager getNetWorkManager() {
		return networkMgr;
	}

	public static Api getApi() {
		return api;
	}
	
    public String getLink_id() {
        return link_id;
    }

    public void setLink_id(String link_id) {
        this.link_id = link_id;
    }

    public String getJump_module() {
        return jump_module;
    }

    public void setJump_module(String jump_module) {
        this.jump_module = jump_module;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

	public String getTitle_name() {
		return title_name;
	}

	public void setTitle_name(String title_name) {
		this.title_name = title_name;
	}

	public String getPushNoticeId() {
		return pushNoticeId;
	}

	public void setPushNoticeId(String pushNoticeId) {
		this.pushNoticeId = pushNoticeId;
	}

	public String getPushNoticeType() {
		return pushNoticeType;
	}

	public void setPushNoticeType(String pushNoticeType) {
		this.pushNoticeType = pushNoticeType;
	}

	public String getPushNoticeSubType() {
		return pushNoticeSubType;
	}

	public void setPushNoticeSubType(String pushNoticeSubType) {
		this.pushNoticeSubType = pushNoticeSubType;
	}

	public static A_0_App getInstance() {
		return mApp;
	}

	public boolean isRuning() {
		return mRuning;
	}

	public void setRuning(boolean runing) {
		this.mRuning = runing;
	}

	public Cpk_User_Authenticate getAuthen() {
		return authen;
	}

	public void setAuthen(Cpk_User_Authenticate authen) {
		this.authen = authen;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public Cpk_Student_Info getStudentInfo() {
		return studentInfo;
	}

	public void setStudentInfo(Cpk_Student_Info studentInfo) {
		this.studentInfo = studentInfo;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

	public Cpk_Version getVersion() {
		return version;
	}

	public void setVersion(Cpk_Version version) {
		this.version = version;
	}
	
	public Cpk_Device_Info getDevice_info() {
        return device_info;
    }

    public void setDevice_info(Cpk_Device_Info device_info) {
        this.device_info = device_info;
    }

    public String getSideReciverMessage() {
        return sideReciverMessage;
    }
    public void setSideReciverMessage(String sideReciverMessage) {
        this.sideReciverMessage = sideReciverMessage;
    }
    
    public String getLoginReciverMessage() {
        return loginReciverMessage;
    }
    public void setLoginReciverMessage(String loginReciverMessage) {
        this.loginReciverMessage = loginReciverMessage;
    }
    
    public static String getClickPushMsgReqPage() {
        return clickPushMsgReqPage;
    }
    public static void setClickPushMsgReqPage(String clickPushMsgReqPage) {
        A_0_App.clickPushMsgReqPage = clickPushMsgReqPage;
    }
    
    public boolean getSocket_login() {
        return socket_login;
    }
    public void setSocket_login(boolean socket_login) {
        this.socket_login = socket_login;
    }
    
	/********* -----------------封装------------结束区域------------ ********/

	/*************************** 融云聊天和图片加载 **********************************/

	/*
	 * 获得当前进程的名字
	 * 
	 * @param context
	 * 
	 * @return 进程号
	 */
	public static String getCurProcessName(Context context) {
		try {

			int pid = android.os.Process.myPid();

			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);

			for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
					.getRunningAppProcesses()) {

				if (appProcess.pid == pid) {
					return appProcess.processName;
				}
			}

		} catch (Exception e) {

		}
		return null;
	}

	public RongIM.LocationProvider.LocationCallback getLastLocationCallback() {
		return mLastLocationCallback;
	}

	public void setLastLocationCallback(
			RongIM.LocationProvider.LocationCallback lastLocationCallback) {
		this.mLastLocationCallback = lastLocationCallback;
	}

	class LocationProvider implements RongIM.LocationProvider {
		// 位置信息提供者:LocationProvider 的回调方法，打开第三方地图页面。
		@Override
		public void onStartLocation(Context context,
				RongIM.LocationProvider.LocationCallback callback) {
			/**
			 * demo 代码 开发者需替换成自己的代码。
			 */
			A_0_App.getInstance().setLastLocationCallback(callback);
			Intent intent = new Intent(context, Conversation_Location_Acy.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);// SOSO地图
		}
	}

	public static void initImageLoader(Context context) {
		// 缓存文件的目录
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				AppStrStatic.SD_PIC_CACHE);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.memoryCacheExtraOptions(480, 800)
				// max width, max height，即保存的每个缓存文件的最大长宽
				.threadPoolSize(3)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()) // 将保存的时候的URI名称用MD5加密
				//.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) //你可以通过自己的内存缓存实现
                .memoryCache(new WeakMemoryCache())
				.memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb sd卡(本地)缓存的最大值
				.tasksProcessingOrder(QueueProcessingType.LIFO)// 由原先的discCache
																// -> diskCache
				.diskCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				.imageDownloader(
						new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout
																				// (5
																				// s),
																				// readTimeout
																				// (30
																				// s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}

	public ImageLoader getimageLoader() {
		return ImageLoader.getInstance();
	}

	public DisplayImageOptions getOptions(int onLoading, int emptyUri,
			int onFail) {
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(onLoading).showImageForEmptyUri(emptyUri)
				.showImageOnFail(onFail) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)//默认是ARGB_8888，使用RGB_565会比使用ARGB_8888少消耗2倍的内
//                .displayer(new RoundedBitmapDisplayer(0))//不推荐用！！！！是否设置为圆角，弧度为多少,他会创建新的ARGB_8888格式的Bitmap对象；
				.build(); // 构建完成
		return options;
	}

    public static ImageOptions getBitmapUtils(Context context, int loding_image_id,int loading_falied_id,boolean circular) {
        ImageOptions options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_CENTER)
                .setCrop(true)
                // 设置 config 每一个像素所占大小
                .setConfig(Bitmap.Config.RGB_565)
                // 设置加载出错的图片
                .setFailureDrawableId(loading_falied_id)
                // 设置加载过程中的图片
                .setLoadingDrawableId(loding_image_id)
                // 是否是圆形的
                .setCircular(circular)
                // 设置是否使用内存缓存
                .setUseMemCache(true)
                // 设置是否渐入
                .setFadeIn(true)
                // 设置是否忽略gif
                .setIgnoreGif(false).build();
        return options;
    }

	public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(context.getPackageName())) {
				/*
				 * BACKGROUND=400 EMPTY=500 FOREGROUND=100 GONE=1000
				 * PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
				 */
				Log.i(context.getPackageName(), "此appimportace ="
						+ appProcess.importance
						+ ",context.getClass().getName()="
						+ context.getClass().getName());
				if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Log.i(context.getPackageName(), "处于后台"
							+ appProcess.processName);
					return true;
				} else {
					Log.i(context.getPackageName(), "处于前台"
							+ appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}

	/*************************** 信鸽推送 **********************************/
	
	/*************************** Dialog **********************************/
	/**
     * 得到 使用Log的权限
     * CoomixSetting文件内容：DEBUG=ON
     */
    private boolean getLogUsePermission() {
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator + "SetSchoolTeacher");
        Reader read;
        char[] b = null;
        int len = 0;
        String str = null;

        if (f.exists()) {
            try {
                read = new InputStreamReader(new FileInputStream(f));
                b = new char[1024];
                len = read.read(b);
                str = new String(b, 0, len);
                read.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (str != null && str.length() >= 8 && str.indexOf('=') == 5) {
                String debug = str.substring(0, 5);
                String debut_Values = str.substring(str.indexOf('=') + 1, str.length());
                if (debug.trim().equals("DEBUG")&& debut_Values.trim().equals("ON")) {
                    return true;
                }
            }
        }
        return false;
    }
    
	private ProgressDialog mProgrDialog;

	// 横向进度条dialog——检查升级使用
	public void showHorizontalDialog(Context con, String id, boolean canced) {
		if (con != null && !((Activity) con).isFinishing()) {
			mProgrDialog = new ProgressDialog(con, R.style.dialog_load);
			mProgrDialog.setMessage(id + " ... ");
			mProgrDialog.setIndeterminate(true);
			mProgrDialog.setCanceledOnTouchOutside(false);
			mProgrDialog.setCancelable(canced);
			if (((Activity)con) != null && mProgrDialog != null && !mProgrDialog.isShowing())
			    mProgrDialog.show();
			else
			    return;
		}
	}

	public void CancelHorizontalDialog(Context con) {
		if (con != null && !((Activity) con).isFinishing()) {
			if (mProgrDialog != null && mProgrDialog.isShowing()) {
				mProgrDialog.cancel();
				mProgrDialog = null;
			} else {
			    logE("mProgrDialog == null");
			}
		}
	}

	// 加载logo旋转的动画__加载更多时使用
	private LogoDialog mDialog = null;

	public void showProgreDialog(Context con, String msg, boolean cancel) {
		if (con != null && !((Activity) con).isFinishing()) {
			mDialog = new LogoDialog(con, R.style.Theme_dim_Dialog, msg,animalDrawable);
			mDialog.setCancelable(cancel);
			mDialog.setCanceledOnTouchOutside(false);
            if (((Activity)con) != null && mDialog != null && !mDialog.isShowing())
                mDialog.show();
			else
			    return;
		} else {
		    logE("Logo dialog isFinishing");
		}
	}

	// 取消Logo动画
	public void CancelProgreDialog(Context con) {
		if (con != null && !((Activity) con).isFinishing()) {
			if (mDialog != null && mDialog.isShowing()) {
				mDialog.closeDialog();
				mDialog.cancel();
				mDialog = null;
			} else {
			    logE("mDialog == null");
			}
		}
	}

	/*
     * 退出登录提示   场景：除了审核通过和融云其他设备挤掉之外的情况
     */
    public void showExitToast(final Context context,final String content) {
        if (content != null && !content.equals(""))
            PubMehods.showToastStr(context, content);
        clearUserSpInfo(false);
        context.startActivity(new Intent(context, A_3_0_Login_Acy_Teacher.class));
        ((Activity)context).finish();
        ((Activity)context).overridePendingTransition(R.anim.animal_push_right_in_normal, R.anim.animal_push_right_out_normal);
    }
    
    /*
     * 退出登录框   场景：审核通过，融云其他设备挤掉
     */
	public void showExitDialog(final Context context, final String content) {
		if (!((Activity) context).isFinishing()) {
			final GeneralDialog upDateDialog = new GeneralDialog(context,
					R.style.Theme_GeneralDialog);
			upDateDialog.setTitle(R.string.kicked_notice_title);
			upDateDialog.setContent(content);
			upDateDialog.showMiddleButton(R.string.pub_exit,
					new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							upDateDialog.cancel();
							clearUserSpInfo(false);
							context.startActivity(new Intent(context,A_3_0_Login_Acy_Teacher.class));
							((Activity) context).finish();
							((Activity) context).overridePendingTransition(
									R.anim.animal_push_right_in_normal,
									R.anim.animal_push_right_out_normal);
						}

					});
			upDateDialog.setCancelable(false);
			if (((Activity)context) != null && !((Activity)context).isFinishing()&& upDateDialog != null && !upDateDialog.isShowing())
				try {
					upDateDialog.show();
				} catch (Exception e) {

				}
			else
			    return;
		}
	}

    /*
     * 引导补全资料框   场景：未认证用户和其他用户进行提示
     */
   public void showPointDialog(final Context context,final String content,final boolean regedit_Enter) {
        if (!((Activity) context).isFinishing()) {
            final GeneralDialog upDateDialog = new GeneralDialog(context, R.style.Theme_GeneralDialog);
            upDateDialog.setTitle(R.string.pub_title);
            upDateDialog.setContent(content);
            upDateDialog.showLeftButton(R.string.pub_ignore,new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    upDateDialog.cancel();
                }
            });
            upDateDialog.showRightButton(R.string.str_going_right_now,new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    upDateDialog.cancel();
                    enter_Perfect_information(context,regedit_Enter);
                }
            });
            upDateDialog.setCancelable(true);
            if (((Activity)context) != null && upDateDialog != null && !upDateDialog.isShowing())
                upDateDialog.show();
            else
                return;
        }
    }
   
   //完善资料
   public void enter_Perfect_information(final Context context,boolean show_Title_Toast) {
       Intent intent = new Intent(context, A_3_3_Complete_marer_Acy.class);
       intent.putExtra("show_Title_Toast", show_Title_Toast);
       context.startActivity(intent);
      // ((Activity)context).overridePendingTransition(R.anim.animal_push_right_in_normal, R.anim.animal_push_right_out_normal);
   }

	/**
	 * 获取APK的包名
	 *
	 * @param apkPath
	 * @return
	 */
	public String getPackageName(String apkPath) {
		PackageInfo pi = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		String packageName = null;
		if (pi != null) {
			packageName = pi.packageName;
		}
		return packageName;
	}

	/**
	 * 获取APK版本名称(versionName)
	 *
	 * @param apkPath
	 * @return
	 */
	public String getVersionName(String apkPath) {
		PackageInfo pi = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		String versionName = null;
		if (pi != null) {
			versionName = pi.versionName;
		}
		return versionName;
	}

	/**
	 * 获取APK版本号(versionCode)
	 *
	 * @param apkPath
	 * @return
	 */
	public int getVersionCode(String apkPath) {
		PackageInfo pi = pm.getPackageArchiveInfo(apkPath,
				PackageManager.GET_ACTIVITIES);
		int versionCode = 1;
		if (pi != null) {
			versionCode = pi.versionCode;
		}
		return versionCode;
	}
   
	/*************************** Dialog **********************************/
	/********* -----------------退出程序------------开始------------ ********/
	public void addActivity(Activity activity) {
		// 添加Activity 到容器中
		activityList.add(activity);
	}

	public void addActivity_rongyun(Activity activity) {
		// 添加Activity 到容器中
		activityList_rongyun.add(activity);
	}

    /**
     * 
     * @Description: TODO(清除登录用户信息)
     * @param     设定文件
     * @return void    返回类型
     * @throws
     */
    public void clearUserSpInfo(boolean cancel_process) {
        if(A_0_App.getInstance() != null)
            A_0_App.getInstance().setStudentInfo(null);
        else
            exit(true);
        A_0_App.USER_TOKEN = "";
        A_0_App.USER_QUNIQID = "";
        A_0_App.USER_QUTOKEN = "";
        
        A_0_App.USER_UNIQID = "";
        A_0_App.USER_PHONE = "";
        
        A_0_App.USER_STATUS = "";
        A_0_App.USER_NAME = "";
        A_0_App.USER_POR_URL = "";
        A_0_App.USER_BANGDOU_URL = "";
		A_0_App.USER_SMS_URL = "";
        mPushState = 0;// 推送设置
        
        Editor editor = getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_INFO, MODE_PRIVATE).edit();
        editor.clear();  
        editor.commit();
        
        setSideReciverMessage("");
        setLoginReciverMessage("");
        exit(cancel_process);
    }
    
	/**
     * 
     * @Title: exit
     * @Description: TODO(关闭整个程序页面)
     * @param @param cancel_process  是否杀掉程序进程
     * @return void    返回类型
     * @throws
     */
	public void exit(boolean cancel_process) {
	    if (BackService.getInstance() != null)
            BackService.getInstance().stopHeartBeat();
        if (RongIMClient.getInstance() != null)
            RongIMClient.getInstance().logout();
        A_0_App.getInstance().setRuning(false);
        
		for (int i = 0; i < activityList.size(); i++) {
			Activity activity = activityList.get(i);
			activity.finish();
		}
		if (cancel_process)
			System.exit(0);
	}

	public void exit_rongyun(boolean cancel_process) {

		for (int i = 0; i < activityList_rongyun.size(); i++) {
			Activity activity = activityList_rongyun.get(i);
			activity.finish();
		}

	}

	// 忘记密码
	public void addReSetPwdAcy(Activity activity) {
		// 添加Activity 到容器中
		resetPwdList.add(activity);
	}

	public void exitResetProcess() {
		for (int i = 0; i < resetPwdList.size(); i++) {
			Activity activity = resetPwdList.get(i);
			activity.finish();
		}
	}

	// 注册流程
	public void addRegisterPwdAcy(Activity activity) {
		// 添加Activity 到容器中
		registerList.add(activity);
	}

	public void exitRegisterProcess() {
		for (int i = 0; i < registerList.size(); i++) {
			Activity activity = registerList.get(i);
			activity.finish();
		}
	}

    public void checkUpdateVersion(Context context) {
        if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
            if (A_0_App.getInstance().getVersion() == null ||A_0_App.getInstance().getVersion().getDownloadUrl() == null) {
                if(PubMehods.checkUpdate(A_0_App.getInstance().getUpdateTime(),System.currentTimeMillis())){
                    Update_App.check_upDate_App(context,false);
                }
            }
        }
    }
    
	/********* -----------------退出程序-----------结束------------ ********/

	public void callSb(final Context con, String name, final String no, final PhoneCallBack callBack) {
		final GeneralDialog upDateDialog = new GeneralDialog(con,
				R.style.Theme_GeneralDialog);
		upDateDialog.setTitle(name);
		upDateDialog.setContent(no);
		upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
			@Override
			public void onClick(View v) {
				upDateDialog.cancel();
			}
		});
		upDateDialog.showRightButton(R.string.pub_call, new OnClickListener() {
			@Override
			public void onClick(View v) {
				upDateDialog.cancel();
				callBack.sPermission();
//                PubMehods.callPhone(con, no);
			}
		});
		upDateDialog.show();
	}
	public interface PhoneCallBack{
		void sPermission();
	}

	public void PermissionToas(String name, Activity activity){
		Toast.makeText(activity, "在设置-应用-" + getString(R.string.app_name) + "-权限中开启" + name + "权限，以正常使用"+A_0_App.APP_NAME+"相关功能服务", Toast.LENGTH_SHORT).show();

	}

//	public void callSb(final Context con, String name, final String no) {
//		final GeneralDialog upDateDialog = new GeneralDialog(con,
//				R.style.Theme_GeneralDialog);
//		upDateDialog.setTitle(name);
//		upDateDialog.setContent(no);
//		upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				upDateDialog.cancel();
//			}
//		});
//		upDateDialog.showRightButton(R.string.pub_call, new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				upDateDialog.cancel();
//				PubMehods.callPhone(con, no);
//			}
//		});
//		upDateDialog.show();
//	}

	public int setSideBar(int sideBar) {
		return DensityUtils.dip2px(A_0_App.this, 9);
	}
	
    public static void logE(String msg) {
        LogUtils.logE("A_0_App", "A_0_App==>" + msg);
    }
}
