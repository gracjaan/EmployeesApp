package nl.earnit.resources.users;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.CompanyDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.dao.WorkedDAO;
import nl.earnit.models.Company;
import nl.earnit.models.UserContract;

import java.util.List;

/**
 * The type User company resource.
 */
public class UserCompanyResource {
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
    private final String companyId;

    /**
     * Instantiates a new User company resource.
     *
     * @param uriInfo   the uri info
     * @param request   the request
     * @param userId    the user id
     * @param companyId the company id
     */
    public UserCompanyResource(UriInfo uriInfo, Request request, String userId, String companyId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
        this.companyId = companyId;
    }

    /**
     * Gets company.
     *
     * @return the company
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCompany() {
        CompanyDAO companyDAO;
        Company company;
        try {
            companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            company = companyDAO.getCompanyById(this.companyId);
        } catch (Exception e){
            return Response.serverError().build();
        }
        return Response.ok(company).build();
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
        WorkedDAO workedDAO;
        List<UserContract> userContracts;
        try {
            workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED);
            userContracts = workedDAO.getUserContracts(this.userId);
        } catch (Exception e){
            return Response.serverError().build();
        }
        return Response.ok(userContracts).build();
    }


    /**
     * Gets contract.
     *
     * @param userContractId the user contract id
     * @return the contract
     */
    @GET
    @Path("/contracts/{userContractId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getContract(@PathParam("userContractId") String userContractId) {
        UserContractDAO userContractDAO;
        UserContract uc;
        try {
            CompanyDAO companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);

            if (!companyDAO.hasCompanyAccessToUserContract(companyId, userContractId)) {
                throw new ForbiddenException();
            }

            userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
            uc = userContractDAO.getUserContract(userContractId);
        } catch (ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            return Response.serverError().build();
        }
        return Response.ok(uc).build();
    }
}
