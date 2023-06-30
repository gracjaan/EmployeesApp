package nl.earnit.test.dao;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import nl.earnit.Auth;
import nl.earnit.dao.*;
import nl.earnit.dto.contracts.ContractDTO;
import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.models.UserContract;
import nl.earnit.models.Worked;
import nl.earnit.TestDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class WorkedWeekDAOTest {
    @RegisterExtension
    public SingleInstancePostgresExtension pg = EmbeddedPostgresExtension.singleInstance();

    private TestDB db;

    public void setupWorkedWeekDAOTest() throws Exception {
        db = new TestDB(pg);
    }

    @Test
    public void testConfirmWorkedWeek() throws Exception {
        setupWorkedWeekDAOTest();
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
        WorkedWeekDAO workedWeekDAO = new WorkedWeekDAO(con);
        workedWeekDAO.addWorkedWeek(userContract.getId(), "2023", "30");
        String workedWeekId = workedWeekDAO.getWorkedWeekIdByDate(userContract.getId(), 2023, 30);
        workedWeekDAO.confirmWorkedWeek(userContract.getId(), "2023", "30");
        assertTrue(workedWeekDAO.isWorkedWeekConfirmed(workedWeekId));
        workedWeekDAO.removeConfirmWorkedWeek(userContract.getId(), "2023", "30");
        assertFalse(workedWeekDAO.isWorkedWeekConfirmed(workedWeekId));
    }

    @Test
    public void testAddWorkedWeekNote() throws Exception {
        setupWorkedWeekDAOTest();
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
        WorkedWeekDAO workedWeekDAO = new WorkedWeekDAO(con);
        workedWeekDAO.addWorkedWeek(userContract.getId(), "2023", "30");
        String workedWeekId = workedWeekDAO.getWorkedWeekIdByDate(userContract.getId(), 2023, 30);
        assertNull(workedWeekDAO.getWorkedWeekById(workedWeekId).getNote());
        workedWeekDAO.addWorkedWeekNote("update", userContract.getId(), "2023", "30");
        String note = workedWeekDAO.getWorkedWeekById(workedWeekId).getNote();
        assertEquals(note, "update");
    }

    @Test
    public void testGetWorkedWeeksForUser() throws Exception {
        setupWorkedWeekDAOTest();
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
        WorkedWeekDAO workedWeekDAO = new WorkedWeekDAO(con);
        workedWeekDAO.addWorkedWeek(userContract.getId(), "2023", "30");
        String workedWeekId = workedWeekDAO.getWorkedWeekIdByDate(userContract.getId(), 2023, 30);
        WorkedDAO workedDAO = new WorkedDAO(con);
        Worked first = new Worked(UUID.randomUUID().toString(), workedWeekId, 2, 240, "did something cool");
        assertTrue(workedDAO.addWorkedWeekTask(first, userContract.getId(), "2023", "30"));
        Worked second = new Worked(UUID.randomUUID().toString(), workedWeekId, 2, 180, "did something cool");
        assertTrue(workedDAO.addWorkedWeekTask(second, userContract.getId(), "2023", "30"));

        List<WorkedWeekDTO> workedWeeks = workedWeekDAO.getWorkedWeeksForUser(user.getId(), userContract.getId(), 2023, 30, true, true, true,true,false, true, "worked_week.year:asc,worked_week.week:asc");
        WorkedWeekDTO ww = workedWeeks.get(0);
        assertEquals(ww.getUser().getId(), user.getId());
        assertEquals(ww.getCompany().getId(), company.getId());
        assertEquals(ww.getUserContract().getId(), userContract.getId());
        assertEquals(ww.getYear(), 2023);
        assertEquals(ww.getWeek(), 30);
        assertEquals(ww.getTotalMinutes(), 420);

        con.close();
    }

    @Test
    public void testGetWorkedWeeksForCompanies() throws Exception {
        setupWorkedWeekDAOTest();
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
        WorkedWeekDAO workedWeekDAO = new WorkedWeekDAO(con);
        workedWeekDAO.addWorkedWeek(userContract.getId(), "2023", "30");
        String workedWeekId = workedWeekDAO.getWorkedWeekIdByDate(userContract.getId(), 2023, 30);
        WorkedDAO workedDAO = new WorkedDAO(con);
        Worked first = new Worked(UUID.randomUUID().toString(), workedWeekId, 2, 240, "did something cool");
        assertTrue(workedDAO.addWorkedWeekTask(first, userContract.getId(), "2023", "30"));
        Worked second = new Worked(UUID.randomUUID().toString(), workedWeekId, 2, 180, "did something cool");
        assertTrue(workedDAO.addWorkedWeekTask(second, userContract.getId(), "2023", "30"));

        workedWeekDAO.setWorkedWeekStatus(workedWeekId, "APPROVED", false, false, false, false, false, false, "");

        List<WorkedWeekDTO> workedWeeks = workedWeekDAO.getWorkedWeeksForCompany(company.getId(), 2023, 30, true, true, true,true,false, false, "worked_week.year:asc,worked_week.week:asc");
        WorkedWeekDTO ww = workedWeeks.get(0);
        assertEquals(ww.getCompany().getId(), company.getId());
        assertEquals(ww.getContract().getId(), contractDTO.getId());
        assertEquals(ww.getUserContract().getId(), userContract.getId());
        assertEquals(ww.getYear(), 2023);
        assertEquals(ww.getWeek(), 30);
        assertNull(ww.getHours());
        assertNull(ww.getTotalMinutes());
        con.close();
    }

    @Test
    public void testSetWorkedWeekStatus() throws Exception {
        setupWorkedWeekDAOTest();
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
        WorkedWeekDAO workedWeekDAO = new WorkedWeekDAO(con);
        workedWeekDAO.addWorkedWeek(userContract.getId(), "2023", "30");
        String workedWeekId = workedWeekDAO.getWorkedWeekIdByDate(userContract.getId(), 2023, 30);

        WorkedWeekDTO ww = workedWeekDAO.setWorkedWeekStatus(workedWeekId, "APPROVED", false, false, false, false, false, false, "worked_week.year:asc,worked_week.week:asc");
        assertNull(ww.getCompany());
        assertNull(ww.getContract());
        assertNull(ww.getUserContract());
        assertNull(ww.getUser());
        assertNull(ww.getHours());
        assertNull(ww.getTotalMinutes());
        assertEquals(ww.getStatus(), "APPROVED");
    }

    @Test
    public void testIsWorkedWeekSuggested() throws Exception {
        setupWorkedWeekDAOTest();
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
        WorkedWeekDAO workedWeekDAO = new WorkedWeekDAO(con);
        workedWeekDAO.addWorkedWeek(userContract.getId(), "2023", "30");
        String workedWeekId = workedWeekDAO.getWorkedWeekIdByDate(userContract.getId(), 2023, 30);
        assertFalse(workedWeekDAO.isWorkedWeekSuggested(workedWeekId));
        WorkedWeekDTO ww = workedWeekDAO.setWorkedWeekStatus(workedWeekId, "SUGGESTED", false, false, false, false, false, false, "worked_week.year:asc,worked_week.week:asc");
        assertTrue(workedWeekDAO.isWorkedWeekSuggested(workedWeekId));
    }








}
