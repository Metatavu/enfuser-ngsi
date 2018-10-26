package fi.metatavu.ngsi.netcdf.search.index;

import java.time.OffsetDateTime;

import fi.metatavu.ngsi.netcdf.search.annotations.Field;

public class EntryLocation implements Indexable {

  public static final String TYPE = "entry-location";
  public static final String ID_FIELD = "entryId";
  public static final String GEO_POINT_FIELD = "geoPoint";
  public static final String LAT_INDEX_FIELD = "latIndex";
  public static final String LON_INDEX_FIELD = "lonIndex";
  public static final String TIME_INDEX_FIELD = "timeIndex";
  public static final String TIME_FIELD = "time";
  
  @Field(type="keyword", store = true)
  private String entryId;
  
  @Field(type="geo_point", store = true)
  private GeoPoint geoPoint;
  
  @Field(type="integer", store = true)
  private Integer latIndex;

  @Field(type="integer", store = true)
  private Integer lonIndex;
  
  @Field(type="integer", store = true)
  private Integer timeIndex;
  
  @Field(type="date", store = true)
  private OffsetDateTime time;
  
  @Field(type="keyword", store = true)
  private String file;
  
  public EntryLocation() {
    // Zero-argument constructor
  }
  
  public EntryLocation(String entryId, String file, OffsetDateTime time, GeoPoint geoPoint, Integer latIndex, Integer lonIndex, Integer timeIndex) {
    super();
    this.file = file;
    this.time = time;
    this.entryId = entryId;
    this.geoPoint = geoPoint;
    this.latIndex = latIndex;
    this.lonIndex = lonIndex;
    this.timeIndex = timeIndex;
  }

  @Override
  public String getType() {
    return TYPE;
  }
  
  @Override
  public String getId() {
    return String.format("%d.%d.%d", latIndex, lonIndex, timeIndex);
  }
  
  public String getFile() {
    return file;
  }
  
  public void setFile(String file) {
    this.file = file;
  }
  
  public OffsetDateTime getTime() {
    return time;
  }
  
  public void setTime(OffsetDateTime time) {
    this.time = time;
  }
  
  public String getEntryId() {
    return entryId;
  }
  
  public void setEntryId(String entryId) {
    this.entryId = entryId;
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
  
  public Integer getTimeIndex() {
    return timeIndex;
  }
  
  public void setTimeIndex(Integer timeIndex) {
    this.timeIndex = timeIndex;
  }
  
}
