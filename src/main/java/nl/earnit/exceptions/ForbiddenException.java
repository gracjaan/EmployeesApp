package nl.earnit.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * The type Forbidden exception.
 */
public class ForbiddenException extends WebApplicationException {
    /**
     * Instantiates a new Forbidden exception.
     */
    public ForbiddenException() {
        super(Response.status(Response.Status.FORBIDDEN).build());
    }

    /**
     * Instantiates a new Forbidden exception.
     *
     * @param message the message
     */
    public ForbiddenException(String message) {
        super(Response.status(Response.Status.FORBIDDEN).entity(message).type("text/plain").build());
    }
}
