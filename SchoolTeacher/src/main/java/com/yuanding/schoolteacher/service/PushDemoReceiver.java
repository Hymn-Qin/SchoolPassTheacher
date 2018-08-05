package com.yuanding.schoolteacher.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.yuanding.schoolteacher.A_0_App;
import com.yuanding.schoolteacher.A_1_Start_Acy;
import com.yuanding.schoolteacher.A_Main_My_Message_Acy;
import com.yuanding.schoolteacher.B_Mess_Notice_Detail_MessText_Teacher;
import com.yuanding.schoolteacher.B_Mess_Notice_Detail_Official_News;
import com.yuanding.schoolteacher.B_Mess_Notice_Detail_Sys_Teacher;
import com.yuanding.schoolteacher.B_Mess_Notice_Detail_Teacher;
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

public class PushDemoReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
//    public static StringBuilder payloadData = new StringBuilder();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

//                String taskid = bundle.getString("taskid");
//                String messageid = bundle.getString("messageid");
//
//                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
//                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
//                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);
                    if (!TextUtils.isEmpty(data)) {
                        JSONObject customJson = null;
                        try {
                            customJson = new JSONObject(data);
                            String myType,myId = null;
                            System.out.println(customJson +"");
                            if (!customJson.isNull("id")) {
                                myId = customJson.getString("id");
                                //保存消息id 详情页面取id
                                PubMehods.saveSharePreferStr(context, "mCurrentClickNotificationMsgId", myId);
                            }
                            if (!customJson.isNull("type")) {
                                myType = customJson.getString("type");
                                A_0_App.getInstance().setPushNoticeType(myType);
                            }
                            if (!customJson.isNull("sub_type")) {
                                myType = customJson.getString("sub_type");
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
                            e.printStackTrace();
                        }
                    }   
                    
                    updateContent(context,"");
//                    Log.d("GetuiSdkDemo", "receiver payload : " + data);
//
//                    payloadData.append(data);
//                    payloadData.append("\n");
//
//                    if (GetuiSdkDemoActivity.tLogView != null) {
//                        GetuiSdkDemoActivity.tLogView.append(data + "\n");
//                    }
                }
                break;

            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                Log.e("BaiduPushMessageReceiver", "GeTui——ClientID==" + cid);
//                if (GetuiSdkDemoActivity.tView != null) {
//                    GetuiSdkDemoActivity.tView.setText(cid);
//                }
                A_0_App.getInstance().setClientid(cid);
                break;
            case PushConsts.GET_SDKONLINESTATE:
                boolean online = bundle.getBoolean("onlineState");
                Log.d("GetuiSdkDemo", "online = " + online);
                break;

            case PushConsts.SET_TAG_RESULT:
                String sn = bundle.getString("sn");
                String code = bundle.getString("code");

                String text = "设置标签失败, 未知异常";
                switch (Integer.valueOf(code)) {
                    case PushConsts.SETTAG_SUCCESS:
                        text = "设置标签成功";
                        break;

                    case PushConsts.SETTAG_ERROR_COUNT:
                        text = "设置标签失败, tag数量过大, 最大不能超过200个";
                        break;

                    case PushConsts.SETTAG_ERROR_FREQUENCY:
                        text = "设置标签失败, 频率过快, 两次间隔应大于1s";
                        break;

                    case PushConsts.SETTAG_ERROR_REPEAT:
                        text = "设置标签失败, 标签重复";
                        break;

                    case PushConsts.SETTAG_ERROR_UNBIND:
                        text = "设置标签失败, 服务未初始化成功";
                        break;

                    case PushConsts.SETTAG_ERROR_EXCEPTION:
                        text = "设置标签失败, 未知异常";
                        break;

                    case PushConsts.SETTAG_ERROR_NULL:
                        text = "设置标签失败, tag 为空";
                        break;

                    case PushConsts.SETTAG_NOTONLINE:
                        text = "还未登陆成功";
                        break;

                    case PushConsts.SETTAG_IN_BLACKLIST:
                        text = "该应用已经在黑名单中,请联系售后支持!";
                        break;

                    case PushConsts.SETTAG_NUM_EXCEED:
                        text = "已存 tag 超过限制";
                        break;

                    default:
                        break;
                }

                Log.d("GetuiSdkDemo", "settag result sn = " + sn + ", code = " + code);
                Log.d("GetuiSdkDemo", "settag result sn = " + text);
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 *
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }
    
    /*
     * 支持推送的功能：消息模块（3种类型）、活动、讲座、考勤、资讯、首页官方通知、审核通知
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
            } else if(A_0_App.getInstance().getPushNoticeType().equals("course")){
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
}
