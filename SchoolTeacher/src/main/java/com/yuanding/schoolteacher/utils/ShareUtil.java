package com.yuanding.schoolteacher.utils;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.BitmapFactory;

import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;
import com.yuanding.schoolteacher.R;


public class ShareUtil {

	static String TAG = "ShareUtil";
	/**
	 * 
	 * @param shareContentText 分享的文本内容		
	 * @param targetUrl  分享的链接
	 * @param sharemedia  分享到的平台
	 * @param mContext 
	 * @param mUMShareListener  分享的回调
	 */
	public static void sendShareReqToWeiXin(String sharetitleText, String shareContentText, String targetUrl,
			String shareIconUrl, SHARE_MEDIA sharemedia, final Activity mContext, UMShareListener mUMShareListener) {

		UMImage image;
		if (shareIconUrl == null || shareIconUrl.length() <= 0) {
			image = new UMImage(mContext, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.img_logo_512z));
		} else {
			image = new UMImage(mContext,shareIconUrl);
		}

		//调起微信之前的进度条
		ProgressDialog dialog =  new ProgressDialog(mContext);		
		dialog.setMessage("   请稍候…       ");
        Config.dialog = dialog;        
        
		new ShareAction(mContext)
		.setPlatform(sharemedia)
		.setCallback(mUMShareListener)
		.withText(shareContentText)
		.withTitle(sharetitleText)
		.withTargetUrl(targetUrl)
		.withMedia(image)		
		.share();
	}
	
	/**
	 * 判断是否安装目标分享平台
	 * @param sharemedia
	 * @return
	 */
	public static  boolean isInstallTargetMedia(final Activity mContext,SHARE_MEDIA sharemedia) {
		Config.IsToastTip = false;
		UMShareAPI mShareAPI = UMShareAPI.get(mContext);
		if(!mShareAPI.isInstall(mContext, sharemedia)){
			Log.i(TAG, sharemedia.name()+",");
			return false;
		}else{
			return true;
		}		

	}
	
}
