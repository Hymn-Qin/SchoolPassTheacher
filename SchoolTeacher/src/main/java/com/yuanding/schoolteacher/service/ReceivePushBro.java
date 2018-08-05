
package com.yuanding.schoolteacher.service;

import com.yuanding.schoolteacher.A_Main_My_Message_Acy;
import com.yuanding.schoolteacher.utils.AppStrStatic;
import com.yuanding.schoolteacher.utils.PubMehods;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
 * (非 Javadoc)
 * 处理所有的接收，局部的和全局的
 * 处理接收到的广播
 */

public class ReceivePushBro extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (null != intent.getAction()) {
            if (intent.getAction().equals(PubMehods.getAppPackageName(context))) {
                if (A_Main_My_Message_Acy.getInstance() != null && !A_Main_My_Message_Acy.getInstance().isFinishing())
                    A_Main_My_Message_Acy.getInstance().refreshMessageList();
            }
        }
    }
}
