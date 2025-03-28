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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * A JDBC driver wrapper that proxies a real connection and generates faults during various stages.
 */
public class FaultyJDBCDriver implements Driver {
  private static final String JDBC_PREFIX = "jdbc:faulty:";
  static final Map<String, Fault> FAULTS = new ConcurrentHashMap<>();

  static {
    try {
      DriverManager.registerDriver(new FaultyJDBCDriver());
    } catch (SQLException e) {
      throw new RuntimeException("Failed to register " + FaultyJDBCDriver.class.getSimpleName(), e);
    }
    loadFaultsFromClasspath("jdbc-faulty.properties");
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

  public static void addFault(String name, Fault fault) {
    FAULTS.put(name, fault);
  }

  public static void clearFaults() {
    FAULTS.clear();
  }

  static void loadFaultsFromClasspath(String resourcePath) {
    URL propertyFile = FaultyJDBCDriver.class.getClassLoader().getResource(resourcePath);
    if (propertyFile != null) {
      try (InputStream is = propertyFile.openStream()) {
        Properties properties = new Properties();
        properties.load(is);
        FaultParser parser = new FaultPropertyParser(properties);
        FAULTS.putAll(parser.parse());
      } catch (IOException e) {
        // Failed to open stream but not a big deal ignore
      }
    }
  }
}
