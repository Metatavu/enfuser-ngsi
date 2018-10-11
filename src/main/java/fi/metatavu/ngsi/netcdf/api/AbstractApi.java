package fi.metatavu.ngsi.netcdf.api;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

/**
 * Abstract base class for all services
 * 
 * @author Antti Leppä
 */
public abstract class AbstractApi {

  protected static final String NOT_FOUND_MESSAGE = "Not found";
  protected static final String UNAUTHORIZED = "Unauthorized";
  protected static final String NOT_SUPPORTED = "This operation is not suppored by this server";
  
  /**
   * Constructs not implemented response
   * 
   * @param message message
   * @return response
   */
  protected Response createNotSupported() {
    return createNotImplemented(NOT_SUPPORTED);
  }
  
  /**
   * Constructs not implemented response
   * 
   * @param message message
   * @return response
   */
  protected Response createNotImplemented(String message) {
    return Response
      .status(Response.Status.NOT_IMPLEMENTED)
      .entity(message)
      .build();
  }

}