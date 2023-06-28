package nl.earnit.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * The type Unauthorized exception.
 */
public class UnauthorizedException extends WebApplicationException {
    /**
     * Instantiates a new Unauthorized exception.
     */
    public UnauthorizedException() {
        super(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    /**
     * Instantiates a new Unauthorized exception.
     *
     * @param message the message
     */
    public UnauthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED).entity(message).type("text/plain").build());
    }
}
