package nl.earnit.test;

import nl.earnit.dao.DAOManager;
import nl.earnit.dao.WorkedDAO;
import nl.earnit.dao.WorkedWeekDAO;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.db.Worked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// To run: edit the run configurations and pass it all the environment variables for the DB
public class WorkedTest {
    WorkedDAO workedDAO;

    @BeforeEach
   public void setUp() {
        try {
            workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void count_ShouldReturnCorrectRowCount() throws SQLException {
        int preCount = workedDAO.count();
        // Prepare test data
        insertDummyWorkedEntries(3);

        // Perform the count operation
        int count = workedDAO.count();

        // Assert the result
        assertEquals(3, count - preCount);
    }


    @Test
    void addWorkedWeekTask_ShouldAddNewWorkedWeekTask() throws SQLException {
        // Prepare test data
        Worked worked = createDummyWorked();
        String userContractId = "3a9cfee3-443f-46e4-a2ae-06c456c071c9";
        String year = "2023";
        String week = "25";

        // Perform the addWorkedWeekTask operation
        boolean success = workedDAO.addWorkedWeekTask(worked, userContractId, year, week);

        // Assert the result
        assertTrue(success);
        // Additional assertions if needed
    }

    @Test
    void updateWorkedWeekTask_ShouldUpdateExistingWorkedWeekTask() throws SQLException {
        // Prepare test data
        Worked worked = createDummyWorked();
        insertDummyWorkedEntry(worked);
        // Update the fields if needed

        // Perform the updateWorkedWeekTask operation
        boolean success = workedDAO.updateWorkedWeekTask(worked);

        // Assert the result
        assertTrue(success);
        // Additional assertions if needed
    }

    @Test
    void deleteWorkedWeekTask_ShouldDeleteExistingWorkedWeekTask() throws SQLException {
        // Prepare test data
        Worked worked = createDummyWorked();
        insertDummyWorkedEntry(worked);

        // Perform the deleteWorkedWeekTask operation
        boolean success = workedDAO.deleteWorkedWeekTask(worked.getId());

        // Assert the result
        assertTrue(success);
        // Additional assertions if needed
    }

    @Test
    void isWorkedWeekConfirmed_ShouldReturnCorrectConfirmationStatus() throws SQLException {
        // Prepare test data
        Worked worked = createDummyWorked();
        insertDummyWorkedEntry(worked);

        // Perform the isWorkedWeekConfirmed operation
        boolean confirmed = workedDAO.isWorkedWeekConfirmed(worked.getWorkedWeekId());

        // Assert the result
        assertFalse(confirmed);
    }

    // Helper methods for test data setup

    private void insertDummyWorkedEntries(int count) throws SQLException {
        String userContractId = "3a9cfee3-443f-46e4-a2ae-06c456c071c9";
        String workedWeekId = "15abeea2-ea1d-4b4c-a8d2-4d325e458211";
        String year = "2023";
        String week = "25";

        for (int i = 1; i <= count; i++) {
            Worked worked = new Worked("id" + i, workedWeekId, i, 60, "work" + i);
            workedDAO.addWorkedWeekTask(worked, userContractId, year, week);
        }
    }

    private void insertDummyWorkedEntry(Worked worked) throws SQLException {
        String userContractId = "3a9cfee3-443f-46e4-a2ae-06c456c071c9";
        String workedWeekId = "15abeea2-ea1d-4b4c-a8d2-4d325e458211";
        String year = "2023";
        String week = "25";
        workedDAO.addWorkedWeekTask(worked, userContractId, year, week);
    }

    private Worked createDummyWorked() {
        // Create and return a dummy Worked object
        return new Worked("3de4d8e7-215b-44d5-9d93-7f7af1e3fa3f", "15abeea2-ea1d-4b4c-a8d2-4d325e458211", 1, 60, "dummyWork");
    }
}



