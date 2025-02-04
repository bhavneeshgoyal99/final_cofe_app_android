package com.cofe.solution.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.activity.MeSharingManagement;
import com.cofe.solution.ui.activity.MySharedUserActivity;

public class MuSharedDeviceAdapter extends RecyclerView.Adapter<MuSharedDeviceAdapter.ItemViewHolder> {

    // private List<Item> itemList;
    private MeSharingManagement activity;

    // Constructor
    public MuSharedDeviceAdapter(MeSharingManagement activity) {
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
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_sharings, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
       /* Item currentItem = itemList.get(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.subtitleTextView.setText(currentItem.getSubtitle());*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, MySharedUserActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
