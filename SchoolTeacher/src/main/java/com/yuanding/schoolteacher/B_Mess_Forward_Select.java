
package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.SendMessageCallback;
import io.rong.imlib.model.Conversation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_Persion_Contact;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.CircleImageView;
import com.yuanding.schoolteacher.view.contact.CharacterParser;
import com.yuanding.schoolteacher.view.contact.PinyinComparator;
import com.yuanding.schoolteacher.view.rongyun.WYZFNoticeContent;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月5日 下午4:12:36 转发选择的主界面
 */
public class B_Mess_Forward_Select extends A_0_CpkBaseTitle_Navi {

    private LinearLayout liner_forward_qun, liner_forward_colleagure, liner_forward_student;
    private Intent intent;
    private String title, content, image_url, type, acy_type, noticeId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.z_connect);

        setTitleText("选择");
        
        intent = getIntent();
        title = intent.getStringExtra("title");
        content = intent.getStringExtra("content");
        type = intent.getStringExtra("type");
        image_url = intent.getStringExtra("image");
        acy_type = intent.getStringExtra("acy_type");
        noticeId = intent.getStringExtra("noticeId");
        
        liner_forward_qun = (LinearLayout) findViewById(R.id.liner_forward_qun);
        liner_forward_colleagure = (LinearLayout) findViewById(R.id.liner_forward_colleagure);
        liner_forward_student = (LinearLayout) findViewById(R.id.liner_forward_student);

        liner_forward_qun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	  if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                      if(!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)){
                    	  goQunliao(v);
                      } else {
                          PubMehods.showToastStr(B_Mess_Forward_Select.this,"您的操作过于频繁！");
                      }
                  } else {
                      PubMehods.showToastStr(B_Mess_Forward_Select.this,R.string.error_title_net_error);
                  }
                
            }
        });

        liner_forward_colleagure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(B_Mess_Forward_Select.this,B_Mess_Forward_Select_Colleague_Cla.class);
                intent.putExtra("title", title);
                intent.putExtra("content",content);
                intent.putExtra("type", type);
                intent.putExtra("image", image_url);
                intent.putExtra("acy_type", acy_type);
                intent.putExtra("noticeId",noticeId);
                startActivity(intent);
            }
        });

        liner_forward_student.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(B_Mess_Forward_Select.this,B_Mess_Forward_Select_Student_Cla.class);
                intent.putExtra("title", title);
                intent.putExtra("content",content);
                intent.putExtra("type", type);
                intent.putExtra("image", image_url);
                intent.putExtra("acy_type", acy_type);
                intent.putExtra("noticeId",noticeId);
                startActivity(intent);
            }
        });

        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        
        if (RongIM.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTED)) {
            System.out.println("融云已连接");
        }else{
            reconnect(A_0_App.USER_QUTOKEN, B_Mess_Forward_Select.this);
        }
    }
    
    private void goQunliao(View arg0) {
        final Dialog upDateDialog = new Dialog(B_Mess_Forward_Select.this, R.style.Theme_GeneralDialog);
        upDateDialog.setContentView(R.layout.dialog_repeat);
        CircleImageView circleImageView = (CircleImageView) upDateDialog
                .findViewById(R.id.re_photo);
        TextView textView = (TextView) upDateDialog.findViewById(R.id.tv_dialog_content);
        TextView cancel = (TextView) upDateDialog.findViewById(R.id.tv_left_button);
        TextView summit = (TextView) upDateDialog.findViewById(R.id.tv_right_button);
        textView.setText("部门群聊");
        cancel.setText("取消");
        summit.setText("发送");
        circleImageView.setBackgroundDrawable(getResources()
                .getDrawable(R.drawable.icon_mess_ban_liao));
        upDateDialog.show();
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                upDateDialog.dismiss();

            }
        });
        summit.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
			@Override
            public void onClick(View arg0) {
                if (RongIM.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTED)) {
                    forward(upDateDialog, title, content, image_url, type, noticeId, "", "" + System.currentTimeMillis(), 
                            "", "", "", acy_type, "");
                }else{
                    reconnectData(A_0_App.USER_QUTOKEN, B_Mess_Forward_Select.this,upDateDialog, title, content, image_url, type, noticeId, "", "" + System.currentTimeMillis(), 
                            "", "", "", acy_type, "");
                }
            }
        });

    }
    
    void forward(final Dialog upDateDialog,final String titleStr, final String share_content, final String imgUrl, final String type,
            final String noticeId,final String notice_sendUserName, String share_time,
            final String unReadCount, final String message_level, final String placeImg,final String acy_type, final String course_user_uniqid) {
        A_0_App.getInstance().showProgreDialog(B_Mess_Forward_Select.this, "", true);
        final WYZFNoticeContent cu = new WYZFNoticeContent(titleStr,share_content,imgUrl, type,
                noticeId,notice_sendUserName, share_time,
                unReadCount, message_level, placeImg,acy_type, course_user_uniqid);

        if (RongIM.getInstance() != null && cu != null && A_0_App.USER_QUNIQID != null
                && A_0_App.USER_QUNIQID.length() > 0) {
            try {
                RongIM.getInstance().sendMessage(Conversation.ConversationType.GROUP,
                        A_0_App.USER_QUNIQID,
                        cu, null, null, new SendMessageCallback() {
                            @Override
                            public void onSuccess(Integer arg0) {
                                A_0_App.getInstance()
                                        .CancelProgreDialog(B_Mess_Forward_Select.this);
                                upDateDialog.dismiss();
                                PubMehods.showToastStr(B_Mess_Forward_Select.this, "转发成功");
                            }

                            @Override
                            public void onError(Integer arg0, ErrorCode arg1) {
                                A_0_App.getInstance()
                                        .CancelProgreDialog(B_Mess_Forward_Select.this);
                                upDateDialog.dismiss();
                                PubMehods.showToastStr(B_Mess_Forward_Select.this, "服务器开了小差,请重试！");
                                reconnect(A_0_App.USER_QUTOKEN, B_Mess_Forward_Select.this);
                            }
                        });
            } catch (Exception e) {
                A_0_App.getInstance().CancelProgreDialog(B_Mess_Forward_Select.this);
                upDateDialog.dismiss();
                PubMehods.showToastStr(B_Mess_Forward_Select.this, "转发失败，请重试");
            }
        }
    }
    
    private void reconnectData(String token, final Context context,final Dialog upDateDialog,final String titleStr, final String share_content, final String imgUrl, final String type,
            final String noticeId,final String notice_sendUserName, String share_time,
            final String unReadCount, final String message_level, final String placeImg,final String acy_type, final String course_user_uniqid) {
        A_0_App.getInstance().showProgreDialog(context, "",true);
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                A_0_App.getInstance().CancelProgreDialog(context);
                PubMehods.showToastStr(context,"抱歉，Token Incorrect");
            }

            @Override
            public void onSuccess(String s) {
                A_0_App.getInstance().CancelProgreDialog(context);
                forward(upDateDialog, title, content, image_url, type, noticeId, "", "" + System.currentTimeMillis(), 
                        "", "", "", acy_type, "");
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                A_0_App.getInstance().CancelProgreDialog(context);
                PubMehods.showToastStr(context, "抱歉，重连接服务器失败，请检查您的网络设置");
            }
        });
    }
    
    private void reconnect(String token, final Context context) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                System.out.println("抱歉，Token错误");
            }

            @Override
            public void onSuccess(String s) {}

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                System.out.println("抱歉，重连接服务器失败，请检查您的网络设置");
            }
        });
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
//                    A_0_App.getInstance().showExitDialog(B_Mess_Forward_Select.this,
//                            getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Mess_Forward_Select.this,
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
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;

            default:
                break;
        }

    }

}
