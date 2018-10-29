package fi.metatavu.ngsi.netcdf.search.searcher;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoDisjointQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoDistanceQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoIntersectionQuery;
import static org.elasticsearch.index.query.QueryBuilders.geoWithinQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.geo.builders.CoordinatesBuilder;
import org.elasticsearch.common.geo.builders.LineStringBuilder;
import org.elasticsearch.common.geo.builders.PolygonBuilder;
import org.elasticsearch.common.geo.builders.ShapeBuilder;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import fi.metatavu.ngsi.netcdf.SystemConsts;
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
  
  /**
   * Searches for entry locations
   * 
   * @param id if defined, id must be exact match
   * @param idPattern if defined, id must match pattern
   * @param geoRel geo rel of geo query
   * @param geometry geometry of geo query
   * @param coordinates coordinates for geo query
   * @return matching entry location references
   * @throws IOException thrown when searching fails
   */
  public List<EntryLocationReference> searchEntryLocations(String id, String idPattern, OffsetDateTime minTime, OffsetDateTime maxTime, GeoRel geoRel, Geometry geometry, Coordinates coordinates, Long firstResult, Long maxResults) throws IOException {
    BoolQueryBuilder query = boolQuery();
    
    if (StringUtils.isNotBlank(id)) {
      query.must(matchQuery(EntryLocation.ID_FIELD, id));
    }
    
    if (StringUtils.isNotBlank(idPattern)) {
      query.must(regexpQuery(EntryLocation.ID_FIELD, idPattern));
    }

    if (minTime != null || maxTime != null) {
      RangeQueryBuilder rangeQuery = rangeQuery(EntryLocation.TIME_FIELD);
      
      if (minTime != null) {
        rangeQuery.gte(minTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
      }
      
      if (maxTime != null) {
        rangeQuery.lte(maxTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
      }    
      
      query.must(rangeQuery);
    }
    
    // TODO: Max distance, mix distance
    
    QueryBuilder geoQuery = createGeoQuery(geoRel, geometry, coordinates);
    if (geoQuery != null) {
      query.must(geoQuery);
    }
    
    SearchResponse searchResponse = executeSearch(
      query,
      Arrays.asList(
        EntryLocation.LAT_INDEX_FIELD,
        EntryLocation.LON_INDEX_FIELD,
        EntryLocation.TIME_INDEX_FIELD,
        EntryLocation.FILE_FIELD
      ),
      firstResult,
      maxResults,
      Collections.emptyList());

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

      DocumentField timeIndexField = fields.get(EntryLocation.TIME_INDEX_FIELD);
      if (timeIndexField == null) {
        return null;
      }

      DocumentField fileField = fields.get(EntryLocation.FILE_FIELD);
      if (fileField == null) {
        return null;
      }
      
      File file = new File(System.getProperty(SystemConsts.DATA_STORE_FOLDER), fileField.getValue());
      if (!file.exists()) {
        return null;
      }

      return new EntryLocationReference(latitudeIndexField.getValue(), longitudeIndexField.getValue(), timeIndexField.getValue(), file.getAbsolutePath());
    }).filter(Objects::nonNull).collect(Collectors.toList());
    
  }

  /**
   * Creates geo query
   * 
   * @param geoRel geo rel of geo query
   * @param geometry geometry of geo query
   * @param coordinates coordinates for geo query
   * @return created geo query
   * @throws IOException thrown when query creation fails
   */
  private QueryBuilder createGeoQuery(GeoRel geoRel, Geometry geometry, Coordinates coordinates) throws IOException {
    QueryBuilder query = null;
    
    if (geoRel == null) {
      return null;
    }

    GeoRelPredicate predicate = geoRel.getPredicate();
    
    if (!GeoRelPredicate.EQUALS.equals(predicate) && geometry == null || coordinates == null) {
      return null;
    }
    
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
    return query;
  }

  /**
   * Returns geo shape
   * 
   * @param geometry geometry
   * @param coordinates coordinates
   * @return geo shape
   */
  private ShapeBuilder<?, ?> getShape(Geometry geometry, Coordinates coordinates) {
    switch (geometry) {
      case LINE:
        return getLine(coordinates);
      case POLYGON:
        return getPolygon(coordinates);
	  default:
	  break;
    }
    
    return null;
  }

  /**
   * Returns geo point
   * 
   * @param coordinates coordinates
   * @return geo point
   */
  private GeoPoint getPoint(Coordinates coordinates) {
    if (coordinates == null || coordinates.getCoordinateList() == null || coordinates.getCoordinateList().isEmpty()) {
      return null;
    }
    
    Coordinate coordinate = coordinates.getCoordinateList().get(0);
    return new GeoPoint(coordinate.getLat(), coordinate.getLon());
  }

  /**
   * Creates geo line
   * 
   * @param coordinates coordinates
   * @return geo line
   */
  private LineStringBuilder getLine(Coordinates coordinates) {
    return new LineStringBuilder(getCoordinatesBuilder(coordinates));
  }

  /**
   * Creates geo polyline
   * 
   * @param coordinates coordinates
   * @return geo polyline
   */
  private PolygonBuilder getPolygon(Coordinates coordinates) {
    return new PolygonBuilder(getCoordinatesBuilder(coordinates));
  }

  /**
   * Creates coordinates builder
   * 
   * @param coordinates coordinates
   * @return coordinates builder
   */
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
   * @param query       query
   * @param firstResult first result
   * @param maxResults  max results
   * @param sorts
   * @return result
   * @throws IOException
   */
  protected SearchResponse executeSearch(QueryBuilder query, List<String> fields, Long firstResult, Long maxResults, List<SortBuilder<?>> sorts) throws IOException {
    return indexReader.executeSearch(getType(), query, fields, firstResult.intValue(), maxResults.intValue(), sorts);
  }
  
  /**
   * Returns searcher type
   * 
   * @return type
   */
  private String getType() {
    return EntryLocation.TYPE;
  }

}
