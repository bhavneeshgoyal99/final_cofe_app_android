package com.cofe.solution.ui.device.alarm.view;

import static com.manager.device.media.MediaManager.PLAY_CLOUD_PLAYBACK;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.MenuPopupWindow;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.alarm.AlarmInfo;
import com.manager.image.BaseImageManager;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;

import org.apache.commons.lang3.time.CalendarUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.cofe.solution.R;
import com.cofe.solution.app.SDKDemoApplication;
import com.cofe.solution.base.DemoBaseActivity;
import com.cofe.solution.ui.adapter.SliderAdapter;
import com.cofe.solution.ui.device.alarm.listener.DevAlarmContract;
import com.cofe.solution.ui.device.alarm.presenter.DevAlarmPresenter;
import com.cofe.solution.ui.device.record.view.DevRecordActivity;
import com.cofe.solution.ui.entity.AlarmTranslationIconBean;
import io.reactivex.annotations.Nullable;

/**
 * 设备报警界面,显示相关的列表菜单.
 * Created by jiangping on 2018-10-23.
 */
public class DevAlarmMsgActivity extends DemoBaseActivity<DevAlarmPresenter> implements DevAlarmContract.IDevAlarmView {
    private RecyclerView recyclerView;
    private AlarmMsgAdapter alarmMsgAdapter;
    LinearLayout noDataContLl;
    TextView noDataTxtv;
    String selectedByuser;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_dev_alarm);
        initView();
        initData();
    }

    private void initView() {
        TextView titleTxtv = findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.push_msg));
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        noDataContLl = findViewById(R.id.no_data_cont_ll);
        noDataTxtv = findViewById(R.id.text_txtv);
        recyclerView = findViewById(R.id.rv_alarm_info);

        /*titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.guide_module_title_device_alarm));
        titleBar.setRightBtnResource(R.mipmap.ic_more, R.mipmap.ic_more);
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());

        titleBar.setRightIvClick(new XTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick() {
                showPopupMenu(titleBar.getRightBtn());
            }
        });*/

        findViewById(R.id.img_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.img_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });

    }

    private void initData() {
        alarmMsgAdapter = new AlarmMsgAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(alarmMsgAdapter);
        showWaitDialog();
        presenter.searchAlarmMsg();
    }

    Dialog calendarDlg;

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.alarm_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item click events here
                switch (item.getItemId()) {
                    case R.id.menu_item1:
                        // Show date selection
                        CalendarView calendarView = new CalendarView(DevAlarmMsgActivity.this);
                        calendarView.setBackgroundColor(Color.WHITE);
                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                            @Override
                            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                                showWaitDialog();
                                selectedByuser = year +"-"+month+"-"+dayOfMonth;

                                Calendar calendar = Calendar.getInstance();
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, month);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                presenter.setSearchTime(calendar);
                                presenter.searchAlarmMsg();
                                if (calendarDlg != null) {
                                    calendarDlg.dismiss();
                                }
                            }
                        });
                        calendarDlg = XMPromptDlg.onShow(DevAlarmMsgActivity.this, calendarView);
                        return true;
                    case R.id.menu_item2:
                        // Delete all messages
                        XMPromptDlg.onShow(DevAlarmMsgActivity.this, "Are you sure you want to delete all messages, images, and videos?", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                presenter.deleteAllAlarmMsg();
                            }
                        }, null);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    public void onUpdateView() {
        hideWaitDialog();
        if (presenter.getAlarmInfoSize() <= 0) {
            showToast("No alarm notifications found", Toast.LENGTH_LONG);
            noDataContLl.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            if(selectedByuser!=null) {
                noDataTxtv.setText(getString(R.string.no_message_yet)+""+selectedByuser);
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calendarShow = Calendar.getInstance();
                noDataTxtv.setText(getString(R.string.no_message_yet)+""+dateFormat.format(calendarShow.getTime()));
            }
        } else {
            noDataContLl.setVisibility(View.GONE);
            alarmMsgAdapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDeleteAlarmMsgResult(boolean isSuccess) {
        showToast(isSuccess ? "Delete successful" : "Delete failed", Toast.LENGTH_LONG);
        alarmMsgAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShowPicResult(boolean isSuccess, Bitmap bitmap) { // Download picture display
        hideWaitDialog();
        showToast(isSuccess ? "Image download successful" : "Image download failed", Toast.LENGTH_LONG);
        if (isSuccess && bitmap != null) {
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            XMPromptDlg.onShow(this, imageView);
        }
    }

    @Override
    public void onTurnToVideo(Calendar searchTime) {
        Intent intent = new Intent(this, DevRecordActivity.class);
        intent.putExtra("devId", presenter.getDevId());
        intent.putExtra("searchTime", searchTime.getTime().getTime());
        intent.putExtra("recordType", PLAY_CLOUD_PLAYBACK);
        startActivity(intent);
    }

    @Override
    public DevAlarmPresenter getPresenter() {
        return new DevAlarmPresenter(this);
    }

    class AlarmMsgAdapter extends RecyclerView.Adapter<AlarmMsgAdapter.ViewHolder> {
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_alarm_msg_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            AlarmInfo alarmInfo = presenter.getAlarmInfo(position);
            if (alarmInfo != null) {
                // Set alarm information in the view
                AlarmTranslationIconBean alarmTranslationIconBean = new AlarmTranslationIconBean();
                String eventTitlte  = alarmInfo.getEvent().replace("Alarm","")
                        .replace("appEvent","");
                eventTitlte = eventTitlte.equals("Human")?"Human Detection": eventTitlte;
                eventTitlte = eventTitlte.equals("VideoBlind")?"Video Blind": eventTitlte;
                holder.detectionText.setText(eventTitlte);

                //Object afterTranslation = ((SDKDemoApplication) getApplication()).getAlarmTranslationIconBean().getLanguageInfo().get("ZH").get(alarmInfo.getEvent());
                String[] dateTime = alarmInfo.getStartTime().split(" ");
                String[] newDateTime = dateTime[0].split("-");
                String dateToshow = newDateTime[2]+"-"+newDateTime[1]+"-"+newDateTime[0];

                holder.detectionTime.setText(dateToshow+" " +dateTime[1]);

                //holder.detectionText.setTip(alarmInfo.getStartTime());
                holder.detectionText.setTag(position);
                //holder.lisAlarmMsg.getImageLeft().setImageBitmap(null);

                // Check if alarm message has an image
                if (alarmInfo.isHavePic()) {
                    if (!StringUtils.isStringNULL(alarmInfo.getPic())) {
                        Glide.with(holder.photoImage).load(alarmInfo.getPic()).into(holder.photoImage);
                    } else {
                        presenter.loadThumb(position, new BaseImageManager.OnImageManagerListener() {
                            @Override
                            public void onDownloadResult(boolean isSuccess, String imagePath, Bitmap bitmap, int mediaType, int seq) {
                                /*ListSelectItem lsiAlarmMsg = recyclerView.findViewWithTag(seq);
                                if (lsiAlarmMsg != null) {
                                    lsiAlarmMsg.getImageLeft().setImageBitmap(bitmap);
                                } else {
                                    holder.photoImage.setImageBitmap(bitmap);
                                }*/
                                holder.photoImage.setImageBitmap(bitmap);

                            }

                            @Override
                            public void onDeleteResult(boolean b, int i) {

                            }
                        });
                    }

                    holder.photoImage.setVisibility(View.VISIBLE);
                } else {
                    holder.photoImage.setVisibility(View.GONE);
                }
                holder.photoImage.setTag((alarmInfo.isVideoInfo())?"video":"image");
                //holder.btnVideo.setVisibility(alarmInfo.isVideoInfo() ? View.VISIBLE : View.GONE);


                // Handle visibility of top and bottom lines
                if (position == 0) {
                    holder.topLine.setVisibility(View.INVISIBLE); // First item, no top line
                } else {
                    holder.topLine.setVisibility(View.VISIBLE);
                }

                if (position == presenter.getAlarmInfoSize() - 1) {
                    holder.bottomLine.setVisibility(View.INVISIBLE); // Last item, no bottom line
                } else {
                    holder.bottomLine.setVisibility(View.VISIBLE);
                }

            }
        }

        @Override
        public int getItemCount() {
            return presenter.getAlarmInfoSize();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ListSelectItem lisAlarmMsg;
            Button btnPicture;
            Button btnVideo;
            Button btnDelete;
            TextView detectionText, detectionTime;
            ImageView leftIcon, photoImage;
            View topLine, bottomLine;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                lisAlarmMsg = itemView.findViewById(R.id.lis_alarm_msg);
                btnPicture = itemView.findViewById(R.id.btn_picture);
                btnVideo = itemView.findViewById(R.id.btn_video);
                btnDelete = itemView.findViewById(R.id.btn_delete);

                detectionText = itemView.findViewById(R.id.detection_text);
                detectionTime = itemView.findViewById(R.id.detection_time);
                leftIcon = itemView.findViewById(R.id.left_icon);
                photoImage = itemView.findViewById(R.id.photo_img);
                topLine = itemView.findViewById(R.id.top_line);
                bottomLine = itemView.findViewById(R.id.bottom_line);


                photoImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (photoImage.getTag() != null) {
                            if (photoImage.getTag().equals("video")) {
                                presenter.showVideo(getAdapterPosition());
                            } else {

                                SliderView sliderView = new SliderView(DevAlarmMsgActivity.this);
                                sliderView.setInfiniteAdapterEnabled(false);
                                AlarmInfo alarmInfo = presenter.getAlarmInfo(getAdapterPosition());
                                List<AlarmInfo.AlarmPicInfo> alarmPicInfos = alarmInfo.getAlarmPicInfos();
                                List<String> imageUrls = new ArrayList<>();
                                for (AlarmInfo.AlarmPicInfo alarmPicInfo : alarmPicInfos) {
                                    imageUrls.add(alarmPicInfo.getUrl());
                                }

                                SliderAdapter adapter = new SliderAdapter(imageUrls);
                                sliderView.setSliderAdapter(adapter);
                                sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
                                sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                                sliderView.setAutoCycle(false);
                                XMPromptDlg.onShow(DevAlarmMsgActivity.this,
                                        sliderView,
                                        (int) (DevAlarmMsgActivity.this.screenWidth * 0.8),
                                        (int) (DevAlarmMsgActivity.this.screenHeight * 0.7),
                                        true,
                                        null);
                            }
                        }
                    }
                });

                /*btnVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.showVideo(getAdapterPosition());
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        XMPromptDlg.onShow(DevAlarmMsgActivity.this, "Are you sure you want to delete this alarm message?", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                presenter.deleteAlarmMsg(getAdapterPosition());
                            }
                        }, null);
                    }
                });*/
            }
        }
    }

}
