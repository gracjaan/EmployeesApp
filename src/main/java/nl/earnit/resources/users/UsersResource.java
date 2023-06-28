package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.Auth;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserDAO;
import nl.earnit.helpers.RequestHelper;
import nl.earnit.models.db.User;
import nl.earnit.models.resource.InvalidEntry;
import nl.earnit.models.resource.users.CreateUser;
import nl.earnit.models.resource.users.UserResponse;
import java.util.List;
import java.util.regex.Pattern;


@Path("/users")
public class UsersResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUsers(@Context HttpHeaders httpHeaders,
                             @QueryParam("order") @DefaultValue("user.last_name:asc") String order) {
        User user = RequestHelper.validateUser(httpHeaders);
        RequestHelper.handleAccessToStaff(user);

        try {
            UserDAO userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            List<UserResponse> users = userDAO.getAllUsers(order);
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createUser(CreateUser createUser) {
        // Validate create user
        if (createUser == null || createUser.getEmail() == null || createUser.getFirstName() == null || createUser.getLastName() == null || createUser.getPassword() == null) {
            return Response.status(400).build();
        }

        // Validate user
        if (createUser.getFirstName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntry("firstName")).build();
        }

        if (createUser.getLastName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntry("lastName")).build();
        }

        String emailRegex = "([-!#-'*+/-9=?A-Z^-~]+(\\.[-!#-'*+/-9=?A-Z^-~]+)*|\"(\\[]!#-[^-~ \\t]|(\\\\[\\t -~]))+\")@[0-9A-Za-z]([0-9A-Za-z-]{0,61}[0-9A-Za-z])?(\\.[0-9A-Za-z]([0-9A-Za-z-]{0,61}[0-9A-Za-z])?)+";
        Pattern emailPattern = Pattern.compile(emailRegex);
        if (!emailPattern.matcher(createUser.getEmail()).matches()) {
            return Response.status(422).entity(new InvalidEntry("email")).build();
        }

        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$");
        if (!passwordPattern.matcher(createUser.getPassword()).matches()) {
            return Response.status(422).entity(new InvalidEntry("password")).build();
        }

        UserDAO userDAO;
        User user;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);

            // Make sure no other user with this email
            User currentUser = userDAO.getUserByEmail(createUser.getEmail());
            if (currentUser != null) {
                return Response.status(409).build();
            }

            // Hash password
            String passwordHash = Auth.hashPassword(createUser.getPassword());

            // Create user
            user = userDAO.createUser(createUser.getEmail(), createUser.getFirstName(),
                createUser.getLastNamePrefix(), createUser.getLastName(), passwordHash,
                "STUDENT", createUser.getKvk(), createUser.getBtw(), createUser.getAddress()); // Make user student by default
        } catch (Exception e) {
            return Response.serverError().build();
        }

        // If no user something must have gone wrong
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Return user without password
        return Response.ok(new UserResponse(user)).build();
    }

    @Path("/{userId}")
    public UserResource getUser(@Context HttpHeaders httpHeaders, @PathParam("userId") String userId) {
        RequestHelper.validateUUID(userId);
        User user = RequestHelper.validateUser(httpHeaders);
        RequestHelper.handleAccessToUser(userId, user);

        return new UserResource(uriInfo, request, userId);
    }
}
