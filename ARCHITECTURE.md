# Data Vessel - Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            CLIENT APPLICATIONS                               │
│                    (Web Browser, Mobile App, Postman)                       │
└─────────────────────────────────┬───────────────────────────────────────────┘
                                  │
                                  │ HTTP/HTTPS
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         API GATEWAY (Port 8080)                              │
│                      Spring Cloud Gateway                                    │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  Routes:                                                              │   │
│  │  /api/auth/**, /api/user/** → User Service                          │   │
│  │  /api/files/**              → File Service                           │   │
│  │  /api/admin/**              → Admin Service                          │   │
│  │  /api/notify/**             → Notification Service                   │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────┬───────────────────────────────────────────┘
                                  │
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
        ▼                         ▼                         ▼
┌───────────────┐        ┌───────────────┐        ┌───────────────┐
│   SERVICE     │        │   SERVICE     │        │   SERVICE     │
│   REGISTRY    │◄───────│   REGISTRY    │◄───────│   REGISTRY    │
│   (Eureka)    │        │   (Eureka)    │        │   (Eureka)    │
│  Port 8761    │        │  Port 8761    │        │  Port 8761    │
└───────────────┘        └───────────────┘        └───────────────┘
        │                         │                         │
        │                         │                         │
        └─────────────────────────┼─────────────────────────┘
                                  │
                                  │ Service Discovery
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
        ▼                         ▼                         ▼
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│  USER SERVICE   │      │  FILE SERVICE   │      │ ADMIN SERVICE   │
│   Port 8081     │      │   Port 8082     │      │   Port 8083     │
│                 │      │                 │      │                 │
│ • Registration  │      │ • File Upload   │      │ • User Mgmt     │
│ • Login/Logout  │      │ • File Download │      │ • Metrics       │
│ • Profile Mgmt  │      │ • Search        │      │ • Audit Logs    │
│ • JWT Auth      │      │ • Folders       │      │ • Deactivate    │
│                 │      │ • Share         │      │                 │
└────────┬────────┘      └────────┬────────┘      └────────┬────────┘
         │                        │                        │
         │                        │                        │
         ▼                        ▼                        ▼
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│  MySQL DB       │      │  MySQL DB       │      │  MySQL DB       │
│  user_service   │      │  file_service   │      │  user_service   │
│                 │      │                 │      │  (shared)       │
│ Tables:         │      │ Tables:         │      │                 │
│ • users         │      │ • file_metadata │      │ Tables:         │
│                 │      │ • folders       │      │ • audit_logs    │
│                 │      │ • file_shares   │      │                 │
└─────────────────┘      └────────┬────────┘      └─────────────────┘
                                  │
                                  │
                                  ▼
                         ┌─────────────────┐
                         │   AWS S3        │
                         │   Cloud Storage │
                         │                 │
                         │ • File Objects  │
                         │ • Versioning    │
                         └─────────────────┘

        ┌─────────────────────────────────────────────────┐
        │                                                 │
        ▼                                                 ▼
┌─────────────────┐                            ┌─────────────────┐
│ NOTIFICATION    │                            │   EXTERNAL      │
│   SERVICE       │                            │   SERVICES      │
│   Port 8084     │                            │                 │
│                 │                            │ • SMTP Server   │
│ • Email Alerts  │───────────────────────────►│   (Gmail)       │
│ • Welcome Email │                            │                 │
│ • File Shared   │                            │ • AWS IAM       │
│ • Notifications │                            │                 │
└─────────────────┘                            └─────────────────┘


┌─────────────────────────────────────────────────────────────────────────────┐
│                           INFRASTRUCTURE LAYER                               │
│                                                                              │
│  ┌────────────────────┐  ┌────────────────────┐  ┌────────────────────┐   │
│  │  Docker Container  │  │  Docker Container  │  │  Docker Container  │   │
│  │  (MySQL)           │  │  (Services)        │  │  (Gateway/Eureka)  │   │
│  └────────────────────┘  └────────────────────┘  └────────────────────┘   │
│                                                                              │
│                    Orchestrated by Docker Compose                           │
└─────────────────────────────────────────────────────────────────────────────┘


TECHNOLOGY STACK:
═══════════════════════════════════════════════════════════════════════════════

Backend Framework:      Spring Boot 3.2.4
Language:              Java 17
API Gateway:           Spring Cloud Gateway
Service Discovery:     Netflix Eureka
Security:              Spring Security + JWT
Database:              MySQL 8.0
Cloud Storage:         AWS S3
Email:                 Spring Mail (SMTP)
Containerization:      Docker & Docker Compose
Build Tool:            Maven


COMMUNICATION PATTERNS:
═══════════════════════════════════════════════════════════════════════════════

1. Client → API Gateway → Microservices
   - RESTful HTTP/HTTPS
   - JSON payloads
   - JWT in Authorization header

2. Microservices → Eureka
   - Service registration
   - Health checks
   - Service discovery

3. Microservices → Database
   - JPA/Hibernate
   - Connection pooling

4. File Service → AWS S3
   - AWS SDK
   - Presigned URLs
   - Multipart upload

5. Notification Service → SMTP
   - JavaMail API
   - HTML/Text emails


DATA FLOW EXAMPLE - File Upload:
═══════════════════════════════════════════════════════════════════════════════

1. Client uploads file → API Gateway (with JWT)
2. API Gateway routes → File Service
3. File Service validates JWT
4. File Service uploads → AWS S3
5. S3 returns URL
6. File Service saves metadata → MySQL
7. File Service returns response → Client
8. (Optional) File Service notifies → Notification Service
9. Notification Service sends email → User


SECURITY LAYERS:
═══════════════════════════════════════════════════════════════════════════════

Layer 1: API Gateway
  ├─ CORS configuration
  └─ Request validation

Layer 2: JWT Authentication
  ├─ Token generation (User Service)
  ├─ Token validation (All services)
  └─ Stateless sessions

Layer 3: Service-Level Security
  ├─ Spring Security filters
  ├─ Role-based access (USER, ADMIN)
  └─ Method-level security

Layer 4: Data Security
  ├─ Password encryption (BCrypt)
  ├─ Database credentials
  └─ AWS credentials


SCALABILITY CONSIDERATIONS:
═══════════════════════════════════════════════════════════════════════════════

✓ Horizontal Scaling: Each service can be scaled independently
✓ Load Balancing: Eureka + API Gateway
✓ Database Scaling: Read replicas, sharding
✓ Caching: Can add Redis for session/token management
✓ Message Queue: Can add RabbitMQ/Kafka for async operations
✓ CDN: Can add CloudFront for static content


DEPLOYMENT OPTIONS:
═══════════════════════════════════════════════════════════════════════════════

Development:
  └─ Docker Compose (current setup)

Production:
  ├─ Kubernetes (recommended)
  ├─ AWS ECS/EKS
  ├─ Azure Container Instances
  └─ Google Kubernetes Engine

```
