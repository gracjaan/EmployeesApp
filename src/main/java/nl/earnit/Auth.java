package nl.earnit;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserDAO;
import nl.earnit.models.db.User;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;

public class Auth {
    public static boolean validatePassword(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    public static String createJWT(User user, long expiresAt) {
        Algorithm algorithm = Algorithm.HMAC256(System.getenv("JWT_SECRET"));
        return JWT.create()
            .withIssuer("earnit")
            .withSubject(user.getEmail())
            .withIssuedAt(Instant.ofEpochMilli(System.currentTimeMillis()))
            .withExpiresAt(Instant.ofEpochMilli(expiresAt))
            .withClaim("user_id", user.getId())
            .withClaim("user_email", user.getEmail())
            .sign(algorithm);
    }

    public static User validateJWT(String token) {
        DecodedJWT jwt;
        try {
            Algorithm algorithm = Algorithm.HMAC256(System.getenv("JWT_SECRET"));
            jwt = JWT.require(algorithm)
                .build()
                .verify(token);
        } catch (JWTVerificationException ignored) {
            // JWT signature invalid
            return null;
        }

        // Check if we issued it
        if (!jwt.getIssuer().equals(System.getenv("JWT_ISSUER"))) {
            return null;
        }

        Claim userId = jwt.getClaim("user_id");
        Claim userEmail = jwt.getClaim("user_email");

        // Make sure the subject and claim match
        if (!jwt.getSubject().equals(userEmail.asString())) {
            return null;
        }

        // Make sure the token is currently valid
        if (!jwt.getIssuedAt().before(Date.from(Instant.ofEpochMilli(System.currentTimeMillis())))) {
            return null;
        }

        if (!jwt.getExpiresAt().after(Date.from(Instant.ofEpochMilli(System.currentTimeMillis())))) {
            return null;
        }

        try {
            UserDAO userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            User user = userDAO.getUserById(userId.asString());

            if (user == null) return null;
            if (!user.getEmail().equals(userEmail.asString())) return null;

            return user;
        } catch (SQLException e) {
            return null;
        }
    }
}
