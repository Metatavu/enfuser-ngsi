package fi.metatavu.ngsi.netcdf.search.updaters;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Startup
@Singleton
public class Indexer {
  
  @Inject
  private NetCdfFileUpdate netCdfFileUpdate;

  @Resource
  private ManagedScheduledExecutorService managedScheduledExecutorService;
  
  @PostConstruct
  public void init() {
    managedScheduledExecutorService.scheduleWithFixedDelay(netCdfFileUpdate, 2, 5, TimeUnit.SECONDS);
  }
  
}
