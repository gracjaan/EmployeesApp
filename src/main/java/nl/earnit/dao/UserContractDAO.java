package nl.earnit.dao;

import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.User;
import nl.earnit.models.db.UserContract;

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
        PostgresJDBCHelper.setUuid(counter, 1, userId);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        res.next();
        return res.getInt("count");
    }

    public UserContract addNewUserContract(String user_id, String contract_id, int hourly_wage) throws SQLException {
        String query = "INSERT INTO" + tableName + "(id, contract_id, user_id, hourly_wage, active) " +
                "VALUES + (gen_random_uuid(), ?, ?, ?, True RETURNING id" ;

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, user_id);
        statement.setString(2, contract_id);
        statement.setInt(3, hourly_wage);

        ResultSet res = statement.executeQuery();

        return getUserContractById(res.getString("id"));
    }

    public void disableUserContract(String id) throws SQLException {

        String query = "UPDATE" + tableName + "SET active = False WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, id);

        statement.executeQuery();
    }

    public void changeHourlyWage(String id, int hourlyWage) throws SQLException {
        String query = "UPDATE" + tableName + "SET hourly_wage = ? WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setInt(1, hourlyWage);
        statement.setString(2, id);

        statement.executeQuery();
    }

    public UserContract getUserContractById( String id) throws SQLException {
        String query = "SELECT id, contract_id, user_id, hourly_wage, active " + tableName + " WHERE id = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, id);

        ResultSet res = statement.executeQuery();
        return new UserContract(res.getString("id"), res.getString("contract_id"), res.getString("user_id"), res.getInt("hourly_wage"), res.getBoolean("active") );
    }
}

