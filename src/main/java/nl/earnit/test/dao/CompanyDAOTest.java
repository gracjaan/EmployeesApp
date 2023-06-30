package nl.earnit.test.dao;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import nl.earnit.Auth;
import nl.earnit.dao.CompanyDAO;
import nl.earnit.dao.ContractDAO;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.dao.UserDAO;
import nl.earnit.dto.contracts.ContractDTO;
import nl.earnit.dto.user.UserDTO;
import nl.earnit.dto.user.UserResponseDTO;
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

public class CompanyDAOTest {
    @RegisterExtension
    public SingleInstancePostgresExtension pg = EmbeddedPostgresExtension.singleInstance();

    private TestDB db;

    public void setupCompanyDAOTest() throws Exception {
        db = new TestDB(pg);
    }

    @Test
    public void testCreateCompany() throws Exception {
        setupCompanyDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        assertEquals(company.getName(), "TestCompany");
        assertEquals(company.getKvk(), "NL845838");
        assertEquals(company.getAddress(), "Finkenstraat 42, 7544NM Amsterdam");

        // Check if company actually exists
        PreparedStatement statement = con.prepareStatement("select count(*) as count, c.id from \"company\" c where c.id = ? group by c.id");
        PostgresJDBCHelper.setUuid(statement, 1, company.getId());

        ResultSet result = statement.executeQuery();
        assertTrue(result.next());
        assertEquals(result.getInt("count"), 1);

        result.close();
        con.close();
    }

    @Test
    public void testFindCompanyById() throws Exception {
        setupCompanyDAOTest();

        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);

        // Create user
        Company generatedCompany = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");

        Company company = companyDAO.getCompanyById(generatedCompany.getId());

        assertNotNull(company);

        assertEquals(company.getName(), "TestCompany");
        assertEquals(company.getKvk(), "NL845838");
        assertEquals(company.getAddress(), "Finkenstraat 42, 7544NM Amsterdam");

        con.close();
    }

    @Test
    public void testUpdateCompany() throws Exception{
        setupCompanyDAOTest();

        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);

        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        company.setName("TestCompanyUpdate");
        company.setActive(true);
        Company updCompany = companyDAO.updateCompany(company);

        assertEquals(company.getId(), updCompany.getId());
        assertEquals(updCompany.getName(), "TestCompanyUpdate");
        assertEquals(company.getKvk(), updCompany.getKvk());
        assertEquals(company.getAddress(), updCompany.getAddress());

        con.close();
    }

    @Test
    public void testGetALlCompanies() throws Exception {
        setupCompanyDAOTest();

        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company1 = companyDAO.createCompany("TestCompany1", "NL845835", "Adamstraat 42, 7544NM Amsterdam");
        Company company2 = companyDAO.createCompany("TestCompany2", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");

        List<Company> companies = companyDAO.getAllCompanies("company.name:asc");

        assertEquals(company1.getId(), companies.get(0).getId());
        assertEquals(company1.getName(), companies.get(0).getName());
        assertEquals(company1.getKvk(), companies.get(0).getKvk());
        assertEquals(company1.getAddress(), companies.get(0).getAddress());

        assertEquals(company2.getId(), companies.get(1).getId());
        assertEquals(company2.getName(), companies.get(1).getName());
        assertEquals(company2.getKvk(), companies.get(1).getKvk());
        assertEquals(company2.getAddress(), companies.get(1).getAddress());

        con.close();
    }

    @Test
    public void testHasCompanyAccessToUser() throws Exception {
        setupCompanyDAOTest();

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

        boolean flag = companyDAO.hasCompanyAccessToUser(company.getId(), user.getId());
        assertEquals(true, flag);

        con.close();
    }

    @Test
    public void getStudentsForCompany() throws Exception {
        setupCompanyDAOTest();

        Connection con = db.getConnection();

        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");

        UserDAO userDAO = new UserDAO(con);
        User user1 = userDAO.createUser("student@example.com", "John", null, "Smith", Auth.hashPassword("test"), "STUDENT",
                "12345678", "NL000099998B57", "Street 2 7522AZ");
        User user2 = userDAO.createUser("studentexample@gmail.com", "Jay", null, "Mayers", Auth.hashPassword("test"), "STUDENT",
                "12745678", "NL000095698B57", "Street 42 7522AZ");

        ContractDAO contractDAO = new ContractDAO(con);
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(null, "Engineer", "doing a lot of work"), company.getId());

        UserContractDAO userContractDAO = new UserContractDAO(con);

        UserContract userContract1 = userContractDAO.addNewUserContract(user1.getId(), contractDTO.getId(), 1200);
        UserContract userContract2 = userContractDAO.addNewUserContract(user2.getId(), contractDTO.getId(), 1500);

        List<UserResponseDTO> users = companyDAO.getStudentsForCompany(company.getId());

        assertEquals(users.get(0).getEmail(), "student@example.com");
        assertEquals(users.get(0).getFirstName(), "John");
        assertNull(users.get(0).getLastNamePrefix());
        assertEquals(users.get(0).getLastName(), "Smith");
        assertEquals(users.get(0).getType(), "STUDENT");
        assertEquals(users.get(0).getKvk(), "12345678");
        assertEquals(users.get(0).getBtw(), "NL000099998B57");
        assertEquals(users.get(0).getAddress(), "Street 2 7522AZ");

        assertEquals(users.get(1).getEmail(), "studentexample@gmail.com");
        assertEquals(users.get(1).getFirstName(), "Jay");
        assertNull(users.get(1).getLastNamePrefix());
        assertEquals(users.get(1).getLastName(), "Mayers");
        assertEquals(users.get(1).getType(), "STUDENT");
        assertEquals(users.get(1).getKvk(), "12745678");
        assertEquals(users.get(1).getBtw(), "NL000095698B57");
        assertEquals(users.get(1).getAddress(), "Street 42 7522AZ");

        con.close();
    }

    @Test
    public void testGetStudentForCompany() throws Exception {
        setupCompanyDAOTest();

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

        UserDTO userDTO = companyDAO.getStudentForCompany(company.getId(), user.getId(), true, true, "company.id:asc");
        assertEquals(userDTO.getEmail(), "student@example.com");
        assertEquals(userDTO.getFirstName(), "John");
        assertNull(userDTO.getLastNamePrefix());
        assertEquals(userDTO.getLastName(), "Smith");
        assertEquals(userDTO.getType(), "STUDENT");
        assertEquals(userDTO.getKvk(), "12345678");
        assertEquals(userDTO.getBtw(), "NL000099998B57");
        assertEquals(userDTO.getAddress(), "Street 2 7522AZ");
        assertNotNull(userDTO.getUserContracts());
        assertNotNull(userDTO.getUserContracts().get(0).getContract());

    }

    @Test
    public void testDisableCompanyById() throws Exception {
        setupCompanyDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        assertTrue(company.getActive());
        companyDAO.disableCompanyById(company.getId());
        PreparedStatement statement = con.prepareStatement("SELECT c.active FROM company c WHERE c.id = ?");
        PostgresJDBCHelper.setUuid(statement, 1, company.getId());
        ResultSet res = statement.executeQuery();
        assertTrue(res.next());
        assertFalse(res.getBoolean("active"));
        res.close();
        con.close();
    }

    @Test
    public void testHasCompanyAccessToContract() throws Exception{
        setupCompanyDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        ContractDAO contractDAO = new ContractDAO(con);
        ContractDTO contractDTO = contractDAO.createContract(new ContractDTO(UUID.randomUUID().toString(), "Engineer", "doing a lot of work"), company.getId());
        boolean flag = companyDAO.hasCompanyAccessToContract(company.getId(), contractDTO.getId());
        assertTrue(flag);
        flag = companyDAO.hasCompanyAccessToContract(UUID.randomUUID().toString(), contractDTO.getId());
        assertFalse(flag);
        con.close();
    }

}
