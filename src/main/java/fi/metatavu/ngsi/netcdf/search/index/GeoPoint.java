package fi.metatavu.ngsi.netcdf.search.index;

import java.math.BigDecimal;

public class GeoPoint {
  
  private BigDecimal lat;
  private BigDecimal lon;
  
  public GeoPoint() {
  }
  
  public GeoPoint(BigDecimal lat, BigDecimal lon) {
    super();
    this.lat = lat;
    this.lon = lon;
  }

  public BigDecimal getLat() {
    return lat;
  }
  
  public BigDecimal getLon() {
    return lon;
  }
  
  /**
   * Creates GeoPoint
   * 
   * @param longitude longitude
   * @param longitude longitude
   * @return GeoPoint
   */
  public static GeoPoint createGeoPoint(BigDecimal latitude, BigDecimal longitude) {
    if (latitude == null || longitude == null) {
      return null;
    }
    
    if (latitude == null || longitude == null) {
      return null;
    }
    
    return new GeoPoint(latitude, longitude);
  }
  
  /**
   * Creates GeoPoint
   * 
   * @param longitude longitude
   * @param longitude longitude
   * @return GeoPoint
   */
  public static GeoPoint createGeoPoint(Double latitude, Double longitude) {
    if (latitude == null || longitude == null) {
      return null;
    }
    
    return createGeoPoint(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
  }
  
}
