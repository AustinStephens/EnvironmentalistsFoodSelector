package com.example.environmentalistsfoodselector;

/*
    Class to contain all Food selection information
*/
public class AddedItem {

    private String name;
    private Food food;
    private String unitName;
    private Unit unit;
    private float amount;

    public AddedItem(String n, Food f, String u, float amt) {
        name = n;
        food = f;
        unitName = u;
        //unit == UnitsMap.getInstance().get(unitName);
        amount = amt;
    }

    // GETTERS
    public String getName() {
        return name;
    }

    public String getUnitName() {
        return unitName;
    }

    public float getAmount() {
        return amount;
    }

    // Information functions
    public float getCarbon() {
        float carbon = amount * unit.scale;
        if(unit.weight) {
            carbon *= food.carbonUsage[0];
        } else {
            carbon *= food.carbonUsage[1];
        }

        return carbon;
    }

    public float getWater() {
        float water = amount * unit.scale;
        if(unit.weight) {
            water *= food.waterUsage[0];
        } else {
            water *= food.waterUsage[1];
        }

        return water;
    }

    public String getCarbonText() {
        return Float.toString(getCarbon());
    }

    public String getWaterText() {
        String water;
        if((unit.weight && food.waterUsage[0] == 0.0f) || (!unit.weight && food.waterUsage[1] == 0.0f))
            water = "N/A";
        else
            water = Float.toString(getWater());

        return water;
    }
}
