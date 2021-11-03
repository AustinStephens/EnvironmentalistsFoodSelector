package com.example.environmentalistsfoodselector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class RecommdenedFoodsViewAdapter extends RecyclerView.Adapter<RecommdenedFoodsViewAdapter.ViewHolder> {
    private Food currentFood;
    private ArrayList<Food> data;
    private ArrayList<String> names;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    RecommdenedFoodsViewAdapter(Context context, ArrayList<Food> data, ArrayList<String> names, Food currentFood) {
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
        Food food = data.get(position);
        if(currentFood.equals(data.get(position))) {
            holder.nameView.setText("        Excellent Selection! No Better Foods Found!");
        } else {
            String name = names.get(position);
            holder.nameView.setText(name);
            DecimalFormat f = new DecimalFormat("#.###");
            if (currentFood.weight && food.weight) {
                holder.carbonView.setText(f.format(1 - food.carbonUsage[0] / currentFood.carbonUsage[0]) + "% less Carbon");
                if (currentFood.waterUsage[0] == 0.0f || food.waterUsage[0] == 0.0f)
                    holder.waterView.setText("No Water Data");
                else
                    holder.waterView.setText(f.format(1 - food.waterUsage[0] / currentFood.waterUsage[0]) + "% less water");
            } else {
                holder.carbonView.setText(f.format(1 - food.carbonUsage[1] / currentFood.carbonUsage[1]) + "% less Carbon");
                if (currentFood.waterUsage[1] == 0.0f || food.waterUsage[1] == 0.0f)
                    holder.carbonView.setText("No Water Data");
                else
                    holder.waterView.setText(f.format(1 - food.waterUsage[1] / currentFood.waterUsage[1]) + "% less water");
            }
        }
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
