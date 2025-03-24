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
package com.github.zabetak.jdbc.faulty;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

public class TestFaultyConnection {

  private static final String DERBY_URL = "jdbc:derby:memory:TestFaultyConnectionDB;create=true";

  @Test
  void testInvokeThrowsSQLExceptionWhenMethodCallThrows() throws Throwable {
    Method prepareMethod = Connection.class.getMethod("prepareStatement", String.class);
    try (Connection real = DriverManager.getConnection(DERBY_URL)) {
      FaultyConnection faultyConnection = new FaultyConnection(real);
      assertThrows(
          SQLException.class,
          () ->
              faultyConnection.invoke(
                  null, prepareMethod, new Object[] {"SELECT * FROM missing_table"}));
    }
  }
}
