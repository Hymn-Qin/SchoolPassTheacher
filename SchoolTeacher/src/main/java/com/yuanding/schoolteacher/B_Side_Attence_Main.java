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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Tab;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.DensityUtils;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月12日 下午3:18:18 考勤主界面
 */
public class B_Side_Attence_Main extends A_0_CpkBaseTitle_Tab {

	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;
	private static B_Side_Attence_Main instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		A_0_App.SIDE_ATTENCE=0;
		
		if (A_0_App.USER_STATUS.equals("2")) {
			 setZuiRightBtn(R.drawable.navigationbar_add_button);
		        showTitleBt(ZUI_RIGHT_BUTTON, true);
		        setPianRightBtn(R.drawable.navigationbar_help);
		        showTitleBt(PIAN_RIGHT_BUTTON, true);
		}else{
         showTitleBt(PIAN_RIGHT_BUTTON, false);
			showTitleBt(ZUI_RIGHT_BUTTON, false);
		}
		setTitleText("课堂考勤");
		  showTitleBt(BACK_BUTTON, true);
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}

	public static B_Side_Attence_Main getInstance(){
	    return instance;
	}
	
	//显示选项卡未读标记
	@Override
	public void updateChildTips(int postion, boolean showTips) {
	    // TODO Auto-generated method stub
	    super.updateChildTips(postion, showTips);
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Repair_Main.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Attence_Main.this, AppStrStatic.kicked_offline());
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
		tabs.add(new TabInfo(FRAGMENT_ONE, getString(R.string.str_attence_title_one),
				B_Side_Attence_Main_A0.class));
		tabs.add(new TabInfo(FRAGMENT_TWO,
				getString(R.string.str_attence_title_two),
				B_Side_Attence_Main_A10.class));

		return FRAGMENT_ONE;
	}

	
	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		case ZUI_RIGHT_BUTTON:
			if (A_0_App.SIDE_ATTENCE==0) {
				if (A_0_App.USER_STATUS.equals("2")) {
					showWindow(v);
				}
			} else {
				 startActivity(new Intent(B_Side_Attence_Main.this, B_Side_Attence_Main_Set_Contact.class));
			}
            break;
		case PIAN_RIGHT_BUTTON:
		    Intent intent=new Intent();
            intent.setClass(B_Side_Attence_Main.this,Pub_WebView_Load_Other_Acy.class);
            intent.putExtra("title_text", getResources().getString(R.string.str_attence_help));
            intent.putExtra("url_text", AppStrStatic.LINK_USER_ATTDENCE_HELP);
            intent.putExtra("tag_skip", "1");
            startActivity(intent);
            break;
		default:
			break;
		}
	}
	
	private PopupWindow statusPopup;
	private LinearLayout mLinerCollct, mLinerForward;

	private void showWindow(View parent) {
		if (statusPopup == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.item_lost_detail, null);
			mLinerCollct = (LinearLayout) view
					.findViewById(R.id.liner_lecture_detail_collect);
			mLinerForward = (LinearLayout) view
					.findViewById(R.id.liner_lecture_detail_forward);
			statusPopup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			TextView tv_001=(TextView) view.findViewById(R.id.tv_001);
			TextView tv_002=(TextView) view.findViewById(R.id.tv_002);
			tv_001.setText("班级考勤");
			tv_002.setText("分组考勤");
		}
		statusPopup.setFocusable(true);
		statusPopup.setOutsideTouchable(true);
		statusPopup.setBackgroundDrawable(new BitmapDrawable());
		int x = DensityUtils.dip2px(B_Side_Attence_Main.this, 155);
		statusPopup.showAsDropDown(parent, -x, A_0_App.getInstance()
				.getShowViewHeight());// 第一参数负的向左，第二个参数正的向下

		mLinerForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (statusPopup != null) {
					statusPopup.dismiss();
				}
				  Intent intent=new Intent();
		            intent.setClass(B_Side_Attence_Main.this,B_Side_Attence_Main_A1_New_Attence.class);
		            intent.putExtra("type", "0");//班级考勤
		            startActivity(intent);
				 
			}
		});

		mLinerCollct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (statusPopup != null) {
					statusPopup.dismiss();
				}
				Intent intent = new Intent();
				intent.setClass(B_Side_Attence_Main.this,B_Side_Attence_Main_A1_New_Attence.class);
				intent.putExtra("type", "1");// 分组考勤
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		A_0_App.SIDE_ATTENCE=0;
	}
}
