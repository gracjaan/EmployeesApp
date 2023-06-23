package nl.earnit.resources.companies;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.*;
import nl.earnit.dto.workedweek.WorkedWeekDTO;
import nl.earnit.dto.workedweek.WorkedWeekUndoApprovalDTO;
import nl.earnit.helpers.RequestHelper;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;
import nl.earnit.models.resource.InvalidEntry;
import nl.earnit.models.resource.users.UserResponse;
import nl.earnit.models.resource.contracts.Contract;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CompanyResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String companyId;

    public CompanyResource(UriInfo uriInfo, Request request, String companyId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.companyId = companyId;
    }

    @GET
    public Response getCompany() {
        try {
            CompanyDAO companyDAO =
                (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);

            return Response.ok(companyDAO.getCompanyById(companyId)).build();
        } catch (SQLException e) {
            return Response.serverError().build();
        }
    }

    @PUT
    public Response updateCompany(@Context HttpHeaders httpHeaders, Company company) {
        // Validate create company
        if (company == null || company.getName() == null) {
            return Response.status(400).build();
        }

        if (company.getActive() != null) {
            RequestHelper.handleAccessToStaff(RequestHelper.validateUser(httpHeaders));
        } else {
            company.setActive(true);
        }

        // Validate company
        if (company.getName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntry("name")).build();
        }

        try {
            CompanyDAO companyDAO =
                (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);

            company.setId(companyId);
            return Response.ok(companyDAO.updateCompany(company)).build();
        } catch (SQLException e) {

            return Response.serverError().build();
        }
    }

    @DELETE
    public Response disableCompany(@Context HttpHeaders httpHeaders) {
        CompanyDAO companyDAO;
        RequestHelper.handleAccessToStaff(RequestHelper.validateUser(httpHeaders));
        try {
            companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            companyDAO.disableCompanyById(companyId);
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/administrators")
    public Response getAdministrators() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @POST
    @Path("/administrators")
    public Response addAdministrator() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    @Path("/administrators/{userId}")
    public Response deleteAdministrator(@PathParam("userId") String userId) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @GET
    @Path("/contracts")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getContracts(@QueryParam("company") @DefaultValue("false") boolean company,
                                 @QueryParam("userContracts") @DefaultValue("false")
                                     boolean userContracts,
                                 @QueryParam("userContractsUser") @DefaultValue("false") boolean userContractsUser,
                                 @QueryParam("order") @DefaultValue("contract.role:asc,user_contract.user.last_name:asc") String order) {
        try {
            ContractDAO contractDAO =
                (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);

            return Response.ok(contractDAO.getAllContractsByCompanyId(companyId,company, userContracts, userContractsUser, order)).build();
        } catch (SQLException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/students")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudents() {
        List<UserResponse> users;
        try {
            CompanyDAO companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            users = companyDAO.getStudentsForCompany(companyId);
        } catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok(users).build();
    }

    @GET
    @Path("/students/{studentId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getStudent(@PathParam("studentId") String studentId,
                               @QueryParam("userContracts") @DefaultValue("false") boolean userContracts,
                               @QueryParam("userContractsContract") @DefaultValue("false") boolean userContractsContract,
                               @QueryParam("order") @DefaultValue("contract.role:asc") String order) {
        try {
            CompanyDAO companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            if (!companyDAO.hasCompanyAccessToUser(companyId, studentId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            return Response.ok(companyDAO.getStudentForCompany(companyId, studentId, userContracts, userContractsContract, order)).build();
        } catch (SQLException e) {
            System.out.println(e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/contracts")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getContracts(Contract contract) {
        ContractDAO contractDAO;

        try {
            contractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);
            contractDAO.createContract(contract, companyId);
        } catch (SQLException e){
            System.out.println(e);
            return Response.serverError().build();
        }
        return Response.ok().build();
    }

    @Path("/contracts/{contractId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CompanyContractResource getCompany(@PathParam("contractId") String contractId) {
        return new CompanyContractResource(uriInfo, request, companyId, contractId);
    }

    @GET
    @Path("/invoices/{year}/{week}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getInvoices(@PathParam("year") String year,
                                @PathParam("week") String week,
                                @QueryParam("company") @DefaultValue("false") boolean company,
                                @QueryParam("contract") @DefaultValue("false") boolean contract,
                                @QueryParam("userContract") @DefaultValue("false")
                                    boolean userContract,
                                @QueryParam("user") @DefaultValue("false") boolean user,
                                @QueryParam("hours") @DefaultValue("false") boolean hours,
                                @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                                @QueryParam("order") @DefaultValue("worked_week.year:asc,worked_week.week:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            List<WorkedWeekDTO> workedWeeks = workedWeekDAO.getWorkedWeeksForCompany(companyId, Integer.parseInt(year), Integer.parseInt(week), company,contract,userContract, user,hours,totalHours, order);
            return Response.ok(workedWeeks).build();
        } catch (SQLException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/invoices/{studentId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getInvoices(@PathParam("studentId") String studentId,
                                @QueryParam("company") @DefaultValue("false") boolean company,
                                @QueryParam("contract") @DefaultValue("false") boolean contract,
                                @QueryParam("userContract") @DefaultValue("false")
                                boolean userContract,
                                @QueryParam("user") @DefaultValue("false") boolean user,
                                @QueryParam("hours") @DefaultValue("false") boolean hours,
                                @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                                @QueryParam("order") @DefaultValue("worked_week.year:asc,worked_week.week:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED_WEEK);
            CompanyDAO companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            if (!companyDAO.hasCompanyAccessToUser(companyId, studentId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            List<WorkedWeekDTO> workedWeeks = workedWeekDAO.getWorkedWeeksForCompanyForUser(companyId, studentId, company,contract,userContract, user,hours,totalHours, order);
            return Response.ok(workedWeeks).build();
        } catch (SQLException e) {
            System.out.println(e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/approves")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getToApprove(@QueryParam("company") @DefaultValue("false") boolean company,
                                 @QueryParam("contract") @DefaultValue("false") boolean contract,
                                 @QueryParam("userContract") @DefaultValue("false")
                                 boolean userContract,
                                 @QueryParam("user") @DefaultValue("false") boolean user,
                                 @QueryParam("hours") @DefaultValue("false") boolean hours,
                                 @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                                 @QueryParam("order") @DefaultValue("worked_week.year:asc,worked_week.week:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED_WEEK);

            return Response.ok(
                workedWeekDAO.getWorkedWeeksToApproveForCompany(companyId, company, contract,
                    userContract, user, hours, totalHours, order)).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/approves/{workedWeekId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getApproveDetails(@PathParam("workedWeekId") String workedWeekId,
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

            if (!workedWeekDAO.hasCompanyAccessToWorkedWeek(companyId, workedWeekId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            return Response.ok(
                workedWeekDAO.getWorkedWeekById(workedWeekId, company, contract, userContract, user,
                    hours, totalHours, order)).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PUT
    @Path("/approves/{workedWeekId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response undoApprovalWorkedWeek(@PathParam("workedWeekId") String workedWeekId,
                                           @QueryParam("company") @DefaultValue("false") boolean company,
                                           @QueryParam("contract") @DefaultValue("false")
                                               boolean contract,
                                           @QueryParam("userContract") @DefaultValue("false")
                                               boolean userContract,
                                           @QueryParam("user") @DefaultValue("false") boolean user,
                                           @QueryParam("hours") @DefaultValue("false") boolean hours,
                                           @QueryParam("totalHours") @DefaultValue("false") boolean totalHours,
                                           @QueryParam("order") @DefaultValue("hours.day:asc") String order,
                                           WorkedWeekUndoApprovalDTO workedWeekUndoApprovalDTO) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED_WEEK);

            if (!workedWeekDAO.hasCompanyAccessToWorkedWeek(companyId, workedWeekId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            return Response.ok(workedWeekDAO.setApproveWorkedWeek(workedWeekId, workedWeekUndoApprovalDTO.getApprove(), company, contract, userContract, user,
                hours, totalHours, order)).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/approves/{workedWeekId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response acceptWorkedWeek(@PathParam("workedWeekId") String workedWeekId,
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

            if (!workedWeekDAO.hasCompanyAccessToWorkedWeek(companyId, workedWeekId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            return Response.ok(workedWeekDAO.setApproveWorkedWeek(workedWeekId, true, company, contract, userContract, user,
                hours, totalHours, order)).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DELETE
    @Path("/approves/{workedWeekId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response rejectWorkedWeek(@PathParam("workedWeekId") String workedWeekId,
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

            if (!workedWeekDAO.hasCompanyAccessToWorkedWeek(companyId, workedWeekId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            return Response.ok(workedWeekDAO.setApproveWorkedWeek(workedWeekId, false, company, contract, userContract, user,
                hours, totalHours, order)).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
