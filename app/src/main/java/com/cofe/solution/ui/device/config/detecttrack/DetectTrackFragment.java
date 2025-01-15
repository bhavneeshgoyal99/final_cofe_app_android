package com.cofe.solution.ui.device.config.detecttrack;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.manager.device.media.MediaManager.PLAY_DEV_PLAYBACK;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cofe.solution.R;
import com.cofe.solution.base.DemoBaseFragment;
import com.cofe.solution.ui.adapter.CustomTractListViewAdapter;
import com.cofe.solution.ui.device.preview.view.DevMonitorActivity;
import com.google.gson.internal.LinkedTreeMap;
import com.lib.SDKCONST;
import com.manager.db.DevDataCenter;
import com.manager.db.XMDevInfo;
import com.manager.device.DeviceManager;
import com.manager.device.media.monitor.MonitorManager;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;
import com.xm.ui.widget.ptzview.PtzView;

import java.util.HashMap;

import io.reactivex.annotations.Nullable;

public class DetectTrackFragment extends DemoBaseFragment<DetectTrackPresenter> implements DetectTrackContract.IDetectTrackView {

    private ListSelectItem lsiEnable;//移动追踪开关
    private ListSelectItem isWatchTime;//移动追踪 守望时间
    private ExtraSpinner spWatchTime;
    private ListSelectItem lsiSensitivity;//移动追踪灵敏度
    private ExtraSpinner spSensitivity;
    private FrameLayout playView;
    private MonitorManager monitorManager;
    private LinkedTreeMap<String, Object> dataMap;
    private DevMonitorActivity activity;
    HashMap<String, Integer> isWatchTimeHasMap =  new HashMap();
    TextView watchtimeTxtv;
    LinearLayout watchTimeLl;
    LinearLayout sensitivityLl;
    ListView watchTimeLv;
    ListView sensitivityLv;
    ImageView closeImag;
    private OnFragmentCallbackListener callbackListener;
    boolean isDemonIsClicked =  false;
    boolean isAovDevice =  false;

    public interface OnFragmentCallbackListener {
        void onDemonButton(String data);
        void motionFramentClose();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_detect_track, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            presenter.setDevId(bundle.getString("devId"));
            Log.d("DevRecordFragment", "Dev ID: " + presenter.getDevId());
        }


        initView(view);
        initData();
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.activity = (DevMonitorActivity) context;
            callbackListener = (OnFragmentCallbackListener) context;
        } else {
            Log.d(context.toString() , " must be an instance of DevMonitorActivity");
        }
    }



    private void initView(View view) {
        closeImag = view.findViewById(R.id.close_img);

        lsiEnable = view.findViewById(R.id.lsi_enable);
        watchtimeTxtv = view.findViewById(R.id.watchtime_txtv);
        watchTimeLl = view.findViewById(R.id.watch_time_ll);
        sensitivityLl = view.findViewById(R.id.sensitivity_ll);
        watchTimeLv = view.findViewById(R.id.watch_time_lv);
        sensitivityLv = view.findViewById(R.id.sensitivity_lv);

        TextView titleTxtv = view.findViewById(R.id.toolbar_title);
        titleTxtv.setText(getString(R.string.set_list));

        view.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        lsiEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showWaitDialog();
                int enable = lsiEnable.getRightValue() == SDKCONST.Switch.Open ? SDKCONST.Switch.Close : SDKCONST.Switch.Open;
                lsiEnable.setRightImage(enable);
                dataMap.put("Enable", enable);
                presenter.setDetectTrack();
            }
        });

        lsiSensitivity = view.findViewById(R.id.lsi_sensitivity);
        isWatchTime = view.findViewById(R.id.lsi_watch_time);

        watchTimeLl.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   lsiEnable.setVisibility(GONE);
                   sensitivityLl.setVisibility(GONE);
                   sensitivityLv.setVisibility(GONE);
                   watchTimeLv.setVisibility(VISIBLE);
               }
           }
        );
        sensitivityLl.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   lsiEnable.setVisibility(GONE);
                   sensitivityLl.setVisibility(VISIBLE);
                   sensitivityLv.setVisibility(VISIBLE);

                   watchTimeLl.setVisibility(GONE);
                   watchTimeLv.setVisibility(GONE);
               }
           }
        );

        spWatchTime = isWatchTime.getExtraSpinner();
        spSensitivity = lsiSensitivity.getExtraSpinner();

        playView = view.findViewById(R.id.fl_monitor_surface);
        view.findViewById(R.id.btn_set_watch_preset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showWaitDialog();
                presenter.setWatchPreset();
            }
        });

        /*PtzView ptzView = view.findViewById(R.id.ptz_view);
        ptzView.setOnPtzViewListener(new PtzView.OnPtzViewListener() {
            @Override
            public void onPtzDirection(int direction, boolean stop) {
                presenter.devicePTZControl(0, direction, 4, stop);
            }
        });*/

        view.findViewById(R.id.close_img).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getView() != null) {
                            if(isDemonIsClicked) {
                                callbackListener.motionFramentClose();
                            }
                            getView().animate()
                                    .translationY(getView().getHeight())
                                    .setDuration(300)
                                    .withEndAction(() -> {
                                        // Remove the fragment from the back stack after sliding down
                                        requireActivity().getSupportFragmentManager().popBackStack();
                                    })
                                    .start();
                        }

                    }
                }
        );
        Button demonBtn = view.findViewById(R.id.demon_btn);

        if(isAovDevice){
                demonBtn.setVisibility(VISIBLE);
        } else {
            demonBtn.setVisibility(GONE);
        }
        view.findViewById(R.id.demon_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callbackListener != null) {
                    isDemonIsClicked = true;
                    if(isDemonIsClicked) {
                        demonBtn.setText(getString(R.string.set_as_demon_point));
                    } else {
                        isDemonIsClicked  = false;
                        demonBtn.setText(getString(R.string.demon_work));
                    }
                    callbackListener.onDemonButton(view.findViewById(R.id.demon_btn).getTag().toString());
                }
            }
        });

    }

    private void initData() {
        spWatchTime.initData(new String[]{
                "3s",
                "5s",
                "10s",
                "15s",
                "30s",
                "60s",
                "180s",
                "300s",
                "600s",
                "900s",
                "1800s"}, new Integer[]{3, 5, 10, 15, 30, 60, 180, 300, 600, 900, 1800});
        /*spWatchTime.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                isWatchTime.toggleExtraView(true);
                isWatchTime.setRightText(key);
                dataMap.put("ReturnTime", value);
                activity.showWaitDialog();
                presenter.setDetectTrack();
            }
        });*/

        String[] sesitivtyItems = new String[]{
                getString(R.string.low),
                getString(R.string.middle),
                getString(R.string.high)
        };

        String[] items = new String[]{
                "3s",
                "5s",
                "10s",
                "15s",
                "30s",
                "60s",
                "180s",
                "300s",
                "600s",
                "900s",
                "1800s"};

        // Set adapter
       /* ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.detect_tract_list_items, R.id.text_item, items);
        listView.setAdapter(adapter);*/

        // List item click listener
        CustomTractListViewAdapter listAdapter = new CustomTractListViewAdapter(activity, items, (position, item) -> {
            Integer value = isWatchTimeHasMap.get(item);
            Toast.makeText(activity.getApplicationContext(), "Clicked: " + item + " = " + value + " seconds", Toast.LENGTH_SHORT).show();

            isWatchTime.setRightText(item);
            dataMap.put("ReturnTime", value);
            activity.showWaitDialog();
            presenter.setDetectTrack();

            lsiEnable.setVisibility(VISIBLE);
            watchTimeLl.setVisibility(VISIBLE);
            sensitivityLl.setVisibility(VISIBLE);

            watchTimeLv.setVisibility(GONE);
            sensitivityLv.setVisibility(GONE);

            // Close the dialog
        });
        watchTimeLv.setAdapter(listAdapter);

        CustomTractListViewAdapter sensitivtyAdapter = new CustomTractListViewAdapter(activity, sesitivtyItems, (position, item) -> {
            Integer value = isWatchTimeHasMap.get(item);
            Toast.makeText(activity.getApplicationContext(), "Clicked: " + item + " = " + value + " seconds", Toast.LENGTH_SHORT).show();

            isWatchTime.setRightText(item);
            dataMap.put("ReturnTime", value);
            activity.showWaitDialog();
            presenter.setDetectTrack();

            lsiEnable.setVisibility(VISIBLE);

            sensitivityLv.setVisibility(VISIBLE);
            watchTimeLl.setVisibility(VISIBLE);
            watchTimeLv.setVisibility(GONE);
            sensitivityLv.setVisibility(GONE);

            // Close the dialog
        });
        sensitivityLv.setAdapter(sensitivtyAdapter);



        /*isWatchTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListViewDialog();
                //isWatchTime.toggleExtraView();
            }
        });*/

        spSensitivity.initData(new String[]{
                getString(R.string.low),
                getString(R.string.middle),
                getString(R.string.high)}, new Integer[]{0, 1, 2});
        spSensitivity.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                lsiSensitivity.toggleExtraView(true);
                lsiSensitivity.setRightText(key);
                dataMap.put("Sensitivity", value);
                activity.showWaitDialog();
                presenter.setDetectTrack();
            }
        });

        lsiSensitivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lsiSensitivity.toggleExtraView();
            }
        });

        activity.showWaitDialog();
        presenter.getDetectTrack();

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) playView.getLayoutParams();
        params.height = screenWidth * 9 / 16;
        monitorManager = DeviceManager.getInstance().createMonitorPlayer(playView, presenter.getDevId());
        monitorManager.startMonitor();
        allListviewData();

    }

    @Override
    public DetectTrackPresenter getPresenter() {
        return new DetectTrackPresenter(this);
    }

    @Override
    public void onGetDetectTrackResult(boolean isSuccess, LinkedTreeMap<String, Object> resultMap, int errorId) {
        activity.hideWaitDialog();
        if (isSuccess) {
            if (resultMap != null) {
                dataMap = resultMap;
                //lsiEnable.setRightImage(((Double) resultMap.get("Enable")).intValue());
                spWatchTime.setValue(((Double) resultMap.get("ReturnTime")).intValue());
                isWatchTime.setRightText(spWatchTime.getSelectedName());
                spSensitivity.setValue(((Double) resultMap.get("Sensitivity")).intValue());
                lsiSensitivity.setRightText(spSensitivity.getSelectedName());
            }
        } else {
            showToast(getString(R.string.get_dev_config_failed) , Toast.LENGTH_LONG);
            activity.finish();
        }
    }

    @Override
    public void onSetDetectRackResult(boolean isSuccess, int errorId) {
        activity.hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.set_dev_config_success), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.set_dev_config_failed) , Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onSetWatchPresetResult(boolean isSuccess, int errorId) {
        activity.hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.libfunsdk_operation_success), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.libfunsdk_operation_failed) , Toast.LENGTH_LONG);
        }
    }

    public void setArgumentsFromActivity(String key, String value, boolean isAovDevice) {
        Log.d(getClass().getName(),"setArgumentsFromActivity > value " + value);
        this.isAovDevice = isAovDevice;
        presenter.setDevId(value);
        Bundle args = new Bundle();
        args.putString(key, value);
        this.setArguments(args);
    }


    private void showListViewDialog() {
        // Create a dialog
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_list_view);

        // Reference to ListView
        ListView listView = dialog.findViewById(R.id.list_view);

        // Sample data
        String[] items = new String[]{
                "3s",
                "5s",
                "10s",
                "15s",
                "30s",
                "60s",
                "180s",
                "300s",
                "600s",
                "900s",
                "1800s"};

        // Set adapter
       /* ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.detect_tract_list_items, R.id.text_item, items);
        listView.setAdapter(adapter);*/

        // List item click listener
        CustomTractListViewAdapter listAdapter = new CustomTractListViewAdapter(activity, items, (position, item) -> {
            Integer value = isWatchTimeHasMap.get(item);
            Toast.makeText(activity.getApplicationContext(), "Clicked: " + item + " = " + value + " seconds", Toast.LENGTH_SHORT).show();

            isWatchTime.setRightText(item);
            dataMap.put("ReturnTime", value);
            activity.showWaitDialog();
            presenter.setDetectTrack();
            dialog.dismiss();

            // Close the dialog
            dialog.dismiss();
        });
        listView.setAdapter(listAdapter);
        Log.d( getClass().getName(),"selectoin data "+ listAdapter.getSelectedItem());

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RadioButton radioButton = view.findViewById(R.id.radio_item);
                radioButton.setChecked(true);

                String selectedItem = ((TextView) view.findViewById(R.id.text_item)).getText().toString();
                Toast.makeText(activity.getApplicationContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
                String selectedItem = ((TextView) view.findViewById(R.id.text_item)).getText().toString();

                isWatchTime.setRightText(selectedItem);
                dataMap.put("ReturnTime", isWatchTimeHasMap.get(selectedItem));
                activity.showWaitDialog();
                presenter.setDetectTrack();
                dialog.dismiss();
            }
        });*/

        // Show the dialog with animation
        Animation slideUp = AnimationUtils.loadAnimation(activity, R.anim.anim_bottom_to_top);
        listView.startAnimation(slideUp);
        dialog.show();

        // Close the dialog with animation
        dialog.setOnDismissListener(dialogInterface -> {
            Animation slideDown = AnimationUtils.loadAnimation(activity, R.anim.anim_top_to_bottom);
            listView.startAnimation(slideDown);

        });
    }

    public void closeThisFragment(){
        closeImag.performClick();
    }

    void allListviewData(){
        isWatchTimeHasMap.put("3s", 3);
        isWatchTimeHasMap.put("5s", 5);
        isWatchTimeHasMap.put("10s", 10);
        isWatchTimeHasMap.put("15s", 15);
        isWatchTimeHasMap.put("30s", 30);
        isWatchTimeHasMap.put("60s", 60);
        isWatchTimeHasMap.put("180s", 180);
        isWatchTimeHasMap.put("300s", 300);
        isWatchTimeHasMap.put("600s", 600);
        isWatchTimeHasMap.put("900s", 900);
        isWatchTimeHasMap.put("1800s", 1800);
        
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (monitorManager != null) {
            monitorManager.destroyPlay();
        }
    }
}