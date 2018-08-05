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
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Side_Wage;
import com.yuanding.schoolteacher.bean.Cpk_Side_Wages_list;
import com.yuanding.schoolteacher.service.Api.InterWagesList;
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
 * @version 创建时间：2015年11月12日 下午3:18:18
 * 工资————工资————工资
 */
public class B_Side_Wages_Acy extends A_0_CpkBaseTitle_Navi{
	
	
	private View mLinerReadDataError,mLinerNoContent,liner_lecture_list_whole_view,side_lecture__loading;
	private PullToRefreshListView mPullDownView;
	private List<Cpk_Side_Wages_list> mLecturesList;
	private Mydapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private Boolean firstLoad = false;
	private Mydapter_Itme adapter_itme;
	private ACache maACache;
	private JSONObject jsonObject;
	
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
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setView(R.layout.activity_side_repair);
		setTitleText("工资");
		firstLoad = true;
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_lecture_list_whole_view = findViewById(R.id.liner_lecture_list_whole_view);
		mPullDownView = (PullToRefreshListView)findViewById(R.id.lv_side_lecture_list);
		side_lecture__loading=findViewById(R.id.side_lecture__loading);
		mLinerReadDataError = findViewById(R.id.side_lecture_load_error);
		mLinerNoContent = findViewById(R.id.side_lecture_no_content);
		ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_wage);
		tv_blank_name.setText("你的工资单尚未发布，再等一等~");
		mLinerReadDataError.setOnClickListener(onClick);
		

		home_load_loading = (LinearLayout) side_lecture__loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		mLecturesList = new ArrayList<Cpk_Side_Wages_list>();
		adapter = new Mydapter();
       // mPullDownView.setMode(Mode.BOTH);
        mPullDownView.setAdapter(adapter);

        mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新
                String label = DateUtils.formatDateTime(getApplicationContext(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				have_read_page = 1;
				getLectureList(have_read_page,true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	//getMoreLecture(have_read_page);
            	
            	if (repfresh==0) {
					repfresh=1;
					demo_swiperefreshlayout.setEnabled(false);
					demo_swiperefreshlayout.setRefreshing(false);  
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
							getLectureList(have_read_page,true);

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
        
		mPullDownView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int posi,long arg3) {
				if (posi == 0) {
					return;
				}
				/*Intent intent = new Intent(B_Side_Wages_Acy.this,B_Side_Repair_Detail.class);
				intent.putExtra("lecture_id", mLecturesList.get(posi-1).getArticle_id());
				startActivity(intent);*/
			}
		});
		if (A_0_App.USER_STATUS.equals("5")||A_0_App.USER_STATUS.equals("0") ) {
			showLoadResult(false,false, false, true);
		} else {
			readCache();
		}
		//getLectureList(have_read_page, false);
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	private void readCache() {
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_wages+A_0_App.USER_UNIQID);
		
        if (jsonObject!= null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			
        	showInfo(jsonObject);
			}else{
		   updateInfo();}
	}
	private void showInfo(JSONObject jsonObject) {
		havaSuccessLoadData = true;   
			int state = jsonObject.optInt("status");
			List<Cpk_Side_Wages_list> mlistContact= new ArrayList<Cpk_Side_Wages_list>();
			if (state == 1) {
				mlistContact=JSON.parseArray(jsonObject.optJSONArray("slist")+"", Cpk_Side_Wages_list.class);
			}
			if (isFinishing())
				return;
			if (mlistContact != null && mlistContact.size() > 0) {
				clearBusinessList();
				mLecturesList = mlistContact;
				adapter.notifyDataSetChanged();
				showLoadResult(false, true, false, false);
			} else {
				showLoadResult(false, false, false, true);
			}
			demo_swiperefreshlayout.setRefreshing(false);  
			if(null!=mPullDownView){
			    mPullDownView.onRefreshComplete();
			    mPullDownView.setMode(Mode.DISABLED);
			}
			repfresh=0;
		
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
        	getLectureList(have_read_page, false);
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
        	
        }}
	
	private void getLectureList(int page_no,final boolean pullRefresh) {
		A_0_App.getApi().getWagesList(B_Side_Wages_Acy.this, A_0_App.USER_TOKEN,String.valueOf(page_no), new InterWagesList() {
			@Override
			public void onSuccess(List<Cpk_Side_Wages_list> mList) {
				if (isFinishing())
					return;
				havaSuccessLoadData = true;   
				if (mList != null && mList.size() > 0) {
					have_read_page = 2;
					clearBusinessList();
					mLecturesList = mList;
					adapter.notifyDataSetChanged();
					showLoadResult(false,true, false, false);
					if(pullRefresh)
						PubMehods.showToastStr(B_Side_Wages_Acy.this, "刷新成功");
				} else {
					showLoadResult(false,false, false, true);
				}
				
				demo_swiperefreshlayout.setRefreshing(false);  
				if(null!=mPullDownView){
				    mPullDownView.onRefreshComplete();
				    mPullDownView.setMode(Mode.DISABLED);
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
                if(!havaSuccessLoadData){
                showLoadResult(false,false, true, false);
                }
                PubMehods.showToastStr(B_Side_Wages_Acy.this, msg);
                
                demo_swiperefreshlayout.setRefreshing(false);  
                if(null!=mPullDownView){
                    mPullDownView.onRefreshComplete();
                    mPullDownView.setMode(Mode.DISABLED);
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
		if(A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getApi().getWagesList(B_Side_Wages_Acy.this, A_0_App.USER_TOKEN,String.valueOf(page_no), new InterWagesList() {
			@Override
			public void onSuccess(List<Cpk_Side_Wages_list> mList) {
				if (isFinishing())
					return;
				//A_0_App.getInstance().CancelProgreDialog(B_Side_Wages_Acy.this);
				if (mList != null && mList.size() > 0) {
					have_read_page += 1;
					int totleSize = mLecturesList.size();
					for (int i = 0; i < mList.size(); i++) {
						mLecturesList.add(mList.get(i));
					}
					adapter.notifyDataSetChanged();
					//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
				} else {
					PubMehods.showToastStr(B_Side_Wages_Acy.this,"没有更多了");
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
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Wages_Acy.this);
                PubMehods.showToastStr(B_Side_Wages_Acy.this, msg);
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
	
	private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile,boolean noData) {
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
	
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_lecture_load_error:
				showLoadResult(true,false, false, false);
				clearBusinessList();
				have_read_page = 1;
				getLectureList(have_read_page,true);
				break;
			default:
				break;
			}
		}
	};
	
	public class Mydapter extends BaseAdapter{
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

		@Override
		public View getView(int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (converView == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Side_Wages_Acy.this).inflate(R.layout.item_wages_list, null);
				holder.tv_name = (TextView) converView.findViewById(R.id.tv_name);
				holder.tv_title = (TextView) converView.findViewById(R.id.tv_title);
				//holder.myListView = (MyListView) converView.findViewById(R.id.lv_mylist);
				holder.tv_time = (TextView) converView.findViewById(R.id.tv_time);
				holder.tv_from_person = (TextView) converView.findViewById(R.id.tv_from_person);
				holder.linearLayout = (LinearLayout) converView.findViewById(R.id.wages_liner);
			    converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
			List<Cpk_Side_Wage> list=JSON.parseArray(mLecturesList.get(posi).getLists(), Cpk_Side_Wage.class);
			
			holder.tv_name.setText(mLecturesList.get(posi).getTeacher_name());
			holder.tv_title.setText(mLecturesList.get(posi).getTitle());
			holder.tv_from_person.setText(mLecturesList.get(posi).getSource());
			holder.tv_time.setText(PubMehods.getFormatDate(mLecturesList.get(posi).getTime(), "yyyy/MM/dd  HH:mm"));
			/*adapter_itme = new Mydapter_Itme(list);
			holder.myListView.setAdapter(adapter_itme);*/
			View view = LayoutInflater.from(B_Side_Wages_Acy.this).inflate(R.layout.item_itme_achievement, null);
			
			for (int i = 0; i <list.size(); i++) {
				TextView name = (TextView) view.findViewById(R.id.tv_class);
				TextView srore = (TextView) view.findViewById(R.id.tv_score);
				name.setText(list.get(i).getName());
				srore.setText(list.get(i).getMoney());
				holder.linearLayout.addView(view);
			}
			if(A_0_App.isShowAnimation==true){
			 if(posi>A_0_App.wage_curPosi)
			 {
				A_0_App.wage_curPosi=posi;
				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(50*posi);
			   converView.startAnimation(an);
			 }
			}
			return converView;
		}
		
	}
	
	class ViewHolder{
		TextView tv_name;
		TextView tv_title;
		TextView tv_post_wage;
		TextView tv_time;
		TextView tv_from_person;
		LinearLayout linearLayout;
		//MyListView myListView;
	}
	class ViewHolder2{
		TextView tv_class;
		TextView tv_srore;
		
	}
	public class Mydapter_Itme extends BaseAdapter{
		List<Cpk_Side_Wage> list=new ArrayList<Cpk_Side_Wage>();
		 public Mydapter_Itme(List<Cpk_Side_Wage> list) {
	        this.list = list;
	    }
		@Override
		public int getCount() {
            if (list != null)
                return list.size();
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

		@Override
		public View getView(int posi, View converView, ViewGroup arg2) {
			ViewHolder2 holder;
			if (converView == null) {
				holder = new ViewHolder2();
				converView = LayoutInflater.from(B_Side_Wages_Acy.this).inflate(R.layout.item_itme_achievement, null);
				holder.tv_class = (TextView) converView.findViewById(R.id.tv_class);
				holder.tv_srore = (TextView) converView.findViewById(R.id.tv_score);
			    converView.setTag(holder);
			} else {
				holder = (ViewHolder2) converView.getTag();
			}
			holder.tv_class.setText(list.get(posi).getName());
			holder.tv_srore.setText(list.get(posi).getMoney());
			return converView;
		}
		
	}
	@Override
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		case ZUI_RIGHT_BUTTON:
			Intent intent=new Intent(B_Side_Wages_Acy.this, B_Side_Repair_Add.class);
			startActivity(intent);
		default:
			break;
		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!firstLoad){
			have_read_page = 1;
			getLectureList(have_read_page,false);
		}else{
			firstLoad = false;
		}
		
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
                   // A_0_App.getInstance().showExitDialog(B_Side_Wages_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Wages_Acy.this, AppStrStatic.kicked_offline());
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
        if (mLecturesList != null) {
        	mLecturesList.clear();
        	mPullDownView = null;
        }
        drawable.stop();
        drawable=null;
        adapter = null;
        super.onDestroy();
    }
    
}
