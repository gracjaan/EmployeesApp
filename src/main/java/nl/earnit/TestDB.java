package nl.earnit;


import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDB {
    private final SingleInstancePostgresExtension pg;

    public TestDB(SingleInstancePostgresExtension pg) throws Exception {
        this.pg = pg;
        setupDB();
    }

    public Connection getConnection() throws SQLException {
        return pg.getEmbeddedPostgres().getPostgresDatabase().getConnection();
    }

    public void setupDB() throws Exception {
        Connection con = getConnection();

        // Load db schema
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL dbSchema = classLoader.getResource("db-structure-v2.4.sql");
        if (dbSchema == null) throw new Exception("Could not load db schema file");
        System.out.println("Loaded db schema");

        System.out.println("Importing db schema");
        String importQuery = Files.readString(Path.of(dbSchema.toURI()), StandardCharsets.UTF_8);
        PreparedStatement importStatement = con.prepareStatement(importQuery);
        importStatement.execute();
        System.out.println("Imported db schema");

    }

    @Test
    public void TestDB() throws Exception {
        setupDB();

        Connection con = getConnection();

        // Test if loaded
        String query = "SELECT COUNT(*) as count FROM \"user\"";
        PreparedStatement statement = con.prepareStatement(query);
        ResultSet res = statement.executeQuery();

        if (!res.next()) throw new Exception("Could not load db schema into db");
        System.out.println("Successfully loaded db: " + res.getInt("count") + " rows in db");

        con.close();
    }
}
