package ufl.ibm.environmentalistsfoodselector;

import java.text.DecimalFormat;

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
        unit = UnitsMap.getInstance().getUnitsMap().get(unitName);
        amount = amt;
    }

    // GETTERS
    public String getName() {
        return name;
    }

    public String getUnitName() {
        return unitName;
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

    public String getCarbonText(float total) {
        DecimalFormat f = new DecimalFormat("#.##");
        float c = getCarbon();
        return f.format(c) + " gCO\u2082e  (" + f.format(c/total * 100f) + "%)";
    }

    public String getWaterText(float total) {
        String water;
        DecimalFormat f = new DecimalFormat("#.##");
        float w = getWater();
        if((unit.weight && food.waterUsage[0] == 0.0f) || (!unit.weight && food.waterUsage[1] == 0.0f))
            water = "N/A (0%)";
        else
            water = f.format(w) + " Gallons  (" + f.format(w/total * 100f) + "%)";

        return water;
    }

    public String getAmountText() {
        String ret = Float.toString(amount);
        ret = ret + " " + getUnitName();
        return ret;
    }
}
