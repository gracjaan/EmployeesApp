package nl.earnit.dao;

import nl.earnit.models.db.User;

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
        try {
            statement.setInt(1, Integer.parseInt(workedWeekId));
        } catch(NumberFormatException e) {
            throw new NumberFormatException("invalid workedWeekId");
        }
        statement.executeUpdate();
    }

    public void removeConfirmWorkedWeekById(String workedWeekId) throws SQLException {
        String query = "UPDATE worked_week SET confirmed = false WHERE id = ?";
        PreparedStatement statement = con.prepareStatement(query);
        try {
            statement.setInt(1, Integer.parseInt(workedWeekId));
        } catch(NumberFormatException e) {
            throw new NumberFormatException("invalid workedWeekId");
        }
        statement.executeUpdate();
    }

}
