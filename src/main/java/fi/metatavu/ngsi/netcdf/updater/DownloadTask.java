package fi.metatavu.ngsi.netcdf.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.metatavu.ngsi.netcdf.SystemConsts;

/**
 * Enfuser data download task
 * 
 * @author Antti Leppä
 */
public class DownloadTask implements Runnable {
 
  private static final String DOWNLOAD_URL = "https://opendata.fmi.fi/download?producer=enfuser_helsinki_metropolitan&param=AQIndex,NO2Concentration,O3Concentration,PM10Concentration,PM25Concentration&bbox=24.58,60.1321,25.1998,60.368&levels=0&format=netcdf&projection=EPSG:4326&starttime=%s";
  private static final Logger logger = LoggerFactory.getLogger(DownloadTask.class);

  @Override
  public void run() {
    String date = OffsetDateTime.now(ZoneOffset.UTC)
      .truncatedTo(ChronoUnit.HOURS)
      .plusHours(1)
      .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    
    File folder = new File(System.getProperty(SystemConsts.DATA_STORE_FOLDER));
    File outputFile = new File(folder, String.format("enfuser-%s.nc", date));
    if (outputFile.exists()) {
      logger.info("Output file already exists, skipping download");
      return;
    }
    
    try {
      outputFile.createNewFile();
    } catch (IOException e) {
      logger.error("Failed to create download file", e);
    }
    
    logger.info("Downloading Enfuser data file into {}", outputFile.getAbsolutePath());
    
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet httpGet = new HttpGet(String.format(DOWNLOAD_URL, date));
      try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
        InputStream content = response.getEntity().getContent();
        
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
          String message = IOUtils.toString(content, "UTF-8");
          logger.error("Failed to download Enfuser data with following error [{}]: {}", statusCode, message);
          return;
        }
        
        try (InputStream data = response.getEntity().getContent()) {
          try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
            IOUtils.copy(data, outputStream);
          }
        }
      }
      
      logger.info("Downloaded Enfuser data file into {}", outputFile.getAbsolutePath());
      
      triggerDownloaded(outputFile);
      
    } catch (IOException e) {
      logger.error("Failed to download Enfuser data", e);
    }
  }
 
  /**
   * Sends JMS message of Enfuser file download
   * 
   * @param file file
   */
  private void triggerDownloaded(File file) {
    try {
      Queue queue = getQueue();
      ConnectionFactory connectionFactory = getConnectionFactory();
      Connection connection = connectionFactory.createConnection();
      Session session = connection.createSession();
      MessageProducer producer = session.createProducer(queue);
      
      MapMessage message = session.createMapMessage();
      message.setString(UpdaterConsts.FILE_NAME, file.getAbsolutePath());
      producer.send(message);
    } catch (NamingException | JMSException e) {
      logger.error("Failed to notfiy cluster", e);
    }
  }
  
  /**
   * Returns connector factory
   * 
   * @return connector factory
   * @throws NamingException thrown when lookup fails
   */
  private ConnectionFactory getConnectionFactory() throws NamingException {
    return (ConnectionFactory) (new InitialContext()).lookup("java:/ConnectionFactory");
  }
  
  /**
   * Returns file queue
   * 
   * @return file queue
   * @throws NamingException thrown when lookup fails
   */
  private Queue getQueue() throws NamingException {
    return (Queue) (new InitialContext()).lookup("java:/jms/queue/enfuserfile");
  }
  
}
