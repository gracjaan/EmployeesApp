package nl.earnit;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.ws.rs.core.HttpHeaders;
import nl.earnit.dao.CompanyUserDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserDAO;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * The type Auth.
 */
public class Auth {
    /**
     * Validate password boolean.
     *
     * @param password       the password
     * @param hashedPassword the hashed password
     * @return the boolean
     */
    public static boolean validatePassword(String password, String hashedPassword) {
        return BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified;
    }

    /**
     * Hash password string.
     *
     * @param password the password
     * @return the string
     */
    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * Creates the JSON Web Token
     *
     * @param user      the user who the token is created for
     * @param companyId the company id
     * @param expiresAt the time that it takes
     * @return string
     */
    public static String createJWT(User user, String companyId, long expiresAt) {
        Algorithm algorithm = Algorithm.HMAC256(System.getenv("JWT_SECRET"));
        return JWT.create()
            .withIssuer("earnit")
            .withSubject(user.getEmail())
            .withIssuedAt(Instant.ofEpochMilli(System.currentTimeMillis()))
            .withExpiresAt(Instant.ofEpochMilli(expiresAt))
            .withClaim("user_id", user.getId())
            .withClaim("user_company", companyId)
            .sign(algorithm);
    }

    /**
     * Validate jwt user.
     *
     * @param token the token
     * @return the user
     */
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

        // Make sure the token is currently valid
        if (!jwt.getIssuedAt().before(Date.from(Instant.ofEpochMilli(System.currentTimeMillis())))) {
            return null;
        }

        if (!jwt.getExpiresAt().after(Date.from(Instant.ofEpochMilli(System.currentTimeMillis())))) {
            return null;
        }

        try {
            UserDAO userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);

            return userDAO.getUserById(userId.asString());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Validate jwt user.
     *
     * @param httpHeaders the http headers
     * @return the user
     */
    public static User validateJWT(HttpHeaders httpHeaders) {
        String token = getAuthenticationToken(httpHeaders);
        if (token == null) return null;

        return validateJWT(token);
    }

    /**
     * Has authentication boolean.
     *
     * @param httpHeaders the http headers
     * @return the boolean
     */
    public static boolean hasAuthentication(HttpHeaders httpHeaders) {
        return getAuthenticationToken(httpHeaders) != null;
    }

    /**
     * Gets authentication token.
     *
     * @param httpHeaders the http headers
     * @return the authentication token
     */
    public static String getAuthenticationToken(HttpHeaders httpHeaders) {
        List<String> authHeaders = httpHeaders.getRequestHeader(HttpHeaders.AUTHORIZATION);
        if (authHeaders == null || authHeaders.isEmpty()) return null;
        if (authHeaders.stream().noneMatch(x -> x.toLowerCase().startsWith("token "))) return null;

        Optional<String>
            tokenHeader = authHeaders.stream().filter(x -> x.toLowerCase().startsWith("token ")).findFirst();
        return tokenHeader.map(x -> x.substring(6)).orElse(null);
    }

    /**
     * Has access to company boolean.
     *
     * @param company the company
     * @param user    the user
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean hasAccessToCompany(Company company, User user) throws SQLException {
        return hasAccessToCompany(company, user.getId());
    }

    /**
     * Has access to company boolean.
     *
     * @param companyId the company id
     * @param user      the user
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean hasAccessToCompany(String companyId, User user) throws SQLException {
        return hasAccessToCompany(companyId, user.getId());
    }

    /**
     * Has access to company boolean.
     *
     * @param company the company
     * @param userId  the user id
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean hasAccessToCompany(Company company, String userId) throws SQLException {
        return hasAccessToCompany(company.getId(), userId);
    }

    /**
     * Has access to company boolean.
     *
     * @param companyId the company id
     * @param userId    the user id
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean hasAccessToCompany(String companyId, String userId) throws SQLException {
        UserDAO userDAO = (UserDAO) DAOManager.getInstance().getDAO(
            DAOManager.DAO.USER);

        if (hasAccessToStaff(userId)) {
            return true;
        }

        CompanyUserDAO companyUserDAO = (CompanyUserDAO) DAOManager.getInstance().getDAO(
            DAOManager.DAO.COMPANY_USER);

        return companyUserDAO.isUserWorkingForCompany(companyId, userId);
    }

    /**
     * Has access to user boolean.
     *
     * @param userIdToAccess the user id to access
     * @param userId         the user id
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean hasAccessToUser(User userIdToAccess, User userId) throws SQLException {
        return hasAccessToUser(userIdToAccess, userId.getId());
    }

    /**
     * Has access to user boolean.
     *
     * @param userIdToAccess the user id to access
     * @param userId         the user id
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean hasAccessToUser(String userIdToAccess, User userId) throws SQLException {
        return hasAccessToUser(userIdToAccess, userId.getId());
    }

    /**
     * Has access to user boolean.
     *
     * @param userIdToAccess the user id to access
     * @param userId         the user id
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean hasAccessToUser(User userIdToAccess, String userId) throws SQLException {
        return hasAccessToUser(userIdToAccess.getId(), userId);
    }

    /**
     * Has access to user boolean.
     *
     * @param userIdToAccess the user id to access
     * @param userId         the user id
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean hasAccessToUser(String userIdToAccess, String userId) throws SQLException {
        UserDAO userDAO = (UserDAO) DAOManager.getInstance().getDAO(
            DAOManager.DAO.USER);

        if (hasAccessToStaff(userId)) {
            return true;
        }

        return userIdToAccess.equals(userId);
    }

    /**
     * Has access to staff boolean.
     *
     * @param user the user
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean hasAccessToStaff(User user) throws SQLException {
        return hasAccessToStaff(user.getId());
    }

    /**
     * Has access to staff boolean.
     *
     * @param userId the user id
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static boolean hasAccessToStaff(String userId) throws SQLException {
        UserDAO userDAO = (UserDAO) DAOManager.getInstance().getDAO(
            DAOManager.DAO.USER);

        return userDAO.getUserById(userId).getType().equals(User.Type.ADMINISTRATOR.toString());
    }
}
