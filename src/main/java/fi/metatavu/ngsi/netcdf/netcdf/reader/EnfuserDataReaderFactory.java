package fi.metatavu.ngsi.netcdf.netcdf.reader;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provder class for Enfuser data file readers
 * 
 * @author Antti Leppä
 */
public class EnfuserDataReaderFactory {
  
  /**
   * Returns Enfuser data file reader for a file
   * 
   * @param file file
   * @return Enfuser data file reader
   */
  public static EnfuserDataReader getReader(File file) {
    return new EnfuserDataReader(file);
  }

  /**
   * Returns Enfuser data file reader for a file
   * 
   * @param filePath file path
   * @return Enfuser data file reader
   */
  public static EnfuserDataReader getReader(String filePath) {
    return getReader(new File(filePath));
  }

  /**
   * Returns map of data readers where file path is used as a key
   * 
   * @param files List of file paths
   * @return map of data readers
   */
  public static EnfuserDataReaderMap getReaders(List<String> files) {
    return new  EnfuserDataReaderMap(files.stream().map(File::new).collect(Collectors.toMap(File::getAbsolutePath, EnfuserDataReaderFactory::getReader)));
  }
}
