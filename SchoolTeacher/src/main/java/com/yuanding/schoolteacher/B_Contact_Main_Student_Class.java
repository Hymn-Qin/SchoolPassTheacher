package com.yuanding.schoolteacher;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanding.schoolteacher.bean.Cpk_Student_Class_Contact;
import com.yuanding.schoolteacher.service.Api.InterStuClassList;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年1月5日 下午4:27:41 
 * 我的学生选项卡
 */
public class B_Contact_Main_Student_Class extends BackHandledFragment {

	private View mLinerReadDataError, mLinerNoContent,lv_stu_contact_loading,
			liner_acy_list_whole_view, viewone;
	private PullToRefreshListView mPullDownView;
	private Mydapter adapter;
	private  List<Cpk_Student_Class_Contact> mClasslist = null;

	public FinishReceiver finishReceiver;
	/**
	 * 0,正，1，返回
	 */
	private String is_back;
	private String str_result_level = "";
	
	/**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewone = inflater.inflate(R.layout.activity_contact_student_class,
				container, false);
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)viewone.findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_acy_list_whole_view = viewone
				.findViewById(R.id.liner_stu_contact_wholeview);
		mLinerReadDataError = viewone
				.findViewById(R.id.lv_stu_contact_load_error);
		mLinerNoContent = viewone.findViewById(R.id.lv_stu_contact_no_content);
		lv_stu_contact_loading=viewone.findViewById(R.id.lv_stu_contact_loading);
		
		home_load_loading = (LinearLayout) lv_stu_contact_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		ImageView iv_blank_por = (ImageView) mLinerNoContent
				.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent
				.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
		tv_blank_name.setText("暂无联系人~");
		tv_blank_name.setText("暂无信息~");
		mPullDownView = (PullToRefreshListView) viewone
				.findViewById(R.id.lv_out_school_list_click);
		mClasslist = new ArrayList<Cpk_Student_Class_Contact>();
		adapter = new Mydapter();
		mPullDownView.setAdapter(adapter);
		finishReceiver = new FinishReceiver();
		IntentFilter intentFilter = new IntentFilter("stu");
		getActivity().registerReceiver(finishReceiver, intentFilter);
		mPullDownView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				if (position == 0) {
					return;
				}
				int posi = position - 1 ;
				is_back="0";
                if (Integer.parseInt(mClasslist.get(posi).getChild_total_num()) > 0) {
                    getData(mClasslist.get(posi).getOrgan_id());
                } else {
                    Intent intent = new Intent(getActivity(),B_Contact_Main_Student_List.class);
                    intent.putExtra("contact_stu_id", mClasslist.get(posi).getOrgan_id());
                    intent.putExtra("contact_stu_name", mClasslist.get(posi).getOrgan_name());
                    startActivity(intent);
                }
			}
		});
		
//	      mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//              @Override
//              public void onPullDownToRefresh(
//                      PullToRefreshBase<ListView> refreshView) {
//                  String label = DateUtils.formatDateTime(getActivity(),
//                          System.currentTimeMillis(),
//                          DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
//                  refreshView.getLoadingLayoutProxy()
//                          .setLastUpdatedLabel(label);
//                  is_back="0";
//                  getData("0");
//              }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//            	if (repfresh==0) {
//					repfresh=1;
//					demo_swiperefreshlayout.setEnabled(false);
//					demo_swiperefreshlayout.setRefreshing(false);  
//				}
//            }
//          });
//	      
	     
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
				demo_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
							public void onRefresh() {
								mPullDownView.setMode(Mode.DISABLED);
								 getData("0");

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

		mLinerReadDataError.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				is_back="0";
				showLoadResult(true,false, false, false);
				clearData(false);
				getData("0");
			}
		});

		mLinerNoContent.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View arg0) {
	                is_back="0";
	                showLoadResult(true,false, false, false);
	                clearData(false);
	                getData("0");
	            }
	        });
	      
		if (A_0_App.USER_STATUS.equals("5")||A_0_App.USER_STATUS.equals("0") ) {
			showLoadResult(false,false, false, true);
		} else {
			is_back="0";
	        getData("0");
		}
		
		
		return viewone;

	}

	/**************************************** 通讯录 ****************************************/
	/**
	 * 初始化数据
	 */
	private void getData(String organ_id) {
		A_0_App.getApi().getStuClassList("1",organ_id, A_0_App.USER_TOKEN,is_back,new InterStuClassList() {
					@Override
					public void onSuccess(List<Cpk_Student_Class_Contact> mList,String message,String result_level) {
					    if (mClasslist == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
					    str_result_level = result_level;
					    A_Main_My_Contact_Acy.logD(result_level +"=result_level");
					    clearData(false);
						if (mList != null && mList.size() > 0) {
							if(mClasslist!=null&&mClasslist.size()>0)
							{
								mClasslist.clear();
							}
							
							mClasslist.addAll(mList);
							if(adapter!=null){
							adapter.notifyDataSetChanged();
							}
							showLoadResult(false,true, false, false);
						} else {
							if(adapter!=null){
					        adapter.notifyDataSetChanged();
							}
					        showLoadResult(false,false, false, true);
						}
						if(null!=mPullDownView){
						    mPullDownView.onRefreshComplete();
						    mPullDownView.setMode(Mode.DISABLED);
						}
						
						demo_swiperefreshlayout.setRefreshing(false);  
						repfresh=0;
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (mClasslist == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        showLoadResult(false,false, true, false);
                        
                         A_0_App.hadIntercept = true;
                         if(null!=mPullDownView){
                                mPullDownView.onRefreshComplete();
                                mPullDownView.setMode(Mode.DISABLED);
                            }
                         demo_swiperefreshlayout.setRefreshing(false);  
                        repfresh=0;
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}

	public class FinishReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
		    A_Main_My_Contact_Acy.logD(str_result_level +"=str_result_level");
            if (mClasslist == null)
                return;
            if(getActivity() == null || getActivity().isFinishing())
                return;
            if (arg1.getStringExtra("name").equals("stu")) {
            	if (!str_result_level.equals("0")) {
            		if (str_result_level.equals("1")) {
           			 is_back = "0";
                        A_0_App.hadIntercept = true;
                        getData("0");
					}else{
                   is_back = "1";
                   A_0_App.hadIntercept = false;
                   if (mClasslist.size()>0) {
                	   getData(mClasslist.get(0).getParent_id());
				    }
					}
               }else{
				A_0_App.hadIntercept=true;
			}
			} else if(arg1.getStringExtra("name").equals("1")){
				if (!str_result_level.equals("0")) {
                    A_0_App.hadIntercept = false;
                } else {
                    A_0_App.hadIntercept = true;
                }
			}
		    
		}}
	private void showLoadResult(boolean loading,boolean wholeView, boolean loadFaile,
			boolean noData) {
		if (loading) {
		    if (drawable!=null) {
                drawable.start();
            }
			lv_stu_contact_loading.setVisibility(View.VISIBLE);
		} else {
			if (drawable != null) {
				drawable.stop();
			}
			lv_stu_contact_loading.setVisibility(View.GONE);
		}
			
		if (wholeView)
			liner_acy_list_whole_view.setVisibility(View.VISIBLE);
		else
			liner_acy_list_whole_view.setVisibility(View.GONE);

		if (loadFaile)
			mLinerReadDataError.setVisibility(View.VISIBLE);
		else
			mLinerReadDataError.setVisibility(View.GONE);

		if (noData)
			mLinerNoContent.setVisibility(View.VISIBLE);
		else
			mLinerNoContent.setVisibility(View.GONE);
	}

	public class Mydapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mClasslist != null)
				return mClasslist.size();
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
				converView = LayoutInflater.from(
						getActivity()).inflate(R.layout.item_contace_student_class, null);
			}
			TextView name = (TextView) converView.findViewById(R.id.tv_student_class_name);
			name.setText(mClasslist.get(posi).getOrgan_name());
			if(A_0_App.isShowAnimation==true){
			  if(posi>A_0_App.student_class_curPosi)
					
				{
					A_0_App.student_class_curPosi=posi;
					Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
					an.setDuration(400);
					an.setStartOffset(50*posi);
				    converView.startAnimation(an);
				}
			}
			return converView;
		}

	}
	
	private void clearData(boolean setNull) {
		if (mClasslist != null) {
			mClasslist.clear();
			if (setNull)
				mClasslist = null;
		}
	}
	
	@Override
	public void onDestroyView() {
		clearData(true);
		drawable.stop();
		drawable=null;
        try {
            getActivity().unregisterReceiver(finishReceiver);
        } catch (Exception e) {

        }
		super.onDestroyView();
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	
}
