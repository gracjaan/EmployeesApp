package nl.earnit.dao;

import jakarta.annotation.Nullable;
import nl.earnit.dto.workedweek.NotificationDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.Notification;
import nl.earnit.models.db.User;
import nl.earnit.models.resource.users.UserResponse;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserDAO extends GenericDAO<User> {
    private final static String TABLE_NAME = "user";

    public UserDAO(Connection con) {
        super(con, TABLE_NAME);
    }

    @Override
    public int count() throws SQLException {
        // Create query
        String query = "SELECT COUNT(*) AS count FROM  " + tableName + "WHERE active = true";
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
            "SELECT id, email, first_name, last_name, last_name_prefix, password, type, kvk, address, btw FROM \"" + tableName + "\" WHERE \"" + colum + "\" = ?";
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
            res.getString("last_name"), res.getString("last_name_prefix"), res.getString("type"), res.getString("password"), res.getString("address"), res.getString("btw"), res.getString("kvk"));
    }

    public List<UserResponse> getAllUsers(String order) throws SQLException {
        OrderBy orderBy = new OrderBy(new HashMap<>() {{
            put("user.first_name", "first_name");
            put("user.last_name", "last_name");
            put("user.last_name_prefix", "last_name_prefix");
            put("user.email", "email");
            put("user.id", "id");
        }});

        ArrayList<UserResponse> userList = new ArrayList<>();


        String query = "SELECT id, first_name, last_name, last_name_prefix, email, active FROM \"" + tableName + "\"WHERE type = 'STUDENT'  ORDER BY " + orderBy.getSQLOrderBy(order, false) ;

        PreparedStatement statement = this.con.prepareStatement(query);



        ResultSet res = statement.executeQuery();


        // Return all users
        while(res.next()) {
            UserResponse user = new UserResponse();
            user.setId(res.getString("id"));
            user.setFirstName(res.getString("first_name"));
            user.setEmail(res.getString("email"));
            user.setLastName(res.getString("last_name"));
            user.setLastNamePrefix(res.getString("last_name_prefix"));
            user.setActive(res.getBoolean("active"));
            userList.add(user);
        }
        return userList;

    }


    public User createUser(String email, String firstName, @Nullable String lastNamePrefix, String lastName, String password, String type, String kvk, String btw, String address)
        throws SQLException {
        // Create query
        String query = "INSERT INTO \"" + tableName + "\" (email, first_name, last_name_prefix, last_name, password, type, kvk, btw, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, email);
        statement.setString(2, firstName);
        statement.setString(3, lastNamePrefix == null || lastNamePrefix.length() < 1 ? null : lastNamePrefix);
        statement.setString(4, lastName);
        statement.setString(5, password);
        statement.setString(6, type);
        statement.setString(7, kvk);
        statement.setString(8, btw);
        statement.setString(9, address);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return user
        return getUserById(res.getString("id"));
    }

    public User updateUser(UserResponse user) throws SQLException {
        // Create query
        String query = "UPDATE \"" + tableName + "\" SET email = ?, first_name = ?, last_name = ?, last_name_prefix = ?, active = ?, kvk = ?, btw = ?, address = ? WHERE \"id\" = ? RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getFirstName());
        statement.setString(3, user.getLastName());
        statement.setString(4, user.getLastNamePrefix() == null || user.getLastNamePrefix().length() < 1 ? null : user.getLastNamePrefix());
        statement.setBoolean(5, user.getActive());
        statement.setString(6, user.getKvk());
        statement.setString(7, user.getBtw());
        statement.setString(8, user.getAddress());
        PostgresJDBCHelper.setUuid(statement, 9, user.getId());

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return user
        return getUserById(res.getString("id"));
    }

    public void disableUserById(String id) throws SQLException {
        String query = "UPDATE \"" + tableName + "\" SET active = false WHERE id = ? returning id";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);
        statement.executeQuery();
    }

    public void renableUserById(String id) throws SQLException {
        String query = "UPDATE \"" + tableName + "\" SET active = true WHERE id = ? returning id";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);
        statement.executeQuery();

    }

    public List<Company> getCompanies(String userId) throws SQLException {
        List<Company> companies = new ArrayList<>();
        String query = "SELECT c.* FROM company c, company_user u WHERE c.id=u.company_id AND u.user_id=?;";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, userId);
        ResultSet res = statement.executeQuery();

        while (res.next()) {
            Company c = new Company(res.getString("id"), res.getString("name"), res.getString("kvk"), res.getString("address"));
            companies.add(c);
        }
        return companies;
    }

    public boolean updateUserType(UserResponse userResponse) throws SQLException {
        // Create query
        String query = "UPDATE \"" + tableName + "\" SET type = ? WHERE \"id\" = ? RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, userResponse.getType());
        PostgresJDBCHelper.setUuid(statement, 2, userResponse.getId());

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        return res.next();
    }

    public boolean isActive(String userId) throws SQLException {
        String query = "SELECT active FROM \"" + tableName + "\" u WHERE u.id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, userId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if (!res.next()) return false;

        return res.getBoolean("active");
    }

    public List<NotificationDTO> getNotificationsForUser(String user_id) throws SQLException {
        if (user_id==null) {
            return null;
        }
        List<NotificationDTO> notifications = new ArrayList<>();
        String query = "SELECT n.*, u.first_name, u.last_name, c.name AS company_name " +
                "FROM \"notification\" n, \"user\" u, \"company\" c " +
                "WHERE n.user_id = u.id AND n.company_id = c.id " +
                "AND u.id = ? ORDER BY n.date";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, user_id);
        ResultSet res = statement.executeQuery();
        while (res.next()) {
            String message = convertToMessage(res.getString("type"), res.getString("company_name"), res.getString("first_name") + " " + res.getString("last_name"));
            NotificationDTO notification = new NotificationDTO(res.getString("id"), res.getDate("date"), res.getBoolean("seen"), message);
            notifications.add(notification);
        }
        return notifications;
    }

    public String convertToMessage(String type, String company_name, String user_name) {
        String message = null;
        switch(type) {
            case "HOURS":
                message =  "You haven't confirmed hours for " + company_name + " yet";
                break;
            case "APPROVED":
                message = company_name + " approved your suggested hours";
                break;
            case "SUGGESTION":
                message = company_name + " has suggested new hours";
                break;
            case "REJECTED":
                message = company_name+ " rejected your suggested hours";
                break;
            case "CONFLICT":
                message = company_name + " and " + user_name + " have a conflict";
            case "LINK":
                message = "You have been linked to " + company_name;
            default:
                System.out.println("No valid notification type");
        }
        return message;
    }

    public void changeNotificationToSeen(String notification_id) throws SQLException {
        String query = "UPDATE \"notification\"" +
                "SET \"seen\" = FALSE WHERE id = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, notification_id);
        ResultSet res = statement.executeQuery();
    }
}

