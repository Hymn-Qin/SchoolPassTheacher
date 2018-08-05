package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Search_Recipient;
import com.yuanding.schoolteacher.service.Api.InterRecipientSearchList;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.CircleImageView;
import com.yuanding.schoolteacher.view.MyListView;

/**
 * 
 * @version 创建时间：2015年11月12日 下午3:18:18 查找收信人
 */
public class B_Side_Notice_Contact_Search_more extends A_0_CpkBaseTitle_Navi{
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, View> lmap_col = new HashMap<Integer, View>();
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, View> lmap_stu = new HashMap<Integer, View>();
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, View> lmap_check = new HashMap<Integer, View>();
	private View mLinerReadDataError, mLinerNoContent,liner_lecture_list_whole_view, side_lecture__loading;
	private MyListView listview_colleague,listview_student;
	private List<Cpk_Search_Recipient> stu_List,teac_List,check_List;
	private Mydapter_Colleague adapter_colleague;
	private Mydapter_Student adapter_student;
	private Mydapter_Check adapter_Check;
	protected Context mContext;
	private EditText mSearchInput;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options_photo;
	private String key="";
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
	private TextView tv_has_chooesed,search_college,search_student,search_history;
	private Button btn_cancle,btn_sure;
    private int choosed_count=0;
    private RelativeLayout rela_colleague,rela_student;
    private ListView listview_check;
    private LinearLayout share_bottom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_notice_search_more);
		A_0_App.getInstance().addActivity_rongyun(this);
		setTitleText("查找收信人");
		imageLoader = A_0_App.getInstance().getimageLoader();
		options_photo  = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center);
		tv_has_chooesed = (TextView) findViewById(R.id.tv_has_chooesed);
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_sure = (Button) findViewById(R.id.btn_sure);
		search_college = (TextView) findViewById(R.id.search_college);
		search_student = (TextView) findViewById(R.id.search_student);
		search_history = (TextView) findViewById(R.id.search_history);
		rela_colleague = (RelativeLayout) findViewById(R.id.rela_colleague);
		rela_student = (RelativeLayout) findViewById(R.id.rela_student);
		listview_check = (ListView) findViewById(R.id.listview_check);
		
		mSearchInput = (EditText)findViewById(R.id.school_friend_member_search_input);
		liner_lecture_list_whole_view =findViewById(R.id.liner_lecture_list_whole_view);
		listview_colleague = (MyListView)findViewById(R.id.listview_colleague);
		listview_student = (MyListView)findViewById(R.id.listview_student);
		share_bottom = (LinearLayout)findViewById(R.id.share_bottom);
		
		listview_colleague.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				display();
				return false;
			}
		});
		listview_check.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				display();
				return false;
			}
		});
		listview_student.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				display();
				return false;
			}
		});
		side_lecture__loading = findViewById(R.id.side_lecture__loading);
		mLinerReadDataError = findViewById(R.id.side_lecture_load_error);
		mLinerNoContent = findViewById(R.id.side_lecture_no_content);
		ImageView iv_blank_por = (ImageView) mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent.findViewById(R.id.tv_blank_name);
		tv_blank_name.setText("没有查找到收信人~");
		iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
		
		home_load_loading = (LinearLayout) side_lecture__loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		
		mLinerReadDataError.setOnClickListener(onClick);
		btn_cancle.setOnClickListener(onClick);
		btn_sure.setOnClickListener(onClick);
		rela_colleague.setOnClickListener(onClick);
		rela_student.setOnClickListener(onClick);
		stu_List = new ArrayList<Cpk_Search_Recipient>();
		teac_List = new ArrayList<Cpk_Search_Recipient>();
		check_List = new ArrayList<Cpk_Search_Recipient>();
		
		adapter_colleague = new Mydapter_Colleague();
		adapter_student = new Mydapter_Student();
		adapter_Check = new Mydapter_Check();
		
		listview_check.setAdapter(adapter_Check);
		listview_colleague.setAdapter(adapter_colleague);
		listview_student.setAdapter(adapter_student);
//		mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//
//					if (mSearchInput.getText().toString().length() > 0) {
//						key=mSearchInput.getText().toString();
//						showLoadResult(true, false, false, false);
//						getLectureList(key,false);
//						 display();
//					}
//
//					return true;
//
//				}
//
//				return false;
//			}
//
//		});
		
       mSearchInput.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
                // 文字变动 ， 有未发出的搜索请求，应取消
                if (mHandler.hasMessages(MSG_SEARCH)) {
                    mHandler.removeMessages(MSG_SEARCH);
                }
                if (TextUtils.isEmpty(arg0)) {
                    showLoadResult(false, false, false, true);
                    listview_check.setVisibility(View.GONE);
                    search_history.setVisibility(View.GONE);
                    share_bottom.setVisibility(View.GONE);
                } else {// 延迟搜索
                    mHandler.sendEmptyMessageDelayed(MSG_SEARCH, TIME_INPUT_REQUEST); // 自动搜索功能
                }
			}
		});
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		if (A_0_App.notice_search_Contacts.size()>0) {
			check_List.addAll(A_0_App.notice_search_Contacts);
			for (int i = 0; i < check_List.size(); i++) {
				check_List.get(i).setCheck(true);
			}
			mLinerNoContent.setVisibility(View.GONE);
			listview_check.setVisibility(View.VISIBLE);
			search_history.setVisibility(View.VISIBLE);
		}else{
			search_history.setVisibility(View.GONE);
			mLinerNoContent.setVisibility(View.GONE);
			  mSearchInput.setFocusable(true);  
			  mSearchInput.setFocusableInTouchMode(true);  
			  mSearchInput.requestFocus();  
			  InputMethodManager inputManager =  (InputMethodManager)mSearchInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
			           inputManager.showSoftInput(mSearchInput, 0); 
		}
		commit();
		tv_has_chooesed.setText((choosed_count+A_0_App.notice_search_Contacts.size())+"");
	}
	
    private static final int MSG_SEARCH = 1;
    public static final int TIME_INPUT_REQUEST = 800;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //搜索请求
            if (mSearchInput.getText().toString().length() > 0) {
                showLoadResult(true, false, false, false);
                listview_check.setVisibility(View.GONE);
                search_history.setVisibility(View.GONE);
                getLectureList( mSearchInput.getText().toString(),false);
            }else{
                getLectureList( mSearchInput.getText().toString(),false);
            }
        }
    };
    
	private void getLectureList(String keyword,final boolean pullRefresh) {
		A_0_App.getApi().getRecipientSearchList(B_Side_Notice_Contact_Search_more.this,A_0_App.USER_TOKEN,keyword,
				new InterRecipientSearchList() {
			@Override
			public void onSuccess(List<Cpk_Search_Recipient> stuList,List<Cpk_Search_Recipient> teacList,String stuIsMore, String teacIsmore) {

						if (B_Side_Notice_Contact_Search_more.this.isFinishing())
							return;
						share_bottom.setVisibility(View.VISIBLE);
						search_history.setVisibility(View.GONE);
						if ((stuList != null && stuList.size() > 0)||(teacList != null && teacList.size() > 0)) {
							stu_List.clear();
							stu_List = stuList;
							
							teac_List.clear();
							teac_List = teacList;
							
							 for (int i = 0; i < A_0_App.notice_search_Contacts.size(); i++) {
	 								for (int j = 0; j < stu_List.size(); j++) {
	 									if (A_0_App.notice_search_Contacts.size()>0) {
	 										if (A_0_App.notice_search_Contacts.get(i).getUser_id().equals(stu_List.get(j).getUser_id())) {
	 											stu_List.get(j).setCheck(true);
		 									}
										}
	 									
	 								}}
							 for (int i = 0; i < A_0_App.notice_search_Contacts.size(); i++) {
	 								for (int j = 0; j < teac_List.size(); j++) {
	 									if (A_0_App.notice_search_Contacts.size()>0) {
	 										if (A_0_App.notice_search_Contacts.get(i).getUser_id().equals(teac_List.get(j).getUser_id())) {
	 											teac_List.get(j).setCheck(true);
		 									}
										}
	 									
	 								}}
							 tv_has_chooesed.setText((choosed_count+A_0_App.notice_search_Contacts.size())+"");
							adapter_student.notifyDataSetChanged();
							adapter_colleague.notifyDataSetChanged();
							showLoadResult(false, true, false, false);
							//check_total();
							listview_check.setVisibility(View.GONE);
							//check_List.clear();
							if (stuIsMore.equals("1")) {
								rela_student.setVisibility(View.VISIBLE);
								
							} else {
								
								rela_student.setVisibility(View.GONE);
							}
							if (teacIsmore.equals("1")) {
								
								rela_colleague.setVisibility(View.VISIBLE);
							} else {
								search_college.setVisibility(View.GONE);
								rela_colleague.setVisibility(View.GONE);
							}
							if (stuList.size()>0) {
								search_student.setVisibility(View.VISIBLE);
							}else{
								search_student.setVisibility(View.GONE);
							}
							if (teacList.size() > 0) {
								search_college.setVisibility(View.VISIBLE);
							} else {
								search_college.setVisibility(View.GONE);
							}
						} else {
							
							   stu_List.clear();
							    teac_List.clear();
							    search_college.setVisibility(View.GONE);
							    search_student.setVisibility(View.GONE);
							    adapter_student.notifyDataSetChanged();
							    adapter_colleague.notifyDataSetChanged();
							//PubMehods.showToastStr(B_Side_Notice_Contact_Search_more.this,"没有查找到收信人~");
							showLoadResult(false, true, false, false);
						}

					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (B_Side_Notice_Contact_Search_more.this.isFinishing())
                            return;
                        //share_bottom.setVisibility(View.GONE);
                            stu_List.clear();
                            teac_List.clear();
                            adapter_student.notifyDataSetChanged();
                            adapter_colleague.notifyDataSetChanged();
                            search_history.setVisibility(View.GONE);
                            listview_check.setVisibility(View.GONE);
                        
                        
                        PubMehods.showToastStr(B_Side_Notice_Contact_Search_more.this, msg);
                        showLoadResult(false, true, false, false);
                    
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}



	private void showLoadResult(boolean loading, boolean wholeView,
			boolean loadFaile, boolean noData) {
		if (wholeView)
			liner_lecture_list_whole_view.setVisibility(View.VISIBLE);
		else
			liner_lecture_list_whole_view.setVisibility(View.GONE);
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
				side_lecture__loading.setVisibility(View.VISIBLE);
			} else {
				if (drawable != null) {
					drawable.stop();
				}
				side_lecture__loading.setVisibility(View.GONE);
			}
	}

	
	 private void commit(){

			for (int i = 0; i < A_0_App.student_temp_Contacts.size(); i++) {//学生
				choosed_count=choosed_count+Integer.parseInt(A_0_App.student_temp_Contacts.get(i)
						.getUser_total_num());
			}
			for (int i = 0; i < A_0_App.student_result_Contacts.size(); i++) {//学生
				choosed_count=choosed_count+Integer.parseInt(A_0_App.student_result_Contacts.get(i)
						.getUser_total_num());
			}
			
			for (int i = 0; i < A_0_App.colleague_temp_Contacts.size(); i++) {//同事
				choosed_count=choosed_count+Integer.parseInt(A_0_App.colleague_temp_Contacts.get(i)
						.getUser_total_num());
			}
			for (int i = 0; i <  A_0_App.colleague_result_Contacts.size(); i++) {//同事
				choosed_count=choosed_count+Integer.parseInt( A_0_App.colleague_result_Contacts.get(i)
						.getUser_total_num());
			}
			
			 for (int i = 0; i < A_0_App.group_temp_Contacts.size(); i++) {//分组
					if (A_0_App.group_temp_Contacts.get(i).getCount()==null) {
						choosed_count=choosed_count+1;
					}else{
						choosed_count=choosed_count+Integer.parseInt(A_0_App.group_temp_Contacts.get(i)
								.getCount());
						
					}
					
				}
			 
			 for (int i = 0; i < A_0_App.group_result_Contacts.size(); i++) {//分组
					if (A_0_App.group_result_Contacts.get(i).getCount()==null) {
						choosed_count=choosed_count+1;
					}else{
						choosed_count=choosed_count+Integer.parseInt(A_0_App.group_result_Contacts.get(i)
								.getCount());
						
					}
					}
			
		}
	
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rela_colleague:
				
				total();
				Intent intent2=new Intent(B_Side_Notice_Contact_Search_more.this, B_Side_Notice_Contact_Search.class);
				intent2.putExtra("type", "1");
				intent2.putExtra("key", mSearchInput.getText().toString());
				startActivity(intent2);
				break;
			case R.id.rela_student:
				total();
				Intent intent=new Intent(B_Side_Notice_Contact_Search_more.this, B_Side_Notice_Contact_Search.class);
				intent.putExtra("type", "2");
				intent.putExtra("key", mSearchInput.getText().toString());
				startActivity(intent);
				break;
			case R.id.side_lecture_load_error:
				
				showLoadResult(true, false, false, false);
				getLectureList(key, true);
				break;
			case R.id.btn_cancle:
				tv_has_chooesed.setText(choosed_count+"");
				for (int i = 0; i < check_List.size(); i++) {
					check_List.get(i).setCheck(false);
				}
				for (int i = 0; i < stu_List.size(); i++) {
					stu_List.get(i).setCheck(false);
				}
				for (int i = 0; i < teac_List.size(); i++) {
					teac_List.get(i).setCheck(false);
				}
				adapter_colleague.notifyDataSetChanged();
				adapter_student.notifyDataSetChanged();
				adapter_Check.notifyDataSetChanged();
				break;
			case R.id.btn_sure:
				total();
				A_0_App.student_result_Contacts_summit.clear();
				A_0_App.colleague_result_Contacts_summit.clear();
				A_0_App.group_result_Contacts_summit.clear();
				A_0_App.summit=1;
				B_Side_Notice_Main_Add_Contacts2.first_load=1;
			    Intent	intent1 = new Intent("student");
				intent1.putExtra("position",5);
				sendBroadcast(intent1);
				
				break;
			default:
				break;
			}
		}
	};

	private void total(){
		A_0_App.notice_search_Contacts.clear();
		for (int i = 0; i < check_List.size(); i++) {
			if (check_List.get(i).isCheck()==true) {
				A_0_App.notice_search_Contacts.add(check_List.get(i));
			}
		}
		
		for (int i = 0; i < stu_List.size(); i++) {
			if (stu_List.get(i).isCheck()==true) {
				A_0_App.notice_search_Contacts.add(stu_List.get(i));
			}
		}
		for (int i = 0; i < teac_List.size(); i++) {
			if (teac_List.get(i).isCheck()==true) {
				A_0_App.notice_search_Contacts.add(teac_List.get(i));
			}
		}
		removeDuplicate(A_0_App.notice_search_Contacts);
	}
	private void check_total(){
		int checked=0;
		
		for (int i = 0; i < check_List.size(); i++) {
			for (int j = 0; j < teac_List.size(); j++) {
				try {
					if (check_List.size()>0) {
						if (check_List.get(i).getUser_id().equals(teac_List.get(j).getUser_id())&&teac_List.get(j).isCheck()==true) {
							check_List.remove(i);
						}
					}
				} catch (Exception e) {
				}
				
				
			}
		}
		for (int i = 0; i < check_List.size(); i++) {
			for (int j = 0; j < stu_List.size(); j++) {
				if (check_List.size()>0) {
					try {
						if (check_List.get(i).getUser_id().equals(stu_List.get(j).getUser_id())&&stu_List.get(j).isCheck()==true) {
							check_List.remove(i);
						}
					} catch (Exception e) {
					}
					
				}
				
			}
		}
		for (int i = 0; i <teac_List.size(); i++) {
			if (teac_List.get(i).isCheck()==true) {
				checked=checked+1;
			}
		}
		
		for (int i = 0; i <check_List.size(); i++) {
			if (check_List.get(i).isCheck()==true) {
				checked=checked+1;
			}
		}
		for (int i = 0; i <stu_List.size(); i++) {
			if (stu_List.get(i).isCheck()==true) {
				checked=checked+1;
			}
		}
		tv_has_chooesed.setText((choosed_count+checked)+"");
	}
	public class Mydapter_Colleague extends BaseAdapter {

		@Override
		public int getCount() {
			if (teac_List != null)
				return teac_List.size();
			return 0;
		}

		@Override
		public Object getItem(int v) {
			return v;
		}

		@Override
		public long getItemId(int v) {
			return v;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (lmap_col.get(posi) == null) {
				
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Side_Notice_Contact_Search_more.this)
						.inflate(R.layout.item_search_contactlist, null);
				holder.tv_name = (TextView) converView
						.findViewById(R.id.tv_black_list_name);
				holder.tv_phone = (TextView) converView
						.findViewById(R.id.tv_black_list_phone);
				holder.liner_search = (LinearLayout) converView
						.findViewById(R.id.liner_search);
				holder.iv_photo = (CircleImageView) converView
						.findViewById(R.id.iv_black_list_por);
				holder.iv_black_lsit_check = (ImageView) converView
						.findViewById(R.id.iv_black_lsit_check);
				lmap_col.put(posi, converView);
				converView.setTag(holder);
			}else{
				converView = lmap_col.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			if (posi % 8 == 0) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_one);

			} else if (posi % 8 == 1) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_two);
			} else if (posi % 8 == 2) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_three);
			} else if (posi % 8 == 3) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_four);
			} else if (posi % 8 == 4) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_five);
			} else if (posi % 8 == 5) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_six);
			} else if (posi % 8 == 6) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_seven);
			} else if (posi % 8 == 7) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_eight);
			}
			
			if (teac_List.get(posi).isCheck()==true) {
				holder.iv_black_lsit_check.setBackgroundResource(R.drawable.register_box_selected);
			}else if(teac_List.get(posi).isCheck()==false){
				holder.iv_black_lsit_check.setBackgroundResource(R.drawable.register_box_unselected);
			}
			
			String uri = teac_List.get(posi).getPhoto_url();
			if(holder.iv_photo.getTag() == null){
				PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			    holder.iv_photo.setTag(uri);
			}else{
			    if(!holder.iv_photo.getTag().equals(uri)){
			    	PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			        holder.iv_photo.setTag(uri);
			    }
			}
			holder.tv_name.setText( teac_List.get(posi).getTrue_name());
			holder.tv_phone.setText(teac_List.get(posi).getPhone());
			
			if(A_0_App.isShowAnimation==true){
			 if (posi > A_0_App.side_found_my_list_curPosi) {
				A_0_App.side_found_my_list_curPosi = posi;
				Animation an = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(20 * posi);
				converView.startAnimation(an);
			 }
			}
			holder.liner_search.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (teac_List.get(posi).isCheck()==false) {
						teac_List.get(posi).setCheck(true);
						for (int i = 0; i < check_List.size(); i++) {
							if (check_List.get(i).getUser_id()==teac_List.get(posi).getUser_id()) {
								check_List.get(i).setCheck(false);
							}
						}
						check_total();
					}else if(teac_List.get(posi).isCheck()==true){
						teac_List.get(posi).setCheck(false);
						for (int i = 0; i < check_List.size(); i++) {
							if (check_List.get(i).getUser_id()==teac_List.get(posi).getUser_id()) {
								check_List.get(i).setCheck(false);
							}
						}
						check_total();
					}
					adapter_colleague.notifyDataSetChanged();
					}
				
				
			});
			holder.iv_photo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(B_Side_Notice_Contact_Search_more.this,
							B_Mess_Persion_Info.class);
					intent.putExtra("uniqid",teac_List.get(posi).getUniqid());
					startActivity(intent);
				
				}
			});
			return converView;
		}

	}

	public class Mydapter_Student extends BaseAdapter {

		@Override
		public int getCount() {
			if (stu_List != null)
				return stu_List.size();
			return 0;
		}

		@Override
		public Object getItem(int v) {
			return v;
		}

		@Override
		public long getItemId(int v) {
			return v;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (lmap_stu.get(posi) == null) {
				
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Side_Notice_Contact_Search_more.this)
						.inflate(R.layout.item_search_contactlist, null);
				holder.tv_name = (TextView) converView
						.findViewById(R.id.tv_black_list_name);
				holder.tv_phone = (TextView) converView
						.findViewById(R.id.tv_black_list_phone);
				holder.liner_search = (LinearLayout) converView
						.findViewById(R.id.liner_search);
				holder.iv_photo = (CircleImageView) converView
						.findViewById(R.id.iv_black_list_por);
				holder.iv_black_lsit_check = (ImageView) converView
						.findViewById(R.id.iv_black_lsit_check);
				lmap_stu.put(posi, converView);
				converView.setTag(holder);
			}else{
				converView = lmap_stu.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			if (posi % 8 == 0) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_one);

			} else if (posi % 8 == 1) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_two);
			} else if (posi % 8 == 2) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_three);
			} else if (posi % 8 == 3) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_four);
			} else if (posi % 8 == 4) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_five);
			} else if (posi % 8 == 5) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_six);
			} else if (posi % 8 == 6) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_seven);
			} else if (posi % 8 == 7) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_eight);
			}
			
			if (stu_List.get(posi).isCheck()==true) {
				holder.iv_black_lsit_check.setBackgroundResource(R.drawable.register_box_selected);
			}else if(stu_List.get(posi).isCheck()==false){
				holder.iv_black_lsit_check.setBackgroundResource(R.drawable.register_box_unselected);
			}
			String uri = stu_List.get(posi).getPhoto_url();
			if(holder.iv_photo.getTag() == null){
				PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			    holder.iv_photo.setTag(uri);
			}else{
			    if(!holder.iv_photo.getTag().equals(uri)){
			    	PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			        holder.iv_photo.setTag(uri);
			    }
			}
			holder.tv_name.setText(stu_List.get(posi).getTrue_name());
			holder.tv_phone.setText(stu_List.get(posi).getPhone());
			
			if(A_0_App.isShowAnimation==true){
			 if (posi > A_0_App.side_found_my_list_curPosi) {
				A_0_App.side_found_my_list_curPosi = posi;
				Animation an = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(20 * posi);
				converView.startAnimation(an);
			 }
			}
			holder.liner_search.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (stu_List.get(posi).isCheck()==false) {
						stu_List.get(posi).setCheck(true);
						for (int i = 0; i < check_List.size(); i++) {
							if (check_List.get(i).getUser_id()==stu_List.get(posi).getUser_id()) {
								check_List.get(i).setCheck(false);
							}
						}
						check_total();
					}else if(stu_List.get(posi).isCheck()==true){
						stu_List.get(posi).setCheck(false);
						for (int i = 0; i < check_List.size(); i++) {
							if (check_List.get(i).getUser_id()==stu_List.get(posi).getUser_id()) {
								check_List.get(i).setCheck(false);
							}
						}
						check_total();
					}
					adapter_student.notifyDataSetChanged();
					}
				
				
			});
         holder.iv_photo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(B_Side_Notice_Contact_Search_more.this,
							B_Mess_Persion_Info.class);
					intent.putExtra("uniqid",stu_List.get(posi).getUniqid());
					startActivity(intent);
				
				}
			});
			return converView;
		}

	} 
	public class Mydapter_Check extends BaseAdapter {

		@Override
		public int getCount() {
			if (check_List!= null)
				return check_List.size();
			return 0;
		}

		@Override
		public Object getItem(int v) {
			return v;
		}

		@Override
		public long getItemId(int v) {
			return v;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (lmap_check.get(posi) == null) {
				
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Side_Notice_Contact_Search_more.this)
						.inflate(R.layout.item_search_contactlist, null);
				holder.tv_name = (TextView) converView
						.findViewById(R.id.tv_black_list_name);
				holder.tv_phone = (TextView) converView
						.findViewById(R.id.tv_black_list_phone);
				holder.liner_search = (LinearLayout) converView
						.findViewById(R.id.liner_search);
				holder.iv_photo = (CircleImageView) converView
						.findViewById(R.id.iv_black_list_por);
				holder.iv_black_lsit_check = (ImageView) converView
						.findViewById(R.id.iv_black_lsit_check);
				lmap_check.put(posi, converView);
				converView.setTag(holder);
			}else{
				converView = lmap_check.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			if (posi % 8 == 0) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_one);

			} else if (posi % 8 == 1) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_two);
			} else if (posi % 8 == 2) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_three);
			} else if (posi % 8 == 3) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_four);
			} else if (posi % 8 == 4) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_five);
			} else if (posi % 8 == 5) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_six);
			} else if (posi % 8 == 6) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_seven);
			} else if (posi % 8 == 7) {
				holder.iv_photo.setBackgroundResource(R.drawable.photo_eight);
			}
			
			if (check_List.get(posi).isCheck()==true) {
				holder.iv_black_lsit_check.setBackgroundResource(R.drawable.register_box_selected);
			}else if(check_List.get(posi).isCheck()==false){
				holder.iv_black_lsit_check.setBackgroundResource(R.drawable.register_box_unselected);
			}
			
			String uri = check_List.get(posi).getPhoto_url();
			if(holder.iv_photo.getTag() == null){
				PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			    holder.iv_photo.setTag(uri);
			}else{
			    if(!holder.iv_photo.getTag().equals(uri)){
			    	PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			        holder.iv_photo.setTag(uri);
			    }
			}
			holder.tv_name.setText(check_List.get(posi).getTrue_name());
			holder.tv_phone.setText(check_List.get(posi).getPhone());
			
			if(A_0_App.isShowAnimation==true){
			 if (posi > A_0_App.side_found_my_list_curPosi) {
				A_0_App.side_found_my_list_curPosi = posi;
				Animation an = new TranslateAnimation(
						Animation.RELATIVE_TO_SELF, 1,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0,
						Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(20 * posi);
				converView.startAnimation(an);
			 }
			}
			holder.liner_search.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (check_List.get(posi).isCheck()==false) {
						check_List.get(posi).setCheck(true);
						int checked=0;
						for (int i = 0; i <check_List.size(); i++) {
							if (check_List.get(i).isCheck()==true) {
								checked=checked+1;
							}
						}
						tv_has_chooesed.setText((choosed_count+checked)+"");
					}else if(check_List.get(posi).isCheck()==true){
						check_List.get(posi).setCheck(false);
						int checked=0;
						for (int i = 0; i <check_List.size(); i++) {
							if (check_List.get(i).isCheck()==true) {
								checked=checked+1;
							}
						}
						tv_has_chooesed.setText((choosed_count+checked)+"");
					}
					adapter_Check.notifyDataSetChanged();
					}
				
				
			});
			 holder.iv_photo.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(B_Side_Notice_Contact_Search_more.this,
								B_Mess_Persion_Info.class);
						intent.putExtra("uniqid",check_List.get(posi).getUniqid());
						startActivity(intent);
					
					}
				});
			return converView;
		}

	}
	class ViewHolder {
		TextView tv_name;
		TextView tv_phone;
		LinearLayout liner_search;
		CircleImageView iv_photo;
		ImageView iv_black_lsit_check;
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		//display();
	}
	@Override
	public void onDestroy() {
		if (stu_List != null) {
			stu_List.clear();
		}
		if (teac_List != null) {
			teac_List.clear();
		}
		drawable.stop();
		drawable=null;
		adapter_colleague = null;
		adapter_student = null;
		super.onDestroy();
	}
	public static float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }
	
	void display(){
		 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.hideSoftInputFromWindow(mSearchInput.getWindowToken(), 0);

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
                    //A_0_App.getInstance().showExitDialog(B_Side_Found_Search.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Notice_Contact_Search_more.this, AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
	/**
	 * 
	 * @param list 过滤列表重复元素
	 */
	public static void removeDuplicate(List<Cpk_Search_Recipient> list) {
		   for ( int i = 0 ; i < list.size() - 1 ; i ++ ) {
		     for ( int j = list.size() - 1 ; j > i; j -- ) {
		       if (list.get(j).getUser_id().equals(list.get(i).getUser_id())) {
		    	   
		         list.remove(j);
		       }
		      }
		    }
		   
		}
	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			overridePendingTransition(R.anim.animal_push_right_in_normal,
					R.anim.animal_push_right_out_normal);
			break;
		default:
			break;
		}
	}
	
}
