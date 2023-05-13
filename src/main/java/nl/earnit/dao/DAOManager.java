package nl.earnit.dao;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DAOManager {

    /**
     * Gets the instance of the DOAManager per thread.
     *
     * @return DOAManager for the thread.
     */
    public static DAOManager getInstance() {
        return DAOManagerSingleton.INSTANCE.get();
    }

    /**
     * Opens connection to database.
     *
     * @throws SQLException If a database error occurs.
     */
    public void open() throws SQLException {
        // Only open if not connected
        if (this.con != null && !this.con.isClosed()) {
            return;
        }

        this.con = src.getConnection();
    }

    /**
     * Closes connection to database.
     *
     * @throws SQLException If a database error occurs.
     */
    public void close() throws SQLException {
        // Only close if connected at all
        if (this.con == null || this.con.isClosed()) {
            return;
        }

        this.con.close();
    }

    public enum DAO {
        USER
    }

    public GenericDAO<?> getDAO(DAO dao) throws SQLException {
        this.open();

        if (dao.equals(DAO.USER)) {
            return new UserDAO(this.con);
        }

        throw new SQLException("Trying to link to an nonexistent dao.");

    }

    private final DataSource src;
    private Connection con;

    /**
     * Create a data source to the database server.
     */
    private DAOManager() {
        PGSimpleDataSource ds = new PGSimpleDataSource();

        // Get connection info from system environment variables
        ds.setDatabaseName(System.getenv("DB_NAME"));
        ds.setUser(System.getenv("DB_USER"));
        ds.setPassword(System.getenv("DB_PASS"));
        ds.setCurrentSchema(System.getenv("DB_SCHEMA"));

        this.src = ds;
    }

    /**
     * Singleton for the DOAManager per thread.
     */
    private static class DAOManagerSingleton {

        public static final ThreadLocal<DAOManager> INSTANCE;

        static {
            // Create thread instance

            ThreadLocal<DAOManager> dm;

            try {
                dm = ThreadLocal.withInitial(() -> {
                    try {
                        return new DAOManager();
                    } catch (Exception e) {
                        // Can't really do anything about a database error here
                        return null;
                    }
                });
            } catch (Exception e) {
                dm = null;
            }

            INSTANCE = dm;
        }
    }
}