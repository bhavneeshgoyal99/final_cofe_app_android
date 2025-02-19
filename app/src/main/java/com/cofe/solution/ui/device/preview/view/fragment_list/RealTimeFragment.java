package com.cofe.solution.ui.device.preview.view.fragment_list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cofe.solution.R;

public class RealTimeFragment extends Fragment {

    private OnFeatureClickListener mListener;

    public interface OnFeatureClickListener {
        void onFeatureClicked(String featureName);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFeatureClickListener) {
            mListener = (OnFeatureClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFeatureClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_real_time, container, false);

        // Initialize icons
        LinearLayout cloudStorageIcon = view.findViewById(R.id.icon_cloud_storage);
        LinearLayout aiIcon = view.findViewById(R.id.icon_ai);
        LinearLayout alarmIcon = view.findViewById(R.id.icon_alarm);

        LinearLayout icon_motion_tracking = view.findViewById(R.id.icon_motion_tracking);
        LinearLayout icon_ptz = view.findViewById(R.id.icon_ptz);
        LinearLayout icon_favorites = view.findViewById(R.id.icon_favorites);

        LinearLayout icon_med_tracking = view.findViewById(R.id.icon_med_tracking);
        LinearLayout icon_ptz_reverse = view.findViewById(R.id.icon_ptz_reverse);
        LinearLayout icon_sd_card_alb = view.findViewById(R.id.icon_sd_card_alb);
        LinearLayout zoom = view.findViewById(R.id.zoom);
        LinearLayout battery = view.findViewById(R.id.battery);

        // Set click listeners
        cloudStorageIcon.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(cloudStorageIcon.getTag().toString());
            }
        });

        aiIcon.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(aiIcon.getTag().toString());
            }
        });

        alarmIcon.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(alarmIcon.getTag().toString());
            }
        });

        icon_motion_tracking.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(icon_motion_tracking.getTag().toString());
            }
        });

        icon_ptz.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(icon_ptz.getTag().toString());
            }
        });


        icon_favorites.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(icon_favorites.getTag().toString());
            }
        });


        icon_med_tracking.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(icon_med_tracking.getTag().toString());
            }
        });


        icon_ptz_reverse.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(icon_ptz_reverse.getTag().toString());
            }
        });


        icon_sd_card_alb.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(icon_sd_card_alb.getTag().toString());
            }
        });

        zoom.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(zoom.getTag().toString());
            }
        });

        battery.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFeatureClicked(battery.getTag().toString());
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
