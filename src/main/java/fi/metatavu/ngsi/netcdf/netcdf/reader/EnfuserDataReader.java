package fi.metatavu.ngsi.netcdf.netcdf.reader;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.metatavu.ngsi.netcdf.fiware.AirPollutant;
import fi.metatavu.ngsi.netcdf.fiware.AirQualityLevel;
import fi.metatavu.ngsi.netcdf.fiware.AirQualityObserved;
import fi.metatavu.ngsi.netcdf.fiware.DateTime;
import fi.metatavu.ngsi.netcdf.fiware.Location;
import fi.metatavu.ngsi.netcdf.fiware.LocationValue;
import fi.metatavu.ngsi.netcdf.fiware.Source;
import fi.metatavu.ngsi.netcdf.netcdf.EnfuserConsts;
import fi.metatavu.ngsi.netcdf.netcdf.EntryLocationReference;
import ucar.ma2.Array;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.units.DateUnit;

public class EnfuserDataReader implements AutoCloseable {

  private static final String SOURCE = "https://en.ilmatieteenlaitos.fi/environmental-information-fusion-service";
  private static final String TYPE = "AirQualityObserved";
  
  private static Logger logger = LoggerFactory.getLogger(EnfuserDataReader.class); 
  
  private NetcdfFile file;
  private String timeVariableName = "time";
  private String latitudeVariableName = "lat";
  private String longitudeVariableName = "lon";
  private String no2VariableName;
  private String pm10VariableName;
  private String pm25VariableName;
  private String aqiVariableName;
  private String o3VariableName;
  
  EnfuserDataReader(File file) {
    this.no2VariableName = System.getProperty(EnfuserConsts.NO2_VARIABLE_PROPERTY);
    this.pm10VariableName = System.getProperty(EnfuserConsts.PM10_VARIABLE_PROPERTY);
    this.pm25VariableName = System.getProperty(EnfuserConsts.PM25_VARIABLE_PROPERTY);
    this.aqiVariableName = System.getProperty(EnfuserConsts.AQI_VARIABLE_PROPERTY);
    this.o3VariableName = System.getProperty(EnfuserConsts.O3_VARIABLE_PROPERTY);
    
    try {
      this.file = NetcdfFile.open(file.getAbsolutePath());
    } catch (IOException e) {
      logger.error("Failed to open NetCDF file", e);
    }
  }
  
  public AirQualityObserved getAirQualityObserved(EntryLocationReference locationReference) throws Exception {
    Variable timeVariable = getVariable(timeVariableName);
    OffsetDateTime originTime = getOriginTime(timeVariable);

    return getAirQualityObserved(locationReference, originTime, originTime.plusHours(locationReference.getTimeIndex()));
  }
  
  private AirQualityObserved getAirQualityObserved(EntryLocationReference locationReference, OffsetDateTime originTime, OffsetDateTime sampleTime) {
    try {
      Float no2 = getComponentValue(no2VariableName, locationReference);
      Float pm10 = getComponentValue(pm10VariableName, locationReference);
      Float pm25 = getComponentValue(pm25VariableName, locationReference);
      Float o3 = getComponentValue(o3VariableName, locationReference);
      Float aqi = getComponentValue(aqiVariableName, locationReference);
      
      return createAirQualityObserved(locationReference, originTime, sampleTime, no2, pm10, pm25, o3, aqi);
    } catch (Exception e) {
      logger.error("Failed to resolve AirQualityObserved for location %s", locationReference);
      return null;
    }
  }

  private AirQualityObserved createAirQualityObserved(EntryLocationReference locationReference, OffsetDateTime originTime, OffsetDateTime sampleTime, Float no2, Float pm10, Float pm25, Float o3, Float aqi) {
    AirQualityObserved airQualityObserved = new AirQualityObserved();
    airQualityObserved.setId(String.format(EnfuserConsts.ID_PATTERN, locationReference.getLatIndex(), locationReference.getLonIndex(), locationReference.getTimeIndex()));
    airQualityObserved.setAirQualityIndex(createAirPollutant(aqi));
    airQualityObserved.setNo2(createAirPollutant(no2));
    airQualityObserved.setPm10(createAirPollutant(pm10));
    airQualityObserved.setPm25(createAirPollutant(pm25));
    airQualityObserved.setO3(createAirPollutant(o3));
    airQualityObserved.setAirQualityLevel(createAirQualityLevel(aqi));
    airQualityObserved.setSource(createSource());
    airQualityObserved.setType(TYPE);
    airQualityObserved.setDateCreated(createDateObserved(originTime));
    airQualityObserved.setDateModified(createDateObserved(originTime));
    airQualityObserved.setDateObserved(createDateObserved(sampleTime));
    airQualityObserved.setLocation(createLoaction(locationReference));
    
    return airQualityObserved;
  }

  /**
   * Reads latitude array from file
   * 
   * @return Latitude array
   */
  public ArrayFloat.D1 getLatitudeArray() {
    Variable variable = getVariable(latitudeVariableName);
    return (ArrayFloat.D1) readArray(variable);
  }

  /**
   * Reads longitude array from file
   * 
   * @return Longitude array
   */
  public ArrayFloat.D1 getLongitudeArray() {
    Variable variable = getVariable(longitudeVariableName);
    return (ArrayFloat.D1) readArray(variable);
  }

  /**
   * Reads time array from file
   * 
   * @return time array
   */
  public ArrayInt.D1 getTimeArray() {
    Variable variable = getVariable(timeVariableName);
    return (ArrayInt.D1) readArray(variable);
  }

  /**
   * Returns origin time of the NetCDF file
   * 
   * @return origin time of the NetCDF file
   */
  public OffsetDateTime getOriginTime() {
    try {
      return getOriginTime(getVariable(timeVariableName));
    } catch (Exception e) {
      logger.error("Failed to resolve origin time", e);
      return null;
    }
  }
  
  private Location createLoaction(EntryLocationReference locationReference) {
    try {
      Float latitude = getSingleFloat(getVariable(latitudeVariableName), locationReference.getLatIndex());
      if (latitude == null) {
        return null;
      }
      
      Float longitude = getSingleFloat(getVariable(longitudeVariableName), locationReference.getLonIndex());
      if (longitude == null) {
        return null;
      }
      
      LocationValue value = new LocationValue();
      value.setType("Point");
      value.setCoordinates(Arrays.asList(longitude.doubleValue(), latitude.doubleValue()));
      
      Location result = new Location();
      result.setValue(value);
      result.setType("geo:json");
      
      return result;
    } catch (Exception e) {
      logger.error("Failed to resolve reference {} coordinates", locationReference, e);
    }
     
    // TODO Auto-generated method stub
    return null;
  }
  
  /**
   * Reads array from variable
   * 
   * @param variable variable to read
   * @return Array of values or null if unsuccesfull
   */
  private Array readArray(Variable variable) {
    try {
      return variable.read(null, variable.getShape());
    } catch (IOException | InvalidRangeException e) {
      logger.error("Error reading array from variable", e);
    }

    return null;
  }

  private Float getSingleFloat(Variable variable, int index) throws IOException, InvalidRangeException {
    ArrayFloat.D1 latitudeArray = (ArrayFloat.D1) variable.read(new int[] { index }, new int[] { 1 });
    if (latitudeArray.getSize() > 0) {
      return latitudeArray.getFloat(0);
    }
    
    return null;
  }

  private DateTime createDateObserved(OffsetDateTime originTime) {
    DateTime result = new DateTime();
    result.setValue(originTime.format(DateTimeFormatter.ISO_DATE_TIME));
    return result;
  }

  private AirQualityLevel createAirQualityLevel(Float aqi) {
    String airQualityLevel = getAirQualityLevel(aqi);
    
    if (airQualityLevel != null) {
      AirQualityLevel result = new AirQualityLevel();
      result.setValue(airQualityLevel);
      return result; 
    }
    
    return null;
  }

  private Source createSource() {
    Source result = new Source();
    result.setValue(SOURCE);
    return result;
  }

  private AirPollutant createAirPollutant(Float value) {
    if (value != null) {
      AirPollutant result = new AirPollutant();
      result.setValue(value);
      return result;
    }
    
    return null;
  }

  private Float getComponentValue(String variableName, EntryLocationReference locationReference) throws IOException, InvalidRangeException {
    try {
      Variable variable = getVariable(variableName);
      if (variable == null) {
        return null; 
      }

      int[] origin = new int[] { locationReference.getTimeIndex(), locationReference.getLatIndex(), locationReference.getLonIndex() };
      int[] shape = new int[] { 1, 1, 1 };
      ArrayFloat.D3 array = (ucar.ma2.ArrayFloat.D3) variable.read(origin, shape);
      
      return array.get(0, 0, 0);
    } catch (Exception e) {
      return null;
    }
  }
  
  /**
   * Returns air quality level for an air quality index
   * 
   * @param aqi air quality index
   * @return air quality level
   */
  private String getAirQualityLevel(Float aqi) {
    if (aqi == null) {
      return null;
    }
    
    if (aqi > 4) {
      return "veryUnhealthy";
    }
    
    if (aqi > 3) {
      return "unhealthy";
    }
    
    if (aqi > 2) {
      return "moderate";
    }
    
    return "good";
  }
  
  /**
   * Returns origin time of the NetCDF file
   * 
   * @param timeVariable time variable name
   * @return origin time of the NetCDF file
   * @throws Exception throw when time resolving fails
   */
  private OffsetDateTime getOriginTime(Variable variable) throws Exception {
    DateUnit dateUnit = new DateUnit(variable.getUnitsString());
    Date dateOrigin = dateUnit.getDateOrigin();
    
    return OffsetDateTime.ofInstant(dateOrigin.toInstant(), ZoneId.systemDefault());
  }

  /**
   * Finds variable by name
   * 
   * @param variableName variable name
   * @return variable or null if not found
   */
  private Variable getVariable(String variableName) {
    return file.findVariable(variableName);
  }

  @Override
  public void close() throws IOException {
    file.close();
  }
 
}
