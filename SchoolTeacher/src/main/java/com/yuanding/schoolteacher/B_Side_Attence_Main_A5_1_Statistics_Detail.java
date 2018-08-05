package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
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
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Middle;
import com.yuanding.schoolteacher.bean.Cpk_Side_Attence_AtdStats;
import com.yuanding.schoolteacher.bean.Cpk_Side_Attence_Statistics;
import com.yuanding.schoolteacher.service.Api.InteAttdenceattenceStatistics;
import com.yuanding.schoolteacher.service.Api.InteAttdenceattenceType;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.DensityUtils;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年5月9日 下午2:47:45
 * 学生出勤统计——明细
 */
public class B_Side_Attence_Main_A5_1_Statistics_Detail extends A_0_CpkBaseTitle_Middle{
	
	private HashMap<Integer, View> lmap = new HashMap<Integer, View>();
	private View mLinerReadDataError,mLinerNoContent,liner_lecture_list_whole_view,side_lecture__loading;
	private PullToRefreshListView mPullDownView;
	private List<Cpk_Side_Attence_Statistics> mLecturesList;
	private Mydapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private Boolean firstLoad = false;
	private ACache maACache;
	private JSONObject jsonObject;
	protected Context mContext;
	private List<Cpk_Side_Attence_AtdStats> atdStats=new ArrayList<Cpk_Side_Attence_AtdStats>();
	private MyTypeAdapter adMyTypeAdapter;
	private String user_id="";
	private  int status=0;
	private TextView tv_attence_all; 
	private SharedPreferences sp;
	
	 //[{"status":0,"name":"全部"},{"status":1,"name":"出勤"},{"status":2,"name":"缺勤"},{"status":3,"name":"迟到"},{"status":4,"name":"早退"},{"status":5,"name":"请假"}]
	private String[] types=new String[]{"全部","出勤","缺勤","迟到","早退","请假"};
	
	 /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;
	public B_Side_Attence_Main_A5_1_Statistics_Detail() {
		super();
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_statistics_detail);
		showTitleBt(BACK_BUTTON, true);
		setTitleText("全部");
		showTitleBt(MIDDLE_CLICK, true);
		
		sp = getSharedPreferences("wxb", Context.MODE_PRIVATE);
		firstLoad = true;
		liner_lecture_list_whole_view = findViewById(R.id.liner_lecture_list_whole_view);
		mPullDownView = (PullToRefreshListView)findViewById(R.id.lv_side_lecture_list);
		
		side_lecture__loading=findViewById(R.id.side_lecture__loading);
		mLinerReadDataError = findViewById(R.id.side_lecture_load_error);
		
		 demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		
		 home_load_loading = (LinearLayout) side_lecture__loading.findViewById(R.id.home_load_loading);
		 home_load_loading.setBackgroundResource(R.drawable.load_progress);
		 drawable = (AnimationDrawable) home_load_loading.getBackground();
		 drawable.start(); 

		 
		tv_attence_all=(TextView) findViewById(R.id.tv_attence_all);
		mLinerNoContent = findViewById(R.id.side_lecture_no_content);
		ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.ico_no_attendance);
		tv_blank_name.setText("总计(0)");
		user_id=getIntent().getStringExtra("user_id");
		
		mLinerReadDataError.setOnClickListener(onClick);
		
		mLecturesList = new ArrayList<Cpk_Side_Attence_Statistics>();
		adapter = new Mydapter();
        mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
        mPullDownView.setAdapter(adapter);

        mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新
                String label = DateUtils.formatDateTime(B_Side_Attence_Main_A5_1_Statistics_Detail.this,
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				setTitleText(types[status]);
				have_read_page = 1;
				getAttdenceList(have_read_page,true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            	setTitleText(types[status]);
            	
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
							getAttdenceList(have_read_page,true);

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
        
			
		if (A_0_App.USER_STATUS.equals("2") ){
			if (sp.getString(A_0_App.USER_UNIQID+"type", "")!=null&&!sp.getString(A_0_App.USER_UNIQID+"type", "").equals("")) {
				String type=sp.getString(A_0_App.USER_UNIQID+"type", "");
				atdStats=JSON.parseArray(type, Cpk_Side_Attence_AtdStats.class);
			} else{
				readType();
			}
			
			readCache();
		}else{
			showLoadResult(false,false, false, true);
		}
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
			startListtenerRongYun();// 监听融云网络变化
		}
		
	}

	private void readType() {
		A_0_App.getApi().getAttdenceType(B_Side_Attence_Main_A5_1_Statistics_Detail.this, A_0_App.USER_TOKEN, new InteAttdenceattenceType() {
			
			@Override
			public void onSuccess(List<Cpk_Side_Attence_AtdStats> statistics,String type) {
				if (isFinishing()) 
					return;
				atdStats=statistics;
				 Editor editor = sp.edit();
				 editor.putString(A_0_App.USER_UNIQID+"type",type);
				 editor.commit();
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	private void readCache() {
		maACache = ACache.get(B_Side_Attence_Main_A5_1_Statistics_Detail.this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_personal_detail
				+ A_0_App.USER_UNIQID+user_id+status);
		if (jsonObject != null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
		}else{
		updateInfo();}
	}

	private void updateInfo() {
		MyAsyncTask updateLectureInfo = new MyAsyncTask(
				B_Side_Attence_Main_A5_1_Statistics_Detail.this);
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
        	getAttdenceList(have_read_page, false);
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
	private void showInfo(JSONObject jsonObject) {
		if (isFinishing())
			return;
		havaSuccessLoadData = true;
			int state = jsonObject.optInt("status");
			String totalCount = jsonObject.optString("totalCount");
			String atdList=jsonObject.optString("atdList");
			List<Cpk_Side_Attence_Statistics> mlistContact= new ArrayList<Cpk_Side_Attence_Statistics>();
			if (state == 1) {
				mlistContact=JSON.parseArray(atdList, Cpk_Side_Attence_Statistics.class);
				tv_attence_all.setText("总计("+totalCount+")");
				if (mlistContact != null && mlistContact.size() > 0) {
					clearBusinessList();
					mLecturesList = mlistContact;
					if (adapter!=null) {
					    adapter.notifyDataSetChanged();
	                }
					showLoadResult(false, true, false, false);
				} else {
					showLoadResult(false, false, false, true);
				}
			}
			
			
			demo_swiperefreshlayout.setRefreshing(false);  
			if(null!=mPullDownView){
			    mPullDownView.onRefreshComplete();
			    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
			}
			repfresh=0;
		
	}
	private void getAttdenceList(int page_no,final boolean pullRefresh) {
		A_0_App.getApi().getAttdenceStatistics(B_Side_Attence_Main_A5_1_Statistics_Detail.this, A_0_App.USER_TOKEN,user_id,String.valueOf(page_no),status+"", new InteAttdenceattenceStatistics() {
			@Override
			public void onSuccess(List<Cpk_Side_Attence_Statistics> mList,String totalCount) {
				if (mLecturesList == null)
					return;
				havaSuccessLoadData = true;
				if (mList != null && mList.size() > 0) {
					tv_attence_all.setText("总计("+totalCount+")");
					have_read_page = 2;
					clearBusinessList();
					mLecturesList = mList;
					if (adapter != null) {
					    adapter.notifyDataSetChanged();
                    }
					showLoadResult(false,true, false, false);
					if(pullRefresh)
						PubMehods.showToastStr(B_Side_Attence_Main_A5_1_Statistics_Detail.this, "刷新成功");
				} else {
					showLoadResult(false,false, false, true);
				}
                try {
                	 A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_A5_1_Statistics_Detail.this);
				} catch (Exception e) {
					
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
                if (mLecturesList == null)
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_A5_1_Statistics_Detail.this);
                if(!havaSuccessLoadData){
                    PubMehods.showToastStr(B_Side_Attence_Main_A5_1_Statistics_Detail.this, msg);
                    showLoadResult(false, false, true, false);
                    if (mPullDownView != null)
                        mPullDownView.onRefreshComplete();
                } else {
                    readCache();
                }
                
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
		if(A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getApi().getAttdenceStatistics(B_Side_Attence_Main_A5_1_Statistics_Detail.this, A_0_App.USER_TOKEN,user_id,String.valueOf(page_no),status+"", new InteAttdenceattenceStatistics() {
			public void onSuccess(List<Cpk_Side_Attence_Statistics> mList,String totalCount) {
				if (mLecturesList == null)
					return;
				//A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_A5_1_Statistics_Detail.this);
				if (mList != null && mList.size() > 0) {
					tv_attence_all.setText("总计("+totalCount+")");
					have_read_page += 1;
					int totleSize = mLecturesList.size();
					for (int i = 0; i < mList.size(); i++) {
						mLecturesList.add(mList.get(i));
					}
					if (adapter!=null) {
						adapter.notifyDataSetChanged();
						//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
					}
					
				} else {
					//PubMehods.showToastStr(B_Side_Attence_Main_A5_1_Statistics_Detail.this,"没有更多了");
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
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(
                        B_Side_Attence_Main_A5_1_Statistics_Detail.this);
                PubMehods.showToastStr(B_Side_Attence_Main_A5_1_Statistics_Detail.this, msg);
                if (mPullDownView!=null) {
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
				setTitleText(types[status]);
				have_read_page = 1;
				getAttdenceList(have_read_page,true);
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
			if (lmap.get(posi) == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Side_Attence_Main_A5_1_Statistics_Detail.this).inflate(R.layout.item_attence_statistics_list, null);
				holder.tv_attence_title = (TextView) converView.findViewById(R.id.tv_attence_title);
				holder.tv_attence_time = (TextView) converView.findViewById(R.id.tv_attence_time);
				holder.tv_attence_type = (TextView) converView.findViewById(R.id.tv_attence_type);
				lmap.put(posi, converView);
			    converView.setTag(holder);
			} else {
				converView = lmap.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			holder.tv_attence_title.setText(mLecturesList.get(posi).getTitle());
			if (!mLecturesList.get(posi).getCreate_time().equals("")&&mLecturesList.get(posi).getCreate_time()!=null) {
				holder.tv_attence_time.setText(PubMehods.getFormatDate(Long.valueOf(mLecturesList.get(posi).getCreate_time()), "yyyy/MM/dd HH:mm:ss"));
			}
			
			holder.tv_attence_type.setText(mLecturesList.get(posi).getName());
			if (mLecturesList.get(posi).getAtd_status().equals("1")) {
				holder.tv_attence_type.setTextColor(getResources().getColor(R.color.attence_nomal));
			}else if(mLecturesList.get(posi).getAtd_status().equals("2")){
				holder.tv_attence_type.setTextColor(getResources().getColor(R.color.attence_lack));
			}else if(mLecturesList.get(posi).getAtd_status().equals("3")){
				holder.tv_attence_type.setTextColor(getResources().getColor(R.color.attence_later));
			}else if(mLecturesList.get(posi).getAtd_status().equals("4")){
				holder.tv_attence_type.setTextColor(getResources().getColor(R.color.attence_leave_early));
			}else if(mLecturesList.get(posi).getAtd_status().equals("5")){
				holder.tv_attence_type.setTextColor(getResources().getColor(R.color.attence_leave));
			}
			if(A_0_App.isShowAnimation==true){
			 if (posi > A_0_App.side_statistics_detail_curPosi) {
				A_0_App.side_statistics_detail_curPosi = posi;
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
			return converView;
		}
		
	}
	
	class ViewHolder{
		TextView tv_attence_title;
		TextView tv_attence_time;
		TextView tv_attence_type;
		
	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		if(!firstLoad){
			have_read_page = 1;
			getAttdenceList(have_read_page,false);
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Lost_Found_Main.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_A5_1_Statistics_Detail.this, AppStrStatic.kicked_offline());
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

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		case MIDDLE_CLICK:
			//select_type();
			showWindow(v, 1);
			
			break;
		default:
			break;
		}
	}
	
	private PopupWindow statusPopup;
	private ListView statusListView;
	@SuppressLint("NewApi")
	private void showWindow(View parent,final int type) {
		  WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
          int width = wm.getDefaultDisplay().getWidth();
		if (statusPopup == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.item_cate_listivew,null);
			statusListView = (ListView) view.findViewById(R.id.lsitview_sort_item);
			 LayoutParams lp;        
		        lp=(LayoutParams)statusListView.getLayoutParams();
		        lp.width=width /5*3;
		        statusListView.setLayoutParams(lp);
			statusPopup = new PopupWindow(view, LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		}
		
		if(adMyTypeAdapter != null)
			adMyTypeAdapter = null;
			adMyTypeAdapter = new MyTypeAdapter();
		statusListView.setAdapter(adMyTypeAdapter);
		adMyTypeAdapter.notifyDataSetChanged();
		
		statusPopup.setFocusable(true);
		statusPopup.setOutsideTouchable(true);
		
		statusPopup.setBackgroundDrawable(new BitmapDrawable());
		
		//背景变暗
	    WindowManager.LayoutParams lp=getWindow().getAttributes();
	    lp.alpha = 1.0f;
	    getWindow().setAttributes(lp);
//	    statusPopup.showAsDropDown(parent, 0, 0, Gravity.CENTER_HORIZONTAL);
//	    
	    int x=DensityUtils.dip2px(B_Side_Attence_Main_A5_1_Statistics_Detail.this,168);
		statusPopup.showAsDropDown(parent,-x, A_0_App.getInstance().getShowViewHeight());// 第一参数负的向左，第二个参数正的向下

		statusListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,int position, long id) {

				
				statusPopup.dismiss();
				setTitleText(atdStats.get(position).getName());
				status=Integer.parseInt(atdStats.get(position).getStatus());
				have_read_page=1;
				if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
					A_0_App.getInstance().showProgreDialog(B_Side_Attence_Main_A5_1_Statistics_Detail.this, "", true);
					getAttdenceList(have_read_page, false);
				}else{
					readCache();
				}
				
			}
		});
		
		statusPopup.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				 WindowManager.LayoutParams lp=getWindow().getAttributes();
		        lp.alpha = 1f;
		        getWindow().setAttributes(lp);	
		        

			}
		});
		
	}

	
    
    /**
	 * 
	 * @author 类型adpter
	 * 
	 */
	// 加载列表数据
	public class MyTypeAdapter extends BaseAdapter {

		@Override
		public int getCount() {

            if (atdStats != null)
                return atdStats.size();
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
			if (converView == null) {
				converView = LayoutInflater.from(
						B_Side_Attence_Main_A5_1_Statistics_Detail.this).inflate(
						R.layout.item_attence_text, null);
			}
			TextView tv_acy_name = (TextView) converView
					.findViewById(R.id.tv_item_pub_text);
			
			tv_acy_name.setText(atdStats.get(posi).getName());
			if (Integer.parseInt(atdStats.get(posi).getStatus())==status) {
				tv_acy_name.setBackgroundColor(getResources().getColor(R.color.attence_select_color));
			}else{
				tv_acy_name.setBackgroundColor(getResources().getColor(R.color.transparent));
			}
			if(A_0_App.isShowAnimation==true){
			 if(posi>A_0_App.sent_sms_curPosi)
			 {
				A_0_App.sent_sms_curPosi=posi;
				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(50*posi);
			   converView.startAnimation(an);
			 }
			}
			
			return converView;
		}

	}
}