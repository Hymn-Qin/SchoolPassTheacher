package com.yuanding.schoolteacher;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
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
import com.yuanding.schoolteacher.bean.Cpk_Attence_Group;
import com.yuanding.schoolteacher.service.Api.InterAttenceAddContact_Group;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.PubMehods;

/**
 * @version 添加自定义分组
 */
public class B_Side_Attence_Main_Class_Defined extends A_0_CpkBaseTitle_Navi {
	
	protected Context mContext;
	private View mLinerLoadError, mLinerWholeView, check_detail_loading,mLinerNoContent;
	private ListView lv_file;
	private List<Cpk_Attence_Group> add_Contacts = new ArrayList<Cpk_Attence_Group>();
	private LinearLayout home_load_loading,share_bottom;
	private AnimationDrawable drawable;
	private TextView tv_has_chooesed;
	private Button btn_cancle,btn_sure;
	private List<String> list_ids = new ArrayList<String>();
	private String ids="";
	private String names="";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setView(R.layout.activity_side_attendence_add_student);
		setTitleText("添加考勤分组");
		
		 ids=getIntent().getStringExtra("organ_ids");
		 if (!ids.equals("")) {
			 ids=ids+",";
		}
		mLinerLoadError = findViewById(R.id.check_load_error);
		mLinerWholeView = findViewById(R.id.liner_check_list_whole_view);
		check_detail_loading = findViewById(R.id.check__loading);
		mLinerNoContent = findViewById(R.id.check_no_content);
		share_bottom = (LinearLayout) findViewById(R.id.share_bottom);
		tv_has_chooesed = (TextView) findViewById(R.id.tv_has_chooesed);
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_sure = (Button) findViewById(R.id.btn_sure);
		tv_has_chooesed.setText("0");
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
		tv_blank_name.setText("暂无联系人~");
		mLinerNoContent.setOnClickListener(onClick);
		mLinerLoadError.setOnClickListener(onClick);
		btn_cancle.setOnClickListener(onClick);
		btn_sure.setOnClickListener(onClick);
		
		lv_file = (ListView)findViewById(R.id.res_lv);
		adapter = new Myadapter();
		lv_file.setAdapter(adapter);

		if(A_0_App.USER_STATUS.equals("2")){
	        getAttdenceList();
        }else{
            showLoadResult(false, false, false,true);
        }
		
	}
	// 数据加载，及网络错误提示
		OnClickListener onClick = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.check_load_error:
					
					showLoadResult(false, false, true,false);
					getAttdenceList();
					break;
				case R.id.check_no_content:
					
					getAttdenceList();
					break;
			    case R.id.btn_cancle:
			    	for (int j = 0; j < add_Contacts.size(); j++) {
							add_Contacts.get(j).setCheck(false);
					}
			    	ids="";
			    	names="";
			    	adapter.notifyDataSetChanged();
			    	tv_has_chooesed.setText("0");
				 break;
				
			    case R.id.btn_sure:
			    	
			    	Intent data = new Intent();
					data.putExtra("organ_name", names);
					data.putExtra("organ_id", ids);
					B_Side_Attence_Main_Class_Defined.this.setResult(6,data);
					finish();
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
	private void getAttdenceList() {
		A_0_App.getApi().AttenceAddContact_Group(B_Side_Attence_Main_Class_Defined.this,A_0_App.USER_TOKEN,new InterAttenceAddContact_Group() {
					@Override
					public void onSuccess(
							List<Cpk_Attence_Group> mList) {
						  if(add_Contacts==null)
			                    return;
	                        if(isFinishing())
	                            return;
						   add_Contacts=mList;
						   for (int i = 0; i < add_Contacts.size(); i++) {
							for (int j = 0; j < A_0_App.attence_result_defined.size(); j++) {
								if (add_Contacts.get(i).getGroup_id().equals(A_0_App.attence_result_defined.get(j).getGroup_id())) {
									add_Contacts.get(i).setCheck(true);
								}
							}
						}
						   adapter.notifyDataSetChanged();
						   if (add_Contacts.size()>0) {
							   share_bottom.setVisibility(View.VISIBLE);
								 showLoadResult(false,true, false,false);
							}else{
								
								showLoadResult(false,false, false,true);
							}
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                         if(add_Contacts==null)
                                return;
                            if(isFinishing())
                                return;
                            share_bottom.setVisibility(View.GONE);
                            PubMehods.showToastStr(B_Side_Attence_Main_Class_Defined.this, msg);
                            showLoadResult(false,false, true,false);
                          
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

		@SuppressWarnings("deprecation")
		@Override
		public View getView(final int position, View convertview, ViewGroup arg2) {

			MyHolder holder;
			if (convertview == null) {
				holder = new MyHolder();
				convertview = View.inflate(
						B_Side_Attence_Main_Class_Defined.this,
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
			
			
			holder.tv_title.setText(add_Contacts.get(position).getGroup_name());
			if (ids.contains(add_Contacts.get(position).getGroup_id()+",")) {
				tv_has_chooesed.setText(add_Contacts.get(position).getGroup_num());
				holder.checBox.setBackgroundDrawable(getResources().getDrawable(R.drawable.send_list_on));
			}else if(add_Contacts.get(position).isCheck()==false){
				holder.checBox.setBackgroundDrawable(getResources().getDrawable(R.drawable.send_list_off));
			}

			holder.liner_checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					
					if (ids.contains(add_Contacts.get(position).getGroup_id()+",")) {
						ids=ids.replace(add_Contacts.get(position).getGroup_id()+",", "");
						list_ids.remove(add_Contacts.get(position).getGroup_id());
						names=ids.replace(add_Contacts.get(position).getGroup_name()+",", "");
						tv_has_chooesed.setText("0");
					}else{
						list_ids.clear();
						ids="";
						names="";
						list_ids.add(add_Contacts.get(position).getGroup_id());
						ids=ids+add_Contacts.get(position).getGroup_id()+",";
						names=names+add_Contacts.get(position).getGroup_name()+",";
						for (int i = 0; i <add_Contacts.size(); i++) {
							if (ids.contains(add_Contacts.get(i).getGroup_id()+",")) {
								tv_has_chooesed.setText(add_Contacts.get(i).getGroup_num());
							}
						}
					}
					
					adapter.notifyDataSetChanged();
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
    public void onDestroy() {
        super.onDestroy();
        add_Contacts = null;
        drawable.stop();
        drawable=null;
       
    }

	@Override
	protected void handleTitleBarEvent(int resId, View v) {

		switch (resId) {
		case BACK_BUTTON:
			this.finish();
			break;
		default:
			break;
		}

	
	}
}
