package nl.earnit.helpers;

import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The type Postgres jdbc helper.
 */
public class PostgresJDBCHelper {
    /**
     * Gets integer.
     *
     * @param set         the set
     * @param columnLabel the column label
     * @return the integer
     * @throws SQLException the sql exception
     */
    public static Integer getInteger(ResultSet set, String columnLabel) throws SQLException {
        int i = set.getInt(columnLabel);
        return set.wasNull() ? null : i;
    }

    /**
     * Gets boolean.
     *
     * @param set         the set
     * @param columnLabel the column label
     * @return the boolean
     * @throws SQLException the sql exception
     */
    public static Boolean getBoolean(ResultSet set, String columnLabel) throws SQLException {
        boolean i = set.getBoolean(columnLabel);
        return set.wasNull() ? null : i;
    }

    /**
     * Sets uuid.
     *
     * @param statement the statement
     * @param index     the index
     * @param value     the value
     * @throws SQLException the sql exception
     */
    public static void setUuid(PreparedStatement statement, int index, String value) throws SQLException {
        PGobject toInsert = new PGobject();
        toInsert.setType("uuid");
        toInsert.setValue(value);
        statement.setObject(index, toInsert);
    }

    /**
     * Sets boolean.
     *
     * @param statement the statement
     * @param index     the index
     * @param value     the value
     * @throws SQLException the sql exception
     */
    public static void setBoolean(PreparedStatement statement, int index, Boolean value) throws SQLException {
        PGobject toInsert = new PGobject();
        toInsert.setType("bool");
        toInsert.setValue(value == null ? null : value ? "TRUE" : "FALSE");
        statement.setObject(index, toInsert);
    }
}
