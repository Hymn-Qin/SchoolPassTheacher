package com.yuanding.schoolteacher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.Cpk_School_Assistant_detail;
import com.yuanding.schoolteacher.bean.Cpk_User_Values;
import com.yuanding.schoolteacher.service.Api.AppNotice_InviteInstallAppCallBack;
import com.yuanding.schoolteacher.service.Api.InterNoticeSchoolAssistantDetail;
import com.yuanding.schoolteacher.service.Api.InterRefuseTipsList;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.ACache;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.MyListView;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016年12月6日 上午11:05:11
 * 审核详情页面
 */
public class B_Mess_School_Assistant_3_Detai_Examine_Acy  extends A_0_CpkBaseTitle_Navi {
    
    private View mess_notice_detail_load_error, liner_notice_detail_whole,
            mess_notice_detail_loading;
    private TextView tv_message_detail_title, tv_message_detail_notice_time, tv_notice_detail;
    private LinearLayout liner_examine_to_action;
    private Button btn_examine_detail_refush,btn_examine_detail_adopt,btn_examine_detail_have_result;
    private String message_id;
    private ImageOptions bitmapUtils;
    private int acy_type;// 页面类型 推送1，正常列表进入2
    private TextView tv_mess_detail_from;
    private ACache maACache;
    private JSONObject jsonObject;
    private Cpk_School_Assistant_detail detail_Notice;

    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;

    private MyAdapter myAdapter;
    private boolean havaSuccessLoadData = false;
    private List<String> mTipsList;
    private MyListView mListView;
    
    private Report_Adapter report_Adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_mess_notice_detail_examine);

        showTitleBt(ZUI_RIGHT_BUTTON, true);
        setTitleText("详情");
        detail_Notice = new Cpk_School_Assistant_detail();
        acy_type = getIntent().getExtras().getInt("acy_type");
        if (acy_type == 2) {
            // 列表进入
            message_id = getIntent().getExtras().getString("message_id");
        } else {
            // 推送进入
            message_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
        }
        
        mTipsList = new ArrayList<String>();
        myAdapter = new MyAdapter();
        mListView = (MyListView)findViewById(R.id.lv_examine_detail);
        mess_notice_detail_load_error = findViewById(R.id.mess_examine_detail_load_error);
        liner_notice_detail_whole = findViewById(R.id.liner_examine_detail_whole);
        mess_notice_detail_loading = findViewById(R.id.mess_examine_detail_loading);

        home_load_loading = (LinearLayout) mess_notice_detail_loading
                .findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        liner_examine_to_action = (LinearLayout)findViewById(R.id.liner_examine_to_action);
        btn_examine_detail_adopt = (Button)findViewById(R.id.btn_examine_detail_adopt);
        btn_examine_detail_refush = (Button)findViewById(R.id.btn_examine_detail_refush);
        btn_examine_detail_have_result = (Button)findViewById(R.id.btn_examine_detail_have_result);
        
        tv_message_detail_title = (TextView) findViewById(R.id.tv_examine_detail_title);
        tv_message_detail_notice_time = (TextView) findViewById(R.id.tv_examine_detail_time_sys);
        tv_mess_detail_from = (TextView) findViewById(R.id.tv_examine_detail_from);
        tv_notice_detail = (TextView) findViewById(R.id.tv_examine_detail_content);

        bitmapUtils = A_0_App.getBitmapUtils(this, R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg,false);
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }

        mess_notice_detail_load_error.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoadResult(true, false, false);
                readData(message_id);
            }
        });

        btn_examine_detail_adopt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                    to_Examine_Action("1", detail_Notice.getMessage_id(), A_0_App.USER_TOKEN, "");
                }
            }
        });
        
        btn_examine_detail_refush.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (mTipsList != null && mTipsList.size() > 0) {
                    report_Dialog();
                } else {
                    getRefushList(false);
                }
            }
        });
        mListView.setAdapter(myAdapter);
        readCache(message_id);
    }

    private void readCache(String message_id) {
        // TODO Auto-generated method stub
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.TAG_EXAMINE_NOTICE_DETAIL+AppStrStatic.cache_key_schoolassistant_detail_text
                + A_0_App.USER_UNIQID + message_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        }else{
            updateInfo();
        }
    }

    private void showInfo(JSONObject jsonObject) {
        Cpk_School_Assistant_detail notice;
        try {
            JSONObject dd = jsonObject.getJSONObject("info");
            notice = new Cpk_School_Assistant_detail();
            notice = JSON.parseObject(dd + "", Cpk_School_Assistant_detail.class);
            List<Cpk_User_Values> list_values = new ArrayList<Cpk_User_Values>();
            list_values = JSON.parseArray(jsonObject.optJSONArray("userInfo") + "", Cpk_User_Values.class);
            notice.setUser_values(list_values);
            detail_Notice = notice;
            if (detail_Notice != null)
                successResult(notice);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /**
     * 多张图片展示
     */

    private void showLoadResult(boolean loading, boolean whole, boolean loadFaile) {
        if (whole)
            liner_notice_detail_whole.setVisibility(View.VISIBLE);
        else
            liner_notice_detail_whole.setVisibility(View.GONE);

        if (loadFaile)
            mess_notice_detail_load_error.setVisibility(View.VISIBLE);
        else
            mess_notice_detail_load_error.setVisibility(View.GONE);
        if (loading) {
            drawable.start();
            mess_notice_detail_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            mess_notice_detail_loading.setVisibility(View.GONE);
        }
    }
    
    private void successResult(Cpk_School_Assistant_detail notice) {
        havaSuccessLoadData = true;   
        if(notice.getBtn_enable() != null && notice.getBtn_enable().equals("1")){//是否显示操作按钮，0：不显示，1：显示
            liner_examine_to_action.setVisibility(View.VISIBLE);
            btn_examine_detail_have_result.setVisibility(View.GONE);
        }else{
            liner_examine_to_action.setVisibility(View.GONE);
            btn_examine_detail_have_result.setVisibility(View.VISIBLE);
            if(notice.getOper_type() != null && notice.getOper_type().equals("1")){//审核状态，1：通过，2：拒绝
                btn_examine_detail_have_result.setText("已通过");
            }else{
                btn_examine_detail_have_result.setText("已拒绝");
            }
        }
        
        if (notice.getTitle() != null && notice.getTitle() != null && notice.getTitle().length() > 0)
            tv_message_detail_title.setText(notice.getTitle());

        if (notice.getCreate_time() != null && notice.getCreate_time() != null && notice.getCreate_time().length() > 0)
            tv_message_detail_notice_time.setText(PubMehods.getFormatDate(Long.valueOf(notice.getCreate_time()), "MM/dd HH:mm"));
        tv_mess_detail_from.setText(R.string.str_school_assistant);

        String content = notice.getContent();
        if (null != content && content.length() > 0) {
            SpannableStringBuilder builder = PubMehods.splitStrWhereStr(B_Mess_School_Assistant_3_Detai_Examine_Acy.this, R.color.main_color, content, "\\{#", "#\\}");
            if(builder != null){
                tv_notice_detail.setText(builder); 
            }
        }
        myAdapter.notifyDataSetChanged();
        showLoadResult(false, true, false);
    }

    private void readData(String message_id) {
        
        getRefushList(true);
        A_0_App.getApi().getNoticeSchoolAssistantDetail(B_Mess_School_Assistant_3_Detai_Examine_Acy.this,AppStrStatic.TAG_EXAMINE_NOTICE_DETAIL,
                A_0_App.USER_TOKEN, message_id, new InterNoticeSchoolAssistantDetail() {

                    @Override
                    public void onSuccess(Cpk_School_Assistant_detail notice) {
                        if (isFinishing())
                            return;
                        detail_Notice = notice;
                        if (detail_Notice != null)
                            successResult(detail_Notice);
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
                        if(!havaSuccessLoadData)
                            showLoadResult(false, false, true);
                        PubMehods.showToastStr(B_Mess_School_Assistant_3_Detai_Examine_Acy.this, msg);
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

    }
    
    private void getRefushList(final boolean haveLoadData) {
        if(!haveLoadData){
            A_0_App.getInstance().showProgreDialog(B_Mess_School_Assistant_3_Detai_Examine_Acy.this,"",true);
        }
      //获取错误提示列表信息
        A_0_App.getApi().getRefuseTipsList(B_Mess_School_Assistant_3_Detai_Examine_Acy.this, A_0_App.USER_TOKEN, new InterRefuseTipsList() {
            
            @Override
            public void onSuccess(List<String> mList) {
                mTipsList = mList;
                if(!haveLoadData){
                    A_0_App.getInstance().CancelProgreDialog(B_Mess_School_Assistant_3_Detai_Examine_Acy.this);
                    report_Dialog();
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
                if(!haveLoadData){
                    A_0_App.getInstance().CancelProgreDialog(B_Mess_School_Assistant_3_Detai_Examine_Acy.this);
                    PubMehods.showToastStr(B_Mess_School_Assistant_3_Detai_Examine_Acy.this, msg);
                }
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    //审核操作 type:1通过，2拒绝
    private void to_Examine_Action(final String type,String messageId, String token,String remark) {
        A_0_App.getInstance().showProgreDialog(B_Mess_School_Assistant_3_Detai_Examine_Acy.this, "", true);
        A_0_App.getApi().user_Examine_Action(type, messageId, token, remark, new AppNotice_InviteInstallAppCallBack() {
            @Override
            public void onSuccess(String message) {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Mess_School_Assistant_3_Detai_Examine_Acy.this);
                liner_examine_to_action.setVisibility(View.GONE);
                btn_examine_detail_have_result.setVisibility(View.VISIBLE);
                if(type.equals("1")){//审核状态，1：通过，2：拒绝
                    btn_examine_detail_have_result.setText("已通过");
                }else{
                    btn_examine_detail_have_result.setText("已拒绝");
                }
                if (message != null && !("").equals(message))
                    PubMehods.showToastStr(B_Mess_School_Assistant_3_Detai_Examine_Acy.this, message);
                else
                    PubMehods.showToastStr(B_Mess_School_Assistant_3_Detai_Examine_Acy.this, "操作成功");
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
                A_0_App.getInstance().CancelProgreDialog(B_Mess_School_Assistant_3_Detai_Examine_Acy.this);
                PubMehods.showToastStr(B_Mess_School_Assistant_3_Detai_Examine_Acy.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (detail_Notice.getUser_values() != null)
                return detail_Notice.getUser_values().size();
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
            if (converView == null) {
                converView = LayoutInflater.from(B_Mess_School_Assistant_3_Detai_Examine_Acy.this)
                        .inflate(R.layout.item_examine_detail, null);
            }

            TextView title = (TextView) converView.findViewById(R.id.tv_item_examine_detail_title);
            TextView content = (TextView) converView.findViewById(R.id.tv_item_examine_detail_content);
            if(detail_Notice.getUser_values() != null){
                title.setText(detail_Notice.getUser_values().get(posi).getName());
                content.setText(detail_Notice.getUser_values().get(posi).getValue());
            }
            return converView;
        }
    }
    
 // 加载列表数据
    public class Report_Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mTipsList != null)
                return mTipsList.size();
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
                converView = LayoutInflater.from(B_Mess_School_Assistant_3_Detai_Examine_Acy.this).inflate(R.layout.item_pub_text, null);
            }
            TextView tv_acy_name = (TextView) converView
                    .findViewById(R.id.tv_item_pub_text);
            tv_acy_name.setText(mTipsList.get(posi));
            
            return converView;
        }

    }
    
    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                goAcy();
                break;
            default:
                break;
        }

    }

    private void goData() {
        Intent it = new Intent();
        setResult(1, it);
    }

    private void goAcy() {
        if (acy_type != 2) {// 推送
            if (A_Main_Acy.getInstance() != null) {
                finish();
            } else {
                startAcy(B_Mess_School_Assistant_3_Detai_Examine_Acy.this, A_Main_Acy.class);
            }
        } else {// 正常进入
            goData();
            finish();
            overridePendingTransition(R.anim.animal_push_right_in_normal,
                    R.anim.animal_push_right_out_normal);
        }
    }

    private void startAcy(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.animal_push_left_in_normal,
                R.anim.animal_push_left_out_normal);
        finish();
    }
    
    /**
     * 拒绝原因列表
     */
    void report_Dialog() {
        final Dialog dialog = new Dialog(B_Mess_School_Assistant_3_Detai_Examine_Acy.this,
                android.R.style.Theme_Translucent_NoTitleBar);
        View view = LayoutInflater.from(B_Mess_School_Assistant_3_Detai_Examine_Acy.this).
                inflate(R.layout.activity_side_select_report_type, null);
        dialog.setContentView(view);
        TextView textview = (TextView)view.findViewById(R.id.dialog_title_version_name);
        textview.setText(R.string.str_refush_dialog_title);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
        report_Adapter=new Report_Adapter();
        ListView listView = (ListView) dialog.findViewById(R.id.lv_side_select_sign);
        listView.setAdapter(report_Adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
                if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                    to_Examine_Action("2", detail_Notice.getMessage_id(), A_0_App.USER_TOKEN, mTipsList.get(arg2));
                }
                dialog.dismiss();
            }
        });
    }
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    goAcy();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
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
                    // A_0_App.getInstance().showExitDialog(B_Mess_Notice_Official_News_1.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(
                                    B_Mess_School_Assistant_3_Detai_Examine_Acy.this,
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

            readData(message_id);
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // bitmapUtils=null;
        drawable.stop();
        drawable = null;
        super.onDestroy();
        System.gc();

    }
}
