package nl.earnit.dao;

import nl.earnit.dto.contracts.ContractDTO;
import nl.earnit.dto.user.UserContractDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.models.UserContract;
import nl.earnit.dto.company.CompanyCountsDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

/**
 * The type User contract dao.
 */
public class UserContractDAO extends GenericDAO<User> {
    private final static String TABLE_NAME = "user_contract";

    /**
     * Instantiates a new User contract dao.
     *
     * @param con the con
     */
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

    /**
     * get all the contracts that belong to a user
     * @param userId the user you want the id's for
     * @return the list of contracts
     * @throws SQLException
     */
    public List<UserContractDTO> getUserContractsByUserId(String userId) throws SQLException {
        String query = "SELECT u.*, c.id as contract_id, c.role as contract_role, c.description as contract_description, cy.name as company_name, cy.id as company_id, cy.address as company_address, cy.kvk as company_kvk FROM  \"" + tableName + "\" u JOIN contract c ON u.contract_id = c.id JOIN company cy on cy.id = c.company_id WHERE u.user_id = ? and u.active = true";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userId);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        List<UserContractDTO> userContracts = new ArrayList<>();
        while (res.next()) {
            Company company = new Company(res.getString("company_id"), res.getString("company_name"), res.getString("company_kvk"), res.getString("company_address"));
            ContractDTO contract = new ContractDTO(res.getString("contract_id"), res.getString("contract_role"), res.getString("contract_description"));
            contract.setCompany(company);

            UserContractDTO userContract = new UserContractDTO(res.getString("id"), res.getString("contract_id"), res.getString("user_id"), res.getInt("hourly_wage"), res.getBoolean("active"));
            userContract.setContract(contract);
            userContracts.add(userContract);
        }
        return userContracts;
    }

    /**
     * Gets user contract.
     *
     * @param userId         the user id
     * @param userContractId the user contract id
     * @return the user contract
     * @throws SQLException the sql SQLException
     */
    public UserContract getUserContract(String userId, String userContractId) throws SQLException {
        String query = "SELECT contract_id, hourly_wage FROM user_contract WHERE id=?;";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userContractId);
        ResultSet res = counter.executeQuery();

        if (res.next()) return null;

        return new UserContract(userContractId, res.getString("contract_id"), res.getString("user_id"), res.getInt("hourly_wage"), res.getBoolean("active"));
    }

    /**
     * Add new user contract.
     *
     * @param user_id     the user id
     * @param contract_id the contract id
     * @param hourly_wage the hourly wage
     * @return the user contract
     * @throws SQLException the sql SQLException
     */
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

    /**
     * Enable user contract.
     *
     * @param id the id
     * @throws SQLException the sql exception
     */
    public void enableUserContract(String id) throws SQLException {

        String query = "UPDATE " + tableName + " SET active = TRUE WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);

        statement.executeUpdate();
    }

    /**
     * Disable user contract.
     *
     * @param id the id
     * @throws SQLException the sql SQLException
     */
    public void disableUserContract(String id) throws SQLException {

        String query = "UPDATE " + tableName + " SET active = FALSE WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);

        statement.executeUpdate();
    }

    /**
     * Change hourly wage.
     *
     * @param id         the id
     * @param hourlyWage the hourly wage
     * @throws SQLException the sql SQLException
     */
    public void changeHourlyWage(String id, int hourlyWage) throws SQLException {
        String query = "UPDATE " + tableName + " SET hourly_wage = ? WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setInt(1, hourlyWage);
        PostgresJDBCHelper.setUuid(statement, 2, id);
        statement.executeQuery();
    }

    /**
     * Gets user contract by id.
     *
     * @param id the id
     * @return the user contract by id
     * @throws SQLException the sql SQLException
     */
    public UserContract getUserContractById(String id) throws SQLException {
        String query = "SELECT id, contract_id, user_id, hourly_wage, active FROM " + tableName + " WHERE id = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);

        ResultSet res = statement.executeQuery();

        if (!res.next()) return null;

        return new UserContract(res.getString("id"), res.getString("contract_id"), res.getString("user_id"), res.getInt("hourly_wage"), res.getBoolean("active") );
    }

    /**
     * Gets user contracts by contract id.
     *
     * @param contractId the contract id
     * @return the user contracts by contract id
     * @throws SQLException the sql SQLException
     */
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

    /**
     * Disable user contracts by user id.
     *
     * @param userId the user id
     * @throws SQLException the sql SQLException
     */
    public void disableUserContractsByUserId(String userId) throws SQLException {
        String query = "UPDATE " + tableName + " SET active = false WHERE user_id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, userId);

        ResultSet res = statement.executeQuery();
    }

    /**
     * Gets number of employees by company.
     *
     * @return the number of employees by company
     * @throws SQLException the sql SQLException
     */
    public List<CompanyCountsDTO> getNumberOfEmployeesByCompany() throws SQLException {

        List<CompanyCountsDTO> result = new ArrayList<>();
        String query = "SELECT COUNT(uc.id) as count, c.company_id, cy.name   FROM user_contract uc JOIN contract c ON c.id = uc.contract_id JOIN company cy ON cy.id = c.company_id  WHERE  uc.active = true GROUP BY c.company_id, cy.name";
        PreparedStatement statement = this.con.prepareStatement(query);

        ResultSet res = statement.executeQuery();
        while(res.next()) {
            CompanyCountsDTO count = new CompanyCountsDTO();
            count.setId(res.getString("company_id"));
            count.setName(res.getString("name"));
            count.setCount(res.getInt("count"));

            result.add(count);

        }
        return result;
    }


}

