#!/bin/bash
# Start script for Render deployment

echo "Starting Brainstorm application..."

# Set production profile
export SPRING_PROFILES_ACTIVE=prod

# Start the application
java -jar target/Brainstorm-0.0.1-SNAPSHOT.jar
