package com.cofe.solution.ui.device.record.view;

import android.Manifest;
import android.app.Dialog;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.cofe.solution.app.SDKDemoApplication;
import com.cofe.solution.base.CustomCalendarDialog;
import com.cofe.solution.base.SharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.struct.H264_DVR_FILE_DATA;
import com.manager.ScreenOrientationManager;
import com.manager.device.media.attribute.PlayerAttribute;
import com.manager.device.media.monitor.MonitorManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.utils.TimeUtils;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.BtnColorBK;
import com.xm.ui.widget.BubbleSeekBar;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XMRecyclerView;
import com.xm.ui.widget.XMSeekBar;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.data.BubbleIndicator;

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
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.device.record.adapter.RecordTimeAxisAdapter;
import com.cofe.solution.ui.device.record.listener.DevRecordContract;
import com.cofe.solution.ui.device.record.presenter.DevRecordPresenter;
import io.reactivex.annotations.Nullable;

import static com.manager.device.media.MediaManager.PLAY_CLOUD_PLAYBACK;
import static com.manager.device.media.MediaManager.PLAY_DEV_PLAYBACK;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_CANNOT_PLAY;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_MEDIA_PLAY_SPEED;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_PAUSE;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_PLAY_COMPLETED;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_SAVE_PIC_FILE_S;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_SAVE_RECORD_FILE_S;
import static com.manager.device.media.attribute.PlayerAttribute.E_STATE_STOP;
import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_COMPLETE_ALL;
import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_FAILED;
import static com.manager.device.media.download.DownloadManager.DOWNLOAD_STATE_START;

public class DevRecordActivity extends DemoBaseActivity<DevRecordPresenter> implements DevRecordContract.IDevRecordView, XTitleBar.OnRightClickListener {
    private Calendar calendarShow;
    private RecyclerView rvRecordList;
    private RecyclerView rvRecordFun;
    private XMRecyclerView rvRecordTimeAxis;
    private XMSeekBar xmSeekBar;
    private RecordListAdapter recordListAdapter;
    private RecordFunAdapter recordFunAdapter;
    private RecordTimeAxisAdapter recordTimeAxisAdapter;
    private LinearLayoutManager linearLayoutManager;
    private ViewGroup wndLayout;
    private TextView tvPlaySpeed;
    private TextView noPlayBackTxtv;
    private boolean isCanScroll = true;
    private byte[] lock = new byte[1];
    private int recordType;//录像类型，是本地卡回放还是云回放
    private long searchTime;//初始查询时间
    private Calendar searchMonthCalendar = Calendar.getInstance();
    private int portraitWidth;
    private int portraitHeight;
    private ScreenOrientationManager screenOrientationManager;//Screen rotation manager
    //是否正在拖动播放进度条
    private boolean isSeekTouchPlayProgress = false;
    LinearLayout parentLL;
    String selectedByuser;
    int sharedWidth;
    int sharedHeight;
    ImageView microphoneImg,cameraImg, videoImg, soundImg;
    boolean isVideoCaptureStart;
    FloatingActionButton fab;
    CustomCalendarDialog cCdialog;
    HashSet<CalendarDay> highlightedDates;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_record_list);
        loaderDialog.show();

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            loaderDialog.show();

            Log.d(getClass().getName(), "highlightedDates > "  +highlightedDates);
            cCdialog = new CustomCalendarDialog(this, highlightedDates, selectedDate -> {
                // Handle selected date
              //  Toast.makeText(this, "Selected: " + selectedDate, Toast.LENGTH_SHORT).show();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                try {
                    Date date1 = sdf.parse((selectedDate.getDate()+"").replace("-",""));
                    selectedByuser = selectedDate.getDate().toString();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date1);
                    noPlayBackTxtv.setVisibility(View.GONE);

                    //parentLL.setVisibility(View.GONE);
                    //presenter.searchRecordByTime(calendar);
                    presenter.searchRecordByTime(calendar);
                    presenter.searchRecordByFile(calendar);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            });
            cCdialog.show();
        });
        fab.bringToFront();
        fab.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                // FAB is attached to window (visible)
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                // Re-add the FAB if it's removed
                //((ViewGroup) findViewById(android.R.id.content)).addView(fab);
            }
        });

        SharedPreference cookies = new SharedPreference(getApplicationContext());

        getWindow().setSharedElementEnterTransition(null);
        getWindow().setSharedElementReturnTransition(null);
        postponeEnterTransition();
        String widthHeight = cookies.retrievDevicePreviewHeightandWidth();

        sharedWidth = Integer.parseInt(widthHeight.split(";;;")[0]);
        sharedHeight = Integer.parseInt(widthHeight.split(";;;")[1]);
        RelativeLayout wndInnerLayout = findViewById(R.id.wnd_inner_layout);
        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                sharedElements.put(names.get(0), wndInnerLayout);

                // Resize shared element before transition
                wndInnerLayout.getLayoutParams().width = sharedWidth;
                wndInnerLayout.getLayoutParams().height = sharedHeight;
                wndInnerLayout.requestLayout();
            }
        });
        wndInnerLayout.post(() -> startPostponedEnterTransition());
        initView();
        initData();
    }

    private void initView() {
        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.playback));
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        noPlayBackTxtv = findViewById(R.id.no_play_back_txtv);

        findViewById(R.id.img_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.img_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.searchMediaFileCalendar(searchMonthCalendar);
            }
        });

        rvRecordList = findViewById(R.id.rv_records);
        rvRecordFun = findViewById(R.id.rv_record_fun);
        tvPlaySpeed = findViewById(R.id.tv_play_speed);
        ((RadioButton) findViewById(R.id.rb_by_file)).setChecked(true);
        parentLL = findViewById(R.id.parent_ll);
        RelativeLayout rlBanner = findViewById(R.id.banner_rl);

        microphoneImg = findViewById(R.id.microphone);
        cameraImg = findViewById(R.id.camera);
        videoImg= findViewById(R.id.video);
        soundImg = findViewById(R.id.sound);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        rvRecordTimeAxis = new XMRecyclerView(this, null);
        rlBanner.addView(rvRecordTimeAxis);

        ImageView arrowView = new ImageView(this);
        arrowView.setImageResource(R.mipmap.arrows);
        arrowView.setScaleType(ImageView.ScaleType.FIT_XY);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        rlBanner.addView(arrowView, params);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvRecordTimeAxis.setLayoutManager(linearLayoutManager);

        rvRecordTimeAxis.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        setCanScroll(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        dealWithTimeScrollEnd();
                        dealWithSlideStop();
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        wndLayout = findViewById(R.id.wnd_layout);

        xmSeekBar = findViewById(R.id.xb_seek_to_record);
        xmSeekBar.getSeekBar().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    H264_DVR_FILE_DATA data = presenter.getCurPlayFileInfo();
                    if (null != data) {
                        Calendar startCalendar =  Calendar.getInstance();
                        startCalendar.set(Calendar.YEAR,data.st_3_beginTime.st_0_year);
                        startCalendar.set(Calendar.MONTH,data.st_3_beginTime.st_1_month - 1);
                        startCalendar.set(Calendar.DATE,data.st_3_beginTime.st_2_day);
                        startCalendar.set(Calendar.HOUR_OF_DAY,data.st_3_beginTime.st_4_hour);
                        startCalendar.set(Calendar.MINUTE,data.st_3_beginTime.st_5_minute);
                        startCalendar.set(Calendar.SECOND,data.st_3_beginTime.st_6_second);
                        long _stime = startCalendar.getTimeInMillis() / 1000;

                        Calendar endCalendar =  Calendar.getInstance();
                        endCalendar.set(Calendar.YEAR,data.st_4_endTime.st_0_year);
                        endCalendar.set(Calendar.MONTH,data.st_4_endTime.st_1_month - 1);
                        endCalendar.set(Calendar.DATE,data.st_4_endTime.st_2_day);
                        endCalendar.set(Calendar.HOUR_OF_DAY,data.st_4_endTime.st_4_hour);
                        endCalendar.set(Calendar.MINUTE,data.st_4_endTime.st_5_minute);
                        endCalendar.set(Calendar.SECOND,data.st_4_endTime.st_6_second);
                        long _etime = endCalendar.getTimeInMillis() / 1000;

                        int times = ((int) (progress * (_etime - _stime)) / 100);
                        Calendar calendar = (Calendar) startCalendar.clone();
                        calendar.set(Calendar.HOUR_OF_DAY,0);
                        calendar.set(Calendar.MINUTE,0);
                        calendar.set(Calendar.SECOND,0);

                        calendar.add(Calendar.SECOND,(int) data.getLongStartTime() + times);

                        ((BubbleSeekBar) seekBar).moveIndicator(TimeUtils.showNormalFormat(calendar.getTimeInMillis()));
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekTouchPlayProgress = true;
                xmSeekBar.getSeekBar().showIndicator(BubbleIndicator.TOP, getResources().getColor(R.color.transparent));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isSeekTouchPlayProgress = false;
                H264_DVR_FILE_DATA data = presenter.getCurPlayFileInfo();
                if (null != data) {
                    Calendar startCalendar =  Calendar.getInstance();
                    startCalendar.set(Calendar.YEAR,data.st_3_beginTime.st_0_year);
                    startCalendar.set(Calendar.MONTH,data.st_3_beginTime.st_1_month - 1);
                    startCalendar.set(Calendar.DATE,data.st_3_beginTime.st_2_day);
                    startCalendar.set(Calendar.HOUR_OF_DAY,data.st_3_beginTime.st_4_hour);
                    startCalendar.set(Calendar.MINUTE,data.st_3_beginTime.st_5_minute);
                    startCalendar.set(Calendar.SECOND,data.st_3_beginTime.st_6_second);
                    long _stime = startCalendar.getTimeInMillis() / 1000;

                    Calendar endCalendar =  Calendar.getInstance();
                    endCalendar.set(Calendar.YEAR,data.st_4_endTime.st_0_year);
                    endCalendar.set(Calendar.MONTH,data.st_4_endTime.st_1_month - 1);
                    endCalendar.set(Calendar.DATE,data.st_4_endTime.st_2_day);
                    endCalendar.set(Calendar.HOUR_OF_DAY,data.st_4_endTime.st_4_hour);
                    endCalendar.set(Calendar.MINUTE,data.st_4_endTime.st_5_minute);
                    endCalendar.set(Calendar.SECOND,data.st_4_endTime.st_6_second);
                    long _etime = endCalendar.getTimeInMillis() / 1000;

                    long playTimes = _etime - _stime;
                    int times = (int) (playTimes * seekBar.getProgress() / 100 + data.getLongStartTime());
                    presenter.seekToTime(startCalendar,times);
                    ((BubbleSeekBar) seekBar).hideIndicator();
                }
            }
        });

        parentLL.setOnTouchListener((v, event) -> {
            hideVideoListRadioBtnAndDeleteButton();
            return true; // Allow other touch events to propagate
        });

        findViewById(R.id.btn_layout1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreference cookies = new SharedPreference(DevRecordActivity.this);
                cookies.savePreviewPageTabSelection("real-tab");
                finishAfterTransition();
                overridePendingTransition(0, 0);
            }
        });
        findViewById(R.id.btn_layout3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreference cookies = new SharedPreference(DevRecordActivity.this);
                cookies.savePreviewPageTabSelection("message-tab");
                finishAfterTransition();
                overridePendingTransition(0, 0);
            }
        });;



        findViewById(R.id.camera_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dealWithMonitorFunction(Integer.parseInt(cameraImg.getTag().toString()), true);

            }
        });
        findViewById(R.id.video_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideoCaptureStart = (isVideoCaptureStart) ? false : true;
                int image= (isVideoCaptureStart) ? R.drawable.active_video : R.drawable.video_icon ;
                Glide.with(getApplicationContext()).load(image).into(videoImg);

                //dealWithMonitorFunction(Integer.parseInt(videoImg.getTag().toString()), isVideoCaptureStart);
            }
        });

        findViewById(R.id.sound_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //isSoundCaptureStart = (isSoundCaptureStart) ? false : true;
                //int image= (isSoundCaptureStart) ? R.drawable.speaker_disabled_icon : R.drawable.speaker_icon_1 ;
                //Glide.with(getApplicationContext()).load(image).into(soundImg);
                //dealWithMonitorFunction(Integer.parseInt(soundImg.getTag().toString()), isSoundCaptureStart);
            }
        });

    }

    // Helper method to check if touch is outside the RecyclerView
    private boolean isTouchOutsideView(View view, MotionEvent event) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);

        float x = event.getRawX();
        float y = event.getRawY();

        return x < location[0] || x > location[0] + view.getWidth() ||
                y < location[1] || y > location[1] + view.getHeight();
    }

    private void initData() {
        Intent intent = getIntent();
        recordType = intent.getIntExtra("recordType", PLAY_DEV_PLAYBACK);
        int chnId = intent.getIntExtra("chnId", 0);
        searchTime = intent.getLongExtra("searchTime", 0L);
        presenter.setChnId(chnId);
        calendarShow = Calendar.getInstance();
        if (searchTime != 0) {
            calendarShow.setTimeInMillis(searchTime);
        }
        presenter.setRecordType(recordType);
        recordFunAdapter = new RecordFunAdapter();
        rvRecordFun.setLayoutManager(new GridLayoutManager(this, 4));
        rvRecordFun.setAdapter(recordFunAdapter);

        rvRecordList.setLayoutManager(new LinearLayoutManager(this));
        recordListAdapter = new RecordListAdapter();
        rvRecordList.setAdapter(recordListAdapter);

        recordTimeAxisAdapter = new RecordTimeAxisAdapter(this,
                presenter.getRecordTimeList(),
                screenWidth,
                presenter.getShowCount(),
                presenter.getTimeUnit());
        rvRecordTimeAxis.setAdapter(recordTimeAxisAdapter);
        loaderDialog.setMessage();
        presenter.initRecordPlayer((ViewGroup) findViewById(R.id.layoutPlayWnd), recordType);
        presenter.searchRecordByFile(calendarShow);
        presenter.searchRecordByTime(calendarShow);
        presenter.searchMediaFileCalendar(searchMonthCalendar);


        showTitleDate();
        screenOrientationManager = ScreenOrientationManager.getInstance();
    }

    private void showTitleDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        if (recordType == PLAY_CLOUD_PLAYBACK) {
            //  titleBar.setTitleText(dateFormat.format(calendarShow.getTime()) + "(" + getString(R.string.cloud_playback) + ")");
        } else {
            // titleBar.setTitleText(dateFormat.format(calendarShow.getTime()) + "(" + getString(R.string.sd_playback) + ")");
        }
    }

    @Override
    public DevRecordPresenter getPresenter() {
        return new DevRecordPresenter(this);
    }

    @Override
    public void onSearchRecordByFileResult(boolean isSuccess) {
        loaderDialog.dismiss();
        recordListAdapter.notifyDataSetChanged();
        if (!isSuccess) {
            noPlayBackTxtv.setVisibility(View.VISIBLE);
            noPlayBackTxtv.setText(getString(R.string.search_record_failed_));
            rvRecordFun.setVisibility(View.GONE);
            //parentLL.setVisibility(View.GONE);
            showToast(getString(R.string.search_record_failed_), Toast.LENGTH_LONG);
        } else{
            noPlayBackTxtv.setVisibility(View.GONE);
            parentLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSearchRecordByTimeResult(boolean isSuccess) {
        loaderDialog.dismiss();
        recordListAdapter.notifyDataSetChanged();
        recordTimeAxisAdapter.notifyDataSetChanged();
        if (isSuccess) {
            if (searchTime != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(searchTime);
                int times = calendar.get(Calendar.HOUR_OF_DAY) * 3600 + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND);
                presenter.seekToTime(calendar,times);
                noPlayBackTxtv.setVisibility(View.GONE);
                parentLL.setVisibility(View.VISIBLE);

            }
            noPlayBackTxtv.setVisibility(View.GONE);
            parentLL.setVisibility(View.VISIBLE);

        } else {
            noPlayBackTxtv.setVisibility(View.VISIBLE);
            rvRecordFun.setVisibility(View.GONE);
            //parentLL.setVisibility(View.GONE);
            showToast(getString(R.string.search_record_failed_), Toast.LENGTH_LONG);
        }



    }

    /**
     * 播放状态回调
     *
     * @param playState
     */
    @Override
    public void onPlayStateResult(int playState, int playSpeed) {
        if (playState == PlayerAttribute.E_STATE_PlAY) {
            loaderDialog.dismiss();
            recordFunAdapter.changeBtnState(0, getString(R.string.playback_pause), true);
        } else if (playState == E_STATE_STOP
                || playState == E_STATE_PAUSE
                || playState == E_STATE_PLAY_COMPLETED
                || playState == E_STATE_CANNOT_PLAY) {
            recordFunAdapter.changeBtnState(0, getString(R.string.playback_play), false);
        } else if (playState == E_STATE_MEDIA_PLAY_SPEED) {
            tvPlaySpeed.setText(getString(R.string.play_speed) + ":" + playSpeed);
        }

        if (playState == E_STATE_SAVE_RECORD_FILE_S) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else{
                ((SDKDemoApplication)getApplicationContext()).makeFilesVisibleInGallery();
                showToast(getString(R.string.record_s), Toast.LENGTH_LONG);
            }

            recordFunAdapter.changeBtnState(3, getString(R.string.cut_video), false);
        } else if (playState == E_STATE_SAVE_PIC_FILE_S) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else{
                ((SDKDemoApplication)getApplicationContext()).makeFilesVisibleInGallery();
                showToast(getString(R.string.capture_s), Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void onPlayInfoResult(String time, String rate) {
        if (!StringUtils.isStringNULL(time) && isCanScroll()) {
            Calendar playCalendar = TimeUtils.getNormalFormatCalender(time);
            if (playCalendar != null) {
                H264_DVR_FILE_DATA data = presenter.getCurPlayFileInfo();
                if (null != data) {
                    xmSeekBar.setVisibility(View.VISIBLE);
                    long cutPlayTimes = playCalendar.getTimeInMillis() / 1000;
                    Calendar startCalendar =  Calendar.getInstance();
                    startCalendar.set(Calendar.YEAR,data.st_3_beginTime.st_0_year);
                    startCalendar.set(Calendar.MONTH,data.st_3_beginTime.st_1_month - 1);
                    startCalendar.set(Calendar.DATE,data.st_3_beginTime.st_2_day);
                    startCalendar.set(Calendar.HOUR_OF_DAY,data.st_3_beginTime.st_4_hour);
                    startCalendar.set(Calendar.MINUTE,data.st_3_beginTime.st_5_minute);
                    startCalendar.set(Calendar.SECOND,data.st_3_beginTime.st_6_second);
                    long _stime = startCalendar.getTimeInMillis() / 1000;

                    Calendar endCalendar =  Calendar.getInstance();
                    endCalendar.set(Calendar.YEAR,data.st_4_endTime.st_0_year);
                    endCalendar.set(Calendar.MONTH,data.st_4_endTime.st_1_month - 1);
                    endCalendar.set(Calendar.DATE,data.st_4_endTime.st_2_day);
                    endCalendar.set(Calendar.HOUR_OF_DAY,data.st_4_endTime.st_4_hour);
                    endCalendar.set(Calendar.MINUTE,data.st_4_endTime.st_5_minute);
                    endCalendar.set(Calendar.SECOND,data.st_4_endTime.st_6_second);
                    long _etime = endCalendar.getTimeInMillis() / 1000;

                    if (_etime > _stime) {
                        int progress = (int) ((cutPlayTimes - _stime) * 100 / (_etime - _stime));
                        if (!isSeekTouchPlayProgress) {
                            xmSeekBar.setProgress(progress);
                        }
                    }
                }

                //获取当前选择播放的日期，并将时间设置为00:00:00
                //Get the currently selected playing date and set the time to 00:00:00
                Calendar curDateTime = (Calendar) calendarShow.clone();
                curDateTime.set(Calendar.HOUR_OF_DAY, 0);
                curDateTime.set(Calendar.MINUTE, 0);
                curDateTime.set(Calendar.SECOND, 0);

                //如果当前播放的时间比当前选择的播放日期0点的时间还小，那就是跨天了，那时间轴默认显示最开始的位置
                //If the current playback time is shorter than the 0 o'clock time of the currently selected
                // playback date, it is a cross-day, and the time axis displays the initial position by default
                if (playCalendar.getTimeInMillis() < curDateTime.getTimeInMillis()) {
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                    return;
                }

                int hour = playCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = playCalendar.get(Calendar.MINUTE);
                int second = playCalendar.get(Calendar.SECOND);
                presenter.setPlayTimeBySecond(second);
                int minutes = (hour) * 60 + minute;
                int position = minutes / presenter.getTimeUnit();
                int offset = (int) (((minutes % presenter.getTimeUnit()) + presenter.getPlayTimeBySecond() / 60.0f) * (screenWidth / presenter.getShowCount())
                        / presenter.getTimeUnit());
                linearLayoutManager.scrollToPositionWithOffset(position, offset * (-1));
            }
        }
    }

    @Override
    public void onSearchCalendarResult(boolean isSuccess, Object result) {
        Log.d("onSearchCalendarResult ","called isSuccess > "   + isSuccess );
        if (result instanceof String) {
            XMPromptDlg.onShow(this, (String) result, null);
        } else {
            Log.d("recordDateMap ","====================================== "  );

            HashMap<Object, Boolean> recordDateMap = (HashMap<Object, Boolean>) result;
            highlightedDates = new HashSet<>();

            for (Map.Entry<Object, Boolean> info : recordDateMap.entrySet()) {
                Log.d("recordDateMap ","recordDateMap looping > "  +info.getKey());

                int year = Integer.parseInt(info.getKey().toString().substring(0, 4));  // First 4 characters
                int month = Integer.parseInt(info.getKey().toString().substring(4, 6)); // Next 2 characters
                int day = Integer.parseInt(info.getKey().toString().substring(6, 8));   // Last 2 characters

                highlightedDates.add(CalendarDay.from(year, month, day)); // Jan 1, 2025
            }
            Log.d(getClass().getName(), "after aded data highlightedDates > "  +highlightedDates);

            cCdialog = new CustomCalendarDialog(this, highlightedDates, selectedDate -> {
                // Handle selected date
              //  Toast.makeText(this, "Selected: " + selectedDate, Toast.LENGTH_SHORT).show();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                try {
                    Date date1 = sdf.parse((selectedDate.getDate()+"").replace("-",""));
                    selectedByuser = selectedDate.getDate().toString();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date1);
                    noPlayBackTxtv.setVisibility(View.GONE);

                    //parentLL.setVisibility(View.GONE);
                    //presenter.searchRecordByTime(calendar);
                    //presenter.searchRecordByTime(calendarShow);
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            });
           // cCdialog.show();
        }
    }

    @Override
    public void onDownloadState(int state, String filePath) {
        if (state == DOWNLOAD_STATE_FAILED) {
            loaderDialog.dismiss();
            Toast.makeText(this, getString(R.string.download_f), Toast.LENGTH_LONG).show();
        } else if (state == DOWNLOAD_STATE_START) {
            Toast.makeText(this, getString(R.string.download_start), Toast.LENGTH_LONG).show();
            loaderDialog.dismiss();
        } else if (state == DOWNLOAD_STATE_COMPLETE_ALL) {
            Toast.makeText(this, getString(R.string.download_s), Toast.LENGTH_LONG).show();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filePath)));
            loaderDialog.dismiss();
        }
    }

    @Override
    public void onDownloadProgress(int progress) {
        String content = String.format(getString(R.string.download_progress), progress);
        loaderDialog.setMessage(content);
    }

    private void dealWithTimeScrollEnd() {
        if (!isCanScroll()) {
            int firstPos = linearLayoutManager.findFirstVisibleItemPosition();
            int firstFset = rvRecordTimeAxis.getChildAt(0).getLeft() * (-1);
            int seconds = firstFset * presenter.getShowCount() * presenter.getTimeUnit() * 60 / screenWidth;
            presenter.setPlayTimeByMinute(firstPos * presenter.getTimeUnit() + seconds / 60);
            presenter.setPlayTimeBySecond(seconds % 60);
        }
    }

    private void dealWithSlideStop() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                loaderDialog.setMessage();
                int times = presenter.getPlayTimeByMinute() * 60 + presenter.getPlayTimeBySecond();

                presenter.setPlayTimeBySecond(times % 60);
                int minutes = (times / 60);
                int position = minutes / presenter.getTimeUnit();
                int offset = (int) (((minutes % presenter.getTimeUnit()) + presenter.getPlayTimeBySecond() / 60.0f)
                        * (screenWidth / presenter.getShowCount()) / presenter.getTimeUnit());
                linearLayoutManager.scrollToPositionWithOffset(position, offset * (-1));

                setCanScroll(true);
                presenter.seekToTime(times);
            }
        });
    }

    private void setCanScroll(boolean isCanScroll) {
        synchronized (lock) {
            this.isCanScroll = isCanScroll;
        }
    }


    private boolean isCanScroll() {
        synchronized (lock) {
            return this.isCanScroll;
        }
    }

    @Override
    public void onUpdateView() {

    }

    @Override
    public void onUpdateVideoThumb(Bitmap bitmap, int position) {
        recordListAdapter.updateVideoThumb(bitmap, position);
    }

    @Override
    public void onDeleteVideoResult(boolean isSuccess, int errorId) {
        if (isSuccess) {
            presenter.searchRecordByFile(calendarShow);
            presenter.searchRecordByTime(calendarShow);
            ToastUtils.showLong(getString(R.string.delete_s));
        } else {
            loaderDialog.dismiss();
            ToastUtils.showLong(getString(R.string.delete_f) );
        }
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onRightClick() {
        presenter.searchMediaFileCalendar(searchMonthCalendar);
    }

    class RecordFunAdapter extends RecyclerView.Adapter<RecordFunAdapter.ViewHolder> {
        private  boolean isLastItemVisible = false; // Control visibility of the last item

        private String[] monitorFun = new String[]{
                getString(R.string.device_opt_play),
                getString(R.string.device_setup_encode_audio),
                getString(R.string.capture),
                getString(R.string.cut_video),
                getString(R.string.playback_fast_play),
                getString(R.string.playback_slow_play),
                getString(R.string.device_opt_fullscreen),
                getString(R.string.sel_record_file_type),
                getString(R.string.delete)};

        @NonNull
        @Override
        public RecordFunAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecordFunAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_monitor_fun, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RecordFunAdapter.ViewHolder holder, int position) {
            holder.btnRecordFun.setText(monitorFun[position]);
            holder.btnRecordFun.setTag(position);
        }

        @Override
        public int getItemCount() {
            // Return count based on the visibility of the last item
            return isLastItemVisible ? monitorFun.length : monitorFun.length - 1;
        }


        // Show or hide the last item
        public void toggleLastItemVisibility() {
            isLastItemVisible = !isLastItemVisible; // Toggle visibility
            notifyDataSetChanged(); // Notify the adapter to refresh the view
        }



        class ViewHolder extends RecyclerView.ViewHolder {
            BtnColorBK btnRecordFun;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                btnRecordFun = itemView.findViewById(R.id.btn_monitor_fun);
                btnRecordFun.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        boolean isBtnChange = dealWithRecordFunction(position, btnRecordFun.isSelected());
                        if (isBtnChange) {
                            btnRecordFun.setSelected(!btnRecordFun.isSelected());
                        }
                    }
                });
            }
        }

        public void changeBtnState(int position, String title, boolean isSelected) {
            BtnColorBK btnRecordFun = rvRecordFun.findViewWithTag(position);
            if (btnRecordFun != null) {
                btnRecordFun.setText(title);
                btnRecordFun.setSelected(isSelected);
            }
        }
    }

    public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.ViewHolder> {
        private boolean isMultiSelectMode = false; // Flag for multi-select mode
        private SparseBooleanArray selectedItems = new SparseBooleanArray(); // Tracks selected items
        private List<Integer> selectedItemList = new ArrayList<>(); // Tracks selected items

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_record_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            H264_DVR_FILE_DATA recordInfo = presenter.getRecordInfo(position);
            if (recordInfo != null) {
                holder.lsiRecordInfo.setTitle(String.format("%s-%s",
                        recordInfo.getStartTimeOfDay(), recordInfo.getEndTimeOfDay()));
                holder.lsiRecordInfo.setTip(recordInfo.getFileName());
                Bitmap bitmap = presenter.getLocalVideoThumb(position);
                if (bitmap == null) {
                    presenter.downloadVideoThumb(position);
                    holder.lsiRecordInfo.setLeftImageResource(R.mipmap.ic_thumb);
                } else {
                    holder.lsiRecordInfo.getImageLeft().setImageBitmap(bitmap);
                }
            }

            // Show radio button based on multi-select mode
            holder.radioButton.setVisibility(isMultiSelectMode ? View.VISIBLE : View.GONE);
            holder.radioButton.setChecked(selectedItems.get(position, false));

            // Handle RadioButton click to toggle selection
            holder.radioButton.setOnClickListener(v -> toggleSelection(position));

            // Handle item click
            holder.itemView.setOnClickListener(v -> {
                if (isMultiSelectMode) {
                    toggleSelection(position); // Toggle selection in multi-select mode
                } else {
                    // Normal click action
                    Toast.makeText(holder.itemView.getContext(), "Item clicked: " + position, Toast.LENGTH_SHORT).show();
                }
            });

            // Handle long press to enable multi-select mode
            holder.itemView.setOnLongClickListener(v -> {
                if (!isMultiSelectMode) {
                    setMultiSelectMode(true); // Enable multi-select mode
                    toggleSelection(position); // Select the long-pressed item
                }
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return presenter.getRecordCount();
        }

        /**
         * 更新缩略图
         *
         * @param bitmap
         * @param position
         */
        public void updateVideoThumb(Bitmap bitmap, int position) {
            if (bitmap != null) {
                ListSelectItem lsiRecordInfo = rvRecordList.findViewWithTag("lsiRecordInfo:" + position);
                if (lsiRecordInfo != null) {
                    lsiRecordInfo.getImageLeft().setImageBitmap(bitmap);
                }
            }
        }

        // Toggle item selection
        private void toggleSelection(int position) {
            if (selectedItems.get(position, false)) {
                // Item is already selected, so deselect it
                selectedItemList.add(position);
                selectedItems.delete(position);
            } else {
                // Select the item
                for(int i = 0; i <selectedItemList.size(); i++) {
                    if(position==selectedItemList.get(i)){
                        selectedItemList.remove(i);
                    }
                }
                selectedItems.put(position, true);
            }
            notifyItemChanged(position); // Refresh the item's state
        }

        private void startVideoDeleteWork() {
            XMPromptDlg.onShow(DevRecordActivity.this, getString(R.string.is_sure_delete_cloud_video), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loaderDialog.setMessage();
                    presenter.stopPlay();
                    presenter.deleteVideo(0);
                }
            },null);
        }

        // Enable or disable multi-select mode
        public void setMultiSelectMode(boolean enabled) {
            isMultiSelectMode = enabled;
            // Trigger visibility toggle when an event occurs
                recordFunAdapter.toggleLastItemVisibility(); // Show or hide the last item

            if (!enabled) {
                selectedItems.clear(); // Clear selections when exiting multi-select mode
            }
            notifyDataSetChanged(); // Refresh all items
        }

        // Get selected items for bulk actions
        public List<Integer> getSelectedItems() {
            List<Integer> selectedList = new ArrayList<>();
            for (int i = 0; i < selectedItems.size(); i++) {
                selectedList.add(selectedItems.keyAt(i));
            }
            return selectedList;
        }

        // ViewHolder class
        public class ViewHolder extends RecyclerView.ViewHolder {
            ListSelectItem lsiRecordInfo;
            RadioButton radioButton;
            Button btnDownload;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                lsiRecordInfo = itemView.findViewById(R.id.lsi_record_info);
                View layout = lsiRecordInfo.getRightExtraView();
                btnDownload = layout.findViewById(R.id.btn_record_download);
                // Long press to enter multi-select mode
                lsiRecordInfo.setOnLongClickListener(v -> {
                    if (!isMultiSelectMode) {
                        setMultiSelectMode(true);
                        toggleSelection(getAdapterPosition());
                    }
                    return true;
                });

                lsiRecordInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loaderDialog.setMessage();
                        presenter.stopPlay();
                        presenter.startPlayRecord(getAdapterPosition());
                    }
                });
                btnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loaderDialog.setMessage();
                        presenter.downloadVideoByFile(getAdapterPosition());
                    }
                });



                radioButton = itemView.findViewById(R.id.radio_button); // Add this to your layout
            }
        }
    }

    private boolean dealWithRecordFunction(int position, boolean isSelected) {
        switch (position) {
            case 0://播放和暂停
                if (presenter.isRecordPlay()) {
                    presenter.pausePlay();
                } else {
                    presenter.rePlay();
                }
                break;
            case 1://开启和关闭音频
                if (isSelected) {
                    presenter.closeVoice();
                } else {
                    presenter.openVoice();
                }
                return true;
            case 2://视频抓图
                presenter.capture();
                break;
            case 3://视频剪切
                if (isSelected) {
                    presenter.stopRecord();
                } else {
                    presenter.startRecord();
                }
                return true;
            case 4://快速播放
                presenter.playFast();
                break;
            case 5://慢速播放
                presenter.playSlow();
                break;
            case 6://全屏
                screenOrientationManager.landscapeScreen(this, true);
                break;
            case 7://切换录像类型
                TextView textView = new TextView(this);
                textView.setText(getString(R.string.record_file_type) + ":");
                Spinner spinner = new Spinner(this);
                spinner.setBackgroundColor(Color.WHITE);
                String[] data = {getString(R.string.record_file_all), getString(R.string.record_file_normal), getString(R.string.record_file_alarm),getString(R.string.EpitomeRecord)};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(presenter.getRecordFileType());


                LinearLayout layout = new LinearLayout(this);
                layout.setBackgroundColor(Color.WHITE);
                layout.addView(textView);
                layout.addView(spinner);
                Dialog dialog = XMPromptDlg.onShow(this,layout);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position != presenter.getRecordFileType()) {
                            loaderDialog.setMessage();
                            presenter.setSearchRecordFileType(position);//position枚举对应的值 0：全部 1：普通 2：报警
                            presenter.searchRecordByFile(calendarShow);
                            presenter.searchRecordByTime(calendarShow);
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                break;
            case 8:
                // call video remove function
                recordListAdapter.startVideoDeleteWork();

                break;
            default:
                break;
        }

        return false;
    }


    void hideVideoListRadioBtnAndDeleteButton(){
        if(recordListAdapter!=null) {
            recordFunAdapter.toggleLastItemVisibility(); // Show or hide the last item
            recordListAdapter.setMultiSelectMode(false); // Exit multi-select mode
        }

    }

    @Override
    public void onBackPressed() {
        hideVideoListRadioBtnAndDeleteButton();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenOrientationManager.portraitScreen(this, true);
        } else {
            SharedPreference cookies = new SharedPreference(getApplicationContext());
            cookies.savePreviewPageTabSelection("real-tab");
            overridePendingTransition(0, 0);
            finishAfterTransition();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.pausePlay();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        presenter.rePlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroyPlay();
        if (screenOrientationManager != null) {
            screenOrientationManager.release(this);
        }
        setResult(RESULT_OK);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            titleBar.setVisibility(View.GONE);
            portraitWidth = wndLayout.getWidth();
            portraitHeight = wndLayout.getHeight();
            ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            wndLayout.requestLayout();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            titleBar.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = wndLayout.getLayoutParams();
            layoutParams.width = portraitWidth;
            layoutParams.height = portraitHeight;
            wndLayout.requestLayout();
        }
        super.onConfigurationChanged(newConfig);
    }
}