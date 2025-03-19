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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class TestFaultPropertyParser {
  @Test
  void testParseExceptionFault() {
    Properties properties = new Properties();
    properties.put("fault.f1.type", "exception");
    properties.put("fault.f1.rate", "0.5");
    properties.put("fault.f1.method", "commit");
    FaultPropertyParser parser = new FaultPropertyParser(properties);
    Map<String, Fault> r = parser.parse();
    assertEquals(1, r.size());
    assertEquals(new ExceptionFault(0.5, "commit"), r.get("f1"));
  }

  @Test
  void testParseDelayFault() {
    Properties properties = new Properties();
    properties.put("fault.f2.type", "delay");
    properties.put("fault.f2.rate", "0.8");
    properties.put("fault.f2.method", "prepareStatement");
    properties.put("fault.f2.delay", "3600");
    FaultPropertyParser parser = new FaultPropertyParser(properties);
    Map<String, Fault> r = parser.parse();
    assertEquals(1, r.size());
    assertEquals(new DelayFault(0.8, "prepareStatement", 3600), r.get("f2"));
  }
}
