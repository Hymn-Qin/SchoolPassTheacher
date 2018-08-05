package com.yuanding.schoolteacher;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yuanding.schoolteacher.R;
import com.yuanding.schoolteacher.R.string;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月2日 下午7:40:28
 * 性别和政治面貌
 */
public class A_3_3_Complete_Marer_Nodify_Acy extends A_0_CpkBaseTitle_Navi{
	
	private String[] sex = {"男", "女"};
	private String[] polices = {"党员", "团员", "群众", "其他"};
	private String[] jobtitle = {"助教", "讲师", "副教授", "教授"};
	private String[] temp;
	private String item_type;
	
	private MyAdapter adapter;
	private ListView lv_user_modify_info;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_complete_two);
		
		item_type = getIntent().getExtras().getString("title_name");
		setTitleText(item_type);
		if (item_type.equals("性别")) {
			temp = sex;
		}else if (item_type.equals("职称")) {
			temp = jobtitle;
		} else {
			temp = polices;
		}
		
		lv_user_modify_info = (ListView)findViewById(R.id.lv_user_modify_info);
		adapter = new MyAdapter();
		lv_user_modify_info.setAdapter(adapter);

		lv_user_modify_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View v,int pos, long id) {
						Intent it = new Intent();
						if (item_type.equals("性别")) {
							it.putExtra("modify_content",sex[pos]);
							setResult(1, it);
						}else if (item_type.equals("职称")){
							it.putExtra("modify_content",temp[pos]);
							setResult(1, it);
						} else {
							it.putExtra("modify_content",polices[pos]);
							setResult(1, it);
						}
						finish();
					}
				});
	}
	
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return temp.length;
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
		public View getView(int posi, View converView, ViewGroup arg2) {
			if(converView == null){
				converView = LayoutInflater.from(A_3_3_Complete_Marer_Nodify_Acy.this).inflate(R.layout.item_select_school_text, null);
			}
			TextView text = (TextView)converView.findViewById(R.id.tv_select_school_text);
			text.setText(temp[posi]);
			if(A_0_App.isShowAnimation==true){
              if(posi>A_0_App.modify_curPosi)
				
			  {
				A_0_App.modify_curPosi=posi;
				Animation an=new TranslateAnimation(Animation.RELATIVE_TO_SELF,1, Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				an.setDuration(400);
				an.setStartOffset(50*posi);
			    converView.startAnimation(an);
			  }
		    }
			return converView;
			
		}
		
	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			adapter = null;
			temp = null;
			finish();
			break;
		default:
			break;
		}
	}

}
