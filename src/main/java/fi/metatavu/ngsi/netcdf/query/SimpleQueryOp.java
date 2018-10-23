package fi.metatavu.ngsi.netcdf.query;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Enum that describes simple query operation
 * 
 * @author Antti Leppä
 */
public enum SimpleQueryOp {
  
  NOT_EXISTS,
  EXISTS,
  EQUALS ("==", ":"),
  DIFFERS ("=!"),
  GREATER_THAN (">"),
  GREATER_THAN_OR_EQUAL ("=>"),
  LESS_THAN ("<"),
  LESS_THAN_OR_EQUAL ("=<"),
  MATCH_PATTERN ("=~");
  
  private String[] ops;
  
  private SimpleQueryOp(String... ops) {
    this.ops = ops;
  }
  
  /**
   * Resolves operation from string
   * 
   * @param op operation
   * @return resolved operation or null if not found
   */
  public static SimpleQueryOp fromString(String op) {
    for (SimpleQueryOp value : SimpleQueryOp.values()) {
      if (ArrayUtils.contains(value.ops, op)) {
        return value;
      }
    }
    
    return null;
  }

}
