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

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * A JDBC driver wrapper that proxies a real connection and generates exceptions/errors during
 * various stages.
 */
public class FaultyJDBCDriver implements Driver {
  private static final String JDBC_PREFIX = "jdbc:faulty:";

  static {
    try {
      DriverManager.registerDriver(new FaultyJDBCDriver());
    } catch (SQLException e) {
      throw new RuntimeException("Failed to register " + FaultyJDBCDriver.class.getSimpleName(), e);
    }
  }

  @Override
  public boolean acceptsURL(String url) {
    return url.startsWith(JDBC_PREFIX);
  }

  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    if (!acceptsURL(url)) {
      return null; // Let other drivers handle it
    }
    String realUrl = url.replace(JDBC_PREFIX, "jdbc:");
    Connection realConnection = DriverManager.getConnection(realUrl, info);
    return wrapConnection(realConnection);
  }

  private Connection wrapConnection(Connection realConnection) {
    return (Connection)
        Proxy.newProxyInstance(
            realConnection.getClass().getClassLoader(),
            new Class<?>[] {Connection.class},
            new FaultyConnection(realConnection));
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
    return new DriverPropertyInfo[0];
  }

  @Override
  public int getMajorVersion() {
    return 1;
  }

  @Override
  public int getMinorVersion() {
    return 0;
  }

  @Override
  public boolean jdbcCompliant() {
    return false;
  }

  @Override
  public Logger getParentLogger() {
    return Logger.getLogger(FaultyJDBCDriver.class.getName());
  }
}
