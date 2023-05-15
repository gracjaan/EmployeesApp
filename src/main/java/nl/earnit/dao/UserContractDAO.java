package nl.earnit.dao;

import jakarta.annotation.Nullable;
import nl.earnit.models.db.User;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserContractDAO extends GenericDAO<User> {
    private final static String TABLE_NAME = "user_contract";

    public UserContractDAO(Connection con) {
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
     * Counts the contracts for a user.
     * @param userId The id of a user.
     * @throws SQLException If a database error occurs.
     */
    public int countContractsForUser(String userId) throws SQLException {
        // Create query
        String query = "SELECT COUNT(*) AS count FROM  \"" + tableName + "\" WHERE \"user_id\" = ?";
        PreparedStatement counter = this.con.prepareStatement(query);

        PGobject forUser = new PGobject();
        forUser.setType("uuid");
        forUser.setValue(userId);
        counter.setObject(1, forUser);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        res.next();
        return res.getInt("count");
    }
}

