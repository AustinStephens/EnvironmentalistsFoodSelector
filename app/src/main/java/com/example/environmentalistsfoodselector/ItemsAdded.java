package com.example.environmentalistsfoodselector;

import java.util.ArrayList;

public class ItemsAdded {

    //Singleton implementation
    private static ItemsAdded instance; // Holds a static reference to an object

    public static ItemsAdded getInstance() { // Call this to get the object
        if(instance == null) {
            instance = new ItemsAdded();
        }
        return instance;
    }

    //Object Vars and Wrapper Functions
    private ArrayList<AddedItem> items;

    public ItemsAdded() {
        items = new ArrayList<AddedItem>();
    }

    public ArrayList<AddedItem> getItems() {
        return items;
    }

    public void addItem(AddedItem i) {
        items.add(i);
    }

    public void removeItem(int index) {
        items.remove(index);
    }

    public void clearItems() {
        items.clear();
    }
}
