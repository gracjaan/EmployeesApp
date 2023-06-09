package nl.earnit.dao;

import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.exceptions.InvalidOrderByException;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.*;
import nl.earnit.models.resource.contracts.Contract;
import nl.earnit.models.resource.users.UserResponse;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkedWeekDAO extends GenericDAO<User> {
    private final OrderBy orderBy = new OrderBy(new HashMap<>() {{
        put("worked_week.id", "ww.id");
        put("worked_week.year", "ww.year");
        put("worked_week.week", "ww.week");
        put("worked_week.confirmed", "ww.confirmed");
        put("worked_week.approved", "ww.approved");
        put("worked_week.solved", "ww.solved");

        put("user_contract.contract_id", "uc.contract_id");
        put("user_contract.user_id", "uc.user_id");
        put("user_contract.hourly_wage", "uc.hourly_wage");
        put("user_contract.active", "uc.active");

        put("user.id", "u.id");
        put("user.first_name", "u.first_name");
        put("user.last_name", "u.last_name");
        put("user.last_name_prefix", "u.last_name_prefix");
        put("user.email", "u.email");

        put("contract.id", "c.id");
        put("contract.company_id", "c.company_id");
        put("contract.role", "c.role");

        put("company.id", "cy.id");
        put("company.name", "cy.name");
    }});

    private final OrderBy orderByHours = new OrderBy(new HashMap<>() {{
        put("hours.day", "w.day");
        put("hours.minutes", "w.minutes");
    }});

    private final static String TABLE_NAME = "worked_week";

    public WorkedWeekDAO(Connection con) {
        super(con, TABLE_NAME);
    }

    @Override
    public int count() throws SQLException {
        // Create query
        String query = """
             SELECT COUNT(DISTINCT ww.id) AS count FROM "%s" ww
            """.formatted(tableName);

        PreparedStatement counter = this.con.prepareStatement(query);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        res.next();
        return res.getInt("count");
    }

    public void confirmWorkedWeek(String year, String week) throws SQLException {
        String query = "UPDATE worked_week SET confirmed = true WHERE year=? AND week=?";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, year);
        statement.setString(2, week);
        statement.executeUpdate();
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
        statement.setString(1, workedWeek.getNote());
        PostgresJDBCHelper.setUuid(statement, 2, workedWeek.getId());
    }

    /**
     * Counts all worked weeks the user has access to. Either via a company user or a contract user.
     *
     * @param userId The id of the user.
     * @throws SQLException If a database error occurs.
     */
    public int countWorkedWeekForUser(String userId) throws SQLException {
        String query = """
            SELECT COUNT(DISTINCT ww.id) AS count FROM "%s" ww
                        
                JOIN user_contract uc ON uc.id = ww.contract_id
                JOIN contract c ON c.id = uc.contract_id
                JOIN company cy ON cy.id = c.company_id
                JOIN company_user cu ON cu.company_id = cy.id
                
                WHERE uc.user_id = ? OR cu.user_id = ?
            """.formatted(tableName);

        PreparedStatement counter = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(counter, 1, userId);
        PostgresJDBCHelper.setUuid(counter, 2, userId);

        // Execute query
        ResultSet res = counter.executeQuery();

        // Return count
        res.next();
        return res.getInt("count");
    }

    public WorkedWeekDTO getWorkedWeekById(String id) throws SQLException {
        return getWorkedWeekById(id, false, false, false, false, false, false, "hours.day:asc");
    }

    public WorkedWeekDTO getWorkedWeekByDate(int year, int week, boolean withCompany,
                                             boolean withContract, boolean withUserContract,
                                             boolean withUser, boolean withHours, boolean withTotalHours, String order)
        throws SQLException {

        String query = """
            SELECT DISTINCT ww.id as worked_week_id,
                ww.contract_id as worked_week_contract_id,
                ww.year as worked_week_year,
                ww.week as worked_week_week,
                ww.note as worked_week_note,
                ww.confirmed as worked_week_confirmed,
                ww.approved as worked_week_approved,
                ww.solved as worked_week_solved,
                
                uc.id as user_contract_id,
                uc.contract_id as user_contract_contract_id,
                uc.user_id as user_contract_user_id,
                uc.hourly_wage as user_contract_hourly_wage,
                uc.active as user_contract_active,
                
                u.id as user_id,
                u.first_name as user_first_name,
                u.last_name as user_last_name,
                u.last_name_prefix as user_last_name_prefix,
                u.email as user_email,
                u.type as user_type,
                
                c.id as contract_id,
                c.company_id as contract_company_id,
                c.role as contract_role,
                c.description as contract_description,
                
                cy.id as company_id,
                cy.name as company_name,
                
                w.hours,
                w.minutes
                
                FROM "%s" ww
                        
                JOIN user_contract uc ON uc.id = ww.contract_id
                JOIN "user" u ON u.id = uc.user_id
                JOIN contract c ON c.id = uc.contract_id
                JOIN company cy ON cy.id = c.company_id
                
                LEFT JOIN (select w.worked_week_id, array_agg(w.*%2$s) as hours, sum(w.minutes) as minutes FROM worked w GROUP BY w.worked_week_id) w ON w.worked_week_id = ww.id
             
                WHERE ww.year = ? AND ww.week = ?
                LIMIT 1
            """.formatted(tableName, orderByHours.getSQLOrderBy(order, true));

        PreparedStatement statement = this.con.prepareStatement(query);

        statement.setInt(1, year);
        statement.setInt(2, week);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return

        if (!res.next()) {
            return null;
        }

        return getWorkedWeekFromRow(res, "worked_week_", withCompany, withContract, withUserContract, withUser, withHours, withTotalHours);
    }

    /**
     * Gets all worked weeks the user has access to. Either via a company user or a contract user.
     *
     * @param userId The id of the user.
     * @throws SQLException If a database error occurs.
     */
    public List<WorkedWeekDTO> getWorkedWeeksForUser(String userId) throws SQLException {
        String query = """
            SELECT DISTINCT ww.* FROM "%s" ww
                        
                JOIN user_contract uc ON uc.id = ww.contract_id
                JOIN contract c ON c.id = uc.contract_id
                JOIN company cy ON cy.id = c.company_id
                JOIN company_user cu ON cu.company_id = cy.id
                
                WHERE uc.user_id = ? OR cu.user_id = ?
            """.formatted(tableName);

        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, userId);
        PostgresJDBCHelper.setUuid(statement, 2, userId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return
        List<WorkedWeekDTO> workedWeeks = new ArrayList<>();

        while (res.next()) {
            workedWeeks.add(getWorkedWeekFromRow(res));
        }

        return workedWeeks;
    }

    /**
     * Gets all worked weeks the company has access to per week.
     *
     * @param companyId The id of the company.
     * @throws SQLException If a database error occurs.
     */
    public List<WorkedWeekDTO> getWorkedWeeksForCompany(String companyId, int year, int week, boolean withCompany,
                                                        boolean withContract, boolean withUserContract,
                                                        boolean withUser, boolean withHours, boolean withTotalHours, String order) throws SQLException {
        String query = """
            SELECT DISTINCT ww.id as worked_week_id,
                ww.contract_id as worked_week_contract_id,
                ww.year as worked_week_year,
                ww.week as worked_week_week,
                ww.note as worked_week_note,
                ww.confirmed as worked_week_confirmed,
                ww.approved as worked_week_approved,
                ww.solved as worked_week_solved,
                
                uc.id as user_contract_id,
                uc.contract_id as user_contract_contract_id,
                uc.user_id as user_contract_user_id,
                uc.hourly_wage as user_contract_hourly_wage,
                uc.active as user_contract_active,
                
                u.id as user_id,
                u.first_name as user_first_name,
                u.last_name as user_last_name,
                u.last_name_prefix as user_last_name_prefix,
                u.email as user_email,
                u.type as user_type,
                
                c.id as contract_id,
                c.company_id as contract_company_id,
                c.role as contract_role,
                c.description as contract_description,
                
                cy.id as company_id,
                cy.name as company_name,
                
                w.hours,
                w.minutes
                
                FROM "%s" ww
                        
                JOIN user_contract uc ON uc.id = ww.contract_id
                JOIN "user" u ON u.id = uc.user_id
                JOIN contract c ON c.id = uc.contract_id
                JOIN company cy ON cy.id = c.company_id
                
                LEFT JOIN (select w.worked_week_id, array_agg(w.*%2$s) as hours, sum(w.minutes) as minutes FROM worked w GROUP BY w.worked_week_id) w ON w.worked_week_id = ww.id
                                
                WHERE ww.year = ? AND ww.week = ? AND cy.id = ?
                %3$s
            """.formatted(tableName, orderByHours.getSQLOrderBy(order, true), orderBy.getSQLOrderBy(order, true));

        PreparedStatement statement = this.con.prepareStatement(query);

        statement.setInt(1, year);
        statement.setInt(2, week);
        PostgresJDBCHelper.setUuid(statement, 3, companyId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return
        List<WorkedWeekDTO> workedWeeks = new ArrayList<>();

        while (res.next()) {
            workedWeeks.add(getWorkedWeekFromRow(res, "worked_week_", withCompany, withContract, withUserContract, withUser, withHours, withTotalHours));
        }

        return workedWeeks;
    }

    /**
     * Get the worked week.
     *
     * @param workedWeekId The id of the worked week.
     * @throws SQLException If a database error occurs.
     */
    public WorkedWeekDTO getWorkedWeekById(String workedWeekId, boolean withCompany,
                                                 boolean withContract, boolean withUserContract,
                                                 boolean withUser, boolean withHours, boolean withTotalHours, String order) throws SQLException {
        String query = """
            SELECT DISTINCT ww.id as worked_week_id,
                ww.contract_id as worked_week_contract_id,
                ww.year as worked_week_year,
                ww.week as worked_week_week,
                ww.note as worked_week_note,
                ww.confirmed as worked_week_confirmed,
                ww.approved as worked_week_approved,
                ww.solved as worked_week_solved,
                
                uc.id as user_contract_id,
                uc.contract_id as user_contract_contract_id,
                uc.user_id as user_contract_user_id,
                uc.hourly_wage as user_contract_hourly_wage,
                uc.active as user_contract_active,
                
                u.id as user_id,
                u.first_name as user_first_name,
                u.last_name as user_last_name,
                u.last_name_prefix as user_last_name_prefix,
                u.email as user_email,
                u.type as user_type,
                
                c.id as contract_id,
                c.company_id as contract_company_id,
                c.role as contract_role,
                c.description as contract_description,
                
                cy.id as company_id,
                cy.name as company_name,
                
                w.hours,
                w.minutes
                
                FROM "%s" ww
                        
                JOIN user_contract uc ON uc.id = ww.contract_id
                JOIN "user" u ON u.id = uc.user_id
                JOIN contract c ON c.id = uc.contract_id
                JOIN company cy ON cy.id = c.company_id
                
                LEFT JOIN (select w.worked_week_id, array_agg(w.*%2$s) as hours, sum(w.minutes) as minutes FROM worked w GROUP BY w.worked_week_id) w ON w.worked_week_id = ww.id
                
                WHERE ww.id = ?
                LIMIT 1
            """.formatted(tableName, orderByHours.getSQLOrderBy(order, true));

        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, workedWeekId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return

        if (!res.next()) {
            return null;
        }
        return getWorkedWeekFromRow(res, "worked_week_", withCompany, withContract, withUserContract, withUser, withHours, withTotalHours);
    }

    /**
     * Gets all worked weeks in a company and is ready for approval.
     *
     * @param companyId The id of the company.
     * @throws SQLException If a database error occurs.
     */
    public List<WorkedWeekDTO> getWorkedWeeksToApproveForCompany(String companyId,
                                                                 boolean withCompany,
                                                                 boolean withContract,
                                                                 boolean withUserContract,
                                                                 boolean withUser, boolean withHours, boolean withTotalHours, String order)
        throws SQLException, InvalidOrderByException {

        int currentWeek = LocalDate.now().get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        int currentYear = LocalDate.now().get(IsoFields.WEEK_BASED_YEAR);

        String query = """
            SELECT DISTINCT ww.id as worked_week_id,
                ww.contract_id as worked_week_contract_id,
                ww.year as worked_week_year,
                ww.week as worked_week_week,
                ww.note as worked_week_note,
                ww.confirmed as worked_week_confirmed,
                ww.approved as worked_week_approved,
                ww.solved as worked_week_solved,
                
                uc.id as user_contract_id,
                uc.contract_id as user_contract_contract_id,
                uc.user_id as user_contract_user_id,
                uc.hourly_wage as user_contract_hourly_wage,
                uc.active as user_contract_active,
                
                u.id as user_id,
                u.first_name as user_first_name,
                u.last_name as user_last_name,
                u.last_name_prefix as user_last_name_prefix,
                u.email as user_email,
                u.type as user_type,
                
                c.id as contract_id,
                c.company_id as contract_company_id,
                c.role as contract_role,
                c.description as contract_description,
                
                cy.id as company_id,
                cy.name as company_name,
                
                w.hours,
                w.minutes
                
                FROM "%s" ww
                        
                JOIN user_contract uc ON uc.id = ww.contract_id
                JOIN "user" u ON u.id = uc.user_id
                JOIN contract c ON c.id = uc.contract_id
                JOIN company cy ON cy.id = c.company_id
                
                LEFT JOIN (select w.worked_week_id, array_agg(w.*%2$s) as hours, sum(w.minutes) as minutes FROM worked w GROUP BY w.worked_week_id) w ON w.worked_week_id = ww.id
                                
                WHERE cy.id = ? AND ww.confirmed IS TRUE AND ww.approved IS NULL AND ww.solved IS NULL AND (ww.year < ? OR (ww.year = ? AND ww.week < ?))
                %3$s
            """.formatted(tableName, orderByHours.getSQLOrderBy(order, true), orderBy.getSQLOrderBy(order, true));

        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, companyId);
        statement.setInt(2, currentYear);
        statement.setInt(3, currentYear);
        statement.setInt(4, currentWeek);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return
        List<WorkedWeekDTO> workedWeeks = new ArrayList<>();

        while (res.next()) {
            workedWeeks.add(getWorkedWeekFromRow(res, "worked_week_", withCompany, withContract, withUserContract, withUser, withHours, withTotalHours));
        }

        return workedWeeks;
    }

    public boolean hasCompanyAccessToWorkedWeek(String companyId, String workedWeekId)
        throws SQLException {
        String query = """
            SELECT COUNT(DISTINCT ww.id) as count FROM "%s" ww
                        
                JOIN user_contract uc ON uc.id = ww.contract_id
                JOIN contract c ON c.id = uc.contract_id
                JOIN company cy ON cy.id = c.company_id
                
                WHERE cy.id = ? AND ww.id = ?
            """.formatted(tableName);

        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, companyId);
        PostgresJDBCHelper.setUuid(statement, 2, workedWeekId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return
        res.next();
        return res.getInt("count") > 0;
    }


    public WorkedWeekDTO updateWorkedWeek(WorkedWeek workedWeek) throws SQLException {
        // Create query
        String query = "UPDATE \"" + tableName +
            "\" SET note = ?, confirmed = ?, approved = ?, solved = ? WHERE \"id\" = ? RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, workedWeek.getNote());

        PostgresJDBCHelper.setBoolean(statement, 2, workedWeek.getConfirmed());
        PostgresJDBCHelper.setBoolean(statement, 3, workedWeek.getApproved());
        PostgresJDBCHelper.setBoolean(statement, 4, workedWeek.getSolved());
        PostgresJDBCHelper.setUuid(statement, 5, workedWeek.getId());

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if (!res.next()) {
            return null;
        }

        // Return worked week
        return getWorkedWeekById(res.getString("id"));
    }

    public WorkedWeekDTO setApproveWorkedWeek(String workedWeekId, Boolean status, boolean withCompany,
                                              boolean withContract, boolean withUserContract,
                                              boolean withUser, boolean withHours, boolean withTotalHours, String order) throws SQLException {
        // Create query
        String query = """
            UPDATE "%s" ww SET approved = ?
                WHERE "id" = ? RETURNING id""".formatted(tableName);

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setBoolean(statement, 1, status);
        PostgresJDBCHelper.setUuid(statement, 2, workedWeekId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if (!res.next()) {
            return null;
        }

        // Return worked week
        return getWorkedWeekById(res.getString("id"), withCompany, withContract, withUserContract, withUser, withHours, withTotalHours, order);
    }

    private WorkedWeekDTO getWorkedWeekFromRow(ResultSet res) throws SQLException {
        return getWorkedWeekFromRow(res, "", false, false, false, false, false, false);
    }

    private WorkedWeekDTO getWorkedWeekFromRow(ResultSet res, String prefix, boolean withCompany, boolean withContract, boolean withUserContract, boolean withUser, boolean withHours, boolean withTotalHours) throws SQLException {

        List<Worked> hours = new ArrayList<>();

        if (withHours) {
            ResultSet hoursSet = res.getArray("hours").getResultSet();

            while (hoursSet.next()) {
                String data = ((PGobject) hoursSet.getObject("VALUE")).getValue();
                if (data == null) continue;

                data = data.substring(1, data.length() - 1);
                String[] dataStrings = data.split(",");

                String note = dataStrings[4];
                note = note.substring(1, note.length() - 1);

                hours.add(new Worked(dataStrings[0], dataStrings[1], Integer.parseInt(dataStrings[2]), Integer.parseInt(dataStrings[3]), note));
            }
        }

        return new WorkedWeekDTO(res.getString(prefix + "id"),
            res.getString(prefix + "contract_id"),
            PostgresJDBCHelper.getInteger(res, prefix + "year"),
            PostgresJDBCHelper.getInteger(res, prefix + "week"),
            res.getString(prefix + "note"),
            PostgresJDBCHelper.getBoolean(res, prefix + "confirmed"),
            PostgresJDBCHelper.getBoolean(res, prefix + "approved"),
            PostgresJDBCHelper.getBoolean(res, prefix + "solved"),
            withUser ? new UserResponse(res.getString("user_id"),
                res.getString("user_email"),
                res.getString("user_first_name"),
                res.getString("user_last_name"),
                res.getString("user_last_name_prefix"),
                res.getString("user_type")) : null,
            withCompany ? new Company(res.getString("company_id"),
                res.getString("company_name")) : null,
            withUserContract ? new UserContract(res.getString("user_contract_id"),
                res.getString("user_contract_contract_id"),
                res.getString("user_contract_user_id"),
                res.getInt("user_contract_hourly_wage"),
                res.getBoolean("user_contract_active")) : null,
            withContract ? new Contract(res.getString("contract_id"),
                res.getString("contract_role"),
                res.getString("contract_description")) : null,
            withHours ? hours : null,
            withTotalHours ? res.getInt("minutes") : null
        );
    }
}
