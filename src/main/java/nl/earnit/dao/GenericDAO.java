package nl.earnit.dao;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The type Generic dao.
 *
 * @param <T> the type parameter
 */
public abstract class GenericDAO<T> {
    /**
     * Get row count in table.
     *
     * @return Row count
     * @throws SQLException If a database error occurs.
     */
    public abstract int count() throws SQLException;

    /**
     * The Table name.
     */
    protected final String tableName;
    /**
     * The Con.
     */
    protected Connection con;

    /**
     * Instantiates a new Generic dao.
     *
     * @param con       the con
     * @param tableName the table name
     */
    protected GenericDAO(Connection con, String tableName) {
        this.tableName = tableName;
        this.con = con;
    }
}
