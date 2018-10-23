package fi.metatavu.ngsi.netcdf.netcdf;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.metatavu.ngsi.netcdf.SystemConsts;
import fi.metatavu.ngsi.netcdf.fiware.AirPollutant;
import fi.metatavu.ngsi.netcdf.fiware.AirQualityLevel;
import fi.metatavu.ngsi.netcdf.fiware.AirQualityObserved;
import fi.metatavu.ngsi.netcdf.fiware.DateTime;
import fi.metatavu.ngsi.netcdf.fiware.Location;
import fi.metatavu.ngsi.netcdf.fiware.LocationValue;
import fi.metatavu.ngsi.netcdf.fiware.Source;
import ucar.ma2.Array;
import ucar.ma2.ArrayFloat;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import ucar.nc2.units.DateUnit;

public class EnfuserDataReader {

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
  
  public EnfuserDataReader() {
    this.no2VariableName = System.getProperty(EnfuserConsts.NO2_VARIABLE_PROPERTY);
    this.pm10VariableName = System.getProperty(EnfuserConsts.PM10_VARIABLE_PROPERTY);
    this.pm25VariableName = System.getProperty(EnfuserConsts.PM25_VARIABLE_PROPERTY);
    this.aqiVariableName = System.getProperty(EnfuserConsts.AQI_VARIABLE_PROPERTY);
    this.o3VariableName = System.getProperty(EnfuserConsts.O3_VARIABLE_PROPERTY);

    try {
      File file = new File(System.getProperty(SystemConsts.INPUT_FILE_PROPERTY));
      this.file = NetcdfFile.open(file.getAbsolutePath());
    } catch (IOException e) {
      logger.error("Failed to read input file");
      System.exit(-1);
    }
  }
  
  public List<AirQualityObserved> getAirQualityObserved(List<EntryLocationReference> locationReferences, OffsetDateTime beforeTime, OffsetDateTime afterTime) throws Exception {
    Variable timeVariable = getVariable(timeVariableName);
    OffsetDateTime originTime = getOriginTime(timeVariable);
    
    int fromTimeIndex;
    int toTimeIndex;
    
    if (beforeTime != null || afterTime != null) {
      fromTimeIndex = afterTime != null ? getTimeIndex(originTime, afterTime) : 0;
      toTimeIndex = beforeTime != null ? getTimeIndex(originTime, beforeTime) : timeVariableName.length() - 1;
    } else {
      OffsetDateTime now = OffsetDateTime.now();
      fromTimeIndex = getTimeIndexClosestTo(originTime, now);
      toTimeIndex = getTimeIndexClosestTo(originTime, now);
    }

    List<AirQualityObserved> result = new ArrayList<>();
    
    for (int timeIndex = fromTimeIndex; timeIndex <= toTimeIndex; timeIndex++) {
      result.addAll(getAirQualityObservedForTime(locationReferences, originTime, timeIndex));
    }
    
    return result;
  }

  private List<AirQualityObserved> getAirQualityObservedForTime(List<EntryLocationReference> locationReferences, OffsetDateTime originTime, int timeIndex) {
    OffsetDateTime sampleTime = originTime.plusHours(timeIndex);
    
    return locationReferences.stream()
      .map((locationReference) -> {
        return getAirQualityObserved(locationReference, originTime, sampleTime, timeIndex);
      })
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }
  
  private AirQualityObserved getAirQualityObserved(EntryLocationReference locationReference, OffsetDateTime originTime, OffsetDateTime sampleTime, int timeIndex) {
    try {
      Float no2 = getComponentValue(no2VariableName, locationReference, timeIndex);
      Float pm10 = getComponentValue(pm10VariableName, locationReference, timeIndex);
      Float pm25 = getComponentValue(pm25VariableName, locationReference, timeIndex);
      Float o3 = getComponentValue(o3VariableName, locationReference, timeIndex);
      Float aqi = getComponentValue(aqiVariableName, locationReference, timeIndex);
      
      return createAirQualityObserved(locationReference, originTime, sampleTime, no2, pm10, pm25, o3, aqi);
    } catch (Exception e) {
      logger.error("Failed to resolve AirQualityObserved for location %s", locationReference);
      return null;
    }
  }

  private AirQualityObserved createAirQualityObserved(EntryLocationReference locationReference, OffsetDateTime originTime, OffsetDateTime sampleTime, Float no2, Float pm10, Float pm25, Float o3, Float aqi) {
    AirQualityObserved airQualityObserved = new AirQualityObserved();
    airQualityObserved.setId(String.format(EnfuserConsts.ID_PATTERN, locationReference.getLatIndex(), locationReference.getLonIndex()));
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

  public Array getLatitudeArray() throws IOException {
    Variable variable = getVariable(latitudeVariableName);
    return variable.read();
  }

  public Array getLongitudeArray() throws IOException {
    Variable variable = getVariable(longitudeVariableName);
    return variable.read();
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

  private Float getComponentValue(String variableName, EntryLocationReference locationReference, int timeIndex) throws IOException, InvalidRangeException {
    try {
      Variable variable = getVariable(variableName);
      if (variable == null) {
        return null; 
      }
  
      int[] origin = new int[] { timeIndex, locationReference.getLatIndex(), locationReference.getLonIndex() };
      int[] shape = new int[] { 1, 1, 1 };
      ArrayFloat.D3 array = (ucar.ma2.ArrayFloat.D3) variable.read(origin, shape);
      
      return array.get(0, 0, 0);
    } catch (Exception e) {
      return null;
    }
  }
  
  private int getTimeIndex(OffsetDateTime originTime, OffsetDateTime requestTime) throws Exception {  
    return (int) ChronoUnit.HOURS.between(originTime, requestTime);
  }
  
  /**
   * Returns closest available time index for a time
   * 
   * @param originTime NetCDF start time
   * @param requestTime requested time
   * @return closest time index for requested time
   * @throws Exception thrown when time resolving fails
   */
  private int getTimeIndexClosestTo(OffsetDateTime originTime, OffsetDateTime requestTime) throws Exception {
    int airQualityHour = getTimeIndex(originTime, requestTime);
    int maxIndex = timeVariableName.length() - 1;
    
    if (airQualityHour > maxIndex) {
      return maxIndex;
    }
    
    if (airQualityHour < 0) {
      return 0;
    }
    
    return airQualityHour;    
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
 
}
