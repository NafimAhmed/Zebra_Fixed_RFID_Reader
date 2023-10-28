package com.zebra.fxreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class InventoryAdapter extends ArrayAdapter<InventoryItem> {


    public InventoryAdapter(@NonNull Context context, ArrayList<InventoryItem> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inventory_list_item, parent, false);
        }
        TextView tagData = convertView.findViewById(R.id.tagData);
        TextView tagCount = convertView.findViewById(R.id.tagCount);
        TextView tagRSSI = convertView.findViewById(R.id.tagRSSI);

        InventoryItem item = getItem(position);
        tagData.setText(item.getTagID());
        tagCount.setText(String.valueOf(item.getCount()));
        tagRSSI.setText(String.valueOf(item.getRSSI()));

        return convertView;
    }
}
