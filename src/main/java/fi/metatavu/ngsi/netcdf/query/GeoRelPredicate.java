package fi.metatavu.ngsi.netcdf.query;

public enum GeoRelPredicate {
  
  NEAR ("near"),
  COVERED_BY ("coveredBy"),
  INTERSECTS ("intersects"),
  EQUALS ("equals"),
  DISJOINT ("disjoint");
  
  private String paramName;
  
  GeoRelPredicate(String paramName) {
    this.paramName = paramName;
  }
  
  public static GeoRelPredicate fromParamName(String paramName) {
    for (GeoRelPredicate value : GeoRelPredicate.values()) {
      if (value.paramName.equals(paramName)) {
        return value;
      }
    }
    
    return null;
  }
  
}
