package ufl.ibm.environmentalistsfoodselector;

import java.util.HashMap;

public class UnitsMap {
    private final HashMap<String, Unit> unitsMap;

      private static UnitsMap instance;

      public static UnitsMap getInstance(){
          if (instance== null)
              instance = new UnitsMap();
          return instance;
      }

    public UnitsMap() {
        unitsMap = new HashMap<String, Unit>();
        // scale for weight units are relative to gram, scale for volume units are relative to mL
        unitsMap.put( "g", new Unit( true, 1.0f ) );
        unitsMap.put( "kg", new Unit( true, 1000.0f ) );
        unitsMap.put( "oz", new Unit( true, 28.35f ) );
        unitsMap.put( "lb", new Unit( true, 453.6f ) );
        unitsMap.put( "mL", new Unit( false, 1.0f ) );
        unitsMap.put( "L", new Unit( false, 1000.0f ) );
        unitsMap.put( "tsp", new Unit( false, 4.93f ) );
        unitsMap.put( "tbsp", new Unit( false, 14.79f ) );
        unitsMap.put( "fl oz", new Unit( false, 29.57f ) );
        unitsMap.put( "cup", new Unit( false, 236.59f ) );
        unitsMap.put( "gallon", new Unit( false, 3785.41f ) );

    }
    public HashMap<String, Unit> getUnitsMap(){

        return unitsMap;
    }
}
