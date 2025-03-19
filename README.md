# Faulty JDBC driver

A JDBC driver implementation that wraps around a real driver and makes it faulty by introducing random faults while
using a `java.sql.Connection`.

## Download

The JDBC driver can be found in [Maven central](https://central.sonatype.com/artifact/com.github.zabetak/jdbc-faulty/overview).

```
<dependency>
    <groupId>com.github.zabetak</groupId>
    <artifactId>jdbc-faulty</artifactId>
    <version>1.0</version>
</dependency>
```

and works with Java >= 1.8.

## Usage

To make a JDBC driver faulty it suffices to add `jdbc-faulty-*.jar` in the classpath of your application and
use the `jdbc:faulty:` prefix before the JDBC url of the real connection.

```
jdbc:postgresql://0.0.0.0:5432/mydb -> jdbc:faulty:postgresql://0.0.0.0:5432/mydb
```

## Configuration

There are two kind of faults that are supported at the moment:
* `ExceptionFault`, raises a `SQLException` randomly when a specific `Connection` method is invoked;
* `DelayFault`, incurs a configurable delay before a specific `Connection` method is invoked.

Check the respective classes for all available configuration attributes.

### Programmatic Java API

The faults can be added/removed programmatically manually by calling the `FaultyJDBCDriver#addFault` and `FaultyJDBCDriver#clearFaults` APIs.

### Properties file

The faults can be loaded from the `jdbc-faulty.properties` file if the latter is present in the classpath.

```properties
fault.f1.type=exception
fault.f1.rate=0.5
fault.f1.method=commit
fault.f2.type=delay
fault.f2.rate=0.01
fault.f2.method=commit
fault.f2.delay=4000
```

The snippet above defines two faults:
* `f1` that is an `ExceptionFault` and raised a `SQLException` 50% of the time when `Connection#commit` is invoked.
* `f2` that is a `DelayFault` fault and incurs a 4000ms delay 1% of the time when `Connection#commit` is invoked.

## Build

### Requirements

* JDK >= 11
* Maven >= 3.6.3

```
mvn clean install
```

The driver jar will be created under the generated `target` directory.
