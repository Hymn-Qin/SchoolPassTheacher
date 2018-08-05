package com.yuanding.schoolteacher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.Manifest;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityGroup;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.DensityUtils;
import com.yuanding.schoolteacher.utils.LogUtils;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

/**
 * 
* @ClassName: A_Main_Activicy
* @Description: TODO(主页面)
* @author Jiaohaili 
* @date 2015年11月2日 下午8:23:15
*
 */

public class A_Main_Acy extends ActivityGroup {

	private A_0_App mApp = null;
	
	public static enum ActivityID {STARTHOME, PROMATE, ACCOUNT,MORE};
	
	private RelativeLayout mToolRlayoutSearcher = null;
	private RelativeLayout mToolRlayoutPromate = null;
	private RelativeLayout mToolRlayoutAccount = null;
	private RelativeLayout mToolRlayoutMore = null;
	private ImageView mIVSearcher = null;
	private ImageView mIVPromate = null;
	private ImageView mIVAccount = null;
	private ImageView mIVMore = null;
	private TextView tv_index_mess_no_read_count = null;
	private ImageView mIV_Side_No_Read_Tag;
	
	private HashMap<String, Class> mActivity;
	private ActivityID mCurActivity = null;
	private FrameLayout mContainer = null;
	private LinearLayout lin = null;
	private int position=0;

	Handler mHandler;
	
	private WindowManager wm;
	private int screenWidth;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mApp = (A_0_App) this.getApplication();
		mApp.setRuning(true);
		instance = this;
		mHandler = new Handler();
		A_0_App.getInstance().addActivity(this);
		
		mActivity = new HashMap<String, Class>();
		mContainer = (FrameLayout) findViewById(R.id.main_container);
		
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();
		
		initActivity();
		initToolBarMainViews();
		UpdateView(ActivityID.STARTHOME);
		if (A_0_App.getInstance().getVersion() != null) {
			if (A_0_App.getInstance().getVersion().getVersionCode() == null
					|| A_0_App.getInstance().getVersion().getVersionCode().length() <= 0) {
				A_0_App.getInstance().checkUpdateVersion(instance);
			}
		}
		initXuanfuMenu();
	}
	
	private static A_Main_Acy instance;
	public static A_Main_Acy getInstance() {
		return instance;
	}
	
	private void initActivity() {
		mActivity.put(ActivityID.STARTHOME.toString(), A_Main_My_Message_Acy.class);
		mActivity.put(ActivityID.PROMATE.toString(), A_Main_My_Contact_Acy.class);
		mActivity.put(ActivityID.ACCOUNT.toString(),A_Main_My_Side_Acy.class);
		mActivity.put(ActivityID.MORE.toString(),A_Main_My_Account_Acy.class);
	}
	
	/*
	 * 调用、监听主窗口的工具栏
	 */
	private void initToolBarMainViews() {
		lin = (LinearLayout) findViewById(R.id.toolbar);
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.toolbar_main, null);

		LinearLayout toolbarmain = (LinearLayout) layout.findViewById(R.id.toolbarmain);
		toolbarmain.setLayoutParams(new LayoutParams());
//		toolbarmain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));

		mToolRlayoutSearcher = (RelativeLayout) layout.findViewById(R.id.toolbar_seracher);
		mIVSearcher = (ImageView) layout.findViewById(R.id.toolb_home);
		mToolRlayoutSearcher.setOnClickListener(onToolbarMainClickListener);

		mToolRlayoutPromate = (RelativeLayout) layout.findViewById(R.id.toolbar_promate);
		mIVPromate = (ImageView) layout.findViewById(R.id.toolb_promate);
		mToolRlayoutPromate.setOnClickListener(onToolbarMainClickListener);

		mToolRlayoutAccount = (RelativeLayout) layout.findViewById(R.id.toolbar_account);
		mIVAccount= (ImageView) layout.findViewById(R.id.toolb_account);
		mToolRlayoutAccount.setOnClickListener(onToolbarMainClickListener);

		mToolRlayoutMore = (RelativeLayout) layout.findViewById(R.id.toolbar_more);
		mIVMore = (ImageView) layout.findViewById(R.id.toolb_more);
		mToolRlayoutMore.setOnClickListener(onToolbarMainClickListener);
		
		tv_index_mess_no_read_count =(TextView)layout.findViewById(R.id.tv_index_mess_no_read_count);
	    mIV_Side_No_Read_Tag = (ImageView)layout.findViewById(R.id.iv_side_no_read_tag);
		lin.removeAllViews();
		lin.addView(layout);
	}
	
	/**
	 * 悬浮菜单
	 */
	RelativeLayout layout_menuback; //菜单的全屏背景
	ImageView img_btnxuanmenufupower ; //菜单开关
	LinearLayout layout_menucontainer; //菜单项容器
	static int animDuration = 100; //动画时常
	//color.xml的#5000000前两位是透明的效果参数从00--99（透明--不怎么透明），后6位是颜色的设置
	static int baise_bantouyming = Color.parseColor("#99f8f8f8");
	static int touyming = Color.parseColor("#00f8f8f8");
	boolean animRuning;
	private int menuItemRes[] = new int[]{
			R.id.iv_b,
            R.id.iv_smsnotice,
            
            R.id.iv_appnotice
    };
    private List<LinearLayout> menuItemViewList = new ArrayList<LinearLayout>();
    
    Context mContext;
    
    boolean isOpenStatus = false;  //解决连续点击开关按钮 多次执行关闭动画造成闪动问题
    
	/**
	 * 初始化悬浮菜单
	 */
	@SuppressLint("ShowToast") 
	private void initXuanfuMenu() {
		mContext = this;	
		layout_menuback = (RelativeLayout)findViewById(R.id.layout_menuback);
		img_btnxuanmenufupower = (ImageView)findViewById(R.id.img_btnxuanmenufupower);
		layout_menucontainer = (LinearLayout)findViewById(R.id.layout_menucontainer);
		layout_menuback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				closeMenuAnim();	
			}
		});
		if(!A_0_App.getInstance().mQuickSwitchState){
			img_btnxuanmenufupower.setVisibility(View.GONE);
		}else{
			img_btnxuanmenufupower.setVisibility(View.VISIBLE);
		}
		
		img_btnxuanmenufupower.setOnClickListener(new OnClickListener() {
			
			@SuppressLint("ShowToast") @Override
			public void onClick(View arg0) {
				if (!FastClick(0.2)) {
				if(!animRuning){
					if(layout_menuback.getVisibility() == View.GONE){									
						startMenuAnim();
					}else{
						closeMenuAnim();						
					}
				}else{
								
				}	}			
			}
		});
		
		for(int i = 0;i < menuItemRes.length;i++){
			LinearLayout itemView = (LinearLayout) findViewById(menuItemRes[i]);
			itemView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					switch (arg0.getId()) {
					case R.id.iv_smsnotice:	
						A_0_App.getInstance().setmCurrentFastIconClickType(1);
						break;
		            case R.id.iv_appnotice:
		            	A_0_App.getInstance().setmCurrentFastIconClickType(0);
						break;
					default:
						break;
					}
					new Handler().postDelayed(new Runnable(){    
					    public void run() {    
					    	closeMenuAnim();
					    }    
					 }, 300); 
					yanzhengShoushiPassWord();
					
				}
			});
			
            menuItemViewList.add(itemView);
        }
								
	}
	
	/**
	 * 判断是否设置了手势密码
	 */
	private void yanzhengShoushiPassWord() {
		// 通知
	    if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
            A_0_App.getInstance().enter_Perfect_information(A_Main_Acy.this,true);
            return;
        }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
            A_0_App.getInstance().enter_Perfect_information(A_Main_Acy.this,true);
            return;
        }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)){
            PubMehods.showToastStr(A_Main_Acy.this, R.string.str_no_certified_open);
            return;
        }
		if (A_0_App.getInstance().mNoticeGesure.equals("0")) {// 未设置过给提示
			showDialog();
		} else if (A_0_App.getInstance().mNoticeGesure.equals("1")) {// 为开 去验证手势密码 
			Intent intent_1 = new Intent(mContext,B_Side_Notice_Gesture_Verifcation.class);
			intent_1.putExtra("enter_acy", 4);
			startActivity(intent_1);
		} else if (A_0_App.getInstance().mNoticeGesure.equals("2")) {// 为关
			startIntentPage();
		}

	}
	
	protected void showDialog() {
		final GeneralDialog upDateDialog = new GeneralDialog(this, R.style.Theme_GeneralDialog);
		upDateDialog.setTitle(R.string.gesture_setting_title);
		upDateDialog.setContent(R.string.gesture_setting_content);
		upDateDialog.setCancelable(true);
		upDateDialog.setCanceledOnTouchOutside(true);
		upDateDialog.showLeftButton(R.string.pub_temporarily_open,
				new OnClickListener() {
					@Override
					public void onClick(View v) {  //暂不开启
						A_0_App.getInstance().saveNoticeGesture("2");
						upDateDialog.cancel();
						PubMehods.showToastStr(mContext,R.string.str_open_gesture_set);
						startIntentPage();
					}
				});
		upDateDialog.showRightButton(R.string.pub_immediately_open,
				new OnClickListener() {   //立即开启
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(mContext,B_Side_Notice_GestureSet_Setting.class);
						intent.putExtra("enter_acy", 4);
						startActivity(intent);
						upDateDialog.cancel();
					}
				});
		upDateDialog.show();
	}
	
	/**
	 * 打开新页面
	 */
	private void startIntentPage() {
		switch (A_0_App.getInstance().getmCurrentFastIconClickType()) {
		case 1:
			Intent intent = new Intent(mContext, B_Side_Notice_Main_Sent_SMS.class);
			intent.putExtra("enter_acy_type", 1);
			startActivity(intent);	
			break;
        case 0:
        	Intent intent1 = new Intent(mContext, B_Side_Notice_Main_Sent_Notice.class);
        	intent1.putExtra("enter_acy_type", 1);
			startActivity(intent1);	
			break;
		default:
			break;
		}

	}
	
	/**
	 * 逐个向上伸展动画
	 */
    private void startMenuAnim() {
    	isOpenStatus = true;
    	animRuning = true;
    	layout_menuback.setVisibility(View.VISIBLE);   	
    	img_btnxuanmenufupower.setImageDrawable(this.getResources().getDrawable(R.drawable.btn_first_sendnotice_close_selector));
    	startMenuBackgroundAnim();
    	
    	
        for (int i = 1; i < menuItemViewList.size(); i++) {
        	AnimatorSet animatorSet = new AnimatorSet();//组合动画  
        	ObjectAnimator anim_location = ObjectAnimator.ofFloat( menuItemViewList.get(i),"translationY",0F,-i * (screenWidth*216/1080+15));
        	ObjectAnimator anim_toumingdu = ObjectAnimator.ofFloat(menuItemViewList.get(i), "alpha", 0f, 0.1f,0.2f,0.3f, 0.4f, 0.5f,0.6f, 0.7f,0.8f,0.9f,1f);  
            anim_location.setDuration(animDuration);
//            anim_location.setStartDelay(10);
            anim_location.setInterpolator(new DecelerateInterpolator());
            animatorSet.play(anim_location).with(anim_toumingdu);//两个动画同时开始  
            animatorSet.start();   
            animatorSet.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator arg0) {}
				
				@Override
				public void onAnimationRepeat(Animator arg0) {}
				
				@Override
				public void onAnimationEnd(Animator arg0) {					
					animRuning = false;
					android.util.Log.i("abc", "------end--");
				}
				
				@Override
				public void onAnimationCancel(Animator arg0) {}
			});
        }
    	
        //为了防止弹出动画开始时 菜单项突然堆叠 所以延时出现菜单项 （在动画播出到三分之一的时候出现）
        mHandler.postDelayed(showMenuItemContainerRunable, animDuration/5);
       
    }
    
    /**
     * 关闭动画
     */
    private void closeMenuAnim() {
    	if(!isOpenStatus){
    		return;
    	}
    	animRuning = true;
    	closeMenuBackgroundAnim();
    	
    	img_btnxuanmenufupower.setImageDrawable(this.getResources().getDrawable(R.drawable.btn_first_sendnotice_open_selector));
        for (int i = 1; i < menuItemViewList.size(); i++) {
        	AnimatorSet animatorSet = new AnimatorSet();//组合动画  
            ObjectAnimator anim_location = ObjectAnimator.ofFloat(menuItemViewList.get(i),"translationY",-i*(screenWidth*216/1080+15),0);
            ObjectAnimator anim_toumingdu = ObjectAnimator.ofFloat(menuItemViewList.get(i), "alpha", 1f, 0.9f,0.8f, 0.6f,0.5f, 0.4f, 0.3f,0.2f,0.1f,0f);  
            anim_location.setDuration(animDuration);
//            anim_location.setStartDelay(50);
            anim_location.setInterpolator(new DecelerateInterpolator());
            animatorSet.play(anim_location).with(anim_toumingdu);//两个动画同时开始  
            animatorSet.start();
            
            animatorSet.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
					android.util.Log.i("abc", "----begin--");
					animRuning = true;
//					 //为了防止白色背景突然隐藏消失 这里延时隐藏白色背景 加300毫秒为了让菜单动画充分执行完毕
//			        mHandler.postDelayed(hindMenuBackgroundRunable, animDuration +300);  
				}
				
				@Override
				public void onAnimationRepeat(Animator arg0) {}
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					animRuning = false;
					android.util.Log.i("abc", "------end--");
				}
				
				@Override
				public void onAnimationCancel(Animator arg0) {}
			});
        }
    	
    	
//        //为了防止白色背景突然隐藏消失 这里延时隐藏白色背景 加300毫秒为了让菜单动画充分执行完毕
        mHandler.postDelayed(hindMenuBackgroundRunable, animDuration +200);      
        isOpenStatus = false;
    }
	
	
//    /**
//     * 透明度渐变
//     */
//    private void startMenuAnim1() {
//    	animRuning = true;
//    	layout_menuback.setVisibility(View.VISIBLE);   	
//    	img_btnxuanmenufupower.setImageDrawable(this.getResources().getDrawable(R.drawable.btn_notice_close));
//    	startMenuBackgroundAnim();
//    	
//    	ObjectAnimator anim_toumingdu = ObjectAnimator.ofFloat(layout_menucontainer, "alpha", 0f, 0.1f,0.2f,0.3f, 0.4f, 0.5f,0.6f, 0.7f,0.8f,0.9f,1f);  
//    	anim_toumingdu.setDuration(animDuration);
//    	anim_toumingdu.setStartDelay(50);
//    	anim_toumingdu.setInterpolator(new DecelerateInterpolator());
//    	anim_toumingdu.start();   
//
//    	
//        /**
//         * 动画播放监听
//         */
//    	anim_toumingdu.addListener(new AnimatorListener() {
//			
//			@Override
//			public void onAnimationStart(Animator arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onAnimationRepeat(Animator arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onAnimationEnd(Animator arg0) {
//				 animRuning = false;
//				
//			}
//			
//			@Override
//			public void onAnimationCancel(Animator arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//    	
//        //为了防止弹出动画开始时 菜单项突然堆叠 所以延时出现菜单项 （在动画播出到三分之一的时候出现）
//        mHandler.postDelayed(showMenuItemContainerRunable, animDuration/3);
//       
//
//	}
//    
//    
//    
//    /**
//     * 关闭动画
//     */
//    private void closeMenuAnim1() {
//    	animRuning = true;
//    	img_btnxuanmenufupower.setImageDrawable(this.getResources().getDrawable(R.drawable.btn_notice_start));
//    	closeMenuBackgroundAnim();    	
//    	
//    	ObjectAnimator anim_toumingdu = ObjectAnimator.ofFloat(layout_menucontainer, "alpha", 1f, 0.9f,0.8f, 0.6f,0.5f, 0.4f, 0.3f,0.2f,0.1f,0f);  
//    	anim_toumingdu.setDuration(animDuration);
//    	anim_toumingdu.setStartDelay(50);
//    	anim_toumingdu.setInterpolator(new DecelerateInterpolator());
//    	anim_toumingdu.start();   
//    	
//        anim_toumingdu.addListener(new AnimatorListener() {
//			
//			@Override
//			public void onAnimationStart(Animator arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onAnimationRepeat(Animator arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void onAnimationEnd(Animator arg0) {
//				 animRuning = false;
//				
//			}
//			
//			@Override
//			public void onAnimationCancel(Animator arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
////        //为了防止白色背景突然隐藏消失 这里延时隐藏白色背景 加300毫秒为了让菜单动画充分执行完毕
//        mHandler.postDelayed(hindMenuBackgroundRunable, animDuration +300);     
//    }
//    
//	
//	
//	
//	 
//   
//	
//    /**
//     * 从底部向上整体伸展
//     */
//    private void startMenuAnim2() {
//    	animRuning = true;
//    	layout_menuback.setVisibility(View.VISIBLE);   	
//    	layout_menucontainer.setVisibility(View.VISIBLE);
//    	img_btnxuanmenufupower.setImageDrawable(this.getResources().getDrawable(R.drawable.btn_notice_close));
//    	startMenuBackgroundAnim();       
//    		
//    	//方案2
//    	 AnimatorSet animatorSet = new AnimatorSet();//组合动画  
//    	 ObjectAnimator anim_location = ObjectAnimator.ofFloat(layout_menucontainer,"translationY",130,120,110,100,90,80,70,50,40,30,20,10,0);
//    	 ObjectAnimator anim_toumingdu = ObjectAnimator.ofFloat(layout_menucontainer, "alpha",  0f, 0.1f,0.2f,0.3f, 0.4f, 0.5f,0.6f, 0.7f,0.8f,0.9f,1f); 
//    	 anim_location.setDuration(animDuration);
//    	 anim_location.setStartDelay(100);
//    	 anim_location.setInterpolator(new DecelerateInterpolator());
//    	 animatorSet.play(anim_location).with(anim_toumingdu);//两个动画同时开始  
//         animatorSet.start();
//    	
//        /**
//         * 动画播放监听
//         */
//        animatorSet.addListener(new AnimatorListener() {
//			
//			@Override
//			public void onAnimationStart(Animator arg0) {}
//			
//			@Override
//			public void onAnimationRepeat(Animator arg0) {}
//			
//			@Override
//			public void onAnimationEnd(Animator arg0) {
//				 animRuning = false;
//			}
//			
//			@Override
//			public void onAnimationCancel(Animator arg0) {}
//		});
//    	
//        //为了防止弹出动画开始时 菜单项突然堆叠 所以延时出现菜单项 （在动画播出到三分之一的时候出现）
////        mHandler.postDelayed(showMenuItemContainerRunable, animDuration/10);
////        layout_menucontainer.setVisibility(View.VISIBLE);	
//
//	}
//    
//    private void closeMenuAnim2() {
//    	
//    	animRuning = true;
//    	img_btnxuanmenufupower.setImageDrawable(this.getResources().getDrawable(R.drawable.btn_notice_start));
//    	closeMenuBackgroundAnim();    	    	
//    	
//    	AnimatorSet animatorSet = new AnimatorSet();//组合动画  
//        ObjectAnimator anim_location = ObjectAnimator.ofFloat(layout_menucontainer,"translationY",0,10,20,30,40,50,60,70,80,90,100,110,120,130);
//        ObjectAnimator anim_toumingdu = ObjectAnimator.ofFloat(layout_menucontainer, "alpha", 1f, 0.9f,0.8f, 0.6f,0.5f, 0.4f, 0.3f,0.2f,0.1f,0f);  
//        anim_location.setDuration(animDuration);
//        anim_location.setStartDelay(100);
//        anim_location.setInterpolator(new DecelerateInterpolator());
//        animatorSet.play(anim_location).with(anim_toumingdu);//两个动画同时开始  
//        animatorSet.start();
//    	
//        anim_toumingdu.addListener(new AnimatorListener() {
//			
//			@Override
//			public void onAnimationStart(Animator arg0) {}
//			
//			@Override
//			public void onAnimationRepeat(Animator arg0) {}
//			
//			@Override
//			public void onAnimationEnd(Animator arg0) {
//				 animRuning = false;				
//			}
//			
//			@Override
//			public void onAnimationCancel(Animator arg0) {}
//		});
////        //为了防止白色背景突然隐藏消失 这里延时隐藏白色背景 加300毫秒为了让菜单动画充分执行完毕
//        mHandler.postDelayed(hindMenuBackgroundRunable, animDuration + 200);     
////        layout_menucontainer.setVisibility(View.VISIBLE);	
//
//	}
    
    private void startMenuBackgroundAnim() {
    	ObjectAnimator translationUp = ObjectAnimator.ofInt(layout_menuback,"backgroundColor", touyming, baise_bantouyming);  
        translationUp.setDuration(animDuration);   
        translationUp.setEvaluator(new ArgbEvaluator());  
        translationUp.start();  
	}
    
    private void closeMenuBackgroundAnim() {
    	ObjectAnimator translationUp = ObjectAnimator.ofInt(layout_menuback,"backgroundColor",  baise_bantouyming,touyming);  
        translationUp.setInterpolator(new DecelerateInterpolator());  
        translationUp.setDuration(animDuration);    
        translationUp.setEvaluator(new ArgbEvaluator());  
        translationUp.start();  

	}
	
    /**
	 * 隐藏全屏的白色背景蒙层和菜单项
	 */
    Runnable showMenuItemContainerRunable = new Runnable() {
		public void run() {
			 layout_menucontainer.setVisibility(View.VISIBLE);			
		};
	};	
	
	/**
	 * 隐藏全屏的白色背景蒙层和菜单项
	 */
    Runnable hindMenuBackgroundRunable = new Runnable() {
		public void run() {
			layout_menucontainer.setVisibility(View.GONE);
			layout_menuback.setVisibility(View.GONE);
			
		};
	};	
    
    // 首页未读消息数量展示
    public void showMainNoReadMessCount(int no_count) {
        if (tv_index_mess_no_read_count == null)
            return;
        if (no_count <= 0) {
            tv_index_mess_no_read_count.setVisibility(View.GONE);
        } else if (0 < no_count && no_count <= 99) {
            tv_index_mess_no_read_count.setVisibility(View.VISIBLE);
            tv_index_mess_no_read_count.setBackgroundResource(R.drawable.icon_message_noread);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
            int left=DensityUtils.dip2px(this, 30);
			layoutParams.setMargins(left, 0, 0, 0);// 4个参数按顺序分别是左上右下
			tv_index_mess_no_read_count.setLayoutParams(layoutParams);
            tv_index_mess_no_read_count.setText(no_count + "");
        } else if (99 < no_count && no_count <= 999) {
            tv_index_mess_no_read_count.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
            int left=DensityUtils.dip2px(this, 33);
			layoutParams.setMargins(left, 0, 0, 0);// 4个参数按顺序分别是左上右下
			tv_index_mess_no_read_count.setLayoutParams(layoutParams);
            tv_index_mess_no_read_count.setBackgroundResource(R.drawable.icon_rong_message_noread);
            tv_index_mess_no_read_count.setText(no_count + "");
        } else if (no_count > 999) {
            tv_index_mess_no_read_count.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
            int left=DensityUtils.dip2px(this, 33);
			layoutParams.setMargins(left, 0, 0, 0);// 4个参数按顺序分别是左上右下
			tv_index_mess_no_read_count.setLayoutParams(layoutParams);
            tv_index_mess_no_read_count.setBackgroundResource(R.drawable.icon_rong_message_noread);
            tv_index_mess_no_read_count.setText("999+");
        }
    }
    
    // 身边未读消息红点展示
    public void showSideNoReadTag(boolean visible) {
        if (visible) {
            int width = DensityUtils.dip2px(this, 12);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    width,width);
            int left = DensityUtils.dip2px(this, 38);
            int top = DensityUtils.dip2px(this, 10);
            layoutParams.setMargins(left, top, 0, 0);// 4个参数按顺序分别是左上右下
            mIV_Side_No_Read_Tag.setLayoutParams(layoutParams);
            mIV_Side_No_Read_Tag.setVisibility(View.VISIBLE);
        } else {
            mIV_Side_No_Read_Tag.setVisibility(View.GONE);
        }
    }
    
	private OnClickListener onToolbarMainClickListener = new View.OnClickListener() {

		public void onClick(View v) {
		    A_0_App.getInstance().checkUpdateVersion(instance);
			switch (v.getId()) {
			case R.id.toolbar_seracher:
				//消息
				if(!A_0_App.getInstance().mQuickSwitchState){
					img_btnxuanmenufupower.setVisibility(View.GONE);
				}else{
					img_btnxuanmenufupower.setVisibility(View.VISIBLE);
				}
				
				position=1;				
				UpdateView(ActivityID.STARTHOME);
				break;
			case R.id.toolbar_promate:
				//通讯录
				position=0;
				img_btnxuanmenufupower.setVisibility(View.GONE);
				UpdateView(ActivityID.PROMATE);
				 
				break;
			case R.id.toolbar_account:
				//身边
				position=1;
				img_btnxuanmenufupower.setVisibility(View.GONE);
				UpdateView(ActivityID.ACCOUNT);
				break;
				//我
			case R.id.toolbar_more:
				position=1;
				img_btnxuanmenufupower.setVisibility(View.GONE);
				UpdateView(ActivityID.MORE);
				break;
			}
		}
	};
	//切换页面
	public boolean UpdateView(ActivityID id) {
		Class activityClass = null;
		if ((activityClass = mActivity.get(id.toString())) == null)
			return false;
		if (!switchActivity(id))
			return false;

		mContainer.removeAllViews();
		Intent intent = new Intent(this, activityClass);
//		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    View view = getLocalActivityManager().startActivity(id.toString(),intent).getDecorView();
		mContainer.addView(view);
		return true;
	}
	
	private boolean switchActivity(ActivityID id) {
		if (id == null || id == mCurActivity)
			return false;
		toolbarItemNormal(mCurActivity);
		toolbarItemSelect(id);
		mCurActivity = id;
		return true;
	}

	/*
	 * 工具栏列表正常状态
	 */
	private void toolbarItemNormal(ActivityID id) {
		if (id == null)
			return;
		switch (id) {
		case STARTHOME:
			if (null != mToolRlayoutSearcher) {
//				mToolRlayoutSearcher.setBackgroundResource(R.drawable.toolbar_bg_change);
				mIVSearcher.setBackgroundResource(R.drawable.ic_main_my_mess_nor);
			}
			break;
		case PROMATE:
			if (null != mToolRlayoutPromate) {
//				mToolRlayoutPromate.setBackgroundResource(R.drawable.toolbar_bg_change);
				mIVPromate.setBackgroundResource(R.drawable.ic_main_my_sch_nor);
			}
			break;
		case ACCOUNT:
			if (null != mToolRlayoutAccount) {
//				mToolRlayoutAccount.setBackgroundResource(R.drawable.toolbar_bg_change);
				mIVAccount.setBackgroundResource(R.drawable.ic_main_my_side_nor);
			}
			break;
		case MORE:
			if (null != mToolRlayoutMore) {
//				mToolRlayoutMore.setBackgroundResource(R.drawable.toolbar_bg_change);
				mIVMore.setBackgroundResource(R.drawable.ic_main_my_acc_nor);
			}
			break;
		}
	}

	/*
	 * 工具栏列表选中状态
	 */
	private void toolbarItemSelect(ActivityID id) {
		if (id == null)
			return;
		switch (id) {
		case STARTHOME:
			if (null != mToolRlayoutSearcher) {
				
//				mToolRlayoutSearcher.setBackgroundColor(getResources().getColor(R.color.transparent));
				mIVSearcher.setBackgroundResource(R.drawable.ic_main_my_mess_pre);
			}
			break;
		case PROMATE:
			if (null != mToolRlayoutPromate) {
//				mToolRlayoutPromate.setBackgroundColor(getResources().getColor(R.color.transparent));
				mIVPromate.setBackgroundResource(R.drawable.ic_main_my_sch_pre);
			}
			break;
		case ACCOUNT:
			if (null != mToolRlayoutAccount) {
//				mToolRlayoutAccount.setBackgroundColor(getResources().getColor(R.color.transparent));
				mIVAccount.setBackgroundResource(R.drawable.ic_main_my_side_pre);
			}
			break;

		case MORE:
			if (null != mToolRlayoutMore) {
//				mToolRlayoutMore.setBackgroundColor(getResources().getColor(R.color.transparent));
				mIVMore.setBackgroundResource(R.drawable.ic_main_my_acc_pre);
			}
			break;
		}
	}
	
    private void showToast(int id) {
        PubMehods.showToastStr(A_Main_Acy.this, getResources().getString(id));
    }
	
	@SuppressWarnings("main  destroy error")
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mApp.setRuning(false);
	}

    public static void logD(String msg) {
        LogUtils.logD("MainActivity", msg);
    }

    public static void logE(String msg) {
        LogUtils.logE("MainActivity", msg);
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                	
                if(layout_menuback.getVisibility() != View.GONE){			
                	if(animRuning){
                		return false;
                	}
                	closeMenuAnim();	
                	return false;
				}
                	
                if (position==1) {
                	 moveTaskToBack(true);
				}else{
                	if (A_0_App.hadIntercept==true) {
//                		 final GeneralDialog upDateDialog = new GeneralDialog(A_Main_Acy.this,R.style.Theme_GeneralDialog);
//                         upDateDialog.setTitle(R.string.pub_title);
//                         upDateDialog.setContent(R.string.put_title_exit);
//                         upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
//                             @Override
//                             public void onClick(View v) {
//                                 upDateDialog.cancel();
//                             }
//                         });
//                         upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
//                             @Override
//                             public void onClick(View v) {
//                             	overridePendingTransition(R.anim.animal_slide_out_top, R.anim.animal_slide_out_top);
//                                 upDateDialog.cancel();
//                                 A_0_App.getInstance().exit(true);
//                             }
//                         });
//                         upDateDialog.show();
//                	    参数为false代表只有当前activity是task根，指应用启动的第一个activity时，才有效;
//                	    如果为true则忽略这个限制，任何activity都可以有效。
                	 moveTaskToBack(false);
                    } else {
                        if (A_0_App.contace_tab_posi == 0) {//我的同事
                            Intent intent = new Intent("stu");
                            intent.putExtra("name", "colleagure");
                            sendBroadcast(intent);
                        } else if (A_0_App.contace_tab_posi == 1) {//我的学生
                            Intent intent1 = new Intent("stu");
                            intent1.putExtra("name", "stu");
                            sendBroadcast(intent1);
                        }
                    }
				}
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    private  long lastClickTime;
    public synchronized  boolean FastClick(Double interval) {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < 1000 * interval) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	@Override
	protected void onResume() {
		if (!MPermissions.shouldShowRequestPermissionRationale(A_Main_Acy.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUECT_CODE_SDCARD)) {
			MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}else{
			if (!MPermissions.shouldShowRequestPermissionRationale(A_Main_Acy.this, Manifest.permission.RECORD_AUDIO, REQUECT_CODE_RECORD_AUDIO)) {
				MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
			}else{
				if (!MPermissions.shouldShowRequestPermissionRationale(A_Main_Acy.this, Manifest.permission.CAMERA, REQUECT_CODE_CAMERA)) {
					MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
				}else{
					if (!MPermissions.shouldShowRequestPermissionRationale(A_Main_Acy.this, Manifest.permission.READ_PHONE_STATE, REQUECT_READ_PHONESTATE)) {
						MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_READ_PHONESTATE, Manifest.permission.READ_PHONE_STATE);
					}else{
//						toDoAction();
					}
				}
			}
		}
		super.onResume();
	}

	private static final int REQUECT_CODE_SDCARD = 11;//内存卡
	private static final int REQUECT_CODE_RECORD_AUDIO = 12;//麦克风
	private static final int REQUECT_CODE_CAMERA = 13;//拍照
	private static final int REQUECT_READ_PHONESTATE = 14;//拍照
	private String string_sdcard = "存储卡";
	private String string_record_audio = "麦克风";
	private String string_camera = "拍照";
	private String string_read_phone_state = "手机识别码";

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@PermissionGrant(REQUECT_CODE_SDCARD)
	public void requestSdcardSuccess()
	{
		MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
	}

	@PermissionGrant(REQUECT_CODE_RECORD_AUDIO)
	public void requestRecordAudioSuccess()
	{
		MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
	}

	@PermissionGrant(REQUECT_CODE_CAMERA)
	public void requestCameraSuccess()
	{
		MPermissions.requestPermissions(A_Main_Acy.this, REQUECT_READ_PHONESTATE, Manifest.permission.READ_PHONE_STATE);
	}

	@PermissionGrant(REQUECT_READ_PHONESTATE)
	public void requestPhoneStateSuccess()
	{
//		toDoAction();
	}

	@PermissionDenied(REQUECT_CODE_SDCARD)
	public void requestSdcardFailed() {
		showTitleDialog(string_sdcard);
	}

	@PermissionDenied(REQUECT_CODE_RECORD_AUDIO)
	public void requestRecordAudioFailed() {
		showTitleDialog(string_record_audio);
	}

	@PermissionDenied(REQUECT_CODE_CAMERA)
	public void requestCameraFailed() {
		showTitleDialog(string_camera);
	}

	@PermissionDenied(REQUECT_READ_PHONESTATE)
	public void requestPhoneStateFailed() {
		showTitleDialog(string_read_phone_state);
	}

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//    }
//
//    @PermissionSuccess(requestCode = REQUECT_CODE_SDCARD)
//    public void requestSdcardSuccess() {
//        PermissionGen.needPermission(A_Main_Acy.this, REQUECT_CODE_RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
//    }
//
//    @PermissionSuccess(requestCode = REQUECT_CODE_RECORD_AUDIO)
//    public void requestRecordAudioSuccess() {
//        PermissionGen.needPermission(A_Main_Acy.this, REQUECT_CODE_CAMERA, Manifest.permission.CAMERA);
//    }
//
//    @PermissionSuccess(requestCode = REQUECT_CODE_CAMERA)
//    public void requestCameraSuccess() {
//        toDoAction();
//    }
//
//    @PermissionFail(requestCode = REQUECT_CODE_SDCARD)
//    public void requestSdcardFailed() {
//        showTitleDialog(string_sdcard);
//    }
//
//    @PermissionFail(requestCode = REQUECT_CODE_RECORD_AUDIO)
//    public void requestRecordAudioFailed() {
//        showTitleDialog(string_record_audio);
//    }
//
//    @PermissionFail(requestCode = REQUECT_CODE_CAMERA)
//    public void requestCameraFailed() {
//        showTitleDialog(string_camera);
//    }

	private GeneralDialog upDateDialog;

	public void showTitleDialog(String permissionName) {
		if (instance == null){
			System.exit(0);
			return;
		}
		if (!upDateDialog.isShowing()) {
			upDateDialog = new GeneralDialog(A_Main_Acy.this,
					R.style.Theme_GeneralDialog);
			upDateDialog.setTitle(R.string.str_permission_request);
			upDateDialog.setContent(getPermessionStr(permissionName));
			upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (upDateDialog != null)
						upDateDialog.cancel();
					android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
			upDateDialog.showRightButton(R.string.pub_go_set, new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent localIntent = new Intent();
					localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
					localIntent.setData(Uri.fromParts("package", getPackageName(), null));
					startActivity(localIntent);
					upDateDialog.cancel();
					finish();
				}
			});
			upDateDialog.setCancelable(false);
			upDateDialog.setCanceledOnTouchOutside(false);
			upDateDialog.show();
		}
	}

	public String getPermessionStr(String permessName) {
		return "在设置 - 应用 - " + getString(R.string.app_name) + " - 权限中开启" + permessName + "权限，以正常使用"+A_0_App.APP_NAME+"相关功能服务";

	}
}