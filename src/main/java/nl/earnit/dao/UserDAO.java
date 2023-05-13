package nl.earnit.dao;

import nl.earnit.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        return getUser("id", id);
    }

    /**
     * Get user with given email.
     * @param email The email of the user.
     * @return The user or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public User getUserByEmail(String email) throws SQLException {
        return getUser("email", email);
    }

    private User getUser(String colum, String value) throws SQLException {
        // Create query
        String query =
            "SELECT id, email, first_name, last_name, last_name_prefix, password, type FROM \"" + tableName + "\" WHERE \"" + colum + "\" = ?";
        PreparedStatement user = this.con.prepareStatement(query);
        user.setString(1, value);

        // Execute query
        ResultSet res = user.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return User
        return new User(res.getString("id"), res.getString("email"), res.getString("first_name"),
            res.getString("last_name"), res.getString("last_name_prefix"), res.getString("type"), res.getString("password"));
    }
}

