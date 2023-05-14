package nl.earnit.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.earnit.Auth;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserDAO;
import nl.earnit.models.User;
import nl.earnit.models.resource.InvalidEntry;
import nl.earnit.models.resource.users.CreateUser;
import nl.earnit.models.resource.users.UserResponse;

import java.sql.SQLException;
import java.util.regex.Pattern;

@Path("/users")
public class UsersResource {
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createUser(CreateUser createUser) {
        UserDAO userDAO;
        User user;

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
                "STUDENT"); // Make user student by default
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Response.serverError().build();
        }

        // If no user something must have gone wrong
        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Return user without password
        return Response.ok(new UserResponse(user)).build();
    }
}
