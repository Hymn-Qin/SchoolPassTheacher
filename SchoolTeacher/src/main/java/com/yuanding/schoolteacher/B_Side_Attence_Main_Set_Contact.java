package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Side_Attence_Class;
import com.yuanding.schoolteacher.service.Api.InterAttenceClassList;
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
 * 设置委派考勤人
 */
public class B_Side_Attence_Main_Set_Contact extends A_0_CpkBaseTitle_Navi{

	private View mLinerReadDataError,mLinerNoContent,liner_acy_list_whole_view,side_acy_loading;
	private PullToRefreshListView mPullDownView;
	private MyAdapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private List<Cpk_Side_Attence_Class> mCourseList = null;
	private String click_id = "";
	private ACache maACache;
	private JSONObject jsonObject;
	
	 /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    
    private ImageView iv_blank_por;
    private TextView tv_blank_name;
    private boolean havaSuccessLoadData = false;
    private HashMap<Integer, View> lmap = new HashMap<Integer, View>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_attence_set_contact);
		setTitleText("设置委派考勤人");
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_acy_list_whole_view = findViewById(R.id.liner_attence_list_whole_view);
		mLinerReadDataError = findViewById(R.id.side_attence_load_error);
		mLinerNoContent = findViewById(R.id.side_attence_no_content);
		side_acy_loading=findViewById(R.id.side_attence_loading);
		iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
		tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.icon_no_info);
		tv_blank_name.setText("太懒了，一个都没有~");
		
		home_load_loading = (LinearLayout) side_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start();
		
		mLinerReadDataError.setOnClickListener(onClick);
       
		mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_side_attence_list);
		mCourseList = new ArrayList<Cpk_Side_Attence_Class>();
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
      
      //**************************新增到这**********************
		
		readCache();
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		
	}
	
	private void readCache() {
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_attence_class_set_list+A_0_App.USER_UNIQID);
        if (jsonObject!= null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
			}else{
		   updateInfo();}
	}

	private void showInfo(JSONObject jsonObject) {
		try {
			havaSuccessLoadData = true;   
			int state = jsonObject.optInt("status");
			List<Cpk_Side_Attence_Class> mlistContact = new ArrayList<Cpk_Side_Attence_Class>();
			if (state == 1) {
					mlistContact=JSON.parseArray(jsonObject.optJSONArray("list")+"",Cpk_Side_Attence_Class.class);
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
				PubMehods.showToastStr(B_Side_Attence_Main_Set_Contact.this,"没有委派人");
			}
			if(null!=mPullDownView){
			    mPullDownView.onRefreshComplete();
			    mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
			}
			
			demo_swiperefreshlayout.setRefreshing(false);  
		
			repfresh=0;
		} catch (Exception e) {
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
			case R.id.side_attence_load_error:
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
		A_0_App.getApi().getAttenceClassList(B_Side_Attence_Main_Set_Contact.this,A_0_App.USER_TOKEN,String.valueOf(page_no),new InterAttenceClassList() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onSuccess(List<Cpk_Side_Attence_Class> mList) {
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
						PubMehods.showToastStr(B_Side_Attence_Main_Set_Contact.this, "刷新成功");
				} else {
					showLoadResult(false,false, false, true);
					PubMehods.showToastStr(B_Side_Attence_Main_Set_Contact.this,"没有班级列表");
				}
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
                if(!havaSuccessLoadData){
                showLoadResult(false,false, true, false);
                }
                PubMehods.showToastStr(B_Side_Attence_Main_Set_Contact.this, msg);
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
		A_0_App.getApi().getAttenceClassList(B_Side_Attence_Main_Set_Contact.this,A_0_App.USER_TOKEN,String.valueOf(page_no), new InterAttenceClassList() {
			@Override
			public void onSuccess(List<Cpk_Side_Attence_Class> mList) {
				if (isFinishing())
					return;
				if (mList != null && mList.size() > 0) {
					have_read_page += 1;
					for (int i = 0; i < mList.size(); i++) {
						mCourseList.add(mList.get(i));
					}
					adapter.notifyDataSetChanged();
				} else {
					PubMehods.showToastStr(B_Side_Attence_Main_Set_Contact.this,"没有更多了");
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
                A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_Set_Contact.this);
                PubMehods.showToastStr(B_Side_Attence_Main_Set_Contact.this, msg);
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

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder;
			if (lmap.get(posi) == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Side_Attence_Main_Set_Contact.this).inflate(R.layout.item_side_attence_set_contact, null);
				
				holder.tv_side_attence_class = (TextView) converView.findViewById(R.id.tv_side_attence_title);
				holder.tv_side_attence_name = (TextView) converView.findViewById(R.id.tv_side_attence_name);
				holder.tv_side_attence_temp = (TextView) converView.findViewById(R.id.tv_side_attence_temp);
				lmap.put(posi, converView);
				converView.setTag(holder);
			} else {
				converView = lmap.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			
		
			holder.tv_side_attence_class.setText(mCourseList.get(posi).getOrgan_name());
			if (mCourseList.get(posi).getName().equals("")) {
				holder.tv_side_attence_temp.setVisibility(View.INVISIBLE);
				holder.tv_side_attence_name.setHint("未选择");
				holder.tv_side_attence_name.setText("");
				 
			}else {
				holder.tv_side_attence_temp.setVisibility(View.VISIBLE);
				holder.tv_side_attence_name.setText(mCourseList.get(posi).getName());
			}
		  
		
			
			
			converView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					    click_id=mCourseList.get(posi).getOrgan_id();
						Intent intent = new Intent(B_Side_Attence_Main_Set_Contact.this,B_Side_Attence_Main_Class_People.class);
						intent.putExtra("organ_id", click_id);
						intent.putExtra("class_name", mCourseList.get(posi).getOrgan_name());
						intent.putExtra("name", mCourseList.get(posi).getName());
						startActivityForResult(intent, 1);
					
				}
			});
			return converView;
		}
	}

	class ViewHolder {
	
		TextView tv_side_attence_class;
		TextView tv_side_attence_name;
		TextView tv_side_attence_temp;
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
			String organ_name = data.getExtras().getString("organ_name");
//			if (!"".equals(organ_name)) {
				if (requestCode == 1) {
					for (int i = 0; i < mCourseList.size(); i++) {
						if (mCourseList.get(i).getOrgan_id().equals(click_id)) {
							mCourseList.get(i).setName(organ_name);
							System.out.println("KKKK"+click_id);
						}
					}
					adapter.notifyDataSetChanged();
				}
			}
		//}
	}
	
	private void updateInfo(){
		MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
		updateLectureInfo.execute();
    }

    class MyAsyncTask extends AsyncTask<Void,Integer,Integer>{
        @SuppressWarnings("unused")
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
    @SuppressWarnings("static-access")
	public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @SuppressWarnings("incomplete-switch")
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
                            A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_Set_Contact.this, AppStrStatic.kicked_offline());
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
		
		drawable.stop();
		drawable=null;
		adapter= null;
		home_load_loading.removeAllViews();
		home_load_loading=null;
		setContentView(R.layout.viewfull);
		jsonObject=null;
		maACache=null;
		iv_blank_por.setBackgroundResource(0);
		tv_blank_name=null;
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
