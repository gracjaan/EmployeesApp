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
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public DescriptionRole getContract(@PathParam("contractId") String contractId) {
        DescriptionRole result = new DescriptionRole();

        if (contractId == null) {
            return null;
        }

        try {
            ContractDAO contractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);

            result.setDescription(contractDAO.getContract(contractId).getDescription());
            result.setRole(contractDAO.getContract(contractId).getRole());

        } catch (Exception e) {
            return null;
        }
        return result;
    }

    @PUT
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

        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @DELETE
    public Response disableContract(@PathParam("contractId") String contractId) {
        if (contractId == null) {
            return Response.status(400).build();
        }

        try {
            ContractDAO contractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);
            contractDAO.disableContract(contractId);

        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @GET
    @Path("/employees")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<UserContract> getUserContracts(@PathParam("contractId") String contractId) {
        if (contractId == null) {
            return null;
        }

        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
             return userContractDAO.getUserContractsByContractId(contractId);

        } catch (Exception e) {
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

            return Response.ok(userContractDAO.addNewUserContract(addUserToContract.getUserId(), contractId, addUserToContract.getHourlyWage())).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/contracts/{userContractId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public UserContract getUserContract(@PathParam("userContractId") String userContractId) {
        if (userContractId == null) {
            return null;
        }

        UserContract result;
        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            result = userContractDAO.getUserContractById(userContractId);

        } catch (Exception e) {
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

        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    @DELETE
    @Path("/employees/{userContractId}")
    public Response disableUserContract(@PathParam("userContractId") String userContractId) {
        if (userContractId == null) {
            return Response.status(400).build();
        }

        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            userContractDAO.disableUserContract(userContractId);

        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }
}
