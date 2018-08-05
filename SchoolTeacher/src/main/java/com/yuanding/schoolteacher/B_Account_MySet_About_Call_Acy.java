package com.yuanding.schoolteacher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.yuanding.schoolteacher.R;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.utils.download.Update_App;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.yuanding.schoolteacher.view.toggle.ToggleButton;
import com.yuanding.schoolteacher.view.toggle.ToggleButton.OnToggleChanged;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 下午2:53:58
 * 类说明
 */
public class B_Account_MySet_About_Call_Acy extends A_0_CpkBaseTitle_Navi{
	
	
	private ImageView iv_dimensional_bar_code;
	private LinearLayout liner_contact_us;
    private TextView tv_temp_weixin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_acc_about_call_us);
		setTitleText(getResources().getString(R.string.str_contact_us));

        tv_temp_weixin = (TextView) findViewById(R.id.tv_temp_weixin);
		liner_contact_us = (LinearLayout)findViewById(R.id.liner_contact_us);
		iv_dimensional_bar_code=(ImageView) findViewById(R.id.iv_dimensional_bar_code);

        tv_temp_weixin.setText(AppStrStatic.str_about_weinxin_number());
        iv_dimensional_bar_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                 // 将文本内容放到系统剪贴板里。
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(AppStrStatic.str_about_weinxin_number());
                PubMehods.showToastStr(B_Account_MySet_About_Call_Acy.this,AppStrStatic.str_app_weixin_copyurlsuccess());
            }
        });
        
        liner_contact_us.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                callSb(B_Account_MySet_About_Call_Acy.this, "服务电话",
                        getResources().getString(R.string.str_service_phone_number));
            }
        });
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
    public void callSb(final Context con, String name, final String no) {
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
                PubMehods.callPhone(con, no);
            }
        });
        upDateDialog.show();
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
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Account_Push_Set_Acy.this,getResources().getString(R.string.token_timeout));
                    break;
                case CONNECTING:// 连接中。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                    class LooperThread extends Thread {
                        public void run() {
                            Looper.prepare();
                            A_0_App.getInstance().showExitDialog(B_Account_MySet_About_Call_Acy.this, AppStrStatic.kicked_offline());
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
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;

		default:
			break;
		}
		
	}

}
