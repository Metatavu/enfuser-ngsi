package fi.metatavu.ngsi.netcdf.search.updaters;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import fi.metatavu.ngsi.netcdf.SystemConsts;
import fi.metatavu.ngsi.netcdf.netcdf.EnfuserConsts;
import fi.metatavu.ngsi.netcdf.netcdf.EnfuserDataReader;
import fi.metatavu.ngsi.netcdf.search.index.EntryLocation;
import fi.metatavu.ngsi.netcdf.search.index.GeoPoint;
import fi.metatavu.ngsi.netcdf.search.io.IndexUpdater;
import ucar.ma2.Array;

/**
 * Enfuser data file updater
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
public class NetCdfFileUpdate implements Runnable {
  
  @Inject
  private Logger logger;
  
  @Inject
  private IndexUpdater indexUpdater;
  
  private long lastModified;
  private int latitudeIndex;
  private boolean updating;
  
  
  @PostConstruct
  public void init() {
    lastModified = -1;
    updating = false;
  }
  
  @Override
  public void run() {
    if (updating) {
      updateNext();
    } else {
      File file = new File(System.getProperty(SystemConsts.INPUT_FILE_PROPERTY));
      if (file.lastModified() > lastModified) {
        updating = true;
        latitudeIndex = 0;
        lastModified = file.lastModified();
      }
    }
  }

  /**
   * Updates Enfuser data from a file
   * 
   * @param file file
   */
  private void updateNext() {
    try {
      EnfuserDataReader enfuserDataReader = new EnfuserDataReader();

      Array latitudeArray = enfuserDataReader.getLatitudeArray();
      Array longitudeArray = enfuserDataReader.getLongitudeArray();
      
      int latitudeArraySize = (int) latitudeArray.getSize();

      logger.info(String.format("Updating Enfuser data from latitudeIndex %d / %d", latitudeIndex, latitudeArraySize));

      for (int longitudeIndex = 0; longitudeIndex < longitudeArray.getSize(); longitudeIndex++) {
        Double latitude = latitudeArray.getDouble(latitudeIndex);
        Double longitude = longitudeArray.getDouble(longitudeIndex);
        GeoPoint geoPoint = GeoPoint.createGeoPoint(latitude, longitude);
        String entryId = String.format(EnfuserConsts.ID_PATTERN, latitudeIndex, longitudeIndex);
        EntryLocation entryLocation = new EntryLocation(entryId, geoPoint, latitudeIndex, longitudeIndex);
        indexUpdater.index(entryLocation);
      }
      
      latitudeIndex++;
      
      if (latitudeIndex > latitudeArraySize) {
        updating = false;
        logger.info("Updated Enfuser data");      
      }
            
    } catch (IOException e) {
      logger.error("Failed to update Enfuser data", e);
    }

  }

}
