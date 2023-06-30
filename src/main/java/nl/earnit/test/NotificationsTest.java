package nl.earnit.test;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import nl.earnit.Auth;
import nl.earnit.dao.*;
import nl.earnit.dto.NotificationDTO;
import nl.earnit.dto.contracts.ContractDTO;
import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.models.UserContract;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NotificationsTest {
    @RegisterExtension
    public SingleInstancePostgresExtension pg = EmbeddedPostgresExtension.singleInstance();

    private TestDB db;

    public void setupNotificationsTest() throws Exception {
        db = new TestDB(pg);
    }

    @Test
    public void testNotificationTriggers() throws Exception {
        setupNotificationsTest();
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

        workedWeekDAO.setWorkedWeekStatus(workedWeekId, "CONFIRMED", false, false, false, false, false, false, "worked_week.year:asc,worked_week.week:asc");
        workedWeekDAO.setWorkedWeekStatus(workedWeekId, "APPROVED", false, false, false, false, false, false, "worked_week.year:asc,worked_week.week:asc");
        assertEquals(1, countNotificationsForUser(user.getId()));
        workedWeekDAO.setWorkedWeekStatus(workedWeekId, "CONFIRMED", false, false, false, false, false, false, "worked_week.year:asc,worked_week.week:asc");
        workedWeekDAO.setWorkedWeekStatus(workedWeekId, "SUGGESTED", false, false, false, false, false, false, "worked_week.year:asc,worked_week.week:asc");
        assertEquals(2, countNotificationsForUser(user.getId()));
    }

    private int countNotificationsForUser(String user_id) throws Exception {
        String query = "SELECT COUNT(*) AS count FROM notification WHERE user_id = ?";
        PreparedStatement statement = db.getConnection().prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, user_id);
        ResultSet res = statement.executeQuery();
        res.next();
        return res.getInt("count");
    }

    private int countNotificationsForCompany(String company_id) throws Exception {
        String query = "SELECT COUNT(*) AS count FROM notification WHERE company_id = ?";
        PreparedStatement statement = db.getConnection().prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, company_id);
        ResultSet res = statement.executeQuery();
        res.next();
        return res.getInt("count");
    }

    @Test
    public void testGetNotificationsForStudent() throws Exception {
        setupNotificationsTest();
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

        workedWeekDAO.setWorkedWeekStatus(workedWeekId, "CONFIRMED", false, false, false, false, false, false, "worked_week.year:asc,worked_week.week:asc");
        workedWeekDAO.setWorkedWeekStatus(workedWeekId, "APPROVED", false, false, false, false, false, false, "worked_week.year:asc,worked_week.week:asc");
        List<NotificationDTO> notifications = userDAO.getNotificationsForUser(user.getId());
        assertEquals(notifications.get(0).getType(), "APPROVED");
        assertEquals(notifications.get(0).getUserId(), user.getId());
        assertEquals(notifications.get(0).getCompanyId(), company.getId());
    }
    @Test
    public void testTypeToTitleDescription() throws Exception {
        setupNotificationsTest();
        Connection con = db.getConnection();
        UserDAO userDAO = new UserDAO(con);
        String message = userDAO.convertToDescription("HOURS", "Amazon", "Software engineer", "Kaya kuchta", "25");
        assertEquals(message,  "You haven't confirmed hours for Software engineer at Amazon for week 25");
        message = userDAO.convertToDescription("APPROVED", "Twitter","Product Owner", "Kaya kuchta", "25");
        assertEquals(message, "Twitter approved your suggested hours for week 25");
        message = userDAO.convertToDescription("CONFLICT", "Blackrock","Scrum Master", "Gabriel Kudow", "25");
        assertEquals(message, "Blackrock and Gabriel Kudow have a conflict for week 25");
    }


}
