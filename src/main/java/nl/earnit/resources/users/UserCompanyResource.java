package nl.earnit.resources.users;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.WorkedDAO;
import nl.earnit.models.db.UserContract;

import java.sql.SQLException;
import java.util.List;

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
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getContracts() {
        WorkedDAO workedDAO;
        List<UserContract> userContracts;
        try {
            workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED);
            userContracts = workedDAO.getUserContracts(this.userId);
        } catch (SQLException e){
            return Response.serverError().build();
        }
        return Response.ok(userContracts).build();
    }

    @GET
    @Path("/contracts/{userContractId}")
    public Response getContract(@PathParam("userContractId") String userContractId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
