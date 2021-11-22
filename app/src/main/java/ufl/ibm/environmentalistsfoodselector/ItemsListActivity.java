package ufl.ibm.environmentalistsfoodselector;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DecimalFormat;
import java.util.ArrayList;

public class ItemsListActivity extends AppCompatActivity {

    Button clearItems;
    TextView carbonTotal;
    TextView waterTotal;
    RecyclerView itemsList;
    RecyclerView.AdapterDataObserver observer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemslist);

        clearItems = (Button) findViewById(R.id.clearButton); // Clear Button
        carbonTotal = (TextView) findViewById(R.id.carbonTotal); // Carbon Total Label
        waterTotal = (TextView) findViewById(R.id.waterTotal); // Water Total Label

        // Need to add recycler View Logic
        itemsList = (RecyclerView) findViewById(R.id.itemsList); // List
        itemsList.setLayoutManager(new LinearLayoutManager(this));
        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                resetPage(ItemsAdded.getInstance().getItems());
            }
        };
        resetPage(ItemsAdded.getInstance().getItems());

        clearItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Adds Food, Unit, and Amount to our singleton arraylist class
                ItemsAdded.getInstance().clearItems();
                resetPage(ItemsAdded.getInstance().getItems());
            }
        });
    }

    public void resetPage(ArrayList<AddedItem> items) {
        float totalCarbon = 0.0f, totalWater = 0.0f;
        for(AddedItem i : items) {
            totalCarbon += i.getCarbon();
            totalWater += i.getWater();
        }
        AddedItemsViewAdapter adapter = new AddedItemsViewAdapter(this, items, totalCarbon, totalWater);
        adapter.registerAdapterDataObserver(observer);
        itemsList.setAdapter(adapter);

        DecimalFormat f = new DecimalFormat("#.##");
        carbonTotal.setText("Total Carbon Footprint:\n" + f.format(totalCarbon) + " gCO\u2082e");
        waterTotal.setText("Total Water Footprint:\n" + f.format(totalWater) + " Gallons");
    }
}
