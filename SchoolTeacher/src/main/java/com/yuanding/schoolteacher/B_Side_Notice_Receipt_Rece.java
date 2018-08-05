package com.yuanding.schoolteacher;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolteacher.bean.Cpk_NoticeReceIpt;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.service.Api.ReceIptDetailCallBack;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.CircleImageView;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshGridView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;
/**
 * 已回执
 * @author Administrator
 *
 */
public class B_Side_Notice_Receipt_Rece extends Fragment{

	protected Context mContext;
	private View viewone;
	
	private View mLinerReadDataError, mLinerNoContent,side_acy_loading,
	liner_acy_list_whole_view;
    private PullToRefreshGridView mPullDownView;
    private MyGridView_NoticeReceiverResult_Aapter adapter;
    private int have_read_page = 1;// 已经读的页数
    private List<Cpk_NoticeReceIpt> mCourseList = null;
   // protected BitmapUtils bitmapUtils;
    private int click_posi = 0;
    private String type = "1";   // 0 未回执  1 已回执
    private JSONObject jsonObject;
    private ACache maACache;
    
    String messageid; //消息id
	
    /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	
	public static long severTime = 0; // 服务器时间
	private LinearLayout home_load_loading;
	private AnimationDrawable drawable;
	private boolean havaSuccessLoadData = false;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity.getApplicationContext();
	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		viewone = inflater.inflate(R.layout.activity_side_notice_receipt, container, false);
		messageid = getActivity().getIntent().getStringExtra("messageId");

		initView(viewone);
		return viewone;
	}
	
	@SuppressWarnings("unchecked")
	private void initView(View rootview) {
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)viewone.findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_acy_list_whole_view = viewone.findViewById(R.id.liner_acy_list_whole_view);
		mLinerReadDataError = viewone.findViewById(R.id.side_acy_load_error);
		mLinerNoContent = viewone.findViewById(R.id.side_acy_no_content);
		side_acy_loading=viewone.findViewById(R.id.side_acy_loading);
		ImageView iv_blank_por = (ImageView) mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_send);
		iv_blank_por.setVisibility(View.GONE);
		tv_blank_name.setText("全部未回执");
		
		home_load_loading = (LinearLayout) side_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		mLinerReadDataError.setOnClickListener(onClick);

		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(
				R.drawable.icon_friend_image,
				R.drawable.icon_friend_image6,
				R.drawable.icon_friend_image4);
		//bitmapUtils=A_0_App.getBitmapUtils(getActivity(),R.drawable.ic_default_empty_bg,R.drawable.ic_default_empty_bg);
		mPullDownView = (PullToRefreshGridView) viewone.findViewById(R.id.mgv_noticereceiver);
		mCourseList = new ArrayList<Cpk_NoticeReceIpt>();
		adapter = new MyGridView_NoticeReceiverResult_Aapter();
		mPullDownView.setMode(Mode.BOTH);
		mPullDownView.setAdapter(adapter);
		
		
		mPullDownView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				String label = DateUtils.formatDateTime(getActivity(),
						System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel(label);
				have_read_page = 1;
				startReadData(have_read_page, true);
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				
				if (repfresh==0) {
					repfresh=1;
					demo_swiperefreshlayout.setEnabled(false);
					demo_swiperefreshlayout.setRefreshing(false);  
					getMoreLecture(have_read_page);
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
			demo_swiperefreshlayout .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {

							have_read_page = 1;
							if(null!=mPullDownView){
							    mPullDownView.setMode(Mode.DISABLED);
							}
							startReadData(have_read_page, true);

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
      
		if (A_0_App.USER_STATUS.equals("2")) {
			readCache();
		} else {
			showLoadResult(false,false, false, true);
		}
		
		
		

	}
	
	private void readCache() {
		// TODO Auto-generated method stub
		maACache = ACache.get(getActivity());
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_receipt_detail + messageid + type + A_0_App.USER_UNIQID);
		
		if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
		}else{
		    updateInfo();
		}
	}

	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
//		severTime=Long.parseLong(jsonObject.optString("time"))*1000;
		havaSuccessLoadData = true;
		List<Cpk_NoticeReceIpt> mlist = getList(jsonObject);
		if (mCourseList == null){
			mCourseList = new ArrayList<Cpk_NoticeReceIpt>();
		}
			
		if (mlist != null && mlist.size() > 0) {
			clearBusinessList(false);
			mCourseList = mlist;
			if (adapter!=null) {
				adapter.notifyDataSetChanged();
			}
			showLoadResult(false,true, false, false);
		} else {
			showLoadResult(false,false, false, true);
		}
		demo_swiperefreshlayout.setRefreshing(false);  
		if(null!=mPullDownView){
		    mPullDownView.onRefreshComplete();
		    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
		}
		repfresh=0;
		
		
	}

	private List<Cpk_NoticeReceIpt> getList(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
			int state = jsonObject.optInt("status");
			List<Cpk_NoticeReceIpt> mlistContact = new ArrayList<Cpk_NoticeReceIpt>();
			
			if (state == 1) {
				JSONArray jsonArrayItem = jsonObject.optJSONArray("list");
				mlistContact=JSON.parseArray(jsonArrayItem+"", Cpk_NoticeReceIpt.class);
				
				int countNum = jsonObject.optInt("count_num");
				B_Side_Notice_Receipt_Main.getmInstace().updateChildTitle(1, "已回执（"+countNum+"）");
			}
			return mlistContact;
	}

	private void showLoadResult(boolean loading,boolean wholeView, boolean loadFaile,
			boolean noData) {

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
		
		if (loading) {
			drawable.start();
			side_acy_loading.setVisibility(View.VISIBLE);
		} else {
			if (drawable != null) {
				drawable.stop();
			}
			side_acy_loading.setVisibility(View.GONE);
		}
			
	}

	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_acy_load_error:
				showLoadResult(true,false, false, false);
				have_read_page = 1;
				startReadData(have_read_page, false);
				break;
			default:
				break;
			}
		}
	};

	
	
	// 读取推荐课程
	private void startReadData(int page_no, final boolean pullRefresh) {
		
		A_0_App.getApi().getReceIptList(type, getActivity(),A_0_App.USER_TOKEN, messageid,String.valueOf(page_no),
				new ReceIptDetailCallBack() {

					@Override
					public void onSuccess(List<Cpk_NoticeReceIpt> mList,int countNum) {
					    if (mCourseList == null)
                            return;
					    
					    havaSuccessLoadData = true;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
						if (mList != null && mList.size() > 0) {
//							severTime=Long.parseLong(servertime)*1000;
							
							clearBusinessList(false);
							have_read_page = 2;
							mCourseList = mList;
							if (adapter!=null) {
								adapter.notifyDataSetChanged();
							}
							B_Side_Notice_Receipt_Main.getmInstace().updateChildTitle(1, "已回执（"+countNum+"）");
							
							showLoadResult(false,true, false, false);
							if (pullRefresh)
								PubMehods.showToastStr(getActivity(), "刷新成功");
						} else {
							showLoadResult(false,false, false, true);
							B_Side_Notice_Receipt_Main.getmInstace().updateChildTitle(1, "已回执（"+countNum+"）");
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
                        if (mCourseList == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        /*if (showDialog)
                            A_0_App.getInstance().CancelProgreDialog(
                                    getActivity());*/
                        if(!havaSuccessLoadData)
                        {
                        showLoadResult(false,false, true, false);
                        }
                        PubMehods.showToastStr(getActivity(), msg);

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
	private void getMoreLecture(int page_no) {
		
		if (A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		
		A_0_App.getApi().getReceIptList(type, getActivity(),A_0_App.USER_TOKEN, messageid,String.valueOf(page_no),
				new ReceIptDetailCallBack() {
					@Override
					public void onSuccess(List<Cpk_NoticeReceIpt> mList,int countNum) {
					    if (mCourseList == null)
							return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
						//A_0_App.getInstance().CancelProgreDialog(getActivity());
						if (mList != null && mList.size() > 0) {
							have_read_page += 1;
							int totleSize = mCourseList.size();
							for (int i = 0; i < mList.size(); i++) {
								mCourseList.add(mList.get(i));
							}
							if (adapter!=null) {
								
								adapter.notifyDataSetChanged();
								//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
							}
						} else {
							PubMehods.showToastStr(getActivity(), "没有更多了");
						}
						if (mPullDownView!=null) {
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
                        if (mCourseList == null)
                            return;
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
                        A_0_App.getInstance().CancelProgreDialog(getActivity());
                        PubMehods.showToastStr(getActivity(), msg);
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
	
	
	/**
	 * 已读 或 未读 头像列表
	 * @author Administrator
	 *
	 */
	public class MyGridView_NoticeReceiverResult_Aapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mCourseList != null)
				return mCourseList.size();
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

		@SuppressLint("NewApi")
		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (converView == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(mContext).inflate(R.layout.item_notice_receiverresult_userhead, null);
				holder.ivHead = (CircleImageView) converView.findViewById(R.id.iv_contact_por);
				holder.tv_username = (TextView) converView.findViewById(R.id.tv_username);
				holder.tv_recestatus = (TextView) converView.findViewById(R.id.tv_recestatus);
				converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
			
			
			if(null!=mCourseList && mCourseList.size()>0 && null!=holder){
				//if (list_lack.get(posi).getPhoto_url()!=null) {
				PubMehods.loadServicePic(imageLoader,mCourseList.get(posi).getPhoto_url(), holder.ivHead,options);
				holder.tv_username.setText(mCourseList.get(posi).getName());
		
				converView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(mContext, B_Mess_Persion_Info.class);
						intent.putExtra("uniqid", mCourseList.get(posi).getUniqid());
						startActivity(intent);						
					}
				});
			}
			
			
			
			
			return converView;
		}
		
		class ViewHolder {
			CircleImageView ivHead;
			TextView tv_username;
			TextView tv_recestatus;
		}

	}
	
	
	
	
	
	
	/**
	 * textView部分文本加粗span
	 * @author Administrator
	 *
	 */
//	public class MyStyleSpan extends StyleSpan {
//		public MyStyleSpan(int style) {
//			super(style);
//		}
//
//		@Override
//		public int describeContents() { 
//
//			return super.describeContents();
//		}
//
//		@Override
//		public int getSpanTypeId() {
//			return super.getSpanTypeId();
//		}
//
//		@Override
//		public int getStyle() {
//			return super.getStyle();
//		}
//
//		@Override
//		public void updateDrawState(TextPaint ds) {
//			ds.setFakeBoldText(true);
//			super.updateDrawState(ds);
//		}
//
//		@Override
//		public void updateMeasureState(TextPaint paint) {
//			paint.setFakeBoldText(true);
//			super.updateMeasureState(paint);
//		}
//
//		@Override
//		public void writeToParcel(Parcel dest, int flags) { 															
//			super.writeToParcel(dest, flags);
//		}
//	}
	

	private void clearBusinessList(boolean setNull) {
		if (mCourseList != null) {
			mCourseList.clear();
			if (setNull)
				mCourseList = null;
		}
	}
	private void updateInfo(){
		MyAsyncTask updateLectureInfo = new MyAsyncTask(getActivity());
		updateLectureInfo.execute();
    }

    class MyAsyncTask extends AsyncTask<Void,Integer,Integer>{
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
        	
        	startReadData(have_read_page, false);
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
//            logD("上传融云数据完毕");
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        	
        }
    }


    @Override
    public void onResume() {
    	super.onResume();
    	if (A_0_App.SIDE_NOTICE==0) {
    		clearBusinessList(false);
    		have_read_page = 1;
    		startReadData(have_read_page, false);
    	} 
    	
    }
    
    @Override
    public void onDestroy() {
        clearBusinessList(true);
        drawable.stop();
		drawable=null;
		
        adapter = null;
        //bitmapUtils=null;
        super.onDestroy();
    }
	
}
