package com.cofe.solution.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.cofe.solution.ui.activity.MeSharingManagement;
import com.lib.sdk.bean.share.OtherShareDevUserBean;

import java.util.List;

public class FromSharedDeviceAdapter extends RecyclerView.Adapter<FromSharedDeviceAdapter.ItemViewHolder> {

    // private List<Item> itemList;
    private MeSharingManagement activity;

    List<OtherShareDevUserBean> data;
    public interface OnItemClickListener {
        void onItemClick();
    }

    private final OnItemClickListener listener;


    // Constructor
    public FromSharedDeviceAdapter(MeSharingManagement activity, OnItemClickListener listener) {
        this.activity = activity;
        this.listener = listener;

    }




    public void setData(List<OtherShareDevUserBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv2,tv1, subtitleTextView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tv2 = itemView.findViewById(R.id.tv2);
            tv1 = itemView.findViewById(R.id.tv1);

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

        holder.tv2.setText("sharing from "+data.get(position).getUsername()+"("+data.get(position).getShareState()+")");
        holder.tv1.setText(data.get(position).getDevId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }
}

