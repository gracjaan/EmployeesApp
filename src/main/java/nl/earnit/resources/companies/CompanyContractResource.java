package nl.earnit.resources.companies;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.xml.bind.JAXBElement;
import nl.earnit.dao.ContractDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.resource.companies.AddUserToContract;
import nl.earnit.models.resource.contracts.Contract;
import nl.earnit.models.resource.contracts.DescriptionRole;

import java.sql.SQLException;
import java.util.List;

/* TODO: 5/31/2023 Change delete methods to disable when the database schema is updated
                   Add @Produces to relevant methods after merge
 */

public class CompanyContractResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String companyId;
    private final String contractId;
    public CompanyContractResource(UriInfo uriInfo, Request request, String companyId,
                                   String contractId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.companyId = companyId;
        this.contractId = contractId;
    }

    @GET
    @PathParam("/companies/{companyId}/contracts")
    public List<Contract> getContracts(@PathParam("companyId") String companyId) {
        if (companyId == null) {
            return null;
        }

        List<Contract> result;

        try {
            ContractDAO ContractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);

            result = ContractDAO.getAllContractsByCompanyId(companyId);

        } catch (SQLException e) {
            return null;
        }
        return result;

    }

    @GET
    @PathParam("/companies/{companyId}/contracts/{contractId}")
    public DescriptionRole getContract(@PathParam("contractId") String contractId) {
        DescriptionRole result = new DescriptionRole();

        if (contractId == null) {
            return null;
        }

        try {
            ContractDAO contractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);

            result.setDescription(contractDAO.getContract(contractId).getDescription());
            result.setRole(contractDAO.getContract(contractId).getRole());

        } catch (SQLException e) {
            return null;
        }
        return result;
    }

    @PUT
    @Path("/companies/{companyId}/contracts/{contractId}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateContract(@PathParam("contractId") String contractId, JAXBElement<DescriptionRole> descriptionRole ) {
        if (contractId == null) {
            return Response.status(400).build();
        }

        String description = descriptionRole.getValue().getDescription();
        String role = descriptionRole.getValue().getRole();

        try {
            ContractDAO contractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);

            if(description!=null) {
                contractDAO.updateContractDescription(contractId, description);
            }
            if(role!=null) {
                contractDAO.updateContractRole(contractId, role);
            }

        } catch (SQLException e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @DELETE
    @PathParam("/companies/{companyId}/contracts/{contractId}")
    public Response deleteContract(@PathParam("contractId") String contractId) {
        if (contractId == null) {
            return Response.status(400).build();
        }

        try {
            ContractDAO contractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);
            contractDAO.deleteContract(contractId);

        } catch (SQLException e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("{contractId}/employees")
    public List<UserContract> getUserContracts(@PathParam("contractId") String contractId) {
        if (contractId == null) {
            return null;
        }

        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
             return userContractDAO.getUserContractByContractId(contractId);

        } catch (SQLException e) {
            return null;
        }
    }

    @POST
    @Path("/employees")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addEmployee(AddUserToContract addUserToContract) {

        if (addUserToContract == null || addUserToContract.getHourlyWage() <= 0 || addUserToContract.getUserId() == null) {
            return Response.status(400).build();
        }


        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            userContractDAO.addNewUserContract(addUserToContract.getUserId(), companyId, addUserToContract.getHourlyWage());

        } catch (SQLException e) {
            return Response.serverError().build();
        }


        return Response.ok().build();
    }

    @GET
    @Path("/contracts/{userContractId}")
    public UserContract getUserContract(@PathParam("userContractId") String userContractId) {
        if (userContractId == null) {
            return null;
        }

        UserContract result;
        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            result = userContractDAO.getUserContractById(userContractId);

        } catch (SQLException e) {
            return null;
        }
        return result;

    }

    @PUT
    @Path("/employees/{userContractId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateUserContract(@PathParam("userContractId") String userContractId, JAXBElement<UserContract> userContractJAXBElement) {
        if (userContractId == null) {
            return Response.status(400).build();
        }

        int hourlyWage = userContractJAXBElement.getValue().getHourlyWage();

        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            userContractDAO.changeHourlyWage(userContractId, hourlyWage);

        } catch (SQLException e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("/employees/{userContractId}")
    public Response deleteUserContract(@PathParam("userContractId") String userContractId) {
        if (userContractId == null) {
            return Response.status(400).build();
        }

        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            userContractDAO.disableUserContract(userContractId);

        } catch (SQLException e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }
}
