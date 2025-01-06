package com.cofe.solution.ui.device.preview.view;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.cofe.solution.R;
import com.cofe.solution.base.HistoryTracker;
import com.cofe.solution.ui.device.preview.view.fragment_list.MessageFragment;
import com.cofe.solution.ui.device.preview.view.fragment_list.PlaybackFragment;
import com.cofe.solution.ui.device.preview.view.fragment_list.RealTimeFragment;
import com.cofe.solution.ui.fragment.DevAlarmMsgFragment;
import com.cofe.solution.ui.fragment.DevMonitorFragment;
import com.cofe.solution.ui.fragment.DevRecordFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DevActivity extends AppCompatActivity implements RealTimeFragment.OnFeatureClickListener{

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    HistoryTracker historyTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dev);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /*getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.main_frame, DevMonitorFragment.class, null)
                .commit();*/

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.main_pager);

        viewPager.setAdapter(new DeviceViewPagerAdapter(this));
        historyTracker = new HistoryTracker();
        historyTracker.trackPageChange(viewPager);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Real-time");
                        break;
                    case 1:
                        tab.setText("Playback");
                        break;
                    case 2:
                        tab.setText("Message");
                        break;
                }
            }
        }).attach();
    }

    private static class DeviceViewPagerAdapter extends FragmentStateAdapter {

        public DeviceViewPagerAdapter(@NonNull AppCompatActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch (position) {
                case 0:
                    return new DevMonitorFragment();
                case 1:
//                    return DevRecordFragment.newInstance();
                    return new DevRecordFragment();
                case 2:
                    return new DevAlarmMsgFragment();
                default:
                    return new RealTimeFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3; // Number of tabs
        }


    }

    @Override
    public void onFeatureClicked(String featureName) {

    }
}