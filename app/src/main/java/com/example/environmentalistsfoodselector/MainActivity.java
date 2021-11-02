package com.example.environmentalistsfoodselector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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
    public MyRecyclerViewAdapter rvadapter;

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
        RecyclerView recFoodsList = (RecyclerView) findViewById(R.id.similarFoods);
        recFoodsList.setLayoutManager(new LinearLayoutManager(this));

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
                if(amount.getText().length() != 0) { // if food is selected and amount isnt empty
                    String amtStr = amount.getText().toString();
                    float amt = Float.parseFloat(amtStr);
                    setLabels(carbonLabel, waterLabel, amt);
                } else if (currentUnit != null){ // for when someone might clear the textbox
                    setLabels(carbonLabel,waterLabel,0);
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

                    //TODO: POPULATE RECOMMENDED FOODS
                    /*RecommenedFoodsViewAdapter adapter = new RecommenedFoodsViewAdapter(this, foodsArray, namesArray, currentFood);
                    recFoodsList.setAdapter(adapter);*/

                    if(currentFood!= null && currentFood.similarFoods!= null && currentFood.similarFoods.size() > 0) {
                        System.out.println(currentFood.similarFoods);
                        initRecycler();
                    }
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
                    ArrayAdapter<String> foodAdapter = createAdapter(foodsArrays[i-1]);
                    foodAdapter.setDropDownViewResource(R.layout.spinner_item);
                    foods.setAdapter(foodAdapter);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button addItems = (Button) findViewById(R.id.button);
        addItems.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            if(currentFood.carbonUsage[0]<2 && currentFood.waterUsage[0]<2){
            // inflate the layout of the popup window
            LayoutInflater inflater = (LayoutInflater)
                    getSystemService(LAYOUT_INFLATER_SERVICE);
            View popupView = inflater.inflate(R.layout.popupwindow, null);

            // create the popup window
            int width = LinearLayout.LayoutParams.WRAP_CONTENT;
            int height = LinearLayout.LayoutParams.WRAP_CONTENT;
            boolean focusable = true; // lets taps outside the popup also dismiss it
            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window token
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

            // dismiss the popup window when touched
            popupView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    popupWindow.dismiss();
                    return true;
                }
            });
        }}});
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

    // Helper functions to set labels based on currentUnit
    public void setLabels(TextView carbonLabel, TextView waterLabel, float amt) {
        DecimalFormat f = new DecimalFormat("#.###"); // rounds to 3 digits

        // if the unit selected is a unit of volume, use 0 indices
        if(currentUnit.weight) {
            carbonLabel.setText(f.format(currentFood.carbonUsage[0] * amt * currentUnit.scale));
            if(currentFood.waterUsage[0] == 0.0f)
                waterLabel.setText("N/A");
            else
                waterLabel.setText(f.format(currentFood.waterUsage[0] * amt * currentUnit.scale));
        } else { // if the unit selected is a unit of volume, use 1 indices
            carbonLabel.setText(f.format(currentFood.carbonUsage[1] * amt * currentUnit.scale));
            if(currentFood.waterUsage[1] == 0.0f)
                waterLabel.setText("N/A");
            else
                waterLabel.setText(f.format(currentFood.waterUsage[1] * amt * currentUnit.scale));
        }
    }

    public void initRecycler(){
        RecyclerView rv = findViewById(R.id.similarFoods);
        rv.setHasFixedSize(true);
        TextView tvAdd, tvUpdate;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        if(currentFood!= null && currentFood.similarFoods!= null && currentFood.similarFoods.size() > 0) {
            rvadapter = new MyRecyclerViewAdapter(this,currentFood.similarFoods);
        }
        else{
            rvadapter = new MyRecyclerViewAdapter(this,new ArrayList<String>());
        }

        rv.setAdapter(rvadapter);
    }

    public static class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;
        private ItemClickListener mClickListener;

        // data is passed into the constructor
        MyRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String animal = mData.get(position);
            holder.myTextView.setText(animal);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView myTextView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.tvAnimalName);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        // convenience method for getting data at click position
        String getItem(int id) {
            return mData.get(id);
        }

        // allows clicks events to be caught
        void setClickListener(ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }
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
        foodsMap.put("Bagel", new Food(0, new float[] {0.0f, 0.0f}, new float[] {1.21f, 0.0f}, new ArrayList<String>(Arrays.asList("Crepe", "Waffle")), true, false));
        foodsMap.put("Baguette", new Food(1, new float[] {0.0f, 0.0f}, new float[] {1.3f, 0.0f}, new ArrayList<String>(Arrays.asList("Bagel", "Bread")), true, false));
        foodsMap.put("Bread", new Food(2, new float[] {0.0f, 0.0f}, new float[] {1.08f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Cake", new Food(3, new float[] {0.0f, 0.0f}, new float[] {3.06f, 0.0f}, new ArrayList<String>(Arrays.asList("Fruit Pie", "Crumpet")), true, false));
        foodsMap.put("Cheesecake", new Food(4, new float[] {0.0f, 0.0f}, new float[] {5.48f, 0.0f}, new ArrayList<String>(Arrays.asList("Cake", "Fruit Pie")), true, false));
        foodsMap.put("Crepe", new Food(5, new float[] {0.0f, 0.0f}, new float[] {0.71f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Crumpet", new Food(6, new float[] {0.0f, 0.0f}, new float[] {1.22f, 0.0f}, new ArrayList<String>(Arrays.asList("Crepe")), true, false));
        foodsMap.put("Doughnut", new Food(7, new float[] {0.0f, 0.0f}, new float[] {6.58f, 0.0f}, new ArrayList<String>(Arrays.asList("Pancake", "Waffle")), true, false));
        foodsMap.put("Fruit Pie", new Food(8, new float[] {0.0f, 0.0f}, new float[] {1.58f, 0.0f}, new ArrayList<String>(Arrays.asList("Crepe", "Crumpet")), true, false));
        foodsMap.put("Muffin", new Food(9, new float[] {0.0f, 0.0f}, new float[] {6.58f, 0.0f}, new ArrayList<String>(Arrays.asList("Bagel", "Crepe")), true, false));
        foodsMap.put("Naan Bread", new Food(10, new float[] {0.0f, 0.0f}, new float[] {1.21f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Pancake", new Food(11, new float[] {0.0f, 0.0f}, new float[] {.71f, 0.0f}, new ArrayList<String>(Arrays.asList("Waffle")), true, false));
        foodsMap.put("Pita Bread", new Food(12, new float[] {0.0f, 0.0f}, new float[] {1.21f, 0.0f}, new ArrayList<String>(Arrays.asList("Bread", "Tortilla")), true, false));
        foodsMap.put("Scone", new Food(13, new float[] {0.0f, 0.0f}, new float[] {3.06f, 0.0f}, new ArrayList<String>(Arrays.asList("Crepe", "Crumpet")), true, false));
        foodsMap.put("Tortilla", new Food(14, new float[] {0.0f, 0.0f}, new float[] {1.08f, 0.0f}, new ArrayList<String>(Arrays.asList("Bread")), true, false));
        foodsMap.put("Waffle", new Food(15, new float[] {0.0f, 0.0f}, new float[] {.71f, 0.0f}, new ArrayList<String>(Arrays.asList("Pancake")), true, false));
        foodsMap.put("Butter", new Food(16, new float[] {1.47f, 0.0f}, new float[] {9.79f, 0.0f}, new ArrayList<String>(Arrays.asList("Cheese", "Sour Cream")), true, false));
        foodsMap.put("Buttermilk", new Food(17, new float[] {0.0f, 0.0f}, new float[] {1.1f, 0.0f}, new ArrayList<String>(Arrays.asList("Milk - Almond", "Milk - Soy")), true, false));
        foodsMap.put("Cheese", new Food(18, new float[] {0.0f, 0.0f}, new float[] {8.8f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Ice Cream", new Food(19, new float[] {0.0f, 0.0f}, new float[] {3.8f, 0.0f}, new ArrayList<String>(Arrays.asList("Yogurt")), true, false));
        foodsMap.put("Milk", new Food(20, new float[] {0.0f, 0.0f}, new float[] {1.76f, 0.0f}, new ArrayList<String>(Arrays.asList("Milk - Almond", "Milk - Soy")), true, false));
        foodsMap.put("Milk - Almond", new Food(21, new float[] {0.0f, 0.0f}, new float[] {0.57f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Milk - Soy", new Food(22, new float[] {0.0f, 0.0f}, new float[] {0.78f, 0.0f}, new ArrayList<String>(Arrays.asList("Milk - Almond")), true, false));
        foodsMap.put("Sour Cream", new Food(23, new float[] {0.0f, 0.0f}, new float[] {2.5f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Yogurt", new Food(24, new float[] {0.0f, 0.0f}, new float[] {2.2f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Americano", new Food(25, new float[] {0.0f, 0.0f}, new float[] {0.0f, 0.71f}, new ArrayList<String>(Arrays.asList("Bottled Water", "Soft Drink")), false, true));
        foodsMap.put("Apple Juice", new Food(26, new float[] {0.0f, 0.0f}, new float[] {0.0f, 2.44f}, new ArrayList<String>(Arrays.asList("Orange Juice", "Soft Drink")), false, true));
        foodsMap.put("Beer", new Food(27, new float[] {0.0f, 0.0f}, new float[] {0.0f, 0.88f}, new ArrayList<String>(), false, true));
        foodsMap.put("Black Tea", new Food(28, new float[] {0.0f, 2.11f}, new float[] {0.0f, 0.61f}, new ArrayList<String>(), false, true));
        foodsMap.put("Bottled Water", new Food(29, new float[] {0.0f, 0.0f}, new float[] {0.0f, 0.32f}, new ArrayList<String>(), false, true));
        foodsMap.put("Brandy", new Food(30, new float[] {0.0f, 0.0f}, new float[] {0.0f, 2.32f}, new ArrayList<String>(Arrays.asList("Wine", "Beer")), false, true));
        foodsMap.put("Cappuccino", new Food(31, new float[] {0.0f, 0.0f}, new float[] {0.0f, 1.61f}, new ArrayList<String>(Arrays.asList("Espresso", "Americano")), false, true));
        foodsMap.put("Espresso", new Food(32, new float[] {0.0f, 0.0f}, new float[] {0.0f, 0.7f}, new ArrayList<String>(Arrays.asList("Black Tea", "Herbal Tea")), false, true));
        foodsMap.put("Fruit Juice", new Food(33, new float[] {0.0f, 0.0f}, new float[] {0.0f, 1.71f}, new ArrayList<String>(Arrays.asList("Soft Drink", "Green Tea")), false, true));
        foodsMap.put("Grapefruit Juice", new Food(34, new float[] {0.0f, 0.0f}, new float[] {0.0f, 2.06f}, new ArrayList<String>(Arrays.asList("Bottled Water", "Orange Juice")), false, true));
        foodsMap.put("Green Tea", new Food(35, new float[] {0.0f, 2.11f}, new float[] {0.0f, 0.61f}, new ArrayList<String>(Arrays.asList("Bottled Water")), false, true));
        foodsMap.put("Herbal Tea", new Food(36, new float[] {0.0f, 2.11f}, new float[] {0.0f, 0.61f}, new ArrayList<String>(Arrays.asList("Bottled Water")), false, true));
        foodsMap.put("Hot Chocolate", new Food(37, new float[] {0.0f, 0.0f}, new float[] {0.0f, 2.73f}, new ArrayList<String>(Arrays.asList("Herbal Tea", "Cappuccino")), false, true));
        foodsMap.put("Instant Coffee", new Food(38, new float[] {0.0f, 5.29f}, new float[] {0.0f, 0.71f}, new ArrayList<String>(Arrays.asList("Bottled Water", "Soft Drink")), false, true));
        foodsMap.put("Orange Juice", new Food(39, new float[] {0.0f, 0.21f}, new float[] {0.0f, 2.0f}, new ArrayList<String>(Arrays.asList("Bottled Water", "Herbal Tea")), false, true));
        foodsMap.put("Pineapple Juice", new Food(40, new float[] {0.0f, 0.0f}, new float[] {0.0f, 2.52f}, new ArrayList<String>(Arrays.asList("Orange Juice", "Green Tea")), false, true));
        foodsMap.put("Rum", new Food(41, new float[] {0.0f, 0.0f}, new float[] {0.0f, 2.32f}, new ArrayList<String>(Arrays.asList("Wine", "Beer")), false, true));
        foodsMap.put("Soft Drink", new Food(42, new float[] {0.0f, 0.0f}, new float[] {0.0f, 0.62f}, new ArrayList<String>(Arrays.asList("Bottled Water")), false, true));
        foodsMap.put("Tequila", new Food(43, new float[] {0.0f, 0.0f}, new float[] {0.0f, 2.32f}, new ArrayList<String>(Arrays.asList("Wine", "Beer")), false, true));
        foodsMap.put("Vodka", new Food(44, new float[] {0.0f, 0.0f}, new float[] {0.0f, 2.32f}, new ArrayList<String>(Arrays.asList("Wine", "Beer")), false, true));
        foodsMap.put("Whiskey", new Food(45, new float[] {0.0f, 0.0f}, new float[] {0.0f, 2.32f}, new ArrayList<String>(Arrays.asList("Wine", "Beer")), false, true));
        foodsMap.put("Wine", new Food(46, new float[] {0.0f, 0.54f}, new float[] {0.0f, 1.37f}, new ArrayList<String>(Arrays.asList("Beer", "Soft Drink")), false, true));
        foodsMap.put("Basil", new Food(47, new float[] {0.0f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Bay Leaves", new Food(48, new float[] {0.0f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Black Beans", new Food(49, new float[] {0.0f, 0.0f}, new float[] {0.85f, 0.0f}, new ArrayList<String>(Arrays.asList("Lentils")), true, false));
        foodsMap.put("Black Pepper", new Food(50, new float[] {1.90f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(Arrays.asList("Chilli Power", "Tumeric")), true, false));
        foodsMap.put("Chilli Powder", new Food(51, new float[] {0.0f, 0.0f}, new float[] {1.0f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Chives", new Food(52, new float[] {0.0f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Cinnamon", new Food(53, new float[] {4.33f, 0.0f}, new float[] {1.0f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Cumin", new Food(54, new float[] {0.0f, 0.0f}, new float[] {1.0f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Dill", new Food(55, new float[] {0.0f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Granola", new Food(56, new float[] {0.0f, 0.0f}, new float[] {4.03f, 0.0f}, new ArrayList<String>(Arrays.asList("Oats")), true, false));
        foodsMap.put("Kidney Beans", new Food(57, new float[] {1.15f, 0.0f}, new float[] {0.85f, 0.0f}, new ArrayList<String>(Arrays.asList("Lentils")), true, false));
        foodsMap.put("Lentils", new Food(58, new float[] {1.26f, 0.0f}, new float[] {0.67f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Noodles", new Food(59, new float[] {0.0f, 0.0f}, new float[] {1.37f, 0.0f}, new ArrayList<String>(Arrays.asList("Rice - White", "Rice - Brown")), true, false));
        foodsMap.put("Nutmeg", new Food(60, new float[] {8.93f, 0.0f}, new float[] {1.0f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Oats", new Food(61, new float[] {0.43f, 0.0f}, new float[] {0.78f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Oregano", new Food(62, new float[] {0.0f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Parsley", new Food(63, new float[] {0.0f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Pasta", new Food(64, new float[] {0.38f, 0.0f}, new float[] {1.37f, 0.0f}, new ArrayList<String>(Arrays.asList("Rice - White", "Rice - Brown")), true, false));
        foodsMap.put("Rice - White", new Food(65, new float[] {0.0f, 0.0f}, new float[] {1.35f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Rice - Brown", new Food(66, new float[] {0.43f, 0.0f}, new float[] {1.35f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Rosemary", new Food(67, new float[] {0.0f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Salt", new Food(68, new float[] {0.0f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Soybeans", new Food(69, new float[] {0.59f, 0.0f}, new float[] {2.0f, 0.0f}, new ArrayList<String>(Arrays.asList("Lentils", "Kidney Beans")), true, false));
        foodsMap.put("Spaghetti", new Food(70, new float[] {0.0f, 0.0f}, new float[] {1.37f, 0.0f}, new ArrayList<String>(Arrays.asList("Rice - White", "Rice - Brown")), true, false));
        foodsMap.put("Sugar - White", new Food(71, new float[] {0.34f, 0.0f}, new float[] {0.75f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Sugar - Brown", new Food(72, new float[] {0.34f, 0.0f}, new float[] {0.75f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Thyme", new Food(73, new float[] {0.0f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Tumeric", new Food(74, new float[] {0.44f, 0.0f}, new float[] {1.0f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Vanilla", new Food(75, new float[] {0.0f, 0.0f}, new float[] {0.6f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Wheat", new Food(76, new float[] {0.37f, 0.0f}, new float[] {0.72f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Apples", new Food(77, new float[] {0.16f, 0.0f}, new float[] {0.9f, 0.0f}, new ArrayList<String>(Arrays.asList("Apricot", "Grapefruit")), true, false));
        foodsMap.put("Apricot", new Food(78, new float[] {0.20f, 0.0f}, new float[] {0.43f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Avocados", new Food(79, new float[] {0.25f, 0.0f}, new float[] {2.4f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Bananas", new Food(80, new float[] {0.19f, 0.0f}, new float[] {1.3f, 0.0f}, new ArrayList<String>(Arrays.asList("Melon", "Pears")), true, false));
        foodsMap.put("Blackberries", new Food(81, new float[] {0.09f, 0.0f}, new float[] {2.86f, 0.0f}, new ArrayList<String>(Arrays.asList("Grapes", "Strawberries")), true, false));
        foodsMap.put("Blueberries", new Food(82, new float[] {0.1f, 0.0f}, new float[] {2.86f, 0.0f}, new ArrayList<String>(Arrays.asList("Strawberries", "Raspberries")), true, false));
        foodsMap.put("Cherries", new Food(83, new float[] {0.28f, 0.0f}, new float[] {2.86f, 0.0f}, new ArrayList<String>(Arrays.asList("Grapes", "Raspberries")), true, false));
        foodsMap.put("Cranberries", new Food(84, new float[] {0.0f, 0.0f}, new float[] {2.86f, 0.0f}, new ArrayList<String>(Arrays.asList("Plums", "Raisins")), true, false));
        foodsMap.put("Dates", new Food(85, new float[] {0.27f, 0.0f}, new float[] {2.1f, 0.0f}, new ArrayList<String>(Arrays.asList("Nectarine", "Plums")), true, false));
        foodsMap.put("Grapefruit", new Food(86, new float[] {0.11f, 0.0f}, new float[] {0.8f, 0.0f}, new ArrayList<String>(Arrays.asList("Kiwi", "Apricot")), true, false));
        foodsMap.put("Grapes", new Food(87, new float[] {0.12f, 0.0f}, new float[] {2.3f, 0.0f}, new ArrayList<String>(Arrays.asList("Strawberries", "Plums")), true, false));
        foodsMap.put("Kiwi", new Food(88, new float[] {0.09f, 0.0f}, new float[] {0.62f, 0.0f}, new ArrayList<String>(Arrays.asList("Apricot")), true, false));
        foodsMap.put("Lemons", new Food(89, new float[] {0.13f, 0.0f}, new float[] {1.9f, 0.0f}, new ArrayList<String>(Arrays.asList("Limes", "Apricot")), true, false));
        foodsMap.put("Limes", new Food(90, new float[] {0.13f, 0.0f}, new float[] {1.89f, 0.0f}, new ArrayList<String>(Arrays.asList("Mandarins", "Grapefruit")), true, false));
        foodsMap.put("Mandarins", new Food(91, new float[] {0.14f, 0.0f}, new float[] {1.1f, 0.0f}, new ArrayList<String>(Arrays.asList("Grapefruit", "Pears")), true, false));
        foodsMap.put("Mango", new Food(92, new float[] {0.38f, 0.0f}, new float[] {4.4f, 0.0f}, new ArrayList<String>(Arrays.asList("Nectarine", "Oranges")), true, false));
        foodsMap.put("Melon", new Food(93, new float[] {0.04f, 0.0f}, new float[] {0.9f, 0.0f}, new ArrayList<String>(Arrays.asList("Apricot")), true, false));
        foodsMap.put("Nectarine", new Food(94, new float[] {0.17f, 0.0f}, new float[] {1.9f, 0.0f}, new ArrayList<String>(Arrays.asList("Pears", "Pineapples")), true, false));
        foodsMap.put("Olives", new Food(95, new float[] {0.72f, 0.0f}, new float[] {0.45f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Oranges", new Food(96, new float[] {0.12f, 0.0f}, new float[] {1.9f, 0.0f}, new ArrayList<String>(Arrays.asList("Mandarins", "Melon")), true, false));
        foodsMap.put("Papaya", new Food(97, new float[] {0.12f, 0.0f}, new float[] {4.4f, 0.0f}, new ArrayList<String>(Arrays.asList("Grapefruit", "Apricot")), true, false));
        foodsMap.put("Peaches", new Food(98, new float[] {0.17f, 0.0f}, new float[] {1.9f, 0.0f}, new ArrayList<String>(Arrays.asList("Pears", "Plums")), true, false));
        foodsMap.put("Pears", new Food(99, new float[] {0.19f, 0.0f}, new float[] {1.03f, 0.0f}, new ArrayList<String>(Arrays.asList("Apricot", "Kiwi")), true, false));
        foodsMap.put("Pineapples", new Food(100, new float[] {0.06f, 0.0f}, new float[] {1.7f, 0.0f}, new ArrayList<String>(Arrays.asList("Apples", "Plums")), true, false));
        foodsMap.put("Plums", new Food(101, new float[] {0.46f, 0.0f}, new float[] {1.13f, 0.0f}, new ArrayList<String>(Arrays.asList("Apricot", "Kiwi")), true, false));
        foodsMap.put("Raisins", new Food(102, new float[] {0.0f, 0.0f}, new float[] {2.1f, 0.0f}, new ArrayList<String>(Arrays.asList("Peaches", "Pears")), true, false));
        foodsMap.put("Raspberries", new Food(103, new float[] {0.09f, 0.0f}, new float[] {2.58f, 0.0f}, new ArrayList<String>(Arrays.asList("Strawberries", "Plums")), true, false));
        foodsMap.put("Strawberries", new Food(104, new float[] {0.06f, 0.0f}, new float[] {2.24f, 0.0f}, new ArrayList<String>(Arrays.asList("Raisins", "Bananas")), true, false));
        foodsMap.put("Tomatoes", new Food(105, new float[] {0.03f, 0.0f}, new float[] {2.9f, 0.0f}, new ArrayList<String>(Arrays.asList("Olives", "Apples")), true, false));
        foodsMap.put("Bacon", new Food(106, new float[] {0.0f, 0.0f}, new float[] {6.63f, 0.0f}, new ArrayList<String>(Arrays.asList("Chicken", "Tofu")), true, false));
        foodsMap.put("Beef", new Food(107, new float[] {4.07f, 0.0f}, new float[] {43.33f, 0.0f}, new ArrayList<String>(Arrays.asList("Lamb", "Duck")), true, false));
        foodsMap.put("Chicken", new Food(108, new float[] {1.14f, 0.0f}, new float[] {4.71f, 0.0f}, new ArrayList<String>(Arrays.asList("Tofu")), true, false));
        foodsMap.put("Duck", new Food(109, new float[] {0.0f, 0.0f}, new float[] {5.06f, 0.0f}, new ArrayList<String>(Arrays.asList("Chicken", "Tofu")), true, false));
        foodsMap.put("Goat", new Food(110, new float[] {2.31f, 0.0f}, new float[] {35.71f, 0.0f}, new ArrayList<String>(Arrays.asList("Lamb", "Rabbit")), true, false));
        foodsMap.put("Goose", new Food(111, new float[] {0.0f, 0.0f}, new float[] {5.06f, 0.0f}, new ArrayList<String>(Arrays.asList("Chicken", "Tofu")), true, false));
        foodsMap.put("Ham", new Food(112, new float[] {0.0f, 0.0f}, new float[] {6.63f, 0.0f}, new ArrayList<String>(Arrays.asList("Turkey", "Chicken")), true, false));
        foodsMap.put("Lamb", new Food(113, new float[] {0.0f, 0.0f}, new float[] {20.83f, 0.0f}, new ArrayList<String>(Arrays.asList("Rabbit", "Goose")), true, false));
        foodsMap.put("Pancetta", new Food(114, new float[] {0.0f, 0.0f}, new float[] {6.63f, 0.0f}, new ArrayList<String>(Arrays.asList("Turkey", "Chicken")), true, false));
        foodsMap.put("Pork", new Food(115, new float[] {1.58f, 0.0f}, new float[] {6.63f, 0.0f}, new ArrayList<String>(Arrays.asList("Duck", "Goose")), true, false));
        foodsMap.put("Rabbit", new Food(116, new float[] {0.0f, 0.0f}, new float[] {11.02f, 0.0f}, new ArrayList<String>(Arrays.asList("Turkey", "Ham")), true, false));
        foodsMap.put("Steak", new Food(117, new float[] {4.07f, 0.0f}, new float[] {43.33f, 0.0f}, new ArrayList<String>(Arrays.asList("Lamb", "Rabbit")), true, false));
        foodsMap.put("Tofu", new Food(118, new float[] {0.0f, 0.0f}, new float[] {1.85f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Turkey", new Food(119, new float[] {0.0f, 0.0f}, new float[] {6.45f, 0.0f}, new ArrayList<String>(Arrays.asList("Duck", "Chicken")), true, false));
        foodsMap.put("Veal", new Food(120, new float[] {0.0f, 0.0f}, new float[] {18.94f, 0.0f}, new ArrayList<String>(Arrays.asList("Pork", "Ham")), true, false));
        foodsMap.put("Venison", new Food(121, new float[] {0.0f, 0.0f}, new float[] {20.99f, 0.0f}, new ArrayList<String>(Arrays.asList("Veal", "Goose")), true, false));
        foodsMap.put("Almonds", new Food(122, new float[] {2.70f, 0.0f}, new float[] {2.85f, 0.0f}, new ArrayList<String>(Arrays.asList("Cashews", "Chestnuts")), true, false));
        foodsMap.put("Cashews", new Food(123, new float[] {3.74f, 0.0f}, new float[] {2.1f, 0.0f}, new ArrayList<String>(Arrays.asList("Chestnuts", "Peanuts")), true, false));
        foodsMap.put("Chestnuts", new Food(124, new float[] {0.71f, 0.0f}, new float[] {1.45f, 0.0f}, new ArrayList<String>(Arrays.asList("Peanuts")), true, false));
        foodsMap.put("Hazelnuts - Choclate", new Food(125, new float[] {2.22f, 0.0f}, new float[] {3.45f, 0.0f}, new ArrayList<String>(Arrays.asList("Cashews", "Mixed Nuts")), true, false));
        foodsMap.put("Mixed Nuts", new Food(126, new float[] {0.0f, 0.0f}, new float[] {2.05f, 0.0f}, new ArrayList<String>(Arrays.asList("Chestnuts", "Peanuts")), true, false));
        foodsMap.put("Peanuts", new Food(127, new float[] {0.0f, 0.0f}, new float[] {1.35f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Pecans", new Food(128, new float[] {0.0f, 0.0f}, new float[] {2.15f, 0.0f}, new ArrayList<String>(Arrays.asList("Cashews", "Chestnuts")), true, false));
        foodsMap.put("Pistachio", new Food(129, new float[] {0.90f, 0.0f}, new float[] {2.35f, 0.0f}, new ArrayList<String>(Arrays.asList("Pecans", "Cashews")), true, false));
        foodsMap.put("Pumpkin Seeds", new Food(130, new float[] {0.0f, 0.0f}, new float[] {4.2f, 0.0f}, new ArrayList<String>(Arrays.asList("Sunflower Seeds")), true, false));
        foodsMap.put("Sesame Seeds", new Food(131, new float[] {2.46f, 0.0f}, new float[] {4.2f, 0.0f}, new ArrayList<String>(Arrays.asList("Sunflower Seeds")), true, false));
        foodsMap.put("Sunflower Seeds", new Food(132, new float[] {0.88f, 0.0f}, new float[] {1.0f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Walnuts", new Food(133, new float[] {1.54f, 0.0f}, new float[] {2.15f, 0.0f}, new ArrayList<String>(Arrays.asList("Peanuts", "Cashews")), true, false));
        foodsMap.put("Coconut Oil", new Food(134, new float[] {1.30f, 0.0f}, new float[] {2.43f, 0.0f}, new ArrayList<String>(Arrays.asList("Soybean Oil", "Corn Oil")), true, false));
        foodsMap.put("Corn Oil", new Food(135, new float[] {0.0f, 0.0f}, new float[] {1.21f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Olive Oil", new Food(136, new float[] {3.44f, 0.0f}, new float[] {2.5f, 0.0f}, new ArrayList<String>(Arrays.asList("Coconut Oil", "Soybean Oil")), true, false));
        foodsMap.put("Palm Oil", new Food(137, new float[] {1.39f, 0.0f}, new float[] {6.14f, 0.0f}, new ArrayList<String>(Arrays.asList("Vegetable Oil", "Peanut Oil")), true, false));
        foodsMap.put("Peanut Oil", new Food(138, new float[] {0.0f, 0.0f}, new float[] {3.64f, 0.0f}, new ArrayList<String>(Arrays.asList("Sunflower Oil", "Coconut Oil")), true, false));
        foodsMap.put("Rapeseed Oil", new Food(139, new float[] {0.94f, 0.0f}, new float[] {2.43f, 0.0f}, new ArrayList<String>(Arrays.asList("Soybean Oil", "Corn Oil")), true, false));
        foodsMap.put("Soybean Oil", new Food(140, new float[] {1.16f, 0.0f}, new float[] {1.57f, 0.0f}, new ArrayList<String>(Arrays.asList("Corn Oil")), true, false));
        foodsMap.put("Sunflower Oil", new Food(141, new float[] {1.74f, 0.0f}, new float[] {2.57f, 0.0f}, new ArrayList<String>(Arrays.asList("Soybean Oil", "Olive Oil")), true, false));
        foodsMap.put("Vegetable Oil", new Food(142, new float[] {0.0f, 0.0f}, new float[] {3.21f, 0.0f}, new ArrayList<String>(Arrays.asList("Sunflower Oil", "Rapeseed Oil")), true, false));
        foodsMap.put("Baked Beans", new Food(143, new float[] {0.0f, 0.0f}, new float[] {2.47f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Chips", new Food(144, new float[] {0.0f, 0.0f}, new float[] {3.83f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Custard", new Food(145, new float[] {0.0f, 0.0f}, new float[] {2.45f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Fisherman Pie", new Food(146, new float[] {0.0f, 0.0f}, new float[] {7.78f, 0.0f}, new ArrayList<String>(Arrays.asList("Pizza", "Pork Roast")), true, false));
        foodsMap.put("Pizza", new Food(147, new float[] {0.0f, 0.0f}, new float[] {3.84f, 0.0f}, new ArrayList<String>(Arrays.asList("Baked Beans")), true, false));
        foodsMap.put("Pork Roast", new Food(148, new float[] {0.0f, 0.0f}, new float[] {5.83f, 0.0f}, new ArrayList<String>(Arrays.asList("Baked Beans", "Pizza")), true, false));
        foodsMap.put("Tomato Paste", new Food(149, new float[] {0.13f, 0.0f}, new float[] {4.75f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Chocolate Spread", new Food(150, new float[] {0.0f, 0.0f}, new float[] {4.38f, 0.0f}, new ArrayList<String>(Arrays.asList("Peanut Butter", "Honey")), true, false));
        foodsMap.put("Golden Syrup", new Food(151, new float[] {0.0f, 0.0f}, new float[] {0.64f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Hazelnut Paste", new Food(152, new float[] {0.0f, 0.0f}, new float[] {1.28f, 0.0f}, new ArrayList<String>(Arrays.asList("Maple Syrup", "Treacle")), true, false));
        foodsMap.put("Hazelnut Spread", new Food(153, new float[] {0.0f, 0.0f}, new float[] {2.7f, 0.0f}, new ArrayList<String>(Arrays.asList("Hazelnut Paste", "Honey")), true, false));
        foodsMap.put("Honey", new Food(154, new float[] {0.0f, 0.0f}, new float[] {1.65f, 0.0f}, new ArrayList<String>(Arrays.asList("Golden Syrup", "Treacle")), true, false));
        foodsMap.put("Maple Syrup", new Food(155, new float[] {0.34f, 0.0f}, new float[] {0.64f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Marmalade", new Food(156, new float[] {0.0f, 0.0f}, new float[] {2.15f, 0.0f}, new ArrayList<String>(Arrays.asList("Honey", "Golden Syrup")), true, false));
        foodsMap.put("Peanut Butter", new Food(157, new float[] {0.0f, 0.0f}, new float[] {2.1f, 0.0f}, new ArrayList<String>(Arrays.asList("Honey", "Hazelnut Paste")), true, false));
        foodsMap.put("Treacle", new Food(158, new float[] {0.0f, 0.0f}, new float[] {0.64f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Apple Sauce", new Food(159, new float[] {0.0f, 0.0f}, new float[] {2.15f, 0.0f}, new ArrayList<String>(Arrays.asList("Chutney", "Hummus")), true, false));
        foodsMap.put("Barbeque Sauce", new Food(160, new float[] {0.0f, 0.0f}, new float[] {0.22f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Bolognese Sauce", new Food(161, new float[] {0.0f, 0.0f}, new float[] {2.43f, 0.0f}, new ArrayList<String>(Arrays.asList("Tomato Ketchup")), true, false));
        foodsMap.put("Chutney", new Food(162, new float[] {0.0f, 0.0f}, new float[] {1.65f, 0.0f}, new ArrayList<String>(Arrays.asList("Hummus")), true, false));
        foodsMap.put("Gravy - Beef", new Food(163, new float[] {0.0f, 0.0f}, new float[] {0.22f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Gravy - Chicken", new Food(164, new float[] {0.0f, 0.0f}, new float[] {0.22f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Hummus", new Food(165, new float[] {0.0f, 0.0f}, new float[] {0.88f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Mayonnaise", new Food(166, new float[] {0.0f, 0.0f}, new float[] {4.0f, 0.0f}, new ArrayList<String>(Arrays.asList("Mustard", "Tomato Ketchup")), true, false));
        foodsMap.put("Mustard", new Food(167, new float[] {0.0f, 0.0f}, new float[] {0.25f, 0.0f}, new ArrayList<String>(Arrays.asList("Barbeque Sauce")), true, false));
        foodsMap.put("Pesto", new Food(168, new float[] {0.0f, 0.0f}, new float[] {2.5f, 0.0f}, new ArrayList<String>(Arrays.asList("Chutney", "Tomato Sauce")), true, false));
        foodsMap.put("Relish", new Food(169, new float[] {0.0f, 0.0f}, new float[] {1.65f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Soy Sauce", new Food(170, new float[] {0.17f, 0.0f}, new float[] {0.22f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Stock - Beef", new Food(171, new float[] {0.0f, 0.0f}, new float[] {0.22f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Stock - Chicken", new Food(172, new float[] {0.0f, 0.0f}, new float[] {0.22f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Stock - Vegetable", new Food(173, new float[] {0.0f, 0.0f}, new float[] {0.22f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Tamari", new Food(174, new float[] {0.0f, 0.0f}, new float[] {0.22f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Tomato Ketchup", new Food(175, new float[] {0.08f, 0.0f}, new float[] {1.55f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Tomato Sauce", new Food(176, new float[] {0.0f, 0.0f}, new float[] {2.43f, 0.0f}, new ArrayList<String>(Arrays.asList("Tomato Ketchup")), true, false));
        foodsMap.put("Vinegar", new Food(177, new float[] {0.0f, 0.0f}, new float[] {0.22f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Worcestershire", new Food(178, new float[] {0.0f, 0.0f}, new float[] {0.22f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Anchovies", new Food(179, new float[] {0.0f, 0.0f}, new float[] {4.23f, 0.0f}, new ArrayList<String>(Arrays.asList("Herring", "Cod")), true, false));
        foodsMap.put("Clams", new Food(180, new float[] {0.0f, 0.0f}, new float[] {3.04f, 0.0f}, new ArrayList<String>(Arrays.asList("Herring")), true, false));
        foodsMap.put("Cod", new Food(181, new float[] {0.0f, 0.0f}, new float[] {3.31f, 0.0f}, new ArrayList<String>(Arrays.asList("Octopus", "Squid")), true, false));
        foodsMap.put("Crab", new Food(182, new float[] {0.0f, 0.0f}, new float[] {17.75f, 0.0f}, new ArrayList<String>(Arrays.asList("Eel", "Smoked Salmon")), true, false));
        foodsMap.put("Eel", new Food(183, new float[] {0.0f, 0.0f}, new float[] {4.22f, 0.0f}, new ArrayList<String>(Arrays.asList("Mussels", "Scallops")), true, false));
        foodsMap.put("Fish Sticks", new Food(184, new float[] {0.0f, 0.0f}, new float[] {1.91f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Flounder", new Food(185, new float[] {0.0f, 0.0f}, new float[] {4.36f, 0.0f}, new ArrayList<String>(Arrays.asList("Mussels", "Squid")), true, false));
        foodsMap.put("Haddock", new Food(186, new float[] {0.0f, 0.0f}, new float[] {4.36f, 0.0f}, new ArrayList<String>(Arrays.asList("Octopus", "Oysters")), true, false));
        foodsMap.put("Halibut", new Food(187, new float[] {0.0f, 0.0f}, new float[] {4.36f, 0.0f}, new ArrayList<String>(Arrays.asList("Herring", "Trout")), true, false));
        foodsMap.put("Herring", new Food(188, new float[] {0.0f, 0.0f}, new float[] {1.59f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Lobster", new Food(189, new float[] {0.0f, 0.0f}, new float[] {17.75f, 0.0f}, new ArrayList<String>(Arrays.asList("Salmon", "Eel")), true, false));
        foodsMap.put("Mussels", new Food(190, new float[] {0.0f, 0.0f}, new float[] {3.04f, 0.0f}, new ArrayList<String>(Arrays.asList("Herring", "Fish Sticks")), true, false));
        foodsMap.put("Octopus", new Food(191, new float[] {0.0f, 0.0f}, new float[] {3.04f, 0.0f}, new ArrayList<String>(Arrays.asList("Herring")), true, false));
        foodsMap.put("Oysters", new Food(192, new float[] {0.0f, 0.0f}, new float[] {3.04f, 0.0f}, new ArrayList<String>(Arrays.asList("Herring")), true, false));
        foodsMap.put("Prawns", new Food(193, new float[] {0.0f, 0.0f}, new float[] {17.75f, 0.0f}, new ArrayList<String>(Arrays.asList("Tuna", "Trout")), true, false));
        foodsMap.put("Salmon", new Food(194, new float[] {0.0f, 0.0f}, new float[] {4.79f, 0.0f}, new ArrayList<String>(Arrays.asList("Cod", "Sea Bass")), true, false));
        foodsMap.put("Scallops", new Food(195, new float[] {0.0f, 0.0f}, new float[] {3.03f, 0.0f}, new ArrayList<String>(Arrays.asList("Herring")), true, false));
        foodsMap.put("Sea Bass", new Food(196, new float[] {0.0f, 0.0f}, new float[] {4.36f, 0.0f}, new ArrayList<String>(Arrays.asList("Scallops", "Seaweed")), true, false));
        foodsMap.put("Seaweed", new Food(197, new float[] {0.0f, 0.0f}, new float[] {3.04f, 0.0f}, new ArrayList<String>(Arrays.asList("Scallops", "Herring")), true, false));
        foodsMap.put("Shrimp", new Food(198, new float[] {0.0f, 0.0f}, new float[] {17.75f, 0.0f}, new ArrayList<String>(Arrays.asList("Tuna", "Halibut")), true, false));
        foodsMap.put("Smoked Salmon", new Food(199, new float[] {0.0f, 0.0f}, new float[] {4.78f, 0.0f}, new ArrayList<String>(Arrays.asList("Haddock", "Mussels")), true, false));
        foodsMap.put("Squid", new Food(200, new float[] {0.0f, 0.0f}, new float[] {3.04f, 0.0f}, new ArrayList<String>(Arrays.asList("Herring")), true, false));
        foodsMap.put("Trout", new Food(201, new float[] {0.0f, 0.0f}, new float[] {4.22f, 0.0f}, new ArrayList<String>(Arrays.asList("Oysters", "Cod")), true, false));
        foodsMap.put("Tuna", new Food(202, new float[] {0.0f, 0.0f}, new float[] {10.16f, 0.0f}, new ArrayList<String>(Arrays.asList("Anchovies", "Clams")), true, false));
        foodsMap.put("Chocolate", new Food(203, new float[] {4.89f, 0.0f}, new float[] {5.77f, 0.0f}, new ArrayList<String>(Arrays.asList("Fudge", "Toffee")), true, false));
        foodsMap.put("Fudge", new Food(204, new float[] {0.0f, 0.0f}, new float[] {2.41f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Liquorice", new Food(205, new float[] {0.0f, 0.0f}, new float[] {2.41f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Marshmallows", new Food(206, new float[] {0.0f, 0.0f}, new float[] {2.41f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Mints", new Food(207, new float[] {0.06f, 0.0f}, new float[] {2.41f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Toffee", new Food(208, new float[] {0.0f, 0.0f}, new float[] {2.41f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Artichoke", new Food(209, new float[] {0.14f, 0.0f}, new float[] {0.34f, 0.0f}, new ArrayList<String>(Arrays.asList("Garlic")), true, false));
        foodsMap.put("Asparagus", new Food(210, new float[] {0.44f, 0.0f}, new float[] {7.04f, 0.0f}, new ArrayList<String>(Arrays.asList("Artichoke", "Aubergine")), true, false));
        foodsMap.put("Aubergine", new Food(211, new float[] {0.07f, 0.0f}, new float[] {3.1f, 0.0f}, new ArrayList<String>(Arrays.asList("Broccoli", "Brussels Sprouts")), true, false));
        foodsMap.put("Beetroot", new Food(212, new float[] {0.0f, 0.0f}, new float[] {1.14f, 0.0f}, new ArrayList<String>(Arrays.asList("Broccoli", "Cabbage")), true, false));
        foodsMap.put("Broccoli", new Food(213, new float[] {0.0f, 0.0f}, new float[] {1.08f, 0.0f}, new ArrayList<String>(Arrays.asList("Artichoke", "Turnips")), true, false));
        foodsMap.put("Brussels Sprouts", new Food(214, new float[] {0.06f, 0.0f}, new float[] {0.75f, 0.0f}, new ArrayList<String>(Arrays.asList("Artichoke", "Ginger")), true, false));
        foodsMap.put("Cabbage", new Food(215, new float[] {0.05f, 0.0f}, new float[] {0.63f, 0.0f}, new ArrayList<String>(Arrays.asList("Artichoke")), true, false));
        foodsMap.put("Carrot", new Food(216, new float[] {0.03f, 0.0f}, new float[] {1.14f, 0.0f}, new ArrayList<String>(Arrays.asList("Cauliflower", "Broccoli")), true, false));
        foodsMap.put("Cauliflower", new Food(217, new float[] {0.06f, 0.0f}, new float[] {0.98f, 0.0f}, new ArrayList<String>(Arrays.asList("Celery", "Turnips")), true, false));
        foodsMap.put("Celery", new Food(218, new float[] {0.03f, 0.0f}, new float[] {0.85f, 0.0f}, new ArrayList<String>(Arrays.asList("Cabbage", "Artichoke")), true, false));
        foodsMap.put("Chilli Pepper", new Food(219, new float[] {0.0f, 0.0f}, new float[] {2.0f, 0.0f}, new ArrayList<String>(Arrays.asList("Mushrooms", "Onion")), true, false));
        foodsMap.put("Cucumber", new Food(220, new float[] {0.06f, 0.0f}, new float[] {1.93f, 0.0f}, new ArrayList<String>(Arrays.asList("Celery", "Cauliflower")), true, false));
        foodsMap.put("Garlic", new Food(221, new float[] {0.1f, 0.0f}, new float[] {0.33f, 0.0f}, new ArrayList<String>(), true, false));
        foodsMap.put("Ginger", new Food(222, new float[] {0.44f, 0.0f}, new float[] {0.43f, 0.0f}, new ArrayList<String>(Arrays.asList("Artichoke", "Garlic")), true, false));
        foodsMap.put("Green Beans", new Food(223, new float[] {0.09f, 0.0f}, new float[] {5.66f, 0.0f}, new ArrayList<String>(Arrays.asList("Peppers", "Leek")), true, false));
        foodsMap.put("Kale", new Food(224, new float[] {0.0f, 0.0f}, new float[] {1.61f, 0.0f}, new ArrayList<String>(Arrays.asList("Spinach", "Cabbage")), true, false));
        foodsMap.put("Leek", new Food(225, new float[] {0.0f, 0.0f}, new float[] {1.05f, 0.0f}, new ArrayList<String>(Arrays.asList("Celery", "Turnips")), true, false));
        foodsMap.put("Lettuce", new Food(226, new float[] {0.04f, 0.0f}, new float[] {1.61f, 0.0f}, new ArrayList<String>(Arrays.asList("Cabbage", "Spinach")), true, false));
        foodsMap.put("Mushrooms", new Food(227, new float[] {0.0f, 0.0f}, new float[] {1.4f, 0.0f}, new ArrayList<String>(Arrays.asList("Onion", "Turnips")), true, false));
        foodsMap.put("Onion", new Food(228, new float[] {0.05f, 0.0f}, new float[] {1.05f, 0.0f}, new ArrayList<String>(Arrays.asList("Turnips", "Artichoke")), true, false));
        foodsMap.put("Peas", new Food(229, new float[] {0.11f, 0.0f}, new float[] {4.23f, 0.0f}, new ArrayList<String>(Arrays.asList("Carrot", "Squash")), true, false));
        foodsMap.put("Peppers", new Food(230, new float[] {0.07f, 0.0f}, new float[] {2.03f, 0.0f}, new ArrayList<String>(Arrays.asList("Mushrooms", "Onion")), true, false));
        foodsMap.put("Potato", new Food(231, new float[] {0.06f, 0.0f}, new float[] {1.27f, 0.0f}, new ArrayList<String>(Arrays.asList("Artichoke", "Beetroot")), true, false));
        foodsMap.put("Spinach", new Food(232, new float[] {0.03f, 0.0f}, new float[] {1.46f, 0.0f}, new ArrayList<String>(Arrays.asList("Cauliflower", "Broccoli")), true, false));
        foodsMap.put("Squash", new Food(233, new float[] {0.0f, 0.0f}, new float[] {1.16f, 0.0f}, new ArrayList<String>(Arrays.asList("Onion", "Artichoke")), true, false));
        foodsMap.put("Sweet Potato", new Food(234, new float[] {0.09f, 0.0f}, new float[] {1.27f, 0.0f}, new ArrayList<String>(Arrays.asList("Celery", "Brussels Sprouts")), true, false));
        foodsMap.put("Turnips", new Food(235, new float[] {0.03f, 0.0f}, new float[] {0.68f, 0.0f}, new ArrayList<String>(Arrays.asList("Artichoke", "Ginger")), true, false));
        foodsMap.put("Watercress", new Food(236, new float[] {0.0f, 0.0f}, new float[] {1.61f, 0.0f}, new ArrayList<String>(Arrays.asList("Spinach", "Cauliflower")), true, false));
    }

}