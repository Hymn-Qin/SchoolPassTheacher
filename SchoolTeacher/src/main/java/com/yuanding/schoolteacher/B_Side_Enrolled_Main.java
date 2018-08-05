package com.yuanding.schoolteacher;


import android.os.Bundle;
import android.view.View;

import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;

/**
 * 迎新主页面
 */
public class B_Side_Enrolled_Main extends A_0_CpkBaseTitle_Navi {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_enrolled_main);
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {

    }
}
