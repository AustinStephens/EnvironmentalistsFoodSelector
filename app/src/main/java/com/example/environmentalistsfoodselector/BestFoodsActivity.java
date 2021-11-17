package com.example.environmentalistsfoodselector;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Objects;

public class BestFoodsActivity extends AppCompatActivity {

    public int[] foodsArr = {R.array.BakedBest, R.array.DairyBest, R.array.DrinkBest, R.array.DryBest, R.array.FruitBest, R.array.MeatBest,
            R.array.NutsBest, R.array.OilsBest, R.array.ProcessedBest, R.array.JamsBest, R.array.SaucesBest, R.array.SeafoodBest, R.array.SweetsBest, R.array.VegetableBest};

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestfoods);

        TextView foodLabel = (TextView) findViewById(R.id.foodLabel); // Name of Selected Food Label


        //Categories dropdown
        Spinner cat = (Spinner) findViewById(R.id.Categories);
        ArrayAdapter<String> catAdapter = createAdapter(R.array.categories);
        catAdapter.setDropDownViewResource(R.layout.spinner_item);
        cat.setAdapter(catAdapter);
        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
             if(i >0) { // first position is invalid
                    // selects the string array resource to populate the foods dropdown with
                    ArrayAdapter<String> foodAdapter = createAdapter(foodsArr[i-1]);
                    foodAdapter.setDropDownViewResource(R.layout.spinner_item);
                    foodLabel.setText("Select catagory");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }

    private ArrayAdapter<String> createAdapter(int id) {

        return new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(id)) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if(position == 0) textView.setTextColor( Color.GRAY);
                else textView.setTextColor(Color.BLACK);
                return view;
            }
        };
    }

}
