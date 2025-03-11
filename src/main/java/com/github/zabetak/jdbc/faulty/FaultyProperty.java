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

public enum FaultyProperty {
  RANDOM_SEED("random.seed", "13"),
  FAILURE_PERCENTAGE("failure.percentage", "0.5"),
  FAILURE_METHOD("failure.method", "commit");
  final String propertyName;
  final String defaultValue;

  FaultyProperty(String s, String def) {
    this.propertyName = s;
    this.defaultValue = def;
  }

  public String key() {
    return "jdbc.faulty." + this.propertyName;
  }

  String value() {
    return System.getProperty("jdbc.faulty." + this.propertyName, this.defaultValue);
  }
}
