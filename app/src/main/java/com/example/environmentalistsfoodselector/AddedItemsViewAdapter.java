package com.example.environmentalistsfoodselector;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AddedItemsViewAdapter extends RecyclerView.Adapter<AddedItemsViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private ArrayList<AddedItem> items;
    private float totalWater;
    private float totalCarbon;

    // data is passed into the constructor
    AddedItemsViewAdapter(Context context, ArrayList<AddedItem> _items, float _carbon, float _water) {
        this.mInflater = LayoutInflater.from(context);
        this.items = _items;
        this.totalCarbon = _carbon;
        this.totalWater = _water;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.added_item_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        AddedItem item = items.get(position);
        holder.nameText.setText(item.getName());
        holder.amountText.setText(item.getAmountText());
        holder.carbonText.setText(item.getCarbonText(totalCarbon));
        holder.waterText.setText(item.getWaterText(totalWater));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return items.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameText;
        TextView carbonText;
        TextView waterText;
        TextView amountText;


        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.nameText);
            carbonText = itemView.findViewById(R.id.carbonText);
            waterText = itemView.findViewById(R.id.waterText);
            amountText = itemView.findViewById(R.id.amountText);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
