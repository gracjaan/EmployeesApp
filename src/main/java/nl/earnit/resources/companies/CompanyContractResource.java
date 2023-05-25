package nl.earnit.resources.companies;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.Auth;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.dao.UserDAO;
import nl.earnit.models.db.User;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.resource.companies.AddUserToContract;
import nl.earnit.models.resource.users.UserResponse;

import java.sql.SQLException;

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
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addEmployee(AddUserToContract addUserToContract) {

        if (addUserToContract == null || addUserToContract.getHourlyWage() <= 0 || addUserToContract.getUserId() == null) {
            return Response.status(400).build();
        }


        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            userContractDAO.addNewUserContract(addUserToContract.getUserId(), companyId, addUserToContract.getHourlyWage());

        } catch (SQLException e) {
            return Response.serverError().build();
        }


        return Response.ok().build();
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
