package nl.earnit.resources.companies;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import nl.earnit.dao.CompanyDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.dao.UserDAO;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.User;
import nl.earnit.models.resource.InvalidEntry;
import nl.earnit.models.resource.companies.CreateCompany;

import java.sql.SQLException;

@Path("/companies")
public class CompaniesResource {
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
                    userDAO.updateUser(user);
                }
            }

            // Create company
            CompanyDAO companyDAO = (CompanyDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.COMPANY);
            company = companyDAO.createCompany(createCompany.getName());
        } catch (SQLException e) {
            return Response.serverError().build();
        }

        // If no company something must have gone wrong
        if (company == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok().entity(company).build();
    }
}
