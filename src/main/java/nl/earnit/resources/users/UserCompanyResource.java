package nl.earnit.resources.users;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

public class UserCompanyResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String userId;
    private final String companyId;

    public UserCompanyResource(UriInfo uriInfo, Request request, String userId, String companyId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
        this.companyId = companyId;
    }

    @GET
    public Response getCompany() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/contracts")
    public Response getContracts() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/contracts/{userContractId}")
    public Response getContract(@PathParam("userContractId") String userContractId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
