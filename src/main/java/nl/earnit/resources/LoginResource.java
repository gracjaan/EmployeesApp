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
import nl.earnit.Constants;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserDAO;
import nl.earnit.models.login.Login;
import nl.earnit.models.User;
import nl.earnit.models.login.Token;

import java.sql.SQLException;
import java.time.Instant;

@Path("/login")
public class LoginResource {
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response login(Login login) throws SQLException {
        UserDAO userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
        User user = userDAO.getUserByEmail(login.getEmail());

        // User does not exist
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        // Check password
        BCrypt.Result passwordResult = BCrypt.verifyer().verify(login.getPassword().toCharArray(), user.getPassword());
        if (!passwordResult.verified) {
            // Password incorrect
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Algorithm algorithm = Algorithm.HMAC256(System.getenv("JWT_SECRET"));
        String token = JWT.create()
            .withIssuer("earnit")
            .withSubject(user.getEmail())
            .withIssuedAt(Instant.ofEpochMilli(System.currentTimeMillis()))
            .withExpiresAt(Instant.ofEpochMilli(System.currentTimeMillis() + Constants.TOKEN_EXPIRE_TIME))
            .withClaim("user_id", user.getId())
            .withClaim("user_email", user.getEmail())
            .sign(algorithm);

        return Response.ok(new Token(token)).build();
    }
}
