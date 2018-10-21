package fi.metatavu.ngsi.netcdf.api;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

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
  
  private static final String SUPPORTED_TYPE = "AirQualityObserved";
  private static final String[] VIRTUAL_ATTRIBUTES = new String[] {"dateCreated", "dateModified"};
  
  @Inject
  private Logger logger;

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
  public Response listEntities(String id, String type, String idPattern, String typePattern, String q, String mq,
      String georel, String geometry, String coords, Double limit, Double offset, String attrs, String metadata,
      String orderBy, String options) throws Exception {
    
    if (type != null && !SUPPORTED_TYPE.matches(type)) {
      return Response.ok().entity(Collections.emptyList()).build();
    }
    
    if (typePattern != null) {
      Matcher matcher = Pattern.compile(typePattern).matcher(SUPPORTED_TYPE);
      if (matcher == null) {
        return createBadRequest(String.format("Invalid pattern %s", typePattern));
      }
      
      if (!matcher.matches()) {
        return Response.ok().entity(Collections.emptyList()).build();
      }
    }
    
    if (q != null) {
      return createNotImplemented("Parameter q is not supported yet");
    }

    if (mq != null) {
      return createNotImplemented("Parameter mq is not supported yet");
    }
    
    Long firstResult = offset != null ? offset.longValue() : 0l;
    Long maxResults = limit != null ? limit.longValue() : 0l;
    
    // TODO: orderBy
    // TODO: metadata,
    // TODO: options

    List<EntryLocationReference> locationReferences = entryLocationSearcher.searchEntryLocations(
        id, idPattern,
        GeoRel.fromString(georel), Geometry.fromParamName(geometry), Coordinates.fromString(coords),
        firstResult, maxResults);

    File file = new File("/home/belvain/otpdata/enfuser_hkimetro.nc");
    NetcdfFile enfuserFile = NetcdfFile.open(file.getAbsolutePath());
    EnfuserDataReader enfuserDataReader = new EnfuserDataReader(enfuserFile);
    OffsetDateTime time = OffsetDateTime.now();

    List<AirQualityObserved> result = enfuserDataReader.getAirQualityObserved(locationReferences, time);

    return Response.ok().entity(filterResultAttrs(result, attrs, options)).build();
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
  public Response query(String contentType, QueryRequest body, Double limit, Double offset, String orderBy,
      String options) throws Exception {
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
  public Response replaceAllEntityAttributes(String entityId, String contentType,
      ReplaceAllEntityAttributesRequest body, String type, String options) throws Exception {
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
    return createNotSupported();
  }

  @Override
  public Response retrieveSubscription(String subscriptionId) throws Exception {
    return createNotSupported();
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
  public Response updateOrAppendEntityAttributes(String entityId, String contentType,
      UpdateOrAppendEntityAttributesRequest body, String type, String options) throws Exception {
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

  /**
   * Filters results by attrs
   * 
   * @param results results
   * @param attrs  attrs
   * @return filtered results
   */
  private List<AirQualityObserved> filterResultAttrs(List<AirQualityObserved> results, String attrs, String options) {
    return results.stream()
      .map(result -> this.filterResultAttrs(result, attrs, options))
      .collect(Collectors.toList());
  }

  /**
   * Filters result by attrs
   * 
   * @param result result
   * @param attrs attrs
   * @param options options
   * @return filtered result
   */
  private AirQualityObserved filterResultAttrs(AirQualityObserved result, String attrs, String options) {
    if (result == null) {
      return null;
    }

    List<String> includeAttributes = parseCDT(attrs);
    List<String> includeOptions = parseCDT(options);
    
    PropertyDescriptor[] propertyDescriptors;
    try {
      propertyDescriptors = Introspector.getBeanInfo(AirQualityObserved.class).getPropertyDescriptors();
    } catch (IntrospectionException e1) {
      return result;
    }
    
    List<String> excludeAttributes = includeAttributes.isEmpty() ? new ArrayList<>() :  Arrays.stream(propertyDescriptors)
      .map(PropertyDescriptor::getName)
      .filter((attr) -> {
         return !ArrayUtils.contains(VIRTUAL_ATTRIBUTES, attr);
      })
      .filter((attr) -> {
        return !includeAttributes.contains(attr);
      })
      .collect(Collectors.toList());
    
    List<String> excludeOptions = Arrays.stream(VIRTUAL_ATTRIBUTES)
      .filter((attr) -> {
        return !includeOptions.contains(attr);
      })
      .collect(Collectors.toList());
    
    excludeAttributes.addAll(excludeOptions);

    for (String excludedAttribute : excludeAttributes) {
      try {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptor(excludedAttribute, AirQualityObserved.class);
        if (propertyDescriptor != null) {
          Method writeMethod = propertyDescriptor.getWriteMethod();
          Object value = null;
          writeMethod.invoke(result, value);
        }
      } catch (IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        logger.error("Failed to null attribute", e);
      }
    }

    return result;
  }
  
  /**
   * Returns comma delimited text as string list 
   * 
   * @param text text
   * @return string list
   */
  private List<String> parseCDT(String text) {
    if (StringUtils.isBlank(text)) {
      return Collections.emptyList();
    }
    
    return Arrays.asList(StringUtils.split(text, ","));
  }
  
}
