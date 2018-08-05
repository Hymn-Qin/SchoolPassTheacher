package com.yuanding.schoolteacher.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.yuanding.schoolteacher.A_0_App;
import com.yuanding.schoolteacher.A_1_Start_Acy;
import com.yuanding.schoolteacher.A_3_0_Login_Acy_Teacher;
import com.yuanding.schoolteacher.A_Main_Acy;
import com.yuanding.schoolteacher.A_Main_My_Message_Acy;
import com.yuanding.schoolteacher.B_Mess_Notice_Detail_Official_News;
import com.yuanding.schoolteacher.B_Mess_Notice_Detail_Teacher;
import com.yuanding.schoolteacher.B_Mess_Notice_Detail_MessText_Teacher;
import com.yuanding.schoolteacher.B_Mess_Notice_Detail_Sys_Teacher;
import com.yuanding.schoolteacher.B_Mess_School_Assistant_0_List_Acy;
import com.yuanding.schoolteacher.B_Mess_School_Assistant_1_Detai_Acy;
import com.yuanding.schoolteacher.B_Mess_School_Assistant_3_Detai_Examine_Acy;
import com.yuanding.schoolteacher.B_Side_Acy_list_Detail_Acy_Teacher;
import com.yuanding.schoolteacher.B_Side_Course_Acy;
import com.yuanding.schoolteacher.B_Side_Info_1_Detail_Acy;
import com.yuanding.schoolteacher.B_Side_Lectures_Detail_Acy_Teacher;
import com.yuanding.schoolteacher.B_Side_Notice_Main_Detail_0;
import com.yuanding.schoolteacher.Pub_WebView_Banner_Acy;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.pulltorefresh.Utils;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月5日 上午10:04:23
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 *onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 *onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 */

public class BaiduPushMessageReceiver  extends PushMessageReceiver {

    /**
     * delTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
     *            成功删除的tag
     * @param failTags
     *            删除失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onDelTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onDelTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags
                + " requestId=" + requestId;
        logD(responseString);
    }

    /**
     * listTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示列举tag成功；非0表示失败。
     * @param tags
     *            当前应用设置的所有tag。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
            String requestId) {
        String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;
        logD(responseString);
    }

    /**
     * 接收透传消息的函数。
     *
     * @param context
     *            上下文
     * @param message
     *            推送的消息
     * @param customContentString
     *            自定义内容,为空或者json字符串
     */
    @Override
    public void onMessage(Context context, String message,
            String customContentString) {
        String messageString = "透传消息 onMessage=\"" + message
                + "\" customContentString=" + customContentString;
        logD(messageString);
        
    }

    /**
     * setTags() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
     *            设置成功的tag
     * @param failTags
     *            设置失败的tag
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onSetTags(Context context, int errorCode,
            List<String> sucessTags, List<String> failTags, String requestId) {
        String responseString = "onSetTags errorCode=" + errorCode
                + " sucessTags=" + sucessTags + " failTags=" + failTags + " requestId=" + requestId;
        logD(responseString);
    }

    /**
     * PushManager.stopWork() 的回调函数。
     *
     * @param context
     *            上下文
     * @param errorCode
     *            错误码。0表示从云推送解绑定成功；非0表示失败。
     * @param requestId
     *            分配给对云推送的请求的id
     */
    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        logD(responseString);
        if (errorCode == 0) {
            logD("解绑成功");
        }
    }

    /**
     * 调用PushManager.startWork后，sdk将对push
     * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
     * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
     *
     * @param context
     *            BroadcastReceiver的执行Context
     * @param errorCode
     *            绑定接口返回值，0 - 成功
     * @param appid
     *            应用id。errorCode非0时为null
     * @param userId
     *            应用user id。errorCode非0时为null
     * @param channelId
     *            应用channel id。errorCode非0时为null
     * @param requestId
     *            向服务端发起的请求id。在追查问题时有用；
     * @return noneerror_code	描述
		0	绑定成功
		10001	当前网络不可用，请检查网络
		10002	服务不可用，连接server失败
		10003	服务不可用，503错误
		10101	应用集成方式错误，请检查各项声明和权限
		20001	未知错误
		30600	服务内部错误
		30601	非法函数请求，请检查您的请求内容
		30602	请求参数错误，请检查您的参数
		30603	非法构造请求，服务端验证失败
		30605	请求的数据在服务端不存在
		30608	绑定关系不存在或未找到
		30609	一个百度账户绑定设备超出个数限制(多台设备登录同一个百度账户)
		30612	百度账户绑定应用时被禁止，需要白名单授权
     */
    @Override
    public void onBind(Context context, int errorCode, String appid,
            String userId, String channelId, String requestId) {
    	
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        logE(responseString);
        if (errorCode == 0) {
            // 绑定成功
        	A_0_App.getInstance().setChannelId(channelId);
        	logD("教师——" + "绑定成功");
        	Log.e("test", "教师——" + "绑定成功");
        }else{
            logD("教师——" +  "绑定失败");
            Log.e("test", "教师——" +  "绑定失败");
        }
    }
    
    /**
     * 接收通知点击的函数。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */
    @Override
    public void onNotificationClicked(Context context, String title,
            String description, String customContentString) {
        String notifyString = "通知点击 title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        logE("BaiduPushMessageReceiver" + notifyString);

        /**
         *  自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
         *  服务器目前返回3个主字段，id、type、sub_type（例：消息分类下的系统、图文、短消息）
         */
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myType,myId = null;
                if (!customJson.isNull("id")) {
                	myId = customJson.optString("id");
//                    A_0_App.getInstance().setPushNoticeId(myId);
                	//保存消息id 详情页面取id
                    PubMehods.saveSharePreferStr(context, "mCurrentClickNotificationMsgId", myId);
                }
                if (!customJson.isNull("type")) {
                	myType = customJson.optString("type");
                    A_0_App.getInstance().setPushNoticeType(myType);
                }
                if (!customJson.isNull("sub_type")) {
                    myType = customJson.optString("sub_type");
                    A_0_App.getInstance().setPushNoticeSubType(myType);
                }
                if (!customJson.isNull("link_id")) {
                    String link_id = customJson.optString("link_id");
                    A_0_App.getInstance().setLink_id(link_id);
                }
                if (!customJson.isNull("jump_module")) {
                    String jump_module = customJson.optString("jump_module");
                    A_0_App.getInstance().setJump_module(jump_module);
                }
                if (!customJson.isNull("message_type")) {
                    String message_type = customJson.optString("message_type");
                    A_0_App.getInstance().setMessage_type(message_type);
                }
                if (!customJson.isNull("nav_name")) {
                    String title_name = customJson.optString("nav_name");
                    A_0_App.getInstance().setTitle_name(title_name);
                }
                if (!customJson.isNull("leave_detail_url")) {
                    String leave_detail_url = customJson.optString("leave_detail_url");
                    A_0_App.getInstance().setLeave_detail_url(leave_detail_url);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        updateContent(context, notifyString);
    }

    /**
     * 接收通知到达的函数。
     *
     * @param context
     *            上下文
     * @param title
     *            推送的通知的标题
     * @param description
     *            推送的通知的描述
     * @param customContentString
     *            自定义内容，为空或者json字符串
     */

    @Override
    public void onNotificationArrived(Context context, String title,
            String description, String customContentString) {

        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        A_0_App.getInstance().schoolStartPush(context, PubMehods.getAppPackageName(context));
    }

    /*
     * 支持推送的功能：消息模块（3种类型）、活动、讲座、考勤、资讯、首页官方通知
     */
    private void updateContent(Context context, String content) {
    	A_Main_My_Message_Acy.logD(content);
        Intent intent = new Intent();  
         	
    	//登录状态
    	if (null != A_0_App.USER_TOKEN && !"".equals(A_0_App.USER_TOKEN) && A_0_App.getInstance().getPushNoticeType() != null) {
       	    
    		/**
    		 *  如果已经登录 并且被杀掉进程 则跳转到闪屏页 由闪屏页决定是否跳转到首页、详情页（已经登录） 或者登录页（未登录）     	
    		 */  
        	if(!A_0_App.isAfterScreenPage()){
        	    updateContentAction(context, content, intent);
				return;
        	}
        	
    		/**
    		 * 如果已经登录  进程没被杀掉 直接跳转到推送详情页	
    		 */
        	if (A_0_App.getInstance().getJump_module() != null && A_0_App.getInstance().getJump_module().equals("INDEX")) {

                if (A_0_App.getInstance().getMessage_type().equals("1")) {
                    intent.setClass(context.getApplicationContext(), B_Mess_Notice_Detail_Teacher.class);
                } else if (A_0_App.getInstance().getMessage_type().equals("4")) {
                    intent.setClass(context.getApplicationContext(),
                            B_Mess_Notice_Detail_MessText_Teacher.class);
                } else {
                    intent.setClass(context.getApplicationContext(), B_Mess_Notice_Detail_Sys_Teacher.class);
                }
                intent.putExtra("acy_type", 4);
                intent.putExtra("message_id", A_0_App.getInstance().getLink_id());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(intent);
            } else if (A_0_App.getInstance().getJump_module() != null && A_0_App.getInstance().getJump_module().equals("SIDE")) {
                A_0_App.setClickPushMsgReqPage("s1");
            } else {
                if(A_0_App.getInstance().getPushNoticeType().equals("message")){               
                    if (A_0_App.getInstance().getPushNoticeSubType().equals("1")) {
                        intent.setClass(context.getApplicationContext(), B_Mess_Notice_Detail_Teacher.class);
                    } else if (A_0_App.getInstance().getPushNoticeSubType().equals("4")) {             	
                        intent.setClass(context.getApplicationContext(), B_Mess_Notice_Detail_MessText_Teacher.class);                   
                    }else{
                        intent.setClass(context.getApplicationContext(), B_Mess_Notice_Detail_Sys_Teacher.class);
                    }
                    intent.putExtra("acy_type", 3);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                } else if(A_0_App.getInstance().getPushNoticeType().equals("activity")){
                    intent.setClass(context.getApplicationContext(), B_Side_Acy_list_Detail_Acy_Teacher.class);
                    intent.putExtra("acy_type", 3);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }else if(A_0_App.getInstance().getPushNoticeType().equals("lecture")){
                    intent.setClass(context.getApplicationContext(), B_Side_Lectures_Detail_Acy_Teacher.class);
                    intent.putExtra("acy_type", 3);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }else if(A_0_App.getInstance().getPushNoticeType().equals("info")){
                    intent.setClass(context.getApplicationContext(), B_Side_Info_1_Detail_Acy.class);
                    intent.putExtra("acy_type", 1);//推送进入
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }else if(A_0_App.getInstance().getPushNoticeType().equals("official")){
                    intent.setClass(context.getApplicationContext(), B_Mess_Notice_Detail_Official_News.class);
                    intent.putExtra("acy_type", 1);//推送进入
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }else if(A_0_App.getInstance().getPushNoticeType().equals("assistant")){
                    intent.setClass(context.getApplicationContext(), B_Mess_School_Assistant_1_Detai_Acy.class);
                    intent.putExtra("acy_type", 1);//推送进入
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }else if(A_0_App.getInstance().getPushNoticeType().equals("audit")){
                    intent.setClass(context.getApplicationContext(), B_Mess_School_Assistant_3_Detai_Examine_Acy.class);
                    intent.putExtra("acy_type", 1);//推送进入
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }else if(A_0_App.getInstance().getPushNoticeType().equals("course")){
                    intent.setClass(context.getApplicationContext(), B_Side_Course_Acy.class);
                    intent.putExtra("acy_type", 1);//推送进入
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }else if(A_0_App.getInstance().getPushNoticeType().equals("sms")){
                    intent.setClass(context.getApplicationContext(), B_Mess_School_Assistant_0_List_Acy.class);
                    intent.putExtra("acy_type", 1);//推送进入
                    intent.putExtra("type","5");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }else if (A_0_App.getInstance().getPushNoticeType().equals("leave")) {
                    intent.setClass(context.getApplicationContext(), Pub_WebView_Banner_Acy.class);
                    intent.putExtra("url_text", A_0_App.getInstance().getLeave_detail_url() + A_0_App.USER_TOKEN);
                    intent.putExtra("title_text", "教师请销假");// 正常列表进入
                    intent.putExtra("tag_show_refresh_btn", "1");
                    intent.putExtra("tag_skip", "1");
                    intent.putExtra("acy_type", 4);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }
            }
        } else {//未登录
            updateContentAction(context, content, intent);
        }
    }
    
    private void updateContentAction(Context context, String content,Intent intent) {
        if (A_0_App.getInstance().getJump_module() != null && A_0_App.getInstance().getJump_module().equals("INDEX")) {
            if (A_0_App.getInstance().getMessage_type().equals("1")) {
                A_0_App.setClickPushMsgReqPage("i1");
            } else if (A_0_App.getInstance().getMessage_type().equals("4")) {
                A_0_App.setClickPushMsgReqPage("i2");
            } else {
                A_0_App.setClickPushMsgReqPage("i3");
            }
        } else if (A_0_App.getInstance().getJump_module() != null && A_0_App.getInstance().getJump_module().equals("SIDE")) {
            intent.setClass(context.getApplicationContext(),B_Side_Notice_Main_Detail_0.class);
            intent.putExtra("acy_type", 4);// 正常列表进入
            intent.putExtra("notice_type",AppStrStatic.cache_key_notice_sent+A_0_App.USER_UNIQID);
            intent.putExtra("acy_detail_id", A_0_App.getInstance().getLink_id());
            intent.putExtra("isHaveSend", 1+""); //是否是已发送页面
            intent.putExtra("type", A_0_App.getInstance().getMessage_type());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        } else {
            if(A_0_App.getInstance().getPushNoticeType().equals("message")){             
                if (A_0_App.getInstance().getPushNoticeSubType().equals("1")) {
                    A_0_App.setClickPushMsgReqPage("m1");
                } else if (A_0_App.getInstance().getPushNoticeSubType().equals("4")) {              
                    A_0_App.setClickPushMsgReqPage("m2");
                }else{
                    A_0_App.setClickPushMsgReqPage("m3");
                }
            }else if(A_0_App.getInstance().getPushNoticeType().equals("activity")){
                A_0_App.setClickPushMsgReqPage("ac1");
            }else if(A_0_App.getInstance().getPushNoticeType().equals("lecture")){
                A_0_App.setClickPushMsgReqPage("l1");
            }else if(A_0_App.getInstance().getPushNoticeType().equals("attendance")){
                A_0_App.setClickPushMsgReqPage("at1");
            }else if(A_0_App.getInstance().getPushNoticeType().equals("info")){
                A_0_App.setClickPushMsgReqPage("in1");
            }else if(A_0_App.getInstance().getPushNoticeType().equals("official")){
                A_0_App.setClickPushMsgReqPage("of1");
            }else if(A_0_App.getInstance().getPushNoticeType().equals("assistant")){
                A_0_App.setClickPushMsgReqPage("as1");
            }else if(A_0_App.getInstance().getPushNoticeType().equals("audit")){
                A_0_App.setClickPushMsgReqPage("au1");
            }else if(A_0_App.getInstance().getPushNoticeType().equals("course")){
                A_0_App.setClickPushMsgReqPage("sc1");
            }else if(A_0_App.getInstance().getPushNoticeType().equals("sms")){
                A_0_App.setClickPushMsgReqPage("tm1");
            }else if (A_0_App.getInstance().getPushNoticeType().equals("leave")) {
                A_0_App.setClickPushMsgReqPage("leave1");
            }
        }
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setClass(context.getApplicationContext(), A_1_Start_Acy.class);
      context.getApplicationContext().startActivity(intent);
    }
    
    public static void logD(String msg) {
        com.yuanding.schoolteacher.utils.LogUtils.logD("BaiduPushMessageReceiver", "BaiduPushMessageReceiver==>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolteacher.utils.LogUtils.logE("BaiduPushMessageReceiver", "BaiduPushMessageReceiver==>" + msg);
    }
}
