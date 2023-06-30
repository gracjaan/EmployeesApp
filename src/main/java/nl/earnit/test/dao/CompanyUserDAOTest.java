package nl.earnit.test.dao;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import nl.earnit.Auth;
import nl.earnit.dao.CompanyDAO;
import nl.earnit.dao.CompanyUserDAO;
import nl.earnit.dao.UserDAO;
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.test.TestDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;

public class CompanyUserDAOTest {
    @RegisterExtension
    public SingleInstancePostgresExtension pg = EmbeddedPostgresExtension.singleInstance();

    private TestDB db;

    public void setupCompanyUserDAOTest() throws Exception {
        db = new TestDB(pg);
    }

    @Test
    public void testCreateCompanyUser() throws Exception {
        setupCompanyUserDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        UserDAO userDAO = new UserDAO(con);
        User user = userDAO.createUser("companyUser@example.com", "John", null, "Smith", Auth.hashPassword("test"), "COMPANY",
                "12345678", "NL000099998B57", "Street 2 7522AZ");
        CompanyUserDAO companyUserDAO = new CompanyUserDAO(con);
        companyUserDAO.createCompanyUser(company.getId(), user.getId());

        assertTrue(companyUserDAO.isUserWorkingForCompany(company.getId(), user.getId()));
        con.close();
    }

    @Test
    public void testIsUserWorkingForCompany() throws Exception {
        setupCompanyUserDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        UserDAO userDAO = new UserDAO(con);
        User user = userDAO.createUser("companyUser@example.com", "John", null, "Smith", Auth.hashPassword("test"), "COMPANY",
                "12345678", "NL000099998B57", "Street 2 7522AZ");
        CompanyUserDAO companyUserDAO = new CompanyUserDAO(con);
        assertFalse(companyUserDAO.isUserWorkingForCompany(company.getId(), user.getId()));
        companyUserDAO.createCompanyUser(company.getId(), user.getId());
        assertTrue(companyUserDAO.isUserWorkingForCompany(company.getId(), user.getId()));
    }

    @Test
    public void testGetCompaniesUserIsWorkingFor() throws Exception {
        setupCompanyUserDAOTest();
        Connection con = db.getConnection();
        CompanyDAO companyDAO = new CompanyDAO(con);
        Company company = companyDAO.createCompany("TestCompany", "NL845838", "Finkenstraat 42, 7544NM Amsterdam");
        Company company2 = companyDAO.createCompany("Test2Company", "NL835838", "Adamstraat 42, 7544NM Amsterdam");
        UserDAO userDAO = new UserDAO(con);
        User user = userDAO.createUser("companyUser@example.com", "John", null, "Smith", Auth.hashPassword("test"), "COMPANY",
                "12345678", "NL000099998B57", "Street 2 7522AZ");
        CompanyUserDAO companyUserDAO = new CompanyUserDAO(con);
        companyUserDAO.createCompanyUser(company.getId(), user.getId());
        companyUserDAO.createCompanyUser(company2.getId(), user.getId());

        List<Company> companies = companyUserDAO.getCompaniesUserIsWorkingFor(user.getId());

        assertEquals(companies.size(), 2);
        assertEquals(companies.get(0).getId(), company.getId());
        assertEquals(companies.get(0).getKvk(), company.getKvk());
        assertEquals(companies.get(0).getAddress(), company.getAddress());

        assertEquals(companies.get(1).getId(), company2.getId());
        assertEquals(companies.get(1).getKvk(), company2.getKvk());
        assertEquals(companies.get(1).getAddress(), company2.getAddress());
    }


}
