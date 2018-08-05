
package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.yuanding.schoolteacher.A_Main_Acy.ActivityID;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Tab;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.DensityUtils;
import com.yuanding.schoolteacher.utils.PubMehods;

public class B_Side_Notice_Main extends A_0_CpkBaseTitle_Tab {

    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_TWO = 1;
    public static final int FRAGMENT_THREE = 2;
    private int enter_acy_type;//enter_acy_type,首页快捷方式进入为1，身边通知进入为2
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (A_0_App.USER_STATUS.equals("2")) {
            setZuiRightBtn(R.drawable.navigationbar_write);
            showTitleBt(ZUI_RIGHT_BUTTON, true);
        } else {
            showTitleBt(ZUI_RIGHT_BUTTON, false);
        }
        showTitleBt(BACK_BUTTON, true);
        setTitleText("通知");
        
        if (getIntent() != null)
            enter_acy_type = getIntent().getExtras().getInt("enter_acy_type");
        else
            Log.e("test", enter_acy_type + "");
        
        if (enter_acy_type > 0) {
            if (enter_acy_type == 1) {
                navigate(A_0_App.SIDE_NOTICE);
            } else if (enter_acy_type == 2) {

            }
        }
        
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
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
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy
                            .logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    // A_0_App.getInstance().showExitDialog(B_Side_Notice_Main.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Notice_Main.this,
                                    AppStrStatic.kicked_offline());
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
    protected int supplyTabs(List<TabInfo> tabs) {
        tabs.add(new TabInfo(FRAGMENT_ONE, getString(R.string.fragment_one),
                B_Side_Notice_Main_Sent.class));
        tabs.add(new TabInfo(FRAGMENT_TWO, getString(R.string.fragment_two),
                B_Side_Notice_Main_UnSent.class));
        tabs.add(new TabInfo(FRAGMENT_THREE, getString(R.string.fragment_three),
                B_Side_Notice_Main_Drafts.class));

        return FRAGMENT_ONE;
    }

    private PopupWindow statusPopup;
    private LinearLayout mLinerCollct, mLinerForward;

    private void showWindow(View parent) {
        if (statusPopup == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_notice_sent,
                    null);
            mLinerCollct = (LinearLayout) view
                    .findViewById(R.id.liner_lecture_detail_collect);
            mLinerForward = (LinearLayout) view
                    .findViewById(R.id.liner_lecture_detail_forward);
            statusPopup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
        }
        statusPopup.setFocusable(true);
        statusPopup.setOutsideTouchable(true);
        statusPopup.setBackgroundDrawable(new BitmapDrawable());
        int x = DensityUtils.dip2px(B_Side_Notice_Main.this, 125);
        statusPopup.showAsDropDown(parent, -x, A_0_App.getInstance().getShowViewHeight());// 第一参数负的向左，第二个参数正的向下

        mLinerForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (statusPopup != null) {
                    statusPopup.dismiss();
                }
                Intent intent = new Intent(B_Side_Notice_Main.this,
                        B_Side_Notice_Main_Sent_Notice.class);
                intent.putExtra("enter_acy_type", enter_acy_type);
                startActivity(intent);
            }
        });

        mLinerCollct.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (statusPopup != null) {
                    statusPopup.dismiss();
                }
                Intent intent = new Intent(B_Side_Notice_Main.this,
                        B_Side_Notice_Main_Sent_SMS.class);
                intent.putExtra("acy_detail_id", "");
                intent.putExtra("enter_acy_type", enter_acy_type);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                goAcy();
                A_0_App.SIDE_NOTICE = 0;
                overridePendingTransition(R.anim.animal_push_right_in_normal,
                        R.anim.animal_push_right_out_normal);
                break;
            case ZUI_RIGHT_BUTTON:
                if (A_0_App.USER_STATUS.equals("2")) {
                    showWindow(v);
                }

                break;
            default:
                break;
        }

    }
    
    private void goAcy() {
        if (enter_acy_type == 1) {
            if (A_Main_Acy.getInstance() != null)
                A_Main_Acy.getInstance().UpdateView(ActivityID.STARTHOME);
            else
                startActivity(new Intent(B_Side_Notice_Main.this, A_Main_Acy.class));
        } else if (enter_acy_type == 2) {

        }
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    goAcy();
                    A_0_App.SIDE_NOTICE = 0;
                    overridePendingTransition(R.anim.animal_push_right_in_normal,
                            R.anim.animal_push_right_out_normal);
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        A_0_App.SIDE_NOTICE = -1;
    }
}
