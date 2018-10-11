package fi.metatavu.ngsi.netcdf.query;

public enum Geometry {
  
  POINT ("point"),
  
  LINE ("line"),
  
  POLYGON ("polygon");
 
  private String paramName;

  Geometry(String paramName) {
    this.paramName = paramName;
  }
  
  public static Geometry fromParamName(String paramName) {
    for (Geometry value : Geometry.values()) {
      if (value.paramName.equals(paramName)) {
        return value;
      }
    }
    
    return null;
  }

}
