package nl.earnit.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * The type Invalid order by exception.
 */
public class InvalidOrderByException extends WebApplicationException {
    /**
     * Instantiates a new Invalid order by exception.
     */
    public InvalidOrderByException() {
        super(Response.status(Response.Status.BAD_REQUEST).build());
    }

    /**
     * Instantiates a new Invalid order by exception.
     *
     * @param message the message
     */
    public InvalidOrderByException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST).entity(message).type("text/plain").build());
    }
}