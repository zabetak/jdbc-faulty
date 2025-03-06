/*
·*·Licensed·to·the·Apache·Software·Foundation·(ASF)·under·one·or·more
·*·contributor·license·agreements.··See·the·NOTICE·file·distributed·with
·*·this·work·for·additional·information·regarding·copyright·ownership.
·*·The·ASF·licenses·this·file·to·you·under·the·Apache·License,·Version·2.0
·*·(the·"License");·you·may·not·use·this·file·except·in·compliance·with
·*·the·License.··You·may·obtain·a·copy·of·the·License·at
·*
·*·http://www.apache.org/licenses/LICENSE-2.0
·*
·*·Unless·required·by·applicable·law·or·agreed·to·in·writing,·software
·*·distributed·under·the·License·is·distributed·on·an·"AS·IS"·BASIS,
·*·WITHOUT·WARRANTIES·OR·CONDITIONS·OF·ANY·KIND,·either·express·or·implied.
·*·See·the·License·for·the·specific·language·governing·permissions·and
·*·limitations·under·the·License.
·*/
package com.github.zabetak.jdbc.faulty.postgres;

import com.github.zabetak.jdbc.faulty.FaultyProperty;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class TestFaultyPostgresConnection {
  private static final PostgreSQLContainer POSTGRES =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

  @BeforeAll
  static void setup() {
    POSTGRES.start();
  }

  @Test
  void testRealConnection() throws SQLException {
    try (Connection c =
        DriverManager.getConnection(
            POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
      PreparedStatement stmt = c.prepareStatement("CREATE TABLE emps (eid INTEGER, ename VARCHAR)");
      stmt.execute();
      stmt.close();
    }
  }

  @Test
  void testFaultyConnection() throws SQLException {
    System.setProperty(FaultyProperty.FAILURE_METHOD.key(), "prepareStatement");
    System.setProperty(FaultyProperty.FAILURE_PERCENTAGE.key(), "1.0");
    try (Connection c =
        DriverManager.getConnection(
            faultyURL(POSTGRES.getJdbcUrl()), POSTGRES.getUsername(), POSTGRES.getPassword())) {
      Assertions.assertThrows(
          SQLException.class,
          () -> c.prepareStatement("CREATE TABLE emps (eid INTEGER, ename VARCHAR)"),
          "Randomly generated failure");
    }
  }

  private static String faultyURL(String realURL) {
    return "jdbc:faulty:" + realURL.replace("jdbc:", "");
  }
}
