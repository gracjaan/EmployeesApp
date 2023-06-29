package nl.earnit.test;


import io.zonky.test.db.postgres.junit5.EmbeddedPostgresExtension;
import io.zonky.test.db.postgres.junit5.SingleInstancePostgresExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDB {
    @RegisterExtension
    public SingleInstancePostgresExtension pg = EmbeddedPostgresExtension.singleInstance();

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
        System.out.println(res.getInt("count"));

        con.close();
    }
}
