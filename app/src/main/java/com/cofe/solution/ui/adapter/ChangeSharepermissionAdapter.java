package com.cofe.solution.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cofe.solution.R;
import com.lib.sdk.bean.share.Permission;

import java.util.ArrayList;
import java.util.List;

public class ChangeSharepermissionAdapter extends RecyclerView.Adapter<ChangeSharepermissionAdapter.ViewHolder> {

    private final Context context;
    private final List<String> itemList;

    private final OnItemClickListener listener;
    private List<Integer> itemDrawableList;

    List<Permission> permissions;

    private final List<Boolean> checkedState;

    public interface OnItemClickListener {
        void onItemClick(String label);
    }

    public ChangeSharepermissionAdapter(Context context, List<String> itemList, List<Integer> itemDrawableList, List<Permission> permissions, OnItemClickListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.itemDrawableList = itemDrawableList;
        this.permissions = permissions;
        this.listener = listener;

        this.checkedState = new ArrayList<>();

        // Initialize all checkboxes as unchecked
        for (int i = 0; i < permissions.size(); i++) {
            checkedState.add(permissions.get(i).isEnabled());
        }

    }

    @NonNull
    @Override
    public ChangeSharepermissionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_share_permission, parent, false);
        return new ChangeSharepermissionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChangeSharepermissionAdapter.ViewHolder holder, int position) {
        /*Permission currentPermission = permissions.get(position);
        holder.textView.setText(currentPermission.getLabel());
        holder.checkBox.setChecked(checkedState.get(holder.getAdapterPosition()));

        holder.checkBox.setOnTouchListener((v, event) -> true);  // Disable direct checkbox click

        holder.parentRl.setOnClickListener(view -> {
            boolean newState = !checkedState.get(holder.getAdapterPosition());
            checkedState.set(holder.getAdapterPosition(), newState);
            currentPermission.setEnabled(newState);  // Update the local permission object

            notifyDataSetChanged();  // Update UI
            listener.onItemClick(currentPermission.getLabel());  // Notify listener if needed
        });*/

        Permission currentPermission = permissions.get(position);
        holder.textView.setText(currentPermission.getLabel());
        holder.checkBox.setChecked(currentPermission.isEnabled());

        holder.checkBox.setOnTouchListener((v, event) -> true);  // Disable direct checkbox click

        holder.parentRl.setOnClickListener(view -> {
            boolean newState = !checkedState.get(holder.getAdapterPosition());
            checkedState.set(holder.getAdapterPosition(), newState);
            if (holder.checkBox.isChecked() == true)
            {
                currentPermission.setEnabled(false);  // Update the local permission object
            }
            else{
                currentPermission.setEnabled(true);  // Update the local permission object

            }


            notifyDataSetChanged();  // Update UI
            listener.onItemClick(currentPermission.getLabel());  // Notify listener if needed
        });

    }

    public void toggleAllCheckboxes(boolean enable) {
        for (int i = 0; i < permissions.size(); i++) {
            permissions.get(i).setEnabled(enable);// Check all if enabled
        }
        notifyDataSetChanged();
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public int getItemCount() {
        return permissions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView imageView;
        TextView textView;
        RelativeLayout parentRl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.item_checkbox);
            imageView = itemView.findViewById(R.id.item_image);
            textView = itemView.findViewById(R.id.item_text);
            parentRl = itemView.findViewById(R.id.parent_rl);
        }
    }

    // Method to show/hide all checkboxes
    /*public void toggleAllCheckboxes(boolean enable) {
        isCheckboxVisible = enable;
        for (int i = 0; i < checkedState.size(); i++) {
            checkedState.set(i, enable); // Check all if enabled
        }
        notifyDataSetChanged();
    }*/

    // Method to get checked item positions
   /* public List<Integer> getCheckedItems() {
        List<Integer> checkedItems = new ArrayList<>();
        for (int i = 0; i < checkedState.size(); i++) {
            if (checkedState.get(i)) {
                checkedItems.add(i);
            }
        }
        return checkedItems;
    }*/
}


