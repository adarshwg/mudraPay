# Use the proper image reference
FROM docker.io/library/openjdk:23-slim

# Set the working directory
WORKDIR /app

# Copy the JAR and .env file
COPY target/MudraPay-1.0-SNAPSHOT-jar-with-dependencies.jar /app/MudraPay.jar
COPY src/main/resources/.env /app/.env

# Expose the port
EXPOSE 8000

# Run the application
CMD ["java", "-jar", "MudraPay.jar"]
