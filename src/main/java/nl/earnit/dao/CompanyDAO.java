package nl.earnit.dao;

import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;
import nl.earnit.models.resource.users.UserResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public Company getCompanyByName(String name) throws SQLException {
        // Create query
        String query =
                "SELECT id, name FROM \"" + tableName + "\" WHERE \"name\" = ?";
        PreparedStatement statement = this.con.prepareStatement(query);

        statement.setString(1, name);
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


}

