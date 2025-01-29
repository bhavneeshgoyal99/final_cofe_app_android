package com.cofe.solution.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.activity.AlarmPeriodActivity;
import com.cofe.solution.ui.activity.BasicSettingsActivity;

public class CustomAlarmAdapter extends RecyclerView.Adapter<CustomAlarmAdapter.ItemViewHolder> {

    // private List<Item> itemList;
    private AlarmPeriodActivity activity;

    // Constructor
    public CustomAlarmAdapter(AlarmPeriodActivity activity) {
        this.activity = activity;
    }

    // ViewHolder class
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, subtitleTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //titleTextView = itemView.findViewById(R.id.textViewTitle);
            // subtitleTextView = itemView.findViewById(R.id.textViewSubtitle);
        }
    }

    @NonNull
    @Override
    public CustomAlarmAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_custom_alarm, parent, false);
        return new CustomAlarmAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAlarmAdapter.ItemViewHolder holder, int position) {
       /* Item currentItem = itemList.get(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.subtitleTextView.setText(currentItem.getSubtitle());*/
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
