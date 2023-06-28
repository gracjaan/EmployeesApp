package nl.earnit.resources.staff;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.dao.WorkedDAO;
import nl.earnit.dao.WorkedWeekDAO;
import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.dto.workedweek.WorkedWeekUndoSolvedDTO;
import nl.earnit.helpers.RequestHelper;
import nl.earnit.models.db.User;
import nl.earnit.models.resource.companies.CompanyCounts;

import java.util.List;

/**
 * The type Staff resource.
 */
@Path("/staff")
public class StaffResource {
    /**
     * The Uri info.
     */
    @Context
    UriInfo uriInfo;
    /**
     * The Request.
     */
    @Context
    Request request;

    /**
     * Gets staff.
     *
     * @param httpHeaders the http headers
     * @return the staff
     */
    @GET
    public Response getStaff(@Context HttpHeaders httpHeaders) {
        User user = RequestHelper.validateUser(httpHeaders);
        RequestHelper.handleAccessToStaff(user);

        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    /**
     * Add staff response.
     *
     * @return the response
     */
    @POST
    public Response addStaff() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    /**
     * Delete staff response.
     *
     * @param userId the user id
     * @return the response
     */
    @DELETE
    @Path("/{userId}")
    public Response deleteStaff(@PathParam("userId") String userId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    // TODO: everything related to rejecting and approving of hours needs to change to the new specification

    /**
     * Gets rejects.
     *
     * @param company      the company
     * @param contract     the contract
     * @param userContract the user contract
     * @param user         the user
     * @param hours        the hours
     * @param totalHours   the total hours
     * @param order        the order
     * @return the rejects
     */
    @GET
    @Path("/rejects")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getRejects(@QueryParam("company") @DefaultValue("false") boolean company,
                               @QueryParam("contract") @DefaultValue("false")
                                   boolean contract,
                               @QueryParam("userContract") @DefaultValue("false")
                                   boolean userContract,
                               @QueryParam("user") @DefaultValue("false") boolean user,
                               @QueryParam("hours") @DefaultValue("false") boolean hours,
                               @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                               @QueryParam("order") @DefaultValue("worked_week.week:asc,hours.day:asc") String order) {

        try {
            WorkedWeekDAO workedWeekDAO =
                    (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            List<WorkedWeekDTO> rejectedWeeks = workedWeekDAO.getWorkedWeeksToApproveForStaff(company, contract, userContract , user, hours, totalHours, order);
            return Response.ok(rejectedWeeks).build();

        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Gets reject details.
     *
     * @param workedWeekId the worked week id
     * @param company      the company
     * @param contract     the contract
     * @param userContract the user contract
     * @param user         the user
     * @param hours        the hours
     * @param totalHours   the total hours
     * @param order        the order
     * @return the reject details
     */
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
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Resolved accept response.
     *
     * @param workedWeekId the worked week id
     * @param company      the company
     * @param contract     the contract
     * @param userContract the user contract
     * @param user         the user
     * @param hours        the hours
     * @param totalHours   the total hours
     * @param order        the order
     * @return the response
     */
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
            WorkedDAO workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED);

            if (!workedDAO.acceptStudentSuggestion(workedWeekId)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(workedWeekDAO.setWorkedWeekStatus(workedWeekId, "APPROVED", company, contract, userContract, user, hours, totalHours, order)).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Resolved reject response.
     *
     * @param workedWeekId the worked week id
     * @param company      the company
     * @param contract     the contract
     * @param userContract the user contract
     * @param user         the user
     * @param hours        the hours
     * @param totalHours   the total hours
     * @param order        the order
     * @return the response
     */
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
            WorkedDAO workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED);

            if (!workedDAO.acceptCompanySuggestion(workedWeekId)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response.ok(workedWeekDAO.setWorkedWeekStatus(workedWeekId, "APPROVED", company, contract, userContract, user, hours, totalHours, order)).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Undo resolved reject response.
     *
     * @param workedWeekId            the worked week id
     * @param company                 the company
     * @param contract                the contract
     * @param userContract            the user contract
     * @param user                    the user
     * @param hours                   the hours
     * @param totalHours              the total hours
     * @param order                   the order
     * @param workedWeekUndoSolvedDTO the worked week undo solved dto
     * @return the response
     */
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
            WorkedDAO workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED);

            String status;
            if (workedWeekUndoSolvedDTO.getSolved() == null) {
                status = "SUGGESTION_DENIED";
            } else {
                status = "APPROVED";

                if (!workedWeekUndoSolvedDTO.getSolved()) {
                    if (!workedDAO.acceptCompanySuggestion(workedWeekId)) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                } else {
                    if (!workedDAO.acceptStudentSuggestion(workedWeekId)) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                }
            }


            return Response.ok(workedWeekDAO.setWorkedWeekStatus(workedWeekId, "APPROVED", company, contract, userContract, user, hours, totalHours, order)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * Gets employee count per company.
     *
     * @return the employee count per company
     */
    @GET
    @Path("/companies")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getEmployeeCountPerCompany() {
        UserContractDAO userContractDAO;
        List<CompanyCounts> count;
        try {
            userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
            count = userContractDAO.getNumberOfEmployeesByCompany();
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok(count).build();
    }

}
