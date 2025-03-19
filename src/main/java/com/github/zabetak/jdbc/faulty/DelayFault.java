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

import java.sql.SQLException;
import java.util.Objects;

public class DelayFault extends Fault {
  private final long delayMs;

  public DelayFault(double rate, String method, long delayMs) {
    super(rate, method);
    this.delayMs = delayMs;
  }

  @Override
  public void apply() throws SQLException {
    try {
      Thread.sleep(delayMs);
    } catch (InterruptedException e) {
      throw new SQLException("Interrupted while applying artificial delay.", e);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    DelayFault that = (DelayFault) o;
    return delayMs == that.delayMs;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delayMs);
  }
}
