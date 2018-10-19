package fi.metatavu.ngsi.netcdf.netcdf;

/**
 * Constants for Enfuser
 * 
 * @author Antti Leppä
 */
public class EnfuserConsts {

  public static final String ID_PREFIX = "fmi::forecast::enfuser::airquality::helsinki-metropolitan::";
  public static final String ID_PATTERN = String.format("%s%s", ID_PREFIX, "%d-%d");
  
  private EnfuserConsts() {
    // Private constructor
  }
  
}
