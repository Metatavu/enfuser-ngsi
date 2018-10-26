package fi.metatavu.ngsi.netcdf.search.io;

import javax.ejb.DependsOn;
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