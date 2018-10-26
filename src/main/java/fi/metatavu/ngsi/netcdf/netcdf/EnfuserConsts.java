package fi.metatavu.ngsi.netcdf.netcdf;

/**
 * Constants for Enfuser
 * 
 * @author Antti Leppä
 */
public class EnfuserConsts {

  public static final String ID_PREFIX = "fmi::forecast::enfuser::airquality::helsinki-metropolitan::";
  public static final String ID_PATTERN = String.format("%s%s", ID_PREFIX, "%d-%d-%d");
  public static final String NO2_VARIABLE_PROPERTY = "enfuser-no2";
  public static final String PM10_VARIABLE_PROPERTY = "enfuser-pm10";
  public static final String PM25_VARIABLE_PROPERTY = "enfuser-pm25";
  public static final String AQI_VARIABLE_PROPERTY = "enfuser-aqi";
  public static final String O3_VARIABLE_PROPERTY = "enfuser-o3";
  
  private EnfuserConsts() {
    // Private constructor
  }
  
}
