package com.yuanding.schoolteacher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.System;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.parser.deserializer.Jdk8DateCodec;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.LogUtils;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.gestruepwd.LocusPassWordView;
import com.yuanding.schoolteacher.view.gestruepwd.LocusPassWordView.OnCompleteListener;
/**
 * 
* @ClassName: B_Side_Notice_Gesture_Verifcation
* @Description: TODO(验证手势密码的正确性)公用页面
* @author Jiaohaili 
* @date 2016年3月11日 下午2:23:25
*
 */
public class B_Side_Notice_Gesture_Verifcation extends Activity {
    
    private boolean firstLoad;
    public  LocusPassWordView lpwv;
    private TextView tv_reset_pwd, tv_forget_pwd, tv_gesture_verivication;
    private int acy_Enter_Type = 0;//1表示为通知页面进入，2 表示设置页面进入从开到关,3重置密码进入 4 消息页面快捷方式进入
    private Animation shake;
    private Button btn_gesture_back;
    private ImageView iv_account_por;
    private TextView tv_account_name;
    private ImageLoader imageLoader;
	private DisplayImageOptions options;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_side_notice_gesture);
		firstLoad = true;
		acy_Enter_Type = getIntent().getExtras().getInt("enter_acy");
        lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
        tv_reset_pwd = (TextView) findViewById(R.id.tv_reset_pwd);
        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
        tv_gesture_verivication = (TextView) findViewById(R.id.tv_gesture_verivication);
        btn_gesture_back=(Button) findViewById(R.id.gesture_back);
        iv_account_por=(ImageView) findViewById(R.id.iv_account_por_tag);
        tv_account_name=(TextView) findViewById(R.id.tv_account_name);
        imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.i_default_por_120)
				.showImageForEmptyUri(R.drawable.i_default_por_120) 
				.showImageOnFail(R.drawable.i_default_por_120) // 设置图片加载或解码过程中发生错误显示的图片
				.cacheInMemory(true) // 设置下载的图片是否缓存在内存中
				.cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
				.displayer(new RoundedBitmapDisplayer(0)) // 设置成圆角图片
				.build(); // 构建完成
		String uri = A_0_App.USER_POR_URL;
		if(iv_account_por.getTag() == null){
			PubMehods.loadServicePic(imageLoader,uri,iv_account_por, options);
		    iv_account_por.setTag(uri);
		}else{
		    if(!iv_account_por.getTag().equals(uri)){
		    	PubMehods.loadServicePic(imageLoader,uri,iv_account_por, options);
                iv_account_por.setTag(uri);
		    }
		}
		tv_account_name.setText(A_0_App.USER_NAME);
        btn_gesture_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		tv_forget_pwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {//忘记密码之后重设状态
			    A_0_App.getInstance().clearNoticeState();
				exit_login();
			}
		});
		
		tv_reset_pwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			    resetPwd();
			}
		});
		
		shake = AnimationUtils.loadAnimation(B_Side_Notice_Gesture_Verifcation.this, R.anim.animal_login_shake);
		logD(A_0_App.getInstance().mFalseAllCount+"oncreate 失败次数");
		LoadAcyData();
        
		lpwv.setOnCompleteListener(new OnCompleteListener() {
			@Override
			public void onComplete(String mPassword) {
                if (A_0_App.getInstance().mFalseAllCount >= AppStrStatic.INTERVAL_NOTCIE_FORGET_COUNT) {
                    int time = judge_Error_Time();
                    if(time > 0){
                        lpwv.disableTouch();
                        tv_gesture_verivication.setText(strTimeRemindM((time)));
                    }else{
                        tv_gesture_verivication.setText("请输入手势密码");
                    	tv_gesture_verivication.setVisibility(View.GONE);
                        lpwv.enableTouch();//可以点击
                        A_0_App.getInstance().clearNoticeErrorValues();//清除之前的错误次数
                    }
                }else{//enter_acy_type,首页快捷方式进入为1，身边通知进入为2
                    if (lpwv.verifyPassword(mPassword)) {
                        A_0_App.getInstance().clearNoticeErrorValues();//清除之前的错误次数
                        lpwv.enableTouch();//可以点击
                        if (acy_Enter_Type == 1) {//1表示为通知页面进入
                            Intent intent=new Intent(B_Side_Notice_Gesture_Verifcation.this,B_Side_Notice_Main.class);
                            intent.putExtra("enter_acy_type", 2);
                            startActivity(intent);
                        } else if (acy_Enter_Type == 2) {//2 表示设置页面进入（关闭验证用）
                            A_0_App.getInstance().clearNoticeState();
                        }else if(acy_Enter_Type == 3){
                            Intent intent=new Intent(B_Side_Notice_Gesture_Verifcation.this, B_Side_Notice_GestureSet_Setting.class);
                            intent.putExtra("enter_acy", 3);
                            startActivity(intent);
                        }else if(acy_Enter_Type == 4){  //消息页面快捷方式进入
                        	switch (A_0_App.getInstance().getmCurrentFastIconClickType()) {
        					case 1:
        						Intent intent = new Intent(B_Side_Notice_Gesture_Verifcation.this, B_Side_Notice_Main_Sent_SMS.class);
        						intent.putExtra("enter_acy_type", 1);
        						startActivity(intent);	
        						break;
        		            case 0:
        		            	Intent intent1 = new Intent(B_Side_Notice_Gesture_Verifcation.this, B_Side_Notice_Main_Sent_Notice.class);
        		            	intent1.putExtra("enter_acy_type", 1);
        		            	startActivity(intent1);	
        						break;
        					default:
        						break;
        					}
                            
                        }
                        finish();
                        PubMehods.showToastStr(B_Side_Notice_Gesture_Verifcation.this, "验证通过");
                    } else {
                        A_0_App.getInstance().mFalseAllCount++;
                        int error_Count = AppStrStatic.INTERVAL_NOTCIE_FORGET_COUNT - A_0_App.getInstance().mFalseAllCount;
                        if (error_Count == 0) {
                            A_0_App.getInstance().saveNoticeErrorValues(A_0_App.getInstance().mFalseAllCount, java.lang.System.currentTimeMillis());
                            int time = AppStrStatic.INTERVAL_NOTCIE_FORGET_PWD/(60 * 1000);//提示时间的设置间隔
                            //tv_gesture_verivication.setText(strTimeRemindM((time)));
                            lpwv.disableTouch();
                            finish();
                            PubMehods.showToastStr(B_Side_Notice_Gesture_Verifcation.this,strTimeRemindM(time));
                        }else{
                            A_0_App.getInstance().saveNoticeErrorValues(A_0_App.getInstance().mFalseAllCount, java.lang.System.currentTimeMillis());
                            tv_gesture_verivication.setText(strTimeCount(error_Count));
                            if (tv_gesture_verivication != null)
                                tv_gesture_verivication.startAnimation(shake);
                        }
                    }
                }
			}
		});
		
	}
	

    public String strTimeRemind(String time) {
        return "多次输入错误，请" + time + "后再试";
    }

	//获取距离上次锁定的时间差
    private int judge_Error_Time() {
        long getErrorIsTime = A_0_App.getInstance().getCountTime(
                A_0_App.getInstance().mFalseAllCount);
        int diff_Time = PubMehods.getErrorTimeDiff(getErrorIsTime);
        logE(diff_Time+"解锁时间差judge_Error_Time");
        return diff_Time;

    }

    public String strTimeRemindM(int time) {
        return "多次输入错误，请" + String.valueOf(time) + "分钟后再试";
    }
    
    public String strTimeCount(int count) {
        return "密码错误，你还可以输入" + String.valueOf(count) + "次";
    }
    
    public void SetLpwv(LocusPassWordView lpwv) {
        this.lpwv = lpwv;
    }

    public LocusPassWordView getLpwv() {
        return lpwv;
    }
	
    //加载验证的初始状态
    private void LoadAcyData() {
        if (A_0_App.getInstance().mFalseAllCount >= AppStrStatic.INTERVAL_NOTCIE_FORGET_COUNT) {
            int time = judge_Error_Time();
            if(time > 0){
                lpwv.disableTouch();
                tv_gesture_verivication.setText(strTimeRemindM((time)));
            }else{
                tv_gesture_verivication.setText("请输入手势密码");
                lpwv.enableTouch();//可以点击
                A_0_App.getInstance().clearNoticeErrorValues();//清除之前的错误次数
            }
        } else {
            if (acy_Enter_Type == 1) {
                tv_gesture_verivication.setText("请输入手势密码");
            } else if (acy_Enter_Type == 2) {
                tv_gesture_verivication.setText("请输入原手势密码");
            }
            lpwv.enableTouch();
        }
    }
    
    private void resetPwd() {
        if (A_0_App.getInstance().mFalseAllCount >= AppStrStatic.INTERVAL_NOTCIE_FORGET_COUNT) {
            int time = judge_Error_Time();
            if(time > 0){
                lpwv.disableTouch();
                //tv_gesture_verivication.setText(strTimeRemindM((time)));
                PubMehods.showToastStr(B_Side_Notice_Gesture_Verifcation.this, strTimeRemindM(time));
            }else{
                tv_gesture_verivication.setText("请输入原手势密码");
                acy_Enter_Type = 3;
                lpwv.enableTouch();//可以点击
            }
        } else {
            tv_gesture_verivication.setText("请输入原手势密码");
            acy_Enter_Type = 3;
            lpwv.enableTouch();//可以点击
        }
    }
    
    // 退出登录
    private void exit_login() {
        if (isFinishing())
            return;
        A_0_App.getInstance().clearUserSpInfo(false);
        startActivity(new Intent(B_Side_Notice_Gesture_Verifcation.this, A_3_0_Login_Acy_Teacher.class));
        A_0_App.getInstance().exit(false);
        finish();
    }

    private void startAcy(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.animal_push_left_in_normal, R.anim.animal_push_left_out_normal);
    }
    
    @Override
    protected void onResume() {
        if(firstLoad){
            firstLoad = false;
        }else{
            LoadAcyData();
        }
        super.onResume();
    }
    
    public static void logD(String msg) {
        LogUtils.logD("B_Side_Notice_Gesture_Verifcation", "B_Side_Notice_Gesture_Verifcation=>" + msg);
    }

    public static void logE(String msg) {
        LogUtils.logE("B_Side_Notice_Gesture_Verifcation", "B_Side_Notice_Gesture_Verifcation=>" + msg);
    }


	
	
}
