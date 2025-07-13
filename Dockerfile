# Use an official OpenJDK runtime as a parent image
FROM amazoncorretto:17-alpine

# Set timezone (adjust as needed)
ENV TZ=UTC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring

WORKDIR /app
COPY --chown=spring:spring target/*.jar app.jar

# Set the working directory in the container
#WORKDIR /app

# Copy the JAR file from the host's target directory into the container
# Replace 'your-application.jar' with your actual JAR filename pattern
#COPY target/*.jar app.jar

# Expose the port your app runs on (default Spring Boot port is 8080)
EXPOSE 9090

# Run the JAR file when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]