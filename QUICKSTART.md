# Data Vessel - Quick Start Guide

This guide will help you get Data Vessel up and running quickly.

## Prerequisites Checklist

- [ ] Java 17 installed
- [ ] Maven 3.6+ installed
- [ ] Docker and Docker Compose installed
- [ ] AWS Account (optional, for S3 storage)
- [ ] Gmail Account (optional, for email notifications)

## Step-by-Step Setup

### Step 1: Clone and Navigate

```bash
cd data-vessel
```

### Step 2: Configure Environment

1. Copy the environment template:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` with your credentials (optional for initial testing):
   ```env
   # AWS S3 Configuration (optional)
   AWS_S3_BUCKET_NAME=data-vessel-bucket
   AWS_S3_REGION=us-east-1
   AWS_ACCESS_KEY_ID=your-access-key
   AWS_SECRET_ACCESS_KEY=your-secret-key

   # Email Configuration (optional)
   MAIL_HOST=smtp.gmail.com
   MAIL_PORT=587
   MAIL_USERNAME=your-email@gmail.com
   MAIL_PASSWORD=your-app-password
   ```

### Step 3: Build All Services

**On Linux/Mac:**
```bash
chmod +x build-all.sh
./build-all.sh
```

**On Windows:**
```bash
build-all.bat
```

**Or manually:**
```bash
cd backend/service-registry && mvn clean package -DskipTests && cd ../..
cd backend/user-service && mvn clean package -DskipTests && cd ../..
cd backend/file-service && mvn clean package -DskipTests && cd ../..
cd backend/admin-service && mvn clean package -DskipTests && cd ../..
cd backend/notification-service && mvn clean package -DskipTests && cd ../..
cd backend/api-gateway && mvn clean package -DskipTests && cd ../..
```

### Step 4: Start Services with Docker

```bash
docker-compose up -d
```

This will start:
- MySQL Database (Port 3306)
- Service Registry - Eureka (Port 8761)
- User Service (Port 8081)
- File Service (Port 8082)
- Admin Service (Port 8083)
- Notification Service (Port 8084)
- API Gateway (Port 8080)

### Step 5: Verify Services

1. Check all containers are running:
   ```bash
   docker-compose ps
   ```

2. Access Eureka Dashboard:
   ```
   http://localhost:8761
   ```
   You should see all services registered.

3. Check API Gateway:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

## Testing the Application

### 1. Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "email": "john@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

**Expected Response:**
```json
{
  "statusCode": 201,
  "message": "User Registered",
  "data": {
    "id": 1,
    "username": "john",
    "email": "john@example.com",
    "role": "USER",
    "active": true
  }
}
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful"
}
```

**Copy the token for next requests!**

### 3. Get User Profile

```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

### 4. Upload a File (Optional - requires S3 configuration)

```bash
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -F "file=@/path/to/your/file.pdf"
```

### 5. List Files

```bash
curl -X GET http://localhost:8080/api/files \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Common Issues and Solutions

### Issue 1: Port Already in Use

**Solution:**
```bash
# Stop conflicting services
docker-compose down

# Check what's using the port
lsof -i :8080  # Mac/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process or change the port in application.properties
```

### Issue 2: MySQL Connection Failed

**Solution:**
```bash
# Wait for MySQL to be ready (takes 30-60 seconds)
docker-compose logs mysql

# Restart services after MySQL is ready
docker-compose restart user-service file-service admin-service
```

### Issue 3: Services Not Registered with Eureka

**Solution:**
```bash
# Wait 1-2 minutes for registration
# Check Eureka dashboard: http://localhost:8761

# Restart a specific service
docker-compose restart user-service
```

### Issue 4: JWT Token Invalid

**Solution:**
- Ensure all services have the same JWT secret in application.properties
- Token expires after some time, login again to get a new token

## Stopping the Application

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clears database)
docker-compose down -v
```

## Viewing Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f user-service

# Last 100 lines
docker-compose logs --tail=100 file-service
```

## Running Without Docker (Development)

### 1. Start MySQL Separately
```bash
# Using Docker for MySQL only
docker run -d \
  --name data-vessel-mysql \
  -e MYSQL_ROOT_PASSWORD=Root@123 \
  -e MYSQL_DATABASE=user_service \
  -p 3306:3306 \
  mysql:8.0
```

### 2. Run Each Service
```bash
# Terminal 1 - Service Registry
cd backend/service-registry
mvn spring-boot:run

# Terminal 2 - User Service
cd backend/user-service
mvn spring-boot:run

# Terminal 3 - File Service
cd backend/file-service
mvn spring-boot:run

# Terminal 4 - Admin Service
cd backend/admin-service
mvn spring-boot:run

# Terminal 5 - Notification Service
cd backend/notification-service
mvn spring-boot:run

# Terminal 6 - API Gateway
cd backend/api-gateway
mvn spring-boot:run
```

## Next Steps

1. **Configure AWS S3**
   - Create S3 bucket
   - Update `.env` with credentials
   - Restart file-service

2. **Configure Email**
   - Enable Gmail App Passwords
   - Update `.env` with credentials
   - Restart notification-service

3. **Test All Features**
   - File upload/download
   - File sharing
   - Email notifications
   - Admin operations

4. **Customize**
   - Update database credentials
   - Modify JWT secret
   - Adjust service ports
   - Add custom features

## Getting Help

- Check logs: `docker-compose logs [service-name]`
- View Eureka Dashboard: http://localhost:8761
- Check README.md for detailed documentation
- Create an issue on GitHub

---

**Happy coding! 🚀**
