
package fi.metatavu.ngsi.netcdf.fiware;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "value"
})
public class Address {

    @JsonProperty("type")
    private String type;
    @JsonProperty("value")
    private AddressValue value;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("value")
    public AddressValue getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(AddressValue value) {
        this.value = value;
    }

}
