package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.utils.AppStrStatic;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月3日 上午9:40:29 
 * 签名选择
 */
public class B_Side_Notice_Main_Selcet_Sign extends A_0_CpkBaseTitle_Navi {

	private View mLinerReadDataError,mLinerNoContent,mWholeView,side_sign__loading;
	private ListView mPullDownView;
	private MyClassAdapter adapter;
	private List<String> mClassList= new ArrayList<String>();
	
	private LinearLayout home_load_loading;
	private AnimationDrawable drawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_select_sign);
		setTitleText("选择签名");
		mPullDownView = (ListView) findViewById(R.id.lv_side_select_sign);
		

		mWholeView = findViewById(R.id.liner_side_sign_whole_view);
		mLinerReadDataError = findViewById(R.id.side_sign_load_error);
		mLinerNoContent = findViewById(R.id.side_sign_no_content);
		side_sign__loading=findViewById(R.id.side_sign__loading);
		ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
		
		home_load_loading = (LinearLayout) side_sign__loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		adapter = new MyClassAdapter();
		mPullDownView.setAdapter(adapter);
		
		// 点击Item触发的事件
		mPullDownView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,long arg3) {
//					Intent intent = new Intent();
//					intent.putExtra("content",mClassList.get(posi));
//					setResult(4, intent);
//					finish();
				A_0_App.notice_sign = mClassList.get(posi);
				finish();
				}
		});
		
		mLinerReadDataError.setOnClickListener(onClick);
		//getData();
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }

	}
	
//	private void getData() {
//		A_0_App.getApi().getSideNoticeSignList(B_Side_Notice_Main_Selcet_Sign.this, A_0_App.USER_TOKEN, new InterSideNoticeSignList() {
//			
//			@Override
//			public void onSuccess(String List) {
//				if(isFinishing())
//					return;
//				String list=(List.replace("[", "")).replace("]", "").replace("\"", "");
//				String content[]=list.split(",");
//				for (int i = 0; i < content.length; i++) {
//					mClassList.add(content[i]);
//				}
//				if(mClassList.size() > 0){
//					showLoadResult(false,true, false, false);
//				}else{
//					showLoadResult(false,false, false, true);
//				}
//				adapter.notifyDataSetChanged();
//			}
//			
//			@Override
//			public void onStart() {
//			}
//			
//			@Override
//			public void onLoading(long total, long current, boolean isUploading) {
//				
//			}
//			
//			@Override
//			public void onFailure(String msg) {
//				if (isFinishing())
//					return;
//				PubMehods.showToastStr(B_Side_Notice_Main_Selcet_Sign.this, msg);
//				showLoadResult(false,false, true, false);
//			}
//			
//			@Override
//			public void onCancelled() {
//				
//			}
//
//			@Override
//			public void onKickedOffline(int state) {
//			    if (isFinishing())
//                    return;
//				if(state==-1){
//    				showLoadResult(false,false, true, false);
//    				A_0_App.getInstance().showExitDialog(B_Side_Notice_Main_Selcet_Sign.this, getResources().getString(R.string.kicked_offline));
//				}
//			}
//		});
//
//	}
	
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_sign_load_error:
				showLoadResult(true,false, false, false);
				mClassList.clear();
				//getData();
				break;
			default:
				break;
			}
		}
	};

    private void showLoadResult(boolean loading,boolean list,boolean loadFaile,boolean noData) {
		if (list)
			mWholeView.setVisibility(View.VISIBLE);
		else
			mWholeView.setVisibility(View.GONE);
		
		if (loadFaile)
			mLinerReadDataError.setVisibility(View.VISIBLE);
		else
			mLinerReadDataError.setVisibility(View.GONE);
		
		if (noData)
			mLinerNoContent.setVisibility(View.VISIBLE);
		else
			mLinerNoContent.setVisibility(View.GONE);
		if (loading) {
			drawable.start();
			side_sign__loading.setVisibility(View.VISIBLE);
		} else {
			if (drawable != null) {
				drawable.stop();
			}
			side_sign__loading.setVisibility(View.GONE);
		}

	}
    
	 // 加载列表数据
	public class MyClassAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mClassList != null)
				return mClassList.size();
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int posi, View converView, ViewGroup arg2) {
			if (converView == null) {
				converView = LayoutInflater.from(B_Side_Notice_Main_Selcet_Sign.this).inflate(R.layout.item_pub_text, null);
			}
			TextView tv_acy_name = (TextView) converView.findViewById(R.id.tv_item_pub_text);
			tv_acy_name.setText(mClassList.get(posi));
			if(A_0_App.isShowAnimation==true){
			 if(posi>A_0_App.select_sign_curPosi)
			 {
				A_0_App.select_sign_curPosi=posi;
				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(50*posi);
			   converView.startAnimation(an);
			 }
			}
			return converView;
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Notice_Main_Selcet_Sign.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Notice_Main_Selcet_Sign.this, AppStrStatic.kicked_offline());
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
	protected void onDestroy() {
		if(mClassList != null && mClassList.size()>0){
			mClassList.clear();
			mClassList = null;
		}
		super.onDestroy();
	}

}
