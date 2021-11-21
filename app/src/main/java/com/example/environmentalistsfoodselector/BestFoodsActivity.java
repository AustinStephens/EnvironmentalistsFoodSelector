package com.example.environmentalistsfoodselector;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class BestFoodsActivity extends AppCompatActivity {

    // use the same array for best and worst, first half is best, second half is worst
    public int[] foodsBestArr = {R.array.BakedBestWorst, R.array.DairyBestWorst, R.array.DrinkBestWorst, R.array.DryBestWorst, R.array.FruitBestWorst, R.array.MeatBestWorst,
            R.array.NutsBestWorst, R.array.OilsBestWorst, R.array.ProcessedBestWorst, R.array.JamsBestWorst, R.array.SaucesBestWorst, R.array.SeafoodBestWorst, R.array.SweetsBestWorst, R.array.VegetableBestWorst};

    public HashMap<String, Food> foodsMap;
    final private int SIZE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get screen width
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        if(screenWidth > 600)
            setContentView(R.layout.activity_bestfoods);
        else
            setContentView(R.layout.activity_bestfoods_small);

        // first item
        TextView foodLabel1 = (TextView) findViewById(R.id.foodLabel1);
        TextView carbon1Output = (TextView) findViewById(R.id.carbon1Output);
        TextView water1Output =  (TextView) findViewById(R.id.water1Output);

        // 2nd item
        TextView foodLabel2 = (TextView) findViewById(R.id.foodLabel2); // Name of Selected Food Label
        TextView carbon2Output = (TextView) findViewById(R.id.carbon2Output);
        TextView water2Output = (TextView) findViewById(R.id.water2Output);
        // call the main FoodsMap singleton
        foodsMap = FoodsMap.getInstance().getFoodsMap();

        //Categories dropdown
        Spinner cat = (Spinner) findViewById(R.id.Categories);
        ArrayAdapter<String> catAdapter = createAdapter(R.array.categories);
        catAdapter.setDropDownViewResource(R.layout.spinner_item);
        cat.setAdapter(catAdapter);

        // when click on an item on spinner
        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i >0) { // first position is invalid
                    //food string adapter from spinner item, get the corresponding array resource from string.xml
                    String[] names = getResources().getStringArray(foodsBestArr[i-1]);
                    // list of best food for each cat item, and names
                    ArrayList<Food> list = new ArrayList<Food>();

                    // use only first 2 item, the last 2 is for worst case
                    for (int j = 0; j < SIZE; j++) {
                        String item = (String) names[j];
                        // store food obj of that food name
                        list.add(foodsMap.get(item));
                    }
                    // populate data
                    foodLabel1.setText(names[0]);
                    foodLabel2.setText(names[1]);

                    setLabels(carbon1Output, water1Output, list.get(0));
                    setLabels(carbon2Output, water2Output, list.get(1));
                    //carbon1Output.setText(f.format(list.get( 0 ).carbonUsage) + " gCO\u2082e");
                    //water1Output.setText(f.format(list.get( 0 ).waterUsage)+ " Gallons");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

    }

    private void setLabels(TextView carbon, TextView water, Food food) {
        DecimalFormat f = new DecimalFormat("#.##");
        if(food.weight) {
            carbon.setText(f.format(food.carbonUsage[0]) + " gCO\u2082e emitted per gram");
            if(food.waterUsage[0] == 0.0f)
                water.setText("N/A");
            else
                water.setText(f.format(food.waterUsage[0])  + " Gallons used per gram");
        } else { // if the unit selected is a unit of volume, use 1 indices
            carbon.setText(f.format(food.carbonUsage[1])  + " gCO\u2082e emitted per gram");
            if(food.waterUsage[1] == 0.0f)
                water.setText("N/A");
            else
                water.setText(f.format(food.waterUsage[1])   + " Gallons used per gram");
        }
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
