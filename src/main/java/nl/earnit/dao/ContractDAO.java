package nl.earnit.dao;

import nl.earnit.models.db.User;
import nl.earnit.models.resource.contracts.Contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContractDAO extends GenericDAO<User> {

    private final static String TABLE_NAME = "contract";

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

    public List<Contract> getAllContractsByCompanyId(String companyId) throws SQLException {

        List<Contract> result = new ArrayList<>();

        String query = "SELECT role, description  FROM  " + tableName + "WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, companyId);

        ResultSet res = statement.executeQuery();

        // None foundA
        if(!res.next()) return null;

        while(res.next()) {
            Contract contract = new Contract();
            contract.setDescription(res.getString("description"));
            contract.setRole(res.getString("role"));
            result.add(contract);
        }
        return result;

    }

    public void updateContractDescription(String contractId, String description) throws SQLException {
        String query = "UPDATE" + tableName + "SET description = ? WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, description);
        statement.setString(2, contractId);

        statement.executeQuery();
    }

    public void updateContractRole(String contractId, String role) throws SQLException {
        String query = "UPDATE" + tableName + "SET role = ? WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, role);
        statement.setString(2, contractId);

        statement.executeQuery();
    }

    public Contract getContract(String contractId) throws SQLException {
        String query = "GET description, role FROM" + tableName + " WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, contractId);

        ResultSet res = statement.executeQuery();

        if(!res.next()) return null;
        return new Contract(contractId, res.getString("description"), res.getString("role") );

    }

    public void deleteContract(String contractId) throws SQLException {
        String query = "DELETE * FROM" + tableName + " WHERE id = ?";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, contractId);

        statement.executeQuery();
    }

}
