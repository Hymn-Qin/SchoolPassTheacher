package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Notice_List;
import com.yuanding.schoolteacher.service.Api.InterNoticeDelete;
import com.yuanding.schoolteacher.service.Api.InterNoticeListWaitSure;
import com.yuanding.schoolteacher.service.Api.InterUpdateList;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.service.Api.SureReceIptCallBack;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.DensityUtils;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月16日 下午1:30:20 校党委的通知的会议和通知 通知级别，1：学校，2：院系，3班级
 */
public class B_Mess_Notice_List_WaitSure extends A_0_CpkBaseTitle_Navi {

	private View mLinerReadDataError, mLinerNoContent, mWholeView,
			message_notice_loading;
	private static final int READ_MESS_COUNT = 10;
	private PullToRefreshListView mPullDownView;
	private Mydapter adapter;
	private int have_read_page = 1;// 已经读的页数
	private List<Cpk_Notice_List> mCourseList = null;
//	protected ImageLoader imageLoader;
//	private DisplayImageOptions options;
	private ImageOptions bitmapUtils;
	private String level;
	private int click_posi = 0;
	private ACache maACache;
	private JSONObject jsonObject;
	private int LONGDELETE;// 布局判断
	private RelativeLayout rel_notice_operating;
	private Button btn_notice_move, btn_notice_cancel;
	private TextView select;
	private int no_Read_Count = 0;
    private boolean firsetEnterLoader,havaSuccessLoadData = false;
    /**
     * 新增下拉使用
     */
    private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
    private int repfresh=0;//避免下拉和上拉冲突
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    
    //当前等待确认的信息的条数
//    int mCurrentWaitSureNoticeNumber = 0;
    
    boolean isHasUpdata = false; //是否有操作过数据（删除数据，阅读数据，确认回执）
    boolean isMultiSelectStatus = false;//是否在多选状态
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setView(R.layout.activity_mess_notice_list);

	    firsetEnterLoader = true;
	    demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		level = getIntent().getExtras().getString("level");
		setTitleText("待确认信息");
		no_Read_Count = getIntent().getIntExtra("count", 0);
		select = (TextView) findViewById(R.id.select);
		rel_notice_operating = (RelativeLayout) findViewById(R.id.rel_notice_operating);
		btn_notice_move = (Button) findViewById(R.id.btn_notice_move);
		btn_notice_cancel = (Button) findViewById(R.id.btn_notice_cancel);

		mWholeView = findViewById(R.id.liner_notice_list_whole_view);
		mLinerReadDataError = findViewById(R.id.message_notice_load_error);
		message_notice_loading = findViewById(R.id.message_notice_info_loading);
		home_load_loading = (LinearLayout) message_notice_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
		mLinerNoContent = findViewById(R.id.message_notice_no_content);
		ImageView iv_blank_por = (ImageView) mLinerNoContent.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.noinfo);
		tv_blank_name.setText("风停叶静，你还没有收到任何消息~");
		mLinerReadDataError.setOnClickListener(onClick);
		btn_notice_move.setOnClickListener(onClick);
		btn_notice_cancel.setOnClickListener(onClick);
//		imageLoader = A_0_App.getInstance().getimageLoader();
//		options = A_0_App.getInstance().getOptions(
//				R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg,
//				R.drawable.ic_default_empty_bg);
		bitmapUtils=A_0_App.getBitmapUtils(this,R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg,false);
		mPullDownView = (PullToRefreshListView) findViewById(R.id.lv_message_notice_list);
		mCourseList = new ArrayList<Cpk_Notice_List>();
		adapter = new Mydapter();
		mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);//新增设置
		mPullDownView.setAdapter(adapter);
		mPullDownView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getApplicationContext(),System.currentTimeMillis(),DateUtils.FORMAT_SHOW_TIME| DateUtils.FORMAT_SHOW_DATE| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
						select.setText("0");
						have_read_page = 1;
						getNoticeListData(level, have_read_page, true,false);
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
			demo_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {

							select.setText("0");
							have_read_page = 1;
							if(null!=mPullDownView){
								mPullDownView.setMode(Mode.DISABLED);
			                }
							
							getNoticeListData(level, have_read_page, true, false);

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
		readCache(level);

		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
			startListtenerRongYun();// 监听融云网络变化
		}
		
		
	}

	private void readCache(String level) {
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_notice_list_waitsure+ A_0_App.USER_UNIQID + level);
		if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
		}else{
		    updateInfo();
		}
	}

	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		List<Cpk_Notice_List> list = getList(jsonObject);
		if (isFinishing())
			return;
		havaSuccessLoadData = true;
		if (list != null && list.size() > 0) {

			judge_read(list,firsetEnterLoader);
			showLoadResult(false, true, false, false);
			clearBusinessList(false);
			mCourseList = list;
			adapter.notifyDataSetChanged();
			
			if(null!=mCourseList && mCourseList.size()>0){
				//显示右上角全部确认按钮
				showTitleBt(ZUI_RIGHT_TEXT, true);
	            setZuiYouText("全部确认");
	            isMultiSelectStatus = false;
			}
			
            
		} else {
			showLoadResult(false, false, false, true);
		}
        demo_swiperefreshlayout.setRefreshing(false);  
        if(null!=mPullDownView){
            mPullDownView.onRefreshComplete();
            mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
        }
        repfresh=0;
	}

	private List<Cpk_Notice_List> getList(JSONObject jsonObject) {
		
			int state = jsonObject.optInt("status");
			List<Cpk_Notice_List> mlistContact = new ArrayList<Cpk_Notice_List>();
			if (state == 1) {
				mlistContact = JSON.parseArray(jsonObject.optJSONArray("list")+ "", Cpk_Notice_List.class);
			}
			return mlistContact;
		
	}
	
    private void judge_read(List<Cpk_Notice_List> list,boolean firsetEnter) {
        
        if (list == null || list.size() <= 0)
            return;
        
        int temp_count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMl_status().equals("1")) {
                temp_count++;
            }
        }
        
        if (list.size() < READ_MESS_COUNT) {
            if (temp_count > 0) {
//                showTitleBt(ZUI_RIGHT_TEXT, true);
//                setZuiYouText("全部设为已读");
            	//显示右上角全部确认按钮
    			showTitleBt(ZUI_RIGHT_TEXT, true);
                setZuiYouText("全部确认");
                isMultiSelectStatus = false;
            } else {
                if(firsetEnter){
                    if (no_Read_Count > 0) {
//                        showTitleBt(ZUI_RIGHT_TEXT, true);
//                        setZuiYouText("全部设为已读");
                    	//显示右上角全部确认按钮
            			showTitleBt(ZUI_RIGHT_TEXT, true);
                        setZuiYouText("全部确认");
                        isMultiSelectStatus = false;
                    } else {
//                        showTitleBt(ZUI_RIGHT_TEXT, false);
                    	//显示右上角全部确认按钮
            			showTitleBt(ZUI_RIGHT_TEXT, true);
                        setZuiYouText("全部确认");
                        isMultiSelectStatus = false;
                    }
                }else{
//                    showTitleBt(ZUI_RIGHT_TEXT, false);
                	//显示右上角全部确认按钮
        			showTitleBt(ZUI_RIGHT_TEXT, true);
                    setZuiYouText("全部确认");
                    isMultiSelectStatus = false;
                }
            }
        } else {
            if(firsetEnter){
                if (no_Read_Count > 0) {
//                    showTitleBt(ZUI_RIGHT_TEXT, true);
//                    setZuiYouText("全部设为已读");
                	//显示右上角全部确认按钮
        			showTitleBt(ZUI_RIGHT_TEXT, true);
                    setZuiYouText("全部确认");
                    isMultiSelectStatus = false;
                } else {
//                    showTitleBt(ZUI_RIGHT_TEXT, false);
                	//显示右上角全部确认按钮
        			showTitleBt(ZUI_RIGHT_TEXT, true);
                    setZuiYouText("全部确认");
                    isMultiSelectStatus = false;
                }
            }else{
                if (temp_count > 0) {
//                    showTitleBt(ZUI_RIGHT_TEXT, true);
//                    setZuiYouText("全部设为已读");
                	//显示右上角全部确认按钮
        			showTitleBt(ZUI_RIGHT_TEXT, true);
                    setZuiYouText("全部确认");
                    isMultiSelectStatus = false;
                } else {
//                    showTitleBt(ZUI_RIGHT_TEXT, false);
                	//显示右上角全部确认按钮
        			showTitleBt(ZUI_RIGHT_TEXT, true);
                    setZuiYouText("全部确认");
                    isMultiSelectStatus = false;
                }
            }
        }
    }
	 
	private void getNoticeListData(final String level, int page_no,final boolean pullRefresh,final boolean onlyUpdateCache) {

		A_0_App.getApi().getNoticeListWaitSure(B_Mess_Notice_List_WaitSure.this,
				A_0_App.USER_TOKEN, level, String.valueOf(page_no),
				new InterNoticeListWaitSure() {
					@Override
					public void onSuccess(List<Cpk_Notice_List> mList) {
						if (isFinishing())
							return;
						havaSuccessLoadData = true;
						if(!onlyUpdateCache){//更新缓存用
    						
    						if (mList != null && mList.size() > 0) {
    
    							if (LONGDELETE == 1) {
    								showTitleBt(ZUI_RIGHT_TEXT, true);
    								setZuiYouText("全选");
    								isMultiSelectStatus = true;
    							} else {
//    								showTitleBt(ZUI_RIGHT_TEXT, false);
    								//显示右上角全部确认按钮
    		            			showTitleBt(ZUI_RIGHT_TEXT, true);
    		                        setZuiYouText("全部确认");
    		                        isMultiSelectStatus = false;
    		                        
    								judge_read(mList,firsetEnterLoader);
    							}
    							showLoadResult(false, true, false, false);
    							have_read_page = 2;
    							clearBusinessList(false);
    							mCourseList = mList;
    							adapter.notifyDataSetChanged();
    							if (pullRefresh)
    								PubMehods.showToastStr(B_Mess_Notice_List_WaitSure.this,"刷新成功");
    						} else {
    							showTitleBt(ZUI_RIGHT_TEXT, false);
    	                        
								judge_read(mList,firsetEnterLoader);
    							showLoadResult(false, false, false, true);
    							// PubMehods.showToastStr(B_Mess_XiaoDangWei.this,"没有消息");
    						}
    						
    						//new add
    						demo_swiperefreshlayout.setRefreshing(false);  
    						if(null!=mPullDownView){
    		                	mPullDownView.onRefreshComplete();
    		                	mPullDownView.setMode(Mode.PULL_UP_TO_REFRESH);
    		                }
    						repfresh=0;
						}
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
                        if (!havaSuccessLoadData) {
                            showLoadResult(false, false, true, false);
                        }
                        showTitleBt(ZUI_RIGHT_TEXT, false);
                        
                        //new add
                        PubMehods.showToastStr(B_Mess_Notice_List_WaitSure.this, msg);
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

	//全部设置为已读、批量更新
	private void batchUpdate(String level) {
        if (isFinishing())
            return;
        A_0_App.getInstance().showProgreDialog(B_Mess_Notice_List_WaitSure.this, "", true);
		A_0_App.getApi().updateNoticeInfo(level, A_0_App.USER_TOKEN,
				new InterUpdateList() {
					@Override
					public void onSuccess() {
						if (isFinishing())
							return;
						A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_List_WaitSure.this);
						for (int i = 0; i < mCourseList.size(); i++) {
							mCourseList.get(i).setMl_status("0");// 全部设置为已读
							adapter.notifyDataSetChanged();
						}
						no_Read_Count=0;
//						showTitleBt(ZUI_RIGHT_TEXT, false);
						//显示右上角全部确认按钮
            			showTitleBt(ZUI_RIGHT_TEXT, true);
                        setZuiYouText("全部确认");
                        isMultiSelectStatus = false;
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
                        A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_List_WaitSure.this);
                        PubMehods.showToastStr(B_Mess_Notice_List_WaitSure.this, msg);

                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}

	private void showLoadResult(boolean loading, boolean list,boolean loadFaile, boolean noData) {
		if (list)
			mWholeView.setVisibility(View.VISIBLE);
		else
			mWholeView.setVisibility(View.GONE);

		if (loadFaile)
			mLinerReadDataError.setVisibility(View.VISIBLE);
		else
			mLinerReadDataError.setVisibility(View.GONE);

		if (noData)
			mLinerNoContent.setVisibility(View.VISIBLE);
		else
			mLinerNoContent.setVisibility(View.GONE);
		if (loading){
			drawable.start();
			message_notice_loading.setVisibility(View.VISIBLE);}
		else{
			if (drawable!=null) {
        		drawable.stop();
			}
			message_notice_loading.setVisibility(View.GONE);}
	}

	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.message_notice_load_error:
				showLoadResult(true, false, false, false);
				getNoticeListData(level, 1, true,false);
				break;
			case R.id.btn_notice_move:// 删除
				isHasUpdata = true;
				String messgesid = "";
				for (int i = 0; i < mCourseList.size(); i++) {
					if (mCourseList.get(i).isSelected() == true) {
						messgesid = mCourseList.get(i).getMessage_id() + ","+ messgesid;
					}

				}
				if (messgesid.length() > 0 && messgesid != "") {
					noticeDelete(messgesid.substring(0, messgesid.length() - 1));
				}

				break;
			case R.id.btn_notice_cancel:// 取消
//				showTitleBt(ZUI_RIGHT_TEXT, false);
				//显示右上角全部确认按钮
    			showTitleBt(ZUI_RIGHT_TEXT, true);
                setZuiYouText("全部确认");
                isMultiSelectStatus = false;
                
				for (int i = 0; i < mCourseList.size(); i++) {
					mCourseList.get(i).setSelected(false);
				}
				select.setText("0");
				LONGDELETE = 0;
				judge_read(mCourseList,firsetEnterLoader);
				rel_notice_operating.setVisibility(View.GONE);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	private void noticeDelete(String messgesid) {
		A_0_App.getApi().NoticeDelete(A_0_App.USER_TOKEN, messgesid,
				new InterNoticeDelete() {

					@Override
					public void onSuccess() {
						if (isFinishing())
							return;

						have_read_page = 1;
						getNoticeListData(level, have_read_page, true,false);

						select.setText("0");
						LONGDELETE = 0;
						rel_notice_operating.setVisibility(View.GONE);

						PubMehods.showToastStr(B_Mess_Notice_List_WaitSure.this, "删除成功");
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        PubMehods.showToastStr(B_Mess_Notice_List_WaitSure.this, msg);
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}

	// 上拉刷新初始化数据
	private void getMoreLecture(int page_no) {
		A_0_App.getApi().getNoticeListWaitSure(B_Mess_Notice_List_WaitSure.this,
				A_0_App.USER_TOKEN, level, String.valueOf(page_no),
				new InterNoticeListWaitSure() {
					@Override
					public void onSuccess(List<Cpk_Notice_List> mList) {
						if (isFinishing())
							return;
//						A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_List.this);
						
						if (mList != null && mList.size() > 0) {
							have_read_page += 1;
							int totleSize = mCourseList.size();
							for (int i = 0; i < mList.size(); i++) {
								mCourseList.add(mList.get(i));
							}
							adapter.notifyDataSetChanged();
							//mPullDownView.getRefreshableView().setSelection(totleSize + 1);
							judge_read(mCourseList,firsetEnterLoader);
						} else {
							PubMehods.showToastStr(B_Mess_Notice_List_WaitSure.this,"没有更多了");
						}
						if(null!=mPullDownView){
		                	mPullDownView.onRefreshComplete();
		                }
						//new add
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
//                      A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_List.this);
                        PubMehods.showToastStr(B_Mess_Notice_List_WaitSure.this, msg);
                        if(null!=mPullDownView){
                            mPullDownView.onRefreshComplete();
                        }
                        
                        //new add
                        repfresh=0;
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}

	public class Mydapter extends BaseAdapter {

		public static final int TYPE_COUNT = 2;
		public static final int TYPE_NOTICEMSG = 1; //正常的数据列表类型
		public static final int TYPE_HAVEBTN = 0; //带按钮类型
		private int currentType;//当前item类型
		@Override
		public int getCount() {
			if (mCourseList != null)
				return mCourseList.size();
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
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (0==position) {
//				return TYPE_HAVEBTN;
				return TYPE_NOTICEMSG;
			} else {
				return TYPE_NOTICEMSG;
			}
		}

		@Override
		public int getViewTypeCount() {
			return TYPE_COUNT;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(int posi, View converView, ViewGroup arg2) {
									
			View haveWaitSureBtnView = null;
			View defaultNoticeMsgView = null;
			currentType = getItemViewType(posi);

				if(currentType==TYPE_HAVEBTN){
					ViewHolder2 holder2 = null;
					if(null==converView){
						haveWaitSureBtnView = LayoutInflater.from(B_Mess_Notice_List_WaitSure.this).inflate(R.layout.item_notificity_list_waitsurebtn, null);
						holder2 = new ViewHolder2();
						holder2.btn_waitsurenotice = (Button) haveWaitSureBtnView.findViewById(R.id.btn_waitsurenotice);
						haveWaitSureBtnView.setTag(holder2);
						converView = haveWaitSureBtnView;						
					}else{
						holder2 = (ViewHolder2)converView.getTag();
					}
					holder2.btn_waitsurenotice.setVisibility(View.GONE);
					
				}else{
				    ViewHolder holder = null;
				    
				    if(null==converView){
				    	defaultNoticeMsgView = LayoutInflater.from(B_Mess_Notice_List_WaitSure.this).inflate(R.layout.item_notificity_list_havesurereceiverbtn, null);
					    holder = new ViewHolder();

					    holder.liner_notice = (RelativeLayout) defaultNoticeMsgView .findViewById(R.id.liner_notice);
					    holder.liner_checkbox = (ImageView) defaultNoticeMsgView.findViewById(R.id.liner_checkbox);
					    holder.ll_notice = (LinearLayout) defaultNoticeMsgView.findViewById(R.id.ll_notice);
					    holder.iv_notice_delete = (ImageView) defaultNoticeMsgView.findViewById(R.id.iv_notice_delete);
					    holder.iv_notice_read_tag = (ImageView) defaultNoticeMsgView.findViewById(R.id.iv_notice_read_tag);
					    holder.iv_notice_deatil_img = (ImageView) defaultNoticeMsgView.findViewById(R.id.iv_notice_deatil_img);
					    holder.iv_notice_type = (TextView) defaultNoticeMsgView.findViewById(R.id.iv_notice_type);
					    holder.tv_notice_detail_title = (TextView) defaultNoticeMsgView.findViewById(R.id.tv_notice_detail_title);
					    holder.tv_notice_detail_time = (TextView) defaultNoticeMsgView.findViewById(R.id.tv_notice_detail_time);
					    holder.tv_notice_detail_replay_count_aa = (TextView) defaultNoticeMsgView.findViewById(R.id.tv_notice_detail_replay_count_aa);
					    holder.tv_notice_detail_read_count = (TextView) defaultNoticeMsgView.findViewById(R.id.tv_notice_detail_read_count);
					    holder.tv_notice_deatil_content = (TextView) defaultNoticeMsgView.findViewById(R.id.tv_notice_deatil_content);
					    holder.tv_notice_deatil_from = (TextView) defaultNoticeMsgView.findViewById(R.id.tv_notice_deatil_from);
					    holder.tv_notice_detail_read_count_temp = (TextView) defaultNoticeMsgView.findViewById(R.id.tv_notice_detail_read_count_temp);
					    holder.tv_replay_count_title_bb = (TextView) defaultNoticeMsgView.findViewById(R.id.tv_replay_count_title_bb);
					    holder.view_bottom=defaultNoticeMsgView.findViewById(R.id.view_bottom);
					    holder.btn_surehuizhirece = (Button) defaultNoticeMsgView.findViewById(R.id.btn_surehuizhirece);
					    defaultNoticeMsgView.setTag(holder);	
					    converView = defaultNoticeMsgView;	
					}else{
						holder = (ViewHolder)converView.getTag();
					}
				    
				    //添加数据
				    final int mPosi = posi; //如果第一个是按钮就减1 否则不需要减1
					if(posi==mCourseList.size()-1)
					{
						holder.view_bottom.setVisibility(View.VISIBLE);
					}else
					{
						holder.view_bottom.setVisibility(View.GONE);
					}
					if (LONGDELETE == 1) {
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
						int left=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 20);
			            int top=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 20);
			            int right=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 40);
			            int bottom=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 1);
						layoutParams.setMargins(-left, top, right, bottom);// 4个参数按顺序分别是左上右下
						holder.ll_notice.setLayoutParams(layoutParams);
						int paddingLeft=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 40);
						int padding=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 17);
						holder.ll_notice.setPadding(paddingLeft, padding, padding, padding);
						holder.iv_notice_delete.setVisibility(View.VISIBLE);
						holder.liner_notice.setVisibility(View.VISIBLE);
						if (mCourseList.get(mPosi).isSelected() == false) {
							holder.iv_notice_delete.setBackgroundDrawable(getResources() .getDrawable(R.drawable.send_list_off));
						} else {
							holder.iv_notice_delete.setBackgroundDrawable(getResources() .getDrawable(R.drawable.send_list_on));
						}
					} else {
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
						int left=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 20);
		                int top=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 20);
		                int right=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 0);
		                int bottom=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 1);
						layoutParams.setMargins(left, top, right, bottom);// 4个参数按顺序分别是左上右下
						int padding=DensityUtils.dip2px(B_Mess_Notice_List_WaitSure.this, 17);
						holder.ll_notice.setPadding(padding, padding, padding, padding);
						holder.ll_notice.setLayoutParams(layoutParams);
						holder.iv_notice_delete.setVisibility(View.GONE);
						holder.liner_notice.setVisibility(View.GONE);
					}

					String image_url = mCourseList.get(mPosi).getBg_img();
					if (image_url != null && image_url.length() > 0	&& !image_url.equals("")) {
						holder.iv_notice_deatil_img.setVisibility(View.VISIBLE);
						if(holder.iv_notice_deatil_img.getTag() == null){
						    PubMehods.loadBitmapUtilsPic(bitmapUtils,holder.iv_notice_deatil_img, image_url);
						    holder.iv_notice_deatil_img.setTag(image_url);
						}else{
		                    if(!holder.iv_notice_deatil_img.getTag().equals(image_url)){
		                        PubMehods.loadBitmapUtilsPic(bitmapUtils,holder.iv_notice_deatil_img, image_url);
		                        holder.iv_notice_deatil_img.setTag(image_url);
		                    }
		                }
//						imageLoader.displayImage(image_url,
//								holder.iv_notice_deatil_img, options);
						holder.tv_notice_deatil_content.setMaxLines(3);// 有图片，最多3行
					} else {
						holder.iv_notice_deatil_img.setVisibility(View.GONE);
						holder.tv_notice_deatil_content.setMaxLines(2);// 无图片，最多2行
					}

					if (mCourseList.get(mPosi).getMl_status().equals("1")) {
						holder.iv_notice_read_tag.setVisibility(View.VISIBLE);
						if (mCourseList.get(mPosi).getType().equals("4")) { //短信 
							// holder.iv_notice_type.setVisibility(View.VISIBLE);
							// holder.tv_notice_detail_title.setText("        "+
							// mCourseList.get(posi).getTitle());
							holder.tv_notice_deatil_content.setVisibility(View.VISIBLE);
							holder.tv_notice_detail_title.setText("手机短信");
							holder.tv_notice_deatil_content.setText(mCourseList.get(mPosi).getDesc());
							holder.btn_surehuizhirece.setVisibility(View.GONE);
						} else {//非短信 
							holder.tv_notice_detail_title.setText("" + mCourseList.get(mPosi).getTitle());
							holder.iv_notice_type.setVisibility(View.GONE);
							holder.tv_notice_deatil_content.setText(mCourseList.get( mPosi).getDesc());
							holder.tv_notice_deatil_content.setVisibility(View.VISIBLE);
							
							if(mCourseList.get(mPosi).getMessage_receipt() ==1){
								if(mCourseList.get(mPosi).getIs_receipt()==1){
									holder.btn_surehuizhirece.setBackgroundResource(R.drawable.btn_huizhi_yiqueren);
									holder.btn_surehuizhirece.setText("已确认");
									holder.btn_surehuizhirece.setOnClickListener(null);
								}else{
									holder.btn_surehuizhirece.setBackgroundResource(R.drawable.btn_huizhi_queren);
									holder.btn_surehuizhirece.setText("确认收到");
									holder.btn_surehuizhirece.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View arg0) {
										    if(!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)){
										        sendSureReceReq(mPosi,mCourseList.get(mPosi).getMessage_id());
										    }
										}
									});
								}
								holder.btn_surehuizhirece.setVisibility(View.VISIBLE);
							}else{
								holder.btn_surehuizhirece.setVisibility(View.GONE);
							}
							
						}
//						if (mCourseList.get(mPosi).getIs_new().equals("1")) {
//						    holder.iv_notice_read_tag.setBackgroundResource(R.drawable.icon_message_noread);
//						} else {
//							holder.iv_notice_read_tag.setBackgroundResource(R.drawable.icon_message_noread);
//						}
						holder.iv_notice_read_tag.setBackgroundResource(R.drawable.icon_message_noread);
						holder.tv_notice_detail_title.setTextColor(getResources().getColor(R.color.title_notice_title));
						holder.tv_notice_deatil_content.setTextColor(getResources().getColor(R.color.start_title_col));
					} else {
						holder.iv_notice_read_tag.setVisibility(View.GONE);
						if (mCourseList.get(mPosi).getType().equals("4")) {
							// holder.tv_notice_detail_title.setText("     " +
							// mCourseList.get(posi).getTitle());
							// holder.iv_notice_type.setVisibility(View.VISIBLE);
							// holder.tv_notice_deatil_content.setVisibility(View.GONE);
							holder.tv_notice_detail_title.setText("手机短信");
							holder.tv_notice_deatil_content.setVisibility(View.VISIBLE);
							holder.tv_notice_deatil_content.setText(mCourseList.get(mPosi).getDesc());
							holder.btn_surehuizhirece.setVisibility(View.GONE);
						} else {
							holder.tv_notice_detail_title.setText(mCourseList.get(mPosi).getTitle());
							holder.iv_notice_type.setVisibility(View.GONE);
							holder.tv_notice_deatil_content.setText(mCourseList.get(mPosi).getDesc());
							holder.tv_notice_deatil_content.setVisibility(View.VISIBLE);
							
							
							if(mCourseList.get(mPosi).getMessage_receipt() ==1){
								if(mCourseList.get(mPosi).getIs_receipt()==1){
									holder.btn_surehuizhirece.setBackgroundResource(R.drawable.btn_huizhi_yiqueren);
									holder.btn_surehuizhirece.setText("已确认");
									holder.btn_surehuizhirece.setOnClickListener(null);
								}else{
									holder.btn_surehuizhirece.setBackgroundResource(R.drawable.btn_huizhi_queren);
									holder.btn_surehuizhirece.setText("确认收到");
									holder.btn_surehuizhirece.setOnClickListener(new OnClickListener() {
										
										@Override
										public void onClick(View arg0) {
										    if(!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)){
										        sendSureReceReq(mPosi,mCourseList.get(mPosi).getMessage_id());
										    }
										}
									});
								}
								holder.btn_surehuizhirece.setVisibility(View.VISIBLE);
							}else{
								holder.btn_surehuizhirece.setVisibility(View.GONE);
							}
							
						}
						holder.tv_notice_detail_title.setTextColor(getResources().getColor(R.color.start_title_col));
						// holder.tv_notice_deatil_content.setTextColor(getResources().getColor(R.color.title_line));
					}

					holder.tv_notice_detail_time.setText(PubMehods.getFormatDate(Long.valueOf(mCourseList.get(mPosi).getCreate_time()),"MM/dd HH:mm"));

//					if (mCourseList.get(mPosi).getIs_default().equals("0")) {// 系统通知隐藏，无法点击
//						holder.tv_notice_detail_read_count_temp.setVisibility(View.VISIBLE);
//						holder.tv_replay_count_title_bb.setVisibility(View.VISIBLE);
//						// holder.tv_notice_detail_replay_count_aa.setVisibility(View.VISIBLE);
//						// holder.tv_notice_detail_read_count.setVisibility(View.VISIBLE);
//						// holder.tv_notice_detail_replay_count_aa.setText(mCourseList.get(posi).getReply_num());
//						// holder.tv_notice_detail_read_count.setText(mCourseList.get(posi).getRead_num());
//					} else {
//						holder.tv_notice_detail_read_count_temp.setVisibility(View.GONE);
//						holder.tv_replay_count_title_bb.setVisibility(View.GONE);
//						holder.tv_notice_detail_replay_count_aa.setVisibility(View.GONE);
//						holder.tv_notice_detail_read_count.setVisibility(View.GONE);
//					}

					holder.tv_notice_deatil_from.setText(mCourseList.get(mPosi).getApp_msg_sign());
					if (A_0_App.isShowAnimation == true) {
						if (mPosi > A_0_App.notice_list_curPosi) {
							A_0_App.notice_list_curPosi = mPosi;
							Animation an = new TranslateAnimation(
									Animation.RELATIVE_TO_SELF, 1,
									Animation.RELATIVE_TO_SELF, 0,
									Animation.RELATIVE_TO_SELF, 0,
									Animation.RELATIVE_TO_SELF, 0);
							an.setDuration(400);
							an.setStartOffset(20 * mPosi);
							converView.startAnimation(an);
						}
					}
					holder.liner_checkbox.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							
							if (mCourseList.get(mPosi).isSelected() == false) {
								mCourseList.get(mPosi).setSelected(true);
							} else {
								mCourseList.get(mPosi).setSelected(false);
							}
							int temp = 0;
							select.setText(temp + "");
							for (int i = 0; i < mCourseList.size(); i++) {
								if (mCourseList.get(i).isSelected() == true) {
									temp = temp + 1;
									select.setText(temp + "");
								}
							}

							adapter.notifyDataSetChanged();

						}
					});
					holder.ll_notice.setOnLongClickListener(new OnLongClickListener() {

						@Override
						public boolean onLongClick(View arg0) {
							LONGDELETE = 1;
							rel_notice_operating.setVisibility(View.VISIBLE);
							adapter.notifyDataSetChanged();
							showTitleBt(ZUI_RIGHT_TEXT, true);
							setZuiYouText("全选");
							isMultiSelectStatus = true;
							return false;
						}
					});
					// holder.ll_notice.setBackgroundResource(R.drawable.notice_list_shape);
					holder.ll_notice.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							isHasUpdata = true;
							click_posi = mPosi;
							// holder.ll_notice.setBackgroundResource(R.drawable.notice_list_press_shape);
							Intent intent = new Intent(B_Mess_Notice_List_WaitSure.this,B_Mess_Notice_Detail_Teacher.class);
							intent.putExtra("acy_type", 2);// 正常列表进入
							intent.putExtra("message_id", mCourseList.get(mPosi).getMessage_id());
							intent.putExtra("mess_content",mCourseList.get(mPosi).getDesc());
							startActivityForResult(intent, 1);
						}
					});
					
				    
			}

			return converView;
		}

	}

	class ViewHolder {

		RelativeLayout liner_notice;
		LinearLayout ll_notice;
		ImageView iv_notice_delete, liner_checkbox;
		ImageView iv_notice_read_tag;
		ImageView iv_notice_deatil_img;
		TextView iv_notice_type;
		TextView tv_notice_detail_title;
		TextView tv_notice_detail_time;
		TextView tv_notice_detail_replay_count_aa;
		TextView tv_notice_detail_read_count;
		TextView tv_notice_deatil_content;
		TextView tv_notice_deatil_from;

		TextView tv_notice_detail_read_count_temp;
		TextView tv_replay_count_title_bb;
		View view_bottom;
		
		Button  btn_surehuizhirece;//回执按钮
	}
	
	//共有x条待确认信息按钮item
	class ViewHolder2 {

		Button btn_waitsurenotice;
	}
	
	/**
	 * 发送确认收到请求
	 */
	private void sendSureReceReq(final int posi,String msgid){
		A_0_App.getInstance().showProgreDialog(B_Mess_Notice_List_WaitSure.this, "", true);
		
		isHasUpdata = true;
		A_0_App.getApi().sendNoticeSureReceIpt(B_Mess_Notice_List_WaitSure.this,
				A_0_App.USER_TOKEN, msgid,
				new SureReceIptCallBack() {
					@Override
					public void onSuccess() {
						if (isFinishing())
							return;
						havaSuccessLoadData = true;
						A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_List_WaitSure.this);
						mCourseList.get(posi).setIs_receipt(1);
//						mCurrentWaitSureNoticeNumber--;
						adapter.notifyDataSetChanged();
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
                        //new add
                        PubMehods.showToastStr(B_Mess_Notice_List_WaitSure.this, msg);
                        A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_List_WaitSure.this);
                        

                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

		
	}
	
	
	/**
	 * 发送确认收到请求
	 */
	private void sendAllSureReceReq(){
		A_0_App.getInstance().showProgreDialog(B_Mess_Notice_List_WaitSure.this, "", true);
		
		isHasUpdata = true;
		A_0_App.getApi().sendAllNoticeSureReceIpt(B_Mess_Notice_List_WaitSure.this,
				A_0_App.USER_TOKEN,level,
				new SureReceIptCallBack() {
					@Override
					public void onSuccess() {
						if (isFinishing())
							return;
						havaSuccessLoadData = true;
						A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_List_WaitSure.this);
//						mCurrentWaitSureNoticeNumber--;
//						adapter.notifyDataSetChanged();
						have_read_page=1;
						updateInfo();
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

                        //new add
                        PubMehods.showToastStr(B_Mess_Notice_List_WaitSure.this, msg);
                        A_0_App.getInstance().CancelProgreDialog(B_Mess_Notice_List_WaitSure.this);
                        

                    }

                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

		
	}

	private void clearBusinessList(boolean setNull) {
		if (mCourseList != null) {
			mCourseList.clear();
			if (setNull)
				mCourseList = null;
		}
	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			Intent intent = new Intent();
            intent.putExtra("isNeedRefreshData", isHasUpdata);
			this.setResult(2, intent);
			this.finish();
			break;
		case ZUI_RIGHT_TEXT:
			if(isMultiSelectStatus){
				if (LONGDELETE == 1) {
					for (int i = 0; i < mCourseList.size(); i++) {
						mCourseList.get(i).setSelected(true);
					}
					int temp = 0;
					for (int i = 0; i < mCourseList.size(); i++) {
						if (mCourseList.get(i).isSelected() == true) {
							temp = temp + 1;
							select.setText(temp + "");
						}
					}
					adapter.notifyDataSetChanged();
				} else {
					batchUpdate(level);
				}
			}else{
							
				final GeneralDialog upDateDialog = new GeneralDialog(B_Mess_Notice_List_WaitSure.this, R.style.Theme_GeneralDialog);
				upDateDialog.setTitle(R.string.pub_title);
				upDateDialog.setContent("你要全部确认这些通知信息吗？");
				upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
					@Override
					public void onClick(View v) {
						upDateDialog.cancel();
					}
				});
				upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
					@Override
					public void onClick(View v) {																
						sendAllSureReceReq();
						upDateDialog.cancel();
					}
					
				});

				upDateDialog.show();
			}
			

			break;
		default:
			break;
		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
		    Boolean haveReceipted = data.getExtras().getBoolean("haveReceipted");
//			String comment_count = data.getExtras().getString("read_count");
//			String join_count = data.getExtras().getString("repley_count");
//			if (!"".equals(comment_count)) {
				if (requestCode == 1) {
                if (mCourseList != null && mCourseList.size() > 0 && click_posi < mCourseList.size()) {
//    					mCourseList.get(click_posi).setRead_num(comment_count);
//    					mCourseList.get(click_posi).setReply_num(join_count);
    					mCourseList.get(click_posi).setMl_status("0");
                        if (haveReceipted != null && haveReceipted) {
                            mCourseList.get(click_posi).setIs_receipt(1);
                        }
                        adapter.notifyDataSetChanged();
                        judge_read(mCourseList,firsetEnterLoader);
                        
                        getNoticeListData(level, 1, false, true);//缓存第一页数据
				    }
//				}
			}
		}
	}

	private void updateInfo() {
		MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
		updateLectureInfo.execute();
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {
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

			getNoticeListData(level, have_read_page, false,false);
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(Integer integer) {
			// logD("上传融云数据完毕");
		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}

	/**
	 * 设置连接状态变化的监听器.
	 */
	public void startListtenerRongYun() {
		RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
	}

	private class MyConnectionStatusListener implements
			RongIMClient.ConnectionStatusListener {
		@Override
		public void onChanged(ConnectionStatus connectionStatus) {

			switch (connectionStatus) {
			case CONNECTED:// 连接成功。
				A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
				break;
			case DISCONNECTED:// 断开连接。
				A_Main_My_Message_Acy
						.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
//				A_0_App.getInstance().showExitDialog(B_Mess_Notice_List.this,
//						getResources().getString(R.string.token_timeout));
				break;
			case CONNECTING:// 连接中。
				A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
				break;
			case NETWORK_UNAVAILABLE:// 网络不可用。
				A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
				break;
			case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
				A_Main_My_Message_Acy
						.logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
				class LooperThread extends Thread {
					public void run() {
						Looper.prepare();
						A_0_App.getInstance().showExitDialog(
								B_Mess_Notice_List_WaitSure.this,
								AppStrStatic.kicked_offline());
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
    protected void onResume() {
        if(firsetEnterLoader){
            firsetEnterLoader = false;
        }else{//第一次加载
            
        }
        super.onResume();
    }

	@Override
	protected void onDestroy() {
		clearBusinessList(true);
		adapter = null;
		// bitmapUtils=null;
		drawable.stop();
		drawable=null;
		super.onDestroy();
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				
				if (LONGDELETE == 1) {
//					showTitleBt(ZUI_RIGHT_TEXT, false);
					//显示右上角全部确认按钮
        			showTitleBt(ZUI_RIGHT_TEXT, true);
                    setZuiYouText("全部确认");
                    isMultiSelectStatus = false;
                    
					for (int i = 0; i < mCourseList.size(); i++) {
						mCourseList.get(i).setSelected(false);
					}
					select.setText("0");
					LONGDELETE = 0;
					judge_read(mCourseList,firsetEnterLoader);
					rel_notice_operating.setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
				} else {
					Intent intent = new Intent();
	                intent.putExtra("isNeedRefreshData", isHasUpdata);
					this.setResult(2, intent);
					this.finish();
				}

				return true;
			default:
				break;
			}
		}
		return super.dispatchKeyEvent(event);
	}
}
