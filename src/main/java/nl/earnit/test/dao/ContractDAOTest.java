package nl.earnit.test.dao;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import nl.earnit.Auth;
import nl.earnit.dao.CompanyDAO;
import nl.earnit.dao.ContractDAO;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.dao.UserDAO;
import nl.earnit.dto.contracts.ContractDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.models.UserContract;
import nl.earnit.test.TestDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ContractDAOTest {
    @RegisterExtension
    public SingleInstancePostgresExtension pg = EmbeddedPostgresExtension.singleInstance();

    private TestDB db;

    public void setupContractDAOTest() throws Exception {
        db = new TestDB(pg);
    }

    @Test
    public void testCreateContract() throws Exception {
        setupContractDAOTest();
        Connection con = db.getConnection();
        ContractDAO contractDAO = new ContractDAO(con);
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany1", "NL845835", "Adamstraat 42, 7544NM Amsterdam");
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(null, "Engineer", "doing a lot of work"), company.getId());

        assertEquals(contractDTO.getRole(), "Engineer");
        assertEquals(contractDTO.getDescription(), "doing a lot of work");

        // check if the contract actually is in the db
        PreparedStatement statement = con.prepareStatement("select count(*) as count, c.id from \"contract\" c where c.id = ? group by c.id");
        PostgresJDBCHelper.setUuid(statement, 1, contractDTO.getId());

        ResultSet result = statement.executeQuery();
        assertTrue(result.next());
        assertEquals(result.getInt("count"), 1);

        result.close();
        con.close();

    }

    @Test
    public void testGetAllContractsByCompanyId() throws Exception{
        setupContractDAOTest();
        Connection con = db.getConnection();
        ContractDAO contractDAO = new ContractDAO(con);
        String order = "contract.role:asc,user_contract.user.last_name:asc";
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany1", "NL845835", "Adamstraat 42, 7544NM Amsterdam");
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(null, "Engineer", "doing a lot of work"), company.getId());

        List<ContractDTO> contracts = contractDAO.getAllContractsByCompanyId(
                company.getId(), true, false, false, order
        );

        assertEquals(contracts.get(0).getCompany().getId(), company.getId());
        assertEquals(contracts.get(0).getCompany().getName(), company.getName());
        assertEquals(contracts.get(0).getCompany().getKvk(), company.getKvk());
        assertEquals(contracts.get(0).getCompany().getAddress(), company.getAddress());

        assertEquals(contracts.get(0).getId(), contractDTO.getId());
        assertEquals(contracts.get(0).getRole(), contractDTO.getRole());
        assertEquals(contracts.get(0).getDescription(), contractDTO.getDescription());

        assertNull(contracts.get(0).getUserContracts());

        con.close();
    }

    @Test
    public void testUpdateContract() throws Exception {
        setupContractDAOTest();
        Connection con = db.getConnection();
        ContractDAO contractDAO = new ContractDAO(con);
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany1", "NL845835", "Adamstraat 42, 7544NM Amsterdam");
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(null, "Engineer", "doing a lot of work"), company.getId());

        contractDAO.updateContractRole(contractDTO.getId(), "Updated role");
        contractDAO.updateContractDescription(contractDTO.getId(), "Updated description");

        ContractDTO updatedContract = contractDAO.getContract(contractDTO.getId());

        assertEquals(contractDTO.getId(), updatedContract.getId());
        assertEquals("Updated role", updatedContract.getRole());
        assertEquals("Updated description", updatedContract.getDescription());

        con.close();
    }

    @Test
    public void testDisableContract() throws Exception{
        setupContractDAOTest();
        Connection con = db.getConnection();
        ContractDAO contractDAO = new ContractDAO(con);
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany1", "NL845835", "Adamstraat 42, 7544NM Amsterdam");
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(null, "Engineer", "doing a lot of work"), company.getId());
        companyDAO.disableCompanyById(company.getId());
        PreparedStatement statement = con.prepareStatement("SELECT c.active FROM contract c WHERE c.id = ?");
        PostgresJDBCHelper.setUuid(statement, 1, contractDTO.getId());
        ResultSet res = statement.executeQuery();
        assertTrue(res.next());
        assertFalse(res.getBoolean("active"));
        res.close();
        con.close();
    }

    @Test
    public void testHasAccessToUserContract() throws Exception {
        setupContractDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        UserDAO userDAO = new UserDAO(con);
        User user = userDAO.createUser("student@example.com", "John", null, "Smith", Auth.hashPassword("test"), "STUDENT",
                "12345678", "NL000099998B57", "Street 2 7522AZ");
        ContractDAO contractDAO = new ContractDAO(con);
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(null, "Engineer", "doing a lot of work"), company.getId());
        UserContractDAO userContractDAO = new UserContractDAO(con);
        UserContract userContract = userContractDAO.addNewUserContract(user.getId(), contractDTO.getId(), 12);
        boolean flag = contractDAO.hasContractAccessToUserContract(contractDTO.getId(), userContract.getId());
        assertTrue(flag);
        flag = contractDAO.hasContractAccessToUserContract(UUID.randomUUID().toString(), userContract.getId());
        assertFalse(flag);
    }
}
