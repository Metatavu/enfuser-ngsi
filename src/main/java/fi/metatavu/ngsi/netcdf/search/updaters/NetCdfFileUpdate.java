package fi.metatavu.ngsi.netcdf.search.updaters;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.metatavu.ngsi.netcdf.search.index.EntryLocation;
import fi.metatavu.ngsi.netcdf.search.index.GeoPoint;
import fi.metatavu.ngsi.netcdf.search.io.IndexUpdater;
import ucar.ma2.Array;
import ucar.nc2.NetcdfFile;

@ApplicationScoped
public class NetCdfFileUpdate implements Runnable {
//  
//  @Inject
//  private NetCdfController netCdfController;
//  
  @Inject
  private IndexUpdater indexUpdater;
  
  private long lastModified;
  
  @PostConstruct
  public void init() {
    lastModified = -1;
  }
  
  @Override
  public void run() {
    System.out.println("Bollero");
    
    File file = new File("/home/belvain/otpdata/enfuser_hkimetro.nc");
    if (file.lastModified() > lastModified) {
      updateFile(file);
      lastModified = file.lastModified();
    }
  }

  private void updateFile(File file) {
//    System.out.println("Updateeraan");
//    
//    try {
//      NetcdfFile netcdfFile = netCdfController.readNetcdfFile(file);
//      
//      netcdfFile.getVariables().stream().forEach((variable) -> {
//        System.out.println( variable );
//      });
//      
//      Array latitudeArray = netCdfController.getArray(netcdfFile, "lat");
//      Array longitudeArray = netCdfController.getArray(netcdfFile, "lon");
//      
//      for (int latitudeIndex = 0; latitudeIndex < latitudeArray.getSize(); latitudeIndex++) {
//        for (int longitudeIndex = 0; longitudeIndex < longitudeArray.getSize(); longitudeIndex++) {
//          Double latitude = latitudeArray.getDouble(latitudeIndex);
//          Double longitude = longitudeArray.getDouble(longitudeIndex);
//          GeoPoint geoPoint = GeoPoint.createGeoPoint(latitude, longitude);
//          EntryLocation entryLocation = new EntryLocation(geoPoint, latitudeIndex, longitudeIndex);
//          indexUpdater.index(entryLocation);
//        }
//      }
//      
//    } catch (IOException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }
//    
//    // TODO Auto-generated method stub
//    
  }

}
