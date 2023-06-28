package nl.earnit.test;

import nl.earnit.dao.CompanyDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserDAO;
import nl.earnit.dto.workedweek.NotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// add environment variables in the run configuration for this test (DB variables)
public class NotificationTest {

    // set this to ids that exist and are linked in your db
    private String user_id = "1e92c3ab-9a95-4c6d-8f7f-75541e8106f1";
    private String company_id = "b6b0d625-b8f5-4fc7-b1c2-31434519690e";
    private String contract_id = "3a9cfee3-443f-46e4-a2ae-06c456c071c9";
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        // create a contract between user and company
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);

        } catch (Exception e) {
            System.out.println("Failed to get the UserDAO, make sure you edited the run configurations");
        }
    }

    @Test
    public void testGetNotificationsForStudent() {
        try {
            List<NotificationDTO> notifications = userDAO.getNotificationsForUser(user_id);
            assertNotNull(notifications);
            // db-specific
            assertEquals(4, notifications.size());
        } catch (Exception e) {
            System.out.println("Test unsuccessful");
        }
    }

    @Test
    public void testGetNotificationsForCompany() {
        CompanyDAO companyDAO;
        try {
            companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            List<NotificationDTO> notifications = companyDAO.getNotificationsForCompany(company_id);
            assertNotNull(notifications);
            // db-specific
            assertEquals(2, notifications.size());
        } catch (Exception e) {
            System.out.println("Test unsuccessfull");
        }
    }

    @Test
    public void testTypeToMessageConversion() {
        UserDAO userDAO = null;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
        } catch (Exception e) {
            System.out.println("Test unsuccessfull");
        }
        String message = userDAO.convertToMessage("HOURS", "Amazon", "Kaya kuchta", "25");
        assertEquals(message,  "You haven't confirmed hours for Amazon in week 25 yet");
        message = userDAO.convertToMessage("APPROVED", "Twitter", "Kaya kuchta", "25");
        assertEquals(message, "Twitter approved your suggested hours for week 25");
        message = userDAO.convertToMessage("CONFLICT", "Blackrock", "Gabriel Kudow", "25");
        assertEquals(message, "Blackrock and Gabriel Kudow have a conflict in week 25");
    }

    @Test
    public void testTriggerForHours() {
        String wwId = UUID.randomUUID().toString();
        String insertQuery = "INSERT INTO worked_week (id, contract_id, week, status) " +
                "VALUES ('" + wwId + "', '" + contract_id + "', 1, 'NOT_CONFIRMED')";
        String updateQuery = "UPDATE worked_week SET type = CONFIRMED WHERE id = " + wwId;
        String selectQuery = "SELECT COUNT(*) AS count FROM notification WHERE user_id = '" + user_id +
                "' AND company_id = '" + company_id + "' AND type = 'HOURS'";
        try {
            ResultSet insertRes = userDAO.executeCustomQuery(insertQuery);
            ResultSet updateRes = userDAO.executeCustomQuery(updateQuery);
            ResultSet selectRes = userDAO.executeCustomQuery(selectQuery);
            assertEquals(1, selectRes.getInt("count"));
        }catch (SQLException e) {
            System.out.println("Test failed");
        }
    }

    @Test
    public void testTriggerForApprovedWeek() {
        String wwId = UUID.randomUUID().toString();
        String insertQuery = "INSERT INTO worked_week (id, contract_id, week, status) " +
                "VALUES ('" + wwId + "', '" + contract_id + "', 25, 'CONFIRMED')";
        String updateQuery = "UPDATE worked_week SET type = APPROVED WHERE id = " + wwId;
        String selectQuery = "SELECT COUNT(*) AS count FROM notification WHERE user_id = '" + user_id +
                "' AND company_id = '" + company_id + "' AND type = 'HOURS'";
        try {
            ResultSet insertRes = userDAO.executeCustomQuery(insertQuery);
            ResultSet updateRes = userDAO.executeCustomQuery(updateQuery);
            ResultSet selectRes = userDAO.executeCustomQuery(selectQuery);
            assertEquals(1, selectRes.getInt("count"));
        }catch (SQLException e) {
            System.out.println("Test failed");
        }
    }

    @Test
    public void testTriggerForRejectedWeek() {
        String wwId = UUID.randomUUID().toString();
        String insertQuery = "INSERT INTO worked_week (id, contract_id, week, status) " +
                "VALUES ('" + wwId + "', '" + contract_id + "', 25, 'CONFIRMED')";
        String updateQuery = "UPDATE worked_week SET type = SUGGESTION WHERE id = " + wwId;
        String selectQuery = "SELECT COUNT(*) AS count FROM notification WHERE user_id = '" + user_id +
                "' AND company_id = '" + company_id + "' AND type = 'HOURS'";
        try {
            ResultSet insertRes = userDAO.executeCustomQuery(insertQuery);
            ResultSet updateRes = userDAO.executeCustomQuery(updateQuery);
            ResultSet selectRes = userDAO.executeCustomQuery(selectQuery);
            assertEquals(1, selectRes.getInt("count"));
        }catch (SQLException e) {
            System.out.println("Test failed");
        }
    }


}
