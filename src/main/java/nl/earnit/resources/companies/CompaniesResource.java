package nl.earnit.resources.companies;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.*;
import nl.earnit.helpers.RequestHelper;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;
import nl.earnit.models.resource.InvalidEntry;
import nl.earnit.models.resource.companies.CreateCompany;
import nl.earnit.models.resource.users.UserResponse;

import java.sql.SQLException;
import java.util.List;

@Path("/companies")
public class CompaniesResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

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

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response createCompany(CreateCompany createCompany) {
        // Validate create company
        if (createCompany == null || createCompany.getName() == null || createCompany.getUserId() == null) {
            return Response.status(400).build();
        }

        // Validate company
        if (createCompany.getName().length() <= 2) {
            return Response.status(422).entity(new InvalidEntry("name")).build();
        }

        // Validate user
        Company company;
        try {
            DAOManager daoManager = DAOManager.getInstance();

            // Check user
            UserDAO userDAO = (UserDAO) daoManager.getDAO(DAOManager.DAO.USER);
            User user = userDAO.getUserById(createCompany.getUserId());

            if (user == null) {
                return Response.status(422).entity(new InvalidEntry("userId")).build();
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
                    if (!userDAO.updateUserType(new UserResponse(user))) {
                        return Response.status(Response.Status.NOT_MODIFIED).build();
                    }
                }
            }

            // Create company
            CompanyDAO companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            company = companyDAO.createCompany(createCompany.getName());

            // Link company user
            CompanyUserDAO companyUserDAO = (CompanyUserDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY_USER);
            if (!companyUserDAO.isUserWorkingForCompany(company.getId(), createCompany.getUserId())) {
                if (!companyUserDAO.createCompanyUser(company.getId(), createCompany.getUserId())) {
                    company = null;
                }
            }
        } catch (Exception e) {
            System.out.println(e);
            return Response.serverError().build();
        }

        // If no company something must have gone wrong
        if (company == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().entity(company).build();
    }

    @Path("/{companyId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public CompanyResource getCompany(@Context HttpHeaders httpHeaders, @PathParam("companyId") String companyId) {
        RequestHelper.validateUUID(companyId);
        User user = RequestHelper.validateUser(httpHeaders);
        RequestHelper.handleAccessToCompany(companyId, user);

        return new CompanyResource(uriInfo, request, companyId);
    }
}
