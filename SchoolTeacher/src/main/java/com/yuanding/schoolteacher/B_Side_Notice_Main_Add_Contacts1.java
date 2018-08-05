package com.yuanding.schoolteacher;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanding.schoolteacher.bean.ChildData;
import com.yuanding.schoolteacher.bean.Cpk_Notice_Linkman;
import com.yuanding.schoolteacher.bean.DataHolder;
import com.yuanding.schoolteacher.bean.GroupData;
import com.yuanding.schoolteacher.bean.ViewHolder;
import com.yuanding.schoolteacher.service.Api.InterSideNoticeContactsList;
import com.yuanding.schoolteacher.service.Api.Inter_Call_Back;
import com.yuanding.schoolteacher.utils.Bimp;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.view.ChildView;
import com.yuanding.schoolteacher.view.GroupView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年12月4日 上午10:46:19
 * 
 *          选择收件人页面 ---无滑动 ---暂时没用
 */
public class B_Side_Notice_Main_Add_Contacts1 extends Activity implements
		GroupView.OnGroupClickListener, ChildView.OnChildClickListener {

	private View mLinerReadDataError, mLinerNoContent, mWholeView,view_sendee_loading;

	private Button btn_black_move, btn_black_cancel,btn_go_action_select_contact;
	private ExpandableListView listView;
	private DataHolder dataHolder = new DataHolder();
	private ViewHolder viewholder = new ViewHolder();
	List<GroupData> groupDatas = new ArrayList<GroupData>();
	private TextView select;
	

	private LinearLayout liner_titlebar_back_select_contact;
	private String iswhite;
	private Cpk_Notice_Linkman notice_Linkman;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_side_notice_add_contacts);

		liner_titlebar_back_select_contact = (LinearLayout) findViewById(R.id.liner_titlebar_back_select_contact);
		btn_go_action_select_contact = (Button) findViewById(R.id.btn_go_action_select_contact);
		mWholeView = findViewById(R.id.liner_sendee_wholeView);
		mLinerReadDataError = findViewById(R.id.view_sendee_load_error);
		mLinerNoContent = findViewById(R.id.view_sendee_no_content);
		view_sendee_loading=findViewById(R.id.view_sendee_loading);
		ImageView iv_blank_por = (ImageView) mLinerNoContent
				.findViewById(R.id.iv_blank_por);
		iv_blank_por.setBackgroundResource(R.drawable.noinfo);
		Intent intent = getIntent();
		iswhite = intent.getStringExtra("iswhite");
		select = (TextView) findViewById(R.id.select);
		btn_black_move = (Button) findViewById(R.id.btn_black_move);
		btn_black_cancel = (Button) findViewById(R.id.btn_black_cancel);

		select.setText(A_0_App.count + "");
		listView = (ExpandableListView) findViewById(R.id.listview_group_list);
		listView.setDivider(null);
		btn_go_action_select_contact.setText(A_0_App.strTitle);
		notice_Linkman=new Cpk_Notice_Linkman();
		// 去掉系统默认的箭头图标
		listView.setGroupIndicator(null);
		// 点击Group不收缩
		listView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});
		btn_black_move.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (A_0_App.userids != null && !A_0_App.userids.equals("")) {
					Bimp.notice_userids = A_0_App.userids
							.substring(0, A_0_App.userids.length() - 1);
				}else{
					Bimp.notice_userids="";
				}
				if (A_0_App.classids != null && !A_0_App.classids.equals("")) {
					Bimp.notice_classids = A_0_App.classids.substring(0,
							A_0_App.classids.length() - 1);
				}else{
					Bimp.notice_classids="";
				}
				if (A_0_App.organids != null && !A_0_App.organids.equals("")) {
					Bimp.notice_organids = A_0_App.organids.substring(0,
							A_0_App.organids.length() - 1);
				}else{
					Bimp.notice_organids="";
				}
				finish();

			}
		});

		btn_black_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mLinerReadDataError.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLoadResult(true,false, false, false);
				getData();
			}
		});

		liner_titlebar_back_select_contact
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(R.anim.animal_push_right_in_normal,
								R.anim.animal_push_right_out_normal);
					}
				});

		btn_go_action_select_contact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				A_0_App.count = 0;
				A_0_App.userids = "";
				A_0_App.classids = "";
				A_0_App.organids = "";
				A_0_App.result = "";
				if (A_0_App.strTitle.equals("全选")) {
					for (int i = 0; i < notice_Linkman.getGroup().size(); i++) {
						A_0_App.userids = A_0_App.userids
								+ notice_Linkman.getGroup().get(i).getId() + ",";
						A_0_App.result = A_0_App.result
								+notice_Linkman.getGroup().get(i).getName() + ",";
						A_0_App.count = Integer.parseInt(notice_Linkman.getGroup().get(i).getCount())+ A_0_App.count;
					}
					
					for (int i = 0; i < notice_Linkman.getStration().size(); i++) {
						A_0_App.organids = A_0_App.organids
								+ notice_Linkman.getStration().get(i).getId() + ",";
						A_0_App.result = A_0_App.result
								+notice_Linkman.getStration().get(i).getName() + ",";
						A_0_App.count = Integer.parseInt(notice_Linkman.getStration().get(i).getCount())+ A_0_App.count;
					}
					for (int i = 0; i < notice_Linkman.getTeach().size(); i++) {
						A_0_App.classids = A_0_App.classids
								+ notice_Linkman.getTeach().get(i).getId() + ",";
						A_0_App.result = A_0_App.result
								+notice_Linkman.getTeach().get(i).getName() + ",";
						A_0_App.count = Integer.parseInt(notice_Linkman.getTeach().get(i).getCount())+ A_0_App.count;
					}
					
				
					select.setText(A_0_App.count + "");

					btn_go_action_select_contact.setText("全不选");
					A_0_App.strTitle = "全不选";
					dataHolder.setAllGroupAndChildChecked();
					viewholder.setAllGroupAndChildChecked();
					
					
				} else {
					A_0_App.count = 0;
					A_0_App.userids = "";
					A_0_App.classids = "";
					A_0_App.organids = "";
					A_0_App.result = "";
					select.setText("0");

					btn_go_action_select_contact.setText("全选");
					A_0_App.strTitle = "全选";
					dataHolder.setAllGroupAndChildUnChecked();
					viewholder.setAllGroupAndChildUnChecked();
				}
			}
		});

		getData();

	}

	private void getData() {
		A_0_App.getApi().getSideNoticeContactsList(
				B_Side_Notice_Main_Add_Contacts1.this, A_0_App.USER_TOKEN,
				iswhite, new InterSideNoticeContactsList() {
					@Override
					public void onSuccess(Cpk_Notice_Linkman mList) {
						if (isFinishing())
							return;
						GroupData groupData1 = new GroupData();
						groupData1.setGroupName("我的分组");
						groupData1.setGroupSelected(false);
						GroupData groupData2 = new GroupData();
						groupData2.setGroupName("我的同事");
						groupData2.setGroupSelected(false);
						GroupData groupData3 = new GroupData();
						groupData3.setGroupName("我的学生");
						groupData3.setGroupSelected(false);
						notice_Linkman=mList;
						List<ChildData> items1 = new ArrayList<ChildData>();
						if (mList.getGroup().size() > 0) {
							for (int j = 0; j < mList.getGroup().size(); j++) {
								ChildData childData = new ChildData();
								childData.setChildName(mList.getGroup().get(j)
										.getName());
								childData
										.setId(mList.getGroup().get(j).getId());
								childData.setCount(mList.getGroup().get(j)
										.getCount());
								
									childData.setChildSelected(false);
								
								if (A_0_App.userids != null
										&& !A_0_App.userids.equals("")) {
									for (int i = 0; i < A_0_App.userids
											.substring(
													0,
													A_0_App.userids.length() - 1)
											.split(",").length; i++) {

										if (mList
												.getGroup()
												.get(j)
												.getId()
												.equals(A_0_App.userids
														.split(",")[i])) {
											childData.setChildSelected(true);
										} 
									}
								} 

								items1.add(childData);
							}
							groupData1.setItems(items1);
							groupDatas.add(groupData1);
						}

						List<ChildData> items2 = new ArrayList<ChildData>();
						if (mList.getStration().size() > 0) {
							for (int j = 0; j < mList.getStration().size(); j++) {
								ChildData childData = new ChildData();
								childData.setChildName(mList.getStration()
										.get(j).getName());
								childData.setId(mList.getStration().get(j)
										.getId());
								childData.setCount(mList.getStration().get(j)
										.getCount());

								childData.setChildSelected(false);

								if (A_0_App.organids != null
										&& !A_0_App.organids.equals("")) {
									for (int i = 0; i < A_0_App.organids
											.substring(
													0,
													A_0_App.organids.length() - 1)
											.split(",").length; i++) {
										if (mList
												.getStration()
												.get(j)
												.getId()
												.equals(A_0_App.organids
														.split(",")[i])) {
											childData.setChildSelected(true);
										}
									}
								}

								items2.add(childData);
							}
							groupData2.setItems(items2);
							groupDatas.add(groupData2);
						}

						List<ChildData> items3 = new ArrayList<ChildData>();
						if (mList.getTeach().size() > 0) {
							for (int j = 0; j < mList.getTeach().size(); j++) {
								ChildData childData = new ChildData();
								childData.setChildName(mList.getTeach().get(j)
										.getName());
								childData
										.setId(mList.getTeach().get(j).getId());
								childData.setCount(mList.getTeach().get(j)
										.getCount());
								
									childData.setChildSelected(false);
								
								if (A_0_App.classids != null
										&& !A_0_App.classids.equals("")) {

									for (int i = 0; i < A_0_App.classids
											.substring(
													0,
													A_0_App.classids.length() - 1)
											.split(",").length; i++) {

										if (mList
												.getTeach()
												.get(j)
												.getId()
												.equals(A_0_App.classids
														.split(",")[i])) {
											childData.setChildSelected(true);
										} 
									}

								} 
								items3.add(childData);
							}
							groupData3.setItems(items3);
							groupDatas.add(groupData3);
						}

						// 加载数据
						List<GroupData> contentData = groupDatas;
						dataHolder.setContentData(contentData);
						listView.setAdapter(new ExpandableListAdapter(
								B_Side_Notice_Main_Add_Contacts1.this));
						// 首次加载全部展开
						for (int i = 0; contentData != null
								&& i < contentData.size(); i++) {
							listView.expandGroup(i);
						}
						if (groupDatas != null && groupDatas.size() > 0)
							showLoadResult(false,true, false, false);
						else
							showLoadResult(false,false, false, true);

					}
				},new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        PubMehods.showToastStr(
                                B_Side_Notice_Main_Add_Contacts1.this, msg);
                        showLoadResult(false,false, true, false);
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}

	private void showLoadResult(boolean loading,boolean list, boolean loadFaile, boolean noData) {
		if (list)
			mWholeView.setVisibility(View.VISIBLE);
		else
			mWholeView.setVisibility(View.GONE);

		if (loadFaile)
			mLinerReadDataError.setVisibility(View.VISIBLE);
		else
			mLinerReadDataError.setVisibility(View.GONE);

		if (noData)
			mLinerNoContent.setVisibility(View.VISIBLE);
		else
			mLinerNoContent.setVisibility(View.GONE);
		if(loading)
			view_sendee_loading.setVisibility(View.VISIBLE);
		else
			view_sendee_loading.setVisibility(View.GONE);	
	}

	@Override
	public void onChildChecked(int groupPosition, int childPosition) {

		if (dataHolder.getGroupData(groupPosition).getGroupName()
				.equals("我的分组")) {
			A_0_App.userids = A_0_App.userids
					+ dataHolder.getChildData(groupPosition, childPosition)
							.getId() + ",";
		} else if (dataHolder.getGroupData(groupPosition).getGroupName()
				.equals("我的学生")) {
			A_0_App.classids = A_0_App.classids
					+ dataHolder.getChildData(groupPosition, childPosition)
							.getId() + ",";
		} else if (dataHolder.getGroupData(groupPosition).getGroupName()
				.equals("我的同事")) {
			A_0_App.organids = A_0_App.organids
					+ dataHolder.getChildData(groupPosition, childPosition)
							.getId() + ",";

		}

		A_0_App.count = A_0_App.count
				+ Integer.parseInt(dataHolder.getChildData(groupPosition,
						childPosition).getCount());

		A_0_App.result = A_0_App.result
				+ dataHolder.getChildData(groupPosition, childPosition)
						.getChildName() + ",";
		dataHolder.setChildChecked(groupPosition, childPosition);
		viewholder.setChildChecked(groupPosition, childPosition);
		select.setText(A_0_App.count + "");
	}

	@Override
	public void onChildUnChecked(int groupPosition, int childPosition) {
		if (dataHolder.getGroupData(groupPosition).getGroupName()
				.equals("我的分组")) {
			A_0_App.userids = A_0_App.userids.replace(
					dataHolder.getChildData(groupPosition, childPosition)
							.getId() + ",", "");

		} else if (dataHolder.getGroupData(groupPosition).getGroupName()
				.equals("我的学生")) {
			A_0_App.classids = A_0_App.classids.replace(dataHolder
					.getChildData(groupPosition, childPosition).getId() + ",",
					"");

		} else if (dataHolder.getGroupData(groupPosition).getGroupName()
				.equals("我的同事")) {
			A_0_App.organids = A_0_App.organids.replace(dataHolder
					.getChildData(groupPosition, childPosition).getId() + ",",
					"");
		}
		A_0_App.count = A_0_App.count
				- Integer.parseInt(dataHolder.getChildData(groupPosition,
						childPosition).getCount());

		A_0_App.result = A_0_App.result.replace(
				dataHolder.getChildData(groupPosition, childPosition)
						.getChildName() + ",", "");
		select.setText(A_0_App.count + "");
		dataHolder.setChildUnChecked(groupPosition, childPosition);
		viewholder.setChildUnChecked(groupPosition, childPosition);
	}

	@Override
	public void onGroupChecked(int groupPosition) {
		dataHolder.setGroupChecked(groupPosition);
		viewholder.setGroupChecked(groupPosition);
	}

	@Override
	public void onGroupUnChecked(int groupPosition) {
		dataHolder.setGroupUnChecked(groupPosition);
		viewholder.setGroupUnChecked(groupPosition);
	}

	private class ExpandableListAdapter extends BaseExpandableListAdapter {

		private Activity activity;

		public ExpandableListAdapter(Activity activity) {
			this.activity = activity;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return dataHolder.getGroupData(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return dataHolder.getGroupCount();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return dataHolder.getChildData(groupPosition, childPosition);
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return dataHolder.getChildCount(groupPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			System.out.println("getGroupView:[" + groupPosition + "]");

			final GroupView groupView = new GroupView(
					(B_Side_Notice_Main_Add_Contacts1) activity,
					getBaseContext());
			groupView.setGroupPosition(groupPosition);

			GroupData groupData = (GroupData) getGroup(groupPosition);
			groupView.getSelectGroup().setChecked(groupData.isGroupSelected());
			groupView.getGroupName().setText(groupData.getGroupName());

			viewholder.setGroupView(groupPosition, groupView);

			groupView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {/*
											 * if(dataHolder.isGroupSelected(
											 * groupPosition)){
											 * ((B_Add_Contacts)
											 * activity).onGroupUnChecked
											 * (groupPosition); }else{
											 * ((B_Add_Contacts
											 * )activity).onGroupChecked
											 * (groupPosition); }
											 */
				}
			});

			return groupView;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {

			System.out.println(("getChildView:[" + groupPosition + ","
					+ childPosition + "]"));

			ImageHolder holder;
			if (convertView == null) {
				holder = new ImageHolder();
				convertView = new ChildView(
						(B_Side_Notice_Main_Add_Contacts1) activity,
						getBaseContext());
				holder.childView = (ChildView) convertView;
				convertView.setTag(holder);
			} else {
				holder = (ImageHolder) convertView.getTag();
			}

			ChildView childView = holder.childView;
			childView.setGroupPosition(groupPosition);
			childView.setChildPosition(childPosition);

			ChildData childData = (ChildData) getChild(groupPosition,
					childPosition);
			childView.getSelectChild().setChecked(childData.isChildSelected());
			if (holder.drawable == null) {
				Resources res = getResources();

			}
			// childView.getChildImage().setImageDrawable(holder.drawable);
			childView.getChildName().setText(childData.getChildName());

			viewholder.setChildView(groupPosition, childPosition, childView);
			childView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (dataHolder
							.isChildSelected(groupPosition, childPosition)) {
						((B_Side_Notice_Main_Add_Contacts1) activity)
								.onChildUnChecked(groupPosition, childPosition);
					} else {
						((B_Side_Notice_Main_Add_Contacts1) activity)
								.onChildChecked(groupPosition, childPosition);
					}

				}

			});
			return childView;
		}

		class ImageHolder {
			Drawable drawable;
			ChildView childView;
		}
	}

	// @Override
	// protected void handleTitleBarEvent(int resId, View v) {
	// switch (v.getId()) {
	// case BACK_BUTTON:
	//
	// break;
	// case TEXT_BUTTON:
	//
	// default:
	// break;
	// }
	//
	// }

}
