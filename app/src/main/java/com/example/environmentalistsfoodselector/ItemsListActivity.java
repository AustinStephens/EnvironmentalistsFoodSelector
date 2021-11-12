package com.example.environmentalistsfoodselector;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class ItemsListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemslist);

        RecyclerView itemsList = (RecyclerView) findViewById(R.id.itemsList); // List
        Button clearItems = (Button) findViewById(R.id.clearButton); // Clear Button
        TextView carbonTotal = (TextView) findViewById(R.id.carbonTotal); // Carbon Total Label
        TextView waterTotal = (TextView) findViewById(R.id.waterTotal); // Water Total Label

        // Need to add recycler View Logic
    }
}
