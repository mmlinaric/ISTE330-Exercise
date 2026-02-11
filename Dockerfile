# Use an official Maven image with Amazon Corretto to build the application
FROM maven:3.9.6-amazoncorretto-21 AS build

# Set the working directory
WORKDIR /app

# Copy the pom.xml file
COPY pom.xml .

# Download all dependencies
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the application
RUN mvn package

# Use an official Amazon Corretto image to run the application
FROM amazoncorretto:21

# Set the working directory
WORKDIR /app

# Copy the executable JAR from the build stage
COPY --from=build /app/target/mysql-connector-example-1.0-SNAPSHOT.jar .

# Run the application
CMD ["java", "-jar", "mysql-connector-example-1.0-SNAPSHOT.jar"]
