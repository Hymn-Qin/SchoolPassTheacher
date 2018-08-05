package com.yuanding.schoolteacher.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yuanding.schoolteacher.A_0_App;
import com.yuanding.schoolteacher.B_Mess_Forward_Select;
import com.yuanding.schoolteacher.B_Side_Info_1_Detail_Acy;
import com.yuanding.schoolteacher.R;

/**
 * Created by Administrator on 2017/4/17.
 */

public class PubForwardOrShare {

    private String share_url_text = "";//分享的URL
    private String share_url_title = "";//分享得标题
    private String share_url_time = "";//分享得时间
    private String share_url_pic = "";//分享得图片
    Context con;
    private String acy_detail_id;
    private String acy_type,type,content ="";

    public PubForwardOrShare(String share_url_text, String share_url_title, String share_url_time, String share_url_pic, Context con, String acy_detail_id, String acy_type, String type, String content) {
        this.share_url_text = share_url_text;
        this.share_url_title = share_url_title;
        this.share_url_time = share_url_time;
        this.share_url_pic = share_url_pic;
        this.con = con;
        this.acy_detail_id = acy_detail_id;
        this.acy_type = acy_type;
        this.type = type;
        this.content = content;
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(con, R.style.shareDialog);
        dialog.setContentView(R.layout.dialog_share_side_info);
        RelativeLayout rel_my_weixin_friend = (RelativeLayout) dialog.findViewById(R.id.rel_my_weixin_friend);
        RelativeLayout rel_my_weixin_friend_circle = (RelativeLayout) dialog.findViewById(R.id.rel_my_weixin_friend_circle);
        RelativeLayout rel_my_application_forward = (RelativeLayout) dialog.findViewById(R.id.rel_my_application_forward);
        RelativeLayout rel_share_side_cancel = (RelativeLayout) dialog.findViewById(R.id.rel_share_side_cancel);
        rel_my_weixin_friend.setVisibility(View.VISIBLE);
        rel_my_weixin_friend_circle.setVisibility(View.VISIBLE);
        rel_my_application_forward.setVisibility(View.VISIBLE);
        rel_share_side_cancel.setVisibility(View.VISIBLE);

        rel_my_weixin_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//微信好友
                dialog.dismiss();
                shareReq(1);
            }
        });
        rel_my_weixin_friend_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//微信朋友圈
                dialog.dismiss();
                shareReq(2);
            }
        });
        rel_my_application_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (A_0_App.USER_STATUS.equals("2")) {//应用内转发
                    Intent intent = new Intent(con, B_Mess_Forward_Select.class);
                    intent.putExtra("title", share_url_title);
                    intent.putExtra("content", content);
                    intent.putExtra("type", type);
                    intent.putExtra("image", share_url_pic);
                    intent.putExtra("acy_type", acy_type);
                    intent.putExtra("noticeId", acy_detail_id);
                    con.startActivity(intent);
                }else{
                    PubMehods.showToastStr(con,R.string.str_no_certified_not_use);
                }
            }
        });
        rel_share_side_cancel.setOnClickListener(new View.OnClickListener() {//取消
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    /**
     * @param sharetype 分享类型
     *                  1 微信朋友 2 微信朋友圈
     */
    private void shareReq(int sharetype) {

        //检查网络
        if (!NetUtils.isConnected(con)) {
            PubMehods.showToastStr(con, con.getResources().getString(R.string.error_title_net_error));
            return;
        }

        SHARE_MEDIA mSHARE_MEDIA = SHARE_MEDIA.WEIXIN;
        if (1 == sharetype) {
            mSHARE_MEDIA = SHARE_MEDIA.WEIXIN;
        }
        if (2 == sharetype) {
            mSHARE_MEDIA = SHARE_MEDIA.WEIXIN_CIRCLE;
        }
        if (share_url_text.isEmpty()) {
            PubMehods.showToastStr(con, con.getResources().getString(R.string.str_my_account_invite_sharedisable_prompt));
            return;
        }
        ShareUtil.sendShareReqToWeiXin(share_url_title, content, share_url_text,share_url_pic,mSHARE_MEDIA, (Activity) con, new UMShareListener() {

            @Override
            public void onResult(SHARE_MEDIA arg0) {
//				Log.i(TAG, "=------onResult arg0.name():"+arg0.name());
            }

            @Override
            public void onError(SHARE_MEDIA arg0, Throwable arg1) {
//				Log.i(TAG, "=------onError arg0.name():"+arg0.name());
            }

            @Override
            public void onCancel(SHARE_MEDIA arg0) {
//				Log.i(TAG, "=------onCancel arg0.name():"+arg0.name());
            }
        });

    }
}
