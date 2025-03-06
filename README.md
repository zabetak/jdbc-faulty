# Faulty JDBC driver

A JDBC driver implementation that wraps around a real driver and makes it faulty by introducing random failures while
using a `java.sql.Connection`.

To make a JDBC driver faulty it suffices to add `jdbc-faulty-*.jar` in the classpath of your application and
use the `jdbc:faulty:` prefix before the JDBC url of the real connection.

```
jdbc:postgresql://0.0.0.0:5432/mydb -> jdbc:faulty:postgresql://0.0.0.0:5432/mydb
```

It allows for some basic configuration via System properties.
For the available options and their default values check: `com.github.zabetak.jdbc.faulty.FaultyProperty`.

## Build

### Requirements

* JDK >= 11
* Maven >= 3.6.3

```
mvn clean install
```

The driver jar will be created under the generated `target` directory.
