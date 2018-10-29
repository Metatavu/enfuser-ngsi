package fi.metatavu.ngsi.netcdf.updater;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.slf4j.Logger;

import fi.metatavu.ngsi.netcdf.netcdf.reader.EnfuserDataReader;
import fi.metatavu.ngsi.netcdf.netcdf.reader.EnfuserDataReaderFactory;
import ucar.ma2.Array;

/**
 * Listens for file download messages and trigger file index messages
 * 
 * @author Antti Leppä
 */
@MessageDriven(
  activationConfig = { 
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/enfuserfile")
  }
)
public class FileReadTask implements MessageListener {
  
  @Inject
  private Logger logger;
  
  @Resource (lookup = "java:/jms/queue/enfuserindex")
  private Queue indexQueue;
  
  @Resource (lookup = "java:/ConnectionFactory")
  private ConnectionFactory connectionFactory;
  
  /**
   * Message handler
   * 
   * @param message message
   */
  public void onMessage(Message message) {
    if (message instanceof MapMessage) {
      MapMessage fileMessage = (MapMessage) message;
      
      try {
        File file = new File(fileMessage.getString(UpdaterConsts.FILE_NAME));
        if (!file.exists()) {
          logger.warn("File does not exist");
          return;
        }
        
        try (EnfuserDataReader enfuserDataReader = EnfuserDataReaderFactory.getReader(file)) {
          Array latitudeArray = enfuserDataReader.getLatitudeArray();
          
          int latitudeArraySize = (int) latitudeArray.getSize();
          
          Connection indexConnection = connectionFactory.createConnection();
          Session indexSession = indexConnection.createSession();
          MessageProducer indexProducer = indexSession.createProducer(indexQueue);
          
          for (int latitudeIndex = 0; latitudeIndex < latitudeArraySize; latitudeIndex++) {
            MapMessage indexMessage = indexSession.createMapMessage();
            indexMessage.setString(UpdaterConsts.INDEX_FILE, file.getAbsolutePath());
            indexMessage.setInt(UpdaterConsts.INDEX_LATITUDE, latitudeIndex);
            indexProducer.send(indexMessage);
          }
        }
      } catch (JMSException | IOException e) {
        logger.error("Failed to receive JMS message", e);
      }
      
    }

  }

}
