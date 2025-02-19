package com.cofe.solution.ui.device.alarm.view;


import static android.app.PendingIntent.getActivity;
import static androidx.core.content.ContentProviderCompat.requireContext;
import static com.manager.device.media.MediaManager.PLAY_CLOUD_PLAYBACK;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cofe.solution.base.DemoBaseFragment;
import com.cofe.solution.base.DemoBaseFragmentActivity;
import com.cofe.solution.base.WifiStateReceiver;
import com.cofe.solution.ui.device.preview.view.DevMonitorActivity;
import com.lib.MsgContent;
import com.lib.sdk.bean.StringUtils;
import com.lib.sdk.bean.alarm.AlarmInfo;
import com.manager.account.AccountManager;
import com.manager.account.BaseAccountManager;
import com.manager.db.DevDataCenter;
import com.manager.image.BaseImageManager;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ListSelectItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.cofe.solution.R;
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
public class DevAlarmMsgFragment extends DemoBaseFragment<DevAlarmPresenter> implements DevAlarmContract.IDevAlarmView {
    private RecyclerView recyclerView;
    private AlarmMsgAdapter alarmMsgAdapter;
    LinearLayout noDataContLl;
    TextView noDataTxtv;
    String selectedByuser;
    private DevMonitorActivity activity;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_dev_alarm, container, false);

        AccountManager.getInstance().xmLogin(DevDataCenter.getInstance().getAccountUserName(), DevDataCenter.getInstance().getAccountPassword(), 1,
                new BaseAccountManager.OnAccountManagerListener() {
                    @Override
                    public void onSuccess(int msgId) {
                        Log.d("Access toekn" ," > "  +DevDataCenter.getInstance().getAccessToken());
                    }

                    @Override
                    public void onFailed(int msgId, int errorId) {
                        Log.d("onFailed" ,"errorId > ");

                    }

                    @Override
                    public void onFunSDKResult(Message msg, MsgContent ex) {

                    }
                });//LOGIN_BY_INTERNET（1）  Account login type


        TextView titleTxtv = view.findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.push_msg));
        titleTxtv.setVisibility(View.GONE);
        view.findViewById(R.id.back_button).setVisibility(View.GONE);
        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        noDataContLl = view.findViewById(R.id.no_data_cont_ll);
        noDataTxtv = view.findViewById(R.id.text_txtv);
        recyclerView = view.findViewById(R.id.rv_alarm_info);

        view.findViewById(R.id.img_btn).setVisibility(View.VISIBLE);
        view.findViewById(R.id.img_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        initData();

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (DevMonitorActivity) context;
        } else {
            throw new ClassCastException(context.toString() + " must be an instance of DevMonitorActivity");
        }
    }



    private void initData() {
        Log.d(getClass().getName(),"initData dev id " + presenter.getDevId());

        alarmMsgAdapter = new AlarmMsgAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(activity.getContext()));
        recyclerView.setAdapter(alarmMsgAdapter);
        activity.loaderDialog.setMessage();
        presenter.searchAlarmMsg();
    }

    Dialog calendarDlg;

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(activity, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.alarm_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle menu item click events here
                switch (item.getItemId()) {
                    case R.id.menu_item1:
                        // Show date selection
                        CalendarView calendarView = new CalendarView ((DevMonitorActivity)getActivity());
                        calendarView.setBackgroundColor(Color.WHITE);
                        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                            @Override
                            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                                activity.loaderDialog.setMessage();
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
                        calendarDlg = XMPromptDlg.onShow(activity, calendarView);
                        return true;
                    case R.id.menu_item2:
                        // Delete all messages
                        XMPromptDlg.onShow(activity, "Are you sure you want to delete all messages, images, and videos?", new View.OnClickListener() {
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
        activity.loaderDialog.dismiss();
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
        activity.loaderDialog.dismiss();
        showToast(isSuccess ? "Image download successful" : "Image download failed", Toast.LENGTH_LONG);
        if (isSuccess && bitmap != null) {
            ImageView imageView = new ImageView(activity.getContext());
            imageView.setImageBitmap(bitmap);
            XMPromptDlg.onShow(activity, imageView);
        }
    }

    @Override
    public void onTurnToVideo(Calendar searchTime) {
        Intent intent = new Intent(activity.getContext(), DevRecordActivity.class);
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
                holder.detectionText.setTag(position);

                if (alarmInfo.isHavePic()) {
                    if (!StringUtils.isStringNULL(alarmInfo.getPic())) {
                        Glide.with(holder.photoImage).load(alarmInfo.getPic()).into(holder.photoImage);
                    } else {
                        presenter.loadThumb(position, new BaseImageManager.OnImageManagerListener() {
                            @Override
                            public void onDownloadResult(boolean isSuccess, String imagePath, Bitmap bitmap, int mediaType, int seq) {
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

                                SliderView sliderView = new SliderView(activity);
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
                                Point screenSize = getScreenSize();
                                int width = (int) (screenSize.x * 0.95);
                                int height = (int) (screenSize.y * 0.8);
                                XMPromptDlg.onShow(activity,
                                        sliderView,
                                        (int) (width * 0.8),
                                        (int) (height * 0.7),
                                        true,
                                        null);
                            }
                        }
                    }
                });

            }
        }
    }

    protected Point getScreenSize() {
        WindowManager windowManager = (WindowManager) activity.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public void setArgumentsFromActivity(String key, String value) {
        Log.d(getClass().getName(),"setArgumentsFromActivity > value " + value);
        presenter.setDevId(value);
        Bundle args = new Bundle();
        args.putString(key, value);
        this.setArguments(args);
    }
}
