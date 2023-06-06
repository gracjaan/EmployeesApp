package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.dao.UserDAO;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;
import nl.earnit.models.db.UserContract;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUser() {
        UserDAO userDAO;
        User user;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            user = userDAO.getUserById(this.userId);
        } catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok(user).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateUser(User user) {
        UserDAO userDAO;
        User dbUser;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            dbUser = userDAO.updateUser(user);
        } catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok(dbUser).build();
    }

    @DELETE
    @Path("/users/{userId}")
    public Response deleteUser(@PathParam("userId") String userId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/companies")
    public Response getCompanies() {
        UserDAO userDAO;
        List<Company> companies;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            companies = userDAO.getCompanies(this.userId);
        } catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok(companies).build();
    }

    @Path("/companies/{companyId}")
    public UserCompanyResource getCompany(@PathParam("companyId") String companyId) {
        return new UserCompanyResource(uriInfo, request, userId, companyId);
    }

    @GET
    @Path("/contracts")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getContracts() {
        UserContractDAO userContractDAO;
        List<UserContract> userContracts;
        try {
            userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
            userContracts = userContractDAO.getUserContractsByUserId(this.userId);
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(userContracts).build();
    }

    @Path("/contracts/{userContractId}")
    public UserContractResource getContract(@PathParam("userContractId") String userContractId) {
        return new UserContractResource(uriInfo, request, userId, userContractId);
    }
}
