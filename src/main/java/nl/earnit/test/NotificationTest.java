package nl.earnit.test;

import nl.earnit.dao.CompanyDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserDAO;
import nl.earnit.dto.NotificationDTO;
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
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
        } catch (Exception e) {
            System.out.println("Failed to get the UserDAO, make sure you edited the run configurations");
        }
    }

    @Test
    public void testGetNotificationsForStudent() {
        try {
            int count = countNotificationsForUser();
            String insertNotificationsQuery = "INSERT INTO notification (id, user_id, company_id, worked_week_id, date, seen, type)" +
                    "VALUES" +
                    "('1aebad42-6a8e-4a5f-8a58-225be9b4bebc', '" + user_id + "', '"+ company_id + "', 'b6b0d625-b8f5-4fc7-b1c2-31434519690e', NULL, '2023-06-26', TRUE, 'HOURS'),\n" +
                    "('6f410e92-3b43-498a-8917-9a54ce2fbcfb', '" + user_id + "', '" + company_id + "', 'b6b0d625-b8f5-4fc7-b1c2-31434519690e', '9e46843e-66cc-4290-9396-456536354703', '2023-06-25', FALSE, 'APPROVED');\n";
            String wwId = "b6b0d625-b8f5-4fc7-b1c2-31434519690e";
            String insertWWQuery = "INSERT INTO worked_week (id, contract_id, year, week, status) " +
                    "VALUES ('" + wwId + "', '" + contract_id + "', 2023, 25, 'CONFIRMED')";
            userDAO.executeCustomQuery(insertWWQuery);
            userDAO.executeCustomQuery(insertNotificationsQuery);
            List<NotificationDTO> notifications = userDAO.getNotificationsForUser(user_id);
            assertNotNull(notifications);
            assertEquals(count + 2, notifications.size());
            String deleteQuery = "DELETE FROM notification WHERE id = '1aebad42-6a8e-4a5f-8a58-225be9b4bebc' OR id = '6f410e92-3b43-498a-8917-9a54ce2fbcfb'";
            String deleteWWQuery = "DELETE FROM worked_week WHERE id = " + wwId;
            userDAO.executeCustomQuery(deleteQuery);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Test1 unsuccessful");
        }
    }

    private int countNotificationsForUser() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM notification WHERE user_id = " + user_id;
        ResultSet res = userDAO.executeCustomQuery(query);
        res.next();
        return res.getInt("count");
    }


    @Test
    public void testGetNotificationsForCompany() {
        CompanyDAO companyDAO;
        try {
            int count = countNotificationsForCompany();
            String insertNotificationsQuery = "INSERT INTO notification (id, user_id, company_id, worked_week_id, date, seen, type)" +
                    "VALUES" +
                    "('1aebad42-6a8e-4a5f-8a58-225be9b4uyer', '" + user_id + "', '" + company_id + "', 'b6b0d625-b8f5-4fc7-b1c2-31434519690e', NULL, '2023-06-26', TRUE, 'HOURS'), " +
                    "('6f410e92-3b43-498a-8917-9a54ce2fghty', '" + user_id + "', '" + company_id + "', 'b6b0d625-b8f5-4fc7-b1c2-31434519690e', '9e46843e-66cc-4290-9396-456536354703', '2023-06-25', FALSE, 'APPROVED');";
            String wwId = "b6b0d625-b8f5-4fc7-b1c2-31434519690e";
            String insertWWQuery = "INSERT INTO worked_week (id, contract_id, year, week, status) " +
                    "VALUES ('" + wwId + "', '" + contract_id + "', 2023, 25, 'CONFIRMED')";
            System.out.println(insertWWQuery);
            userDAO.executeCustomQuery(insertWWQuery);
            userDAO.executeCustomQuery(insertNotificationsQuery);
            companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            List<NotificationDTO> notifications = companyDAO.getNotificationsForCompany(company_id);
            assertNotNull(notifications);
            assertEquals(count + 2, notifications.size());
            String deleteQuery = "DELETE FROM notification WHERE id = '1aebad42-6a8e-4a5f-8a58-225be9b4uyer' OR id = '6f410e92-3b43-498a-8917-9a54ce2fghty'";
            userDAO.executeCustomQuery(deleteQuery);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Test2 unsuccessfull");
        }
    }

    private int countNotificationsForCompany() throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM notification WHERE company_id = " + company_id;
        ResultSet res = userDAO.executeCustomQuery(query);
        res.next();
        return res.getInt("count");
    }

    @Test
    public void testTypeToDescriptionConversion() {
        UserDAO userDAO = null;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Test3 unsuccessful");
        }
        String message = userDAO.convertToDescription("HOURS", "Amazon", "Software engineer", "Kaya kuchta", "25");
        assertEquals(message,  "You haven't confirmed hours for Software engineer at Amazon for week 25");
        message = userDAO.convertToDescription("APPROVED", "Twitter","Product Owner", "Kaya kuchta", "25");
        assertEquals(message, "Twitter approved your suggested hours for week 25");
        message = userDAO.convertToDescription("CONFLICT", "Blackrock","Scrum Master", "Gabriel Kudow", "25");
        assertEquals(message, "Blackrock and Gabriel Kudow have a conflict for week 25");
    }

    @Test
    public void testTriggerForHours() {
        String wwId = UUID.randomUUID().toString();
        String insertQuery = "INSERT INTO worked_week (id, contract_id, year, week, status) " +
                "VALUES ('" + wwId + "', '" + contract_id + "', 2023, 20, 'NOT_CONFIRMED')";
        String updateQuery = "UPDATE worked_week SET type = CONFIRMED WHERE id = " + wwId;
        String selectQuery = "SELECT COUNT(*) AS count FROM notification WHERE user_id = '" + user_id +
                "' AND company_id = '" + company_id + "' AND type = 'HOURS'";
        try {
           userDAO.executeCustomQuery(insertQuery);
           userDAO.executeCustomQuery(updateQuery);
            ResultSet selectRes = userDAO.executeCustomQuery(selectQuery);
            selectRes.next();
            assertEquals(1, selectRes.getInt("count"));
            String deleteQuery = "DELETE FROM worked_week WHERE id = " + wwId;
            userDAO.executeCustomQuery(deleteQuery);
        }catch (SQLException e) {
            System.out.println(e);
            System.out.println("Test4 failed");
        }
    }

    @Test
    public void testTriggerForApprovedWeek() {
        String wwId = UUID.randomUUID().toString();
        String insertQuery = "INSERT INTO worked_week (id, contract_id, year, week, status) " +
                "VALUES ('" + wwId + "', '" + contract_id + "', 2023, 25, 'CONFIRMED')";
        String updateQuery = "UPDATE worked_week SET type = APPROVED WHERE id = " + wwId;
        String selectQuery = "SELECT COUNT(*) AS count FROM notification WHERE user_id = '" + user_id +
                "' AND company_id = '" + company_id + "' AND type = 'HOURS'";
        try {
            userDAO.executeCustomQuery(insertQuery);
            userDAO.executeCustomQuery(updateQuery);
            ResultSet selectRes = userDAO.executeCustomQuery(selectQuery);
            assertEquals(1, selectRes.getInt("count"));
            String deleteQuery = "DELETE FROM worked_week WHERE id = " + wwId;
            userDAO.executeCustomQuery(deleteQuery);
        }catch (SQLException e) {
            System.out.println(e);
            System.out.println("Test5 failed");
        }
    }

    @Test
    public void testTriggerForRejectedWeek() {
        String wwId = UUID.randomUUID().toString();
        String insertQuery = "INSERT INTO worked_week (id, contract_id, year, week, status) " +
                "VALUES ('" + wwId + "', '" + contract_id + "', 2023, 25, 'CONFIRMED')";
        String updateQuery = "UPDATE worked_week SET type = SUGGESTION WHERE id = " + wwId;
        String selectQuery = "SELECT COUNT(*) AS count FROM notification WHERE user_id = '" + user_id +
                "' AND company_id = '" + company_id + "' AND type = 'HOURS'";
        try {
            userDAO.executeCustomQuery(insertQuery);
            userDAO.executeCustomQuery(updateQuery);
            ResultSet selectRes = userDAO.executeCustomQuery(selectQuery);
            assertEquals(1, selectRes.getInt("count"));
            String deleteQuery = "DELETE FROM worked_week WHERE id = " + wwId;
            userDAO.executeCustomQuery(deleteQuery);
        }catch (SQLException e) {
            System.out.println(e);
            System.out.println("Test6 failed");
        }
    }


}
