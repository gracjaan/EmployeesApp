package nl.earnit.dao;

import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.User;
import nl.earnit.models.db.WorkedWeek;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WorkedWeekDAO extends GenericDAO<User>{

    private final static String TABLE_NAME = "worked_week";

    public WorkedWeekDAO(Connection con) {
        super(con, TABLE_NAME);
    }
    @Override
    public int count() throws SQLException {
        // Create query
        String query = "SELECT COUNT(*) AS count FROM  \"" + tableName + "\"";
        PreparedStatement counter = this.con.prepareStatement(query);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        res.next();
        return res.getInt("count");
    }

    public void confirmWorkedWeek(String year, String week) {


    }

    public void confirmWorkedWeekById(String workedWeekId) throws SQLException {
        String query = "UPDATE worked_week SET confirmed = true WHERE id = ?";
        PreparedStatement statement = con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, workedWeekId);
        statement.executeUpdate();
    }

    public void removeConfirmWorkedWeekById(String workedWeekId) throws SQLException {
        String query = "UPDATE worked_week SET confirmed = false WHERE id = ?";
        PreparedStatement statement = con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, workedWeekId);
        statement.executeUpdate();
    }

    public void updateWorkedWeekNote(WorkedWeek workedWeek) throws SQLException {
        String query = "UPDATE worked_week SET note = ? WHERE id = ?";
        PreparedStatement statement = con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, workedWeek.getNote());
        PostgresJDBCHelper.setUuid(statement, 2, workedWeek.getId());
    }

}
