package fi.metatavu.ngsi.netcdf.query;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple query
 * 
 * @author Antti Leppä
 */
public class SimpleQuery {

  private List<SimpleQueryItem> items;

  /**
   * Constructor
   * 
   * @param items query item
   */
  public SimpleQuery(List<SimpleQueryItem> items) {
    super();
    this.items = items;
  }
  
  /**
   * Returns query items
   * 
   * @return query items
   */
  public List<SimpleQueryItem> getItems() {
    return items;
  }

  /**
   * Parses simple query from string
   * 
   * @param string string
   * @return parsed query
   */
  public static SimpleQuery fromString(String string) {
    List<SimpleQueryItem> items = Arrays.stream(StringUtils.split(string, ';'))
      .map(SimpleQueryItem::fromString)
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
    
    return new SimpleQuery(items);
  }

}
