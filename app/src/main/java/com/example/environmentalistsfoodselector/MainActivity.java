package com.example.environmentalistsfoodselector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public int[] foodsArrays = {R.array.BakedFoods, R.array.DairyFoods, R.array.DrinkFoods, R.array.DryFoods, R.array.FruitFoods, R.array.MeatFoods,
            R.array.NutsFoods, R.array.OilsFoods, R.array.ProcessedFoods, R.array.JamsFoods, R.array.SaucesFoods, R.array.SeafoodFoods, R.array.SweetsFoods, R.array.VegetableFoods};

    // create food and unit map object, then get the maps for later use
    public HashMap<String, Food> foodsMap;
    public HashMap<String, Unit> unitsMap;

    public Food currentFood;
    public String currentUnit;
    public float currentAmount = 0.0f;
    public String currentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get screen width
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        setContentView(R.layout.activity_main);

        // create food and unit maps
        foodsMap = FoodsMap.getInstance().getFoodMap();
        unitsMap = UnitsMap.getInstance().getUnitsMap();

        //Widget Vars

        TextView foodLabel = (TextView) findViewById(R.id.foodLabel); // Name of Selected Food Label
        TextView carbonLabel = (TextView) findViewById(R.id.carbon); // carbon usage label
        TextView waterLabel = (TextView) findViewById(R.id.water); // water usage label
        TextInputEditText amount = (TextInputEditText) findViewById(R.id.amount); // Amount input textbox
        Spinner units = (Spinner) findViewById(R.id.units); // Units Dropdown
        Button addItems = (Button) findViewById(R.id.addItem); // Add item Button
        Button itemsPage = (Button) findViewById(R.id.itemsPage); // Go to Added Items Page
        Button bestFoods = (Button) findViewById(R.id.bestButton); // Best Foods Button
        Button worstFoods = (Button) findViewById(R.id.worstButton); // Worst Foods Button
        RecyclerView recFoodsList = (RecyclerView) findViewById(R.id.similarFoods); // Suggested Foods List
        recFoodsList.setLayoutManager(new LinearLayoutManager(this));

        //set width
        bestFoods.setWidth(screenWidth/2);
        worstFoods.setWidth(screenWidth/2);

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
                        currentAmount = amt;
                    } else if (currentUnit != null){ // for when someone might clear the textbox
                        setLabels(carbonLabel,waterLabel,0);
                        currentAmount = 0;
                    }
                }
                return true;
            }
        });


        units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectItemText = (String) adapterView.getItemAtPosition(i); // gets unit name
                currentUnit = selectItemText; // gets Unit object from unitsMap
                if(amount.getText().length() != 0) { // if food is selected and amount isnt empty
                    String amtStr = amount.getText().toString();
                    currentAmount = Float.parseFloat(amtStr);
                    setLabels(carbonLabel, waterLabel, currentAmount);
                } else if (currentUnit != null){ // for when someone might clear the textbox
                    setLabels(carbonLabel,waterLabel,0);
                    currentAmount = 0.0f;
                }
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
                    currentName = selectItemText;
                    currentFood = foodsMap.get(selectItemText);
                    foodLabel.setText(selectItemText);

                    // If someone puts a number in before selecting a food
                    currentAmount = 1.0f;
                    if(Objects.requireNonNull( amount.getText() ).length() == 0)
                        amount.setText("1");
                    else
                        currentAmount = Float.parseFloat(amount.getText().toString());


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
                        currentUnit = "g";
                    else
                        currentUnit = "mL";
                    setLabels(carbonLabel, waterLabel, currentAmount);

                    //Array of food objects for similar foods
                    ArrayList<Food> foodsArray = new ArrayList<>();

                    // if we don't have any, we pass in an array of just the current food, it's a work around for getting the
                    // excellent message printed into the list
                    if(currentFood.similarFoods.isEmpty()) {
                        foodsArray.add(currentFood);
                    }
                    // otherwise, we look up the food objects for each string in the similarfoods array and store them
                    // into a parallel array
                    for(int index = 0; index < currentFood.similarFoods.size(); index++) {
                        foodsArray.add(foodsMap.get(currentFood.similarFoods.get(index)));
                    }

                    // set the new lists to the recycler view
                    RecommendedFoodsViewAdapter adapter = new RecommendedFoodsViewAdapter(view.getContext(), foodsArray, currentFood.similarFoods, currentFood);
                    recFoodsList.setAdapter(adapter);
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
                    currentFood = null;
                    currentName = null;
                    ArrayAdapter<String> foodAdapter = createAdapter(foodsArrays[i-1]);
                    foodAdapter.setDropDownViewResource(R.layout.spinner_item);
                    foods.setAdapter(foodAdapter);
                    RecommendedFoodsViewAdapter adapter = new RecommendedFoodsViewAdapter(view.getContext(), new ArrayList<Food>(), new ArrayList<String>(), currentFood);
                    recFoodsList.setAdapter(adapter);
                    carbonLabel.setText("0");
                    waterLabel.setText("0");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Adds Food, Unit, and Amount to our singleton arraylist class
                if(currentName != null && currentUnit  != null && currentAmount != 0.0f)
                    ItemsAdded.getInstance().addItem(new AddedItem(currentName, currentFood, currentUnit, currentAmount));
            }
        });

        itemsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ItemsListActivity.class);
                startActivity(intent);
            }
        });

        bestFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BestFoodsActivity.class);
                startActivity(intent);
            }
        });

        worstFoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WorstFoodsActivity.class);
                startActivity(intent);
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
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if(position == 0) textView.setTextColor(Color.GRAY);
                else textView.setTextColor(Color.BLACK);
                return view;
            }
        };
    }

    // Helper functions to set labels based on currentUnit
    public void setLabels(TextView carbonLabel, TextView waterLabel, float amt) {
        DecimalFormat f = new DecimalFormat("#.###"); // rounds to 3 digits

        // if the unit selected is a unit of volume, use 0 indices
        Unit unit = unitsMap.get(currentUnit);
        if(unit.weight) {
            carbonLabel.setText(f.format(currentFood.carbonUsage[0] * amt * unit.scale));
            if(currentFood.waterUsage[0] == 0.0f)
                waterLabel.setText("N/A");
            else
                waterLabel.setText(f.format(currentFood.waterUsage[0] * amt * unit.scale));
        } else { // if the unit selected is a unit of volume, use 1 indices
            carbonLabel.setText(f.format(currentFood.carbonUsage[1] * amt * unit.scale));
            if(currentFood.waterUsage[1] == 0.0f)
                waterLabel.setText("N/A");
            else
                waterLabel.setText(f.format(currentFood.waterUsage[1] * amt * unit.scale));
        }
    }


}