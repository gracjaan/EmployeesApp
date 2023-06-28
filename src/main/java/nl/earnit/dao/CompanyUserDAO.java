package nl.earnit.dao;

import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Company user dao.
 */
public class CompanyUserDAO extends GenericDAO<User> {
    private final static String TABLE_NAME = "company_user";

    /**
     * Instantiates a new Company user dao.
     *
     * @param con the con
     */
    public CompanyUserDAO(Connection con) {
        super(con, TABLE_NAME);
    }

    /**
     * returns all the users that are registered as company users (ADMINISTRATORS)
     * @return the amount of company users
     * @throws SQLException
     */
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
     * Checks if a user works for a company.
     *
     * @param companyId The id of a company.
     * @param userId    The id of a user.
     * @return the boolean
     * @throws SQLException If a database error occurs.
     */
    public boolean isUserWorkingForCompany(String companyId, String userId) throws SQLException {
        // Create query
        String query = "SELECT COUNT(*) AS count FROM  \"" + tableName + "\" WHERE \"user_id\" = ? AND \"company_id\" = ?";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userId);
        PostgresJDBCHelper.setUuid(counter, 2, companyId);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        res.next();
        return res.getInt("count") > 0;
    }

    /**
     * Get all the companies a user is working for.
     *
     * @param userId The id of a user.
     * @return the companies user is working for
     * @throws SQLException If a database error occurs.
     */
    public List<Company> getCompaniesUserIsWorkingFor(String userId) throws SQLException {
        // Create query
        String query = "SELECT c.* AS count FROM  \"" + tableName + "\" t join company c on c.id = t.company_id WHERE c.active IS TRUE AND \"user_id\" = ?";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userId);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        List<Company> companies = new ArrayList<>();
        while (res.next()) {
            companies.add(new Company(res.getString("id"), res.getString("name"), res.getString("kvk"), res.getString("address")));
        }

        return companies;
    }

    /**
     * Creates a company user (ADMINISTRATOR)
     * @param companyId the id of the company you want to create an admin for
     * @param userId The userID that will be used for the making of the administrator
     * @return returns whether the user was created successfully
     * @throws SQLException
     */
    public boolean createCompanyUser(String companyId, String userId) throws SQLException {
        String query = "INSERT INTO \"" + tableName + "\" (company_id, user_id) "+
            "VALUES (?, ?) RETURNING company_id";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, companyId);
        PostgresJDBCHelper.setUuid(statement, 2, userId);
        ResultSet res = statement.executeQuery();
        return res.next();
    }
}

