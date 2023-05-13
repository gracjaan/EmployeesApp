package nl.earnit.dao;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class GenericDAO<T> {
    /**
     * Get row count in table.
     * @return Row count
     * @throws SQLException If a database error occurs.
     */
    public abstract int count() throws SQLException;

    protected final String tableName;
    protected Connection con;

    protected GenericDAO(Connection con, String tableName) {
        this.tableName = tableName;
        this.con = con;
    }
}
