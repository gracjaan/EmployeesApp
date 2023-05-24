package nl.earnit.dao;

import jakarta.annotation.Nullable;
import nl.earnit.models.db.User;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO extends GenericDAO<User> {
    private final static String TABLE_NAME = "user";

    public UserDAO(Connection con) {
        super(con, TABLE_NAME);
    }

    @Override
    public int count() throws SQLException {
        // Create query
        String query = "SELECT COUNT(*) AS count FROM  \"" + tableName + "\"";
        PreparedStatement counter = this.con.prepareStatement(query);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        res.next();
        return res.getInt("count");
    }

    /**
     * Get user with given id.
     * @param id The id of the user.
     * @return The user or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public User getUserById(String id) throws SQLException {
        return getUser("id", id, "uuid");
    }

    /**
     * Get user with given email.
     * @param email The email of the user.
     * @return The user or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public User getUserByEmail(String email) throws SQLException {
        return getUser("email", email, "text");
    }

    private User getUser(String colum, String value, String type) throws SQLException {
        // Create query
        String query =
            "SELECT id, email, first_name, last_name, last_name_prefix, password, type FROM \"" + tableName + "\" WHERE \"" + colum + "\" = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PGobject toInsert = new PGobject();
        toInsert.setType(type);
        toInsert.setValue(value);
        statement.setObject(1, toInsert);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return User
        return new User(res.getString("id"), res.getString("email"), res.getString("first_name"),
            res.getString("last_name"), res.getString("last_name_prefix"), res.getString("type"), res.getString("password"));
    }

    public List<User> getAllUsers() throws SQLException {
        ArrayList<User> userList = new ArrayList<>();
        String query = "SELECT id, first_name, last_name, last_name_prefix FROM \"" + tableName + "\"";
        PreparedStatement statement = this.con.prepareStatement(query);
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return all users
        while(res.next()) {
            User user = new User();
            user.setId(res.getString("id"));
            user.setFirstName(res.getString("first_name"));
            user.setLastName(res.getString("last_name"));
            user.setLastNamePrefix(res.getString("last_name_prefix"));
            userList.add(user);
        }
        return userList;

    }

    public User createUser(String email, String firstName, @Nullable String lastNamePrefix, String lastName, String password, String type)
        throws SQLException {
        // Create query
        String query = "INSERT INTO \"" + tableName + "\" (id, email, first_name, last_name_prefix, last_name, password, type) VALUES (gen_random_uuid(), ?, ?, ?, ?, ?, ?) RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, email);
        statement.setString(2, firstName);
        statement.setString(3, lastNamePrefix == null || lastNamePrefix.length() < 1 ? null : lastNamePrefix);
        statement.setString(4, lastName);
        statement.setString(5, password);
        statement.setString(6, type);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return user
        return getUserById(res.getString("id"));
    }

    public User updateUser(User user) throws SQLException {
        // Create query
        String query = "UPDATE \"" + tableName + "\" SET email = ?, first_name = ?, last_name = ?, last_name_prefix = ?, password = ?, type = ? WHERE \"id\" = ? RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getFirstName());
        statement.setString(3, user.getLastNamePrefix() == null || user.getLastNamePrefix().length() < 1 ? null : user.getLastNamePrefix());
        statement.setString(4, user.getLastName());
        statement.setString(5, user.getPassword());
        statement.setString(6, user.getType());

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return user
        return getUserById(res.getString("id"));
    }
}

