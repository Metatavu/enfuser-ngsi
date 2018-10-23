package fi.metatavu.ngsi.netcdf.api;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@RequestScoped
@Stateful
@Path("/")
public class ApiRoot extends AbstractApi {

  @GET
  @Consumes({ "text/plan" })
  @Produces({ "text/plan" })
  public Response getRoot() throws Exception {
    return Response
      .status(Response.Status.OK)
      .entity("ENFUSER NGSIv2 Server")
      .build();
  }

}
