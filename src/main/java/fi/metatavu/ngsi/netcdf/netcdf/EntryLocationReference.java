package fi.metatavu.ngsi.netcdf.netcdf;

public class EntryLocationReference {

  private Integer latIndex;

  private Integer lonIndex;

  public EntryLocationReference(Integer latIndex, Integer lonIndex) {
    super();
    this.latIndex = latIndex;
    this.lonIndex = lonIndex;
  }

  public Integer getLatIndex() {
    return latIndex;
  }

  public Integer getLonIndex() {
    return lonIndex;
  }
  
  @Override
  public String toString() {
    return String.format("%d, %d", latIndex, lonIndex);
  }

}
