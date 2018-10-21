package fi.metatavu.ngsi.netcdf.search.io;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.metatavu.ngsi.netcdf.search.index.EntryLocation;
import fi.metatavu.ngsi.netcdf.search.index.Indexable;

/**
 * Updater for Elastic Search index 
 */
@ApplicationScoped
@Singleton
@Startup
public class IndexUpdater extends AbstractIndexHander {
  
  @Inject
  private Logger logger;
 
  /**
   * Initializes indexables
   */
  @Override
  public void setup() {
    this.registerIndexable(EntryLocation.class);
  }

  /**
   * Indexes an indexable
   */
  @Lock (LockType.READ)
  public void index(Indexable indexable) {
    if (!isEnabled()) {
      logger.warn("Could not index entity. Search functions are disabled");
      return;
    }

    if (indexable == null) {
      logger.warn("Indexable is null");
      return;
    }
    
    getClient().prepareIndex(getIndex(), indexable.getType(), indexable.getId().toString())
      .setSource(serialize(indexable), XContentType.JSON)
      .execute()
      .actionGet();
  }
  
  /**
   * Removes item from index
   */
  @Lock (LockType.READ)
  public void remove(String type, String id) {
    if (!isEnabled()) {
      logger.warn("Could not remove entity. Search functions are disabled");
      return;
    }
    
    getClient()
      .prepareDelete(getIndex(), type, id)
      .execute()
      .actionGet();
  }
  
  /**
   * Registers an indexable
   * 
   * @param indexable indexable
   */
  private void registerIndexable(Class<? extends Indexable> indexable) {
    Map<String, Map<String, Object>> properties = new HashMap<>();
    
    try {
      Indexable instance = indexable.newInstance();
      readProperties(indexable, properties);
      updateTypeMapping(instance.getType(), properties);
      
    } catch (IntrospectionException e) {
      logger.error("Failed to inspect indexable {}", indexable.getName(), e);
    } catch (InstantiationException | IllegalAccessException e) {
      logger.error("Failed to initialize indexable {}", indexable.getName(), e);
    }
  }

  /**
   * Reads properties from indexable
   * 
   * @param indexable indexable
   * @param properties properties
   */
  @SuppressWarnings("unchecked")
  private void readProperties(Class<? extends Indexable> indexable, Map<String, Map<String, Object>> properties) throws InstantiationException, IllegalAccessException, IntrospectionException {
    BeanInfo beanInfo = Introspector.getBeanInfo(indexable);
    for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
      readPropertyMapping(indexable, properties, propertyDescriptor);
    }
    
    Class<?> superclass = indexable.getSuperclass();
    if (superclass == null || superclass.equals(Object.class)) {
      return;
    }
    
    
    readProperties((Class<? extends Indexable>) indexable.getSuperclass(), properties);
  }

  /**
   * Reads property mapping from indexable
   * 
   * @param indexable indexable
   * @param properties properties
   * @param propertyDescriptor property descriptor
   */
  private void readPropertyMapping(Class<? extends Indexable> indexable, Map<String, Map<String, Object>> properties, PropertyDescriptor propertyDescriptor) {
    String fieldName = propertyDescriptor.getName();
    Field propertyField = getField(indexable, fieldName);
    Method readMethod = propertyDescriptor.getReadMethod();
    
    if (propertyField != null || readMethod != null) {
      fi.metatavu.ngsi.netcdf.search.annotations.Field fieldAnnotation = readMethod.getAnnotation(fi.metatavu.ngsi.netcdf.search.annotations.Field.class);
      
      if (fieldAnnotation == null && propertyField != null) {
        fieldAnnotation = propertyField.getAnnotation(fi.metatavu.ngsi.netcdf.search.annotations.Field.class);
      }
      
      if (fieldAnnotation != null) {
        Map<String, Object> fieldProperties = new HashMap<>();
        fieldProperties.put("type", fieldAnnotation.type());

        if (StringUtils.isNotBlank(fieldAnnotation.analyzer())) {
          fieldProperties.put("analyzer", fieldAnnotation.analyzer());
        }
        
        fieldProperties.put("index", fieldAnnotation.index());
        fieldProperties.put("store", fieldAnnotation.store());
        
        properties.put(fieldName, fieldProperties);
      }
    }
  }

  /**
   * Updates type mapping into Elastic Search
   * 
   * @param type type
   * @param properties properties
   */
  private void updateTypeMapping(String type, Map<String, Map<String, Object>> properties) {
    if (!isEnabled()) {
      logger.warn("Could not update type mapping. Search functions are disabled");
      return;
    }
    
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      Map<String, Map<String, Map<String, Object>>> mapping = new HashMap<>();
      mapping.put("properties", properties);
      String source = objectMapper.writeValueAsString(mapping);
      
      getClient()
        .admin()
        .indices()
        .preparePutMapping(getIndex())
        .setType(type)
        .setSource(source, XContentType.JSON)
        .execute()
        .actionGet();
      
    } catch (JsonProcessingException e) {
      logger.error("Failed to serialize mapping update properties", e);
    }
  }
  
  /**
   * Returns fields from an indexable using reflection API
   * 
   * @param indexable indexable
   * @param fieldName field's name
   * @return field or null if not found
   */
  @SuppressWarnings ("squid:S1166")
  private Field getField(Class<? extends Indexable> indexable, String fieldName) {
    try {
      return indexable.getDeclaredField(fieldName);
    } catch (NoSuchFieldException | SecurityException e) {
      return null;
    }
  }
}