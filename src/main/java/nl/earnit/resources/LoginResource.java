package nl.earnit.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.earnit.Auth;
import nl.earnit.Constants;
import nl.earnit.dao.CompanyUserDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserDAO;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;
import nl.earnit.models.resource.login.Login;
import nl.earnit.models.resource.login.Token;

import java.sql.SQLException;
import java.util.List;

@Path("/login")
public class LoginResource {
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response login(Login login) {
        UserDAO userDAO;
        User user;

        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            user = userDAO.getUserByEmail(login.getEmail());
        } catch (SQLException e) {
            System.out.println(e);
            return Response.serverError().build();
        }

        // User does not exist
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Check password
        if (!Auth.validatePassword(login.getPassword(), user.getPassword())) {
            // Password incorrect
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        long expiresAt = System.currentTimeMillis() + Constants.TOKEN_EXPIRE_TIME;

        // TODO: Make user choose what company to use
        String companyId = null;

        if (user.getType().equals("COMPANY")) {
            try {
                CompanyUserDAO companyUserDAO = (CompanyUserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY_USER);
                List<Company> companies = companyUserDAO.getCompaniesUserIsWorkingFor(user.getId());

                if (!companies.isEmpty()) {
                    companyId = companies.get(0).getId();
                }
            } catch (SQLException e) {
                return Response.serverError().build();
            }
        }

        return Response.ok(new Token(Auth.createJWT(user, companyId, expiresAt), expiresAt)).build();
    }
}
