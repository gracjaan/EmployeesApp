package nl.earnit.test.dao;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import nl.earnit.Auth;
import nl.earnit.dao.*;
import nl.earnit.dto.company.CreateSuggestionDTO;
import nl.earnit.dto.contracts.ContractDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.models.UserContract;
import nl.earnit.models.Worked;
import nl.earnit.test.TestDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class WorkedDAOTest {
    @RegisterExtension
    public SingleInstancePostgresExtension pg = EmbeddedPostgresExtension.singleInstance();

    private TestDB db;

    public void setupWorkedDAOTest() throws Exception {
        db = new TestDB(pg);
    }

    @Test
    public void addWorkedWeekTask() throws Exception {
        setupWorkedDAOTest();
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
        workedWeekDAO.addWorkedWeek(contractDTO.getId(), "2023", "30");
        String workedWeekId = workedWeekDAO.getWorkedWeekIdByDate(userContract.getId(), 2023, 30);
        WorkedDAO workedDAO = new WorkedDAO(con);
        Worked worked = new Worked(UUID.randomUUID().toString(), workedWeekId, 2, 240, "did something cool");
        boolean flag = workedDAO.addWorkedWeekTask(worked, userContract.getId(), "2023", "30");
        assertTrue(flag);

        PreparedStatement statement = con.prepareStatement("select count(*) as count, w.id from \"worked\" w where w.worked_week_id = ? group by w.id");
        PostgresJDBCHelper.setUuid(statement, 1, workedWeekId);

        ResultSet result = statement.executeQuery();
        assertTrue(result.next());
        assertEquals(result.getInt("count"), 1);

        // test delete task
        workedDAO.deleteWorkedWeekTask(result.getString("id"));
        result = statement.executeQuery();
        assertFalse(result.next());


        result.close();
    }


    @Test
    public void testGetWorkedHours() throws Exception {
        setupWorkedDAOTest();
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

        List<Worked> workeds = workedDAO.getWorkedHours(userContract.getId());

        assertEquals(workeds.get(0).getWork(), "did something cool");
        assertEquals(workeds.get(0).getDay(), 2);
        assertTrue(workeds.get(0).getMinutes() == 240 && workeds.get(1).getMinutes() == 180 || workeds.get(1).getMinutes() == 240 && workeds.get(0).getMinutes() == 180);
        assertEquals(workeds.get(0).getWorkedWeekId(), workedWeekId);

    }

    @Test
    public void testSuggestions() throws Exception {
        setupWorkedDAOTest();
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
        List<Worked> workeds = workedDAO.getWorkedWeek(userContract.getId(), 2023, 30);
        workedDAO.setSuggestion(workeds.get(0).getId(),new CreateSuggestionDTO(200));

        PreparedStatement statment = con.prepareStatement("SELECT suggestion FROM worked WHERE id = ?");
        PostgresJDBCHelper.setUuid(statment, 1, workeds.get(0).getId());
        ResultSet res = statment.executeQuery();
        res.next();
        assertEquals(res.getInt("suggestion"), 200);

        workedDAO.acceptStudentSuggestion(workedWeekId);

        res = statment.executeQuery();
        res.next();
        res.getInt("suggestion");
        assertTrue(res.wasNull());

        res.close();
    }

}
