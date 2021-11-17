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

public class WorstFoodsActivity extends AppCompatActivity {

    public int[] foodsArr = {R.array.BakedFoods, R.array.DairyFoods, R.array.DrinkFoods, R.array.DryFoods, R.array.FruitFoods, R.array.MeatFoods,
            R.array.NutsFoods, R.array.OilsFoods, R.array.ProcessedFoods, R.array.JamsFoods, R.array.SaucesFoods, R.array.SeafoodFoods, R.array.SweetsFoods, R.array.VegetableFoods};

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worstfoods);

        TextView foodLabel = (TextView) findViewById(R.id.foodLabel); // Name of Selected Food Label
        TextView carbonLabel = (TextView) findViewById(R.id.carbon); // carbon usage label
        TextView waterLabel = (TextView) findViewById(R.id.water);

        //Categories dropdown
        Spinner cat = (Spinner) findViewById(R.id.Categories);
        ArrayAdapter<String> catAdapter = createAdapter(R.array.categories);
        catAdapter.setDropDownViewResource(R.layout.spinner_item);
        cat.setAdapter(catAdapter);
        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { // i is the position in the array
                if(i > 0) { // first position is invalid
                    // selects the string array resource to populate the foods dropdown with
                    ArrayAdapter<String> foodAdapter = createAdapter(foodsArr[i-1]);
                    foodAdapter.setDropDownViewResource(R.layout.spinner_item);
                    carbonLabel.setText("0");
                    waterLabel.setText("0");
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
