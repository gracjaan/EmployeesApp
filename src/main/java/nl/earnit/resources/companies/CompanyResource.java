package nl.earnit.resources.companies;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import nl.earnit.resources.users.UserCompanyResource;

public class CompanyResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String companyId;

    public CompanyResource(UriInfo uriInfo, Request request, String companyId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.companyId = companyId;
    }

    @GET
    public Response getCompany() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    public Response updateCompany() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    public Response deleteCompany() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/administrators")
    public Response getAdministrators() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/administrators")
    public Response addAdministrator() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/administrators/{userId}")
    public Response deleteAdministrator(@PathParam("userId") String userId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/contracts")
    public Response getContracts() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/contracts")
    public Response addContract() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Path("/contracts/{contractId}")
    public CompanyContractResource getCompany(@PathParam("contractId") String contractId) {
        return new CompanyContractResource(uriInfo, request, companyId, contractId);
    }

    @GET
    @Path("/approves")
    public Response getToApprove() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/approves/{workedWeekId}")
    public Response getApproveDetails(@PathParam("workedWeekId") String workedWeekId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/approves/{workedWeekId}")
    public Response acceptWorkedWeek(@PathParam("workedWeekId") String workedWeekId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/approves/{workedWeekId}")
    public Response rejectWorkedWeek(@PathParam("workedWeekId") String workedWeekId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
