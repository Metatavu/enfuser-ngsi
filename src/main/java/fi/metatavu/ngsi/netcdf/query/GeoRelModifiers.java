package fi.metatavu.ngsi.netcdf.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class GeoRelModifiers {
  
  private Long maxDistance;
  private Long minDistance;
  
  public Long getMaxDistance() {
    return maxDistance;
  }
  
  public void setMaxDistance(Long maxDistance) {
    this.maxDistance = maxDistance;
  }
  
  public Long getMinDistance() {
    return minDistance;
  }
  
  public void setMinDistance(Long minDistance) {
    this.minDistance = minDistance;
  }

  public static GeoRelModifiers fromTokens(String[] tokens) {
    GeoRelModifiers result = new GeoRelModifiers();
    
    for (String token : tokens) {
      String[] tokenParts = StringUtils.split(token, ":");
      if (tokenParts.length == 2) {
        switch (tokenParts[0]) {
          case "minDistance":
            result.setMinDistance(NumberUtils.createLong(tokenParts[1]));
          break;
          case "maxDistance":
            result.setMaxDistance(NumberUtils.createLong(tokenParts[1]));
          break;
        }
      }
    }
    
    return result;
  }

}
