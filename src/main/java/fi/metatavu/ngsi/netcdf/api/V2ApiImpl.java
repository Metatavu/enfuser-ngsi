package fi.metatavu.ngsi.netcdf.api;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import fi.metatavu.ngsi.netcdf.api.V2Api;
import fi.metatavu.ngsi.netcdf.api.model.CreateEntityRequest;
import fi.metatavu.ngsi.netcdf.api.model.CreateRegistrationRequest;
import fi.metatavu.ngsi.netcdf.api.model.CreateSubscriptionRequest;
import fi.metatavu.ngsi.netcdf.api.model.NotifyRequest;
import fi.metatavu.ngsi.netcdf.api.model.QueryRequest;
import fi.metatavu.ngsi.netcdf.api.model.ReplaceAllEntityAttributesRequest;
import fi.metatavu.ngsi.netcdf.api.model.UpdateAttributeDataRequest;
import fi.metatavu.ngsi.netcdf.api.model.UpdateAttributeValueRequest;
import fi.metatavu.ngsi.netcdf.api.model.UpdateExistingEntityAttributesRequest;
import fi.metatavu.ngsi.netcdf.api.model.UpdateOrAppendEntityAttributesRequest;
import fi.metatavu.ngsi.netcdf.api.model.UpdateRegistrationRequest;
import fi.metatavu.ngsi.netcdf.api.model.UpdateRequest;
import fi.metatavu.ngsi.netcdf.api.model.UpdateSubscriptionRequest;
import fi.metatavu.ngsi.netcdf.fiware.AirQualityObserved;
import fi.metatavu.ngsi.netcdf.netcdf.EnfuserDataReader;
import fi.metatavu.ngsi.netcdf.netcdf.EntryLocationReference;
import fi.metatavu.ngsi.netcdf.query.Coordinates;
import fi.metatavu.ngsi.netcdf.query.GeoRel;
import fi.metatavu.ngsi.netcdf.query.Geometry;
import fi.metatavu.ngsi.netcdf.search.searcher.EntryLocationSearcher;
import ucar.nc2.NetcdfFile;

@RequestScoped
@Stateful
public class V2ApiImpl extends AbstractApi implements V2Api {

  @Inject
  private EntryLocationSearcher entryLocationSearcher; 
  
  @Override
  public Response createEntity(String contentType, CreateEntityRequest body, String options) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response createRegistration(String contentType, CreateRegistrationRequest body) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response createSubscription(String contentType, CreateSubscriptionRequest body) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response deleteRegistration(String registrationId) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response deleteSubscription(String subscriptionId) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response getAttributeData(String entityId, String attrName, String type, String metadata) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response getAttributeValue(String entityId, String attrName, String type) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listEntities(String id, String type, String idPattern, String typePattern, String q, String mq, String georel, String geometry, String coords, Double limit, Double offset, String attrs, String metadata, String orderBy, String options) throws Exception {
    // TODO: type, id, idPattern, typePattern
    // TODO: q, mq
    // TODO: limit, offset, orderBy
    // TODO: attrs, metadata, 
    // TODO: options
    
    List<EntryLocationReference> locationReferences = entryLocationSearcher.searchEntryLocations(GeoRel.fromString(georel), Geometry.fromParamName(geometry), Coordinates.fromString(coords));
    
    File file = new File("/home/belvain/otpdata/enfuser_hkimetro.nc");
    NetcdfFile enfuserFile = NetcdfFile.open(file.getAbsolutePath());
    EnfuserDataReader enfuserDataReader = new EnfuserDataReader(enfuserFile);
    OffsetDateTime time = OffsetDateTime.now();
    
    List<AirQualityObserved> result = enfuserDataReader.getAirQualityObserved(locationReferences, time);
    
    return Response.ok().entity(result).build();
  }

  @Override
  public Response listEntityTypes(Double limit, Double offset, String options) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listRegistrations(Double limit, Double offset, String options) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response listSubscriptions(Double limit, Double offset, String options) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response notify(String contentType, NotifyRequest body, String options) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response query(String contentType, QueryRequest body, Double limit, Double offset, String orderBy, String options) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response removeASingleAttribute(String entityId, String attrName, String type) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response removeEntity(String entityId, String type) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response replaceAllEntityAttributes(String entityId, String contentType, ReplaceAllEntityAttributesRequest body, String type, String options) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response retrieveAPIResources() throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response retrieveEntity(String entityId, String type, String attrs, String metadata, String options) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response retrieveEntityAttributes(String entityId, String type, String attrs, String metadata, String options) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response retrieveEntityType(String entityType) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response retrieveRegistration(String registrationId) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response retrieveSubscription(String subscriptionId) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Response update(String contentType, UpdateRequest body, String options) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response updateAttributeData(String entityId, String attrName, String contentType, UpdateAttributeDataRequest body, String type) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response updateAttributeValue(String entityId, String attrName, String contentType, UpdateAttributeValueRequest body, String type) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response updateExistingEntityAttributes(String entityId, String contentType, UpdateExistingEntityAttributesRequest body, String type, String options) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response updateOrAppendEntityAttributes(String entityId, String contentType, UpdateOrAppendEntityAttributesRequest body, String type, String options) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response updateRegistration(String registrationId, String contentType, UpdateRegistrationRequest body) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response updateSubscription(String subscriptionId, String contentType, UpdateSubscriptionRequest body) throws Exception {
    return createNotSupported();
  }

}
