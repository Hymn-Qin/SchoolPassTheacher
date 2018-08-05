
package com.yuanding.schoolteacher;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.yuanding.schoolteacher.A_0_App;
import com.yuanding.schoolteacher.B_Side_Notice_Main;
import com.yuanding.schoolteacher.R;
import com.yuanding.schoolteacher.R.id;
import com.yuanding.schoolteacher.R.layout;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.CircleImageView;
import com.yuanding.schoolteacher.view.gestruepwd.LocusPassWordView;
import com.yuanding.schoolteacher.view.gestruepwd.LocusPassWordView.OnCompleteListener;
import com.yuanding.schoolteacher.view.gestruepwd.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @ClassName: B_Side_Notice_GestureSet_Activity
 * @Description: TODO(重置密码和开启手势密码)公用页面
 * @author Jiaohaili
 * @date 2016年3月11日 下午2:34:06
 */
public class B_Side_Notice_GestureSet_Setting extends Activity {

    private LocusPassWordView lpwv;
    private Button BtnReset;
    private TextView tv_pwd_title;
    private String password = "";
    private int acy_Enter_Type = 0;// 1表示为通知页面进入，2 表示设置页面进入,3表示重设密码进入 4 表示消息页面快捷方式进入
    private ImageView iv_account_por;
    private TextView tv_set_gesture_account_name;
    private ImageLoader imageLoader;
   	private DisplayImageOptions options;
   	private Button  btn_gesture_back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_notice_setpassword_activity);

        acy_Enter_Type = getIntent().getExtras().getInt("enter_acy");
        tv_pwd_title = (TextView) findViewById(R.id.tv_gesture_verivication);
        lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView);
        BtnReset = (Button) this.findViewById(R.id.tvReset);
        iv_account_por=(ImageView) findViewById(R.id.iv_account_por_tag);
        tv_set_gesture_account_name=(TextView) findViewById(R.id.tv_account_name);
        btn_gesture_back=(Button) findViewById(R.id.btn_gesture_back);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.i_default_por_120)
                .showImageForEmptyUri(R.drawable.i_default_por_120) 
                .showImageOnFail(R.drawable.i_default_por_120) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                .displayer(new RoundedBitmapDisplayer(0)) // 设置成圆角图片
                .build(); // 构建完成
        String uri = A_0_App.USER_POR_URL;
        if(iv_account_por.getTag() == null){
            PubMehods.loadServicePic(imageLoader,uri,iv_account_por, options);
            iv_account_por.setTag(uri);
        }else{
            if(!iv_account_por.getTag().equals(uri)){
                PubMehods.loadServicePic(imageLoader,uri,iv_account_por, options);
                iv_account_por.setTag(uri);
            }
        }
        tv_set_gesture_account_name.setText(A_0_App.USER_NAME);
        lpwv.clearPassword();
        tv_pwd_title.setText("请绘制解锁图案");

        lpwv.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(String mPassword) {
                if(password.equals("")){
                    password = mPassword;
                    tv_pwd_title.setText("请再次绘制图案进行确认");
                    saveSetPwd(mPassword,false);
                }else{
                    if (lpwv.verifyPassword(mPassword)) {
                        PubMehods.showToastStr(B_Side_Notice_GestureSet_Setting.this, "密码设置成功");
                        saveSetPwd(mPassword,true);
                    } else {
                        tv_pwd_title.setText("两次密码不一致，请重新绘制");
                        PubMehods.showToastStr(B_Side_Notice_GestureSet_Setting.this, "两次密码不一致，请重新绘制");
                        clearPwd();
                    }
                }
            }
        });
      btn_gesture_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        BtnReset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                clearPwd();
            }
        });
    }
    
    private void clearPwd() {
        password = "";
        lpwv.clearPassword();
    }
    
    //保存设置好的密码
    private void saveSetPwd(String password,Boolean go) {
        if (StringUtil.isNotEmpty(password)) {
            lpwv.resetPassWord(password);
            lpwv.clearPassword();
            // 1表示为通知页面进入，2 表示设置页面进入 4 消息页面快捷方式进入
            if(go){
                if (acy_Enter_Type == 1) {
                    Intent intent=new Intent(B_Side_Notice_GestureSet_Setting.this,B_Side_Notice_Main.class);
                    intent.putExtra("enter_acy_type", 2);//enter_acy_type,首页快捷方式进入为1，身边通知进入为2
                    startActivity(intent);
                }else if(acy_Enter_Type == 4){ 
                	//判断是
                	switch (A_0_App.getInstance().getmCurrentFastIconClickType()) {
					case 1:
						Intent intent = new Intent(B_Side_Notice_GestureSet_Setting.this, B_Side_Notice_Main_Sent_SMS.class);
						intent.putExtra("enter_acy_type", 1);
						startActivity(intent);	
						break;
		            case 0:
		            	Intent intent1 = new Intent(B_Side_Notice_GestureSet_Setting.this, B_Side_Notice_Main_Sent_Notice.class);
		            	intent1.putExtra("enter_acy_type", 1);
						startActivity(intent1);	
						break;
					default:
						break;
					}
                }
                A_0_App.getInstance().saveNoticeGesture("1");
                finish();
            }
        } else {
            lpwv.clearPassword();
            password = "";
        }

    }
    
    @Override
    protected void onDestroy() {
        password ="";
        super.onDestroy();
    }
}
