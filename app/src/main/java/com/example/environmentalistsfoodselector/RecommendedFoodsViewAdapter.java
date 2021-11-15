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

public class RecommendedFoodsViewAdapter extends RecyclerView.Adapter<RecommendedFoodsViewAdapter.ViewHolder> {
    private Food currentFood;
    private ArrayList<Food> data;
    private ArrayList<String> names;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    RecommendedFoodsViewAdapter(Context context, ArrayList<Food> data, ArrayList<String> names, Food currentFood) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.names = names;
        this.currentFood = currentFood;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.similar_foods_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        Food food = data.get(position);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(70, 40, 0, 0);

        // if we passed in the current food object, meaning the list is empty
        if(currentFood.equals(data.get(position))) {
            lp.setMargins((width / 2) - 400, 40, 0, 0);
            holder.nameView.setText("Excellent Selection! No Better Foods Found!"); // looks weird without spaces, but may need to keep the same because this doesn't adjust to different screen sizes
        }
        else {
            String name = names.get(position);
            holder.nameView.setText(name);
            DecimalFormat f = new DecimalFormat("#.##");
            // makes sure they line up with units
            if (currentFood.weight && food.weight) {
                holder.carbonView.setText(f.format( food.carbonUsage[0] / currentFood.carbonUsage[0]*100) + "% less Carbon");
                if (currentFood.waterUsage[0] == 0.0f || food.waterUsage[0] == 0.0f)
                    holder.waterView.setText("No Water Data");
                else
                    holder.waterView.setText(f.format(food.waterUsage[0] / currentFood.waterUsage[0]* 100) + "% less water");
            } else {
                holder.carbonView.setText(f.format(food.carbonUsage[1] / currentFood.carbonUsage[1]*100) + "% less Carbon");
                if (currentFood.waterUsage[1] == 0.0f || food.waterUsage[1] == 0.0f)
                    holder.carbonView.setText("No Water Data");
                else
                    holder.waterView.setText(f.format( food.waterUsage[1] / currentFood.waterUsage[1]*100) + "% less water");
            }
        }
        holder.nameView.setLayoutParams(lp);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameView;
        TextView carbonView;
        TextView waterView;


        ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.nameView);
            carbonView = itemView.findViewById(R.id.carbonView);
            waterView = itemView.findViewById(R.id.waterView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
