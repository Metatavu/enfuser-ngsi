package fi.metatavu.ngsi.netcdf.search.io;

import javax.ejb.DependsOn;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;

/**
 * Reader for Elastic Search index
 */
@ApplicationScoped
@Singleton
@DependsOn (value = "IndexUpdater")
public class IndexReader extends AbstractIndexHander {
  
  @Override
  public void setup() {
    // No setup needed for reader
  }
 
  /**
   * Creates a request builder
   * 
   * @param types types
   * @return  a request builder
   */
  @Lock (LockType.READ)
  public SearchRequestBuilder requestBuilder(String... types) {
    return getClient()
      .prepareSearch(getIndex())
      .setTypes(types);
  }
  
  /**
   * Executes a search
   * 
   * @param searchRequest search request
   * @return search response
   */
  public SearchResponse executeSearch(SearchRequestBuilder searchRequest) {
    return searchRequest
      .execute()
      .actionGet(); 
  }

  
}