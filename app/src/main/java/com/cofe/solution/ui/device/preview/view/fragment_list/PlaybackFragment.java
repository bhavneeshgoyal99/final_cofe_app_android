package com.cofe.solution.ui.device.preview.view.fragment_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cofe.solution.R;
import com.cofe.solution.ui.device.preview.view.DevMonitorActivity;
import com.cofe.solution.ui.device.record.view.DevRecordActivity;

public class  PlaybackFragment extends Fragment {

    private RealTimeFragment.OnFeatureClickListener mListener;

    public interface OnFeatureClickListener {
        void onFeatureClicked(String featureName);
    }
    String devId, chId;
    Context cxt;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof RealTimeFragment.OnFeatureClickListener) {
            mListener = (RealTimeFragment.OnFeatureClickListener) context;
            //cxt = Context;

           // ((DevMonitorActivity)context).devId
           // ((DevMonitorActivity)context).chId
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFeatureClickListener");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playback, container, false);

        mListener.onFeatureClicked("6");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Call your function here
        //mListener.onFeatureClicked("6");

    }

    public void callbackToActivity(){
        //mListener.onFeatureClicked("6");
        Intent dintent = new Intent (cxt, DevRecordActivity.class);
        dintent.putExtra("devId",devId);
        dintent.putExtra("chnId",chId);
        startActivity(dintent);

    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
