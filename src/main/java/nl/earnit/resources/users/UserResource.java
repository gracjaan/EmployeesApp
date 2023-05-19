package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

public class UserResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String userId;

    public UserResource(UriInfo uriInfo, Request request, String userId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
    }

    @GET
    public Response getUser() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    public Response updateUser() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    public Response deleteUser() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/companies")
    public Response getCompanies() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Path("/companies/{companyId}")
    public UserCompanyResource getCompany(@PathParam("companyId") String companyId) {
        return new UserCompanyResource(uriInfo, request, userId, companyId);
    }

    @GET
    @Path("/contracts")
    public Response getContracts() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Path("/contracts/{userContractId}")
    public UserContractResource getContract(@PathParam("userContractId") String userContractId) {
        return new UserContractResource(uriInfo, request, userId, userContractId);
    }
}
