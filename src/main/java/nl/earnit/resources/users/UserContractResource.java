package nl.earnit.resources.users;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import nl.earnit.dao.DAOManager;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.dao.UserDAO;
import nl.earnit.dao.WorkedDAO;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.db.Worked;

import javax.print.attribute.standard.Media;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserContractResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;
    private final String userId;
    private final String userContractId;

    public UserContractResource(UriInfo uriInfo, Request request, String userId, String userContractId) {
        this.uriInfo = uriInfo;
        this.request = request;
        this.userId = userId;
        this.userContractId = userContractId;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getContract() {
        UserContractDAO userContractDAO;
        UserContract uc;
        try {
            userContractDAO = (UserContractDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.USER_CONTRACT);
            uc = userContractDAO.getUserContract(this.userId, this.userContractId);
        } catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok(uc).build();
    }

    @GET
    @Path("/worked")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getWorked() {
        WorkedDAO workedDAO;
        List<Worked> workedList;
        try {
            workedDAO = (WorkedDAO) DAOManager.getInstance().getDAO(DAOManager.DAO.WORKED);
            workedList = workedDAO.getWorkedHours(userContractId);
        } catch (SQLException e) {
            return Response.serverError().build();
        }
        return Response.ok(workedList).build();
    }

    @Path("/worked/{year}/{week}")
    public UserContractWorkedResource getWorkedWeek(@PathParam("year") String year, @PathParam("week") String week) {
        return new UserContractWorkedResource(uriInfo, request, userId, userContractId, year, week);
    }

    @Path("/worked/{weekId}")
    public UserContractWorkedResource getWorkedWeek(@PathParam("weekId") String weekId) {
        return new UserContractWorkedResource(uriInfo, request, userId, userContractId, weekId);
    }
}
