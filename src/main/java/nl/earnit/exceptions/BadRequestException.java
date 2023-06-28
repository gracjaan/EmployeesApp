package nl.earnit.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * The type Bad request exception.
 */
public class BadRequestException extends WebApplicationException {
    /**
     * Instantiates a new Bad request exception.
     */
    public BadRequestException() {
        super(Response.status(Response.Status.BAD_REQUEST).build());
    }

    /**
     * Instantiates a new Bad request exception.
     *
     * @param message the message
     */
    public BadRequestException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST).entity(message).type("text/plain").build());
    }
}
