package com.example.environmentalistsfoodselector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public int[] foodsArrays = {R.array.BakedFoods, R.array.DairyFoods, R.array.DrinkFoods, R.array.DryFoods, R.array.FruitFoods, R.array.MeatFoods,
            R.array.NutsFoods, R.array.OilsFoods, R.array.ProcessedFoods, R.array.JamsFoods, R.array.SaucesFoods, R.array.SeafoodFoods, R.array.SweetsFoods, R.array.VegetableFoods};

    public HashMap<String, Food> foodsMap;
    public HashMap<String, Unit> unitsMap;
    public Food currentFood;
    public Unit currentUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init
        initMaps();
        //Widget Vars
        TextView foodLabel = (TextView) findViewById(R.id.foodLabel);
        TextView carbonLabel = (TextView) findViewById(R.id.carbon);
        TextView waterLabel = (TextView) findViewById(R.id.water);
        TextInputEditText amount = (TextInputEditText) findViewById(R.id.amount);
        Spinner units = (Spinner) findViewById(R.id.units);

        //Listeners
        amount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int key, KeyEvent event) {
                if(event.getAction() != KeyEvent.ACTION_DOWN) {
                    view.onKeyDown(key, event);
                    if(amount.getText().length() != 0 && currentUnit != null) {
                        String amtStr = amount.getText().toString();
                        float amt = Float.parseFloat(amtStr);
                        setLabels(carbonLabel, waterLabel, amt);
                    } else if (currentUnit != null){
                        setLabels(carbonLabel,waterLabel,0);
                    }
                }
                return true;
            }
        });

        units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectItemText = (String) adapterView.getItemAtPosition(i);
                currentUnit = unitsMap.get(selectItemText);
                String amtStr = amount.getText().toString();
                float amt = Float.parseFloat(amtStr);
                setLabels(carbonLabel, waterLabel, amt);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Foods Dropdown
        Spinner foods = (Spinner) findViewById(R.id.Foods);
        ArrayAdapter<String> foodAdapter = createAdapter(R.array.AllFoods);
        foodAdapter.setDropDownViewResource(R.layout.spinner_item);
        foods.setAdapter(foodAdapter);
        foods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { // i is the position in the array
                if(i > 0) {
                    String selectItemText = (String) adapterView.getItemAtPosition(i);
                    currentFood = foodsMap.get(selectItemText);
                    foodLabel.setText(selectItemText);

                    float amt = 1.0f;
                    if(amount.getText().length() == 0)
                        amount.setText("1");
                    else
                        amt = Float.parseFloat(amount.getText().toString());


                    ArrayAdapter<CharSequence> unitsAdapter;
                    if(currentFood.weight) {
                        if(currentFood.volume) unitsAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.AllUnits, R.layout.spinner_item);
                        else unitsAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.WeightUnits, R.layout.spinner_item);
                    } else unitsAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.VolumeUnits, R.layout.spinner_item);

                    unitsAdapter.setDropDownViewResource(R.layout.spinner_item);
                    units.setAdapter(unitsAdapter);


                    if(currentFood.weight)
                        currentUnit = unitsMap.get("g");
                    else
                        currentUnit = unitsMap.get("mL");
                    setLabels(carbonLabel, waterLabel, amt);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Categories dropdown
        Spinner categories = (Spinner) findViewById(R.id.Categories);
        ArrayAdapter<String> catAdapter = createAdapter(R.array.categories);
        catAdapter.setDropDownViewResource(R.layout.spinner_item);
        categories.setAdapter(catAdapter);
        categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { // i is the position in the array
                if(i > 0) {
                    ArrayAdapter<String> foodAdapter = createAdapter(foodsArrays[i]);
                    foodAdapter.setDropDownViewResource(R.layout.spinner_item);
                    foods.setAdapter(foodAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




    }

    public String skeleton0(){
        return "Test Skeleton0 Code";
    }

    public String skeleton1() {
       return "Test Skeleton1 Code";

    }
    
    public String skeleton2() {
        return "Test Skeleton2 Code";
    }

    private ArrayAdapter<String> createAdapter(int id) {

        return new ArrayAdapter<String>(this, R.layout.spinner_item, getResources().getStringArray(id)) {
            @Override
            public boolean isEnabled(int position) {
                if(position == 0) return false;
                else return true;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if(position == 0) textView.setTextColor(Color.GRAY);
                else textView.setTextColor(Color.BLACK);
                return view;
            }
        };
    }

    public void initMaps() {
        unitsMap = new HashMap<String, Unit>();
        unitsMap.put("g", new Unit(true, 1.0f));
        unitsMap.put("kg", new Unit(true, 1000.0f));
        unitsMap.put("oz", new Unit(true, 28.35f));
        unitsMap.put("lb", new Unit(true, 453.6f));
        unitsMap.put("mL", new Unit(false, 1.0f));
        unitsMap.put("L", new Unit(false, 1000.0f));
        unitsMap.put("tsp", new Unit(false, 4.93f));
        unitsMap.put("tbsp", new Unit(false, 14.79f));
        unitsMap.put("fl oz", new Unit(false, 29.57f));
        unitsMap.put("cup", new Unit(false, 236.59f));
        unitsMap.put("gallon", new Unit(false, 3785.41f));

        foodsMap = new HashMap<String, Food>();
        foodsMap.put("Allspice", new Food(0, new float[] {1.0f, 2.0f}, new float[] {0.5f, .25f}, new ArrayList<String>(Arrays.asList("Cumin")), true, true ));
        foodsMap.put("Almond Extract", new Food(1, new float[] {1.0f, 2.0f}, new float[] {1.0f, 2.0f}, new ArrayList<String>(Arrays.asList("Vanilla Extract")), true, true ));
        foodsMap.put("Almonds", new Food(2, new float[] {1.0f, 0.0f}, new float[] {1.0f, 0.0f}, new ArrayList<String>(Arrays.asList("Cashews", "Walnuts")), true, false));
        foodsMap.put("Americano", new Food(3, new float[] {0.0f, 1.5f}, new float[] {0.0f, .8f}, new ArrayList<String>(), false, true));
    }

    public void setLabels(TextView carbonLabel, TextView waterLabel, float amt) {
        DecimalFormat f = new DecimalFormat("#.###");

        if(currentUnit.weight) {
            carbonLabel.setText(f.format(currentFood.carbonUsage[0] * amt * currentUnit.scale));
            waterLabel.setText(f.format(currentFood.waterUsage[0] * amt * currentUnit.scale));
        } else {
            carbonLabel.setText(f.format(currentFood.carbonUsage[1] * amt * currentUnit.scale));
            waterLabel.setText(f.format(currentFood.waterUsage[1] * amt * currentUnit.scale));
        }
    }

}