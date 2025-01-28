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
import com.bumptech.glide.Glide;
import com.cofe.solution.R;
import java.util.ArrayList;
import java.util.List;

public class ShareDevicePermissionGridAdapter extends RecyclerView.Adapter<ShareDevicePermissionGridAdapter.ViewHolder> {

    private final Context context;
    private final List<String> itemList;
    private final List<Boolean> checkedState;
    private boolean isCheckboxVisible = true; // Initially hidden
    private List<Integer> itemDrawableList;

    public ShareDevicePermissionGridAdapter(Context context, List<String> itemList, List<Integer> itemDrawableList) {
        this.context = context;
        this.itemList = itemList;
        this.itemDrawableList = itemDrawableList;
        this.checkedState = new ArrayList<>();

        // Initialize all checkboxes as unchecked
        for (int i = 0; i < itemList.size(); i++) {
            checkedState.add(false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shared_permission_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String itemText = itemList.get(holder. getAdapterPosition());
        holder.textView.setText(itemText);
        Glide.with(holder.imageView.getContext()).load(itemDrawableList.get(holder. getAdapterPosition())).into(holder.imageView);

        // Set checkbox state and visibility
        holder.checkBox.setVisibility(isCheckboxVisible ? View.VISIBLE : View.GONE);
        holder.checkBox.setChecked(checkedState.get(holder. getAdapterPosition()));

        // Handle long press to show all checkboxes but only check the long-pressed item
        holder.parentRl.setOnLongClickListener(view -> {
            isCheckboxVisible = true;

            for (int i = 0; i < checkedState.size(); i++) {
                if(checkedState.get(i)!= true) {
                    checkedState.set(i, false);
                }
            }
            checkedState.set(holder. getAdapterPosition(), true);
            notifyDataSetChanged();
            return true;
        });

        holder.parentRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCheckboxVisible) {
                    if (checkedState.get(holder.getAdapterPosition())) {
                        checkedState.set(holder.getAdapterPosition(), false);
                        holder.checkBox.setChecked(false);
                    }  else{
                        checkedState.set(holder.getAdapterPosition(), true);
                        holder.checkBox.setChecked(true);
                    }
                } else {

                    if (checkedState.get(holder.getAdapterPosition())) {
                        checkedState.set(holder.getAdapterPosition(), false);
                    }
                    holder.checkBox.setChecked(false);
                }
            }
        });

            holder.checkBox.setOnTouchListener((v, event) -> true); // Consumes the touch event
            holder.checkBox.setOnClickListener(v -> {}); // Prevents state change

            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                //buttonView.setChecked(!isChecked); // Revert back to original state
            });

        // Handle checkbox clicks
        /*holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            checkedState.set(holder. getAdapterPosition(), isChecked);
        });*/
    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
    public void toggleAllCheckboxes(boolean enable) {
        isCheckboxVisible = enable;
        for (int i = 0; i < checkedState.size(); i++) {
            checkedState.set(i, enable); // Check all if enabled
        }
        notifyDataSetChanged();
    }

    // Method to get checked item positions
    public List<Integer> getCheckedItems() {
        List<Integer> checkedItems = new ArrayList<>();
        for (int i = 0; i < checkedState.size(); i++) {
            if (checkedState.get(i)) {
                checkedItems.add(i);
            }
        }
        return checkedItems;
    }
}
