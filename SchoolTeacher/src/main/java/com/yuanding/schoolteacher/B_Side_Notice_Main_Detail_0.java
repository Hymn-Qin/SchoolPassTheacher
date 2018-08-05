package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Info_Detail_Content_More;
import com.yuanding.schoolteacher.bean.Cpk_Side_Notice_Comment_detail;
import com.yuanding.schoolteacher.bean.Cpk_Side_Notice_Detail;
import com.yuanding.schoolteacher.service.Api;
import com.yuanding.schoolteacher.service.Api.InterAcy_Apply;
import com.yuanding.schoolteacher.service.Api.InterAdd_Acy_Comment;
import com.yuanding.schoolteacher.service.Api.InterSideNoticeCommentList;
import com.yuanding.schoolteacher.service.Api.InterSideNoticeDetail;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.FileSizeUtils;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.CircleImageView;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.yuanding.schoolteacher.view.VerticalImageSpan;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月24日 下午2:47:12 通知详情(已发送未发送草稿箱)
 */
public class B_Side_Notice_Main_Detail_0 extends A_0_CpkBaseTitle_Navi {

	private View side_acy_detail_load_error, liner_acy_detail_whole,side_acy_detail_loading;
	private LinearLayout liner_notice_detail_send;
	private ImageView iv_acy_detail_banner, iv_acy_comment_send;
	private TextView tv_acy_detail_title, tv_acy_detail_time,tv_acy_detail_from, tv_detail_comment_count,tv_detail_sent_count_sms,tv_detail_re_count_sms;
	private TextView tv_act_detail_time, tv_act_detail_place;
	private Button btn_sign_up_me;
	
	private LinearLayout layout_read_or_unread; //已读未读按钮container
	private Button btn_unread,btn_readed;
	private Button btn_sending; // 正在发送灰色按钮

	private LinearLayout liner_notice_detail_container;
	private WebView wv_acy_detail;
	private TextView tv_acy_detail;
	private PullToRefreshListView lv_act_detail_comment;
	private EditText tv_acy_detail_comment_content;

	private Mydapter adapter;
	private String acy_detail_id;
	private List<Cpk_Side_Notice_Comment_detail> list;
	private Cpk_Side_Notice_Detail detail_Notice;

	protected ImageLoader imageLoader, imageLoaderBanner;
	private DisplayImageOptions options, optionsBanner;
	protected ImageOptions bitmapUtils, bitmapUtilsBanner;
	//private SolveClashListView solveClashListView;

	private int have_read_page = 2;// 已经读的页数
	private int acy_type;//页面类型    1:转发,2：正常列表进入 ，3推送：正常的首页消息推送,推送4:校务助手消息推送
	private boolean readCommentData = false;
	private String type;
	private LinearLayout liner_detail,liner_detail_sms;
	private JSONObject jsonObject;
	private ACache maACache;
	private String notice_type;
	private LinearLayout tv_notice_detail_comment_count;
	private RelativeLayout rela_notice_detail;
	private static long severTime=0;
	private String logid="0",comment_id="",school_notice ="";
	private Boolean firstLoad = false;
	private LinearLayout home_load_loading;
	private AnimationDrawable drawable;

	/**
	 * 附件
	 */
	private LinearLayout tv_notice_detail_comment_other;
	private ImageView fileImage;
	private TextView fileName, fileDet;
	private ProgressBar fileProgressBar;
	private RelativeLayout mess_notice_detail_load_other;
	private String file_url;
	private String file_name;
	private String file_ext;
	private long file_size;
	private String BASE_PATH=android.os.Environment.getExternalStorageDirectory()+ AppStrStatic.SD_PIC+"/";
	boolean DonDownload = false;
	double process;
	/**
	 * 新增下拉使用
	 */
	private SimpleSwipeRefreshLayout demo_swiperefreshlayout;
	private int repfresh = 0;// 避免下拉和上拉冲突
	private boolean havaSuccessLoadData = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_notice_detail);

		firstLoad = true;
		showTitleBt(ZUI_RIGHT_BUTTON, false);
		setZuiRightBtn(R.drawable.navigationbar_more_button);
		setTitleText("通知详情");
		acy_type = getIntent().getExtras().getInt("acy_type");
		type = getIntent().getExtras().getString("type");
		notice_type=getIntent().getExtras().getString("notice_type");
        //页面类型    1:转发,2：正常列表进入 ，3推送：正常的首页消息推送,推送4:校务助手消息推送
        if (acy_type == 1) {
            acy_detail_id = getIntent().getExtras().getString("acy_detail_id");
        } else if (acy_type == 2) {
            acy_detail_id = getIntent().getExtras().getString("acy_detail_id");
            school_notice = getIntent().getExtras().getString("school_notice");
        } else if (acy_type == 3) {
            acy_detail_id =PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
        } else if (acy_type == 4) {
            acy_detail_id = getIntent().getExtras().getString("acy_detail_id");
            school_notice = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
        }
        if (school_notice == null) {
            school_notice = "";
        }
        
		detail_Notice = new Cpk_Side_Notice_Detail();
		
		rela_notice_detail = (RelativeLayout)findViewById(R.id.rela_notice_detail);
		if (acy_type==2) {
			rela_notice_detail.setVisibility(View.VISIBLE);
		}
		demo_swiperefreshlayout=(SimpleSwipeRefreshLayout)findViewById(R.id.demo_swiperefreshlayout); //新增下拉使用
		
		side_acy_detail_load_error = findViewById(R.id.side_side_notice_detail_load_error);
		liner_acy_detail_whole = findViewById(R.id.liner_side_notice_detail_whole);
		side_acy_detail_loading=findViewById(R.id.side_side_notice_detail_loading);
		lv_act_detail_comment = (PullToRefreshListView) findViewById(R.id.lv_side_notice_detail_comment);
		tv_acy_detail_comment_content = (EditText) findViewById(R.id.tv_side_notice_detail_comment_content);
		liner_notice_detail_send = (LinearLayout)findViewById(R.id.liner_side_notice_comment_send);
		iv_acy_comment_send = (ImageView) findViewById(R.id.iv_side_notice_comment_send);

		home_load_loading = (LinearLayout) side_acy_detail_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 

		imageLoaderBanner = A_0_App.getInstance().getimageLoader();
		optionsBanner = A_0_App.getInstance().getOptions(
				R.drawable.ic_default_empty_bg,
				R.drawable.ic_default_empty_bg,
				R.drawable.ic_default_empty_bg);

		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center);
		bitmapUtilsBanner=A_0_App.getBitmapUtils(this,R.drawable.ic_default_empty_bg,R.drawable.ic_default_empty_bg,false);
		bitmapUtils=A_0_App.getBitmapUtils(this,R.drawable.ic_defalut_person_center,R.drawable.ic_defalut_person_center,false);
		list = new ArrayList<Cpk_Side_Notice_Comment_detail>();
		adapter = new Mydapter();
		addHeadView(lv_act_detail_comment);

		lv_act_detail_comment.setAdapter(adapter);
		lv_act_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
		lv_act_detail_comment.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {


			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				
				String label = DateUtils.formatDateTime(
						getApplicationContext(),System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME| DateUtils.FORMAT_SHOW_DATE| DateUtils.FORMAT_ABBREV_ALL);
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
				readData(acy_detail_id);
				
			
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (repfresh==0) {
					repfresh=1;
					demo_swiperefreshlayout.setEnabled(false);
					demo_swiperefreshlayout.setRefreshing(false);  
					getMoreComment(acy_detail_id, have_read_page);
						
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
			if (lv_act_detail_comment!=null) {
				lv_act_detail_comment.onRefreshComplete();
			}
			demo_swiperefreshlayout
					.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {
							have_read_page = 1;
							lv_act_detail_comment.setMode(Mode.DISABLED);
							readData(acy_detail_id);

						};
					});
		}
		lv_act_detail_comment.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				 if (demo_swiperefreshlayout!=null&&lv_act_detail_comment.getChildCount() > 0 && lv_act_detail_comment.getRefreshableView().getFirstVisiblePosition() == 0
			                && lv_act_detail_comment.getChildAt(0).getTop() >= lv_act_detail_comment.getPaddingTop()) {
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
		
		
		// 报名成功
		btn_sign_up_me.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});

		side_acy_detail_load_error.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showLoadResult(true,false, false);
				readData(acy_detail_id);
			}
		});

		tv_acy_detail_comment_content
				.setBackgroundResource(R.drawable.bg_edit_source_normal);
		tv_acy_detail_comment_content
				.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						if (arg1) {
							tv_acy_detail_comment_content
									.setBackgroundResource(R.drawable.bg_edit_source_focus);
						} else {
							tv_acy_detail_comment_content
									.setTextColor(getResources().getColor(
											R.color.title_no_focus_login));
						}
					}
				});

		tv_acy_detail_comment_content.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2,
                    int arg3) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && !"".equals(s.toString()))
                {
                    iv_acy_comment_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_comment_send));
                    if (AppStrStatic.WORD_COMMENT_MAX_LIMIT < s.length()) {
                        PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this, "已达到最大字数限制");
                        tv_acy_detail_comment_content.setText(tv_acy_detail_comment_content.getText().toString()
                                .substring(0, AppStrStatic.WORD_COMMENT_MAX_LIMIT));
                        tv_acy_detail_comment_content
                                .setSelection(AppStrStatic.WORD_COMMENT_MAX_LIMIT);
                    } else {
                        // sent_notice_tv.setText("还可以输入"+(content_limit-arg0.length())+"字");
                    }
                } else
                {
                    iv_acy_comment_send.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.ic_comment_send_enable));
                }
            }
        });
        
		// 评论
		liner_notice_detail_send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String str = tv_acy_detail_comment_content.getText().toString();

				if (str != null && !(str.equals("")) && str.length() >= AppStrStatic.WORD_COMMENT_MIN_LIMIT) {
                    if(!judgeNoNullStr(str)){
                        PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this,"请输入3个字以上的评论内容");
                        return;
                    }
					if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_RONGYUN_CONNECT)) {
						commentAcy(acy_detail_id,logid,comment_id, str);
					} else {
						PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this,
								"您的评论过于频繁！");
					}

				} else {
					PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this,
							"请输入3个字以上的评论内容");
				}
			}
		});

		lv_act_detail_comment.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.e("test", list.size() + "");

			}
		});
		
            tv_acy_detail_comment_content.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			    if (s.length() > 0 && !"".equals(s.toString()))
                {
                    iv_acy_comment_send.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_comment_send));
                    if (AppStrStatic.WORD_COMMENT_MAX_LIMIT < s.length()) {
                        PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this, "已达到最大字数限制");
                        tv_acy_detail_comment_content.setText(tv_acy_detail_comment_content.getText().toString()
                                .substring(0, AppStrStatic.WORD_COMMENT_MAX_LIMIT));
                        tv_acy_detail_comment_content
                                .setSelection(AppStrStatic.WORD_COMMENT_MAX_LIMIT);
                    } else {
                        // sent_notice_tv.setText("还可以输入"+(content_limit-arg0.length())+"字");
                    }
                } else
                {
                    iv_acy_comment_send.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.ic_comment_send_enable));
                }
			}
		});
		readCache(acy_detail_id);
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }

	}

    /*
     * 不能为空字符串
     */
    private boolean judgeNoNullStr(String str_Str) {
        String str = str_Str.replaceAll(" ", "");
        if (!"".equals(str)) {
            return true;
        } else {
            return false;
        }
    }
    
	private void readCache(String acy_detail_id) {
		// TODO Auto-generated method stub
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_notice_detail+A_0_App.USER_UNIQID+acy_detail_id);
        if (jsonObject!= null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
			
        	showInfo(jsonObject);
			}else{
		    updateInfo();}
	}

	private void showInfo(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		Cpk_Side_Notice_Detail mNotice = getNotice(jsonObject);
		if (isFinishing())
			return;
		havaSuccessLoadData = true; 
		list.clear();
		detail_Notice = mNotice;
		try {
			//severTime=mNotice.getTime()*1000;
		} catch (Exception e) {
			severTime=0;
		}
		
		String img_url = mNotice.getBg_img();
		if (img_url != null && img_url.length() > 0
				&& !img_url.equals("")) {
			//iv_acy_detail_banner.setVisibility(View.VISIBLE);
			/*imageLoaderBanner.displayImage(img_url,
					iv_acy_detail_banner, optionsBanner);*/
			//bitmapUtilsBanner.display(iv_acy_detail_banner, img_url);
		} else {
			iv_acy_detail_banner.setVisibility(View.GONE);
		}

		tv_detail_sent_count_sms.setText(mNotice.getSend_count());
		tv_acy_detail_time.setText(PubMehods.getFormatDate(mNotice.getCreate_time(), "MM/dd HH:mm"));
		tv_detail_comment_count.setText(mNotice.getSend_count());
		try {
			tv_acy_detail_from.setText(mNotice.getApp_msg_sign());
		} catch (Exception e) {
		}
		
		if(mNotice.getContent_type().equals("1"))
        {
        wv_acy_detail.setVisibility(View.VISIBLE);
        tv_acy_detail.setVisibility(View.GONE);
      // wv_acy_detail.loadDataWithBaseURL("",mNotice.getContent(), "text/html","UTF-8", null);
        /**
         * 本地读取html
         */
        PubMehods.saveHtml(mNotice.getContent(), "TEMP_HTML");
        String html_path="file://"+Environment.getExternalStorageDirectory()+"/TEMP_HTML.html";
        wv_acy_detail.loadUrl(html_path);
        wv_acy_detail.addJavascriptInterface(new JavascriptInterface(B_Side_Notice_Main_Detail_0.this), "imagelistner");
        wv_acy_detail.setWebViewClient(new MyWebViewClient());
        }else if(mNotice.getContent_type().equals("0"))
        {
       	 wv_acy_detail.setVisibility(View.GONE);
       	 tv_acy_detail.setVisibility(View.VISIBLE);
       	 tv_acy_detail.setText(mNotice.getContent());
        }
		if (type.equals("4")) {
			//SpannableStringBuilder builder = new SpannableStringBuilder(mNotice.getContent()+"  ["+mNotice.getApp_msg_sign()+"]");
			//ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.tab_indicator_text_selected));  
			//builder.setSpan(redSpan, mNotice.getContent().length(), mNotice.getContent().length()+4+mNotice.getApp_msg_sign().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			//			tv_acy_detail_title.setText("手机短信");
			setSendStatusText(mNotice, 1);
			wv_acy_detail
			.setVisibility(View.GONE);
			liner_detail
			.setVisibility(View.GONE);
			liner_detail_sms
			.setVisibility(View.VISIBLE);
		} else {
			setSendStatusText(mNotice, 0);
			liner_detail
			.setVisibility(View.VISIBLE);
			liner_detail_sms
			.setVisibility(View.GONE);
//			tv_acy_detail_title.setText(mNotice.getTitle());
			//附件
			switch (mNotice.getIs_appendix()){
				case 0:
					tv_acy_detail_title.setText(mNotice.getTitle());
					tv_acy_detail_title.setCompoundDrawables(null, null, null, null);
					tv_notice_detail_comment_other.setVisibility(View.GONE);
					break;
				case 1:
					VerticalImageSpan span = new VerticalImageSpan(B_Side_Notice_Main_Detail_0.this, R.drawable.file_zhizhen);
					SpannableString spanStr = new SpannableString(mNotice.getTitle() + "    ");
					spanStr.setSpan(span, spanStr.length()-1, spanStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
					tv_acy_detail_title.setText(spanStr);
					tv_notice_detail_comment_other.setVisibility(View.VISIBLE);
					file_name = mNotice.getFile_name();
					file_ext = mNotice.getFile_ext();
					String size = mNotice.getFile_size();
					if (size != null){
						if (size.contains(".")){
							size = size.substring(0, size.indexOf("."));
						}
						file_size = Long.parseLong(size);
						fileDet.setText(FileSizeUtils.FormetFileSize(Long.parseLong(size)));
					}
					fileName.setText(mNotice.getFile_name());
					file_url = mNotice.getFile_url();
					String substr = mNotice.getFile_ext().substring(mNotice.getFile_ext().length() - 3, mNotice.getFile_ext().length()).trim().toLowerCase();
					switch (substr){
						case "doc":
							fileImage.setImageResource(R.drawable.file_word);
							break;
						case "ocx":
							fileImage.setImageResource(R.drawable.file_word);
							break;
						case "xls":
							fileImage.setImageResource(R.drawable.file_xl);
							break;
						case "lsx":
							fileImage.setImageResource(R.drawable.file_xl);
							break;
						case "ppt":
							fileImage.setImageResource(R.drawable.file_ppt);
							break;
						case "ptx":
							fileImage.setImageResource(R.drawable.file_ppt);
							break;
						case "txt":
							fileImage.setImageResource(R.drawable.file_txt);
							break;
					}
					File f = new File(Environment.getExternalStorageDirectory().getPath()+ "//" + AppStrStatic.SD_PIC+"/" + file_name);
					Log.i("aaa", "showInfo: " + f.exists());
					if (f.exists()){
						fileProgressBar.setVisibility(View.VISIBLE);
						fileProgressBar.setProgress(100);
						DonDownload = true;
					}else {
						DonDownload = false;
					}
					break;
			}

			wv_acy_detail
			.setVisibility(View.VISIBLE);
		}
		if(acy_type==2){
            tv_notice_detail_comment_count.setVisibility(View.VISIBLE);
         }else
         {
           tv_notice_detail_comment_count.setVisibility(View.GONE);
         }
		list = mNotice.getList();
		 if (list.size()==0) {
			 Cpk_Side_Notice_Comment_detail all=new Cpk_Side_Notice_Comment_detail();
					all.setUser_id("yuanding");
					list.add(all);
				}
		adapter.notifyDataSetChanged();
		//solveClashListView = new SolveClashListView();
		//solveClashListView.setListViewHeightBasedOnChildren(
				//lv_act_detail_comment, 1);
		pic_show();
		showLoadResult(false,true, false);
		 if (lv_act_detail_comment != null) {
			 lv_act_detail_comment.onRefreshComplete();
			 lv_act_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
			}
			demo_swiperefreshlayout.setRefreshing(false);
			repfresh = 0;
	}


	 // 注入js函数监听
	  	private void addImageClickListner() {
	  		// 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
	  		wv_acy_detail.loadUrl("javascript:(function(){" +
	  		"var objs = document.getElementsByTagName(\"img\"); " + 
	  				"for(var i=0;i<objs.length;i++)  " + 
	  		"{"+
	  				"objs[i].onclick=function()" +
	  		         "{ "+ 
	  		           "window.imagelistner.openImage(this.src);  " + 
	  		         "}" + 
	  		"}" + 
	  		"})()");
	  	}
	  	

	  	
	  	/**
	  	 * 获取当前页面所有img地址为list
	  	 * @author Administrator
	  	 *
	  	 */
	      private void getWebViewAllImgSrc() {
	    	  wv_acy_detail.loadUrl("javascript:(function(){" +
	      			"var objs = document.getElementsByTagName(\"img\"); " + 
	      			"var imgScr = '';"+
	      			   "for(var i=0;i<objs.length;i++){"+
	      			       "imgScr =imgScr +  '\\n'+ objs[i].src;"+
	      		       " };"+
	      		     "window.imagelistner.getAllImg(imgScr);  " + 
//	      			"return imgScr;"+
	      			"})()");

	  	}
	  	
	  	// js通信接口
	  	public class JavascriptInterface {
	  	
	 			
	 		private Context context;
	  		final ArrayList<String> image_List = new ArrayList<String>();
	  		int temp=0;
	  		public JavascriptInterface(Context context) {
	  			this.context = context;
	  			
	  		}

	  		public void openImage(String img) {
	  			
	  			for (int i = 0; i < image_List.size(); i++) {
	 				if (image_List.get(i).equals(img)) {
	 					temp=i;
	 				}
	 			}
	  			
	  			Intent intent = new Intent();
	  			intent.putStringArrayListExtra("path", image_List);
	 			intent.putExtra("num", temp);
	  			intent.setClass(context, B_Side_Found_BigImage.class);
	  			context.startActivity(intent);
	  			
	  		}
	  		
	  		public void getAllImg(String img) {
	  			String [] temp = null;  
	  			temp = img.split("\n"); 
	  			//以换行符为url分隔符 第一个元素的null
	  			int counts = temp.length-1;
	  			for(int i=0;i<temp.length;i++){
	  				//以换行符为url分隔符 第一个元素的null
	  				if(i==0){continue;}
	  				image_List.add(temp[i]);
	  			}
	  		}
	  	}

	  	// 监听
	  	private class MyWebViewClient extends WebViewClient {
	  		@Override
	  		public boolean shouldOverrideUrlLoading(WebView view, String url) {

	  			if(url.contains("//img")){
	  				
	  				return true;
	  			}else {
	  				
	  				return super.shouldOverrideUrlLoading(view, url);
	  			}
	  			
	  		}

	  		@Override
	  		public void onPageFinished(WebView view, String url) {

	  			view.getSettings().setJavaScriptEnabled(true);

	  			super.onPageFinished(view, url);
	  			// html加载完成之后，添加监听图片的点击js函数
	  			addImageClickListner();
	  			getWebViewAllImgSrc();

	  		}

	  		@Override
	  		public void onPageStarted(WebView view, String url, Bitmap favicon) {
	  			view.getSettings().setJavaScriptEnabled(true);

	  			super.onPageStarted(view, url, favicon);
	  		}

	  		@Override
	  		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

	  			super.onReceivedError(view, errorCode, description, failingUrl);

	  		}
	  	}
	  	
	
	
	
	private Cpk_Side_Notice_Detail getNotice(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		
			int state = jsonObject.optInt("status");
			if (state == 1) {
				JSONObject dd = jsonObject
						.optJSONObject("info");
				Cpk_Side_Notice_Detail notice = new Cpk_Side_Notice_Detail();
				notice=JSON.parseObject(dd+"", Cpk_Side_Notice_Detail.class);
				List<Cpk_Side_Notice_Comment_detail> mlistContact = new ArrayList<Cpk_Side_Notice_Comment_detail>();
				mlistContact=JSON.parseArray(jsonObject.optJSONArray("comment")+"", Cpk_Side_Notice_Comment_detail.class);
				notice.setList(mlistContact);
				return notice;
		}
		
		return null;
	}

	private void addHeadView(PullToRefreshListView listview) {
		ListView listView2=listview.getRefreshableView();
		View view = LayoutInflater.from(B_Side_Notice_Main_Detail_0.this)
				.inflate(R.layout.activity_side_notice_head, null);

		iv_acy_detail_banner = (ImageView) view
				.findViewById(R.id.iv_acy_detail_banner);
		liner_detail = (LinearLayout) view
				.findViewById(R.id.liner_detail);
		liner_detail_sms = (LinearLayout) view
				.findViewById(R.id.liner_detail_sms);
		tv_acy_detail_title = (TextView) view
				.findViewById(R.id.tv_acy_detail_title);
		tv_acy_detail_time = (TextView) view
				.findViewById(R.id.tv_acy_detail_time);
		tv_detail_sent_count_sms = (TextView) view
				.findViewById(R.id.tv_detail_sent_count_sms);
		tv_detail_re_count_sms = (TextView) view
				.findViewById(R.id.tv_detail_re_count_sms);
		tv_detail_comment_count = (TextView) view
				.findViewById(R.id.tv_replay_count_title_bb);
		tv_act_detail_time = (TextView) view
				.findViewById(R.id.tv_act_detail_time);
		tv_act_detail_place = (TextView) view
				.findViewById(R.id.tv_act_detail_place);
        tv_acy_detail_from=(TextView) view.findViewById(R.id.tv_acy_detail_from);
        tv_notice_detail_comment_count=(LinearLayout) view.findViewById(R.id.tv_notice_detail_comment_count);
		btn_sign_up_me = (Button) view.findViewById(R.id.btn_sign_up_me);
		wv_acy_detail = (WebView) view.findViewById(R.id.wv_acy_detail);
		liner_notice_detail_container = (LinearLayout)view.findViewById(R.id.liner_notice_detail_container);
		 tv_acy_detail=(TextView) view.findViewById(R.id.tv_acy_detail);
		 
		 //图文详情-已读未读按钮 和包裹已读未读按钮的布局容器
		 btn_unread = (Button)view.findViewById(R.id.btn_unread);
	     btn_readed = (Button)view.findViewById(R.id.btn_readed);
		layout_read_or_unread = (LinearLayout)view.findViewById(R.id.layout_read_or_unread);
		
		//短信详情-发送中按钮 发送失败按钮 发送成功按钮 以及标题前面的状态值
		btn_sending = (Button)view.findViewById(R.id.btn_sending);
		 		
		//附件
		tv_notice_detail_comment_other = (LinearLayout) view.findViewById(R.id.tv_notice_detail_comment_other);
		mess_notice_detail_load_other = (RelativeLayout) view.findViewById(R.id.mess_notice_detail_load_other);
		fileImage = (ImageView) view.findViewById(R.id.fileImage);
		fileName = (TextView) view.findViewById(R.id.fileName);
		fileDet = (TextView) view.findViewById(R.id.fileDet);
		fileProgressBar = (ProgressBar) view.findViewById(R.id.fileProgressBar);
		mess_notice_detail_load_other.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//开始下载
				if (!DonDownload){
					if (file_size > 0){
						downloadFile(file_url, BASE_PATH + file_name);
					}else {
						showToastFileStr("空文件，无法下载");
					}

				}else {
						try {
							String substr = file_ext.substring(file_ext.length() - 3, file_ext.length()).trim().toLowerCase();
							switch (substr){
								case "doc":
									B_Side_Notice_Main_Detail_0.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/msword"));
									break;
								case "ocx":
									B_Side_Notice_Main_Detail_0.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/msword"));
									break;
								case "xls":
									B_Side_Notice_Main_Detail_0.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-excel"));
									break;
								case "lsx":
									B_Side_Notice_Main_Detail_0.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-excel"));
									break;
								case "ptx":
									B_Side_Notice_Main_Detail_0.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-powerpoint"));
									break;
								case "ppt":
									B_Side_Notice_Main_Detail_0.this.startActivity(getFileIntent(BASE_PATH + file_name, "application/vnd.ms-powerpoint"));
									break;
								case "txt":
									B_Side_Notice_Main_Detail_0.this.startActivity(getFileIntent(BASE_PATH + file_name, "text/plain"));
									break;
							}
						}catch (Exception e){
							showToastFileStr("打开文件失败或没有打开文件的应用");
						}
				}

			}
		});


		wv_acy_detail.getSettings().setLayoutAlgorithm( LayoutAlgorithm.SINGLE_COLUMN);
		wv_acy_detail.setBackgroundColor(getResources().getColor( R.color.col_account_bg));
		wv_acy_detail.getSettings().setJavaScriptEnabled(true);
		int size=A_0_App.getInstance().setWebviewSize();
		wv_acy_detail.getSettings().setDefaultFontSize(size);
		wv_acy_detail.getSettings().setDefaultTextEncodingName("utf-8"); //设置文本编码
		wv_acy_detail.setWebViewClient(new WebViewClient() { 
            public boolean shouldOverrideUrlLoading(WebView view, String url) 
              { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边 
            	
                return true; 
             }
        });
		
		
		listView2.addHeaderView(view);
		listView2.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				comment_id="";
				tv_acy_detail_comment_content.setHint("写评论");
				closeKeybord(tv_acy_detail_comment_content, B_Side_Notice_Main_Detail_0.this);
				return false;
			}
		});
	}
	public static Intent getFileIntent( String param, String type)
	{
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param ));
		intent.setDataAndType(uri, type);
		return intent;
	}
	/**
     * 多张图片展示
     */
    private void pic_show(){
    	if (detail_Notice.getPhoto_url()!=null&&detail_Notice.getPhoto_url()!=""&&detail_Notice.getPhoto_url().length()>0) {
    		final ArrayList<String> path = new ArrayList<String>();
    		liner_notice_detail_container.setVisibility(View.VISIBLE);
			String notice_photos[]=detail_Notice.getPhoto_url().replace("\\", "").split(",");
			for (int i = 0; i <notice_photos.length; i++) {
				path.add(notice_photos[i]);
			}
			if (notice_photos.length==1) {
	            liner_notice_detail_container.removeAllViews();
	            LinearLayout horizonLayout = new LinearLayout(B_Side_Notice_Main_Detail_0.this);
	            RelativeLayout.LayoutParams params;
	            ImageView image1 = new ImageView(B_Side_Notice_Main_Detail_0.this);
	            float density = PubMehods.getDensity(B_Side_Notice_Main_Detail_0.this);
	            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
	            int width = wm.getDefaultDisplay().getWidth();
	            float imageLayWidth = width - (20) * density;
	            params = new RelativeLayout.LayoutParams((int) (imageLayWidth),
	                    (int) (imageLayWidth));
	            horizonLayout.addView(image1, params);
	            image1.setScaleType(ScaleType.CENTER_CROP);
	            if (notice_photos[0]!=null) {
	                PubMehods.loadBitmapUtilsPic(bitmapUtilsBanner, image1, notice_photos[0]);
				}
	            
	            image1.setOnClickListener(new OnClickListener() {

	                @Override
	                public void onClick(View arg0) {
	                	Intent intent = new Intent(B_Side_Notice_Main_Detail_0.this,
								B_Side_Found_BigImage.class);
						intent.putStringArrayListExtra("path", path);
						intent.putExtra("num", 0);
						startActivity(intent);
	                }
	            });
	            liner_notice_detail_container.addView(horizonLayout);
	        
			} else if(notice_photos.length==2){
				liner_notice_detail_container.removeAllViews();
	            LinearLayout horizonLayout = new LinearLayout(B_Side_Notice_Main_Detail_0.this);
	            LinearLayout.LayoutParams params;
	            float density = PubMehods.getDensity(B_Side_Notice_Main_Detail_0.this);
	            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
	            int width = wm.getDefaultDisplay().getWidth();
	            float imageLayWidth = width - (36) * density;
	            for (int i = 0; i < notice_photos.length; i++) {
	                params = new LinearLayout.LayoutParams(
	                        (int) (imageLayWidth /2-10), (int) (imageLayWidth / 2-10));
	                params.setMargins(5, 5, 5, 5);
	                ImageView image2 = new ImageView(B_Side_Notice_Main_Detail_0.this);
	                final int a = i;
	                image2.setLayoutParams(params);
	                image2.setScaleType(ScaleType.CENTER_CROP);
	                horizonLayout.addView(image2);
	                if (notice_photos[i]!=null) {
	                    PubMehods.loadBitmapUtilsPic(bitmapUtilsBanner, image2, notice_photos[i]);
					}
	              
	               // imageLoaderBanner.displayImage(notice_photos[i],image2, optionsBanner);
	                image2.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {

							Intent intent = new Intent(B_Side_Notice_Main_Detail_0.this,
									B_Side_Found_BigImage.class);
							intent.putStringArrayListExtra("path", path);
							intent.putExtra("num", a);
							startActivity(intent);						
						}
					});
	            }
	            liner_notice_detail_container.addView(horizonLayout);
	        
			}else if (notice_photos.length> 2 && notice_photos.length<= 9) {

				liner_notice_detail_container.removeAllViews();
	            LinearLayout.LayoutParams params;
	            float density = PubMehods.getDensity(B_Side_Notice_Main_Detail_0.this);
	            WindowManager wm = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
	            int width = wm.getDefaultDisplay().getWidth();
	            float imageLayWidth = width - (38) * density;
	            int size = notice_photos.length;
	            int yuShu = size % 3;
	            if (yuShu == 0) {
	                int hangNum = (int) (size / 3);
	                for (int i = 0; i < hangNum; i++) {
	                    LinearLayout horizonLayout = new LinearLayout(B_Side_Notice_Main_Detail_0.this);
	                   // horizonLayout.setPadding(0, 10, 0, 0);
	                    for (int j = 0; j < 3; j++) {
	                        params = new LinearLayout.LayoutParams(
	                                (int) (imageLayWidth / 3-10),
	                                (int) (imageLayWidth / 3-10));
	                        params.setMargins(5, 5, 5, 5);
	                        ImageView image3 = new ImageView(B_Side_Notice_Main_Detail_0.this);
	                       // image3.setBackgroundResource(R.drawable.ic_default_empty_bg);
	                       
	                        image3.setScaleType(ScaleType.CENTER_CROP);
	                        image3.setLayoutParams(params);
	                        horizonLayout.addView(image3);
	                        final int a = i * 3 + j;
	                        if (notice_photos[a]!=null) {
	                            PubMehods.loadBitmapUtilsPic(bitmapUtilsBanner,image3, notice_photos[a]);
	                        }
	                       // imageLoaderBanner.displayImage(notice_photos[a],image3, optionsBanner);
	                        image3.setOnClickListener(new OnClickListener() {

	                            @Override
	                            public void onClick(View arg0) {

									Intent intent = new Intent(B_Side_Notice_Main_Detail_0.this,
											B_Side_Found_BigImage.class);
									intent.putStringArrayListExtra("path", path);
									intent.putExtra("num", a);
									startActivity(intent);								
	                            }
	                        });
	                    }
	                    liner_notice_detail_container.addView(horizonLayout);
	                }
	            } else {
	                int hangNum = (int) (size / 3) + 1;
	                for (int i = 0; i <= hangNum - 1; i++) {
	                    LinearLayout horizonLayout = new LinearLayout(B_Side_Notice_Main_Detail_0.this);
	                    //horizonLayout.setPadding(0, 10, 0, 0);
	                    if (i < hangNum - 1) {
	                        for (int j = 0; j < 3; j++) {
	                            params = new LinearLayout.LayoutParams(
	                                    (int) (imageLayWidth / 3-10),
	                                    (int) (imageLayWidth / 3-10));
	                            params.setMargins(5, 5, 5, 5);
	                            ImageView image3 = new ImageView(B_Side_Notice_Main_Detail_0.this);
	                           // image3.setBackgroundResource(R.drawable.ic_default_empty_bg);
	                            image3.setScaleType(ScaleType.CENTER_CROP);
	                            image3.setLayoutParams(params);
	                            //image3.setPadding(5, 0, 5, 0);
	                            horizonLayout.addView(image3);
	                            final int a = i * 3 + j;
	                            if (notice_photos[a]!=null) {
	                                PubMehods.loadBitmapUtilsPic(bitmapUtilsBanner,image3, notice_photos[a]);
	                            }
	                           // imageLoaderBanner.displayImage(notice_photos[a],image3, optionsBanner);
	                            image3.setOnClickListener(new OnClickListener() {

	                                @Override
	                                public void onClick(View arg0) {

	    								Intent intent = new Intent(B_Side_Notice_Main_Detail_0.this,
	    										B_Side_Found_BigImage.class);
	    								intent.putStringArrayListExtra("path", path);
	    								intent.putExtra("num", a);
	    								startActivity(intent);

	    							
	                                }
	                            });
	                        }
	                        liner_notice_detail_container.addView(horizonLayout);
	                    } else if (i == hangNum - 1) {
	                        for (int j = 0; j < yuShu; j++) {
	                            params = new LinearLayout.LayoutParams(
	                                    (int) (imageLayWidth / 3-10),
	                                    (int) (imageLayWidth / 3-10));
	                            params.setMargins(5, 5, 5, 5);
	                            ImageView image3 = new ImageView(B_Side_Notice_Main_Detail_0.this);
	                            //image3.setBackgroundResource(R.drawable.ic_default_empty_bg);
	                           //image3.setPadding(5, 0, 5, 0);
	                            image3.setLayoutParams(params);
	                            image3.setScaleType(ScaleType.CENTER_CROP);
	                            horizonLayout.addView(image3);
	                            final int a = i * 3 + j;
	                            if (notice_photos[a]!=null) {
	                                PubMehods.loadBitmapUtilsPic(bitmapUtilsBanner,image3, notice_photos[a]);
	                           }
	                            //imageLoaderBanner.displayImage(notice_photos[a],image3, optionsBanner);
	                            image3.setOnClickListener(new OnClickListener() {

	                                @Override
	                                public void onClick(View arg0) {

	    								Intent intent = new Intent(B_Side_Notice_Main_Detail_0.this,
	    										B_Side_Found_BigImage.class);
	    								intent.putStringArrayListExtra("path", path);
	    								intent.putExtra("num", a);
	    								startActivity(intent);

	    							
	                                }
	                            });
	                        }
	                        liner_notice_detail_container.addView(horizonLayout);
	                    }
	                }
	            }
	        
			}
		}
    }
	private PopupWindow statusPopup;
	private LinearLayout mLinerCollct, mLinerForward;

	private void showWindow(View parent) {
		if (statusPopup == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.item_lecture_detail,
					null);
			mLinerCollct = (LinearLayout) view
					.findViewById(R.id.liner_lecture_detail_collect);
			mLinerForward = (LinearLayout) view
					.findViewById(R.id.liner_lecture_detail_forward);
			statusPopup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}
		statusPopup.setFocusable(true);
		statusPopup.setOutsideTouchable(true);
		statusPopup.setBackgroundDrawable(new BitmapDrawable());
		statusPopup.showAsDropDown(parent, 0, A_0_App.getInstance()
				.getShowViewHeight());// 第一参数负的向左，第二个参数正的向下

		mLinerForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (statusPopup != null) {
					statusPopup.dismiss();
				}
				Intent intent = new Intent(B_Side_Notice_Main_Detail_0.this,
						B_Mess_Forward_Select.class);
				intent.putExtra("title", detail_Notice.getTitle());
				intent.putExtra("content",detail_Notice.getContent());
				intent.putExtra("type", "4");
				intent.putExtra("image", detail_Notice.getBg_img());
				intent.putExtra("acy_type", "2");
				intent.putExtra("noticeId", acy_detail_id);
				startActivity(intent);
			}
		});

		mLinerCollct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (statusPopup != null) {
					statusPopup.dismiss();
				}
				PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this, "后续开放");
			}
		});
	}

	private void showLoadResult(boolean loading,boolean whole, boolean loadFaile) {
		if (whole)
			liner_acy_detail_whole.setVisibility(View.VISIBLE);
		else
			liner_acy_detail_whole.setVisibility(View.GONE);

		if (loadFaile)
			side_acy_detail_load_error.setVisibility(View.VISIBLE);
		else
			side_acy_detail_load_error.setVisibility(View.GONE);
		if (loading) {
			drawable.start();
			side_acy_detail_loading.setVisibility(View.VISIBLE);
		} else {
			if (drawable != null) {
				drawable.stop();
			}
			side_acy_detail_loading.setVisibility(View.GONE);
		}
	}
	private void downloadFile(String url, final String path){
		mess_notice_detail_load_other.setClickable(false);
		fileProgressBar.setVisibility(View.VISIBLE);
		A_0_App.getApi().download_Photo(url, path, new Api.Inter_DownLoad_Photo() {
			@Override
			public void onSuccess(String message) {
				mess_notice_detail_load_other.setClickable(true);
				DonDownload = true;
				showToastFileStr("文件已保存至" + AppStrStatic.SD_PIC +"/文件夹");
			}

			@Override
			public void onLoading(long arg0, long arg1, boolean arg2) {
				process = 1.0 * arg1 / arg0;
				fileProgressBar.setProgress((int)(process * 100));
			}

			@Override
			public void onStart() {

			}

			@Override
			public void onWaiting() {

			}
		}, new Inter_Call_Back() {
			@Override
			public void onCancelled() {

			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onFailure(String msg) {
				if (isFinishing())
					return;
				PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this, msg);
				mess_notice_detail_load_other.setClickable(true);
				fileProgressBar.setVisibility(View.GONE);
			}
		});
	}
	private void readData(String id) {
		A_0_App.getApi().getSideNoticeDetail(B_Side_Notice_Main_Detail_0.this,school_notice,
				A_0_App.USER_TOKEN, id,notice_type,new InterSideNoticeDetail() {
					@Override
					public void onSuccess(Cpk_Side_Notice_Detail notice,long time) {
						if (isFinishing())
							return;
						havaSuccessLoadData = true; 
						try {
							severTime=time*1000;
						} catch (Exception e) {
							severTime=0;
						}

						have_read_page = 2;
						list.clear();
						detail_Notice = notice;


						String img_url = notice.getBg_img();
						if (img_url != null && img_url.length() > 0
								&& !img_url.equals("")) {
							//iv_acy_detail_banner.setVisibility(View.VISIBLE);
							/*imageLoaderBanner.displayImage(img_url,
									iv_acy_detail_banner, optionsBanner);*/
							//bitmapUtilsBanner.display(iv_acy_detail_banner, img_url);
						} else {
							iv_acy_detail_banner.setVisibility(View.GONE);
						}

						tv_detail_sent_count_sms.setText(notice.getSend_count());
						tv_acy_detail_time.setText(PubMehods.getFormatDate(
								notice.getCreate_time(), "MM/dd HH:mm"));
						tv_detail_comment_count.setText(notice.getSend_count());
						try {
							tv_acy_detail_from.setText(notice.getApp_msg_sign());
						} catch (Exception e) {
						}
					
						if (notice.getContent_type()!=null) {
							
						
						if (notice.getContent_type().equals("1")) {
							wv_acy_detail.setVisibility(View.VISIBLE);
							tv_acy_detail.setVisibility(View.GONE);
							// wv_acy_detail.loadDataWithBaseURL("",notice.getContent(),
							// "text/html","UTF-8", null);

							/**
							 * 网络读取html
							 */
							PubMehods.saveHtml(notice.getContent(), "TEMP_HTML");
							String html_path = "file://" + Environment.getExternalStorageDirectory() + "/TEMP_HTML.html";
							wv_acy_detail.loadUrl(html_path);
							wv_acy_detail.addJavascriptInterface( new JavascriptInterface( B_Side_Notice_Main_Detail_0.this), "imagelistner");
							wv_acy_detail .setWebViewClient(new MyWebViewClient());

						} else if (notice.getContent_type().equals("0")) { 
							wv_acy_detail.setVisibility(View.GONE);
							tv_acy_detail.setVisibility(View.VISIBLE);
							tv_acy_detail.setText(notice.getContent());
						}
						
						}
						if (type.equals("4")) {     //短信详情
							
							setSendStatusText(notice, 1);					
							liner_detail.setVisibility(View.GONE);
							liner_detail_sms.setVisibility(View.VISIBLE);
						} else {          //图文详情								
							
							setSendStatusText(notice, 0);
							liner_detail.setVisibility(View.VISIBLE);
							liner_detail_sms.setVisibility(View.GONE);
							//附件
							switch (notice.getIs_appendix()){
								case 0:
									tv_acy_detail_title.setText(notice.getTitle());
									tv_acy_detail_title.setCompoundDrawables(null, null, null, null);
									tv_notice_detail_comment_other.setVisibility(View.GONE);
									break;
								case 1:
									VerticalImageSpan span = new VerticalImageSpan(B_Side_Notice_Main_Detail_0.this, R.drawable.file_zhizhen);
									SpannableString spanStr = new SpannableString(notice.getTitle() + "    ");
									spanStr.setSpan(span, spanStr.length()-1, spanStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
									tv_acy_detail_title.setText(spanStr);
									tv_notice_detail_comment_other.setVisibility(View.VISIBLE);
									file_name = notice.getFile_name();
									file_ext = notice.getFile_ext();
									fileName.setText(notice.getFile_name());
									String size = notice.getFile_size();
									if (size != null){
										if (size.contains(".")){
											size = size.substring(0, size.indexOf("."));
										}
										file_size = Long.parseLong(size);
										fileDet.setText(FileSizeUtils.FormetFileSize(Long.parseLong(size)));
									}
									file_url = notice.getFile_url();
									String substr = notice.getFile_ext().substring(notice.getFile_ext().length() - 3, notice.getFile_ext().length()).trim().toLowerCase();
									switch (substr){
										case "doc":
											fileImage.setImageResource(R.drawable.file_word);
											break;
										case "ocx":
											fileImage.setImageResource(R.drawable.file_word);
											break;
										case "xls":
											fileImage.setImageResource(R.drawable.file_xl);
											break;
										case "lsx":
											fileImage.setImageResource(R.drawable.file_xl);
											break;
										case "ppt":
											fileImage.setImageResource(R.drawable.file_ppt);
											break;
										case "ptx":
											fileImage.setImageResource(R.drawable.file_ppt);
											break;
										case "txt":
											fileImage.setImageResource(R.drawable.file_txt);
											break;
									}
									File f = new File(Environment.getExternalStorageDirectory().getPath()+ "//" + AppStrStatic.SD_PIC+"/" + file_name);
									Log.i("aaa", "showInfo: " + f.exists());
									if (f.exists()){
										fileProgressBar.setVisibility(View.VISIBLE);
										fileProgressBar.setProgress(100);
										DonDownload = true;
									}else {
										DonDownload = false;
									}
									break;
							}

							
						}
						if(acy_type==2){
				            tv_notice_detail_comment_count.setVisibility(View.VISIBLE);
				         }else
				         {
				           tv_notice_detail_comment_count.setVisibility(View.GONE);
				         }
						list = notice.getList();
						 if (list.size()==0) {
							 Cpk_Side_Notice_Comment_detail all=new Cpk_Side_Notice_Comment_detail();
		     					all.setUser_id("yuanding");
		     					list.add(all);
		     				}
						adapter.notifyDataSetChanged();
						//solveClashListView = new SolveClashListView();
						//solveClashListView.setListViewHeightBasedOnChildren(
								//lv_act_detail_comment, 1);
						pic_show();
						showLoadResult(false,true, false);
						 if (lv_act_detail_comment != null) {
							 lv_act_detail_comment.onRefreshComplete();
							 lv_act_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
							}
							demo_swiperefreshlayout.setRefreshing(false);
							repfresh = 0;
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
                        PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this,
                                msg);
                        showLoadResult(false,false, true);
                        }
                         if (lv_act_detail_comment != null) {
                             lv_act_detail_comment.onRefreshComplete();
                             lv_act_detail_comment.setMode(Mode.PULL_UP_TO_REFRESH);
                            }
                            demo_swiperefreshlayout.setRefreshing(false);
                            repfresh = 0;
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

	}
	

	/**
	 * 设置已读未读、发送成功失败布局文本内容
	 * @param notice 
	 * @param smsOrAppnotice  1 短信 2 图文
	 */
	private void  setSendStatusText(Cpk_Side_Notice_Detail notice,int smsOrAppnotice){
		if(1==smsOrAppnotice){
			String text_front = "["+notice.getStatus_text()+"]";
			String text_last = "手机短信";
			String status_text_color = notice.getStatus_text_color();													
			
			if(1==notice.getMsg_status()){
				//发送中
				btn_sending.setVisibility(View.VISIBLE);
				
				//更改标题前缀文本颜色
				SpannableStringBuilder builder = new SpannableStringBuilder(text_front+text_last);  		  
				ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(Color.parseColor(status_text_color));  
				builder.setSpan(greenSpan2, 0, text_front.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				tv_acy_detail_title.setText(builder); 
				
				
			}else if(8==notice.getMsg_status()){
				//发送完成
				btn_sending.setVisibility(View.GONE);
				
				//更改标题前缀文本颜色
				SpannableStringBuilder builder = new SpannableStringBuilder(text_front+text_last);  		  
				ForegroundColorSpan greenSpan2 = new ForegroundColorSpan(Color.parseColor(status_text_color));  
				builder.setSpan(greenSpan2, 0, text_front.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
				tv_acy_detail_title.setText(builder); 
				
				/**
				 * 显示接收成功失败按钮
				 */
				String isHaveSend = getIntent().getExtras().getString("isHaveSend");
				if(null!=isHaveSend && !"".equals(isHaveSend) && isHaveSend.equals("1")){									
					
					btn_unread.setText("接收失败 "+notice.getSms_failed()+"人");
					btn_readed.setText("接收成功 "+notice.getSms_success()+"人");
					
					btn_unread.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Intent intent1 = new Intent(B_Side_Notice_Main_Detail_0.this, B_Side_Notice_Failure_SmsList.class);
							intent1.putExtra("messageId", acy_detail_id); //图文未读
							startActivity(intent1);
							
						}
					});
					
					btn_readed.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View arg0) {
							Intent intent2 = new Intent(B_Side_Notice_Main_Detail_0.this, B_Side_Notice_Main_Detail_0_ReceiveResult_SmsNotice.class);
							intent2.putExtra("acy_type", "1"); //图文已读
							intent2.putExtra("messageId", acy_detail_id); //图文已读
							startActivity(intent2);
						}
					});
					
					layout_read_or_unread.setVisibility(View.VISIBLE);
					
				}								
				
			}else {
				tv_acy_detail_title.setText("手机短信");
			}
		} else if(0==smsOrAppnotice){
			String isHaveSend = getIntent().getExtras().getString("isHaveSend");
			if(null!=isHaveSend && !"".equals(isHaveSend) && isHaveSend.equals("1")){								
				
				btn_unread.setText("未读 "+notice.getUnread_num()+"人");
				btn_readed.setText("已读 "+notice.getRead_num()+"人");
				
				btn_unread.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent1 = new Intent(B_Side_Notice_Main_Detail_0.this, B_Side_Notice_Main_Detail_0_ReceiveResult_AppNotice2.class);
						intent1.putExtra("acy_type", 1); //图文未读
						intent1.putExtra("messageId", acy_detail_id); //图文未读
						startActivity(intent1);
						
					}
				});
				
				btn_readed.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent2 = new Intent(B_Side_Notice_Main_Detail_0.this, B_Side_Notice_Main_Detail_0_ReceiveResult_AppNotice2.class);
						intent2.putExtra("acy_type", 2); //图文已读
						intent2.putExtra("messageId", acy_detail_id); //图文已读
						startActivity(intent2);
					}
				}); 
				
				layout_read_or_unread.setVisibility(View.VISIBLE);
			}
		}
	}

	private void signUp(final String id) {
	    A_0_App.getInstance().showProgreDialog(
                B_Side_Notice_Main_Detail_0.this, "提交中",true);
		A_0_App.getApi().get_Acy_Apply(id, A_0_App.USER_TOKEN,
				new InterAcy_Apply() {

					@Override
					public void onSuccess() {
						if (isFinishing())
							return;
						A_0_App.getInstance().CancelProgreDialog(
								B_Side_Notice_Main_Detail_0.this);
						singResult(true);
						btn_sign_up_me
								.setBackgroundResource(R.drawable.navigationbar_text_button_normal);
						readData(id);
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
                                B_Side_Notice_Main_Detail_0.this);
                        PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this,
                                msg);
                        singResult(false);
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}

	// 上拉刷新初始化数据
	private void getMoreComment(String message_id, int page_no) {
		if (readCommentData)
			return;
		readCommentData = true;
		A_0_App.getApi().getSideNoticeCommentList(
				B_Side_Notice_Main_Detail_0.this, A_0_App.USER_TOKEN, message_id,
				String.valueOf(page_no), new InterSideNoticeCommentList() {

					public void onSuccess(
							List<Cpk_Side_Notice_Comment_detail> mList) {
						if (isFinishing())
							return;
						//A_0_App.getInstance().CancelProgreDialog(B_Side_Notice_Main_Detail_0.this);
						readCommentData = false;
						if (mList != null && mList.size() > 0) {
							have_read_page += 1;
							int totleSize = list.size();
							for (int i = 0; i < mList.size(); i++) {
								list.add(mList.get(i));
							}
							adapter.notifyDataSetChanged();
							//solveClashListView = new SolveClashListView();
							//solveClashListView
									//.setListViewHeightBasedOnChildren(
											//lv_act_detail_comment, 1);
							//lv_act_detail_comment.setSelection(totleSize + 1);
						} else {
							PubMehods.showToastStr(
									B_Side_Notice_Main_Detail_0.this, "没有更多了");
						}
						 if (lv_act_detail_comment!=null) {
								lv_act_detail_comment.onRefreshComplete();
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
                        //A_0_App.getInstance().CancelProgreDialog(B_Side_Notice_Main_Detail_0.this);
                        readCommentData = false;
                        PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this,
                                msg);
                         if (lv_act_detail_comment!=null) {
                                lv_act_detail_comment.onRefreshComplete();
                            }
                            repfresh=0;
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

	}

	private void commentAcy(String article_id, String log_id,String to_comment_id,String content) {
		A_0_App.getApi().add_Acy_Comment(article_id,log_id,to_comment_id, A_0_App.USER_TOKEN,
				content, new InterAdd_Acy_Comment() {
					@Override
					public void onSuccess() {
						if (isFinishing())
							return;
//						A_0_App.getInstance().CancelProgreDialog(
//								B_Side_Notice_Main_Detail_0.this);
						PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this,
								"评论成功");
						comment_id="";
						
						tv_acy_detail_comment_content.getText().clear();
						tv_acy_detail_comment_content.setHint("写评论");
						closeKeybord(tv_acy_detail_comment_content, B_Side_Notice_Main_Detail_0.this);
						readData(acy_detail_id);
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
                                B_Side_Notice_Main_Detail_0.this);
                        PubMehods.showToastStr(B_Side_Notice_Main_Detail_0.this,
                                msg);

                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

	}

	public class Mydapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (list != null)
				return list.size();
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
		public View getView(final int posi, View converView, ViewGroup arg2) {
			
			final ViewHolder holder;
			if (converView == null) {
				converView = LayoutInflater.from(B_Side_Notice_Main_Detail_0.this).inflate(R.layout.item_info_comment_more, null);
				holder = new ViewHolder();
				
				holder.iv_comment_user_por = (CircleImageView)converView.findViewById(R.id.iv_comment_user_por);
				holder.tv_comment_user_name = (TextView)converView.findViewById(R.id.tv_comment_user_name);
				holder.tv_comment_user_content = (TextView)converView.findViewById(R.id.tv_comment_user_content);
				holder.tv_comment_user_time = (TextView)converView.findViewById(R.id.tv_comment_user_time);
				holder.tv_detaili_info_surname_count = (TextView)converView.findViewById(R.id.tv_detaili_info_surname_count);
				holder.liner_comment_more = (LinearLayout)converView.findViewById(R.id.liner_comment_more);
				holder.liner_info = (LinearLayout)converView.findViewById(R.id.liner_info);
				holder.liner_detail_info_count = (LinearLayout)converView.findViewById(R.id.liner_detail_info_count);
				holder.iv_detaili_info_surname_count = (ImageView)converView.findViewById(R.id.iv_detaili_info_surname_count);
				holder.rela_itme_info=(RelativeLayout)converView.findViewById(R.id.rela_itme_info);
				holder.rela_itme_info_down=(RelativeLayout)converView.findViewById(R.id.rela_itme_info_down);
				holder.tv_one = (TextView)converView.findViewById(R.id.tv_one);
				holder.tv_comment_user_order = (TextView)converView.findViewById(R.id.tv_comment_user_order);
				converView.setTag(holder);
			}else{
				holder = (ViewHolder)converView.getTag();
			}
			
			holder.iv_detaili_info_surname_count.setBackgroundResource(R.drawable.icon_notice_detail_repaly);
			if (list.size()==1&&list.get(posi).getUser_id().equals("yuanding")) {
				holder.rela_itme_info.setVisibility(View.GONE);

			}else
			{
				holder.rela_itme_info_down.setVisibility(View.VISIBLE);
				holder.rela_itme_info.setVisibility(View.VISIBLE);
			if (posi % 8 == 0) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_one);
				
			} else if (posi % 8 == 1) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_two);
			} else if (posi % 8 == 2) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_three);
			} else if (posi % 8 == 3) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_four);
			} else if (posi % 8 == 4) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_five);
			} else if (posi % 8 == 5) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_six);
			} else if (posi % 8 == 6) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_seven);
			} else if (posi % 8 == 7) {
				holder.iv_comment_user_por.setBackgroundResource(R.drawable.photo_eight);
			}
			String uri = list.get(posi).getPhoto_url();
			if(holder.iv_comment_user_por.getTag() == null){
				PubMehods.loadServicePic(imageLoader,uri,holder.iv_comment_user_por, options);
			    holder.iv_comment_user_por.setTag(uri);
			}else{ 
			    if(!holder.iv_comment_user_por.getTag().equals(uri)){
			    	PubMehods.loadServicePic(imageLoader,uri,holder.iv_comment_user_por, options);
			        holder.iv_comment_user_por.setTag(uri);
			    }
			}
			//bitmapUtils.display(holder.iv_comment_user_por, list.get(posi).getPhoto_url());
			holder.tv_comment_user_name.setText(list.get(posi).getName());
			holder.tv_comment_user_content.setText(list.get(posi).getContent());
			holder.tv_comment_user_time.setText(PubMehods.getTimeDifference(severTime,Long.valueOf(list.get(posi).getCreate_time())*1000));
			if (list.get(posi).getReply_comment_list()!=null&&!list.get(posi).getReply_comment_list().equals("")) {
				holder.liner_comment_more.removeAllViews();
				List<Cpk_Info_Detail_Content_More>	 comment_List_All=new ArrayList<Cpk_Info_Detail_Content_More>();
			comment_List_All=JSON.parseArray(list.get(posi).getReply_comment_list(), Cpk_Info_Detail_Content_More.class);
			for (int i = 0; i < comment_List_All.size(); i++) {
				View view = LayoutInflater.from(B_Side_Notice_Main_Detail_0.this).inflate(R.layout.item_side_notice_detail_more, null);
				TextView textView1=(TextView) view.findViewById(R.id.tv_side_more_time);
				TextView textView2=(TextView) view.findViewById(R.id.tv_side_more_content);
				textView1.setText(PubMehods.getTimeDifference(severTime,Long.valueOf(comment_List_All.get(i).getCreate_time())*1000));
				textView2.setText(comment_List_All.get(i).getContent());
				holder.liner_comment_more.addView(view);
				}
//			if (temp!=null&&!temp.equals("")) {
//					holder.liner_info .setVisibility(View.VISIBLE);
//					SpannableStringBuilder builder = new SpannableStringBuilder("发信者回复");  
//					ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.info_green_name));  
//					builder.setSpan(greenSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
//					holder.tv_comment_reply_name.setText(builder);
//					holder.tv_comment_reply_content.setText("    "+temp);
//				}else{
//					holder.liner_info .setVisibility(View.GONE);
//				}
			}else{
				holder.liner_info .setVisibility(View.GONE);
			}
			}
			holder.liner_detail_info_count.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					comment_id=list.get(posi).getReply_id();
					
					tv_acy_detail_comment_content.setHint("回复@"+list.get(posi).getName());
					tv_acy_detail_comment_content.setFocusable(true);
					tv_acy_detail_comment_content.requestFocus();
					openKeybord(tv_acy_detail_comment_content, B_Side_Notice_Main_Detail_0.this);
				}
			});
			return converView;
		}

	}

	class ViewHolder {
		CircleImageView iv_comment_user_por;
		TextView tv_comment_user_name;
		TextView tv_comment_user_content;
		TextView tv_comment_user_time,tv_detaili_info_surname_count;
		LinearLayout liner_info,liner_detail_info_count;
		ImageView iv_detaili_info_surname_count;
		TextView tv_one;
		RelativeLayout rela_itme_info,rela_itme_info_down;
		TextView tv_comment_user_order;
		LinearLayout liner_comment_more;
	}

	 @SuppressLint("ClickableViewAccessibility")
		public static void openKeybord(EditText mEditText, Context mContext)
		    {
		        InputMethodManager imm = (InputMethodManager) mContext
		                .getSystemService(Context.INPUT_METHOD_SERVICE);
		        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
		        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
		                InputMethodManager.HIDE_IMPLICIT_ONLY);
		    }
		 public static void closeKeybord(EditText mEditText, Context mContext)
		    {
			 InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			 
		        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
		    }
		
	
	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			goData();
			finish();
			overridePendingTransition(R.anim.animal_push_right_in_normal,
					R.anim.animal_push_right_out_normal);
			break;
		case ZUI_RIGHT_BUTTON:
			showWindow(v);
			break;
		default:
			break;
		}
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

	public void singResult(boolean success) {
		final GeneralDialog upDateDialog = new GeneralDialog(
				B_Side_Notice_Main_Detail_0.this, R.style.Theme_GeneralDialog);
		upDateDialog.setTitle(R.string.pub_title);
		if (success) {
			upDateDialog.setContent("恭喜你报名成功");
			upDateDialog.showMiddleButton(R.string.pub_sure,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							upDateDialog.cancel();
						}
					});
			upDateDialog.show();
		} else {
			upDateDialog.setContent("服务器开了小差，报名失败");
			upDateDialog.showMiddleButton(R.string.pub_sure,
					new OnClickListener() {
						@Override
						public void onClick(View v) {
							upDateDialog.cancel();
						}
					});
			upDateDialog.show();
		}
	}

	private void goData() {/*
							 * Intent it = new Intent();
							 * it.putExtra("comment_count"
							 * ,detail_Notice.getComment_num());
							 * it.putExtra("join_count",
							 * detail_Notice.getJoin_num()); setResult(1, it);
							 */
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
        	
        	readData(acy_detail_id);
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Notice_Main_Detail_0.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Notice_Main_Detail_0.this, AppStrStatic.kicked_offline());
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

		if (list != null) {
			list.clear();
			list = null;
		}
		drawable.stop();
		drawable=null;
		adapter = null;
		detail_Notice = null;
		bitmapUtils=null;
		bitmapUtilsBanner=null;
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if(!firstLoad){
			have_read_page = 1;
			readData(acy_detail_id);
		}else{
			firstLoad = false;
		}
	}
}