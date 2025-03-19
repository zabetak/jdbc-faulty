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

import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class FaultPropertyParser implements FaultParser {

  private final Properties properties;

  FaultPropertyParser(Properties properties) {
    this.properties = properties;
  }

  public Map<String, Fault> parse() {
    return properties.stringPropertyNames().stream()
        .map(key -> key.split("\\."))
        .collect(Collectors.groupingBy(parts -> parts[1]))
        .entrySet()
        .stream()
        .collect(
            Collectors.toMap(Map.Entry::getKey, entry -> createFault(entry.getKey(), properties)));
  }

  private static Fault createFault(String faultId, Properties properties) {
    String prefix = "fault." + faultId + ".";
    String type = properties.getProperty(prefix + "type", "exception");
    double rate = Double.parseDouble(properties.getProperty(prefix + "rate", "0.5"));
    String method = properties.getProperty(prefix + "method", "commit");
    long delay = Long.parseLong(properties.getProperty(prefix + "delay", "1000"));

    switch (Fault.Type.valueOf(type.toUpperCase())) {
      case DELAY:
        return new DelayFault(rate, method, delay);
      case EXCEPTION:
        return new ExceptionFault(rate, method);
      default:
        throw new IllegalArgumentException("Unknown fault type: " + type);
    }
  }
}
