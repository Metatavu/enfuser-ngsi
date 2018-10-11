package fi.metatavu.ngsi.netcdf.query;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class Coordinates {
  
  private List<Coordinate> coordinateList;
  
  public Coordinates(List<Coordinate> coordinateList) {
    this.coordinateList = coordinateList;
  }

  public List<Coordinate> getCoordinateList() {
    return coordinateList;
  }
  
  public static Coordinates fromString(String coordsString) {
    List<Coordinate> coordinateList = Arrays.stream(StringUtils.split(coordsString, ";")).map((coordString) -> {
      String[] pair = StringUtils.split(coordString, ",");
      if (pair.length == 2) {
        Double lat = NumberUtils.createDouble(pair[0]);
        Double lon = NumberUtils.createDouble(pair[1]);
        
        if (lat != null & lon != null) {
          return new Coordinate(lat, lon);
        }
      }
      
      return (Coordinate) null;
    })
    .collect(Collectors.toList());
    
    if (coordinateList.stream().filter(Objects::isNull).count() == 0) {
      return new Coordinates(coordinateList);
    }
    
    return null;
  }

}
