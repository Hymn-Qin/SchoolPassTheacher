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
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Search_Recipient;
import com.yuanding.schoolteacher.service.Api.InterRecipientSearchMoreList;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.CircleImageView;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * 
 * @version 创建时间：2015年11月12日 下午3:18:18 查找收信人
 */
public class B_Side_Notice_Contact_Search extends A_0_CpkBaseTitle_Navi{
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, View> lmap = new HashMap<Integer, View>();
	private View mLinerReadDataError, mLinerNoContent,
			liner_lecture_list_whole_view, side_lecture__loading;
	private PullToRefreshListView mPullDownView;
	private List<Cpk_Search_Recipient> mLecturesList,temp_all_list;
	private Mydapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private Boolean firstLoad = false;
	protected Context mContext;
	private EditText mSearchInput;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options_photo;
	private String key="";
	
	/**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
	private TextView tv_has_chooesed;
	private Button btn_cancle,btn_sure;
    private int choosed_count=0;
    private String type="";
    private LinearLayout share_bottom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_notice_search);
		A_0_App.getInstance().addActivity_rongyun(this);
		firstLoad = true;
		imageLoader = A_0_App.getInstance().getimageLoader();
		options_photo  = A_0_App.getInstance().getOptions(R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center);
		type=getIntent().getStringExtra("type");
		if (type.equals("1")) {
			setTitleText("同事");
		}else{
			setTitleText("学生");
		}
		mLecturesList = new ArrayList<Cpk_Search_Recipient>();
		temp_all_list = new ArrayList<Cpk_Search_Recipient>();
		tv_has_chooesed = (TextView) findViewById(R.id.tv_has_chooesed);
		btn_cancle = (Button) findViewById(R.id.btn_cancle);
		btn_sure = (Button) findViewById(R.id.btn_sure);
		key=getIntent().getStringExtra("key");
		share_bottom = (LinearLayout)findViewById(R.id.share_bottom);
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		mSearchInput = (EditText)findViewById(R.id.school_friend_member_search_input);
		mSearchInput.setText(key+"");
		liner_lecture_list_whole_view =findViewById(R.id.liner_lecture_list_whole_view);
		mPullDownView = (PullToRefreshListView)findViewById(R.id.lv_side_lecture_list);
		side_lecture__loading = findViewById(R.id.side_lecture__loading);
		mLinerReadDataError = findViewById(R.id.side_lecture_load_error);
		mLinerNoContent = findViewById(R.id.side_lecture_no_content);
		ImageView iv_blank_por = (ImageView) mLinerNoContent
				.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent
				.findViewById(R.id.tv_blank_name);
		tv_blank_name.setText("没有查到收信人~");
		iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
		
		home_load_loading = (LinearLayout) side_lecture__loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		commit();
		temp_all_list.addAll(A_0_App.notice_search_Contacts);
		tv_has_chooesed.setText((choosed_count+temp_all_list.size())+"");
		mLinerReadDataError.setOnClickListener(onClick);
		btn_cancle.setOnClickListener(onClick);
		btn_sure.setOnClickListener(onClick);
		
		
		adapter = new Mydapter();
		mPullDownView.setMode(Mode.BOTH);
		mPullDownView.setAdapter(adapter);
		mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// 下拉刷新
						String label = DateUtils.formatDateTime(
								B_Side_Notice_Contact_Search.this,System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						have_read_page = 1;
						getLectureList(have_read_page,key, true);
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						
						if (repfresh==0) {
							repfresh=1;
							demo_swiperefreshlayout.setEnabled(false);
							demo_swiperefreshlayout.setRefreshing(false);  
							getMoreLecture(have_read_page,key);
						}
					}

				});
		
		/**
		 * 新增下拉使用 new add
		 */
		demo_swiperefreshlayout.setSize(SwipeRefreshLayout.DEFAULT);
		demo_swiperefreshlayout.setColorSchemeResources(R.color.main_color);
		if (repfresh == 0) {
			repfresh = 1;
			if(null!=mPullDownView){
			    mPullDownView.onRefreshComplete();
			}
			demo_swiperefreshlayout
					.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {
							have_read_page = 1;
							if(null!=mPullDownView){
							    mPullDownView.setMode(Mode.DISABLED);
							}
							getLectureList(have_read_page,key, true);

						};
					});
		}
		
		
      mPullDownView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				 if (demo_swiperefreshlayout!=null&&mPullDownView.getChildCount() > 0 && mPullDownView.getRefreshableView().getFirstVisiblePosition() == 0
			                && mPullDownView.getChildAt(0).getTop() >= mPullDownView.getPaddingTop()) {
			            //解决滑动冲突，当滑动到第一个item，下拉刷新才起作用
					   demo_swiperefreshlayout.setEnabled(true);
			        } else {
			        	demo_swiperefreshlayout.setEnabled(false);
			        }
				
			}
			
			@Override
			public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
			}
		});
      
      //**************************新增到这**********************
		
		
		
//		mSearchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//
//					if (mSearchInput.getText().toString().length() > 0) {
//						key=mSearchInput.getText().toString();
//						have_read_page = 1;
//						showLoadResult(true, false, false, false);
//						getLectureList(have_read_page, key,false);
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
//		
		 mSearchInput.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
					
					
					
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					have_read_page=1;
	                // 文字变动 ， 有未发出的搜索请求，应取消
	                if (mHandler.hasMessages(MSG_SEARCH)) {
	                    mHandler.removeMessages(MSG_SEARCH);
	                }
	                if (TextUtils.isEmpty(arg0)) {
	                    showLoadResult(false, false, false, true);
	                    share_bottom.setVisibility(View.GONE);
	                } else {// 延迟搜索
	                    mHandler.sendEmptyMessageDelayed(MSG_SEARCH, TIME_INPUT_REQUEST); // 自动搜索功能
	                }
				
				}
			});
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		getLectureList(have_read_page,key, false);
	}
	
	 private static final int MSG_SEARCH = 1;
	    public static final int TIME_INPUT_REQUEST = 800;
	    private Handler mHandler = new Handler(){
	        @Override
	        public void handleMessage(Message msg) {
	            //搜索请求
	            if (mSearchInput.getText().toString().length() > 0) {
	                showLoadResult(true, false, false, false);
	                getLectureList(have_read_page, mSearchInput.getText().toString(),false);
	            }else{
	            	getLectureList(have_read_page, mSearchInput.getText().toString(),false);
	            }
	        }
	    };
	    
	private void getLectureList(int page_no,String keyword,final boolean pullRefresh) {
		A_0_App.getApi().getRecipientSearchMoreList(B_Side_Notice_Contact_Search.this,A_0_App.USER_TOKEN,type, String.valueOf(page_no),keyword,
				new InterRecipientSearchMoreList() {
					@Override
					public void onSuccess(List<Cpk_Search_Recipient> stuList) {
						if (B_Side_Notice_Contact_Search.this.isFinishing())
							return;
						share_bottom.setVisibility(View.VISIBLE);
						if (stuList != null && stuList.size() > 0) {
							have_read_page = 2;
							clearBusinessList();
							mLecturesList = stuList;
							for (int i = 0; i <temp_all_list.size(); i++) {
 								for (int j = 0; j < mLecturesList.size(); j++) {
 									if (temp_all_list.size()>0) {
 										if (temp_all_list.get(i).getUser_id().equals(mLecturesList.get(j).getUser_id())) {
 											mLecturesList.get(j).setCheck(true);
	 									}
									}
 									
 								}}
							adapter.notifyDataSetChanged();
							//check_total();
							tv_has_chooesed.setText((choosed_count+A_0_App.notice_search_Contacts.size())+"");
							showLoadResult(false, true, false, false);
							if (pullRefresh)
								PubMehods.showToastStr(B_Side_Notice_Contact_Search.this,
										"刷新成功");
						} else {
							mLecturesList.clear();
							adapter.notifyDataSetChanged();
//							PubMehods.showToastStr(B_Side_Notice_Contact_Search.this,
//									"没有查到收信人~");
							showLoadResult(false, true, false, false);
						}
						demo_swiperefreshlayout.setRefreshing(false);  
						if(null!=mPullDownView){
						    mPullDownView.onRefreshComplete();
						    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
						}
						repfresh=0;
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (B_Side_Notice_Contact_Search.this.isFinishing())
                            return;
                        //share_bottom.setVisibility(View.GONE);
                        PubMehods.showToastStr(B_Side_Notice_Contact_Search.this, msg);
                        mLecturesList.clear();
                        adapter.notifyDataSetChanged();
                        showLoadResult(false, true, false, false);
                        demo_swiperefreshlayout.setRefreshing(false);  
                        if(null!=mPullDownView){
                            mPullDownView.onRefreshComplete();
                            mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
                        }
                        repfresh=0;
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}

	// 上拉刷新初始化数据
	private void getMoreLecture(int page_no,String keyword) {
		if (A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getApi().getRecipientSearchMoreList(B_Side_Notice_Contact_Search.this,
				A_0_App.USER_TOKEN,type, String.valueOf(page_no),keyword,
				new InterRecipientSearchMoreList() {
					@Override
					public void onSuccess(List<Cpk_Search_Recipient> mList) {
						if (B_Side_Notice_Contact_Search.this.isFinishing())
							return;
						//A_0_App.getInstance().CancelProgreDialog(B_Side_Found_Search.this);
						if (mList != null && mList.size() > 0) {
							have_read_page += 1;
							int totleSize = mLecturesList.size();
							for (int i = 0; i < mList.size(); i++) {
								mLecturesList.add(mList.get(i));
							}
							for (int i = 0; i <temp_all_list.size(); i++) {
 								for (int j = 0; j < mLecturesList.size(); j++) {
 									if (temp_all_list.size()>0) {
 										if (temp_all_list.get(i).getUser_id().equals(mLecturesList.get(j).getUser_id())) {
 											mLecturesList.get(j).setCheck(true);
	 									}
									}
 									
 								}}
							adapter.notifyDataSetChanged();
							//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
						} else {
							PubMehods.showToastStr(B_Side_Notice_Contact_Search.this,
									"没有更多了");
						}
						if(null!=mPullDownView){
						    mPullDownView.onRefreshComplete();
						}
						repfresh=0;
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (B_Side_Notice_Contact_Search.this.isFinishing())
                            return;
                        A_0_App.getInstance().CancelProgreDialog(
                                B_Side_Notice_Contact_Search.this);
                        PubMehods.showToastStr(B_Side_Notice_Contact_Search.this, msg);
                        if(null!=mPullDownView){
                            mPullDownView.onRefreshComplete();
                        }
                        repfresh=0;
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

	}

	private void clearBusinessList() {
		if (mLecturesList != null && mLecturesList.size() > 0) {
			mLecturesList.clear();
		}
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
			case R.id.side_lecture_load_error:
				showLoadResult(true, false, false, false);
				have_read_page = 1;
				getLectureList(have_read_page,key, true);
				break;
			case R.id.btn_cancle:
				
				tv_has_chooesed.setText(choosed_count+"");
				temp_all_list.clear();
				for (int i = 0; i < mLecturesList.size(); i++) {
					mLecturesList.get(i).setCheck(false);
				}
				adapter.notifyDataSetChanged();
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
		A_0_App.notice_search_Contacts.addAll(temp_all_list);
		for (int i = 0; i < mLecturesList.size(); i++) {
			if (mLecturesList.get(i).isCheck()==true) {
				A_0_App.notice_search_Contacts.add(mLecturesList.get(i));
			}
		}
		
		removeDuplicate(A_0_App.notice_search_Contacts);
	}
	private void check_total(){
		int checked=0;
		for (int i = 0; i < temp_all_list.size(); i++) {
			for (int j = 0; j < mLecturesList.size(); j++) {
				try {
					if (mLecturesList.size()>0) {
						if (temp_all_list.get(i).getUser_id().equals(mLecturesList.get(j).getUser_id())&&mLecturesList.get(j).isCheck()==true) {
							temp_all_list.remove(i);
						}
					}
				} catch (Exception e) {
				}
				
				
			}
		}
		for (int i = 0; i <mLecturesList.size(); i++) {
			if (mLecturesList.get(i).isCheck()==true) {
				checked=checked+1;
			}
		}
		tv_has_chooesed.setText((choosed_count+temp_all_list.size()+checked)+"");
		}
	public class Mydapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mLecturesList != null)
				return mLecturesList.size();
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
			if (lmap.get(posi) == null) {
				
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Side_Notice_Contact_Search.this)
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
				lmap.put(posi, converView);
				converView.setTag(holder);
			}else{
				converView = lmap.get(posi);
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
			
			if (mLecturesList.get(posi).isCheck()==true) {
				holder.iv_black_lsit_check.setBackgroundResource(R.drawable.register_box_selected);
			}else if(mLecturesList.get(posi).isCheck()==false){
				holder.iv_black_lsit_check.setBackgroundResource(R.drawable.register_box_unselected);
			}
			String uri = mLecturesList.get(posi).getPhoto_url();
			if(holder.iv_photo.getTag() == null){
				PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			    holder.iv_photo.setTag(uri);
			}else{
			    if(!holder.iv_photo.getTag().equals(uri)){
			    	PubMehods.loadServicePic(imageLoader,uri,holder.iv_photo, options_photo);
			        holder.iv_photo.setTag(uri);
			    }
			}
			holder.tv_name.setText(mLecturesList.get(posi).getTrue_name());
			holder.tv_phone.setText(mLecturesList.get(posi).getPhone());
			
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
					if (mLecturesList.get(posi).isCheck()==false) {
						mLecturesList.get(posi).setCheck(true);
						for (int i = 0; i < temp_all_list.size(); i++) {
							if (temp_all_list.get(i).getUser_id().equals(mLecturesList.get(posi).getUser_id())) {
								temp_all_list.remove(i);
							}
						}
						check_total();
					}else if(mLecturesList.get(posi).isCheck()==true){
						mLecturesList.get(posi).setCheck(false);
						for (int i = 0; i < temp_all_list.size(); i++) {
							if (temp_all_list.get(i).getUser_id().equals(mLecturesList.get(posi).getUser_id())) {
								temp_all_list.remove(i);
							}
						}
						check_total();
					}
					adapter.notifyDataSetChanged();
					}
				
				
			});
			 holder.iv_photo.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(B_Side_Notice_Contact_Search.this,
								B_Mess_Persion_Info.class);
						intent.putExtra("uniqid",mLecturesList.get(posi).getUniqid());
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
	public void onResume() {
		super.onResume();
		if (!firstLoad) {
			have_read_page = 1;
			getLectureList(have_read_page,key, false);
		} else {
			firstLoad = false;
		}

	}

	@Override
	public void onDestroy() {
		if (mLecturesList != null) {
			mLecturesList.clear();
			mPullDownView = null;
		}
		drawable.stop();
		drawable=null;
		adapter = null;
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
                            A_0_App.getInstance().showExitDialog(B_Side_Notice_Contact_Search.this, AppStrStatic.kicked_offline());
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
