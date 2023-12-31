package nl.earnit.dao;

import jakarta.annotation.Nullable;
import nl.earnit.dto.NotificationDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.dto.user.UserResponseDTO;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static nl.earnit.Constants.getName;

/**
 * The type User dao.
 */
public class UserDAO extends GenericDAO<User> {
    private final static String TABLE_NAME = "user";

    /**
     * Instantiates a new User dao.
     *
     * @param con the con
     */
    public UserDAO(Connection con) {
        super(con, TABLE_NAME);
    }

    /**
     * Counts all the rows with active users
     * @return amount of active users
     * @throws SQLException
     */
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
     *
     * @param id The id of the user.
     * @return The user or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public User getUserById(String id) throws SQLException {
        return getUser("id", id, "uuid");
    }

    /**
     * Get user with given email.
     *
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

    /**
     * Gets all users.
     *
     * @param order the order
     * @return the all users
     * @throws SQLException the sql SQLException
     */
    public List<UserResponseDTO> getAllUsers(String order) throws SQLException {
        OrderBy orderBy = new OrderBy(new HashMap<>() {{
            put("user.first_name", "first_name");
            put("user.last_name", "last_name");
            put("user.last_name_prefix", "last_name_prefix");
            put("user.email", "email");
            put("user.id", "id");
        }});

        ArrayList<UserResponseDTO> userList = new ArrayList<>();


        String query = "SELECT id, first_name, last_name, last_name_prefix, email, active FROM \"" + tableName + "\"WHERE type = 'STUDENT'  ORDER BY " + orderBy.getSQLOrderBy(order, false) ;

        PreparedStatement statement = this.con.prepareStatement(query);



        ResultSet res = statement.executeQuery();


        // Return all users
        while(res.next()) {
            UserResponseDTO user = new UserResponseDTO();
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


    /**
     * Create user user.
     *
     * @param email          the email
     * @param firstName      the first name
     * @param lastNamePrefix the last name prefix
     * @param lastName       the last name
     * @param password       the password
     * @param type           the type
     * @param kvk            the kvk
     * @param btw            the btw
     * @param address        the address
     * @return the user
     * @throws SQLException the sql SQLException
     */
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

    /**
     * Update user user.
     *
     * @param user the user
     * @return the user
     * @throws SQLException the sql SQLException
     */
    public User updateUser(UserResponseDTO user) throws SQLException {
        // Create query
        String query = "UPDATE \"" + tableName + "\" SET email = ?, first_name = ?, last_name = ?, last_name_prefix = ?, active = ?, kvk = ?, btw = ?, address = ? WHERE \"id\" = ? RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, user.getEmail().toLowerCase());
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

    /**
     * Disable user by id.
     *
     * @param id the id
     * @throws SQLException the sql SQLException
     */
    public void disableUserById(String id) throws SQLException {
        String query = "UPDATE \"" + tableName + "\" SET active = false WHERE id = ? returning id";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);
        statement.executeQuery();
    }

    /**
     * Renable user by id.
     *
     * @param id the id
     * @throws SQLException the sql SQLException
     */
    public void renableUserById(String id) throws SQLException {
        String query = "UPDATE \"" + tableName + "\" SET active = true WHERE id = ? returning id";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);
        statement.executeQuery();
    }

    /**
     * gets all the companies that a user works for (an admin can work for multiple companies)
     * @param userId the id of the user
     * @return List of companies the user works for
     * @throws SQLException
     */
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

    /**
     * Updates the user type to either a: "STUDENT", "ADMINISTRATOR", or "COMPANY"
     * @param userResponseDTO The user object that you want to change the user to.
     * @return whether the user was updated ? true : false
     * @throws SQLException
     */
    public boolean updateUserType(UserResponseDTO userResponseDTO) throws SQLException {
        // Create query
        String query = "UPDATE \"" + tableName + "\" SET type = ? WHERE \"id\" = ? RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, userResponseDTO.getType());
        PostgresJDBCHelper.setUuid(statement, 2, userResponseDTO.getId());

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        return res.next();
    }

    /**
     * Is active boolean.
     *
     * @param userId the user id
     * @return the boolean
     * @throws SQLException the sql SQLException
     */
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
        String query = """
            SELECT n.*, u.first_name, u.last_name_prefix, u.last_name, c.role, cy.name AS company_name, ww.week, u.id as user_id, cy.id as company_id, ww.id as worked_week_id, uc.id as user_contract_id, c.id as contract_id 
            FROM "notification" n
            JOIN "user" u ON u.id = n.user_id
            JOIN company cy ON cy.id = n.company_id
            LEFT JOIN worked_week ww ON ww.id = n.worked_week_id
            LEFT JOIN user_contract uc ON uc.id = ww.contract_id 
            LEFT JOIN contract c ON c.id = uc.contract_id 
            WHERE u.id = ? AND n.type != 'SUGGESTION REJECTED' AND n.type != 'SUGGESTION ACCEPTED' AND n.type != 'CONFLICT'
            ORDER BY n.date DESC, n.seen
            """;
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, user_id);
        ResultSet res = statement.executeQuery();
        while (res.next()) {
            String name = getName(res.getString("first_name"), res.getString("last_name_prefix"),  res.getString("last_name"));

            String type = res.getString("type");
            String title = convertToTitle(type);
            String description = convertToDescription(type, res.getString("company_name"), res.getString("role"), name, res.getString("week"));

            if (title == null) continue;

            NotificationDTO notification = new NotificationDTO(res.getString("id"), res.getString("date"), res.getBoolean("seen"), type, title, description, res.getString("user_id"), res.getString("company_id"), res.getString("user_contract_id"), res.getString("contract_id"), res.getString("worked_week_id"), res.getInt("week"));
            notifications.add(notification);
        }
        return notifications;
    }


    public List<NotificationDTO> getNotificationsForStaffUser() throws SQLException {
        List<NotificationDTO> notifications = new ArrayList<>();
        String query = """
            SELECT n.*, u.first_name, u.last_name_prefix, u.last_name, c.role, cy.name AS company_name, ww.week, u.id as user_id, cy.id as company_id, ww.id as worked_week_id, uc.id as user_contract_id, c.id as contract_id 
            FROM "notification" n
            JOIN "user" u ON u.id = n.user_id
            JOIN company cy ON cy.id = n.company_id
            LEFT JOIN worked_week ww ON ww.id = n.worked_week_id
            LEFT JOIN user_contract uc ON uc.id = ww.contract_id 
            LEFT JOIN contract c ON c.id = uc.contract_id 
            WHERE n.type = 'CONFLICT'
            ORDER BY n.date DESC, n.seen
            """;
        PreparedStatement statement = this.con.prepareStatement(query);
        ResultSet res = statement.executeQuery();
        while (res.next()) {
            String name = getName(res.getString("first_name"), res.getString("last_name_prefix"),  res.getString("last_name"));

            String type = res.getString("type");
            String title = convertToTitle(type);
            String description = convertToDescription(type, res.getString("company_name"), res.getString("role"), name, res.getString("week"));

            if (title == null) continue;

            NotificationDTO notification = new NotificationDTO(res.getString("id"), res.getString("date"), res.getBoolean("seen"), type, title, description, res.getString("user_id"), res.getString("company_id"), res.getString("user_contract_id"), res.getString("contract_id"), res.getString("worked_week_id"), res.getInt("week"));
            notifications.add(notification);
        }
        return notifications;
    }

    public String convertToTitle(String type) {
        String title = null;
        switch(type) {
            case "HOURS":
                title = "Week not confirmed";
                break;
            case "APPROVED":
                title = "Week approved";
                break;
            case "SUGGESTION":
                title = "Week denied";
                break;
            case "CONFLICT":
                title = "Conflict";
                break;
            case "LINK":
                title = "New contract";
            default:
                System.out.println("No relevant notification type");
        }
        return title;
    }

    public String convertToDescription(String type, String company_name, String role, String user_name, String week) {
        String description = null;
        switch(type) {
            case "HOURS":
                description = "You haven't confirmed hours for " + role + " at " + company_name + " for week " + week;
                break;
            case "APPROVED":
                description = company_name + " approved your suggested hours for week " + week;
                break;
            case "SUGGESTION":
                description = company_name + " has suggested new hours for week " + week;
                break;
            case "CONFLICT":
                description = company_name + " and " + user_name + " have a conflict for week " + week;
                break;
            case "LINK":
                description = "You have been linked to " + company_name;
            default:
                System.out.println("No relevant notification type");
        }
        return description;
    }

    public void changeNotificationToSeen(String notification_id) throws SQLException {
        String query = "UPDATE \"notification\" SET seen = true WHERE id = ? returning id";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, notification_id);
        statement.executeQuery();
    }

    // for unit tests
    public ResultSet executeCustomQuery(String query) throws SQLException {
        PreparedStatement statement = this.con.prepareStatement(query);
        ResultSet res = statement.executeQuery();
        return res;
    }

    public boolean hasUserAccessToUserContract(String userId, String userContractId)
        throws SQLException {
        String query = """
            SELECT COUNT(*) as contracts FROM user_contract uc
            WHERE uc.id = ? AND uc.user_id = ?
        """;


        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, userContractId);
        PostgresJDBCHelper.setUuid(statement, 2, userId);

        ResultSet res = statement.executeQuery();

        if (!res.next()) return false;
        return res.getInt("contracts") > 0;
    }

    /**
     * Shows whether a user has access to a notification. If the notification is for the user, then the user has access to the notification
     * @param userId The id of the user
     * @param notificationId The id of the notification
     * @return whether the notification is for the user ? true : false
     * @throws SQLException
     */
    public boolean hasUserAccessToNotification(String userId, String notificationId) throws SQLException {
        String query = """
            SELECT COUNT(*) as notifications FROM notification n
            WHERE n.id = ? and n.user_id = ?
        """;


        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, notificationId);
        PostgresJDBCHelper.setUuid(statement, 2, userId);

        ResultSet res = statement.executeQuery();

        if (!res.next()) return false;
        return res.getInt("notifications") > 0;
    }


    /**
     * Shows whether a user has access to a company. If the user works for the company, then the user has access to the company
     * @param companyId The id of the company
     * @param studentId The id of the Student
     * @return whether the student has a contract with the company ? true : false
     * @throws SQLException
     */
    public boolean hasUserAccessToCompany(String companyId, String studentId) throws SQLException {
        String query = """
            SELECT uc.id, COUNT(*) as contracts FROM user_contract uc
            JOIN contract c ON c.id = uc.contract_id
            WHERE uc.user_id = ? AND c.company_id = ?
            GROUP BY uc.id
        """;


        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, studentId);
        PostgresJDBCHelper.setUuid(statement, 2, companyId);

        ResultSet res = statement.executeQuery();

        if (!res.next()) return false;
        return res.getInt("contracts") > 0;
    }
}

