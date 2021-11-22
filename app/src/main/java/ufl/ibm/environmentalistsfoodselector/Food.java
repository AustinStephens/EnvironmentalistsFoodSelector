package ufl.ibm.environmentalistsfoodselector;

import java.util.ArrayList;

public class Food {

    public int id;
    // size 2. 0 index is for 1 gram, 1 index is for 1 mL
    public float[] waterUsage;
    public float[] carbonUsage;

    // List of Strings of the names of similar foods, can cross reference with foodsMap in MainActivity
    public ArrayList<String> similarFoods;
    // If it has weight units and if it has volume units
    boolean weight, volume;

    //
    public Food(int _id, float[] _waterUsage, float[] _carbonUsage, ArrayList<String> _similarFoods, boolean _weight, boolean _volume) {
        id = _id;
        waterUsage = _waterUsage;
        carbonUsage = _carbonUsage;
        similarFoods = _similarFoods;
        weight = _weight;
        volume = _volume;
    }
}
