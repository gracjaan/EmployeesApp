package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.WorkedDAO;
import nl.earnit.models.db.Worked;

import java.sql.SQLException;
import java.util.List;

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
    public Response getWorkedWeek() {
        WorkedDAO workedDAO;
        List<Worked> workedList;
        try {
            workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED);
            if (this.weekId!=null) {
                workedList = workedDAO.getWorkedWeekById(userContractId, weekId);
            } else if (this.year!=null&&this.week!=null) {
                workedList = workedDAO.getWorkedWeek(userContractId, year, week);
            }
            else {
                return Response.serverError().build();
            }
        } catch (SQLException e) {
            return Response.serverError().build();
        }

        return Response.ok(workedList).build();
    }

    @PUT
    public Response updateWorkedWeekTask() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    public Response addWorkedWeekTask() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    public Response deleteWorkedWeekTask() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }


    @POST
    @Path("/confirm")
    public Response confirmWorkedWeek() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/confirm")
    public Response removeConfirmWorkedWeek() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    @Path("/note")
    public Response updateWorkedWeekNote() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }
}
