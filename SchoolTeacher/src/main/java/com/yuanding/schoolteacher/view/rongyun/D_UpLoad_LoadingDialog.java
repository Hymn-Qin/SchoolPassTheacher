package com.yuanding.schoolteacher.view.rongyun;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.yuanding.schoolteacher.R;


/**
 *
 * Created by bob on 2015/1/28.
 */
public class D_UpLoad_LoadingDialog extends Dialog{

    private TextView mTextView;

    public D_UpLoad_LoadingDialog(Context context) {

        super(context, R.style.WinDialog);
        setContentView(R.layout.upload_de_ui_dialog_loading);
        mTextView = (TextView) findViewById(android.R.id.message);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
    public void setText(String s) {
        if (mTextView != null) {
            mTextView.setText(s);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    public void setText(int res) {
        if (mTextView != null) {
            mTextView.setText(res);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        return super.onTouchEvent(event);
    }
}
