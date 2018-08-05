package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.GetBlacklistCallback;
import io.rong.imlib.model.Conversation;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Side_Attence_Personal;
import com.yuanding.schoolteacher.service.Api.InteAttdenceattencePersonal;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;


/**
 * @author Jiaohaili 
 * @version 创建时间：2016年5月9日 下午2:49:12
 * 学生出勤个人详情
 */
public class B_Side_Attence_Main_A5_0_Personal_Statistics extends A_0_CpkBaseTitle_Navi {
	
	private View lienr_ifno_view_temp002,view_ifno_view_temp002,view_ifno_view_loading;
	private String photo="",class_name="";
	private TextView tv_attendence_name,tv_attendence_class,tv_attendence_num;
	private ImageView iv_attendence_photo;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	private Button btn_persion_send_zhitiao,btn_persion_info_tel;
	private ACache maACache;
	private JSONObject jsonObject;
	private Cpk_Side_Attence_Personal attence_detail;
	private LinearLayout liner_attence_person;
	private String user_id="";
	private LinearLayout liner_attence_bottom;
	private int on_off=0;
	private LinearLayout home_load_loading;
	private AnimationDrawable drawable;
	private boolean havaSuccessLoadData = false;
    @SuppressLint("ResourceAsColor")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.activity_side_attence_personal_statistics);
        
        view_ifno_view_temp002 = findViewById(R.id.view_ifno_view_temp002);
		lienr_ifno_view_temp002 = findViewById(R.id.lienr_ifno_view_temp002);
		view_ifno_view_loading=findViewById(R.id.view_ifno_view_loading);
		liner_attence_person=(LinearLayout) findViewById(R.id.liner_attence_person);
		liner_attence_bottom=(LinearLayout) findViewById(R.id.liner_attence_bottom);
		
        iv_attendence_photo=(ImageView) findViewById(R.id.iv_attendence_photo);
        tv_attendence_name=(TextView) findViewById(R.id.tv_attendence_name);
        tv_attendence_class=(TextView) findViewById(R.id.tv_attendence_class);
        tv_attendence_num=(TextView) findViewById(R.id.tv_attendence_num);
        
        btn_persion_send_zhitiao=(Button) findViewById(R.id.btn_persion_send_zhitiao);
        btn_persion_info_tel=(Button) findViewById(R.id.btn_persion_info_tel);
        btn_persion_send_zhitiao.setOnClickListener(onclick);
        btn_persion_info_tel.setOnClickListener(onclick);
        view_ifno_view_temp002.setOnClickListener(onclick);
        photo=getIntent().getStringExtra("photo");
        class_name=getIntent().getStringExtra("class");
        user_id=getIntent().getStringExtra("user_id");
       
        home_load_loading = (LinearLayout) view_ifno_view_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start(); 

        tv_attendence_class.setText(class_name);
        
		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(
				R.drawable.i_default_por_120,
				R.drawable.i_default_por_120,
				R.drawable.i_default_por_120);
		if(iv_attendence_photo.getTag() == null){
			PubMehods.loadServicePic(imageLoader,photo,iv_attendence_photo, options);
		    iv_attendence_photo.setTag(photo);
		}else{
		    if(!iv_attendence_photo.getTag().equals(photo)){
		    	PubMehods.loadServicePic(imageLoader,photo,iv_attendence_photo, options);
		        iv_attendence_photo.setTag(photo);
		    }
		}
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		
		if (A_0_App.USER_STATUS.equals("2")) {
			 
		      setZuiYouText("明细");
			 readCache();
		} else {
			showLoadResult(false, false, true);
		}
		 
    }
    /**
     * 动态设置出勤
     */
    private void attendence_type(){
    	liner_attence_person.removeAllViews();
    	 WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
         int width = wm.getDefaultDisplay().getWidth();
         LinearLayout.LayoutParams  params = new LinearLayout.LayoutParams((int)width/attence_detail.getAtdStats().size(),LayoutParams.WRAP_CONTENT);
		for (int i = 0; i < attence_detail.getAtdStats().size(); i++) {
			View view=LayoutInflater.from(B_Side_Attence_Main_A5_0_Personal_Statistics.this).inflate(R.layout.itme_attence_person, null);
			
			TextView tv_attendence_name=(TextView) view.findViewById(R.id.tv_attendence_name);
			TextView tv_attendence_nomal=(TextView) view.findViewById(R.id.tv_attendence_nomal);
			tv_attendence_name.setText(attence_detail.getAtdStats().get(i).getName());
			tv_attendence_nomal.setText(attence_detail.getAtdStats().get(i).getCount());
			if (attence_detail.getAtdStats().get(i).getStatus().equals("1")) {
				tv_attendence_nomal.setTextColor(getResources().getColor(R.color.attence_nomal));
			}else if(attence_detail.getAtdStats().get(i).getStatus().equals("2")){
				tv_attendence_nomal.setTextColor(getResources().getColor(R.color.attence_lack));
			}else if(attence_detail.getAtdStats().get(i).getStatus().equals("3")){
				tv_attendence_nomal.setTextColor(getResources().getColor(R.color.attence_leave_early));
			}else if(attence_detail.getAtdStats().get(i).getStatus().equals("4")){
				tv_attendence_nomal.setTextColor(getResources().getColor(R.color.attence_later));
			}else if(attence_detail.getAtdStats().get(i).getStatus().equals("5")){
				tv_attendence_nomal.setTextColor(getResources().getColor(R.color.attence_leave));
			}
			liner_attence_person.addView(view,params);
		}
		tv_attendence_num.setText(attence_detail.getNormalRate()+"%");
    }
    
	private void readCache() {
		maACache = ACache.get(B_Side_Attence_Main_A5_0_Personal_Statistics.this);
		jsonObject = maACache
				.getAsJSONObject(AppStrStatic.cache_key_personal_statistics+A_0_App.USER_UNIQID+user_id);
		if (jsonObject != null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
		}else{
		updateInfo();}
	}
    
	private void showInfo(JSONObject jsonObject) {
		Cpk_Side_Attence_Personal notice = getNotice(jsonObject);
		if (isFinishing())
			return;
		havaSuccessLoadData = true;   
		attence_detail = notice;
		try {
			if (attence_detail.getAtdStats().size() > 0) {
				attendence_type();
			}
			setTitleText(attence_detail.getUserInfo().getTrue_name());
	        tv_attendence_name.setText(attence_detail.getUserInfo().getTrue_name());
	        if (attence_detail.getUserInfo().getIs_delete().equals("0")) {
				liner_attence_bottom.setVisibility(View.VISIBLE);
			} else {
				liner_attence_bottom.setVisibility(View.GONE);
			}
	        if (RongIM.getInstance()!= null) {
				RongIM.getInstance().getBlacklist(new GetBlacklistCallback() {
							@Override
							public void onSuccess(String[] arg0) {
								if (arg0 != null) {
									for (int i = 0; i < arg0.length; i++) {
										
										if (arg0[i].equals(attence_detail.getUserInfo().getUniqid())) {
											on_off = 1;
										}
									}
								}
							}

							@Override
							public void onError(ErrorCode arg0) {

							}
						});
			}
			showTitleBt(ZUI_RIGHT_TEXT, true);
			showLoadResult(false, true, false);
		} catch (Exception e) {
			showLoadResult(false, false, true);
			showTitleBt(ZUI_RIGHT_BUTTON, false);
		}
       
	}
	private Cpk_Side_Attence_Personal getNotice(JSONObject jsonObject) {
		int state;
		
			state = jsonObject.optInt("status");
			if (state == 1) {
				Cpk_Side_Attence_Personal mlistContact= JSON.parseObject(jsonObject+"",
            			Cpk_Side_Attence_Personal.class);
				return mlistContact;
			}
			
		
		return null;
	}
	
	private void updateInfo() {
		MyAsyncTask updateLectureInfo = new MyAsyncTask(
				B_Side_Attence_Main_A5_0_Personal_Statistics.this);
		updateLectureInfo.execute();
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {
		private Context context;

		MyAsyncTask(Context context) {
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

			startReadData();
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(Integer integer) {

		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}
	
    private void showLoadResult(boolean loading,boolean whole,boolean loadFaile) {
		if (whole)
			lienr_ifno_view_temp002.setVisibility(View.VISIBLE);
		else
			lienr_ifno_view_temp002.setVisibility(View.GONE);
		
		if (loadFaile)
			view_ifno_view_temp002.setVisibility(View.VISIBLE);
		else
			view_ifno_view_temp002.setVisibility(View.GONE);
		
		if (loading) {
			drawable.start();
			view_ifno_view_loading.setVisibility(View.VISIBLE);
		} else {
			if (drawable != null) {
				drawable.stop();
			}
			view_ifno_view_loading.setVisibility(View.GONE);
		}
	}
    
    OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			//打电话
			case R.id.btn_persion_info_tel:
				if (on_off==1) {
					PubMehods.showToastStr(B_Side_Attence_Main_A5_0_Personal_Statistics.this, "该用户为黑名单，您可在设置中修改");
				}else{
					PubMehods.callPhone(B_Side_Attence_Main_A5_0_Personal_Statistics.this, attence_detail.getUserInfo().getPhone());
				}
			    
				break;
			//发消息
			case R.id.btn_persion_send_zhitiao:
				if (on_off==1) {
					PubMehods.showToastStr(B_Side_Attence_Main_A5_0_Personal_Statistics.this, "该用户为黑名单，您可在设置中修改");
				} else {
					if (attence_detail.getUserInfo().getUniqid()!=null) {
						A_0_App.getInstance().exit_rongyun(true);
						RongIM.getInstance().startConversation(B_Side_Attence_Main_A5_0_Personal_Statistics.this, Conversation.ConversationType.PRIVATE, attence_detail.getUserInfo().getUniqid(), attence_detail.getUserInfo().getTrue_name());
					}
				}
				
				
				break;
           case R.id.view_ifno_view_temp002:
        	   
        		showLoadResult(true,false, false);
        		startReadData();
				
					
				break;
			default:
				break;
			}
			
		}
	};
    
	private void startReadData() {
		A_0_App.getApi().getAttdencePersonal(B_Side_Attence_Main_A5_0_Personal_Statistics.this, A_0_App.USER_TOKEN, user_id, new InteAttdenceattencePersonal() {
			
			@Override
			public void onSuccess(Cpk_Side_Attence_Personal side_Attence_Detail) {
				if (isFinishing())
					return;
				havaSuccessLoadData = true;   
				attence_detail=side_Attence_Detail;
				if (attence_detail.getAtdStats().size()>0) {
					showLoadResult(false,true, false);
					attendence_type();
					showTitleBt(ZUI_RIGHT_TEXT, true);
				}
				if (!attence_detail.getUserInfo().getTrue_name().equals("")) {
					setTitleText(attence_detail.getUserInfo().getTrue_name());
			        tv_attendence_name.setText(attence_detail.getUserInfo().getTrue_name());
				}
				if (attence_detail.getUserInfo().getIs_delete().equals("0")) {
					liner_attence_bottom.setVisibility(View.VISIBLE);
				} else {
					liner_attence_bottom.setVisibility(View.GONE);
				}
				if (RongIM.getInstance()!= null) {
					RongIM.getInstance().getBlacklist(new GetBlacklistCallback() {
								@Override
								public void onSuccess(String[] arg0) {
									if (arg0 != null) {
										for (int i = 0; i < arg0.length; i++) {
											
											if (arg0[i].equals(attence_detail.getUserInfo().getUniqid())) {
												on_off = 1;
											}
										}
									}
								}

								@Override
								public void onError(ErrorCode arg0) {

								}
							});
				}
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

            
                if(!havaSuccessLoadData){
                    PubMehods.showToastStr(
                            B_Side_Attence_Main_A5_0_Personal_Statistics.this, msg);
                    showLoadResult(false, false, true);
                }else{
                    showInfo(jsonObject);
                }
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	
    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;
            case ZUI_RIGHT_TEXT:
            	Intent intent=new Intent(B_Side_Attence_Main_A5_0_Personal_Statistics.this, B_Side_Attence_Main_A5_1_Statistics_Detail.class);
            	intent.putExtra("user_id", user_id);
            	startActivity(intent);
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_A5_0_Personal_Statistics.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_A5_0_Personal_Statistics.this, AppStrStatic.kicked_offline());
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

		drawable.stop();
		drawable=null;
    	super.onDestroy();
    }
}
