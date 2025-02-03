package com.cofe.solution.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cofe.solution.R;

public class CustomTractSensitvityListViewAdapter extends BaseAdapter {

    private final Context context;
    private final String[] items;
    private int selectedPosition = -1;  // Keeps track of the selected item
    private final OnItemClickListener listener;

    // Custom interface for click events
    public interface OnItemClickListener {
        void onItemClick(int position, String item);
    }

    public CustomTractSensitvityListViewAdapter(Context context, String[] items, OnItemClickListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.detect_tract_list_items, parent, false);
        }
        LinearLayout parentLL =  convertView.findViewById(R.id.parent_ll);
        TextView textItem = convertView.findViewById(R.id.text_item);
        RadioButton radioButton = convertView.findViewById(R.id.radio_item);

        // Set the text for each item
        textItem.setText(items[position]);

        // Update the RadioButton state based on the selected position
        radioButton.setChecked(position == selectedPosition);

        parentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position;
                notifyDataSetChanged();  // Refresh the ListView to update the selection
                listener.onItemClick(position, items[position]);
                radioButton.setChecked(true);
            }
        });
        // Handle RadioButton click
        radioButton.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();  // Refresh the ListView to update the selection
            listener.onItemClick(position, items[position]);

        });


        return convertView;
    }

    public String getSelectedItem() {
        if (selectedPosition >= 0 && selectedPosition < items.length) {
            return items[selectedPosition];
        }
        return null;
    }
}