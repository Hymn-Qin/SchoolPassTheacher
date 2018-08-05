package com.yuanding.schoolteacher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月17日 上午10:38:58
 *  ListView和scrowView的配合
 */
public class MyListView extends ListView { 
	 
    public  MyListView  (Context context, AttributeSet attrs) { 
 
        super(context, attrs); 
 
    } 
 
    public  MyListView  (Context context) { 
 
        super(context); 
 
    } 
 
    public  MyListView  (Context context, AttributeSet attrs, int defStyle) { 
 
        super(context, attrs, defStyle); 
 
    } 
 
    @Override
 
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
 
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, 
 
                MeasureSpec.AT_MOST); 
 
        super.onMeasure(widthMeasureSpec, expandSpec); 
 
    } 
 
}