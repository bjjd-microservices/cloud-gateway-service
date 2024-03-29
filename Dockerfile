# Use a minimal OpenJDK image as the base
FROM openjdk:11-jre-slim-buster

# Set the working directory
WORKDIR /app

# Copy the application code and any other necessary files
COPY target/cloud-gateway-service-*.jar /app

# Expose the port on which the application will listen
EXPOSE 8181

# Set the entry point
CMD ["java","-Dspring.profiles.active=kubernetes","-jar","/app/cloud-gateway-service-0.0.1.RELEASE.jar"]