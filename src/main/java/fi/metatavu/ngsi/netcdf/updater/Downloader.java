package fi.metatavu.ngsi.netcdf.updater;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.lang3.math.NumberUtils;

import fi.metatavu.ngsi.netcdf.netcdf.EnfuserConsts;

/**
 * Downloader class
 * 
 * @author Antti Leppä
 */
@ApplicationScoped
@Startup
@Singleton
public class Downloader {
  
  @Resource
  private ManagedScheduledExecutorService managedScheduledExecutorService;
  
  @PostConstruct
  public void init() {
    if (!"FALSE".equalsIgnoreCase(System.getProperty(EnfuserConsts.DOWNLOAD_PROPERTY))) {
      managedScheduledExecutorService.scheduleAtFixedRate(new DownloadTask(), 1, NumberUtils.createInteger(System.getProperty(EnfuserConsts.DOWNLOAD_INTERVAL_PROPERTY, "160")), TimeUnit.MINUTES);
    }
  }
  
}
