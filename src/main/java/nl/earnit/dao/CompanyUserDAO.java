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

public class CompanyUserDAO extends GenericDAO<User> {
    private final static String TABLE_NAME = "company_user";

    public CompanyUserDAO(Connection con) {
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
     * Checks if a user works for a company.
     * @param companyId The id of a company.
     * @param userId The id of a user.
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
     * @param userId The id of a user.
     * @throws SQLException If a database error occurs.
     */
    public List<Company> getCompaniesUserIsWorkingFor(String userId) throws SQLException {
        // Create query
        String query = "SELECT c.* AS count FROM  \"" + tableName + "\" t join company c on c.id = t.company_id WHERE \"user_id\" = ?";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userId);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        List<Company> companies = new ArrayList<>();
        while (res.next()) {
            companies.add(new Company(res.getString("id"), res.getString("name")));
        }

        return companies;
    }

    public boolean createCompanyUser(String companyId, String userId) throws SQLException {
        String query = "INSERT INTO \"" + tableName + "\" (company_id, user_id) "+
            "VALUES (?, ?) RETURNING id";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, companyId);
        PostgresJDBCHelper.setUuid(statement, 2, userId);
        ResultSet res = statement.executeQuery();
        return res.next();
    }
}

