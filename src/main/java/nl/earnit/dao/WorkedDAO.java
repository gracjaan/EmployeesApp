package nl.earnit.dao;

import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.User;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.db.Worked;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkedDAO extends GenericDAO<User> {

    private final static String TABLE_NAME = "worked";

    public WorkedDAO(Connection con) {
        super(con, TABLE_NAME);
    }

    // counts how many rows worked has
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

    public List<Worked> getWorkedHours(String userContractId) throws SQLException {
        // Create query
        String query = "SELECT id, worked_week_id, day, minutes, work  FROM  \"" + tableName + "\" t JOIN worked_week ww ON ww.id=t.worked_week_id WHERE ww.contract_id=?";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userContractId);
        // Execute query
        ResultSet res = counter.executeQuery();
        // Return count
        res.next();
        List<Worked> list = new ArrayList<>();
        while (res.next()) {
            Worked w = new Worked(res.getString("id"), res.getString("worked_week_id"), res.getInt("day"), res.getInt("minutes"), res.getString("work"));
            list.add(w);
        }
        return list;
    }

    public List<Worked> getWorkedWeek(String userContractId, String year, String week) throws SQLException {
        String query = "SELECT id, worked_week_id, day, minutes, work  FROM  \"" + tableName + "\" t JOIN worked_week ww ON ww.id=t.worked_week_id WHERE ww.contract_id=? AND ww.year=? AND ww.week=?";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userContractId);
        counter.setString(2, year);
        counter.setString(3, week);
        // Execute query
        ResultSet res = counter.executeQuery();
        // Return count
        res.next();
        List<Worked> list = new ArrayList<>();
        while (res.next()) {
            Worked w = new Worked(res.getString("id"), res.getString("worked_week_id"), res.getInt("day"), res.getInt("minutes"), res.getString("work"));
            list.add(w);
        }
        return list;
    }

    public List<UserContract> getUserContracts(String userId) throws SQLException {
        String query = "SELECT * AS count FROM  \"" + tableName + "\" WHERE \"user_id\" = ?";
        PreparedStatement counter = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(counter, 1, userId);
        // Execute query
        ResultSet res = counter.executeQuery();
        // Return count
        res.next();
        List<UserContract> contractList = new ArrayList<>();
        while (res.next()) {
            UserContract userContract = new UserContract(res.getString("id"), res.getString("contract_id"), res.getString("user_id"), res.getInt("hourly_wage"), res.getBoolean("active"));
            contractList.add(userContract);
        }
        return contractList;
    }

    public boolean addWorkedWeekTask(Worked worked, String userContractId, String year, String week) throws SQLException {
            WorkedWeekDAO wwDao = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            WorkedWeekDTO ww = wwDao.getWorkedWeekByDate(userContractId, Integer.parseInt(year), Integer.parseInt(week), false, false, false, false, false, "hours.day:asc");
            if (ww == null) {
                wwDao.addWorkedWeek(userContractId, year, week);
                return addWorkedWeekTask(worked, userContractId, year, week);
            }

            worked.setWorkedWeekId(ww.getId());

            if (this.isWorkedWeekConfirmed(worked.getWorkedWeekId())) {
                return false;
            }

            String query = "INSERT INTO \"" + tableName + "\" (worked_week_id, day, minutes, work) " +
                    "VALUES (?, ?, ?, ?) RETURNING id";
            PreparedStatement counter = this.con.prepareStatement(query);
            PostgresJDBCHelper.setUuid(counter, 1, ww.getId());
            counter.setInt(2, worked.getDay());
            counter.setInt(3, worked.getMinutes());
            counter.setString(4, worked.getWork());
            // Execute query
            ResultSet res = counter.executeQuery();
            // Return count
            res.next();
            return true;
    }


    public boolean updateWorkedWeekTask(Worked worked) throws SQLException {
        if (this.isWorkedWeekConfirmed(worked.getWorkedWeekId())) {
            return false;
        }

        String query = "UPDATE \"" + tableName + "\" SET day = ?, minutes = ?, work = ? WHERE id = ?;";
        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setInt(1, worked.getDay());
        statement.setInt(2, worked.getMinutes());
        statement.setString(3, worked.getWork());
        PostgresJDBCHelper.setUuid(statement, 4, worked.getId());
        ResultSet res = statement.executeQuery();
        res.next();
        return true;
    }

    public boolean deleteWorkedWeekTask(Worked worked) throws SQLException {
        if (this.isWorkedWeekConfirmed(worked.getWorkedWeekId())) {
            return false;
        }

        String query = "DELETE FROM \"" + tableName + "\" WHERE id = ?;";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, worked.getId());
        statement.executeUpdate();
        return true;
    }

    public boolean isWorkedWeekConfirmed(String workedWeekId) throws SQLException {
        String query = "SELECT confirmed FROM worked_week WHERE id = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, workedWeekId);
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) return false;
        return resultSet.getBoolean("confirmed");
    }


}
