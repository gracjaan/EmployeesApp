package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.Auth;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserDAO;
import nl.earnit.helpers.RequestHelper;
import nl.earnit.models.User;
import nl.earnit.dto.InvalidEntryDTO;
import nl.earnit.dto.user.CreateUserDTO;
import nl.earnit.dto.user.UserResponseDTO;
import java.util.List;
import java.util.regex.Pattern;


/**
 * The type Users resource.
 */
@Path("/users")
public class UsersResource {
    /**
     * The Uri info.
     */
    @Context
    UriInfo uriInfo;
    /**
     * The Request.
     */
    @Context
    Request request;

    /**
     * Gets users.
     *
     * @param httpHeaders the http headers
     * @param order       the order
     * @return the users
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUsers(@Context HttpHeaders httpHeaders,
                             @QueryParam("order") @DefaultValue("user.last_name:asc") String order) {
        User user = RequestHelper.validateUser(httpHeaders);
        RequestHelper.handleAccessToStaff(user);

        try {
            UserDAO userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            List<UserResponseDTO> users = userDAO.getAllUsers(order);
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create user response.
     *
     * @param createUserDTO the create user
     * @return the response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createUser(CreateUserDTO createUserDTO) {
        // Validate create user
        if (createUserDTO == null || createUserDTO.getEmail() == null || createUserDTO.getFirstName() == null || createUserDTO.getLastName() == null || createUserDTO.getPassword() == null) {
            return Response.status(400).build();
        }

        // Validate user
        if (createUserDTO.getFirstName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntryDTO("firstName")).build();
        }

        if (createUserDTO.getLastName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntryDTO("lastName")).build();
        }

        String emailRegex = "([-!#-'*+/-9=?A-Z^-~]+(\\.[-!#-'*+/-9=?A-Z^-~]+)*|\"(\\[]!#-[^-~ \\t]|(\\\\[\\t -~]))+\")@[0-9A-Za-z]([0-9A-Za-z-]{0,61}[0-9A-Za-z])?(\\.[0-9A-Za-z]([0-9A-Za-z-]{0,61}[0-9A-Za-z])?)+";
        Pattern emailPattern = Pattern.compile(emailRegex);
        if (!emailPattern.matcher(createUserDTO.getEmail()).matches()) {
            return Response.status(422).entity(new InvalidEntryDTO("email")).build();
        }

        Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,}$");
        if (!passwordPattern.matcher(createUserDTO.getPassword()).matches()) {
            return Response.status(422).entity(new InvalidEntryDTO("password")).build();
        }

        UserDAO userDAO;
        User user;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);

            // Make sure no other user with this email
            User currentUser = userDAO.getUserByEmail(createUserDTO.getEmail().toLowerCase());
            if (currentUser != null) {
                return Response.status(409).build();
            }

            // Hash password
            String passwordHash = Auth.hashPassword(createUserDTO.getPassword());

            // Create user
            user = userDAO.createUser(createUserDTO.getEmail().toLowerCase(), createUserDTO.getFirstName(),
                createUserDTO.getLastNamePrefix(), createUserDTO.getLastName(), passwordHash,
                "STUDENT", createUserDTO.getKvk(), createUserDTO.getBtw(), createUserDTO.getAddress()); // Make user student by default
        } catch (Exception e) {
            return Response.serverError().build();
        }

        // If no user something must have gone wrong
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Return user without password
        return Response.ok(new UserResponseDTO(user)).build();
    }

    /**
     * Gets user.
     *
     * @param httpHeaders the http headers
     * @param userId      the user id
     * @return the user
     */
    @Path("/{userId}")
    public UserResource getUser(@Context HttpHeaders httpHeaders, @PathParam("userId") String userId) {
        RequestHelper.validateUUID(userId);
        User user = RequestHelper.validateUser(httpHeaders);
        RequestHelper.handleAccessToUser(userId, user);

        return new UserResource(uriInfo, request, userId);
    }
}
