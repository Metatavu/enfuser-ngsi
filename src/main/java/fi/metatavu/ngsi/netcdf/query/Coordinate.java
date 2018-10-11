package fi.metatavu.ngsi.netcdf.query;

public class Coordinate {
  
  private double lat;
  private double lon;
  
  public Coordinate(double lat, double lon) {
    super();
    this.lat = lat;
    this.lon = lon;
  }

  public double getLat() {
    return lat;
  }
  
  public double getLon() {
    return lon;
  }

}
