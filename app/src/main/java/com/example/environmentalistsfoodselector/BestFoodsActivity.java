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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BestFoodsActivity extends AppCompatActivity {

    // use the same array for best and worst, first half is best, second half is worst
    public int[] foodsBestArr = {R.array.BakedBestWorst, R.array.DairyBestWorst, R.array.DrinkBestWorst, R.array.DryBestWorst, R.array.FruitBestWorst, R.array.MeatBestWorst,
            R.array.NutsBestWorst, R.array.OilsBestWorst, R.array.ProcessedBestWorst, R.array.JamsBestWorst, R.array.SaucesBestWorst, R.array.SeafoodBestWorst, R.array.SweetsBestWorst, R.array.VegetableBestWorst};

    public ArrayList <Food> list;
    public ArrayList <String> names;
    public HashMap<String, Food> foodsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestfoods);

        // first item
        TextView foodLabel1 = (TextView) findViewById(R.id.foodLabel1);
        TextView carbon1 = (TextView) findViewById(R.id.carbon1);
        TextView water1 =  (TextView) findViewById(R.id.water1);

        // 2nd item
        TextView foodLabel2 = (TextView) findViewById(R.id.foodLabel2); // Name of Selected Food Label
        TextView carbon2=(TextView) findViewById(R.id.carbon2);
        TextView water2 =  (TextView) findViewById(R.id.water2);
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

                    //food string adapter from spinner item, get the corresponding array resource from strinig.xml
                    ArrayAdapter<String> foodBestAdapter = createAdapter(foodsBestArr[i-1]);
                    foodBestAdapter.setDropDownViewResource( R.layout.spinner_item );

                    // list of best food for each cat item, and names
                    list = new ArrayList<Food>();
                    names = new ArrayList<String>();

                    // use only first 2 item, the last 2 is for worst case
                    for (int j = 0; j < 2; j++) {
                        String item = (String) foodBestAdapter.getItem( j );

                         // store name in names array, because Food obj doesn't have name
                        names.add(item);
                        // store food obj of that food name
                        list.add(foodsMap.get(item));
                    }

                    // populate data
                    foodLabel1.setText( names.get( 0 ) );
                 foodLabel2.setText( names.get( 1 ) );

                 DecimalFormat f = new DecimalFormat("#.##");
                 //carbon1.setText("Carbon Footprint:\n" + f.format(list.get( 0 ).carbonUsage) + " gCO\u2082e");
                // water1.setText("Total Water Footprint:\n" + f.format(list.get( 0 ).waterUsage)+ " Gallons");
                    System.out.println(list.size());

                    //System.out.println("Carbon Footprint:\n" + f.format(list.get( 0 ).carbonUsage) + " gCO\u2082e");


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
