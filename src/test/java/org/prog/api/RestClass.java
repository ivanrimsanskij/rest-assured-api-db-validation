package org.prog.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.sql.*;
@Slf4j

public class RestClass {
    private Connection connection;
    private Statement statement;

    @BeforeSuite
    public void beforeSuite() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/db";
        String user = System.getenv("Db_User");
        String password = System.getenv("Db_Password");
        connection = DriverManager.getConnection(url, user, password);
    }

    @Test
        public void restSqlTest() throws SQLException {
        statement = connection.createStatement();
        int insertedMalesCount = 0;
        int entrycount = countPersons(statement);

        RequestSpecification requestSpecification = RestAssured.given();
        String apiBaseUrl = System.getenv("Api_Base_Url");
        String apiBasePath = System.getenv("Api_Base_Path");
        requestSpecification.baseUri(apiBaseUrl);
        requestSpecification.basePath(apiBasePath);
        requestSpecification.header("Content-Type", "application/json; charset=UTF-8");
        requestSpecification.queryParam("nat", "fi");
        requestSpecification.queryParam("results", 20);

        Response response = requestSpecification.get();
        Assert.assertEquals(response.getStatusCode(), 200, "API request failed!");

        ResultDto dto = response.as(ResultDto.class);
        Assert.assertNotNull(dto.getResults(), "Results list in DTO is null");

        PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO Persons (FirstName, LastName, Gender, Title, Nat) " +
                        "VALUES (?, ?, ?, ?, ?)");

        for (PersonDto d: dto.getResults()) {

            if (d.getGender().equals("female")) {
                continue;
            }

            try {
                preparedStatement.setString(1, d.getName().getFirst());
                preparedStatement.setString(2, d.getName().getLast());
                preparedStatement.setString(3, d.getGender());
                preparedStatement.setString(4, d.getName().getTitle());
                preparedStatement.setString(5, d.getNat());
                preparedStatement.execute();

                insertedMalesCount++;

                log.info("Male user added to DB " + insertedMalesCount);

            } catch (SQLException e) {
                log.error("Failed to insert user into DB", e);
                Assert.fail("Test failed due to database exception: " + e.getMessage());
            }
        }

        int postInsertCount = countPersons(statement);

        Assert.assertEquals(postInsertCount - entrycount, insertedMalesCount);
    }

    private int countPersons(Statement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM Persons")) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    @AfterSuite
    public void afterSuite() throws SQLException {
        if (connection != null) {
                PreparedStatement truncateStatement =
                        connection.prepareStatement("Truncate Table Persons");
            truncateStatement.execute();
            connection.close();
        }
    }
}