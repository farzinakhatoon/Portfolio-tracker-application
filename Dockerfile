# Use an official OpenJDK base image
FROM openjdk:21-jdk-slim as build

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the entire project and build it
COPY . .
RUN mvn clean package -DskipTests

# Use OpenJDK as the runtime environment
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/target/portfolio-tracker-1.0.0.jar /app/portfolio-tracker.jar

# Expose the port that the Spring Boot app will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/portfolio-tracker.jar"]
