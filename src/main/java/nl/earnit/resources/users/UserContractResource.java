package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.InvoicePDFHandler;
import nl.earnit.dao.*;
import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.models.UserContract;
import nl.earnit.models.Worked;

import java.util.List;

/**
 * The type User contract resource.
 */
public class UserContractResource {
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
    private final String userId;
    private final String userContractId;

    /**
     * Instantiates a new User contract resource.
     *
     * @param uriInfo        the uri info
     * @param request        the request
     * @param userId         the user id
     * @param userContractId the user contract id
     */
    public UserContractResource(UriInfo uriInfo, Request request, String userId, String userContractId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
        this.userContractId = userContractId;
    }

    /**
     * Gets contract.
     *
     * @return the contract
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getContract() {
        UserContractDAO userContractDAO;
        UserContract uc;
        try {
            userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
            uc = userContractDAO.getUserContract(this.userContractId);
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok(uc).build();
    }

    /**
     * Gets worked.
     *
     * @return the worked
     */
    @GET
    @Path("/worked")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorked() {
        WorkedDAO workedDAO;
        List<Worked> workedList;
        try {
            workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED);
            workedList = workedDAO.getWorkedHours(userContractId);
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok(workedList).build();
    }

    /**
     * Gets worked week.
     *
     * @param year the year
     * @param week the week
     * @return the worked week
     */
    @Path("/worked/{year}/{week}")
    public UserContractWorkedResource getWorkedWeek(@PathParam("year") String year, @PathParam("week") String week) {
        return new UserContractWorkedResource(uriInfo, request, userId, userContractId, year, week);
    }

    /**
     * Gets worked week.
     *
     * @param weekId the week id
     * @return the worked week
     */
    @Path("/worked/{weekId}")
    public UserContractWorkedResource getWorkedWeek(@PathParam("weekId") String weekId) {
        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            if (!userContractDAO.hasUserContractAccessToWorked(userContractId, weekId)) {
                throw new ForbiddenException();
            }

            return new UserContractWorkedResource(uriInfo, request, userId, userContractId, weekId);
        } catch (ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerErrorException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets invoices.
     *
     * @param company      the company
     * @param contract     the contract
     * @param userContract the user contract
     * @param user         the user
     * @param hours        the hours
     * @param totalHours   the total hours
     * @param order        the order
     * @return the invoices
     */
    @GET
    @Path("/invoices")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getInvoices(@QueryParam("company") @DefaultValue("false") boolean company,
                                @QueryParam("contract") @DefaultValue("false") boolean contract,
                                @QueryParam("userContract") @DefaultValue("false")
                                boolean userContract,
                                @QueryParam("user") @DefaultValue("false") boolean user,
                                @QueryParam("hours") @DefaultValue("false") boolean hours,
                                @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                                @QueryParam("order") @DefaultValue("worked_week.year:asc,worked_week.week:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            List<WorkedWeekDTO> workedWeeks = workedWeekDAO.getWorkedWeeksForUser(userId, userContractId, null, null, company, contract,userContract, user,hours,totalHours, order);
            workedWeeks = workedWeeks.stream().filter(x -> x.getStatus().equals("APPROVED")).toList();

            return Response.ok(workedWeeks).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Gets invoices per student.
     *
     * @return the invoices per student
     */
    @GET
    @Path("/invoices/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getInvoicesPerStudent() {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);

            List<WorkedWeekDTO> workedWeeks = workedWeekDAO.getWorkedWeeksForUser(userId, userContractId, null, null, true, true, true, true, false, true, "");
            workedWeeks = workedWeeks.stream().filter(x -> x.getStatus().equals("APPROVED")).toList();

            if (workedWeeks.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            return Response
                .ok(InvoicePDFHandler.createInvoices(workedWeeks.stream().map(
                    InvoicePDFHandler.InvoiceInformation::fromWorkedWeek).toList()), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = invoices.zip")
                .build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Gets invoices per student.
     *
     * @param year the year
     * @param week the week
     * @return the invoices per student
     */
    @GET
    @Path("/invoices/download/{year}/{week}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getInvoicesPerStudent(@PathParam("year") String year, @PathParam("week") String week) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);

            List<WorkedWeekDTO> workedWeeks = workedWeekDAO.getWorkedWeeksForUser(null, userContractId, Integer.parseInt(year), Integer.parseInt(week), true, true, true, true, false, true, "");
            if (workedWeeks.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();

            return Response
                .ok(InvoicePDFHandler.createSingleInvoice(InvoicePDFHandler.InvoiceInformation.fromWorkedWeek(workedWeeks.get(0))), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = " +
                    InvoicePDFHandler.InvoiceInformation.getInvoiceNameFromWorkedWeek(workedWeeks.get(0)))
                .build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}
