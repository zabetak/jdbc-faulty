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

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

public abstract class Fault {
  public enum Type {
    EXCEPTION,
    DELAY
  }

  private static final Random RANDOM =
      new Random(Long.parseLong(FaultyProperty.RANDOM_SEED.value()));
  private final double rate;
  private final String method;

  public Fault(double rate, String method) {
    this.rate = rate;
    this.method = method;
  }

  /**
   * Checks if the fault applies on the given method.
   *
   * @param method the method that is currently executed.
   * @return true if the fault applies on the method and false otherwise.
   */
  public boolean check(Method method) {
    return this.method.equals(method.getName()) && RANDOM.nextDouble() <= rate;
  }

  public abstract void apply() throws SQLException;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    Fault fault = (Fault) o;
    return Double.compare(rate, fault.rate) == 0 && Objects.equals(method, fault.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rate, method);
  }

  @Override
  public String toString() {
    return "Fault{" + "rate=" + rate + ", method='" + method + '\'' + '}';
  }
}
