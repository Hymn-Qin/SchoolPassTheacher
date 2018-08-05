package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yuanding.schoolteacher.bean.Cpk_Student_Info;
import com.yuanding.schoolteacher.service.Api.InterStudentInfo;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.CircleImageView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月2日 下午8:29:57
 * 个人账户信息 数据缓存为一周
 */
public class A_Main_My_Account_Acy extends Activity {
	private RelativeLayout rel_myaccount_no;
	private LinearLayout liner_myaccount_class,liner_myaccount_collect,liner_myaccount_set,lienr_wanshan_message,liner_myaccount_invite
            ,liner_myaccount_sms;
	private TextView tv_account_name,tv_account_no,account_renzheng_state,account_my_sms_count;
	private TextView account_phone_no,account_my_banji;
	private CircleImageView iv_account_por;
	private static A_Main_My_Account_Acy instance;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	//private BitmapUtils bitmapUtils;
	private boolean firstLoad = false;//第一次进入
	private ACache  maACache;
	private JSONObject jsonObject;
	private Cpk_Student_Info mstudent;
	private LinearLayout liner_myaccount_bangdou;
	private LinearLayout ll_account_renzheng;
	private TextView tv_my_bangdou;
	private ImageView iv_account_renzheng;
	private ImageView iv_sysnotice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_account);
		A_0_App.getInstance().addActivity(this);
//		setTitleText(R.string.str_my_account_title);
		instance = this;
		firstLoad = true;
		iv_account_renzheng=(ImageView) findViewById(R.id.iv_myaccount_rezheng);
		ll_account_renzheng=(LinearLayout) findViewById(R.id.ll_myaccount_rezheng);
		rel_myaccount_no = (RelativeLayout) findViewById(R.id.rel_myaccount_no);
		
		//lienr_wanshan_message = (LinearLayout) findViewById(R.id.lienr_wanshan_message);
		liner_myaccount_class = (LinearLayout) findViewById(R.id.liner_myaccount_class);
		liner_myaccount_collect = (LinearLayout) findViewById(R.id.liner_myaccount_collect);
		liner_myaccount_set = (LinearLayout) findViewById(R.id.liner_myaccount_set);
		liner_myaccount_bangdou=(LinearLayout) findViewById(R.id.liner_myaccount_bangdou);
        liner_myaccount_sms=(LinearLayout) findViewById(R.id.liner_myaccount_sms);
		liner_myaccount_invite=(LinearLayout) findViewById(R.id.liner_myaccount_invite);
		iv_account_por = (CircleImageView)findViewById(R.id.iv_account_por_tag);
		tv_account_name = (TextView)findViewById(R.id.tv_account_name);
		tv_account_no = (TextView)findViewById(R.id.tv_account_no);
		account_renzheng_state = (TextView)findViewById(R.id.account_renzheng_state);
		account_phone_no = (TextView)findViewById(R.id.account_phone_no);
		account_my_banji = (TextView)findViewById(R.id.account_my_banji);
		tv_my_bangdou = (TextView)findViewById(R.id.account_my_bangdou);
        account_my_sms_count = (TextView)findViewById(R.id.account_my_sms_count);
		iv_sysnotice = (ImageView)findViewById(R.id.iv_sysnotice);
		
		ll_account_renzheng.setOnClickListener(onclick);
		rel_myaccount_no.setOnClickListener(onclick);
		liner_myaccount_class.setOnClickListener(onclick);
		liner_myaccount_collect.setOnClickListener(onclick);
		liner_myaccount_set.setOnClickListener(onclick);
		//lienr_wanshan_message.setOnClickListener(onclick);
		liner_myaccount_bangdou.setOnClickListener(onclick);
        liner_myaccount_sms.setOnClickListener(onclick);
		liner_myaccount_invite.setOnClickListener(onclick);
		iv_account_por.setOnClickListener(onclick);
		iv_sysnotice.setOnClickListener(onclick);
		
		//bitmapUtils=A_0_App.getBitmapUtils(this,R.drawable.i_person_img,R.drawable.i_person_img);
        imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.i_default_por_120)
				.showImageForEmptyUri(R.drawable.i_default_por_120) 
				.showImageOnFail(R.drawable.i_default_por_120) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new RoundedBitmapDisplayer(0)) // 设置成圆角图片
				.build(); // 构建完成
		
		mstudent = new Cpk_Student_Info();
		readCache();
		
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
    //读取缓存
	private void readCache() {
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_my_account+A_0_App.USER_UNIQID);
		if (jsonObject != null) {
		    mstudent = JSON.parseObject(jsonObject + "", Cpk_Student_Info.class);
			showInfo(mstudent);
		}
		updateInfoToRongYun();
	}
    
	private void showInfo(Cpk_Student_Info student) {
	    mstudent = student;
        if(A_0_App.USER_UNIQID.equals(student.getUniqid())){
            A_0_App.getInstance().updateUserLoginInfo(student.getTeacher_status(),student.getUniqid(),student.getPhone(),
                    student.getName(), student.getPhoto_url());
        }
        A_0_App.getInstance().setStudentInfo(student);
        String uri = student.getPhoto_url();

        if(iv_account_por.getTag() == null){
            PubMehods.loadServicePic(imageLoader,uri,iv_account_por, options);
            iv_account_por.setTag(uri);
        }else{
            if(!iv_account_por.getTag().equals(uri)){
                PubMehods.loadServicePic(imageLoader,uri,iv_account_por, options);
                iv_account_por.setTag(uri);
            }
        }
        //bitmapUtils.display(iv_account_por, student.getPhoto_url());
        // 0：审核中  2：已认证 
        String auther = student.getTeacher_status();
        if (auther.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)) {
            tv_account_name.setText("访客");
            tv_account_no.setText("");
        } else {
            tv_account_name.setText(student.getName());
            tv_account_no.setText("工号：" +  student.getSn_number());
        }
        if (auther.equals("0")){
            account_renzheng_state.setText("审核中");
            iv_account_renzheng.setBackgroundResource(R.drawable.icon_account_no_renzheng);
        }
        else if (auther.equals("2")){
            account_renzheng_state.setText("已认证");
            iv_account_renzheng.setBackgroundResource(R.drawable.icon_account_renzheng);
        }
        else if (auther.equals("5")){
            account_renzheng_state.setText("审核失败");
            iv_account_renzheng.setBackgroundResource(R.drawable.icon_account_no_renzheng);
        }else if (auther.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
            account_renzheng_state.setText("未认证");
            iv_account_renzheng.setBackgroundResource(R.drawable.icon_account_no_renzheng);
        }
        account_phone_no.setText(student.getPhone());
        account_my_banji.setText(student.getOrgan_name());
        tv_my_bangdou.setText(student.getBang_total());
        ll_account_renzheng.setVisibility(View.VISIBLE);

        if (student.getSms_display() != null && student.getSms_display().equals("1")) {
            liner_myaccount_sms.setVisibility(View.VISIBLE);
            account_my_sms_count.setText(student.getSms_num() +"条");
        } else {
            liner_myaccount_sms.setVisibility(View.GONE);
        }
    }

	OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.iv_account_por_tag:
				//完善信息
				if (A_0_App.getInstance().getStudentInfo() == null) {
					PubMehods.showToastStr(A_Main_My_Account_Acy.this,R.string.error_title_net_error);
					return;
				}
				A_0_App.getInstance().enter_Perfect_information(A_Main_My_Account_Acy.this,false);
				break;
			case R.id.ll_myaccount_rezheng:
				//认证
				intent.setClass(A_Main_My_Account_Acy.this,B_Account_Certivication_Temp_Acy.class);
				startActivity(intent);
				break;
			case R.id.rel_myaccount_no:
				//手机号
				intent.setClass(A_Main_My_Account_Acy.this, A_3_5_Change_No_Acy.class);
				startActivity(intent);
				break;
			case R.id.liner_myaccount_class:
				//我的班级
                if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)) {
                    PubMehods.showToastStr(A_Main_My_Account_Acy.this,R.string.str_no_certified_open);
                    return;
                }
                if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)||
                        A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
                    A_0_App.getInstance().enter_Perfect_information(A_Main_My_Account_Acy.this,true);
                    return;
                }
			    if (A_0_App.USER_STATUS.equals("2")) {
    				if (A_0_App.USER_QUNIQID != null&&A_0_App.USER_QUNIQID.length() > 0) {
    					intent.setClass(A_Main_My_Account_Acy.this,B_Mess_Group_Info.class);
    					intent.putExtra("uniqid", A_0_App.USER_QUNIQID);
    					startActivity(intent);
    				}else{
                        PubMehods.showToastStr(A_Main_My_Account_Acy.this, R.string.str_title_no_group);
                    }
			    }
				break;
			case R.id.liner_myaccount_collect:
				//我的收藏
				intent.setClass(A_Main_My_Account_Acy.this, B_Pub_Side_NoDo_Action.class);
				intent.putExtra("title", "我的收藏");
				intent.putExtra("side_enter_type",3);
				startActivity(intent);
				break;
			case R.id.liner_myaccount_set:
				//我的设置
				intent.setClass(A_Main_My_Account_Acy.this, B_Account_My_Set_Acy.class);
				startActivity(intent);
				break;
			case R.id.liner_myaccount_bangdou:
                //我的邦豆
                if(A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)){
                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, R.string.str_no_certified_open);
                    return;
                }
                if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
                        ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
                    A_0_App.getInstance().enter_Perfect_information(A_Main_My_Account_Acy.this,true);
                    return;
                }
                String url = A_0_App.USER_BANGDOU_URL;
                logD(url + "=邦豆");
                if(url == null || url.equals("")){
                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, "数据请求失败，请重试");
                    return;
                }
                intent.setClass(A_Main_My_Account_Acy.this, Pub_WebView_Load_Acy.class);
                intent.putExtra("acy_type", 1);
                intent.putExtra("title_text", "我的邦豆");
                intent.putExtra("url_text", url);
                intent.putExtra("tag_show_refresh_btn", "1");
                intent.putExtra("tag_skip", "1");
                intent.putExtra("tag_show_help_btn","0");
                startActivity(intent);
				break;
            case R.id.liner_myaccount_sms:
                //我的短信账户
                if(A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)){
                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, R.string.str_no_certified_open);
                    return;
                }
                if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
                        ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
                    A_0_App.getInstance().enter_Perfect_information(A_Main_My_Account_Acy.this,true);
                    return;
                }
                String url_sms = A_0_App.USER_SMS_URL;
                logD(url_sms + "=短信账户");
                if(url_sms == null || url_sms.equals("")){
                    url_sms = mstudent.getSms_url();
                }
                if(url_sms == null || url_sms.equals("")){
                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, "数据请求失败，请重试");
                    return;
                }
                intent.setClass(A_Main_My_Account_Acy.this, Pub_WebView_Load_Acy.class);
                intent.putExtra("acy_type", 2);
                intent.putExtra("title_text", getResources().getString(R.string.str_my_sms_acc));
                intent.putExtra("url_text", url_sms);
                intent.putExtra("tag_show_refresh_btn", "1");
                intent.putExtra("tag_skip", "1");
                intent.putExtra("tag_show_help_btn","1");
                startActivity(intent);
                break;
			case R.id.liner_myaccount_invite:
				//我的邀请人
                if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
                        ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
                    A_0_App.getInstance().enter_Perfect_information(A_Main_My_Account_Acy.this,true);
                    return;
                }
				intent.setClass(A_Main_My_Account_Acy.this, B_Account_Invite_Main.class);
				startActivity(intent);
				break;
			case R.id.iv_sysnotice:
//                if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
//                        ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)
//                        ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
//                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, R.string.str_no_certified_open);
//                    return;
//                }
				intent.setClass(A_Main_My_Account_Acy.this, B_Account_SystemNotice_Main.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		}
	};

	 //读取用户信息
     private void readUserInfo(final boolean firstload) {
		A_0_App.getApi().getStudentInfo(A_Main_My_Account_Acy.this, A_0_App.USER_UNIQID,
				A_0_App.USER_TOKEN, new InterStudentInfo() {
					@Override
					public void onSuccess(Cpk_Student_Info student) {
                        if(isFinishing())
                            return;
                        showInfo(student);
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        if (msg.equals(AppStrStatic.TAG_USER_IS_DELETE)) {
                            A_0_App.getInstance().showExitDialog(A_Main_My_Account_Acy.this,getResources().getString(R.string.str_user_is_delete));
                            PubMehods.showToastStr(A_Main_My_Account_Acy.this, "该用户已删除");
                        } else {
                            if (jsonObject == null) {
                                ll_account_renzheng.setVisibility(View.GONE);
                            } else {
                                ll_account_renzheng.setVisibility(View.VISIBLE);
                            }
                            if (firstload) {
                                PubMehods.showToastStr(A_Main_My_Account_Acy.this,R.string.error_title_net_error);
                            }
                            
                        }
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}
     
    
	public static A_Main_My_Account_Acy getInstace(){
    	 return instance;
     }
     
	private void updateInfoToRongYun(){
		UpdateInfoToRongYun updateTextTask = new UpdateInfoToRongYun(this);
        updateTextTask.execute();
    }

    class UpdateInfoToRongYun extends AsyncTask<Void,Integer,Integer>{
        private Context context;
        UpdateInfoToRongYun(Context context) {
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
        }
        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {
        	readUserInfo(firstLoad);
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
//            logD("上传融云数据完毕");
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        	
        }
    }
    
    /**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logD("A_Main_My_Account_Acy监听=教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logD("A_Main_My_Account_Acy监听=教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(A_Main_My_Account_Acy.this,getResources().getString(R.string.token_timeout));
                    break;
                case CONNECTING:// 连接中。
                    A_Main_My_Message_Acy.logD("A_Main_My_Account_Acy监听=教师——connectRoogIm()，融云连接中");
                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。
                    A_Main_My_Message_Acy.logD("A_Main_My_Account_Acy监听=教师——connectRoogIm()，融云连接网络不可用");
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    A_Main_My_Message_Acy.logD("A_Main_My_Account_Acy监听=教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                    class LooperThread extends Thread {
                        public void run() {
                            Looper.prepare();
                            A_0_App.getInstance().showExitDialog(A_Main_My_Account_Acy.this, AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		if (!firstLoad) {
			readCache();
		} else {
			firstLoad = false;
		}
		
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
    public static void logD(String msg) {
        com.yuanding.schoolteacher.utils.LogUtils.logD("A_Main_My_Account_Acy", "A_Main_My_Account_Acy=教师=>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolteacher.utils.LogUtils.logE("A_Main_My_Account_Acy", "A_Main_My_Account_Acy=教师=>" + msg);
    }
}
