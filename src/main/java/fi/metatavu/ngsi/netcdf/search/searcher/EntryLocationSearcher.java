package fi.metatavu.ngsi.netcdf.search.searcher;

import static org.elasticsearch.index.query.QueryBuilders.geoDisjointQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoIntersectionQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoWithinQuery;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.builders.CoordinatesBuilder;
import org.elasticsearch.common.geo.builders.LineStringBuilder;
import org.elasticsearch.common.geo.builders.PointBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import fi.metatavu.ngsi.netcdf.netcdf.EntryLocationReference;
import fi.metatavu.ngsi.netcdf.query.Coordinate;
import fi.metatavu.ngsi.netcdf.query.Coordinates;
import fi.metatavu.ngsi.netcdf.query.GeoRel;
import fi.metatavu.ngsi.netcdf.query.GeoRelPredicate;
import fi.metatavu.ngsi.netcdf.query.Geometry;
import fi.metatavu.ngsi.netcdf.search.index.EntryLocation;
import fi.metatavu.ngsi.netcdf.search.io.IndexReader;

@ApplicationScoped
public class EntryLocationSearcher {
  
  private static final int DEFALT_MAX_RESULTS = 999;
  @Inject
  private IndexReader indexReader;
  
  public List<EntryLocationReference> searchEntryLocations(GeoRel geoRel, Geometry geometry, Coordinates coordinates) throws IOException {
    QueryBuilder query = null;
    
    
    // TODO: Max distance, mix distance
    
    GeoRelPredicate predicate = geoRel.getPredicate();
    switch (predicate) {
      case COVERED_BY:
        query = geoWithinQuery(EntryLocation.GEO_POINT_FIELD, getShape(geometry, coordinates));
      break;
      case DISJOINT:
        query = geoDisjointQuery(EntryLocation.GEO_POINT_FIELD, getShape(geometry, coordinates));
      break;
      case EQUALS:
        query = geoDistanceQuery(EntryLocation.GEO_POINT_FIELD).distance(1, DistanceUnit.METERS);
      break;
      case INTERSECTS:
        query = geoIntersectionQuery(EntryLocation.GEO_POINT_FIELD, getShape(geometry, coordinates));
      break;
      case NEAR:
        query = geoDistanceQuery(EntryLocation.GEO_POINT_FIELD)
          .point(getPoint(coordinates))
          .distance(geoRel.getModifiers().getMaxDistance(), DistanceUnit.METERS);
      break;
    }
    
    SearchResponse searchResponse = executeSearch(query, EntryLocation.LAT_INDEX_FIELD, EntryLocation.LON_INDEX_FIELD);
    
    return Arrays.stream(searchResponse.getHits().getHits()).map((hit) -> {
      Map<String, DocumentField> fields = hit.getFields();
      
      DocumentField latitudeIndexField = fields.get(EntryLocation.LAT_INDEX_FIELD);
      if (latitudeIndexField == null) {
        return null;
      }
      
      DocumentField longitudeIndexField = fields.get(EntryLocation.LON_INDEX_FIELD);
      if (longitudeIndexField == null) {
        return null;
      }
      
      return new EntryLocationReference(latitudeIndexField.getValue(), longitudeIndexField.getValue());
    }).collect(Collectors.toList());
    
  }
  
  private SearchResponse executeSearch(QueryBuilder query, String... fields) {
    return executeSearch(query, fields, null, null, Collections.emptyList());
  }
  
  private ShapeBuilder getShape(Geometry geometry, Coordinates coordinates) {
    switch (geometry) {
      case LINE:
        return getLine(coordinates);
      case POLYGON:
        return getPolygon(coordinates);
    }
    
    return null;
  }

  private GeoPoint getPoint(Coordinates coordinates) {
    if (coordinates == null || coordinates.getCoordinateList() == null || coordinates.getCoordinateList().isEmpty()) {
      return null;
    }
    
    Coordinate coordinate = coordinates.getCoordinateList().get(0);
    return new GeoPoint(coordinate.getLat(), coordinate.getLon());
  }

  private LineStringBuilder getLine(Coordinates coordinates) {
    return new LineStringBuilder(getCoordinatesBuilder(coordinates));
  }

  private PolygonBuilder getPolygon(Coordinates coordinates) {
    return new PolygonBuilder(getCoordinatesBuilder(coordinates));
  }

  private CoordinatesBuilder getCoordinatesBuilder(Coordinates coordinates) {
    CoordinatesBuilder coordinatesBuilder = new CoordinatesBuilder();
    coordinates.getCoordinateList().stream().forEach((coordinate) -> {
      coordinatesBuilder.coordinate(coordinate.getLon(), coordinate.getLat());  
    });
    
    return coordinatesBuilder;
  }

  /**
   * Executes a search and returns result as UUIDs
   * 
   * @param query query
   * @param firstResult first result
   * @param maxResults max results
   * @param sorts 
   * @return result
   */
  protected SearchResponse executeSearch(QueryBuilder query, String[] fields, Long firstResult, Long maxResults, List<SortBuilder<?>> sorts) {
    SearchRequestBuilder requestBuilder = indexReader
      .requestBuilder(getType())
      .setQuery(query)
      .fields(fields)
      .setFrom(firstResult != null ? firstResult.intValue() : 0)
      .setSize(maxResults != null ? maxResults.intValue() : DEFALT_MAX_RESULTS);

    sorts.stream().forEach(requestBuilder::addSort);
    
    return indexReader.executeSearch(requestBuilder);
  }
  
  private String getType() {
    return EntryLocation.TYPE;
  }

}
