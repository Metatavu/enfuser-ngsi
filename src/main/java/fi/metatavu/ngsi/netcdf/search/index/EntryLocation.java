package fi.metatavu.ngsi.netcdf.search.index;

import fi.metatavu.ngsi.netcdf.search.annotations.Field;

public class EntryLocation implements Indexable {
  
  public static final String TYPE = "entry-location";
  public static final String GEO_POINT_FIELD = "geoPoint";
  public static final String LAT_INDEX_FIELD = "latIndex";
  public static final String LON_INDEX_FIELD = "lonIndex";
  
  @Field(type="geo_point", store = true)
  private GeoPoint geoPoint;
  
  @Field(type="integer", store = true)
  private Integer latIndex;

  @Field(type="integer", store = true)
  private Integer lonIndex;
  
  public EntryLocation() {
    // Zero-argument constructor
  }
  
  public EntryLocation(GeoPoint geoPoint, Integer latIndex, Integer lonIndex) {
    super();
    this.geoPoint = geoPoint;
    this.latIndex = latIndex;
    this.lonIndex = lonIndex;
  }

  @Override
  public String getType() {
    return TYPE;
  }
  
  @Override
  public String getId() {
    return String.format("%d.%d", latIndex, lonIndex);
  }
  
  public GeoPoint getGeoPoint() {
    return geoPoint;
  }
  
  public void setGeoPoint(GeoPoint geoPoint) {
    this.geoPoint = geoPoint;
  }
  
  public Integer getLatIndex() {
    return latIndex;
  }
  
  public void setLatIndex(Integer latIndex) {
    this.latIndex = latIndex;
  }
  
  public Integer getLonIndex() {
    return lonIndex;
  }
  
  public void setLonIndex(Integer lonIndex) {
    this.lonIndex = lonIndex;
  }
  
}
