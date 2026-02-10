@echo off
REM Data Vessel - Build All Services Script (Windows)
REM This script builds all microservices

echo ================================
echo Building Data Vessel Services
echo ================================

set services=service-registry user-service file-service admin-service notification-service api-gateway

for %%s in (%services%) do (
    echo.
    echo Building %%s...
    echo --------------------------------
    cd backend\%%s
    call mvn clean package -DskipTests
    if errorlevel 1 (
        echo Build failed for %%s
        exit /b 1
    )
    echo %%s built successfully
    cd ..\..
)

echo.
echo ================================
echo All services built successfully!
echo ================================
echo.
echo Next steps:
echo 1. Update .env file with your credentials
echo 2. Run: docker-compose up -d
echo 3. Access Eureka at: http://localhost:8761
echo 4. Access API Gateway at: http://localhost:8080
