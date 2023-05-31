package nl.earnit.dao;

import nl.earnit.dto.workedweek.WorkedWeekToApproveDTO;
import nl.earnit.helpers.PostgresJDBCHelper;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.db.WorkedWeek;
import nl.earnit.models.resource.contracts.Contract;
import nl.earnit.models.resource.users.UserResponse;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkedWeekDAO extends GenericDAO<User> {
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

    /**
     * Counts all worked weeks the user has access to. Either via a company user or a contract user.
     *
     * @param userId The id of the user.
     * @throws SQLException If a database error occurs.
     */
    public int countWorkedWeekForUser(String userId) throws SQLException {
        // Create query
        // Checks if user has access to user contract either via company user or directly. Then checks if the worked week belongs to it
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

    public WorkedWeek getWorkedWeekById(String id) throws SQLException {// Create query
        String query =
            "SELECT * FROM \"" + tableName + "\" WHERE \"id\" = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, id);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return User
        return getWorkedWeekFromRow(res);
    }

    public WorkedWeek getWorkedWeekByDate(String userContractId, int year, int week) throws SQLException {
        // Create query
        String query =
            "SELECT * FROM \"" + tableName + "\" WHERE \"contract_id\" = ? AND \"year\" = ? AND \"week\" = ?";
        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setUuid(statement, 1, userContractId);

        statement.setInt(2, year);
        statement.setInt(2, week);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return User
        return getWorkedWeekFromRow(res);
    }

    /**
     * Gets all worked weeks the user has access to. Either via a company user or a contract user.
     *
     * @param userId The id of the user.
     * @throws SQLException If a database error occurs.
     */
    public List<WorkedWeek> getWorkedWeeksForUser(String userId) throws SQLException {
        // Create query
        // Checks if user has access to user contract either via company user or directly. Then checks if the worked week belongs to it
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
        List<WorkedWeek> workedWeeks = new ArrayList<>();

        while (res.next()) {
            workedWeeks.add(getWorkedWeekFromRow(res));
        }

        return workedWeeks;
    }

    /**
     * Gets all worked weeks the user has access to and is ready for approval.
     *
     * @param userId The id of the user.
     * @throws SQLException If a database error occurs.
     */
    public List<WorkedWeek> getWorkedWeeksToApproveForUser(String userId) throws SQLException {
        // Create query
        // Checks if user has access to user contract either via company user or directly. Then checks if the worked week belongs to it
        // TODO: Don't immediately add when confirmed wait until x date has passed.
        String query = """
            SELECT DISTINCT ww.* FROM "%s" ww
                        
                JOIN user_contract uc ON uc.id = ww.contract_id
                JOIN contract c ON c.id = uc.contract_id
                JOIN company cy ON cy.id = c.company_id
                JOIN company_user cu ON cu.company_id = cy.id
                
                WHERE (uc.user_id = ? OR cu.user_id = ?) AND ww.confirmed IS TRUE AND ww.approved IS NULL AND ww.solved IS NULL
            """.formatted(tableName);

        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, userId);
        PostgresJDBCHelper.setUuid(statement, 2, userId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return
        List<WorkedWeek> workedWeeks = new ArrayList<>();

        while (res.next()) {
            workedWeeks.add(getWorkedWeekFromRow(res));
        }

        return workedWeeks;
    }

    /**
     * Gets all worked weeks in a company and is ready for approval.
     *
     * @param companyId The id of the company.
     * @throws SQLException If a database error occurs.
     */
    public List<WorkedWeekToApproveDTO> getWorkedWeeksToApproveForCompany(String companyId, boolean withCompany, boolean withContract, boolean withUserContract, boolean withUser, boolean asc) throws SQLException {
        // Create query
        // Checks if user has access to user contract either via company user or directly. Then checks if the worked week belongs to it
        // TODO: Don't immediately add when confirmed wait until x date has passed.
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
                cy.name as company_name
                
                FROM "%s" ww
            
                JOIN user_contract uc ON uc.id = ww.contract_id
                JOIN "user" u ON u.id = uc.user_id
                JOIN contract c ON c.id = uc.contract_id
                JOIN company cy ON cy.id = c.company_id
                
                WHERE cy.id = ? AND ww.confirmed IS TRUE AND ww.approved IS NULL AND ww.solved IS NULL
                ORDER BY ww.year %2$s, ww.week %2$s
            """.formatted(tableName, asc ? "ASC" : "DESC");

        PreparedStatement statement = this.con.prepareStatement(query);

        PostgresJDBCHelper.setUuid(statement, 1, companyId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // Return
        List<WorkedWeekToApproveDTO> workedWeeks = new ArrayList<>();

        while (res.next()) {
            workedWeeks.add(new WorkedWeekToApproveDTO(res.getString("worked_week_id"), res.getString("worked_week_contract_id"),
                PostgresJDBCHelper.getInteger(res, "worked_week_year"),
                PostgresJDBCHelper.getInteger(res, "worked_week_week"), res.getString("worked_week_note"),
                PostgresJDBCHelper.getBoolean(res, "worked_week_confirmed"),
                PostgresJDBCHelper.getBoolean(res, "worked_week_approved"),
                PostgresJDBCHelper.getBoolean(res, "worked_week_solved"),
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
                    res.getInt("user_contract_hourly_wage"),
                    res.getBoolean("user_contract_active")) : null,
                withContract ? new Contract(res.getString("contract_id"),
                    res.getString("contract_role"),
                    res.getString("contract_description")) : null
            ));
        }

        return workedWeeks;
    }

    public boolean hasCompanyAccessToWorkedWeek(String companyId, String workedWeekId) throws SQLException {
        // Create query
        // Checks if user has access to user contract either via company user or directly. Then checks if the worked week belongs to it
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


    public WorkedWeek updateWorkedWeek(WorkedWeek workedWeek) throws SQLException {
        // Create query
        String query = "UPDATE \"" + tableName + "\" SET note = ?, confirmed = ?, approved = ?, solved = ? WHERE \"id\" = ? RETURNING id";

        PreparedStatement statement = this.con.prepareStatement(query);
        statement.setString(1, workedWeek.getNote());

        PostgresJDBCHelper.setBoolean(statement, 2, workedWeek.getConfirmed());
        PostgresJDBCHelper.setBoolean(statement, 3, workedWeek.getApproved());
        PostgresJDBCHelper.setBoolean(statement, 4, workedWeek.getSolved());
        PostgresJDBCHelper.setUuid(statement, 5, workedWeek.getId());

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return worked week
        return getWorkedWeekById(res.getString("id"));
    }

    public WorkedWeek approveWorkedWeek(String workedWeekId) throws SQLException {
        // Create query
        String query = """
        UPDATE "%s" ww SET approved = ?
            WHERE "id" = ? RETURNING id""".formatted(tableName);

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setBoolean(statement, 1, true);
        PostgresJDBCHelper.setUuid(statement, 2, workedWeekId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return worked week
        return getWorkedWeekById(res.getString("id"));
    }

    public WorkedWeek rejectWorkedWeek(String workedWeekId) throws SQLException {
        // Create query
        String query = """
        UPDATE "%s" ww SET approved = ?
            WHERE "id" = ? RETURNING id""".formatted(tableName);

        PreparedStatement statement = this.con.prepareStatement(query);
        PostgresJDBCHelper.setBoolean(statement, 1, false);
        PostgresJDBCHelper.setUuid(statement, 2, workedWeekId);

        // Execute query
        ResultSet res = statement.executeQuery();

        // None found
        if(!res.next()) return null;

        // Return worked week
        return getWorkedWeekById(res.getString("id"));
    }

    private WorkedWeek getWorkedWeekFromRow(ResultSet res) throws SQLException {
        return new WorkedWeek(res.getString("id"), res.getString("contract_id"),
            PostgresJDBCHelper.getInteger(res, "year"),
            PostgresJDBCHelper.getInteger(res, "week"), res.getString("note"),
            PostgresJDBCHelper.getBoolean(res, "confirmed"),
            PostgresJDBCHelper.getBoolean(res, "approved"),
            PostgresJDBCHelper.getBoolean(res, "solved"));
    }
}
