package nl.earnit.dao;

import nl.earnit.dto.contracts.ContractDTO;
import nl.earnit.dto.user.UserContractDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.dto.user.UserResponseDTO;
import org.postgresql.util.PGobject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The type Contract dao.
 */
public class ContractDAO extends GenericDAO<User> {
    private final OrderBy orderBy = new OrderBy(new HashMap<>() {{
        put("contract.id", "c.id");
        put("contract.role", "c.role");

        put("company.id", "cy.id");
        put("company.name", "cy.name");
    }});

    private final OrderBy orderByUserContracts = new OrderBy(new HashMap<>() {{
        put("user_contract.id", "uc.id");
        put("user_contract.user_id", "uc.user_id");
        put("user_contract.hourly_wage", "uc.hourly_wage");

        put("user_contract.user.id", "u.id");
        put("user_contract.user.email", "u.email");
        put("user_contract.user.first_name", "u.first_name");
        put("user_contract.user.last_name", "u.last_name");
        put("user_contract.user.last_name_prefix", "u.last_name_prefix");
    }});

    private final static String TABLE_NAME = "contract";

    /**
     * Instantiates a new Contract dao.
     *
     * @param con the con
     */
    public ContractDAO(Connection con) {
        super(con, TABLE_NAME);
    }

    /**
     * Get row count in table.
     *
     * @return Row count
     * @throws SQLException If a database error occurs.
     */
    @Override
    public int count() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM  \"" + tableName + "\"";
        PreparedStatement counter = this.con.prepareStatement(query);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        res.next();
        return res.getInt("count");
    }
    /**
     * gets all the contracts that a company has
     * @param companyId the id of the company
     * @param withCompany
     * @param withUserContracts
     * @param withUserContractsUser
     * @param order
     * @return
     * @throws SQLException
     */
    public List<ContractDTO> getAllContractsByCompanyId(String companyId, boolean withCompany, boolean withUserContracts, boolean withUserContractsUser, String order) throws SQLException {
        List<ContractDTO> result = new ArrayList<>();

        String query = """
                SELECT c.id, c.role, c.description, cy.id as company_id, cy.name as company_name, cy.kvk as company_kvk, cy.address as company_address, uc.user_contracts FROM "%s" c
                JOIN company cy ON cy.id = c.company_id
                LEFT JOIN (select uc.contract_id,
                            array_agg((uc.id, uc.contract_id, uc.user_id, uc.hourly_wage, u.id, u.email, u.first_name, u.last_name, u.last_name_prefix, u.type, u.kvk, u.btw, u.address)%s) as user_contracts
                            from user_contract uc
                            join "user" u on u.id = uc.user_id
                            where uc.active IS TRUE
                            group by uc.contract_id
                          ) uc ON uc.contract_id = c.id
                WHERE c.company_id = ? and c.active = true
                %s""".formatted(tableName, orderByUserContracts.getSQLOrderBy(order, true), orderBy.getSQLOrderBy(order, true));

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, companyId);

        ResultSet res = statement.executeQuery();

        while (res.next()) {
            ContractDTO contract = new ContractDTO(res.getString("id"), res.getString("role"), res.getString("description"));

            if (withCompany) {
                contract.setCompany(new Company(res.getString("company_id"), res.getString("company_name"), res.getString("company_kvk"), res.getString("company_address")));
            }

            if (withUserContracts) {
                List<UserContractDTO> userContracts = new ArrayList<>();
                Array userContractsArray = res.getArray("user_contracts");
                if(!res.wasNull()) {
                    ResultSet userContractsSet = userContractsArray.getResultSet();

                    while (userContractsSet.next()) {
                        String data = ((PGobject) userContractsSet.getObject("VALUE")).getValue();
                        if (data == null) continue;

                        data = data.substring(1, data.length() - 1);
                        String[] dataStrings = data.split(",");

                        UserContractDTO userContract = new UserContractDTO(dataStrings[0], dataStrings[1], dataStrings[2], Integer.parseInt(dataStrings[3]), true);

                        if (withUserContractsUser) {
                            String firstName = dataStrings[6];
                            if (firstName.startsWith("\"") && firstName.endsWith("\"")) firstName = firstName.substring(1, firstName.length() - 1);

                            String lastName = dataStrings[7];
                            if (lastName.startsWith("\"") && lastName.endsWith("\"")) lastName = lastName.substring(1, lastName.length() - 1);

                            String lastNamePrefix = dataStrings[8];
                            if (lastNamePrefix.startsWith("\"") && lastNamePrefix.endsWith("\"")) lastNamePrefix = lastNamePrefix.substring(1, lastNamePrefix.length() - 1);

                            userContract.setUser(new UserResponseDTO(dataStrings[4], dataStrings[5], firstName, lastName, lastNamePrefix, dataStrings[9], dataStrings[10], dataStrings[11], dataStrings[12]));
                        }

                        userContracts.add(userContract);
                    }
                }

                contract.setUserContracts(userContracts);
            }


            result.add(contract);
        }

        return result;
    }

    /**
     * Update contract description.
     *
     * @param contractId  the contract id
     * @param description the description
     * @throws SQLException the sql SQLException
     */
    public void updateContractDescription(String contractId, String description) throws SQLException {
        String query = "UPDATE " + tableName + " SET description = ? WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, description);
        PostgresJDBCHelper.setUuid(statement, 2, contractId);

        statement.executeQuery();
    }

    /**
     * Update contract role.
     *
     * @param contractId the contract id
     * @param role       the role
     * @throws SQLException the sql SQLException
     */
    public void updateContractRole(String contractId, String role) throws SQLException {
        String query = "UPDATE " + tableName + " SET role = ? WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, role);
        PostgresJDBCHelper.setUuid(statement, 2, contractId);

        statement.executeQuery();
    }

    /**
     * Creates a contract for a company that users can then be linked to
     * @param contract the contract object you want to insert in the database
     * @param company_id the id of the company where the contract is for
     * @throws SQLException
     */
    public void createContract(ContractDTO contract, String company_id) throws SQLException {
        String query = "INSERT INTO \"" + tableName + "\" (company_id, role, description) "+
                "VALUES (?, ?, ?) RETURNING id";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, company_id);
        statement.setString(2, contract.getRole());
        statement.setString(3, contract.getDescription());
        ResultSet res = statement.executeQuery();
    }

    /**
     * Gets contract.
     *
     * @param contractId the contract id
     * @return the contract
     * @throws SQLException the sql SQLException
     */
    public ContractDTO getContract(String contractId) throws SQLException {
        String query = "GET description, role FROM " + tableName + " WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, contractId);

        ResultSet res = statement.executeQuery();

        if (!res.next()) {
            return null;
        }

        return new ContractDTO(contractId, res.getString("description"), res.getString("role"));
    }

    /**
     * disables the contract for a company and automatically all the links with users that associate to the contract
     * @param contractId the id of the contract
     * @throws SQLException
     */
    public void disableContract(String contractId) throws SQLException {
        String query = "UPDATE " + tableName + " SET active = false WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, contractId);

        statement.executeQuery();
    }

    /**
     * Renable contract.
     *
     * @param contractId the contract id
     * @throws SQLException the sql SQLException
     */
    public void renableContract(String contractId) throws SQLException {
        String query = "UPDATE " + tableName + " SET active = true WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, contractId);

        statement.executeQuery();
    }

    /**
     * disables all the contracts for a company
     * @param companyId the company id you want to disable the contracts for
     * @throws SQLException
     */
    public void disableContractsByCompanyId(String companyId) throws SQLException {
        String query = "UPDATE " + tableName + " SET active = false WHERE company_id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, companyId);

        ResultSet res = statement.executeQuery();
    }

    /**
     * Shows whether a contract has access to a user contract.
     * @param contractId The id of the contract
     * @param userContractId The id of the user contract
     * @return whether the user contract is for the contract ? true : false
     * @throws SQLException
     */
    public boolean hasContractAccessToUserContract(String contractId, String userContractId) throws SQLException {
        String query = """
            SELECT COUNT(*) as contracts FROM user_contract uc
            WHERE uc.id = ? and uc.contract_id = ?
        """;


        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, userContractId);
        PostgresJDBCHelper.setUuid(statement, 2, contractId);

        ResultSet res = statement.executeQuery();

        if (!res.next()) return false;
        return res.getInt("contracts") > 0;
    }
}
