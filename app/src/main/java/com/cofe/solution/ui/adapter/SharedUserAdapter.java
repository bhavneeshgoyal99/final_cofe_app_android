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

public class SharedUserAdapter extends RecyclerView.Adapter<SharedUserAdapter.ItemViewHolder> {


    private MySharedUserActivity activity;
    // Define the interface
    public interface OnItemClickListener {
        void onItemClick();
    }

     private final OnItemClickListener listener;

    // Constructor
    public SharedUserAdapter(MySharedUserActivity activity,OnItemClickListener listener) {
        this.activity = activity;
        this.listener = listener;
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
    public SharedUserAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shared_user, parent, false);
        return new SharedUserAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedUserAdapter.ItemViewHolder holder, int position) {
       /* Item currentItem = itemList.get(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.subtitleTextView.setText(currentItem.getSubtitle());*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             listener.onItemClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}

