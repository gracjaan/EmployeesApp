package nl.earnit.resources.users;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

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
    public Response getWorked() {
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
