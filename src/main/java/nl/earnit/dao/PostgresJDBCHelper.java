package nl.earnit.dao;

import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresJDBCHelper {
    public static Integer getInteger(ResultSet set, String columnLabel) throws SQLException {
        int i = set.getInt(columnLabel);
        return set.wasNull() ? null : i;
    }

    public static Boolean getBoolean(ResultSet set, String columnLabel) throws SQLException {
        boolean i = set.getBoolean(columnLabel);
        return set.wasNull() ? null : i;
    }

    public static void setUuid(PreparedStatement statement, int index, String value) throws SQLException {
        PGobject toInsert = new PGobject();
        toInsert.setType("uuid");
        toInsert.setValue(value);
        statement.setObject(index, toInsert);
    }

    public static void setBoolean(PreparedStatement statement, int index, Boolean value) throws SQLException {
        PGobject toInsert = new PGobject();
        toInsert.setType("boolean");
        toInsert.setValue(value == null ? "NULL" : value ? "TRUE" : "FALSE");
        statement.setObject(index, toInsert);
    }
}
