package fi.metatavu.ngsi.netcdf.search.io;

import java.io.IOException;
import java.util.List;

import javax.ejb.DependsOn;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;

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
   * Executes search
   * 
   * @param type type to search for
   * @param query query object
   * @param fields returned fields
   * @param from first result
   * @param size max results
   * @param sorts sort objects
   * 
   * @return search response
   * 
   * @throws IOException
   */
  public SearchResponse executeSearch(String type, QueryBuilder query, List<String> fields, int from, int size, List<SortBuilder<?>> sorts) throws IOException {
    return getClient().search(getSearchRequest(type, query, fields, from, size, sorts), RequestOptions.DEFAULT);
  }

  /**
   * Builds search request
   * 
   * @param type type to search for
   * @param query query object
   * @param fields returned fields
   * @param from first result
   * @param size max results
   * @param sorts sort objects
   *
   * @return search request
   */
  private SearchRequest getSearchRequest(String type, QueryBuilder query, List<String> fields, int from, int size, List<SortBuilder<?>> sorts) {
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.query(query);
    sourceBuilder.storedFields(fields);
    sourceBuilder.from(from);
    sourceBuilder.size(size);
    sorts.stream().forEach(sourceBuilder::sort);
    return new SearchRequest(getIndex()).types(type).source(sourceBuilder);
  }

}