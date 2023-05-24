package nl.earnit.resources.users;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;

import javax.print.attribute.standard.Media;

public class UserContractResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String userId;
    private final String userContractId;

    public UserContractResource(UriInfo uriInfo, Request request, String userId, String userContractId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
        this.userContractId = userContractId;
    }

    @GET
    public Response getContract() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/worked")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorked() {
        // access DAO
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Path("/worked/{year}/{week}")
    public UserContractWorkedResource getWorkedWeek(@PathParam("year") String year, @PathParam("week") String week) {
        return new UserContractWorkedResource(uriInfo, request, userId, userContractId, year, week);
    }

    @Path("/worked/{weekId}")
    public UserContractWorkedResource getWorkedWeek(@PathParam("weekId") String weekId) {
        return new UserContractWorkedResource(uriInfo, request, userId, userContractId, weekId);
    }
}
