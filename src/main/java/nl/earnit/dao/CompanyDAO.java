package nl.earnit.dao;

import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CompanyDAO extends GenericDAO<User> {
    private final static String TABLE_NAME = "company";

    public CompanyDAO(Connection con) {
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
        PGobject toInsert = new PGobject();
        toInsert.setType("uuid");
        toInsert.setValue(id);
        statement.setObject(1, toInsert);

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
        String query = "INSERT INTO \"" + tableName + "\" (name) VALUES (?) RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, name);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return company
        return getCompanyById(res.getString("id"));
    }
}

