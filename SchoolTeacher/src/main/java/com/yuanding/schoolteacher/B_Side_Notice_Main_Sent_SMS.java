package com.yuanding.schoolteacher;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuanding.schoolteacher.service.Api.InterSideNoticeSignList;
import com.yuanding.schoolteacher.service.Api.InterSideSaveSmsSent;
import com.yuanding.schoolteacher.service.Api.InterSideSmsSent;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.Bimp;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.yuanding.schoolteacher.view.date.SlideDateTimeListener;
import com.yuanding.schoolteacher.view.date.SlideDateTimePicker;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月4日 上午10:46:19 
 * 写短消息页面
 */
public class B_Side_Notice_Main_Sent_SMS extends FragmentActivity {

	public static final int STATE_RESULT = 5;
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private EditText  tv_time, tv_sign;
	private Button btn_sent;
	private LinearLayout liner_titlebar_back;
	private String st_time, st_sign, st_content;
	private EditText sent_notice_content,tv_contacts;
	private String userids = "", classids = "", organids = "",st_re="",groupids="",search_id="";
	private String date;
	private Button btn_select_contacts;
	private RelativeLayout rl_sent_time,rl_sent_sign;

	/**
	 * 签名
	 */
	private MySignAdapter adapter;
	private List<String> mClassList = new ArrayList<String>();
	private SharedPreferences sp;

	/**
	 * status 0,发送，1 ，保存
	 */
	private int status;
	private int dialog_show = 0;

	private int first=0;//第一次加载
	private int limit=280;
	private int content_limit;
	private LinearLayout sent_notice_liner;
	private TextView sent_notice_tv;
	private long stime=0;
	private Editor editor;
	private String  message_id,content;
	private int enter_acy_type;//enter_acy_type,首页快捷方式进入为1，身边通知进入为2
	private String is_sure="0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_side_notice_sent_sms);
		sp = getSharedPreferences("wxb", Context.MODE_PRIVATE);
		A_0_App.summit=0;
		sent_notice_content = (EditText) findViewById(R.id.sent_notice_content);
		tv_contacts = (EditText) findViewById(R.id.tv_contacts);
		tv_time = (EditText) findViewById(R.id.tv_time);
		tv_sign = (EditText) findViewById(R.id.tv_sign);
		btn_sent = (Button) findViewById(R.id.btn_sent);
		btn_select_contacts=(Button) findViewById(R.id.btn_select_contacts);
		liner_titlebar_back = (LinearLayout) findViewById(R.id.liner_titlebar_back);
		sent_notice_liner = (LinearLayout) findViewById(R.id.sent_notice_liner);
		sent_notice_tv = (TextView) findViewById(R.id.sent_notice_tv);
        rl_sent_sign=(RelativeLayout) findViewById(R.id.rl_sent_sign);
        rl_sent_time=(RelativeLayout) findViewById(R.id.rl_notice_time);
		liner_titlebar_back.setOnClickListener(onClick);
		//tv_contacts.setOnClickListener(onClick);
		btn_select_contacts.setOnClickListener(onClick);
		tv_time.setOnClickListener(onClick);
		tv_sign.setOnClickListener(onClick);
		btn_sent.setOnClickListener(onClick);
		date = sdf.format(new Date());
		if (Bimp.found_date == "") {
			st_time = "";

		} else {
			st_time = Bimp.found_date;
			tv_time.setText(Bimp.found_date);
		}
		tv_time.setInputType(InputType.TYPE_NULL);
		tv_sign.setInputType(InputType.TYPE_NULL);
//		tv_contacts.setInputType(InputType.TYPE_NULL);
		Intent intent = getIntent();

		enter_acy_type = intent.getExtras().getInt("enter_acy_type");
        if (enter_acy_type == 0) {
            enter_acy_type = 2;
        }
		message_id = intent.getStringExtra("acy_detail_id");
		content = intent.getStringExtra("content");
        if (message_id == null || content == null) {
            message_id = "";
            content = "";
        }
		sent_notice_content.setText(content);
		tv_sign.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					rl_sent_sign.setBackgroundResource(R.drawable.login_input_hover_bg);
					dialog_show = 1;
					first=1;
					if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_MAIN_TIME)) {
						getData();
					}
				}else
				{
					rl_sent_sign.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});
		tv_time.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				{
					rl_sent_time.setBackgroundResource(R.drawable.login_input_hover_bg);
					date_choose();
				}else
				{
					rl_sent_time.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});
		sent_notice_content.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1)
				{
					sent_notice_liner.setBackgroundResource(R.drawable.side_remark_hover_bg);
					
				}else
				{
					sent_notice_liner.setBackgroundResource(R.drawable.side_remark_normal_bg);
				}
			}
		});
		sent_notice_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

//				if (arg0.length() > 0) {
//					btn_sent.setBackgroundResource(R.drawable.checked);
//				} else {
//					btn_sent.setBackgroundResource(R.drawable.unchecked);
//				}
				if (content_limit<arg0.length()) {
					sent_notice_content.setText(sent_notice_content.getText().toString().substring(0, content_limit));
					sent_notice_content.setSelection(content_limit);
					sent_notice_tv.setText("还可以输入0字");
				}else{
					sent_notice_tv.setText("还可以输入"+(content_limit-arg0.length())+"字");
				}

				
			}
		
		});
		/**
		 * 过滤表情
		 */
//		sent_notice_content.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				
//			}
//			
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//					int arg3) {
//				
//			}
//			
//			@Override
//			public void afterTextChanged(Editable editable) {
//				  int index = sent_notice_content.getSelectionStart() - 1;
//				                  if (index > 0) {
//				                     if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
//				                        Editable edit = sent_notice_content.getText();
//				                          edit.delete(index, index + 1);
//				                     }
//				                 }
//			}
//		});
		if (sp.getString(A_0_App.USER_UNIQID+"sms", "")!=null&&!sp.getString(A_0_App.USER_UNIQID+"sms", "").equals("")) {
			String sign=sp.getString(A_0_App.USER_UNIQID+"sms", "");
			content_limit=limit-sign.length()-sent_notice_content.getText().toString().length();
			sent_notice_tv.setText("还可以输入"+content_limit+"字");
			//sent_notice_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(content_limit-sent_notice_content.getText().toString().length())});
			tv_sign.setText(sign);
		} 
			getData();
		
		
	}
	
	OnClickListener onClick = new OnClickListener() {

		@SuppressLint("ResourceAsColor")
		@Override
		public void onClick(View v) {
			final Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn_select_contacts:// 添加联系人
//				intent.setClass(B_Side_Notice_Main_Sent_SMS.this,
//						B_Side_Notice_Main_Add_Contacts.class);
//				intent.putExtra("iswhite", "0");
//				startActivity(intent);
//				overridePendingTransition(R.anim.animal_push_left_in_normal,
//						R.anim.animal_push_left_out_normal);
				
				 intent.setClass(B_Side_Notice_Main_Sent_SMS.this, B_Side_Notice_Main_Add_Contacts2.class);
	                startActivityForResult(intent, 6);
				break;
			case R.id.tv_time:// 设置定时
				if (stime==0) {
					stime=sp.getLong("time", 0);
				}
				date_choose();
				break;
			case R.id.tv_sign:// 选择签名
				dialog_show = 1;
				first=1;
				if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_MAIN_TIME)) {
					getData();
				}
				
				break;

			case R.id.btn_sent:// 发送
				status = 0;
				if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
					sent(Bimp.type, message_id);
				} else {
//					PubMehods.showToastStr(B_Side_Notice_Main_Sent_SMS.this,
//							"请勿重复操作！");
				}
				

				break;
			case R.id.liner_titlebar_back:// 发送
				if (sent_notice_content.getText().toString().equals("")) {
					finish();
					overridePendingTransition(
							R.anim.animal_push_right_in_normal,
							R.anim.animal_push_right_out_normal);
				}else{
				if (status == 0&&!isFinishing()) {
					final GeneralDialog upDateDialog = new GeneralDialog(
							B_Side_Notice_Main_Sent_SMS.this,
							R.style.Theme_GeneralDialog);
					upDateDialog.setTitle(R.string.pub_title);
					upDateDialog.setContent("已填写的内容将丢失，或保存至草稿！");
					upDateDialog.showLeftButton(R.string.save_draft,
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									upDateDialog.cancel();

									if (!sent_notice_content.getText()
											.toString().equals("")) {
										status = 1;
										sent(Bimp.type, message_id);
									} else {
										if (sent_notice_content.getText()
												.toString().equals("")) {
											PubMehods
													.showToastStr(
															B_Side_Notice_Main_Sent_SMS.this,
															"内容不能为空");
											return;
										}
									}

								}
							});
					upDateDialog.showRightButton(R.string.pub_sure,
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									upDateDialog.cancel();
									finish();
									overridePendingTransition(
											R.anim.animal_push_right_in_normal,
											R.anim.animal_push_right_out_normal);
								}
							});
					upDateDialog.show();
				} else {
					finish();
					overridePendingTransition(R.anim.animal_push_right_in_normal,
							R.anim.animal_push_right_out_normal);
				}}
				break;
			}
			;
		}
	};

	/**
	 * 得到根Fragment
	 * 
	 * @return
	 */
	// private Fragment getRootFragment() {
	// Fragment fragment = get
	// while (fragment.getParentFragment() != null) {
	// fragment = fragment.getParentFragment();
	// }
	// return fragment;
	//
	// }

	// 时间选择监听
	private SlideDateTimeListener listener = new SlideDateTimeListener() {
		@Override
		public void onDateTimeSet(Date date) {
			String s = sdf.format(date);
			if ((date.getTime() - stime*1000) / 1000 <=2592000
					&& (date.getTime() - stime*1000) / 1000 > 560) {
				tv_time.setText(s);
				Bimp.found_date = s;
				Bimp.type = "2";
			} else if ((date.getTime() - stime*1000) / 1000 < 600&&(date.getTime() - stime*1000)/1000>-59) {
				Bimp.found_date = s;
				tv_time.setText(s);
				Bimp.type = "1";
				PubMehods.showToastStr(B_Side_Notice_Main_Sent_SMS.this,
						"定时发送需大于10分钟");
			} else if((date.getTime() - stime*1000) / 1000 > 2592000){
				PubMehods.showToastStr(B_Side_Notice_Main_Sent_SMS.this,
						"定时发送时间不能超过一个月");
			}else{
				PubMehods.showToastStr(B_Side_Notice_Main_Sent_SMS.this,
						"发送时间不能小于当前时间");
			}

		}

		@Override
		public void onDateTimeCancel() {
			st_time="";
			tv_time.setText("");
			Bimp.type = "1";
		}
	};

	/**
	 * 签名数据
	 */
	private void getData() {
		mClassList.clear();
		A_0_App.getApi().getSideNoticeSignList(
				B_Side_Notice_Main_Sent_SMS.this, A_0_App.USER_TOKEN,
				new InterSideNoticeSignList() {

					@Override
					public void onSuccess(String List,long severtime) {
						if (isFinishing())
							return;
						stime=severtime;
						editor = sp.edit();
						editor.putLong("time",severtime);
						editor.commit();
						String list = (List.replace("[", "")).replace("]", "")
								.replace("\"", "");
						String content[] = list.split(",");
						for (int i = 0; i < content.length; i++) {
							mClassList.add(content[i]);
						}
						if (dialog_show == 1) {
							sign_Dialog();
						} else {
							

							if (mClassList.size()>0) {
								if (sp.getString(A_0_App.USER_UNIQID + "sms", "") != null
										&& !sp.getString(A_0_App.USER_UNIQID + "sms", "").equals("")) {
									
									if (!list.contains(sp.getString(A_0_App.USER_UNIQID + "sms", ""))) {
										tv_sign.setText(mClassList.get(0));
										content_limit=limit-mClassList.get(0).length();
										sent_notice_tv.setText("还可以输入"+(content_limit-sent_notice_content.getText().toString().length())+"字");
										tv_sign.setText(mClassList.get(0));
										 editor = sp.edit();
										editor.putString(A_0_App.USER_UNIQID + "sms",
												mClassList.get(0));
										editor.commit();
									}
								} else{
									tv_sign.setText(mClassList.get(0));
									content_limit=limit-mClassList.get(0).length();
									sent_notice_tv.setText("还可以输入"+(content_limit-sent_notice_content.getText().toString().length())+"字");
								tv_sign.setText(mClassList.get(0));
								editor = sp.edit();
								editor.putString(A_0_App.USER_UNIQID + "",
										mClassList.get(0));
								editor.commit();}
							}
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
                        if (first!=0) {
                            PubMehods.showToastStr(B_Side_Notice_Main_Sent_SMS.this, msg);
                        }
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

	}

	/**
	 * 签名选择
	 */
	void sign_Dialog() {
		final Dialog dialog = new Dialog(B_Side_Notice_Main_Sent_SMS.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.activity_side_select_sign_dialog);
		LinearLayout liner=(LinearLayout) dialog.findViewById(R.id.liner_side_repair);
		/*
		 * Window window = dialog.getWindow();
		 * window.setGravity(Gravity.CENTER); //此处可以设置dialog显示的位置
		 * window.setWindowAnimations(R.style.mystyle); //添加动画
		 */dialog.show();
		ListView listView = (ListView) dialog
				.findViewById(R.id.lv_side_select_sign);
		adapter = new MySignAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				 tv_sign.setText(mClassList.get(arg2));
				 content_limit=limit-mClassList.get(arg2).length();
				
				 if (content_limit<sent_notice_content.getText().toString().length()) {
					 sent_notice_tv.setText("还可以输入0字");
					 sent_notice_content.setText(sent_notice_content.getText().toString().substring(0, content_limit));
				}else{
					 sent_notice_tv.setText("还可以输入"+(content_limit-sent_notice_content.getText().toString().length())+"字");
				}
				 //sent_notice_content.setFilters(new InputFilter[]{new InputFilter.LengthFilter(content_limit-sent_notice_content.getText().toString().length())});
				  Editor editor = sp.edit();
				  editor.putString(A_0_App.USER_UNIQID+"sms", mClassList.get(arg2));
				  editor.commit();
				  dialog.dismiss();
			}
		});
liner.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 
	 * @author MyPC签名adpter
	 * 
	 */
	// 加载列表数据
	public class MySignAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mClassList != null)
				return mClassList.size();
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
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
						B_Side_Notice_Main_Sent_SMS.this).inflate(
						R.layout.item_pub_text, null);
			}
			TextView tv_acy_name = (TextView) converView
					.findViewById(R.id.tv_item_pub_text);
			tv_acy_name.setText(mClassList.get(posi));
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

	/**
	 * 发送
	 */
	void sent(final String type, final String message_id) {
		if (type.equals("1")) {
			st_time = "";
		} else {
			st_time = tv_time.getText().toString();
		}

		st_sign = tv_sign.getText().toString();
		st_content = sent_notice_content.getText().toString();

		if (status == 0) {
			if (tv_contacts.getText().toString().equals("")) {
				PubMehods.showToastStr(B_Side_Notice_Main_Sent_SMS.this,
						"请选择收信人");
				return;
			}

			
			if (tv_sign.getText().toString().equals("")) {
				PubMehods
						.showToastStr(B_Side_Notice_Main_Sent_SMS.this, "签名为空");
				return;
			}
			if (sent_notice_content.getText().toString().equals("")) {
				PubMehods.showToastStr(B_Side_Notice_Main_Sent_SMS.this,
						"内容不能为空");
				return;
			}
			if (isFinishing())
                return;
            A_0_App.getInstance().showProgreDialog(B_Side_Notice_Main_Sent_SMS.this,"发送中，请稍候", true);
			A_0_App.getApi().SideSmsSent(A_0_App.USER_TOKEN, Bimp.notice_userids, Bimp.notice_classids,Bimp.notice_organids,Bimp.notice_groupids, st_time, st_sign, st_content, type, message_id,is_sure,
					new InterSideSmsSent() {

						@Override
						public void onSuccess(int state ,String msg) {
							if (isFinishing())
								return;
							A_0_App.getInstance().CancelProgreDialog(
									B_Side_Notice_Main_Sent_SMS.this);
							if (state==1){
								if (type.equals("1")) {
									A_0_App.SIDE_NOTICE = 0;
								} else {
									A_0_App.SIDE_NOTICE = 1;
								}
								Intent intent1 = new Intent("notice");
								sendBroadcast(intent1);

								PubMehods.showToastStr(
										B_Side_Notice_Main_Sent_SMS.this, "发送成功！");
								clear();
								if(enter_acy_type==1){
									Intent intent = new Intent();
									intent.setClass(B_Side_Notice_Main_Sent_SMS.this, B_Side_Notice_Main.class);
									intent.putExtra("enter_acy_type", 1);
									startActivity(intent);
									finish();
								}else if(enter_acy_type==2){
									finish();
								}
							}else if(state==2){
								final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Notice_Main_Sent_SMS.this,
										R.style.Theme_GeneralDialog);
								upDateDialog.setTitle(R.string.pub_title);
								upDateDialog.setContent(msg);
								upDateDialog.showMiddleButton(R.string.pub_sure, new OnClickListener() {
									@Override
									public void onClick(View v) {
										upDateDialog.cancel();
									}
								});
								upDateDialog.show();
							}else  if (state==3){
								{
									final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Notice_Main_Sent_SMS.this,
											R.style.Theme_GeneralDialog);
									upDateDialog.setTitle(R.string.pub_title);
									upDateDialog.setContent(msg);
									upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
										@Override
										public void onClick(View v) {
											upDateDialog.cancel();
										}
									});
									upDateDialog.showRightButton(R.string.pub_sent, new OnClickListener() {
										@Override
										public void onClick(View v) {
											is_sure="1";
											upDateDialog.cancel();
											sent(type,message_id);
										}
									});
									upDateDialog.show();
								}
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
                            A_0_App.getInstance().CancelProgreDialog(
                                    B_Side_Notice_Main_Sent_SMS.this);
                            PubMehods.showToastStr(
                                    B_Side_Notice_Main_Sent_SMS.this, msg);
                        }
                        
                        @Override
                        public void onCancelled() {
                            // TODO Auto-generated method stub
                            
                        }
                    });
		} else {
			if (sent_notice_content.getText().toString().equals("")) {
				PubMehods.showToastStr(B_Side_Notice_Main_Sent_SMS.this,
						"内容不能为空");
				return;
			}
			if (isFinishing())
                return;
            A_0_App.getInstance().showProgreDialog(B_Side_Notice_Main_Sent_SMS.this,"保存中，请稍候", true);
			A_0_App.getApi().SideSaveSmsSent(A_0_App.USER_TOKEN, userids,
					classids, organids, st_time, st_sign, st_content, type,
					message_id, new InterSideSaveSmsSent() {

						@Override
						public void onSuccess() {
						    if (isFinishing())
	                            return;
							A_0_App.SIDE_NOTICE = 2;
							Intent intent1 = new Intent("notice");
							sendBroadcast(intent1);
							A_0_App.getInstance().CancelProgreDialog(
									B_Side_Notice_Main_Sent_SMS.this);
							PubMehods.showToastStr(
									B_Side_Notice_Main_Sent_SMS.this, "保存成功！");
							if(enter_acy_type==1){
                                Intent intent = new Intent();
                                intent.setClass(B_Side_Notice_Main_Sent_SMS.this, B_Side_Notice_Main.class);
                                intent.putExtra("enter_acy_type", 1);
                                startActivity(intent);
                                finish();
                            }else if(enter_acy_type==2){
                                finish();
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
                            A_0_App.getInstance().CancelProgreDialog(B_Side_Notice_Main_Sent_SMS.this);
                            PubMehods.showToastStr(B_Side_Notice_Main_Sent_SMS.this, msg);
                        }
                        
                        @Override
                        public void onCancelled() {
                            // TODO Auto-generated method stub
                            
                        }
                    });
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {

			if(requestCode==6){
				
				A_0_App.colleague_result_Contacts_summit.addAll(A_0_App.colleague_result_Contacts);
				A_0_App.colleague_result_Contacts_summit.addAll(A_0_App.colleague_temp_Contacts);
				
				A_0_App.student_result_Contacts_summit.addAll(A_0_App.student_result_Contacts);
				A_0_App.student_result_Contacts_summit.addAll(A_0_App.student_temp_Contacts);
				
				A_0_App.group_result_Contacts_summit.addAll(A_0_App.group_result_Contacts);
				A_0_App.group_result_Contacts_summit.addAll(A_0_App.group_temp_Contacts);
				

				String names="";
				search_id="";
				for (int i = 0; i <A_0_App.notice_search_Contacts.size(); i++) {
					names=names+A_0_App.notice_search_Contacts.get(i).getTrue_name()+",";
					search_id=search_id+A_0_App.notice_search_Contacts.get(i).getUser_id()+",";
				}
				if (A_0_App.notice_search_Contacts.size()>0) {
					names=names.substring(0, names.length()-1);
					search_id=search_id.substring(0, search_id.length()-1);
				}

				st_re=data.getExtras().getString("name");
				//classids=data.getExtras().getString("classids");
				//userids=data.getExtras().getString("userids");
				//groupids=data.getExtras().getString("groupids");
				//organids=data.getExtras().getString("organids");
				tv_contacts.setText(st_re);

				userids="";
				classids="";
				groupids="";
				organids="";
				commit();
				if (userids.length()>0){
					userids=userids.substring(0,userids.length()-1);
				}
				if (st_re.length()>0&&st_re!="") {
					Bimp.found_name=st_re.substring(0, st_re.length()-1);
					tv_contacts.setText(Bimp.found_name+","+names);
				}else{
					Bimp.found_name="";
					tv_contacts.setText(Bimp.found_name+names);
				}
				if (classids.length()>0&&classids!="") {
					Bimp.notice_classids=classids.substring(0, classids.length()-1);
				}else {
					Bimp.notice_classids="";
				}
				if (userids.length()>0&&userids!="") {
					if (search_id.equals("")){
						Bimp.notice_userids=userids;
					}else {
						Bimp.notice_userids=userids.substring(0, userids.length()-1)+","+search_id;
					}

				}else{
					Bimp.notice_userids=search_id;
				}
				if (organids.length()>0&&organids!="") {
					Bimp.notice_organids=organids.substring(0, organids.length()-1);
				}else{
					Bimp.notice_organids="";
				}
				if (groupids.length()>0&&groupids!="") {
					Bimp.notice_groupids=groupids.substring(0, groupids.length()-1);
				}else{
					Bimp.notice_groupids="";
				}
				
				
				
			
				
		}
	
		}else{

			String names="";
			search_id="";
			for (int i = 0; i <A_0_App.notice_search_Contacts.size(); i++) {
				names=names+A_0_App.notice_search_Contacts.get(i).getTrue_name()+",";
				search_id=search_id+A_0_App.notice_search_Contacts.get(i).getUser_id()+",";
			}
			if (A_0_App.notice_search_Contacts.size()>0) {
				if (Bimp.found_name.length()>0) {
					tv_contacts.setText(Bimp.found_name+","+names.substring(0, names.length()-1));
				}else{
					tv_contacts.setText(names.substring(0, names.length()-1));
				}
				
				search_id=search_id.substring(0, search_id.length()-1);
			}
			Bimp.notice_userids=search_id;
		
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		tv_contacts.setText(Bimp.found_name);
//		if (B_Side_Notice_Main_Add_Contacts2.first_load==0) {
//			A_0_App.student_all_Contacts.clear();
//			A_0_App.colleague_all_Contacts.clear();
//			A_0_App.student_result_Contacts.clear();
//			A_0_App.student_temp_Contacts.clear();
//			A_0_App.colleague_result_Contacts.clear();
//			A_0_App.colleague_temp_Contacts.clear();
//			A_0_App.group_result_Contacts.clear();
//			A_0_App.group_temp_Contacts.clear();
//		}
	}

	public void date_choose() {

		Date d = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
		if (stime==0) {
			try {
	        	if (Bimp.found_date.equals("")) {
	        		 d= df.parse(date);
	        		
				}else{
					
					 d= df.parse(Bimp.found_date);
					
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		}else{
			d=new Date(stime*1000);
		}
        
		
		new SlideDateTimePicker.Builder(getSupportFragmentManager())
				.setListener(listener)
				.setInitialDate(d)
				// .setMinDate(minDate)
				// .setMaxDate(maxDate)
				 .setIs24HourTime(true)
				// .setTheme(SlideDateTimePicker.HOLO_DARK)
				.setIndicatorColor(Color.parseColor("#1EC348"))
				.build().show();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				if (sent_notice_content.getText().toString().equals("")) {
					finish();
					overridePendingTransition(R.anim.animal_push_right_in_normal,
							R.anim.animal_push_right_out_normal);
				} else {
					if (status == 0) {
						final GeneralDialog upDateDialog = new GeneralDialog(
								B_Side_Notice_Main_Sent_SMS.this,
								R.style.Theme_GeneralDialog);
						upDateDialog.setTitle(R.string.pub_title);
						upDateDialog.setContent("已填写的内容将丢失，或保存至草稿！");
						upDateDialog.showLeftButton(R.string.save_draft,
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										upDateDialog.cancel();
										A_0_App.SIDE_NOTICE = 1;
										if (!sent_notice_content.getText()
												.toString().equals("")) {
											status = 1;
											sent(Bimp.type, message_id);
										}

									}
								});
						upDateDialog.showRightButton(R.string.pub_sure,
								new OnClickListener() {
									@Override
									public void onClick(View v) {
										upDateDialog.cancel();
										clear();
										finish();
										overridePendingTransition(
												R.anim.animal_push_right_in_normal,
												R.anim.animal_push_right_out_normal);
									}
								});
						upDateDialog.show();
					} else {
						clear();
						finish();
						overridePendingTransition(R.anim.animal_push_right_in_normal,
								R.anim.animal_push_right_out_normal);
					}

				}

				return true;
			default:
				break;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		clear();
	}
private void clear(){
	A_0_App.student_result_Contacts_summit.clear();
	A_0_App.colleague_result_Contacts_summit.clear();
	A_0_App.group_result_Contacts_summit.clear();
	A_0_App.student_all_Contacts.clear();
	A_0_App.colleague_all_Contacts.clear();
	A_0_App.student_result_Contacts.clear();
	A_0_App.student_temp_Contacts.clear();
	A_0_App.colleague_result_Contacts.clear();
	A_0_App.colleague_temp_Contacts.clear();
	A_0_App.group_result_Contacts.clear();
	A_0_App.group_temp_Contacts.clear();
	Bimp.notice_userids="";
	Bimp.notice_classids="";
	Bimp.notice_groupids="";
	Bimp.notice_organids="";
	Bimp.found_name="";
	A_0_App.summit=0;
	Bimp.found_date ="";
	Bimp.type="1";
	A_0_App.notice_search_Contacts.clear();
}
	public static void removeDuplicate(List<String> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).equals(list.get(i))) {

					list.remove(j);
				}
			}
		}

	}

	private void commit(){
		for (int i = 0; i < A_0_App.student_temp_Contacts.size(); i++) {//学生
			classids=classids+A_0_App.student_temp_Contacts.get(i).getOrgan_id()+",";
		}
		for (int i = 0; i < A_0_App.student_result_Contacts.size(); i++) {//学生
			classids=classids+A_0_App.student_result_Contacts.get(i).getOrgan_id()+",";
		}

		for (int i = 0; i < A_0_App.colleague_temp_Contacts.size(); i++) {//同事
			organids=organids+A_0_App.colleague_temp_Contacts.get(i).getOrgan_id()+",";
		}
		for (int i = 0; i <  A_0_App.colleague_result_Contacts.size(); i++) {//同事
			organids=organids+ A_0_App.colleague_result_Contacts.get(i).getOrgan_id()+",";
		}

		for (int i = 0; i < A_0_App.group_temp_Contacts.size(); i++) {//分组
			if (A_0_App.group_temp_Contacts.get(i).getCount()==null) {
				userids=userids+A_0_App.group_temp_Contacts.get(i).getId()+",";
			}else{
				if (A_0_App.group_temp_Contacts.get(i).isCheck()==true){
					groupids=groupids+A_0_App.group_temp_Contacts.get(i).getId()+",";
				}


			}

		}

		for (int i = 0; i < A_0_App.group_result_Contacts.size(); i++) {//分组
			if (A_0_App.group_result_Contacts.get(i).getCount()==null) {
				userids=userids+A_0_App.group_result_Contacts.get(i).getId()+",";
			}else {
				if (A_0_App.group_result_Contacts.get(i).isCheck()==true) {
					groupids = groupids + A_0_App.group_result_Contacts.get(i).getId() + ",";
				}
			}}
//		for (int i = 0; i <A_0_App.notice_search_Contacts.size() ; i++) {
//			userids=userids+A_0_App.notice_search_Contacts.get(i).getUser_id()+",";
//		}
		}

}
