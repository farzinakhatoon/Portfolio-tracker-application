# Use an official Java runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven jar file into the container at /app
COPY target/portfolio-tracker-1.0.0.jar /app/portfolio-tracker.jar

# Expose the port the app will run on
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "portfolio-tracker.jar"]
