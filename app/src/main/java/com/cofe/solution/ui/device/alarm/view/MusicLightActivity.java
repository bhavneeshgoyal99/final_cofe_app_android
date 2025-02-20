package com.cofe.solution.ui.device.alarm.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.lib.FunSDK;
import com.lib.sdk.bean.WhiteLightBean;
import com.utils.XUtils;
import com.xm.ui.widget.ListSelectItem;
import com.xm.ui.widget.listselectitem.extra.adapter.ExtraSpinnerAdapter;
import com.xm.ui.widget.listselectitem.extra.view.ExtraSpinner;

import com.cofe.solution.R;
import com.cofe.solution.utils.ParseTimeUtil;

/**
 * 声光报警:音乐灯界面
 */
public class MusicLightActivity extends WhiteLightActivity{

    private String[] mMusicSwitchArray;
    private ExtraSpinner mSpMusicSwitch;
    private ListSelectItem mLsiMusicSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLsiMusicSwitch = findViewById(R.id.lsi_white_light_music_switch);
        mLsiMusicSwitch.setVisibility(View.VISIBLE);
        if(mLisWhiteLightSwitch != null){
            mLisWhiteLightSwitch.setVisibility(View.GONE);
        }
        initWhiteLightMusicSwitch();
    }


    private void initWhiteLightMusicSwitch() {
        //音乐灯泡控制开关
        initMusicSwitchData();
    }

    private void initMusicSwitchData() {
        mMusicSwitchArray = new String[]{
                FunSDK.TS(getString(R.string.auto_model)),FunSDK.TS(getString(R.string.open)),FunSDK.TS(getString(R.string.close)),
                FunSDK.TS(getString(R.string.timing)),FunSDK.TS(getString(R.string.atmosphere)),FunSDK.TS(getString(R.string.glint))
        };
        mLsiMusicSwitch.setTip(XUtils.getLightViewTips(mMusicSwitchArray));
        mSpMusicSwitch = mLsiMusicSwitch.getExtraSpinner();
//        mSpMusicSwitch.setScrollEnable(false);
        mSpMusicSwitch.initData(mMusicSwitchArray,new Integer[]{0,1,2,3,4,5});
        mLsiMusicSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLsiMusicSwitch.toggleExtraView();
            }
        });
        mSpMusicSwitch.setOnExtraSpinnerItemListener(new ExtraSpinnerAdapter.OnExtraSpinnerItemListener() {
            @Override
            public void onItemClick(int position, String key, Object value) {
                mLsiMusicSwitch.toggleExtraView(true);
                WhiteLightBean mWhiteLight = presenter.getWhiteLightBean();
                if(null != mWhiteLight){
                    switch (position){
                        case 0:
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode(getString(R.string.auto));
                            presenter.saveWhiteLight();
                            break;
                        case 1:
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode(getString(R.string.keepopen));
                            presenter.saveWhiteLight();
                            break;
                        case 2:
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode(getString(R.string.close));
                            presenter.saveWhiteLight();
                            break;
                        case 3:
                            String openTime = ParseTimeUtil.parseTime(mWhiteLight.getWorkPeriod().getSHour(),
                                    mWhiteLight.getWorkPeriod().getSMinute());
                            String closeTime = ParseTimeUtil.parseTime(mWhiteLight.getWorkPeriod().getEHour(),
                                    mWhiteLight.getWorkPeriod().getEMinute());
                            mWhiteLightOpenTime.setText(openTime);
                            mWhiteLightCloseTime.setText(closeTime);
                            mWhiteLight.setWorkMode(getString(R.string.timing));
                            mTimeSettingLayout.setVisibility(View.VISIBLE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            presenter.saveWhiteLight();
                            break;
                        case 4:
                            //气氛灯开关
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);
                            mWhiteLight.setWorkMode(getString(R.string.atmosphere));
                            presenter.saveWhiteLight();
                            break;
                        case 5:
                            //随音乐灯开关
                            mTimeSettingLayout.setVisibility(View.GONE);
                            mIntelligentModelLayout.setVisibility(View.GONE);

                            mWhiteLight.setWorkMode(getString(R.string.glint));
                            presenter.saveWhiteLight();
                            break;
                        default:
                            break;
                    }
                }
                mLsiMusicSwitch.setRightText(key);

            }
        });
    }


    @Override
    public void showWorkMode(WhiteLightBean mWhiteLight) {
        super.showWorkMode(mWhiteLight);
        if (mWhiteLight != null) {
            if (mWhiteLight.getWorkMode().equals(getString(R.string.auto))) {
                mSpMusicSwitch.setValue(0);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals(getString(R.string.keepopen))) {
                mSpMusicSwitch.setValue(1);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals(getString(R.string.timing))) {
                mSpMusicSwitch.setValue(3);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals(getString(R.string.close))) {
                mSpMusicSwitch.setValue(2);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals(getString(R.string.intelligent)) && null != mWhiteLight.getMoveTrigLight()) {

            } else if (mWhiteLight.getWorkMode().equals(getString(R.string.atmosphere))) {
                mSpMusicSwitch.setValue(4);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else if (mWhiteLight.getWorkMode().equals(getString(R.string.glint))) {
                mSpMusicSwitch.setValue(5);
                mLsiMusicSwitch.setRightText((CharSequence)mSpMusicSwitch.getSelectedName());
            } else {
                Toast.makeText(getApplicationContext(), FunSDK.TS(getString(R.string.data_exception)), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
