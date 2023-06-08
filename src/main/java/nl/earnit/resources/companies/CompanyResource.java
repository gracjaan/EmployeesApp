package nl.earnit.resources.companies;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.ContractDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.WorkedWeekDAO;
import nl.earnit.dto.workedweek.WorkedWeekUndoApprovalDTO;

import java.sql.SQLException;

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
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @PUT
    public Response updateCompany() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @DELETE
    public Response deleteCompany() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
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
    public Response getContracts(@Context HttpHeaders httpHeaders) {

        try {
            ContractDAO contractDAO =
                (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);

            return Response.ok(contractDAO.getAllContractsByCompanyId(companyId)).build();

        } catch (SQLException e) {
            System.out.println(e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/contracts")
    public Response addContract() {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
    }

    @Path("/contracts/{contractId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CompanyContractResource getCompany(@PathParam("contractId") String contractId) {
        return new CompanyContractResource(uriInfo, request, companyId, contractId);
    }

    @GET
    @Path("/approves")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getToApprove(@QueryParam("company") @DefaultValue("false") boolean company,
                                 @QueryParam("contract") @DefaultValue("false") boolean contract,
                                 @QueryParam("user_contract") @DefaultValue("false")
                                 boolean userContract,
                                 @QueryParam("user") @DefaultValue("false") boolean user,
                                 @QueryParam("order") @DefaultValue("worked_week.year:asc,worked_week.week:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED_WEEK);

            return Response.ok(
                workedWeekDAO.getWorkedWeeksToApproveForCompany(companyId, company, contract,
                    userContract, user, order)).build();
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
                                      @QueryParam("user_contract") @DefaultValue("false")
                                      boolean userContract,
                                      @QueryParam("user") @DefaultValue("false") boolean user,
                                      @QueryParam("hours") @DefaultValue("false") boolean hours,
                                      @QueryParam("order") @DefaultValue("hours.day:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED_WEEK);

            if (!workedWeekDAO.hasCompanyAccessToWorkedWeek(companyId, workedWeekId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            return Response.ok(
                workedWeekDAO.getWorkedWeekById(workedWeekId, company, contract, userContract, user,
                    hours, order)).build();
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
                                           @QueryParam("user_contract") @DefaultValue("false")
                                               boolean userContract,
                                           @QueryParam("user") @DefaultValue("false") boolean user,
                                           @QueryParam("hours") @DefaultValue("false") boolean hours,
                                           @QueryParam("order") @DefaultValue("hours.day:asc") String order,
                                           WorkedWeekUndoApprovalDTO workedWeekUndoApprovalDTO) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED_WEEK);

            if (!workedWeekDAO.hasCompanyAccessToWorkedWeek(companyId, workedWeekId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            return Response.ok(workedWeekDAO.setApproveWorkedWeek(workedWeekId, workedWeekUndoApprovalDTO.getApprove(), company, contract, userContract, user,
                hours, order)).build();
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
                                     @QueryParam("user_contract") @DefaultValue("false")
                                         boolean userContract,
                                     @QueryParam("user") @DefaultValue("false") boolean user,
                                     @QueryParam("hours") @DefaultValue("false") boolean hours,
                                     @QueryParam("order") @DefaultValue("hours.day:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED_WEEK);

            if (!workedWeekDAO.hasCompanyAccessToWorkedWeek(companyId, workedWeekId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            return Response.ok(workedWeekDAO.setApproveWorkedWeek(workedWeekId, true, company, contract, userContract, user,
                hours, order)).build();
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
                                     @QueryParam("user_contract") @DefaultValue("false")
                                         boolean userContract,
                                     @QueryParam("user") @DefaultValue("false") boolean user,
                                     @QueryParam("hours") @DefaultValue("false") boolean hours,
                                     @QueryParam("order") @DefaultValue("hours.day:asc") String order) {
        try {
            WorkedWeekDAO workedWeekDAO = (WorkedWeekDAO) DAOManager.getInstance().getDAO(
                DAOManager.DAO.WORKED_WEEK);

            if (!workedWeekDAO.hasCompanyAccessToWorkedWeek(companyId, workedWeekId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            return Response.ok(workedWeekDAO.setApproveWorkedWeek(workedWeekId, false, company, contract, userContract, user,
                hours, order)).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }
}
