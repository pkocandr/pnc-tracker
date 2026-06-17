# PNC Tracker

Service for tracking content access events and allowing retrieval of that data.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

Kudos to the Indy team for working on Indy Tracking Service, the initial version of this before the PNC fork.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

**_NOTE:_**  Quarkus ships with a Dev UI, available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application is pre-configured to build as an Über-JAR (via application.yaml). To package it, simply run:

```shell script
./mvnw package
```
This produces the single executable JAR file `pnc-tracker-runner.jar` directly in the `target/` directory.

You can run the packaged application using:

```shell script
java -jar target/pnc-tracker-runner.jar
```

## Creating a native executable

You can create a native executable (compiled via GraalVM) using:

```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed locally, you can run the native build inside a container:

```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute the native binary with:

```shell script
./target/pnc-tracker-runner
```

## Architecture & Technology Stack

### Quarkus REST (Jackson)

The service utilizes the modern Quarkus REST extension (reactive JAX-RS engine) combined with Jackson for high-performance JSON serialization and deserialization.

### Panache (PostgreSQL)

Data persistence is handled by Hibernate ORM with Panache, using decoupled entities (DbTrackingReport and DbTrackingEntry) linked dynamically by IDs to maximize performance and avoid JPA relational pitfalls.
