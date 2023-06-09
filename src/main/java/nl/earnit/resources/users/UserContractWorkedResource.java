package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.WorkedDAO;
import nl.earnit.dao.WorkedWeekDAO;
import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.models.db.Worked;
import nl.earnit.models.db.WorkedWeek;

import java.sql.SQLException;

public class UserContractWorkedResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String userId;
    private final String userContractId;
    private final String year;
    private final String week;

    // If weekId is null use year, week and user contract id
    private final String weekId;

    public UserContractWorkedResource(UriInfo uriInfo, Request request, String userId, String userContractId, String year, String week) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
        this.userContractId = userContractId;
        this.year = year;
        this.week = week;
        this.weekId = null;
    }

    public UserContractWorkedResource(UriInfo uriInfo, Request request, String userId, String userContractId, String weekId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
        this.userContractId = userContractId;
        this.year = null;
        this.week = null;
        this.weekId = weekId;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorkedWeek(@QueryParam("company") @DefaultValue("false") boolean company,
                                  @QueryParam("contract") @DefaultValue("false") boolean contract,
                                  @QueryParam("user_contract") @DefaultValue("false")
                                      boolean userContract,
                                  @QueryParam("user") @DefaultValue("false") boolean user,
                                  @QueryParam("hours") @DefaultValue("false") boolean hours,
                                  @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                                  @QueryParam("order") @DefaultValue("hours.day:asc") String order) {
        WorkedWeekDTO workedWeek = null;
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            if (this.weekId != null) {
                workedWeek = workedWeekDAO.getWorkedWeekById(weekId, company, contract, userContract, user, hours, totalHours, order);
            } else if (this.year != null && this.week != null) {
                workedWeek = workedWeekDAO.getWorkedWeekByDate(Integer.parseInt(year), Integer.parseInt(week), company, contract, userContract, user, hours, totalHours, order);
            }
        } catch (SQLException e) {
            return Response.serverError().build();
        }

        if (workedWeek == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok(workedWeek).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateWorkedWeekTask(Worked entry) {
        WorkedDAO workedDAO;
        try {
            workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED);
            workedDAO.updateWorkedWeekTask(entry);
        } catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addWorkedWeekTask(Worked entry) {
        WorkedDAO workedDAO;
        try {
            workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED);
            workedDAO.addWorkedWeekTask(entry);
        } catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @DELETE
    @Consumes({MediaType.TEXT_PLAIN})
    public Response deleteWorkedWeekTask(String workedId) {
        WorkedDAO workedDAO;
        try {
            workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED);
            workedDAO.deleteWorkedWeekTask(workedId);
        } catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }


    @POST
    @Path("/confirm")
    @Consumes({MediaType.TEXT_PLAIN})
    public Response confirmWorkedWeek(String workedWeekId) {
        WorkedWeekDAO workedWeekDAO;
        try {
            workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            workedWeekDAO.confirmWorkedWeekById(workedWeekId);
        }catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }



    @DELETE
    @Path("/confirm")
    @Consumes({MediaType.TEXT_PLAIN})
    public Response removeConfirmWorkedWeek(String workedWeekId) {
        WorkedWeekDAO workedWeekDAO;
        try {
            workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            workedWeekDAO.removeConfirmWorkedWeekById(workedWeekId);
        }catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @PUT
    @Path("/note")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateWorkedWeekNote(WorkedWeek workedWeek) {
        WorkedWeekDAO workedWeekDAO;
        try {
            workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            workedWeekDAO.updateWorkedWeekNote(workedWeek);
        }catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }
}
