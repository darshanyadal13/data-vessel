#!/bin/bash

# Data Vessel - Build All Services Script
# This script builds all microservices

echo "================================"
echo "Building Data Vessel Services"
echo "================================"

services=("service-registry" "user-service" "file-service" "admin-service" "notification-service" "api-gateway")

for service in "${services[@]}"; do
    echo ""
    echo "📦 Building $service..."
    echo "--------------------------------"
    cd backend/$service
    mvn clean package -DskipTests
    if [ $? -eq 0 ]; then
        echo "✅ $service built successfully"
    else
        echo "❌ $service build failed"
        exit 1
    fi
    cd ../..
done

echo ""
echo "================================"
echo "✅ All services built successfully!"
echo "================================"
echo ""
echo "Next steps:"
echo "1. Update .env file with your credentials"
echo "2. Run: docker-compose up -d"
echo "3. Access Eureka at: http://localhost:8761"
echo "4. Access API Gateway at: http://localhost:8080"
