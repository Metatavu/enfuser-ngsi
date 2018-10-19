
package fi.metatavu.ngsi.netcdf.fiware;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "type",
    "dateObserved",
    "airQualityLevel",
    "temperature",
    "refPointOfInterest",
    "windDirection",
    "source",
    "windSpeed",
    "location",
    "address",
    "reliability",
    "relativeHumidity",
    "precipitation",
    "CO_Level"
})
public class AirQualityObserved {

    @JsonProperty("id")
    private String id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("dateCreated")
    private DateTime dateCreated;
    @JsonProperty("dateModified")
    private DateTime dateModified;
    @JsonProperty("dateObserved")
    private DateTime dateObserved;
    @JsonProperty("airQualityLevel")
    private AirQualityLevel airQualityLevel;
    @JsonProperty("CO")
    private AirPollutant co;
    @JsonProperty("temperature")
    private Temperature temperature;
    @JsonProperty("NO")
    private AirPollutant no;
    @JsonProperty("refPointOfInterest")
    private RefPointOfInterest refPointOfInterest;
    @JsonProperty("windDirection")
    private WindDirection windDirection;
    @JsonProperty("source")
    private Source source;
    @JsonProperty("windSpeed")
    private WindSpeed windSpeed;
    @JsonProperty("SO2")
    private AirPollutant so2;
    @JsonProperty("NOx")
    private AirPollutant nox;
    @JsonProperty("location")
    private Location location;
    @JsonProperty("airQualityIndex")
    private AirPollutant airQualityIndex;
    @JsonProperty("address")
    private Address address;
    @JsonProperty("reliability")
    private Reliability reliability;
    @JsonProperty("relativeHumidity")
    private RelativeHumidity relativeHumidity;
    @JsonProperty("precipitation")
    private Precipitation precipitation;
    @JsonProperty("NO2")
    private AirPollutant no2;
    @JsonProperty("CO_Level")
    private COLevel coLevel;
    @JsonProperty("O3")
    private AirPollutant o3;
    @JsonProperty("PM10")
    private AirPollutant pm10;
    @JsonProperty("PM25")
    private AirPollutant pm25;
    
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }
    
    public DateTime getDateCreated() {
      return dateCreated;
    }
    
    public void setDateCreated(DateTime dateCreated) {
      this.dateCreated = dateCreated;
    }
    
    public DateTime getDateModified() {
      return dateModified;
    }
    
    public void setDateModified(DateTime dateModified) {
      this.dateModified = dateModified;
    }

    @JsonProperty("dateObserved")
    public DateTime getDateObserved() {
        return dateObserved;
    }

    @JsonProperty("dateObserved")
    public void setDateObserved(DateTime dateObserved) {
        this.dateObserved = dateObserved;
    }

    @JsonProperty("airQualityLevel")
    public AirQualityLevel getAirQualityLevel() {
        return airQualityLevel;
    }

    @JsonProperty("airQualityLevel")
    public void setAirQualityLevel(AirQualityLevel airQualityLevel) {
        this.airQualityLevel = airQualityLevel;
    }
    
    @JsonProperty("CO")
    public AirPollutant getCo() {
      return co;
    }
    
    @JsonProperty("CO")
    public void setCo(AirPollutant co) {
      this.co = co;
    }
    
    @JsonProperty("temperature")
    public Temperature getTemperature() {
        return temperature;
    }

    @JsonProperty("temperature")
    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }

    @JsonProperty("NO")
    public AirPollutant getNo() {
      return no;
    }
    
    @JsonProperty("NO")
    public void setNo(AirPollutant no) {
      this.no = no;
    }

    @JsonProperty("refPointOfInterest")
    public RefPointOfInterest getRefPointOfInterest() {
        return refPointOfInterest;
    }

    @JsonProperty("refPointOfInterest")
    public void setRefPointOfInterest(RefPointOfInterest refPointOfInterest) {
        this.refPointOfInterest = refPointOfInterest;
    }

    @JsonProperty("windDirection")
    public WindDirection getWindDirection() {
        return windDirection;
    }

    @JsonProperty("windDirection")
    public void setWindDirection(WindDirection windDirection) {
        this.windDirection = windDirection;
    }

    @JsonProperty("source")
    public Source getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(Source source) {
        this.source = source;
    }

    @JsonProperty("windSpeed")
    public WindSpeed getWindSpeed() {
        return windSpeed;
    }

    @JsonProperty("windSpeed")
    public void setWindSpeed(WindSpeed windSpeed) {
        this.windSpeed = windSpeed;
    }

    @JsonProperty("SO2")
    public AirPollutant getSo2() {
      return so2;
    }
    
    @JsonProperty("SO2")
    public void setSo2(AirPollutant so2) {
      this.so2 = so2;
    }

    @JsonProperty("NOx")
    public AirPollutant getNox() {
      return nox;
    }

    @JsonProperty("NOx")
    public void setNox(AirPollutant nox) {
      this.nox = nox;
    }

    @JsonProperty("location")
    public Location getLocation() {
        return location;
    }

    @JsonProperty("location")
    public void setLocation(Location location) {
        this.location = location;
    }

    @JsonProperty("airQualityIndex")
    public AirPollutant getAirQualityIndex() {
        return airQualityIndex;
    }

    @JsonProperty("airQualityIndex")
    public void setAirQualityIndex(AirPollutant airQualityIndex) {
        this.airQualityIndex = airQualityIndex;
    }

    @JsonProperty("address")
    public Address getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(Address address) {
        this.address = address;
    }

    @JsonProperty("reliability")
    public Reliability getReliability() {
        return reliability;
    }

    @JsonProperty("reliability")
    public void setReliability(Reliability reliability) {
        this.reliability = reliability;
    }

    @JsonProperty("relativeHumidity")
    public RelativeHumidity getRelativeHumidity() {
        return relativeHumidity;
    }

    @JsonProperty("relativeHumidity")
    public void setRelativeHumidity(RelativeHumidity relativeHumidity) {
        this.relativeHumidity = relativeHumidity;
    }

    @JsonProperty("precipitation")
    public Precipitation getPrecipitation() {
        return precipitation;
    }

    @JsonProperty("precipitation")
    public void setPrecipitation(Precipitation precipitation) {
        this.precipitation = precipitation;
    }

    @JsonProperty("NO2")
    public AirPollutant getNo2() {
      return no2;
    }

    @JsonProperty("NO2")
    public void setNo2(AirPollutant no2) {
      this.no2 = no2;
    }

    @JsonProperty("CO_Level")
    public COLevel getCoLevel() {
      return coLevel;
    }

    @JsonProperty("CO_Level")
    public void setCoLevel(COLevel coLevel) {
      this.coLevel = coLevel;
    }

    public AirPollutant getO3() {
      return o3;
    }
    
    public void setO3(AirPollutant o3) {
      this.o3 = o3;
    }
    
    public AirPollutant getPm10() {
      return pm10;
    }
    
    public void setPm10(AirPollutant pm10) {
      this.pm10 = pm10;
    }
    
    public AirPollutant getPm25() {
      return pm25;
    }
    
    public void setPm25(AirPollutant pm25) {
      this.pm25 = pm25;
    }
    
}
