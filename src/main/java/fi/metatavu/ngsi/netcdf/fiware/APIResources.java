package fi.metatavu.ngsi.netcdf.fiware;

import javax.json.bind.annotation.JsonbProperty;

public class APIResources {
  
  @JsonbProperty ("entities_url")
  private String entitiesUrl;

  @JsonbProperty ("types_url")
  private String typesUrl;
  
  @JsonbProperty ("subscriptions_url")
  private String subscriptionsUrl;

  public String getEntitiesUrl() {
    return entitiesUrl;
  }
  
  public void setEntitiesUrl(String entitiesUrl) {
    this.entitiesUrl = entitiesUrl;
  }
  
  public String getSubscriptionsUrl() {
    return subscriptionsUrl;
  }
  
  public void setSubscriptionsUrl(String subscriptionsUrl) {
    this.subscriptionsUrl = subscriptionsUrl;
  }
  
  public String getTypesUrl() {
    return typesUrl;
  }
  
  public void setTypesUrl(String typesUrl) {
    this.typesUrl = typesUrl;
  }
  
}
