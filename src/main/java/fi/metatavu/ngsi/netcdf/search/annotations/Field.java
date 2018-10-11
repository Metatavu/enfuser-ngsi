package fi.metatavu.ngsi.netcdf.search.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for declaring how indexed field is handled
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Field {

  /**
   * Analyzer
   */
  String analyzer() default "";
  
  /**
   * Type. Defaults to sring
   */
  String type() default "text";

  /**
   * Index. Defaults to analyzed
   */
  boolean index() default true;
  
  /**
   * Store. Defaults to false
   */
  boolean store() default false;
  
}
