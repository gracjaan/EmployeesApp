package nl.earnit.test.dao;

import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import nl.earnit.test.TestDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class WorkedWeekDAOTest {
    @RegisterExtension
    public SingleInstancePostgresExtension pg = EmbeddedPostgresExtension.singleInstance();

    private TestDB db;

    public void setupCompanyUserDAOTest() throws Exception {
        db = new TestDB(pg);
    }

    @Test
    public void testConfirmWorkedWeek() throws Exception {
        // test confirm and unconfirm
    }

    @Test
    public void testAddWorkedWeeknote() throws Exception {

    }

    @Test
    public void testGetWorkedWeeksForUser() throws Exception {

    }

    @Test
    public void testGetWorkedWeeksForCompanies() throws Exception {

    }








}
