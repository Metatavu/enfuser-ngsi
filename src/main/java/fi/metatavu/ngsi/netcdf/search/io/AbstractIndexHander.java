package fi.metatavu.ngsi.netcdf.search.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
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
  private static final String DEFAULT_HOSTS = "192.168.2.125:9200,192.168.2.125:9201";

  @Inject
  private Logger logger;
  
  private String index;
  
  private RestHighLevelClient client;
  
  /**
   * Post construct method
   */
  @PostConstruct
  public void init() {
    String[] hosts = StringUtils.split(getSettingValue(SearchConsts.ELASTIC_HOSTS, DEFAULT_HOSTS), ",");
    String clusterName = getSettingValue(SearchConsts.ELASTIC_CLUSTER_NAME, DEFAULT_CLUSTERNAME);
    index = getSettingValue(SearchConsts.ELASTIC_INDEX, DEFAULT_INDEX);
    client = createTransportClient(hosts, clusterName);
    
    try {
      prepareIndex(client);
    } catch (IOException e) {
      logger.error("Failed to prepare index", e);
    }
    
    setup();
  }
  
  /**
   * Returns setting value
   * 
   * @param key key
   * @param defaultValue default value
   * @return setting value
   */
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
      try {
        closeClient(client);
      } catch (IOException e) {
        logger.error("Failed to close client", e);
      }
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
  protected RestHighLevelClient getClient() {
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
  private RestHighLevelClient createTransportClient(String[] hosts, String clusterName) {
    List<HttpHost> httpHosts = new ArrayList<>(hosts.length);
    
    for (String host : hosts) {
      String[] parts = StringUtils.split(host, ':');
      if (parts.length != 2 || !NumberUtils.isDigits(parts[1])) {
        logger.warn("Invalid elastic search host {}, dropped", host);
      }
      
      String name = parts[0];
      Integer port = NumberUtils.createInteger(parts[1]);
      
      httpHosts.add(new HttpHost(name, port, "http"));
    }
    
    return new RestHighLevelClient(RestClient.builder(httpHosts.toArray(new HttpHost[0])));
  }
  
  /**
   * Closes transport client
   * 
   * @param client transport client
   * @throws IOException 
   */
  private void closeClient(RestHighLevelClient client) throws IOException {
    client.close();
  }
  
  /**
   * Prepares an index
   * 
   * @param client transport client
   * @throws IOException 
   */
  private void prepareIndex(RestHighLevelClient client) throws IOException {
    if (!indexExists(client)) {
      createIndex(client);
    }
  }
  
  /**
   * Checks whether the index exists or not
   * 
   * @param client transport client
   * @return whether the index exists or not
   * @throws IOException 
   */
  private boolean indexExists(RestHighLevelClient client) throws IOException {
    GetIndexRequest request = new GetIndexRequest().indices(getIndex());
    return client.indices().exists(request, RequestOptions.DEFAULT);
  }

  /**
   * Creates an index
   * 
   * @param client transport client
   * @throws IOException 
   */
  private void createIndex(RestHighLevelClient client) throws IOException {
    CreateIndexRequest createIndexRequest = new CreateIndexRequest(getIndex());
    client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
  }
}
