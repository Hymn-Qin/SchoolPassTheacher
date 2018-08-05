package io.dcloud;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yuanding.schoolteacher.A_0_App;
import com.yuanding.schoolteacher.A_3_0_Login_Acy_Teacher;
import com.yuanding.schoolteacher.A_Main_Acy;
import com.yuanding.schoolteacher.A_Main_My_Message_Acy;
import com.yuanding.schoolteacher.B_Side_Notice_Main;
import com.yuanding.schoolteacher.Pub_WebView_Load_Acy;
import com.yuanding.schoolteacher.R;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import java.lang.reflect.Method;

import io.dcloud.common.DHInterface.ICore;
import io.dcloud.common.DHInterface.ICore.ICoreStatusListener;
import io.dcloud.common.DHInterface.ISysEventListener.SysEventType;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.IWebviewStateListener;
import io.dcloud.feature.internal.sdk.SDK;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

/**
 * 本demo为以webview控件方式集成5+ sdk， 
 *
 */
public class SDK_WebView extends Activity {

	boolean doHardAcc = true;
	EntryProxy mEntryProxy = null;
	public static SDK_WebView mContext;
	private int acy_type;//1表示身边进入  2表示登录进入
	FrameLayout rootView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mContext = this;
		if(getIntent() != null){
			acy_type = getIntent().getExtras().getInt("acy_type",1);
		}
		if (mEntryProxy == null) {
			 rootView = new FrameLayout(this);
			// 创建5+内核运行事件监听
			WebviewModeListener wm = new WebviewModeListener(SDK_WebView.this, rootView);
			// 初始化5+内核
			mEntryProxy = EntryProxy.init(this, wm);
			// 启动5+内核
			mEntryProxy.onCreate(this, savedInstanceState, SDK.IntegratedMode.WEBVIEW, null);
			if (!A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()){
				setContentView(R.layout.pub_read_faile_enroll);
				RelativeLayout button = (RelativeLayout)findViewById(R.id.home_loading_error);
				LinearLayout liner_titlebar_back = (LinearLayout)findViewById(R.id.liner_titlebar_back);
				liner_titlebar_back.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						finish();
					}
										  });
				button.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if(!A_0_App.getInstance().getNetWorkManager().isNetWorkConnected())
						{
							PubMehods.showToastStr(SDK_WebView.this, "请检查您的网络设置");
							return;
						}else{
							setContentView(rootView);						}

						//hideErrorPage();
					}
				});
			}else {
				setContentView(rootView);
			}

			rootView.setFitsSystemWindows(false);
			if (checkDeviceHasNavigationBar(SDK_WebView.this)==true){
				rootView.setPadding(0,0,0,getVirtualBarHeigh(SDK_WebView.this));
			}

			startListtenerRongYun();
			MPermissions.requestPermissions(SDK_WebView.this, REQUECT_CODE_CALLPHONE, Manifest.permission.CALL_PHONE);
		}
	}

	//登录超时，供JS调用
	public static void doLogin() {
		A_0_App.getInstance().WB_BangDou_Tag = 3;
		PubMehods.showToastStr(mContext, R.string.str_token_timeout);
		mContext.startActivity(new Intent(mContext, A_3_0_Login_Acy_Teacher.class));
		A_0_App.getInstance().clearUserSpInfo(false);
		mContext.finish();
		mContext.overridePendingTransition(R.anim.animal_push_right_in_normal, R.anim.animal_push_right_out_normal);
	}

	//关闭页面设置
	private void finishAcyGo(int acy_type) {
		if (acy_type == 2) {
			startAcy(getApplicationContext(), A_Main_Acy.class);
			A_0_App.getInstance().WB_BangDou_Tag = 0;
			finish();

		} else {
			finish();
		}
	}

	private void startAcy(Context packageContext, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(packageContext, cls);
		startActivity(intent);
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
					A_Main_My_Message_Acy
							.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
					// A_0_App.getInstance().showExitDialog(B_Side_Notice_Main.this,getResources().getString(R.string.token_timeout));
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
							A_0_App.getInstance().showExitDialog(mContext,
									AppStrStatic.kicked_offline());
							Looper.loop();
						}
					}
					LooperThread looper = new LooperThread();
					looper.start();
					break;
			}
		}
	}String namePhone;
	String telephone;
	private static final int REQUECT_CODE_CAMERA = 2;
	private static final int REQUECT_CODE_ACCESS_FINE_LOCATION = 3;
	private static final int REQUECT_CODE_CALLPHONE = 4;
//	A_0_App.getInstance().callSb(getActivity(), namePhone, telephone, new A_0_App.PhoneCallBack() {
//		@Override
//		public void sPermission() {
//			MPermissions.requestPermissions(getActivity(), REQUECT_CODE_CALLPHONE, Manifest.permission.CALL_PHONE);
//		}
//	}
//	);

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
	{
		MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}
	@PermissionGrant(REQUECT_CODE_CALLPHONE)
	public void requestCallPhoneSuccess()
	{
		//A_0_App.getInstance().PermissionToas("成功", SDK_WebView.this);
	}

	@PermissionDenied(REQUECT_CODE_CALLPHONE)
	public void requestCallPhoneFailed()
	{
		A_0_App.getInstance().PermissionToas("拨打电话", SDK_WebView.this);
	}


	public static boolean checkDeviceHasNavigationBar(Context context) {
		boolean hasNavigationBar = false;
		Resources rs = context.getResources();
		int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
		if (id > 0) {
			hasNavigationBar = rs.getBoolean(id);
		}
		try {
			Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
			Method m = systemPropertiesClass.getMethod("get", String.class);
			String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
			if ("1".equals(navBarOverride)) {
				hasNavigationBar = false;
			} else if ("0".equals(navBarOverride)) {
				hasNavigationBar = true;
			}
		} catch (Exception e) {

		}
		return hasNavigationBar;

	}

	/**
	 * 获取虚拟功能键高度
	 * @param context
	 * @return
	 */
	public static int getVirtualBarHeigh(Context context) {
		int vh = 0;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		try {
			@SuppressWarnings("rawtypes")
			Class c = Class.forName("android.view.Display");
			@SuppressWarnings("unchecked")
			Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
			method.invoke(display, dm);
			vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vh;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return mEntryProxy.onActivityExecute(this, SysEventType.onCreateOptionMenu, menu);
	}

	//沉沁式状态栏
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && Build.VERSION.SDK_INT >= 21) {
			try {
				//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = SDK_WebView.this.getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(SDK_WebView.this.getResources().getColor(R.color.transparent));
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

				//底部导航栏
				//window.setNavigationBarColor(activity.getResources().getColor(colorResId));
				//}
			} catch (Exception e) {
				e.printStackTrace();
			}
			View decorView = getWindow().getDecorView();
			decorView.setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_STABLE
							|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
							|View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

		}
	}

	@Override
	public void onPause() {
		super.onPause();
		mEntryProxy.onPause(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		mEntryProxy.onResume(this);
	}

	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent.getFlags() != 0x10600000) {// 非点击icon调用activity时才调用newintent事件
			mEntryProxy.onNewIntent(this, intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mEntryProxy.onStop(this);
		finishAcyGo(acy_type);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyDown, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyUp, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		boolean _ret = mEntryProxy.onActivityExecute(this, SysEventType.onKeyLongPress, new Object[] { keyCode, event });
		return _ret ? _ret : super.onKeyLongPress(keyCode, event);
	}

	public void onConfigurationChanged(Configuration newConfig) {
		try {
			int temp = this.getResources().getConfiguration().orientation;
			if (mEntryProxy != null) {
				mEntryProxy.onConfigurationChanged(this, temp);
			}
			super.onConfigurationChanged(newConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mEntryProxy.onActivityExecute(this, SysEventType.onActivityResult, new Object[] { requestCode, resultCode, data });
	}

}

class WebviewModeListener implements ICoreStatusListener {

	LinearLayout btns = null;
	Activity activity = null;
	ViewGroup mRootView = null;
	IWebview webview = null;
	ProgressDialog pd = null;

	public WebviewModeListener(final Activity activity, ViewGroup rootView) {
		this.activity = activity;
		mRootView = rootView;
		btns = new LinearLayout(activity);
		mRootView.setBackgroundColor(0xffffffff);
		mRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				try {
					webview.onRootViewGlobalLayout(mRootView);
				} catch (Exception e) {
					activity.finish();
				}
			}
		});
	}

	/**
	 * 5+内核初始化完成时触发
	 * */
	@Override
	public void onCoreInitEnd(ICore coreHandler) {
		// 设置单页面集成的appid
		String appid = "TestAppid";
		// 单页面集成时要加载页面的路径，可以是本地文件路径也可以是网络路径
		String url = "file:///android_asset/apps/enroll/www/index.html?token=" + A_0_App.USER_TOKEN;
		System.out.println(A_0_App.USER_TOKEN);
		webview = SDK.createWebview(activity, url, appid, new IWebviewStateListener() {
			@Override
			public Object onCallBack(int pType, Object pArgs) {
				switch (pType) {
				case IWebviewStateListener.ON_WEBVIEW_READY:
					// 准备完毕之后添加webview到显示父View中，设置排版不显示状态，避免显示webview时，html内容排版错乱问题
					((IWebview) pArgs).obtainFrameView().obtainMainView().setVisibility(View.INVISIBLE);
					SDK.attach(mRootView, ((IWebview) pArgs));
					break;
				case IWebviewStateListener.ON_PAGE_STARTED:
					// 首页面开始加载事件
					break;
				case IWebviewStateListener.ON_PROGRESS_CHANGED:
					// 首页面加载进度变化
					break;
				case IWebviewStateListener.ON_PAGE_FINISHED:
					// 页面加载完毕，设置显示webview

					webview.obtainFrameView().obtainMainView().setVisibility(View.VISIBLE);
					try {
						A_0_App.getInstance().CancelProgreDialog(activity);
					} catch (Exception e) {
						System.out.println(e);
					}
					break;
				}
				return null;
			}
		});

//		final WebView webviewInstance = webview.obtainWebview();
		// 监听返回键
//		webviewInstance.setOnKeyListener(new OnKeyListener() {
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				if (keyCode == KeyEvent.KEYCODE_BACK) {
//					if (webviewInstance.canGoBack()) {
//						webviewInstance.goBack();
//						return true;
//					}
//				}
//				return false;
//			}
//		});

	}

	// 5+SDK 开始初始化时触发
	@Override
	public void onCoreReady(ICore coreHandler) {
		try {
			// 初始化5+ SDK，
			// 5+SDK的其他接口需要在SDK初始化后才能調用
			SDK.initSDK(coreHandler);
			// 当前应用可使用全部5+API
			SDK.requestAllFeature();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/**
*  	// 通过代码注册扩展插件的示例
*	private void regNewApi() {
*       // 扩展插件在js层的标识
*		String featureName = "T";
*		// 扩展插件的原生类名
*		String className = "com.HBuilder.integrate.webview.WebViewMode_FeatureImpl";
*		// 扩展插件的JS层封装的方法
*		String content = "(function(plus){function test(){return plus.bridge.execSync('T','test',[arguments]);}plus.T = {test:test};})(window.plus);";
*	 	// 向5+SDK注册扩展插件
*		SDK.registerJsApi(featureName, className, content);
*	}
**/

	@Override
	public boolean onCoreStop() {
		// TODO Auto-generated method stub
		return false;
	}
}
