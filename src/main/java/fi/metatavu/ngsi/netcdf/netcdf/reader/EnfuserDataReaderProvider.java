package fi.metatavu.ngsi.netcdf.netcdf.reader;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provder class for Enfuser data file readers
 * 
 * @author Antti Leppä
 */
public class EnfuserDataReaderProvider {

  private static final Map<String, EnfuserDataReader> READERS = Collections.synchronizedMap(new HashMap<>()); 
  
  /**
   * Returns Enfuser data file reader for a file
   * 
   * @param file file
   * @return Enfuser data file reader
   */
  public static synchronized EnfuserDataReader getReader(File file) {
    String path = file.getAbsolutePath();
    if (!READERS.containsKey(path)) {
      READERS.put(path, new EnfuserDataReader(file));
    }
    
    return READERS.get(path);
  }
  
}
