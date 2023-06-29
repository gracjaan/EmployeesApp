package nl.earnit.test.dao;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import nl.earnit.Auth;
import nl.earnit.dao.UserDAO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.User;
import nl.earnit.test.TestDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    @RegisterExtension
    public SingleInstancePostgresExtension pg = EmbeddedPostgresExtension.singleInstance();

    private TestDB db;

    public void setupUserDAOTest() throws Exception {
        db = new TestDB(pg);
        db.setupDB();
    }

    @Test
    public void testCreateUser() throws Exception {
        setupUserDAOTest();

        Connection con = db.getConnection();
        UserDAO userDAO = new UserDAO(con);

        // Create user
        User user = userDAO.createUser("student@example.com", "John", null, "Smith", Auth.hashPassword("test"), "STUDENT",
            "12345678", "NL000099998B57", "Street 2 7522AZ");

        assertEquals(user.getEmail(), "student@example.com");
        assertEquals(user.getFirstName(), "John");
        assertNull(user.getLastNamePrefix());
        assertEquals(user.getLastName(), "Smith");
        assertNotEquals(user.getPassword(), "test");
        assertEquals(user.getType(), "STUDENT");
        assertEquals(user.getKvk(), "12345678");
        assertEquals(user.getBtw(), "NL000099998B57");
        assertEquals(user.getAddress(), "Street 2 7522AZ");

        // Check if user actually exists
        PreparedStatement statement = con.prepareStatement("select count(*) as count, u.id from \"user\" u where u.id = ? group by u.id");
        PostgresJDBCHelper.setUuid(statement, 1, user.getId());

        ResultSet result = statement.executeQuery();
        assertTrue(result.next());
        assertEquals(result.getInt("count"), 1);

        result.close();
        con.close();
    }

    @Test
    public void testFindUserById() throws Exception {
        setupUserDAOTest();

        Connection con = db.getConnection();
        UserDAO userDAO = new UserDAO(con);

        // Create user
        User generatedUser = userDAO.createUser("student@example.com", "John", null, "Smith", Auth.hashPassword("test"), "STUDENT",
            "12345678", "NL000099998B57", "Street 2 7522AZ");

        // Create user
        User user = userDAO.getUserById(generatedUser.getId());

        assertNotNull(user);

        assertEquals(user.getEmail(), "student@example.com");
        assertEquals(user.getFirstName(), "John");
        assertNull(user.getLastNamePrefix());
        assertEquals(user.getLastName(), "Smith");
        assertNotEquals(user.getPassword(), "test");
        assertEquals(user.getType(), "STUDENT");
        assertEquals(user.getKvk(), "12345678");
        assertEquals(user.getBtw(), "NL000099998B57");
        assertEquals(user.getAddress(), "Street 2 7522AZ");

        con.close();
    }

    @Test
    public void testFindUserByEmail() throws Exception {
        setupUserDAOTest();

        Connection con = db.getConnection();
        UserDAO userDAO = new UserDAO(con);

        // Create user
        User generatedUser = userDAO.createUser("student@example.com", "John", null, "Smith", Auth.hashPassword("test"), "STUDENT",
            "12345678", "NL000099998B57", "Street 2 7522AZ");

        // Create user
        User user = userDAO.getUserByEmail(generatedUser.getEmail());

        assertNotNull(user);

        assertEquals(user.getEmail(), "student@example.com");
        assertEquals(user.getFirstName(), "John");
        assertNull(user.getLastNamePrefix());
        assertEquals(user.getLastName(), "Smith");
        assertNotEquals(user.getPassword(), "test");
        assertEquals(user.getType(), "STUDENT");
        assertEquals(user.getKvk(), "12345678");
        assertEquals(user.getBtw(), "NL000099998B57");
        assertEquals(user.getAddress(), "Street 2 7522AZ");

        con.close();
    }
}
