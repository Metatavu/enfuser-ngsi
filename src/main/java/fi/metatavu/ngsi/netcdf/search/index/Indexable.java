package fi.metatavu.ngsi.netcdf.search.index;

/**
 * Interface that describes indexable class
 */
public interface Indexable {

  /**
   * Returns elastic search type
   * 
   * @return elastic search type
   */
  public String getType();
  
  /**
   * Returns id in elastic search
   * 
   * @return id in elastic search
   */
  public String getId();
  
}
