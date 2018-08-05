package com.yuanding.schoolteacher;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.yuanding.schoolteacher.bean.CpkUserModel;
import com.yuanding.schoolteacher.bean.Cpk_Device_Info;
import com.yuanding.schoolteacher.service.Api.InterLogin;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.utils.download.ApkDownloader;
import com.yuanding.schoolteacher.utils.download.AsyncDownLoadManager;
import com.yuanding.schoolteacher.utils.download.WebResource;

import io.dcloud.SDK_WebView;

/**
 * @author Jiaohaili ――――――
 * @version 创建时间：2015年11月7日 下午4:24:15 登录、注册、找回密码
 */
public class A_3_0_Login_Acy_Teacher extends Activity implements AsyncDownLoadManager.OnDownLoadProgrossListener{

    private SharedPreferences mSharePre;
	private EditText tv_login_account;// 用户名
	private EditText tv_login_pwd;// 用户密码

	private Button btn_login_dd, btn_regedit;

	private TextView tv_forgit_pwd;// 忘记密码
	private ImageView iv_login_show_pwd;
	private Boolean showPwd = false;//密码状态

	private LinearLayout liner_accont,liner_pwd;
	
	A_0_App app = A_0_App.getInstance();
	private static A_3_0_Login_Acy_Teacher instance;
	public static A_3_0_Login_Acy_Teacher getInstance() {
		return instance;
	}
	private int enter_type = -1; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		instance = this;
	    A_0_App.getInstance().addRegisterPwdAcy(this);
        A_0_App.getInstance().addActivity(this);
        if(A_0_App.getInstance().isRuning()){
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
	    mSharePre = getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_PHONE,
                  Activity.MODE_PRIVATE);
		liner_accont = (LinearLayout)findViewById(R.id.liner_accont);
		liner_pwd = (LinearLayout)findViewById(R.id.liner_pwd);
		tv_forgit_pwd = (TextView) findViewById(R.id.tv_forgit_pwd);
		iv_login_show_pwd = (ImageView)findViewById(R.id.iv_login_show_pwd_hh);
		tv_login_account = (EditText) findViewById(R.id.tv_login_account);
		tv_login_pwd = (EditText) findViewById(R.id.tv_login_pwd);
		btn_login_dd = (Button) findViewById(R.id.btn_login_dd);
		btn_regedit = (Button) findViewById(R.id.btn_regedit);
		
		btn_regedit.setOnClickListener(onClick);
		tv_forgit_pwd.setOnClickListener(onClick);
		btn_login_dd.setOnClickListener(onClick);
		
		iv_login_show_pwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 显示密码
				if (!showPwd) {
					//显示密码
					showPwd = true;
					tv_login_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
					iv_login_show_pwd.setBackgroundResource(R.drawable.icon_login_password_open);
				} else {
					//隐藏密码
					showPwd = false;
					tv_login_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
					iv_login_show_pwd.setBackgroundResource(R.drawable.icon_login_password_close);
				}
			}
		});
		
		liner_accont.setBackgroundResource(R.drawable.login_input_normal_bg);
		liner_pwd.setBackgroundResource(R.drawable.login_input_normal_bg);
		
		tv_login_account.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1){
					liner_accont.setBackgroundResource(R.drawable.login_input_hover_bg);
				}else{
					liner_accont.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});
		
		tv_login_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				
				if(arg1){
					liner_pwd.setBackgroundResource(R.drawable.login_input_hover_bg);
				}else{
					liner_pwd.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});
		
		String temp_text = getUserPHone();
        if (tv_login_account != null && mSharePre != null && !temp_text.equals("")) {
            tv_login_account.setText(temp_text);
        }

        getDeviceToken();
		A_0_App.getInstance().checkUpdateVersion(A_3_0_Login_Acy_Teacher.this);
    }
    
    /*
     * 重新获取设备Token
     */
    private void getDeviceToken() {
        String channelId = A_0_App.getInstance().getChannelId();
        if (channelId == null || channelId.equals("")) {
            Log.e("test", "教师——获取Device_Token失败，重新获取");
            PushManager.startWork(A_3_0_Login_Acy_Teacher.this, PushConstants.LOGIN_TYPE_API_KEY,AppStrStatic.API_KEY_BAI_DU_PUSH);// 重读百度云
        }
    }

	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn_regedit:
                // 注册页面
                intent.setClass(A_3_0_Login_Acy_Teacher.this, A_3_2_Register_Main_0_Acy.class);
				startActivity(intent);
				overridePendingTransition(R.anim.animal_push_left_in_normal,R.anim.animal_push_left_out_normal);
				break;

			case R.id.tv_forgit_pwd:
				//忘记密码页面
				intent.setClass(A_3_0_Login_Acy_Teacher.this, A_3_1_1_Forget_Main_Acy.class);
				intent.putExtra("acy_type", 2);
				startActivity(intent);
				overridePendingTransition(R.anim.animal_push_left_in_normal,R.anim.animal_push_left_out_normal);
				break;
			case R.id.btn_login_dd:
				// 登录
			    if(PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)){
			        logE("重复点击登录按钮");
			        return;
			    }
			    
				String str_phone = tv_login_account.getText().toString().trim();
				String str_pwd = tv_login_pwd.getText().toString().trim();
				
				if (str_phone == null || str_phone.length() <= 0 || str_phone.equals("")) {
					PubMehods.showToastStr(A_3_0_Login_Acy_Teacher.this, "请输入手机号");
					return;
				}

				if (!PubMehods.isMobileNO(str_phone)) {
					PubMehods.showToastStr(A_3_0_Login_Acy_Teacher.this, "请输入正确的手机号");
					return;
				}

				if (str_pwd == null || str_pwd.length() <= 0 || str_pwd.equals("")) {
					PubMehods.showToastStr(A_3_0_Login_Acy_Teacher.this, "请输入密码");
					return;
				}
				
				String channelId = A_0_App.getInstance().getChannelId();
				String clientid = A_0_App.getInstance().getClientid();
				if (channelId == null || channelId.equals("")) {
                    channelId = "";
                    Log.e("test", "教师获取channelId失败");
                }
                if (clientid == null || clientid.equals("")) {
                    clientid = "";
                    Log.e("test", "教师获取clientid失败");
                }
                userLogin(clientid, channelId, str_phone, str_pwd);
			}
		}
	};
	
    //status状态值 ：0：审核中    1：未激活 (库中)  2：已认证   3：已锁定   4：新用户 （非库）  5：审核失败
	private void userLogin(String ClientID,String device_token,final String str_phone, String str_pwd) {
        String device_info = getToJsonDevice_Info(A_0_App.getInstance().getDevice_info());
        A_0_App.getInstance().showProgreDialog(A_3_0_Login_Acy_Teacher.this, "正在登录,请稍候",true);
		A_0_App.getApi().Login(A_3_0_Login_Acy_Teacher.this, ClientID,device_token, str_phone, str_pwd,device_info,new InterLogin() {
			
			@Override
			public void onSuccess(CpkUserModel user) {
				if (isFinishing())
					return;
				app.CancelProgreDialog(A_3_0_Login_Acy_Teacher.this);
				if(user.getTeacher_status().equals("5")){
					PubMehods.showToastStr(A_3_0_Login_Acy_Teacher.this, "您的账户审核失败,请重新修改提交");
				}
				A_0_App.getInstance().saveUserLoginInfo(user);
				saveUserPhone(str_phone);
				saveChannelId(user.getDevice_token(), user.getGetui_client_id());
				getUserSetPush();

                if (A_0_App.getInstance().WB_BangDou_Tag == 0){//正常登录
                    A_1_Start_Acy.pushOperating(A_3_0_Login_Acy_Teacher.this,A_Main_Acy.class);
                }else if (A_0_App.getInstance().WB_BangDou_Tag == 2){//短信账户
					Intent intent = new Intent();
					String url = user.getSms_url();
					if(url == null || url.equals("")){
						PubMehods.showToastStr(A_3_0_Login_Acy_Teacher.this, "数据请求失败，请重试");
						return;
					}
					intent.setClass(A_3_0_Login_Acy_Teacher.this, Pub_WebView_Load_Acy.class);
					intent.putExtra("acy_type", 2);
					intent.putExtra("title_text", "短信账户");
					intent.putExtra("url_text", url);
					intent.putExtra("tag_show_refresh_btn", "1");
					intent.putExtra("tag_skip", "1");
					intent.putExtra("tag_show_help_btn","1");
					startActivity(intent);
				}else if (A_0_App.getInstance().WB_BangDou_Tag == 1){//邦豆URl
                    Intent intent = new Intent();
                    String url = user.getBang_url();
                    if(url == null || url.equals("")){
                        PubMehods.showToastStr(A_3_0_Login_Acy_Teacher.this, "数据请求失败，请重试");
                        return;
                    }
                    intent.setClass(A_3_0_Login_Acy_Teacher.this, Pub_WebView_Load_Acy.class);
					intent.putExtra("acy_type", 1);
                    intent.putExtra("title_text", "我的邦豆");
                    intent.putExtra("url_text", url);
                    intent.putExtra("tag_show_refresh_btn", "1");
                    intent.putExtra("tag_skip", "1");
					intent.putExtra("tag_show_help_btn","0");
                    startActivity(intent);
                }else if(A_0_App.getInstance().WB_BangDou_Tag == 3){//迎新管理
					Intent courseIntent = new Intent(A_3_0_Login_Acy_Teacher.this, SDK_WebView.class);
                    courseIntent.putExtra("acy_type",2);
					startActivity(courseIntent);
					A_0_App.getInstance().showProgreDialog(A_3_0_Login_Acy_Teacher.this, "", false);
				}else if (A_0_App.getInstance().WB_BangDou_Tag == 4){//邦豆URl
					Intent intent = new Intent();
					String url = user.getBang_url();
					if(url == null || url.equals("")){
						PubMehods.showToastStr(A_3_0_Login_Acy_Teacher.this, "数据请求失败，请重试");
						return;
					}
					intent.setClass(A_3_0_Login_Acy_Teacher.this, Pub_WebView_Banner_Acy.class);
					intent.putExtra("acy_type", 4);
					intent.putExtra("title_text", "教师请销假");
					intent.putExtra("url_text", url);
					intent.putExtra("tag_show_refresh_btn", "1");
					intent.putExtra("tag_skip", "1");
					startActivity(intent);
				}
                A_0_App.getInstance().ReadGestureSet();//重读手势设置
				finish();
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
                app.CancelProgreDialog(A_3_0_Login_Acy_Teacher.this);
                PubMehods.showToastStr(A_3_0_Login_Acy_Teacher.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	
    private String getToJsonDevice_Info(Cpk_Device_Info device) {
        JSON json = new JSON() {
        };
        if (device == null) {
            return "";
        }
        return json.toJSONString(device);
    }

    //获取用户设置的推送状态
    private void getUserSetPush() {
        A_0_App.getInstance().mPushState =  A_0_App.getInstance().getUserPushSetState();
        A_0_App.getInstance().mQuickSwitchState =  A_0_App.getInstance().getUserQuickSetState();
    }
    
	private void startAcy(Context packageContext, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(packageContext, cls);
		startActivity(intent);
	}

    private String getUserPHone() {
        String phone = mSharePre.getString(AppStrStatic.SHARE_PREFER_USER_PHONE, "");
        return phone;
    }
    
    private void saveUserPhone(String phone) {
        Editor editor = getSharedPreferences(
                AppStrStatic.SHARE_PREFER_USER_PHONE, MODE_PRIVATE).edit();
        editor.putString(AppStrStatic.SHARE_PREFER_USER_PHONE, phone);
        editor.commit();
    }
    
    private void saveChannelId(String device_token,String getui_client_id) {
        if(device_token != null && !"".equals(device_token) ){
            A_0_App.getInstance().saveChannelId(AppStrStatic.KEY_USER_LOGIN_CHANNEL_ID,device_token);
        }
        if(getui_client_id != null && !"".equals(getui_client_id) ){
            A_0_App.getInstance().saveChannelId(AppStrStatic.KEY_USER_LOGIN_CLIENT_ID,getui_client_id);
        }
    }

	/***************************************升级*****************************************************/
	private static ProgressDialog updateDialog;
	public void startDownloadApp(String url) {
		if (updateDialog == null) {
			updateDialog = new ProgressDialog(this);
		}
		updateDialog.setIcon(android.R.drawable.ic_dialog_info);
		updateDialog.setTitle("温馨提示");
//        updateDialog.setCancelable(false);
		updateDialog.setCanceledOnTouchOutside(false);
		updateDialog.setMessage("正在下载");
		updateDialog.setMax(100);
		ApkDownloader downloader = new ApkDownloader(A_3_0_Login_Acy_Teacher.this, url,R.drawable.ic_launcher,
				"", A_3_0_Login_Acy_Teacher.this);
		downloader.downLoadApp();
		//updateDialog.show();
	}

	public void onUpdataDownLoadProgross(WebResource resource, int progross) {
		updateDialog.setMessage("正在下载..." + progross + " %");
	}

	@Override
    protected void onResume() {

        super.onResume();
    }
    
    public static void logD(String msg) {
        com.yuanding.schoolteacher.utils.LogUtils.logD("A_3_0_Login_Acy", "A_3_0_Login_Acy==>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolteacher.utils.LogUtils.logE("A_3_0_Login_Acy", "A_3_0_Login_Acy==>" + msg);
    }
	
}
