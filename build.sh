#!/bin/bash
# Build script for Render deployment

echo "Starting build process..."

# Build the application
./mvnw clean package -DskipTests

echo "Build completed successfully!"
