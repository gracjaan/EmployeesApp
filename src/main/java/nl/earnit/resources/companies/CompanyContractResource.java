package nl.earnit.resources.companies;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.xml.bind.JAXBElement;
import nl.earnit.dao.ContractDAO;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.models.UserContract;
import nl.earnit.dto.company.AddUserToContractDTO;

import nl.earnit.dto.contracts.ContractInformationDTO;


import java.util.List;

/* TODO: 5/31/2023 Change delete methods to disable when the database schema is updated
                   Add @Produces to relevant methods after merge
 */

/**
 * The type Company contract resource.
 */
public class CompanyContractResource {
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
    private final String companyId;
    private final String contractId;

    /**
     * Instantiates a new Company contract resource.
     *
     * @param uriInfo    the uri info
     * @param request    the request
     * @param companyId  the company id
     * @param contractId the contract id
     */
    public CompanyContractResource(UriInfo uriInfo, Request request, String companyId,
                                   String contractId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.companyId = companyId;
        this.contractId = contractId;
    }

    /**
     * Gets contract.
     *
     * @param contractId the contract id
     * @return the contract
     */
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ContractInformationDTO getContract(@PathParam("contractId") String contractId) {
        ContractInformationDTO result = new ContractInformationDTO();

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

    /**
     * Update contract response.
     *
     * @param contractId      the contract id
     * @param descriptionRole the description role
     * @return the response
     */
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response updateContract(@PathParam("contractId") String contractId, JAXBElement<ContractInformationDTO> descriptionRole ) {
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

    /**
     * Disable contract response.
     *
     * @param contractId the contract id
     * @return the response
     */
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

    /**
     * Gets user contracts.
     *
     * @param contractId the contract id
     * @return the user contracts
     */
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

    /**
     * Add employee response.
     *
     * @param addUserToContractDTO the add user to contract
     * @return the response
     */
    @POST
    @Path("/employees")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response addEmployee(AddUserToContractDTO addUserToContractDTO) {

        if (addUserToContractDTO == null || addUserToContractDTO.getHourlyWage() <= 0 || addUserToContractDTO.getUserId() == null) {
            return Response.status(400).build();
        }


        try {
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            return Response.ok(userContractDAO.addNewUserContract(addUserToContractDTO.getUserId(), contractId, addUserToContractDTO.getHourlyWage())).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    /**
     * Gets user contract.
     *
     * @param userContractId the user contract id
     * @return the user contract
     */
    @GET
    @Path("/contracts/{userContractId}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public UserContract getUserContract(@PathParam("userContractId") String userContractId) {
        if (userContractId == null) {
            return null;
        }

        UserContract result;
        try {
            ContractDAO contractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);
            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            if (!contractDAO.hasContractAccessToUserContract(contractId, userContractId)) {
                throw new ForbiddenException();
            }

            result = userContractDAO.getUserContractById(userContractId);

        } catch (ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
        return result;

    }

    /**
     * Update user contract response.
     *
     * @param userContractId          the user contract id
     * @param userContractJAXBElement the user contract jaxb element
     * @return the response
     */
    @PUT
    @Path("/employees/{userContractId}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response updateUserContract(@PathParam("userContractId") String userContractId, JAXBElement<UserContract> userContractJAXBElement) {
        if (userContractId == null) {
            return Response.status(400).build();
        }

        int hourlyWage = userContractJAXBElement.getValue().getHourlyWage();

        try {
            ContractDAO contractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);

            if (!contractDAO.hasContractAccessToUserContract(contractId, userContractId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);

            userContractDAO.changeHourlyWage(userContractId, hourlyWage);

        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

    /**
     * Disable user contract response.
     *
     * @param userContractId the user contract id
     * @return the response
     */
    @DELETE
    @Path("/employees/{userContractId}")
    public Response disableUserContract(@PathParam("userContractId") String userContractId) {
        if (userContractId == null) {
            return Response.status(400).build();
        }

        try {
            ContractDAO contractDAO = (ContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.CONTRACT);

            if (!contractDAO.hasContractAccessToUserContract(contractId, userContractId)) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }

            UserContractDAO userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
            userContractDAO.disableUserContract(userContractId);
        } catch (Exception e) {
            return Response.serverError().build();
        }

        return Response.ok().build();
    }
}
