package com.cofe.solution.ui.activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.user.info.view.BasicSettingsActivity;

public class WifiListAdapter extends RecyclerView.Adapter<WifiListAdapter.ItemViewHolder> {

    // private List<Item> itemList;
    private NetworkSettings activity;

    // Constructor
    public  WifiListAdapter(NetworkSettings activity) {
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
    public WifiListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wifi_list, parent, false);
        return new WifiListAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiListAdapter.ItemViewHolder holder, int position) {
       /* Item currentItem = itemList.get(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.subtitleTextView.setText(currentItem.getSubtitle());*/
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}

