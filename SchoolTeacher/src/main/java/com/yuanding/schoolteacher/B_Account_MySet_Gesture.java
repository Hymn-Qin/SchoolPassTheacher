package com.yuanding.schoolteacher;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yuanding.schoolteacher.A_0_App;
import com.yuanding.schoolteacher.R;
import com.yuanding.schoolteacher.R.id;
import com.yuanding.schoolteacher.R.layout;
import com.yuanding.schoolteacher.R.string;
import com.yuanding.schoolteacher.R.style;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.yuanding.schoolteacher.view.toggle.ToggleButton;
import com.yuanding.schoolteacher.view.toggle.ToggleButton.OnToggleChanged;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 下午2:53:58 类说明
 */
public class B_Account_MySet_Gesture extends A_0_CpkBaseTitle_Navi {

    private ToggleButton TB_Notice_GestureSet;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_acc_gesture_set);
        setTitleText("手势密码");

        TB_Notice_GestureSet = (ToggleButton) findViewById(R.id.start_gesture_pwd);
        
        TB_Notice_GestureSet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (A_0_App.getInstance().mNoticeGesure.equals("0")) {//未设置过给提示
                    showDialog();
                } else if (A_0_App.getInstance().mNoticeGesure.equals("1")) {//为开
                    Intent intent_1 = new Intent(B_Account_MySet_Gesture.this, B_Side_Notice_Gesture_Verifcation.class);
                    intent_1.putExtra("enter_acy", 2);
                    startActivity(intent_1);
                }else  if (A_0_App.getInstance().mNoticeGesure.equals("2")) {//为关
                     showDialog();                                                                  
                }
            }
        });
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }
    
    protected void showDialog() {
        final GeneralDialog upDateDialog = new GeneralDialog(B_Account_MySet_Gesture.this,R.style.Theme_GeneralDialog);
        upDateDialog.setTitle(R.string.pub_title);
        upDateDialog.setContent(R.string.gesture_setting_content_go);
        upDateDialog.setCancelable(true);
        upDateDialog.setCanceledOnTouchOutside(true);
        upDateDialog.showLeftButton(R.string.pub_temporarily_set, new OnClickListener() {
            @Override
            public void onClick(View v) {
                A_0_App.getInstance().saveNoticeGesture("2");
                upDateDialog.cancel();
            }
        });
        upDateDialog.showRightButton(R.string.pub_immediately_set, new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSetPwd();
                upDateDialog.cancel();
            }
        });
        upDateDialog.show();
    }

    //设置手势密码
    private void goToSetPwd() {
        Intent intent=new Intent(B_Account_MySet_Gesture.this, B_Side_Notice_GestureSet_Setting.class);
        intent.putExtra("enter_acy", 2);
        startActivity(intent);
    }
    
    @Override
    protected void onResume() {
        if (TB_Notice_GestureSet != null) {
            if (A_0_App.getInstance().mNoticeGesure.equals("1")) {
                TB_Notice_GestureSet.setToggleOn();
            } else {// 默认为关闭状态
                TB_Notice_GestureSet.setToggleOff();
            }
        }
        super.onResume();
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
                    //A_0_App.getInstance().showExitDialog(B_Account_MySet_Gesture.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_MySet_Gesture.this, AppStrStatic.kicked_offline());
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
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;

            default:
                break;
        }

    }

}
