# Data Vessel - Setup Checklist

Use this checklist to ensure your Data Vessel project is properly configured and running.

## ✅ Prerequisites

- [ ] Java 17 installed
  ```bash
  java -version  # Should show version 17+
  ```

- [ ] Maven 3.6+ installed
  ```bash
  mvn -version  # Should show version 3.6+
  ```

- [ ] Docker installed
  ```bash
  docker --version
  docker-compose --version
  ```

- [ ] Git installed (for version control)
  ```bash
  git --version
  ```

## ✅ Configuration

### Database
- [ ] MySQL configuration verified in `docker-compose.yml`
- [ ] Database name: `user_service` (will be created automatically)
- [ ] Default credentials: root/Root@123

### JWT Secret
- [ ] JWT secret configured in all services (already done)
- [ ] Secret is consistent across:
  - `backend/user-service/src/main/resources/application.properties`
  - `backend/file-service/src/main/resources/application.properties`
  - `backend/admin-service/src/main/resources/application.properties`

### AWS S3 (Optional for testing, required for file uploads)
- [ ] AWS account created
- [ ] S3 bucket created
- [ ] IAM user with S3 permissions created
- [ ] Access keys generated
- [ ] `.env` file updated with:
  - `AWS_S3_BUCKET_NAME`
  - `AWS_S3_REGION`
  - `AWS_ACCESS_KEY_ID`
  - `AWS_SECRET_ACCESS_KEY`

### Email Configuration (Optional for testing, required for notifications)
- [ ] Gmail account with 2FA enabled
- [ ] App password generated
- [ ] `.env` file updated with:
  - `MAIL_USERNAME`
  - `MAIL_PASSWORD`

## ✅ Build Process

- [ ] All services built successfully
  ```bash
  ./build-all.sh  # Linux/Mac
  # OR
  build-all.bat   # Windows
  ```

- [ ] No build errors in output
- [ ] JAR files created in each service's `target/` directory

## ✅ Docker Deployment

- [ ] Docker daemon is running
- [ ] Build all Docker images
  ```bash
  docker-compose build
  ```

- [ ] Start all services
  ```bash
  docker-compose up -d
  ```

- [ ] All containers are running
  ```bash
  docker-compose ps
  # All services should show "Up" status
  ```

- [ ] Check container logs for errors
  ```bash
  docker-compose logs
  ```

## ✅ Service Verification

### Service Registry (Eureka)
- [ ] Eureka dashboard accessible at http://localhost:8761
- [ ] All services registered in Eureka:
  - user-service
  - file-service
  - admin-service
  - notification-service
  - api-gateway

### API Gateway
- [ ] API Gateway accessible at http://localhost:8080
- [ ] Test health endpoint:
  ```bash
  curl http://localhost:8080/actuator/health
  ```

### User Service
- [ ] User Service running on port 8081
- [ ] Database connection successful (check logs)
- [ ] Tables created automatically

### File Service
- [ ] File Service running on port 8082
- [ ] Database connection successful
- [ ] S3 configuration loaded (if configured)

### Admin Service
- [ ] Admin Service running on port 8083
- [ ] Database connection successful

### Notification Service
- [ ] Notification Service running on port 8084
- [ ] SMTP configuration loaded (if configured)

## ✅ Functional Testing

### User Registration
- [ ] Register a test user
  ```bash
  curl -X POST http://localhost:8080/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser","email":"test@example.com","password":"test123","role":"USER"}'
  ```
- [ ] Response: 201 Created
- [ ] User data returned (without password)

### User Login
- [ ] Login with test user
  ```bash
  curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"testuser","password":"test123"}'
  ```
- [ ] Response: 200 OK
- [ ] JWT token received
- [ ] Save token for next requests

### Get Profile
- [ ] Get user profile with JWT token
  ```bash
  curl -X GET http://localhost:8080/api/user/profile \
    -H "Authorization: Bearer YOUR_TOKEN_HERE"
  ```
- [ ] Response: 200 OK
- [ ] Profile data returned

### File Upload (if S3 configured)
- [ ] Upload a test file
  ```bash
  curl -X POST http://localhost:8080/api/files/upload \
    -H "Authorization: Bearer YOUR_TOKEN_HERE" \
    -F "file=@/path/to/test.pdf"
  ```
- [ ] Response: 201 Created
- [ ] File metadata returned

### List Files
- [ ] List user files
  ```bash
  curl -X GET http://localhost:8080/api/files \
    -H "Authorization: Bearer YOUR_TOKEN_HERE"
  ```
- [ ] Response: 200 OK
- [ ] Files array returned

### Admin Operations
- [ ] Get all users (with admin token)
  ```bash
  curl -X GET http://localhost:8080/api/admin/users \
    -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
  ```
- [ ] Response: 200 OK
- [ ] Users list returned

### System Metrics
- [ ] Get system metrics
  ```bash
  curl -X GET http://localhost:8080/api/admin/metrics \
    -H "Authorization: Bearer YOUR_ADMIN_TOKEN"
  ```
- [ ] Response: 200 OK
- [ ] Metrics data returned

## ✅ Postman Testing

- [ ] Postman installed
- [ ] Import collection: `Data-Vessel-API-Collection.postman_collection.json`
- [ ] Run "Register User" request
- [ ] Run "Login" request (token auto-saved to environment)
- [ ] Run "Get Profile" request
- [ ] All requests in collection tested

## ✅ Database Verification

- [ ] Connect to MySQL
  ```bash
  docker exec -it data-vessel-mysql mysql -uroot -pRoot@123
  ```

- [ ] Check databases created
  ```sql
  SHOW DATABASES;
  # Should show: user_service, file_service
  ```

- [ ] Check users table
  ```sql
  USE user_service;
  SHOW TABLES;
  SELECT * FROM users;
  ```

## ✅ Monitoring & Logs

- [ ] Check Eureka dashboard for service health
- [ ] Review service logs for errors
  ```bash
  docker-compose logs user-service
  docker-compose logs file-service
  docker-compose logs admin-service
  docker-compose logs notification-service
  docker-compose logs api-gateway
  ```

- [ ] No error messages in logs
- [ ] All services connected to Eureka
- [ ] Database connections successful

## ✅ Common Issues Resolved

- [ ] Port conflicts resolved
- [ ] MySQL connection issues fixed
- [ ] JWT token validation working
- [ ] Service registration with Eureka successful
- [ ] All services accessible through API Gateway

## ✅ Documentation

- [ ] README.md reviewed
- [ ] QUICKSTART.md followed
- [ ] API-DOCUMENTATION.md bookmarked
- [ ] PROJECT-SUMMARY.md understood

## ✅ Optional Enhancements

- [ ] Custom domain configured (if deploying to production)
- [ ] SSL/TLS certificates configured
- [ ] Rate limiting implemented
- [ ] Additional security measures added
- [ ] Monitoring tools integrated (Prometheus, Grafana)
- [ ] CI/CD pipeline setup

## ✅ Production Readiness (Future)

- [ ] Environment-specific configurations
- [ ] Secrets management (Vault, AWS Secrets Manager)
- [ ] Database backups configured
- [ ] Logging aggregation setup (ELK Stack)
- [ ] Container orchestration (Kubernetes)
- [ ] Auto-scaling configured
- [ ] Health checks and monitoring
- [ ] Load testing completed
- [ ] Security audit performed

---

## 🎉 Project Status

Once all checkboxes above are checked, your Data Vessel project is:
- ✅ **Fully Configured**
- ✅ **Running Successfully**
- ✅ **Ready for Development**
- ✅ **Ready for Testing**

---

## 🚀 Next Steps

1. Start building your frontend (React, Angular, Vue.js)
2. Implement additional features from the design document
3. Add custom business logic
4. Deploy to cloud (AWS, Azure, GCP)
5. Set up CI/CD pipeline

---

**Happy Coding! 🎊**
