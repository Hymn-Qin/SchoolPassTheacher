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

import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Tab_Bottom;
import com.yuanding.schoolteacher.bean.Cpk_Side_Attence_Add_Group;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.Bimp;

/**
 * 
 * @author 通知添加收信人总页面---应用中
 *
 */

public class B_Side_Notice_Main_Add_Contacts2 extends A_0_CpkBaseTitle_Tab_Bottom {
	
    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_TWO = 1;
    public static final int FRAGMENT_THREE = 2;
    public FinishReceiver finishReceiver;
    private int choosed_count=0;
    private String userids = "", classids = "", organids = "",groupids="",name="";
    public static int first_load=0;//点击返回和确定的判断
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        A_0_App.getInstance().addActivity_rongyun(this);
		if(A_0_App.USER_STATUS.equals("2")){
			setZuiRightBtn(R.drawable.navigationbar_search_button);
			 showTitleBt(ZUI_RIGHT_BUTTON, true);
		}else{
			showTitleBt(ZUI_RIGHT_BUTTON, false);
		}
		showTitleBt(BACK_BUTTON, true);
		setTitleText("添加收信人");
		
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        showTitleBt(SHARE_Bottom, false);
        finishReceiver = new FinishReceiver();
		IntentFilter intentFilter = new IntentFilter("add");
	    registerReceiver(finishReceiver, intentFilter);
	    
	    
		A_0_App.group_result_Contacts.clear();
		A_0_App.colleague_result_Contacts.clear();
		A_0_App.student_result_Contacts.clear();
		A_0_App.student_result_Contacts.addAll(A_0_App.student_result_Contacts_summit);
		A_0_App.student_temp_Contacts.clear();
		A_0_App.colleague_result_Contacts.addAll(A_0_App.colleague_result_Contacts_summit);
		A_0_App.colleague_temp_Contacts.clear();
		A_0_App.group_result_Contacts.addAll(A_0_App.group_result_Contacts_summit);
		A_0_App.group_temp_Contacts.clear();
	
	    
	    commit();
	    setTitleBottom((choosed_count+A_0_App.notice_search_Contacts.size())+"");
	    if(A_0_App.student_result_Contacts.size()>0){
	    	A_0_App.add_student_check=1;
	    }
	    if(A_0_App.colleague_result_Contacts.size()>0){
	    	A_0_App.add_colleague_check=1;
	    }
	    
	    if(A_0_App.group_result_Contacts.size()>0){
	    	A_0_App.add_group_check=1;
	    }
	    
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
                            A_0_App.getInstance().showExitDialog(B_Side_Notice_Main_Add_Contacts2.this, AppStrStatic.kicked_offline());
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
        tabs.add(new TabInfo(FRAGMENT_ONE, getString(R.string.fragment_one_add),
        		B_Side_Notice_Add_Contact_Student.class));
        tabs.add(new TabInfo(FRAGMENT_TWO, getString(R.string.fragment_two_add),
        		B_Side_Notice_Add_Contact_Colleague.class));
        tabs.add(new TabInfo(FRAGMENT_THREE, getString(R.string.fragment_three_add),
        		B_Side_Notice_Add_Contact_Group.class));

        return FRAGMENT_ONE;
    }
    
   

	public class FinishReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			
			 choosed_count=0;
			 name="";
			 classids="";
			 organids="";
			 groupids="";
			if (arg1.getStringExtra("all").equals("1")||arg1.getStringExtra("all").equals("2")) {
				commit();
			}else if(arg1.getStringExtra("all").equals("3")){
				
				Intent intent1 = new Intent("student");
				intent1.putExtra("position", A_0_App.add_position);
				sendBroadcast(intent1);
			}else if(arg1.getStringExtra("all").equals("4")){
				
				if(B_Side_Notice_Add_Contact_Colleague.colleague==1&&B_Side_Notice_Add_Contact_Student.student==1&&B_Side_Notice_Add_Contact_Group.Group==1){
					
					if (A_0_App.summit==1) {
						if (first_load!=0) {
							commit();
						}else{
							Bimp.found_name="";
						}
						Intent data = new Intent();
						data.putExtra("name", name);
						data.putExtra("userids", userids);
						data.putExtra("classids", classids);
						data.putExtra("organids", organids);
						data.putExtra("groupids", groupids);
						B_Side_Notice_Main_Add_Contacts2.this.setResult(6,data);
						A_0_App.getInstance().exit_rongyun(true);
						
					} else {
						finish();
					}
					
					
				}
				
				
			}else if(arg1.getStringExtra("all").equals("5")){
				if (first_load==0) {
					A_0_App.student_all_Contacts.clear();
					A_0_App.colleague_all_Contacts.clear();
					A_0_App.student_result_Contacts.clear();
					A_0_App.student_temp_Contacts.clear();
					A_0_App.colleague_result_Contacts.clear();
					A_0_App.colleague_temp_Contacts.clear();
					A_0_App.group_result_Contacts.clear();
					A_0_App.group_temp_Contacts.clear();
				}
				finish();
			}else if(arg1.getStringExtra("all").equals("6")) {
				if (B_Side_Notice_Add_Contact_Colleague.bottom==1||B_Side_Notice_Add_Contact_Student.bottom==1||B_Side_Notice_Add_Contact_Group.bottom==1) {
					 showTitleBt(SHARE_Bottom, true);
				}else{
					showTitleBt(SHARE_Bottom, false);
				}
			}
			
		}}
   private void commit(){
	   removeDuplicate(A_0_App.group_temp_Contacts);
	   removeDuplicate(A_0_App.group_result_Contacts);
	for (int i = 0; i < A_0_App.student_temp_Contacts.size(); i++) {//学生
		choosed_count=choosed_count+Integer.parseInt(A_0_App.student_temp_Contacts.get(i)
				.getUser_total_num());
		name=name+A_0_App.student_temp_Contacts.get(i).getOrgan_name()+",";
		classids=classids+A_0_App.student_temp_Contacts.get(i).getOrgan_id()+",";
	}
	for (int i = 0; i < A_0_App.student_result_Contacts.size(); i++) {//学生
		choosed_count=choosed_count+Integer.parseInt(A_0_App.student_result_Contacts.get(i)
				.getUser_total_num());
		name=name+A_0_App.student_result_Contacts.get(i).getOrgan_name()+",";
		classids=classids+A_0_App.student_result_Contacts.get(i).getOrgan_id()+",";
	}
	
	for (int i = 0; i < A_0_App.colleague_temp_Contacts.size(); i++) {//同事
		choosed_count=choosed_count+Integer.parseInt(A_0_App.colleague_temp_Contacts.get(i)
				.getUser_total_num());
		name=name+A_0_App.colleague_temp_Contacts.get(i).getOrgan_name()+",";
		organids=organids+A_0_App.colleague_temp_Contacts.get(i).getOrgan_id()+",";
	}
	for (int i = 0; i <  A_0_App.colleague_result_Contacts.size(); i++) {//同事
		choosed_count=choosed_count+Integer.parseInt( A_0_App.colleague_result_Contacts.get(i)
				.getUser_total_num());
		name=name+ A_0_App.colleague_result_Contacts.get(i).getOrgan_name()+",";
		organids=organids+ A_0_App.colleague_result_Contacts.get(i).getOrgan_id()+",";
	}
	
	 for (int i = 0; i < A_0_App.group_temp_Contacts.size(); i++) {//分组
			if (A_0_App.group_temp_Contacts.get(i).getCount()==null) {
				choosed_count=choosed_count+1;
				name=name+A_0_App.group_temp_Contacts.get(i).getName()+",";
				userids=userids+A_0_App.group_temp_Contacts.get(i).getId()+",";
			}else{
				groupids=groupids+A_0_App.group_temp_Contacts.get(i).getId()+",";
				name=name+A_0_App.group_temp_Contacts.get(i).getName()+",";
				choosed_count=choosed_count+Integer.parseInt(A_0_App.group_temp_Contacts.get(i)
						.getCount());
				
			}
			
		}
	 
	 for (int i = 0; i < A_0_App.group_result_Contacts.size(); i++) {//分组
			if (A_0_App.group_result_Contacts.get(i).getCount()==null) {
				choosed_count=choosed_count+1;
				name=name+A_0_App.group_result_Contacts.get(i).getName()+",";
				userids=userids+A_0_App.group_result_Contacts.get(i).getId()+",";
			}else{
				groupids=groupids+A_0_App.group_result_Contacts.get(i).getId()+",";
				name=name+A_0_App.group_result_Contacts.get(i).getName()+",";
				choosed_count=choosed_count+Integer.parseInt(A_0_App.group_result_Contacts.get(i)
						.getCount());
				
			}
			}

	 if (name.length()>0&&name!="") {
		 Bimp.found_name=name.substring(0, name.length()-1);
		
	}else{
		Bimp.found_name="";
	}
	
	setTitleBottom((choosed_count+A_0_App.notice_search_Contacts.size())+"");
}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		Intent intent1;
		switch (resId) {
		case BACK_BUTTON:
			first_load=0;
			A_0_App.summit=0;
			intent1 = new Intent("student");
			intent1.putExtra("position", A_0_App.add_position);
			sendBroadcast(intent1);
			
			break;
		case BOTTOM_CANCEL:
			A_0_App.notice_search_Contacts.clear();
			setTitleBottom("0");
			intent1 = new Intent("student");
			intent1.putExtra("position",3);
			sendBroadcast(intent1);
			break;
			
		case BOTTOM_COMMIT:
			A_0_App.student_result_Contacts_summit.clear();
			A_0_App.colleague_result_Contacts_summit.clear();
			A_0_App.group_result_Contacts_summit.clear();
			A_0_App.summit=1;
			first_load=1;
			intent1 = new Intent("student");
			intent1.putExtra("position",5);
			sendBroadcast(intent1);
			
			
			break;
		case ZUI_RIGHT_BUTTON:
		      Intent intent=new Intent(B_Side_Notice_Main_Add_Contacts2.this, B_Side_Notice_Contact_Search_more.class);
		      startActivity(intent);
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
	                	A_0_App.summit=0;
	                	first_load=0;
	                	Intent intent1 = new Intent("student");
	        			intent1.putExtra("position", A_0_App.add_position);
	        			sendBroadcast(intent1);
	        			
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
		unregisterReceiver(finishReceiver);
	}

	public static void removeDuplicate(List<Cpk_Side_Attence_Add_Group> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).getId().equals(list.get(i).getId())) {

					list.remove(j);
				}
			}
		}

	}
}
