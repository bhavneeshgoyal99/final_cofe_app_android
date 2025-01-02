package com.cofe.solution.base;

import android.content.Context;
import android.util.AttributeSet;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;

public class HistoryTracker {
    private final ArrayList<Integer> navigationHistory = new ArrayList<>();

    public void trackPageChange(ViewPager2 viewPager2) {
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Track the navigation history
                if (navigationHistory.isEmpty() || navigationHistory.get(navigationHistory.size() - 1) != position) {
                    navigationHistory.add(position);

                    // Optional: Keep history manageable
                    if (navigationHistory.size() > 50) {
                        navigationHistory.remove(0);
                    }
                }
            }
        });
    }

    public int getSecondLastPosition() {
        if (navigationHistory.size() >= 2) {
            return navigationHistory.get(navigationHistory.size() - 2);
        }
        return -1; // Return -1 if no second last position exists
    }

    public void resetHistory() {
        navigationHistory.clear();
    }
}
