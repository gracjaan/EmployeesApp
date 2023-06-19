package nl.earnit.dao;

import nl.earnit.dto.workedweek.ContractDTO;
import nl.earnit.dto.workedweek.UserContractDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.User;
import nl.earnit.models.db.UserContract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        String query = "SELECT COUNT(*) AS count FROM  \"" + tableName + "\" u WHERE u.user_id = ? and u.active = true";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userId);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        res.next();
        return res.getInt("count");
    }

    public List<UserContractDTO> getUserContractsByUserId(String userId) throws SQLException {
        String query = "SELECT u.*, c.* FROM  \"" + tableName + "\" u JOIN contract c ON u.contract_id = c.id WHERE u.user_id = ? and u.active = true";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userId);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        List<UserContractDTO> userContracts = new ArrayList<>();
        while (res.next()) {
            ContractDTO c = new ContractDTO(res.getString("id"), res.getString("role"), res.getString("description"));
            UserContractDTO uc = new UserContractDTO(res.getString("id"), res.getString("contract_id"), res.getString("user_id"), res.getInt("hourly_wage"), res.getBoolean("active"));
            uc.setContract(c);
            userContracts.add(uc);
        }
        return userContracts;
    }

    public UserContract getUserContract(String userId, String userContractId) throws SQLException {
        String query = "SELECT contract_id, hourly_wage FROM user_contract WHERE id=?;";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userContractId);
        ResultSet res = counter.executeQuery();

        if (res.next()) return null;

        return new UserContract(userContractId, res.getString("contract_id"), res.getString("user_id"), res.getInt("hourly_wage"), res.getBoolean("active"));
    }

    public UserContract addNewUserContract(String user_id, String contract_id, int hourly_wage) throws SQLException {
        String query = "INSERT INTO " + tableName + " (contract_id, user_id, hourly_wage) " +
                "VALUES (?, ?, ?) RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, contract_id);
        PostgresJDBCHelper.setUuid(statement, 2, user_id);
        statement.setInt(3, hourly_wage);

        ResultSet res = statement.executeQuery();

        if (!res.next()) return  null;

        return getUserContractById(res.getString("id"));
    }

    public void disableUserContract(String id) throws SQLException {

        String query = "UPDATE " + tableName + " SET active = False WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);

        statement.executeQuery();
    }

    public void changeHourlyWage(String id, int hourlyWage) throws SQLException {
        String query = "UPDATE " + tableName + " SET hourly_wage = ? WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setInt(1, hourlyWage);
        PostgresJDBCHelper.setUuid(statement, 2, id);
        statement.executeQuery();
    }

    public UserContract getUserContractById(String id) throws SQLException {
        String query = "SELECT id, contract_id, user_id, hourly_wage, active FROM " + tableName + " WHERE id = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);

        ResultSet res = statement.executeQuery();

        if (!res.next()) return null;

        return new UserContract(res.getString("id"), res.getString("contract_id"), res.getString("user_id"), res.getInt("hourly_wage"), res.getBoolean("active") );
    }

    public List<UserContract> getUserContractsByContractId(String contractId) throws SQLException {
        List<UserContract> result = new ArrayList<>();
        String query = "SELECT id, contract_id, user_id, hourly_wage, active FROM " + tableName + " WHERE contract_id = ? and active = true ";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, contractId);

        ResultSet res = statement.executeQuery();


        while(res.next()) {
            UserContract userContract = new UserContract();
            userContract.setContractId(res.getString("contract_id"));
            userContract.setUserId(res.getString("user_id"));
            userContract.setId(res.getString("id"));
            userContract.setActive(res.getBoolean("active"));
            userContract.setHourlyWage(res.getInt("hourly_wage"));
            result.add(userContract);
        }

        return result;

    }

    public void disableUserContractsByUserId(String userId) throws SQLException {
        String query = "UPDATE " + tableName + " SET active = false WHERE user_id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, userId);

        ResultSet res = statement.executeQuery();
    }
}

