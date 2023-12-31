package nl.earnit.helpers;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.HttpHeaders;
import nl.earnit.Auth;
import nl.earnit.exceptions.UnauthorizedException;
import nl.earnit.models.User;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * The type Request helper.
 */
public class RequestHelper {
    /**
     * Validates uuid, if invalid throws BadRequestException
     *
     * @param uuid Uuid to validate.
     * @throws BadRequestException If uuid is invalid.
     */
    public static void validateUUID(String uuid) throws BadRequestException {
        try{
            Pattern UUID_REGEX =
                Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
            if (!UUID_REGEX.matcher(uuid).matches()) throw new IllegalArgumentException();

            UUID.fromString(uuid);
        } catch (IllegalArgumentException exception){
            throw new BadRequestException();
        }
    }

    /**
     * Validates user from authorization header.
     *
     * @param httpHeaders Request headers.
     * @return user for token.
     * @throws UnauthorizedException Authorization header invalid or no user found.
     */
    public static User validateUser(HttpHeaders httpHeaders) throws UnauthorizedException {
        User user = Auth.validateJWT(httpHeaders);
        if (user == null) throw new UnauthorizedException();

        return user;
    }

    /**
     * Handle access to company.
     *
     * @param companyId the company id
     * @param user      the user
     */
    public static void handleAccessToCompany(String companyId, User user) {
        try {
            if (!Auth.hasAccessToCompany(companyId, user)) throw new ForbiddenException();
        } catch (ForbiddenException e) {
            throw new ForbiddenException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Handle access to staff.
     *
     * @param user the user
     */
    public static void handleAccessToStaff(User user) {
        try {
            if (!Auth.hasAccessToStaff(user)) throw new ForbiddenException();
        } catch (ForbiddenException e) {
            throw new ForbiddenException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }

    /**
     * Handle access to user.
     *
     * @param userId the user id
     * @param user   the user
     */
    public static void handleAccessToUser(String userId, User user) {
        try {
            if (!Auth.hasAccessToUser(userId, user)) throw new ForbiddenException();
        } catch (ForbiddenException e) {
            throw new ForbiddenException();
        } catch (Exception e) {
            throw new InternalServerErrorException();
        }
    }
}
