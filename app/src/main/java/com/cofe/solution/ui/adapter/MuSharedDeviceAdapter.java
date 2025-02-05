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
import com.lib.sdk.bean.share.MyShareUserInfoBean;

import java.util.List;

public class MuSharedDeviceAdapter extends RecyclerView.Adapter<MuSharedDeviceAdapter.ItemViewHolder> {

    // private List<Item> itemList;
    private MeSharingManagement activity;
    List<MyShareUserInfoBean> data;

    // Constructor
    public MuSharedDeviceAdapter(MeSharingManagement activity) {
        this.activity = activity;
    }
    public void setData(List<MyShareUserInfoBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv1, tv2;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
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
        if (data != null && position < data.size()) {
        holder.tv1.setText(data.get(position).getDevId());
        holder.tv2.setText("Shared with "+data.get(position).getSameDevUserCount()+" User");
        } else {
            // Handle the null case appropriately, maybe set a default value or log an error.
            holder.tv1.setText("No data available");
        }

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
        return (data != null) ? data.size() : 0;
    }
}
