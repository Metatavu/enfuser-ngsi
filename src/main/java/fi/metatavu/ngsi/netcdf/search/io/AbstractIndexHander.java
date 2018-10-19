package fi.metatavu.ngsi.netcdf.search.io;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.metatavu.ngsi.netcdf.search.SearchConsts;
import fi.metatavu.ngsi.netcdf.search.index.Indexable;

/**
 * Abstract base class for index io handlerse
 */
public abstract class AbstractIndexHander {
  
  private static final String DEFAULT_INDEX = "enfuser-ngsi";
  private static final String DEFAULT_CLUSTERNAME = "elasticsearch";
  private static final String DEFAULT_HOSTS = "localhost:9300";

  @Inject
  private Logger logger;
  
  private String index;
  
  private TransportClient client;
  
  /**
   * Post construct method
   */
  @PostConstruct
  public void init() {
    String[] hosts = StringUtils.split(getSettingValue(SearchConsts.ELASTIC_HOSTS, DEFAULT_HOSTS));
    String clusterName = getSettingValue(SearchConsts.ELASTIC_CLUSTER_NAME, DEFAULT_CLUSTERNAME);
    index = getSettingValue(SearchConsts.ELASTIC_INDEX, DEFAULT_INDEX);
    client = createTransportClient(hosts, clusterName);
    setup();
  }
  
  private String getSettingValue(String key, String defaultValue) {
    String result = System.getProperty(key);
    if (StringUtils.isBlank(result)) {
      return defaultValue;
    }
    
    return result;
  }
  
  /**
   * Pre destroy method
   */
  @PreDestroy
  public void deinit() {
    if (client != null) {
      closeClient(client);
    }
  }
  
  /**
   * Returns whether searching is enabled or not 
   */
  public boolean isEnabled() {
    return client != null;
  }
  
  /**
   * Setup method
   */
  public abstract void setup();
  
  /**
   * Returns elastic search client
   * 
   * @return elastic search client
   */
  protected Client getClient() {
    return client;
  }
  
  /**
   * Serializes indexable
   * 
   * @param indexable indexable
   * @return serialized indeable
   */
  protected String serializeString(Indexable indexable) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      return objectMapper.writeValueAsString(indexable);
    } catch (JsonProcessingException e) {
      logger.error("Failed to serialize indexable object", e);
    }
    
    return null;
  }
  
  /**
   * Serializes indexable
   * 
   * @param indexable indexable
   * @return serialized indeable
   */
  protected byte[] serialize(Indexable indexable) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      return objectMapper.writeValueAsBytes(indexable);
    } catch (JsonProcessingException e) {
      logger.error("Failed to serialize indexable object", e);
    }
    
    return new byte[0];
  }
  
  /**
   * Returns index
   * 
   * returns index
   */
  protected String getIndex() {
    return index;
  }
  
  /**
   * Creates initialized transport client
   * 
   * @return initialized transport client
   */
  private TransportClient createTransportClient(String[] hosts, String clusterName) {
    try {
      TransportClient transportClient;
      Settings settings = Settings.builder()
        .put("cluster.name", clusterName)
        .build();
      
      transportClient = new PreBuiltTransportClient(settings);
      
      for (String host : hosts) {
        String[] parts = StringUtils.split(host, ':');
        if (parts.length != 2 || !NumberUtils.isDigits(parts[1])) {
          logger.warn("Invalid elastic search host {}, dropped", host);
        }
        
        String name = parts[0];
        Integer port = NumberUtils.createInteger(parts[1]);
        transportClient.addTransportAddress(resolveTransportAddress(name, port));
      }
  
      prepareIndex(transportClient);

      return transportClient;
    } catch (Exception e) {
      logger.error("Elastic client creation failed. All search functions are disbled", e);
    }

    return null;
  }
  
  /**
   * Returns transport address for name and port
   * 
   * @param name name
   * @param port port
   * @return transport address for name and port
   * @throws UnknownHostException 
   */
  private TransportAddress resolveTransportAddress(String name, int port) {
    return new TransportAddress(resolveInetAddress(name), port);
  }
  
  /**
   * Resolves inet address for a name
   * 
   * @param name
   * @return inet address
   */
  private InetAddress resolveInetAddress(String name) {
    try {
      return InetAddress.getByName(name);
    } catch (UnknownHostException e) {
      logger.warn("Could resolve address {}, falling back to localhost", name, e);
    }
    
    try {
      return InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      logger.error("Could not resolve localhost either", e);
    }
    
    return null;
  }
  
  /**
   * Closes transport client
   * 
   * @param transportClient transport client
   */
  private void closeClient(TransportClient transportClient) {
    transportClient.close();
  }
  
  /**
   * Prepares an index
   * 
   * @param transportClient transport client
   */
  private void prepareIndex(TransportClient transportClient) {
    if (!indexExists(transportClient)) {
      createIndex(transportClient);
    }
  }
  
  /**
   * Checks whether the index exists or not
   * 
   * @param transportClient transport client
   * @return whether the index exists or not
   */
  private boolean indexExists(TransportClient transportClient) {
    return transportClient
      .admin()
      .indices()
      .prepareExists(getIndex())
      .execute()
      .actionGet()
      .isExists();
  }

  /**
   * Creates an index
   * 
   * @param transportClient transport client
   */
  private void createIndex(TransportClient transportClient) {
    transportClient
      .admin()
      .indices()
      .prepareCreate(getIndex())
      .execute()
      .actionGet();
  }
}
