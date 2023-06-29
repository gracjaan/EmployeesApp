package nl.earnit.resources.companies;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.*;
import nl.earnit.helpers.RequestHelper;
import nl.earnit.models.Company;
import nl.earnit.models.User;
import nl.earnit.dto.InvalidEntryDTO;
import nl.earnit.dto.company.CreateCompanyDTO;
import nl.earnit.dto.user.UserResponseDTO;

import java.util.List;

/**
 * The type Companies resource.
 */
@Path("/companies")
public class CompaniesResource {
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
     * Gets companies.
     *
     * @param httpHeaders the http headers
     * @param order       the order
     * @return the companies
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getCompanies(@Context HttpHeaders httpHeaders,
                                 @QueryParam("order") @DefaultValue("company.id:asc") String order) {
        User user = RequestHelper.validateUser(httpHeaders);
        RequestHelper.handleAccessToStaff(user);

        try {
            CompanyDAO companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            List<Company> companies = companyDAO.getAllCompanies(order);

            return Response.ok(companies).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create company response.
     *
     * @param createCompanyDTO the create company
     * @return the response
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCompany(CreateCompanyDTO createCompanyDTO) {
        // Validate create company
        if (createCompanyDTO == null || createCompanyDTO.getName() == null || createCompanyDTO.getUserId() == null) {
            return Response.status(400).build();
        }

        // Validate company
        if (createCompanyDTO.getName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntryDTO("name")).build();
        }

        // Validate user
        Company company;
        try {
            DAOManager daoManager = DAOManager.getInstance();

            // Check user
            UserDAO userDAO = (UserDAO) daoManager.getDAO(DAOManager.DAO.USER);
            User user = userDAO.getUserById(createCompanyDTO.getUserId());

            if (user == null) {
                return Response.status(422).entity(new InvalidEntryDTO("userId")).build();
            }

            // Convert user to a company user
            if (!user.getType().equals("COMPANY")) {
                // Earn it staff can not have a company and also not become company users.
                if (user.getType().equals("ADMINISTRATOR")) {
                    return Response.status(403).build();
                }

                // Students can only become a company user if they have no contracts
                if (user.getType().equals("STUDENT")) {
                    UserContractDAO userContractDAO = (UserContractDAO) daoManager.getDAO(
                        DAOManager.DAO.USER_CONTRACT);

                    if (userContractDAO.countContractsForUser(user.getId()) > 0) {
                        return Response.status(403).build();
                    }

                    user.setType("COMPANY");
                    if (!userDAO.updateUserType(new UserResponseDTO(user))) {
                        return Response.status(Response.Status.NOT_MODIFIED).build();
                    }
                }
            }

            // Create company
            CompanyDAO companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            company = companyDAO.createCompany(createCompanyDTO.getName(), createCompanyDTO.getKvk(), createCompanyDTO.getAddress());

            // Link company user
            CompanyUserDAO companyUserDAO = (CompanyUserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY_USER);
            if (!companyUserDAO.isUserWorkingForCompany(company.getId(), createCompanyDTO.getUserId())) {
                if (!companyUserDAO.createCompanyUser(company.getId(), createCompanyDTO.getUserId())) {
                    company = null;
                }
            }
        } catch (Exception e) {
            return Response.serverError().build();
        }

        // If no company something must have gone wrong
        if (company == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().entity(company).build();
    }

    /**
     * Gets company.
     *
     * @param httpHeaders the http headers
     * @param companyId   the company id
     * @return the company
     */
    @Path("/{companyId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CompanyResource getCompany(@Context HttpHeaders httpHeaders, @PathParam("companyId") String companyId) {
        RequestHelper.validateUUID(companyId);
        User user = RequestHelper.validateUser(httpHeaders);
        RequestHelper.handleAccessToCompany(companyId, user);

        return new CompanyResource(uriInfo, request, companyId);
    }
}
