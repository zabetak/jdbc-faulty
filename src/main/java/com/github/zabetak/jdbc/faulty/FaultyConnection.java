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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

public class FaultyConnection implements InvocationHandler {
  private final Connection realConnection;
  private final Random random = new Random(Long.parseLong(FaultyProperty.RANDOM_SEED.value()));
  private final double failurePercentage;
  private final String failureMethod;

  public FaultyConnection(Connection realConnection) {
    this.realConnection = realConnection;
    this.failurePercentage = Double.parseDouble(FaultyProperty.FAILURE_PERCENTAGE.value());
    this.failureMethod = FaultyProperty.FAILURE_METHOD.value();
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (failureMethod.equals(method.getName())) {
      if (random.nextDouble() < failurePercentage) {
        throw new SQLException("Randomly generated failure");
      }
    }
    return method.invoke(realConnection, args);
  }
}
