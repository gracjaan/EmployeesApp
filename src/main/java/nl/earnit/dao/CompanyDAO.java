package nl.earnit.dao;

import nl.earnit.dto.workedweek.ContractDTO;
import nl.earnit.dto.workedweek.UserContractDTO;
import nl.earnit.dto.workedweek.UserDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;
import nl.earnit.models.db.Worked;
import nl.earnit.models.resource.users.UserResponse;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CompanyDAO extends GenericDAO<User> {
    private final static String TABLE_NAME = "company";

    public CompanyDAO(Connection con) {
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
     * Get company with given id.
     * @param id The id of the company.
     * @return The company or null if not found.
     * @throws SQLException If a database error occurs.
     */
    public Company getCompanyById(String id) throws SQLException {
        // Create query
        String query =
            "SELECT id, name FROM \"" + tableName + "\" WHERE \"id\" = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return Company
        return new Company(res.getString("id"), res.getString("name"));
    }

    public Company createCompany(String name)
        throws SQLException {
        // Create query
        String query = "INSERT INTO " + tableName + " (name) VALUES (?) RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, name);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return company
        return getCompanyById(res.getString("id"));
    }

    public Company updateCompany(Company company) throws SQLException {
        // Create query
        String query = "UPDATE \"" + tableName + "\" SET name = ? WHERE \"id\" = ? RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, company.getName());
        PostgresJDBCHelper.setUuid(statement, 2, company.getId());

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return company
        return getCompanyById(res.getString("id"));
    }

    public List<Company> getAllCompanies(String order) throws SQLException {
        OrderBy orderBy = new OrderBy(new HashMap<>() {{
            put("company.name", "name");
            put("company.id", "id");
        }});

        ArrayList<Company> companyList = new ArrayList<>();
        String query = "SELECT id, name FROM " + tableName + " WHERE active = true ORDER BY " + orderBy.getSQLOrderBy(order, false) ;
        PreparedStatement statement = this.con.prepareStatement(query);

        ResultSet res = statement.executeQuery();

        while(res.next()) {
            Company company = new Company();
            company.setId(res.getString("id"));
            company.setName(res.getString("name"));
            companyList.add(company);
        }

        return companyList;
    }

    public List<Company> getAllCompaniesUsers(String order) throws SQLException {
        ArrayList<Company> companyList = new ArrayList<>();
        String query = "SELECT id, name FROM " + tableName + "WHERE active = true ORDER BY ? " ;
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
            companyList.add(company);
        }

        return companyList;
    }

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

    public List<UserResponse> getStudentsForCompany(String companyId) throws SQLException {
        String query = "SELECT u.id, u.first_name, u.last_name, u.last_name_prefix, u.type, u.email FROM user u, company_user c WHERE u.id = c.user_id AND c.company_id = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, companyId);
        ResultSet resultSet = statement.executeQuery();
        List<UserResponse> users = new ArrayList<>();
        while (resultSet.next()) {
            UserResponse user = new UserResponse(resultSet.getString("id"), resultSet.getString("email"), resultSet.getString("first_name"), resultSet.getString("last_name"), resultSet.getString("last_name_prefix"), resultSet.getString("type"));
            users.add(user);
        }
        return users;
    }


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
            SELECT u.id, u.first_name, u.last_name, u.last_name_prefix, u.type, u.email, uc.user_contracts FROM "user" u
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
                res.getString("last_name_prefix"), res.getString("type"));

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
}

