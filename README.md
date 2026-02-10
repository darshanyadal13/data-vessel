# Data Vessel - Cloud-Based File Management System

Data Vessel is a scalable, microservices-based file management system inspired by Google Drive and Data Lake concepts. Built with Java, Spring Boot, and designed for cloud deployment with Docker.

## 🏗️ Architecture Overview

This project follows a **Microservices Architecture** with the following components:

### Core Services

1. **User Service** (Port 8081)
   - User registration and authentication
   - Profile management
   - JWT token generation
   - Role-based access control

2. **File Service** (Port 8082)
   - File upload/download with AWS S3 integration
   - File metadata management
   - Folder creation and organization
   - File sharing and permissions
   - Soft delete with trash/restore functionality
   - File search and tagging

3. **Admin Service** (Port 8083)
   - User management (view, delete, deactivate)
   - System metrics and statistics
   - Audit logs and activity tracking

4. **Notification Service** (Port 8084)
   - Email notifications for file operations
   - Welcome emails
   - File sharing notifications
   - Password reset emails

### Infrastructure Services

5. **API Gateway** (Port 8080)
   - Unified entry point for all services
   - Request routing with Spring Cloud Gateway
   - CORS configuration

6. **Service Registry** (Port 8761)
   - Eureka Server for service discovery
   - Load balancing support

## 🛠️ Tech Stack

- **Backend Framework**: Spring Boot 3.2.4
- **Language**: Java 17
- **Database**: MySQL 8.0
- **Cloud Storage**: AWS S3
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Security**: Spring Security + JWT
- **Email**: Spring Mail (SMTP)
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose
- MySQL 8.0 (or use Docker)
- AWS Account (for S3 storage)
- Gmail Account (for email notifications)

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd data-vessel
```

### 2. Configure Environment Variables

Copy the example environment file and update with your credentials:

```bash
cp .env.example .env
```

Edit `.env` file with your actual values:

```env
# AWS S3 Configuration
AWS_S3_BUCKET_NAME=your-bucket-name
AWS_S3_REGION=us-east-1
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key

# Email Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

### 3. Update JWT Secret

Update the JWT secret in all service `application.properties` files:
- `backend/user-service/src/main/resources/application.properties`
- `backend/file-service/src/main/resources/application.properties`
- `backend/admin-service/src/main/resources/application.properties`

Replace:
```properties
jwt.secret=your-secret-key-here-make-it-long-and-secure-at-least-256-bits
```

Generate a secure secret:
```bash
openssl rand -base64 64
```

### 4. Build All Services

```bash
# Build each service
cd backend/service-registry && mvn clean package && cd ../..
cd backend/user-service && mvn clean package && cd ../..
cd backend/file-service && mvn clean package && cd ../..
cd backend/admin-service && mvn clean package && cd ../..
cd backend/notification-service && mvn clean package && cd ../..
cd backend/api-gateway && mvn clean package && cd ../..
```

Or use this script:

```bash
#!/bin/bash
services=("service-registry" "user-service" "file-service" "admin-service" "notification-service" "api-gateway")
for service in "${services[@]}"; do
    echo "Building $service..."
    cd backend/$service
    mvn clean package -DskipTests
    cd ../..
done
```

### 5. Run with Docker Compose

```bash
docker-compose up -d
```

This will start all services including MySQL database.

### 6. Verify Services

Check if all services are running:

```bash
docker-compose ps
```

Access the Eureka Dashboard to see registered services:
```
http://localhost:8761
```

## 📡 API Endpoints

All requests go through the API Gateway at `http://localhost:8080`

### User Service Endpoints

```
POST   /api/auth/register       - Register new user
POST   /api/auth/login          - Login and get JWT token
POST   /api/auth/logout         - Logout user
GET    /api/user/profile        - Get user profile (requires JWT)
PUT    /api/user/profile        - Update user profile (requires JWT)
```

### File Service Endpoints

```
POST   /api/files/upload                 - Upload file (multipart/form-data)
GET    /api/files/download/{id}          - Download file
GET    /api/files                        - List all user files
GET    /api/files/search?query=filename  - Search files
POST   /api/files/folder/create          - Create folder
PUT    /api/files/rename/{id}            - Rename file/folder
PUT    /api/files/move                   - Move file to folder
DELETE /api/files/{id}                   - Soft delete (move to trash)
GET    /api/files/trash                  - View trash
PUT    /api/files/restore/{id}           - Restore from trash
DELETE /api/files/permanent-delete/{id}  - Permanently delete
PUT    /api/files/share                  - Share file with user
```

### Admin Service Endpoints

```
GET    /api/admin/users            - List all users
DELETE /api/admin/user/{id}        - Delete user
PUT    /api/admin/user/{id}/deactivate - Deactivate user
GET    /api/admin/metrics          - Get system metrics
GET    /api/admin/logs             - View audit logs
```

### Notification Service Endpoints

```
POST   /api/notify/email          - Send custom email
POST   /api/notify/welcome        - Send welcome email
POST   /api/notify/file-upload    - Send file upload notification
POST   /api/notify/file-shared    - Send file shared notification
```

## 🔐 Authentication

### Register a User

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

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john",
    "password": "password123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "message": "Login successful"
}
```

### Use Token for Protected Endpoints

```bash
curl -X GET http://localhost:8080/api/user/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📁 Project Structure

```
data-vessel/
├── backend/
│   ├── service-registry/      # Eureka Server
│   ├── api-gateway/           # Spring Cloud Gateway
│   ├── user-service/          # User management
│   ├── file-service/          # File operations
│   ├── admin-service/         # Admin operations
│   └── notification-service/  # Email notifications
├── docker-compose.yml         # Docker orchestration
├── .env                       # Environment variables
├── .env.example              # Environment template
├── .gitignore                # Git ignore rules
└── README.md                 # This file
```

## 🗄️ Database Schema

### Users Table (user_service database)
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);
```

### File Metadata Table (file_service database)
```sql
CREATE TABLE file_metadata (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    owner_id BIGINT NOT NULL,
    size BIGINT NOT NULL,
    file_type VARCHAR(100),
    s3_url VARCHAR(500) NOT NULL,
    s3_key VARCHAR(500),
    version INT DEFAULT 1,
    deleted BOOLEAN DEFAULT FALSE,
    folder_id BIGINT,
    tags VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Folders Table
```sql
CREATE TABLE folders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    owner_id BIGINT NOT NULL,
    parent_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### File Shares Table
```sql
CREATE TABLE file_shares (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_id BIGINT NOT NULL,
    shared_with_user_id BIGINT NOT NULL,
    permission VARCHAR(20) NOT NULL,
    shared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 🐳 Docker Commands

### Start all services
```bash
docker-compose up -d
```

### Stop all services
```bash
docker-compose down
```

### View logs
```bash
docker-compose logs -f [service-name]
```

### Rebuild a specific service
```bash
docker-compose build [service-name]
docker-compose up -d [service-name]
```

### Remove all containers and volumes
```bash
docker-compose down -v
```

## 🧪 Testing

### Test User Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"test123","role":"USER"}'
```

### Test File Upload
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@/path/to/your/file.pdf"
```

## 📊 Monitoring

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **User Service**: http://localhost:8081
- **File Service**: http://localhost:8082
- **Admin Service**: http://localhost:8083
- **Notification Service**: http://localhost:8084

## 🔧 Configuration

### Database Configuration
Update database credentials in docker-compose.yml or service application.properties files.

### AWS S3 Configuration
1. Create an S3 bucket in AWS
2. Create IAM user with S3 access
3. Update credentials in .env file

### Email Configuration
For Gmail:
1. Enable 2-Factor Authentication
2. Generate App Password
3. Update credentials in .env file

## 🚦 Future Enhancements

- [ ] OAuth2 / SSO Integration
- [ ] API Rate Limiting
- [ ] CI/CD Pipeline (GitHub Actions/Jenkins)
- [ ] Multi-region Deployment
- [ ] File Previews (Images/Docs/Media)
- [ ] Virus Scanning on Upload
- [ ] User Analytics Dashboard
- [ ] File Versioning UI
- [ ] Real-time Notifications (WebSocket)
- [ ] Mobile App Integration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License.

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Spring Boot Team
- Netflix OSS
- AWS Services
- Docker Community

## 📧 Support

For support, email support@datavessel.com or create an issue in the repository.

---

**Built with ❤️ using Spring Boot Microservices Architecture**
