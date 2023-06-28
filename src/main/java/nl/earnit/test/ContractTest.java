package nl.earnit.test;

import nl.earnit.dao.ContractDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.WorkedDAO;
import nl.earnit.dto.workedweek.ContractDTO;
import nl.earnit.models.resource.contracts.Contract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The type Contract test.
 */
public class ContractTest {
    /**
     * The Contract dao.
     */
    ContractDAO contractDAO;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        try {
            contractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test get all contracts by company id.
     *
     * @throws SQLException the sql exception
     */
    @Test
    void testGetAllContractsByCompanyId() throws SQLException {
        // Prepare test data
        String companyId = "9bfda60f-f88c-46b7-a4b7-8c49fc8e10be";
        boolean withCompany = true;
        boolean withUserContracts = true;
        boolean withUserContractsUser = true;
        String order = "contract.role:asc,user_contract.user.last_name:asc";

        // Call the method under test
        List<ContractDTO> contracts = contractDAO.getAllContractsByCompanyId(
                companyId, withCompany, withUserContracts, withUserContractsUser, order
        );

        assertNotNull(contracts);
        // Assert additional conditions on the returned contracts if necessary
    }

//    @Test
//    void testUpdateContractDescription() throws SQLException {
//        // Prepare test data
//        String contractId = "ae3edddb-992e-4c0c-adac-380ef360a781";
//        String description = "New contract description";
//
//        contractDAO.updateContractDescription(contractId, description);
//
//        // Retrieve the contract from the DAO
//        Contract updatedContract = contractDAO.getContract(contractId);
//
//        // Perform assertions
//        assertNotNull(updatedContract);
//        assertEquals(description, updatedContract.getDescription());
//    }

//    @Test
//    void testUpdateContractRole() throws SQLException {
//        // Prepare test data
//        String contractId = "ae3edddb-992e-4c0c-adac-380ef360a781";
//        String role = "New contract role";
//
//        // Call the method under test
//        contractDAO.updateContractRole(contractId, role);
//
//        // Retrieve the contract from the DAO
//        Contract updatedContract = contractDAO.getContract(contractId);
//
//        // Perform assertions
//        assertNotNull(updatedContract);
//        assertEquals(role, updatedContract.getRole());
//    }

//    @Test
//    void testCreateContract() throws SQLException {
//        // Prepare test data
//        String companyId = "9bfda60f-f88c-46b7-a4b7-8c49fc8e10be";
//        Contract contract = new Contract("ae3edddb-992e-4c0c-adac-380ef360a781","Contract role", "Contract description");
//
//        // Call the method under test
//        contractDAO.createContract(contract, companyId);
//
//        // Retrieve the contract from the DAO
//        Contract createdContract = contractDAO.getContract(contract.getId());
//
//        // Perform assertions
//        assertNotNull(createdContract);
//        // Assert additional conditions on the created contract if necessary
//    }



}
