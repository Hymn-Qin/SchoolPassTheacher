package com.yuanding.schoolteacher.utils;

import android.content.Context;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年1月13日 下午5:54:19
 * 类说明
 */
public class DensityUtils {
	 /** 
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素) 
     */  
	public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
	
	
	/** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  

}
