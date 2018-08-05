package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Persion_Contact;
import com.yuanding.schoolteacher.service.Api.InterAttenceClassUserList;
import com.yuanding.schoolteacher.service.Api.InterSideAttdenceStatus;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.CircleImageView;
import com.yuanding.schoolteacher.view.contact.CharacterParser;
import com.yuanding.schoolteacher.view.contact.PinyinComparator;
import com.yuanding.schoolteacher.view.contact.SideBar;
import com.yuanding.schoolteacher.view.pulltorefresh.PullToRefreshBase.Mode;
import com.yuanding.schoolteacher.view.refreshtools.PullToRefreshListView;
import com.yuanding.schoolteacher.view.refreshtools.SimpleSwipeRefreshLayout;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年1月6日 下午12:56:10 班级列表详情
 */
public class B_Side_Attence_Main_Class_People extends A_0_CpkBaseTitle_Navi
		implements SideBar.OnTouchingLetterChangedListener, TextWatcher {

	private View mLinerReadDataError, mLinerNoContent, liner_class_contact1,
			mLinerLoading;
	// 通讯录
	private SideBar mSideBar;
	private TextView mDialog;
	private PullToRefreshListView mListView;
	private EditText mSearchInput;
	private CharacterParser characterParser;// 汉字转拼音
	private PinyinComparator pinyinComparator;// 根据拼音来排列ListView里面的数据类
	private List<Cpk_Persion_Contact> sortDataList = new ArrayList<Cpk_Persion_Contact>();
	private SchoolFriendMemberListAdapter mAdapter;

	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String contact_stu_id = "";
	private String click_id = "";
	private String check_id = "";
	private String select_name = "";
	private String class_name = "";
	private String check_name = "";
	private boolean havaSuccessLoadData = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			A_0_App.student_list_flag = false;
		};
	};

	private ACache maACache;
	private JSONObject jsonObject;
	/**
	 * 新增下拉使用
	 */
	private SimpleSwipeRefreshLayout demo_swiperefreshlayout;
	private int repfresh = 0;// 避免下拉和上拉冲突
	private LinearLayout home_load_loading;
	private AnimationDrawable drawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_contact_student_list);
		handler.sendEmptyMessageDelayed(22, 2000);

		setZuiRightBtn(R.drawable.navigationbar_save);

		contact_stu_id = getIntent().getStringExtra("organ_id");
		class_name = getIntent().getStringExtra("class_name");
		check_name=getIntent().getStringExtra("name");
		setTitleText(class_name);
		demo_swiperefreshlayout = (SimpleSwipeRefreshLayout) findViewById(R.id.demo_swiperefreshlayout); // 新增下拉使用
		liner_class_contact1 = findViewById(R.id.liner_class_contact15);
		mLinerReadDataError = findViewById(R.id.contact_stu_list_acy_load_error);
		mLinerNoContent = findViewById(R.id.contact_stu_list_no_content);
		mLinerLoading = findViewById(R.id.contact_stu_list_loading);
		ImageView iv_blank_por = (ImageView) mLinerNoContent
				.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView) mLinerNoContent
				.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_tongxunlu);
		tv_blank_name.setText("暂无联系人~");

		home_load_loading = (LinearLayout) mLinerLoading
				.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start();

		imageLoader = A_0_App.getInstance().getimageLoader();
		options = A_0_App.getInstance().getOptions(
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center,
				R.drawable.ic_defalut_person_center);

		mListView = (PullToRefreshListView) findViewById(R.id.school_friend_member);
		// mListView.setMode(Mode.BOTH);
		mSideBar = (SideBar) findViewById(R.id.school_friend_sidrbar);
		mSideBar.setVisibility(View.GONE);
		mDialog = (TextView) findViewById(R.id.school_friend_dialog);
		mSearchInput = (EditText) findViewById(R.id.school_friend_member_search_input);

		mSideBar.setTextView(mDialog);
		mSideBar.setOnTouchingLetterChangedListener(this);

		/**
		 * 新增下拉使用 new add
		 */
		demo_swiperefreshlayout.setSize(SwipeRefreshLayout.DEFAULT);
		demo_swiperefreshlayout.setColorSchemeResources(R.color.main_color);
		if (repfresh == 0) {
			repfresh = 1;
			mListView.onRefreshComplete();
			demo_swiperefreshlayout
					.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
						public void onRefresh() {
							mListView.setMode(Mode.DISABLED);
							initData();

						};
					});
		}

		mListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				if (demo_swiperefreshlayout != null
						&& mListView.getChildCount() > 0
						&& mListView.getRefreshableView()
								.getFirstVisiblePosition() == 0
						&& mListView.getChildAt(0).getTop() >= mListView
								.getPaddingTop()) {
					// 解决滑动冲突，当滑动到第一个item，下拉刷新才起作用
					demo_swiperefreshlayout.setEnabled(true);
				} else {
					demo_swiperefreshlayout.setEnabled(false);
				}

			}

			@Override
			public void onScroll(AbsListView arg0, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		mLinerReadDataError.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				showLoadResult(true, false, false, false);
				initData();
			}
		});

		mSearchInput.addTextChangedListener(this);
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();

		sortDataList.clear();
		mAdapter = new SchoolFriendMemberListAdapter(
				B_Side_Attence_Main_Class_People.this, sortDataList);
		mListView.setAdapter(mAdapter);
		readCache();
		// initData();
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
			startListtenerRongYun();// 监听融云网络变化
		}
	}

	private void readCache() {
		maACache = ACache.get(this);
		jsonObject = maACache
				.getAsJSONObject(AppStrStatic.cache_key_attence_class_people_list
						+ A_0_App.USER_UNIQID + contact_stu_id);
		if (jsonObject != null
				&& !A_0_App.getInstance().getNetWorkManager()
						.isNetWorkConnected()) {// 说明有缓存
			showInfo(jsonObject);
		}else{
		updateInfo();}
	}

	private void showInfo(JSONObject jsonObject) {
		try {
			havaSuccessLoadData = true;   
			int state = jsonObject.optInt("status");
			List<Cpk_Persion_Contact> mlistContact = new ArrayList<Cpk_Persion_Contact>();
			if (state == 1) {
				mlistContact = JSON.parseArray(jsonObject.optJSONArray("list")
						+ "", Cpk_Persion_Contact.class);
			}
			if (isFinishing())
				return;
			if (mlistContact != null && mlistContact.size() > 0) {
				clearBusinessList(false);
				sortDataList.addAll(mlistContact);
				fillData(sortDataList);
				// 根据a-z进行排序源数据
				Collections.sort(sortDataList, pinyinComparator);
				mAdapter.notifyDataSetChanged();
				showLoadResult(false, true, false, false);
				showTitleBt(ZUI_RIGHT_BUTTON, true);
			} else {
				showLoadResult(false, false, false, true);

			}
			demo_swiperefreshlayout.setRefreshing(false);
			if (null != mListView) {
				mListView.onRefreshComplete();
				mListView.setMode(Mode.DISABLED);
			}
			repfresh = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearBusinessList(boolean setNull) {
		if (sortDataList != null && sortDataList.size() > 0) {
			sortDataList.clear();
			if (setNull)
				sortDataList = null;
		}
	}

	private void updateInfo() {
		MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
		updateLectureInfo.execute();
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {
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

			initData();
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

	private void showLoadResult(boolean loading, boolean list,
			boolean loadFaile, boolean noData) {
		if (loading) {
			drawable.start();
			mLinerLoading.setVisibility(View.VISIBLE);
		} else {
			if (drawable != null) {
				drawable.stop();
			}
			mLinerLoading.setVisibility(View.GONE);
		}
		if (list)
			liner_class_contact1.setVisibility(View.VISIBLE);
		else
			liner_class_contact1.setVisibility(View.GONE);

		if (loadFaile)
			mLinerReadDataError.setVisibility(View.VISIBLE);
		else
			mLinerReadDataError.setVisibility(View.GONE);

		if (noData)
			mLinerNoContent.setVisibility(View.VISIBLE);
		else
			mLinerNoContent.setVisibility(View.GONE);
	}

	/**************************************** 通讯录 ****************************************/
	/**
	 * 初始化数据
	 */
	private void initData() {

		A_0_App.getApi().getAttenceClassUserList(
				B_Side_Attence_Main_Class_People.this, A_0_App.USER_TOKEN,
				contact_stu_id, "", new InterAttenceClassUserList() {
					@Override
					public void onSuccess(List<Cpk_Persion_Contact> mList) {
						if (sortDataList == null)
							return;
						havaSuccessLoadData = true;   
						if (mList != null && mList.size() > 0) {
							sortDataList.clear();
							sortDataList.addAll(mList);
							fillData(sortDataList);
							// 根据a-z进行排序源数据
							Collections.sort(sortDataList, pinyinComparator);
							mAdapter.notifyDataSetChanged();
							showLoadResult(false, true, false, false);
							showTitleBt(ZUI_RIGHT_BUTTON, true);
						} else {
							mAdapter.notifyDataSetChanged();
							showLoadResult(false, false, false, true);
						}

						demo_swiperefreshlayout.setRefreshing(false);
						if (null != mListView) {
							mListView.onRefreshComplete();
							mListView.setMode(Mode.DISABLED);
						}
						repfresh = 0;
					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (sortDataList == null)
                            return;
                        PubMehods.showToastStr(
                                B_Side_Attence_Main_Class_People.this, msg);
                        if(!havaSuccessLoadData){
                            showLoadResult(false, false, true, false);
                        }
                        

                        demo_swiperefreshlayout.setRefreshing(false);
                        if (null != mListView) {
                            mListView.onRefreshComplete();
                            mListView.setMode(Mode.DISABLED);
                        }
                        repfresh = 0;
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

	}

	private void commit() {

		A_0_App.getApi().SidePostSendAttdence(click_id, contact_stu_id,
				new InterSideAttdenceStatus() {

					@Override
					public void onSuccess(String message) {
						if (isFinishing())
							return;
						if (sortDataList == null)
							return;
						if (message != null && !"".equals(message))
                            PubMehods.showToastStr(B_Side_Attence_Main_Class_People.this, message);
						Intent data = new Intent();
						data.putExtra("organ_name", select_name);
						B_Side_Attence_Main_Class_People.this
								.setResult(1, data);
						finish();
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
                        if (sortDataList == null)
                            return;
                        PubMehods.showToastStr(
                                B_Side_Attence_Main_Class_People.this, msg);
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

	}

	@Override
	public void onTouchingLetterChanged(String s) {
		int position = 0;
		// 该字母首次出现的位置
		if (mAdapter != null) {
			position = mAdapter.getPositionForSection(s.charAt(0));
		}
		if (position != -1) {
			mListView.getRefreshableView().setSelection(position);
			// mListView.setSelection(position);
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
		filterData(s.toString(), sortDataList);
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr, List<Cpk_Persion_Contact> list) {
		List<Cpk_Persion_Contact> filterDateList = new ArrayList<Cpk_Persion_Contact>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = list;
		} else {
			filterDateList.clear();
			for (Cpk_Persion_Contact sortModel : list) {
				String name = sortModel.getName();
				String suoxie = sortModel.getSuoxie();
				if (name.indexOf(filterStr.toString()) != -1
						|| suoxie.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		mAdapter.updateListView(filterDateList);
	}

	/**
	 * 填充数据
	 * 
	 * @param list
	 */
	private void fillData(List<Cpk_Persion_Contact> list) {
		for (Cpk_Persion_Contact cUserInfoDto : list) {
			if (cUserInfoDto != null && cUserInfoDto.getName() != null) {
				String pinyin = characterParser.getSelling(cUserInfoDto
						.getName());
				String suoxie = CharacterParser.getFirstSpell(cUserInfoDto
						.getName());

				cUserInfoDto.setSuoxie(suoxie);
				String sortString = pinyin.substring(0, 1).toUpperCase();

				if ("1".equals(cUserInfoDto.getUtype())) {// 判断是否是管理员
					cUserInfoDto.setSortLetters("☆");
				} else if (sortString.matches("[A-Z]")) {// 正则表达式，判断首字母是否是英文字母
					cUserInfoDto.setSortLetters(sortString);
				} else {
					cUserInfoDto.setSortLetters("#");
				}
			}
		}
	}

	/**
	 * 成员列表适配器
	 */
	public class SchoolFriendMemberListAdapter extends BaseAdapter implements
			SectionIndexer {

		private LayoutInflater inflater;

		private Activity mActivity;

		private List<Cpk_Persion_Contact> list;

		public SchoolFriendMemberListAdapter(Activity mActivity,
				List<Cpk_Persion_Contact> list) {
			this.mActivity = mActivity;
			this.list = list;
		}

		/**
		 * 当ListView数据发生变化时,调用此方法来更新ListView
		 * 
		 * @param list
		 */
		public void updateListView(List<Cpk_Persion_Contact> list) {
			this.list = list;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				inflater = (LayoutInflater) mActivity
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.items_contact_list,
						null);
				holder = new ViewHolder();
				holder.ivHead = (CircleImageView) convertView
						.findViewById(R.id.iv_contact_por);
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_contact_name);
				holder.tvLetter = (TextView) convertView
						.findViewById(R.id.catalog);
				holder.tvContent = (RelativeLayout) convertView
						.findViewById(R.id.content);
				holder.iv_contact_select = (ImageView) convertView
						.findViewById(R.id.iv_contact_select);
				holder.liner_iv_select = (LinearLayout) convertView
						.findViewById(R.id.liner_iv_select);
				convertView.setTag(holder);
			} else {
				convertView.clearAnimation();
				holder = (ViewHolder) convertView.getTag();
			}
			holder.iv_contact_select.setVisibility(View.VISIBLE);
			final Cpk_Persion_Contact dto = list.get(position);

			if (dto != null) {
				// 根据position获取分类的首字母的Char ascii值
				int section = getSectionForPosition(position);
				// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
				if (position == getPositionForSection(section)) {
					holder.tvLetter.setVisibility(View.VISIBLE);
					holder.tvLetter
							.setText("☆".equals(dto.getSortLetters()) ? dto
									.getSortLetters() + "(管理员)" : dto
									.getSortLetters());
				} else {
					holder.tvLetter.setVisibility(View.GONE);
				}
				holder.tvTitle.setText(dto.getName());
				if (position % 8 == 0) {
					holder.ivHead.setBackgroundResource(R.drawable.photo_one);

				} else if (position % 8 == 1) {
					holder.ivHead.setBackgroundResource(R.drawable.photo_two);
				} else if (position % 8 == 2) {
					holder.ivHead.setBackgroundResource(R.drawable.photo_three);
				} else if (position % 8 == 3) {
					holder.ivHead.setBackgroundResource(R.drawable.photo_four);
				} else if (position % 8 == 4) {
					holder.ivHead.setBackgroundResource(R.drawable.photo_five);
				} else if (position % 8 == 5) {
					holder.ivHead.setBackgroundResource(R.drawable.photo_six);
				} else if (position % 8 == 6) {
					holder.ivHead.setBackgroundResource(R.drawable.photo_seven);
				} else if (position % 8 == 7) {
					holder.ivHead.setBackgroundResource(R.drawable.photo_eight);
				}
				String uri = dto.getPhoto_url();
			
					if (holder.ivHead.getTag() == null) {
						PubMehods.loadServicePic(imageLoader, uri,
								holder.ivHead, options);
						holder.ivHead.setTag(uri);
					} else {
						if (!holder.ivHead.getTag().equals(uri)) {
							PubMehods.loadServicePic(imageLoader, uri,
									holder.ivHead, options);
							holder.ivHead.setTag(uri);
						}
					}
				
				
				if (list.get(position).getId().equals(click_id)) {
					holder.iv_contact_select
							.setBackgroundResource(R.drawable.button_radio_selected);
				} else {
					if (list.get(position).getName().equals(check_name)) {
						check_id=list.get(position).getId();
						holder.iv_contact_select.setBackgroundResource(R.drawable.button_radio_selected);
					}else{
						holder.iv_contact_select.setBackgroundResource(R.drawable.button_radio);
					}
				}
				
				// bitmapUtils.display(holder.ivHead, dto.getPhoto_url());
				holder.tvContent.setTag(dto);
				holder.tvContent.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Cpk_Persion_Contact dd = (Cpk_Persion_Contact) v
								.getTag();
						Intent intent = new Intent(
								B_Side_Attence_Main_Class_People.this,
								B_Mess_Persion_Info.class);
						intent.putExtra("uniqid", dd.getUniqid());
						startActivity(intent);
					}
				});
				holder.liner_iv_select.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								check_name="";
									
									if (list.get(position).getId().equals(click_id)) {
										click_id = "";
										select_name = "";
									} else {
										if (list.get(position).getId().equals(check_id)) {
											click_id = "";
											select_name = "";
											check_id="";
										}else{
										click_id = list.get(position).getId();
										select_name = list.get(position).getName();}
									}
									mAdapter.notifyDataSetChanged();
								
							}
						});
			}
			if (A_0_App.isShowAnimation == true) {
				if (position > A_0_App.student_list_curPosi
						|| A_0_App.student_list_flag)

				{
					A_0_App.student_list_curPosi = position;
					Animation an = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 1,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0);
					an.setDuration(400);
					an.setStartOffset(50 * position);
					convertView.startAnimation(an);
				}
			}

			return convertView;
		}

		class ViewHolder {
			CircleImageView ivHead;
			TextView tvLetter;
			TextView tvTitle;
			ImageView iv_contact_select;
			RelativeLayout tvContent;
			LinearLayout liner_iv_select;
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		public int getSectionForPosition(int position) {
			return list.get(position).getSortLetters().charAt(0);
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = list.get(i).getSortLetters();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}

		@Override
		public Object[] getSections() {
			return null;
		}

	}

	/**
	 * 设置连接状态变化的监听器.
	 */
	public void startListtenerRongYun() {
		RongIM.getInstance();
		RongIM.setConnectionStatusListener(new MyConnectionStatusListener());
	}

	private class MyConnectionStatusListener implements
			RongIMClient.ConnectionStatusListener {
		@SuppressWarnings("incomplete-switch")
		@Override
		public void onChanged(ConnectionStatus connectionStatus) {

			switch (connectionStatus) {
			case CONNECTED:// 连接成功。
				A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
				break;
			case DISCONNECTED:// 断开连接。
				A_Main_My_Message_Acy
						.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
				// A_0_App.getInstance().showExitDialog(B_Contact_Main_Colleague_List.this,getResources().getString(R.string.token_timeout));
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
								B_Side_Attence_Main_Class_People.this,
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
	public void onDestroy() {
		if (sortDataList != null) {
			sortDataList.clear();
			sortDataList = null;
		}
		drawable.stop();
		drawable = null;
		super.onDestroy();
	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			this.finish();
			break;
		case ZUI_RIGHT_BUTTON:
			commit();
//			if (!select_name.equals("")) {
//				if (select_name.equals(getIntent().getStringExtra("name"))) {
//					finish();
//				}else{
//					commit();
//				}
//
//			} else {
//				if (getIntent().getStringExtra("name").equals("")) {
//					PubMehods.showToastStr(B_Side_Attence_Main_Class_People.this,
//							"请选择委派人！");
//				}else{
//					if (check_name.equals("")) {
//						PubMehods.showToastStr(B_Side_Attence_Main_Class_People.this,
//								"请选择委派人！");
//					}else{
//						finish();
//					}
//
//				}
//
//
//
//			}

			break;
		default:
			break;
		}

	}
}
