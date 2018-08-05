package com.yuanding.schoolteacher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yuanding.schoolteacher.bean.Cpk_Side_Attence_Add_Contact;
import com.yuanding.schoolteacher.service.Api.InterNoticeAddContact_Student;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年2月22日 上午10:56:19 添加通知收信人学生
 */
public class B_Side_Notice_Add_Contact_Student extends Fragment {
	protected Context mContext;
	private View mLinerLoadError, mLinerWholeView, check_detail_loading,mLinerNoContent, viewone;
	private ListView lv_file;
	private String parentid;
	private String back_level="0";
	private List<Cpk_Side_Attence_Add_Contact> add_Contacts = new ArrayList<Cpk_Side_Attence_Add_Contact>();

	private String back = "0";
	private String temp = "0";
	
	
	public FinishReceiver_Student finishReceiver;
	private int position=0;
	public static int student=1;//判断最终退出
	private int first=0;//进度条崩溃添加字段
	public static int bottom=0;//
	private String temp_id="";
	private String praise_temp="";
	private LinearLayout home_load_loading;
	private AnimationDrawable drawable;
	public B_Side_Notice_Add_Contact_Student() {
		super();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity.getApplicationContext();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		viewone = inflater.inflate(R.layout.activity_side_attendence_add_student, container, false);
		A_0_App.add_position=0;
		mLinerLoadError = viewone.findViewById(R.id.check_load_error);
		mLinerWholeView = viewone.findViewById(R.id.liner_check_list_whole_view);
		check_detail_loading = viewone.findViewById(R.id.check__loading);
		mLinerNoContent = viewone.findViewById(R.id.check_no_content);
		
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
		lv_file = (ListView) viewone.findViewById(R.id.res_lv);
		
		
		finishReceiver = new FinishReceiver_Student();
		IntentFilter intentFilter = new IntentFilter("student");
		getActivity().registerReceiver(finishReceiver, intentFilter);
		
		
		
		adapter = new Myadapter();
		lv_file.setAdapter(adapter);

		if(A_0_App.USER_STATUS.equals("2")){
	        getAttdenceList("0", "0");
        }else{
            showLoadResult(false, false, false,true);
        }
		return viewone;
		
	
	
		
	}
	// 数据加载，及网络错误提示
		OnClickListener onClick = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				switch (arg0.getId()) {
				case R.id.check_load_error:
					
					//A_0_App.student_temp_Contacts.clear();
					//A_0_App.student_result_Contacts.clear();
					showLoadResult(false, false, true,false);
					getAttdenceList("0", "0");
					break;

					
				case R.id.check_no_content:
					
					
					//A_0_App.student_temp_Contacts.clear();
					//A_0_App.student_result_Contacts.clear();
					
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
	private void getAttdenceList(String is_back, final String organ_id) {
		
		A_0_App.getApi().NoticeAddContact_Student(getActivity(),
				A_0_App.USER_TOKEN, is_back, organ_id,
				new InterNoticeAddContact_Student() {

					@Override
					public void onSuccess(
							List<Cpk_Side_Attence_Add_Contact> mList,
							String level) {
						 
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
						if (add_Contacts==null) {
							return;
						}else{
							if (first!=0) {
								A_0_App.getInstance().CancelProgreDialog(getActivity());
							}
							
						}
						
						
						
						if (back.equals("1")) {
							
							
							//返回
							if (add_Contacts.size()>0) {
								
							if (A_0_App.student_temp_Contacts.size()==add_Contacts.size()) {
								if (add_Contacts!=null) {
									add_Contacts.clear();
								}
	                             add_Contacts=mList;
	                             A_0_App.student_temp_Contacts.clear();
	                             for (int i = 0; i < add_Contacts.size(); i++) {
	                            	 
									if (add_Contacts.get(i).getOrgan_id().equals(parentid)) {
										add_Contacts.get(i).setCheck(true);
										A_0_App.student_temp_Contacts.add(add_Contacts.get(i));
									}
								}
	                             
	                             for (int i = 0; i < A_0_App.student_result_Contacts.size(); i++) {
	 								for (int j = 0; j < add_Contacts.size(); j++) {
	 									if (A_0_App.student_result_Contacts.size()>0) {
	 										if (A_0_App.student_result_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(j).getOrgan_id())) {
		 										A_0_App.student_result_Contacts.remove(i);
		 										A_0_App.student_temp_Contacts.add(add_Contacts.get(j));
		 										add_Contacts.get(j).setCheck(true);
		 									}
										}
	 									
	 								}
	 							}
	                            
							}else{
								A_0_App.student_result_Contacts.addAll(A_0_App.student_temp_Contacts);
								if (add_Contacts!=null) {
									add_Contacts.clear();
								}
	                            add_Contacts=mList;
	                            A_0_App.student_temp_Contacts.clear();
	                            
	                            try {
	                            	for (int i = 0; i < A_0_App.student_result_Contacts.size(); i++) {
										for (int j = 0; j < add_Contacts.size(); j++) {
											if (A_0_App.student_result_Contacts.size()>0) {
												if (A_0_App.student_result_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(j).getOrgan_id())) {
													A_0_App.student_result_Contacts.remove(i);
													A_0_App.student_temp_Contacts.add(add_Contacts.get(j));
													add_Contacts.get(j).setCheck(true);
													
												}
											}
											
										}
									}
								} catch (Exception e) {
								}
								
							}
							}
							
						} 
						 
						if (back.equals("0")) {//正常
							if (add_Contacts!=null) {
								add_Contacts.clear();
							}
                            add_Contacts=mList;
                            if (A_0_App.student_all_Contacts.size()==0) {
                            	A_0_App.student_all_Contacts.addAll(mList);
                            	for (int i = 0; i < A_0_App.student_all_Contacts.size(); i++) {
                            		String temp=A_0_App.student_all_Contacts.get(i).getOrgan_id();
                            		A_0_App.student_all_Contacts.get(i).setNext(temp+",");
								}
							}else{
								
								for (int i = 0; i < mList.size(); i++) {
									String temp=mList.get(i).getOrgan_id()+","+mList.get(i).getParentid()+",";
									mList.get(i).setNext(temp);
								}
								
								A_0_App.student_all_Contacts.addAll(mList);
								removeDuplicate(A_0_App.student_all_Contacts);
								String temp="";
                             for (int i = 0; i <A_0_App.student_all_Contacts.size(); i++) {
                            	 
                            	 if (A_0_App.student_all_Contacts.get(i).getNext().split(",").length>1) {
										
                            		 if (A_0_App.student_all_Contacts.get(i).getOrgan_id().equals(organ_id)) {
                            			 temp=A_0_App.student_all_Contacts.get(i).getNext();
 									}
									if (A_0_App.student_all_Contacts.get(i).getParentid().equals(organ_id)) {
										String m=A_0_App.student_all_Contacts.get(i).getNext();
										A_0_App.student_all_Contacts.get(i).setNext(temp+m);
									}	
									
										
									
								}

								
							}
                             
							}
                           
                            A_0_App.student_temp_Contacts.clear();
							if (temp.equals("1")) {
								for (int i = 0; i < A_0_App.student_result_Contacts.size(); i++) {
									for (int j = 0; j < add_Contacts.size(); j++) {
										if (A_0_App.student_result_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(j).getOrgan_id())) {
											A_0_App.student_result_Contacts.remove(i);
										}
									}
									
								}
								for (int i = 0; i < add_Contacts.size(); i++) {
									add_Contacts.get(i).setCheck(true);
									
								}
								A_0_App.student_temp_Contacts.addAll(add_Contacts);
							}else{
								
								 for (int i = 0; i < A_0_App.student_temp_Contacts.size(); i++) {
										for (int j = 0; j < add_Contacts.size(); j++) {
											if (A_0_App.student_temp_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(j).getOrgan_id())) {
												add_Contacts.get(i).setCheck(true);
											}
										}
									}
								 try {
									 if (A_0_App.student_result_Contacts.size()>0) {
											Iterator<Cpk_Side_Attence_Add_Contact> iter = A_0_App.student_result_Contacts.iterator(); 
											while(iter.hasNext()){  
												Cpk_Side_Attence_Add_Contact s = iter.next(); 
												for (int j = 0; j < add_Contacts.size(); j++) {
				 									if ( A_0_App.student_result_Contacts.size()>0) {
				 										if (s.getOrgan_id().equals(add_Contacts.get(j).getOrgan_id())) {
				 											 iter.remove(); 
				 											 A_0_App.student_temp_Contacts.add(add_Contacts.get(j));
					 										 add_Contacts.get(j).setCheck(true);
														}
				 									}
				 									
				 								}
												
										}
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
									
								 
//								 for (int i = 0; i < A_0_App.student_result_Contacts.size(); i++) {
//		 								for (int j = 0; j < add_Contacts.size(); j++) {
//		 									if (A_0_App.student_result_Contacts.size()>0) {
//		 										try {
//		 											if (A_0_App.student_result_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(j).getOrgan_id())) {
//				 										A_0_App.student_result_Contacts.remove(i);
//				 										A_0_App.student_temp_Contacts.add(add_Contacts.get(j));
//				 										add_Contacts.get(j).setCheck(true);
//				 									}
//												} catch (Exception e) {
//													
//												}
//		 										
//											}
//		 									
//		 								}
//		 							}
								 
								
							}
							
							bottom=1;
							Intent intent1 = new Intent("add");
		    				intent1.putExtra("all", "6");
		    				if (getActivity()!=null) {
		    					getActivity().sendBroadcast(intent1);
							}
		    				
						}
						back_level = level;
						if (level.equals("0")) {
							if (add_Contacts.size()>0) {
								for (int i = 0; i < add_Contacts.size(); i++) {
									temp_id=temp_id+add_Contacts.get(i).getParentid()+",";
								}
							
						}}
						if (position == 5) {
							if (add_Contacts.size()>0) {
								parentid=add_Contacts.get(0).getParentid();
								
							}
							
							Intent intent1 = new Intent("student");
							intent1.putExtra("position", 6);
							if (getActivity()!=null) {
		    					getActivity().sendBroadcast(intent1);
							}
						} else {
							if (add_Contacts.size()>0&&temp_id.contains(add_Contacts.get(0).getParentid()+",")&&level.equals("0")) {
								
								int temp=0;
								for (int i = 0; i < add_Contacts.size(); i++) {
									if (add_Contacts.get(i).isCheck()==true) {
										temp=temp+1;
									}else{
										temp=temp-1;
									}
								}
								if (add_Contacts.size()==temp) {
									Cpk_Side_Attence_Add_Contact add_Contact_all=new Cpk_Side_Attence_Add_Contact();
									add_Contact_all.setOrgan_name("全部");
									add_Contact_all.setChild_total_num("0");
									add_Contact_all.setCheck(true);
									add_Contacts.add(0,add_Contact_all);
								}else{
									Cpk_Side_Attence_Add_Contact add_Contact_all=new Cpk_Side_Attence_Add_Contact();
									add_Contact_all.setOrgan_name("全部");
									add_Contact_all.setChild_total_num("0");
									add_Contact_all.setCheck(false);
									add_Contacts.add(0,add_Contact_all);
								}
								
								
							}
							adapter.notifyDataSetChanged();
						}	
						
						if (add_Contacts.size()>0) {
							
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
                        bottom=0;
                        if(getActivity() == null || getActivity().isFinishing()||getActivity().isDestroyed())
                            return;
                         if(add_Contacts==null){
                             return;
                         }else{
                              PubMehods.showToastStr(getActivity(), msg);
                                showLoadResult(false,false, true,false);
                                if (position==5) {
                                    student=1;
                                    Intent intent1 = new Intent("add");
                                    intent1.putExtra("all", "4");
                                    if (getActivity()!=null) {
                                        getActivity().sendBroadcast(intent1);
                                    }
                                }
                                A_0_App.getInstance().CancelProgreDialog(getActivity());
                         }
                                
                          
                           
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
						getActivity(),
						R.layout.item_side_attence_add_student, null);
				holder.checBox = (CheckBox) convertview
						.findViewById(R.id.cb_share_detail);
				holder.liner_checkbox = (ImageView) convertview
						.findViewById(R.id.liner_checkbox);
				holder.tv_title = (TextView) convertview
						.findViewById(R.id.tv_txt_name);
				convertview.setTag(holder);

			} else {
				holder = (MyHolder) convertview.getTag();
			}

			
			if (add_Contacts.size()>position) {
				parentid = add_Contacts.get(position).getParentid();
				holder.tv_title.setText(add_Contacts.get(position).getOrgan_name());
				if (add_Contacts.get(position).isCheck()==true) {
					holder.checBox.setBackgroundDrawable(getResources().getDrawable(R.drawable.send_list_on));
				}else if(add_Contacts.get(position).isCheck()==false){
					holder.checBox.setBackgroundDrawable(getResources().getDrawable(R.drawable.send_list_off));
				}
              }
			
			
			holder.liner_checkbox.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (add_Contacts.get(position).getOrgan_name().equals("全部")) {
						if (add_Contacts.get(position).isCheck() == false) {
							for (int i = 0; i < add_Contacts.size(); i++) {
								add_Contacts.get(i).setCheck(true);
							}
							Iterator<Cpk_Side_Attence_Add_Contact> iter = A_0_App.student_temp_Contacts.iterator(); 
							while(iter.hasNext()){  
								Cpk_Side_Attence_Add_Contact s = iter.next(); 
							    if(temp_id.contains(s.getParentid()+",")){ 
							        iter.remove();  
							    
							}
						}
							 A_0_App.student_temp_Contacts.addAll(add_Contacts);
							 Iterator<Cpk_Side_Attence_Add_Contact> iter2= A_0_App.student_temp_Contacts.iterator(); 
								while(iter2.hasNext()){  
									Cpk_Side_Attence_Add_Contact s = iter2.next(); 
								    if(s.getOrgan_name().contains("全部")){ 
								        iter2.remove();  
								    
								}
							}
						}else{
							for (int i = 0; i < add_Contacts.size(); i++) {
								add_Contacts.get(i).setCheck(false);
							}
							Iterator<Cpk_Side_Attence_Add_Contact> iter = A_0_App.student_temp_Contacts.iterator(); 
							while(iter.hasNext()){  
								Cpk_Side_Attence_Add_Contact s = iter.next(); 
							    if(temp_id.contains(s.getParentid()+",")){ 
							        iter.remove();  
							    
							}
						}
							A_0_App.student_temp_Contacts.clear();
							A_0_App.student_result_Contacts.clear();
						}
						Intent intent1 = new Intent("add");
						intent1.putExtra("all", "1");
						if (getActivity()!=null) {
	    					getActivity().sendBroadcast(intent1);
						}
					}else{
					if (add_Contacts.get(position).isCheck() == false) {
						add_Contacts.get(position).setCheck(true);
						A_0_App.student_temp_Contacts.add(add_Contacts.get(position));
						for (int j = 0; j < A_0_App.student_result_Contacts.size(); j++) {
							for (int i = 0; i < A_0_App.student_all_Contacts.size(); i++) {
								if (A_0_App.student_result_Contacts.get(j).getOrgan_id().equals(A_0_App.student_all_Contacts.get(i).getOrgan_id())) {
									
									A_0_App.student_result_Contacts.get(j).setNext(A_0_App.student_all_Contacts.get(i).getNext());
							}
						}
					}
						
						if (A_0_App.student_result_Contacts.size()>0) {
							Iterator<Cpk_Side_Attence_Add_Contact> iter = A_0_App.student_result_Contacts.iterator(); 
							while(iter.hasNext()){  
								Cpk_Side_Attence_Add_Contact s = iter.next(); 
							    if(s.getNext().contains(add_Contacts.get(position).getOrgan_id()+",")){ 
							        iter.remove();  
							    
							}
						}
					}
						if (A_0_App.student_result_Contacts.size()>0) {
								Iterator<Cpk_Side_Attence_Add_Contact> iter = A_0_App.student_result_Contacts.iterator(); 
								while(iter.hasNext()){  
									Cpk_Side_Attence_Add_Contact s = iter.next(); 
								    if(s.getParentid().equals(add_Contacts.get(position).getOrgan_id())){ 
								        iter.remove();  
								    
								}
							}
						}
						
						Intent intent1 = new Intent("add");
						intent1.putExtra("all", "1");
						if (getActivity()!=null) {
	    					getActivity().sendBroadcast(intent1);
						}
						if (temp_id.contains(add_Contacts.get(position).getParentid()+",")) {
							int temp=0;
							for (int i = 0; i < add_Contacts.size(); i++) {
								if (!add_Contacts.get(i).getOrgan_name().equals("全部")) {
									if (add_Contacts.get(i).isCheck()==true) {
										temp=temp+1;
									} else {
										temp=temp-1;
									}
								} 
							}
							if (temp==add_Contacts.size()-1) {
								for (int i = 0; i < add_Contacts.size(); i++) {
									if (add_Contacts.get(i).getOrgan_name().equals("全部")) {
										add_Contacts.get(i).setCheck(true);
									}
								}
							}
							
						}
						
						
					} else {
						if (temp_id.contains(add_Contacts.get(position).getParentid()+",")) {

							for (int i = 0; i < add_Contacts.size(); i++) {
								if (add_Contacts.get(i).getOrgan_name().equals("全部")) {
									add_Contacts.get(i).setCheck(false);
								}
							}
						
						}
						add_Contacts.get(position).setCheck(false);
						for (int i = 0; i < A_0_App.student_temp_Contacts.size(); i++) {
							if (A_0_App.student_temp_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(position).getOrgan_id())) {
								A_0_App.student_temp_Contacts.remove(i);
							}
						}
						Intent intent1 = new Intent("add");
						intent1.putExtra("all", "2");
						if (getActivity()!=null) {
	    					getActivity().sendBroadcast(intent1);
						}
						
					}}
					
					
					adapter.notifyDataSetChanged();
				}

			});

			convertview.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (!add_Contacts.get(position).getOrgan_name().equals("全部")) {
					
					if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)||!praise_temp.equals(add_Contacts.get(position).getOrgan_id())) {
						praise_temp=add_Contacts.get(position).getOrgan_id();
					if (Integer.parseInt(add_Contacts.get(position).getChild_total_num()) > 0) {
						first=1;
						if (add_Contacts.get(position).isCheck() == true) {
							temp = "1";
							for (int i = 0; i < A_0_App.student_temp_Contacts.size(); i++) {
								if (A_0_App.student_temp_Contacts.get(i).getOrgan_id().equals(add_Contacts.get(position).getOrgan_id())) {
									A_0_App.student_temp_Contacts.remove(i);
								}
							}
							
						} else {
							temp = "0";
						}
						A_0_App.getInstance().showProgreDialog(getActivity(),"", true);
                        back="0";
						A_0_App.student_result_Contacts.addAll(A_0_App.student_temp_Contacts);
						getAttdenceList("0", add_Contacts.get(position)
								.getOrgan_id());
					}
					}
				}}
			});
			return convertview;
		}

	}

	private class MyHolder {
		CheckBox checBox;
		TextView tv_title;
		ImageView liner_checkbox;
	}


	public class FinishReceiver_Student extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			if (arg1.getIntExtra("position", 0)==0) {//当前页面返回
				if (Integer.parseInt(back_level) != 0) {
					if (back_level.equals("1")) {
						 temp = "0";
		   				 back = "1";
		   				
		   				getAttdenceList("0", "0");
					}else{
						 temp = "0";
		   				 back = "1";
		   				getAttdenceList("1", parentid);
					}
   				
   				A_0_App.getInstance().showProgreDialog(getActivity(),"", true);
   			} else {
   				Intent intent1 = new Intent("add");
				intent1.putExtra("all", "5");
				if (getActivity()!=null) {
					getActivity().sendBroadcast(intent1);
				}
   			}
			}else if(arg1.getIntExtra("position", 0)==3){//取消刷新

				if (A_0_App.add_position==0) {
					A_0_App.student_temp_Contacts.clear();
					A_0_App.student_result_Contacts.clear();
					for (int i = 0; i < add_Contacts.size(); i++) {
						add_Contacts.get(i).setCheck(false);
					}
					adapter.notifyDataSetChanged();
					PubMehods.showToastStr(getActivity(), "该类目下收信人已取消选择！");
					Intent intent1 = new Intent("add");
					intent1.putExtra("all", "2");
					if (getActivity()!=null) {
    					getActivity().sendBroadcast(intent1);
					}
				}
				
			
			}else if(arg1.getIntExtra("position", 0)==5){//提交返回
				 position=5;
				if (Integer.parseInt(back_level) != 0) {
				 student=0;
  				 temp = "0";
  				 back = "1";
  				getAttdenceList("1", parentid);
  			} else {
  				student=1;
  				Intent intent1 = new Intent("add");
				intent1.putExtra("all", "4");
				if (getActivity()!=null) {
					getActivity().sendBroadcast(intent1);
				}
  			}
			
			}else if(arg1.getIntExtra("position", 0)==6){//提交返回
				if (Integer.parseInt(back_level) != 0) {
				 student=0;
 				 temp = "0";
 				 back = "1";
 				getAttdenceList("1", parentid);
 			} else {
 				student=1;
 				Intent intent1 = new Intent("add");
				intent1.putExtra("all", "4");
				if (getActivity()!=null) {
					getActivity().sendBroadcast(intent1);
				}
				
 			}}
			
		}}


    /**
     * @param list 过滤重复元素
     */
    public static void removeDuplicate(List<Cpk_Side_Attence_Add_Contact> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getOrgan_id().equals(list.get(i).getOrgan_id())) {

                    list.remove(j);
                }
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        add_Contacts = null;
        drawable.stop();
        drawable=null;
        try {
            getActivity().unregisterReceiver(finishReceiver);
        } catch (Exception e) {

        }
    }
}
