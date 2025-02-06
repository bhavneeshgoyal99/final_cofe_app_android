package com.cofe.solution.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.activity.MySharedUserActivity;
import com.lib.sdk.bean.share.MyShareUserInfoBean;

import java.util.List;

public class SharedUserAdapter extends RecyclerView.Adapter<SharedUserAdapter.ItemViewHolder> {

    List<MyShareUserInfoBean> data;
    private MySharedUserActivity activity;
    // Define the interface
    public interface OnItemClickListener {
        void onItemClick(String shareId);
    }

    public void setData(List<MyShareUserInfoBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private final OnItemClickListener listener;

    // Constructor
    public SharedUserAdapter(MySharedUserActivity activity,OnItemClickListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    // ViewHolder class
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv1, subtitleTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv1);
            // subtitleTextView = itemView.findViewById(R.id.textViewSubtitle);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shared_user, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
       /* Item currentItem = itemList.get(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.subtitleTextView.setText(currentItem.getSubtitle());*/
        holder.tv1.setText(data.get(position).getAccount());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(data.get(position).getShareId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }
}
