package com.yuanding.schoolteacher;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yuanding.schoolteacher.service.Api.InterSideNoticeSent;
import com.yuanding.schoolteacher.service.Api.InterSideNoticeSignList;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.service.Api.Inter_UpLoad_Photo_More;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.Bimp;
import com.yuanding.schoolteacher.utils.FileUtils;
import com.yuanding.schoolteacher.utils.NetUtils;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.yuanding.schoolteacher.view.MyGridView;
import com.yuanding.schoolteacher.view.date.SlideDateTimeListener;
import com.yuanding.schoolteacher.view.date.SlideDateTimePicker;
import com.yuanding.schoolteacher.view.toggle.ToggleButton;
import com.yuanding.schoolteacher.view.toggle.ToggleButton.OnToggleChanged;

import static com.yuanding.schoolteacher.utils.Bimp.file_id;

/**
 * 
 * @version 创建时间：2015年12月4日 上午10:46:19 写图文信息页面
 * 
 * 其他页面进入传入的acy_detail_id，type，没有使用，enter_acy_type只有一个有效
 */
public class B_Side_Notice_Main_Sent_Notice extends FragmentActivity {
	@SuppressLint("SimpleDateFormat")
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	/**
	 * ,,status，1：发送，2：草稿
	 */
	private String status;
	private TextView tv_add_image, tv_titlebar_title;
	private Button btn_sent;
	private EditText et_title, sent_notice_content, tv_contacts, tv_time,
			tv_sign;
	ToggleButton tb_ishuizhi; //是否要求回执
	private String st_re,  st_time, st_sign, st_image_url = "";
	private LinearLayout liner_titlebar_back;
	private boolean upLoadPhoto = false;
	/**
	 * userids用户；classids：学生；organids：同事；groupids：分组；
	 */
	private String userids = "", classids = "", organids = "", groupids = "",search_id="";
	private String date;
	private Button btn_add_contacts;
	
	/**
	 * 签名
	 */
	private MySignAdapter adapter;
	private List<String> mClassList = new ArrayList<String>();
	private int dialog_show = 0;
	
	
	/**
	 * 添加多张图片
	 */
	private MyGridView noScrollgridview;
	private GridAdapter gridAdapter;
	public static String temppath = "";

	private String text = "默认封面";
	private int screenWidth;
	private RelativeLayout rl_sent_time, rl_sign;
	

	private SharedPreferences sp;
	private int FIRST_SIGN = 0;// 签名第一次加载

    private int fileSize = 0;
    private int downloadSize = 0;
    private int FIRST_UPLOAD_IMAGE = 0;// 上传图片
    private ScrollView scrollView;
    private WindowManager wm;
    private String failure_url = "";
    private long stime = 0;
    private Editor editor;

    private int enter_acy_type;//enter_acy_type,首页快捷方式进入为1，身边通知进入为2

    private ImageView sendOthers, deleteOthers;//添加附件
    private TextView sendTextName;

    //private String file_id = "0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_notice_sent_notice);
        enter_acy_type = getIntent().getExtras().getInt("enter_acy_type");
        if (enter_acy_type == 0) {
            enter_acy_type = 2;
        }
		A_0_App.Notice_more = 9;
		A_0_App.summit = 0;
		sp = getSharedPreferences("wxb", Context.MODE_PRIVATE);
		liner_titlebar_back = (LinearLayout) findViewById(R.id.liner_titlebar_back);
		tv_titlebar_title = (TextView) findViewById(R.id.tv_titlebar_title);

        scrollView = (ScrollView) findViewById(R.id.notice_scrollView);
        tv_contacts = (EditText) findViewById(R.id.tv_contacts);
        tv_add_image = (TextView) findViewById(R.id.tv_add_image);
        tv_time = (EditText) findViewById(R.id.tv_time);
        tv_sign = (EditText) findViewById(R.id.tv_sign);
        tb_ishuizhi = (ToggleButton) findViewById(R.id.tb_ishuizhi);
        btn_sent = (Button) findViewById(R.id.btn_sent);
        et_title = (EditText) findViewById(R.id.et_title);
        btn_add_contacts = (Button) findViewById(R.id.btn_add_contacts);
        sent_notice_content = (EditText) findViewById(R.id.sent_notice_content);
        rl_sent_time = (RelativeLayout) findViewById(R.id.rl_sent_time);
        rl_sign = (RelativeLayout) findViewById(R.id.rl_sign);
        /**********附件**********/
        sendOthers = (ImageView) findViewById(R.id.send_others_image);
        deleteOthers = (ImageView) findViewById(R.id.delete_others_image);
        sendTextName = (TextView) findViewById(R.id.send_others);
        sendTextName.setText("添加附件 ( < 5M )");
        sendOthers.setOnClickListener(onClick);
        deleteOthers.setOnClickListener(onClick);
        sendTextName.setOnClickListener(onClick);
        /**********附件**********/
        liner_titlebar_back.setOnClickListener(onClick);
        // tv_contacts.setInputType(InputType.TYPE_NULL);
        tv_time.setInputType(InputType.TYPE_NULL);
        tv_sign.setInputType(InputType.TYPE_NULL);
        // tv_contacts.setOnClickListener(onClick);
        btn_add_contacts.setOnClickListener(onClick);
        tv_time.setOnClickListener(onClick);
        tv_sign.setOnClickListener(onClick);
        btn_sent.setOnClickListener(onClick);
        tv_add_image.setOnClickListener(onClick);
        tv_titlebar_title.setText("新建APP通知");

		noScrollgridview = (MyGridView) findViewById(R.id.noScrollgridview);
		noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridAdapter = new GridAdapter(this);
		Bimp.upload_biao = true;
		sent_notice_content.setText(Bimp.found_desc);
		final int[] position = new int[2];
		
		noScrollgridview.getLocationInWindow(position);
		scrollView.post(new Runnable() {

			@Override
			public void run() {
				
				scrollView.scrollTo(0, position[1]);// 改变滚动条的位置
			}

		});
		
		gridAdapter.update();

		noScrollgridview.setAdapter(gridAdapter);
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();
		tv_time.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					rl_sent_time.setBackgroundResource(R.drawable.login_input_hover_bg);
					date_choose();
				} else {
					rl_sent_time.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});
		tv_sign.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					rl_sign.setBackgroundResource(R.drawable.login_input_hover_bg);
					upLoadPhoto = false;
					dialog_show = 1;
					if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_MAIN_TIME)) {
						getData();
					}
					FIRST_SIGN = 1;
				} else {
					rl_sign.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});

		tb_ishuizhi.setOnToggleChanged(new OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
            	if(A_0_App.isHuizhi){
            		A_0_App.isHuizhi = false;
            		tb_ishuizhi.setToggleOn(false);
            	}else{
            		A_0_App.isHuizhi = true;
            		tb_ishuizhi.setToggleOn(true);
            	}
            	
            }
        });
		
		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
if (fileSize==0) {
	if (arg2 == Bimp.drr.size()) {
		init();
		upLoadPhoto = true;
		if (A_0_App.biaozhi == Bimp.drr.size()) {
			photo();
		}
	} else {
		
		if (Bimp.drr.size()>=arg2) {
		if (A_0_App.biaozhi == Bimp.drr.size()) {
			//判断图片上传成功或者失败
			
			
			if (A_0_App.map_url.get(Bimp.drr.get(arg2)).contains(Environment.getExternalStorageDirectory() + "")) {
				failure_url=Bimp.drr.get(arg2);
				FIRST_UPLOAD_IMAGE=1;
				String newStr = A_0_App.map_url.get(Bimp.drr.get(arg2)).substring(A_0_App.map_url.get(Bimp.drr.get(arg2))
										.lastIndexOf("/") + 1,A_0_App.map_url.get(Bimp.drr.get(arg2)).lastIndexOf("."));
				String upload_path = "";
				upload_path = FileUtils.SDPATH + newStr + ".JPEG";
				upload_single_failure(upload_path);
				
			}else if(A_0_App.map_url.get(Bimp.drr.get(arg2)).contains("http")){
				if (arg2 == 0) {
					text = "默认封面";
					
				} else {
					text = "设为封面";
				}
				try {
					A_0_App.checked = 1;
					A_0_App.bgtemp = Bimp.drr.get(arg2);
					
				} catch (Exception e) {
					A_0_App.checked =0;
				}
				
				gridAdapter.notifyDataSetChanged();
			}
			
		}
			
		}

	}
}
				
			}
		});
		date = sdf.format(new Date());
		if (Bimp.found_date == "") {
         st_time="";
			//st_time = date;
		} else {
			st_time = Bimp.found_date;
			tv_time.setText(Bimp.found_date);
		}
		
		tv_contacts.setText(Bimp.found_name);
		
		et_title.setText(Bimp.found_phone);
		if (Bimp.file_name == ""){
			sendTextName.setText("添加附件 ( < 5M )");
			sendOthers.setVisibility(View.VISIBLE);
			deleteOthers.setVisibility(View.GONE);
		}else {
			sendTextName.setText(Bimp.file_name);
			sendOthers.setVisibility(View.GONE);
			deleteOthers.setVisibility(View.VISIBLE);
		}

		
		adapter = new MySignAdapter();
		if (sp.getString(A_0_App.USER_UNIQID + "", "") != null
				&& !sp.getString(A_0_App.USER_UNIQID + "", "").equals("")) {
			String sign = sp.getString(A_0_App.USER_UNIQID + "", "");
			tv_sign.setText(sign + "");
		} 
			getData();
		

		/**
		 * 过滤表情
		 */
		et_title.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				int index = et_title.getSelectionStart() - 1;
				if (index > 0) {
					if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
						Editable edit = et_title.getText();
						edit.delete(index, index + 1);
					}
				}
			}
		});

	}
	
	/**
	 * 上传图片此处返回就上传图片并添加进度条 一张一张上传
	 */

	private void upload_single(final String url) {

		A_0_App.getApi().upload_Photo(new File(url), "2",
				new Inter_UpLoad_Photo_More() {

					@Override
					public void onSuccess(String imageUrl) {
						if (isFinishing()) 
							return;
						fileSize = 0;
						downloadSize = 0;
						
						
						A_0_App.map_url.put(Bimp.drr.get(A_0_App.biaozhi),
								imageUrl);
						A_0_App.biaozhi++;
						if (Bimp.drr.size() != A_0_App.biaozhi) {
							String newStr = Bimp.drr.get(A_0_App.biaozhi).substring(
											Bimp.drr.get(A_0_App.biaozhi)
													.lastIndexOf("/") + 1,
											Bimp.drr.get(A_0_App.biaozhi)
													.lastIndexOf("."));
							String upload_path = "";
							upload_path = FileUtils.SDPATH + newStr + ".JPEG";
							upload_single(upload_path);
						} 
						String temp_path="";
						for (int i = 0; i < Bimp.drr.size(); i++) {
							temp_path = temp_path +  A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
						}
						if (A_0_App.biaozhi == Bimp.drr.size()&& !temp_path.contains(Environment.getExternalStorageDirectory() + "")) {
							btn_sent.getBackground().mutate().setAlpha(255);
						}
						gridAdapter.update();
					}

					@Override
					public void onStart() {
						
						if (isFinishing()) 
							return;
						fileSize = 0;
						downloadSize = 0;

						A_0_App.map_url.put(Bimp.drr.get(A_0_App.biaozhi), "");
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						
						if (isFinishing())
							return;
						fileSize = (int) total;
						downloadSize = (int) current;
						
						
						if (total == -1)
							return;
						
						gridAdapter.update();
					}
                    @Override
                    public void onWaiting() {
                        // TODO Auto-generated method stub
                        
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
                        
                        PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice.this, msg);
                        fileSize = 0;
                        downloadSize = 0;
                        A_0_App.map_url.put(Bimp.drr.get(A_0_App.biaozhi), url);
                        A_0_App.biaozhi++;
                        if (Bimp.drr.size() != A_0_App.biaozhi) {
                            String newStr = Bimp.drr.get(A_0_App.biaozhi)
                                    .substring(
                                            Bimp.drr.get(A_0_App.biaozhi)
                                                    .lastIndexOf("/") + 1,
                                            Bimp.drr.get(A_0_App.biaozhi)
                                                    .lastIndexOf("."));
                            String upload_path = "";
                            upload_path = FileUtils.SDPATH + newStr + ".JPEG";
                            upload_single(upload_path);
                        } 
                        gridAdapter.notifyDataSetChanged();
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}

	private void upload_single_failure(final String url) {

		A_0_App.getApi().upload_Photo(new File(url), "2",
				new Inter_UpLoad_Photo_More() {

					@Override
					public void onSuccess(String imageUrl) {
						if (isFinishing()) 
							return;
						fileSize = 0;
						downloadSize = 0;
						A_0_App.map_url.put(failure_url,imageUrl);
						String temp_path="";
						for (int i = 0; i < Bimp.drr.size(); i++) {
							temp_path = temp_path +  A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
						}
						if (A_0_App.biaozhi == Bimp.drr.size()&& !temp_path.contains(Environment.getExternalStorageDirectory() + "")) {
							btn_sent.getBackground().mutate().setAlpha(255);
						}
						gridAdapter.update();
					}

					@Override
					public void onStart() {
						
						if (isFinishing()) 
							return;
						fileSize = 0;
						downloadSize = 0;
						A_0_App.map_url.put(failure_url, "");
					}

					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						
						if (isFinishing())
							return;
						fileSize = (int) total;
						downloadSize = (int) current;
						if (total == -1)
							return;

						gridAdapter.update();
					}
                    @Override
                    public void onWaiting() {
                        // TODO Auto-generated method stub
                        
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
                        fileSize = 0;
                        downloadSize = 0;
                        A_0_App.map_url.put(failure_url, url);
                        gridAdapter.notifyDataSetChanged();
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}
	
	
	OnClickListener onClick = new OnClickListener() {

		@SuppressLint("ResourceAsColor")
		@Override
		public void onClick(View v) {
			final Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.btn_add_contacts:// 添加联系人
				upLoadPhoto = false;
				intent.setClass(B_Side_Notice_Main_Sent_Notice.this,
						B_Side_Notice_Main_Add_Contacts2.class);
				startActivityForResult(intent, 6);

				break;
			case R.id.tv_time:// 设置定时
				if (stime==0) {
					stime=sp.getLong("time", 0);
				}
				date_choose();

				break;
			case R.id.tv_sign:// 选择签名
				upLoadPhoto = false;
				dialog_show = 1;
				FIRST_SIGN = 1;
				if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_MAIN_TIME)) {
					getData();
				}
				/*
				 * intent.setClass(B_Side_Notice_Main_Sent_Notice.this,
				 * B_Side_Notice_Main_Selcet_Sign.class);
				 * startActivityForResult(intent, 4);
				 */

				break;
			case R.id.tv_add_image:// 照片
				upLoadPhoto = true;
				//A_0_App.biaozhi == Bimp.drr.size();
				if (A_0_App.biaozhi == Bimp.drr.size()) {
					photo();
				}
				

				break;

			case R.id.btn_sent:// 发送

				// 首先判断是否可以发送，同时设置背景图片
				status = "1";
				if (NetUtils.isConnected(B_Side_Notice_Main_Sent_Notice.this)) {
					if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
						sent(Bimp.type, status);
					} else {
//						PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice.this,
//								"请勿重复操作！");
                        }


                    } else {
                        PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice.this,
                                "请检查您的网络连接");
                    }


                    break;
                case R.id.liner_titlebar_back:// 返回
                    if (A_0_App.biaozhi == Bimp.drr.size()) {
                        clear();
                    }

                    break;
                case R.id.send_others_image://添加附件
                    Intent i = new Intent(B_Side_Notice_Main_Sent_Notice.this, B_Side_Notice_Main_Sent_Notice_Files.class);
                    startActivityForResult(i,10);
                    break;
                case R.id.delete_others_image://删除附件
                    dialog();
                    break;
            }
            ;
        }

    };

    /**
     * 删除附件
     */
    public void dialog(){
        final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Notice_Main_Sent_Notice.this,
                R.style.Theme_GeneralDialog);
        upDateDialog.setTitle("你将删除已上传的文件");
        upDateDialog.setContent(fileName);
        upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
            @Override
            public void onClick(View v) {
                upDateDialog.cancel();
            }
        });
        upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
            @Override
            public void onClick(View v) {
                upDateDialog.cancel();
                deleteOthers.setVisibility(View.GONE);
                sendOthers.setVisibility(View.VISIBLE);
                sendTextName.setText("添加附件 ( < 5M )");
                Bimp.file_id = "0";
            }
        });
        upDateDialog.show();
    }
    /**
     * 选择时间监听
     */
    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            String s = sdf.format(date);

            if ((date.getTime() - stime * 1000) / 1000 <= 2592000
                    && (date.getTime() - stime * 1000) / 1000 > 560) {
                tv_time.setText(s);
                Bimp.found_date = s;
                Bimp.type = "2";
            } else if ((date.getTime() - stime * 1000) / 1000 < 600 && (date.getTime() - stime * 1000) / 1000 > -59) {
                Bimp.found_date = s;
                tv_time.setText(s);
                Bimp.type = "1";
                PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice.this,
                        "定时发送需大于10分钟");
            } else if ((date.getTime() - stime * 1000) / 1000 > 2592000) {
                PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice.this,
                        "定时发送时间不能超过一个月");
            } else {
                PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice.this,
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
	 * 签名选择
	 */
	void sign_Dialog() {
		final Dialog dialog = new Dialog(B_Side_Notice_Main_Sent_Notice.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.activity_side_select_sign_dialog);
		LinearLayout liner=(LinearLayout) dialog.findViewById(R.id.liner_side_repair);
		/*
		 * Window window = dialog.getWindow();
		 * window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置
		 * window.setWindowAnimations(R.style.mystyle); // 添加动画
		 */dialog.show();
		ListView listView = (ListView) dialog
				.findViewById(R.id.lv_side_select_sign);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				tv_sign.setText(mClassList.get(arg2));
				 editor = sp.edit();
				editor.putString(A_0_App.USER_UNIQID + "", mClassList.get(arg2));
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
	 * 签名数据
	 */
	private void getData() {
		mClassList.clear();
		A_0_App.getApi().getSideNoticeSignList(
				B_Side_Notice_Main_Sent_Notice.this, A_0_App.USER_TOKEN,
				new InterSideNoticeSignList() {

					@Override
					public void onSuccess(String List,long severtime) {
						if (isFinishing())
							return;
						stime=severtime;
						editor = sp.edit();
						editor.putLong("time",severtime);
						editor.commit();
						st_time= sdf.format(new Date(severtime*1000));
						String list = (List.replace("[", "")).replace("]", "")
								.replace("\"", "");
						String content[] = list.split(",");
						for (int i = 0; i < content.length; i++) {
							mClassList.add(content[i]);
						}
						if (dialog_show == 0) {
							if (mClassList.size()>0) {
								if (sp.getString(A_0_App.USER_UNIQID + "", "") != null
										&& !sp.getString(A_0_App.USER_UNIQID + "", "").equals("")) {
									
									if (!list.contains(sp.getString(A_0_App.USER_UNIQID + "", ""))) {

										tv_sign.setText(mClassList.get(0));
										 
										editor.putString(A_0_App.USER_UNIQID + "",
												mClassList.get(0));
										editor.commit();
									}
								} else{
								tv_sign.setText(mClassList.get(0));
								 editor = sp.edit();
								editor.putString(A_0_App.USER_UNIQID + "",
										mClassList.get(0));
								editor.commit();}
							}
							
						} else {
							sign_Dialog();
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
                        if (FIRST_SIGN != 0) {
//                          PubMehods.showToastStr(
//                                  B_Side_Notice_Main_Sent_Notice.this, msg);
                        }

                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
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
						B_Side_Notice_Main_Sent_Notice.this).inflate(
						R.layout.item_pub_text, null);
			}
			TextView tv_acy_name = (TextView) converView
					.findViewById(R.id.tv_item_pub_text);
			tv_acy_name.setText(mClassList.get(posi));
			if (A_0_App.isShowAnimation == true) {
				if (posi > A_0_App.sent_curPosi) {
					A_0_App.sent_curPosi = posi;
					Animation an = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 1,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0);
					an.setDuration(400);
					an.setStartOffset(50 * posi);
					converView.startAnimation(an);
				}
			}
			return converView;
		}

	}

	/**
	 * 发送
	 */
	void sent(final String type, String status) {

		st_re = tv_contacts.getText().toString();
		if (type.equals("1")) {
			st_time = "";
		} else {
			st_time = tv_time.getText().toString();
		}

		st_sign = tv_sign.getText().toString();

		if (et_title.getText().toString().equals("")) {
			PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice.this,
					"标题不能为空");
			return;
		}
		if (tv_contacts.getText().toString().equals("")) {
			PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice.this,
					"请选择收信人");
			return;
		}

		if (tv_sign.getText().toString().equals("")) {
			PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice.this, "签名为空");
			return;
		}
		if (sent_notice_content.getText().toString().equals("")) {
			PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice.this,
					"内容不能为空");
			return;
		}
		
       init();
        if (Bimp.drr.size() > 0 && Bimp.drr.size() == A_0_App.map_url.size()) {
            String temp_path = "";
            Bimp.notice_bg_url = A_0_App.map_url.get(A_0_App.bgtemp);
            for (int i = 0; i < Bimp.drr.size(); i++) {
                temp_path = temp_path + A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
            }
            if (A_0_App.biaozhi == Bimp.drr.size() && !temp_path.contains(Environment.getExternalStorageDirectory() + "")) {
                st_image_url = temp_path.substring(0, temp_path.length() - 1);
                post_data();
            } else {
                dialog_post();

            }
        } else {
            post_data();
        }
	}
	
	public void dialog_post() {
		final GeneralDialog upDateDialog = new GeneralDialog(
				B_Side_Notice_Main_Sent_Notice.this, R.style.Theme_GeneralDialog);
		upDateDialog.setTitle(R.string.pub_title);
		upDateDialog.setContent("您有图片上传失败，是否继续提交?");
		upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
			@Override
			public void onClick(View v) {
				upDateDialog.cancel();
			}
		});
		upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
			@Override
			public void onClick(View v) {
				upDateDialog.cancel();
				String temp_path = "";
				for (int i = 0; i < Bimp.drr.size(); i++) {
					if (A_0_App.map_url.get(Bimp.drr.get(i)).contains("http")) {
						temp_path = temp_path +  A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
					}
					
				}
				if (A_0_App.map_url.get(A_0_App.bgtemp).contains(Environment.getExternalStorageDirectory() + "")) {
					if (temp_path.length()>0) {
						Bimp.notice_bg_url=temp_path.split(",")[0];
					}
					
				}
				if (temp_path.length()>0) {
					st_image_url = temp_path.substring(0, temp_path.length() - 1);
					post_data();
				}
				
			}

			
		});

		upDateDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {

			}
		});
		upDateDialog.show();
	}

	/**
	 * 发送信息
	 */
	private void post_data() {
		int is_receipt;
		if(A_0_App.isHuizhi){
			is_receipt = 1;
		}else{
			is_receipt = 0;
		}
		if (isFinishing())
            return;
        if (B_Side_Notice_Main_Sent_Notice.this == null)
            return;
        if (Bimp.found_desc != "") {
            A_0_App.getInstance().showProgreDialog(B_Side_Notice_Main_Sent_Notice.this, "发送中，请稍候",
                    true);
        }
        A_0_App.getApi().SideNoticeSent(A_0_App.USER_TOKEN,
                Bimp.notice_userids, Bimp.notice_classids,
                Bimp.notice_organids, Bimp.notice_groupids, Bimp.found_phone,
                st_time, st_sign, Bimp.notice_bg_url, st_image_url,
                Bimp.found_desc, Bimp.type, status, Bimp.found_desc, Bimp.file_id, is_receipt,
                new InterSideNoticeSent() {

					@Override
					public void onSuccess(int state ,String msg) {
						if (isFinishing())
							return;
						A_0_App.getInstance().CancelProgreDialog(
								B_Side_Notice_Main_Sent_Notice.this);
						if (state==1){
//							if (type.equals("1")) {
//								A_0_App.SIDE_NOTICE = 0;
//							} else {
//								A_0_App.SIDE_NOTICE = 1;
//							}
							Intent intent1 = new Intent("notice");
							sendBroadcast(intent1);

							PubMehods.showToastStr(
									B_Side_Notice_Main_Sent_Notice.this, "发送成功！");
							clear();
							if(enter_acy_type==1){
								Intent intent = new Intent();
								intent.setClass(B_Side_Notice_Main_Sent_Notice.this, B_Side_Notice_Main.class);
								intent.putExtra("enter_acy_type", 1);
								startActivity(intent);
								finish();
							}else if(enter_acy_type==2){
								finish();
							}
						}else{
							final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Notice_Main_Sent_Notice.this,
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
                        if (B_Side_Notice_Main_Sent_Notice.this==null)
                            return;
                        A_0_App.getInstance().CancelProgreDialog(
                                B_Side_Notice_Main_Sent_Notice.this);
                        PubMehods.showToastStr(
                                B_Side_Notice_Main_Sent_Notice.this, msg);
                    }

                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
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
				// .setMinDate(d)
				 
				 //.setMaxDate(maxDate)
				 .setIs24HourTime(true)
				// .setTheme(SlideDateTimePicker.HOLO_DARK)
				 .setIndicatorColor(Color.parseColor("#1EC348"))
				.build().show();
	}

    private static final String IMAGE_FILE_NAME = "pic.jpg";
    private String path = "";
    private Bitmap photo;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    String fileName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //附件上传
        if (resultCode == 10){
            fileName = data.getStringExtra("name");
            Bimp.file_id = data.getStringExtra("file_id");
            sendTextName.setText(fileName);
            sendOthers.setVisibility(View.GONE);
            deleteOthers.setVisibility(View.VISIBLE);

        }

        if (upLoadPhoto) {
            if (resultCode != RESULT_CANCELED) {
                try {
                    path = android.os.Environment.getExternalStorageDirectory() + AppStrStatic.SD_PIC + "/"
                            + B_Side_Notice_Main_Sent_Notice.temppath
                            + IMAGE_FILE_NAME;
                    File tempFile = new File(path);

                    if (Bimp.drr.size() < 9) {
                        Bimp.drr.add(path);
                    }
                    init();
                    FIRST_UPLOAD_IMAGE = 0;
                    gridAdapter.update();

				} catch (Exception e) {

				}
			}
		} else {
			if (data != null) {
				if (requestCode == 6) {

					A_0_App.colleague_result_Contacts_summit
							.addAll(A_0_App.colleague_result_Contacts);
					A_0_App.colleague_result_Contacts_summit
							.addAll(A_0_App.colleague_temp_Contacts);

					A_0_App.student_result_Contacts_summit
							.addAll(A_0_App.student_result_Contacts);
					A_0_App.student_result_Contacts_summit
							.addAll(A_0_App.student_temp_Contacts);

					A_0_App.group_result_Contacts_summit
							.addAll(A_0_App.group_result_Contacts);
					A_0_App.group_result_Contacts_summit
							.addAll(A_0_App.group_temp_Contacts);


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
					
					st_re = data.getExtras().getString("name");
					classids = data.getExtras().getString("classids");
					userids = data.getExtras().getString("userids");
					groupids = data.getExtras().getString("groupids");
					organids = data.getExtras().getString("organids");

					if (st_re.length() > 0 && st_re != "") {
						Bimp.found_name = st_re
								.substring(0, st_re.length() - 1);
						if (names.equals("")) {
							tv_contacts.setText(Bimp.found_name);
						}else{
							tv_contacts.setText(Bimp.found_name+","+names);
						}
						
					} else {
						Bimp.found_name = "";
						tv_contacts.setText(Bimp.found_name+names);
					}
					if (classids.length() > 0 && classids != "") {
						Bimp.notice_classids = classids.substring(0,
								classids.length() - 1);
					}
					if (userids.length() > 0 && userids != "") {
						if (search_id.equals("")) {
							Bimp.notice_userids = userids.substring(0,userids.length() - 1);
						}else{
							Bimp.notice_userids = userids.substring(0,
									userids.length() - 1)+","+search_id;
						}
						
					}else{
						Bimp.notice_userids=search_id;
					}
					if (organids.length() > 0 && organids != "") {
						Bimp.notice_organids = organids.substring(0,
								organids.length() - 1);
					}
					if (groupids.length() > 0 && groupids != "") {
						Bimp.notice_groupids = groupids.substring(0,
								groupids.length() - 1);
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
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 图片
	 */
	void photo() {
		final Dialog dialog = new Dialog(B_Side_Notice_Main_Sent_Notice.this,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setContentView(R.layout.upload_choosepicture);
		dialog.show();
		Button btn1 = (Button) dialog.findViewById(R.id.btn1);
		Button btn2 = (Button) dialog.findViewById(R.id.btn2);
		Button btn3 = (Button) dialog.findViewById(R.id.btn3);
		btn3.setText("取消");
		btn1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				upLoadPhoto = true;
				B_Side_Notice_Main_Sent_Notice.temppath = System
						.currentTimeMillis() + "";
				Intent intentFromCapture = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
						.fromFile(new File(android.os.Environment.getExternalStorageDirectory() + AppStrStatic.SD_PIC+"/"
								+ B_Side_Notice_Main_Sent_Notice.temppath
								+ IMAGE_FILE_NAME)));
				startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
				dialog.dismiss();

			}
		});
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				upLoadPhoto = true;
				dialog.dismiss();
				Intent intent = new Intent(B_Side_Notice_Main_Sent_Notice.this,
						B_Side_Found_Add_Images.class);
				intent.putExtra("type", Bimp.type);
				intent.putExtra("enter_acy_type", enter_acy_type);
				Bimp.add_edit = "3";
				startActivity(intent);
				finish();
				init();
			}
		});
		btn3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		A_0_App.getInstance().CancelProgreDialog(B_Side_Notice_Main_Sent_Notice.this);
		A_0_App.count = 0;
		A_0_App.userids = "";
		A_0_App.classids = "";
		A_0_App.organids = "";
		A_0_App.result = "";
		
	}

	public class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int selectedPosition = -1;
		private boolean shape;
		
		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public GridAdapter(Context context) {
			inflater = LayoutInflater.from(context);
		}

		public void update() {
			loading();
		}

		public int getCount() {
			
			if (Bimp.drr.size()==9) {
				return Bimp.drr.size();
			}else{
				return Bimp.drr.size()+1;
			}
			
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item
		 */
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {

				convertView = inflater.inflate(R.layout.item_add_notice_images,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				holder.item_half = (ImageView) convertView
						.findViewById(R.id.item_half);
				holder.delete = (ImageView) convertView
						.findViewById(R.id.item_grida_delete);
				holder.itme_process_text = (TextView) convertView
						.findViewById(R.id.itme_process_text);
				holder.text = (TextView) convertView
						.findViewById(R.id.itme_grida_text);
				holder.relativeLayout = (RelativeLayout) convertView
						.findViewById(R.id.itme_rela_notice);
				holder.pb = (ProgressBar) convertView.findViewById(R.id.pb);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text.setText(text);
			if (Bimp.drr.size()==1) {
				A_0_App.checked=0;
			}
			if (A_0_App.checked == 0) {// 未选择时设置默认图片
				if (Bimp.drr.size() >=1) {
					A_0_App.bgtemp = Bimp.drr.get(0);
				}

			} else if (A_0_App.bgtemp.equals("")) {
				if (Bimp.drr.size() > 1) {
					A_0_App.bgtemp = Bimp.drr.get(0);

				}
			}
			RelativeLayout.LayoutParams lp_menpiao1 = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			lp_menpiao1.width = (int) (screenWidth / 4);
			lp_menpiao1.height = (int) (screenWidth / 4);
			lp_menpiao1.setMargins(0, 30, 0, 0);
			holder.image.setLayoutParams(lp_menpiao1);
			holder.pb.setLayoutParams(lp_menpiao1);
			holder.item_half.setLayoutParams(lp_menpiao1);
			holder.delete.setPadding(screenWidth / 4 - 30, 0, 0, 0);
			LayoutParams lp;
			lp = (LayoutParams) holder.text.getLayoutParams();
			lp.width = screenWidth / 4;
			holder.text.setLayoutParams(lp);
			holder.itme_process_text.setLayoutParams(lp_menpiao1);
			holder.delete.setVisibility(View.GONE);
			if (position == Bimp.bmp.size()) {
				holder.item_half.setVisibility(View.GONE);
				holder.text.setVisibility(View.GONE);
				holder.delete.setVisibility(View.GONE);
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.add));
				if (position == 9) {
					holder.delete.setVisibility(View.GONE);
					holder.image.setVisibility(View.GONE);
				}
			} else {

				try {
					
					holder.image.setImageBitmap(Bimp.bmp.get(position));
					holder.item_half.setVisibility(View.VISIBLE);
					holder.delete.setVisibility(View.VISIBLE);
					holder.item_half.getBackground().mutate().setAlpha(100);
				} catch (Exception e) {
					holder.image.setImageBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.side_lost_posi));
				}
				try {
					if (A_0_App.map_url.size() > 0) {
						if (A_0_App.map_url.get(Bimp.drr.get(position)).equals(
								"")) {
							
							holder.item_half.setVisibility(View.GONE);
							holder.pb.setVisibility(View.VISIBLE);
							holder.itme_process_text
									.setVisibility(View.VISIBLE);
							holder.pb.setMax(fileSize);
							holder.pb.setProgress(downloadSize);
							
							if (fileSize > 0) {
								
								double price = (double) downloadSize * 100
										/ (double) fileSize;

								if ((price + "").length() >= 4) {
									holder.itme_process_text
											.setText((price + "").substring(0,
													4) + "%");
								} else {
									holder.itme_process_text.setText(price
											+ "%");
								}
								
							}
								

						} else if (A_0_App.map_url.get(Bimp.drr.get(position)).contains("http")) {
							holder.image.setVisibility(View.VISIBLE);
							holder.pb.setVisibility(View.GONE);
							holder.item_half.setVisibility(View.GONE);
							holder.itme_process_text.setVisibility(View.GONE);

						} else {

							holder.itme_process_text.setVisibility(View.GONE);
							holder.image.setVisibility(View.VISIBLE);
							holder.pb.setVisibility(View.GONE);
							holder.item_half.setVisibility(View.VISIBLE);
							holder.item_half.setImageBitmap(BitmapFactory
									.decodeResource(getResources(),
											R.drawable.ico_side_notice));
						}
					}
					if (Bimp.drr.get(position).equals(A_0_App.bgtemp)
							&& A_0_App.map_url.get(A_0_App.bgtemp).contains(
									"http")) {
						holder.text.setVisibility(View.VISIBLE);
					} else {
						holder.text.setVisibility(View.GONE);
					}
				} catch (Exception e) {

				}

			}
			if (A_0_App.biaozhi == Bimp.drr.size()) {
				holder.delete.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						try {
							init();
							if (Bimp.drr.get(position).equals(A_0_App.bgtemp)) {
								A_0_App.bgtemp = "";
							}
							FileUtils.delFile(Bimp.drr.get(position));
							A_0_App.map_url.remove(Bimp.drr.get(position));
							Bimp.drr.remove(position);
							if (Bimp.bmp.size() > position) {
								Bimp.bmp.remove(position);
							}
							Bimp.max = Bimp.max - 1;
							A_0_App.biaozhi--;
							loading();
							String temp_path="";
							for (int i = 0; i < Bimp.drr.size(); i++) {
								temp_path = temp_path +  A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
							}
							if (A_0_App.biaozhi == Bimp.drr.size()&& !temp_path.contains(Environment.getExternalStorageDirectory() + "")) {
								btn_sent.getBackground().mutate().setAlpha(255);
							}
						} catch (Exception e) {
							loading();
						}

					}
				});
			}

			return convertView;
		}
	}

	public class ViewHolder {
		private ImageView image,item_half;
		private ImageView delete;
		private TextView text, itme_process_text;
		private RelativeLayout relativeLayout;
		private ProgressBar pb;
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:

				Bimp.act_bool = true;
				
				gridAdapter.notifyDataSetChanged();

				break;
			}
			super.handleMessage(msg);
		}
	};

	public void loading() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					if (isFinishing()) 
					     return;
					if (Bimp.max == Bimp.drr.size()) {
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);

						final int[] position = new int[2];
						noScrollgridview.getLocationInWindow(position);
						if (A_0_App.map_url.size() >= 0) {
							if (Bimp.drr.size() > 0 && FIRST_UPLOAD_IMAGE == 0) {
								FIRST_UPLOAD_IMAGE = 1;
								scrollView.post(new Runnable() {

									@Override
									public void run() {
										btn_sent.getBackground().mutate().setAlpha(100);
										//scrollView.scrollTo(0, position[1]);// 改变滚动条的位置
									}

								});
								if (Bimp.drr.size() > A_0_App.biaozhi) {

									String newStr = Bimp.drr.get(
											A_0_App.biaozhi).substring(
											Bimp.drr.get(A_0_App.biaozhi)
													.lastIndexOf("/") + 1,
											Bimp.drr.get(A_0_App.biaozhi)
													.lastIndexOf("."));
									String upload_path = "";
									upload_path = FileUtils.SDPATH + newStr
											+ ".JPEG";

									upload_single(upload_path);
								}

							}
						}

						break;
					} else {
						try {
							if (isFinishing())
								return;
							if (Bimp.drr.size() - Bimp.max >=1) {
								String path = Bimp.drr.get(Bimp.max);
								
								Bitmap bm = Bimp.revitionImageSize(path);
								Bitmap bmBitmap= PubMehods.rotateBitmapByDegree(bm, PubMehods.getBitmapDegree(path));
								Bimp.bmp.add(bmBitmap);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bmBitmap, "" + newStr);
								Bimp.max += 1;
								Message message = new Message();
								message.what = 1;
								handler.sendMessage(message);
							}
							
						} catch (IOException e) {

							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				if (A_0_App.biaozhi == Bimp.drr.size()) {
					clear();
				}
				return true;
			default:
				break;
			}
		}
		return super.dispatchKeyEvent(event);
	}

    private void init() {
        Bimp.found_name = tv_contacts.getText().toString();
        Bimp.found_desc = sent_notice_content.getText().toString();
        Bimp.found_phone = et_title.getText().toString();
		Bimp.file_name = sendTextName.getText().toString();
		Log.i("aaa", "init: " + Bimp.file_name + Bimp.file_id);
	}

	private void clear() {
		A_0_App.isHuizhi=false;
	    Bimp.type="1";
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
		Bimp.notice_classids = "";
		Bimp.notice_organids = "";
		Bimp.notice_userids = "";
		Bimp.notice_groupids = "";

		A_0_App.biaozhi = 0;
		A_0_App.bgtemp = "";
		A_0_App.map_url.clear();
		A_0_App.summit = 0;
		Bimp.notice_bg_url = "";
		Bimp.add_edit = "";
		Bimp.found_date = "";
		Bimp.found_desc = "";
		Bimp.found_name = "";
		Bimp.found_phone = "";
		Bimp.found_place = "";
		Bimp.file_id = "0";
		Bimp.file_name = "";
		Bimp.bmp.clear();
		Bimp.upload_path.clear();
		Bimp.drr.clear();
		Bimp.max = 0;
		Bimp.image_names.clear();
		FileUtils.deleteDir();
		A_0_App.notice_search_Contacts.clear();
		overridePendingTransition(R.anim.animal_push_right_in_normal,
				R.anim.animal_push_right_out_normal);
		finish();
	}
	
	@Override
	protected void onResume() {
        if (tb_ishuizhi != null) {
            if (A_0_App.isHuizhi) {
            	tb_ishuizhi.setToggleOn();
            } else {
            	tb_ishuizhi.setToggleOff();
            }
        }
		super.onResume();
	}

}
