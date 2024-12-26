# Use an official openjdk runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container at /app
COPY target/portfolio-tracker-1.0.0.jar /app/portfolio-tracker.jar

# Expose the port that the Spring Boot app will run on (default is 8080)
EXPOSE 8080

# Run the Spring Boot application JAR file
ENTRYPOINT ["java", "-jar", "/app/portfolio-tracker.jar"]

# Alternatively, if you want to ensure the main class is explicitly set in the Dockerfile, you can add this (although not needed for most Spring Boot apps with proper manifest)
# ENTRYPOINT ["java", "-cp", "/app/portfolio-tracker.jar", "com.portfolio.PortfolioTrackerApplication"]
