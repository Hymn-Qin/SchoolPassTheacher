package com.yuanding.schoolteacher;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
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
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolteacher.bean.Cpk_Side_Notice_List;
import com.yuanding.schoolteacher.service.Api.InterSideNoticeList;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * 
 * @author 通知---> type 0 未发送，1 已发送，2存草稿
 * 待发送页面
 */
public class B_Side_Notice_Main_UnSent extends Fragment {
	private boolean hadIntercept;
	protected Context mContext;
	private View mLinerReadDataError, mLinerNoContent,
			liner_acy_list_whole_view,side_acy_loading;
	private PullToRefreshListView mPullDownView;
	private MyAdapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private List<Cpk_Side_Notice_List> mCourseList = null;
//	protected ImageLoader imageLoader;
//	private DisplayImageOptions options;
	protected ImageOptions bitmapUtils;
	private int click_posi = 0;
	private View viewone;
	private String type = "0";
	private ACache maACache;
	private JSONObject jsonObject;

	public B_Side_Notice_Main_UnSent() {
		super();
	}

	 /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity.getApplicationContext();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewone = inflater.inflate(R.layout.notice_sent_main, container, false);
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)viewone.findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_acy_list_whole_view = viewone
				.findViewById(R.id.liner_acy_list_whole_view);
		mLinerReadDataError = viewone.findViewById(R.id.side_acy_load_error);
		mLinerNoContent = viewone.findViewById(R.id.side_acy_no_content);
		side_acy_loading=viewone.findViewById(R.id.side_acy_loading);
		ImageView iv_blank_por = (ImageView) mLinerNoContent
				.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent
				.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_send);
		tv_blank_name.setText("没有通知记录~");

		mLinerReadDataError.setOnClickListener(onClick);


		home_load_loading = (LinearLayout) side_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		/*imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(
				R.drawable.ic_default_acy_empty,
				R.drawable.ic_default_acy_empty,
				R.drawable.ic_default_acy_empty);*/
		bitmapUtils=A_0_App.getBitmapUtils(getActivity(),R.drawable.ic_default_empty_bg,R.drawable.ic_default_empty_bg,false);
		mPullDownView = (PullToRefreshListView) viewone
				.findViewById(R.id.lv_side_acy_list);
		mCourseList = new ArrayList<Cpk_Side_Notice_List>();
		adapter = new MyAdapter();
		mPullDownView.setMode(Mode.BOTH);
		mPullDownView.setAdapter(adapter);
		mPullDownView
				.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
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
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
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
			demo_swiperefreshlayout
					.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
      
		// 点击Item触发的事件
		mPullDownView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,
					long arg3) {
				if (posi == 0) {
					return;
				}
				click_posi = posi - 1;
				Intent intent = new Intent(getActivity(),
						B_Side_Notice_Main_Detail_0.class);
				intent.putExtra("acy_type", 1);// 正常列表进入
				intent.putExtra("notice_type",AppStrStatic.cache_key_notice_unsent+A_0_App.USER_UNIQID);
				intent.putExtra("acy_detail_id", mCourseList.get(posi - 1)
						.getMessage_id());
				intent.putExtra("type", mCourseList.get(posi - 1)
						.getType());
				startActivityForResult(intent, 1);
			}
		});
		if (A_0_App.USER_STATUS.equals("2")) {
			readCache();
		} else {
			showLoadResult(false,false, false, true);
		}
		   //updateInfo();
		return viewone;
	}

	private void readCache() {
		// TODO Auto-generated method stub
		maACache = ACache.get(getActivity());
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_notice_unsent+A_0_App.USER_UNIQID);
        if (jsonObject!= null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			
        	showInfo(jsonObject);
			}else{
		    updateInfo();}
	}

	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		List<Cpk_Side_Notice_List> mList = getList(jsonObject);
		if (mCourseList == null)
			return;
		havaSuccessLoadData = true;   
		if (mList != null && mList.size() > 0) {
			clearBusinessList(false);
			mCourseList = mList;
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

	private List<Cpk_Side_Notice_List> getList(JSONObject jsonObject2) {
		// TODO Auto-generated method stub
		
			int state = jsonObject.optInt("status");
			List<Cpk_Side_Notice_List> mlistContact = new ArrayList<Cpk_Side_Notice_List>();
			if (state == 1) {
				JSONArray jsonArrayItem = jsonObject.optJSONArray("mlist");
				mlistContact=JSON.parseArray(jsonArrayItem+"", Cpk_Side_Notice_List.class);
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
	private void startReadData(int page_no, final boolean pullRefresh
		) {
		A_0_App.getApi().getSideNoticeList(type, getActivity(),
				A_0_App.USER_TOKEN, String.valueOf(page_no),new InterSideNoticeList() {

					@Override
					public void onSuccess(List<Cpk_Side_Notice_List> mList) {
                        if (mCourseList == null)
                            return;
                        havaSuccessLoadData = true;   
                        if(getActivity() == null || getActivity().isFinishing())
                            return;
						if (mList != null && mList.size() > 0) {
							clearBusinessList(false);
							have_read_page = 2;
							mCourseList = mList;
							if (adapter!=null) {
								adapter.notifyDataSetChanged();
								
							}
							showLoadResult(false,true, false, false);
							if (pullRefresh)
								PubMehods.showToastStr(getActivity(), "刷新成功");
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
                        if(!havaSuccessLoadData){
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
		A_0_App.getApi().getSideNoticeList(type, getActivity(),
				A_0_App.USER_TOKEN, String.valueOf(page_no),
				new InterSideNoticeList() {
					@Override
					public void onSuccess(List<Cpk_Side_Notice_List> mList) {
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

	// 加载列表数据
	public class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mCourseList != null)
				return mCourseList.size();
			else
				return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (converView == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(getActivity()).inflate(
						R.layout.item_side_notice_sent, arg2, false);
				holder.iv_acy_lsit_img = (ImageView) converView
						.findViewById(R.id.iv_acy_lsit_img);
				holder.itme_side_liner = (LinearLayout) converView
						.findViewById(R.id.itme_side_liner);
				holder.tv_join_end_time_txt = (TextView) converView
						.findViewById(R.id.tv_join_end_time_txt);
				holder.tv_acy_sms = (ImageView) converView
						.findViewById(R.id.tv_acy_sms);
				holder.tv_acy_name = (TextView) converView
						.findViewById(R.id.tv_acy_name);
				holder.tv_join_end_time = (TextView) converView
						.findViewById(R.id.tv_join_end_time);
				holder.tv_acy_list_join_count = (TextView) converView
						.findViewById(R.id.tv_acy_list_join_count);
				holder.tv_acy_list_comment_count = (TextView) converView
						.findViewById(R.id.tv_acy_list_comment_count);
				holder.tv_acy_list_reply_count = (TextView) converView
						.findViewById(R.id.tv_acy_list_reply_count);
				holder.tv_unsent_sign=(TextView) converView.findViewById(R.id.tv_sent_sign);
				holder.tv_unsent_content=(TextView) converView.findViewById(R.id.tv_sent_content);
				holder.tv_join_time_txt=(TextView) converView.findViewById(R.id.tv_join_time_txt);
				holder.view_bottom=converView.findViewById(R.id.view_bottom);
				converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
			String img_url = mCourseList.get(posi).getBg_img();
			if (img_url != null && img_url.length() > 0 && !img_url.equals("")) {
				holder.iv_acy_lsit_img.setVisibility(View.VISIBLE);
				/*imageLoader.displayImage(img_url, holder.iv_acy_lsit_img,
						options);*/
				PubMehods.loadBitmapUtilsPic(bitmapUtils,holder.iv_acy_lsit_img, mCourseList.get(posi).getBg_img());
				holder.tv_unsent_content.setMinLines(3);
			} else {
				holder.iv_acy_lsit_img.setVisibility(View.GONE);
				holder.tv_unsent_content.setMinLines(2);
			}
			holder.tv_join_end_time_txt.setVisibility(View.VISIBLE);
			holder.tv_join_time_txt.setVisibility(View.VISIBLE);
			
			if (mCourseList.get(posi).getFixed_time().length()>0&&!mCourseList.get(posi).getFixed_time().equals("")) {
				holder.tv_join_end_time.setText(PubMehods.getStrTime(mCourseList.get(posi).getFixed_time()));
			} else {
				holder.tv_join_end_time.setText("");
			}
			
			holder.tv_acy_list_join_count.setText(mCourseList.get(posi)
					.getRead_num());
			holder.tv_acy_list_comment_count.setText(mCourseList.get(posi)
					.getUnread_num());
			holder.tv_acy_list_reply_count.setText(mCourseList.get(posi)
					.getReply_num());
			holder.tv_unsent_sign.setText(mCourseList.get(posi).getApp_msg_sign());
			if (mCourseList.get(posi).getType().equals("1")) {

				switch (mCourseList.get(posi).getIs_appendix()){
					case 0:
						holder.tv_acy_name.setText(mCourseList.get(posi).getTitle());
						holder.tv_acy_name.setCompoundDrawables(null, null, null, null);
						break;
					case 1:
						holder.tv_acy_name.setText(mCourseList.get(posi).getTitle());

						Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.file_zhizhen);

						drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

						holder.tv_acy_name.setCompoundDrawables(null, null, drawable, null);

						holder.tv_acy_name.setCompoundDrawablePadding(25);
						break;
				}
				holder.itme_side_liner.setVisibility(View.GONE);
				//holder.tv_acy_sms.setVisibility(View.GONE);
				holder.tv_acy_name.setText(mCourseList.get(posi).getTitle());
				holder.tv_unsent_content.setText(mCourseList.get(posi).getDesc());
			} else {
				holder.tv_acy_name.setText("手机短信");
				holder.tv_acy_name.setCompoundDrawables(null, null, null, null);
				holder.itme_side_liner.setVisibility(View.GONE);
				//holder.tv_acy_sms.setVisibility(View.VISIBLE);
				holder.tv_unsent_content.setText(mCourseList.get(posi).getDesc());

			}
			if(A_0_App.isShowAnimation==true){
			 if(posi>A_0_App.unsent_curPosi)
			 {
				A_0_App.unsent_curPosi=posi;
				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(10*posi);
			   converView.startAnimation(an);
			 }
			}
			if(posi==mCourseList.size()-1)
			{
				holder.view_bottom.setVisibility(View.VISIBLE);
			}else
			{
				holder.view_bottom.setVisibility(View.GONE);
			}
			return converView;
		}
	}

	class ViewHolder {
		ImageView iv_acy_lsit_img;
		TextView tv_acy_name;
		TextView tv_join_end_time;
		TextView tv_acy_list_join_count;
		TextView tv_acy_list_comment_count;
		TextView tv_acy_list_reply_count;
		LinearLayout itme_side_liner;
		ImageView tv_acy_sms;
		TextView tv_join_end_time_txt;
		TextView tv_unsent_sign;
		TextView tv_unsent_content;
		TextView tv_join_time_txt;
		View view_bottom;
	}

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
		
		if (A_0_App.SIDE_NOTICE==1) {
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
	    bitmapUtils=null;
	    super.onDestroy();
	}
	
	/*
	 * @Override public void onActivityResult(int requestCode, int resultCode,
	 * Intent data) { if (data != null) { String comment_count =
	 * data.getExtras().getString("comment_count"); String join_count =
	 * data.getExtras().getString("join_count"); if (!"".equals(comment_count))
	 * { if (requestCode == 1) {
	 * mCourseList.get(click_posi).setComment_num(comment_count);
	 * mCourseList.get(click_posi).setJoin_num(join_count);
	 * adapter.notifyDataSetChanged(); } } }
	 * 
	 * }
	 */
	/*protected boolean onBackPressed() {
		if(hadIntercept){
			return false;
		}else{
			Toast.makeText(getActivity(), "Click From MyFragment", Toast.LENGTH_SHORT).show();
			hadIntercept = true;
			return true;
		}
	}*/

}
