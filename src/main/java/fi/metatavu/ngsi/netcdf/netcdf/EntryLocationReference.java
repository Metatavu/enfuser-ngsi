package fi.metatavu.ngsi.netcdf.netcdf;

public class EntryLocationReference {

  private Integer latIndex;

  private Integer lonIndex;

  private Integer timeIndex;

  private String file;

  public EntryLocationReference(Integer latIndex, Integer lonIndex, Integer timeIndex, String file) {
    super();
    this.latIndex = latIndex;
    this.lonIndex = lonIndex;
    this.timeIndex = timeIndex;
    this.file = file;
  }

  public Integer getLatIndex() {
    return latIndex;
  }

  public Integer getLonIndex() {
    return lonIndex;
  }

  public Integer getTimeIndex() {
    return timeIndex;
  }

  public String getFile() {
    return file;
  }
  
  @Override
  public String toString() {
    return String.format("%s: %d, %d, %d", file, latIndex, lonIndex, timeIndex);
  }

}
