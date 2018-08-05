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
import com.yuanding.schoolteacher.bean.Cpk_Notice_Failure_List;
import com.yuanding.schoolteacher.service.Api.AppNotice_InviteInstallAppCallBack;
import com.yuanding.schoolteacher.service.Api.InterNoticeFailureList;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月12日 下午1:33:48  通知短信接收失败列表
 * 
 */
public class B_Side_Notice_Failure_SmsList extends A_0_CpkBaseTitle_Navi{

	private View mLinerReadDataError,mLinerNoContent,liner_notice_failure_list_whole_view,side_notice_failure_loading;
	private PullToRefreshListView mPullDownView;
	private MyAdapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private List<Cpk_Notice_Failure_List> mCourseList = null;
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	//private BitmapUtils bitmapUtils;
	private int click_posi = 0;
	private ACache maACache;
	private JSONObject jsonObject;
	 /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;

    
    private String messageId="";// 消息id
    private Boolean firstLoad = false;
    private boolean havaSuccessLoadData = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_notice_failure_smslist);
		
		messageId = getIntent().getExtras().getString("messageId");
		firstLoad = true;
		setTitleText("接收失败");
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		liner_notice_failure_list_whole_view = findViewById(R.id.liner_notice_failure_list_whole_view);
		mLinerReadDataError = findViewById(R.id.side_notice_failure_load_error);
		mLinerNoContent = findViewById(R.id.side_notice_failure_no_content);
		side_notice_failure_loading=findViewById(R.id.side_notice_failure_loading);
		ImageView iv_blank_por = (ImageView)mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView)mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_huodong);
		tv_blank_name.setText("还是空空的");

		mLinerReadDataError.setOnClickListener(onClick);
		
		home_load_loading = (LinearLayout) side_notice_failure_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start();
		
		imageLoader = A_0_App.getInstance().getimageLoader();
		//bitmapUtils=A_0_App.getBitmapUtils(this,R.drawable.ic_default_acy_empty,R.drawable.ic_default_acy_empty);
       
		options = A_0_App.getInstance().getOptions(R.drawable.ic_default_banner_empty_bg, R.drawable.ic_default_banner_empty_bg,
				R.drawable.ic_default_banner_empty_bg);
		mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_notice_failure_list);
		mCourseList = new ArrayList<Cpk_Notice_Failure_List>();
		adapter = new MyAdapter();
        mPullDownView.setMode(Mode.DISABLED);
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
				Intent intent = new Intent(B_Side_Notice_Failure_SmsList.this,B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.class);
				intent.putExtra("acy_type", mCourseList.get(posi-1).getParam());// 正常列表进入
				intent.putExtra("messageId",messageId);
				intent.putExtra("title",mCourseList.get(posi-1).getTitle());
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
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_notice_failure_list+messageId+A_0_App.USER_UNIQID);
        if (jsonObject!= null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
			}else{
		   updateInfo();}
	}

	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			havaSuccessLoadData = true;   
			int state = jsonObject.optInt("status");
			List<Cpk_Notice_Failure_List> mlistContact = new ArrayList<Cpk_Notice_Failure_List>();
			if (state == 1) {
					mlistContact=JSON.parseArray(jsonObject.optJSONArray("data")+"",Cpk_Notice_Failure_List.class);
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
			}
			if(null!=mPullDownView){
			    mPullDownView.onRefreshComplete();
			    mPullDownView.setMode(Mode.DISABLED);
			}
			
			demo_swiperefreshlayout.setRefreshing(false);  
			
			repfresh=0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile,boolean noData) {
		
		if (wholeView)
			liner_notice_failure_list_whole_view.setVisibility(View.VISIBLE);
		else
			liner_notice_failure_list_whole_view.setVisibility(View.GONE);
		
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
			side_notice_failure_loading.setVisibility(View.VISIBLE);
		} else {
			if (drawable != null) {
				drawable.stop();
			}
			side_notice_failure_loading.setVisibility(View.GONE);
		}
	}
	
	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.side_notice_failure_load_error:
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
		A_0_App.getApi().getNoticeFailureList(B_Side_Notice_Failure_SmsList.this,A_0_App.USER_TOKEN,messageId,String.valueOf(page_no), new InterNoticeFailureList() {
			@Override
			public void onSuccess(List<Cpk_Notice_Failure_List> mList) {
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
						PubMehods.showToastStr(B_Side_Notice_Failure_SmsList.this, "刷新成功");
				} else {
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
                if (isFinishing())
                    return;
                if(!havaSuccessLoadData){
                showLoadResult(false,false, true, false);
                }
                PubMehods.showToastStr(B_Side_Notice_Failure_SmsList.this, msg);
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

	// 上拉刷新初始化数据
	private void getMoreLecture(int page_no) {
		if(A_0_App.USER_TOKEN == null || A_0_App.USER_TOKEN.equals(""))
			return;
		A_0_App.getApi().getNoticeFailureList(B_Side_Notice_Failure_SmsList.this,A_0_App.USER_TOKEN,messageId,String.valueOf(page_no), new InterNoticeFailureList() {
			@Override
			public void onSuccess(List<Cpk_Notice_Failure_List> mList) {
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
					PubMehods.showToastStr(B_Side_Notice_Failure_SmsList.this,"没有更多了");
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
//              A_0_App.getInstance().CancelProgreDialog(B_Side_Notice_Failure_SmsList.this);
                PubMehods.showToastStr(B_Side_Notice_Failure_SmsList.this, msg);
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
     * @Title: sendInviteApp
     * @Description: 
     * @return void 返回类型
     */
    private void sendInviteApp() {
        A_0_App.getApi().send_AppNoticeInviteInstall_Req("messageId", A_0_App.USER_TOKEN, new AppNotice_InviteInstallAppCallBack() {
            @Override
            public void onSuccess(String message) {
                if (isFinishing())
                    return;
                PubMehods.showToastStr(B_Side_Notice_Failure_SmsList.this, message);  
                showTitleBt(ZUI_RIGHT_TEXT, false);
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
//                A_0_App.getInstance().CancelProgreDialog(B_Side_Notice_Failure_SmsList.this);
                PubMehods.showToastStr(B_Side_Notice_Failure_SmsList.this, msg);

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
				converView = LayoutInflater.from(B_Side_Notice_Failure_SmsList.this).inflate(R.layout.item_side_notice_failure_sms, null);
				holder.tv_notice_title = (TextView) converView.findViewById(R.id.tv_notice_title);
				holder.tv_notice_content = (TextView) converView.findViewById(R.id.tv_notice_content);
				holder.tv_notice_count = (TextView) converView.findViewById(R.id.tv_notice_count);
				converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
        
			holder.tv_notice_title.setText( mCourseList.get(posi).getTitle()+"：当前 ");
			holder.tv_notice_content.setText( mCourseList.get(posi).getContent());
			holder.tv_notice_count.setText( mCourseList.get(posi).getNum());
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
		
		TextView tv_notice_count;
		TextView tv_notice_content;
		TextView tv_notice_title;
		
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
		if(!firstLoad){
			have_read_page = 1;
			startReadData(have_read_page, false);
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
                            A_0_App.getInstance().showExitDialog(B_Side_Notice_Failure_SmsList.this, AppStrStatic.kicked_offline());
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
		//bitmapUtils=null;
		super.onDestroy();
	}
	
	
	@Override
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		 case ZUI_RIGHT_TEXT:
	        	//邀请安装APP
	        	//showDialog();
	        	break;
		default:
			break;
		}
		
	}
	protected void showDialog() {
		final GeneralDialog upDateDialog = new GeneralDialog(this, R.style.Theme_GeneralDialog);
		upDateDialog.setTitle(R.string.str_my_side_appnotice_receiverresult_dialog_title);
		upDateDialog.setContent(AppStrStatic.str_my_side_appnotice_receiverresult_dialog_content());
		upDateDialog.setCancelable(true);
		upDateDialog.setCanceledOnTouchOutside(true);
		upDateDialog.showLeftButton(R.string.str_my_side_appnotice_receiverresult_dialog_leftbtn,
				new OnClickListener() {
					@Override
					public void onClick(View v) {  //暂不开启
						upDateDialog.cancel();

					}
				});
		upDateDialog.showRightButton(R.string.str_my_side_appnotice_receiverresult_dialog_rightbtn,
				new OnClickListener() {   //立即开启
					@Override
					public void onClick(View v) {
						upDateDialog.cancel();
						sendInviteApp();
					}
				});
		upDateDialog.show();
	}
	
}
