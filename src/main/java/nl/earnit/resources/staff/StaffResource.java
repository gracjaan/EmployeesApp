package nl.earnit.resources.staff;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.helpers.RequestHelper;
import nl.earnit.models.db.User;

@Path("/staff")
public class StaffResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @GET
    public Response getStaff(@Context HttpHeaders httpHeaders) {
        User user = RequestHelper.validateUser(httpHeaders);
        RequestHelper.handleAccessToStaff(user);

        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    public Response addStaff() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteStaff(@PathParam("userId") String userId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    // TODO: everything related to rejecting and approving of hours needs to change to the new specification

    @GET
    @Path("/rejects")
    public Response getRejects() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/rejects/{workedWeekId}")
    public Response getRejectDetails(@PathParam("workedWeekId") String workedWeekId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/rejects/{workedWeekId}")
    public Response resolvedAccept(@PathParam("workedWeekId") String workedWeekId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/rejects/{workedWeekId}")
    public Response resolvedReject(@PathParam("workedWeekId") String workedWeekId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
