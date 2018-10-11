
package fi.metatavu.ngsi.netcdf.fiware;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "addressCountry",
    "addressLocality",
    "streetAddress"
})
public class AddressValue {

    @JsonProperty("addressCountry")
    private String addressCountry;
    @JsonProperty("addressLocality")
    private String addressLocality;
    @JsonProperty("streetAddress")
    private String streetAddress;

    @JsonProperty("addressCountry")
    public String getAddressCountry() {
        return addressCountry;
    }

    @JsonProperty("addressCountry")
    public void setAddressCountry(String addressCountry) {
        this.addressCountry = addressCountry;
    }

    @JsonProperty("addressLocality")
    public String getAddressLocality() {
        return addressLocality;
    }

    @JsonProperty("addressLocality")
    public void setAddressLocality(String addressLocality) {
        this.addressLocality = addressLocality;
    }

    @JsonProperty("streetAddress")
    public String getStreetAddress() {
        return streetAddress;
    }

    @JsonProperty("streetAddress")
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

}
