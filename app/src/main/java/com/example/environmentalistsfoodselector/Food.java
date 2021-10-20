package com.example.environmentalistsfoodselector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;

public class Food {

    public int id;
    public float[] waterUsage;
    public float[] carbonUsage;
    public ArrayList<String> similarFoods;
    boolean weight, volume;

    public Food(int _id, float[] _waterUsage, float[] _carbonUsage, ArrayList<String> _similarFoods, boolean _weight, boolean _volume) {
        id = _id;
        waterUsage = _waterUsage;
        carbonUsage = _carbonUsage;
        similarFoods = _similarFoods;
        weight = _weight;
        volume = _volume;
    }
}
