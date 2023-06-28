package nl.earnit.dao;

import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.User;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.db.Worked;
import nl.earnit.models.resource.companies.CreateSuggestion;

import java.sql.*;
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

    //TODO: Multiple employees can have the same contract. So if we retrieve the hours of a contract, those hours belong to multiple employees right
    //TODO: Don't we need to make it so that we only get the hours worked under a contract of one employee
    /**
     * gets a list of all the worked weeks with hours that were worked under a specific contract
     * @param userContractId
     * @return A list of all the worked weeks
     * @throws SQLException
     */
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

    /**
     * Gets all the contracts that a user has
     * @param userId the id of the user
     * @return the list of contracts the user has
     * @throws SQLException
     */
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

    /**
     * Adds a particular task that was done in a worked week, only when the worked week was not yet confirmed
     *
     * @param worked the worked task that was executed
     * @param userContractId the contract it was executed under
     * @param year the year of execution
     * @param week the week of execution
     * @return whether the work was added to the week ? true : false
     * @throws SQLException
     */
    public boolean addWorkedWeekTask(Worked worked, String userContractId, String year, String week) throws SQLException {
            WorkedWeekDAO wwDao = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            List<WorkedWeekDTO> workedWeeks = wwDao.getWorkedWeeksForUser(null, userContractId, Integer.parseInt(year), Integer.parseInt(week), false, false, false, false, false, false, "hours.day:asc");

            WorkedWeekDTO ww = null;
            if (!workedWeeks.isEmpty()) ww = workedWeeks.get(0);

            if (ww == null) {
                wwDao.addWorkedWeek(userContractId, year, week);
                return addWorkedWeekTask(worked, userContractId, year, week);
            }

            worked.setWorkedWeekId(ww.getId());

            if (new WorkedWeekDAO(this.con).isWorkedWeekConfirmed(worked.getWorkedWeekId())) {
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

    /**
     * Updates the worked week in case it was flagged and changed
     * @param worked the new amount of worked hours of a task
     * @return whether the worked object was updated in the table ? true : false
     * @throws SQLException
     */
    public boolean updateWorkedWeekTask(Worked worked) throws SQLException {
        if (new WorkedWeekDAO(this.con).isWorkedWeekConfirmed(worked.getWorkedWeekId())) {
            return false;
        }

        String query = "UPDATE \"" + tableName + "\" SET day = ?, minutes = ?, work = ? WHERE id = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setInt(1, worked.getDay());
        statement.setInt(2, worked.getMinutes());
        statement.setString(3, worked.getWork());
        PostgresJDBCHelper.setUuid(statement, 4, worked.getId());
        statement.executeUpdate();

        return true;
    }

    public boolean deleteWorkedWeekTask(String workedId) throws SQLException {
        if (new WorkedWeekDAO(this.con).isWorkedWeekConfirmed(workedId)) {
            return false;
        }
        String query = "DELETE FROM \"" + tableName + "\" WHERE id = ?;";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, workedId);
        statement.executeUpdate();
        return true;
    }

    /**
     * Checks whether a worked week is already confirmed
     * @param workedId the id of the worked week
     * @return the week is confirmed ? true : false
     * @throws SQLException
     */
    public boolean isWorkedWeekConfirmedOfWorked(String workedId) throws SQLException {
        String query = "SELECT ww.status FROM worked_week ww JOIN worked w ON w.worked_week_id = ww.id WHERE w.id = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, workedId);
        ResultSet resultSet = statement.executeQuery();
        if (!resultSet.next()) return false;
        return !resultSet.getString("status").equals("NOT_CONFIRMED");
    }

    /**
     * checks whether the company is allowed to see the worked week
     * @param companyId the id of the company
     * @param workedId the id of the worked week
     * @return
     * @throws SQLException
     */
    public boolean hasCompanyAccessToWorked(String companyId, String workedId) throws SQLException {
        String query = """
            SELECT COUNT(DISTINCT ww.id) as count FROM "%s" w
                
                JOIN worked_week ww ON ww.id = w.worked_week_id
                JOIN user_contract uc ON uc.id = ww.contract_id
                JOIN contract c ON c.id = uc.contract_id
                JOIN company cy ON cy.id = c.company_id
                
                WHERE cy.id = ? AND w.id = ?
            """.formatted(tableName);

        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, companyId);
        PostgresJDBCHelper.setUuid(statement, 2, workedId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return
        res.next();
        return res.getInt("count") > 0;
    }

    /**
     * Sets a suggestion for the worked hours so that it can be considered by staff
     *
     * @param workedId the id of the worked object that the suggestion is added to
     * @param suggestion the suggestion of new hours
     * @throws SQLException
     */
    public void setSuggestion(String workedId, CreateSuggestion suggestion) throws SQLException {
        String query = "UPDATE \"" + tableName + "\" SET suggestion = ? WHERE id = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        if (suggestion.getSuggestion() == null) {
            statement.setNull(1, Types.INTEGER);
        } else {
            statement.setInt(1, suggestion.getSuggestion());
        }
        PostgresJDBCHelper.setUuid(statement, 2, workedId);
        statement.executeUpdate();
    }

    /**
     * accept the suggestion of hours that was made by the company
     * @param workedWeekId the id of the week when the suggestion was made for
     * @return whether the suggestion was accepted and hours were updated ? true : false
     * @throws SQLException
     */
    public boolean acceptCompanySuggestion(String workedWeekId) throws SQLException {
        // Create query
        String query = """
            UPDATE "%s" SET minutes = suggestion where worked_week_id = ? and suggestion IS NOT NULL RETURNING id""".formatted(tableName);
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, workedWeekId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return success
        return res.next();
    }

    /**
     * accepts the hours that were filled in by the student and ignores the suggestion that was made by the company
     *
     * @param workedWeekId the week were of the suggestion is accepted
     * @return whether the suggestion was accepted and hours were updated ? true : false
     * @throws SQLException
     */
    public boolean acceptStudentSuggestion(String workedWeekId) throws SQLException {
        // Create query
        String query = """
            UPDATE "%s" SET suggestion = null where worked_week_id = ? RETURNING id""".formatted(tableName);
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, workedWeekId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return success
        return res.next();
    }
}
