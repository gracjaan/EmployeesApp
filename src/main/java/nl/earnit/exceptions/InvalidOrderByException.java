package nl.earnit.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class InvalidOrderByException extends WebApplicationException {
    public InvalidOrderByException() {
        super(Response.status(Response.Status.BAD_REQUEST).build());
    }

    public InvalidOrderByException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST).entity(message).type("text/plain").build());
    }
}