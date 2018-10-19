package fi.metatavu.ngsi.netcdf.search.updaters;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import fi.metatavu.ngsi.netcdf.netcdf.EnfuserDataReader;
import fi.metatavu.ngsi.netcdf.search.index.EntryLocation;
import fi.metatavu.ngsi.netcdf.search.index.GeoPoint;
import fi.metatavu.ngsi.netcdf.search.io.IndexUpdater;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;

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
  
  @PostConstruct
  public void init() {
    lastModified = -1;
  }
  
  @Override
  public void run() {
    File file = new File("/home/belvain/otpdata/enfuser_hkimetro.nc");
    if (file.lastModified() > lastModified) {
      updateFile(file);
      lastModified = file.lastModified();
    }
  }

  /**
   * Updates ENFUSER data from a file
   * 
   * @param file file
   */
  private void updateFile(File file) {
    logger.info("Updating Enfuser data");
    
    try {
      try (NetcdfFile netcdfFile = NetcdfFile.open(file.getAbsolutePath())) {
        EnfuserDataReader enfuserDataReader = new EnfuserDataReader(netcdfFile);

        Array latitudeArray = enfuserDataReader.getLatitudeArray();
        Array longitudeArray = enfuserDataReader.getLongitudeArray();
        
        for (int latitudeIndex = 0; latitudeIndex < latitudeArray.getSize(); latitudeIndex++) {
          for (int longitudeIndex = 0; longitudeIndex < longitudeArray.getSize(); longitudeIndex++) {
            Double latitude = latitudeArray.getDouble(latitudeIndex);
            Double longitude = longitudeArray.getDouble(longitudeIndex);
            GeoPoint geoPoint = GeoPoint.createGeoPoint(latitude, longitude);
            EntryLocation entryLocation = new EntryLocation(geoPoint, latitudeIndex, longitudeIndex);
            indexUpdater.index(entryLocation);
          }
        }
      }

      logger.info("Updated Enfuser data");      
    } catch (IOException e) {
      logger.error("Failed to update Enfuser data", e);
    }


  }

}
