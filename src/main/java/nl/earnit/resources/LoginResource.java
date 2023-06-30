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
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.dto.login.LoginDTO;
import nl.earnit.dto.login.TokenDTO;

import java.util.List;

/**
 * The type Login resource.
 */
@Path("/login")
public class LoginResource {
    /**
     * Login response.
     *
     * @param loginDTO the login
     * @return the response
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response login(LoginDTO loginDTO) {
        UserDAO userDAO;
        User user;

        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            user = userDAO.getUserByEmail(loginDTO.getEmail().toLowerCase());

            // User does not exist
            if (user == null) {
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }

            if (!userDAO.isActive(user.getId())) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }

        // Check password
        if (!Auth.validatePassword(loginDTO.getPassword(), user.getPassword())) {
            // Password incorrect
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        long expiresAt = System.currentTimeMillis() + Constants.TOKEN_EXPIRE_TIME;

        String companyId = null;

        if (user.getType().equals("COMPANY")) {
            try {
                CompanyUserDAO companyUserDAO = (CompanyUserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY_USER);
                List<Company> companies = companyUserDAO.getCompaniesUserIsWorkingFor(user.getId());

                if (!companies.isEmpty()) {
                    if (companies.stream().noneMatch(x -> x.getId().equals(loginDTO.getCompanyId()))) {
                        companyId = companies.get(0).getId();
                    } else {
                        companyId = loginDTO.getCompanyId();
                    }
                }
            } catch (Exception e) {
                return Response.serverError().build();
            }
        }

        return Response.ok(new TokenDTO(Auth.createJWT(user, companyId, expiresAt), expiresAt)).build();
    }
}
