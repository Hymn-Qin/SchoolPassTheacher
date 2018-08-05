package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Tab_Bottom;
import com.yuanding.schoolteacher.bean.Cpk_Side_Attence_Add_Group;
import com.yuanding.schoolteacher.service.Api.AppNotice_InviteInstallAppCallBack;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.Bimp;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.GeneralDialog;

/**
 * 
 * @author 添加考勤班级人总页面---应用中
 *
 */

public class B_Side_Attence_Main_Add_Contacts extends A_0_CpkBaseTitle_Tab_Bottom {
	
    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_TWO = 1;
    public FinishReceiver finishReceiver;
    private int count_default=0,count_defined=0;
    private String organids = "",name_default="";
    public static int first_load=0;//点击返回和确定的判断
    public String type,acy_enter,weekday_id = "";
    
    private static B_Side_Attence_Main_Add_Contacts instance;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
      
		showTitleBt(BACK_BUTTON, true);
		type=getIntent().getStringExtra("type");
		acy_enter = getIntent().getExtras().getString("acy_enter","");
		weekday_id = getIntent().getExtras().getString("weekday_id","");
		if (type.equals("0")) {
			setTitleText("添加考勤班级");
		} else {
			setTitleText("添加考勤分组");
		}
		mIndicator.setVisibility(View.GONE);
		
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        showTitleBt(SHARE_Bottom, false);
        finishReceiver = new FinishReceiver();
		IntentFilter intentFilter = new IntentFilter("acctence_add");
	    registerReceiver(finishReceiver, intentFilter);
	    
	    setBottomName("人上课");
	    
		A_0_App.attence_result_Contacts.clear();
		A_0_App.attence_result_Contacts.addAll(A_0_App.attence_result_Contacts_summit);
		A_0_App.attence_temp_Contacts.clear();
	
		//A_0_App.attence_result_defined.clear();
		//A_0_App.attence_result_defined.addAll(A_0_App.attence_result_defined_summit);
		
//      if (A_0_App.attence_result_defined.size()>0&&A_0_App.attence_result_Contacts.size()==0) {
//    	  count_defined=0;
//			for (int i = 0; i < A_0_App.attence_result_defined.size(); i++) {
//				count_defined=count_defined+Integer.parseInt(A_0_App.attence_result_defined.get(i).getCount());
//			}
//			setTitleBottom(count_defined+"");
//    	  navigate(0);
//		} else if(A_0_App.attence_result_Contacts.size()>0&&A_0_App.attence_result_defined.size()==0){
			commit();
			//navigate(0);
		//}
		
	   
	    
    }
    
    public static B_Side_Attence_Main_Add_Contacts getInstance() {
        return instance;
    }

	/**
     * 设置连接状态变化的监听器.
     */
    @SuppressWarnings("static-access")
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
                   // A_0_App.getInstance().showExitDialog(B_Side_Notice_Main_Add_Contacts2.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_Add_Contacts.this, AppStrStatic.kicked_offline());
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
    	
    		 tabs.add(new TabInfo(FRAGMENT_ONE, getString(R.string.str_attence_title_default),
    	        		B_Side_Attence_Main_Class_Default.class));
		
//			 tabs.add(new TabInfo(FRAGMENT_TWO, getString(R.string.str_attence_title_defined),
//		        		B_Side_Attence_Main_Class_Defined.class));
	
       
       

        return FRAGMENT_ONE;
    }
    
   

	public class FinishReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			
			count_default=0;
			name_default="";
			 organids="";
			 if (arg1.getStringExtra("all").equals("8")) {
	                    commit();
	                
	                
	            }
			 else if (arg1.getStringExtra("all").equals("1")||arg1.getStringExtra("all").equals("2")) {
				if (A_0_App.add_position==0) {
					commit();
				}else{
					count_defined=0;
					setTitleBottom(count_defined+"");
				}
				
			}else if(arg1.getStringExtra("all").equals("3")){
				
				Intent intent1 = new Intent("attence");
				intent1.putExtra("position", A_0_App.add_position);
				sendBroadcast(intent1);
			}else if(arg1.getStringExtra("all").equals("4")){
				
				if(B_Side_Attence_Main_Class_Default.attence==1){
					if (A_0_App.summit==1) {
						if (first_load!=0) {
							commit();
						}
						if (acy_enter != null && acy_enter.equals("0")) {
						    if (organids != null && organids.length() > 1) {
						        String ids = organids.substring(0, organids.length() - 1);
						        String name = name_default.substring(0, name_default.length() - 1);
						        to_Add_Course_Class(weekday_id, ids, name,ids);
		                    }else{
		                        PubMehods.showToastStr(B_Side_Attence_Main_Add_Contacts.this, "请添加考勤班级");
		                    }
		                } else {
    						Intent data = new Intent();
    						data.putExtra("organ_name", name_default);
    						data.putExtra("organ_id", organids);
    						B_Side_Attence_Main_Add_Contacts.this.setResult(6,data);
    						finish();
		                }
					} else {
						finish();
					}
					
					
				}
				
				
			}else if(arg1.getStringExtra("all").equals("5")){
				if (first_load==0) {
					A_0_App.attence_all_Contacts.clear();
					A_0_App.attence_result_Contacts.clear();
					A_0_App.attence_temp_Contacts.clear();
				}
				finish();
			}else if(arg1.getStringExtra("all").equals("6")) {
				if (B_Side_Attence_Main_Class_Default.bottom==1||B_Side_Notice_Add_Contact_Student.bottom==1||B_Side_Notice_Add_Contact_Group.bottom==1) {
					 showTitleBt(SHARE_Bottom, true);
				}else{
					showTitleBt(SHARE_Bottom, false);
				}
			}else if(arg1.getStringExtra("all").equals("define")){
				count_defined=0;
				for (int i = 0; i < A_0_App.attence_result_defined.size(); i++) {
					count_defined=count_defined+Integer.parseInt(A_0_App.attence_result_defined.get(i).getGroup_num());
				}
				setTitleBottom(count_defined+"");
			}else if(arg1.getStringExtra("all").equals("change")){
				if (A_0_App.add_position==1) {
					count_defined=0;
					for (int i = 0; i < A_0_App.attence_result_defined.size(); i++) {
						count_defined=count_defined+Integer.parseInt(A_0_App.attence_result_defined.get(i).getGroup_num());
					}
					setTitleBottom(count_defined+"");
				}else if(A_0_App.add_position==0){
					count_default=0;
					for (int i = 0; i < A_0_App.attence_temp_Contacts.size(); i++) {//同事
						count_default=count_default+Integer.parseInt(A_0_App.attence_temp_Contacts.get(i).getUser_total_num());
					}
					for (int i = 0; i <  A_0_App.attence_result_Contacts.size(); i++) {//同事
						count_default=count_default+Integer.parseInt( A_0_App.attence_result_Contacts.get(i).getUser_total_num());
					}
					setTitleBottom(count_default+"");
				}
				
			}
			
		}}
   private void commit(){
	   count_default=0;
	   name_default="";
	   organids="";
	for (int i = 0; i < A_0_App.attence_temp_Contacts.size(); i++) {//同事
		count_default=count_default+Integer.parseInt(A_0_App.attence_temp_Contacts.get(i)
				.getUser_total_num());
		name_default=name_default+A_0_App.attence_temp_Contacts.get(i).getOrgan_name()+",";
		organids=organids+A_0_App.attence_temp_Contacts.get(i).getOrgan_id()+",";
	}
	for (int i = 0; i <  A_0_App.attence_result_Contacts.size(); i++) {//同事
		count_default=count_default+Integer.parseInt( A_0_App.attence_result_Contacts.get(i)
				.getUser_total_num());
		name_default=name_default+ A_0_App.attence_result_Contacts.get(i).getOrgan_name()+",";
		organids=organids+ A_0_App.attence_result_Contacts.get(i).getOrgan_id()+",";
	}
	 
	 setTitleBottom(count_default+""); 
}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		Intent intent1;
		switch (resId) {
		case BACK_BUTTON:
			//if (A_0_App.add_position == 0) {
				A_0_App.summit = 0;
				first_load = 0;
				intent1 = new Intent("attence");
				intent1.putExtra("position", A_0_App.add_position);
				sendBroadcast(intent1);
//			} else if (A_0_App.add_position == 1) {
//				finish();
//			}
			
			break;
		case BOTTOM_CANCEL:
			
				setTitleBottom("0");
				intent1 = new Intent("attence");
				intent1.putExtra("position", 3);
				sendBroadcast(intent1);
			
			break;
			
		case BOTTOM_COMMIT:
			
			//CommitDailog();
			
			A_0_App.summit=1;
			first_load=1;
			intent1 = new Intent("attence");
			intent1.putExtra("position",5);
			sendBroadcast(intent1);
			A_0_App.attence_result_Contacts_summit.clear();
		
			
			break;
		default:
			break;
		}
		
	}
	
	 @Override
	    public boolean dispatchKeyEvent(KeyEvent event) {
	        if (event.getAction() == KeyEvent.ACTION_DOWN) {
	            switch (event.getKeyCode()) {
	                case KeyEvent.KEYCODE_BACK:
				//if (A_0_App.add_position == 0) {
					A_0_App.summit = 0;
					first_load = 0;
					Intent intent1 = new Intent("attence");
					intent1.putExtra("position", A_0_App.add_position);
					sendBroadcast(intent1);
//				} else if (A_0_App.add_position == 1) {
//					
//					finish();
//				}
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
		A_0_App.add_position=0;
		unregisterReceiver(finishReceiver);
	}
//public void CommitDailog(){
//
//	final GeneralDialog upDateDialog = new GeneralDialog(
//			B_Side_Attence_Main_Add_Contacts.this,
//			R.style.Theme_GeneralDialog);
//	upDateDialog.setTitle(R.string.pub_title);
//	upDateDialog.setContent("班级和群组不可同时选择！");
//	upDateDialog.showLeftButton(R.string.pub_cancel,
//			new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					upDateDialog.cancel();
//					
//
//				}
//			});
//	upDateDialog.showRightButton(R.string.pub_sure,
//			new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					upDateDialog.cancel();
//					if (A_0_App.add_position == 0) {
//						A_0_App.attence_result_defined_summit.clear();
//						A_0_App.summit=1;
//						first_load=1;
//						Intent intent1 = new Intent("attence");
//						intent1.putExtra("position",5);
//						sendBroadcast(intent1);
//						A_0_App.attence_result_defined_summit.clear();
//					} else if(A_0_App.add_position == 1){
//						
//						for (int i = 0; i < A_0_App.attence_result_defined.size(); i++) {
//							name_defined=name_defined+A_0_App.attence_result_defined.get(i).getName()+",";
//							userids=userids+A_0_App.attence_result_defined.get(i).getId()+",";
//						}
//						Intent data = new Intent();
//						data.putExtra("organ_name", name_defined);
//						data.putExtra("organ_id", userids);
//						B_Side_Attence_Main_Add_Contacts.this.setResult(6,data);
//						A_0_App.attence_result_Contacts.clear();
//						A_0_App.attence_result_Contacts_summit.clear();
//						A_0_App.attence_temp_Contacts.clear();
//						finish();
//					}
//				}
//			});
//	upDateDialog.show();
//
//}
	public static void removeDuplicate(List<Cpk_Side_Attence_Add_Group> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).getId().equals(list.get(i).getId())) {

					list.remove(j);
				}
			}
		}

	}
	
    private void to_Add_Course_Class(String weekday_id,String ids,final String organNames,final String class_ids) {
        A_0_App.getInstance().showProgreDialog(B_Side_Attence_Main_Add_Contacts.this, "", true);
        A_0_App.getApi().to_Add_Course_Class(weekday_id, ids, A_0_App.USER_TOKEN, "1",new AppNotice_InviteInstallAppCallBack() {
            @Override
            public void onSuccess(String message) {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_Add_Contacts.this);
                if (message != null && !("").equals(message))
                    PubMehods.showToastStr(B_Side_Attence_Main_Add_Contacts.this, message);
                
                Intent data = new Intent();
                data.putExtra("organNames", organNames);
                data.putExtra("class_ids", class_ids);
                B_Side_Attence_Main_Add_Contacts.this.setResult(0,data);
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
                A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_Add_Contacts.this);
                PubMehods.showToastStr(B_Side_Attence_Main_Add_Contacts.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
}
