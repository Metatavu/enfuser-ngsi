package fi.metatavu.ngsi.netcdf.netcdf.reader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnfuserDataReaderMap extends HashMap<String, EnfuserDataReader> implements AutoCloseable {

  private static final long serialVersionUID = 6533362427792031791L;

  public EnfuserDataReaderMap(Map<String, EnfuserDataReader> map) {
    super(map);
  }

  @Override
  public void close() throws IOException {
    for (EnfuserDataReader reader : values()) {
      reader.close();
    }
  }

}