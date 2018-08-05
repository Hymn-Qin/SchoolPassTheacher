package com.yuanding.schoolteacher;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sea_monster.network.HttpHandler;
import com.yuanding.schoolteacher.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolteacher.bean.FileBean;
import com.yuanding.schoolteacher.https.HttpUtil;
import com.yuanding.schoolteacher.service.Api;
import com.yuanding.schoolteacher.utils.FileSizeUtils;
import com.yuanding.schoolteacher.utils.LogUtils;
import com.yuanding.schoolteacher.utils.PubMehods;
import com.yuanding.schoolteacher.utils.XmlUtils;
import com.yuanding.schoolteacher.view.GeneralDialog;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;

import org.xutils.HttpManager;
import org.xutils.common.Callback;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.rong.imkit.MainActivity;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getRootDirectory;


/**
 * Created by Administrator on 2017/4/11.
 */

public class B_Side_Notice_Main_Sent_Notice_Files extends Activity implements View.OnClickListener {

    private List<FileBean> fileBeanListDOC;
    private List<FileBean> fileBeanList;
    private FileChooseAdapter adapter;
    private ListView fileChoose;
    private Button getF, startGetFile, noGF, endSendF;
    boolean isFileC = true;
    private int fileSizeMax = 5;//限制传输文件大小5m
    private String filePath;//路径
    private String fileName;//文件名
    private String fileSize;//文件大小
    int filePosition;//新进度
    private Handler handler;
    private LinearLayout side_lecture_detail_loading, home_load_loadings, for_help_status_loading_error, liner_titlebar_back;
    private TextView error_line;
    private AnimationDrawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_notice_sent_notice_flies);
        initView();
        if (!MPermissions.shouldShowRequestPermissionRationale(B_Side_Notice_Main_Sent_Notice_Files.this, Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUECT_CODE_SDCARD)) {
            MPermissions.requestPermissions(B_Side_Notice_Main_Sent_Notice_Files.this, REQUECT_CODE_SDCARD, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }else {
            initData();
        }

    }


    private void initView() {
        liner_titlebar_back = (LinearLayout) findViewById(R.id.liner_titlebar_back);
        liner_titlebar_back.setOnClickListener(this);
        fileChoose = (ListView) findViewById(R.id.file_Choose);
        fileChoose.setVerticalScrollBarEnabled(false);
        fileChoose.setFastScrollEnabled(false);
        getF = (Button) findViewById(R.id.getFile);
        getF.setVisibility(View.GONE);
        startGetFile = (Button) findViewById(R.id.startGetFile);
        noGF = (Button) findViewById(R.id.noGetFile);
        startGetFile.setOnClickListener(this);
        noGF.setOnClickListener(this);
// 加载失败
        for_help_status_loading_error = (LinearLayout) findViewById(R.id.for_help_status_loading_error);
        error_line = (TextView) findViewById(R.id.tv_reload);
        // 加载动画
        side_lecture_detail_loading = (LinearLayout) findViewById(R.id.for_help_status_loading);
        home_load_loadings = (LinearLayout) side_lecture_detail_loading.findViewById(R.id.home_load_loading);
        home_load_loadings.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loadings.getBackground();
        side_lecture_detail_loading.setOnClickListener(this);
        error_line.setOnClickListener(this);
        side_lecture_detail_loading.setVisibility(View.VISIBLE);
        drawable.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startGetFile://开始上传
                upLoad();
                break;
            case R.id.noGetFile://取消上传

                final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Notice_Main_Sent_Notice_Files.this,
                        R.style.Theme_GeneralDialog);
                upDateDialog.setTitle("取消上传");
                upDateDialog.setContent("文件上传将被中断，是否确认取消");
                upDateDialog.showLeftButton(R.string.pub_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        upDateDialog.cancel();
                    }
                });
                upDateDialog.showRightButton(R.string.pub_sure, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upDateDialog.cancel();
                        isFileC = true;
                        noGF.setVisibility(View.GONE);
                        startGetFile.setVisibility(View.GONE);
                        fileBeanList.get(filePosition).setFileChoose(false);
                        adapter.notifyDataSetChanged();
                        getF.setVisibility(View.VISIBLE);
//                        endSendF.setVisibility(View.GONE);
                        HttpUtil.cancelUpLoadFile();

                    }
                });
                upDateDialog.show();

                break;
            case R.id.liner_titlebar_back:
                finish();
                break;
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @PermissionGrant(REQUECT_CODE_SDCARD)
    public void requestSdcardSuccess()
    {
        initData();
    }
    @PermissionDenied(REQUECT_CODE_SDCARD)
    public void requestSdcardFailed() {
        A_0_App.getInstance().PermissionToas("SD卡读写", B_Side_Notice_Main_Sent_Notice_Files.this);
    }
    private static final int REQUECT_CODE_SDCARD = 11;//内存卡

    private void initData() {
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == 200) {
                    //按时间排序
                    fileBeanList = invertOrderList(fileBeanListDOC);
                    adapter = new FileChooseAdapter(fileBeanList, B_Side_Notice_Main_Sent_Notice_Files.this);
                    //添加尾布局显示文件数目
                    View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.file_choose_footer, null);
                    TextView fileNum = (TextView) view.findViewById(R.id.fileNum);
                    fileNum.setText(fileBeanList.size() + "个文件");
                    fileChoose.addFooterView(view, null, false);
                    fileChoose.setAdapter(adapter);

                    //动画
                    side_lecture_detail_loading.setVisibility(View.GONE);
                    getF.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (fileBeanListDOC == null) {
                    // 获得SD卡根目录路径 "/sdcard"
                    // 判断SD卡是否存在，并且是否具有读写权限
                    if (Environment.getExternalStorageState().
                            equals(Environment.MEDIA_MOUNTED)) {
                        File sdDir = Environment.getExternalStorageDirectory();
//                        File sdDir1 = Environment.getRootDirectory();
                        // 读取文件夹下文件
                        //保存读取的文件
                        Log.i("aaaa", "run: " + sdDir.toString());
                        fileBeanListDOC = new ArrayList<>();
                        ToSearchFiles(sdDir);
//                        ToSearchFiles(sdDir1);
                        handler.sendEmptyMessage(200);

                    } else {
                        Toast.makeText(B_Side_Notice_Main_Sent_Notice_Files.this, "SD卡不存在", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        }).start();
        fileChoose.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (isFileC) {
                    filePosition = position;
                    for (FileBean fileBean : fileBeanList) {
                        fileBean.setFileChoose(false);
                        fileBean.setProcess(0);
                    }
                    fileBeanList.get(position).setFileChoose(true);
                    adapter.notifyDataSetChanged();
                    startGetFile.setVisibility(View.VISIBLE);
                    getF.setVisibility(View.GONE);
//                    endSendF.setVisibility(View.GONE);


                }
            }
        });

    }

    public List<FileBean> ToSearchFiles(File file) {
    /* 定义一个File文件数组，用来存放/sdcard目录下的文件或文件夹 */
        if (file != null && file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File tempF : files) {
                    if (tempF.isDirectory()) {
                        ToSearchFiles(tempF);
                    } else if (tempF.exists() && tempF.canRead()) {
                        //"find video file , we can save it to file list
                        try {
                /* 是文件，进行比较，如果文件名称中包含文件类型关键字，则保存到list中 */
                     /* 在这里做文件类型的判断 */
                            String name = tempF.getName();
                            if (name.length() > 3) {
                                String substr = name.substring(name.length() - 3, name.length());
                                FileBean fileBean;
                                switch (substr.trim().toLowerCase()) {
                                    case "ocx":
                                        fileBean = new FileBean();
                                        fileBean.setFile(tempF);
                                        fileBean.setName(name);
                                        fileBean.setFileBig(FileSizeUtils.getAutoFileOrFilesSize(tempF));
                                        fileBean.setFileTime(getStandardTime(tempF));
                                        fileBean.setType(1);
                                        fileBean.setFileUri(tempF.getPath());
                                        fileBean.setFileChoose(false);
                                        fileBean.setProcess(0);
                                        fileBeanListDOC.add(fileBean);
                                        break;
                                    case "doc":
                                        fileBean = new FileBean();
                                        fileBean.setFile(tempF);
                                        fileBean.setName(name);
                                        fileBean.setFileBig(FileSizeUtils.getAutoFileOrFilesSize(tempF));
                                        fileBean.setFileTime(getStandardTime(tempF));
                                        fileBean.setType(1);
                                        fileBean.setFileUri(tempF.getPath());
                                        fileBean.setFileChoose(false);
                                        fileBean.setProcess(0);
                                        fileBeanListDOC.add(fileBean);
                                        break;
                                    case "lsx":
                                        fileBean = new FileBean();
                                        fileBean.setFile(tempF);
                                        fileBean.setName(name);
                                        fileBean.setFileBig(FileSizeUtils.getAutoFileOrFilesSize(tempF));
                                        fileBean.setFileTime(getStandardTime(tempF));
                                        fileBean.setType(2);
                                        fileBean.setFileUri(tempF.getPath());
                                        fileBean.setFileChoose(false);
                                        fileBean.setProcess(0);
                                        fileBeanListDOC.add(fileBean);
                                        break;
                                    case "xls":
                                        fileBean = new FileBean();
                                        fileBean.setFile(tempF);
                                        fileBean.setName(name);
                                        fileBean.setFileBig(FileSizeUtils.getAutoFileOrFilesSize(tempF));
                                        fileBean.setFileTime(getStandardTime(tempF));
                                        fileBean.setType(2);
                                        fileBean.setFileUri(tempF.getPath());
                                        fileBean.setFileChoose(false);
                                        fileBean.setProcess(0);
                                        fileBeanListDOC.add(fileBean);
                                        break;
                                    case "ptx":
                                        fileBean = new FileBean();
                                        fileBean.setFile(tempF);
                                        fileBean.setName(name);
                                        fileBean.setFileBig(FileSizeUtils.getAutoFileOrFilesSize(tempF));
                                        fileBean.setFileTime(getStandardTime(tempF));
                                        fileBean.setType(3);
                                        fileBean.setFileUri(tempF.getPath());
                                        fileBean.setFileChoose(false);
                                        fileBean.setProcess(0);
                                        fileBeanListDOC.add(fileBean);
                                        break;
                                    case "ppt":
                                        fileBean = new FileBean();
                                        fileBean.setFile(tempF);
                                        fileBean.setName(name);
                                        fileBean.setFileBig(FileSizeUtils.getAutoFileOrFilesSize(tempF));
                                        fileBean.setFileTime(getStandardTime(tempF));
                                        fileBean.setType(3);
                                        fileBean.setFileUri(tempF.getPath());
                                        fileBean.setFileChoose(false);
                                        fileBean.setProcess(0);
                                        fileBeanListDOC.add(fileBean);
                                        break;
                                    case "txt":
                                        fileBean = new FileBean();
                                        fileBean.setFile(tempF);
                                        fileBean.setName(name);
                                        fileBean.setFileBig(FileSizeUtils.getAutoFileOrFilesSize(tempF));
                                        fileBean.setFileTime(getStandardTime(tempF));
                                        fileBean.setType(7);
                                        fileBean.setFileUri(tempF.getPath());
                                        fileBean.setFileChoose(false);
                                        fileBean.setProcess(0);
                                        fileBeanListDOC.add(fileBean);
                                        break;
                                }
                            }

                        } catch (Exception e) {
                /* 如果路径找不到，提示出错 */
                            Toast.makeText(this, "搜索失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        return fileBeanListDOC;
    }

    public void upLoad() {

        FileBean fileBean = fileBeanList.get(filePosition);
        filePath = fileBean.getFileUri();//路径
        fileName = fileBean.getName();//名字
        fileSize = fileBean.getFileBig();
        //文件大小B为单位
        long fs = FileSizeUtils.getFileOrFilesSize(filePath);
        if (fs > 1024 * 1024 * fileSizeMax) {
            PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice_Files.this, "文件不能大于5M");
        } else {
            noGF.setVisibility(View.VISIBLE);
            startGetFile.setVisibility(View.GONE);
            isFileC = false;
            A_0_App.getApi().upload_File(fileBean.getFile(), new Api.Inter_UpLoad_File_More() {
                @Override
                public void onSuccess(String fileUrl, String fileId) {
                    if (isFinishing())
                        return;
                    PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice_Files.this, "上传完成");
                    noGF.setVisibility(View.GONE);
                    startGetFile.setVisibility(View.VISIBLE);

//                    endSendF.setVisibility(View.VISIBLE);
                    isFileC = true;
                    adapter.notifyDataSetChanged();
                    Intent intent = new Intent();
                    intent.putExtra("name", fileName);
                    intent.putExtra("type", fileBeanList.get(filePosition).getType() + "");
                    intent.putExtra("file_id", fileId);
                    setResult(10, intent);
                    finish();


                }

                @Override
                public void onLoading(long arg0, long arg1, boolean arg2) {
                    //arg0 总大小  arg1 已经上传的大小
                    double process = 1.0 * arg1 / arg0;
                    fileBeanList.get(filePosition).setProcess((int) (process * 100));
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onWaiting() {

                }
            }, new Api.Inter_Call_Back() {
                @Override
                public void onCancelled() {

                }

                @Override
                public void onFinished() {

                }

                @Override
                public void onFailure(String msg) {
                    PubMehods.showToastStr(B_Side_Notice_Main_Sent_Notice_Files.this, msg);
                    isFileC = true;
                    noGF.setVisibility(View.GONE);
                    startGetFile.setVisibility(View.VISIBLE);
                    fileBeanList.get(filePosition).setProcess(0);
                    fileBeanList.get(filePosition).setFileChoose(true);
                    adapter.notifyDataSetChanged();

                }
            });


        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (fileName == null) {
                setResult(1);
            }

            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private List<FileBean> invertOrderList(List<FileBean> fileBean) {
        Collections.sort(fileBean, new Comparator<FileBean>() {
            @Override
            public int compare(FileBean lhs, FileBean rhs) {
                long date1 = lhs.getFile().lastModified();
                long date2 = rhs.getFile().lastModified();
                if (date1 < date2) {
                    return 1;
                }
                return -1;
            }
        });
        return fileBeanListDOC;
    }

    public static String getStandardTime(File filePath) {
        long timestamp = filePath.lastModified();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        //时区不是北京时
//        sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date date = new Date(timestamp);
        sdf.format(date);
        return sdf.format(date);
    }

    public class FileChooseAdapter extends BaseAdapter {
        List<FileBean> fileBeanList = new ArrayList<>();
        Context context;

        private FileChooseAdapter(List<FileBean> fileBeanList,
                                  Context context) {
            this.context = context;
            this.fileBeanList = fileBeanList;
        }

        @Override
        public int getCount() {
            return fileBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return fileBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(B_Side_Notice_Main_Sent_Notice_Files.this).inflate(R.layout.file_choose_item, parent, false);
                //converView = LayoutInflater.from(getActivity()).inflatR.layout.item_side_notice_sent, null);
                holder.file_pb = (ProgressBar) convertView.findViewById(R.id.fileProgressBar);
                holder.file_Image = (ImageView) convertView.findViewById(R.id.fileImage);
                holder.file_name = (TextView) convertView.findViewById(R.id.fileName);
                holder.file_det = (TextView) convertView.findViewById(R.id.fileDet);
                holder.line = convertView.findViewById(R.id.my_line);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                holder.line.setVisibility(View.GONE);
            } else {
                holder.line.setVisibility(View.VISIBLE);
            }
            FileBean files = new FileBean();
            files = fileBeanList.get(position);
            holder.file_name.setText(files.getName());
            holder.file_det.setText(files.getFileBig() + ",   " + files.getFileTime());
            switch (files.getType()) {
                case 1:
                    holder.file_Image.setImageResource(R.drawable.file_word);
                    break;
                case 2:
                    holder.file_Image.setImageResource(R.drawable.file_xl);
                    break;
                case 3:
                    holder.file_Image.setImageResource(R.drawable.file_ppt);
                    break;
                case 7:
                    holder.file_Image.setImageResource(R.drawable.file_txt);
                    break;
            }
            if (files.isFileChoose()) {
                holder.file_pb.setVisibility(View.VISIBLE);
                holder.file_pb.setProgress(files.getProcess());
            } else {
                holder.file_pb.setVisibility(View.GONE);
            }


            return convertView;
        }

        public class ViewHolder {

            ImageView file_Image;
            TextView file_name, file_det;
            ProgressBar file_pb;
            View line;

        }
    }
}
