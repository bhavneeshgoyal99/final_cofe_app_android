package com.cofe.solution.ui.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.activity.MeSharingManagement;
import com.lib.sdk.bean.share.MyShareUserInfoBean;

import java.util.List;

public class MuSharedDeviceAdapter extends RecyclerView.Adapter<MuSharedDeviceAdapter.ItemViewHolder> {

    // private List<Item> itemList;
    private MeSharingManagement activity;
    List<MyShareUserInfoBean> data;
    public interface OnItemClickListener {
        void onItemClick(String devId);
    }

    private final OnItemClickListener listener;

    // Constructor
    public MuSharedDeviceAdapter(MeSharingManagement activity, OnItemClickListener listener) {
        this.activity = activity;
        this.listener = listener;
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
        Log.d("FULL DA",data.get(position).getShareId());
        Log.d("FULL DA",data.get(position).getAccount());
        Log.d("FULL DA",data.get(position).getPowers());
        Log.d("FULL DA",data.get(position).getDevPermissions());
        Log.d("FULL DA",data.get(position).getDevId());
        Log.d("FULL DA", String.valueOf(data.get(position).getSameDevUserCount()));
        Log.d("FULL DA", String.valueOf(data.get(position).getShareState()));
        if (data != null && position < data.size()) {
            holder.tv1.setText(data.get(position).getDevId());
            holder.tv2.setText(activity.getString(R.string.shared_with)+""+data.get(position).getAccount()+""+activity.getString(R.string.user));
        } else {
            // Handle the null case appropriately, maybe set a default value or log an error.
            holder.tv1.setText(activity.getString(R.string.no_data_available));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(data.get(position).getDevId());

            }
        });
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }
}