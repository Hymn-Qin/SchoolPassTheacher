package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Acy_list;
import com.yuanding.schoolteacher.service.Api.InterAcyList;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月12日 下午1:33:48
 * 身边中的活动列表
 */
public class B_Side_Acy_list_Scy extends A_0_CpkBaseTitle_Navi{

	private View mLinerReadDataError,mLinerNoContent,liner_acy_list_whole_view,side_acy_loading;
	private PullToRefreshListView mPullDownView;
	private MyAdapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private List<Cpk_Acy_list> mCourseList = null;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	//private BitmapUtils bitmapUtils;
	private int click_posi = 0;
	private ACache maACache;
	private JSONObject jsonObject;
	private long mSystemTime;
	
	 /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    
    private boolean havaSuccessLoadData = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_acy_list);
		
		setTitleText("活动");
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_acy_list_whole_view = findViewById(R.id.liner_acy_list_whole_view);
		mLinerReadDataError = findViewById(R.id.side_acy_load_error);
		mLinerNoContent = findViewById(R.id.side_acy_no_content);
		side_acy_loading=findViewById(R.id.side_acy_loading);
		ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_huodong);
		tv_blank_name.setText("一大波学生福利在酝酿，请稍候再来~");
		
		mLinerReadDataError.setOnClickListener(onClick);
		
		home_load_loading = (LinearLayout) side_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		imageLoader = A_0_App.getInstance().getimageLoader();
		//bitmapUtils=A_0_App.getBitmapUtils(this,R.drawable.ic_default_acy_empty,R.drawable.ic_default_acy_empty);
       
		options = A_0_App.getInstance().getOptions(R.drawable.ic_default_banner_empty_bg, R.drawable.ic_default_banner_empty_bg,
				R.drawable.ic_default_banner_empty_bg);
		mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_side_acy_list);
		mCourseList = new ArrayList<Cpk_Acy_list>();
		adapter = new MyAdapter();
        mPullDownView.setMode(Mode.BOTH);
        mPullDownView.setAdapter(adapter);
		mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getApplicationContext(),System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME| DateUtils.FORMAT_SHOW_DATE| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
						have_read_page = 1;
						startReadData(have_read_page, true);
					}

					@Override
					public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
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
				click_posi = posi-1;
				Intent intent = new Intent(B_Side_Acy_list_Scy.this,B_Side_Acy_list_Detail_Acy_Teacher.class);
				intent.putExtra("acy_type", 2);// 正常列表进入
				intent.putExtra("acy_detail_id",mCourseList.get(posi-1).getArticle_id());
				intent.putExtra("share_url_text", mCourseList.get(posi-1).getShare_url());//分享的URL
				intent.putExtra("share_url_title", mCourseList.get(posi-1).getTitle());//分享的标题
				intent.putExtra("share_url_time", mCourseList.get(posi-1).getCreate_time());//分享的时间
				intent.putExtra("share_url_pic", mCourseList.get(posi-1).getBg_img());//分享的图片
				startActivityForResult(intent, 1);
			}
		});
		readCache();
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
	private void readCache() {
		// TODO Auto-generated method stub
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_acy+A_0_App.USER_UNIQID);
		if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        }else{
            updateInfo();
        }
	}

	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			havaSuccessLoadData = true;   
			int state = jsonObject.optInt("status");
			List<Cpk_Acy_list> mlistContact = new ArrayList<Cpk_Acy_list>();
			if (state == 1) {
					mlistContact=JSON.parseArray(jsonObject.optJSONArray("alist")+"",Cpk_Acy_list.class);
			}
			if (isFinishing())
				return;
			if (mlistContact != null && mlistContact.size() > 0) {
				clearBusinessList(false);
				mCourseList = mlistContact;
				adapter.notifyDataSetChanged();
				showLoadResult(false,true, false, false);
			} else {
				showLoadResult(false,false, false, true);
				PubMehods.showToastStr(B_Side_Acy_list_Scy.this,"没有活动");
			}
			mSystemTime=jsonObject.getLong("time");
			if(null!=mPullDownView){
			    mPullDownView.onRefreshComplete();
			    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
			}
			
			demo_swiperefreshlayout.setRefreshing(false);  
			
			repfresh=0;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile,boolean noData) {
		
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
				startReadData(have_read_page,false);
				break;
			default:
				break;
			}
		}
	};
	// 读取推荐课程
	private void startReadData(final int page_no, final boolean pullRefresh) {
		A_0_App.getApi().getAcyList(B_Side_Acy_list_Scy.this,A_0_App.USER_TOKEN,String.valueOf(page_no), new InterAcyList() {
			
			@Override
			public void onSuccess(List<Cpk_Acy_list> mList,long servertime) {
				if (isFinishing())
					return;
				havaSuccessLoadData = true;   
				if (mList != null && mList.size() > 0) {
					clearBusinessList(false);
					have_read_page = 2;
					mCourseList = mList;
					adapter.notifyDataSetChanged();
					showLoadResult(false,true, false, false);
					if(pullRefresh)
						PubMehods.showToastStr(B_Side_Acy_list_Scy.this, "刷新成功");
				} else {
					showLoadResult(false,false, false, true);
					PubMehods.showToastStr(B_Side_Acy_list_Scy.this,"没有活动");
				}
				mSystemTime=servertime;
				if(null!=mPullDownView){
				    mPullDownView.onRefreshComplete();
				    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
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
                if (isFinishing())
                    return;
                if(!havaSuccessLoadData)
                {
                showLoadResult(false,false, true, false);
                }
                PubMehods.showToastStr(B_Side_Acy_list_Scy.this, msg);
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
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

	// 上拉刷新初始化数据
	private void getMoreLecture(int page_no) {
		if(A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getApi().getAcyList(B_Side_Acy_list_Scy.this,A_0_App.USER_TOKEN,String.valueOf(page_no), new InterAcyList() {
			@Override
			public void onSuccess(List<Cpk_Acy_list> mList,long servertime) {
				if (isFinishing())
					return;
				//A_0_App.getInstance().CancelProgreDialog(B_Side_Acy_list_Scy.this);
				if (mList != null && mList.size() > 0) {
					have_read_page += 1;
					int totleSize = mCourseList.size();
					for (int i = 0; i < mList.size(); i++) {
						mCourseList.add(mList.get(i));
					}
					adapter.notifyDataSetChanged();
					//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
				} else {
					PubMehods.showToastStr(B_Side_Acy_list_Scy.this,"没有更多了");
				}
				mSystemTime=servertime;
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
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Acy_list_Scy.this);
                PubMehods.showToastStr(B_Side_Acy_list_Scy.this, msg);
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
				converView = LayoutInflater.from(B_Side_Acy_list_Scy.this).inflate(R.layout.item_side_acys, null);
				holder.iv_acy_lsit_img = (ImageView) converView.findViewById(R.id.iv_acy_lsit_img);
				holder.tv_acy_name = (TextView) converView.findViewById(R.id.tv_acy_name);
				holder.tv_acy_tag = (TextView) converView.findViewById(R.id.tv_acy_tag);
				holder.tv_join_end_time = (TextView) converView.findViewById(R.id.tv_join_end_time);
				holder.tv_acy_list_join_count = (TextView) converView.findViewById(R.id.tv_acy_list_join_count);
				holder.tv_acy_list_comment_count = (TextView) converView.findViewById(R.id.tv_acy_list_comment_count);
				holder.tv_acy_title=(TextView) converView.findViewById(R.id.tv_activity_title);
				holder.tv_acy_status=(TextView) converView.findViewById(R.id.tv_activity_status);
				holder.ll_acy_list=(LinearLayout) converView.findViewById(R.id.ll_acy_list);
				converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
			String img_url = mCourseList.get(posi).getBg_img();
			if(img_url != null && img_url.length()>0 && !img_url.equals("")){
				holder.iv_acy_lsit_img.setVisibility(View.VISIBLE);
				if(holder.iv_acy_lsit_img.getTag() == null){
					PubMehods.loadServicePic(imageLoader,img_url,holder.iv_acy_lsit_img, options);
				    holder.iv_acy_lsit_img.setTag(img_url);
				}else{
				    if(!holder.iv_acy_lsit_img.getTag().equals(img_url)){
				    	PubMehods.loadServicePic(imageLoader,img_url,holder.iv_acy_lsit_img, options);
				        holder.iv_acy_lsit_img.setTag(img_url);
				    }
				}
				//bitmapUtils.display(holder.iv_acy_lsit_img, img_url);
			}else{
				holder.iv_acy_lsit_img.setVisibility(View.GONE);
			}
//			holder.tv_acy_tag.setText("[进行中]");
//			holder.tv_acy_name.setText("                 " + mCourseList.get(posi).getTitle());
			holder.tv_acy_name.setText( mCourseList.get(posi).getTitle());
			holder.tv_join_end_time.setText(PubMehods.getTimeDiffeng(mCourseList.get(posi).getJoin_end_time()));
			holder.tv_acy_list_join_count.setText(mCourseList.get(posi).getJoin_num());
			holder.tv_acy_list_comment_count.setText(mCourseList.get(posi).getComment_num());
			long start_time=mCourseList.get(posi).getStart_time();
			long end_time=mCourseList.get(posi).getEnd_time();
			long server_time=mSystemTime;
			long time_to_start=start_time-server_time;
			long time_to_end=end_time-server_time;
			if(server_time<start_time)
			{
				
				//未开始
				holder.tv_acy_status.setText("活动未开始");
				holder.tv_acy_status.setTextColor(getResources().getColor(R.color.repair_blue));
				holder.ll_acy_list.setVisibility(View.VISIBLE);
				holder.tv_acy_title.setText("距离开始还有");
				
					String temp1=PubMehods.getFormatDate(start_time, "yyyy/MM/dd");
					String temp2=PubMehods.getFormatDate(server_time, "yyyy/MM/dd");
				if (temp1.equals(temp2)) {
					 holder.tv_join_end_time.setText("0");
				}else{
					 holder.tv_join_end_time.setText(PubMehods.get_ac_RemainDate(time_to_start));
				}
			   
            } else if (server_time >= start_time && server_time < end_time)
			{
				//进行中
				holder.tv_acy_status.setText("活动进行中");
				holder.tv_acy_status.setTextColor(getResources().getColor(R.color.GREENlIGHT));
				holder.tv_acy_title.setText("距离结束还有");
				holder.ll_acy_list.setVisibility(View.VISIBLE);
				String temp1=PubMehods.getFormatDate(start_time, "yyyy/MM/dd");
				String temp2=PubMehods.getFormatDate(server_time, "yyyy/MM/dd");
			if (temp1.equals(temp2)) {
				 holder.tv_join_end_time.setText("0");
			}else{
				 holder.tv_join_end_time.setText(PubMehods.get_ac_RemainDate(time_to_end));
			}
			}else
			{
				//已结束
				holder.tv_acy_status.setText("活动已结束");
				holder.tv_acy_status.setTextColor(getResources().getColor(R.color.repair_grey));
				holder.ll_acy_list.setVisibility(View.GONE);
			}
		  if(A_0_App.isShowAnimation==true){
			if(posi>A_0_App.acy_list_curPosi)
			{
				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(50*posi);
				A_0_App.acy_list_curPosi=posi;
			    converView.startAnimation(an);
			}
		    }
			return converView;
		}
	}

	class ViewHolder {
		ImageView iv_acy_lsit_img;
		TextView tv_acy_name;
		TextView tv_acy_tag;
		TextView tv_join_end_time;
		TextView tv_acy_list_join_count;
		TextView tv_acy_list_comment_count;
		TextView tv_acy_title,tv_acy_status;
		LinearLayout ll_acy_list;
	}

	private void clearBusinessList(boolean setNull) {
		if (mCourseList != null && mCourseList.size() > 0) {
			mCourseList.clear();
			if (setNull)
				mCourseList = null;
		}
	}			
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
//			String comment_count = data.getExtras().getString("comment_count");
//			String join_count = data.getExtras().getString("join_count");
//			if (!"".equals(comment_count)) {
//				if (requestCode == 1) {
//					mCourseList.get(click_posi).setComment_num(comment_count);
//					mCourseList.get(click_posi).setJoin_num(join_count);
//					adapter.notifyDataSetChanged();
//				}
//			}
		}
	}
	
	private void updateInfo(){
		MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
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
	protected void onResume() {
		
		super.onResume();
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Acy_list_Scy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Acy_list_Scy.this, AppStrStatic.kicked_offline());
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
	protected void onDestroy() {
		clearBusinessList(true);
		adapter= null;
		drawable.stop();
		drawable=null;
		//bitmapUtils=null;
		super.onDestroy();
	}
	
	
	@Override
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;

		default:
			break;
		}
		
	}

}
