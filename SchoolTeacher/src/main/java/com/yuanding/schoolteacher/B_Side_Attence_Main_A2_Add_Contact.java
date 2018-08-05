package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Side_Attence_Add_Contact;
import com.yuanding.schoolteacher.service.Api.InterAttdenceAddContact;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年2月22日 上午10:56:19 添加考勤参与人---正在用
 */
public class B_Side_Attence_Main_A2_Add_Contact extends A_0_CpkBaseTitle_Navi {

	private View mLinerLoadError, mLinerWholeView, check_detail_loading,mLinerNoContent;
	private ListView lv_file;
	private LinearLayout res_bottom;
	private TextView tv_has_chooesed;

	private int choosed_count;
	private Button btn_sure, btn_cancle;

	private String parentid;
	private String back_level="0";
	private List<Cpk_Side_Attence_Add_Contact> add_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();

	private String back = "0";
	private String name = "";
	private String organ_id = "";
	private String temp = "0";
	private List<Cpk_Side_Attence_Add_Contact> result_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	private List<Cpk_Side_Attence_Add_Contact> temp_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();
	private FinishReceiver finishReceiver;
	private int position_commit=0;
	private int dailog_show=0;//dailog 崩溃加固
	private LinearLayout home_load_loading;
	private AnimationDrawable drawable;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setView(R.layout.activity_side_attendence_add_student);
		setTitleText("添加考勤班级");
		

		 
        finishReceiver = new FinishReceiver();
		IntentFilter intentFilter = new IntentFilter("contact");
		
	    registerReceiver(finishReceiver, intentFilter);
		mLinerLoadError = findViewById(R.id.check_load_error);
		mLinerWholeView = findViewById(R.id.liner_check_list_whole_view);
		check_detail_loading = findViewById(R.id.check__loading);
		
		mLinerNoContent = findViewById(R.id.check_no_content);
		
		home_load_loading = (LinearLayout) check_detail_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		ImageView iv_blank_por = (ImageView) mLinerNoContent
				.findViewById(R.id.iv_blank_por);
		iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
		TextView tv_blank_name = (TextView) mLinerNoContent
				.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
		tv_blank_name.setText("暂未添加上课人员~");
		mLinerNoContent.setOnClickListener(onClick);
		mLinerLoadError.setOnClickListener(onClick);
		lv_file = (ListView) findViewById(R.id.res_lv);
		res_bottom = (LinearLayout) findViewById(R.id.share_bottom);
		res_bottom.setVisibility(View.VISIBLE);
		btn_sure = (Button) res_bottom.findViewById(R.id.btn_sure);
		btn_cancle = (Button) res_bottom.findViewById(R.id.btn_cancle);
		tv_has_chooesed = (TextView) res_bottom
				.findViewById(R.id.tv_has_chooesed);
		btn_sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				position_commit=5;
				Intent intent1 = new Intent("contact");
				sendBroadcast(intent1);
				
				
			}
		});
		btn_cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				tv_has_chooesed.setText("0");
				temp_Contacts.clear();
				result_Contacts.clear();
				for (int i = 0; i < add_Contacts.size(); i++) {
					add_Contacts.get(i).setCheck(false);
				}
				adapter.notifyDataSetChanged();
			}
		});
		adapter = new Myadapter();
		lv_file.setAdapter(adapter);

		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
			startListtenerRongYun();// 监听融云网络变化
		}

		if(A_0_App.USER_STATUS.equals("2")){
			
	        getAttdenceList("0", "0");

        }else{
            showTitleBt(ZUI_RIGHT_BUTTON, false);
            showLoadResult(false, false, false,true);
        }
		
		
		
		
	}
	// 数据加载，及网络错误提示
		OnClickListener onClick = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.check_load_error:
					choosed_count=0;
					tv_has_chooesed.setText("0");
					temp_Contacts.clear();
					result_Contacts.clear();
					showLoadResult(false, false, true,false);
					parentid="0";
					getAttdenceList("0", "0");
					break;

					
				case R.id.check_no_content:
					
					choosed_count=0;
					tv_has_chooesed.setText("0");
					temp_Contacts.clear();
					result_Contacts.clear();
					parentid="0";
					getAttdenceList("0", "0");
					
					break;
				default:
					break;
				}
			}
		};
	
	private void showLoadResult(boolean loading, boolean show_content,
			boolean loadFaile,boolean noData) {
		if (show_content)
			mLinerWholeView.setVisibility(View.VISIBLE);
		else
			mLinerWholeView.setVisibility(View.GONE);

		if (loadFaile)
			mLinerLoadError.setVisibility(View.VISIBLE);
		else
			mLinerLoadError.setVisibility(View.GONE);
		if (loading) {
			drawable.start();
			check_detail_loading.setVisibility(View.VISIBLE);
		} else {
			if (drawable != null) {
				drawable.stop();
			}
			check_detail_loading.setVisibility(View.GONE);
		}
		if (noData)
			mLinerNoContent.setVisibility(View.VISIBLE);
		else
			mLinerNoContent.setVisibility(View.GONE);
	}
	// 获取考勤列表数据is_back 返回传1，点击传0
	private void getAttdenceList(String is_back,  String organ_id) {
		A_0_App.getApi().getJoinList(B_Side_Attence_Main_A2_Add_Contact.this,
				A_0_App.USER_TOKEN, is_back, organ_id,
				new InterAttdenceAddContact() {

					@Override
					public void onSuccess(List<Cpk_Side_Attence_Add_Contact> mList,String level) {
                        if(isFinishing())
                            return;
                        System.out.println("rrrrr----"+level);
						if (back.equals("1")) {//返回
							if (temp_Contacts.size()==add_Contacts.size()) {
								 add_Contacts.clear();
	                             add_Contacts=mList;
	                             temp_Contacts.clear();
	                             for (int i = 0; i < add_Contacts.size(); i++) {
									if (add_Contacts.get(i).getOrgan_id().equals(parentid)) {
										add_Contacts.get(i).setCheck(true);
										temp_Contacts.add(add_Contacts.get(i));
									}
								}
	                             
	                             for (int i = 0; i < result_Contacts.size(); i++) {
	 								for (int j = 0; j < add_Contacts.size(); j++) {
	 									if (result_Contacts.size()>0) {
	 										if (result_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(j).getOrgan_id())) {
		 										result_Contacts.remove(i);
		 										temp_Contacts.add(add_Contacts.get(j));
		 										add_Contacts.get(j).setCheck(true);
		 									}
										}
	 									
	 								}
	 							}
	                             
							}else{
								result_Contacts.addAll(temp_Contacts);
								add_Contacts.clear();
	                            add_Contacts=mList;
	                            temp_Contacts.clear();
	                            
								for (int i = 0; i < result_Contacts.size(); i++) {
									for (int j = 0; j < add_Contacts.size(); j++) {
										if (result_Contacts.size()>0) {
											if (result_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(j).getOrgan_id())) {
												result_Contacts.remove(i);
												temp_Contacts.add(add_Contacts.get(j));
												add_Contacts.get(j).setCheck(true);
											}
										}
										
									}
								}

							}
							
							
						} 
						 
						if (back.equals("0")) {//正常
							add_Contacts.clear();
                            add_Contacts=mList;
                            temp_Contacts.clear();
							
							
							if (temp.equals("1")) {
								for (int i = 0; i < result_Contacts.size(); i++) {
									for (int j = 0; j < add_Contacts.size(); j++) {
										if (result_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(j).getOrgan_id())) {
											result_Contacts.remove(i);
										}
									}
									
								}
								for (int i = 0; i < add_Contacts.size(); i++) {
									add_Contacts.get(i).setCheck(true);
									
								}
								temp_Contacts.addAll(add_Contacts);
							}else{
								 for (int i = 0; i < result_Contacts.size(); i++) {
		 								for (int j = 0; j < add_Contacts.size(); j++) {
		 									if (result_Contacts.size()>0) {
		 										if (result_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(j).getOrgan_id())) {
			 										result_Contacts.remove(i);
			 										temp_Contacts.add(add_Contacts.get(j));
			 										add_Contacts.get(j).setCheck(true);
			 									}
											}
		 									
		 								}
		 							}
							}
						}

						back_level = level;
						if (position_commit==5) {
							if (add_Contacts.size()>0) {
								parentid = add_Contacts.get(0).getParentid();
								Intent intent1 = new Intent("contact");
								sendBroadcast(intent1);
							}
							
						}else{
							adapter.notifyDataSetChanged();
						}
						if (add_Contacts.size()>0) {
							showTitleBt(ZUI_RIGHT_TEXT, true);
							setZuiYouText("全选");
							 showLoadResult(false,true, false,false);
						}else{
							
							showLoadResult(false,false, false,true);
						}
						
						choosed_count=0;
						for (int i = 0; i < temp_Contacts.size(); i++) {
							choosed_count = choosed_count
									+ Integer.parseInt(temp_Contacts.get(i)
											.getUser_total_num());
						}
						for (int i = 0; i < result_Contacts.size(); i++) {
							choosed_count = choosed_count
									+ Integer.parseInt(result_Contacts.get(i)
											.getUser_total_num());
						}
					
						tv_has_chooesed.setText(choosed_count + "");
						try {
							if (dailog_show==1) {
								A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_A2_Add_Contact.this);
							}
							  
						} catch (Exception e) {
						
						}
						
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                         if(isFinishing())
                                return;
                          
                            PubMehods.showToastStr(B_Side_Attence_Main_A2_Add_Contact.this, msg);
                            showLoadResult(false,false, true,false);
                            if (dailog_show==1) {
                            A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_A2_Add_Contact.this);}
                            if (position_commit==5) {
                                back_level="0";
                                if (add_Contacts.size()>0) {
                                    parentid = add_Contacts.get(0).getParentid();
                                    Intent intent1 = new Intent("contact");
                                    sendBroadcast(intent1);
                                }}
                                
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}


	private Myadapter adapter;

	private class Myadapter extends BaseAdapter {

		@Override
		public int getCount() {
			return add_Contacts.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertview, ViewGroup arg2) {

			MyHolder holder;
			if (convertview == null) {
				holder = new MyHolder();
				convertview = View.inflate(
						B_Side_Attence_Main_A2_Add_Contact.this,
						R.layout.item_side_attence_add_student, null);
				holder.checBox = (CheckBox) convertview
						.findViewById(R.id.cb_share_detail);
				holder.tv_title = (TextView) convertview
						.findViewById(R.id.tv_txt_name);
				holder.liner_checkbox = (ImageView) convertview
						.findViewById(R.id.liner_checkbox);
				convertview.setTag(holder);

			} else {
				holder = (MyHolder) convertview.getTag();
			}

			parentid = add_Contacts.get(position).getParentid();
			holder.tv_title.setText(add_Contacts.get(position).getOrgan_name());
			if (add_Contacts.get(position).isCheck()==true) {
				holder.checBox.setBackgroundDrawable(getResources().getDrawable(R.drawable.send_list_on));
			}else if(add_Contacts.get(position).isCheck()==false){
				holder.checBox.setBackgroundDrawable(getResources().getDrawable(R.drawable.send_list_off));
			}

			holder.liner_checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					
					if (add_Contacts.get(position).isCheck() == false) {
						
						add_Contacts.get(position).setCheck(true);
						temp_Contacts.add(add_Contacts.get(position));
						choosed_count=0;
						if (result_Contacts.size()>0) {
							for (int i = 0; i <temp_Contacts.size(); i++) {
								Iterator<Cpk_Side_Attence_Add_Contact> iter = result_Contacts.iterator(); 
								while(iter.hasNext()){  
									Cpk_Side_Attence_Add_Contact s = iter.next();  
								    if(s.getOrgan_id().equals(temp_Contacts.get(i).getOrgan_id())){  
								    	
								        iter.remove();  
								    }  
								}
							}
						}
						
						for (int i = 0; i <result_Contacts.size(); i++) {
							choosed_count = choosed_count+ Integer.parseInt(result_Contacts.get(i)
											.getUser_total_num());
						}
						for (int j = 0; j < temp_Contacts.size(); j++) {
							choosed_count = choosed_count+ Integer.parseInt(temp_Contacts.get(j)
									.getUser_total_num());
						}
						tv_has_chooesed.setText(choosed_count + "");
					} else {

						add_Contacts.get(position).setCheck(false);
						for (int i = 0; i < temp_Contacts.size(); i++) {
							if (temp_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(position).getOrgan_id())) {
								temp_Contacts.remove(i);
							}
						}

						choosed_count=0;
						if (result_Contacts.size()>0&&temp_Contacts.size()>0) {
							for (int i = 0; i <temp_Contacts.size(); i++) {
								try {
									Iterator<Cpk_Side_Attence_Add_Contact> iter = result_Contacts.iterator(); 
									while(iter.hasNext()){  
										Cpk_Side_Attence_Add_Contact s = iter.next();  
									    if(s.getOrgan_id().equals(temp_Contacts.get(i).getOrgan_id())){  
									        iter.remove();  
									    }  
									}
								} catch (Exception e) {
									
								}
								
							}
						}
						
						for (int i = 0; i <result_Contacts.size(); i++) {
							choosed_count = choosed_count+ Integer.parseInt(result_Contacts.get(i)
											.getUser_total_num());
						}
						for (int j = 0; j < temp_Contacts.size(); j++) {
							choosed_count = choosed_count+ Integer.parseInt(temp_Contacts.get(j)
									.getUser_total_num());
						}
						tv_has_chooesed.setText(choosed_count + "");
					}
					adapter.notifyDataSetChanged();
				}

			});
			convertview.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (Integer.parseInt(add_Contacts.get(position).getChild_total_num()) > 0) {
						dailog_show=1;
						if (add_Contacts.get(position).isCheck() == true) {
							temp = "1";
							
							for (int i = 0; i < temp_Contacts.size(); i++) {
								if (temp_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(position).getOrgan_id())) {
									temp_Contacts.remove(i);
								}
							}
							
						} else {
							temp = "0";
						}
                        back="0";
                    	A_0_App.getInstance().showProgreDialog(B_Side_Attence_Main_A2_Add_Contact.this,"", true);
						result_Contacts.addAll(temp_Contacts);
						getAttdenceList("0", add_Contacts.get(position)
								.getOrgan_id());
					}

				}
			});
			return convertview;
		}

	}

	private class MyHolder {
		CheckBox checBox;
		TextView tv_title;
		ImageView liner_checkbox;
	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			
			if (Integer.parseInt(back_level) != 0) {
				if (back_level.equals("1")) {
					temp = "0";
   				    back = "1";
   				getAttdenceList("0", "0");
				}else{
				 temp = "0";
				 back = "1";
				getAttdenceList("1", parentid);}
			} else {
				finish();
			}


			break;
		case ZUI_RIGHT_TEXT:
			choosed_count = 0;
			temp_Contacts.clear();
			
			
			if (result_Contacts.size()>0&&add_Contacts.size()>0) {
				for (int i = 0; i <add_Contacts.size(); i++) {
					
					Iterator<Cpk_Side_Attence_Add_Contact> iter = result_Contacts.iterator(); 
					while(iter.hasNext()){  
					
						Cpk_Side_Attence_Add_Contact s = iter.next(); 
						
					    if(s.getParentid().equals(add_Contacts.get(i).getOrgan_id())){ 
					    
					        iter.remove();  
					    }  
					}
				}
			}
			
			 
			if (result_Contacts.size()>0) {
				for (int i = 0; i <add_Contacts.size(); i++) {
					Iterator<Cpk_Side_Attence_Add_Contact> iter = result_Contacts.iterator(); 
					while(iter.hasNext()){  
						Cpk_Side_Attence_Add_Contact s = iter.next();  
					    if(s.getOrgan_id().equals(add_Contacts.get(i).getOrgan_id())){  
					    	
					        iter.remove();  
					    }  
					}
				}
			}
			
			temp_Contacts.addAll(add_Contacts);

			for (int i = 0; i < add_Contacts.size(); i++) {
				add_Contacts.get(i).setCheck(true);
			}
			if (result_Contacts.size() > 0) {
				for (int i = 0; i < temp_Contacts.size(); i++) {
					choosed_count = choosed_count
							+ Integer.parseInt(temp_Contacts.get(i)
									.getUser_total_num());
				}
				for (int i = 0; i < result_Contacts.size(); i++) {
					choosed_count = choosed_count
							+ Integer.parseInt(result_Contacts.get(i)
									.getUser_total_num());
				}
			} else {

				for (int i = 0; i < temp_Contacts.size(); i++) {
					choosed_count = choosed_count
							+ Integer.parseInt(temp_Contacts.get(i)
									.getUser_total_num());
				}
			}

			tv_has_chooesed.setText(choosed_count + "");
			adapter.notifyDataSetChanged();
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

	private class MyConnectionStatusListener implements
			RongIMClient.ConnectionStatusListener {
		@Override
		public void onChanged(ConnectionStatus connectionStatus) {
			switch (connectionStatus) {
			case CONNECTED:// 连接成功。
				A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
				break;
			case DISCONNECTED:// 断开连接。
				A_Main_My_Message_Acy
						.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
//				A_0_App.getInstance().showExitDialog(
//						B_Side_Attence_Main_A2_Add_Contact.this,
//						getResources().getString(R.string.token_timeout));
				break;
			case CONNECTING:// 连接中。
				A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
				break;
			case NETWORK_UNAVAILABLE:// 网络不可用。
				A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
				break;
			case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
				A_Main_My_Message_Acy
						.logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
				class LooperThread extends Thread {
					public void run() {
						Looper.prepare();
						A_0_App.getInstance().showExitDialog(
								B_Side_Attence_Main_A2_Add_Contact.this,
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

	public class FinishReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {

			System.out.println("PPPP----"+back_level);
			if (Integer.parseInt(back_level) != 0) {
			       position_commit=5;
       				
       				 temp = "0";
       				 back = "1";
       				getAttdenceList("1", parentid);
       			
				
			} else {
				result_Contacts.addAll(temp_Contacts);
				for (int i = 0; i < result_Contacts.size(); i++) {
					organ_id=organ_id+result_Contacts.get(i).getOrgan_id()+",";
					name=name+result_Contacts.get(i).getOrgan_name()+",";
				}
				Intent data = new Intent();
				data.putExtra("organ_id", organ_id);
				data.putExtra("organ_name", name);
				B_Side_Attence_Main_A2_Add_Contact.this.setResult(B_Side_Attence_Main_A1_New_Attence.IMAGE_REQUEST_CODE,data);
				finish();
			}
		
		
			
		}}
	@Override
	protected void onResume() {
		super.onResume();

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		drawable.stop();
		drawable=null;
		unregisterReceiver(finishReceiver);
	}
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:

        			if (Integer.parseInt(back_level) != 0) {
        				if (back_level.equals("1")) {
        					temp = "0";
           				    back = "1";
           				getAttdenceList("0", "0");
						}else{
        				 temp = "0";
        				 back = "1";
        				getAttdenceList("1", parentid);}
        			} else {
        				finish();
        			}
                	
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
