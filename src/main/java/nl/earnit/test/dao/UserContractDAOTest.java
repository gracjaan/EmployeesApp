package nl.earnit.test.dao;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import nl.earnit.Auth;
import nl.earnit.dao.*;
import nl.earnit.dto.contracts.ContractDTO;
import nl.earnit.dto.user.UserContractDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.models.UserContract;
import nl.earnit.models.Worked;
import nl.earnit.test.TestDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserContractDAOTest {
    @RegisterExtension
    public SingleInstancePostgresExtension pg = EmbeddedPostgresExtension.singleInstance();

    private TestDB db;

    public void setupUserContractDAOTest() throws Exception {
        db = new TestDB(pg);
    }

    @Test
    public void testGetUserContractById() throws Exception{
        setupUserContractDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        UserDAO userDAO = new UserDAO(con);
        User user = userDAO.createUser("student@example.com", "John", null, "Smith", Auth.hashPassword("test"), "STUDENT",
                "12345678", "NL000099998B57", "Street 2 7522AZ");
        ContractDAO contractDAO = new ContractDAO(con);
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(UUID.randomUUID().toString(), "Engineer", "doing a lot of work"), company.getId());
        UserContractDAO userContractDAO = new UserContractDAO(con);
        UserContract userContract = userContractDAO.addNewUserContract(user.getId(), contractDTO.getId(), 12);

        UserContract uc = userContractDAO.getUserContractById(userContract.getId());

        assertEquals(userContract.getId(), uc.getId());
        assertEquals(userContract.getUserId(), uc.getUserId());
        assertEquals(userContract.getContractId(), uc.getContractId());
        assertEquals(userContract.getHourlyWage(), uc.getHourlyWage());

        con.close();
    }

    @Test
    public void addNewUserContract() throws Exception {
        setupUserContractDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        UserDAO userDAO = new UserDAO(con);
        User user = userDAO.createUser("student@example.com", "John", null, "Smith", Auth.hashPassword("test"), "STUDENT",
                "12345678", "NL000099998B57", "Street 2 7522AZ");
        ContractDAO contractDAO = new ContractDAO(con);
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(UUID.randomUUID().toString(), "Engineer", "doing a lot of work"), company.getId());
        UserContractDAO userContractDAO = new UserContractDAO(con);
        UserContract userContract = userContractDAO.addNewUserContract(user.getId(), contractDTO.getId(), 12);

        assertNotNull(userContract);
        assertEquals(userContract.getUserId(), user.getId());
        assertEquals(userContract.getContractId(), contractDTO.getId());
        assertEquals(userContract.getHourlyWage(), 12);

        con.close();
    }

    @Test
    public void testDisableUserContract() throws Exception {
        setupUserContractDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        UserDAO userDAO = new UserDAO(con);
        User user = userDAO.createUser("student@example.com", "John", null, "Smith", Auth.hashPassword("test"), "STUDENT",
                "12345678", "NL000099998B57", "Street 2 7522AZ");
        ContractDAO contractDAO = new ContractDAO(con);
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(UUID.randomUUID().toString(), "Engineer", "doing a lot of work"), company.getId());
        UserContractDAO userContractDAO = new UserContractDAO(con);
        UserContract userContract = userContractDAO.addNewUserContract(user.getId(), contractDTO.getId(), 12);
        userContractDAO.disableUserContract(userContract.getId());

        PreparedStatement statement = con.prepareStatement("SELECT c.active FROM user_contract c WHERE c.id = ?");
        PostgresJDBCHelper.setUuid(statement, 1, contractDTO.getId());
        ResultSet res = statement.executeQuery();
        assertTrue(res.next());
        assertFalse(res.getBoolean("active"));
        res.close();
        con.close();
    }

    @Test
    public void testChangeHourlyWage() throws Exception{
        setupUserContractDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        UserDAO userDAO = new UserDAO(con);
        User user = userDAO.createUser("student@example.com", "John", null, "Smith", Auth.hashPassword("test"), "STUDENT",
                "12345678", "NL000099998B57", "Street 2 7522AZ");
        ContractDAO contractDAO = new ContractDAO(con);
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(UUID.randomUUID().toString(), "Engineer", "doing a lot of work"), company.getId());
        UserContractDAO userContractDAO = new UserContractDAO(con);
        UserContract userContract = userContractDAO.addNewUserContract(user.getId(), contractDTO.getId(), 12);

        userContractDAO.changeHourlyWage(userContract.getId(), 15);

        UserContract updUserContract = userContractDAO.getUserContract(user.getId(), userContract.getId());
        assertEquals(userContract.getContractId(), updUserContract.getContractId());
        assertEquals(15, updUserContract.getHourlyWage());

        con.close();

    }

    @Test
    public void testGetUserContracts() throws Exception {
        setupUserContractDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        UserDAO userDAO = new UserDAO(con);
        User user = userDAO.createUser("student@example.com", "John", null, "Smith", Auth.hashPassword("test"), "STUDENT",
                "12345678", "NL000099998B57", "Street 2 7522AZ");
        ContractDAO contractDAO = new ContractDAO(con);
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(UUID.randomUUID().toString(), "Engineer", "doing a lot of work"), company.getId());
        UserContractDAO userContractDAO = new UserContractDAO(con);
        UserContract userContract = userContractDAO.addNewUserContract(user.getId(), contractDTO.getId(), 12);

        List<UserContractDTO> ucByUserId = userContractDAO.getUserContractsByUserId(user.getId());
        List<UserContract> ucByContractId = userContractDAO.getUserContractsByContractId(contractDTO.getId());

        assertEquals(userContract.getId(), ucByUserId.get(0).getId());
        assertEquals(userContract.getUserId(), ucByUserId.get(0).getUserId());
        assertEquals(userContract.getContractId(), ucByUserId.get(0).getContractId());
        assertEquals(userContract.getHourlyWage(), ucByUserId.get(0).getHourlyWage());

        assertEquals(userContract.getId(), ucByContractId.get(0).getId());
        assertEquals(userContract.getUserId(), ucByContractId.get(0).getUserId());
        assertEquals(userContract.getContractId(), ucByContractId.get(0).getContractId());
        assertEquals(userContract.getHourlyWage(), ucByContractId.get(0).getHourlyWage());

        con.close();
    }

  
}
