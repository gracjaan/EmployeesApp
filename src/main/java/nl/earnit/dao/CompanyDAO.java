package nl.earnit.dao;

import nl.earnit.dto.workedweek.ContractDTO;
import nl.earnit.dto.workedweek.NotificationDTO;
import nl.earnit.dto.workedweek.UserContractDTO;
import nl.earnit.dto.workedweek.UserDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.Company;
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

import static nl.earnit.Constants.getName;

/**
 * The companyDAO is used to access the entries in the company table of the database
 * This stores all the companies that use the application
 */
public class CompanyDAO extends GenericDAO<User> {
    private final static String TABLE_NAME = "company";

    /**
     * Instantiates a new Company dao.
     *
     * @param con the con
     */
    public CompanyDAO(Connection con) {
        super(con, TABLE_NAME);
    }

    /**
     * Counts all the companies that are on the platform and are active
     * @return the amount of active companies
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
     * Get company with given id.
     * @param id The id of the company.
     * @return The company or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public Company getCompanyById(String id) throws SQLException {
        // Create query
        String query =
            "SELECT id, name, kvk, address FROM \"" + tableName + "\" WHERE \"id\" = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return Company
        return new Company(res.getString("id"), res.getString("name"), res.getString("kvk"), res.getString("address"));
    }

    /**
     * creates a company entry in the database
     * @param name the name of the company
     * @param kvk the kvk number
     * @param address the address of
     * @return the company object that was just created
     * @throws SQLException
     */
    public Company createCompany(String name, String kvk, String address)
        throws SQLException {
        // Create query
        String query = "INSERT INTO " + tableName + " (name, kvk, address) VALUES (?,?,?) RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, name);
        statement.setString(2, kvk);
        statement.setString(3, address);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return company
        return getCompanyById(res.getString("id"));
    }

    /**
     * Update a company object in the database
     * @param company the company object with the id you want to update and the other parameters set to updated values
     * @return the updated company object
     * @throws SQLException
     */
    public Company updateCompany(Company company) throws SQLException {
        // Create query
        String query = "UPDATE \"" + tableName + "\" SET name = ?, kvk = ?, address = ?, active = ? WHERE \"id\" = ? RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, company.getName());
        statement.setString(2, company.getKvk());
        statement.setString(3, company.getAddress());
        statement.setBoolean(4, company.getActive());
        PostgresJDBCHelper.setUuid(statement, 5, company.getId());

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return company
        return getCompanyById(res.getString("id"));
    }

    /**
     * Gets all the companies whether they are active or not
     * @param order whether the way that the companies are returned are in a certain order: asc, desc
     * @return A list with all the company objects
     * @throws SQLException
     */
    public List<Company> getAllCompanies(String order) throws SQLException {
        OrderBy orderBy = new OrderBy(new HashMap<>() {{
            put("company.name", "name");
            put("company.id", "id");
        }});

        ArrayList<Company> companyList = new ArrayList<>();
        String query = "SELECT id, name, kvk, address, active FROM " + tableName + " ORDER BY " + orderBy.getSQLOrderBy(order, false) ;
        PreparedStatement statement = this.con.prepareStatement(query);

        ResultSet res = statement.executeQuery();

        while(res.next()) {
            Company company = new Company();
            company.setId(res.getString("id"));
            company.setName(res.getString("name"));
            company.setActive(res.getBoolean("active"));
            company.setKvk(res.getString("kvk"));
            company.setAddress(res.getString("address"));
            companyList.add(company);
        }

        return companyList;
    }

    /**
     * gets all the companies that are active
     * @param order whether the way that the companies are returned are in a certain order: asc, desc
     * @return The list of companies
     * @throws SQLException
     */
    public List<Company> getAllCompaniesUsers(String order) throws SQLException {
        ArrayList<Company> companyList = new ArrayList<>();
        String query = "SELECT * FROM " + tableName + "WHERE active = true ORDER BY ? " ;
        PreparedStatement statement = this.con.prepareStatement(query);


        if(order.equals("name")) {
            statement.setString(1, order);
        } else {
            statement.setString(1, "id");
        }

        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        while(res.next()) {
            Company company = new Company();
            company.setId(res.getString("id"));
            company.setName(res.getString("name"));
            company.setKvk(res.getString("kvk"));
            company.setAddress(res.getString("address"));
            companyList.add(company);
        }

        return companyList;
    }

    /**
     * Shows whether a company has access to a user. If the user works for the company, then the company has access to the user
     * @param companyId The id of the company
     * @param studentId The id of the Student
     * @return whether the student has a contract with the company ? true : false
     * @throws SQLException
     */
    public boolean hasCompanyAccessToUser(String companyId, String studentId) throws SQLException {
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

        return res.next();
    }

    /**
     * Gets all the students that work for a company
     * @param companyId the id of the company where you want the employees of
     * @return A list of Users formatted as a UserResponse object
     * @throws SQLException
     */
    public List<UserResponse> getStudentsForCompany(String companyId) throws SQLException {
        String query = """
            SELECT u.id, u.first_name, u.last_name, u.last_name_prefix, u.type, u.email, u.btw, u.kvk, u.address FROM "user" u, company_user c WHERE u.id = c.user_id AND c.company_id = ?""";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, companyId);
        ResultSet resultSet = statement.executeQuery();
        List<UserResponse> users = new ArrayList<>();
        while (resultSet.next()) {
            UserResponse user = new UserResponse(resultSet.getString("id"), resultSet.getString("email"), resultSet.getString("first_name"), resultSet.getString("last_name"), resultSet.getString("last_name_prefix"), resultSet.getString("type"), resultSet.getString("btw"), resultSet.getString("kvk"), resultSet.getString("address"));
            users.add(user);
        }
        return users;
    }
    /**
     * Gets a particular user that works for a particular company
     * @param companyId The company id
     * @param studentId The student id
     * @param withUserContracts Whether we want the contract with the result ? true : false
     * @param withUserContractsContract
     * @param order
     * @return 
     * @throws SQLException
     */
    public UserDTO getStudentForCompany(String companyId, String studentId, boolean withUserContracts, boolean withUserContractsContract, String order) throws SQLException {
        OrderBy orderByContracts = new OrderBy(new HashMap<>() {{
            put("contract.id", "c.id");
            put("contract.company_id", "c.company_id");
            put("contract.role", "c.role");

            put("user_contract.contract_id", "uc.contract_id");
            put("user_contract.user_id", "uc.user_id");
            put("user_contract.hourly_wage", "uc.hourly_wage");
            put("user_contract.active", "uc.active");
        }});

        String query = """
            SELECT u.id, u.first_name, u.last_name, u.last_name_prefix, u.type, u.email, u.kvk, u.btw, u.address, uc.user_contracts FROM "user" u
            JOIN (select c.company_id, uc.user_id, array_agg((uc.*, c.*)%s) as user_contracts from user_contract uc
                    join contract c on c.id = uc.contract_id
                    group by c.id, uc.user_id
                ) uc on uc.user_id = u.id AND uc.company_id = ?
            WHERE u.id = ?
        """.formatted(orderByContracts.getSQLOrderBy(order, true));

        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, companyId);
        PostgresJDBCHelper.setUuid(statement, 2, studentId);

        ResultSet res = statement.executeQuery();

        if (!res.next()) return null;

        UserDTO user =
            new UserDTO(res.getString("id"), res.getString("email"),
                res.getString("first_name"), res.getString("last_name"),
                res.getString("last_name_prefix"), res.getString("type"), res.getString("kvk"),res.getString("btw"), res.getString("address"));

        if (withUserContracts) {
            List<UserContractDTO> userContracts = new ArrayList<>();
            ResultSet userContractsSet = res.getArray("user_contracts").getResultSet();

            while (userContractsSet.next()) {
                String data = ((PGobject) userContractsSet.getObject("VALUE")).getValue();
                if (data == null) continue;

                data = data.substring(1, data.length() - 1);
                String[] dataStrings = data.split(",");

                UserContractDTO userContract = new UserContractDTO(dataStrings[0], dataStrings[1], dataStrings[2], Integer.parseInt(dataStrings[3]), dataStrings[4].equals("t"));

                if (withUserContractsContract) {
                    String role = dataStrings[7];
                    if (role.startsWith("\"") && role.endsWith("\"")) role = role.substring(1, role.length() - 1);

                    String description = dataStrings[8];
                    description = description.substring(1, description.length() - 1);

                    ContractDTO contract = new ContractDTO(dataStrings[5], role, description);
                    userContract.setContract(contract);
                }

                userContracts.add(userContract);
            }

            user.setUserContracts(userContracts);
        }

        return user;
    }

    /**
     * gets a company by its id only if it's disabled
     * @param id the id of the company
     * @throws SQLException
     */
    public void disableCompanyById(String id) throws SQLException {
        String query = "UPDATE \"" + tableName + "\" SET active = false WHERE id = ? returning id";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);
        statement.executeQuery();
    }

    public List<NotificationDTO> getNotificationsForCompany(String company_id) throws SQLException {
        if (company_id==null) {
            return null;
        }
        List<NotificationDTO> notifications = new ArrayList<>();
        String query = """
            SELECT n.*, u.first_name, u.last_name_prefix, u.last_name, ww.week, c.role, u.id as user_id, cy.id as company_id, ww.id as worked_week_id, uc.id as user_contract_id, c.id as contract_id 
            FROM "notification" n
            JOIN "user" u ON u.id = n.user_id
            JOIN company cy ON cy.id = n.company_id
            LEFT JOIN worked_week ww ON ww.id = n.worked_week_id
            LEFT JOIN user_contract uc ON uc.id = ww.contract_id 
            LEFT JOIN contract c ON c.id = uc.contract_id 
            WHERE n.company_id = ? AND n.type != 'CONFLICT'
            ORDER BY n.date DESC, n.seen
            """;
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, company_id);
        ResultSet res = statement.executeQuery();
        while (res.next()) {
            String user_name = getName(res.getString("first_name"), res.getString("last_name_prefix"),  res.getString("last_name"));
            String week = res.getString("week");
            String role = res.getString("role");

            String title = "";
            String description = "";
            String type = res.getString("type");
            switch (type) {
                case "SUGGESTION ACCEPTED":
                    title = "Suggestion accepted";
                    description = user_name + " accepted your suggestion for week " + week + " for the position of " + role;
                    break;
                case "SUGGESTION REJECTED":
                    title = "Suggestion rejected";
                    description = user_name + " rejected your suggested hours for week " + week + " for the position of " + role;
                    break;
                case "LINK":
                    title = "New employee";
                    description = "New employee " + user_name;
                    break;
                case "CONFLICT":
                    title = "Conflict";
                    description = "You have a conflict in week " + week + " with " + user_name;
                    break;
                default:
                    continue;
            }

            NotificationDTO notification = new NotificationDTO(res.getString("id"), res.getString("date"), res.getBoolean("seen"), type, title, description, res.getString("user_id"), res.getString("company_id"), res.getString("user_contract_id"), res.getString("contract_id"), res.getString("worked_week_id"), res.getInt("week"));
            notifications.add(notification);
        }
        return notifications;
    }
}

