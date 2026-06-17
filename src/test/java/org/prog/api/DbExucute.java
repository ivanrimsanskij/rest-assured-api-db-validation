package org.prog.api;

import lombok.extern.slf4j.Slf4j;
import java.sql.*;
@Slf4j

public class DbExucute {
    private Connection connection;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public int setValuesToDb(ResultDto dto) throws SQLException {
        int insertedMalesCount = 0;

       try (PreparedStatement preparedStatement = connection.prepareStatement(
                   "INSERT INTO Persons (FirstName, LastName, Gender, Title, Nat) " +
                           "VALUES (?, ?, ?, ?, ?)")) {

           for (PersonDto d : dto.getResults()) {

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
               }
           }

           return insertedMalesCount;
       }
    }

    public int countPersons() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("Select Count(*) From Persons")) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }
}
