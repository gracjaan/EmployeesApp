package nl.earnit.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class UnauthorizedException extends WebApplicationException {
    public UnauthorizedException() {
        super(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    public UnauthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED).entity(message).type("text/plain").build());
    }
}
