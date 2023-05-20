package nl.earnit.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class ForbiddenException extends WebApplicationException {
    public ForbiddenException() {
        super(Response.status(Response.Status.FORBIDDEN).build());
    }

    public ForbiddenException(String message) {
        super(Response.status(Response.Status.FORBIDDEN).entity(message).type("text/plain").build());
    }
}
