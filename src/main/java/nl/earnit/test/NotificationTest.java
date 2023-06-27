package nl.earnit.test;

import nl.earnit.dao.CompanyDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.GenericDAO;
import nl.earnit.dao.UserDAO;
import nl.earnit.dto.workedweek.NotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NotificationTest {

    // add db-specific user_id
    private String user_id = "1e92c3ab-9a95-4c6d-8f7f-75541e8106f1";
    private String company_id = "b6b0d625-b8f5-4fc7-b1c2-31434519690e";
    @Test
    public void testGetNotificationsForStudent() {
        UserDAO userDAO;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
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
        String message = userDAO.convertToMessage("HOURS", "Amazon", "Kaya kuchta");
        assertEquals(message,  "You haven't confirmed hours for Amazon yet");
        message = userDAO.convertToMessage("APPROVED", "Twitter", "Kaya kuchta");
        assertEquals(message, "Twitter approved your suggested hours");
        message = userDAO.convertToMessage("CONFLICT", "Blackrock", "Gabriel Kudow");
        assertEquals(message, "Blackrock and Gabriel Kudow have a conflict");
    }

    // test the triggers


}
