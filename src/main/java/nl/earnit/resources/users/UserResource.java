package nl.earnit.resources.users;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.Auth;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.dao.UserDAO;
import nl.earnit.dto.workedweek.UserContractDTO;
import nl.earnit.helpers.RequestHelper;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.resource.InvalidEntry;
import nl.earnit.models.resource.users.UserResponse;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok(user).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateUser(@Context HttpHeaders httpHeaders, UserResponse user) {
        // Validate create user
        if (user == null || user.getEmail() == null || user.getFirstName() == null || user.getLastName() == null) {
            return Response.status(400).build();
        }
        if (user.getActive() != null) {
            RequestHelper.handleAccessToStaff(RequestHelper.validateUser(httpHeaders));
        } else {
            user.setActive(true);
        }

        // Validate user
        if (user.getFirstName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntry("firstName")).build();
        }

        if (user.getLastName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntry("lastName")).build();
        }

        String emailRegex = "([-!#-'*+/-9=?A-Z^-~]+(\\.[-!#-'*+/-9=?A-Z^-~]+)*|\"(\\[]!#-[^-~ \\t]|(\\\\[\\t -~]))+\")@[0-9A-Za-z]([0-9A-Za-z-]{0,61}[0-9A-Za-z])?(\\.[0-9A-Za-z]([0-9A-Za-z-]{0,61}[0-9A-Za-z])?)+";
        Pattern emailPattern = Pattern.compile(emailRegex);
        if (!emailPattern.matcher(user.getEmail()).matches()) {
            return Response.status(422).entity(new InvalidEntry("email")).build();
        }

        UserDAO userDAO;
        User dbUser;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            User userEmailCheck = userDAO.getUserByEmail(user.getEmail());
            if (userEmailCheck != null && !userEmailCheck.getId().equals(userId)) {
                return Response.status(Response.Status.CONFLICT).entity(new InvalidEntry("email")).build();
            }

            user.setId(userId);
            dbUser = userDAO.updateUser(user);
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok(dbUser).build();
    }

    @DELETE
    public Response disableUser(@Context HttpHeaders httpHeaders) {
        UserDAO userDAO;
        RequestHelper.handleAccessToStaff(RequestHelper.validateUser(httpHeaders));
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            userDAO.disableUserById(userId);
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/companies")
    public Response getCompanies() {
        UserDAO userDAO;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            return Response.ok(userDAO.getCompanies(this.userId)).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
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
        List<UserContractDTO> userContracts;
        try {
            userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
            userContracts = userContractDAO.getUserContractsByUserId(this.userId);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(userContracts).build();
    }

    @Path("/contracts/{userContractId}")
    public UserContractResource getContract(@PathParam("userContractId") String userContractId) {
        return new UserContractResource(uriInfo, request, userId, userContractId);
    }
}
