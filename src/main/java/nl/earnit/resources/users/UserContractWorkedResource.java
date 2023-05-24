package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

public class UserContractWorkedResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String userId;
    private final String userContractId;
    private final String year;
    private final String week;

    // If weekId is null use year, week and user contract id
    private final String weekId;

    public UserContractWorkedResource(UriInfo uriInfo, Request request, String userId, String userContractId, String year, String week) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
        this.userContractId = userContractId;
        this.year = year;
        this.week = week;
        this.weekId = null;
    }

    public UserContractWorkedResource(UriInfo uriInfo, Request request, String userId, String userContractId, String weekId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
        this.userContractId = userContractId;
        this.year = null;
        this.week = null;
        this.weekId = weekId;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkedWeek() {
        // check which values are null, so what is filtered by
        // access the corresponding function in the DAO
        // return all the entries for the week
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    public Response updateWorkedWeekTask() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    public Response addWorkedWeekTask() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    public Response deleteWorkedWeekTask() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }


    @POST
    @Path("/confirm")
    public Response confirmWorkedWeek() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/confirm")
    public Response removeConfirmWorkedWeek() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    @Path("/note")
    public Response updateWorkedWeekNote() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
