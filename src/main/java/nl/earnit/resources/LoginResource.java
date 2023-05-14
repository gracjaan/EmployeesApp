package nl.earnit.resources;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.earnit.Auth;
import nl.earnit.Constants;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserDAO;
import nl.earnit.models.resource.login.Login;
import nl.earnit.models.User;
import nl.earnit.models.resource.login.Token;

import java.sql.SQLException;
import java.time.Instant;

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
        return Response.ok(new Token(Auth.createJWT(user, expiresAt), expiresAt)).build();
    }
}
