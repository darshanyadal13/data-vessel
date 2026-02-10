#!/bin/bash

# Run all services locally (no Docker required)
# Make sure MySQL is installed and running on localhost:3306

PROJECT_DIR=$(pwd)
PIDS_DIR="$PROJECT_DIR/.pids"
LOGS_DIR="$PROJECT_DIR/logs"

# Create directories if they don't exist
mkdir -p "$PIDS_DIR" "$LOGS_DIR"

echo "Starting all services..."
echo "====================================="
echo ""

# Start service-registry in background
echo "🚀 Starting service-registry on port 8761..."
cd "$PROJECT_DIR/backend/service-registry"
nohup mvn spring-boot:run > "$LOGS_DIR/service-registry.log" 2>&1 &
echo $! > "$PIDS_DIR/service-registry.pid"
cd "$PROJECT_DIR"
sleep 8

# Start api-gateway
echo "🚀 Starting api-gateway on port 8080..."
cd "$PROJECT_DIR/backend/api-gateway"
nohup mvn spring-boot:run > "$LOGS_DIR/api-gateway.log" 2>&1 &
echo $! > "$PIDS_DIR/api-gateway.pid"
cd "$PROJECT_DIR"
sleep 3

# Start user-service
echo "🚀 Starting user-service on port 8081..."
cd "$PROJECT_DIR/backend/user-service"
nohup mvn spring-boot:run > "$LOGS_DIR/user-service.log" 2>&1 &
echo $! > "$PIDS_DIR/user-service.pid"
cd "$PROJECT_DIR"
sleep 3

# Start file-service
echo "🚀 Starting file-service on port 8082..."
cd "$PROJECT_DIR/backend/file-service"
nohup mvn spring-boot:run > "$LOGS_DIR/file-service.log" 2>&1 &
echo $! > "$PIDS_DIR/file-service.pid"
cd "$PROJECT_DIR"
sleep 3

# Start admin-service
echo "🚀 Starting admin-service on port 8083..."
cd "$PROJECT_DIR/backend/admin-service"
nohup mvn spring-boot:run > "$LOGS_DIR/admin-service.log" 2>&1 &
echo $! > "$PIDS_DIR/admin-service.pid"
cd "$PROJECT_DIR"
sleep 3

# Start notification-service
echo "🚀 Starting notification-service on port 8084..."
cd "$PROJECT_DIR/backend/notification-service"
nohup mvn spring-boot:run > "$LOGS_DIR/notification-service.log" 2>&1 &
echo $! > "$PIDS_DIR/notification-service.pid"
cd "$PROJECT_DIR"

echo ""
echo "====================================="
echo "✅ All services started!"
echo "====================================="
echo ""
echo "Service endpoints:"
echo "  - Eureka Server: http://localhost:8761"
echo "  - API Gateway: http://localhost:8080"
echo "  - User Service: http://localhost:8081"
echo "  - File Service: http://localhost:8082"
echo "  - Admin Service: http://localhost:8083"
echo "  - Notification Service: http://localhost:8084"
echo ""
echo "To stop all services, run: ./stop-all.sh"
echo "To view logs: tail -f logs/<service-name>.log"
