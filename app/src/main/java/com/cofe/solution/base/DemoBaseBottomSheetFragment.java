package com.cofe.solution.base;


import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cofe.solution.app.SDKDemoApplication;
import com.xm.activity.base.XMBaseFragment;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.widget.XTitleBar;

import java.util.Locale;

public abstract class DemoBaseBottomSheetFragment<T extends XMBasePresenter> extends XMBaseFragment<T> {
    public static final String androidDoc = "https://docs.jftech.com/docs?menusId=ab0ed73834f54368be3e375075e27fb2&siderId=45357c529496431590a7e3463b7cc520&lang=" + Locale.getDefault().getLanguage();
    protected XTitleBar titleBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (titleBar != null) {
            titleBar.setBottomTip(getClass().getName());
        }
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getActivity() != null && getActivity().getApplication() instanceof SDKDemoApplication) {
            ((SDKDemoApplication) getActivity().getApplication()).addActivity(getActivity());
        }
    }

    protected boolean checkLocationService() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            boolean isOpenGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            return isOpenGPS;
        } else {
            return true;
        }
    }



}
