package fi.metatavu.ngsi.netcdf.api;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

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
import fi.metatavu.ngsi.netcdf.fiware.APIResources;
import fi.metatavu.ngsi.netcdf.fiware.AirQualityObserved;
import fi.metatavu.ngsi.netcdf.netcdf.EntryLocationReference;
import fi.metatavu.ngsi.netcdf.netcdf.reader.EnfuserDataReader;
import fi.metatavu.ngsi.netcdf.netcdf.reader.EnfuserDataReaderProvider;
import fi.metatavu.ngsi.netcdf.query.Coordinates;
import fi.metatavu.ngsi.netcdf.query.GeoRel;
import fi.metatavu.ngsi.netcdf.query.Geometry;
import fi.metatavu.ngsi.netcdf.query.SimpleQuery;
import fi.metatavu.ngsi.netcdf.query.SimpleQueryItem;
import fi.metatavu.ngsi.netcdf.search.searcher.EntryLocationSearcher;

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
    if (type != null && !SUPPORTED_TYPE.matches(type)) {
      return createNotFound("Not found");
    }
    
    AirQualityObserved entry = findAirQualityObservedById(entityId);
    if (entry == null) {
      return createNotFound("Not found");
    }
    
    return Response.ok().entity(getObjectValue(entry, AirQualityObserved.class, attrName)).build();
  }

  @Override
  public Response getAttributeValue(String entityId, String attrName, String type) throws Exception {
    if (type != null && !SUPPORTED_TYPE.matches(type)) {
      return createNotFound("Not found");
    }
    
    AirQualityObserved entry = findAirQualityObservedById(entityId);
    if (entry == null) {
      return createNotFound("Not found");
    }
    
    Object attr = getObjectValue(entry, AirQualityObserved.class, attrName);
    Object value = attr == null ? null : getObjectValue(attr, attr.getClass(), "value");

    if (value == null) {
      return createNoContent();
    }
    
    return Response.ok().entity(value).build();    
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

    OffsetDateTime observedBefore = null;
    OffsetDateTime observedAfter = null;

    SimpleQuery query = SimpleQuery.fromString(q);
    for (SimpleQueryItem queryItem : query.getItems()) {
      if ("dateObserved".equals(queryItem.getLhs())) {
        OffsetDateTime rhsTime = OffsetDateTime.parse(queryItem.getRhs());
        if (rhsTime == null) {
          return createNotImplemented(String.format("Invalid valud %s for attribute %s", queryItem.getRhs(), queryItem.getLhs()));
        }
        
        switch (queryItem.getOp()) {
          case GREATER_THAN_OR_EQUAL:
            observedAfter = rhsTime;
          break;
          case LESS_THAN_OR_EQUAL:
            observedBefore = rhsTime;
          break;
          default:
            return createNotImplemented(String.format("Operator %s not supported for q", queryItem.getOp()));
        }
      } else {
        return createNotImplemented(String.format("Using attribute %s is not supported in q", queryItem.getLhs()));
      }
    }
    
    if (mq != null) {
      return createNotImplemented("Parameter mq is not supported yet");
    }

    if (orderBy != null) {
      return createNotImplemented("Parameter orderBy is not supported yet");
    }

    if (metadata != null) {
      return createNotImplemented("Parameter orderBy is not supported yet");
    }

    Long firstResult = offset != null ? offset.longValue() : 0l;
    Long maxResults = limit != null ? limit.longValue() : 20l;

    List<EntryLocationReference> locationReferences = entryLocationSearcher.searchEntryLocations(
        id, idPattern,
        GeoRel.fromString(georel), Geometry.fromParamName(geometry), Coordinates.fromString(coords),
        firstResult, maxResults);

    EnfuserDataReader enfuserDataReader = EnfuserDataReaderProvider.getReader(null);
    
    List<AirQualityObserved> result = enfuserDataReader.getAirQualityObserved(locationReferences, observedBefore, observedAfter);

    return Response.ok().entity(filterResultAttrs(result, attrs, options)).build();
  }

  @Override
  public Response listEntityTypes(Double limit, Double offset, String options) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response listRegistrations(Double limit, Double offset, String options) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response listSubscriptions(Double limit, Double offset, String options) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response notify(String contentType, NotifyRequest body, String options) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response query(String contentType, QueryRequest body, Double limit, Double offset, String orderBy, String options) throws Exception {
    return createNotSupported();
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
    return createNotSupported();
  }

  @Override
  public Response retrieveEntity(String entityId, String type, String attrs, String metadata, String options) throws Exception {
    APIResources resources = new APIResources();
    
    resources.setEntitiesUrl("/v2/entities");
    resources.setSubscriptionsUrl("/v2/types");
    resources.setTypesUrl("/v2/subscriptions");
      
    return Response.ok().entity(resources).build();
  }

  @Override
  public Response retrieveEntityAttributes(String entityId, String type, String attrs, String metadata, String options) throws Exception {
    return createNotSupported();
  }

  @Override
  public Response retrieveEntityType(String entityType) throws Exception {
    return createNotSupported();
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
  
  private Object getObjectValue(Object entity, Class<?> objectClass, String propertyName) {
    if (entity == null || propertyName == null) {
      return null;
    }
    
    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(objectClass, propertyName);
    if (propertyDescriptor == null) {
      return null;
    }
    
    try {
      return propertyDescriptor.getReadMethod().invoke(entity);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      logger.error("Failed to get attribute value");
    }
    
    return null;
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
    PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(AirQualityObserved.class);
    
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
   * Returns property descriptor for an object
   * 
   * @param objectClass object class
   * @param propertyName property name
   * @return property descriptor or null if not found
   */
  private PropertyDescriptor getPropertyDescriptor(Class<?> objectClass, String propertyName) {
    PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(objectClass);
    for (int i = 0; i < propertyDescriptors.length; i++) {
      if (propertyName.equals(propertyDescriptors[i].getName())) {
        return propertyDescriptors[i];
      }
    }
    
    return null;
  }
  
  /**
   * Returns property descriptors for an entity
   * 
   * @return property descriptors
   */
  private PropertyDescriptor[] getPropertyDescriptors(Class<?> objectClass) {
    try {
      return Introspector.getBeanInfo(objectClass).getPropertyDescriptors();
    } catch (IntrospectionException e1) {
      logger.error("Failed to resolve property descriptors");
    }
    
    return new PropertyDescriptor[0];
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

  /**
   * Finds air quality observed by id
   * 
   * @param id id
   * @return found entity or null if not found
   */
  private AirQualityObserved findAirQualityObservedById(String id) {
    EnfuserDataReader enfuserDataReader = EnfuserDataReaderProvider.getReader(null);
    OffsetDateTime time = OffsetDateTime.now();

    try {
      List<EntryLocationReference> locationReferences = entryLocationSearcher.searchEntryLocations(id, null, null, null, null, 0l, 1l);
      List<AirQualityObserved> result = enfuserDataReader.getAirQualityObserved(locationReferences, null, null);

      if (!result.isEmpty()) {
        return result.get(0);
      }
    } catch (Exception e) {
      logger.error("Failed to find air quality entry", e);
    } 

    return null;
  }
  
}
