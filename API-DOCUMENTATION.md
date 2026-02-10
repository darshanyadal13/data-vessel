# Data Vessel API Documentation

## Base URL
All API requests go through the API Gateway:
```
http://localhost:8080
```

## Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer YOUR_JWT_TOKEN
```

---

## User Service APIs

### 1. Register User
**Endpoint:** `POST /api/auth/register`

**Description:** Register a new user account

**Request Body:**
```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "password123",
  "role": "USER"
}
```

**Response:** `201 Created`
```json
{
  "statusCode": 201,
  "message": "User Registered",
  "data": {
    "id": 1,
    "username": "john",
    "email": "john@example.com",
    "role": "USER",
    "active": true,
    "password": null
  }
}
```

---

### 2. Login
**Endpoint:** `POST /api/auth/login`

**Description:** Authenticate user and receive JWT token

**Request Body:**
```json
{
  "username": "john",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful"
}
```

---

### 3. Logout
**Endpoint:** `POST /api/auth/logout`

**Description:** Logout user (invalidate token client-side)

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "message": "Logout successful"
}
```

---

### 4. Get User Profile
**Endpoint:** `GET /api/user/profile`

**Description:** Get current user's profile

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "User profile fetched successfully",
  "data": {
    "id": 1,
    "username": "john",
    "email": "john@example.com",
    "role": "USER",
    "active": true
  }
}
```

---

### 5. Update User Profile
**Endpoint:** `PUT /api/user/profile`

**Description:** Update current user's profile

**Headers:**
- `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "email": "newemail@example.com"
}
```

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "username": "john",
    "email": "newemail@example.com",
    "role": "USER",
    "active": true
  }
}
```

---

## File Service APIs

### 1. Upload File
**Endpoint:** `POST /api/files/upload`

**Description:** Upload a new file

**Headers:**
- `Authorization: Bearer {token}`
- `Content-Type: multipart/form-data`

**Form Data:**
- `file` (file): The file to upload
- `folderId` (optional, number): Target folder ID
- `tags` (optional, string): Comma-separated tags

**Response:** `201 Created`
```json
{
  "statusCode": 201,
  "message": "File uploaded successfully",
  "data": {
    "id": 1,
    "filename": "document.pdf",
    "message": "File uploaded successfully",
    "url": "https://s3.amazonaws.com/...",
    "size": 1024000
  }
}
```

---

### 2. Download File
**Endpoint:** `GET /api/files/download/{id}`

**Description:** Download a file by ID

**Headers:**
- `Authorization: Bearer {token}`

**Response:** Binary file stream

---

### 3. List Files
**Endpoint:** `GET /api/files`

**Description:** List all files owned by current user

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Files retrieved successfully",
  "data": [
    {
      "id": 1,
      "filename": "document.pdf",
      "originalFilename": "document.pdf",
      "ownerId": 1,
      "size": 1024000,
      "fileType": "application/pdf",
      "s3Url": "https://s3.amazonaws.com/...",
      "version": 1,
      "deleted": false,
      "folderId": null,
      "tags": "important,work",
      "createdAt": "2026-02-09T10:30:00",
      "updatedAt": "2026-02-09T10:30:00"
    }
  ]
}
```

---

### 4. Search Files
**Endpoint:** `GET /api/files/search?query={searchTerm}`

**Description:** Search files by filename

**Headers:**
- `Authorization: Bearer {token}`

**Query Parameters:**
- `query` (string): Search term

**Response:** `200 OK` (same format as List Files)

---

### 5. Create Folder
**Endpoint:** `POST /api/files/folder/create`

**Description:** Create a new folder

**Headers:**
- `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "name": "Documents",
  "parentId": null
}
```

**Response:** `201 Created`
```json
{
  "statusCode": 201,
  "message": "Folder created successfully",
  "data": {
    "id": 1,
    "name": "Documents",
    "ownerId": 1,
    "parentId": null,
    "createdAt": "2026-02-09T10:30:00"
  }
}
```

---

### 6. Rename File
**Endpoint:** `PUT /api/files/rename/{id}?newName={name}`

**Description:** Rename a file or folder

**Headers:**
- `Authorization: Bearer {token}`

**Query Parameters:**
- `newName` (string): New name for the file

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "File renamed successfully",
  "data": "NewFileName.pdf"
}
```

---

### 7. Move File
**Endpoint:** `PUT /api/files/move`

**Description:** Move file to a different folder

**Headers:**
- `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "fileId": 1,
  "targetFolderId": 2
}
```

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "File moved successfully"
}
```

---

### 8. Delete File (Soft Delete)
**Endpoint:** `DELETE /api/files/{id}`

**Description:** Move file to trash

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "File moved to trash"
}
```

---

### 9. Get Trash
**Endpoint:** `GET /api/files/trash`

**Description:** List all deleted files

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK` (same format as List Files)

---

### 10. Restore File
**Endpoint:** `PUT /api/files/restore/{id}`

**Description:** Restore file from trash

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "File restored successfully"
}
```

---

### 11. Permanent Delete
**Endpoint:** `DELETE /api/files/permanent-delete/{id}`

**Description:** Permanently delete file (cannot be recovered)

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "File permanently deleted"
}
```

---

### 12. Share File
**Endpoint:** `PUT /api/files/share`

**Description:** Share file with another user

**Headers:**
- `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "fileId": 1,
  "sharedWithUserId": 2,
  "permission": "READ"
}
```

**Permissions:** `READ`, `WRITE`, `DELETE`

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "File shared successfully"
}
```

---

## Admin Service APIs

### 1. Get All Users
**Endpoint:** `GET /api/admin/users`

**Description:** List all registered users

**Headers:**
- `Authorization: Bearer {token}` (Admin role required)

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Users retrieved successfully",
  "data": [
    {
      "id": 1,
      "username": "john",
      "email": "john@example.com",
      "role": "USER",
      "active": true
    }
  ]
}
```

---

### 2. Delete User
**Endpoint:** `DELETE /api/admin/user/{id}`

**Description:** Delete a user account

**Headers:**
- `Authorization: Bearer {token}` (Admin role required)

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "User deleted successfully"
}
```

---

### 3. Deactivate User
**Endpoint:** `PUT /api/admin/user/{id}/deactivate`

**Description:** Deactivate a user account

**Headers:**
- `Authorization: Bearer {token}` (Admin role required)

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "User deactivated successfully"
}
```

---

### 4. Get System Metrics
**Endpoint:** `GET /api/admin/metrics`

**Description:** Get system statistics

**Headers:**
- `Authorization: Bearer {token}` (Admin role required)

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Metrics retrieved successfully",
  "data": {
    "totalUsers": 150,
    "activeUsers": 142,
    "totalFiles": 1250,
    "totalStorageUsed": 52428800,
    "storageUnit": "MB"
  }
}
```

---

### 5. Get Audit Logs
**Endpoint:** `GET /api/admin/logs`

**Description:** View system audit logs

**Headers:**
- `Authorization: Bearer {token}` (Admin role required)

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Audit logs retrieved successfully",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "username": null,
      "action": "DELETE_USER",
      "details": "User deleted by admin",
      "ipAddress": "system",
      "timestamp": "2026-02-09T10:30:00"
    }
  ]
}
```

---

## Notification Service APIs

### 1. Send Custom Email
**Endpoint:** `POST /api/notify/email`

**Description:** Send a custom email

**Request Body:**
```json
{
  "to": "user@example.com",
  "subject": "Test Email",
  "body": "This is a test email",
  "isHtml": false
}
```

**Response:** `200 OK`
```json
{
  "statusCode": 200,
  "message": "Email sent successfully"
}
```

---

### 2. Send Welcome Email
**Endpoint:** `POST /api/notify/welcome?email={email}&username={username}`

**Description:** Send welcome email to new user

**Query Parameters:**
- `email` (string): User's email
- `username` (string): User's username

**Response:** `200 OK`

---

### 3. Send File Upload Notification
**Endpoint:** `POST /api/notify/file-upload?email={email}&filename={filename}`

**Description:** Notify user of successful file upload

**Query Parameters:**
- `email` (string): User's email
- `filename` (string): Uploaded filename

**Response:** `200 OK`

---

### 4. Send File Shared Notification
**Endpoint:** `POST /api/notify/file-shared?email={email}&filename={filename}&sharedBy={sharedBy}`

**Description:** Notify user that a file was shared with them

**Query Parameters:**
- `email` (string): Recipient's email
- `filename` (string): Shared filename
- `sharedBy` (string): Name of person who shared

**Response:** `200 OK`

---

## Error Responses

All endpoints may return the following error responses:

### 400 Bad Request
```json
{
  "statusCode": 400,
  "message": "Invalid request parameters"
}
```

### 401 Unauthorized
```json
{
  "statusCode": 401,
  "message": "Authentication required"
}
```

### 403 Forbidden
```json
{
  "statusCode": 403,
  "message": "Access denied"
}
```

### 404 Not Found
```json
{
  "statusCode": 404,
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "statusCode": 500,
  "message": "Internal server error"
}
```

---

## Rate Limiting
Currently, no rate limiting is implemented. This is a future enhancement.

## Versioning
API Version: 1.0

## Support
For API support, contact: support@datavessel.com
