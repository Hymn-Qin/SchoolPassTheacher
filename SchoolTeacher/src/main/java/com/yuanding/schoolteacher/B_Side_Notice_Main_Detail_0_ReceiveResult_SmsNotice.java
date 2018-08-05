package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_NoticeReceiverResult_AppNotice_SuccessOrFailure;
import com.yuanding.schoolteacher.bean.Cpk_NoticeReceiverResult_AppNotice_UserInfo;
import com.yuanding.schoolteacher.service.Api.AppNotice_InviteInstallAppCallBack;
import com.yuanding.schoolteacher.service.Api.AppNotice_ReceResult_SmsNoticeSuccessOrFailure_CallBack;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.CircleImageView;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshGridView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;


/**
 * 通知接收结果页面（身边-》通知列表--》通知详情--》手机短信通知接收结果
 * @author Administrator
 *
 */
public class B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice extends A_0_CpkBaseTitle_Navi{

	 Context mContext;
	 PullToRefreshGridView  mgv_noticereceiver;
	 TextView tx_read_or_unread_num;
	 EditText edit_receiverresult_userkey; 
	 
	 private List<Cpk_NoticeReceiverResult_AppNotice_UserInfo> list_lack;
	 MyGridView_NoticeReceiverResult_Aapter mread_or_unreadAdapter;
	 
	 protected ImageLoader imageLoader;
	 private DisplayImageOptions options;	 
	 
	 String mCurrentPageType; // 1: 接收成功 ；2 接收失败 ；除了1和2是后来改的短信发送失败列表类型页面跳转进来的
	 String messageId;// 消息id
	 
	 private int have_read_page = 1;// 已经读的页数
	 
	 /**
	  * 加载动画
	  */
	 private View mLinerReadDataError, mLinerNoContent, mWholeView,message_notice_info_loading;
	 TextView tv_blank_name; //无内容提示
	 
	 /**
	  * 缓存
	  */
	 private ACache maACache;
	 private JSONObject jsonObject;
	 private long mSystemTime;
	 
	 /**
	   * 新增下拉使用
	  */
	 private SimpleSwipeRefreshLayout   demo_swiperefreshlayout;
	 private int repfresh=0;//避免下拉和上拉冲突
	 
	 boolean isFirstLoad = true;
	 
	 private LinearLayout home_load_loading;
	 private AnimationDrawable drawable;
//	 boolean isAllowInvative = false;
	 private String title;
	   private boolean havaSuccessLoadData = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_notice_detail_receiverresult);
		mContext = this;
		
		mCurrentPageType = getIntent().getExtras().getString("acy_type");;
		messageId = getIntent().getExtras().getString("messageId");
		
		if(mCurrentPageType.equals("1")){
			setTitleText("接收成功");
		}else if(mCurrentPageType.equals("2")){
			setTitleText("接收失败");
		}else {
			title=getIntent().getExtras().getString("title");
			setTitleText(title);
		}
		
		initView();		
		
		readCache();
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
	private void readCache() {
		// TODO Auto-generated method stub
		maACache = ACache.get(this);
			jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_appnotice_success_or_failure + mCurrentPageType + messageId + A_0_App.USER_UNIQID);
		if (jsonObject != null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
		}else{
		updateInfo();}
	}
	
	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			havaSuccessLoadData = true; 
			int state = jsonObject.optInt("status");
			List<Cpk_NoticeReceiverResult_AppNotice_UserInfo> mlistContact = new ArrayList<Cpk_NoticeReceiverResult_AppNotice_UserInfo>();
			if (state == 1) {
				mlistContact=JSON.parseArray(jsonObject.optJSONArray("list")+"",Cpk_NoticeReceiverResult_AppNotice_UserInfo.class);
			}
			if (isFinishing()){
				return;
			}
							
			//设置顶部文字内容			
			
			Cpk_NoticeReceiverResult_AppNotice_SuccessOrFailure mSmsNoticeSuccessOrFailure = new Cpk_NoticeReceiverResult_AppNotice_SuccessOrFailure();
			int sms_count = jsonObject.optInt("sms_count");
			int app__num = jsonObject.optInt("app_num");
			mSmsNoticeSuccessOrFailure.setSms_count(sms_count);
			mSmsNoticeSuccessOrFailure.setApp_num(app__num);
			setTopTextContent(mSmsNoticeSuccessOrFailure);
			
			//设置列表内容
			if (mlistContact != null && mlistContact.size() > 0) {
				
				clearBusinessList(false);				
				list_lack = mlistContact;
				mread_or_unreadAdapter.notifyDataSetChanged();
				
				showLoadResult(false,true, false, false);
			} else {
				showLoadResult(false,false, false, true);
//				PubMehods.showToastStr(B_Side_Notice_Main_Detail_0_ReceiveResult_AppNotice.this,"没有内容");
			}
			mSystemTime=jsonObject.getLong("time");
			demo_swiperefreshlayout.setRefreshing(false);  
			repfresh=0;
			if(null!=mgv_noticereceiver){
				mgv_noticereceiver.onRefreshComplete();
				mgv_noticereceiver.setMode(Mode.PULL_UP_TO_REFRESH);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	private void clearBusinessList(boolean setNull) {
		if (list_lack != null && list_lack.size() > 0) {
			list_lack.clear();
			if (setNull)
				list_lack = null;
		}
	}	
	
	@SuppressWarnings("unchecked")
	private void initView() {
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		mgv_noticereceiver = (PullToRefreshGridView)findViewById(R.id.mgv_noticereceiver);
		tx_read_or_unread_num = (TextView)findViewById(R.id.tx_read_or_unread_num);
		edit_receiverresult_userkey = (EditText)findViewById(R.id.edit_receiverresult_userkey);
		
		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(
				R.drawable.icon_friend_image4,
				R.drawable.icon_friend_image8,
				R.drawable.icon_friend_image6);
		
		list_lack = new ArrayList<Cpk_NoticeReceiverResult_AppNotice_UserInfo>();
		mread_or_unreadAdapter = new MyGridView_NoticeReceiverResult_Aapter();
		mgv_noticereceiver.setAdapter(mread_or_unreadAdapter);
		
		mgv_noticereceiver.setMode(Mode.BOTH);
		
		mgv_noticereceiver.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				//下拉刷新的时候 先判断输入框有没有关键词 有关键词的话 就根据这个关键词去请求 否则就请求第一页				
//				if (edit_receiverresult_userkey.getText().toString().length() > 0) {
//					have_read_page = 1;
//					searchKeyWordData(edit_receiverresult_userkey.getText().toString());
//				}else{
//					have_read_page = 1;
//					startReadData();
//				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				
				if (repfresh==0) {
					repfresh=1;
					demo_swiperefreshlayout.setEnabled(false);
					demo_swiperefreshlayout.setRefreshing(false);  
					
					//加载更多的时候 先判断输入框有没有关键词 有关键词的话 就根据这个关键词去请求下一页 否则就按照没有关键词去请求		
					have_read_page++;
					if (edit_receiverresult_userkey.getText().toString().length() > 0) {		
						getMoreData(have_read_page,edit_receiverresult_userkey.getText().toString());
					}else{
						getMoreData(have_read_page,"");
					}
				}				
				
				
				
			}
		});	
		
		/**
		 * 新增下拉使用 new add
		 */
		demo_swiperefreshlayout.setSize(SwipeRefreshLayout.DEFAULT);
//		demo_swiperefreshlayout.setProgressViewEndTarget(scale, end)
		demo_swiperefreshlayout.setColorSchemeResources(R.color.main_color);
		if (repfresh == 0) {
			repfresh = 1;
			if(null!=mgv_noticereceiver){
				mgv_noticereceiver.onRefreshComplete();
			}
			demo_swiperefreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {
							//下拉刷新的时候 先判断输入框有没有关键词 有关键词的话 就根据这个关键词去请求 否则就请求第一页				
							if (edit_receiverresult_userkey.getText().toString().length() > 0) {
								have_read_page = 1;
//								searchKeyWordData(edit_receiverresult_userkey.getText().toString());
								startReadData(edit_receiverresult_userkey.getText().toString());
							}else{
								have_read_page = 1;
								startReadData("");
							}
							
							if(null!=mgv_noticereceiver){
								mgv_noticereceiver.setMode(Mode.DISABLED);
							}

						};
					});
			
		}
		
		mgv_noticereceiver.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {				

				if (mgv_noticereceiver.getRefreshableView().getFirstVisiblePosition() <= 1) {
					final View firstVisibleChild = mgv_noticereceiver.getRefreshableView().getChildAt(0);
					if (firstVisibleChild != null) {
						if( firstVisibleChild.getTop() >= mgv_noticereceiver.getRefreshableView().getTop()){
							  demo_swiperefreshlayout.setEnabled(true);
						}else{
							 demo_swiperefreshlayout.setEnabled(false);
						};
					}else{
						 demo_swiperefreshlayout.setEnabled(false);
					}
				}else{
					  demo_swiperefreshlayout.setEnabled(false);
				}
				
//				 if (demo_swiperefreshlayout!=null&&mgv_noticereceiver.getChildCount() > 0 && mgv_noticereceiver.getRefreshableView().getFirstVisiblePosition() == 0
//			                && mgv_noticereceiver.getChildAt(0).getTop() >= mgv_noticereceiver.getPaddingTop()) {
//			            //解决滑动冲突，当滑动到第一个item，下拉刷新才起作用
//					   demo_swiperefreshlayout.setEnabled(true);
//			        } else {
//			        	demo_swiperefreshlayout.setEnabled(false);
//			        }
				
			
			}
			
			@Override
			public void onScroll(AbsListView arg0, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				
			}
		});
		
	      //**************************新增到这**********************
		
		edit_receiverresult_userkey.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

					if (edit_receiverresult_userkey.getText().toString().length() > 0) {
						have_read_page = 1;
						searchKeyWordData(edit_receiverresult_userkey.getText().toString());
					}else{
						have_read_page = 1;
						searchKeyWordData("");
					}
					
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					
				}
			});
		
				
		/**
		 * 加载动画
		 */
		mLinerReadDataError = findViewById(R.id.message_notice_load_error);
		message_notice_info_loading = findViewById(R.id.message_notice_info_loading);
		mLinerNoContent = findViewById(R.id.message_notice_no_content);
		mWholeView = findViewById(R.id.liner_notice_list_whole_view);
		tv_blank_name = (TextView)findViewById(R.id.tv_blank_name);
		tv_blank_name.setText("还是空空的");
		mLinerReadDataError.setOnClickListener(onClick);



		home_load_loading = (LinearLayout) message_notice_info_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
	}
	

	/**
	 * 请求首页或下拉刷新 请求数据
	 */
	private void startReadData(String keyword) {
        A_0_App.getApi().getNoticeReceiverResult_AppNoticeSuccessOrFailure(A_0_App.USER_TOKEN, mCurrentPageType,messageId, keyword, 1, new AppNotice_ReceResult_SmsNoticeSuccessOrFailure_CallBack() {
			
			@Override
			public void onSuccess(Cpk_NoticeReceiverResult_AppNotice_SuccessOrFailure mAppNoticeSuccessOrFailure) {
                if (isFinishing())
                    return;
                havaSuccessLoadData = true; 
				if(null!=mAppNoticeSuccessOrFailure){
					/**
					 * 判断列表是否有数据
					 */
					if(null!=mAppNoticeSuccessOrFailure.getUserlist() && mAppNoticeSuccessOrFailure.getUserlist().size()>0){
						
						list_lack = mAppNoticeSuccessOrFailure.getUserlist();
						mread_or_unreadAdapter.notifyDataSetChanged();
						
						//有数据  那么显示数据
						showLoadResult(false,true, false, false);
						
						//除了接收成功页面，失败的三个页面都显示邀请安装按钮
						if(!mCurrentPageType.equals("1")){
							//显示右上角邀请安装app按钮
							showTitleBt(ZUI_RIGHT_TEXT, true);
							setZuiYouText(mContext.getResources().getString(R.string.str_my_side_appnotice_receiverresult_btn));
//							if(1==mAppNoticeSuccessOrFailure.getAllow_invite()){
//								isAllowInvative = true;
//							}else{
//								isAllowInvative = false;
//							}
						}						
						
						
						if(!isFirstLoad){
							PubMehods.showToastStr(mContext, "刷新成功");							
						}
						isFirstLoad = false;
						
						
					}else{						
						showLoadResult(false,false, false, true);
						
					}
					

					/**
					 * 设置顶部文字
					 */
					setTopTextContent(mAppNoticeSuccessOrFailure);
										
					
				}
				
				demo_swiperefreshlayout.setRefreshing(false);  
				repfresh=0;
				if(null!=mgv_noticereceiver){
					mgv_noticereceiver.onRefreshComplete();
					mgv_noticereceiver.setMode(Mode.PULL_UP_TO_REFRESH);
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
                if(!havaSuccessLoadData){
                    showLoadResult(false,false, true, false);
                }else{
                List<Cpk_NoticeReceiverResult_AppNotice_UserInfo>   mlistContact=JSON.parseArray(jsonObject.optJSONArray("list")+"",Cpk_NoticeReceiverResult_AppNotice_UserInfo.class);
                if (mlistContact.size()==0) {
                    showLoadResult(false,false, false, true);
                }
                }
                
//              A_0_App.getInstance().CancelProgreDialog(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this);
                PubMehods.showToastStr(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this, msg);
                if(null!=mgv_noticereceiver){
                    mgv_noticereceiver.onRefreshComplete();
                }
                
                demo_swiperefreshlayout.setRefreshing(false);  
                repfresh=0;
                if(null!=mgv_noticereceiver){
                    mgv_noticereceiver.onRefreshComplete();
                    mgv_noticereceiver.setMode(Mode.PULL_UP_TO_REFRESH);
                }
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	
	/**
	 * 加载更多（有或没有关键词）
	 * @param page_no
	 */
	private void getMoreData(int page_no,String keyword) {
        A_0_App.getApi().getNoticeReceiverResult_AppNoticeSuccessOrFailure(A_0_App.USER_TOKEN, mCurrentPageType,messageId, "", page_no, new AppNotice_ReceResult_SmsNoticeSuccessOrFailure_CallBack() {
			
			@Override
			public void onSuccess(Cpk_NoticeReceiverResult_AppNotice_SuccessOrFailure mAppNoticeUnRead) {
                if (isFinishing())
                    return;
				if(null!=mAppNoticeUnRead){
					/**
					 * 判断列表是否有数据
					 */
					if(null!=mAppNoticeUnRead.getUserlist() && mAppNoticeUnRead.getUserlist().size()>0){
						if(null!=list_lack && null!=mread_or_unreadAdapter){
							list_lack.addAll(mAppNoticeUnRead.getUserlist());
							mread_or_unreadAdapter.notifyDataSetChanged();
							setTopTextContent(mAppNoticeUnRead);
						}												
												
					}else{
						PubMehods.showToastStr(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this,"没有更多了");
					}
										
					
				}
				
				if(null!=mgv_noticereceiver){
					mgv_noticereceiver.onRefreshComplete();
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
//              A_0_App.getInstance().CancelProgreDialog(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this);
                PubMehods.showToastStr(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this, msg);
                
                if(null!=mgv_noticereceiver){
                    mgv_noticereceiver.onRefreshComplete();
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
	 * 搜索
	 * @param keyword
	 * @param pageno
	 */
	private void searchKeyWordData(final String keyword) {
        A_0_App.getApi().getNoticeReceiverResult_AppNoticeSuccessOrFailure(A_0_App.USER_TOKEN, mCurrentPageType,messageId, keyword, 1, new AppNotice_ReceResult_SmsNoticeSuccessOrFailure_CallBack() {
			
			@Override
			public void onSuccess(Cpk_NoticeReceiverResult_AppNotice_SuccessOrFailure mSmsNoticeSuccessOrFailure) {
                if (isFinishing())
                    return;
				if(null!=mSmsNoticeSuccessOrFailure){
					/**
					 * 判断列表是否有数据
					 */
					if(null!=mSmsNoticeSuccessOrFailure.getUserlist() && mSmsNoticeSuccessOrFailure.getUserlist().size()>0){
						
						list_lack = mSmsNoticeSuccessOrFailure.getUserlist();
						mread_or_unreadAdapter.notifyDataSetChanged();
						
						//不是加载更多的时候（请求首页数据或搜索的时候） 有数据  那么显示数据
						showLoadResult(false,true, false, false);		
					}else{						

						//搜索关键词 没有搜索到数据 也更新列表
						list_lack = new ArrayList<Cpk_NoticeReceiverResult_AppNotice_UserInfo>();
						mread_or_unreadAdapter.notifyDataSetChanged();
						list_lack = mSmsNoticeSuccessOrFailure.getUserlist();
						mread_or_unreadAdapter.notifyDataSetChanged();
						
					}
					

					/**
					 * 设置顶部文字
					 */
					setTopTextContent(mSmsNoticeSuccessOrFailure);
					
					if(null!=mgv_noticereceiver){
						mgv_noticereceiver.onRefreshComplete();
					}
					
					
				}else{
					if(null!=mgv_noticereceiver){
						mgv_noticereceiver.onRefreshComplete();
					}
				}
				
				demo_swiperefreshlayout.setRefreshing(false);  
				repfresh=0;
				if(null!=mgv_noticereceiver){
					mgv_noticereceiver.onRefreshComplete();
					mgv_noticereceiver.setMode(Mode.PULL_UP_TO_REFRESH);
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
                if(jsonObject==null){
                    showLoadResult(false,false, true, false);
                }
                
//              A_0_App.getInstance().CancelProgreDialog(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this);
                PubMehods.showToastStr(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this, msg);
                if(null!=mgv_noticereceiver){
                    mgv_noticereceiver.onRefreshComplete();
                }
                
                demo_swiperefreshlayout.setRefreshing(false);  
                repfresh=0;
                if(null!=mgv_noticereceiver){
                    mgv_noticereceiver.onRefreshComplete();
                    mgv_noticereceiver.setMode(Mode.PULL_UP_TO_REFRESH);
                }
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	
	/**
	 * 设置顶部文字内容
	 */
	private void setTopTextContent(Cpk_NoticeReceiverResult_AppNotice_SuccessOrFailure mSmsNoticeSuccessOrFailure){
		
		int sms_count = mSmsNoticeSuccessOrFailure.getSms_count();
		int app_num = mSmsNoticeSuccessOrFailure.getApp_num();
		Integer sms_count_numInt = sms_count;
		Integer app_num_numInt = app_num;
		if(mCurrentPageType.equals("1")){  //接收成功页面
												
			//更改tx_detailrule的部分文本颜色
			String text_front = "当前 ";
			String text_middle = " 人短信接收成功   有 ";
			String text_last = " 人APP接收成功";
			SpannableStringBuilder builder = new SpannableStringBuilder(text_front+sms_count_numInt+text_middle+app_num_numInt+text_last);  	
			
			//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
			ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.main_color));  
			builder.setSpan(greenSpan2, text_front.length(), text_front.length()+sms_count_numInt.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			
			ForegroundColorSpan greenSpan3 = new ForegroundColorSpan(getResources().getColor(R.color.main_color));  
			builder.setSpan(greenSpan3, text_front.length()+sms_count_numInt.toString().length()+text_middle.length(), text_front.length()+sms_count_numInt.toString().length()+text_middle.length()+app_num_numInt.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			
			tx_read_or_unread_num.setText(builder); 
		}else {   //接收失败的三个页面
									
			//更改tx_detailrule的部分文本颜色
			String text_front = "当前 ";
			String text_middle = " 人短信接收失败   有 ";
			String text_last = " 人未安装APP";
            SpannableStringBuilder builder = new SpannableStringBuilder(text_front+sms_count_numInt+text_middle+app_num_numInt+text_last);  	
			
			//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
			ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.red2));  
			builder.setSpan(greenSpan2, text_front.length(), text_front.length()+sms_count_numInt.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			
			ForegroundColorSpan greenSpan3 = new ForegroundColorSpan(getResources().getColor(R.color.red2));  
			builder.setSpan(greenSpan3, text_front.length()+sms_count_numInt.toString().length()+text_middle.length(), text_front.length()+sms_count_numInt.toString().length()+text_middle.length()+app_num_numInt.toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			
			tx_read_or_unread_num.setText(builder); 
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
        	have_read_page = 1;
        	startReadData("");
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
	
	/**
	 * 已读 或 未读 头像列表
	 * @author Administrator
	 *
	 */
	public class MyGridView_NoticeReceiverResult_Aapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (list_lack != null)
				return list_lack.size();
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
				converView = LayoutInflater.from(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this).inflate(R.layout.item_notice_receiverresult_userhead, null);
				holder.ivHead = (CircleImageView) converView.findViewById(R.id.iv_contact_por);
				holder.tv_username = (TextView) converView.findViewById(R.id.tv_username);
				holder.tv_recestatus = (TextView) converView.findViewById(R.id.tv_recestatus);
				converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
			
			
			if(null!=list_lack && list_lack.size()>0 && null!=holder){
				PubMehods.loadServicePic(imageLoader,list_lack.get(posi).getPhoto_url(), holder.ivHead,options);
				holder.tv_username.setText(list_lack.get(posi).getTrue_name());
				
				//无论接收成功还是失败的三个页面 用户列表都可以显示未安装APP
				if(2!=list_lack.get(posi).getStatus()){
					//更改tx_detailrule的部分文本颜色
					SpannableStringBuilder builder = new SpannableStringBuilder("未安装APP");  		  
					//ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色    
					ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(getResources().getColor(R.color.red2));  
					builder.setSpan(greenSpan2, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
					holder.tv_recestatus.setText(builder); 
					holder.tv_recestatus.setVisibility(View.VISIBLE);
				}else{
					holder.tv_recestatus.setVisibility(View.GONE);
				}
							
				converView.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(mContext, B_Mess_Persion_Info.class);
						intent.putExtra("uniqid", list_lack.get(posi).getUniqid());
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
     * @Title: sendInviteApp
     * @Description: 
     * @return void 返回类型
     */
    private void sendInviteApp() {
        A_0_App.getApi().send_AppNoticeInviteInstall_Req(messageId, A_0_App.USER_TOKEN, new AppNotice_InviteInstallAppCallBack() {
            @Override
            public void onSuccess(String message) {
                if (isFinishing())
                    return;
                PubMehods.showToastStr(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this, message);
//                isAllowInvative = false;
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
//                A_0_App.getInstance().CancelProgreDialog(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this);
                PubMehods.showToastStr(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this, msg);

            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
	
	
	
	
	/**
	 * 
	 * @param loading
	 * @param list
	 * @param loadFaile
	 * @param noData
	 */
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
		
		if (loading) {
			drawable.start();
			message_notice_info_loading.setVisibility(View.VISIBLE);
		} else {
			if (drawable != null) {
				drawable.stop();
			}
			message_notice_info_loading.setVisibility(View.GONE);
		}
	}

	// 数据加载，及网络错误提示
	OnClickListener onClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.message_notice_load_error:
					showLoadResult(true,false, false, false);
					have_read_page = 1;
					startReadData("");
					break;
				default:
					break;
				}
			}
		};
	
	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
        case BACK_BUTTON:
        	goData();
            finish();
            overridePendingTransition(R.anim.animal_push_right_in_normal,
					R.anim.animal_push_right_out_normal);
            break;  
        case ZUI_RIGHT_TEXT:
        	//邀请安装APP
        	if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_MAIN_TIME)) {
//        		if(isAllowInvative){
//        			showDialog();
//        		}else{
//        			PubMehods.showToastStr(mContext, "您已邀请过");
//        		}
        		
        		showDialog();
        	}
        	break;
        default:
            break;}
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
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				goData();
				finish();
				overridePendingTransition(R.anim.animal_push_right_in_normal,
						R.anim.animal_push_right_out_normal);
				return true;
			default:
				break;
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	private void goData() {/*
		 * Intent it = new Intent();
		 * it.putExtra("comment_count"
		 * ,detail_Notice.getComment_num());
		 * it.putExtra("join_count",
		 * detail_Notice.getJoin_num()); setResult(1, it);
		 */
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Notice_Main.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.this, AppStrStatic.kicked_offline());
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
    	// TODO Auto-generated method stub
    	drawable.stop();
    	drawable=null;
    	super.onDestroy();
    }

}
