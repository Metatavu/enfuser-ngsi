package fi.metatavu.ngsi.netcdf.query;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class GeoRel {
  
  private GeoRelPredicate predicate;
  private GeoRelModifiers modifiers;
  
  public GeoRel(GeoRelPredicate predicate, GeoRelModifiers modifiers) {
    super();
    this.predicate = predicate;
    this.modifiers = modifiers;
  }
  
  public GeoRelModifiers getModifiers() {
    return modifiers;
  }
  
  public GeoRelPredicate getPredicate() {
    return predicate;
  }

  public static GeoRel fromString(String geoRelString) {
    String[] tokens = StringUtils.split(geoRelString, ";");
    if (tokens == null || tokens.length == 0) {
      return null;
    }
    
    GeoRelPredicate predicate = GeoRelPredicate.fromParamName(tokens[0]);
    if (predicate == null) {
      return null;
    }
    
    GeoRelModifiers modifiers = GeoRelModifiers.fromTokens(ArrayUtils.subarray(tokens, 1, tokens.length));

    return new GeoRel(predicate, modifiers);
  }
  
  
}
