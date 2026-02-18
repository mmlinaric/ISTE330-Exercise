# ISTE330 Exercise

A Java application that connects to a MySQL database and performs CRUD operations on the Equipment table using a persistence layer pattern.

## Prerequisites

- Docker and Docker Compose

## Setup

1. Copy the example environment file and adjust values if needed:

```
cp .env.example .env
```

2. Build and run the application:

```
docker compose up --build
```

This will start a MySQL database container, initialize it with the `travel23` schema, build the Java application, and run it.

## Stopping

```
docker compose down
```

To also remove the database volume (resets all data):

```
docker compose down -v
```

## Project Structure

```
src/main/java/com/example/
  Main.java           - Entry point; tests all Equipment CRUD operations
  MySQLDatabase.java  - Database connection and query methods (getData, setData)
  Equipment.java      - Persistence layer mirroring the equipment table
  DLException.java    - Custom exception for SQL-related errors
init/
  travel.sql          - SQL script to initialize the travel23 database
```
