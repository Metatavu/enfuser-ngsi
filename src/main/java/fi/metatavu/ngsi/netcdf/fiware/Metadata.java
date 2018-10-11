
package fi.metatavu.ngsi.netcdf.fiware;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "unitCode"
})
public class Metadata {

    @JsonProperty("unitCode")
    private UnitCode unitCode;

    @JsonProperty("unitCode")
    public UnitCode getUnitCode() {
        return unitCode;
    }

    @JsonProperty("unitCode")
    public void setUnitCode(UnitCode unitCode) {
        this.unitCode = unitCode;
    }

}
