package fi.metatavu.ngsi.netcdf.updater;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;

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
    managedScheduledExecutorService.scheduleAtFixedRate(new DownloadTask(), 1, 60, TimeUnit.MINUTES);
  }
  
}
