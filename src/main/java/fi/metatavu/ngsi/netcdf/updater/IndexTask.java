package fi.metatavu.ngsi.netcdf.updater;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.Logger;

import fi.metatavu.ngsi.netcdf.netcdf.EnfuserConsts;
import fi.metatavu.ngsi.netcdf.netcdf.reader.EnfuserDataReader;
import fi.metatavu.ngsi.netcdf.netcdf.reader.EnfuserDataReaderFactory;
import fi.metatavu.ngsi.netcdf.search.index.EntryLocation;
import fi.metatavu.ngsi.netcdf.search.index.GeoPoint;
import fi.metatavu.ngsi.netcdf.search.index.Indexable;
import fi.metatavu.ngsi.netcdf.search.io.IndexUpdater;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;

/**
 * Listens for index messages
 * 
 * @author Antti Leppä
 */
@MessageDriven(
  activationConfig = { 
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:/jms/queue/enfuserindex"),
    @ActivationConfigProperty(propertyName = "maxConcurrency", propertyValue = "3")
  }
)
public class IndexTask implements MessageListener {
  
  @Inject
  private Logger logger;

  @Inject
  private IndexUpdater indexUpdater;
  
  /**
   * Message handler
   * 
   * @param message message
   */
  @Lock (LockType.READ)
  @TransactionAttribute (TransactionAttributeType.NOT_SUPPORTED)
  public void onMessage(Message message) {
    if (message instanceof MapMessage) {
      try {
        MapMessage indexMessage = (MapMessage) message;
        
        File file = new File(indexMessage.getString(UpdaterConsts.INDEX_FILE));
        if (!file.exists()) {
          return;
        }
        
        int latitudeIndex = indexMessage.getInt(UpdaterConsts.INDEX_LATITUDE);
        indexLatitude(file, latitudeIndex);
      } catch (JMSException e) {
        logger.error("Failed to receive JMS message", e);
      }
    }

  }

  /**
   * Indexes Enfuser data from a file
   * 
   * @param file file
   */
  private void indexLatitude(File file, int latitudeIndex) {
    try (EnfuserDataReader enfuserDataReader = EnfuserDataReaderFactory.getReader(file)) {
      ArrayFloat.D1 latitudeArray = enfuserDataReader.getLatitudeArray();
      ArrayFloat.D1 longitudeArray = enfuserDataReader.getLongitudeArray();
      ArrayInt.D1 timeArray = enfuserDataReader.getTimeArray();
      
      int latitudeArraySize = (int) latitudeArray.getSize();
      int longitudeArraySize = (int) longitudeArray.getSize();
      int timeArraySize = (int) timeArray.getSize();
      OffsetDateTime originTime = enfuserDataReader.getOriginTime();
      
      logger.info(String.format("Updating Enfuser data %s from latitudeIndex %d / %d", file.getAbsolutePath(), latitudeIndex, latitudeArraySize));

      for (int timeIndex = 0; timeIndex < timeArraySize; timeIndex++) {
        List<Indexable> entryLocations = new ArrayList<>(longitudeArraySize);
        OffsetDateTime time = originTime.plusHours(timeIndex);
        
        for (int longitudeIndex = 0; longitudeIndex < longitudeArraySize; longitudeIndex++) {
          Float latitude = latitudeArray.getFloat(latitudeIndex);
          Float longitude = longitudeArray.getFloat(longitudeIndex);
          GeoPoint geoPoint = GeoPoint.createGeoPoint(latitude, longitude);
          String entryId = String.format(EnfuserConsts.ID_PATTERN, latitudeIndex, longitudeIndex, timeIndex);
          EntryLocation entryLocation = new EntryLocation(entryId, file.getName(), time, geoPoint, latitudeIndex, longitudeIndex, timeIndex);
          entryLocations.add(entryLocation);
        }

        indexUpdater.index(entryLocations);
      }

      logger.info(String.format("Updated Enfuser data from latitudeIndex %d / %d", latitudeIndex, latitudeArraySize));
      
    } catch (IOException e) {
      logger.error("Failed to index Enfuser data", e);
    }
  
  }

}
