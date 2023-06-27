package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.InvoicePDFHandler;
import nl.earnit.dao.*;
import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.db.Worked;

import java.util.List;

public class UserContractResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String userId;
    private final String userContractId;

    public UserContractResource(UriInfo uriInfo, Request request, String userId, String userContractId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
        this.userContractId = userContractId;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getContract() {
        UserContractDAO userContractDAO;
        UserContract uc;
        try {
            userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
            uc = userContractDAO.getUserContract(this.userId, this.userContractId);
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok(uc).build();
    }

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

    @Path("/worked/{year}/{week}")
    public UserContractWorkedResource getWorkedWeek(@PathParam("year") String year, @PathParam("week") String week) {
        return new UserContractWorkedResource(uriInfo, request, userId, userContractId, year, week);
    }

    @Path("/worked/{weekId}")
    public UserContractWorkedResource getWorkedWeek(@PathParam("weekId") String weekId) {
        return new UserContractWorkedResource(uriInfo, request, userId, userContractId, weekId);
    }

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
            return Response.ok(workedWeeks).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/invoices/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getInvoicesPerStudent() {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);

            List<WorkedWeekDTO> workedWeeks = workedWeekDAO.getWorkedWeeksForUser(userId, userContractId, null, null, true, true, true, true, false, true, "");

            return Response
                .ok(InvoicePDFHandler.createInvoices(workedWeeks.stream().map(
                    InvoicePDFHandler.InvoiceInformation::fromWorkedWeek).toList()), MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = invoices.zip")
                .build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

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
