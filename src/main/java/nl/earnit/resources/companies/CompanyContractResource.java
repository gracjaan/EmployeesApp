package nl.earnit.resources.companies;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

public class CompanyContractResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String companyId;
    private final String contractId;
    public CompanyContractResource(UriInfo uriInfo, Request request, String companyId,
                                   String contractId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.companyId = companyId;
        this.contractId = contractId;
    }

    @GET
    public Response getContract() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    public Response updateContract() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    public Response deleteContract() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/employees")
    public Response getEmployees() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/employees")
    public Response addEmployee() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/employees/{userContractId}")
    public Response getEmployee(@PathParam("userContractId") String userContractId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    @Path("/employees/{userContractId}")
    public Response updateEmployee(@PathParam("userContractId") String userContractId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/employees/{userContractId}")
    public Response deleteEmployee(@PathParam("userContractId") String userContractId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
