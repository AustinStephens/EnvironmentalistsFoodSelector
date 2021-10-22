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
        TextView foodLabel = (TextView) findViewById(R.id.foodLabel); // Name of Selected Food Label
        TextView carbonLabel = (TextView) findViewById(R.id.carbon); // carbon usage label
        TextView waterLabel = (TextView) findViewById(R.id.water); // water usage label
        TextInputEditText amount = (TextInputEditText) findViewById(R.id.amount); // Amount input textbox
        Spinner units = (Spinner) findViewById(R.id.units); // Units Dropdown

        //Listeners
        amount.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int key, KeyEvent event) {
                if(event.getAction() != KeyEvent.ACTION_DOWN) {
                    view.onKeyDown(key, event); // call regular onKeyDown
                    if(amount.getText().length() != 0 && currentUnit != null) { // if food is selected and amount isnt empty
                        String amtStr = amount.getText().toString();
                        float amt = Float.parseFloat(amtStr);
                        setLabels(carbonLabel, waterLabel, amt);
                    } else if (currentUnit != null){ // for when someone might clear the textbox
                        setLabels(carbonLabel,waterLabel,0);
                    }
                }
                return true;
            }
        });

        units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectItemText = (String) adapterView.getItemAtPosition(i); // gets unit name
                currentUnit = unitsMap.get(selectItemText); // gets Unit object from unitsMap
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
        ArrayAdapter<String> foodAdapter = createAdapter(R.array.AllFoods); // starts off as every food item until category is selected
        foodAdapter.setDropDownViewResource(R.layout.spinner_item);
        foods.setAdapter(foodAdapter);
        foods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { // i is the position in the array
                if(i > 0) { // first position is invalid
                    String selectItemText = (String) adapterView.getItemAtPosition(i);
                    currentFood = foodsMap.get(selectItemText);
                    foodLabel.setText(selectItemText);

                    // If someone puts a number in before selecting a food
                    float amt = 1.0f;
                    if(amount.getText().length() == 0)
                        amount.setText("1");
                    else
                        amt = Float.parseFloat(amount.getText().toString());


                    // gets proper string array of units based on food's unit types
                    ArrayAdapter<CharSequence> unitsAdapter;
                    if(currentFood.weight) {
                        if(currentFood.volume) unitsAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.AllUnits, R.layout.spinner_item);
                        else unitsAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.WeightUnits, R.layout.spinner_item);
                    } else unitsAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.VolumeUnits, R.layout.spinner_item);

                    unitsAdapter.setDropDownViewResource(R.layout.spinner_item);
                    units.setAdapter(unitsAdapter); // populates units dropdown

                    // sets starting unit
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
                if(i > 0) { // first position is invalid
                    // selects the string array resource to populate the foods dropdown with
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

    // Helper Function to create both category and foods spinners, passes in the string array resource ID
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

    // Initialize both Maps with all the information here.
    public void initMaps() {
        unitsMap = new HashMap<String, Unit>();
        // scale for weight units are relative to gram, scale for volume units are relative to mL
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
        // put(String name, new Food(int id, float[2] waterUsage, float[2] carbonUsage, ArrayList<String> similarFoods, boolean weight, boolean volume));
        // if weight or volume is false, make respective index in water and carbon usage 0.0f
        foodsMap.put("Allspice", new Food(0, new float[] {1.0f, 2.0f}, new float[] {0.5f, .25f}, new ArrayList<String>(Arrays.asList("Cumin")), true, true ));
        foodsMap.put("Almond Extract", new Food(1, new float[] {1.0f, 2.0f}, new float[] {1.0f, 2.0f}, new ArrayList<String>(Arrays.asList("Vanilla Extract")), true, true ));
        foodsMap.put("Almonds", new Food(2, new float[] {1.0f, 0.0f}, new float[] {1.0f, 0.0f}, new ArrayList<String>(Arrays.asList("Cashews", "Walnuts")), true, false));
        foodsMap.put("Americano", new Food(3, new float[] {0.0f, 1.5f}, new float[] {0.0f, .8f}, new ArrayList<String>(), false, true));
    }

    // Helper functions to set labels based on currentUnit
    public void setLabels(TextView carbonLabel, TextView waterLabel, float amt) {
        DecimalFormat f = new DecimalFormat("#.###"); // rounds to 3 digits

        // if the unit selected is a unit of volume, use 0 indices
        if(currentUnit.weight) {
            carbonLabel.setText(f.format(currentFood.carbonUsage[0] * amt * currentUnit.scale));
            waterLabel.setText(f.format(currentFood.waterUsage[0] * amt * currentUnit.scale));
        } else { // if the unit selected is a unit of volume, use 1 indices
            carbonLabel.setText(f.format(currentFood.carbonUsage[1] * amt * currentUnit.scale));
            waterLabel.setText(f.format(currentFood.waterUsage[1] * amt * currentUnit.scale));
        }
    }

}