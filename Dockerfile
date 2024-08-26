# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/SecurityConfig-0.0.1-SNAPSHOT.jar /app/SecurityConfig-0.0.1-SNAPSHOT.jar

# Expose the application port
EXPOSE 8080

# Run the JAR file
ENTRYPOINT ["java", "-jar", "/app/SecurityConfig-0.0.1-SNAPSHOT.jar"]
