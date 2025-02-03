package com.cofe.solution.ui.device.picture.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.cofe.solution.base.CustomCalendarDialog;
import com.cofe.solution.ui.device.alarm.view.DevAlarmMsgActivity;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.manager.image.BaseImageManager;
import com.manager.image.DevImageManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.utils.XUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.cofe.solution.R;
import com.cofe.solution.app.SDKDemoApplication;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.picture.listener.DevPictureContract;
import com.cofe.solution.ui.device.picture.presenter.DevPicturePresenter;
import io.reactivex.annotations.Nullable;
import org.threeten.bp.LocalDate;

import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_COMPLETE_ALL;
import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_FAILED;
import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_START;

public class DevPictureActivity extends DemoBaseActivity<DevPicturePresenter> implements
        DevPictureContract.IDevPictureView, XTitleBar.OnRightClickListener, BaseImageManager.OnImageManagerListener {
    private PicListAdapter picListAdapter;
    private RecyclerView rvDevPic;
    private Calendar searchMonthCalendar = Calendar.getInstance();
    private Calendar calendarShow;
    private DevImageManager devImageManager;//设备端图片管理（下载缩略图）
    LinearLayout noDataContLl;
    TextView noDatTextv;
    String selectedByuser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_picture_list);

        findViewById(R.id.img_btn).setVisibility(View.VISIBLE);
        noDataContLl = findViewById(R.id.no_data_cont_ll);
        noDatTextv = findViewById(R.id.text_txtv);



        findViewById(R.id.img_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenter.searchMediaFileCalendar(searchMonthCalendar);
            }
        });

        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.picture_list));
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.app_name));
        titleBar.setRightBtnResource(R.mipmap.icon_date, R.mipmap.icon_date);
        titleBar.setLeftClick(this);
        titleBar.setRightIvClick(this);
        titleBar.setBottomTip(DevPictureActivity.class.getName());

        rvDevPic = findViewById(R.id.rv_dev_pic);
        rvDevPic.setLayoutManager(new LinearLayoutManager(this));

        initData();
    }

    private void initData() {
        calendarShow = Calendar.getInstance();
        picListAdapter = new PicListAdapter();
        rvDevPic.setAdapter(picListAdapter);
        presenter.searchPicByFile(calendarShow);
        showTitleDate();

        //初始化设备图片下载管理（缩略图），传入要保存的图片路径
        devImageManager = new DevImageManager(SDKDemoApplication.PATH_PHOTO_TEMP);
        devImageManager.setOnImageManagerListener(this);//监听回调事件
        devImageManager.setDevId(presenter.getDevId());//设置设备序列号

    }

    private void showTitleDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        titleBar.setTitleText(dateFormat.format(calendarShow.getTime()));
    }

    @Override
    public DevPicturePresenter getPresenter() {
        return new DevPicturePresenter(this);
    }

    @Override
    public void onUpdateView() {
        loaderDialog.dismiss();

        if(picListAdapter.getItemCount()<=0){
            noDataContLl.setVisibility(View.VISIBLE);
            rvDevPic.setVisibility(View.GONE);

            if(selectedByuser!=null) {
                noDatTextv.setText(getString(R.string.no_message_yet)+""+selectedByuser);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendarShow = Calendar.getInstance();
                noDatTextv.setText(getString(R.string.no_picture_video_yet)+""+dateFormat.format(calendarShow.getTime()));
            }

        } else{
            picListAdapter.notifyDataSetChanged();
            noDataContLl.setVisibility(View.GONE);
            rvDevPic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDownloadResult(int state, String filePath) {
        loaderDialog.dismiss();
        if (state == DOWNLOAD_STATE_FAILED) {
            Toast.makeText(this, getString(R.string.download_f), Toast.LENGTH_LONG).show();
        } else if (state == DOWNLOAD_STATE_START) {
            Toast.makeText(this, getString(R.string.download_start), Toast.LENGTH_LONG).show();
        } else if (state == DOWNLOAD_STATE_COMPLETE_ALL) {
            Toast.makeText(this, getString(R.string.download_s), Toast.LENGTH_LONG).show();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ImageView imageView = new ImageView(DevPictureActivity.this);
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    imageView.setImageBitmap(bitmap);
                    XMPromptDlg.onShow(DevPictureActivity.this, imageView);
                }
            });
        }
    }
    Dialog calendarDlg;
    @Override
    public void onSearchCalendarResult(boolean isSuccess, Object result) {
        if (result instanceof String) {
            XMPromptDlg.onShow(this, (String) result, null);
        } else {

            Log.d("recordDateMap ","====================================== "  );

            Log.d("recordDateMap ","====================================== "  );


            HashMap<Object, Boolean> recordDateMap = (HashMap<Object, Boolean>) result;
            HashSet<CalendarDay> highlightedDates = new HashSet<>();

            for (Map.Entry<Object, Boolean> info : recordDateMap.entrySet()) {
                Log.d("recordDateMap ","recordDateMap looping > "  +info.getKey());

                int year = Integer.parseInt(info.getKey().toString().substring(0, 4));  // First 4 characters
                int month = Integer.parseInt(info.getKey().toString().substring(4, 6)); // Next 2 characters
                int day = Integer.parseInt(info.getKey().toString().substring(6, 8));   // Last 2 characters

                highlightedDates.add(CalendarDay.from(year, month, day)); // Jan 1, 2025
            }

                CustomCalendarDialog dialog = new CustomCalendarDialog(this, highlightedDates, selectedDate -> {
                    // Handle selected date
                    Toast.makeText(this, "Selected: " + selectedDate, Toast.LENGTH_SHORT).show();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                try {
                    Date date1 = sdf.parse((selectedDate.getDate()+"").replace("-",""));
                    selectedByuser = selectedDate.getDate().toString();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date1);
                    noDataContLl.setVisibility(View.GONE);
                    presenter.searchPicByFile(calendar);
                } catch (ParseException e) {
                    e.printStackTrace();
                    //throw new RuntimeException(e);
                }


            });
            dialog.show();

            /*CalendarView calendarView = new CalendarView(DevPictureActivity.this);
            calendarView.setBackgroundColor(Color.WHITE);
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    loaderDialog.setMessage();
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    presenter.searchPicByFile(calendar);
                    showTitleDate();
                    if (calendarDlg != null) {
                        calendarDlg.dismiss();
                    }
                }
            });
            calendarDlg = XMPromptDlg.onShow(DevPictureActivity.this, calendarView);



            /*

            HashMap<Object, Boolean> recordDateMap = (HashMap<Object, Boolean>) result;
            List recordDateList = new ArrayList();
            HashMap<String, Object> itemMap;
            for (Map.Entry<Object, Boolean> info : recordDateMap.entrySet()) {
                itemMap = new HashMap<>();
                itemMap.put("date", info.getKey());
                recordDateList.add(itemMap);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(this, recordDateList,
                    R.layout.adapter_date_list,
                    new String[]{"date"}, new int[]{R.id.tv_date});

            View layout = LayoutInflater.from(this).inflate(R.layout.dialog_date, null);
            Dialog dialog = XMPromptDlg.onShow(this, layout);

            TextView tvTitle = layout.findViewById(R.id.tv_title);
            tvTitle.setText(getString(R.string.month) + ":" + (searchMonthCalendar.get(Calendar.MONTH) + 1));

            ListView listView = layout.findViewById(R.id.lv_date);
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    HashMap<String, Object> itemData = (HashMap<String, Object>) simpleAdapter.getItem(i);
                    String date = (String) itemData.get("date");
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                        calendarShow = Calendar.getInstance();
                        calendarShow.setTime(dateFormat.parse(date));

                        presenter.searchPicByFile(calendarShow);
                        showTitleDate();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    dialog.dismiss();
                }

            });

            //取消 Cancel
            Button btnCancel = layout.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            //上个月 last month
            Button btnLastMonth = layout.findViewById(R.id.btn_pre_month);
            btnLastMonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchMonthCalendar.add(Calendar.MONTH, -1);
                    presenter.searchMediaFileCalendar(searchMonthCalendar);
                    dialog.dismiss();
                }
            });

            //下个月 next month
            Button btnNextMonth = layout.findViewById(R.id.btn_next_month);
            btnNextMonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    searchMonthCalendar.add(Calendar.MONTH, 1);
                    presenter.searchMediaFileCalendar(searchMonthCalendar);
                    dialog.dismiss();
                }
            });

             */
        }
    }

    @Override
    public void onRightClick() {
        presenter.searchMediaFileCalendar(searchMonthCalendar);
    }

    /**
     * 下载回调结果
     *
     * @param isSuccess 是否成功
     * @param imagePath 图片路径
     * @param bitmap
     * @param mediaType 媒体类型
     * @param seq
     */
    @Override
    public void onDownloadResult(boolean isSuccess, String imagePath, Bitmap bitmap, int mediaType, int seq) {
        ListSelectItem listSelectItem = rvDevPic.findViewWithTag(seq);
        if (listSelectItem != null) {
            if (isSuccess) {
                listSelectItem.getImageLeft().setImageBitmap(bitmap);
            } else {
                listSelectItem.setLeftImageResource(R.mipmap.ic_thumb);
            }
        }
    }

    /**
     * 删除图片回调结果
     *
     * @param isSuccess
     * @param seq
     */
    @Override
    public void onDeleteResult(boolean isSuccess, int seq) {
        // 设备卡存相册中的图片无法单个删除，只能格式化存储卡
    }

    class PicListAdapter extends RecyclerView.Adapter<DevPictureActivity.PicListAdapter.ViewHolder> {
        @NonNull
        @Override
        public DevPictureActivity.PicListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DevPictureActivity.PicListAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_record_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DevPictureActivity.PicListAdapter.ViewHolder holder, int position) {
            H264_DVR_FILE_DATA imageInfo = presenter.getPicInfo(position);
            if (imageInfo != null) {
                holder.lsiPicInfo.setTitle(String.format("%s-%s", imageInfo.getStartTimeOfDay(), imageInfo.getEndTimeOfDay()));
                holder.lsiPicInfo.setTip(imageInfo.getFileName());
                holder.lsiPicInfo.setTag(position);
                /**
                 * 下载设备端图片
                 *
                 * @param h264DvrFileData 图片信息
                 * @param position        回传的Id（在回调的时候知道是哪个消息发送的）
                 * @param imgWidth        图片宽度
                 * @param imgHeight       图片高度
                 * @return
                 */
                Bitmap bitmap = devImageManager.downloadVideoThumb(imageInfo, position, 160, 90);
                if (bitmap != null) {
                    holder.lsiPicInfo.getImageLeft().setImageBitmap(bitmap);
                } else {
                    holder.lsiPicInfo.setLeftImageResource(R.mipmap.ic_thumb);
                }
            }
        }

        @Override
        public int getItemCount() {
            return presenter.getPicCount();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ListSelectItem lsiPicInfo;
            Button btnDownload;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                lsiPicInfo = itemView.findViewById(R.id.lsi_record_info);
                View layout = lsiPicInfo.getRightExtraView();
                btnDownload = layout.findViewById(R.id.btn_record_download);
                btnDownload.setVisibility(View.GONE);

                lsiPicInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loaderDialog.setMessage();
                        presenter.downloadFile(getAdapterPosition());
                    }
                });
            }
        }
    }
}
