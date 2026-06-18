package org.prog.api;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.sql.*;
@Slf4j

public class TestClass {
    private Connection connection;
    private RestExecute restExecute;
    private DbExecute dbExecute;

    @BeforeSuite
    public void beforeSuite() throws SQLException {
        String url = System.getenv("Db_Url");
        String user = System.getenv("Db_User");
        String password = System.getenv("Db_Password");
        connection = DriverManager.getConnection(url, user, password);
        restExecute = new RestExecute();
        dbExecute = new DbExecute();
        dbExecute.setConnection(connection);
    }

    @Test
        public void restSqlTest() throws SQLException {
        ResultDto dto = restExecute.getUsers(20);

        int entrycount = dbExecute.countPersons();
        int insertedMalesCount = dbExecute.setValuesToDb(dto);
        int postInsertCount = dbExecute.countPersons();
        Assert.assertEquals(postInsertCount - entrycount, insertedMalesCount,
                "The number of rows in DB doesn't match!");
        Assert.assertTrue(dbExecute.getFemale().isEmpty(), "Error: female found in DB");
    }

    @AfterSuite
    public void afterSuite() throws SQLException {
        if (connection != null) {
            try (PreparedStatement truncateStatement =
                         connection.prepareStatement("Truncate Table Persons")) {
                truncateStatement.execute();
            }
            connection.close();
        }
    }
}