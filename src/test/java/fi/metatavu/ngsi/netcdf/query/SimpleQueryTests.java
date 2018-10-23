package fi.metatavu.ngsi.netcdf.query;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class SimpleQueryTests {
  
  @Test
  public void testParse() {
    SimpleQuery simpleQuery = SimpleQuery.fromString("lhs1==rhs1;lhs2=<rhs2");
    List<SimpleQueryItem> items = simpleQuery.getItems();
    
    assertEquals(2, items.size());
    assertEquals("lhs1", items.get(0).getLhs());
    assertEquals("rhs1", items.get(0).getRhs());
    assertEquals(SimpleQueryOp.EQUALS, items.get(0).getOp());
    assertEquals("lhs2", items.get(1).getLhs());
    assertEquals("rhs2", items.get(1).getRhs());
    assertEquals(SimpleQueryOp.LESS_THAN_OR_EQUAL, items.get(1).getOp());
  }
  
  @Test
  public void testItems() {
    assertParsedItem("lhs:rhs", "lhs", "rhs", SimpleQueryOp.EQUALS);
    assertParsedItem("lhs==rhs", "lhs", "rhs", SimpleQueryOp.EQUALS);
    assertParsedItem("lhs=!rhs", "lhs", "rhs", SimpleQueryOp.DIFFERS);
    assertParsedItem("lhs>rhs", "lhs", "rhs", SimpleQueryOp.GREATER_THAN);
    assertParsedItem("lhs<rhs", "lhs", "rhs", SimpleQueryOp.LESS_THAN);
    assertParsedItem("lhs=>rhs", "lhs", "rhs", SimpleQueryOp.GREATER_THAN_OR_EQUAL);
    assertParsedItem("lhs=<rhs", "lhs", "rhs", SimpleQueryOp.LESS_THAN_OR_EQUAL);
    assertParsedItem("lhs=~rhs", "lhs", "rhs", SimpleQueryOp.MATCH_PATTERN);
  }
  
  private void assertParsedItem(String string, String lhs, String rhs, SimpleQueryOp op) {
    SimpleQueryItem simpleQueryItem = SimpleQueryItem.fromString(string);
    assertEquals(lhs, simpleQueryItem.getLhs());
    assertEquals(rhs, simpleQueryItem.getRhs());
    assertEquals(op, simpleQueryItem.getOp());
  }

}
