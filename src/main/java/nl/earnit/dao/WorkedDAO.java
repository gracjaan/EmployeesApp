package nl.earnit.dao;

import nl.earnit.models.db.User;

import java.sql.Connection;
import java.sql.SQLException;

public class WorkedDAO extends GenericDAO<User> {

    private final static String TABLE_NAME = "worked";

    public WorkedDAO(Connection con) {
        super(con, TABLE_NAME);
    }


    // create and execute the necessary queries
    @Override
    public int count() throws SQLException {
        return 0;
    }
}
