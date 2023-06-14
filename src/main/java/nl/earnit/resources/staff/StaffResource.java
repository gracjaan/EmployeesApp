package nl.earnit.resources.staff;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.ContractDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.WorkedWeekDAO;
import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.dto.workedweek.WorkedWeekUndoApprovalDTO;
import nl.earnit.dto.workedweek.WorkedWeekUndoSolvedDTO;
import nl.earnit.helpers.RequestHelper;
import nl.earnit.models.db.User;
import nl.earnit.models.db.WorkedWeek;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/staff")
public class StaffResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @GET
    public Response getStaff(@Context HttpHeaders httpHeaders) {
        User user = RequestHelper.validateUser(httpHeaders);
        RequestHelper.handleAccessToStaff(user);

        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    public Response addStaff() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/{userId}")
    public Response deleteStaff(@PathParam("userId") String userId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    // TODO: everything related to rejecting and approving of hours needs to change to the new specification

    @GET
    @Path("/rejects")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getRejects() {

        try {
            WorkedWeekDAO workedWeekDAO =
                    (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            List<WorkedWeekDTO> rejectedWeeks = workedWeekDAO.getWorkedWeeksToApproveForStaff(true, true, true , true, true, true, "");
            return Response.ok(rejectedWeeks).build();

        } catch (SQLException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/rejects/{workedWeekId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getRejectDetails(@PathParam("workedWeekId") String workedWeekId,
                                     @QueryParam("company") @DefaultValue("false") boolean company,
                                     @QueryParam("contract") @DefaultValue("false")
                                         boolean contract,
                                     @QueryParam("userContract") @DefaultValue("false")
                                         boolean userContract,
                                     @QueryParam("user") @DefaultValue("false") boolean user,
                                     @QueryParam("hours") @DefaultValue("false") boolean hours,
                                     @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                                     @QueryParam("order") @DefaultValue("hours.day:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED_WEEK);

            return Response.ok(
                workedWeekDAO.getWorkedWeekById(workedWeekId, company, contract, userContract, user,
                    hours, totalHours, order)).build();
        } catch (SQLException e) {
            System.out.println(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/rejects/{workedWeekId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response resolvedAccept(@PathParam("workedWeekId") String workedWeekId,
                                   @QueryParam("company") @DefaultValue("false") boolean company,
                                   @QueryParam("contract") @DefaultValue("false")
                                       boolean contract,
                                   @QueryParam("userContract") @DefaultValue("false")
                                       boolean userContract,
                                   @QueryParam("user") @DefaultValue("false") boolean user,
                                   @QueryParam("hours") @DefaultValue("false") boolean hours,
                                   @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                                   @QueryParam("order") @DefaultValue("hours.day:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO =
                    (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);

            return Response.ok(workedWeekDAO.setSolvedWorkedWeek(workedWeekId, true, company, contract, userContract, user, hours, totalHours, order)).build();
        } catch (SQLException e) {
            System.out.println(e);
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/rejects/{workedWeekId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response resolvedReject(@PathParam("workedWeekId") String workedWeekId,
                                   @QueryParam("company") @DefaultValue("false") boolean company,
                                   @QueryParam("contract") @DefaultValue("false")
                                       boolean contract,
                                   @QueryParam("userContract") @DefaultValue("false")
                                       boolean userContract,
                                   @QueryParam("user") @DefaultValue("false") boolean user,
                                   @QueryParam("hours") @DefaultValue("false") boolean hours,
                                   @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                                   @QueryParam("order") @DefaultValue("hours.day:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO =
                    (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            return Response.ok(workedWeekDAO.setSolvedWorkedWeek(workedWeekId, false, company, contract, userContract, user, hours, totalHours, order)).build();
        } catch (SQLException e) {
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/rejects/{workedWeekId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response undoResolvedReject(@PathParam("workedWeekId") String workedWeekId,
                                           @QueryParam("company") @DefaultValue("false") boolean company,
                                           @QueryParam("contract") @DefaultValue("false")
                                           boolean contract,
                                           @QueryParam("userContract") @DefaultValue("false")
                                           boolean userContract,
                                           @QueryParam("user") @DefaultValue("false") boolean user,
                                           @QueryParam("hours") @DefaultValue("false") boolean hours,
                                           @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                                           @QueryParam("order") @DefaultValue("hours.day:asc") String order,
                                           WorkedWeekUndoSolvedDTO workedWeekUndoSolvedDTO) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED_WEEK);

            return Response.ok(workedWeekDAO.setSolvedWorkedWeek(workedWeekId, workedWeekUndoSolvedDTO.getSolved(), company, contract, userContract, user,
                hours, totalHours, order)).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
