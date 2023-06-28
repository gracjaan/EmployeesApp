package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.InvoicePDFHandler;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.dao.UserDAO;
import nl.earnit.dao.WorkedWeekDAO;
import nl.earnit.dto.workedweek.NotificationDTO;
import nl.earnit.dto.workedweek.UserContractDTO;
import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.helpers.RequestHelper;
import nl.earnit.models.db.User;
import nl.earnit.models.resource.InvalidEntry;
import nl.earnit.models.resource.users.UserResponse;

import java.util.List;
import java.util.regex.Pattern;

/**
 * The type User resource.
 */
public class UserResource {
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

    /**
     * Instantiates a new User resource.
     *
     * @param uriInfo the uri info
     * @param request the request
     * @param userId  the user id
     */
    public UserResource(UriInfo uriInfo, Request request, String userId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUser() {
        UserDAO userDAO;
        User user;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            user = userDAO.getUserById(this.userId);
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok(new UserResponse(user)).build();
    }

    /**
     * Update user response.
     *
     * @param httpHeaders the http headers
     * @param user        the user
     * @return the response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateUser(@Context HttpHeaders httpHeaders, UserResponse user) {
        // Validate create user
        if (user == null || user.getEmail() == null || user.getFirstName() == null || user.getLastName() == null) {
            return Response.status(400).build();
        }
        if (user.getActive() != null) {
            RequestHelper.handleAccessToStaff(RequestHelper.validateUser(httpHeaders));
        } else {
            user.setActive(true);
        }

        // Validate user
        if (user.getFirstName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntry("firstName")).build();
        }

        if (user.getLastName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntry("lastName")).build();
        }

        String emailRegex = "([-!#-'*+/-9=?A-Z^-~]+(\\.[-!#-'*+/-9=?A-Z^-~]+)*|\"(\\[]!#-[^-~ \\t]|(\\\\[\\t -~]))+\")@[0-9A-Za-z]([0-9A-Za-z-]{0,61}[0-9A-Za-z])?(\\.[0-9A-Za-z]([0-9A-Za-z-]{0,61}[0-9A-Za-z])?)+";
        Pattern emailPattern = Pattern.compile(emailRegex);
        if (!emailPattern.matcher(user.getEmail()).matches()) {
            return Response.status(422).entity(new InvalidEntry("email")).build();
        }

        UserDAO userDAO;
        User dbUser;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            User userEmailCheck = userDAO.getUserByEmail(user.getEmail());
            if (userEmailCheck != null && !userEmailCheck.getId().equals(userId)) {
                return Response.status(Response.Status.CONFLICT).entity(new InvalidEntry("email")).build();
            }

            user.setId(userId);
            dbUser = userDAO.updateUser(user);
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok(new UserResponse(dbUser)).build();
    }

    /**
     * Disable user response.
     *
     * @param httpHeaders the http headers
     * @return the response
     */
    @DELETE
    public Response disableUser(@Context HttpHeaders httpHeaders) {
        UserDAO userDAO;
        RequestHelper.handleAccessToStaff(RequestHelper.validateUser(httpHeaders));
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            userDAO.disableUserById(userId);
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    /**
     * Gets companies.
     *
     * @return the companies
     */
    @GET
    @Path("/companies")
    public Response getCompanies() {
        UserDAO userDAO;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            return Response.ok(userDAO.getCompanies(this.userId)).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Gets company.
     *
     * @param companyId the company id
     * @return the company
     */
    @Path("/companies/{companyId}")
    public UserCompanyResource getCompany(@PathParam("companyId") String companyId) {
        return new UserCompanyResource(uriInfo, request, userId, companyId);
    }

    /**
     * Gets contracts.
     *
     * @return the contracts
     */
    @GET
    @Path("/contracts")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getContracts() {
        UserContractDAO userContractDAO;
        List<UserContractDTO> userContracts;
        try {
            userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
            userContracts = userContractDAO.getUserContractsByUserId(this.userId);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(userContracts).build();
    }

    /**
     * Gets contract.
     *
     * @param userContractId the user contract id
     * @return the contract
     */
    @Path("/contracts/{userContractId}")
    public UserContractResource getContract(@PathParam("userContractId") String userContractId) {
        return new UserContractResource(uriInfo, request, userId, userContractId);
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
            List<WorkedWeekDTO> workedWeeks = workedWeekDAO.getWorkedWeeksForUser(userId, null, null, null, company, contract,userContract, user,hours,totalHours, order);
            workedWeeks = workedWeeks.stream().filter(x -> x.getStatus().equals("APPROVED")).toList();

            return Response.ok(workedWeeks).build();
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

            List<WorkedWeekDTO> workedWeeks = workedWeekDAO.getWorkedWeeksForUser(userId, null, Integer.parseInt(year), Integer.parseInt(week), true, true, true, true, false, true, "");
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

    @GET
    @Path("/notifications")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getNotifications() {
        UserDAO userDAO;
        List<NotificationDTO> notifications;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            User user = userDAO.getUserById(userId);
            if (user.getType().equals("ADMINISTRATOR")) {
                notifications = userDAO.getNotificationsForStaffUser();
            } else {
                notifications = userDAO.getNotificationsForUser(userId);
            }
        }
        catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok(notifications).build();
    }

    @POST
    @Path("/notifications/{notificationId}")
    public Response changeToNotificationSeen(@PathParam("notificationId") String notificationId) {
        UserDAO userDAO;
        try {
            userDAO = (UserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER);
            userDAO.changeNotificationToSeen(notificationId);
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }
}
