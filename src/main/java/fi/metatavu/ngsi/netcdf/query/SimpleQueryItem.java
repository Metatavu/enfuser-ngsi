package fi.metatavu.ngsi.netcdf.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Parser for simple queries
 * 
 * @author Antti Leppä
 */
public class SimpleQueryItem {

  private static final Pattern PATTERN = Pattern.compile("([a-zA-Z0-9]{1,})([=]{0,1}[=!><~:]{1})(.*)");

  private String lhs;
  private String rhs;
  private SimpleQueryOp op;

  /**
   * Constructor 
   * 
   * @param lhs left hand side
   * @param rhs right hand side
   * @param op operation
   */
  public SimpleQueryItem(String lhs, String rhs, SimpleQueryOp op) {
    super();
    this.lhs = lhs;
    this.rhs = rhs;
    this.op = op;
  }

  /**
   * Left hand side of query item
   * 
   * @return Left hand side of query item
   */
  public String getLhs() {
    return lhs;
  }
  
  /**
   * Returns query item operation
   * 
   * @return query item operation
   */
  public SimpleQueryOp getOp() {
    return op;
  }
  
  /**
   * Right hand side of query item
   * 
   * @return Right hand side of query item
   */
  public String getRhs() {
    return rhs;
  }

  /**
   * Parses single simple query item from string
   * 
   * @param string string
   * @return parsed item or null if parsing fails
   */
  public static SimpleQueryItem fromString(String string) {
    if (StringUtils.isBlank(string)) {
      return null;
    }
    
    Matcher matcher = PATTERN.matcher(string);
    if (!matcher.matches()) {
      return null;
    }
    
    if (matcher.groupCount() != 3) {
      return null;
    }

    SimpleQueryOp op = SimpleQueryOp.fromString(matcher.group(2));
    if (op == null) {
      return null;
    }
    
    return new SimpleQueryItem(matcher.group(1), matcher.group(3), op);
  }

}
