#!/bin/bash

# Data Vessel - Comprehensive API Test Suite
# This script tests all APIs across all microservices

BASE_URL="http://localhost:8080"
ADMIN_SERVICE="http://localhost:8083"
FILE_SERVICE="http://localhost:8082"

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counters
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0

# Function to print test header
print_header() {
    echo -e "\n${BLUE}========================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}========================================${NC}\n"
}

# Function to test API endpoint
test_api() {
    local test_name="$1"
    local method="$2"
    local endpoint="$3"
    local data="$4"
    local auth_header="$5"
    local expected_status="$6"
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    
    echo -e "${YELLOW}Test $TOTAL_TESTS: $test_name${NC}"
    
    if [ -z "$auth_header" ]; then
        response=$(curl -s -w "\n%{http_code}" -X "$method" "$endpoint" \
            -H "Content-Type: application/json" \
            ${data:+-d "$data"} 2>&1)
    else
        response=$(curl -s -w "\n%{http_code}" -X "$method" "$endpoint" \
            -H "Content-Type: application/json" \
            -H "$auth_header" \
            ${data:+-d "$data"} 2>&1)
    fi
    
    http_code=$(echo "$response" | tail -n 1)
    body=$(echo "$response" | sed '$d')
    
    echo "Response: $body"
    echo "Status: $http_code"
    
    if [ "$http_code" = "$expected_status" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        echo "$body"
        return 0
    else
        echo -e "${RED}❌ FAILED (Expected: $expected_status, Got: $http_code)${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
        return 1
    fi
}

# Variables to store tokens and IDs
USER_TOKEN=""
ADMIN_TOKEN=""
FILE_ID=""
FOLDER_ID=""

print_header "DATA VESSEL API TEST SUITE"

# ============================================
# USER SERVICE TESTS
# ============================================
print_header "1. USER SERVICE - Authentication & User Management"

# Test 1: Register a new user
echo -e "${YELLOW}Test 1: Register New User${NC}"
REGISTER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "testuser1",
        "email": "testuser1@test.com",
        "password": "Test@123"
    }')

HTTP_CODE=$(echo "$REGISTER_RESPONSE" | tail -n 1)
BODY=$(echo "$REGISTER_RESPONSE" | sed '$d')
echo "Response: $BODY"
echo "Status: $HTTP_CODE"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
if [ "$HTTP_CODE" = "201" ]; then
    echo -e "${GREEN}✅ PASSED${NC}\n"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}❌ FAILED${NC}\n"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 2: Register another user for sharing tests
echo -e "${YELLOW}Test 2: Register Second User${NC}"
REGISTER_RESPONSE2=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/auth/register" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "testuser2",
        "email": "testuser2@test.com",
        "password": "Test@123"
    }')

HTTP_CODE=$(echo "$REGISTER_RESPONSE2" | tail -n 1)
BODY=$(echo "$REGISTER_RESPONSE2" | sed '$d')
echo "Response: $BODY"
echo "Status: $HTTP_CODE"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
if [ "$HTTP_CODE" = "201" ]; then
    echo -e "${GREEN}✅ PASSED${NC}\n"
    PASSED_TESTS=$((PASSED_TESTS + 1))
else
    echo -e "${RED}❌ FAILED${NC}\n"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 3: Login with the first user
echo -e "${YELLOW}Test 3: User Login${NC}"
LOGIN_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "testuser1",
        "password": "Test@123"
    }')

HTTP_CODE=$(echo "$LOGIN_RESPONSE" | tail -n 1)
BODY=$(echo "$LOGIN_RESPONSE" | sed '$d')
echo "Response: $BODY"
echo "Status: $HTTP_CODE"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ PASSED${NC}\n"
    PASSED_TESTS=$((PASSED_TESTS + 1))
    USER_TOKEN=$(echo "$BODY" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "Token saved: ${USER_TOKEN:0:30}..."
else
    echo -e "${RED}❌ FAILED${NC}\n"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 4: Get user profile (requires authentication)
if [ -n "$USER_TOKEN" ]; then
    echo -e "${YELLOW}Test 4: Get User Profile (Authenticated)${NC}"
    PROFILE_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/user/profile" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $USER_TOKEN")
    
    HTTP_CODE=$(echo "$PROFILE_RESPONSE" | tail -n 1)
    BODY=$(echo "$PROFILE_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Test 5: Update user profile
if [ -n "$USER_TOKEN" ]; then
    echo -e "${YELLOW}Test 5: Update User Profile${NC}"
    UPDATE_RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/api/user/profile" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $USER_TOKEN" \
        -d '{
            "email": "newemail@test.com"
        }')
    
    HTTP_CODE=$(echo "$UPDATE_RESPONSE" | tail -n 1)
    BODY=$(echo "$UPDATE_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Test 6: Logout
if [ -n "$USER_TOKEN" ]; then
    echo -e "${YELLOW}Test 6: User Logout${NC}"
    LOGOUT_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/auth/logout" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $USER_TOKEN")
    
    HTTP_CODE=$(echo "$LOGOUT_RESPONSE" | tail -n 1)
    BODY=$(echo "$LOGOUT_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Re-login for remaining tests
echo -e "${YELLOW}Re-authenticating for remaining tests...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"username": "testuser1", "password": "Test@123"}')
USER_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
echo "Token: ${USER_TOKEN:0:30}...\n"

# ============================================
# FILE SERVICE TESTS
# ============================================
print_header "2. FILE SERVICE - File Management"

# Test 7: Create a folder
if [ -n "$USER_TOKEN" ]; then
    echo -e "${YELLOW}Test 7: Create Folder${NC}"
    FOLDER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/files/folder" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $USER_TOKEN" \
        -d '{
            "name": "My Test Folder",
            "parentFolderId": null
        }')
    
    HTTP_CODE=$(echo "$FOLDER_RESPONSE" | tail -n 1)
    BODY=$(echo "$FOLDER_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "201" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        FOLDER_ID=$(echo "$BODY" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
        echo "Folder ID: $FOLDER_ID"
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Test 8: Upload a file (simulated - creating a test file)
if [ -n "$USER_TOKEN" ]; then
    echo -e "${YELLOW}Test 8: Upload File (Multipart)${NC}"
    echo "Test file content" > /tmp/test-upload.txt
    
    UPLOAD_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/files/upload" \
        -H "Authorization: Bearer $USER_TOKEN" \
        -F "file=@/tmp/test-upload.txt" \
        -F "folderId=${FOLDER_ID:-0}" 2>&1)
    
    HTTP_CODE=$(echo "$UPLOAD_RESPONSE" | tail -n 1)
    BODY=$(echo "$UPLOAD_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "201" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        FILE_ID=$(echo "$BODY" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
        echo "File ID: $FILE_ID"
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
    rm -f /tmp/test-upload.txt
fi

# Test 9: List user files
if [ -n "$USER_TOKEN" ]; then
    echo -e "${YELLOW}Test 9: List User Files${NC}"
    LIST_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/files/list" \
        -H "Authorization: Bearer $USER_TOKEN")
    
    HTTP_CODE=$(echo "$LIST_RESPONSE" | tail -n 1)
    BODY=$(echo "$LIST_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Test 10: Get file metadata
if [ -n "$USER_TOKEN" ] && [ -n "$FILE_ID" ]; then
    echo -e "${YELLOW}Test 10: Get File Metadata${NC}"
    META_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/files/$FILE_ID" \
        -H "Authorization: Bearer $USER_TOKEN")
    
    HTTP_CODE=$(echo "$META_RESPONSE" | tail -n 1)
    BODY=$(echo "$META_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Test 11: Share a file
if [ -n "$USER_TOKEN" ] && [ -n "$FILE_ID" ]; then
    echo -e "${YELLOW}Test 11: Share File with Another User${NC}"
    SHARE_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/files/share" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $USER_TOKEN" \
        -d "{
            \"fileId\": $FILE_ID,
            \"sharedWithUsername\": \"testuser2\",
            \"permission\": \"READ\"
        }")
    
    HTTP_CODE=$(echo "$SHARE_RESPONSE" | tail -n 1)
    BODY=$(echo "$SHARE_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "201" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Test 12: Search files
if [ -n "$USER_TOKEN" ]; then
    echo -e "${YELLOW}Test 12: Search Files${NC}"
    SEARCH_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/files/search?query=test" \
        -H "Authorization: Bearer $USER_TOKEN")
    
    HTTP_CODE=$(echo "$SEARCH_RESPONSE" | tail -n 1)
    BODY=$(echo "$SEARCH_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Test 13: Download file
if [ -n "$USER_TOKEN" ] && [ -n "$FILE_ID" ]; then
    echo -e "${YELLOW}Test 13: Download File${NC}"
    DOWNLOAD_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/files/download/$FILE_ID" \
        -H "Authorization: Bearer $USER_TOKEN" -o /tmp/downloaded-file.txt 2>&1)
    
    HTTP_CODE=$(echo "$DOWNLOAD_RESPONSE" | tail -n 1)
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        rm -f /tmp/downloaded-file.txt
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# ============================================
# ADMIN SERVICE TESTS
# ============================================
print_header "3. ADMIN SERVICE - Administration & Monitoring"

# Register admin user (assuming you have one in database)
echo -e "${YELLOW}Test 14: Admin Login${NC}"
ADMIN_LOGIN=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "prasad",
        "password": "Root@123"
    }')

HTTP_CODE=$(echo "$ADMIN_LOGIN" | tail -n 1)
BODY=$(echo "$ADMIN_LOGIN" | sed '$d')
echo "Response: $BODY"
echo "Status: $HTTP_CODE"
TOTAL_TESTS=$((TOTAL_TESTS + 1))
if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ PASSED${NC}\n"
    PASSED_TESTS=$((PASSED_TESTS + 1))
    ADMIN_TOKEN=$(echo "$BODY" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
else
    echo -e "${RED}❌ FAILED${NC}\n"
    FAILED_TESTS=$((FAILED_TESTS + 1))
fi

# Test 15: Get all users (admin endpoint)
if [ -n "$ADMIN_TOKEN" ]; then
    echo -e "${YELLOW}Test 15: Get All Users (Admin)${NC}"
    USERS_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/admin/users" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    
    HTTP_CODE=$(echo "$USERS_RESPONSE" | tail -n 1)
    BODY=$(echo "$USERS_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Test 16: Get system metrics
if [ -n "$ADMIN_TOKEN" ]; then
    echo -e "${YELLOW}Test 16: Get System Metrics${NC}"
    METRICS_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/admin/metrics" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    
    HTTP_CODE=$(echo "$METRICS_RESPONSE" | tail -n 1)
    BODY=$(echo "$METRICS_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Test 17: Get audit logs
if [ -n "$ADMIN_TOKEN" ]; then
    echo -e "${YELLOW}Test 17: Get Audit Logs${NC}"
    AUDIT_RESPONSE=$(curl -s -w "\n%{http_code}" -X GET "$BASE_URL/api/admin/audit-logs" \
        -H "Authorization: Bearer $ADMIN_TOKEN")
    
    HTTP_CODE=$(echo "$AUDIT_RESPONSE" | tail -n 1)
    BODY=$(echo "$AUDIT_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# ============================================
# NOTIFICATION SERVICE TESTS
# ============================================
print_header "4. NOTIFICATION SERVICE - Email Notifications"

# Test 18: Send test notification
if [ -n "$USER_TOKEN" ]; then
    echo -e "${YELLOW}Test 18: Send Test Notification${NC}"
    NOTIFY_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/notify/send" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $USER_TOKEN" \
        -d '{
            "to": "testuser1@test.com",
            "subject": "Test Notification",
            "body": "This is a test notification from Data Vessel"
        }')
    
    HTTP_CODE=$(echo "$NOTIFY_RESPONSE" | tail -n 1)
    BODY=$(echo "$NOTIFY_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "202" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# ============================================
# CLEANUP & DELETE TESTS
# ============================================
print_header "5. CLEANUP - Delete Operations"

# Test 19: Delete shared file
if [ -n "$USER_TOKEN" ] && [ -n "$FILE_ID" ]; then
    echo -e "${YELLOW}Test 19: Delete File${NC}"
    DELETE_RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/api/files/$FILE_ID" \
        -H "Authorization: Bearer $USER_TOKEN")
    
    HTTP_CODE=$(echo "$DELETE_RESPONSE" | tail -n 1)
    BODY=$(echo "$DELETE_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "204" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# Test 20: Delete folder
if [ -n "$USER_TOKEN" ] && [ -n "$FOLDER_ID" ]; then
    echo -e "${YELLOW}Test 20: Delete Folder${NC}"
    DELETE_FOLDER_RESPONSE=$(curl -s -w "\n%{http_code}" -X DELETE "$BASE_URL/api/files/folder/$FOLDER_ID" \
        -H "Authorization: Bearer $USER_TOKEN")
    
    HTTP_CODE=$(echo "$DELETE_FOLDER_RESPONSE" | tail -n 1)
    BODY=$(echo "$DELETE_FOLDER_RESPONSE" | sed '$d')
    echo "Response: $BODY"
    echo "Status: $HTTP_CODE"
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "204" ]; then
        echo -e "${GREEN}✅ PASSED${NC}\n"
        PASSED_TESTS=$((PASSED_TESTS + 1))
    else
        echo -e "${RED}❌ FAILED${NC}\n"
        FAILED_TESTS=$((FAILED_TESTS + 1))
    fi
fi

# ============================================
# FINAL SUMMARY
# ============================================
print_header "TEST SUMMARY"

echo -e "${BLUE}Total Tests: $TOTAL_TESTS${NC}"
echo -e "${GREEN}Passed: $PASSED_TESTS${NC}"
echo -e "${RED}Failed: $FAILED_TESTS${NC}"
echo ""

PASS_RATE=$(awk "BEGIN {printf \"%.2f\", ($PASSED_TESTS/$TOTAL_TESTS)*100}")
echo -e "${BLUE}Pass Rate: ${PASS_RATE}%${NC}"
echo ""

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}🎉 All tests passed! Your Data Vessel APIs are working perfectly!${NC}"
    exit 0
else
    echo -e "${YELLOW}⚠️  Some tests failed. Please review the output above.${NC}"
    exit 1
fi
